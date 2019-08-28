package arcgis;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import com.esri.arcgis.carto.IFeatureLayer;
import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureCursor;
import com.esri.arcgis.geodatabase.IFeatureWorkspace;
import com.esri.arcgis.geodatabase.IQueryFilter;
import com.esri.arcgis.geodatabase.ISpatialFilter;
import com.esri.arcgis.geodatabase.IWorkspace;
import com.esri.arcgis.geodatabase.QueryFilter;
import com.esri.arcgis.geodatabase.SpatialFilter;
import com.esri.arcgis.geodatabase.esriSpatialRelEnum;
import com.esri.arcgis.geometry.CircularArc;
import com.esri.arcgis.geometry.ICircularArc;
import com.esri.arcgis.geometry.IGeometry;
import com.esri.arcgis.geometry.IGeometryCollection;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.IPolygon;
import com.esri.arcgis.geometry.ISegment;
import com.esri.arcgis.geometry.ISegmentCollection;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.geometry.Polygon;
import com.esri.arcgis.geometry.Ring;
import beans.BLH;
import beans.GPSData;
import beans.Loc;
import beans.ProjPoint;
import dataCache.Displacement;
import dbHelper.DBHelper;
import metaData.StaticMetaData;
import utils.Config;

public class ArcgisEQHelper {
	private static Hashtable<Integer, String> provNameMap = new Hashtable<Integer, String>();
	
	public static String getCountyNameFromLoc(Loc loc) {
		IFeatureClass featureClass = ArcgisShpHelper.getShpFeatureClass(Config.demoFolder, "counties_china.shp");
		try {
			ISpatialFilter filter = new SpatialFilter();
			filter.setGeometryByRef(loc.getWGS84Point());
			filter.setGeometryField("Shape");
			filter.setSpatialRel(esriSpatialRelEnum.esriSpatialRelWithin);
			IFeatureCursor featureCursor = featureClass.search(filter, false);
			IFeature feature = null;
			if ((feature=featureCursor.nextFeature())!=null) {
				return feature.getValue(4)+"";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return "";
	}
//	public static String getCountyNameFromLoc(Loc loc) {
//		IFeatureLayer layer = (IFeatureLayer)ArcgisHelper.getCountiesLayer();
//		try {
//			ISpatialFilter filter = new SpatialFilter();
//			filter.setGeometryByRef(loc.getWGS84Point());
//			filter.setGeometryField("Shape");
//			filter.setSpatialRel(esriSpatialRelEnum.esriSpatialRelWithin);
//			IFeatureCursor featureCursor = layer.search(filter, false);
//			IFeature feature = null;
//			while ((feature=featureCursor.nextFeature())!=null) {
//				return feature.getValue(3)+"";
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return "";
//	}
	/**
	 * 台站点的shp文件中添加最大水平位移量和最大垂直位移量
	 * @param stationMaxHDis key:stationID value:station的最大水平位移量
	 * @param stationMaxVDis key:stationID value:station的最大垂直位移量
	 * @return
	 */
	public static boolean addMaxHVDis(String shpFolder,String stationShpName,Hashtable<String, Displacement> stationMaxHDis, Hashtable<String, Displacement> stationMaxVDis) {
//		IFeatureClass stationsShp = ArcgisHelper.getStationsShp();
		IFeatureWorkspace shpFWorkspace = null;
		IFeatureClass stationsShp = null;
		try {
			shpFWorkspace = (IFeatureWorkspace)ArcgisHelper.getShpFactory().openFromFile(Config.filePath+shpFolder, 0);
			stationsShp = shpFWorkspace.openFeatureClass(stationShpName);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		try {
			IFeature feature = null;
			String sID = "";
			int vDisIdx = stationsShp.findField("VMaxDis");
			int vTIdx = stationsShp.findField("VMaxT");
			int hDisIdx = stationsShp.findField("HMaxDis");
			int hTIdx = stationsShp.findField("HMaxT");
			for (int i = 0; i < stationsShp.featureCount(null); i++) {
				feature = stationsShp.getFeature(i);
				sID = feature.getValue(3)+"";
				Displacement dis = stationMaxVDis.get(sID);
				if (dis != null) {
					feature.setValue(vDisIdx, Math.abs(dis.zDisplacement));
					feature.setValue(vTIdx, dis.time);
//					System.out.println(sID+" v ::"+dis.zDisplacement + dis.time);
				}
				dis = null;
				dis = stationMaxHDis.get(sID);
				if (dis != null) {
					feature.setValue(hDisIdx, Math.abs(dis.zDisplacement));
					feature.setValue(hTIdx, dis.time);
//					System.out.println(sID+" h ::"+dis.zDisplacement + dis.time);
				}
				feature.store();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
//	public static boolean updateStation(String stationID, double B, double L) {
//		IFeatureLayer stationLayer = (IFeatureLayer)ArcgisHelper.getStationsLayer();
//		try {
//			IQueryFilter queryFilter = new QueryFilter();
//			if(stationLayer == null ){
//				System.out.println("updateStation::stationLayer == null ");
//				return false;
//			}
//			IFeatureCursor featureCursor = stationLayer.search(queryFilter, false);
//			try {
//				while(true){
//					IFeature feature  = featureCursor.nextFeature() ;
//					if(feature == null )
//					{
//						break ; 
//					}
//					else if(feature.getValue(2).equals(stationID)){
//						IPoint stationPoint = null;
//						try {
//							stationPoint = new Point();
//							stationPoint.setSpatialReferenceByRef(ArcgisHelper.wgs84CoordinateSystem);
//							stationPoint.setX(L);
//							stationPoint.setY(B);
//							
//							feature.setShapeByRef(stationPoint);
//							feature.store();
//						} catch (IOException e) {
//							e.printStackTrace();
//							return false;
//						}
//					}
//				}
//				featureCursor = null ;
//				queryFilter = null ;
//				return true;
//			} catch (Exception e) {
//				System.out.println("updateStation err 1 !"  );
//				e.printStackTrace();
//				return false;
//			}
//		} catch (Exception e) {
//			System.out.println("updateStation err 2 !"  );
//			e.printStackTrace();
//			return false;
//		}
//	}
//	public static boolean addStation(String stationID, double B, double L) {
//		IFeatureClass featureClass;
//		try {
////			featureClass = ArcgisHelper.getFeatureWorkspace().openFeatureClass("Stations_Project");
//			featureClass = ArcgisShpHelper..getFeatureWorkspace().openFeatureClass("Stations_Project");
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//		IPoint stationPoint = null;
//		IFeature pointFeature = null;
//		try {
//			stationPoint = new Point();
//			stationPoint.setSpatialReferenceByRef(ArcgisHelper.wgs84CoordinateSystem);
//			stationPoint.setX(L);
//			stationPoint.setY(B);
//			pointFeature = featureClass.createFeature();
//			pointFeature.setShapeByRef(stationPoint);
//			pointFeature.setValue(2, stationID);
//			pointFeature.setValue(4, new Date());
//			pointFeature.store();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}
	public static boolean initStations() {
		String sql = "select stationID,longitude,latitude,x_3857,y_3857 from stationsInfo where status=1;";
		ResultSet resultSet = DBHelper.runQuerySql(sql);
		if (resultSet != null) {
			try {
				while (resultSet.next()) {
					Loc curLoc = new Loc(new GPSData(new BLH(resultSet.getDouble("latitude"), resultSet.getDouble("longitude"), 0)), new ProjPoint(resultSet.getDouble("x_3857"), resultSet.getDouble("y_3857"), 0));
					StaticMetaData.getStationOriginalLocs().put(resultSet.getString("stationID"), curLoc);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	/**
	 * center为圆心，过内点innerP和外点outerP生成环状面元素
	 * @param center
	 * @param innerP
	 * @param outerP
	 * @return
	 */
	public static IPolygon drawEQPolygon(IPoint center, IPoint innerP, IPoint outerP) {
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
			
//			double outterRadius = calDisInDecimalDegree(outerP, center);
//			double innerRadius = calDisInDecimalDegree(innerP, center);;
//			System.out.println(outterRadius + ";;" + innerRadius);
			
			circularArc1.putCoordsByAngle(center, 0, 2*Math.PI, 0);
			circularArc2.putCoordsByAngle(center, 0, 2*Math.PI, 0);
			circularArc1.putRadiusByPoint(outerP);
			circularArc2.putRadiusByPoint(innerP);
			circularArc1.reverseOrientation();
//			System.out.println(circularArc1.getRadius()+"::"+circularArc2.getRadius());
//			System.out.println("createAnnulus center:: " + center.getX() + ", " + center.getY() + ", " + center.getZ());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return polygon;
	}
	/**
	 * center为圆心，过点point生成圆形面元素
	 * @param center
	 * @param point
	 * @return
	 */
	public static IPolygon drawCirclePolygon(IPoint center, IPoint point) {
		IPolygon polygon;
		try {
			polygon = new Polygon();
			ICircularArc circularArc1 = new CircularArc();
			ISegmentCollection ring1 = new Ring();
			ring1.addSegment((ISegment)circularArc1, null, null);
			IGeometryCollection geometryCollection = (IGeometryCollection)polygon;
			geometryCollection.addGeometry((IGeometry)ring1, null, null);
			circularArc1.putCoordsByAngle(center, 0, 2*Math.PI, 0);
			circularArc1.putRadiusByPoint(point);
			circularArc1.reverseOrientation();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return polygon;
	}
	/**
	 * 以center为圆心，innerRadius为内半径，outterRadius为外半径，生成环状面元素
	 * @param center 圆心
	 * @param innerRadius 内半径
	 * @param outterRadius 外半径
	 * @return
	 */
	public static IPolygon drawEQPolygon(IPoint center, double innerRadius, double outterRadius) {
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
	 * 计算两点之间的decimal degree距离
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static double calDisInDecimalDegree(IPoint p1, IPoint p2) {
		try {
			return Math.abs(p1.getX()-p2.getX());
		} catch (IOException e) {
			e.printStackTrace();
			return 0.0;
		}
	}
	/**
	 * 计算某一台站的震级
	 * @param epicenter 震中距
	 * @param eventList 当前台站的event列表
	 * @return
	 */
//	public static double calMagnitude(GKPoint epicenter, List<GPSEvent> eventList) {
//		if (eventList == null || eventList.size() < 1) {
//			System.out.println("当前台站的事件列表为空");
//			return 0;
//		}
//		double maxHDis = 0;//水平方向最大位移
//		for (int i = 0; i < eventList.size(); i++) {
//			double curDis = eventList.get(i).snapshot.displacement2D;
//			if (maxHDis < curDis) {
//				maxHDis = curDis;
//			}
//		}
////		double epiDistance = epicenter.getDistance2D(eventList.get(0).snapshot.loc.gkPoint);
//		double epiDistance = epicenter.getDistance2D(eventList.get(0).snapshot.loc.gkPoint);
//		return GisHelper.calMagnitude(maxHDis, epiDistance);
//	}

	//包括点要素，以及相关的地震信息
	/**
	 * 
	 * @param featureName
	 * @param eq
	 * @param num
	 * @param note
	 * @param type 点类型
	 * @return
	 */
//	public static boolean addEQPoint(EQInfo eq, int num, String note, EQPointType type, String state) {
//		IFeatureClass featureClass;
//		try {
//			featureClass = ArcgisHelper.getFeatureWorkspace().openFeatureClass("EQPoints_Project");
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//		IPoint epic = eq.getEpicenter().getProjPoint().getPointCopy().getPoint();
//		IFeature pointFeature;
//		try {
//			pointFeature = featureClass.createFeature();
//			pointFeature.setShapeByRef(epic);
//			pointFeature.setValue(2, eq.getEQID());
//			pointFeature.setValue(3, eq.getMagnitude());
//			pointFeature.setValue(4, eq.getEqTime());
//			pointFeature.setValue(5, num);
//			pointFeature.setValue(6, note);
////			pointFeature.setValue(7, eq.getStationIDs().toString());
//			pointFeature.setValue(8, type.toString());
//			//pointFeature.setValue(9, state);
//			pointFeature.store();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//		System.out.println("addEQPoint eqpoints true");
//		return true;
//	}
//	public static boolean addEQPolygon(EQInfo eq, int num, String note, Hashtable<BaseStation, Report> stationMap) {
//		//计算各个station到震中的decimal degree距离
//		Enumeration<BaseStation> enumeration = stationMap.keys();
////		GKPoint center = eq.getEpicenter().getGkPoint();
//		Loc center = eq.getEpicenter();
//		Hashtable<BaseStation, Double> disStationMap = new Hashtable<>();
//		while (enumeration.hasMoreElements()) {
//			BaseStation baseStation = (BaseStation) enumeration.nextElement();
////			double dis = center.getDistance2D(baseStation.myLocation.getGkPoint());
//			double dis = GisHelper.calDisByBL(center.getGpsData().blh.L, center.getGpsData().blh.B, baseStation.myLocation.getGpsData().blh.L, baseStation.myLocation.getGpsData().blh.B);
//			disStationMap.put(baseStation, dis);
//		}
//		//根据disDegree排序
//		Map<BaseStation, Double> linkedHashMap = MapUtil.sortByValue(disStationMap);
//		Iterator<Entry<BaseStation, Double>> iterator = linkedHashMap.entrySet().iterator();
//		IPoint lastPoint = null;
//		IPoint curPoint = null;
//		IPolygon polygon;
//		IFeature polygonFeature;
//		IFeatureClass featureClass;
//		Report report;
//		try {
//			featureClass = ArcgisHelper.getFeatureWorkspace().openFeatureClass("EQPolygons_Project");
//		} catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//		while (iterator.hasNext()) {
//			Entry<BaseStation, Double> entry = iterator.next();
//			BaseStation baseStation = (BaseStation)entry.getKey();
//			report = stationMap.get(baseStation);
//			curPoint = baseStation.myLocation.getGkPoint().getPointCopy().getPoint();
//			if (lastPoint == null) {
//				polygon = drawCirclePolygon(center.getWGS84Point(),curPoint);
//				report.setFirst(true);
//			}else {
//				polygon = drawEQPolygon(center.getWGS84Point(), lastPoint, curPoint);
//			}
//			lastPoint = curPoint;
//			 try {
//				polygonFeature = featureClass.createFeature();
//				polygonFeature.setShapeByRef(polygon);
//				polygonFeature.setValue(2, eq.getEQID());
//				polygonFeature.setValue(3, report.getMagnitude());//magnitude
//				polygonFeature.setValue(4, note);//note
//				polygonFeature.setValue(5, num);
//				polygonFeature.store();
//			} catch (IOException e) {
//				e.printStackTrace();
//				return false;
//			} 
//			//计算detail
//			report.setDetail(DetailHelper.detail(polygon, report.getMagnitude()));
//		}
//		return true;
//	}
	
	/**
	 * 根据省级编码从 省界_region图层中获取省名称
	 * @param provin
	 * @return
	 */
	private static String getProvNameFromLocal(int pcode){
		if(provNameMap == null )
			return "" ;
		
		if(provNameMap.containsKey((Integer)pcode))
		{
			String name = provNameMap.get((Integer)pcode) ;
			return name;
		}
		return "" ;
	}
	private static void cacheLocalProvName(String pname,int provin) {
		if(provNameMap == null )
			return   ;
		provNameMap.put(provin, pname) ;
	}
	public static String getProvName(int provin) {
		String pname =getProvNameFromLocal(provin) ;
		if(pname.equals("")==false)
			return pname ;
//		IFeatureLayer provLayer = (IFeatureLayer)ArcgisHelper.getProvLayer();
		IFeatureClass featureClass = ArcgisShpHelper.getShpFeatureClass(Config.demoFolder, "province_china.shp");
		try {
			IQueryFilter queryFilter = new QueryFilter();
			queryFilter.setWhereClause("CODE like '" + provin + "%'");
			if(featureClass == null )
				System.out.println("getProvName::featureLayer==null"+"getProvName::" + queryFilter.getWhereClause() );
//			else System.out.println("getProvName::featureLayer is ok " +"getProvName::" + queryFilter.getWhereClause());
			IFeatureCursor featureCursor = featureClass.search(queryFilter, false);
			try {
				int idx = 0 ; 
				while(true){
					idx ++ ;
					if(idx > 1000)
						break ;
					IFeature feature  = featureCursor.nextFeature() ;
					if(feature == null )
					{
						pname="";
						break ; 
					}
					else {
						pname = feature.getValue(2)+"";
						break ;
					}
				}
				featureCursor = null ;
				queryFilter = null ;
				cacheLocalProvName(pname,provin) ;
				return pname ;
			} catch (Exception e) {
				System.out.println("getProvName err 1 !" + provin  );
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println("getProvName err 2 !" + provin  );
			e.printStackTrace();
		}
		return "";
	}
	
	

}
