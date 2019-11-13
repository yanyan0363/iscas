package metaData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.esri.arcgis.cartoUI.TableHistogram;
import com.sun.jna.Native;

import arcgis.ArcgisEQHelper;
import beans.Loc;
import beans.slip.MyDcgrn;
import beans.slip.MyDspmdl;
import beans.slip.grid;
import slipHelper.FortranLibrary;
import utils.Config;

public class StaticMetaData {

	public static enum FileAnalyseTaskState{
		start,
		end
	}
	public static enum GNSSMode{
		AUTO,
		MANUAL
	}
	public enum EQPointType {station, epicenter};
	public enum GPSTaskStatus {start, end, failed};//GPS数据请求的状态：成功，结束，失败
	
	public enum DAType {
		MTAccept,//多线程接收 
		STAccept//单线程接收(NIO)
	};
	
	public static SimpleDateFormat formatMs = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS"); 
	public static SimpleDateFormat formatS = new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss");
//	SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
//	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss");
	
	private static String SDEvtID = "";//向SD发送的evtID
	private static Set<String> SDStIDSet = new HashSet<>();//向SD发送的StIDSet
	
	public static int S = (int)(Config.MEMSHz * Config.stw);//短窗口
	public static int L = (int)(Config.MEMSHz * Config.ltw);//长窗口
	public static double dSNR = Math.pow(10, (Config.SNR/10));
	public static double thresh = (dSNR+1)/(dSNR*Config.stw/Config.ltw+1);//阈值
	
	private static Map<String, Loc> stationOriginalLocs = new HashMap<>();
	private static MyDspmdl[] dspmdls = null;
	private static MyDcgrn[] dcgrns = null;
	private static grid[] gridArray = null;
	private static FortranLibrary dll = (FortranLibrary)Native.loadLibrary("FortranLibrary", FortranLibrary.class);
	
	public static void initStaticData() {
		initStations();
		//init slip params
		if (Config.isGreen) {
			initDspmdl();
			initDcgrn();
			initGridArray();
		}
	}
	/**
	 * 初始化台站的位置信息
	 */
	private static void initStations(){
		ArcgisEQHelper.initStations();//初始化台站位置信息
	}
	private static MyDspmdl[] initDspmdl(){
		dspmdls = (MyDspmdl[])new MyDspmdl().toArray(198*126);
//		File dspgrn = new File("D:/workspace/GNSSWorkspace/Fortrantest/src/data/dspgrn");
		File dspgrn = new File(Config.dspgrn);
		try {
			FileReader reader = new FileReader(dspgrn);
			BufferedReader bufferedReader = new BufferedReader(reader);
			int i=0, j=1;
			int lastsubID = 0;
			try {
				bufferedReader.readLine();
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					while (line.contains("  ")||line.contains("	")) {
						line = line.replaceAll("  ", " ");
						line = line.replaceAll("	", " ");
					}
					String[] ss = line.trim().split(" ");
					if (ss==null || ss.length==0) {
						continue;
					}
					dspmdls[i].subfaultid=(int)Double.parseDouble(ss[0]);
					if (lastsubID != dspmdls[i].subfaultid) {
						lastsubID = dspmdls[i].subfaultid;
						j = 1;
					}
					dspmdls[i].num=j++;
					dspmdls[i].dspmdl_str=Double.parseDouble(ss[9]);
					dspmdls[i].dspmdl_dip=Double.parseDouble(ss[10]);
					i++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					bufferedReader.close();
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return dspmdls;
	}
	private static MyDcgrn[] initDcgrn(){
		dcgrns = (MyDcgrn[])new MyDcgrn().toArray(198*198);
//		File smooth = new File("D:/workspace/GNSSWorkspace/Fortrantest/src/data/smoothing_grn.grn");
		File smooth = new File(Config.smooth);
		try {
			FileReader reader = new FileReader(smooth);
			BufferedReader bufferedReader = new BufferedReader(reader);
			int i=0, j=1;
			int lastsubID = 0;
			try {
				bufferedReader.readLine();
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					while (line.contains("  ")||line.contains("	")) {
						line = line.replaceAll("  ", " ");
						line = line.replaceAll("	", " ");
					}
					String[] ss = line.trim().split(" ");
					dcgrns[i].subfaultid1=(int)Double.parseDouble(ss[0]);
					if (lastsubID != dcgrns[i].subfaultid1) {
						lastsubID = dcgrns[i].subfaultid1;
						j = 1;
					}
					dcgrns[i].subfaultid2=j++;
					dcgrns[i].dcgrn_str1 = Double.parseDouble(ss[1]);
					dcgrns[i].dcgrn_str2 = Double.parseDouble(ss[2]);
					dcgrns[i].dcgrn_str3 = Double.parseDouble(ss[3]);
					dcgrns[i].dcgrn_str4 = Double.parseDouble(ss[4]);
					dcgrns[i].dcgrn_str5 = Double.parseDouble(ss[5]);
					dcgrns[i].dcgrn_str6 = Double.parseDouble(ss[6]);
					dcgrns[i].dcgrn_dip1 = Double.parseDouble(ss[7]);
					dcgrns[i].dcgrn_dip2 = Double.parseDouble(ss[8]);
					dcgrns[i].dcgrn_dip3 = Double.parseDouble(ss[9]);
					dcgrns[i].dcgrn_dip4 = Double.parseDouble(ss[10]);
					dcgrns[i].dcgrn_dip5 = Double.parseDouble(ss[11]);
					dcgrns[i].dcgrn_dip6 = Double.parseDouble(ss[12]);
					i++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					bufferedReader.close();
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return dcgrns;
	}
	private static grid[] initGridArray(){
		gridArray = (grid[])new grid().toArray(1*198*126);
//		File dspgrn = new File("D:/workspace/GNSSWorkspace/Fortrantest/src/data/dspgrn");
		File dspgrn = new File(Config.dspgrn);
		try {
			FileReader reader = new FileReader(dspgrn);
			BufferedReader bufferedReader = new BufferedReader(reader);
			int i=0,j=0;
			try {
				bufferedReader.readLine();
				j++;
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					if (j%126 != 1) {
						j++;
						continue;
					}
					while (line.contains("  ")||line.contains("	")) {
						line = line.replaceAll("  ", " ");
						line = line.replaceAll("	", " ");
					}
					String[] ss = line.trim().split(" ");
					//"gridid", "faultid", "subfaultid", "lat", "lon", "depth", "square", "strike", "dip"
					gridArray[i].faultid=1;
					gridArray[i].subfaultid=(int)Double.parseDouble(ss[0]);
					String gridid = gridArray[i].faultid+"_"+(int)gridArray[i].subfaultid;
					gridArray[i].gridid= String.format("%1$-11s",gridid);//11为字符串长度，尾部补充空格
//					System.out.println(gridArray[i].gridid);
					gridArray[i].lat = Double.parseDouble(ss[1]);
					gridArray[i].lon = Double.parseDouble(ss[2]);
					gridArray[i].depth = Double.parseDouble(ss[3]);
					gridArray[i].square = Double.parseDouble(ss[6]);
					gridArray[i].strike = Double.parseDouble(ss[7]);
					gridArray[i].dip = Double.parseDouble(ss[8]);
					i++;
					j++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					bufferedReader.close();
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return gridArray;
	}
	public static void updateStationOriginalLoc(String stationID, Loc loc) {
		addStationOriginalLoc(stationID, loc);
	}
	public static void addStationOriginalLoc(String stationID, Loc loc) {
		stationOriginalLocs.put(stationID, loc);
	}
	public static void deleteStationOriginalLoc(String stationID) {
		stationOriginalLocs.remove(stationID);
	}
	public static Map<String, Loc> getStationOriginalLocs() {
		return stationOriginalLocs;
	}
	public static MyDspmdl[] getDspmdls() {
		return dspmdls;
	}
	public static MyDcgrn[] getDcgrns() {
		return dcgrns;
	}
	public static grid[] getGridArray() {
		return gridArray;
	}
	public static FortranLibrary getDll() {
		return dll;
	}
	public static String getSDEvtID() {
		return SDEvtID;
	}
	public static void setSDEvtID(String sDEvtID) {
		SDEvtID = sDEvtID;
	}
	public static Set<String> getSDStIDSet() {
		return SDStIDSet;
	}
	public static void updateSDStIDSet(Set<String> sDStIDSet) {
		SDStIDSet.clear();
		sDStIDSet.addAll(sDStIDSet);
	}
}
