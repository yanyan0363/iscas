package dataAccept;

import java.nio.channels.SelectionKey;

import utils.Config;

public class MEMSServerNIO extends DataServerNIO{

	 public MEMSServerNIO(int port) {
		 super(port);
	}
	public static void main(String[] args) {
		MEMSServerNIO server =  new MEMSServerNIO(Config.MEMSDataAcceptServerPort);
		server.start();
	}
	@Override
	public boolean handleRead(SelectionKey key,int BUF_SIZE) {
		HandleMEMSNIOReadThread memsNIOReadThread = new HandleMEMSNIOReadThread(key, BUF_SIZE,this);
		memsNIOReadThread.start();
		return true;
	}
	
	
}
