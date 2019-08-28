package helper;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jetty.http.MetaData;

import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geoprocessing.tools.coveragetools.SDTSImport;
import com.esri.arcgis.server.json.JSONArray;
import com.esri.arcgis.server.json.JSONObject;

import arcgis.ArcgisEQHelper;
import arcgis.ArcgisHelper;
import arcgis.ArcgisShpHelper;
import baseObject.BaseStation;
import beans.AReport;
import beans.BLH;
import beans.GPSData;
import beans.Loc;
import dbHelper.DBHelper;
import dbHelper.GNSSDBHelper;
import event.EQEvent;
import http.SDHttpHelper;
import mainFrame.FrameRunner;
import mainFrame.GNSSFrame;
import metaData.StaticMetaData;
import metaData.StaticMetaData.GNSSMode;
import utils.Config;

public class HttpServerHelper {
	public static String getIndexNums(String callback) {
		int stNum = StaticMetaData.getStationOriginalLocs().size();
		int stInNum = GNSSFrame.myStations.size();
		int eqInNum = GNSSFrame.eqEvents.size();
		int eqNum = 0;
		String sql = "select count(*) as count from eq;";
		ResultSet resultSet = DBHelper.runQuerySql(sql);
		try {
			if (resultSet != null && resultSet.next()) {
				eqNum = resultSet.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("stNum", stNum);
		jsonObject.put("stInNum", stInNum);
		jsonObject.put("eqInNum", eqInNum);
		jsonObject.put("eqNum", eqNum);
		return packCallBack(callback, jsonObject.toString());
	}
	public static String getEQEvents(String callback) {
		StringBuilder builder = new StringBuilder("[");
		Hashtable<String, EQEvent> eqEv = FrameRunner.iFrame.eqEvents;
		synchronized (eqEv) {
			Iterator<EQEvent> eqEvents = eqEv.values().iterator();
			for (Iterator iterator = GNSSFrame.eqEvents.values().iterator(); iterator.hasNext();) {
				EQEvent eqEvent = (EQEvent) iterator.next();
				synchronized (eqEvent) {
					try {
						builder.append("{\"lon\":"+eqEvent.eqInfo.getEpicenter().getWGS84Point().getX()+",\"lat\":"+eqEvent.eqInfo.getEpicenter().getWGS84Point().getY()+",\"EQID\":\""+eqEvent.eqInfo.getEQID()+"\"");
						builder.append(",\"firstSt\":\""+eqEvent.getFirstStation().ID+"\"");
						builder.append(",\"eqTime\":\""+eqEvent.eqInfo.getEqTime().toLocaleString()+"\"");
						builder.append(",\"GPSMag\":"+eqEvent.eqInfo.getGpsMag());
						builder.append(",\"MEMSMag\":"+eqEvent.eqInfo.getMemsMag());
						builder.append(",\"ARs\":[");
						List<AReport> aReports = eqEvent.getAReportList();
						synchronized (aReports) {
							if (aReports.size() > 0) {
//								AReport lastReport = aReports.get(aReports.size()-1);
//								JSONArray array = new JSONArray(lastReport.getStMs());
//								array.getJSONObject(0).
								for (int i = 0; i < aReports.size(); i++) {
									AReport aReport = aReports.get(i);
									builder.append("{\"EpiLon\":" + aReport.getEpiLon()+",\"EpiLat\":" + aReport.getEpiLat()+",\"EpiDis\":" + aReport.getEpiDis()+",\"PT\":\"" + aReport.getPT().toLocaleString()+"\"");
									builder.append(",\"MEMSMag\":"+aReport.getMemsMag()+",\"GPSMag\":"+aReport.getGpsMag()+",\"stGPSMag\":"+aReport.getStGPSMag()+",\"station\":\""+aReport.getStation()+"\",\"stMs\":"+aReport.getStGPSMags()+"},");
								}
								if (builder.lastIndexOf(",") == builder.length()-1) {
									builder.deleteCharAt(builder.lastIndexOf(","));
								}
							}
						}
						builder.append("]},");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if (builder.lastIndexOf(",") == builder.length()-1) {
				builder.deleteCharAt(builder.lastIndexOf(","));
			}
			builder.append("]");
		}
		return packCallBack(callback, builder.toString());
	}
	public static String getTimeLine(String callback) {
//		Set<Integer> timeSet = FrameRunner.iFrame.timeLineTask.myTaskRunner.getTimeSet();
//		if (timeSet == null || timeSet.size() == 0) {
//			return packCallBack(callback, null);
//		}
//		List<Integer> list = new ArrayList<>(timeSet);
//		Collections.sort(list);
//		StringBuilder builder = new StringBuilder("[");
//		for (Integer time : list) {
//			builder.append("\""+time+"s\",");
//		}
//		builder.append("]");
//		return packCallBack(callback, builder.toString());
		return packCallBack(callback, "[]");
	}
	public static String updateStation(String callback, String stationID, String stLoc, double B, double L) {
//		boolean result = ArcgisEQHelper.updateStation(stationID, B, L);
		IPoint point = ArcgisHelper.getWGS84PointInstance(L, B);
		try {
			point.project(ArcgisHelper.webMercatorCoordinateSystem);
			double x = point.getX();
			double y = point.getY();
			String sql = "update stationsInfo set stLocName='"+stLoc+"',longitude="+L+",latitude="+B+",x_3857="+x+",y_3857="+y+" where stationID='"+stationID+"' and status=1;";
			boolean res = DBHelper.runUpdateSql(sql);
			System.out.println(res+" - "+sql);
			if (!res) {
				return packCallBack(callback, "{\"result\":"+false+",\"msg\":\"更新数据库出错\"}");
			}
			//缓存中更新station,重新发送GPS台站请求
			if (res) {
				StaticMetaData.updateStationOriginalLoc(stationID, new Loc(new GPSData(new BLH(B, L, 0))));
				boolean ress = SDHttpHelper.startAllStGPS();
				if (!ress) {
					return packCallBack(callback, "{\"result\":"+false+",\"msg\":\"GPS数据请求出错\"}");
				}
			}
			//更新shp
			res = ArcgisShpHelper.updateStationInDemoShp(stationID, stLoc, B, L);
			if (!res) {
				return packCallBack(callback, "{\"result\":"+false+",\"msg\":\"台站shp文件更新出错\"}");
			}
			return packCallBack(callback, "{\"result\":"+res+",\"msg\":\"\"}");
		} catch (IOException e) {
			e.printStackTrace();
			return packCallBack(callback, "{\"result\":"+false+",\"msg\":\"更新台站出错\"}");
		}
	}
	/**
	 * ．缓存更新
	 * ．重新发送ＧＰＳ数据请求
	 * ．更新ｄｅｍｏ中台站ｓｈｐ图层文件
	 * @param callback
	 * @param stationID
	 * @return
	 */
	public static String deleteStation(String callback, String stationID) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql = "update stationsInfo set status=0, deleteTime='"+format.format(new Date())+"' where stationID='"+stationID+"';";
		boolean res = DBHelper.runUpdateSql(sql);
		System.out.println(res+" - "+sql);
		if (!res) {
			return packCallBack(callback, "{\"result\":"+false+",\"msg\":\"更新数据库出错\"}");
		}
		//缓存中更新station,重新发送GPS台站请求
		if (res) {
			StaticMetaData.deleteStationOriginalLoc(stationID);
			boolean ress = SDHttpHelper.startAllStGPS();
			if (!ress) {
				return packCallBack(callback, "{\"result\":"+false+",\"msg\":\"GPS数据请求出错\"}");
			}
		}
		//更新shp
		res = ArcgisShpHelper.deleteStationInDemoShp(stationID);
		if (!res) {
			return packCallBack(callback, "{\"result\":"+false+",\"msg\":\"台站shp文件更新出错\"}");
		}
		return packCallBack(callback, "{\"result\":"+res+",\"msg\":\"删除台站出错\"}");
	}
	/**
	 * 增加台站
	 * 1.台站ＩＤ唯一，否则返回
	 * ２．写入数据库，若ｆａｌｓｅ，返回
	 * ３．写入缓存
	 * ４．重新发送ＧＰＳ数据请求
	 * ５．更新ｄｅｍｏ中台站ｓｈｐ图层文件
	 * @param callback
	 * @param stationID
	 * @param stLoc
	 * @param B
	 * @param L
	 * @return
	 */
	public static String addStation(String callback, String stationID, String stLoc, double B, double L) {
//		boolean result = ArcgisEQHelper.addStation(stationID, B, L);
		//1.台站ＩＤ唯一，否则返回
		String sql = "select count(*) as count from stationsInfo where stationID='"+stationID+"' and status=1;";
		ResultSet resultSet = DBHelper.runQuerySql(sql);
		try {
			if (resultSet != null && resultSet.next()) {
				int count  = resultSet.getInt("count");
				System.out.println(stationID+" count:"+count);
				if (count > 0) {
					return packCallBack(callback, "{\"result\":"+false+",\"msg\":\"台站ID已存在\"}");
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		//２．写入数据库，若ｆａｌｓｅ，返回
		IPoint point = ArcgisHelper.getWGS84PointInstance(L, B);
		try {
			point.project(ArcgisHelper.webMercatorCoordinateSystem);
			double x = point.getX();
			double y = point.getY();
			sql = "insert into stationsInfo(stationID,stLocName,longitude,latitude,x_3857,y_3857,status) values('"
					+stationID+"','"+stLoc+"',"+L+","+B+","+x+","+y+",1);";
			boolean res = DBHelper.runUpdateSql(sql);
			System.out.println(res+" - "+sql);
			if (!res) {
				return packCallBack(callback, "{\"result\":"+false+",\"msg\":\"写入数据库出错\"}");
			}
			//３．写入缓存
			//４．重新发送ＧＰＳ数据请求
			else {
				StaticMetaData.addStationOriginalLoc(stationID, new Loc(new GPSData(new BLH(B, L, 0))));
				boolean ress = SDHttpHelper.startAllStGPS();
				if (!ress) {
					return packCallBack(callback, "{\"result\":"+false+",\"msg\":\"GPS数据请求出错\"}");
				}
			}
			//５．更新ｄｅｍｏ中台站ｓｈｐ图层文件
			res = ArcgisShpHelper.addStationInDemoShp(stationID, B, L, stLoc);
			if (!res) {
				return packCallBack(callback, "{\"result\":"+false+",\"msg\":\"台站shp文件写入出错\"}");
			}
			return packCallBack(callback, "{\"result\":"+res+",\"msg\":\"\"}");
		} catch (IOException e) {
			e.printStackTrace();
			return packCallBack(callback, "{\"result\":"+false+",\"msg\":\"添加台站出错\"}");
		}
	}
	
	public static String getStationMetaData(String callback) {
		StringBuilder builder = new StringBuilder("[");
		Set<Entry<String, Loc>> set = StaticMetaData.getStationOriginalLocs().entrySet();
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			Entry<String, Loc> entry = (Entry<String, Loc>) iterator.next();
			builder.append("{\"ID\":\""+entry.getKey()+"\",\"B\":"+entry.getValue().getGpsData().blh.B+",\"L\":"+entry.getValue().getGpsData().blh.L+"},");
		}
		if (builder.lastIndexOf(",")==builder.length()-1) {
			builder.deleteCharAt(builder.lastIndexOf(","));
		}
		builder.append("]");
		return packCallBack(callback, builder.toString());
	}
	
   
    /**
     * 获取当前时刻的台站分类，包括overSt和inSt两种类别，其中overSt表示活跃台站列表，inSt表示非活跃台站列表
     * @param resource
     * @return
     */
    public static String getCurStationFamily(String callback) {
    	
//		double maxDisOver = Property.getDoubleProperty("maxDisOver");
//		double minVDis = Property.getDoubleProperty("minVDis");
		StringBuilder over = new StringBuilder("\"overSt\":[");
		StringBuilder in = new StringBuilder("\"inSt\":[");
		BaseStation station;
		Enumeration<BaseStation> stations = FrameRunner.iFrame.myStations.elements();
		while (stations.hasMoreElements()) {
			station = (BaseStation) stations.nextElement();
//			boolean flag = false;
//			for (int j = 0; j < station.myDataCache.myDataList.size(); j++) {
//				if (station.myDataCache.myDataList.get(j).displacement2D >= maxDisOver) {
//				if (station.myDataCache.myDataList.get(j).zDisToLastDis >= minVDis) {
//					over.append("\""+station.ID+"\",");
//					flag = true;
//					break;
//				}
//			}
//			if (!flag) {
//				in.append("\""+station.ID+"\",");
//			}
			if (station.isActive) {
				over.append("\""+station.ID+"\",");
			}else{
				in.append("\""+station.ID+"\",");
			}
		}
		over.append("]");
		in.append("]");
		return packCallBack(callback, "{" + over.toString() + "," + in.toString() + "}");
	}
    public static String getStationsDis2DZ(String callback, String stations){
    	if (stations == null || stations == "") {
			return packCallBack(callback, "");
		}
    	String[] stationArray = stations.split(",");
    	StringBuilder builder = new StringBuilder("[");
    	for (String station : stationArray) {
    		builder.append("{\"ID\":\"").append(station).append("\",\"value\":").append(getCurStationDis2DZ(station)).append("},");
    	}
    	builder.append("]");
    	return packCallBack(callback, builder.toString());
    }
    /**
     * 获取所有台站最后30s的位移数据
     * @param callback
     * @return
     */
    public static String getStationslast30sDisVH(String callback, String stID){
    	StringBuilder builder = new StringBuilder();
    	Collection<BaseStation> stations = FrameRunner.iFrame.myStations.values();
    	for (BaseStation station : stations) {
    		if (station.ID.equalsIgnoreCase(stID)) {
    			builder.append("{\"ID\":\"").append(station.ID).append("\",\"value\":");
    			builder.append(station.last30sDis2DZ());
    			builder.append("}");
			}
		}
    	return packCallBack(callback, builder.toString());
    }
    /**
     * 获取所有台站最后30s的位移数据
     * @param callback
     * @return
     */
    public static String getStationslast30sDis2DZ(String callback){
    	StringBuilder builder = new StringBuilder("[");
    	Collection<BaseStation> stations = FrameRunner.iFrame.myStations.values();
    	for (BaseStation station : stations) {
    		builder.append("{\"ID\":\"").append(station.ID).append("\",\"value\":").append(getCurStationlast30sDis2DZ(station)).append("},");
    	}
    	builder.append("]");
    	return packCallBack(callback, builder.toString());
    }
    private static String getCurStationlast30sDis2DZ(BaseStation station){
    	if (station== null) {
    		System.out.println("getCurStationlast30sDis2DZ::station is null.");
    		return "";
    	}
    	String reString = station.last30sDis2DZ();
//		return "{\"z\":{\"dis\":[],\"time\":[]},\"dis2D\":{\"dis\":[],\"time\":[]}}";
    	return reString;
    }
    /**
     * 获取所有台站的time时刻前后timeExt ms的位移数据
     * @param callback
     * @param stations
     * @param time
     * @return
     */
    public static String getStationsDis2DZ(String callback, String stations, String time){
    	if (stations == null || stations == "") {
    		return packCallBack(callback, "");
    	}
    	String[] stationArray = stations.split(",");
    	StringBuilder builder = new StringBuilder("[");
    	if (time == null || time == "" || time.equals("")) {
    		for (String station : stationArray) {
    			builder.append("{\"ID\":\"").append(station).append("\",\"value\":").append(getCurStationDis2DZ(station)).append("},");
    		}
    	}else{
    		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		long timeExt = Config.timeExt;
    		Date date;
    		Date startTime = null;
    		Date endTime = null;
    		try {
    			date = dFormat.parse(time);
    			startTime = new Date(date.getTime()-timeExt);
    			endTime = new Date(date.getTime()+timeExt);
    		} catch (ParseException e) {
    			e.printStackTrace();
    		}
    		for (String station : stationArray) {
    			builder.append("{\"ID\":\"").append(station).append("\",\"value\":").append(getCurStationDis2DZ(station, startTime, endTime)).append("},");
    		}
    	}
    	builder.append("]");
    	return packCallBack(callback, builder.toString());
    }
    /**
     * 获取当前台站startTime~endTime的位移数据
     * @param stationID
     * @param startTime
     * @param endTime
     * @return
     */
    private static String getCurStationDis2DZ(String stationID, Date startTime, Date endTime){
    	if (startTime == null && endTime == null) {
			return getCurStationDis2DZ(stationID);
		}
    	if (stationID == null || stationID.equals("")) {
			System.out.println("getCurStationDis2DZ::station is null.");
			return "";
		}
		String reString = "";
		if (FrameRunner.iFrame.myStations.get(stationID)!=null) {
			reString = FrameRunner.iFrame.myStations.get(stationID).curDis2DZ(startTime, endTime);
		}else {
			//System.out.println("FrameRunner.iFrame.myStations.size():::" + FrameRunner.iFrame.myStations.size());
			System.out.println("当前station：" + stationID + "不存在");
			return "{\"z\":{\"dis\":[],\"time\":[]},\"dis2D\":{\"dis\":[],\"time\":[]}}";
		}
		return reString;
    }
    private static String getCurStationDis2DZ(String stationID){
    	if (stationID == null || stationID.equals("")) {
			System.out.println("getCurStationDis2DZ::station is null.");
			return "";
		}
		String reString = "";
		if (FrameRunner.iFrame.myStations.get(stationID)!=null) {
			reString = FrameRunner.iFrame.myStations.get(stationID).curDis2DZ();
		}else {
			//System.out.println("FrameRunner.iFrame.myStations.size():::" + FrameRunner.iFrame.myStations.size());
			System.out.println("当前station：" + stationID + "不存在");
			return "{\"z\":{\"dis\":[],\"time\":[]},\"dis2D\":{\"dis\":[],\"time\":[]}}";
		}
		return reString;
    }
    public static String handleStationDisplacement(String callback, String station) {
		
		if (station == null || station.equals("")) {
			System.out.println("handleStationDisplacement::station is null.");
			return "";
		}
		String reString = "";
		if (FrameRunner.iFrame.myStations.get(station)!=null) {
			reString = FrameRunner.iFrame.myStations.get(station).curDisplacement();
//			System.out.println(reString);
		}else {
			System.out.println("FrameRunner.iFrame.myStations.size():::" + FrameRunner.iFrame.myStations.size());
			System.out.println("当前station：" + station + "不存在");
		}
		return packCallBack(callback, reString);
	}
    public static String handleStLast2MinDisp(String callback, String station) {
    	
    	if (station == null || station.equals("")) {
    		System.out.println("handleStationDisplacement::station is null.");
    		return "";
    	}
    	String reString = "";
    	if (FrameRunner.iFrame.myStations.get(station)!=null) {
    		reString = FrameRunner.iFrame.myStations.get(station).curLast2MinDisp();
//			System.out.println(reString);
    	}else {
    		System.out.println("FrameRunner.iFrame.myStations.size():::" + FrameRunner.iFrame.myStations.size());
    		System.out.println("当前station：" + station + "不存在");
    	}
    	return packCallBack(callback, reString);
    }
    public static String handleStationDispWithMEMS(String callback, String station) {
    	
    	if (station == null || station.equals("")) {
    		System.out.println("handleStationDisplacement::station is null.");
    		return "";
    	}
    	String reString = "";
    	if (FrameRunner.iFrame.myStations.get(station)!=null) {
    		reString = FrameRunner.iFrame.myStations.get(station).curDispWithMEMS();
//			System.out.println(reString);
    	}else {
    		System.out.println("FrameRunner.iFrame.myStations.size():::" + FrameRunner.iFrame.myStations.size());
    		System.out.println("当前station：" + station + "不存在");
    	}
    	return packCallBack(callback, reString);
    }
    public static String handleStLastXMinDispWithMEMS(String callback, String station, int xMin) {
    	
    	if (station == null || station.equals("")) {
    		System.out.println("handleStLast2MinDispWithMEMS::station is null.");
    		return "";
    	}
    	String reString = "";
    	if (FrameRunner.iFrame.myStations.get(station)!=null) {
    		reString = FrameRunner.iFrame.myStations.get(station).curLastXMinDispWithMEMS(xMin);
//			System.out.println(reString);
    	}else {
    		System.out.println("FrameRunner.iFrame.myStations.size():::" + FrameRunner.iFrame.myStations.size());
    		System.out.println("当前station：" + station + "不存在");
    	}
    	return packCallBack(callback, reString);
    }
    public static String handleStLastXMinDispWithMEMSWithoutFitting(String callback, String station, int xMin) {
    	
    	if (station == null || station.equals("")) {
    		System.out.println("handleStLast2MinDispWithMEMS::station is null.");
    		return "";
    	}
    	String reString = "";
    	if (FrameRunner.iFrame.myStations.get(station)!=null) {
    		reString = FrameRunner.iFrame.myStations.get(station).curLastXMinDispWithMEMSWithoutFitting(xMin);
//			System.out.println(reString);
    	}else {
    		System.out.println("FrameRunner.iFrame.myStations.size():::" + FrameRunner.iFrame.myStations.size());
    		System.out.println("当前station：" + station + "不存在");
    	}
    	return packCallBack(callback, reString);
    }
    public static String handleStLast2MinDispWithMEMS(String callback, String station) {
    	
    	if (station == null || station.equals("")) {
    		System.out.println("handleStLast2MinDispWithMEMS::station is null.");
    		return "";
    	}
    	String reString = "";
    	if (FrameRunner.iFrame.myStations.get(station)!=null) {
    		reString = FrameRunner.iFrame.myStations.get(station).curLast2MinDispWithMEMS();
//			System.out.println(reString);
    	}else {
    		System.out.println("FrameRunner.iFrame.myStations.size():::" + FrameRunner.iFrame.myStations.size());
    		System.out.println("当前station：" + station + "不存在");
    	}
    	return packCallBack(callback, reString);
    }
    public static String getStationDis2DZWithMEMS(String callback, String station) {
    	
    	if (station == null || station.equals("")) {
    		System.out.println("handleStationDisplacement::station is null.");
    		return "";
    	}
    	String reString = "";
    	if (FrameRunner.iFrame.myStations.get(station)!=null) {
    		reString = FrameRunner.iFrame.myStations.get(station).curDis2DZ();
//			System.out.println(reString);
    	}else {
    		System.out.println("FrameRunner.iFrame.myStations.size():::" + FrameRunner.iFrame.myStations.size());
    		System.out.println("当前station：" + station + "不存在");
    	}
    	return packCallBack(callback, reString);
    }
    /**
     * 将返回值携带callback信息
     * @param callback
     * @param result
     * @return
     */
	private static String packCallBack(String callback, String result){
		if (callback == null || callback == "") {
			return result;
		}else {
			return callback + "(" + result + ")";
		}
	}
	public static String getEQImgs(String callback,String eqID) {
		String folder = Config.filePath+eqID+"/img";
		File folderFile = new File(folder);
		if (!folderFile.exists() || !folderFile.isDirectory()) {
			return packCallBack(callback, null);
		}
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".png")) {
					return true;
				}
				return false;
			}
		};
		File[] imgs = folderFile.listFiles(filter);
		if (imgs.length == 0) {
			return packCallBack(callback, null);
		}
		Map<Integer, JSONArray> eqArrowsMap = new HashMap<>();
		Map<Integer, JSONArray> eqContoursMap = new HashMap<>();
		for (File img : imgs) {
			System.out.println(img.getName());
			String imgBase64 = ImgHelper.imgToBase64(img);
			JSONObject imgObject = new JSONObject();
			imgObject.put("name", img.getName().replace(".png", ""));
			imgObject.put("content", imgBase64);
			int time = getTimeFromFileName(img.getName());
			if (img.getName().startsWith("EQArrows_")) {
				if (eqArrowsMap.containsKey(time)) {
					eqArrowsMap.get(time).put(imgObject);
				}else{
					eqArrowsMap.put(time, new JSONArray());
					eqArrowsMap.get(time).put(imgObject);
				}
			}else if (img.getName().startsWith("eqcontours")) {
				if (eqContoursMap.containsKey(time)) {
					eqContoursMap.get(time).put(imgObject);
				}else{
					eqContoursMap.put(time, new JSONArray());
					eqContoursMap.get(time).put(imgObject);
				}
			}
		}
		System.out.println("eqArrowsMap.size()::"+eqArrowsMap.size());
		System.out.println("eqContoursMap.size()::"+eqContoursMap.size());
		//排序EQArrows
		String[] names = folderFile.list();
		List<Integer> timeList = getTimeList(names);
		JSONArray imgArrowsArray = new JSONArray();
		JSONArray imgContoursArray = new JSONArray();
		for (int i = 0; i < timeList.size(); i++) {
			if (eqArrowsMap.containsKey(timeList.get(i))) {
				JSONObject resObject = new JSONObject();
				resObject.put("timeNode", timeList.get(i)+"s");
				resObject.put("imgs", eqArrowsMap.get(timeList.get(i)));
				imgArrowsArray.put(resObject);
			}
			if (eqContoursMap.containsKey(timeList.get(i))) {
				JSONObject resObject = new JSONObject();
				resObject.put("timeNode", timeList.get(i)+"s");
				resObject.put("imgs", eqContoursMap.get(timeList.get(i)));
				imgContoursArray.put(resObject);
			}
		}
		JSONObject resObject = new JSONObject();
		resObject.put("EQArrows", imgArrowsArray);
		resObject.put("EQContours", imgContoursArray);
		return packCallBack(callback, resObject.toString());
	}

	private static int getTimeFromFileName(String fileName) {
		return Integer.parseInt(fileName.substring(fileName.lastIndexOf("_")+1, fileName.indexOf("s.png")));
	}
	private static List<Integer> getTimeList(String[] fileNames) {
		if (fileNames == null || fileNames.length < 1) {
			return new ArrayList<>();
		}
		Set<Integer> set = new HashSet<>();
		for (String fName : fileNames) {
			String time = fName.substring(fName.lastIndexOf("_")+1, fName.indexOf("s.png"));
			set.add(Integer.parseInt(time));
		}
		List<Integer> timeList = new ArrayList<>();
		timeList.addAll(set);
		Collections.sort(timeList);
		System.out.println(timeList);
		return timeList;
	}
	public static void main(String[] args) {
//		String folder = Property.getStringProperty("filePath")+"1503820206706"+"/img";
//		File folderFile = new File(folder);
//		String[] names = folderFile.list();
//		getTimeList(names);
		getEQImgs("callback", "1503820206706");
	}
}
