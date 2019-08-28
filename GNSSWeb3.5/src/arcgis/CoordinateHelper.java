package arcgis;

import java.io.IOException;

import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.ISpatialReferenceFactory;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.geometry.SpatialReferenceEnvironment;
import com.esri.arcgis.geometry.esriSRGeoCSType;
import com.esri.arcgis.geometry.esriSRProjCS4Type;

public class CoordinateHelper {

	/**
	 * 经纬度点转换为平面坐标(未测试未使用)
	 * @param x
	 * @param y
	 * @return
	 */
	public static IPoint GCStoPRJ(double x, double y) {
		IPoint cPoint = null;
        try {
        	cPoint = new Point();
        	cPoint.putCoords(x, y);
        	ISpatialReferenceFactory pSpatialReferenceFactory = new SpatialReferenceEnvironment();
        	cPoint.setSpatialReferenceByRef(pSpatialReferenceFactory.createGeographicCoordinateSystem(4326));
        	//设置投影参数
			//cPoint.project(pSpatialReferenceFactory.createProjectedCoordinateSystem((int)esriSRProjCS4Type.esriSRProjCS_WGS1984_Complex_UTM_Zone_30N));
        	//cPoint.project(pSpatialReferenceFactory.createGeographicCoordinateSystem((int)esriSRProjCS4Type.));
		} catch (IOException e) {
			e.printStackTrace();
		}
        return cPoint;
		
	}
	/**
	 * 将平面坐标转换为经纬度(未测试未使用)
	 * @param x
	 * @param y
	 * @return
	 */
	public static IPoint PJRtoGCS(double x, double y) {
		IPoint point = null;
		try {
			point = new Point();
			point.putCoords(x, y);
			ISpatialReferenceFactory psrf = new SpatialReferenceEnvironment();
//			point.setSpatialReferenceByRef(psrf.createProjectedCoordinateSystem((int)esriSRProjCS4Type.esriSRProjCS_Beijing1954_3_Degree_GK_CM_117E));
			point.setSpatialReferenceByRef(psrf.createProjectedCoordinateSystem((int)esriSRProjCS4Type.esriSRProjCS_Beijing1954_3_Degree_GK_CM_117E));
			point.project(psrf.createGeographicCoordinateSystem(4326));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return point;
	}
}
