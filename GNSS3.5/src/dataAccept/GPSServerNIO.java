package dataAccept;

import java.nio.channels.SelectionKey;

public class GPSServerNIO extends DataServerNIO{

	public GPSServerNIO(int port) {
		super(port);
	}

	@Override
	public boolean handleRead(SelectionKey key, int BUF_SIZE) {
		HandleGPSNIOReadThread gpsNIOReadThread = new HandleGPSNIOReadThread(key, BUF_SIZE,this);
		gpsNIOReadThread.start();
		return true;
	}
}
