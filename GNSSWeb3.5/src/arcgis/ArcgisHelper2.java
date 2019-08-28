package arcgis;

import java.io.IOException;

import com.esri.arcgis.datasourcesGDB.FileGDBWorkspaceFactory;
import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureDataset;
import com.esri.arcgis.geodatabase.IFeatureWorkspace;
import com.esri.arcgis.geodatabase.IWorkspace;
import com.esri.arcgis.geodatabase.IWorkspaceFactory;
import com.esri.arcgis.geometry.BezierCurve;
import com.esri.arcgis.geometry.CircularArc;
import com.esri.arcgis.geometry.IBezierCurveGEN;
import com.esri.arcgis.geometry.ICircularArc;
import com.esri.arcgis.geometry.IGeometry;
import com.esri.arcgis.geometry.IGeometryCollection;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.IPolygon;
import com.esri.arcgis.geometry.IPolyline;
import com.esri.arcgis.geometry.ISegment;
import com.esri.arcgis.geometry.ISegmentCollection;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.geometry.Polygon;
import com.esri.arcgis.geometry.Ring;
import com.esri.arcgis.system.AoInitialize;
import com.esri.arcgis.system.EngineInitializer;
import com.esri.arcgis.system.esriLicenseProductCode;
import com.esri.arcgis.system.esriLicenseStatus;

public class ArcgisHelper2 {

	
	private static IWorkspaceFactory workspaceFactory;
	private static IWorkspace workspace;
	private static IFeatureWorkspace featureWorkspace;

	public static void main(String[] args) {
//		//Step 1: Initialize the Java Componet Object Model (COM) Interop.
//				EngineInitializer.initializeEngine();
//				//Step 2: Initialize an ArcGIS license.
//				AoInitialize aoInit;
//				try {
//					aoInit = new AoInitialize();
//					initializeArcGISLicenses(aoInit);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				//Step 3: Initialize workspaceFactory, workspace, featureWorkspace.
//				try {
//					workspaceFactory = new FileGDBWorkspaceFactory();
//					workspace = workspaceFactory.openFromFile(Property.getStringProperty("gdbPath"), 0);
//					featureWorkspace = (IFeatureWorkspace)workspace;
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
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
			System.out.println("center:: " + center.getX() + ", " + center.getY() + ", " + center.getZ());
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
	public static boolean addPolygonToFeatureClass(String featureName, IPolygon polygon) {
		try {
			IFeatureClass featureClass = featureWorkspace.openFeatureClass(featureName);
			IFeature polygonFeature = featureClass.createFeature();
			polygonFeature.setShapeByRef(polygon);
			polygonFeature.store();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * 添加线要素
	 * @param featureName
	 * @param polyline
	 * @return
	 */
	public static boolean addPolylineToFeatureClass(String featureName, IPolyline polyline) {
		try {
			IFeatureClass featureClass = featureWorkspace.openFeatureClass(featureName);
			IFeature lineFeature = featureClass.createFeature();
			lineFeature.setShapeByRef(polyline);
			lineFeature.store();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * 添加点要素
	 * @param featureName
	 * @param point
	 * @return
	 */
	public static boolean addPointToFeatureClass(String featureName, IPoint point) {
		try {
			IFeatureClass featureClass = featureWorkspace.openFeatureClass(featureName);
			IFeature pointFe = featureClass.createFeature();
			pointFe.setShapeByRef(point);
			pointFe.store();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public static IFeatureDataset getFeatureDataset(String dataset) {
		IFeatureDataset featureDataset = null;
		try {
			featureDataset = featureWorkspace.openFeatureDataset(dataset);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return featureDataset;
	}
	
	public static IWorkspaceFactory getWorkspaceFactory() {
		return workspaceFactory;
	}

	public static IWorkspace getWorkspace() {
		return workspace;
	}

	public static IFeatureWorkspace getFeatureWorkspace() {
		return featureWorkspace;
	}

//	static{
//		//Step 1: Initialize the Java Componet Object Model (COM) Interop.
//		EngineInitializer.initializeEngine();
//		//Step 2: Initialize an ArcGIS license.
//		AoInitialize aoInit;
//		try {
//			aoInit = new AoInitialize();
//			initializeArcGISLicenses(aoInit);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		//Step 3: Initialize workspaceFactory, workspace, featureWorkspace.
//		try {
//			workspaceFactory = new FileGDBWorkspaceFactory();
//			workspace = workspaceFactory.openFromFile(Property.getStringProperty("gdbPath"), 0);
//			featureWorkspace = (IFeatureWorkspace)workspace;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
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
