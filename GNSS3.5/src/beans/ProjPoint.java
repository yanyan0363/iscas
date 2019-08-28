package beans;

import java.io.IOException;

import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.ISpatialReference;
import com.esri.arcgis.interop.AutomationException;

import arcgis.ArcgisHelper;
import mathUtil.DoubleUtil;

public class ProjPoint {


	private IPoint point = null;
	private double H = 0;//BLH中的H值
	public void dispose() {
		this.point = null;
	}
	public ProjPoint() {
		this.point = ArcgisHelper.getPointInstance();
		try {
			point.setSpatialReferenceByRef(ArcgisHelper.webMercatorCoordinateSystem);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 返回IPoint的点拷贝，包含ArcgisHelper.projectedCoordinateSystem,即esriSRProjCS_Xian1980_3_Degree_GK_CM_102E投影的XY信息
	 * @return
	 */
	public ProjPoint getPointCopy() {
		ProjPoint copyPoint = new ProjPoint();
		try {
			copyPoint.point.setX(point.getX());
			copyPoint.point.setY(point.getY());
			copyPoint.point.setZ(point.getZ());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		copyPoint.H = this.H;
		return copyPoint;
	}
	/**
	 * 获取当前点对象的WGS84点对象的拷贝
	 * @return
	 */
	public IPoint getWGS84PointCopy() {
		ProjPoint point = this.getPointCopy();
		try {
			point.getPoint().project(ArcgisHelper.wgs84CoordinateSystem);
			return point.getPoint();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public double getX() {
		try {
			return point.getX();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public double getY() {
		try {
			return point.getY();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	//BLH中的H值
	public double getH() {
		return H;
	}
	//BLH中的H值
	public void setH(double h) {
		H = h;
	}
	public ISpatialReference getSpatialReference() throws AutomationException, IOException {
		return point.getSpatialReference();
	}
	public void setX(double X) throws AutomationException, IOException {
		point.setX(X);
	}
	public void setY(double Y) throws AutomationException, IOException {
		point.setY(Y);
	}
	public IPoint getPoint() {
		return point;
	}
	/**
	 * 
	 * @param X 横轴
	 * @param Y 纵轴
	 * @param H BLH中的H值
	 */
	public ProjPoint(double X, double Y, double H){
		this.point = ArcgisHelper.getPointInstance();
		try {
			point.setSpatialReferenceByRef(ArcgisHelper.webMercatorCoordinateSystem);
			point.setX(X);
			point.setY(Y);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.H = H;
	}
	public ProjPoint(GPSData gpsData){
		this.point = ArcgisHelper.getPointInstance();
		try {
			point.setSpatialReferenceByRef(ArcgisHelper.wgs84CoordinateSystem);
			point.setX(gpsData.blh.L);
			point.setY(gpsData.blh.B);
			point.project(ArcgisHelper.webMercatorCoordinateSystem);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.H = gpsData.blh.H;
	}

	@Override
	public String toString() {
		try {
			return point.getSpatialReference().getName()+",X:"+point.getX()+",Y:"+point.getY()+",H:"+this.getH();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
