package mainFrame;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.TimerTask;

import com.esri.arcgis.arcmapui.MapIlluminationPropertyPage;

import baseObject.BaseStation;
import dataCache.Displacement;
import dbHelper.GNSSDBHelper;
import event.EQEvent;
import event.GPSEvent;
import helper.EQEventHelper;
import tcp.ShortSender;
import utils.Config;

public class AnalysisTimerTask extends TimerTask{
	public AnalysisTimerTaskRunner myTask= null ;
	@Override
	public void run() {
		long t1 = System.nanoTime()/1000000;
		if(myTask == null )
			return ;
		try{
		      runTickJob();
		}
		catch(Exception exp){
			System.out.println("timer eq checker error");
			exp.printStackTrace();
		}
		try{
			maintainEQEvent();
		}
		catch(Exception exp){
			System.out.println("timer evt worker error");
			exp.printStackTrace();
		}
		long t2 = System.nanoTime()/1000000;
		System.out.println("                                                                 AnalysisTimerTask 耗时 -- " + (t2-t1)+"ms");
	
		System.gc();
	}
	public void runTickJob(){
		Hashtable<String , BaseStation> stations = GNSSFrame.myStations;
		Enumeration<String> keys = stations.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			BaseStation curStation = stations.get(key);
			if (curStation.isActive) {
				continue;
			}else{
				//here changed, remember to get back!!!
//				if (curStation.myDataCache.isTriggeredDesc()) {
				if (curStation.myDataCache.isTriggeredAsc()) {
//				if(false){
					curStation.isActive = true;
					EQEvent event = EQEventHelper.evtBelongsTo(curStation.myDataCache.triggerTime, curStation);
					if (event == null) {
//						event = new EQEvent(curStation.ID+"-"+curStation.myDataCache.triggerTime.getTime(), curStation, curStation.myDataCache.triggerTime);
						event = new EQEvent(curStation.ID+"-"+curStation.myDataCache.triggerTime.getTime(), curStation, new Date());
						System.out.println("New EQEvent:" + event.getEqEventID());
						synchronized (event.triggerSt) {
							event.triggerSt.add(curStation);
//							event.f3TriggerStDate.put(curStation, new Date());
							event.triggerSt.notifyAll();
						}
						GNSSFrame.eqEvents.put(event.getEqEventID(), event);
						//here...首台信息发送至SD，待添加
//						new ShortSender(Config.SDIP, Config.SDPort).send("this is a trigger msg.");//发送首台信息至SD平台接口
						//首台出速报1
						boolean areportRes = event.createAReport(curStation);
						System.out.println("速报1："+areportRes);
					}else {
						if (!event.triggerSt.contains(curStation)) {
							synchronized (event.triggerSt) {
								event.triggerSt.add(curStation);
								if (event.triggerSt.size() != 5) {
									boolean areportRes = event.createAReport(curStation);
									System.out.println("速报："+areportRes);
								}
								event.triggerSt.notifyAll();
							}
						}
						if (event.triggerSt.size() == 5 && !event.isEndDealed()) {
							event.updateEpiOriginTimeMagnitude();
							boolean areportRes = event.createAReport(curStation);
							System.out.println("速报：" + areportRes);
							event.endDeal();
						}
					}
				}
			}

//			long t1 = System.nanoTime()/1000000;
			if (curStation.isActive) {
				continue;
			}else {
				curStation.myDataCache.maintainTimeWindow();
			}
//			long t2 = System.nanoTime()/1000000;
//			System.out.println("       AnalysisTimerTask.runTickJob() maintainTimeWindow -- "+(t2-t1)+"ms");
		}
	 
		keys=null ;
		stations=null; 
	 
	}
	/**
	 * 若触发台站<5,且触发时长>EQEndTime,且isEndDealed==false,则移除putEqEventNotActive
	 */
	private void maintainEQEvent() {
		EQEvent eqEvent = null;
		Enumeration<String> keys = GNSSFrame.eqEvents.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			eqEvent = GNSSFrame.eqEvents.get(key);
			if (eqEvent.triggerSt.size() < 5 && new Date().getTime() > (eqEvent.getFirstTriggerTime().getTime() + Config.EQEndTime*60*1000) && eqEvent.isEndDealed()==false) {
				GNSSFrame.eqEvents.remove(key);
				eqEvent.putEqEventNotActive();
				key = null;
				eqEvent = null;
			}
		}
	}
}
