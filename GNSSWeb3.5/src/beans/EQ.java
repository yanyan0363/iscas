package beans;

import java.util.Date;

import com.esri.arcgis.geoprocessing.tools.analyst3dtools.Int;

public class EQ {

	private String EQID;
	private GPSData epicenter;//震中
	private double magnitude;//震级
	private Date originTime;//发震时间
	private String stationIDs = "";//台站ID信息，以,间隔
	
	public EQ(GPSData epicenter, Date originTime, String stationID) {
		this.EQID = originTime.getTime()+"";
		this.epicenter = epicenter;
		this.originTime = originTime;
		this.stationIDs = stationID;
	}
	
	public EQ(GPSData epicenter, int magnitude, Date originTime, String stationIDs) {
		this.EQID = originTime.getTime()+"";
		this.epicenter = epicenter;
		this.magnitude = magnitude;
		this.originTime = originTime;
		this.stationIDs = stationIDs;
	}
	
	public void addStationID(String stationID) {
		this.stationIDs += (","+stationID);
	}
	
	public String getStationIDs() {
		return stationIDs;
	}
	public String getEQID() {
		return EQID;
	}
	public GPSData getEpicenter() {
		return epicenter;
	}
	public void setEpicenter(GPSData epicenter) {
		this.epicenter = epicenter;
	}
	public Date getOriginTime() {
		return originTime;
	}
	public void setOriginTime(Date originTime) {
		this.originTime = originTime;
	}
	public double getMagnitude() {
		return magnitude;
	}
	public void setMagnitude(double magnitude) {
		this.magnitude = magnitude;
	}
	
	
	
}
