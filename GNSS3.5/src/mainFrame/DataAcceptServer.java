package mainFrame;

import dataAccept.DataServerNIO;
import dataAccept.GPSServer;
import dataAccept.GPSServerNIO;
import dataAccept.MEMSServer;
import dataAccept.MEMSServerNIO;
import metaData.StaticMetaData.DAType;
import utils.Config;

public class DataAcceptServer {
	
	public void start(DAType type) {
		
		if (type == DAType.MTAccept) {//多线程接收
			GPSServer myGPSServer = new GPSServer(Config.GNSSDataAcceptServerPort);//接收GNSS数据
			MEMSServer myMEMSServer = new MEMSServer(Config.MEMSDataAcceptServerPort);//接收MEMS数据
			myGPSServer.start(); //接收GNSS数据
			myMEMSServer.start();//接收MEMS数据
		}else if (type == DAType.STAccept) {//单线程接收
			//重写单线程接收程序
			DataServerNIO GPSServerNIO = new GPSServerNIO(Config.GNSSDataAcceptServerPort);
			GPSServerNIO.start();
			DataServerNIO MEMSServerNIO = new MEMSServerNIO(Config.MEMSDataAcceptServerPort);
			MEMSServerNIO.start();
		}
		
	}
}
