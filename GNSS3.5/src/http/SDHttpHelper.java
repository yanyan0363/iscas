package http;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.esri.arcgis.server.json.JSONObject;

import beans.BLH;
import dbHelper.GNSSDBHelper;
import metaData.StaticMetaData;
import metaData.StaticMetaData.GPSTaskStatus;
import utils.Config;

public class SDHttpHelper {

	public static void main(String[] args) {
//		String token = getSDAccessToken();
//		System.out.println("token:\n"+token);
		Set<String> set = new HashSet<>();
		set.add("SMMNX");
		set.add("SMPGX");
		set.add("SMBTX");
		boolean result = SendSDHttp(Config.SDToken, "SMMNX", set, new Date(), 108.1, 31.1, 12233, "evtID", "start");
		System.out.println("result:"+result);
		boolean result2 = SendSDHttp(Config.SDToken, "SMMNX", set, new Date(), 108.1, 31.1, 12233, "evtID", "end");
		System.out.println("result2:"+result);
		boolean resultJson = SendSDHttpJson(Config.SDToken, "SMMNX", set, new Date(), 108.1, 31.1, 12233, "evtID", "end");
		System.out.println("resultJson:"+resultJson);
	}
	/**
	 * 先停止，再开始
	 * （去除Config.SDStInlineSet筛选）
	 * @return
	 */
	public static boolean startAllStGPS() {
//		String token = getSDAccessToken();
		System.out.println("token:\n"+Config.SDToken);
		Set<String> set = StaticMetaData.getStationOriginalLocs().keySet();
		System.out.println(set);
//		Set<String> curSDStIDSet = new HashSet<>();
//		Iterator<String> iterator = set.iterator();
//		while (iterator.hasNext()) {
//			String string = iterator.next();
//			if (Config.SDStInlineSet.contains(string)) {
//				curSDStIDSet.add(string);
//				continue;
//			}
//		}
//		System.out.println("stSet for SD:"+curSDStIDSet.size()+"-"+curSDStIDSet);
//		if (curSDStIDSet==null || curSDStIDSet.size() <= 0) {
//			System.out.println("stSet for SD: size <= 0");
////			return false;
//		}
		String firstST = Config.SDTriggerSt;//"TCGH10";//TCGH10 SMMNX
		BLH blh = StaticMetaData.getStationOriginalLocs().get(firstST).getGpsData().blh;//H为0值，无H数据，需要和SD确认BLH是否影响算法——已确认，不影响
		Date now = new Date();
		String evtID = "evt-"+now.getTime();
		System.out.println("evtID:"+evtID);
		StaticMetaData.setSDEvtID(evtID);
		StaticMetaData.getSDStIDSet().clear();
//		StaticMetaData.setSDStIDSet(curSDStIDSet);
		StaticMetaData.setSDStIDSet(set);
		endAllGPSTask();
//		boolean resultJson = SendSDHttpJson(Config.SDToken, firstST, curSDStIDSet, new Date(), blh.L, blh.B, blh.H, evtID, "start");
		boolean resultJson = SendSDHttpJson(Config.SDToken, firstST, set, new Date(), blh.L, blh.B, blh.H, evtID, "start");
		System.out.println("    start resultJson:"+resultJson);
		GPSTaskStatus status = null;
		if (resultJson) {
			status = StaticMetaData.GPSTaskStatus.start;
		}else {
			status = StaticMetaData.GPSTaskStatus.failed;
		}
//		boolean res = GNSSDBHelper.insertGPSTask(status, evtID, curSDStIDSet.toString(),firstST,blh.B,blh.L,blh.H);
		boolean res = GNSSDBHelper.insertGPSTask(status, evtID, set.toString(),firstST,blh.B,blh.L,blh.H);
		System.out.println("解算请求写入数据库："+res);
		return resultJson;
	}
	private static void endAllGPSTask() {
		ResultSet resultSet = GNSSDBHelper.allStartGPSTask();
		try {
			while (resultSet != null && resultSet.next()) {
				String evtID = resultSet.getString("evtID");
				String firstSt = resultSet.getString("firstST");
				String stSet = resultSet.getString("SDStIDSet");
				double B = resultSet.getDouble("B");
				double L = resultSet.getDouble("L");
				double H = resultSet.getDouble("H");
				if (evtID != null && evtID != "") {
					if (endAllStGPS(evtID,firstSt,stSet,B,L,H)) {
						GNSSDBHelper.updateGPSTaskEnd(evtID);
					};
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static boolean endAllStGPS(String evtID, String firstSt, String stSet, double B, double L, double H) {
//		String token = getSDAccessToken();
//		System.out.println("token:\n"+Config.SDToken);
//		Set<String> set = StaticMetaData.getSDStIDSet();
//		if (set == null || set.size() <= 0) {
//			return true;
//		}
		//TCGH10 SMMNX
//		BLH blh = StaticMetaData.getStationOriginalLocs().get("TCGH10").getGpsData().blh;//H为0值，已确认不影响算法
		boolean resultJson = SendSDHttpJson(Config.SDToken, firstSt, stSet, new Date(), L, B, H, evtID, "end");
		System.out.println("end resultJson:"+resultJson);
		return resultJson;
	}
//	public static boolean endAllStGPS() {
//		System.out.println("token:\n"+Config.SDToken);
//		Set<String> set = StaticMetaData.getSDStIDSet();
//		BLH blh = StaticMetaData.getStationOriginalLocs().get("SMMNX").getGpsData().blh;//H为0值，无H数据，需要和SD确认BLH是否影响算法-确认不影响
//		boolean resultJson = SendSDHttpJson(Config.SDToken, "SMMNX", set, new Date(), blh.L, blh.B, blh.H, StaticMetaData.getSDEvtID(), "end");
//		System.out.println("end resultJson:"+resultJson);
//		return resultJson;
//	}
//	public static String getSDAccessToken() {
//		Map<String, String> params = new HashMap<>();
//		params.put("grant_type", "password");
//		params.put("username", Config.SDUsername);
//		params.put("password", Config.SDPassword);
//		params.put("client_id", Config.SDClientID);
//		params.put("client_secret", Config.SDClientKey);
//		try {
//			String resp = HttpHelper.post(params, Config.SDURL);
//			JSONObject jsonObject = new JSONObject(resp);
//			String accessToken = jsonObject.getString("access_token");
//			return accessToken;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "";
//		}
//	}
	/**
	 * 
	 * @param firstSt 首台
	 * @param stList 台站列表
	 * @param time 时刻(YYYY-MM-DD hh:mm:ss.xxx)
	 * @param B 首台经度
	 * @param L 首台纬度
	 * @param H 首台高度
	 * @param evtID 事件ID
	 * @param status 状态start、end
	 * @return
	 */
	public static boolean SendSDHttp(String accessToken, String firstSt, Set<String> stSet, Date time, 
			double B, double L, double H, String evtID, String state) {
		Map<String, String> header = new HashMap<>();
		header.put("Content-Type", "application/json");
//		header.put("authorization", "Bearer{"+accessToken+"}");
		header.put("authorization", accessToken);
		Map<String, String> params = new HashMap<>();
		params.put("msgName", Config.SDMsgName);
		params.put("stationID", firstSt);
		params.put("stationIDlist", stSet.toString());
		params.put("time", StaticMetaData.formatMs.format(time));
		params.put("Lng", L+"");
		params.put("Lat", B+"");
		params.put("Z", H+"");
		params.put("eventID", evtID);
		params.put("state", state);
		try {
			String resp = HttpHelper.post(header, params, Config.SDURL);
//			String resp = HttpHelper.post(header, params, "http://localhost:8090/GNSS/getSDStartEndTest");
			JSONObject jsonObject = new JSONObject(resp);
			String status = jsonObject.getString("status");
			if (status.equalsIgnoreCase("OK")) {
				return true;
			}else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public static boolean SendSDHttpJson(String accessToken, String firstSt, Set<String> stSet, Date time, 
			double B, double L, double H, String evtID, String state) {
		Map<String, String> header = new HashMap<>();
		header.put("Content-Type", "application/json;charset=UTF-8");
//		header.put("authorization", "Bearer{"+accessToken+"}");
		header.put("authorization", accessToken);
		JSONObject object = new JSONObject();
		object.put("msgName", Config.SDMsgName);
		object.put("stationID", firstSt);
		object.put("stationIDlist", stSet);
		object.put("time", StaticMetaData.formatMs.format(time));
		object.put("lng", L);
		object.put("lat", B);
		object.put("Z", H);
		object.put("eventID", evtID);
		object.put("state", state);
		try {
			String resp = HttpHelper.post(header, object.toString(), Config.SDURL);
			System.out.println("Config.SDURL:"+Config.SDURL);
			System.out.println("resp:"+resp);
			if (resp == null || resp.equals("")) {
				System.out.println("resp == null || resp.equals(\"\"),解算请求失败。");
				return false;
			}
			JSONObject jsonObject = new JSONObject(resp);
			String status = jsonObject.getString("status");
			if (status.equalsIgnoreCase("OK")) {
				return true;
			}else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public static boolean SendSDHttpJson(String accessToken, String firstSt, String stSet, Date time, 
			double B, double L, double H, String evtID, String state) {
		Map<String, String> header = new HashMap<>();
		header.put("Content-Type", "application/json;charset=UTF-8");
//		header.put("authorization", "Bearer{"+accessToken+"}");
		header.put("authorization", accessToken);
		JSONObject object = new JSONObject();
		object.put("msgName", Config.SDMsgName);
		object.put("stationID", firstSt);
		object.put("stationIDlist", stSet);
		object.put("time", StaticMetaData.formatMs.format(time));
		object.put("lng", L);
		object.put("lat", B);
		object.put("Z", H);
		object.put("eventID", evtID);
		object.put("state", state);
		try {
			String resp = HttpHelper.post(header, object.toString(), Config.SDURL);
			System.out.println("Config.SDURL:"+Config.SDURL);
			System.out.println("resp:"+resp);
			if (resp == null || resp.equals("")) {
				System.out.println("resp == null || resp.equals(\"\"),解算请求失败。");
				return false;
			}
			JSONObject jsonObject = new JSONObject(resp);
			String status = jsonObject.getString("status");
			if (status.equalsIgnoreCase("OK")) {
				return true;
			}else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
