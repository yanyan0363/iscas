package test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.esri.arcgis.datasourcesfile.ShapefileWorkspaceFactory;
import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureWorkspace;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.ISpatialReference;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.geometry.SpatialReferenceEnvironment;
import com.esri.arcgis.geometry.esriSRGeoCSType;
import com.esri.arcgis.system.AoInitialize;
import com.esri.arcgis.system.EngineInitializer;
import com.esri.arcgis.system.esriLicenseProductCode;
import com.esri.arcgis.system.esriLicenseStatus;

import arcgis.ArcgisHelper;
import arcgis.GisHelper;
import beans.BLH;
import beans.XYZ;

public class GPSStationLocCal {

	public static void main(String[] args) {
		//原有台站坐标验证
		System.out.println("\n原有台站坐标验证");
//		System.out.println("\nSMTWX:" + GisHelper.XYZtoBLH(new XYZ(-1189855.2484, 5467056.4675, 3056402.6414)));
//		System.out.println("\nSMXJZ:" + GisHelper.XYZtoBLH(new XYZ(-1256602.3924, 5557093.5621, 2861814.6407)));
//		System.out.println("\nSMMYX:" + GisHelper.XYZtoBLH(new XYZ(-1194209.7485, 5566494.7707, 2868251.6426)));
//		System.out.println("\nSMPGX:" + GisHelper.XYZtoBLH(new XYZ(-1230840.7377, 5533456.14, 2916662.7109)));
//		System.out.println("\nSMBTX:" + GisHelper.XYZtoBLH(new XYZ(-1253338.2475, 5512171.4972, 2949145.3383)));
//		System.out.println("\nSMHGZ:" + GisHelper.XYZtoBLH(new XYZ(-1180361.8121, 5587766.0456, 2832936.1387)));
//		System.out.println("\nSMZSZ:" + GisHelper.XYZtoBLH(new XYZ(-1213236.217, 5473341.2661, 3034904.7516)));
//		System.out.println("\nSMMNX:" + GisHelper.XYZtoBLH(new XYZ(-1181143.1369, 5482752.094, 3030756.2033)));
//		System.out.println("\nSMFYX:" + GisHelper.XYZtoBLH(new XYZ()));
//		System.out.println("\nSMCLX:" + GisHelper.XYZtoBLH(new XYZ()));
		BLH blh = GisHelper.XYZtoBLH(new XYZ(-1189855.2484, 5467056.4675, 3056402.6414));
		boolean res = addStation("SMTWX", blh.B, blh.L, "STA01", "冕宁拖乌乡", "拖乌", "28°48′31.02028″", "102°16′42.35145″");
		System.out.println("\nSMTWX:"+ res + ", " + blh);
		

//		blh = GisHelper.XYZtoBLH(new XYZ(-1256602.3924, 5557093.5621, 2861814.6407));
		res = addStation("SMFYX", 26.49791, 102.121371, "STA02", "会理凤营乡", "凤营", "26°29′52.47421″", "102°7′16.93444″");
		System.out.println("\nSMFYX:"+ res + ", " + blh);
		
		
		blh = GisHelper.XYZtoBLH(new XYZ(-1256602.3924, 5557093.5621, 2861814.6407));
		res = addStation("SMXJZ", blh.B, blh.L, "STA03", "会东新街镇", "新街", "26°49′30.42175″", "102°44′30.33277″");
		System.out.println("\nSMXJZ:"+ res + ", " + blh);
		
		
//		blh = GisHelper.XYZtoBLH(new XYZ(-1256602.3924, 5557093.5621, 2861814.6407));
		res = addStation("SMCLX", 29.135447, 102.340863, "STA04", "石棉擦罗乡", "擦罗", "29°8′7.60961″", "102°20′27.10515″");
		System.out.println("\nSMCLX:"+ res + ", " + blh);
		
		
		blh = GisHelper.XYZtoBLH(new XYZ(-1194209.7485, 5566494.7707, 2868251.6426));
		res = addStation("SMMYX", blh.B, blh.L, "STA05", "米易县", "米易", "26°53′39.23220″", "102°6′30.33294″");
		System.out.println("\nSMMYX:"+ res + ", " + blh);
		
		blh = GisHelper.XYZtoBLH(new XYZ(-1230840.7377, 5533456.14, 2916662.7109));
		res = addStation("SMPGX", blh.B, blh.L, "STA06", "普格县", "普格", "27°23′1.03150″", "102°32′25.75693″");
		System.out.println("\nSMPGX:"+ res + ", " + blh);
		
		blh = GisHelper.XYZtoBLH(new XYZ(-1253338.2475, 5512171.4972, 2949145.3383));
		res = addStation("SMBTX", blh.B, blh.L, "STA07", "布拖县", "布拖", "27°42′34.15076″", "102°48′35.72119″");
		System.out.println("\nSMBTX:"+ res + ", " + blh);
		
		blh = GisHelper.XYZtoBLH(new XYZ(-1180361.8121, 5587766.0456, 2832936.1387));
		res = addStation("SMHGZ", blh.B, blh.L, "STA08", "攀枝花红格镇", "红格", "26°32′12.72960″", "101°55′40.17929″");
		System.out.println("\nSMHGZ:"+ res + ", " + blh);
		
		blh = GisHelper.XYZtoBLH(new XYZ(-1213236.217, 5473341.2661, 3034904.7516));
		res = addStation("SMZSZ", blh.B, blh.L, "STA09", "越西中所镇", "中所", "28°35′24.75470″", "102°29′53.74010″");
		System.out.println("\nSMZSZ:"+ res + ", " + blh);
		
		blh = GisHelper.XYZtoBLH(new XYZ(-1181143.1369, 5482752.094, 3030756.2033));
		res = addStation("SMMNX", blh.B, blh.L, "STA10", "冕宁县", "冕宁", "28°32′50.12476″", "102°9′26.51968″");
		System.out.println("\nSMMNX:"+ res + ", " + blh);
		
//		System.out.println("\nSMFYX:" + GisHelper.XYZtoBLH(new XYZ()));
//		System.out.println("\nSMCLX:" + GisHelper.XYZtoBLH(new XYZ()));
		
		//新添GPS台站
		System.out.println("新添GPS台站");
		
		blh = GisHelper.XYZtoBLH(new XYZ(-1234664.6588, 5447783.9533, 3070425.4449));
		res = addStation("HBDGL", blh.B, blh.L, "", "甘洛", "", "", "");
		System.out.println("\nHBDGL:"+ res + ", " + blh);
		
		blh = GisHelper.XYZtoBLH(new XYZ(-1185090.7727, 5493376.7606, 3009772.838));
		res = addStation("HBDLG", blh.B, blh.L, "", "冕宁泸沽镇", "", "", "");
		System.out.println("\nHBDLG:"+ res + ", " + blh);
		
		blh = GisHelper.XYZtoBLH(new XYZ(-1199051.8463, 5515876.7884, 2962758.985));
		res = addStation("HBDXC", blh.B, blh.L, "", "西昌", "", "", "");
		System.out.println("\nHBDXC:"+ res + ", " + blh);
		
		blh = GisHelper.XYZtoBLH(new XYZ(-1253162.8105, 5495691.9832, 2978924.3525));
		res = addStation("HBDZJ", blh.B, blh.L, "", "昭觉", "", "", "");
		System.out.println("\nHBDZJ:"+ res + ", " + blh);
		
		blh = GisHelper.XYZtoBLH(new XYZ(-1217030.1968, 5430223.4391, 3108250.2921));
		res = addStation("ZBDHY", blh.B, blh.L, "", "汉源", "", "", "");
		System.out.println("\nZBDHY:"+ res + ", " + blh);
		
		blh = GisHelper.XYZtoBLH(new XYZ(-1215550.5914, 5466670.1621, 3045829.8534));
		res = addStation("ZBDYX", blh.B, blh.L, "", "越西", "", "", "");
		System.out.println("\nZBDYX:"+ res + ", " + blh);
		
		blh = GisHelper.XYZtoBLH(new XYZ(-1210702.5965, 5489704.442, 3006718.9536));
		res = addStation("ZBDXD", blh.B, blh.L, "", "喜德", "", "", "");
		System.out.println("\nZBDXD:"+ res + ", " + blh);
		
		blh = GisHelper.XYZtoBLH(new XYZ(-1276999.8772, 5472880.3503, 3010126.1189));
		res = addStation("ZBDMG", blh.B, blh.L, "", "美姑", "", "", "");
		System.out.println("\nZBDMG:"+ res + ", " + blh);
		
//		System.out.println("\nHBDGL:" + GisHelper.XYZtoBLH(new XYZ(-1234664.6588, 5447783.9533, 3070425.4449)));
//		System.out.println("\nHBDLG:" + GisHelper.XYZtoBLH(new XYZ(-1185090.7727, 5493376.7606, 3009772.838)));
//		System.out.println("\nHBDXC:" + GisHelper.XYZtoBLH(new XYZ(-1199051.8463, 5515876.7884, 2962758.985)));
//		System.out.println("\nHBDZJ:" + GisHelper.XYZtoBLH(new XYZ(-1253162.8105, 5495691.9832, 2978924.3525)));
//		System.out.println("\nZBDHY:" + GisHelper.XYZtoBLH(new XYZ(-1217030.1968, 5430223.4391, 3108250.2921)));
//		System.out.println("\nZBDYX:" + GisHelper.XYZtoBLH(new XYZ(-1215550.5914, 5466670.1621, 3045829.8534)));
//		System.out.println("\nZBDXD:" + GisHelper.XYZtoBLH(new XYZ(-1210702.5965, 5489704.442, 3006718.9536)));
//		System.out.println("\nZBDMG:" + GisHelper.XYZtoBLH(new XYZ(-1276999.8772, 5472880.3503, 3010126.1189)));
		
//		System.out.println("\nZBDLW:" + GisHelper.XYZtoBLH(new XYZ()));
//		System.out.println("\nHBDMB:" + GisHelper.XYZtoBLH(new XYZ()));
		
	}
	private static ShapefileWorkspaceFactory shpFactory = null;
	private static IFeatureWorkspace shpFWorkspace = null;
	public static SpatialReferenceEnvironment spatialReferenceEnvironment;
	public static ISpatialReference wgs84CoordinateSystem;
	private static String shpFolder = "E:\\2019\\GNSS\\shp";
	private static String stationShpFile = "Stations.shp";
	private static IFeatureClass stationShp = null;
	/**
	 * 
	 * @param stationID 
	 * @param B 十进制纬度
	 * @param L 十进制经度
	 * @param machineID 仪器编号
	 * @param stLocName 站点名称
	 * @param name 
	 * @param latitude 纬度（度分秒）
	 * @param longitude 经度（度分秒）
	 * @return
	 */
	public static boolean addStation(String stationID, double B, double L, String machineID, String stLocName, 
			String name, String latitude, String longitude) {
		if (stationShp == null) {
			System.out.println("stationShp == null");
			return false;
		}
		IPoint stationPoint = null;
		IFeature pointFeature = null;
		try {
			stationPoint = new Point();
			stationPoint.setSpatialReferenceByRef(wgs84CoordinateSystem);
			stationPoint.setX(L);
			stationPoint.setY(B);
			pointFeature = stationShp.createFeature();
			pointFeature.setShapeByRef(stationPoint);
			pointFeature.setValue(2, machineID);
			pointFeature.setValue(3, stationID);
			pointFeature.setValue(4, stLocName);
			pointFeature.setValue(5, name);
			pointFeature.setValue(6, latitude);
			pointFeature.setValue(7, longitude);
			pointFeature.setValue(8, L);
			pointFeature.setValue(9, B);
			pointFeature.store();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
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
			spatialReferenceEnvironment = new SpatialReferenceEnvironment();
			wgs84CoordinateSystem = spatialReferenceEnvironment.createGeographicCoordinateSystem(esriSRGeoCSType.esriSRGeoCS_WGS1984);
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
