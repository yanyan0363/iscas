package dataAccept;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import baseObject.BaseStation;
import baseObject.GNSSStation;
import helper.GPSFileIOHelper;
import mainFrame.GNSSFrame;
import metaData.GPSMetaData;
import metaData.StaticMetaData;
import utils.Config;
import utils.StringHelper;

public class HandleGPSSocket extends Thread{

	Socket client = null;
	private SimpleDateFormat formatS = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public HandleGPSSocket(Socket client) {
		this.client = client;
	}
	@Override
	public void run() {
		SocketAddress clientAddress = client.getRemoteSocketAddress();  
        System.out.println("GNSSServer Handling client at "+ clientAddress);  
        // 读取客户端数据 
		try {
			BufferedReader  in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String  requestMsg = null;
			boolean res = true;
			String errMsg = "";
			try {
//				while((requestMsg=in.readLine())!=null && !requestMsg.equals("")){
				while(true){
					requestMsg = in.readLine();
					if (requestMsg == null || requestMsg.equals("")) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						continue;
					}
					if (Config.isGPSSysOut) {
						System.out.println(requestMsg);
					}
					if (!handleRequest(requestMsg)) {
						res = false;
						errMsg+=requestMsg+";";
					}
					requestMsg = null;
				}
			} catch (SocketException e) {
//				e.printStackTrace();
				if (in != null) {
					in.close();
				}
				if(client!= null && client.isClosed() == false )
					client.close();
			}
			
			try{
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));  
				String responseMessgae =  getResponseMessage(res,errMsg) ;//"{\"response\":\"true\",\"time\":\""+DateHelper.getDateString(DateHelper.getToday())+"\"}" ;
				out.write(responseMessgae);
				System.out.println(responseMessgae);
				out.flush();		        
				in.close();
				out.close();
				System.out.println("after close.");
			}
			catch(Exception exp)
			{
				if(client!= null && client.isClosed() == false )
					client.close();
				exp.printStackTrace();
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}  
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
						Date tt = formatS.parse(messages[GPSMetaData.timeIdx]);
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
	 public String getResponseMessage(boolean f, String errMsg){
         Date time = Calendar.getInstance().getTime() ;
		    String s = StringHelper.getStringFromDate(time) + "::" + f + "\r\n"+ errMsg;
			return s ;
		}
	 
}
