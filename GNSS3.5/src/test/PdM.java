package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.interop.AutomationException;

import arcgis.ArcgisHelper;
import arcgis.GisHelper;
import baseObject.BaseStation;
import baseObject.GNSSStation;
import beans.MEMSData;
import dataCache.DataCache;
import dataCache.DispWithMEMS;
import event.ButterWorthFilter;
import mathUtil.DoubleUtil;
import metaData.StaticMetaData;
import utils.Config;

public class PdM {

//	public static HashSet<BaseStation> triggerSt = new HashSet<>();//触发的台站列表
//	public static void main(String[] args) {
////		FrameRunner.startUp();
//		StaticMetaData.initStaticData();
//		File folder = new File("E:\\2019\\GNSS\\JP\\JPTest\\强震仪数据");
//		if (!folder.exists() || !folder.isDirectory()) {
//			return;
//		}
//		File[] files = folder.listFiles();
//		//IWT013  2019/05/14 14:46:43.860
//		//MYG003  2019/05/14 14:46:42.670
//		//IWT009  2019/05/14 14:46:40.640
//		//MYG011  2019/05/14 14:46:37.720
//		//MYG001  2019/05/14 14:46:30.760
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//		for (int i = 0; i < files.length; i++) {
//			File file = files[i];
//			System.out.println(file.getName().substring(0, 6));
//			BaseStation station = new GNSSStation(file.getName().substring(0, 6)).getStationInstance() ;
//			triggerSt.add(station);
//			station.myDataCache = new DataCache(station);
//			try {
//				switch (station.ID) {
//				case "IWT013":
//					station.myDataCache.triggerTime = format.parse("2019-05-14 14:46:43.860");
//					break;
//				case "MYG003":
//					station.myDataCache.triggerTime = format.parse("2019-05-14 14:46:42.670");
//					break;
//				case "IWT009":
//					station.myDataCache.triggerTime = format.parse("2019-05-14 14:46:40.640");
//					break;
//				case "MYG011":
//					station.myDataCache.triggerTime = format.parse("2019-05-14 14:46:37.720");
//					break;
//				case "MYG001":
//					station.myDataCache.triggerTime = format.parse("2019-05-14 14:46:30.760");
//					break;
//				default:
//					break;
//				}
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			BufferedReader reader = null;
//			try {
//				reader = new BufferedReader(new FileReader(file));
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			String tmpLine = null;
//			//startTime:: 14:46:23
//			Calendar calendar = Calendar.getInstance();
//			calendar.set(Calendar.HOUR_OF_DAY, 14);
//			calendar.set(Calendar.MINUTE, 46);
//			calendar.set(Calendar.SECOND, 23);
//			calendar.set(Calendar.MILLISECOND, 0);
//			try {
//				long time = calendar.getTimeInMillis();
//				while ((tmpLine = reader.readLine())!= null) {
////				System.out.println(tmpLine);
//					MEMSData memsData = new MEMSData();
//					String items[] = tmpLine.split("	");
//					double tt = Double.parseDouble(items[1]);
//					Date curDate = new Date(time+(long)(tt*1000));
//					memsData.time = curDate;
//					memsData.accE = Double.parseDouble(items[2]);
//					memsData.accN = Double.parseDouble(items[3]);
//					memsData.accH = Double.parseDouble(items[4]);
//					station.myDataCache.insertMEMSData(memsData);
////			station.myDataCache.insertMEMSData(memsData);
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		PdM pdM = new PdM();
//		pdM.calMEMSPdMag();
//	}
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
//				sum+=Math.abs(signal[i]);
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
//	 * MEMS数据计算震级
//	 */
//	public void calMEMSPdMag_copy() {
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
//				if (dispWithMEMS.time.getTime() > t2.getTime()) {
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
//					if (count>signal.length) {
//						break;
//					}
//					MEMSData memsData2 = memsDataList.get(j);
//					if (memsData2.time.getTime() > triggerDate.getTime()) {
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
//	/**
//	 * 计算某个台站的震中距,单位：km
//	 * @param stationID 台站ID
//	 * @return
//	 */
//	private double calEpiDistanceByProjPoint(BaseStation station) {
////		double x1 = 142.6;
////		double y1 = 38.1;
////		IPoint point;
////		try {
////			point = ArcgisHelper.getWGS84PointInstance(x1, y1);
////			point.project(ArcgisHelper.webMercatorCoordinateSystem);
////			x1 = point.getX();
////			y1 = point.getY();
////			System.out.println("x1:"+x1+", y1:"+y1);
////		} catch (AutomationException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//		double x1 = 1.5891781262513386E7;
//		double y1 = 4679160.663693626;
//		double x2 = station.myLocation.getProjPoint().getX();
//		double y2 = station.myLocation.getProjPoint().getY();
//		return GisHelper.calDisByProjPoint(x1, y1, x2, y2);
//	}
}
