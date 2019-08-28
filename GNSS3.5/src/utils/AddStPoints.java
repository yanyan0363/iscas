package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.esri.arcgis.carto.MapDocument;
import com.esri.arcgis.datasourcesGDB.FileGDBWorkspaceFactory;
import com.esri.arcgis.datasourcesfile.ShapefileWorkspaceFactory;
import com.esri.arcgis.geodatabase.Field;
import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureWorkspace;
import com.esri.arcgis.geodatabase.IField;
import com.esri.arcgis.geodatabase.IFieldEdit;
import com.esri.arcgis.geodatabase.IFields;
import com.esri.arcgis.geodatabase.esriFieldType;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.SpatialReferenceEnvironment;
import com.esri.arcgis.geometry.esriSRGeoCSType;
import com.esri.arcgis.geometry.esriSRProjCS4Type;
import com.esri.arcgis.system.AoInitialize;
import com.esri.arcgis.system.EngineInitializer;
import com.esri.arcgis.system.esriLicenseProductCode;
import com.esri.arcgis.system.esriLicenseStatus;

import arcgis.ArcgisHelper;
import metaData.StaticMetaData.EQPointType;

public class AddStPoints {
	
	public static void main(String[] args) {
		initAO();
		String shpFolder = "E:/2018/GNSS/arcgis/JPTest/JPShp";
		String shpName = "Stations.shp";
		String stsFile = "E:/2018/GNSS/arcgis/JPTest/坐标.txt";
		try {
			ShapefileWorkspaceFactory shpFactory = new ShapefileWorkspaceFactory();
			IFeatureWorkspace featureWorkspace = (IFeatureWorkspace)shpFactory.openFromFile(shpFolder, 0);
			IFeatureClass featureClass = featureWorkspace.openFeatureClass(shpName);
			IFields fields = featureClass.getFields();
			if (fields.findField("StationID")<0) {
				IField field = new Field();
				IFieldEdit fieldEdit = (IFieldEdit)field;
				fieldEdit.setName("StationID");
				fieldEdit.setType(esriFieldType.esriFieldTypeString);
				featureClass.addField(fieldEdit);
			}
			if (fields.findField("latitude")<0) {
				IField field = new Field();
				IFieldEdit fieldEdit = (IFieldEdit)field;
				fieldEdit.setName("latitude");
				fieldEdit.setType(esriFieldType.esriFieldTypeDouble);
				featureClass.addField(fieldEdit);
			}
			if (fields.findField("longitude")<0) {
				IField field = new Field();
				IFieldEdit fieldEdit = (IFieldEdit)field;
				fieldEdit.setName("longitude");
				fieldEdit.setType(esriFieldType.esriFieldTypeDouble);
				featureClass.addField(fieldEdit);
			}
			if (fields.findField("X")<0) {
				IField field = new Field();
				IFieldEdit fieldEdit = (IFieldEdit)field;
				fieldEdit.setName("X");
				fieldEdit.setType(esriFieldType.esriFieldTypeDouble);
				featureClass.addField(fieldEdit);
			}
			if (fields.findField("Y")<0) {
				IField field = new Field();
				IFieldEdit fieldEdit = (IFieldEdit)field;
				fieldEdit.setName("Y");
				fieldEdit.setType(esriFieldType.esriFieldTypeDouble);
				featureClass.addField(fieldEdit);
			}
			File stFile = new File(stsFile);
			FileReader reader = new FileReader(stFile);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line;
			int stIdx = featureClass.findField("StationID");
			int lonIdx = featureClass.findField("longitude");
			int latIdx = featureClass.findField("latitude");
			int XIdx = featureClass.findField("X");
			int YIdx = featureClass.findField("Y");
			while ((line = bufferedReader.readLine())!=null) {
				String[] ar = line.split("	");
				String stName = ar[0];
				double lon = Double.parseDouble(ar[1]);
				double lat = Double.parseDouble(ar[2]);
				IFeature feature = featureClass.createFeature();
				IPoint point = ArcgisHelper.getWGS84PointInstance(lon, lat);
				feature.setShapeByRef(point);
				feature.setValue(stIdx, stName);
				feature.setValue(lonIdx, lon);
				feature.setValue(latIdx, lat);
				feature.setValue(XIdx, lon);
				feature.setValue(YIdx, lat);
				feature.store();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void initAO() {
		EngineInitializer.initializeEngine();
		//Step 2: Initialize an ArcGIS license.
		AoInitialize aoInit;
		try {
			aoInit = new AoInitialize();
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
