package arcgis.pyHelper;

import java.util.Hashtable;

import arcgis.ArcgisEQHelper;
import dataCache.Displacement;
import utils.Config;

public class ArcgisPyHelper {

	/**
	 * 生成等值线，重构mxd，并发布服务
	 * @param eqID
	 * @param stationShpName
	 * @param timeNode
	 * @param stationMaxHDis
	 * @param stationMaxVDis
	 * @return
	 */
	public static boolean createContours(String eqID,String stationShpName, String timeNode, 
			Hashtable<String, Displacement> stationMaxHDis, Hashtable<String, Displacement> stationMaxVDis) {
		if (ArcgisEQHelper.addMaxHVDis(eqID,stationShpName,stationMaxHDis, stationMaxVDis)) {//添加最大水平位移量和最大垂直位移量
			ContoursProc contoursProc = new ContoursProc(Config.ArcpyFolder, Config.filePath,
					Config.filePath+eqID+"/"+stationShpName, eqID, Config.contoursInterval+"",
					Config.demoMXD,eqID+".mxd",
					Config.servicePath,Config.serviceFolder,timeNode);
			return contoursProc.exec();
		}else{
			System.out.println("台站点的shp文件中添加最大水平位移量和最大垂直位移量失败");
			return false;
		}
	}
	public static boolean createMxdAndPub(String eqID) {
		NodeOpProc proc = new NodeOpProc(Config.ArcpyFolder, Config.filePath,eqID,
					Config.demoMXD,eqID+".mxd", Config.servicePath,Config.serviceFolder);
		return proc.exec();
	}
	public static void main(String[] args) {
		createMxdAndPub("1504664051755");
	}
}
