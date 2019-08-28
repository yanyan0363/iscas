package test;

import java.io.IOException;

import com.esri.arcgis.datasourcesfile.ShapefileWorkspaceFactory;
import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureWorkspace;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.system.AoInitialize;
import com.esri.arcgis.system.EngineInitializer;
import com.esri.arcgis.system.esriLicenseProductCode;
import com.esri.arcgis.system.esriLicenseStatus;

import arcgis.ArcgisHelper;
import dbHelper.DBHelper;

public class InitStationsInfo {

	public static void main(String[] args) {
		
	}
	private static void initStationsInfoInDB() {
		if (stationShp == null) {
			System.out.println("stationShp is null.");
			return ;
		}
		try {
			int lonIdx = stationShp.findField("X");
			int latIdx = stationShp.findField("Y");
			int stIDIdx = stationShp.findField("StationID");
//			int stLocNameIdx = stationShp.findField("站点名称");
			int count = stationShp.featureCount(null);
			for (int i = 0; i < count; i++) {
				IFeature feature = stationShp.getFeature(i);
				double lon = (double)feature.getValue(lonIdx);
				double lat = (double)feature.getValue(latIdx);
				String stID = "" + feature.getValue(stIDIdx);
//				String stLocName = "" + feature.getValue(stLocNameIdx);
				System.out.println(i+" - "+stID+" - "+""+" - "+lon+" - "+lat);
//				insertStToDB(stID, "", lon, lat);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static boolean insertStToDB(String stID, String stLocName, double lon, double lat) {
		IPoint point = ArcgisHelper.getWGS84PointInstance(lon, lat);
		try {
			point.project(ArcgisHelper.webMercatorCoordinateSystem);
			double x = point.getX();
			double y = point.getY();
			String sql = "insert into stationsInfo(stationID,stLocName,longitude,latitude,x_3857,y_3857,status) values('"
			+stID+"','"+stLocName+"',"+lon+","+lat+","+x+","+y+",1);";
			boolean res = DBHelper.runUpdateSql(sql);
			System.out.println(res+" - "+sql);
			return res;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	private static ShapefileWorkspaceFactory shpFactory = null;
	private static IFeatureWorkspace shpFWorkspace = null;
//	public static SpatialReferenceEnvironment spatialReferenceEnvironment;
//	public static ISpatialReference wgs84CoordinateSystem;
	private static String shpFolder = "E:\\2019\\GNSS\\JP\\JPTest\\JPShp";//E:\\2019\\GNSS\\shp
	private static String stationShpFile = "Stations.shp";
	private static IFeatureClass stationShp = null;
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
//			spatialReferenceEnvironment = new SpatialReferenceEnvironment();
//			wgs84CoordinateSystem = spatialReferenceEnvironment.createGeographicCoordinateSystem(esriSRGeoCSType.esriSRGeoCS_WGS1984);
			shpFactory = new ShapefileWorkspaceFactory();
			shpFWorkspace = (IFeatureWorkspace)shpFactory.openFromFile(shpFolder, 0);
			stationShp = shpFWorkspace.openFeatureClass(stationShpFile);
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
