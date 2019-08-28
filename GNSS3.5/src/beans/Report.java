package beans;

import java.util.Date;

import dataCache.Displacement;

public class Report {

	private String EQID = null;
	private int num = 0;
	private double dis = 0;
	private double magnitude = 0;
	private Date inTime = null;//时报生成时间,首台站的最后一个数据的时间
	private String station = null;
	private boolean isFirst;//距离震中最近的台站
	private String detail = null;
	
	
	public Report(String EQID, int num, double dis, double magnitude, Date inTime,String station, boolean isFirst, String detail) {
		this.EQID = EQID;
		this.num = num;
		this.dis = dis;
		this.magnitude = magnitude;
		this.inTime = inTime;
		this.station = station;
		this.isFirst = isFirst;
		this.detail = detail;
	}
	
//	public Report(String EQID, int num, double dis, double magnitude, Date inTime,String station, String detail) {
//		this.EQID = EQID;
//		this.num = num;
//		this.dis = dis;
//		this.magnitude = magnitude;
//		this.inTime = inTime;
//		this.station = station;
//		this.detail = detail;
//	}


	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public double getDis() {
		return dis;
	}

	public void setDis(double dis) {
		this.dis = dis;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public void setMagnitude(double magnitude) {
		this.magnitude = magnitude;
	}

	public Date getInTime() {
		return inTime;
	}

	public void setInTime(Date inTime) {
		this.inTime = inTime;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getEQID() {
		return EQID;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	
}
