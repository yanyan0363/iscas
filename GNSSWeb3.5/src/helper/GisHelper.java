package helper;

import java.math.BigDecimal;

import beans.BLH;
import beans.GPSData;
import beans.XYZ;

public class GisHelper {

	public static BigDecimal aBigDecimal = new BigDecimal("6378137");
	public static BigDecimal bBigDecimal = new BigDecimal("6356752.3142");
	
	public static void main(String[] args) {
		XYZ xyz = BLHtoXYZ(30, 45, 800);
		System.out.println(xyz.toString());
		BLH blh = XYZtoBLH(3909557.6557369004, 3909557.6557369, 3170773.7353560845);
		System.out.println(blh.toString());
	}
	/**
	 * XYZtoBLH
	 * @param x
	 * @param y
	 * @param z
	 * @param b
	 * @param l
	 * @param h
	 */
	public static BLH XYZtoBLH(double x, double y, double z) {
		BLH blh = new BLH();
		blh.L = Math.toDegrees(Math.atan2(y,x));
		BigDecimal xBigDecimal = new BigDecimal(x);
		BigDecimal yBigDecimal = new BigDecimal(y);
		BigDecimal zBigDecimal = new BigDecimal(z);
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
	 * @param x 
	 * @param y
	 * @param z
	 */
	public static XYZ BLHtoXYZ(double b, double l, double h) {
		//转弧度
		b = Math.toRadians(b);
		l = Math.toRadians(l);
		BigDecimal ee = aBigDecimal.multiply(aBigDecimal).subtract(bBigDecimal.multiply(bBigDecimal)).divide(aBigDecimal.multiply(aBigDecimal),9,BigDecimal.ROUND_HALF_EVEN);
		BigDecimal nBigDecimal = aBigDecimal.divide(new BigDecimal(Math.sqrt(new BigDecimal(1).subtract(ee.multiply(new BigDecimal(Math.sin(b)).multiply(new BigDecimal(Math.sin(b))))).doubleValue())),9,BigDecimal.ROUND_HALF_EVEN);
		BigDecimal hBigDecimal = new BigDecimal(h);
		XYZ xyz = new XYZ();
		xyz.X = nBigDecimal.add(hBigDecimal).multiply(new BigDecimal(Math.cos(b))).multiply(new BigDecimal(Math.cos(l))).doubleValue();
		xyz.Y = nBigDecimal.add(hBigDecimal).multiply(new BigDecimal(Math.cos(b))).multiply(new BigDecimal(Math.sin(l))).doubleValue();
		xyz.Z = nBigDecimal.multiply(new BigDecimal(1).subtract(ee)).add(hBigDecimal).multiply(new BigDecimal(Math.sin(b))).doubleValue();
		return xyz;
	}
	
}
