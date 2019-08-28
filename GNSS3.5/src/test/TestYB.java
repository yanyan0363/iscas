package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import arcgis.GisHelper;
import baseObject.BaseStation;
import baseObject.GNSSStation;
import beans.BLT;
import beans.GPSData;
import beans.Loc;
import beans.MEMSData;
import dataCache.DataCache;
import dataCache.DispWithMEMS;
import dbHelper.DBHelper;
import helper.EpiHelper;
import metaData.StaticMetaData;
import utils.Config;

public class TestYB {

	public static void main(String[] args) {
		calEpiYB();
//		calTriggerYB();
//		calTriggerYBGX();
//		double dis = GisHelper.calDisByBLProjTo3857(104.90, 28.34, 100.61, 26.85);
//		System.out.println(dis);
//		stEpiDis();
//		double dis = GisHelper.calDisByBLProjTo3857(102.77, 28.47, 104.77, 28.43);
//		System.out.println(dis);
		
	}
	private static void stEpiDis() {
		int i = 0;
		BLT[] blts = new BLT[5];
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		HashMap<String, Date> stTs = new HashMap<>();
		Date epiTime = null;
		long epiTLong = 0;
		try {
			epiTime = format.parse("2019/06/17 22:55:43.000");
			epiTLong = epiTime.getTime();
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			//服务器
//			stTs.put("SMZSZ", format.parse("2019/06/17 22:56:51.340"));
//			stTs.put("SMTWX", format.parse("2019/06/17 22:56:42.300"));
//			stTs.put("SMCLX", format.parse("2019/06/17 22:56:34.440"));
//			stTs.put("SMBTX", format.parse("2019/06/17 22:56:55.120"));
//			stTs.put("SMXJZ", format.parse("2019/06/17 22:57:08.420"));
			//本地参数1
			stTs.put("SMZSZ", format.parse("2019/06/17 22:56:51.600"));
			stTs.put("SMTWX", format.parse("2019/06/17 22:56:42.300"));
			stTs.put("SMCLX", format.parse("2019/06/17 22:56:34.440"));
			stTs.put("SMBTX", format.parse("2019/06/17 22:56:56.640"));
			stTs.put("SMXJZ", format.parse("2019/06/17 22:57:10.020"));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		String sql = "select stationID,longitude,latitude,x_3857,y_3857 from stationsInfo where status=1;";
		ResultSet resultSet = DBHelper.runQuerySql(sql);
		if (resultSet != null) {
			try {
				while (resultSet.next()) {
					String stID = resultSet.getString("stationID");
					if (stTs.containsKey(stID)) {
						Date tDate = stTs.get(stID);
						double latitude = resultSet.getDouble("latitude");
						double longitude = resultSet.getDouble("longitude");
						BLT blt = new BLT(latitude, longitude, tDate);
						blts[i] = blt;
						double stDis = GisHelper.calDisByBLProjTo3857(104.90, 28.34, longitude, latitude);
						i++;
						System.out.println(stID+", "+blt+", "+stDis+", "+(tDate.getTime()-epiTLong)+"ms");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	private static void calTriggerYBGX() {
		StaticMetaData.initStaticData();
		Map<String, DataCache> stCache = new HashMap<>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		String folderPath = "E:\\2019\\GNSS\\20190622-宜宾珙县地震数据\\MEMS\\";
		File folder = new File(folderPath);
		if (!folder.exists() || !folder.isDirectory()) {
			return;
		}
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			String fileName = file.getName();
			System.out.println(fileName);
			String stationId = fileName.substring(0, fileName.indexOf("_"));
			DataCache dataCache = null;
			if (stCache.containsKey(stationId)) {
				dataCache = stCache.get(stationId);
			}else {
				BaseStation station = new GNSSStation(stationId).getStationInstance();
				dataCache = new DataCache(station);
				stCache.put(stationId, dataCache);
			}
			FileReader fileReader = null;
			try {
				fileReader = new FileReader(file);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return;
			}
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line  = null;
			try {
				while ((line = bufferedReader.readLine()) != null && line != "") {
					String[] items = line.split(",");
					Date time = null;
					try {
						time = dateFormat.parse(items[1]);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					double E = Double.parseDouble(items[2]);
					double N = Double.parseDouble(items[3]);
					double U = Double.parseDouble(items[4]);
					MEMSData memsData = new MEMSData(time, E, N, U, new Date());
					dataCache.insertMEMSData(memsData);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		for (Entry<String, String> entry : map.entrySet()) {
//			entry.getKey();
//			entry.getValue();
//			}
		for (Entry<String, DataCache> entry : stCache.entrySet()) {
			DataCache dataCache = entry.getValue();
			System.out.println("dataCache.myDataList.size() -- "+dataCache.myDataList.size());
			boolean isTriggeredDesc = dataCache.isTriggeredDesc();
			System.out.println("isTriggeredDesc: " + isTriggeredDesc);
			for (int i = 0; i < dataCache.myDataList.size(); i++) {
				DispWithMEMS dispWithMEMS = dataCache.myDataList.get(i);
				java.util.List<MEMSData> memsList = dispWithMEMS.memsDataList;
				for (int j = 0; j < memsList.size(); j++) {
					memsList.get(j).setTriggerCalculated(false);
				}
			}
			boolean isTriggeredAsc = dataCache.isTriggeredAsc();
			System.out.println("isTriggeredAsc: " + isTriggeredAsc);
		}
	}
	private static void calTriggerYB() {
		StaticMetaData.initStaticData();
		Map<String, DataCache> stCache = new HashMap<>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		String folderPath = "E:\\2019\\GNSS\\20190617-宜宾地震\\MEMS本地测试数据\\";
		File folder = new File(folderPath);
		if (!folder.exists() || !folder.isDirectory()) {
			return;
		}
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			String fileName = file.getName();
			System.out.println(fileName);
			String stationId = fileName.substring(0, fileName.indexOf("_"));
			DataCache dataCache = null;
			if (stCache.containsKey(stationId)) {
				dataCache = stCache.get(stationId);
			}else {
				BaseStation station = new GNSSStation(stationId).getStationInstance();
				dataCache = new DataCache(station);
				stCache.put(stationId, dataCache);
			}
			FileReader fileReader = null;
			try {
				fileReader = new FileReader(file);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return;
			}
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line  = null;
			try {
				while ((line = bufferedReader.readLine()) != null && line != "") {
					String[] items = line.split(",");
					Date time = null;
					try {
						time = dateFormat.parse(items[1]);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					double E = Double.parseDouble(items[2]);
					double N = Double.parseDouble(items[3]);
					double U = Double.parseDouble(items[4]);
					MEMSData memsData = new MEMSData(time, E, N, U, new Date());
					dataCache.insertMEMSData(memsData);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		for (Entry<String, String> entry : map.entrySet()) {
//			entry.getKey();
//			entry.getValue();
//			}
		for (Entry<String, DataCache> entry : stCache.entrySet()) {
			DataCache dataCache = entry.getValue();
			System.out.println("dataCache.myDataList.size() -- "+dataCache.myDataList.size());
			boolean isTriggeredDesc = dataCache.isTriggeredDesc();
			System.out.println("isTriggeredDesc: " + isTriggeredDesc);
			for (int i = 0; i < dataCache.myDataList.size(); i++) {
				DispWithMEMS dispWithMEMS = dataCache.myDataList.get(i);
				java.util.List<MEMSData> memsList = dispWithMEMS.memsDataList;
				for (int j = 0; j < memsList.size(); j++) {
					memsList.get(j).setTriggerCalculated(false);
				}
			}
			boolean isTriggeredAsc = dataCache.isTriggeredAsc();
			System.out.println("isTriggeredAsc: " + isTriggeredAsc);
		}
	}
	private static void calEpiYB() {
		BLT[] blts = initBLTS();
		EpiHelper epiHelper = new EpiHelper(blts);
		GPSData epi = epiHelper.calEpi(16, Config.epiCalTime);
		Date epiT = epiHelper.getEQTime();
//		System.out.println("震中点：" + epi);
//		System.out.println("发震时间：" + epiT.toLocaleString());
	}
	
	private static BLT[] initBLTS() {
		int i = 0;
		BLT[] blts = new BLT[5];
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		HashMap<String, Date> stTs = new HashMap<>();
		try {
			//服务器
//			stTs.put("SMZSZ", format.parse("2019/06/17 22:56:51.340"));
//			stTs.put("SMTWX", format.parse("2019/06/17 22:56:42.300"));
//			stTs.put("SMCLX", format.parse("2019/06/17 22:56:34.440"));
//			stTs.put("SMBTX", format.parse("2019/06/17 22:56:55.120"));
//			stTs.put("SMXJZ", format.parse("2019/06/17 22:57:08.420"));
			//本地参数1
//			stTs.put("SMZSZ", format.parse("2019/06/17 22:56:51.600"));
//			stTs.put("SMTWX", format.parse("2019/06/17 22:56:42.300"));
//			stTs.put("SMCLX", format.parse("2019/06/17 22:56:34.440"));
//			stTs.put("SMBTX", format.parse("2019/06/17 22:56:56.640"));
//			stTs.put("SMXJZ", format.parse("2019/06/17 22:57:10.020"));
			//本地参数1
//			stTs.put("SMCLX", format.parse("2019/06/22 22:30:45.800"));
//			stTs.put("SMPGX", format.parse("2019/06/22 22:30:43.920"));
////			stTs.put("SMZSZ", format.parse("2019/06/22 22:31:11.260"));
//			stTs.put("SMMNX", format.parse("2019/06/22 22:30:58.800"));
//			stTs.put("SMBTX", format.parse("2019/06/22 22:30:39.040"));
//			stTs.put("SMTWX", format.parse("2019/06/22 22:30:53.300"));
			//others
			stTs.put("SMZSZ", format.parse("2019/06/22 22:31:11.800"));
			stTs.put("SMTWX", format.parse("2019/06/22 22:30:54.120"));
			stTs.put("SMCLX", format.parse("2019/06/22 22:30:46.000"));
			stTs.put("SMBTX", format.parse("2019/06/22 22:30:39.280"));
			stTs.put("SMMNX", format.parse("2019/06/22 22:30:59.300"));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return blts;
		}
		String sql = "select stationID,longitude,latitude,x_3857,y_3857 from stationsInfo where status=1;";
		ResultSet resultSet = DBHelper.runQuerySql(sql);
		if (resultSet != null) {
			try {
				while (resultSet.next()) {
					String stID = resultSet.getString("stationID");
					if (stTs.containsKey(stID)) {
						Date tDate = stTs.get(stID);
						BLT blt = new BLT(resultSet.getDouble("latitude"), resultSet.getDouble("longitude"), tDate);
						blts[i] = blt;
						i++;
						System.out.println(stID+", "+blt);
					}
				}
				return blts;
			} catch (SQLException e) {
				e.printStackTrace();
				return blts;
			}
		}
		return blts;
	}
}
