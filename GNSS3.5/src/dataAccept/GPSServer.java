package dataAccept;

import java.net.Socket;
import utils.Config;

public class GPSServer extends DataServer{

	public GPSServer(int port) {
		super(port);
	}
	//	 int port = 9009 ;
	 public static void main(String[] args) {
		GPSServer server =  new GPSServer(Config.GNSSDataAcceptServerPort);
		server.start();
	}
	 public void doListenTCP(){
		while(true){
			try{
				Socket client = myServerSocket.accept(); 
				HandleGPSSocket handleGPSSocket = new HandleGPSSocket(client);
				handleGPSSocket.start();
			}catch(Exception exp){
				exp.printStackTrace();
			}
		}
	}
//	@Override
//	public void doListenTCPST() {
//		HandleMultiGPSSocket multiGPSSocket = new HandleMultiGPSSocket();
//		multiGPSSocket.start();
//		while(true){
//			try{
//				Socket client = myServerSocket.accept(); 
//				multiGPSSocket.addClient(client);
//			}catch(Exception exp){
//				exp.printStackTrace();
//			}
//		}
//	}
//	@Override
//	public void doListenTCPST() {
//		int recvMsgSize = 0;
//		byte[] recvBuf = new byte[1024];
//		while (true) {
//			try {
//				Socket client = myServerSocket.accept();
//				SocketAddress address = client.getRemoteSocketAddress();
//				System.out.println("Handling client at " + address);
//				InputStream in = client.getInputStream();
//				while ((recvMsgSize=in.read())!=-1) {
//					byte[] tmp = new byte[recvMsgSize];
//					System.arraycopy(recvBuf, 0, tmp, 0, recvMsgSize);
//					System.out.println(new String(tmp));
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//		}
//		
//	}
}
