package helper;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.esri.arcgis.carto.IFeatureLayer;
import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureCursor;
import com.esri.arcgis.geodatabase.IFeatureWorkspace;
import com.esri.arcgis.geometry.IPolygon;
import com.esri.arcgis.geometry.Polygon;
import com.esri.arcgis.geometry.esriGeometryDimension;
import arcgis.ArcgisEQHelper;
import arcgis.ArcgisHelper;
import arcgis.ArcgisShpHelper;
import utils.Config;

public class DetailHelper {

	public static String detail(IPolygon polygon, double mag) {
		StringBuilder detail = new StringBuilder("{\"info\":{\"provinces\":[");
		//info
		IFeatureCursor featureCursor = null;
		try {
//			IFeatureWorkspace workspace = (IFeatureWorkspace)ArcgisHelper.getShpFactory().openFromFile(Config.demoFolder, 0);
			IFeatureWorkspace workspace = ArcgisHelper.getDemoShpFWorkspace();
			IFeatureClass featureClass = workspace.openFeatureClass("counties_china.shp");
			featureCursor = ArcgisHelper.intersect(featureClass, polygon);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		polygon = (Polygon)polygon;
		double sumArea = 0.0;
		IFeature feature;
		if (featureCursor != null) {
			Map<Integer, StringBuilder> proMap = new LinkedHashMap<Integer, StringBuilder>();//key:省级编号， value:省级detail
			int pro;
			String proName;
			StringBuilder proBuilder;
//		IFeatureLayer provLayer = (IFeatureLayer) ArcgisHelper.getLayer(Property.getStringProperty("baseMXDPath"), Property.getIntProperty("provLayer"));
			try {
				while((feature = featureCursor.nextFeature()) != null){
					Polygon countyPloygon = (Polygon)feature.getShape();
					Polygon intersectPolygon = (Polygon)countyPloygon.intersect((Polygon)polygon, esriGeometryDimension.esriGeometry2Dimension);
//					sumArea += (double)feature.getValue(7)/1000000;
//					intersectPolygon.project(ArcgisHelper.projectedCoordinateSystem);//wgs84投影至GK，弃用，投影至3857
					intersectPolygon.project(ArcgisHelper.webMercatorCoordinateSystem);//wgs84投影至GK
					int area = (int)intersectPolygon.getArea()/1000000;//平方公里
					sumArea += area;
					pro = (int)((double) feature.getValue(4));
					if (!proMap.containsKey(pro)) {
						proName = ArcgisEQHelper.getProvName(pro);
						proBuilder = new StringBuilder("{\"prov\":\""+proName+"\",\"counties\":[");
						proMap.put(pro, proBuilder);
					}
//					System.out.println(feature.getValue(3)+"::"+countyPloygon.getArea()/1000000+" intersectArea::" + area);
//					proMap.get(pro).append("{\"OID\":\"" + feature.getOID() + "\",\"name\":\"" + feature.getValue(3) + "\",\"area\":" + String.format("%.6f",feature.getValue(7)) + ",\"mag\":" + mag + "},");
//					proMap.get(pro).append("{\"OID\":\"" + feature.getOID() + "\",\"name\":\"" + feature.getValue(3) + "\",\"area\":" + (int)((double)feature.getValue(7)/1000000) + ",\"mag\":" + mag + "},");
					proMap.get(pro).append("{\"OID\":\"" + feature.getOID() + "\",\"name\":\"" + feature.getValue(3) + "\",\"area\":" + area + ",\"mag\":" + mag + "},");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (StringBuilder builder : proMap.values()) {
				if (builder.lastIndexOf(",") == builder.length()-1) {
					builder.deleteCharAt(builder.lastIndexOf(","));
					builder.append("]},");
				}
				detail.append(builder);			
			}
			if (detail.lastIndexOf(",") == detail.length()-1) {
				detail.deleteCharAt(detail.lastIndexOf(","));
			}
		}
		detail.append("],\"totalArea\":" + (int)sumArea + ",\"totalPop\":0}");
		//target,待添加
		detail.append(",\"target\":{\"school\":0,\"reservior\":0}");
		//rail
		detail.append(",\"rail\":[");
		IFeatureClass featureClass = ArcgisShpHelper.getShpFeatureClass(Config.demoFolder, "rails_china.shp");
		featureCursor = ArcgisHelper.intersect(featureClass, polygon);
		if (featureCursor != null) {
			try {
				while ((feature = featureCursor.nextFeature()) != null) {
					detail.append("{\"OID\":\"" + feature.getOID() + "\",\"name\":\"" + feature.getValue(10) + "\"},");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (detail.lastIndexOf(",") == detail.length()-1) {
				detail.deleteCharAt(detail.lastIndexOf(","));
			}
		}
		detail.append("]");
		//end
		detail.append("}");
		return detail.toString();
	}

}
