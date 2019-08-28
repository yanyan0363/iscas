package arcgis;

import java.io.IOException;
import java.math.BigDecimal;

import com.esri.arcgis.geometry.IPoint;

import beans.BLH;
import beans.XYZ;

public class GisHelper {

	public static BigDecimal aBigDecimal = new BigDecimal("6378137");//地球长半轴，单位m
	public static BigDecimal bBigDecimal = new BigDecimal("6356752.3142");//地球短半轴，单位m
	public static double R = 6370;//表示地球半径，单位km，用于表示经纬度计算距离的地球平均半径
	
	public static void main(String[] args) {
//		XYZ xyz = BLHtoXYZ(new BLH(30, 45, 800));
//		System.out.println(xyz.toString());
//		BLH blh = XYZtoBLH(new XYZ(3909557.6557369004, 3909557.6557369, 3170773.7353560845));
//		System.out.println(blh.toString());
//		System.out.println(metersToDecimalDegree(222.6));
		
//		System.out.println("here...");
//		double res = calDisByBL(102, 31, 102, 32);
//		System.out.println(res);
//		res = calDisByBL(102, 31, 103, 31);
//		System.out.println(res);
//		System.out.println("calDisByBLProjTo3857::");
//		res = calDisByBLProjTo3857(102, 31, 102, 32);
//		System.out.println(res);
//		res = calDisByBLProjTo3857(102, 31, 103, 31);
//		System.out.println(res);
		
	}
	/**
	 * 长度单位m转为wgs84中的单位DecimalDegree
	 * 转换方式来源于网络，正确性有待确认_不可用，废弃
	 * @param meters
	 * @return
	 */
	public static double metersToDecimalDegree(double meters){
		return new BigDecimal(meters).divide(new BigDecimal(111200), 5).doubleValue();
	}
	/**
	 * XYZtoBLH
	 * @param x
	 * @param y
	 * @param z
	 */
	public static BLH XYZtoBLH(XYZ xyz) {
		BLH blh = new BLH();
		blh.L = Math.toDegrees(Math.atan2(xyz.Y,xyz.X));
		BigDecimal xBigDecimal = new BigDecimal(xyz.X);
		BigDecimal yBigDecimal = new BigDecimal(xyz.Y);
		BigDecimal zBigDecimal = new BigDecimal(xyz.Z);
		BigDecimal epep = (aBigDecimal.multiply(aBigDecimal).subtract(bBigDecimal.multiply(bBigDecimal))).divide(bBigDecimal.multiply(bBigDecimal),9,BigDecimal.ROUND_HALF_EVEN);
		BigDecimal xy = xBigDecimal.multiply(xBigDecimal).add(yBigDecimal.multiply(yBigDecimal));
		BigDecimal xySqart = new BigDecimal(Math.sqrt(xy.doubleValue())); 
		double theata = Math.atan(aBigDecimal.multiply(zBigDecimal).divide(xySqart.multiply(bBigDecimal),9,BigDecimal.ROUND_HALF_EVEN).doubleValue());
		BigDecimal ee = aBigDecimal.multiply(aBigDecimal).subtract(bBigDecimal.multiply(bBigDecimal)).divide(aBigDecimal.multiply(aBigDecimal),9,BigDecimal.ROUND_HALF_EVEN);
		blh.B = Math.atan2(zBigDecimal.add(epep.multiply(bBigDecimal).multiply(new BigDecimal(Math.pow(Math.sin(theata), 3)))).doubleValue(), 
				xySqart.subtract(ee.multiply(aBigDecimal).multiply(new BigDecimal(Math.pow(Math.cos(theata), 3)))).doubleValue());
		BigDecimal nBigDecimal = aBigDecimal.divide(new BigDecimal(Math.sqrt(new BigDecimal(1).subtract(ee.multiply(new BigDecimal(Math.sin(blh.B)*Math.sin(blh.B)))).doubleValue())),5,BigDecimal.ROUND_HALF_EVEN);
		blh.H = xySqart.divide(new BigDecimal(Math.cos(blh.B)),9,BigDecimal.ROUND_HALF_EVEN).subtract(nBigDecimal).doubleValue();
		//弧度转角度
		blh.B = Math.toDegrees(blh.B);
		return blh;
	}
	/**
	 * BLHtoXYZ
	 * @param b 纬度
	 * @param l 经度
	 * @param h 高度
	 */
	public static XYZ BLHtoXYZ(BLH blh) {
		//转弧度
		double b = Math.toRadians(blh.B);
		double l = Math.toRadians(blh.L);
		BigDecimal ee = aBigDecimal.multiply(aBigDecimal).subtract(bBigDecimal.multiply(bBigDecimal)).divide(aBigDecimal.multiply(aBigDecimal),9,BigDecimal.ROUND_HALF_EVEN);
		BigDecimal nBigDecimal = aBigDecimal.divide(new BigDecimal(Math.sqrt(new BigDecimal(1).subtract(ee.multiply(new BigDecimal(Math.sin(b)).multiply(new BigDecimal(Math.sin(b))))).doubleValue())),9,BigDecimal.ROUND_HALF_EVEN);
		BigDecimal hBigDecimal = new BigDecimal(blh.H);
		XYZ xyz = new XYZ();
		xyz.X = nBigDecimal.add(hBigDecimal).multiply(new BigDecimal(Math.cos(b))).multiply(new BigDecimal(Math.cos(l))).doubleValue();
		xyz.Y = nBigDecimal.add(hBigDecimal).multiply(new BigDecimal(Math.cos(b))).multiply(new BigDecimal(Math.sin(l))).doubleValue();
		xyz.Z = nBigDecimal.multiply(new BigDecimal(1).subtract(ee)).add(hBigDecimal).multiply(new BigDecimal(Math.sin(b))).doubleValue();
		return xyz;
	}
	
	/**
	 * 经纬度计算两点距离，单位与R一致，为km
	 * @param x1 起点经度
	 * @param y1 起点纬度
	 * @param x2 终点经度
	 * @param y2 终点纬度
	 * @return
	 */
//	public static double calDisByBL(double x1, double y1, double x2, double y2) {
//		return R*Math.acos(Math.sin(degreeToRadian(y1))*Math.sin(degreeToRadian(y2))+Math.cos(degreeToRadian(y1))*Math.cos(degreeToRadian(y2))*Math.cos(degreeToRadian(x1)-degreeToRadian(x2)));
//	}
	/**
	 * 经纬度计算两点距离,使用3857投影，单位为km,
	 * @param x1 起点经度
	 * @param y1 起点纬度
	 * @param x2 终点经度
	 * @param y2 终点纬度
	 * @return
	 */
	public static double calDisByBLProjTo3857(double x1, double y1, double x2, double y2) {
		IPoint point1 = ArcgisHelper.getWGS84PointInstance(x1, y1);
		IPoint point2 = ArcgisHelper.getWGS84PointInstance(x2, y2);
		try {
			point1.project(ArcgisHelper.webMercatorCoordinateSystem);
			point2.project(ArcgisHelper.webMercatorCoordinateSystem);
			double dx = point1.getX() - point2.getX();
			double dy = point1.getY() - point2.getY();
			return Math.sqrt(dx*dx+dy*dy)/1000;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	/**
	 * 根据投影坐标计算两点距离，单位为km,
	 * @param x1 起点横轴
	 * @param y1 起点纵轴
	 * @param x2 终点横轴
	 * @param y2 终点纵轴
	 * @return
	 */
	public static double calDisByProjPoint(double x1, double y1, double x2, double y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return Math.sqrt(dx*dx+dy*dy)/1000;
	}
	/**
	 * 角度转弧度
	 * @param degree 角度
	 * @return 弧度
	 */
	private static double degreeToRadian(double degree) {
		return degree*Math.PI/180;
	}
	/**
	 * 弧度转角度
	 * @param radian 弧度
	 * @return 角度
	 */
	private static double radianToDegree(double radian) {
		return radian*180/Math.PI;
	}
	/**
	 * 计算震级
	 * @param maxDis 垂直方向最大位移
	 * @param epiDistance 震中距
	 * @return
	 */
//	public static double calMagnitude(double maxDis, double epiDistance) {
//		if (maxDis == 0) {
//			return 0;
//		}
//		return Double.parseDouble(String.format("%.2f", (Math.log10(maxDis) + 1.66*Math.log(epiDistance)+2.0)));
//	}
//	/**
//	 * 
//	 * @param maxPGD ENU合位移最大值，单位：m
//	 * @param epiDis 震中距，单位：m
//	 * @return 震级
//	 */
//	public static double pgdCalMagnitude(double maxPGD, double epiDis) {
//		if (maxPGD <= 0 || epiDis == 0) {
//			return 0;
//		}
////		System.out.println("maxPGD:"+maxPGD+", epiDis:" + epiDis);
////		return Double.parseDouble(String.format("%.2f", (Math.log10(maxPGD)+4.434)/(1.047-1.038*Math.log(epiDis))));
//		return DoubleUtil.div(DoubleUtil.add(Math.log10(maxPGD*100), 4.434), DoubleUtil.sub(1.047, DoubleUtil.mul(0.138, Math.log10(epiDis/1000))), 2);
//	}
}
