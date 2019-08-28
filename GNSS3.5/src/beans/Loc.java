package beans;

import java.io.IOException;

import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.interop.AutomationException;

import arcgis.ArcgisHelper;

public class Loc {

	private GPSData gpsData = null;
	private ProjPoint projPoint = null;
	private IPoint wgs84Point = null;
	public Loc copy() {
		Loc copyLoc = new Loc(gpsData.copy());
		return copyLoc;
	}
	public void dispose() {
		this.gpsData.dispose();
		this.projPoint.dispose();
	}
	public Loc(GPSData gpsData) {
		this.gpsData = gpsData;
		this.projPoint = new ProjPoint(gpsData);
	}
	public Loc(ProjPoint projPoint) {
		this.projPoint = projPoint;
		this.gpsData = projPointToGPSData(projPoint);
	}
	public Loc(GPSData gpsData, ProjPoint projPoint) {
		this.gpsData = gpsData;
		this.projPoint = projPoint;
	}
	public IPoint getWGS84Point() {
		if (wgs84Point != null) {
			return wgs84Point;
		}
		wgs84Point = ArcgisHelper.getWGS84PointInstance(gpsData.blh.L, gpsData.blh.B);
		return wgs84Point;
	}
	private GPSData projPointToGPSData(ProjPoint projPoint){
		try {
			ProjPoint point = projPoint.getPointCopy();
			point.getPoint().project(ArcgisHelper.wgs84CoordinateSystem);
			GPSData gpsData = new GPSData(new BLH(point.getY(),point.getX(), point.getH()));
			return gpsData;
		} catch (IOException e ) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public String toString() {
		return gpsData.toString()+", " + projPoint.toString();
	}
	public GPSData getGpsData() {
		return gpsData;
	}
	public ProjPoint getProjPoint() {
		return projPoint;
	}
}
