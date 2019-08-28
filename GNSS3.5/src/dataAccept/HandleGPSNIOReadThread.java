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
import helper.GPSFileIOHelper;
import mainFrame.GNSSFrame;
import metaData.GPSMetaData;
import metaData.StaticMetaData;
import utils.Config;
import utils.StringHelper;

public class HandleGPSNIOReadThread extends Thread{

	private SelectionKey key = null;
	private ByteBuffer buffer = null;
	private DataServerNIO dataServerNIO = null;
	private int BUF_SIZE = 0;
	private String tmpLine = ""; 
	private boolean flag = true;
	private SimpleDateFormat formatS = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private String remoteAddr = "";
	//断网检测
//	public int netErrTime = 10*60 ;//提取至config.properties
	Date netChecker =  null; 
	@Override
	public void run() {
		buffer = ByteBuffer.allocate(BUF_SIZE);
		SocketChannel channel = (SocketChannel)key.channel();
		try {
			remoteAddr = channel.getRemoteAddress().toString();
			System.out.println("HandleGPSNIOReadThread start:"+remoteAddr);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		netChecker = new Date();
		while (flag) {
			try {
				if (key.isValid()==false||key.isReadable()==false) {
					flag = false;
					dataServerNIO.removeKeyInReadSet(key);
					//清理缓存
					clearMemory();
					break;
				}
				try {
					handleRead(channel);
//					System.out.println("sleep 2s...");
					Thread.sleep(2000);
//					sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				System.out.println("thread working excep: HandleGPSNIOReadThread "+remoteAddr);
				e.printStackTrace();
				flag = false;
				dataServerNIO.removeKeyInReadSet(key);
				break;
			}
		}
	}
	private void clearMemory() {
		key = null;
		tmpLine = null;
		formatS = null;
		buffer.clear();
		buffer = null;
		dataServerNIO = null;
		remoteAddr = null;
		netChecker = null;
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
				long t =netAfterChecker.getTime() -netChecker.getTime() ;
				if(t/1000 >= Config.netErrTime){
					flag = false ;
					dataServerNIO.removeKeyInReadSet(key);
					System.out.println("current net connection is out shut down the key: "+remoteAddr);
					//清理缓存
					clearMemory();
					return ;
				}
			}
			//断网检测----------------------
			while (bytesRead > 0) {
//				long t1 = System.nanoTime();
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
				long t2 = System.nanoTime();
//				if (t2-t1>1000000) {
//					System.out.println("     GPS handle over..., --  -- "+(t2-t1)/1000000+"ms");
//				}
//				System.out.println("     GPS handle over..., --  -- "+(t2-t1)/1000000+"ms");
//				t1 = 0;
//				t2 = 0; 
				try {
					bytesRead = channel.read(buffer);
//					System.out.println("GPS bytesRead -- "+bytesRead);
				} catch (IOException e) {
					e.printStackTrace();
					bytesRead = -1;
				}
			}
			tmpLine = lastLine;
			lastLine = null;
			if (bytesRead == -1) {
				flag = false;
				dataServerNIO.removeKeyInReadSet(key);
				System.out.println("HandleGPSNIOReadThread bytesRead == -1, dataServerNIO.removeKeyInReadSet(key):"+remoteAddr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			tmpLine = null;
			buffer = null;
			flag = false;
			dataServerNIO.removeKeyInReadSet(key);
		}
	}
	public boolean handleLines(String content) {
		if(content == null )
			return false ;
		if(GNSSFrame.myStations == null ){
			GNSSFrame.myStations = new Hashtable<String , BaseStation>();
		}
		BaseStation bs = null ;
		String[] lines = content.split("\r\n");
		for (int i = 0; i < lines.length; i++) {
//			System.out.println("line "+i+" -- "+lines[i]);
			if (Config.isGPSSysOut) {
				System.out.println("line "+i+" -- "+lines[i]);
			}
			String[] messages = StringHelper.getStringArray(lines[i], GPSMetaData.dataSeperator) ;
			if(messages == null  || messages.length < 8 ){
				continue;
			}
			int stationIdIdx = GPSMetaData.stationIdIdx;
			String stationId = messages[stationIdIdx];
			//添加过滤器，过滤非指定id的台站数据
			if (!StaticMetaData.getStationOriginalLocs().containsKey(stationId)) {
				System.out.println(stationId + "台站不存在");
				continue;
			}
			//写入GPS数据文件
			if (Config.isGPSFiled) {
				try {
					Date tt = formatS.parse(messages[GPSMetaData.timeIdx]);
					boolean saveRes = GPSFileIOHelper.saveGPSLine(stationId, tt, lines[i]);
					if (!saveRes) {
						System.out.println("当前GPSLine持久化存储出错：："+lines[i]);
					}
				} catch (ParseException e) {
					System.out.println("curLine:"+lines[i]);
					System.out.println("messages[GPSMetaData.timeIdx]::"+messages[GPSMetaData.timeIdx]);
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(GNSSFrame.myStations.containsKey(stationId)){
				bs = GNSSFrame.myStations.get(stationId) ;
			}
			else {
				bs = new GNSSStation(stationId).getStationInstance() ;
				if (bs == null) {
					System.out.println("台站"+stationId+"不存在");
					continue;
				}
				GNSSFrame.myStations.put(stationId, bs) ;
			}
			boolean res = bs.handleMyMessage(messages) ;
			if (!res) {
				System.out.println(lines[i]);
				continue;
			}
			messages= null ;
		}
		for (int i = 0; i < lines.length; i++) {
			lines[i] = null;
		}
		lines = null;
		return true;
	}
	public HandleGPSNIOReadThread(SelectionKey key,int BUF_SIZE, DataServerNIO dataServerNIO) {
		this.key = key;
		this.BUF_SIZE = BUF_SIZE;
		this.dataServerNIO = dataServerNIO;
	}
}
