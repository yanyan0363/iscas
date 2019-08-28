package beans;

import arcgis.GisHelper;
import mathUtil.DoubleUtil;

public class GPSData {

	public BLH blh = null;
	public XYZ xyz = null;
	
	public void dispose() {
		this.blh = null;
		this.xyz = null;
	}
//	public GPSData() {
//		this.blh = new BLH();
//		this.xyz = new XYZ();
//	}
	public GPSData copy() {
		GPSData copyGpsData = new GPSData(blh.copy(), xyz.copy());
		return copyGpsData;
	}
	public GPSData(BLH blh) {
		this.blh = blh;
		this.xyz = GisHelper.BLHtoXYZ(blh);
	}
	public GPSData(XYZ xyz) {
		this.xyz = xyz;
		this.blh = GisHelper.XYZtoBLH(xyz);
	}
	public GPSData(BLH blh, XYZ xyz) {
		this.blh = blh;
		this.xyz = xyz;
	}
	/**
	 * 
	 * @param B 纬度
	 * @param L 经度
	 */
//	public GPSData(double B, double L) {
//		this.blh = new BLH();
//		this.blh.B = Double.parseDouble(String.format("%.4f", B));
//		this.blh.L = Double.parseDouble(String.format("%.4f", L));
//	}
	
	/**
	public double getDistance2D(GPSData d){
		if(d == null )
			return 0 ;
		GPSData d1 = this; 
		GPSData d2 = d; 
		double len1 = Math.sqrt((d1.xyz.X-d2.xyz.X)* (d1.xyz.X-d2.xyz.X) + (d1.xyz.Y-d2.xyz.Y)* (d1.xyz.Y-d2.xyz.Y));
		d1 = null ;
		d2 = null ;
		return len1 ;
	}
	
	 **/
	public double getDistance3D(GPSData d){
		if(d == null )
			return 0 ;
		GPSData d1 = this; 
		GPSData d2 = d; 
		double len1 = Math.sqrt(DoubleUtil.add(DoubleUtil.add(Math.pow(DoubleUtil.sub(d1.xyz.X, d2.xyz.X), 2), Math.pow(DoubleUtil.sub(d1.xyz.Y, d2.xyz.Y), 2)), 
				Math.pow(DoubleUtil.sub(d1.xyz.Z, d2.xyz.Z), 2)));
		d1 = null ;
		d2 = null ;
		return len1 ;
	}
	/**
	 * 获取地心距离
	 * @return
	 */
	public double getZDis(){
		GPSData d1 = this; 
		double len1 = Math.sqrt(DoubleUtil.add(DoubleUtil.add(Math.pow(d1.xyz.X, 2), Math.pow(d1.xyz.Y, 2)), Math.pow(d1.xyz.Z, 2)));
		d1 = null ;
		return DoubleUtil.round(len1, 2);
	}
	@Override
	public String toString() {
		if (blh == null) {
			if (xyz == null) {
				return null;
			}else{
				return xyz.toString();
			}
		}else {
			if (xyz == null) {
				return blh.toString();
			}else {
				return blh.toString()+", " + xyz.toString();
			}
		}
	}
}
