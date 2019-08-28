package arcgis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esri.arcgis.carto.IFeatureLayer;
import com.esri.arcgis.datasourcesfile.ShapefileWorkspaceFactory;
import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureWorkspace;
import com.esri.arcgis.geodatabase.IField;
import com.esri.arcgis.geodatabase.IFields;
import com.esri.arcgis.geodatabase.IWorkspace;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.IPolygon;
import com.esri.arcgis.geometry.IPolyline;
import baseObject.BaseStation;
import beans.BLH;
import beans.EQInfo;
import beans.Loc;
import beans.Report;
import dataCache.Displacement;
import helper.DetailHelper;
import mathUtil.DoubleUtil;
import metaData.StaticMetaData;
import metaData.StaticMetaData.EQPointType;
import utils.MapUtil;
import utils.Config;

public class ArcgisShpHelper {

	public static IFeatureClass getShpFeatureClass(String shpFolder, String shpName) {
		try {
			IFeatureWorkspace workspace = (IFeatureWorkspace)ArcgisHelper.getShpFactory().openFromFile(shpFolder, 0);
			IFeatureClass featureClass = workspace.openFeatureClass(shpName);
			return featureClass;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static boolean updateStationInDemoShp(String stationID, String stLoc, double B, double L) {
		IFeatureClass featureClass = ArcgisShpHelper.getShpFeatureClass(Config.demoFolder, "Export_Stations.shp");
		int count;
		try {
			count = featureClass.featureCount(null);
			int stIDIdx = featureClass.findField("StationID");
			int nameIdx = featureClass.findField("name");
			int xIdx = featureClass.findField("X");
			int yIdx = featureClass.findField("Y");
			for (int i = 0; i < count; i++) {
				IFeature feature = featureClass.getFeature(i);
				String stID = (String)feature.getValue(stIDIdx);
				if (stationID.equals(stID)) {
					feature.setValue(nameIdx, stLoc);
					feature.setValue(xIdx, L);
					feature.setValue(yIdx, B);
					IPoint point = ArcgisHelper.getWGS84PointInstance(L, B);
					feature.setShapeByRef(point);
					feature.store();
					break;
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	public static boolean deleteStationInDemoShp(String stationID) {
		IFeatureClass featureClass = ArcgisShpHelper.getShpFeatureClass(Config.demoFolder, "Export_Stations.shp");
		int count;
		try {
			count = featureClass.featureCount(null);
			int stIDIdx = featureClass.findField("StationID");
			for (int i = 0; i < count; i++) {
				IFeature feature = featureClass.getFeature(i);
				String stID = (String)feature.getValue(stIDIdx);
				if (stationID.equals(stID)) {
					feature.delete();
					break;
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * demo中添加台站
	 * @param stationID
	 * @param B
	 * @param L
	 * @return
	 */
	public static boolean addStationInDemoShp(String stationID, double B, double L, String stLoc) {
		IFeatureClass featureClass = ArcgisShpHelper.getShpFeatureClass(Config.demoFolder, "Export_Stations.shp");
		try {
			IFeature feature = featureClass.createFeature();
			IPoint point = ArcgisHelper.getWGS84PointInstance(L, B);
			feature.setShapeByRef(point);
			int stationIDIdx = featureClass.findField("StationID");
			int xIdx = featureClass.findField("X");
			int yIdx = featureClass.findField("Y");
			int nameIdx = featureClass.findField("name");
			feature.setValue(stationIDIdx, stationID);
			feature.setValue(xIdx, L);
			feature.setValue(yIdx, B);
			feature.setValue(nameIdx, stLoc);
			feature.store();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	public static boolean createArrows(String eqID, String timeNode, 
			Hashtable<String, Displacement> stationMaxHDis, Hashtable<String, Displacement> stationMaxVDis) {
		File source = new File(Config.demoFolder+"EQLines.shp");
		String outDir = Config.filePath+eqID+"/";
		
		String outShpName = "EQArrows_V_"+timeNode+".shp";
		boolean vCRes = createVArrow(source, outDir, outShpName, stationMaxVDis);
		
		outShpName = "EQArrows_H_"+timeNode+".shp";
		boolean hCRes = createHArrow(source, outDir, outShpName, stationMaxHDis);
		
		return vCRes&&hCRes;
	}
	/**
	 * 
	 * @param source 源文件，模板shp
	 * @param outDir 输出文件的目录
	 * @param shpFileName 输出文件的名称
	 * @param stationMaxHDis 最大水平位移量
	 * @return
	 */
	private static boolean createHArrow(File source, String outDir, String shpFileName, Hashtable<String, Displacement> stationMaxHDis){
		File dest = new File(outDir+shpFileName);
		if (!shpCopy(source, dest)) {
			return false;
		}
		ShapefileWorkspaceFactory factory = ArcgisHelper.getShpFactory();
		IFeatureWorkspace featureWorkspace = null;
		IFeatureClass featureClass = null;
		try {
			featureWorkspace = (IFeatureWorkspace)factory.openFromFile(outDir, 0);
			featureClass = featureWorkspace.openFeatureClass(shpFileName);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		Map<String, Loc> stationOriginalLocs = StaticMetaData.getStationOriginalLocs();
		if (stationMaxHDis != null && stationMaxHDis.size() > 0) {
			try {
				for (Iterator iterator = stationMaxHDis.keySet().iterator(); iterator.hasNext();) {
					String key = (String) iterator.next();
					if (!stationOriginalLocs.containsKey(key)) {
						continue;
					}
					Displacement curDis = stationMaxHDis.get(key);
					IPoint startP = stationOriginalLocs.get(key).getWGS84Point();
					//控制Arrow最大长度
					double dx = DoubleUtil.mul(curDis.xDisplacement, Config.multiLine);
					if (dx > Config.maxArrow) {
						dx = Config.maxArrow;
					}
					double dy = DoubleUtil.mul(curDis.yDisplacement, Config.multiLine);
					if (dy > Config.maxArrow) {
						dy = Config.maxArrow;
					}
					IPoint endP = ArcgisHelper.getWGS84PointInstance(dx+startP.getX(), dy+startP.getY());
					//两点生成线
					IPolyline polyline = ArcgisHelper.points2ToPolyline(startP, endP);
					//写入shp
				    IFeature feature = featureClass.createFeature();
				    feature.setShapeByRef((IPolyline)polyline);
				    feature.setValue(3, key);
				    feature.store();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	/**
	 * 
	 * @param source 源文件，模板shp
	 * @param outDir 输出文件的目录
	 * @param shpFileName 输出文件的名称
	 * @param stationMaxVDis 最大垂直位移量
	 * @return
	 */
	private static boolean createVArrow(File source, String outDir, String shpFileName, Hashtable<String, Displacement> stationMaxVDis){
		File dest = new File(outDir+shpFileName);
		if (!shpCopy(source, dest)) {
			return false;
		}
		ShapefileWorkspaceFactory factory = ArcgisHelper.getShpFactory();
		IFeatureWorkspace featureWorkspace = null;
		IFeatureClass featureClass = null;
		try {
			featureWorkspace = (IFeatureWorkspace)factory.openFromFile(outDir, 0);
			featureClass = featureWorkspace.openFeatureClass(shpFileName);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		Map<String, Loc> stationOriginalLocs = StaticMetaData.getStationOriginalLocs();
		if (stationMaxVDis != null && stationMaxVDis.size() > 0) {
			try {
				for (Iterator iterator = stationMaxVDis.keySet().iterator(); iterator.hasNext();) {
					String key = (String) iterator.next();
					if (!stationOriginalLocs.containsKey(key)) {
						continue;
					}
					Displacement curDis = stationMaxVDis.get(key);
					IPoint startP = stationOriginalLocs.get(key).getWGS84Point();
					//控制Arrow最大长度
					double dy = DoubleUtil.mul(curDis.zDisplacement, Config.multiLine);
					if (dy > Config.maxArrow) {
						dy = Config.maxArrow;
					}
					IPoint endP = ArcgisHelper.getWGS84PointInstance(startP.getX(), dy+startP.getY());
					//两点生成线
					IPolyline polyline = ArcgisHelper.points2ToPolyline(startP, endP);
					//写入shp
					IFeature feature = featureClass.createFeature();
					feature.setShapeByRef((IPolyline)polyline);
					feature.setValue(3, key);
					feature.store();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	public static boolean addEQPolygon(EQInfo eq, int num, String note, Hashtable<BaseStation, Report> stationMap) {
		//计算各个station到震中的2D距离
		Enumeration<BaseStation> enumeration = stationMap.keys();
//		GKPoint center = eq.getEpicenter().gkPoint;
		BLH blh = eq.getEpicenter().getGpsData().blh;
		IPoint center = ArcgisHelper.getWGS84PointInstance(blh.L, blh.B);
		Hashtable<BaseStation, Double> disStationMap = new Hashtable<>();
		while (enumeration.hasMoreElements()) {
			BaseStation baseStation = (BaseStation) enumeration.nextElement();
//			double dis = eq.getEpicenter().getGkPoint().getDistance2D(baseStation.myLocation.getGkPoint());
//			double dis = 1000*GisHelper.calDisByBL(blh.L, blh.B, baseStation.myLocation.getGpsData().blh.L, baseStation.myLocation.getGpsData().blh.B);
			double dis = 1000*GisHelper.calDisByBLProjTo3857(blh.L, blh.B, baseStation.myLocation.getGpsData().blh.L, baseStation.myLocation.getGpsData().blh.B);
			disStationMap.put(baseStation, dis);
		}
		//根据各个station到震中的2D距离排序
		Map<BaseStation, Double> linkedHashMap = MapUtil.sortByValue(disStationMap);
		Iterator<Entry<BaseStation, Double>> iterator = linkedHashMap.entrySet().iterator();
		IPoint lastPoint = null;
		IPoint curPoint = null;
		IPolygon polygon;
		IFeature polygonFeature;
		IFeatureClass featureClass;
		Report report;
//		//here get featureClass from shp
		String outDir = Config.filePath+eq.getEQID()+"/";
		String fileName = "EQPolygons.shp";
		try {
			ShapefileWorkspaceFactory factory = ArcgisHelper.getShpFactory();
			IWorkspace workspace = factory.openFromFile(outDir, 0);
			IFeatureWorkspace featureWorkspace = (IFeatureWorkspace)workspace;
			featureClass = featureWorkspace.openFeatureClass(fileName);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		while (iterator.hasNext()) {
			Entry<BaseStation, Double> entry = iterator.next();
			BaseStation baseStation = (BaseStation)entry.getKey();
			report = stationMap.get(baseStation);
//			curPoint = baseStation.myLocation.getGkPoint().getPointCopy().getPoint();
			curPoint = baseStation.myLocation.getWGS84Point();
			if (lastPoint == null) {
				polygon = ArcgisEQHelper.drawCirclePolygon(center,curPoint);
				report.setFirst(true);
			}else {
				polygon = ArcgisEQHelper.drawEQPolygon(center, lastPoint, curPoint);
			}
			lastPoint = curPoint;
			 try {
				polygonFeature = featureClass.createFeature();
				polygonFeature.setShapeByRef(polygon);
				polygonFeature.setValue(3, report.getMagnitude());//magnitude
				polygonFeature.setValue(4, num);//note
				polygonFeature.setValue(5, note);
				polygonFeature.setValue(6, baseStation.ID);//stationID,用于记录当前面的所属台站ID
				polygonFeature.setValue(7, format.format(report.getInTime()));//inTime,用于记录当前面的report生成时间
				
				polygonFeature.store();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} 
			//计算detail
			report.setDetail(DetailHelper.detail(polygon, report.getMagnitude()));
		}
		return true;
	}
	
	public static boolean addEQPoint(EQInfo eqInfo, List<BaseStation> triggerSt, Map<String, Double> stMEMSMagMap, String note) {
		String outDir = Config.filePath+eqInfo.getEQID()+"/";
		String fileName = "EQPoints.shp";
		try {
			ShapefileWorkspaceFactory factory = ArcgisHelper.getShpFactory();
			IWorkspace workspace = factory.openFromFile(outDir, 0);
			IFeatureWorkspace featureWorkspace = (IFeatureWorkspace)workspace;
			IFeatureClass featureClass = featureWorkspace.openFeatureClass(fileName);
			IFields fields = featureClass.getFields();
			int memsMagIdx = fields.findField("memsMag");
			int gpsMagIdx = fields.findField("gpsMag");
			int eqTimeIdx = fields.findField("eqTime");
			int numIdx = fields.findField("num");
			int typeIdx = fields.findField("type");
			int noteIdx = fields.findField("note");
			//添加震中点
			IFeature feature = featureClass.createFeature();
			IPoint epic = eqInfo.getEpicenter().getWGS84Point();
			feature.setShapeByRef(epic);
			feature.setValue(memsMagIdx, eqInfo.getMemsMag());
			feature.setValue(gpsMagIdx, eqInfo.getGpsMag());
			feature.setValue(eqTimeIdx, eqInfo.getEqTime());
			feature.setValue(numIdx, 1);
			feature.setValue(typeIdx, EQPointType.epicenter.name());
			feature.setValue(noteIdx, note);
			feature.store();
			feature = null;
			//添加台站点
			synchronized (triggerSt) {
				for (BaseStation station:triggerSt) {//ConcurrentModificationException异常
					BLH blh = station.myLocation.getGpsData().blh;
					feature = featureClass.createFeature();
					feature.setShapeByRef(ArcgisHelper.getWGS84PointInstance(blh.L, blh.B));
					feature.setValue(memsMagIdx, eqInfo.getMemsMag());
					feature.setValue(gpsMagIdx, eqInfo.getGpsMag());
					feature.setValue(eqTimeIdx, eqInfo.getEqTime());
					feature.setValue(numIdx, 1);
					feature.setValue(typeIdx, EQPointType.station.name());
					feature.setValue(noteIdx, note);
					feature.store();
					feature = null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
//	public static boolean addEQStationPoint(EQInfo eq, int num, String note, IPoint stationPoint) {
//		String outDir = Config.filePath+eq.getEQID()+"/";
//		String fileName = "EQPoints.shp";
//		try {
//			ShapefileWorkspaceFactory factory = ArcgisHelper.getShpFactory();
//			IWorkspace workspace = factory.openFromFile(outDir, 0);
//			IFeatureWorkspace featureWorkspace = (IFeatureWorkspace)workspace;
//			IFeatureClass featureClass = featureWorkspace.openFeatureClass(fileName);
//			IFeature feature = featureClass.createFeature();
//			feature.setShapeByRef(stationPoint);
//			feature.setValue(3, eq.getMagnitude());
//			feature.setValue(4, eq.getEqTime());
//			feature.setValue(5, num);
//			feature.setValue(6, EQPointType.station.name());
//			feature.setValue(7, note);
//			feature.store();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//		return false;
//	}
//	public static boolean addEQPoint(EQInfo eq, int num, String note, EQPointType type) {
//		String outDir = Config.filePath+eq.getEQID()+"/";
//		String fileName = "EQPoints.shp";
//		IPoint epic = eq.getEpicenter().getWGS84Point();
//		try {
//			ShapefileWorkspaceFactory factory = ArcgisHelper.getShpFactory();
//			IWorkspace workspace = factory.openFromFile(outDir, 0);
//			IFeatureWorkspace featureWorkspace = (IFeatureWorkspace)workspace;
//			IFeatureClass featureClass = featureWorkspace.openFeatureClass(fileName);
//			IFeature feature = featureClass.createFeature();
//			feature.setShapeByRef(epic);
//			feature.setValue(3, (int)eq.getMagnitude());
//			feature.setValue(4, eq.getEqTime());
//			feature.setValue(5, num);
//			feature.setValue(6, type.toString());
//			feature.setValue(7, note);
//			feature.store();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}
	public static boolean shpCopy(File source,File dest) {
		if (!dest.getName().endsWith(".shp")) {
			System.out.println("dest file is invalid.");
			return false;
		}
		if (source.exists() && source.isFile() && source.getName().endsWith(".shp")) {
			String sourcePath = source.getPath();
			File dbfFile = new File(sourcePath.replace(".shp", ".dbf"));
			File prjFile = new File(sourcePath.replace(".shp", ".prj"));
			File shxFile = new File(sourcePath.replace(".shp", ".shx"));
			if (!dbfFile.exists() || !prjFile.exists() || !shxFile.exists()) {
				System.out.println("source file is invalid.");
				return false;
			}
			String destPath = dest.getPath();
			File dbfFileDest = new File(destPath.replace(".shp", ".dbf"));
			File prjFileDest = new File(destPath.replace(".shp", ".prj"));
			File shxFileDest = new File(destPath.replace(".shp", ".shx"));
			if (dest.exists()) {
				dest.delete();
				if (dbfFileDest.exists()) {
					dbfFileDest.delete();
				}
				if (prjFileDest.exists()) {
					prjFileDest.delete();
				}
				if (shxFileDest.exists()) {
					shxFileDest.delete();
				}
			}
			File destDir = new File(dest.getParent());
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
			try {
				Files.copy(source.toPath(), dest.toPath());
				Files.copy(dbfFile.toPath(), dbfFileDest.toPath());
				Files.copy(prjFile.toPath(), prjFileDest.toPath());
				Files.copy(shxFile.toPath(), shxFileDest.toPath());
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}else {
			System.out.println("source file is invalid.");
			return false;
		}
		return true;
	}

}
