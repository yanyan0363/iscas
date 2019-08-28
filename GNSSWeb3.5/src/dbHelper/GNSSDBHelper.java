package dbHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import utils.Config;

public class GNSSDBHelper {

	public static String getStationsInfo() {
		String sql = "select stationID,x_3857,y_3857 from stationsInfo where status=1;";
		ResultSet resultSet = DBHelper.runQuerySql(sql);
		StringBuilder builder = new StringBuilder("[");
		if (resultSet != null) {
			try {
				while (resultSet.next()) {
					builder.append("{\"StationID\":\""+resultSet.getString("stationID")
					+"\",\"x\":"+resultSet.getDouble("x_3857")+",\"y\":"+resultSet.getDouble("y_3857")+"},");
				}
				if (builder.lastIndexOf(",") == builder.length() - 1) {
					builder.deleteCharAt(builder.lastIndexOf(","));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		builder.append("]");
		return builder.toString();
	}
	public static String getEQStPTMEpiDis(String eqID){
		StringBuilder builder = new StringBuilder("[");
		String sql = "select * from AReport3_1 where EQID='" + eqID + "' order by num asc;";
		System.out.println(sql);
		ResultSet rSet = DBHelper.runQuerySql(sql);
		if (rSet != null) {
			try {
				if (rSet.last()) {
					JSONArray stMsArray = JSONArray.parseArray(rSet.getString("stGPSMags"));
					rSet.beforeFirst();
					while (rSet.next()) {
						String st = rSet.getString("Station");
						builder.append("{\"station\":\""+st+"\"");
						builder.append(",\"PT\":\""+rSet.getString("PT")+"\"");
						for (int i = 0; i < stMsArray.size(); i++) {
							JSONObject object = stMsArray.getJSONObject(i);
							if (object.getString("station").equals(st)) {
								builder.append(",\"gpsMag\":" + object.getDoubleValue("M"));
								stMsArray.remove(object);
								break;
							}
						}
						builder.append(",\"memsMag\":" + rSet.getDouble("memsMag"));
						builder.append(",\"EpiDis\":"+rSet.getDouble("EpiDis")+"},");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (builder.lastIndexOf(",") > 0) {
				builder.deleteCharAt(builder.lastIndexOf(","));
			}
			builder.append("]");
		}
		return builder.toString();
	}
	public static String getEQIdxInfo(String eqID){
		StringBuilder builder = new StringBuilder("{");
		String sql = "select * from eq where eqID='" +eqID+"';";
		ResultSet resultSet = DBHelper.runQuerySql(sql);
		if (resultSet != null) {
			try {
				if (resultSet.next()) {
					builder.append("\"EQInfo\":{");
					builder.append("\"lon\":"+resultSet.getDouble("L"));
					builder.append(",\"lat\":"+resultSet.getDouble("B"));
					builder.append(",\"gpsMag\":"+resultSet.getDouble("gpsMag"));
					builder.append(",\"memsMag\":"+resultSet.getDouble("memsMag"));
					builder.append(",\"firstSt\":\""+resultSet.getString("firstStation")+"\"");
					builder.append(",\"eqTime\":\""+resultSet.getString("originTime")+"\"");
					builder.append("}");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		sql = "select * from AReport3_1 where EQID='" + eqID + "' order by num asc;";
		ResultSet rSet = DBHelper.runQuerySql(sql);
		if (rSet != null) {
			builder.append(",\"EQARs\":[");
			try {
				while (rSet.next()) {
					builder.append("{\"station\":\""+rSet.getString("Station")+"\"");
					builder.append(",\"PT\":\""+rSet.getString("PT")+"\"");
					builder.append(",\"gpsMag\":" + rSet.getDouble("gpsMag"));
					builder.append(",\"memsMag\":" + rSet.getDouble("memsMag"));
					builder.append(",\"EpiLon\":" + rSet.getDouble("EpiLon"));
					builder.append(",\"EpiLat\":" + rSet.getDouble("EpiLat"));
					builder.append(",\"EpiDis\":"+rSet.getDouble("EpiDis"));
					builder.append(",\"stGPSMags\":"+rSet.getString("stGPSMags")+"},");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (builder.lastIndexOf(",") > 0) {
				builder.deleteCharAt(builder.lastIndexOf(","));
			}
			builder.append("]");
		}
		builder.append("}");
		return builder.toString();
	}
	public static ResultSet getGPSByTSt(String stationID, String time) {
		String sql = "select * from records where stationID='"+stationID+"' and time='"+time+"' group by time;";
		System.out.println(sql);
		ResultSet resultSet = DBHelper.runQuerySql(sql);
		return resultSet;
	}
	public static String getWarnPSDTServlet(String eqID){
		JSONArray jsonArray = new JSONArray();
		String sql = "select * from areport3_1 where EQID='" + eqID + "' order by num;";
		ResultSet rSet = DBHelper.runQuerySql(sql);
		if (rSet == null) {
			return "";
		}
		try {
			while (rSet.next()) {
				JSONObject object = new JSONObject();
				object.put("stationID", rSet.getString("station"));
				object.put("PT", rSet.getString("PT"));
				object.put("ST", new Date(rSet.getDate("PT").getTime()+rSet.getTime("PT").getTime()+1000).toLocaleString());
				object.put("EpiDis", rSet.getString("EpiDis"));
				System.out.println(object.toString());
				jsonArray.add(object);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jsonArray.toJSONString();
	}
	public static String getWarnTMServlet(String eqID){
		JSONArray jsonArray = new JSONArray();
		String sql = "select * from AReport3_1 where EQID='" + eqID + "' order by num asc;";
		ResultSet rSet = DBHelper.runQuerySql(sql);
		if (rSet == null) {
			return "";
		}
		try {
			while (rSet.next()) {
				JSONObject object = new JSONObject();
				object.put("Idx", rSet.getInt("num"));
				object.put("T", rSet.getString("createTime"));
				object.put("stGPSMag", rSet.getDouble("stGPSMag"));
				jsonArray.add(object);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			return null;
		}
		return jsonArray.toJSONString();
	}

	public static String getAReportByEQID(String eqID) {
//		eqID = "Thisisatest";
		String sql = "select * from AReport3_1 where EQID='" + eqID + "' order by num;";
		ResultSet rSet = DBHelper.runQuerySql(sql);
		if (rSet == null) {
			return "[]";
		}
		StringBuilder builder = new StringBuilder("[");
		try {
			while (rSet.next()) {
				builder.append("{\"station\":\""+rSet.getString("Station")+"\"");
				builder.append(",\"PT\":\""+rSet.getString("PT")+"\"");
				builder.append(",\"ST\":\""+rSet.getString("ST")+"\"");
				builder.append(",\"stGPSMag\":" + rSet.getInt("stGPSMag"));
				builder.append(",\"stMEMSMag\":" + rSet.getInt("stMEMSMag"));
				builder.append(",\"EpiDis\":"+rSet.getDouble("EpiDis")+"},");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "[]";
		}
		if (builder.lastIndexOf(",") > 0) {
			builder.deleteCharAt(builder.lastIndexOf(","));
		}
		builder.append("]");
		return builder.toString();
	}
//	public static String getAReportByEQID(String eqID, int rNum) {
////		eqID = "Thisisatest";
//		String col = "A"+rNum;
//		String sql = "select "+col+" from AReport where EQID='" + eqID + "';";
//		ResultSet rSet = DBHelper.runQuerySql(sql);
//		if (rSet == null) {
//			return "{\"S\":[]}";
//		}
//		try {
//			rSet.next();
//			String ss = rSet.getString(col);
//			return ss;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return "{\"S\":[]}";
//		}
//	}
	/**
     * 获取所有台站的time时刻前后一分钟的位移数据
     * @param callback
     * @param stations
     * @param time
     * @return
     */
    public static String getStationsDis2DZ(String stations, String time){
    	if (stations == null || stations == "") {
			return "";
		}
    	String[] stationArray = stations.split(",");
    	StringBuilder builder = new StringBuilder("[");
    	System.out.println(time);
    	if (time == null || time == "") {
    		for (String station : stationArray) {
    			builder.append("{\"ID\":\"").append(station).append("\",\"value\":").append(getCurStationDis2DZ(station, null, null)).append("},");
    		}
		}else{
			SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			int timeExt = Property.getIntProperty("timeExt");
			int timeExt = Config.timeExt;
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
    	if (builder.length() == builder.lastIndexOf(",")+1) {
    		builder.deleteCharAt(builder.lastIndexOf(","));
		}
    	builder.append("]");
    	return builder.toString();
    }
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String getCurStationDis2DZ(String stationID, Date startTime, Date endTime){
    	if (stationID == null || stationID.equals("")) {
			System.out.println("getCurStationDis2DZ::station is null.");
			return "";
		}
    	StringBuilder sql = new StringBuilder("select dx,dy,dz,time from records where stationID='" + stationID + "' ");//order by time asc;";
    	if (startTime != null) {
			sql.append(" and time >= '" + format.format(startTime) + "' ");
		}
    	if (endTime != null) {
			sql.append("and time <= '" + format.format(endTime) + "' ");
		}
    	sql.append("order by time asc;");
    	System.out.println(sql);
    	ResultSet resultSet = DBHelper.runQuerySql(sql.toString());
    	StringBuilder disZBuilder = new StringBuilder("\"dis\":[");
    	StringBuilder dis2DBuilder = new StringBuilder("\"dis\":[");
    	StringBuilder timeBuilder = new StringBuilder("\"time\":[");
    	if (resultSet != null) {
			try {
				while (resultSet.next()) {
					disZBuilder.append(String.format("%.2f", resultSet.getDouble("dz"))+",");
					dis2DBuilder.append(calDis2D(resultSet.getDouble("dx"), resultSet.getDouble("dy"))+",");
					timeBuilder.append("\""+resultSet.getString("time").substring(0,19)+"\""+",");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    	if (disZBuilder.length() == disZBuilder.lastIndexOf(",")+1) {
    		disZBuilder.deleteCharAt(disZBuilder.lastIndexOf(","));
		}
    	if (dis2DBuilder.length() == dis2DBuilder.lastIndexOf(",")+1) {
    		dis2DBuilder.deleteCharAt(dis2DBuilder.lastIndexOf(","));
    	}
    	if (timeBuilder.length() == timeBuilder.lastIndexOf(",")+1) {
    		timeBuilder.deleteCharAt(timeBuilder.lastIndexOf(","));
    	}
    	disZBuilder.append("]");
    	dis2DBuilder.append("]");
    	timeBuilder.append("]");
//		return "{\"z\":{\"dis\":[],\"time\":[]},\"dis2D\":{\"dis\":[],\"time\":[]}}";
		return "{\"z\":{"+disZBuilder.toString()+","+timeBuilder.toString()+"},\"dis2D\":{"+dis2DBuilder.toString()+","+timeBuilder.toString()+"}}";
    }
    /**
     * 根据dx，dy计算二维距离
     * @param dx
     * @param dy
     * @return
     */
    private static String calDis2D(double dx, double dy) {
    	return String.format("%.2f", Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2)));
    }
	public static boolean isTaskAvailable() {
		String sql = "select count(state) from task where state='start';";
		ResultSet res = DBHelper.runQuerySql(sql);
		if (res != null) {
			try {
				res.next();
				int count = res.getInt("count(state)");
				System.out.println("count:"+count);
				if (count > 0) {
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public static String getEpiBL(String eqID) {
		String sql = "select B,L from eq where eqID='" + eqID + "';";
		ResultSet resultSet = DBHelper.runQuerySql(sql);
		if (resultSet != null) {
			try {
				resultSet.next();
				return "{\"B\":"+String.format("%.2f", resultSet.getDouble("B"))+",\"L\":"+String.format("%.2f", resultSet.getDouble("L"))+"}";
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "{}";
	}
	public static String getCurReportStations(String eqID, int reportNum) {
		String sql = "select station from reports where EQID='" + eqID + "' and num="+reportNum+" ;";
		ResultSet resultSet = DBHelper.runQuerySql(sql);
		StringBuilder builder = new StringBuilder("{\"stations\":[");
		if (resultSet != null) {
			try {
				while (resultSet.next()) {
					builder.append("\""+resultSet.getString("station")+"\",");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (builder.lastIndexOf(",") > 0) {
			builder.deleteCharAt(builder.lastIndexOf(","));
		}
		builder.append("]}");
		return builder.toString();
	}
	public static ResultSet getReportDetail(String eqID) {
		String sql = "select detail from reports where eqID='" + eqID + "';";
		return DBHelper.runQuerySql(sql);
	}
	public static String getStInfoByEQSt(String eqID, String stationID) {
		if (eqID == null || eqID == "" || stationID == null || stationID == "") {
			return "";
		}
		String sql = "select * from areport3_1 where EQID='" + eqID + "' and Station='"+stationID+"';";
		ResultSet resultSet = DBHelper.runQuerySql(sql);
		try {
			if (resultSet != null && resultSet.next()) {
				StringBuilder builder = new StringBuilder("{");
				builder.append("\"station\":\""+resultSet.getString("Station")+"\"");
				builder.append(",\"num\":"+resultSet.getInt("num"));
				builder.append(",\"gpsMag\":"+resultSet.getDouble("gpsMag"));
				builder.append(",\"memsMag\":"+resultSet.getDouble("memsMag"));
				builder.append(",\"EpiDis\":"+resultSet.getDouble("EpiDis"));
				builder.append(",\"PT\":\""+resultSet.getString("PT")+"\"");
				builder.append(",\"ST\":\""+resultSet.getString("ST")+"\"");
				builder.append("}");
				return builder.toString();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "{}";
		}
		return "{}";
	}
	public static String getEQInfoByID(String eqID) {
		if (eqID == null || eqID == "") {
			return "";
		}
		String sql = "select * from eq where EQID='" + eqID + "' ;";
		ResultSet resultSet = DBHelper.runQuerySql(sql);
		StringBuilder builder = new StringBuilder("{");
		try {
			if (resultSet != null && resultSet.next()) {
				builder.append("\"EQID\":\""+resultSet.getString("EQID")+"\"").append(",\"epi\":\"");
				double B = resultSet.getDouble("B");
				double L = resultSet.getDouble("L");
				if (L > 0) {
					builder.append(String.format("%.2f", L)+"E, ");
				}else if (L < 0) {
					builder.append(String.format("%.2f", (-1)*L)+"W, ");
				}else {
					builder.append(String.format("%.2f", L)+", ");
				}
				if (B > 0) {
					builder.append(String.format("%.2f", B)+"N\"");
				}else if (B < 0) {
					builder.append(String.format("%.2f", (-1)*B)+"S\"");
				}else {
					builder.append(String.format("%.2f", B)+"\"");
				}
				builder.append(",\"name\":\""+resultSet.getString("name")+"\"");
				builder.append(",\"gpsMag\":"+resultSet.getDouble("gpsMag"));
				builder.append(",\"memsMag\":"+resultSet.getDouble("memsMag"));
				builder.append(",\"lon\":"+L);
				builder.append(",\"lat\":"+B);
				builder.append(",\"originTime\":\""+resultSet.getString("originTime").substring(0,19)+"\"");
				builder.append(",\"createTime\":\""+resultSet.getString("createTime").substring(0,19)+"\"");
				builder.append(",\"firstSt\":\""+resultSet.getString("firstStation")+"\"");
			}
			builder.append("}");
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
		return builder.toString();
	}
	public static String getAlarmTime(String eqID) {
		if (eqID == null || eqID == "") {
			return "";
		}
		String sql = "select inTime from reports where EQID='" + eqID + "' and num=1;";
		ResultSet resultSet = DBHelper.runQuerySql(sql);
		String alarmTime = "";
		try {
			if (resultSet != null && resultSet.next()) {
				alarmTime = resultSet.getString("inTime");
				alarmTime = alarmTime.substring(0, 19);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
		return alarmTime;
	}
	public static String getEQMag(String eqID) {
		if (eqID == null || eqID == "") {
			return "";
		}
		StringBuilder mBuilder = new StringBuilder("[");
		StringBuilder tBuilder = new StringBuilder("[");
		String sql = "select magnitude, inTime from reports where eqID='" + eqID + "';";
		ResultSet rSet = DBHelper.runQuerySql(sql);
		try {
			if (rSet != null && rSet.next()) {
					try {
						double mag = rSet.getDouble("magnitude");
						mBuilder.append(mag+",");
						tBuilder.append("\""+rSet.getString("inTime").substring(0, 19)+"\",");
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		mBuilder.append("]");
		tBuilder.append("]");
		return "["+ mBuilder.toString() + "," + tBuilder.toString()+"]";
	}
	public static String getMagByEQIDStID(String eqID, String stationID) {
		if (eqID == null || eqID == "" || stationID == null || stationID == "") {
			return "";
		}
		StringBuilder gpsMBuilder = new StringBuilder("[");
		StringBuilder memsMBuilder = new StringBuilder("[");
		StringBuilder tBuilder = new StringBuilder("[");
		String sql = "select * from areport3_1 where eqID='" + eqID + "' and station='"+stationID+"';";
		ResultSet rSet = DBHelper.runQuerySql(sql);
		int num = 0;
		double memsMag = 0;
		try {
			if (rSet != null && rSet.next()) {
				gpsMBuilder.append(rSet.getDouble("gpsMag")+",");
				memsMag = rSet.getDouble("memsMag");
				memsMBuilder.append(memsMag+",");
				num = rSet.getInt("num");
				tBuilder.append("\"第"+num+"报\",");
			}
			sql = "select num,stGPSMags from areport3_1 where eqID='" + eqID + "' and num>"+num+" order by num asc;";
			rSet = DBHelper.runQuerySql(sql);
			if (rSet != null) {
				while (rSet.next()) {
					JSONArray array = JSONArray.parseArray(rSet.getString("stGPSMags"));
					for (int j = 0; j < array.size(); j++) {
						JSONObject object = array.getJSONObject(j);
						if (object.getString("station").equals(stationID)) {
							gpsMBuilder.append(object.getDoubleValue("M")+",");
							memsMBuilder.append(memsMag+",");
							tBuilder.append("\"第"+rSet.getInt("num")+"报\",");
							break;
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		gpsMBuilder.append("]");
		memsMBuilder.append("]");
		tBuilder.append("]");
		return "["+ gpsMBuilder.toString() + ","+ memsMBuilder.toString() + "," + tBuilder.toString()+"]";
	}
	public static String getMagByEQID(String eqID) {
		if (eqID == null || eqID == "") {
			return "";
		}
		StringBuilder gpsMBuilder = new StringBuilder("[");
		StringBuilder memsMBuilder = new StringBuilder("[");
		StringBuilder tBuilder = new StringBuilder("[");
		String sql = "select * from areport3_1 where eqID='" + eqID + "' order by num asc;";
		ResultSet rSet = DBHelper.runQuerySql(sql);
		int i = 1;
		if (rSet != null) {
			try {
				while (rSet.next()) {
					gpsMBuilder.append(rSet.getDouble("stGPSMag")+",");
					memsMBuilder.append(rSet.getDouble("stMEMSMag")+",");
					tBuilder.append("\"第"+i+"报\",");
					i++;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		gpsMBuilder.append("]");
		memsMBuilder.append("]");
		tBuilder.append("]");
		return "["+ gpsMBuilder.toString() + ","+ memsMBuilder.toString() + "," + tBuilder.toString()+"]";
	}
	
	public static String getEQList() {
		String sql = "select * from EQ;";
		ResultSet rSet = DBHelper.runQuerySql(sql);
		StringBuilder json = new StringBuilder("{\"total\":" + countEQ() + ",\"rows\":[");
		try {
			while (!rSet.isLast() && rSet.next()) {
				json.append("{\"EQID\":\"" + rSet.getString("EQID")+"\"")
					.append(",\"loc\":\"" + rSet.getString("X") + "," + rSet.getString("Y")+"\"")
					.append(",\"originTime\":\"" + rSet.getString("originTime")+"\"")
					.append(",\"magnitude\":\"" + rSet.getString("magnitude")+"\"")
					.append(",\"state\":\"" + rSet.getString("state")+"\"},");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (json.lastIndexOf(",") > 0) {
			json.deleteCharAt(json.lastIndexOf(","));
		}
		json.append("]}");
		return json.toString();
	}
	public static boolean deleteEQByEQID(String eqID) {
		String sql = "update eq set deleted='1' where EQID='"+eqID+"';";
		System.out.println(sql);
		boolean res = DBHelper.runUpdateSql(sql);
		return res;
	}
	public static String getEQList(int offset, int limit, String order) {
		String sql = "select * from EQ where deleted is null or deleted <>1 order by createTime "+ order+" limit " + offset + "," + limit  + ";";
		ResultSet rSet = DBHelper.runQuerySql(sql);
		if (rSet == null) {
			return "{\"total\":0,\"rows\":[]}";
		}
		StringBuilder json = new StringBuilder("{\"total\":" + countEQ() + ",\"rows\":[");
		try {
			while (!rSet.isLast() && rSet.next()) {
				json.append("{\"EQID\":\"" + rSet.getString("EQID")+"\"")
				.append(",\"originTime\":\"" + rSet.getString("originTime")+"\"")
				.append(",\"loc\":\"" + rSet.getString("loc") +"\"")
				.append(",\"GPSMag\":\"" + rSet.getString("gpsMag")+"\"")
				.append(",\"MEMSMag\":\"" + rSet.getString("memsMag")+"\"")
				.append(",\"status\":\"" + rSet.getString("status") +"\"")
				.append(",\"type\":\"" + rSet.getString("type") +"\"")
				.append(",\"firstStation\":\"" + rSet.getString("firstStation")+"\"},");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (json.lastIndexOf(",") > 0) {
			json.deleteCharAt(json.lastIndexOf(","));
		}
		json.append("]}");
		return json.toString();
	}
	public static String getTaskList(int offset, int limit, String order) {
		String sql = "select * from task order by startTime "+ order+" limit " + offset + "," + limit  + ";";
//		System.out.println(sql);
		ResultSet rSet = DBHelper.runQuerySql(sql);
		if (rSet == null) {
			return "{\"total\":0,\"rows\":[]}";
		}
		StringBuilder json = new StringBuilder("{\"total\":" + countTask() + ",\"rows\":[");
		try {
			while (!rSet.isLast() && rSet.next()) {
				json.append("{\"taskID\":\"" + rSet.getString("taskID")+"\"")
					.append(",\"startTime\":\"" + rSet.getString("startTime") +"\"")
					.append(",\"endTime\":\"" + rSet.getString("endTime") +"\"")
					.append(",\"state\":\"" + rSet.getString("state")+"\"")
					.append(",\"eqSet\":\"" + rSet.getString("eqSet")+"\"")
					.append(",\"msg\":\"" + rSet.getString("msg")+"\"},");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (json.lastIndexOf(",") > 0) {
			json.deleteCharAt(json.lastIndexOf(","));
		}
		json.append("]}");
		return json.toString();
	}
	public static String getReportsStationByEQIDReportNum(String eqID, int reportNum) {
		String sql = "select station,magnitude,dis,isFirst from reports where EQID='" + eqID + "' and num = " + reportNum + " order by isFirst desc, dis asc;";
		ResultSet rSet = DBHelper.runQuerySql(sql);
		if (rSet == null) {
			return "{\"total\":0,\"rows\":[]}";
		}
		StringBuilder json = new StringBuilder("[");
		try {
			while (!rSet.isLast() && rSet.next()) {
				json.append("{\"station\":\"" + rSet.getString("station")+"\"")
					.append(",\"magnitude\":\"" + rSet.getString("magnitude")+"\"")
					.append(",\"isFirst\":" + rSet.getInt("isFirst"))
					.append(",\"dis\":\"" + String.format("%.3f", rSet.getDouble("dis"))+"\"},");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (json.lastIndexOf(",") > 0) {
			json.deleteCharAt(json.lastIndexOf(","));
		}
		json.append("]");
		return json.toString();
	}
	public static String getReportsByEQID(String eqID) {
		String sql = "select * from reports where EQID='" + eqID + "' and isFirst=1 order by num desc;";
//		String sql = "select * from reports where EQID='" + eqID + "' group by num order by num asc desc;";
		ResultSet rSet = DBHelper.runQuerySql(sql);
		if (rSet == null) {
			return "{\"total\":0,\"rows\":[]}";
		}
//		StringBuilder json = new StringBuilder("{\"total\":" + countReportsByEQID(eqID) + ",\"rows\":[");
		StringBuilder json = new StringBuilder("[");
		try {
			while (!rSet.isLast() && rSet.next()) {
				json.append("{\"num\":\"" + rSet.getString("num")+"\"")
				.append(",\"dis\":\"" + rSet.getString("dis")+"\"")
				.append(",\"inTime\":\"" + rSet.getString("inTime")+"\"")
				.append(",\"magnitude\":\"" + rSet.getString("magnitude")+"\"},");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (json.lastIndexOf(",") > 0) {
			json.deleteCharAt(json.lastIndexOf(","));
		}
		json.append("]");
		return json.toString();
	}
	public static String getPublishedReportsByEQID(String eqID) {
		String sql = "select * from reports where EQID='" + eqID + "' and isFirst=1 and isPublished=1 order by num desc;";
		ResultSet rSet = DBHelper.runQuerySql(sql);
		if (rSet == null) {
			return "{\"total\":0,\"rows\":[]}";
		}
//		StringBuilder json = new StringBuilder("{\"total\":" + countReportsByEQID(eqID) + ",\"rows\":[");
		StringBuilder json = new StringBuilder("[");
		try {
			while (!rSet.isLast() && rSet.next()) {
				json.append("{\"num\":\"" + rSet.getString("num")+"\"")
				.append(",\"dis\":\"" + rSet.getString("dis")+"\"")
				.append(",\"inTime\":\"" + rSet.getString("inTime")+"\"")
				.append(",\"magnitude\":\"" + rSet.getString("magnitude")+"\"},");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (json.lastIndexOf(",") > 0) {
			json.deleteCharAt(json.lastIndexOf(","));
		}
		json.append("]");
		return json.toString();
	}
	public static int countReportsByEQID(String eqID) {
		String sql = "select count(*) from EQ where EQID='"+eqID+"';";
		ResultSet rSet = DBHelper.runQuerySql(sql);
		int count = 0;
		try {
			if (rSet != null && rSet.next()) {
				count = rSet.getInt("count(*)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	public static int countEQ() {
		String sql = "select count(*) from EQ where deleted is null or deleted <>1;";
		ResultSet rSet = DBHelper.runQuerySql(sql);
		int count = 0;
		try {
			if (rSet != null && rSet.next()) {
				count = rSet.getInt("count(*)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	public static int countTask() {
		String sql = "select count(*) from task;";
		ResultSet rSet = DBHelper.runQuerySql(sql);
		int count = 0;
		try {
			if (rSet != null && rSet.next()) {
				count = rSet.getInt("count(*)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	public static void main(String[] args) {
		String json = getEQList();
		System.out.println(json);
		System.out.println(countEQ());
	}
}
