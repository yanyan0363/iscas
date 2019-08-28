package dataAccept;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import utils.Config;

public abstract class DataServerNIO extends Thread{
	private static final int BUF_SIZE = 2048;
	private static final int TIMEOUT = 1;
	int port;
//	public abstract boolean handleLines(String content);
	public abstract boolean handleRead(SelectionKey key,int BUF_SIZE);
//	ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);
	private Set<SelectionKey> readSet = new HashSet<>();
	
	public DataServerNIO(int port) {
		this.port = port;
	}
	public void removeKeyInReadSet(SelectionKey key){
		if (readSet.contains(key)) {
			System.out.println(" remove a channel run, readSet size:"+readSet.size());
			key.cancel();
			readSet.remove(key);
			System.out.println(" remove a channel done, readSet resize:"+readSet.size());
		}
	}
	public void run() {
		Selector selector = null;
		ServerSocketChannel serverSocketChannel = null;
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			while (true) {
				try {
					if (selector.select(TIMEOUT) == 0) {
						try{
							Thread.sleep(5);
						}
						catch(Exception exp){
							exp.printStackTrace();
						}
//					continue;
					}
					Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
					while (iterator.hasNext()) {
						SelectionKey key = iterator.next();
						if (!key.isValid()) {
							System.out.println("!key.isValid(), iterator.remove();");
						}else if (key.isAcceptable()) {
							handleAccept(key);
						}else if (key.isReadable() && !readSet.contains(key)) {
							readSet.add(key);
							System.out.println("读取连接, readSet.size():"+readSet.size());
							handleRead(key, BUF_SIZE);
						}
						iterator.remove();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void handleAccept(SelectionKey key){
		ServerSocketChannel channel = (ServerSocketChannel)key.channel();
		SocketChannel socketChannel=null;
//		System.out.println(" before register, key.isValid():"+key.isValid()+", key.isAcceptable():"+key.isAcceptable()+", key.isReadable():"+key.isReadable()+", key.isConnectable():" +key.isConnectable()+", key.isWritable():" +key.isWritable());
		try {
			socketChannel = channel.accept();
			socketChannel.configureBlocking(false);
			//socketChannel.configureBlocking(true);
			socketChannel.register(key.selector(), SelectionKey.OP_READ);
		 
			socketChannel.socket().getRemoteSocketAddress().toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
//		GPSServerNIO serverNIO = new GPSServerNIO(Config.GNSSDataAcceptServerPort);
//		serverNIO.start();
		MEMSServerNIO memsNIO = new MEMSServerNIO(Config.MEMSDataAcceptServerPort);
		memsNIO.start();
	}
}
