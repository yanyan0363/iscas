package helper;

import java.io.IOException;
import java.net.UnknownHostException;

import com.esri.arcgis.datasourcesfile.ShapefileWorkspaceFactory;
import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureWorkspace;
import com.esri.arcgis.geodatabase.IWorkspace;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.IPolyline;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.geoprocessing.GeoProcessor;
import com.esri.arcgis.geoprocessing.tools.datamanagementtools.AddField;
import com.esri.arcgis.geoprocessing.tools.datamanagementtools.CreateFeatureclass;
import com.esri.arcgis.interop.AutomationException;

import arcgis.ArcgisHelper;
import beans.Extent;

public class ImgBLHelper {
	public static void main(String[] args) {
		String pathFolder = "E:/GNSS/shp/";
		String noteName = "ImgBLNote.shp";
		String boundaryName = "ImgBLBoundary.shp";
		createShpFile(pathFolder, noteName, "point");
		createShpFile(pathFolder, boundaryName, "polyline");
		String stationFolder = "E:/GNSS/ArcGISServer/file/demo/";
		String stationName = "Export_Stations.shp";
		Extent extent = getStationExtent(stationFolder, stationName);
		System.out.println(extent);
		addBLNoteAndBoundary(extent, pathFolder, noteName, boundaryName);
	}
	private static Extent getStationExtent(String stationFolder, String stationName) {
		ShapefileWorkspaceFactory factory = ArcgisHelper.getShpFactory();
		IWorkspace workspace = null;
		try {
			workspace = factory.openFromFile(stationFolder, 0);
			IFeatureWorkspace featureWorkspace = (IFeatureWorkspace)workspace;
			IFeatureClass featureClass = featureWorkspace.openFeatureClass(stationName);
			int count = featureClass.featureCount(null);
			double minX = 180, minY  = 90, maxX = 0, maxY = 0;
			for (int i = 0; i < count; i++) {
				IFeature feature = featureClass.getFeature(i);
				Point point = (Point)feature.getShape();
				if (point.getX() > maxX) {
					maxX = Math.ceil(point.getX());
				}else if (point.getX() < minX) {
					minX = Math.floor(point.getX());
				}
				if (point.getY() > maxY) {
					maxY = Math.ceil(point.getY());
				}else if (point.getY() < minY) {
					minY = Math.floor(point.getY());
				}
			}
			Extent extent = new Extent(minX, minY, maxX, maxY, ArcgisHelper.wgs84CoordinateSystem);
			return extent;
		} catch (AutomationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	private static void createShpFile(String pathFolder, String shpName, String type) {
		CreateFeatureclass createFeatureclass = new CreateFeatureclass(pathFolder, shpName);
		createFeatureclass.setSpatialReference(ArcgisHelper.wgs84CoordinateSystem);
		createFeatureclass.setGeometryType(type);
		GeoProcessor gProcessor = null;
		try {
			gProcessor = new GeoProcessor();
			gProcessor.setOverwriteOutput(true);
			gProcessor.execute(createFeatureclass, null);
			AddField newField = new AddField(pathFolder+shpName, "note", "text");
			newField.setFieldLength(40);
			gProcessor.execute(newField, null);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param extent
	 * @param pathFolder
	 * @return
	 */
	private static boolean addBLNoteAndBoundary(Extent extent, String pathFolder, String noteName, String boundaryName) {
		try {
			ShapefileWorkspaceFactory factory = ArcgisHelper.getShpFactory();
			IWorkspace workspace = factory.openFromFile(pathFolder, 0);
			IFeatureWorkspace featureWorkspace = (IFeatureWorkspace)workspace;
			IFeatureClass featureClass = featureWorkspace.openFeatureClass(noteName);
			IFeatureClass lineFeatureClass = featureWorkspace.openFeatureClass(boundaryName);
			int minX = (int)extent.minX;
			int maxX = (int)extent.maxX;
			int minY = (int)extent.minY;
			int maxY = (int)extent.maxY;
			System.out.println(extent.toString());
			//画边框
			//上边界
			IFeature lineFeature = lineFeatureClass.createFeature();
			IPolyline polyline = ArcgisHelper.createLine(minX, maxY, maxX, maxY);
			lineFeature.setShapeByRef(polyline);
			lineFeature.store();
			//下边界
			lineFeature = lineFeatureClass.createFeature();
			polyline = ArcgisHelper.createLine(minX, minY, maxX, minY);
			lineFeature.setShapeByRef(polyline);
			lineFeature.store();
			//左边界
			lineFeature = lineFeatureClass.createFeature();
			polyline = ArcgisHelper.createLine(minX, minY, minX, maxY);
			lineFeature.setShapeByRef(polyline);
			lineFeature.store();
			//右边界
			lineFeature = lineFeatureClass.createFeature();
			polyline = ArcgisHelper.createLine(maxX, minY, maxX, maxY);
			lineFeature.setShapeByRef(polyline);
			lineFeature.store();
			System.out.println("X:");
			int noteIdx = featureClass.findField("note");
			for (int i = minX; i <= maxX; i++) {
				System.out.println(i);
				IFeature feature = featureClass.createFeature();
				IPoint point = new Point();
				point.setSpatialReferenceByRef(ArcgisHelper.wgs84CoordinateSystem);
				point.setX(i-0.1);
				point.setY(minY - 0.3);
				feature.setShapeByRef(point);
				feature.setValue(noteIdx, i+"°");
				feature.store();
				lineFeature = lineFeatureClass.createFeature();
				polyline = ArcgisHelper.createLine(i, minY, i, minY - 0.1);
				lineFeature.setShapeByRef(polyline);
				lineFeature.store();
			}
			System.out.println("Y:");
			for (int i = minY; i <= maxY; i++) {
				System.out.println(i);
				IFeature feature = featureClass.createFeature();
				IPoint point = new Point();
				point.setSpatialReferenceByRef(ArcgisHelper.wgs84CoordinateSystem);
				point.setX(minX - 0.3);
				point.setY(i - 0.1);
				feature.setShapeByRef(point);
				feature.setValue(noteIdx, i+"°");
				feature.store();
				lineFeature = lineFeatureClass.createFeature();
				polyline = ArcgisHelper.createLine(minX, i, minX - 0.1, i);
				lineFeature.setShapeByRef(polyline);
				lineFeature.store();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 *  //1.发布边框服务(一次，手动，作为基础数据存在)-ok
		//2.截取边框图片(按需生成，再截取)
		//写入ImgBLPoint和ImgBLBoundary
		addBLNoteAndBoundary(stBBox);
		//stBBox周围各扩0.5°,用于截取边框图
		stBBox.maxX = stBBox.maxX+0.5;
		stBBox.maxY = stBBox.maxY+0.5;
		stBBox.minX = stBBox.minX-0.5;
		stBBox.minY = stBBox.minY-0.5;
		//截取边框图片
		//http://localhost:6080/arcgis/rest/services/GNSSTools/MapServer/export?format=png&transparent=false&f=image&bbox=136,34,142,42&mapScale=5000000&size=700,800&layers=show:0,1
		//next here 调试，路径存在问题
		http://localhost:6080/arcgis/rest/services/GNSSTools/MapServer/export?bbox=135.5,33.5,142.5,42.5&size=700,900&imageSR=&format=png&transparent=true&mapScale=4200000&f=image
		
		String BLImg = folder+"BLBoundary.png";
		ArcgisServiceHelper.exportImg(Config.servicePath+"GNSSTools"+"/MapServer/export?format=png&transparent=true&f=image&mapScale=2500000"
				+"&bbox="+stBBox.toBBox()+"&size="+(stBBox.maxX-stBBox.minX)*100+","+(stBBox.maxY-stBBox.minY)*100+"&layers=show:0,1", BLImg);
		//3.合成边框图片(overlapImg_TrueColor)
			overlapImg_TrueColor(BLImg, img, img);
	 */
}
