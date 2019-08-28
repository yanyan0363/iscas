package beans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.esri.arcgis.geoprocessing.tools.analyst3dtools.Int;

import arcgis.ArcgisEQHelper;
import baseObject.BaseStation;

public class EQInfo {

	private String EQID = null;
	private Loc epicenter = null;//震中
//	private double magnitude;//震级
	private double gpsMag = 0;//震级
	private double memsMag = 0;//震级
	private Date eqTime = null;//发震时间
	private List<String> stationIDs = new ArrayList<>();//台站ID信息
//	private String state;//表示当前地震的状态
//	private BaseStation firstStation;
	private Date createTime = null;//当前EQ的生成时间,本机时间
	private String name = null;//地震名称，yyyyMMdd hhmmss—地区—震级

	private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	
	public EQInfo(Loc epicenter, Date eqTime,String stationID, Date createTime) {
		this.EQID = new Date().getTime()+"";
		this.epicenter = epicenter;
		this.eqTime = eqTime;
		this.stationIDs.add(stationID);
		this.createTime = createTime;
	}
	
//	public EQInfo(Loc epicenter, int magnitude, Date eqTime,String stationID, Date createTime) {
//		this.EQID = new Date().getTime()+"";
//		this.epicenter = epicenter;
//		this.magnitude = magnitude;
//		this.eqTime = eqTime;
//		this.stationIDs.add(stationID);
//		this.createTime = createTime;
//	}
	public void dispose() {
		this.EQID = null;
		this.epicenter.dispose();
		this.epicenter = null;
		this.eqTime = null;
		this.stationIDs.clear();
		this.stationIDs = null;
		this.createTime = null;
		this.name = null;
	}
	/**
	 * 生成或更新name
	 * 发震时刻-震中位置-震级（gpsMag、memsMag中的较大值）
	 */
	public void updateEQName(){
		this.name = format.format(this.eqTime) + "-" + ArcgisEQHelper.getCountyNameFromLoc(epicenter) + "-" + Math.max(gpsMag, memsMag)+"级";
	}
//	public BaseStation getFirstStation() {
//		return firstStation;
//	}
//
//	public void setFirstStation(BaseStation firstStation) {
//		this.firstStation = firstStation;
//	}

	public void addStationID(String stationID) {
		this.stationIDs.add(stationID);
	}
	
	public List<String> getStationIDs() {
		return stationIDs;
	}
	public String getEQID() {
		return EQID;
	}
	public Loc getEpicenter() {
		return epicenter;
	}
	public void setEpicenter(Loc epicenter) {
		this.epicenter = epicenter;
	}
	public Date getEqTime() {
		return eqTime;
	}
	public void setEqTime(Date eqTime) {
		this.eqTime = eqTime;
	}
	public Date getCreateTime() {
		return createTime;
	}

	public String getName() {
		return name;
	}

	public double getGpsMag() {
		return gpsMag;
	}

	public void setGpsMag(double gpsMag) {
		this.gpsMag = gpsMag;
	}

	public double getMemsMag() {
		return memsMag;
	}

	public void setMemsMag(double memsMag) {
		this.memsMag = memsMag;
	}
	public String getBLString() {
		BLH blh = epicenter.getGpsData().blh;
		String res = "";
		if (blh.L > 0) {
			res = blh.L + "°E ";
		}else if (blh.L < 0) {
			res = blh.L + "°W ";
		}else {
			res = blh.L + "° ";
		}
		if (blh.B > 0) {
			res += blh.B + "°N";
		}else if (blh.B < 0) {
			res += blh.B + "°S";
		}else {
			res += blh.B+"°";
		}
		return res;
	}
	
	
}
