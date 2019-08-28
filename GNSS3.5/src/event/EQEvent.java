package event;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import arcgis.ArcgisShpHelper;
import arcgis.GisHelper;
import arcgis.pyHelper.ArcgisPyHelper;
import baseObject.BaseStation;
import beans.AReport;
import beans.BLH;
import beans.BLT;
import beans.EQInfo;
import beans.GPSData;
import beans.Loc;
import beans.Report;
import dataCache.DataCache;
import dataCache.DispWithMEMS;
import dataCache.Displacement;
import dbHelper.GNSSDBHelper;
import helper.ImgHelper;
import helper.EpiHelper;
import mainFrame.EndDealThread;
import mainFrame.FrameRunner;
import mathUtil.DoubleUtil;
import metaData.StaticMetaData;
import utils.Config;

public class EQEvent {

	private String eqEventID = null;
	public EQInfo eqInfo = null;
//	public Hashtable<BaseStation, Date> f3TriggerStDate = new Hashtable<>();//key:触发地震的台站，value：触发时间
//	public HashSet<BaseStation> triggerSt = new HashSet<>();//触发的台站列表——无序，弃用
	public List<BaseStation> triggerSt = new ArrayList();//触发的台站列表——无序，弃用
	private BaseStation firstStation = null;
//	private Set<Integer> reportSet = new HashSet<>(); //0表示三点计算时，生成的report
	private List<AReport> aReportList = new ArrayList<>();
	private Date firstTriggerTime = null;//首次触发时间，本机时间
	private Hashtable<String, Displacement> stationMaxHDis = new Hashtable<>();//key:stationID value:station的最大水平位移量的displacement，在lastTimeOp，初始化数据
	private Hashtable<String, Displacement> stationMaxVDis = new Hashtable<>();//key:stationID value:station的最大垂直位移量的displacement，在lastTimeOp，初始化数据
	private boolean isEndDealed = false;
	private Map<String, Double> stMEMSMagMap = new HashMap<>();//key:station.ID, value:MEMSMag

	/**
	 * 
	 * @param eqEventID
	 * @param firstStation
	 * @param firstTriggerTime 本机时间
	 */
	public EQEvent(String eqEventID, BaseStation firstStation, Date firstTriggerTime) {
		this.eqEventID = eqEventID;
		this.firstStation = firstStation;
		this.firstTriggerTime = firstTriggerTime;
		this.eqInfo = new EQInfo(firstStation.myLocation.copy(), firstStation.myDataCache.triggerTime, firstStation.ID, new Date());
	}
	public void endDeal() {
		this.isEndDealed = true;
		//执行线程任务F-03-01
		new EndDealThread(this).start();
	}
	/**
	 * 生成速报信息，并写入AReport表中,并更新EQInfo中的M为最大M
	 * @return
	 */
	public boolean createAReport(BaseStation curStation) {
		double epiDis = 0;
		if (aReportList.size()>0) {
//			epiDis = Double.parseDouble(String.format("%.4f",GisHelper.calDisByBL(curStation.myLocation.getGpsData().blh.L, curStation.myLocation.getGpsData().blh.B, this.eqInfo.getEpicenter().getGpsData().blh.L, this.eqInfo.getEpicenter().getGpsData().blh.B)));
			epiDis = Double.parseDouble(String.format("%.4f",GisHelper.calDisByBLProjTo3857(curStation.myLocation.getGpsData().blh.L, curStation.myLocation.getGpsData().blh.B, this.eqInfo.getEpicenter().getGpsData().blh.L, this.eqInfo.getEpicenter().getGpsData().blh.B)));
		}
		double maxM = 0;//作为当前时报震级，取各个台站最大震级
		double curStGPSMag = 0;//表示当前台站震级
		double sumGPSMag = 0;
		StringBuilder builder = new StringBuilder("[");
		for (int j = 0; j < triggerSt.size(); j++) {
			BaseStation station = triggerSt.get(j);
			double maxHDis = calHMaxDis(station);
			double maxVDis = calVMaxDis(station);
			double maxENUDis = calMaxENUDis(station);
			double epiD = calEpiDistance(station);
//			double mag = GisHelper.calMagnitude(maxHDis, epiD);
			double mag = pgdCalMagnitude(maxENUDis, epiD);
			if (mag > 20) {
				mag = 20;
			}else if (mag < 0) {
				mag = 0;
			}
			sumGPSMag+=mag;
			builder.append("{\"station\":\""+station.ID+"\",\"M\":"+mag+",\"maxHDis\":"+String.format("%.4f", maxHDis)+",\"maxVDis\":"+String.format("%.4f", maxVDis)+",\"maxENUDis\":"+String.format("%.4f", maxENUDis)+"},");
			if (mag>maxM) {
				maxM = mag;
			}
			if (station.ID.equals(curStation.ID)) {
				curStGPSMag = mag;
			}
		}
		if (builder.lastIndexOf(",")>0) {
			builder.deleteCharAt(builder.lastIndexOf(","));
		}
		builder.append("]");
		//MEMS计算震级
		double curStMEMSMag = 0;
		if (!stMEMSMagMap.containsKey(curStation.ID)) {//MEMSMag已计算
			curStMEMSMag = calMEMSMag(curStation);
			stMEMSMagMap.put(curStation.ID, curStMEMSMag);
			System.out.println(curStation.ID+" memsMag:"+curStMEMSMag);
			eqInfo.setMemsMag(curStMEMSMag);
		}else {
			curStMEMSMag = stMEMSMagMap.get(curStation.ID);
		}
		double sumMEMSMag = 0;
		Collection<Double> values = stMEMSMagMap.values();
		for (Double value : values) {
			sumMEMSMag+=value;
		}
		//更新eqInfo的震级，取各个触发台站的平均值
//		eqInfo.setMagnitude(DoubleUtil.round(sumM/i, 2));
		eqInfo.setGpsMag(DoubleUtil.round(sumGPSMag/triggerSt.size(), 2));
		eqInfo.setMemsMag(DoubleUtil.round(sumMEMSMag/values.size(), 2));
		eqInfo.updateEQName();
		GNSSDBHelper.updateEQ(eqInfo);
		
		//计算ST
		Date ST = calST(this.eqInfo.getEqTime(), epiDis);
		AReport aReport = new AReport(this.eqInfo.getEQID(), curStation.ID, aReportList.size()+1, 
				this.eqInfo.getEpicenter().getGpsData().blh.L, this.eqInfo.getEpicenter().getGpsData().blh.B, epiDis,
				curStation.myDataCache.triggerTime, ST, this.eqInfo.getMemsMag(), this.eqInfo.getGpsMag(), new Date(), curStGPSMag, builder.toString(), curStMEMSMag);//此处PT为P波触发时刻,ST为S波触发时刻
		
		aReportList.add(aReport);
		return GNSSDBHelper.insertAReport(aReport);
	}

	/**
	 * 计算S波到达时刻
	 * @param epiT 震中时刻
	 * @param epiDis 震中距，单位km
	 * @return
	 */
	private Date calST(Date epiT, double epiDis){
		return new Date(epiT.getTime()+(long)(1000*DoubleUtil.div(epiDis, Config.Vs, 3)));
	}
	public boolean endOp(){
		//eqInfo写入DB
		this.eqInfo.updateEQName();
		GNSSDBHelper.insertEQ(eqInfo,firstStation.ID);
		//拷贝EQPoints.shp EQPolygons.shp
		if (!demoShpCopy()) {
			System.out.println("demo shp copy failed.");
			return false;
		}
		//添加震中点
//		boolean res1 = ArcgisShpHelper.addEQPoint(eqInfo, 1, "", EQPointType.epicenter);
		//添加台站点
//		synchronized (triggerSt) {
//			for (BaseStation station:triggerSt) {//ConcurrentModificationException异常
//				BLH blh = station.myLocation.getGpsData().blh;
//				ArcgisShpHelper.addEQStationPoint(eqInfo, 1, "", ArcgisHelper.getWGS84PointInstance(blh.L, blh.B));
//			}
//		}
//		ArcgisShpHelper.addEQStationPoint(eqInfo, triggerSt, stMEMSMagMap, "");
		//添加震中点和台站点
		ArcgisShpHelper.addEQPoint(eqInfo, triggerSt, stMEMSMagMap, "");
		String startT = this.getFirstTriggerTime().toLocaleString();
		System.out.println(this.eqInfo.getEQID()+"::");
		System.out.println("startT:" + startT);
		long startTL = this.getFirstTriggerTime().getTime();
		Date endDate = new Date();
		System.out.println("endT:" + endDate.toLocaleString());
		System.out.println("计算耗时：" + (endDate.getTime() - startTL)/1000+"s");
		while (true) {
			Date endT = new Date(startTL+(long)Config.EQWarnTime*60*1000);
			if (new Date().before(endT)) {
				try {
					System.out.println("waiting 5s...");
					Thread.sleep(5000);//5s
					continue;
				} catch (InterruptedException e) {
					e.printStackTrace();
					continue;
				}
			}else {
				System.out.println("5min,生成箭头走向图层、等值线图层，并发布...");
				Date ttDate = new Date();
				//生成箭头走向图
				String tNode = this.createArrows(Config.ArrowsIntervalTime);//60000ms实现每分钟时段内生成箭头图,当前值为15000ms
				boolean repRes = createReport();
				//生成等值线图并发布
				boolean resContours = this.createContours(tNode);
				if (resContours) {
					boolean setPublished = GNSSDBHelper.setReportPublished(eqInfo.getEQID(), 1);
				}
				ImgHelper.ExportEQImgByService(this.eqInfo);
				System.out.println("生成箭头走向图、等值线图、发布耗时：" + (new Date().getTime() - ttDate.getTime())/1000+"s");
				//短连接发送事件关闭消息至SD平台
				//new ShortSender(Config.SDIP, Config.SDPort).send("this is an end msg.");//发送事件关闭消息至SD平台
				break;
			}
		}
		while (true) {
			Date endT = new Date(this.getFirstTriggerTime().getTime()+(long)Config.EQEndTime*60*1000);
			if (new Date().before(endT)) {
				try {
					System.out.println("waiting 1min...");
					Thread.sleep(60000);//1min
					continue;
				} catch (InterruptedException e) {
					e.printStackTrace();
					continue;
				}
			}else {
				putEqEventNotActive();
				//短连接发送事件关闭消息至SD平台
				//new ShortSender(Config.SDIP, Config.SDPort).send("this is an end msg.");//发送事件关闭消息至SD平台
//				break;
				System.out.println("总耗时：" + (new Date().getTime() - startTL)/1000+"s");
				break;
			}
		}
		return true;
	}
	/**
	 * 生成箭头走向图,自发震时间始，每间隔tInterval ms产生箭头图
	 * @param tInterval 生成箭头走向图的时间间隔
	 * @return tNode String 最后一个箭头图的时间节点 
	 */
	private String createArrows(long tInterval) {
		stationMaxHDis = new Hashtable<>();
		stationMaxVDis = new Hashtable<>();
		BaseStation baseStation = null;
		long eqTLong = this.eqInfo.getEqTime().getTime();
		long startLong = eqTLong;
		long nextLong = startLong + tInterval;//加1min，实现每分钟时段内生成箭头图
		String tNode = null;
		while (true) {
			synchronized (triggerSt) {
				Iterator<BaseStation> triggerSet = triggerSt.iterator();
				while (triggerSet.hasNext()) {
					baseStation = triggerSet.next();
					double maxV = 0 ;
					double maxH = 0 ;
					Displacement maxVDis = null;
					Displacement maxHDis = null;
					List<DispWithMEMS> dataList = baseStation.myDataCache.myDataList;
					for(int i = 0 ; i < dataList.size(); i ++){
						Displacement d = dataList.get(i);
						long nowLong = d.time.getTime();
						if (nowLong < startLong) {
							continue;
						}
						if (nowLong >= nextLong) {
							break;
						}
						if(Math.abs(maxV) < Math.abs(d.zDisplacement))
						{
							maxV = d.zDisplacement ;
							maxVDis = d;
						}
						if(Math.abs(maxH) < Math.abs(d.displacement2D))
						{
							maxH = d.displacement2D ;
							maxHDis = d;
						}
					}
					if (maxHDis != null) {
						stationMaxHDis.put(baseStation.ID, maxHDis);
					}
					if (maxVDis != null) {
						stationMaxVDis.put(baseStation.ID, maxVDis);
					}
				}
			}
			tNode = (nextLong - eqTLong)/1000 + "s"; 
			boolean resArrow = ArcgisShpHelper.createArrows(eqInfo.getEQID(), tNode, stationMaxHDis, stationMaxVDis);
			if (!resArrow) {
				System.out.println(tNode+"箭头图生成失败。。。");
			}
			List<DispWithMEMS> list = this.getFirstStation().myDataCache.myDataList;
			if (nextLong > list.get(list.size()-1).time.getTime()) {
				break;
			}else {
				startLong = nextLong;
				nextLong += tInterval;
			}
		}
		return tNode;
	}
	/**
	 * 生成等值线图
	 * @param tNode
	 * @return
	 */
	private boolean createContours(String tNode) {
		//初始化stationMaxVHDis在全部时间段范围内的最大值
		initStationMaxVHDis();
		String shpName = "stations_"+tNode+".shp";
		System.out.println(shpName);
		System.out.println("stationMaxHDis::\n"+stationMaxHDis);
		System.out.println("stationMaxVDis::\n"+stationMaxVDis);
		boolean resCopy = stationsShpCopyTo(shpName);
		if (!resCopy) {
			System.out.println("台站最大位移量shp文件拷贝失败");
			return false;
		}
		return ArcgisPyHelper.createContours(eqInfo.getEQID(), shpName, tNode, stationMaxHDis, stationMaxVDis);
	}
	private boolean stationsShpCopyTo(String newNameForStations) {
		String outDir = Config.filePath+eqInfo.getEQID()+"/";
		String demoFolder = Config.demoFolder;
		String fileName = "Export_Stations.shp";
//		String fileName = "JP_Stations.shp";
		File source = new File(demoFolder+fileName);
		File dest = new File(outDir+newNameForStations);
		if (!ArcgisShpHelper.shpCopy(source, dest)) {
			return false;
		}
		return true;
	}
	/**
	 * 初始化stationMaxVDis key:stationID value:station的最大垂直位移量的displacement
	 * 初始化stationMaxHDis key:stationID value:station的最大水平位移量的displacement
	 * 
	 */
	private boolean initStationMaxVHDis() {
		stationMaxHDis = new Hashtable<>();
		stationMaxVDis = new Hashtable<>();
		Iterator<BaseStation> triggerSet = triggerSt.iterator();
		BaseStation baseStation = null;
		while (triggerSet.hasNext()) {
			baseStation = triggerSet.next();
			double maxV = 0 ;
			double maxH = 0 ;
			Displacement maxVDis = null;
			Displacement maxHDis = null;
			for(int i = 0 ; i < baseStation.myDataCache.myDataList.size(); i ++){
				Displacement d = baseStation.myDataCache.myDataList.get(i) ;
				if(maxV < Math.abs(d.zDisplacement))
				{
					maxV = Math.abs(d.zDisplacement);
					maxVDis = d;
				}
				if(maxH < Math.abs(d.displacement2D))
				{
					maxH = Math.abs(d.displacement2D) ;
					maxHDis = d;
				}
			}
			if (maxHDis != null) {
				stationMaxHDis.put(baseStation.ID, maxHDis);
			}
			if (maxVDis != null) {
				stationMaxVDis.put(baseStation.ID, maxVDis);
			}
		}
		return true;
	}
	/**
	 * 生成时报,是否发布
	 */
	public boolean createReport() {
		initStationMaxVHDis();
		if (triggerSt.size() < 1) {
			return false;
		}
		Report report = null;
		double epiDis = 0;
		double mag = 0;
		BaseStation baseStation = null;

		Hashtable<BaseStation, Report> stationMap = new Hashtable<>();
		Enumeration<BaseStation> enumeration = FrameRunner.iFrame.myStations.elements();
		while (enumeration.hasMoreElements()) {
			baseStation = enumeration.nextElement();
			epiDis = calEpiDistance(baseStation);
//			double maxVDis = calVMaxDis(baseStation);
//			mag = GisHelper.calMagnitude(maxVDis, epiDis);
			double maxPGD = calMaxENUDis(baseStation);
			mag = pgdCalMagnitude(maxPGD, epiDis);
			report = new Report(eqInfo.getEQID(), 1, Double.parseDouble(String.format("%.3f", epiDis/1000)), mag, firstStation.myDataCache.myDataList.get(firstStation.myDataCache.myDataList.size()-1).time, baseStation.ID, false, "");
			stationMap.put(baseStation, report);
		}
		//添加EQPolygons,且计算并更新report的detail
		boolean res2 = ArcgisShpHelper.addEQPolygon(eqInfo, 1, "", stationMap);
		//批量写入report
		boolean res3 = GNSSDBHelper.insertReportAll(stationMap.values(),true);
		System.out.println("批量写入report: " + stationMap.size() + ", " + res3);
		return (res2 && res3);
	}
	/**
	 * 拷贝EQPoints.shp EQPolygons.shp EQImgBLNote.shp EQImgBLBoundary.shp
	 * @return
	 */
	public boolean demoShpCopy() {
		String outDir = Config.filePath+eqInfo.getEQID()+"/";
		String demoFolder = Config.demoFolder;
		String fileName = "EQPoints.shp";
		File source = new File(demoFolder+fileName);
		File dest = new File(outDir+fileName);
		if (!ArcgisShpHelper.shpCopy(source, dest)) {
			return false;
		}
		fileName = "EQPolygons.shp";
		source = new File(demoFolder+fileName);
		dest = new File(outDir+fileName);
		if (!ArcgisShpHelper.shpCopy(source, dest)) {
			return false;
		}
		fileName = "EQImgBLNote.shp";
		source = new File(demoFolder+fileName);
		dest = new File(outDir+fileName);
		if (!ArcgisShpHelper.shpCopy(source, dest)) {
			return false;
		}
		fileName = "EQImgBLBoundary.shp";
		source = new File(demoFolder+fileName);
		dest = new File(outDir+fileName);
		if (!ArcgisShpHelper.shpCopy(source, dest)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 当前EQEvent失活，则
	 * //台站isActive置为false
	 * //维护台站位移数据的时间窗口
	 * //scene列表中移除当前scene
	 * //scene置为null
	 * @param scene
	 */
	public void putEqEventNotActive(){
//		System.out.println("putEqEventNotActive ...");
		Hashtable<String, EQEvent> eqEv = FrameRunner.iFrame.eqEvents;
		synchronized (eqEv) {
			if (eqEv.containsKey(this.getEqEventID())) {
				eqEv.remove(this.getEqEventID());	 
			}
		}
		synchronized (this) {
			synchronized (triggerSt) {
				Iterator<BaseStation> stations = triggerSt.iterator();
				while (stations.hasNext()) {
					BaseStation baseStation = (BaseStation) stations.next();
					boolean flag = false;
					//判断当前station是否活跃于其它EQEvent，若活跃于其它EQEvent，则不清除数据
					Enumeration<EQEvent> eqEvents = FrameRunner.iFrame.eqEvents.elements();
					while (eqEvents.hasMoreElements()) {
						EQEvent otherEQEvent = (EQEvent) eqEvents.nextElement();
						if (this.equals(otherEQEvent)) {
							continue;
						}
						if (otherEQEvent.triggerSt.contains(baseStation)) {
							flag = true;
							break;
						}
					}
					if(flag){
						continue;
					}else{
						baseStation.isActive = false;
						baseStation.myDataCache.maintainTimeWindow();
						System.gc();
					}
				}
				if (isEndDealed) {
					GNSSDBHelper.endEQ(this.eqInfo.getEQID());
				}
				
				this.eqInfo.dispose();
				this.eqInfo = null;
				this.firstStation = null;
				synchronized (aReportList) {
					ListIterator<AReport> iterator = aReportList.listIterator();
					while (iterator.hasNext()) {
						AReport aReport = iterator.next();
						iterator.remove();
						aReport.dispose();
						aReport = null;
					}
					aReportList.clear();
					aReportList = null;
				}
				firstTriggerTime = null;
				firstStation = null;
				stationMaxHDis.clear();
				stationMaxHDis = null;
				stationMaxVDis.clear();
				stationMaxVDis = null;
				triggerSt.clear();
				
				this.eqEventID = null;
				this.triggerSt = null;
				this.stMEMSMagMap.clear();
				this.stMEMSMagMap = null;
			}
		}
		System.out.println("putEqEventNotActive after remove:" + FrameRunner.iFrame.eqEvents.size());
	}

	/**
	 * 计算当前台站的震级
	 * @param station
	 * @return
	 */
	private double calMagnitudeDouble(BaseStation station) {
		double maxENUDis = calMaxENUDis(station);//单位：m
		double epiDis = calEpiDistance(station);//单位：m
		double mag = pgdCalMagnitude(maxENUDis, epiDis);
		if (mag > 20) {
			mag = 20;
		}else if (mag < 0) {
			mag = 0;
		}
		return mag;
	}
	/**
	 * 计算当前台站的震级
	 * @param station
	 * @return Json String
	 */
	private String calMagnitudeJson(BaseStation station) {
		double maxHDis = calHMaxDis(station);
		double maxVDis = calVMaxDis(station);
		double epiDis = calEpiDistance(station);
		double maxENUDis = calMaxENUDis(station);
		double mag = pgdCalMagnitude(maxENUDis, epiDis);
		if (mag > 20) {
			mag = 20;
		}else if (mag < 0) {
			mag = 0;
		}
		return "{\"station\":\""+station.ID+"\",\"M\":"+mag+",\"maxHDis\":"+maxHDis+",\"maxVDis\":"+maxVDis+",\"maxENUDis\":"+maxENUDis+"}";
	}
//	Logger logger = Logger.getLogger(EQEvent.class);
	/*
	 * 计算震中点、发震时间、震级、name
	 */
	public void updateEpiOriginTimeMagnitude(){
		BLT[] blts = new BLT[triggerSt.size()];
		int i = 0;
		for (BaseStation station : triggerSt) {
			BLH blh = station.myLocation.getGpsData().blh;
			blts[i] = new BLT(blh.B, blh.L, station.myDataCache.triggerTime);
			i++;
		}
		EpiHelper epiHelper = new EpiHelper(blts);
		GPSData epi = epiHelper.calEpi(0, Config.epiCalTime);
		Date epiT = epiHelper.getEQTime();
		eqInfo.setEpicenter(new Loc(epi));
	 	eqInfo.setEqTime(epiT);
	 	System.out.println("震中点：" + epi);
	 	System.out.println("发震时间：" + eqInfo.getEqTime().toLocaleString());
		//计算首台震级，并将首台震级设为eq震级
	 	double maxENUDis = calMaxENUDis(firstStation);
		double epiD = calEpiDistance(firstStation);
		double gpsMag = pgdCalMagnitude(maxENUDis, epiD);
//		eqInfo.setMagnitude(mag);
		eqInfo.setGpsMag(gpsMag);
		eqInfo.updateEQName();
		System.out.println("name:" + eqInfo.getName());
//		logger.info("震中点：" + epi+",发震时间：" + eqInfo.getEqTime().toLocaleString()+", M:"+mag);
		//测试Pd计算震级
//		calMEMSPdMag();
	}
	/**
	 * Tc方法MEMS数据计算震级
	 * @param station
	 * @return
	 */
	public double calMEMSMag(BaseStation station) {
		DataCache cache = station.myDataCache;
		List<Double> udList = cache.getUDListAfterTriggerIn3s();
		System.out.println("udList.size() - "+udList.size());
		List<Double> vList = new ArrayList<>();//对加速度积分，获取速度
		List<Double> dList = new ArrayList<>();//对速度积分，获取位移
		Double[] dBWHighPass = null;//对位移滤波，获取滤波后的位移数组
		List<Double> vList2 = new ArrayList<>();//对滤波后的位移进行微分，获取速度序列
		double memsHz = (double)Config.MEMSHz;
		int scale = 15;
		double sumDBW = 0;
		double sumV2 = 0;
		for (int i = 0; i < udList.size(); i++) {
			if (i == 0) {
				double v = DoubleUtil.div(udList.get(0), memsHz, scale);
				vList.add(v);
				double d = DoubleUtil.div(v, memsHz, scale);
				dList.add(d);
			}else {
				double v = DoubleUtil.add(vList.get(i-1), DoubleUtil.div(udList.get(i), memsHz, scale));
				vList.add(v);
				double d = DoubleUtil.add(dList.get(i-1), DoubleUtil.div(vList.get(i), memsHz, scale));
				dList.add(d);
			}
		}
		dBWHighPass = ButterWorthFilter.highpassFilter(dList);
		for (int i = 0; i < dBWHighPass.length; i++) {
			if (i == 0) {
				double v2 = DoubleUtil.mul(dBWHighPass[0], memsHz);
				vList2.add(v2);
				sumDBW+=(DoubleUtil.mul(dBWHighPass[0], dBWHighPass[0]));
				sumV2+=(DoubleUtil.mul(v2, v2));
			}else {
				double v2 = DoubleUtil.mul(DoubleUtil.sub(dBWHighPass[i], dBWHighPass[i-1]), memsHz);
				vList2.add(v2);
				sumDBW+=(DoubleUtil.mul(dBWHighPass[i], dBWHighPass[i]));
				sumV2+=(DoubleUtil.mul(v2, v2));
			}
		}
		double r = DoubleUtil.div(sumV2, sumDBW, scale);
		double tc = DoubleUtil.div(2*Math.PI, Math.sqrt(r), scale);
		double memsMag = calMEMSMag(tc);
		udList.clear();
		udList = null;
		vList.clear();
		vList = null;
		dList.clear();
		dList = null;
		for (int i = 0; i < dBWHighPass.length; i++) {
			dBWHighPass[i] = null;
		}
		dBWHighPass = null;
		vList2.clear();
		vList2 = null;
		return memsMag;
	}
	private double calMEMSMag(double tc) {
		if (tc<=0) {
			return 0;
		}
		return DoubleUtil.mul(3.57, Math.log10(tc))+5.29;
	}
	
//	/**
//	 * MEMS数据计算震级
//	 */
//	public void calMEMSPdMag() {
//		for (BaseStation station : triggerSt) {
//			if (station.isMEMSMagCaled) {//MEMSMag已计算
//				continue;
//			}
//			if (triggerSt.size() < 5) {//触发台站<5,则无震中，无法计算震中距
//				continue;
//			}
//			DataCache cache = station.myDataCache;
//			Date triggerDate = cache.triggerTime;
////			Date t0 = new Date(triggerDate.getTime() - 10000);//计算前10s数据，不包含t0
//			Date t2 = new Date(triggerDate.getTime() + 3*1000);//计算触发后的3s数据，包含t2
//			double[] signal = new double[3*Config.MEMSHz];
//			int size = 0;
//			int k = 0;
//			boolean flag = false;//用于标记是否存在后三秒数据
//			while (k < 3) {//等待至触发后3s数据存在,最多等3s
//				size = cache.myDataList.size();
//				if (size <= 0 || cache.myDataList.get(size -1).time.getTime() < t2.getTime()) {
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					k++;
//					continue;
//				}else {
//					flag = true;
//					break;
//				}
//			}
//			if (!flag) {//后三秒数据不足，跳过本台站MEMSMag计算
//				continue;
//			}
//			int count = 0;
//			for (int i = size - 1; i >= 0; i--) {
//				DispWithMEMS dispWithMEMS = cache.myDataList.get(i);
////				if (dispWithMEMS.time.getTime()+1000 < t0.getTime()) {
////					break;
////				}
//				if (dispWithMEMS.time.getTime() - 1000 > t2.getTime()) {
//					continue;
//				}
//				if (dispWithMEMS.time.getTime() + 1000 < triggerDate.getTime()) {
//					break;
//				}
//				List<MEMSData> memsDataList = dispWithMEMS.memsDataList;
//				if (memsDataList == null || memsDataList.size() <= 0) {
//					continue;
//				}
//				for (int j = memsDataList.size() - 1; j >= 0; j--) {
//					if (count>=signal.length) {
//						break;
//					}
//					MEMSData memsData2 = memsDataList.get(j);
//					if (memsData2.time.getTime() > t2.getTime()) {
//						continue;
//					}
//					//here 计算当前时刻点的位移量=(a1+a2)/2*0.0001=(a1+a2)/2/Config.MEMSHz/Config.MEMSHz,单位cm，a2表示当前时刻点的垂直向加速度，a1表示前一时刻点的垂直向加速度
//					double a2 = memsData2.getAccH();//垂直向加速度
//					double a1 = 0;
////					System.out.println(i+" - "+j);
//					if (j > 0) {
//						MEMSData memsData1 = memsDataList.get(j-1);
//						//确保是上一时刻点的数据存在,且添加前后2ms的容忍度
//						if (memsData1.getTime().getTime() + (1000/Config.MEMSHz) >= (memsData2.getTime().getTime()-2) && memsData1.getTime().getTime() + (1000/Config.MEMSHz) <= (memsData2.getTime().getTime()+2)) {
//							a1 = memsData1.getAccH();
//							double s = calDSByMEMSUD(a1, a2);//单位：cm
//							//here 3s位移取和，再滤波， or 3s位移滤波再取峰值？
//							try {
//								signal[count++] = s;
//							} catch (Exception e) {
//								e.printStackTrace();
//								System.out.println("count:"+count);
//							}
////							System.out.println(a1+","+a2+", 积分位移："+s);
//						}else {
//							System.out.println("当前s："+StaticMetaData.formatMs.format(memsData1.time)+" - "+StaticMetaData.formatMs.format(memsData2.time));
//						}
//					}else {//j == 0
//						if (i > 0) {
//							dispWithMEMS = cache.myDataList.get(i - 1);
//							memsDataList = dispWithMEMS.memsDataList;
//							if (memsDataList == null || memsDataList.size() <= 0) {
//								break;
//							}
//							MEMSData memsData1 = memsDataList.get(memsDataList.size() - 1);
//							//确保是上一时刻点的数据存在,且添加前后2ms的容忍度
//							if (memsData1.getTime().getTime() + (1000/Config.MEMSHz) >= (memsData2.getTime().getTime()-2) && memsData1.getTime().getTime() + (1000/Config.MEMSHz) <= (memsData2.getTime().getTime()+2)) {
//								a1 = memsData1.getAccH();
//								double s = calDSByMEMSUD(a1, a2);
//								signal[count++] = s;
////								System.out.println(a1+","+a2+", 积分位移："+s);
//							}else {
//								System.out.println("前1s："+StaticMetaData.formatMs.format(memsData1.time)+" - "+StaticMetaData.formatMs.format(memsData2.time));
//							}
//						}
//					}
//				}
//			}
//			System.out.println(station.ID+":");
//			System.out.println("before - "+count+", "+signal.length);
//			double sum = 0;
//			for (int i = 0; i < count; i++) {
//				sum+=signal[i];
//			}
////			System.out.println();
////			double[] ffs = ButterWorthFilter.highpass(signal);
////			double pd = ButterWorthFilter.highpassMax(signal);
//			System.out.println("before - sum(pd):"+sum);
//			double pd = ButterWorthFilter.highpass(sum);
//			System.out.println("after - pd:"+pd);
//			double curEpiDis = calEpiDistanceByProjPoint(station);
//			double memsPgMag1 = pdMagnitude1(pd, curEpiDis);
//			double memsPgMag2 = pdMagnitude2(pd, curEpiDis);
//			System.out.println("MEMS 震级结果：" + memsPgMag1+" - "+memsPgMag2);
//		}
//	}
//	/**
//	 * 
//	 * @param pd加速度积分获取位移，对位移使用2阶高通巴特沃斯滤波器（低频截止频率为0.075Hz）,计算峰值为pd
//	 * @param epiDis,震中距，单位：km
//	 * @return
//	 */
//	private double pdMagnitude1(double pd, double epiDis) {
//		System.out.println("pd:"+pd+", epiDis:"+epiDis);
//		if (pd == 0 || epiDis == 0) {
//			return 0;
//		}
//		if (pd < 0) {
//			pd *= (-1);
//		}
//		return DoubleUtil.mul(0.91, Math.log(pd)) + DoubleUtil.add(0.48, Math.log(epiDis)) + 5.65 - 0.56;
//	}
//	/**
//	 * 
//	 * @param pd加速度积分获取位移，对位移使用2阶高通巴特沃斯滤波器（低频截止频率为0.075Hz）,计算峰值为pd
//	 * @param epiDis,震中距，单位：km
//	 * @return
//	 */
//	private double pdMagnitude2(double pd, double epiDis) {
//		System.out.println("pd:"+pd+", epiDis:"+epiDis);
//		if (pd == 0 || epiDis == 0) {
//			return 0;
//		}
//		if (pd < 0) {
//			pd *= (-1);
//		}
//		return DoubleUtil.mul(0.91, Math.log(pd)) + DoubleUtil.add(0.48, Math.log(epiDis)) + 5.65 + 0.56;
//	}
//	/**
//	 * 计算当前时刻点的位移量=(a1+a2)/2*0.0001=(a1+a2)/2/Config.MEMSHz/Config.MEMSHz,单位cm，a2表示当前时刻点的垂直向加速度，a1表示前一时刻点的垂直向加速度
//	 * @param a1
//	 * @param a2
//	 * @return 积分位移，单位：cm
//	 */
//	private double calDSByMEMSUD(double a1, double a2) {
//		double hz = DoubleUtil.mul(Config.MEMSHz, Config.MEMSHz);
//		return DoubleUtil.div(DoubleUtil.div(DoubleUtil.add(a1, a2), 2.0, 16), hz, 16);
//	}
	/**
	 * 计算发震时间
	 * @return
	 */
	private Date calEqTime(){
//		double dis = epicenter.getDistance2D(firstGKP);
		double dis = calEpiDistance(firstStation);
		double v = Config.v;
		long disT = Long.parseLong((int)(dis/v)+"");
		Date oriTime = new Date(eqInfo.getEqTime().getTime() - disT);
		return oriTime;
	}
	/**
	 * 计算某个台站的震中距,单位：m
	 * @param stationID 台站ID
	 * @return
	 */
	private double calEpiDistance(BaseStation station) {
//		return eqInfo.getEpicenter().getGkPoint().getDistance2D(station.myLocation.getGkPoint());
		BLH epiBLH = eqInfo.getEpicenter().getGpsData().blh;
		BLH stBLH = station.myLocation.getGpsData().blh;
		if (epiBLH == null) {
			System.out.println("calEpiDistance: epiBLH is null.");
			return 0;
		}
		if (stBLH == null) {
			System.out.println("calEpiDistance: stBLH is null.");
			return 0;
		}
		return 1000*GisHelper.calDisByBLProjTo3857(epiBLH.L, epiBLH.B, stBLH.L, stBLH.B);
	}
	/**
	 * 计算某个台站的震中距,单位：km
	 * @param stationID 台站ID
	 * @return
	 */
	private double calEpiDistanceByProjPoint(BaseStation station) {
//		return eqInfo.getEpicenter().getGkPoint().getDistance2D(station.myLocation.getGkPoint());
		double x1 = eqInfo.getEpicenter().getProjPoint().getX();
		double y1 = eqInfo.getEpicenter().getProjPoint().getY();
		System.out.println("epi:"+x1+","+y1);
		double x2 = station.myLocation.getProjPoint().getX();
		double y2 = station.myLocation.getProjPoint().getY();
		return GisHelper.calDisByProjPoint(x1, y1, x2, y2);
	}
	/**
	 * 计算某个台站水平方向上的最大位移量
	 * @param station
	 * @return
	 */
	private double calHMaxDis(BaseStation station) {
		if (!triggerSt.contains(station)) {
			System.out.println("calHMaxDis:: triggerStDate containsKey == false");
			return 0;
		}
		List<DispWithMEMS> dispList = station.myDataCache.myDataList;
		if (dispList == null || dispList.size() < 1) {
			return 0;
		}
		double maxHDis = 0;//水平方向最大位移
		for (int i = 0; i < dispList.size(); i++) {
			double curDis = dispList.get(i).displacement2D;
			if (maxHDis < curDis) {
				maxHDis = curDis;
			}
		}
		return maxHDis;
	}
	/**
	 * 计算某个台站垂直方向上的最大位移量
	 * @param station
	 * @return
	 */
	private double calVMaxDis(BaseStation station) {
		if (!triggerSt.contains(station)) {
			System.out.println("calVMaxDis:: triggerStDate containsKey == false");
			return 0;
		}
		List<DispWithMEMS> dispList = station.myDataCache.myDataList;
		if (dispList == null || dispList.size() < 1) {
			return 0;
		}
		double maxVDis = 0;//垂直方向最大位移
		for (int i = 0; i < dispList.size(); i++) {
			double curDis = dispList.get(i).zDisplacement;
			if (maxVDis < curDis) {
				maxVDis = curDis;
			}
		}
		return maxVDis;
	}
	/**
	 * 计算某个台站ENU方向上的合位移峰值,单位：m
	 * @param station
	 * @return
	 */
	private double calMaxENUDis(BaseStation station) {
//		if (!triggerSt.contains(station)) {
////			System.out.println("calMaxENUPGD:: triggerSt containsKey == false  " + station.ID);
//			return 0;
//		}
		List<DispWithMEMS> dispList = station.myDataCache.myDataList;
		if (dispList == null || dispList.size() < 1) {
			return 0;
		}
		double maxPGDDis = 0;//ENU方向最大位移
		for (int i = 0; i < dispList.size(); i++) {
			double curDis = dispList.get(i).getPGD();
			if (maxPGDDis < curDis) {
				maxPGDDis = curDis;
			}
		}
		return maxPGDDis;
	}
	/**
	 * 
	 * @param maxPGD ENU合位移最大值，单位：m
	 * @param epiDis 震中距，单位：m
	 * @return 震级
	 */
	private double pgdCalMagnitude(double maxPGD, double epiDis) {
		if (maxPGD <= 0 || epiDis == 0) {
			return 0;
		}
		return DoubleUtil.div(DoubleUtil.add(Math.log10(maxPGD*100), 4.434), DoubleUtil.sub(1.047, DoubleUtil.mul(0.138, Math.log10(epiDis/1000))), 2);
	}
	/**
	 * 垂直向的加速度积分，得到位移，单位cm
	 * @param ud1 前一时刻的垂直向加速度
	 * @param ud2 当前时刻的垂直向加速度
	 * @return
	 */
	private double definiteIntegral(double ud1, double ud2) {
		return (ud2 - ud1)*100/Config.MEMSHz/Config.MEMSHz + ud1/Config.MEMSHz;
	}
	/**
	 * 判断参数date的时刻，是否包含在当前scene的发震时刻的时间阈值范围内
	 * @param date
	 * @return
	 */
	public boolean isInTimeZone(Date date){
//		long l1=  eqInfo.getEqTime().getTime() - Config.timeExtent  ;
//		long l2 =  eqInfo.getEqTime().getTime() + Config.timeExtent ;
//		long l3 = date.getTime() ;
		System.out.println(StaticMetaData.formatMs.format(date)+", "+StaticMetaData.formatMs.format(eqInfo.getEqTime()));
		if (date.getTime() >= eqInfo.getEqTime().getTime() - Config.timeExtent 
				&& date.getTime() <= eqInfo.getEqTime().getTime() + Config.timeExtent) {
			return true;
		}
		return false;
	}
	/**
	 * 判断参数station的原始位置，是否包含在当前scene的发震空间阈值范围内
	 * @param station
	 * @return
	 */
	public boolean isInSpatialZone(BaseStation station) {
		
		if (calEpiDistance(station) <= Config.spatialExtent) {
			return true;
		}
		return false;
	}
//	public boolean isReportExist(int reportNum) {
//		return reportSet.contains(reportNum);
//	}
	
//	public void addReport(int timeNode){
//		if (reportSet == null) {
//			reportSet = new HashSet<>();
//		}
//		reportSet.add(timeNode);
//	}
	public BaseStation getFirstStation() {
		return firstStation;
	}

	public void setFirstStation(BaseStation firstStation) {
		this.firstStation = firstStation;
	}

//	public Set<Integer> getReportSet() {
//		return reportSet;
//	}

	public String getEqEventID() {
		return eqEventID;
	}

	/**
	 * 本机时间
	 * @return
	 */
	public Date getFirstTriggerTime() {
		return firstTriggerTime;
	}
	public boolean isEndDealed() {
		return isEndDealed;
	}
	public List<AReport> getAReportList() {
		return aReportList;
	}
	
}
