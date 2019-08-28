package beans;

import helper.GisHelper;
import utils.SystemDefines;
import utils.SystemDefines.pointTypeDef;

public class GPSData {

	public SystemDefines.pointTypeDef myPointType = SystemDefines.pointTypeDef.none ;
	public double x = 0 ;
	public double y = 0 ;
	public double z = 0 ;
	public GPSData(double x1, double y1, double z1,SystemDefines.pointTypeDef t){
		x = x1 ;
		y = y1;
		z = z1 ;
		this.myPointType = t ;
	}
//	public double getXCoordinate(){
//		if(this.myPointType ==SystemDefines.pointTypeDef.LonLat )
//		    return convertLng(x) ;
//		return x ; 
//	}
//	public double getYCoordinate(){
//		if(this.myPointType ==SystemDefines.pointTypeDef.LonLat )
//	    	return convertLat(y) ;
//		return y; 
//	}
//	public GPSData getMidPoint(GPSData d){
//		if (d == null) {
//			return null;
//		}
//		double x1,y1;
//		if (myPointType != pointTypeDef.Coordinate) {
//			x1 = getXCoordinate();
//			y1 = getYCoordinate();
//		}else {
//			x1 = this.x;
//			y1 = this.y;
//		}
//		if (d.myPointType != pointTypeDef.Coordinate) {
//			x1+=d.getXCoordinate();
//			y1+=d.getYCoordinate();
//		}else {
//			x1+=d.x;
//			y1+=d.y;
//		}
//		return new GPSData(x1/2, y1/2, (this.z+d.z)/2, pointTypeDef.Coordinate);
//	}
	public double getDistance2D(GPSData d){
		if(d == null )
			return 0 ;
		GPSData d1 = this; 
		if(this.myPointType ==  SystemDefines.pointTypeDef.LonLat)
			d1 = getCoordinatePointFromLonLat(this.x, this.y, this.z) ;
		GPSData d2 = d; 
		if(d.myPointType ==SystemDefines.pointTypeDef.LonLat)
			d2 = getCoordinatePointFromLonLat(d.x, d.y, d.z) ;
		
		double len = Math.sqrt((d1.x-d2.x)* (d1.x-d2.x) + (d1.y-d2.y)* (d1.y-d2.y));
		d1 = null ;
		d2 = null ;
		return len ;
	}
	public static GPSData getCoordinatePointFromLonLat(double lng, double lat, double z){
		XYZ xyz = GisHelper.BLHtoXYZ(lat, lng, z);
		return new GPSData(xyz.X,xyz.Y,xyz.Z,SystemDefines.pointTypeDef.Coordinate ) ;
	}
//	public static double convertLng(double v){
//		return -1; 
//	}
//	public static double convertLat(double v){
//		return -1; 
//	}
	@Override
	public String toString() {
		return myPointType+", " + x + ", " + y + ", " + z;
	}
}
