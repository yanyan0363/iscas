package slipHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esri.arcgis.carto.AnnotateFeature;
import com.esri.arcgis.carto.AnnotationFeature;
import com.esri.arcgis.carto.IAnnotateFeature;
import com.esri.arcgis.carto.ITextElement;
import com.esri.arcgis.carto.TextElement;
import com.esri.arcgis.controls.EngineNAWindowEvents;
import com.esri.arcgis.datasourcesfile.ShapefileWorkspaceFactory;
import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureDataset;
import com.esri.arcgis.geodatabase.IFeatureWorkspace;
import com.esri.arcgis.geodatabase.IWorkspace;
import com.esri.arcgis.geodatabase.IWorkspaceFactory;
import com.esri.arcgis.geometry.GeometryEnvironment;
import com.esri.arcgis.geometry.IGeometryBridge2;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.IPointCollection4;
import com.esri.arcgis.geometry.IPolygon;
import com.esri.arcgis.geometry.IPolyline;
import com.esri.arcgis.geometry.ISpatialReference;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.geometry.Polygon;
import com.esri.arcgis.geometry.Polyline;
import com.esri.arcgis.geometry.esriSRProjCS4Type;
import com.esri.arcgis.system.AoInitialize;
import com.esri.arcgis.system.EngineInitializer;
import com.esri.arcgis.system._WKSPoint;
import com.esri.arcgis.system._WKSPointZ;

import arcgis.ArcgisHelper;
import arcgis.ArcgisServiceHelper;
import beans.BLH;
import beans.Extent;
import beans.slip.Slip;
import helper.ImgHelper;
import utils.Config;
import utils.StringHelper;

public class SlipHelper {

	private static boolean addSlipBLNote(Extent extent) {
		try {
//			String outDir = Property.getStringProperty("filePath")+eq.getEQID()+"/";
			String outDir = Config.filePath+"test/";
			String fileName = "EQSlipBLPoint.shp";
			
			ShapefileWorkspaceFactory factory = ArcgisHelper.getShpFactory();
			IWorkspace workspace = factory.openFromFile(outDir, 0);
			IFeatureWorkspace featureWorkspace = (IFeatureWorkspace)workspace;
			IFeatureClass featureClass = featureWorkspace.openFeatureClass(fileName);
			IFeatureClass lineFeatureClass = featureWorkspace.openFeatureClass("EQSlipBLGrid");
			int minX = (int)extent.minX;
			int maxX = (int)extent.maxX;
			int minY = (int)extent.minY;
			int maxY = (int)extent.maxY;
			System.out.println(extent.toString());
			System.out.println("X:");
			for (int i = minX; i <= maxX; i++) {
				System.out.println(i);
				IFeature feature = featureClass.createFeature();
				IPoint point = new Point();
				point.setSpatialReferenceByRef(ArcgisHelper.wgs84CoordinateSystem);
				point.setX(i);
				point.setY(minY - 0.3);
				feature.setShapeByRef(point);
				feature.setValue(featureClass.findField("text"), i+"°");
				feature.store();
				
				IFeature lineFeature = lineFeatureClass.createFeature();
				IPolyline polyline = createSlipBLLine(i, minY, i, minY - 0.1);
				lineFeature.setShapeByRef(polyline);
				lineFeature.store();
			}
			System.out.println("Y:");
			for (int i = minY; i <= maxY; i++) {
				System.out.println(i);
				IFeature feature = featureClass.createFeature();
				IPoint point = new Point();
				point.setSpatialReferenceByRef(ArcgisHelper.wgs84CoordinateSystem);
				point.setX(minX - 0.2);
				point.setY(i);
				feature.setShapeByRef(point);
				feature.setValue(featureClass.findField("text"), i+"°");
				feature.store();
				
				IFeature lineFeature = lineFeatureClass.createFeature();
				IPolyline polyline = createSlipBLLine(minX, i, minX - 0.1, i);
				lineFeature.setShapeByRef(polyline);
				lineFeature.store();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	private static IPolyline createSlipBLLine(double startX, double startY, double endX, double endY) {
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
	private static IPolygon createNoteRectangle() {
		try {
			IGeometryBridge2 geometryBridge2 = new GeometryEnvironment();
			IPointCollection4 pointCollection4 = new Polygon();
			_WKSPoint[] wksPoints = new _WKSPoint[5];//2D
			//初始化Rectangle的四个顶点
			//0
			wksPoints[0] = new _WKSPoint();
			wksPoints[0].x = 101.9;
			wksPoints[0].y = 30;
			//1
			wksPoints[1] = new _WKSPoint();
			wksPoints[1].x = 102;
			wksPoints[1].y = 30;
			//2
			wksPoints[2] = new _WKSPoint();
			wksPoints[2].x = 102;
			wksPoints[2].y = 30.1;
			//3
			wksPoints[3] = new _WKSPoint();
			wksPoints[3].x = 101.9;
			wksPoints[3].y = 30.1;
			//4
			wksPoints[4] = new _WKSPoint();
			wksPoints[4].x = 101.9;
			wksPoints[4].y = 30;
			
			geometryBridge2.setWKSPoints(pointCollection4, wksPoints);
			
			return (IPolygon)pointCollection4;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	private static boolean handleTNodeFolder(File tNodeFolder) {
		String tNode = tNodeFolder.getName();
		System.out.println("tNode:"+tNode);
		if (!StringHelper.isNumeric(tNode)) {
			return false;
		}
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith("_slip.dat")) {
					return true;
				}
				return false;
			}
		};
		File[] dats = tNodeFolder.listFiles(filter);
//		System.out.println(dats.length);
		for (File dat : dats) {
			boolean datRes = handleDatFile(Integer.parseInt(tNode), dat);
			System.out.println("  datName:" + dat.getName() + "  " + datRes);
		}
		return false;
	}
	private static boolean handleDatFile(int tNode, File dat) {
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader(dat);
			bufferedReader = new BufferedReader(fileReader);
			String line = "";
			String datName = dat.getName().substring(0, dat.getName().lastIndexOf(".dat"));
			bufferedReader.readLine();
			while((line=bufferedReader.readLine())!= null && !line.equals("")){
				Slip slip = strLineToSlip(tNode, datName, line);
				if (slip == null) {
					continue;
				}
//				boolean res = ArcgisShpHelper.addSlipPoint(slip);
//				System.out.println("addSlipPoint::"+res);
				boolean res = addSlipRectangle(slip);
				System.out.println("addSlipRectangle::"+res);
			}
			bufferedReader.close();
			fileReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	private static boolean addSlipRectangle(Slip slip) {
		if (slip == null) {
			return false;
		}
//		String outDir = Property.getStringProperty("filePath")+eq.getEQID()+"/";
		String outDir = Config.filePath+"test/";
		String fileName = "EQSlipRectangle.shp";
		try {
			IPolygon rectangle  = createRectangle(slip.getBlh());
			ShapefileWorkspaceFactory factory = ArcgisHelper.getShpFactory();
			IWorkspace workspace = factory.openFromFile(outDir, 0);
			IFeatureWorkspace featureWorkspace = (IFeatureWorkspace)workspace;
			IFeatureClass featureClass = featureWorkspace.openFeatureClass(fileName);
			IFeature feature = featureClass.createFeature();
			feature.setShapeByRef(rectangle);
			feature.setValue(featureClass.findField("strk_m"), slip.getSlp_strk_m());
			feature.setValue(featureClass.findField("ddip_m"), slip.getSlp_ddip_m());
			feature.setValue(featureClass.findField("am_m"), slip.getSlp_am_m());
			feature.setValue(featureClass.findField("tNode"), slip.gettNode());
			feature.setValue(featureClass.findField("slip"), slip.getSlip());
			feature.store();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private static Slip strLineToSlip(int tNode, String slipName, String line) {
		if (line == null || line == "" || line.equals("")) {
			return null;
		}
		line = line.trim();
		while (line.contains("  ")) {
			line = line.replace("  ", " ");
		}
		String[] strArr = line.split(" ");
//		System.out.println(line);
//		System.out.println(strArr.length);
//		for (int i = 0; i < strArr.length; i++) {
//			System.out.println(strArr[i]);
//		}
		if (!isStrValid(strArr)) {
			System.out.println("line is not valid.");
			return null;
		}
		
		BLH[] blhs = calVertex(strArr);
		if (blhs == null || blhs.length < 4) {
			System.out.println("顶点计算错误...");
			return null;
		}
		Slip slip = new Slip(tNode, blhs, Double.parseDouble(strArr[8]), Double.parseDouble(strArr[9]), Double.parseDouble(strArr[10]), slipName);
		return slip;
	}
	/**
	 * 计算矩形单元的四个顶点
	 * @param strArr
	 * @return
	 */
	private static BLH[] calVertex(String[] strArr){
		try {
			IPoint center = new Point();
			center.setSpatialReferenceByRef(ArcgisHelper.wgs84CoordinateSystem);
			center.setX(Double.parseDouble(strArr[1]));
			center.setY(Double.parseDouble(strArr[0]));
			System.out.println("中心点投影前:" + center.getSpatialReference().getName()+", "+center.getX()+"  " + center.getY());
			boolean projRes = project_GK(center);//中心点投影
			if (!projRes) {
				System.out.println("中心点投影失败");
				return null;
			}
			System.out.println("中心点投影后:" + center.getSpatialReference().getName()+", "+center.getX()+"  " + center.getY());
			ISpatialReference reference = center.getSpatialReference();
			double halfLength = Double.parseDouble(strArr[5])*500;//长的一半
			double halfWidth = Double.parseDouble(strArr[6])*500;//宽的一半
			BLH[] blhs = new BLH[4];
			double strike = Double.parseDouble(strArr[10]);//走向
			double rotate = (360 - strike)/360 * 2 * Math.PI;//旋转角度的弧度表示
			//左下角顶点
			Point vertex = new Point();
			vertex.setSpatialReferenceByRef(reference);
			vertex.setX(center.getX() - halfWidth);
			vertex.setY(center.getY() - halfLength);
			System.out.println("左下角顶点旋转前:" + vertex.getSpatialReference().getName()+", "+vertex.getX()+"  " + vertex.getY());
			vertex.rotate(center, rotate);//旋转
			System.out.println("左下角顶点投影前:" + vertex.getSpatialReference().getName()+", "+vertex.getX()+"  " + vertex.getY());
			vertex.project(ArcgisHelper.wgs84CoordinateSystem);
			System.out.println("左下角顶点投影后:" + vertex.getSpatialReference().getName()+", "+vertex.getX()+"  " + vertex.getY());
			blhs[0] = new BLH(vertex.getY(), vertex.getX(), vertex.getZ());//获取左下角顶点
			//右下角顶点
			vertex = new Point();
			vertex.setSpatialReferenceByRef(reference);
			vertex.setX(center.getX() + halfWidth);
			vertex.setY(center.getY() - halfLength);
			System.out.println("右下角顶点旋转前:" + vertex.getSpatialReference().getName()+", "+vertex.getX()+"  " + vertex.getY());
			vertex.rotate(center, rotate);//旋转
			System.out.println("获取右下角顶点投影前:" + vertex.getSpatialReference().getName()+", "+vertex.getX()+"  " + vertex.getY());
			vertex.project(ArcgisHelper.wgs84CoordinateSystem);
			System.out.println("获取右下角顶点投影后:" + vertex.getSpatialReference().getName()+", "+vertex.getX()+"  " + vertex.getY());
			blhs[1] = new BLH(vertex.getY(), vertex.getX(), vertex.getZ());//获取右下角顶点
			//右上角顶点
			vertex = new Point();
			vertex.setSpatialReferenceByRef(reference);
			vertex.setX(center.getX() + halfWidth);
			vertex.setY(center.getY() + halfLength);
			System.out.println("右上角顶点旋转前:" + vertex.getSpatialReference().getName()+", "+vertex.getX()+"  " + vertex.getY());
			vertex.rotate(center, rotate);//旋转
			System.out.println("获取右上角顶点投影前:" + vertex.getSpatialReference().getName()+", "+vertex.getX()+"  " + vertex.getY());
			vertex.project(ArcgisHelper.wgs84CoordinateSystem);
			System.out.println("获取右上角顶点投影后:" + vertex.getSpatialReference().getName()+", "+vertex.getX()+"  " + vertex.getY());
			blhs[2] = new BLH(vertex.getY(), vertex.getX(), vertex.getZ());//获取右上角顶点
			//左上角顶点
			vertex = new Point();
			vertex.setSpatialReferenceByRef(reference);
			vertex.setX(center.getX() - halfWidth);
			vertex.setY(center.getY() + halfLength);
			System.out.println("左上角顶点旋转前:" + vertex.getSpatialReference().getName()+", "+vertex.getX()+"  " + vertex.getY());
			vertex.rotate(center, rotate);//旋转
			System.out.println("获取左上角顶点投影前:" + vertex.getSpatialReference().getName()+", "+vertex.getX()+"  " + vertex.getY());
			vertex.project(ArcgisHelper.wgs84CoordinateSystem);
			System.out.println("获取左上角顶点投影后:" + vertex.getSpatialReference().getName()+", "+vertex.getX()+"  " + vertex.getY());
			blhs[3] = new BLH(vertex.getY(), vertex.getX(), vertex.getZ());//获取左上角顶点
			return blhs;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static Map<Integer, ISpatialReference> gKSpatialReferenceMap = new HashMap<>();
	/**
	 * 根据点所在投影带进行高斯投影（3°投影）
	 * @param point
	 * @return
	 */
	public static boolean project_GK(IPoint point) {
		//esriSRProjCS4Type.esriSRProjCS_Xian1980_3_Degree_GK_CM_102E
		try {
			int centL = ((int)(point.getX()+1.5)/3)*3;//中央经线
			String projName = "esriSRProjCS_Xian1980_3_Degree_GK_CM_"+centL;
			if (centL > 0) {
				projName = projName+"E";
			}else{
				System.out.println("中央经线<0, " + centL);
				return false;
			}
			int projNum = getProjNum(projName);
//			System.out.println(projName+" = " + projNum);
			if (gKSpatialReferenceMap.containsKey(projNum)) {
				point.project(gKSpatialReferenceMap.get(projNum));
			}else {
				ISpatialReference spatialReference = ArcgisHelper.spatialReferenceEnvironment.createProjectedCoordinateSystem(projNum);
				gKSpatialReferenceMap.put(projNum, spatialReference);
				point.project(spatialReference);
			}
			return true;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 根据投影参数的名称，获取相应的代码值
	 * @param projName
	 * @return
	 */
	public static int getProjNum(String projName) {
		try {
			Field field = esriSRProjCS4Type.class.getField(projName);
			boolean access = field.isAccessible();  
            if(!access) field.setAccessible(true);
            Object o = field.get(esriSRProjCS4Type.class); 
            if(!access) field.setAccessible(false);
//            System.out.println(o);
            return (int)o;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return -1;
		}
	}

	private static boolean isStrValid(String[] strArr) {
		if (!StringHelper.isNumeric(strArr[8])) {
			return false;
		}
		if (!StringHelper.isNumeric(strArr[9])) {
			return false;
		}
		if (!StringHelper.isNumeric(strArr[10])) {
			return false;
		}
		return true;
	}

	private static boolean addSlipBLGrid(Extent extent) {
		IPolyline grid = createSlipBLGrid(extent);
		if (grid == null) {
			return false;
		}
	//	String outDir = Property.getStringProperty("filePath")+eq.getEQID()+"/";
		String outDir = Config.filePath+"test/";
		String fileName = "EQSlipBLGrid.shp";
		try {
			ShapefileWorkspaceFactory factory = ArcgisHelper.getShpFactory();
			IWorkspace workspace = factory.openFromFile(outDir, 0);
			IFeatureWorkspace featureWorkspace = (IFeatureWorkspace)workspace;
			IFeatureClass featureClass = featureWorkspace.openFeatureClass(fileName);
			IFeature feature = featureClass.createFeature();
			feature.setShapeByRef(grid);
			feature.store();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	private static IPolyline createSlipBLGrid(Extent extent){
		try {
			IGeometryBridge2 geometryBridge2 = new GeometryEnvironment();
			IPointCollection4 pointCollection4 = new Polyline();
			_WKSPoint[] wksPoints = new _WKSPoint[5];//2D
			//初始化Rectangle的四个顶点
			//左下角顶点
			wksPoints[0] = new _WKSPoint();
			wksPoints[0].x = extent.minX;
			wksPoints[0].y = extent.minY;
			//右下角顶点
			wksPoints[1] = new _WKSPoint();
			wksPoints[1].x = extent.maxX;
			wksPoints[1].y = extent.minY;
			//右上角顶点
			wksPoints[2] = new _WKSPoint();
			wksPoints[2].x = extent.maxX;
			wksPoints[2].y = extent.maxY;
			//左上角顶点
			wksPoints[3] = new _WKSPoint();
			wksPoints[3].x = extent.minX;
			wksPoints[3].y = extent.maxY;
			//左下角顶点
			wksPoints[4] = new _WKSPoint();
			wksPoints[4].x = extent.minX;
			wksPoints[4].y = extent.minY;
			
			geometryBridge2.setWKSPoints(pointCollection4, wksPoints);
			return (IPolyline)pointCollection4;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static IPolygon createRectangle(BLH[] blhs) {
		try {
			IGeometryBridge2 geometryBridge2 = new GeometryEnvironment();
			IPointCollection4 pointCollection4 = new Polygon();
//			_WKSPoint[] wksPoints = new _WKSPoint[4];//2D
			_WKSPointZ[] wksPointZs = new _WKSPointZ[5];//3D
			//初始化Rectangle的四个顶点
			for (int i = 0; i < 4; i++) {
				wksPointZs[i] = new _WKSPointZ();
				wksPointZs[i].x = blhs[i].L;
				wksPointZs[i].y = blhs[i].B;
			}
			wksPointZs[4] = new _WKSPointZ();
			wksPointZs[4].x = blhs[0].L;
			wksPointZs[4].y = blhs[0].B;
//			geometryBridge2.setWKSPoints(pointCollection4, wksPoints);
			geometryBridge2.setWKSPointZs(pointCollection4, wksPointZs);
			
			return (IPolygon)pointCollection4;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static Extent getSlipLayerExtent(String serviceFolder, String serviceName, int layerId) {
		Extent extent = ArcgisServiceHelper.getLayerExtent(serviceFolder, serviceName, layerId);
		if (extent != null) {
			//min向下取整， max向上取整，获取整数范围
			extent.minX = Math.floor(extent.minX);
			extent.minY = Math.floor(extent.minY);
			extent.maxX = Math.ceil(extent.maxX);
			extent.maxY = Math.ceil(extent.maxY);
		}
		return extent;
	}
	  public static void getObjAttr()  
	  {  
	      // 获取对象obj的所有属性域  
	      Field[] fields = esriSRProjCS4Type.class.getDeclaredFields();  
	        
	      for (Field field : fields)  
	      {  
	          // 对于每个属性，获取属性名  
	          String varName = field.getName();  
	          try  
	          {  
	              boolean access = field.isAccessible();  
	              if(!access) field.setAccessible(true);  
	                
	              //从obj中获取field变量  
	              Object o = field.get(esriSRProjCS4Type.class);  
	              System.out.println("变量： " + varName + " = " + o);  
	                
	              if(!access) field.setAccessible(false);  
	          }  
	          catch (Exception ex)  
	          {  
	              ex.printStackTrace();  
	          }  
	      }  
	  }
}
