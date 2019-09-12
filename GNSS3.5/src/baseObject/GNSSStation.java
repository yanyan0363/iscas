package baseObject;

 

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import beans.MEMSData;
import dataCache.DataCache;
import dataCache.DispWithMEMS;
import dataCache.Displacement;
import dbHelper.GNSSDBHelper;
import mathUtil.DoubleUtil;
import metaData.GPSMetaData;
import metaData.StaticMetaData;
import utils.Config;
import utils.StringHelper;

public class GNSSStation extends BaseStation {

	private SimpleDateFormat formatS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat formatMs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private SimpleDateFormat formatHMSMs = new SimpleDateFormat("HH:mm:ss.SSS");
	private SimpleDateFormat formatHMS = new SimpleDateFormat("HH:mm:ss");
	
	public GNSSStation(String id) {
		//super(id, n, p);
		super(id);
		
	}

	public BaseStation getStationInstance(){
    	if (this.init()) {
			return this;
		}
    	return null;
    }
	
	@Override
	public Displacement addData(String[] ss) {
		Displacement d = getDataFromStringLine(ss) ;
		return d;
	}
    public Displacement getDataFromStringLine(String[] ss){
    	try{
    		if(GPSMetaData.xIdx >= ss.length 
    				|| GPSMetaData.yIdx >= ss.length 
    				|| GPSMetaData.zIdx >= ss.length 
    				|| GPSMetaData.timeIdx >= ss.length
    				|| GPSMetaData.dxIdx >= ss.length 
    				|| GPSMetaData.dyIdx >= ss.length 
    				|| GPSMetaData.dzIdx >= ss.length
    				)
    			return  null ;
//    		GPSData gpsData = new GPSData(new XYZ(Double.valueOf(ss[GPSMetaData.xIdx]), Double.valueOf(ss[GPSMetaData.yIdx]), Double.valueOf(ss[GPSMetaData.zIdx])));
//    		Loc loc = new Loc(gpsData);
    		
    	   double xDisplacement = Double.valueOf(ss[GPSMetaData.dxIdx]);//东西向位移量
    	   double yDisplacement = Double.valueOf(ss[GPSMetaData.dyIdx]);//南北向位移量
    	   double zDisplacement = Double.valueOf(ss[GPSMetaData.dzIdx]);//地心向位移量
    	   double hDisplacement = Math.sqrt(DoubleUtil.mul(xDisplacement, xDisplacement)+DoubleUtil.mul(yDisplacement, yDisplacement));//水平向位移量
    	   Date t = StringHelper.getDateFromString(ss[GPSMetaData.timeIdx]) ;
    	   Displacement d = this.myDataCache.getDispWithMEMSByTime(t);
//    	   synchronized (d) {
    		   if (d.isDispExist()) {
    			   System.out.println("当前时刻位移数据已存在，添加失败。");
    			   return null;
    		   }
    		   //计算zDisToLastDis
//    		   double zDisToLastDis = this.myDataCache.calZDisToLastDis(t, zDisplacement);
    		   d.xDisplacement = xDisplacement;
    		   d.yDisplacement = yDisplacement;
    		   d.zDisplacement = zDisplacement;
    		   d.displacement2D = hDisplacement;
//    		   d.zDisToLastDis = zDisToLastDis;
    		   d.myCache = this.myDataCache;
    		   d.localTime = new Date();
    		   d.setDispExist(true);
    		   return d ;
//    	   }
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	return null ;
    }

   
    /**
     * 获取地心向位移
     * 远离地心为正，靠近地心为负
     * @param newGps
     * @return 地心向位移
     */
//    private double getZDisplacement(GPSData newGps) {
//    	if (zDis == 0.0) {
// 		   zDis = myLocation.gpsData.getZDis();
// 	   }
// 	   double d0 = zDis;
// 	   double d1 = newGps.getZDis();
//// 	   System.out.println("地心向位移：" + DoubleUtil.sub(d1, d0));
// 	   return DoubleUtil.sub(d1, d0);
//	}
	@Override
	public boolean init() {
		this.myDataCache = new DataCache(this) ;
//		this.myMetaData = new StationMetaData();
		if (StaticMetaData.getStationOriginalLocs().containsKey(ID)) {
			myLocation = StaticMetaData.getStationOriginalLocs().get(ID);
//			zDis = myLocation.gpsData.getZDis();//地心距离
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean handleMyMessage(String[] ss) {
		Displacement d = addData(ss) ;
		if (d == null) {
//			utils.SystemHelper.writeLog("添加台站位移数据失败：");
			return false;
		}
		//写入数据库
		if (this.isActive) {
			return GNSSDBHelper.insertRecord(this.ID, d);
		}
//		return GNSSDBHelper.insertRecord(this.ID, d);
		return true;
	}
	/**
	 * 判断是否触发地震事件
	 * @return true 表示触发地震事件
	 * false 表示不触发地震事件
	 */
//	public boolean isEventTriggerByDisplacement(displacement d){
////		displacement d = this.getLastData() ;//why last, not current?
//		if(! event.Event.isDisplacementDead(d))
//			return true; 
//		return false ;
//	}

	/**
	 * 获取地心方向上最大振幅的位移数据
	 */
	@Override
	public Displacement getVStartUpDisplacement() {
		if(this.myDataCache == null )
			return null ;
		//目前应用datacache中原始数据处理
		double max = 0 ;
		Displacement maxV = null ;
		for(int i = 0 ; i < this.myDataCache.myDataList.size(); i ++){
			Displacement d = this.myDataCache.myDataList.get(i) ;
			if(Math.abs(max) < Math.abs(d.zDisplacement))
			{
				max = d.zDisplacement ;
			    maxV= d; 
			}
		}
		return maxV;
	}
	/**
	 * 获取地心方向上最大振幅的位移数据
	 */
	@Override
	public double getVMaxDis() {
		if(this.myDataCache == null )
			return 0 ;
		//目前应用datacache中原始数据处理
		double max = 0 ;
		for(int i = 0 ; i < this.myDataCache.myDataList.size(); i ++){
			Displacement d = this.myDataCache.myDataList.get(i) ;
			if(Math.abs(max) < Math.abs(d.zDisplacement))
			{
				max = d.zDisplacement ;
			}
		}
		return max;
	}
	@Override
	public String curDisplacement() {
		return "{\"EWGPS\":"+getEWDisplacement()+",\"NSGPS\":"+getNSDisplacement()+",\"ZGPS\":"+getZDisplacement()+"}";
	}
	@Override
	public String curLast2MinDisp() {
		return "{\"EWGPS\":"+getLast2MinEWDisp()+",\"NSGPS\":"+getLast2MinNSDisp()+",\"ZGPS\":"+getLast2MinUDDisp()+"}";
	}
	@Override
	public String curDispWithMEMS() {
//		StringBuilder timeBuilder = new StringBuilder("\"time\":[");
		StringBuilder GPSEW = new StringBuilder("\"GPSEW\":[");
		StringBuilder GPSNS = new StringBuilder("\"GPSNS\":[");
		StringBuilder GPSZ = new StringBuilder("\"GPSZ\":[");
		StringBuilder MEMSEW = new StringBuilder("\"MEMSEW\":[");
		StringBuilder MEMSNS = new StringBuilder("\"MEMSNS\":[");
		StringBuilder MEMSZ = new StringBuilder("\"MEMSZ\":[");
		StringBuilder MEMSEWAcc = new StringBuilder("\"MEMSEWAcc\":[");
		StringBuilder MEMSNSAcc = new StringBuilder("\"MEMSNSAcc\":[");
		StringBuilder MEMSZAcc = new StringBuilder("\"MEMSZAcc\":[");
		if (this.myDataCache.myDataList.size() > 0) {
			List<DispWithMEMS> mydataList = this.myDataCache.myDataList;
			for (int i = 0; i < mydataList.size(); i++) {
				if (mydataList.get(i) instanceof DispWithMEMS) {
					DispWithMEMS dispWithMEMS = (DispWithMEMS)mydataList.get(i);
					if (dispWithMEMS.isDispExist()) {
						synchronized (dispWithMEMS) {
							String tString = formatMs.format(dispWithMEMS.time);
							GPSEW.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dispWithMEMS.xDisplacement)+"]},");
							GPSNS.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dispWithMEMS.yDisplacement)+"]},");
							GPSZ.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dispWithMEMS.zDisplacement)+"]},");
							dispWithMEMS.notifyAll();
						}
					}
					List<MEMSData> memslist = dispWithMEMS.memsDataList;
					if (memslist == null || memslist.size() <= 0) {
						continue;
					}
					for (int j = 0; j < memslist.size(); j++) {
						MEMSData memsData = memslist.get(j);
						synchronized (memsData) {
							String tString = formatMs.format(memsData.time);
							MEMSEW.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.getE())+"]},");
							MEMSNS.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.getN())+"]},");
							MEMSZ.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.getH())+"]},");
							MEMSEWAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.accE)+"]},");
							MEMSNSAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.accN)+"]},");
							MEMSZAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.accH)+"]},");
							memsData.notifyAll();
						}
						
					}
				}
			}
		}
//		timeBuilder.append("]");
		GPSEW.append("]");
		GPSNS.append("]");
		GPSZ.append("]");
		MEMSEW.append("]");
		MEMSNS.append("]");
		MEMSZ.append("]");
		MEMSEWAcc.append("]");
		MEMSNSAcc.append("]");
		MEMSZAcc.append("]");
		String reString = "{"
		+GPSEW.toString()+","+GPSNS.toString()+","+GPSZ.toString()+","
		+MEMSEW.toString()+","+MEMSNS.toString()+","+MEMSZ.toString()+","
		+MEMSEWAcc.toString()+","+MEMSNSAcc.toString()+","+MEMSZAcc.toString()+"}";
//		return "{"+timeBuilder.toString()+","
//		+GPSEW.toString()+","+GPSNS.toString()+","+GPSZ.toString()+","
//		+MEMSEW.toString()+","+MEMSNS.toString()+","+MEMSZ.toString()+"}";
		GPSEW = null;
		GPSNS = null;
		GPSZ = null;
		MEMSEW = null;
		MEMSNS = null;
		MEMSZ = null;
		MEMSEWAcc = null;
		MEMSNSAcc = null;
		MEMSZAcc = null;
		return reString;
	}
	//next:保持两分钟的长度，即-2~now min
	@Override
	public String curLast2MinDispWithMEMS() {
//		StringBuilder timeBuilder = new StringBuilder("\"time\":[");
		StringBuilder GPSEW = new StringBuilder("\"GPSEW\":[");
		StringBuilder GPSNS = new StringBuilder("\"GPSNS\":[");
		StringBuilder GPSZ = new StringBuilder("\"GPSZ\":[");
		StringBuilder MEMSEW = new StringBuilder("\"MEMSEW\":[");
		StringBuilder MEMSNS = new StringBuilder("\"MEMSNS\":[");
		StringBuilder MEMSZ = new StringBuilder("\"MEMSZ\":[");
		StringBuilder MEMSEWAcc = new StringBuilder("\"MEMSEWAcc\":[");
		StringBuilder MEMSNSAcc = new StringBuilder("\"MEMSNSAcc\":[");
		StringBuilder MEMSZAcc = new StringBuilder("\"MEMSZAcc\":[");
		int size = this.myDataCache.myDataList.size();
		if (size > 0) {
			Calendar now = Calendar.getInstance();
//			if (last.getTime().before(myDataCache.myDataList.get(size-1).time)) {
//				last.setTime(myDataCache.myDataList.get(size-1).time);
//			}
			Calendar last  = Calendar.getInstance();
			long t2Long = now.getTimeInMillis();
			last.setTimeInMillis(t2Long);
			last.add(Calendar.MINUTE, -2);
			Date lastT = last.getTime();
			List<DispWithMEMS> mydataList = this.myDataCache.myDataList;
			boolean firstDisp = true;
			DispWithMEMS endDisp = null;
			boolean firstMems = true;
			MEMSData endMems = null;
			for (int i = 0; i < mydataList.size(); i++) {
//				if (mydataList.get(i) instanceof DispWithMEMS && lastT.before(mydataList.get(i).time)) {
				if (lastT.before(mydataList.get(i).localTime) && !now.before(mydataList.get(i).localTime) && mydataList.get(i) instanceof DispWithMEMS) {
					DispWithMEMS dispWithMEMS = (DispWithMEMS)mydataList.get(i);
					if (dispWithMEMS.isDispExist()) {
						synchronized (dispWithMEMS) {
							//首补
							if (firstDisp) {
								firstDisp = false;
								long lastLong = lastT.getTime()+1000;
								while (dispWithMEMS.localTime.getTime() > lastLong) {
									String tString = formatMs.format(new Date(dispWithMEMS.time.getTime()-(dispWithMEMS.localTime.getTime()-lastLong)));
									GPSEW.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
									GPSNS.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
									GPSZ.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
									lastLong += 1000;
								}
								
							}
							endDisp = dispWithMEMS;
							String tString = formatMs.format(dispWithMEMS.time);
							GPSEW.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dispWithMEMS.xDisplacement)+"]},");
							GPSNS.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dispWithMEMS.yDisplacement)+"]},");
							GPSZ.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dispWithMEMS.zDisplacement)+"]},");
							dispWithMEMS.notifyAll();
						}
					}
					List<MEMSData> memslist = dispWithMEMS.memsDataList;
					if (memslist == null || memslist.size() <= 0) {
						continue;
					}
					for (int j = 0; j < memslist.size(); j++) {
						MEMSData memsData = memslist.get(j);
						if(lastT.before(memsData.localTime) && !now.before(memsData.localTime)){
							synchronized (memsData) {
								//首补
								if (firstMems) {
									firstMems = false;
									long lastLong = lastT.getTime()+1000;
									while (memsData.localTime.getTime() > lastLong) {
										String tString = formatMs.format(new Date(memsData.time.getTime()-(memsData.localTime.getTime()-lastLong)));
										MEMSEW.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
										MEMSNS.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
										MEMSZ.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
										MEMSEWAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
										MEMSNSAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
										MEMSZAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
										lastLong += 1000/Config.MEMSHz;
									}
								}
								endMems = memsData;
								String tString = formatMs.format(memsData.time);
								MEMSEW.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.getE())+"]},");
								MEMSNS.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.getN())+"]},");
								MEMSZ.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.getH())+"]},");
								MEMSEWAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.accE)+"]},");
								MEMSNSAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.accN)+"]},");
								MEMSZAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.accH)+"]},");
								memsData.notifyAll();
							}
						}
					}
				}
			}
			//尾补
			if (endDisp != null) {
				long endDispTLong = endDisp.localTime.getTime();
				long ttLong = endDispTLong+1000;
//				System.out.println("disp: ttLong<lastT.getTime()+2000  " + (ttLong < lastT.getTime()+2000));
				while (ttLong < t2Long) {
					String tString = formatMs.format(new Date(endDisp.time.getTime() + (ttLong-endDispTLong)));
					GPSEW.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
					GPSNS.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
					GPSZ.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
					ttLong += 1000;
				}
			}
			if (endMems != null ) {
				long endMemsLong = endMems.localTime.getTime();
				long ts = 1000/Config.MEMSHz;
				long ttLong = endMemsLong + ts;
//				System.out.println("endMemsLong:" + endMemsLong + " t2Long:" + t2Long);
				while (ttLong < t2Long) {
//					System.out.println("ttlong::" + ttLong + " llst:" + t2Long +" < ? "+(ttLong < t2Long));
					String tString = formatMs.format(new Date(endMems.time.getTime()+(ttLong-endMemsLong)));
					MEMSEW.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
					MEMSNS.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
					MEMSZ.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
					MEMSEWAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
					MEMSNSAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
					MEMSZAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
					ttLong += ts;
				}
			}
		}
//		timeBuilder.append("]");
		GPSEW.append("]");
		GPSNS.append("]");
		GPSZ.append("]");
		MEMSEW.append("]");
		MEMSNS.append("]");
		MEMSZ.append("]");
		MEMSEWAcc.append("]");
		MEMSNSAcc.append("]");
		MEMSZAcc.append("]");
		String reString = "{"
		+GPSEW.toString()+","+GPSNS.toString()+","+GPSZ.toString()+","
		+MEMSEW.toString()+","+MEMSNS.toString()+","+MEMSZ.toString()+","
		+MEMSEWAcc.toString()+","+MEMSNSAcc.toString()+","+MEMSZAcc.toString()+"}";
		GPSEW = null;
		GPSNS = null;
		GPSZ = null;
		MEMSEW = null;
		MEMSNS = null;
		MEMSZ = null;
		MEMSEWAcc = null;
		MEMSNSAcc = null;
		MEMSZAcc = null;
		return reString;
//		return "{"+timeBuilder.toString()+","
//		+GPSEW.toString()+","+GPSNS.toString()+","+GPSZ.toString()+","
//		+MEMSEW.toString()+","+MEMSNS.toString()+","+MEMSZ.toString()+"}";
	}
	
	//next:保持x分钟的长度，即-x~now min
	/**
	 * 获取前x分钟长度的DispWithMEMS，即-x~now min
	 * @param x
	 * @return
	 */
	@Override
	public String curLastXMinDispWithMEMS(int x) {
		StringBuilder GPSEW = new StringBuilder("\"GPSEW\":[");
		StringBuilder GPSNS = new StringBuilder("\"GPSNS\":[");
		StringBuilder GPSZ = new StringBuilder("\"GPSZ\":[");
		StringBuilder MEMSEW = new StringBuilder("\"MEMSEW\":[");
		StringBuilder MEMSNS = new StringBuilder("\"MEMSNS\":[");
		StringBuilder MEMSZ = new StringBuilder("\"MEMSZ\":[");
		StringBuilder MEMSEWAcc = new StringBuilder("\"MEMSEWAcc\":[");
		StringBuilder MEMSNSAcc = new StringBuilder("\"MEMSNSAcc\":[");
		StringBuilder MEMSZAcc = new StringBuilder("\"MEMSZAcc\":[");
		int size = this.myDataCache.myDataList.size();
		if (size > 0) {
			Calendar now = Calendar.getInstance();
			Calendar last  = Calendar.getInstance();
			long t2Long = now.getTimeInMillis();
			last.setTimeInMillis(t2Long);
			last.add(Calendar.MINUTE, -1*x);
			Date lastT = last.getTime();
			List<DispWithMEMS> mydataList = this.myDataCache.myDataList;
			boolean firstDisp = true;
			DispWithMEMS endDisp = null;
			boolean firstMems = true;
			MEMSData endMems = null;
			for (int i = 0; i < mydataList.size(); i++) {
				if (lastT.before(mydataList.get(i).localTime) && !now.before(mydataList.get(i).localTime) && mydataList.get(i) instanceof DispWithMEMS) {
					DispWithMEMS dispWithMEMS = (DispWithMEMS)mydataList.get(i);
					if (dispWithMEMS.isDispExist()) {
						synchronized (dispWithMEMS) {
							//首补
							if (firstDisp) {
								firstDisp = false;
								long lastLong = lastT.getTime()+1000;
								while (dispWithMEMS.localTime.getTime() > lastLong) {
									String tString = formatS.format(new Date(dispWithMEMS.time.getTime()-(dispWithMEMS.localTime.getTime()-lastLong)));
									GPSEW.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
									GPSNS.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
									GPSZ.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
									lastLong += 1000;
								}
							}
							endDisp = dispWithMEMS;
							String tString = formatS.format(dispWithMEMS.time);
							GPSEW.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dispWithMEMS.xDisplacement)+"]},");
							GPSNS.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dispWithMEMS.yDisplacement)+"]},");
							GPSZ.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dispWithMEMS.zDisplacement)+"]},");
							dispWithMEMS.notifyAll();
						}
					}
					List<MEMSData> memslist = dispWithMEMS.memsDataList;
					if (memslist == null || memslist.size() <= 0) {
						continue;
					}
					double maxAbsE = 0, maxAbsN = 0, maxAbsH = 0;
					for (int j = 0; j < memslist.size(); j++) {
						MEMSData memsData = memslist.get(j);
						if(lastT.before(memsData.localTime) && !now.before(memsData.localTime)){
							synchronized (memsData) {
								//首补
								if (firstMems) {
									firstMems = false;
									long lastLong = lastT.getTime()+1000;
									while (memsData.localTime.getTime() > lastLong) {
										String tString = formatS.format(new Date(memsData.time.getTime()-(memsData.localTime.getTime()-lastLong)));
										MEMSEW.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
										MEMSNS.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
										MEMSZ.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
										lastLong += 1000;
									}
									while (memsData.localTime.getTime() > lastLong) {
										String tString = formatMs.format(new Date(memsData.time.getTime()-(memsData.localTime.getTime()-lastLong)));
										MEMSEWAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
										MEMSNSAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
										MEMSZAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
										lastLong += 1000/Config.MEMSHz;
									}
								}
								endMems = memsData;
								String tString = formatMs.format(memsData.time);
								MEMSEWAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.accE)+"]},");
								MEMSNSAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.accN)+"]},");
								MEMSZAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.accH)+"]},");
								if (Math.abs(maxAbsE) < Math.abs(memsData.getE())) {
									maxAbsE = memsData.getE();
								}
								if (Math.abs(maxAbsN) < Math.abs(memsData.getN())) {
									maxAbsN = memsData.getN();
								}
								if (Math.abs(maxAbsH) < Math.abs(memsData.getH())) {
									maxAbsH = memsData.getH();
								}
								memsData.notifyAll();
							}
						}
					}
					String tString = formatS.format(dispWithMEMS.time);
					MEMSEW.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", maxAbsE)+"]},");
					MEMSNS.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", maxAbsN)+"]},");
					MEMSZ.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", maxAbsH)+"]},");
				}
			}
			//尾补
			if (endDisp != null) {
				long endDispTLong = endDisp.localTime.getTime();
				long ttLong = endDispTLong+1000;
//				System.out.println("disp: ttLong<lastT.getTime()+2000  " + (ttLong < lastT.getTime()+2000));
				while (ttLong < t2Long) {
					String tString = formatS.format(new Date(endDisp.time.getTime() + (ttLong-endDispTLong)));
					GPSEW.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
					GPSNS.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
					GPSZ.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
					MEMSEW.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
					MEMSNS.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
					MEMSZ.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
					ttLong += 1000;
				}
			}
			if (endMems != null ) {
				long endMemsLong = endMems.localTime.getTime();
				long ts = 1000/Config.MEMSHz;
				long ttLong = endMemsLong + ts;
				while (ttLong < t2Long) {
					String tString = formatMs.format(new Date(endMems.time.getTime()+(ttLong-endMemsLong)));
					MEMSEWAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
					MEMSNSAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
					MEMSZAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
					ttLong += ts;
				}
			}
		}
		GPSEW.append("]");
		GPSNS.append("]");
		GPSZ.append("]");
		MEMSEW.append("]");
		MEMSNS.append("]");
		MEMSZ.append("]");
		MEMSEWAcc.append("]");
		MEMSNSAcc.append("]");
		MEMSZAcc.append("]");
		String reString = "{"
				+GPSEW.toString()+","+GPSNS.toString()+","+GPSZ.toString()+","
				+MEMSEW.toString()+","+MEMSNS.toString()+","+MEMSZ.toString()+","
				+MEMSEWAcc.toString()+","+MEMSNSAcc.toString()+","+MEMSZAcc.toString()+"}";
		GPSEW = null;
		GPSNS = null;
		GPSZ = null;
		MEMSEW = null;
		MEMSNS = null;
		MEMSZ = null;
		MEMSEWAcc = null;
		MEMSNSAcc = null;
		MEMSZAcc = null;
		return reString;
//		return "{"+timeBuilder.toString()+","
//		+GPSEW.toString()+","+GPSNS.toString()+","+GPSZ.toString()+","
//		+MEMSEW.toString()+","+MEMSNS.toString()+","+MEMSZ.toString()+"}";
	}
	/**
	 * 获取前x分钟长度的DispWithMEMSWithoutFitting，即-x~now min
	 * @param x
	 * @return
	 */
	@Override
	public String curLastXMinDispWithMEMSWithoutFitting(int x) {
		StringBuilder GPSEW = new StringBuilder("\"GPSEW\":[");
		StringBuilder GPSNS = new StringBuilder("\"GPSNS\":[");
		StringBuilder GPSZ = new StringBuilder("\"GPSZ\":[");
		StringBuilder MEMSEWAcc = new StringBuilder("\"MEMSEWAcc\":[");
		StringBuilder MEMSNSAcc = new StringBuilder("\"MEMSNSAcc\":[");
		StringBuilder MEMSZAcc = new StringBuilder("\"MEMSZAcc\":[");
		int size = this.myDataCache.myDataList.size();
		if (size > 0) {
			Calendar now = Calendar.getInstance();
			Calendar last  = Calendar.getInstance();
			long t2Long = now.getTimeInMillis();
			last.setTimeInMillis(t2Long);
			last.add(Calendar.MINUTE, -1*x);
			Date lastT = last.getTime();
			List<DispWithMEMS> mydataList = this.myDataCache.myDataList;
			boolean firstDisp = true;
			DispWithMEMS endDisp = null;
			boolean firstMems = true;
			MEMSData endMems = null;
			for (int i = 0; i < mydataList.size(); i++) {
				if (lastT.before(mydataList.get(i).localTime) && !now.before(mydataList.get(i).localTime) && mydataList.get(i) instanceof DispWithMEMS) {
					DispWithMEMS dispWithMEMS = (DispWithMEMS)mydataList.get(i);
					if (dispWithMEMS.isDispExist()) {
						synchronized (dispWithMEMS) {
							//首补
							if (firstDisp) {
								firstDisp = false;
								long lastLong = lastT.getTime()+1000;
								while (dispWithMEMS.localTime.getTime() > lastLong) {
									String tString = formatS.format(new Date(dispWithMEMS.time.getTime()-(dispWithMEMS.localTime.getTime()-lastLong)));
									GPSEW.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
									GPSNS.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
									GPSZ.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
									lastLong += 1000;
								}
							}
							endDisp = dispWithMEMS;
							String tString = formatS.format(dispWithMEMS.time);
							GPSEW.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dispWithMEMS.xDisplacement)+"]},");
							GPSNS.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dispWithMEMS.yDisplacement)+"]},");
							GPSZ.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dispWithMEMS.zDisplacement)+"]},");
							dispWithMEMS.notifyAll();
						}
					}
					List<MEMSData> memslist = dispWithMEMS.memsDataList;
					if (memslist == null || memslist.size() <= 0) {
						continue;
					}
					double maxAbsE = 0, maxAbsN = 0, maxAbsH = 0;
					for (int j = 0; j < memslist.size(); j++) {
						MEMSData memsData = memslist.get(j);
						if(lastT.before(memsData.localTime) && !now.before(memsData.localTime)){
							synchronized (memsData) {
								//首补
								if (firstMems) {
									firstMems = false;
									long lastLong = lastT.getTime()+1000;
									while (memsData.localTime.getTime() > lastLong) {
										String tString = formatMs.format(new Date(memsData.time.getTime()-(memsData.localTime.getTime()-lastLong)));
										MEMSEWAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
										MEMSNSAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
										MEMSZAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
										lastLong += 1000/Config.MEMSHz;
									}
								}
								endMems = memsData;
								String tString = formatMs.format(memsData.time);
								MEMSEWAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.accE)+"]},");
								MEMSNSAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.accN)+"]},");
								MEMSZAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", memsData.accH)+"]},");
								if (Math.abs(maxAbsE) < Math.abs(memsData.getE())) {
									maxAbsE = memsData.getE();
								}
								if (Math.abs(maxAbsN) < Math.abs(memsData.getN())) {
									maxAbsN = memsData.getN();
								}
								if (Math.abs(maxAbsH) < Math.abs(memsData.getH())) {
									maxAbsH = memsData.getH();
								}
								memsData.notifyAll();
							}
						}
					}
				}
			}
			//尾补
			if (endDisp != null) {
				long endDispTLong = endDisp.localTime.getTime();
				long ttLong = endDispTLong+1000;
//				System.out.println("disp: ttLong<lastT.getTime()+2000  " + (ttLong < lastT.getTime()+2000));
				while (ttLong < t2Long) {
					String tString = formatS.format(new Date(endDisp.time.getTime() + (ttLong-endDispTLong)));
					GPSEW.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
					GPSNS.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
					GPSZ.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\",0]},");
					ttLong += 1000;
				}
			}
			if (endMems != null ) {
				long endMemsLong = endMems.localTime.getTime();
				long ts = 1000/Config.MEMSHz;
				long ttLong = endMemsLong + ts;
				while (ttLong < t2Long) {
					String tString = formatMs.format(new Date(endMems.time.getTime()+(ttLong-endMemsLong)));
					MEMSEWAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
					MEMSNSAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
					MEMSZAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\",0]},");
					ttLong += ts;
				}
			}
		}
		GPSEW.append("]");
		GPSNS.append("]");
		GPSZ.append("]");
		MEMSEWAcc.append("]");
		MEMSNSAcc.append("]");
		MEMSZAcc.append("]");
		String reString = "{"
				+GPSEW.toString()+","+GPSNS.toString()+","+GPSZ.toString()+","
				+MEMSEWAcc.toString()+","+MEMSNSAcc.toString()+","+MEMSZAcc.toString()+"}";
		GPSEW = null;
		GPSNS = null;
		GPSZ = null;
		MEMSEWAcc = null;
		MEMSNSAcc = null;
		MEMSZAcc = null;
		return reString;
	}

//	@Override
//	public String curDisplacement() {
//		return "{\"x\":"+getXDisplacement()+",\"y\":"+getYDisplacement()+",\"z\":"+getZDisplacement()+",\"dis2D\":"+getDisplacement2D()+"}";
//	}
	@Override
	public String curDis2DZ(){
		return "{\"z\":"+getZDisplacement()+",\"dis2D\":"+getDisplacement2D()+"}";
	}
	/**
	 * 前30s数据
	 */
	@Override
	public String last30sDis2DZ(){
		StringBuilder disHBuilder = new StringBuilder();
		StringBuilder disVBuilder = new StringBuilder();
		StringBuilder timeBuilder = new StringBuilder();
		if ( this.myDataCache.myDataList.size() > 0) {
			Calendar last = Calendar.getInstance();
			last.add(Calendar.SECOND, -30);
			Date lastT = last.getTime();
			List<DispWithMEMS> mydataList = this.myDataCache.myDataList;
			for (int i = mydataList.size()-1; i >= 0; i--) {
//				if (mydataList.get(i).isDispExist() && lastT.before(mydataList.get(i).time)) {
				if (mydataList.get(i).isDispExist()) {
					if (lastT.before(mydataList.get(i).localTime)) {
						disHBuilder.insert(0,String.format("%.3f", mydataList.get(i).displacement2D)+",");
						disVBuilder.insert(0,String.format("%.3f", mydataList.get(i).zDisplacement)+",");
						timeBuilder.insert(0, "\""+formatHMS.format(mydataList.get(i).time)+"\""+",");
					}else {
						break;
					}
				}
			}
		}
		disHBuilder.insert(0,"\"disH\":[");
		disVBuilder.insert(0,"\"disV\":[");
		timeBuilder.insert(0, "\"time\":[");
		disHBuilder.append("]");
		disVBuilder.append("]");
		timeBuilder.append("]");
		String reString = "{"+disHBuilder.toString()+","+disVBuilder.toString()+","+timeBuilder.toString()+"}";
		disHBuilder = null;
		disVBuilder = null;
		timeBuilder = null;
		return reString;
	}
	/**
	 * 前30个数据
	 */
	@Override
	public String last30Dis2DZ(){
//		StringBuilder disHBuilder = new StringBuilder("\"disH\":[");
//		StringBuilder disVBuilder = new StringBuilder("\"disV\":[");
//		StringBuilder timeBuilder = new StringBuilder("\"time\":[");
		StringBuilder disHBuilder = new StringBuilder();
		StringBuilder disVBuilder = new StringBuilder();
		StringBuilder timeBuilder = new StringBuilder();
		if ( this.myDataCache.myDataList.size() > 0) {
			List<DispWithMEMS> mydataList = this.myDataCache.myDataList;
			
			for (int i = mydataList.size()-1, j = 0; i >= 0 &&j<30; i--, j++) {
				if (mydataList.get(i).isDispExist()) {
//					disHBuilder.append(String.format("%.3f", mydataList.get(i).displacement2D)+",");
//					disVBuilder.append(String.format("%.3f", mydataList.get(i).zDisplacement)+",");
//					timeBuilder.append("\""+formatHMS.format(mydataList.get(i).time)+"\""+",");
					disHBuilder.insert(0,String.format("%.3f", mydataList.get(i).displacement2D)+",");
					disVBuilder.insert(0,String.format("%.3f", mydataList.get(i).zDisplacement)+",");
					timeBuilder.insert(0, "\""+formatHMS.format(mydataList.get(i).time)+"\""+",");
				}
			}
		}
		disHBuilder.insert(0,"\"disH\":[");
		disVBuilder.insert(0,"\"disV\":[");
		timeBuilder.insert(0, "\"time\":[");
		disHBuilder.append("]");
		disVBuilder.append("]");
		timeBuilder.append("]");
		String reString = "{"+disHBuilder.toString()+","+disVBuilder.toString()+","+timeBuilder.toString()+"}";
		disHBuilder = null;
		disVBuilder = null;
		timeBuilder = null;
		return reString;
	}
	@Override
	public String curDis2DZ(Date startTime, Date endTime){
		StringBuilder zdisBuilder = new StringBuilder("\"dis\":[");
		StringBuilder timeBuilder = new StringBuilder("\"time\":[");
		StringBuilder ddisBuilder = new StringBuilder("\"dis\":[");
		if ( this.myDataCache.myDataList.size() > 0) {
			List<DispWithMEMS> mydataList = this.myDataCache.myDataList;
			for (int i = 0; i < mydataList.size(); i++) {
				Displacement dis = mydataList.get(i);
				if (dis.time.before(startTime)) {
					continue;
				}
				if (dis.time.after(endTime)) {
					break;
				}
				zdisBuilder.append(String.format("%.3f", mydataList.get(i).zDisplacement)+",");
				ddisBuilder.append(String.format("%.3f", mydataList.get(i).displacement2D)+",");
				timeBuilder.append("\""+formatS.format(mydataList.get(i).time)+"\""+",");
			}
		}
		zdisBuilder.append("]");
		ddisBuilder.append("]");
		timeBuilder.append("]");
		String reString = "{\"z\":{"+zdisBuilder.toString()+","+timeBuilder.toString()+"},\"dis2D\":{"+ddisBuilder.toString()+","+timeBuilder.toString()+"}}";
		zdisBuilder = null;
		timeBuilder = null;
		ddisBuilder = null;
		return reString;
	}
	private String getEWDisplacement(){
		StringBuilder disBuilder = new StringBuilder("\"dis\":[");
		StringBuilder timeBuilder = new StringBuilder("\"time\":[");
		if ( this.myDataCache.myDataList.size() > 0) {
			List<DispWithMEMS> mydataList = this.myDataCache.myDataList;
			for (int i = 0; i < mydataList.size(); i++) {
				if (mydataList.get(i).isDispExist()) {
					disBuilder.append(String.format("%.3f", mydataList.get(i).xDisplacement)+",");
					timeBuilder.append("\""+formatS.format(mydataList.get(i).time)+"\""+",");
				}
			}
		}
		disBuilder.append("]");
		timeBuilder.append("]");
		String reString = "{"+disBuilder.toString()+","+timeBuilder+"}";
		disBuilder = null;
		timeBuilder = null;
		return reString;
	}
	private String getLast2MinEWDisp(){
		StringBuilder disBuilder = new StringBuilder();
		StringBuilder timeBuilder = new StringBuilder();
		if ( this.myDataCache.myDataList.size() > 0) {
			Calendar last = Calendar.getInstance();
			last.add(Calendar.MINUTE, -2);
			Date lastT = last.getTime();
			List<DispWithMEMS> mydataList = this.myDataCache.myDataList;
			for (int i = mydataList.size()-1; i >= 0; i--) {
//				if (mydataList.get(i).isDispExist() && lastT.before(mydataList.get(i).time)) {
				if (mydataList.get(i).isDispExist() && lastT.before(mydataList.get(i).localTime)) {
					disBuilder.insert(0,String.format("%.3f", mydataList.get(i).xDisplacement)+",");
					timeBuilder.insert(0, "\""+formatHMS.format(mydataList.get(i).time)+"\""+",");
				}
			}
			last = null;
			lastT = null;
		}
		disBuilder.insert(0,"\"dis\":[");
		timeBuilder.insert(0, "\"time\":[");
		disBuilder.append("]");
		timeBuilder.append("]");
		String reString = "{"+disBuilder.toString()+","+timeBuilder.toString()+"}";
		disBuilder = null;
		timeBuilder = null;
		return reString;
	}
	private String getLast2MinNSDisp(){
		StringBuilder disBuilder = new StringBuilder();
		StringBuilder timeBuilder = new StringBuilder();
		if ( this.myDataCache.myDataList.size() > 0) {
			Calendar last = Calendar.getInstance();
			last.add(Calendar.MINUTE, -2);
			Date lastT = last.getTime();
			List<DispWithMEMS> mydataList = this.myDataCache.myDataList;
			for (int i = mydataList.size()-1; i >= 0; i--) {
//				if (mydataList.get(i).isDispExist() && lastT.before(mydataList.get(i).time)) {
				if (mydataList.get(i).isDispExist() && lastT.before(mydataList.get(i).localTime)) {
					disBuilder.insert(0,String.format("%.3f", mydataList.get(i).yDisplacement)+",");
					timeBuilder.insert(0, "\""+formatHMS.format(mydataList.get(i).time)+"\""+",");
				}
			}
			last = null;
			lastT = null;
		}
		disBuilder.insert(0,"\"dis\":[");
		timeBuilder.insert(0, "\"time\":[");
		disBuilder.append("]");
		timeBuilder.append("]");
		String reString = "{"+disBuilder.toString()+","+timeBuilder.toString()+"}";
		disBuilder = null;
		timeBuilder = null;
		return reString;
	}
	private String getLast2MinUDDisp(){
		StringBuilder disBuilder = new StringBuilder();
		StringBuilder timeBuilder = new StringBuilder();
		if ( this.myDataCache.myDataList.size() > 0) {
			Calendar last = Calendar.getInstance();
			last.add(Calendar.MINUTE, -2);
			Date lastT = last.getTime();
			List<DispWithMEMS> mydataList = this.myDataCache.myDataList;
			for (int i = mydataList.size()-1; i >= 0; i--) {
//				if (mydataList.get(i).isDispExist() && lastT.before(mydataList.get(i).time)) {
				if (mydataList.get(i).isDispExist() && lastT.before(mydataList.get(i).localTime)) {
					disBuilder.insert(0,String.format("%.3f", mydataList.get(i).zDisplacement)+",");
					timeBuilder.insert(0, "\""+formatHMS.format(mydataList.get(i).time)+"\""+",");
				}
			}
			last = null;
			lastT = null;
		}
		disBuilder.insert(0,"\"dis\":[");
		timeBuilder.insert(0, "\"time\":[");
		disBuilder.append("]");
		timeBuilder.append("]");
		String reString = "{"+disBuilder.toString()+","+timeBuilder.toString()+"}";
		disBuilder = null;
		timeBuilder = null;
		return reString;
	}
//	private String getDispWithMEMS(){
//		
//	}
	private String getEWMEMS(){
		StringBuilder disBuilder = new StringBuilder("\"dis\":[");
		StringBuilder timeBuilder = new StringBuilder("\"time\":[");
		if ( this.myDataCache.myDataList.size() > 0) {
			List<DispWithMEMS> mydataList = this.myDataCache.myDataList;
			for (int i = 0; i < mydataList.size(); i++) {
				if (mydataList.get(i) instanceof DispWithMEMS) {
					DispWithMEMS dispWithMEMS = (DispWithMEMS)mydataList.get(i);
					List<MEMSData> memslist = dispWithMEMS.memsDataList;
					if (memslist == null || memslist.size() <= 0) {
						continue;
					}
					for (int j = 0; j < memslist.size(); j++) {
						disBuilder.append(String.format("%.3f", memslist.get(j).getE())+",");
						timeBuilder.append("\""+formatMs.format(memslist.get(j).time)+"\""+",");
					}
				}
			}
		}
		disBuilder.append("]");
		timeBuilder.append("]");
		String reString = "{"+disBuilder.toString()+","+timeBuilder+"}";
		disBuilder = null;
		timeBuilder = null;
		return reString;
	}
	private String getNSDisplacement(){
		StringBuilder disBuilder = new StringBuilder("\"dis\":[");
		StringBuilder timeBuilder = new StringBuilder("\"time\":[");
		if ( this.myDataCache.myDataList.size() > 0) {
			List<DispWithMEMS> mydataList = this.myDataCache.myDataList;
			for (int i = 0; i < mydataList.size(); i++) {
				if (mydataList.get(i).isDispExist()) {
					disBuilder.append(String.format("%.3f", mydataList.get(i).yDisplacement)+",");
					timeBuilder.append("\""+formatS.format(mydataList.get(i).time)+"\""+",");
				}
			}
		}
		disBuilder.append("]");
		timeBuilder.append("]");
		String reString = "{"+disBuilder.toString()+","+timeBuilder+"}";
		disBuilder = null;
		timeBuilder = null;
		return reString;
	}
	private String getZDisplacement(){
		StringBuilder disBuilder = new StringBuilder("\"dis\":[");
		StringBuilder timeBuilder = new StringBuilder("\"time\":[");
		if ( this.myDataCache.myDataList.size() > 0) {
			List<DispWithMEMS> mydataList = this.myDataCache.myDataList;
			for (int i = 0; i < mydataList.size(); i++) {
				if (mydataList.get(i).isDispExist()) {
					disBuilder.append(String.format("%.3f", mydataList.get(i).zDisplacement)+",");
					timeBuilder.append("\""+formatS.format(mydataList.get(i).time)+"\""+",");
				}
			}
		}
		disBuilder.append("]");
		timeBuilder.append("]");
		String reString = "{"+disBuilder.toString()+","+timeBuilder+"}";
		disBuilder = null;
		timeBuilder = null;
		return reString;
	}

	private String getNSMEMS(){
		StringBuilder disBuilder = new StringBuilder("\"dis\":[");
		StringBuilder timeBuilder = new StringBuilder("\"time\":[");
		if ( this.myDataCache.myDataList.size() > 0) {
			List<DispWithMEMS> mydataList = this.myDataCache.myDataList;
			for (int i = 0; i < mydataList.size(); i++) {
				if (mydataList.get(i) instanceof DispWithMEMS) {
					DispWithMEMS dispWithMEMS = (DispWithMEMS)mydataList.get(i);
					List<MEMSData> memslist = dispWithMEMS.memsDataList;
					if (memslist == null || memslist.size() <= 0) {
						continue;
					}
					for (int j = 0; j < memslist.size(); j++) {
						disBuilder.append(String.format("%.3f", memslist.get(j).getN())+",");
						timeBuilder.append("\""+formatMs.format(memslist.get(j).time)+"\""+",");
					}
				}
			}
		}
		disBuilder.append("]");
		timeBuilder.append("]");
		String reString = "{"+disBuilder.toString()+","+timeBuilder+"}";
		disBuilder = null;
		timeBuilder = null;
		return reString;
	}
	private String getZMEMS(){
		StringBuilder disBuilder = new StringBuilder("\"dis\":[");
		StringBuilder timeBuilder = new StringBuilder("\"time\":[");
		if ( this.myDataCache.myDataList.size() > 0) {
			List<DispWithMEMS> mydataList = this.myDataCache.myDataList;
			for (int i = 0; i < mydataList.size(); i++) {
				if (mydataList.get(i) instanceof DispWithMEMS) {
					DispWithMEMS dispWithMEMS = (DispWithMEMS)mydataList.get(i);
					List<MEMSData> memslist = dispWithMEMS.memsDataList;
					if (memslist == null || memslist.size() <= 0) {
						continue;
					}
					for (int j = 0; j < memslist.size(); j++) {
						disBuilder.append(String.format("%.3f", memslist.get(j).getH())+",");
						timeBuilder.append("\""+formatMs.format(memslist.get(j).time)+"\""+",");
					}
				}
			}
		}
		disBuilder.append("]");
		timeBuilder.append("]");
		String reString = "{"+disBuilder.toString()+","+timeBuilder+"}";
		disBuilder = null;
		timeBuilder = null;
		return reString;
	}
	private String getDisplacement2D(){
		StringBuilder disBuilder = new StringBuilder("\"dis\":[");
		StringBuilder timeBuilder = new StringBuilder("\"time\":[");
		if ( this.myDataCache.myDataList.size() > 0) {
			List<DispWithMEMS> mydataList = this.myDataCache.myDataList;
			for (int i = 0; i < mydataList.size(); i++) {
				disBuilder.append(String.format("%.3f", mydataList.get(i).displacement2D)+",");
				timeBuilder.append("\""+formatS.format(mydataList.get(i).time)+"\""+",");
			}
		}
		disBuilder.append("]");
		timeBuilder.append("]");
		String reString = "{"+disBuilder.toString()+","+timeBuilder+"}";
		disBuilder = null;
		timeBuilder = null;
		return reString;
	}


}
