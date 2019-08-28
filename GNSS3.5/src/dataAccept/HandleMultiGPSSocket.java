package dataAccept;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import baseObject.BaseStation;
import baseObject.GNSSStation;
import helper.GPSFileIOHelper;
import mainFrame.GNSSFrame;
import metaData.GPSMetaData;
import metaData.StaticMetaData;
import utils.Config;
import utils.StringHelper;

public class HandleMultiGPSSocket extends Thread {

	List<Socket> clientList = new ArrayList<>();
	List<BufferedReader> readerList = new ArrayList<>();
	
	public HandleMultiGPSSocket() {
		
	}
	public boolean addClient(Socket client) {
		SocketAddress clientAddress = client.getRemoteSocketAddress();  
		System.out.println("GNSSServer Handling client at "+ clientAddress);  
		clientList.add(client);
		try {
			readerList.add(new BufferedReader(new InputStreamReader(client.getInputStream())));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	@Override
	public void run() {
		
//		while (true) {
//			for (BufferedReader reader : readerList) {
//				// 读取客户端数据 
//				try {
//					String  requestMsg = null;
//					boolean res = true;
//					String errMsg = "";
//					try {
//						while((requestMsg=reader.readLine()).equals("") == false){
//							System.out.println(requestMsg);
//							if (Config.isGPSSysOut) {
//								System.out.println(requestMsg);
//							}
//							if (!handleRequest(requestMsg)) {
//								res = false;
//								errMsg+=requestMsg+";";
//							}
//							requestMsg = null;
//						}
//					} catch (SocketException e) {
//						if (reader != null) {
//							reader.close();
//						}
//					}catch (Exception e) {
//					}
//				}catch (Exception e) {
//				}
//			}
//		}
	}	
		public boolean handleRequest(String requestMsg){
			if(requestMsg == null )
				return false ;
			if(GNSSFrame.myStations == null ){
				GNSSFrame.myStations = new Hashtable<String , BaseStation>();
			}
			BaseStation bs = null ;
			String[] lines = StringHelper.getStringArray(requestMsg, ";");
			for (int i = 0; i < lines.length; i++) {
				String[] messages = StringHelper.getStringArray(lines[i], GPSMetaData.dataSeperator) ;
				if(messages == null  || messages.length < 8 )
					return false ;
				int stationIdIdx = GPSMetaData.stationIdIdx;
				String stationId = messages[stationIdIdx];
				//添加过滤器，过滤非指定id的台站数据
				if (!StaticMetaData.getStationOriginalLocs().containsKey(stationId)) {
					System.out.println(stationId + "不存在");
					return false;
				}
				if(GNSSFrame.myStations.containsKey(stationId)){
					bs = GNSSFrame.myStations.get(stationId) ;
				}
				else {
					bs = new GNSSStation(stationId).getStationInstance() ;
					if (bs == null) {
						System.out.println("台站"+stationId+"不存在");
						return false;
					}
					GNSSFrame.myStations.put(stationId, bs) ;
				}
				//写入GPS数据文件
				if (Config.isGPSFiled) {
					try {
						Date tt = StaticMetaData.formatS.parse(messages[GPSMetaData.timeIdx]);
						GPSFileIOHelper.saveGPSLine(bs.ID, tt, lines[i]);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				boolean res = bs.handleMyMessage(messages) ;
				if (!res) {
					System.out.println(lines[i]);
					return false;
				}
				messages= null ;
			}
			return true;
		}
}
