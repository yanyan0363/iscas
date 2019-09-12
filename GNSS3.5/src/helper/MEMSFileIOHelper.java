package helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import utils.Config;

public class MEMSFileIOHelper {

//	private static Map<String, FileWriter> fileIO = new HashMap<>();
	private static ConcurrentHashMap<String, FileWriter> fileIO = new ConcurrentHashMap<>();
	private static String MEMSFolder = Config.MEMSFolder;
	private static File folder = new File(MEMSFolder);
//	private static String day = "20171208";
	private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	static{
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
//				System.out.println("TimerTask...");
				checkFileIO();
			}
		};
        Timer timer = new Timer();
        long period = Config.fileIOClearTime;//10min
        //每天的date时刻执行task，每隔10min重复执行
        timer.schedule(task, 60000, period);
	}
	public static boolean saveMEMSLine(String stationID, Date time, String memsLine) {
//		String fileName = stationID+"_"+format.format(new Date());
		String fileName = mkFileName(stationID, time);
		FileWriter fWriter = null;
		synchronized (fileIO) {
			if (!fileIO.containsKey(fileName)) {
				fWriter = initFileWriter(stationID, fileName);
			}else {
				fWriter = fileIO.get(fileName);
			}
			if (fWriter == null) {
				System.out.println("MEMS数据文件输入流为null");
				fWriter = initFileWriter(stationID, fileName);
			}
			while (true) {
				try {
					fWriter.write(memsLine+"\r\n");
					fWriter.flush();
					break;
				} catch (IOException e) {
					e.printStackTrace();
					fWriter = initFileWriter(stationID, fileName);
				}
			}
			fileIO.notifyAll();
		}
		memsLine = null;
		fWriter = null;
		fileName = null;
		return true;
	}
	private static String mkFileName(String stationID, Date time) {
		return stationID+"_"+initTimeSuffix(time);
	}
	private static String initTimeSuffix(Date time) {
		int mIdx = time.getMinutes()/5;//每5min一个文件
		int hours = time.getHours();
		String mIdxStr = "";
		String hoursStr = "";
		if (mIdx < 10) {
			mIdxStr = "0" + mIdx;
		}else {
			mIdxStr = mIdx + "";
		}
		if (hours < 10) {
			hoursStr = "0" + hours;
		}else {
			hoursStr = hours + "";
		}
		return format.format(time)+"_"+hoursStr+"_"+mIdxStr;
	}
	public static void checkFileIO(){
//		System.out.println("before checkFileIO, "+fileIO.size()+"::"+fileIO.keySet());
		String now = initTimeSuffix(new Date());
		Iterator iterator = fileIO.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, FileWriter> item = (Entry<String, FileWriter>) iterator.next();
			String key = item.getKey();
//			System.out.println("now::"+now+", " + key.substring(key.lastIndexOf("_")+1)+", " + now.compareTo(key.substring(key.lastIndexOf("_")+1)));
			if (now.compareTo(key.substring(key.indexOf("_")+1))>0) {
				FileWriter fw= item.getValue();
				synchronized (fw) {
					try {
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					fw=null;
				}
				try {
					iterator.remove();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		try{
			
			System.gc();
		}
		catch(Exception exp){
			exp.printStackTrace();
		}
//		System.out.println("after checkFileIO, "+fileIO.size()+"::"+fileIO.keySet());
	}
	public static void main(String[] args) {
		String d1 = "20171208";
		String d2 = "20171208";
		String d3 = "20171209";
		String d4 = "20171207";
		System.out.println(d1.compareTo(d2));
		System.out.println(d1.compareTo(d3));
		System.out.println(d1.compareTo(d4));
		String key="JXKC_19_20171208";
		System.out.println(key.substring(key.lastIndexOf("_")+1));
		System.out.println(d2.compareTo(key.substring(key.lastIndexOf("_")+1)));
		System.out.println(new Date().getMinutes()/5);
		key = "JXKC12_20171222_11_11";
		String now = initTimeSuffix(new Date());
		System.out.println(now);
		System.out.println(now.compareTo(key.substring(key.indexOf("_")+1))>0);
	}
	public static boolean closeAllFileIO(){
		Collection<FileWriter> fileIOs = fileIO.values();
		for (Iterator iterator = fileIOs.iterator(); iterator.hasNext();) {
			FileWriter fileWriter = (FileWriter) iterator.next();
			try {
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	private static FileWriter initFileWriter(String stationID, String fileName){
		if (!folder.exists()) {
			folder.mkdirs();
		}
		if (!folder.isDirectory()) {
			System.out.println("存储文件夹不存在");
			return null;
		}
		File folderFile = new File(MEMSFolder+stationID);
		if (!folderFile.exists()) {
			folderFile.mkdirs();
		}
		File file = new File(MEMSFolder+stationID+"/"+fileName+".txt");
		try {
			if (!file.exists()) {
				if (!file.createNewFile()) {
					System.out.println("创建MEMS存储文件失败，文件名称：" + file.getName());
					return null;
				}
			}
			FileWriter fWriter = new FileWriter(file,true);
			fileIO.put(fileName, fWriter);
			file = null;
			return fWriter;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
