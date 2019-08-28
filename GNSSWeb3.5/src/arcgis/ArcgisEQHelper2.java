package arcgis;

import java.io.IOException;
import java.util.Date;


import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geometry.BezierCurve;
import com.esri.arcgis.geometry.CircularArc;
import com.esri.arcgis.geometry.GeometryEnvironment;
import com.esri.arcgis.geometry.IBezierCurveGEN;
import com.esri.arcgis.geometry.ICircularArc;
import com.esri.arcgis.geometry.IGeometry;
import com.esri.arcgis.geometry.IGeometryBridge2;
import com.esri.arcgis.geometry.IGeometryCollection;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.IPointCollection4;
import com.esri.arcgis.geometry.IPolygon;
import com.esri.arcgis.geometry.IPolyline;
import com.esri.arcgis.geometry.IRing;
import com.esri.arcgis.geometry.ISegment;
import com.esri.arcgis.geometry.ISegmentCollection;
import com.esri.arcgis.geometry.ITopologicalOperator;
import com.esri.arcgis.geometry.Line;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.geometry.Polygon;
import com.esri.arcgis.geometry.Polyline;
import com.esri.arcgis.geometry.Ring;
import com.esri.arcgis.system._WKSPoint;

import beans.BLH;
import beans.EQ;
import beans.GPSData;
import helper.GisHelper;
import utils.SystemDefines.EQPointType;

public class ArcgisEQHelper2 {
	public static void main(String[] args) {
		System.out.println("here...");
//		boolean res = addEQPoint("EQPoints", new EQ(new GPSData(-1228223.212991, 5362704.159926, 3218975.250421, utils.SystemDefines.pointTypeDef.Coordinate), 6, new Date()), 1, "notes");
//		System.out.println("res:" + res);
		
//		boolean res = addEQPolygon(new EQ(new GPSData(-1228223.212991, 5362704.159926, 3218975.250421, utils.SystemDefines.pointTypeDef.Coordinate), new Date(), ""), 1, "notes");
//		System.out.println(res);
		
		//IPolygon polygon = createPolygonBySegments();
//		boolean ss = addEQPolygon(new EQ(new GPSData(-1228223.212991, 5362704.159926, 3218975.250421, utils.SystemDefines.pointTypeDef.Coordinate), new Date(), ""), 1, "notes");
//		System.out.println(ss);
	}
	/**
	 * 娴嬭瘯鐢紝鐢熸垚鍦嗙幆闈�
	 * @return
	 */
	public static IPolygon createPolygonBySegments() {
		IPolygon polygon;
		try {
			polygon = new Polygon();
			ICircularArc circularArc1 = new CircularArc();
			ICircularArc circularArc2 = new CircularArc();
			IBezierCurveGEN bezCurve = new BezierCurve();
			ISegmentCollection ring1 = new Ring();
			ISegmentCollection ring2 = new Ring();
			ring1.addSegment((ISegment)circularArc1, null, null);
			ring2.addSegment((ISegment)circularArc2, null, null);
			IGeometryCollection geometryCollection = (IGeometryCollection)polygon;
			geometryCollection.addGeometry((IGeometry)ring1, null, null);
			geometryCollection.addGeometry((IGeometry)ring2, null, null);
			
			IPoint point = new Point();
			point.setX(100);
			point.setY(35);
			circularArc1.putCoordsByAngle(point, 0, 2*Math.PI, 1);
			circularArc2.putCoordsByAngle(point, 0, 2*Math.PI, 0.5);
			IPoint[] points = new IPoint[]{
					new Point(), new Point(), new Point(), new Point()
			};
			points[0].setX(70);
			points[0].setY(5);
			points[1].setX(70);
			points[1].setY(20);
			points[2].setX(90);
			points[2].setY(20);
			points[3].setX(70);
			points[3].setY(5);
			bezCurve.putCoords(points);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return polygon;
	}
	//鍖呮嫭鐐硅绱狅紝浠ュ強鐩稿叧鐨勫湴闇囦俊鎭�
	/**
	 * 
	 * @param featureName
	 * @param eq
	 * @param num
	 * @param note
	 * @param type 鐐圭被鍨�
	 * @return
	 */
//	public static boolean addEQPoint(EQ eq, int num, String note, EQPointType type) {
//		IFeatureClass featureClass;
//		try {
//			featureClass = ArcgisHelper.getFeatureWorkspace().openFeatureClass("EQPoints");
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//		IPoint epic = GPSDataToIPoint(eq.getEpicenter());
//		IFeature pointFeature;
//		try {
//			pointFeature = featureClass.createFeature();
//			pointFeature.setShapeByRef(epic);
//			pointFeature.setValue(2, eq.getEQID());
//			pointFeature.setValue(3, eq.getMagnitude());
//			pointFeature.setValue(4, eq.getOriginTime());
//			pointFeature.setValue(5, num);
//			pointFeature.setValue(6, note);
//			pointFeature.setValue(7, eq.getStationIDs());
//			pointFeature.setValue(8, type.toString());
//			pointFeature.store();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}
//	
	private static IPoint GPSDataToIPoint(GPSData gpsData) {
		System.out.println(gpsData.toString());
		IPoint point;
		try {
			point = new Point();
			//鏂归噷缃戝潗鏍�
			if (gpsData.myPointType == utils.SystemDefines.pointTypeDef.Coordinate) {
				BLH blh = GisHelper.XYZtoBLH(gpsData.x, gpsData.y, gpsData.z);
				point.setX(blh.L);
				point.setY(blh.B);
				point.setZ(blh.H);
			}else{//缁忕含搴﹀潗鏍�
				point.setX(gpsData.x);
				point.setY(gpsData.y);
				point.setZ(gpsData.z);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return point;
	}
	//鍖呮嫭闈㈣绱犱互鍙婄浉鍏崇殑鍦伴渿淇℃伅
//	public static boolean addEQPolygon(EQ eq, int num, String note) {
//		IFeatureClass featureClass;
//		try {
//			featureClass = ArcgisHelper.getFeatureWorkspace().openFeatureClass("EQPolygons");
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//		IFeature polygonFeature;
//		try {
//			polygonFeature = featureClass.createFeature();
//			polygonFeature.setShapeByRef(ArcgisHelper.createAnnulus(GPSDataToIPoint(eq.getEpicenter()), 0.5, 1));
//			polygonFeature.setValue(2, eq.getEQID());
//			polygonFeature.setValue(3, 0);//intensity
//			polygonFeature.setValue(4, note);//note
//			polygonFeature.setValue(5, num);
//			polygonFeature.store();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}

	//鍖呮嫭绾胯绱狅紝浠ュ強鐩稿叧鐨勫湴闇囦俊鎭�
	public static boolean addEQLine() {
		
		return true;
	}
	
	
	

}
