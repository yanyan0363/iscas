package dbHelper;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import beans.AReport;
import beans.EQInfo;
import beans.Report;
import beans.XYZ;
import dataCache.Displacement;
import mathUtil.DoubleUtil;
import metaData.StaticMetaData;
import metaData.StaticMetaData.GPSTaskStatus;

public class GNSSDBHelper {
	private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	/**
	 * 写入解算请求
	 * @param status 解算请求状态，start（请求成功）、failed（请求失败）
	 * @param evtID
	 * @param SDStIDSet 请求的台站列表
	 * @param firstST 首台
	 * @param B 纬度
	 * @param L 经度
	 * @param H 高度
	 * @return
	 */
	public static boolean insertGPSTask(GPSTaskStatus status, String evtID, String SDStIDSet, String firstST, double B, double L, double H) {
		String sql = "insert into gpstask(evtID,status,startTime,SDStIDSet,firstST,B,L,H) values('"+evtID+"','"+status+"','"
	+new Date().toLocaleString()+"','"+SDStIDSet+"','"+firstST+"',"+B+","+L+","+H+");";
		boolean res = DBHelper.runUpdateSql(sql);
		System.out.println(res+":"+sql);
		return res;
	}
	public static boolean updateGPSTaskEnd(String evtID) {
		String sql = "update gpstask set status='"+StaticMetaData.GPSTaskStatus.end+"',endTime='"+new Date().toLocaleString()
				+"' where evtID='"+evtID+"' and status='"+StaticMetaData.GPSTaskStatus.start +"';";
		boolean res = DBHelper.runUpdateSql(sql);
		System.out.println(res+":"+sql);
		return res;
	}
	public static ResultSet allStartGPSTask() {
		String sql = "select * from gpstask where status='start';";
		ResultSet resultSet = DBHelper.runQuerySql(sql);
		return resultSet;
	}
	public static boolean insertAReport(AReport aReport) {
		if (aReport.getST() != null) {
			String sql = "insert into areport3_1(EQID,Station,num,EpiLon,EpiLat,EpiDis,PT,ST,memsMag,gpsMag,createTime,stGPSMag,stGPSMags,stMEMSMag) values('"
					+aReport.getEQID()+"','"+aReport.getStation()+"',"+aReport.getNum()+","+aReport.getEpiLon()+","
					+aReport.getEpiLat()+","+aReport.getEpiDis()+",'"+format.format(aReport.getPT())+"','"
					+format.format(aReport.getST())+"',"+aReport.getMemsMag()+","+aReport.getGpsMag()+",'"+format.format(aReport.getCreateTime())+"',"
					+aReport.getStGPSMag()+",'"+aReport.getStGPSMags()+"',"+aReport.getStMEMSMag()+");";
//			System.out.println(sql);
			return DBHelper.runUpdateSql(sql);
		}else {
			String sql = "insert into areport3_1(EQID,Station,num,EpiLon,EpiLat,EpiDis,PT,memsMag,gpsMag,createTime,stGPSMag,stGPSMags,stMEMSMag) values('"
					+aReport.getEQID()+"','"+aReport.getStation()+"',"+aReport.getNum()+","+aReport.getEpiLon()+","
					+aReport.getEpiLat()+","+aReport.getEpiDis()+",'"+format.format(aReport.getPT())+"',"
					+aReport.getMemsMag()+","+aReport.getGpsMag()+",'"+format.format(aReport.getCreateTime())+"',"
					+aReport.getStGPSMag()+",'"+aReport.getStGPSMags()+"',"+aReport.getStMEMSMag()+");";
//			System.out.println(sql);
			return DBHelper.runUpdateSql(sql);
		}
	}
//	public static boolean insertAReport(String eqID, int num, String aReport) {
//		String sql = "insert into areport(EQID,A" + num + ") values('" + eqID + "','" + aReport + "');";
//		return DBHelper.runUpdateSql(sql);
//	}
//	public static boolean updateAReport(String eqID, int num, String aReport) {
//		String sql = "update areport set A" + num + "='" + aReport + "' where EQID='" + eqID + "';";
//		return DBHelper.runUpdateSql(sql);
//	}
	public static boolean insertRecord(String stationID, Displacement dis) {
//		XYZ xyz = dis.loc.gpsData.xyz;
		XYZ xyz = dis.myCache.myStation.myLocation.getGpsData().xyz;//台站所在位置的XYZ值
		String sql = "insert into records values('"+stationID+"','"+dis.time.toLocaleString()+"',"+xyz.X+","+xyz.Y+","+xyz.Z
				+","+dis.xDisplacement+","+dis.yDisplacement+","+dis.zDisplacement+",'"+dis.localTime.toLocaleString()+"');";
		return DBHelper.runUpdateSql(sql);
	}

	public static boolean abandonFileAnalyseTask(String taskID){
		String sql = "update task set state='abandoned' where taskID='" + taskID + "';";
		boolean res = DBHelper.runUpdateSql(sql);
		return res;
	}
	public static boolean insertEQ(EQInfo eq, String firstStationID) {
		String sql = "insert into EQ(EQID,name,B,L,H,memsMag,gpsMag,originTime,createTime,status,loc,firstStation) values('" + eq.getEQID() + "','" + eq.getName()+"',";
		sql += eq.getEpicenter().getGpsData().blh.B + "," +eq.getEpicenter().getGpsData().blh.L + "," + eq.getEpicenter().getGpsData().blh.H 
//				+ "," + eq.getMagnitude() + ",'" + eq.getEqTime().toLocaleString() + "','"+eq.getCreateTime().toLocaleString()
				+ "," + eq.getMemsMag()+","+eq.getGpsMag() + ",'" + format.format(eq.getEqTime()) + "','"+format.format(eq.getCreateTime())
				+"','进行中','"+eq.getName().substring(eq.getName().indexOf("-")+1, eq.getName().lastIndexOf("-"))+"','"+firstStationID+"');";
		System.out.println(sql);
		return DBHelper.runUpdateSql(sql);
	}
	public static boolean endEQ(String eqID) {
		String sql = "update EQ set status='结束' where EQID='"+eqID+"';";
		return DBHelper.runUpdateSql(sql);
	}
	public static boolean insertReportAll(Collection<Report> reports,boolean isPublished) {
		if (reports.isEmpty() || reports.size() <= 0) {
			return true;
		}
		Iterator<Report> iterator = reports.iterator();
		Report report;
		boolean res = true;
		while(iterator.hasNext()){
//			StringBuilder builder = new StringBuilder("insert into reports(EQID,num,dis,magnitude,inTime,station,isFirst,detail,isPublished) values");
			StringBuilder builder = new StringBuilder("insert into reports(EQID,num,dis,magnitude,inTime,detail,isPublished) values");
			report = iterator.next();
//			builder.append("('"+ report.getEQID() + "'," + report.getNum() + "," + report.getDis() + "," + report.getMagnitude() + ",'" + report.getInTime().toLocaleString()
//					+ "','"+report.getStation()+"',"+report.isFirst()+",'" + report.getDetail()+"'," + isPublished + ");");
			builder.append("('"+ report.getEQID() + "'," + report.getNum() + "," + report.getDis() + "," + report.getMagnitude() + ",'" + format.format(report.getInTime())
					+ "','"+ report.getDetail()+"'," + isPublished + ");");
			boolean singleRes = DBHelper.runUpdateSql(builder.toString().replaceAll("\"", "\\\""));
			res = res && singleRes;
			if (!singleRes) {
				System.out.println(res+","+singleRes+","+builder.length()+","+builder.toString());
			}
		}
		return res;
	}
	public static boolean setReportPublished(String eqID, int reportNum) {
		String sql = "update reports set isPublished=1 where EQID='" + eqID +"' and num=" + reportNum + ";";
		return DBHelper.runUpdateSql(sql);
	}
	public static boolean insertReport(Report report) {
		String sql = "insert into reports(EQID,num,dis,magnitude,inTime,station,isFirst,detail) values('" + report.getEQID() + "'," + report.getNum() + "," + report.getDis() + ","
				+ report.getMagnitude() + ",'" + format.format(report.getInTime()) + "','"+report.getStation()+"',"+report.isFirst()+",'" + report.getDetail() + "');";
		return DBHelper.runUpdateSql(sql);
	}
	public static boolean updateEQ(EQInfo eq) {
		String sql = "update EQ set name='"+eq.getName()+"', B=";
		sql += eq.getEpicenter().getGpsData().blh.B + ",L=" +eq.getEpicenter().getGpsData().blh.L + ",H=" + eq.getEpicenter().getGpsData().blh.H; 
		sql += ",gpsMag=" + eq.getGpsMag() + ", memsMag="+ eq.getMemsMag() 
			+ ", originTime='" + format.format(eq.getEqTime()) + "' where EQID='" + eq.getEQID()+"';";
		System.out.println(sql);
		return DBHelper.runUpdateSql(sql);
	}
}
