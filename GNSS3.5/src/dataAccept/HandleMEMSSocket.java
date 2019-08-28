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
import beans.MEMSData;
import helper.MEMSFileIOHelper;
import mainFrame.GNSSFrame;
import metaData.MEMSMetaData;
import metaData.StaticMetaData;
import utils.Config;
import utils.StringHelper;

public class HandleMEMSSocket extends Thread{

	 SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	 
	Socket client = null;
	public HandleMEMSSocket(Socket client) {
		this.client = client;
	}
	@Override
	public void run() {
		SocketAddress clientAddress = client.getRemoteSocketAddress();  
        System.out.println("MEMSServer Handling client at "+ clientAddress);  
        // 读取客户端数据 
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String  requestMsg = null;
			boolean res = true;
			String errMsg = "";
			try {
//				while((requestMsg=in.readLine())!=null && !requestMsg.equals("")){
				while(true){
					requestMsg = in.readLine();
					if (requestMsg == null || requestMsg.equals("")) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						continue;
					}
					if (Config.isMEMSSysOut) {
						System.out.println(requestMsg);
					}
					if (!handelRequest(requestMsg)) {
						res = false;
						errMsg+=requestMsg+";";
					}
					requestMsg = null;
				}
			} catch (SocketException e) {
//				e.printStackTrace();
				if(client!= null && client.isClosed() == false )
					client.close();
			}
//			try{
//				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));  
//				String responseMessgae =  getResponseMessage(res,errMsg) ;//"{\"response\":\"true\",\"time\":\""+DateHelper.getDateString(DateHelper.getToday())+"\"}" ;
//				out.write(responseMessgae);
//				System.out.println(responseMessgae);
//				out.flush();		        
//				in.close();
//				out.close();
//				System.out.println("after close.");
//			}
//			catch(Exception exp)
//			{
//				if(client!= null && client.isClosed() == false )
//					client.close();
//				exp.printStackTrace();
//			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
	}
	 public  boolean  handelRequest(String requestMsg) {
//			System.out.println(requestMsg);
		 if(requestMsg == null )
				return false ;
			if(GNSSFrame.myStations == null ){
				GNSSFrame.myStations = new Hashtable<String , BaseStation>();
			}
			BaseStation bs = null ;
			String[] messages = StringHelper.getStringArray(requestMsg, MEMSMetaData.seperator) ;
			if(messages == null  || messages.length < 5 )
				return false ;
			int stationIdIdx = MEMSMetaData.stationIdIdx;
			String stationId = messages[stationIdIdx];
			//添加过滤器，过滤非指定id的台站数据
			if (!StaticMetaData.getStationOriginalLocs().containsKey(stationId)) {
				System.out.println(stationId + "不存在");
				return false;
			}
			if(Config.isMEMSFiled && stationIdIdx <= messages.length-1 && MEMSMetaData.timeIdx <= messages.length-1){
				try {
					Date time = format.parse(messages[MEMSMetaData.timeIdx]);
					boolean saveRes = MEMSFileIOHelper.saveMEMSLine(messages[stationIdIdx], time, requestMsg);
					if (!saveRes) {
						System.out.println("当前MEMSLine持久化存储出错：："+requestMsg);
					}
				} catch (ParseException e) {
					e.printStackTrace();
					return false;
				}
			}
			if(GNSSFrame.myStations.containsKey(messages[stationIdIdx])){
				bs = GNSSFrame.myStations.get(messages[stationIdIdx]) ;
			}
			else {
				bs = new GNSSStation(messages[stationIdIdx]).getStationInstance() ;
				if (bs == null) {
					System.out.println("台站"+messages[stationIdIdx]+"不存在");
					return false;
				}
				GNSSFrame.myStations.put(messages[stationIdIdx], bs) ;
			}
			boolean res = handleMessage(bs, messages);
			messages= null ;
			return res ; 
		}
	 private boolean handleMessage(BaseStation station, String[] messages) {
		 if(MEMSMetaData.stationIdIdx >= messages.length-1
 				|| MEMSMetaData.timeIdx >= messages.length-1 
 				|| MEMSMetaData.accEIdx >= messages.length-1
 				|| MEMSMetaData.accNIdx >= messages.length-1
 				|| MEMSMetaData.accHIdx >= messages.length-1){
			 System.out.println("MEMS数据行格式不匹配：：" + messages.length);
			 return false;
		 }
		 MEMSData memsData = new MEMSData();
		 try {
			memsData.time = format.parse(messages[MEMSMetaData.timeIdx]);
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("时间格式错误：：" + messages[MEMSMetaData.timeIdx]);
			return false;
		}
		 try {
			 if (StringHelper.isNumeric(messages[MEMSMetaData.accEIdx])) {
				 memsData.accE = Double.parseDouble(messages[MEMSMetaData.accEIdx]);
			 }else{
				 System.out.println("accE错误：："+messages[MEMSMetaData.accEIdx]);
			 }
			 if (StringHelper.isNumeric(messages[MEMSMetaData.accNIdx])) {
				 memsData.accN = Double.parseDouble(messages[MEMSMetaData.accNIdx]);
			 }else{
				 System.out.println("accN错误：："+messages[MEMSMetaData.accNIdx]);
			 }
			 if (StringHelper.isNumeric(messages[MEMSMetaData.accHIdx])) {
				 memsData.accH = Double.parseDouble(messages[MEMSMetaData.accHIdx]);
			 }else{
				 System.out.println("accH错误：："+messages[MEMSMetaData.accHIdx]);
			 }
			 if (messages.length >= 6) {
				 memsData.token = messages[MEMSMetaData.tokenIdx];
			 }
			 memsData.localTime = new Date();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		boolean res = station.myDataCache.insertMEMSData(memsData);
		return res;
	}
	public String getResponseMessage(boolean f, String errMsg){
        Date time = Calendar.getInstance().getTime() ;
	    String s = StringHelper.getStringFromDate(time) + "::" + f + "\r\n"+ errMsg;
		return s ;
	}
}
