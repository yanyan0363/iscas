package mainFrame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.util.StringUtil;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

import arcgis.ArcgisShpHelper;
import arcgis.pyHelper.ArcgisPyHelper;
import baseObject.BaseStation;
import beans.slip.GifHelper;
import beans.slip.MyDcgrn;
import beans.slip.MyDspmdl;
import beans.slip.grid;
import beans.slip.stationdata;
import dbHelper.GNSSDBHelper;
import event.EQEvent;
import metaData.StaticMetaData;
import metaData.StaticMetaData.EQPointType;
import slipHelper.FortSlipHelper;
import slipHelper.FortranLibrary;
import utils.Config;
import utils.StringHelper;


public class EndDealThread extends Thread{
	
//	public static void main(String[] args) {
//		GreenThread greenThread = new GreenThread();
//		greenThread.start();
//	}
	private EQEvent eqEvent = null;
	
	
	
	public EndDealThread(EQEvent eqEvent) {
		this.eqEvent = eqEvent;
	}
	@Override
	public void run() {
		if (eqEvent.triggerSt == null || eqEvent.triggerSt.size() < 5) {
			System.out.println("触发台站异常，线程启动失败. eqEvent.triggerSt.size():"+eqEvent.triggerSt.size());
			return;
		}
		System.out.println("线程启动 eqEvent.triggerSt.size():"+eqEvent.triggerSt.size());
		if (Config.isGreen) {
			for (int i = 0; i < 5; i++) {
				tNodeSlip(
						Config.slipParamFolder, 
						eqEvent.eqInfo.getEQID()+"_"+(new Date().getTime()-eqEvent.eqInfo.getEqTime().getTime())/1000
						);
			}
			//生成gif
			boolean res = GifHelper.ExportSlipGif(eqEvent.eqInfo.getEQID());
			System.out.println("生成gif：" + res);
		}
		
		eqEvent.endOp();
		
	}
	public boolean tNodeSlip(String slipFolder, String eqTime) {
		System.out.println(eqTime);
		Date startT = new Date();
		boolean res = callFortran2018(slipFolder,eqTime);
		Date endT = new Date();
		long tt = endT.getTime() - startT.getTime();
		System.out.println("dll计算 耗时：：" + tt + "ms");
		return res;
	}
	public boolean callFortran2018(String folder,String fileName) {
//		int NSMAX = 1, NPSMAX = 40, NOBSMAX = 40;
		//stationDataArray
		stationdata[] stationDataArray = initStationDataArray();
		
		try{
			Date startT = new Date();
		    StaticMetaData.getDll().startdll(stationDataArray, StaticMetaData.getGridArray(), StaticMetaData.getDspmdls(), StaticMetaData.getDcgrns(), fileName, new IntByReference(fileName.length()), folder, new IntByReference(folder.length()));
		    Date endT = new Date();
			long tt = endT.getTime() - startT.getTime();
		    return true;
		}
		catch(Exception exp){
			exp.printStackTrace();
		}
		return false ;
	}
	
	private stationdata[] initStationDataArray(){
		stationdata[] stationDataArray = (stationdata[]) new stationdata().toArray(42);
		File ns = new File("D:/workspace/GNSSWorkspace/Fortrantest/src/data/coseismic-ns.txt");
		File ew = new File("D:/workspace/GNSSWorkspace/Fortrantest/src/data/coseismic-ew.txt");
		File ud = new File("D:/workspace/GNSSWorkspace/Fortrantest/src/data/coseismic-ud.txt");
		try {
			FileReader nsReader = new FileReader(ns);
			BufferedReader nsBufferedReader = new BufferedReader(nsReader);
			FileReader ewReader = new FileReader(ew);
			BufferedReader ewBufferedReader = new BufferedReader(ewReader);
			FileReader udReader = new FileReader(ud);
			BufferedReader udBufferedReader = new BufferedReader(udReader);
			try {
				for (int i = 0; i < 42; i++) {
					String pp = nsBufferedReader.readLine();
					while (pp.contains("  ")||pp.contains("	")) {
						pp = pp.replaceAll("  ", " ");
						pp = pp.replaceAll("	", " ");
					}
					String[] ss = pp.split(" ");
					stationDataArray[i].lat = Double.parseDouble(ss[0]);
					stationDataArray[i].lon = Double.parseDouble(ss[1]);
					stationDataArray[i].ns = Double.parseDouble(ss[2]);
					
					pp = ewBufferedReader.readLine();
					while (pp.contains("  ")||pp.contains("	")) {
						pp = pp.replaceAll("  ", " ");
						pp = pp.replaceAll("	", " ");
					}
					ss = pp.split(" ");
					stationDataArray[i].ew = Double.parseDouble(ss[2]);
					pp = udBufferedReader.readLine();
					while (pp.contains("  ")||pp.contains("	")) {
						pp = pp.replaceAll("  ", " ");
						pp = pp.replaceAll("	", " ");
					}
					ss = pp.split(" ");
					stationDataArray[i].ud = Double.parseDouble(ss[2]);
//			System.out.println(stationDataArray[i].lat+","+stationDataArray[i].lon+", "+stationDataArray[i].ns+","
//			+stationDataArray[i].ew+", "+stationDataArray[i].ud);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					nsBufferedReader.close();
					ewBufferedReader.close();
					udBufferedReader.close();
					nsReader.close();
					ewReader.close();
					udReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return stationDataArray;
	}
}
