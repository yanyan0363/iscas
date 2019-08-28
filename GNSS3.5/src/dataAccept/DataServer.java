package dataAccept;

import java.net.ServerSocket;

import metaData.StaticMetaData.DAType;
import utils.SystemHelper;

public abstract class DataServer extends Thread{
	ServerSocket myServerSocket = null ;
	int port;
//	DAType type;
	public abstract void doListenTCP();//多线程接收
//	public abstract void doListenTCPST();//单线程接收
	public DataServer(int port) {
		this.port = port;
//		this.type = type;
	}
	public void run(){
		try{
			SystemHelper.writeLog("------start taskmgr work thread------");
			if(!createSocket()){
				SystemHelper.writeLog("------failed to create work thread------");
				return ;
			}
			doListenTCP() ;
//			if (type == DAType.MTAccept) {//多线程接收
//				doListenTCPMT() ;
//			}else if (type == DAType.STAccept) {//单线程接收
//				//重写单线程接收程序
//				doListenTCPST();	
//			}
		}catch(Exception exp){
			
		}
	}
	 
	 public boolean  createSocket(){
			try{
				if(myServerSocket!=null  &&  myServerSocket.isClosed() == false )
				{
					SystemHelper.writeLog("------the TCP listen has already been created------");
					return true; 
				}
				 myServerSocket = new ServerSocket(port); 
				 SystemHelper.writeLog("------the TCP listen is created------");
				 return true ;
			}
			catch(Exception exp){
				
			}
			return false ;
		}
}
