package arcgis;

import java.io.IOException;

import com.esri.arcgis.carto.IFeatureLayer;
import com.esri.arcgis.datasourcesfile.ShapefileWorkspaceFactory;
import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureCursor;
import com.esri.arcgis.geodatabase.IFeatureWorkspace;
import com.esri.arcgis.geodatabase.IFields;
import com.esri.arcgis.geodatabase.ISpatialFilter;
import com.esri.arcgis.geodatabase.SpatialFilter;
import com.esri.arcgis.geodatabase.esriSpatialRelEnum;
import com.esri.arcgis.geometry.CircularArc;
import com.esri.arcgis.geometry.GeometryEnvironment;
import com.esri.arcgis.geometry.ICircularArc;
import com.esri.arcgis.geometry.IGeometry;
import com.esri.arcgis.geometry.IGeometryBridge2;
import com.esri.arcgis.geometry.IGeometryCollection;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.IPointCollection4;
import com.esri.arcgis.geometry.IPolygon;
import com.esri.arcgis.geometry.IPolyline;
import com.esri.arcgis.geometry.ISegment;
import com.esri.arcgis.geometry.ISegmentCollection;
import com.esri.arcgis.geometry.ISpatialReference;
import com.esri.arcgis.geometry.Path;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.geometry.Polygon;
import com.esri.arcgis.geometry.Polyline;
import com.esri.arcgis.geometry.Ring;
import com.esri.arcgis.geometry.SpatialReferenceEnvironment;
import com.esri.arcgis.geometry.esriSRGeoCSType;
import com.esri.arcgis.system.AoInitialize;
import com.esri.arcgis.system.EngineInitializer;
import com.esri.arcgis.system._WKSPoint;
import com.esri.arcgis.system.esriLicenseProductCode;
import com.esri.arcgis.system.esriLicenseStatus;

import utils.Config;

public class ArcgisHelper {

	
//	private static IWorkspaceFactory workspaceFactory;
//	private static IWorkspace workspace;
//	private static IFeatureWorkspace featureWorkspace;
//	private static IMapDocument mapDoc =null;
//	private static ILayer provLayer = null;
//	private static ILayer railLayer = null;
//	private static ILayer stationsLayer = null;
	private static ShapefileWorkspaceFactory shpFactory = null;
	private static IFeatureWorkspace demoShpFWorkspace = null;
	private static IFeatureClass stationsShpDemo = null;

	public static void main(String[] args) {
//		try {
//			IPoint center = new Point();
//			center.setX(108.10605017383432);
//			center.setY(27.405890505682276);
//			center.setZ(21406.855293651);
//			IPolygon polygon = createAnnulus(center, 0, 5.436956498229184);
//			addPolygonToFeatureClass("EQPolygons", polygon);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			IFields fields = stationsShpDemo.getFields();
//			IFeature feature = null;
//			for (int i = 0; i < stationsShpDemo.featureCount(null); i++) {
//				feature = stationsShpDemo.getFeature(i);
//				feature.setValue(9, i);
//				feature.store();
//				System.out.println(stationsShpDemo.getFeature(i).getValue(3)+"::"+stationsShpDemo.getFeature(i).getValue(9));
//			}
//			
//			System.out.println(stationsShpDemo.featureCount(null));
//			System.out.println(stationsShpDemo.getShapeType());
//			System.out.println(fields.getFieldCount());
//			System.out.println(fields.findField("VMaxDis"));
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}

	/**
	 * 由两点生成线
	 * @param startPoint 起始点
	 * @param endPoint 结束点
	 * @return
	 */
	public static IPolyline points2ToPolyline(IPoint startPoint, IPoint endPoint) {
		try {
			GeometryEnvironment gEnvironment = new GeometryEnvironment();
			Path path = new Path();
			Point[] points = new Point[]{ (Point)startPoint, (Point)endPoint};
			gEnvironment.addPoints(path, points);
			IGeometryCollection polyline = new Polyline();
			polyline.addGeometry(path, null, null);
			return (IPolyline)polyline;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static IPolyline createLine(double startX, double startY, double endX, double endY) {
		IGeometryBridge2 geometryBridge2;
		try {
			geometryBridge2 = new GeometryEnvironment();
			IPointCollection4 pointCollection4 = new Polyline();
			_WKSPoint[] wksPoints = new _WKSPoint[2];//2D
			wksPoints[0] = new _WKSPoint();
			wksPoints[0].x = startX;
			wksPoints[0].y = startY;
			wksPoints[1] = new _WKSPoint();
			wksPoints[1].x = endX;
			wksPoints[1].y = endY;
			geometryBridge2.setWKSPoints(pointCollection4, wksPoints);
			return (IPolyline)pointCollection4;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static IPoint getPointInstance() {
		try {
			return new Point();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取WGS84点对象
	 * @param lon经度
	 * @param lat纬度
	 * @return
	 */
	public static IPoint getWGS84PointInstance(double lon, double lat) {
		try {
			IPoint point  = new Point();
			point.setSpatialReferenceByRef(wgs84CoordinateSystem);
			point.setX(lon);
			point.setY(lat);
			return point;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取和目标面要素相交的要素的IFeatureCursor
	 * @param path
	 * @param layer
	 * @param polygon
	 * @return
	 */
	public static IFeatureCursor intersect(IFeatureLayer featureLayer, IPolygon polygon){
		if (featureLayer == null) {
			System.out.println("intersect的要素图层为null.");
			return null;
		}
		if (polygon == null) {
			System.out.println("intersect的polygon为null.");
			return null;
		}
		//面面相交
		try {
			ISpatialFilter spatialFilter = new SpatialFilter();
			spatialFilter.setGeometryByRef(polygon);
			spatialFilter.setSpatialRel(esriSpatialRelEnum.esriSpatialRelIntersects);
			IFeatureCursor featureCursor = featureLayer.search(spatialFilter, false);
			return featureCursor;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static IFeatureCursor intersect(IFeatureClass featureClass, IPolygon polygon){
		if (featureClass == null) {
			System.out.println("intersect的要素图层为null.");
			return null;
		}
		if (polygon == null) {
			System.out.println("intersect的polygon为null.");
			return null;
		}
		//面面相交
		try {
			ISpatialFilter spatialFilter = new SpatialFilter();
			spatialFilter.setGeometryByRef(polygon);
			spatialFilter.setSpatialRel(esriSpatialRelEnum.esriSpatialRelIntersects);
			IFeatureCursor featureCursor = featureClass.search(spatialFilter, false);
			return featureCursor;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 获取在目标面要素范围内的要素的IFeatureCursor
	 * @param path
	 * @param layer
	 * @param polygon
	 * @return
	 */
//	public static IFeatureCursor contains(String path, int layer, IPolygon polygon){
//		// 面面相交
//		try {
//			ISpatialFilter spatialFilter = new SpatialFilter();
//			spatialFilter.setGeometryByRef(polygon);
//			spatialFilter.setSpatialRel(esriSpatialRelEnum.esriSpatialRelContains);
//			IFeatureLayer featureLayer = (IFeatureLayer) getLayer(path, layer);
//			IFeatureCursor featureCursor = featureLayer.search(spatialFilter, false);
//			return featureCursor;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
//	public static ILayer getLayer(String path, int layer) {
//		try {
////			if(mapDoc == null && mapDoc.isMapDocument(path))
////				mapDoc.open(path, null) ; 
////			else if(mapDoc == null)
////				return null ;
//			if (mapDoc == null) {
//				mapDoc = new MapDocument();
//			}
//			if(mapDoc.isMapDocument(path))
//				mapDoc.open(path, null) ; 
//			else{
//				System.out.println(path+" is not mapDocument or not exist, return null.");
//				return null ;
//			}
//				IMap map;
//			map = mapDoc.getMap(0);
//			ILayer layer2 = map.getLayer(layer);
//			return layer2;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//		 
//	}
	/**
	 * 生成圆环面
	 * @param center 中心点
	 * @param innerPoint 内圆上一点
	 * @param outterPoint 外圆上一点
	 * @return
	 */
	public static IPolygon createAnnulus(IPoint center, IPoint innerPoint, IPoint outterPoint) {
		IPolygon polygon;
		try {
			polygon = new Polygon();
			ICircularArc circularArc1 = new CircularArc();
			ICircularArc circularArc2 = new CircularArc();
			ISegmentCollection ring1 = new Ring();
			ISegmentCollection ring2 = new Ring();
			ring1.addSegment((ISegment)circularArc1, null, null);
			ring2.addSegment((ISegment)circularArc2, null, null);
			IGeometryCollection geometryCollection = (IGeometryCollection)polygon;
			geometryCollection.addGeometry((IGeometry)ring1, null, null);
			geometryCollection.addGeometry((IGeometry)ring2, null, null);
			circularArc1.putCoordsByAngle(center, 0, 2*Math.PI, 0);
			circularArc2.putCoordsByAngle(center, 0, 2*Math.PI, 0);
			circularArc1.putRadiusByPoint(outterPoint);
			circularArc2.putRadiusByPoint(innerPoint);
			
//			System.out.println("circularArc1.getRadius():" + circularArc1.getRadius());
//			System.out.println("circularArc2.getRadius():" + circularArc2.getRadius());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return polygon;
	}
	/**
	 * 生成圆环面
	 * @param center中心点
	 * @param innerRadius内半径
	 * @param outterRadius外半径
	 * @return
	 */
	public static IPolygon createAnnulus(IPoint center, double innerRadius, double outterRadius) {
		IPolygon polygon;
		try {
			polygon = new Polygon();
			ICircularArc circularArc1 = new CircularArc();
			ICircularArc circularArc2 = new CircularArc();
			ISegmentCollection ring1 = new Ring();
			ISegmentCollection ring2 = new Ring();
			ring1.addSegment((ISegment)circularArc1, null, null);
			ring2.addSegment((ISegment)circularArc2, null, null);
			IGeometryCollection geometryCollection = (IGeometryCollection)polygon;
			geometryCollection.addGeometry((IGeometry)ring1, null, null);
			geometryCollection.addGeometry((IGeometry)ring2, null, null);
			circularArc1.putCoordsByAngle(center, 0, 2*Math.PI, outterRadius);
			circularArc2.putCoordsByAngle(center, 0, 2*Math.PI, innerRadius);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return polygon;
	}
	/**
	 * 添加面要素
	 * @param featureName
	 * @param polygon
	 * @return
	 */
//	public static boolean addPolygonToFeatureClass(String featureName, IPolygon polygon) {
//		try {
//			IFeatureClass featureClass = featureWorkspace.openFeatureClass(featureName);
//			IFeature polygonFeature = featureClass.createFeature();
//			polygonFeature.setShapeByRef(polygon);
//			polygonFeature.store();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}
	/**
	 * 添加线要素
	 * @param featureName
	 * @param polyline
	 * @return
	 */
//	public static boolean addPolylineToFeatureClass(String featureName, IPolyline polyline) {
//		try {
//			IFeatureClass featureClass = featureWorkspace.openFeatureClass(featureName);
//			IFeature lineFeature = featureClass.createFeature();
//			lineFeature.setShapeByRef(polyline);
//			lineFeature.store();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}
	/**
	 * 添加点要素
	 * @param featureName
	 * @param point
	 * @return
	 */
//	public static boolean addPointToFeatureClass(String featureName, IPoint point) {
//		try {
//			IFeatureClass featureClass = featureWorkspace.openFeatureClass(featureName);
//			IFeature pointFe = featureClass.createFeature();
//			pointFe.setShapeByRef(point);
//			pointFe.store();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}

	public static IFeatureClass getStationsShp() {
		return stationsShpDemo;
	}

	public static ShapefileWorkspaceFactory getShpFactory() {
		return shpFactory;
	}

	public static IFeatureWorkspace getDemoShpFWorkspace() {
		return demoShpFWorkspace;
	}

	public static SpatialReferenceEnvironment spatialReferenceEnvironment;
//	public static ISpatialReference projectedCoordinateSystem;
	public static ISpatialReference wgs84CoordinateSystem;
	public static ISpatialReference webMercatorCoordinateSystem;
	static{
		
		//Step 1: Initialize the Java Componet Object Model (COM) Interop.
		EngineInitializer.initializeEngine();
		//Step 2: Initialize an ArcGIS license.
		AoInitialize aoInit;
		try {
			aoInit = new AoInitialize();
			initializeArcGISLicenses(aoInit);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Step 3: Initialize workspaceFactory, workspace, featureWorkspace.
		try {
//			workspaceFactory = new FileGDBWorkspaceFactory();
//			workspace = workspaceFactory.openFromFile(Config.gdbPath, 0);
//			featureWorkspace = (IFeatureWorkspace)workspace;
//			mapDoc = new MapDocument();
//			provLayer = getLayer(Config.baseMXDPath, Config.provLayer);
//			countiesLayer = getLayer(Config.baseMXDPath, Config.countiesLayer);
//			railLayer = getLayer(Config.baseMXDPath, Config.railLayer);
//			stationsLayer = getLayer(Config.baseMXDPath, Config.stationsLayer);
			spatialReferenceEnvironment = new SpatialReferenceEnvironment();
//			projectedCoordinateSystem = spatialReferenceEnvironment.createProjectedCoordinateSystem(esriSRProjCS4Type.esriSRProjCS_Xian1980_3_Degree_GK_CM_102E);
			wgs84CoordinateSystem = spatialReferenceEnvironment.createGeographicCoordinateSystem(esriSRGeoCSType.esriSRGeoCS_WGS1984);
			webMercatorCoordinateSystem = spatialReferenceEnvironment.createProjectedCoordinateSystem(3857);
			shpFactory = new ShapefileWorkspaceFactory();
			demoShpFWorkspace = (IFeatureWorkspace)shpFactory.openFromFile(Config.demoFolder, 0);
			stationsShpDemo = demoShpFWorkspace.openFeatureClass("Export_Stations.shp");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//License initialization 
		static void initializeArcGISLicenses(AoInitialize aoInit) {
			try {
				if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeEngine) == esriLicenseStatus.esriLicenseAvailable){
					aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeEngine);
				} else if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeBasic) == esriLicenseStatus.esriLicenseAvailable){
					aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeBasic);
				} else {
					System.err.println("Engine Runtime or Desktop Basic license not initialized.");
					System.err.println("Exiting application.");
					System.exit(-1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}//End of method initializeArcGISLicenses
}
/**
shp文件添加字段
try {
			ShapefileWorkspaceFactory shpFactory = new ShapefileWorkspaceFactory();
			IWorkspace workspace = shpFactory.openFromFile(Property.getStringProperty("filePath"), 0);
			IFeatureWorkspace featureWorkspace = (IFeatureWorkspace)workspace;
			IFeatureClass featureClass = featureWorkspace.openFeatureClass("Export_Stations.shp");
			System.out.println(featureClass.featureCount(null));
			System.out.println(featureClass.getShapeType());
			IFields fields = featureClass.getFields();
			IField vDisField = new Field();
			IFieldEdit fieldEdit = (IFieldEdit)vDisField;
			fieldEdit.setName("HMaxDis");
			fieldEdit.setType(esriFieldType.esriFieldTypeDouble);
			
			featureClass.addField(fieldEdit);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

**/