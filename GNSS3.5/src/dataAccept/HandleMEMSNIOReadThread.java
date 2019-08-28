package dataAccept;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class HandleMEMSNIOReadThread extends Thread{
	private SelectionKey key = null;
	private ByteBuffer buffer = null;
	private DataServerNIO dataServerNIO = null;
	private int BUF_SIZE = 0;
	private String tmpLine = ""; 
	private boolean flag = true;
	private SimpleDateFormat formatMs = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	private String remoteAddr = "";
	//断网检测
//	public int netErrTime = 10*60 ;//提取至config.properties
	Date netChecker =  null; 
	
	public HandleMEMSNIOReadThread(SelectionKey key,int BUF_SIZE, DataServerNIO dataServerNIO) {
		this.key = key;
		this.BUF_SIZE = BUF_SIZE;
		this.dataServerNIO = dataServerNIO;
	}
	@Override
	public void run() {
		buffer = ByteBuffer.allocate(BUF_SIZE);
		SocketChannel channel = (SocketChannel)key.channel();
		try {
			remoteAddr = channel.getRemoteAddress().toString();
			System.out.println("HandleMEMSNIOReadThread start:"+remoteAddr);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		netChecker = new Date(); 
		while (flag) {
			try{// keep current thread working 
			   	if (key.isValid()==false||key.isReadable()==false) {
			   		flag = false;
					dataServerNIO.removeKeyInReadSet(key);
					//清理缓存
					clearMemory();
					channel = null;
					break;
				}
				try {
					handleRead(channel);
//					sleep(2);
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
					flag = false;
					dataServerNIO.removeKeyInReadSet(key);
					//清理缓存
					clearMemory();
					channel = null;
				}
			}catch(Exception exp){
				System.out.println("thread working excep: HandleMEMSNIOReadThread "+remoteAddr);
				exp.printStackTrace();
				flag = false;
				dataServerNIO.removeKeyInReadSet(key);
				//清理缓存
				clearMemory();
				channel = null;
				break;
			}
		}
	}
	private void clearMemory() {
		tmpLine = null;
		formatMs = null;
		buffer = null;
		dataServerNIO = null;
		remoteAddr = null;
		netChecker = null;
		key = null;
	}
	public void handleRead(SocketChannel channel){
		buffer.clear();
		String lastLine = tmpLine;
		try {
			long bytesRead = channel.read(buffer);
			//断网检测----------------------
			if(bytesRead > 0 ){
				netChecker= new Date();
			}else if (bytesRead == 0) {
				Date netAfterChecker = new Date(); 
				long t =netAfterChecker.getTime() - netChecker.getTime() ;
				if(t/1000 >= Config.netErrTime){
					flag = false ;
					dataServerNIO.removeKeyInReadSet(key);
					System.out.println("current net connection is out shut down the key: "+remoteAddr);
					//清理缓存
					clearMemory();
					channel = null;
					return ;
				}
			}
			//断网检测----------------------
			while (bytesRead > 0) {
				buffer.flip();
				byte[] content = new byte[buffer.limit()];
				buffer.get(content);
				String tmp = lastLine + new String(content);
				content = null;
				if (!tmp.endsWith("\r\n")) {
					if (tmp.lastIndexOf("\r\n") != -1) {
						lastLine = tmp.substring(tmp.lastIndexOf("\r\n")+2);
						tmp = tmp.substring(0, tmp.lastIndexOf("\r\n"));
					}else {
						lastLine = tmp;
						tmp = null;
					}
				}else {
					lastLine = "";
				}
				handleLines(tmp);
				buffer.clear();
				bytesRead = channel.read(buffer);
//				System.out.println("MEMS bytesRead -- "+bytesRead);
			}
			tmpLine = lastLine;
			lastLine = null;
			if (bytesRead == -1) {
				flag = false;
				dataServerNIO.removeKeyInReadSet(key);
				System.out.println("HandleMEMSNIOReadThread bytesRead == -1, dataServerNIO.removeKeyInReadSet(key): "+remoteAddr);
				//清理缓存
				clearMemory();
				channel = null;
			}
		} catch (Exception e) {
//			e.printStackTrace();
			tmpLine = null;
			buffer = null;
			flag = false;
			dataServerNIO.removeKeyInReadSet(key);
			//清理缓存
			clearMemory();
			channel = null;
		}
	}
	public boolean handleLines(String content) {
		if(content == null )
			return false ;
//		if(GNSSFrame.myStations == null ){
//			GNSSFrame.myStations = new Hashtable<String , BaseStation>();
//		}
		BaseStation bs = null ;
		String[] lines = content.split("\r\n");
//		System.out.println("MEMS handle start..., start -- "+formatMs.format(new Date())+" -- "+lines.length);
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			lines[i] = null;
//			System.out.println("line "+i+" -- "+line);
			if (Config.isMEMSSysOut) {
				System.out.println("line "+i+" -- "+line);
			}
			line = lineFilter(line);
			if (line == null) {
				continue;
			}
			String[] messages = filterFields(line);
			if (messages == null) {
				continue;
			}
//			String[] messages = StringHelper.getStringArray(lines[i], MEMSMetaData.seperator) ;
//			if(messages == null  || messages.length < 5 )
//				continue;
			int stationIdIdx = MEMSMetaData.stationIdIdx;
			String stationId = messages[stationIdIdx];
//			if(Config.isMEMSFiled && stationIdIdx <= messages.length-1 && MEMSMetaData.timeIdx <= messages.length-1){
			if(Config.isMEMSFiled){
				try {
					Date time = formatMs.parse(messages[MEMSMetaData.timeIdx]);
					boolean saveRes = MEMSFileIOHelper.saveMEMSLine(stationId, time, line);
					if (!saveRes) {
						System.out.println("当前MEMSLine持久化存储出错：："+line);
					}
					time = null;
				} catch (ParseException e) {
//					System.out.println(stationId+":"+messages[MEMSMetaData.timeIdx]);
//					e.printStackTrace();
//					return false;
					continue;
				} catch (NumberFormatException e) {
					System.out.println("curLine:"+line);
					System.out.println("messages[MEMSMetaData.timeIdx]:"+messages[MEMSMetaData.timeIdx]);
					e.printStackTrace();
					continue;
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
			if(GNSSFrame.myStations.containsKey(stationId)){
				bs = GNSSFrame.myStations.get(stationId) ;
			}
			else {
				bs = new GNSSStation(stationId).getStationInstance() ;
				if (bs == null) {
					System.out.println("台站"+stationId+"不存在,continue,放弃缓存.");
//					return false;
					continue;
				}
				GNSSFrame.myStations.put(stationId, bs) ;
			}
			stationId = null;
			boolean res = handleMessage(bs, messages);
			if (!res) {
				System.out.println(line);
			}
		}
//		for (int i = 0; i < lines.length; i++) {
//			lines[i] = null;
//		}
		lines = null;
		return true; 
	}
	private boolean handleMessage(BaseStation station, String[] messages) {
		 MEMSData memsData = new MEMSData();
		 try {
			memsData.time = formatMs.parse(messages[MEMSMetaData.timeIdx]);
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("时间格式错误：：" + messages[MEMSMetaData.timeIdx]);
			clearMessages(messages);
			return false;
		} catch (NumberFormatException e) {
			System.out.println(messages);
			System.out.println("messages[MEMSMetaData.timeIdx]:"+messages[MEMSMetaData.timeIdx]);
			e.printStackTrace();
			clearMessages(messages);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			clearMessages(messages);
			return false;
		}
		 try {
			 memsData.accE = Double.parseDouble(messages[MEMSMetaData.accEIdx]);
			 memsData.accN = Double.parseDouble(messages[MEMSMetaData.accNIdx]);
			 memsData.accH = Double.parseDouble(messages[MEMSMetaData.accHIdx]);
			 if (messages.length >= 6) {
				 memsData.token = messages[MEMSMetaData.tokenIdx];
			 }
			 memsData.localTime = new Date();
		 } catch (Exception e) {
			 e.printStackTrace();
			 clearMessages(messages);
			 return false;
		 }
		 clearMessages(messages);
		boolean res = station.myDataCache.insertMEMSData(memsData);
		return res;
	}
	private void clearMessages(String[] messages) {
		if (messages == null) {
			return;
		}
		for (int j = 0; j < messages.length; j++) {
			messages[j] = null;
		}
		messages= null ;
	}
	/**
	 * 过滤数据行中的字段
	 * @param line过滤后的数据行
	 * @return 合法有效的字符串数组，若为null,则表示存在不合法字段，舍弃
	 */
	private String[] filterFields(String line) {
		String[] messages = StringHelper.getStringArray(line, MEMSMetaData.seperator); 
		if(messages == null  || messages.length < 5 ){
			return null;
		}
		if(MEMSMetaData.stationIdIdx >= messages.length-1
				|| MEMSMetaData.timeIdx >= messages.length-1 
				|| MEMSMetaData.accEIdx >= messages.length-1
				|| MEMSMetaData.accNIdx >= messages.length-1
				|| MEMSMetaData.accHIdx >= messages.length-1){
			 System.out.println("MEMS数据行格式不匹配：：" + messages.length);
			 return null;
		 }
		if (StringHelper.isNumeric(messages[MEMSMetaData.accEIdx])
				&& StringHelper.isNumeric(messages[MEMSMetaData.accNIdx])
				&& StringHelper.isNumeric(messages[MEMSMetaData.accHIdx])
				&& messages.length == 6) {
			return messages;
		 }else{
			 System.out.println("数据格式错误："+line);
			 return null;
		 }
	}
	/**
	 * 数据行过滤,包括合法有效的台站id过滤
	 * @param line原始数据行
	 * @return 过滤后的数据行，若为null,则表明原始数据行不合规范，舍弃。
	 * 用于去除断行，行首乱码，不存在的台站id等
	 */
	private String lineFilter(String line) {
		if (line == null || line.length() <= 0 ) {
			return null;
		}
		String stID = line.substring(0, line.indexOf(MEMSMetaData.seperator));
		String stationID = stationFilter(stID);
		if (stationID == null) {
			return null;
		}else if (!stID.equals(stationID)) {
			line = line.substring(stID.length()-stationID.length());
			System.out.println("line过滤后："+line);
		}
		return line;
		
	}
	/**
	 * 台站id过滤器，过滤行首可能会出现的乱码，过滤id不存在的台站
	 * @param stationID 给定的台站id
	 * @return null或过滤后合法的stationID
	 */
	private String stationFilter(String stationID) {
		if (stationID == null || stationID == "" || stationID.equals(" ") || stationID.length() < Config.stIDLength) {
			return null;
		}else if (stationID.length() > Config.stIDLength) {
			System.out.print(stationID+"过滤后:");
			stationID = stationID.substring(stationID.length() - Config.stIDLength);
			System.out.println(stationID);
		}
		if (!StaticMetaData.getStationOriginalLocs().containsKey(stationID)) {
			System.out.println(stationID + "台站不存在，过滤丢弃");
			return null;
		}
		return stationID;
	}
	public static void main(String[] args) {
		StaticMetaData.initStaticData();
		HandleMEMSNIOReadThread instance = new HandleMEMSNIOReadThread(null, 1024, null);
		System.out.println(instance.stationFilter(""));
		System.out.println("����SMZSZ:"+instance.stationFilter("����SMZSZ"));
		System.out.println("�����SMTWX:"+instance.stationFilter("�����SMTWX"));
		System.out.println("����SMMNX:"+instance.stationFilter("����SMMNX"));
		System.out.println("xSMXJZ:"+instance.stationFilter("xSMXJZ"));
	}
}
