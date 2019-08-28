package dataAccept;

import java.net.Socket;

import metaData.StaticMetaData.DAType;
import utils.Config;

public class MEMSServer extends DataServer{

	 public MEMSServer(int port) {
		 super(port);
	}
	public static void main(String[] args) {
		MEMSServer server =  new MEMSServer(Config.MEMSDataAcceptServerPort);
		server.start();
	}
	@Override
	public void doListenTCP(){
		while(true){
			try{
				Socket client = myServerSocket.accept(); 
				HandleMEMSSocket handleMEMSSocket = new HandleMEMSSocket(client);
				handleMEMSSocket.start();
			}catch(Exception exp){
				exp.printStackTrace();
			}
		}
	}
//	@Override
//	public void doListenTCPST() {
//		// TODO Auto-generated method stub
//		
//	}

}
