package tcp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ShortSender {

	public static void main(String[] args) {
		ShortSender sender = new ShortSender("192.168.137.35", 9999);
		sender.send("this is a test msg.");
	}
	protected String targetIP;
	protected int targetPort;
	private Socket socket=null ;
	private OutputStream outputStream= null;
	
	public ShortSender(String targetIP, int targetPort) {
		this.targetIP = targetIP;
		this.targetPort = targetPort;
	}
	
	public boolean send(String msg){
		if(msg == null || msg.length() <=0 ){
			System.out.println("发送内容为空...");
			return false;
		}
		try {
			socket = new Socket(targetIP, targetPort);
			outputStream=socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("TCP连接建立失败 @ " + targetIP + ":" + targetPort);
			return false;
		}
		try {
			outputStream.write(msg.getBytes());
			outputStream.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("发送失败...");
			return false;
		}finally {
			try {
				outputStream.close();
				outputStream = null;
				socket.close();
				socket = null;
			} catch (IOException e) {
				e.printStackTrace();
				outputStream = null;
				socket = null;
			}
		}
	}
	
	/**
	* 判断是否断开连接，断开返回true,没有返回false
	* @param socket
	* @return
	*/ 
	public Boolean isRemoteServerClose(){ 
	   try{ 
		   socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信 
		   return false; 
	   }catch(Exception se){ 
		   System.out.println("远程连接已断开...");
		   return true; 
	   } 
	} 
	
}
