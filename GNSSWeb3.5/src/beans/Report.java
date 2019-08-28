package beans;

import java.util.Date;

public class Report {

	private String EQID;
	private int num;
	private double dis;
	private int magnitude;
	private Date inTime;
	private String station;
	private boolean isFirst;
	private String detail;
	
	public Report(String EQID, int num, double dis, int magnitude, Date inTime,String station, boolean isFirst, String detail) {
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

	public int getNum() {
		return num;
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public double getDis() {
		return dis;
	}

	public void setDis(double dis) {
		this.dis = dis;
	}

	public int getMagnitude() {
		return magnitude;
	}

	public void setMagnitude(int magnitude) {
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
	
	
}
