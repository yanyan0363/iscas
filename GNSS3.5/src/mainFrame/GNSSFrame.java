package mainFrame;

import java.util.Hashtable;

import baseObject.BaseStation;
import event.EQEvent;
import http.SDHttpHelper;
import metaData.StaticMetaData;
import metaData.StaticMetaData.DAType;

public class GNSSFrame {

	
	public static Hashtable<String , BaseStation> myStations = new Hashtable<String , BaseStation>();
	public static Hashtable<String, EQEvent> eqEvents = new Hashtable<>();
	
	public AnalysisTimerTaskRunner  analysisTimerTaskRunner = null ;
//	public GPSServer myGPSServer = null;
//	public MEMSServer myMEMSServer = null;
	public GNSSHttpServer httpServer = null;
	
	public boolean init(){
		//初始化静态数据
		StaticMetaData.initStaticData();
		
//		myGPSServer = new GPSServer(Config.GNSSDataAcceptServerPort);//接收GNSS数据
//		
//		myMEMSServer = new MEMSServer(Config.MEMSDataAcceptServerPort);//接收MEMS数据
		
		analysisTimerTaskRunner = new AnalysisTimerTaskRunner() ;//解析数据
		
		httpServer = new GNSSHttpServer();//接收http请求
		
		return true ;
	}
	public boolean startUp(){
		
//		myGPSServer.start(); //接收GNSS数据
//		myMEMSServer.start();//接收MEMS数据
		new DataAcceptServer().start(DAType.STAccept);
//		new DataAcceptServer().start(DAType.MTAccept);
		System.out.println("数据接收端口启动");
		analysisTimerTaskRunner.run(); //解析数据
		System.out.println("数据解析线程启动");
		//
		boolean SDGPS = SDHttpHelper.startAllStGPS();//发送GPS解算请求，开始全部台站的GPS解算
		System.out.println("GPS解算请求："+SDGPS);
		httpServer.start();  //接收http请求
		System.out.println("httpServer启动");
		
		return true ;
	}
	/**
	 * 清空缓存
	 */
//	public void clearCache(){
//		myStations = new Hashtable<String , BaseStation>();
//		eqEvents = new Hashtable<>();
//	}
}
