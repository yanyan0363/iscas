package beans;

import java.util.Date;

public class AReport {
	private String EQID = null;
	private String Station = null;
	private int num = 0;
	private double EpiLon = 0;//震中经度
	private double EpiLat = 0;//震中纬度
	private double EpiDis = 0;//震中距,单位km
//	private Date EpiT;//震中时刻
	private Date PT = null;//P波到达时刻
	private Date ST = null;//S波到达时刻
	private double memsMag = 0;//mems计算震级，为各个台站MEMS震级的平均值
	private double gpsMag = 0;//gps计算震级，为各个台站GPS震级的平均值
	private Date createTime = null;//当前速报生成时刻
	private double stGPSMag = 0;//用于存储当前台站的gpsMag
	private String stGPSMags = null;//用于存储已触发台站在当前时报中的M,JsonArray表示[{"station":stationID,"M":M},...,{...}]
	private double stMEMSMag = 0;//用于存储当前台站的MEMSMag
//	private String stMEMSMags;//stMEMSMag仅计算一次
//	public AReport(String eQID, String station, int num, double epiLon, double epiLat, double epiDis, Date pT, Date sT,
//			double gpsMag, double memsMag, Date createTime, double stGPSMag, String stGPSMags) {
//		super();
//		this.EQID = eQID;
//		this.Station = station;
//		this.num = num;
//		this.EpiLon = epiLon;
//		this.EpiLat = epiLat;
//		this.EpiDis = epiDis;
//		this.PT = pT;
//		this.ST = sT;
//		this.gpsMag = gpsMag;
//		this.memsMag = memsMag;
//		this.createTime = createTime;
//		this.stGPSMag = stGPSMag;
//		this.stGPSMags = stGPSMags;
//	}
//	public AReport(String eQID, String station, int num, double epiLon, double epiLat, double epiDis, Date pT,
//			double gpsMag, double memsMag, Date createTime, double stGPSMag, String stGPSMags) {
//		super();
//		this.EQID = eQID;
//		this.Station = station;
//		this.num = num;
//		this.EpiLon = epiLon;
//		this.EpiLat = epiLat;
//		this.EpiDis = epiDis;
//		this.PT = pT;
//		this.gpsMag = gpsMag;
//		this.memsMag = memsMag;
//		this.createTime = createTime;
//		this.stGPSMag = stGPSMag;
//		this.stGPSMags = stGPSMags;
//	}
	public AReport(String eQID, String station, int num, double epiLon, double epiLat, double epiDis, Date pT, Date sT,
			double memsMag, double gpsMag, Date createTime, double stGPSMag, String stGPSMags, double stMEMSMag) {
		super();
		this.EQID = eQID;
		this.Station = station;
		this.num = num;
		this.EpiLon = epiLon;
		this.EpiLat = epiLat;
		this.EpiDis = epiDis;
		this.PT = pT;
		this.ST = sT;
		this.memsMag = memsMag;
		this.gpsMag = gpsMag;
		this.createTime = createTime;
		this.stGPSMag = stGPSMag;
		this.stGPSMags = stGPSMags;
		this.stMEMSMag = stMEMSMag;
	}
//	public AReport(String eQID, String station, int num, double epiLon, double epiLat, double epiDis, Date pT,
//			double gpsMag, Date createTime, double stGPSMag, String stGPSMags) {
//		super();
//		this.EQID = eQID;
//		this.Station = station;
//		this.num = num;
//		this.EpiLon = epiLon;
//		this.EpiLat = epiLat;
//		this.EpiDis = epiDis;
//		this.PT = pT;
//		this.gpsMag = gpsMag;
//		this.createTime = createTime;
//		this.stGPSMag = stGPSMag;
//		this.stGPSMags = stGPSMags;
//	}
	public String getEQID() {
		return EQID;
	}
	public String getStation() {
		return Station;
	}
	public int getNum() {
		return num;
	}
	public double getEpiLon() {
		return EpiLon;
	}
	public double getEpiLat() {
		return EpiLat;
	}
	public double getEpiDis() {
		return EpiDis;
	}
	public Date getPT() {
		return PT;
	}
	public Date getST() {
		return ST;
	}
	public double getMemsMag() {
		return memsMag;
	}
	public double getGpsMag() {
		return gpsMag;
	}
	public Date getCreateTime() {
		return createTime;
	}
	
	public double getStGPSMag() {
		return stGPSMag;
	}
	public void setStGPSMag(double stGPSMag) {
		this.stGPSMag = stGPSMag;
	}
	public String getStGPSMags() {
		return stGPSMags;
	}
	public void setStGPSMags(String stGPSMags) {
		this.stGPSMags = stGPSMags;
	}
	public double getStMEMSMag() {
		return stMEMSMag;
	}
	public void setStMEMSMag(double stMEMSMag) {
		this.stMEMSMag = stMEMSMag;
	}
	public void setMemsMag(double memsMag) {
		this.memsMag = memsMag;
	}
	public void setGpsMag(double gpsMag) {
		this.gpsMag = gpsMag;
	}
	public void dispose() {
		this.EQID = null;
		this.Station = null;
		this.PT = null;
		this.ST = null;
		this.createTime = null;
		this.stGPSMags = null;
	}
}
