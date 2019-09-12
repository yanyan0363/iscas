package helper;

import java.util.Date;
import java.util.Enumeration;
import baseObject.BaseStation;
import event.EQEvent;
import event.GPSEvent;
import mainFrame.GNSSFrame;

public class EQEventHelper {


//	public static boolean addOrUpdateScene(GPSEvent event) {
//		if (GNSSFrame.eqEvents == null ) {
//			GNSSFrame.eqEvents = new Hashtable<>();
//		}
//		EQEvent curScene = evtBelongsTo(event);
//		if (curScene == null) {
//		
//			curScene = new EQEvent(event.generateSceneKey(), event.station, event.snapshot.localTime);
//			GNSSFrame.eqEvents.put(curScene.getEqEventID(), curScene);
//			//System.out.println("new evt no connected !! " + event.station.ID + " create new scene " +curScene.getSceneID() );
//		 
//		}
//		return curScene.updateEvt(event);
//	}
	public static void showInfo(){
		if(GNSSFrame.eqEvents == null )
			return ;
		System.out.println("------------------------------------------------");
		for(String key: GNSSFrame.eqEvents.keySet()){
			System.out.println("scene " + key );
		}
		System.out.println("------------------------------------------------");
	}
	
	/**
	 * 获取当前事件所属的EQEvent,若所属EQEvent不存在，则返回null
	 * @param event
	 * @return
	 */
	public static synchronized EQEvent evtBelongsTo(GPSEvent event) {
		EQEvent scene = null;
		Enumeration<String> keys = GNSSFrame.eqEvents.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			scene = GNSSFrame.eqEvents.get(key);
			//时间&&空间 满足阈值条件
			boolean here1 = scene.isInTimeZone(event.snapshot.time);
			boolean here2 = scene.isInSpatialZone(event.station);
			 
			if (here1 && here2) {
				return scene;
			}
		}
		return null;
	}
	/**
	 * 获取当前time，station所属的EQEvent，若不存在，则返回null
	 * @param time
	 * @param station
	 * @return
	 */
	public static EQEvent evtBelongsTo(Date time, BaseStation station) {
		EQEvent eqEvent = null;
		Enumeration<String> keys = GNSSFrame.eqEvents.keys();
		synchronized (keys) {
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				eqEvent = GNSSFrame.eqEvents.get(key);
				//时间&&空间 满足阈值条件
				boolean here1 = eqEvent.isInTimeZone(time);
				boolean here2 = eqEvent.isInSpatialZone(station);
				System.out.println("isInTimeZone:"+here1+", isInSpatialZone:"+here2);
				if (here1 && here2) {
					keys.notifyAll();
					return eqEvent;
				}
			}
			keys.notifyAll();
		}
		return null;
	}
	/**
	 * 计算震中点
	 * @param eventList 事件列表
	 * @return
	 */
//	public static GKPoint calEpicenter(List<GPSEvent> eventList) {
//		if (eventList == null || eventList.size() != 3) {
//			utils.SystemHelper.writeLog("event列表为空或数量有误.");
//			return null;
//		}
//		List<GKPoint> points = new Vector<>();
//		List<Long> times = new Vector<>();
//		for (int i = 0; i < eventList.size(); i++) {
//			points.add(eventList.get(i).snapshot.loc.gkPoint);
//			times.add(eventList.get(i).snapshot.time.getTime());
//			
//			System.out.println("p " +eventList.get(i).station.ID );
//			System.out.println("t " +eventList.get(i).snapshot.time.getTime() );
//		}
//		GKPoint epi = new Epicenter2D(points, times).getEpicenter(Config.epiCalTime);
//		System.out.println("epi:"+epi.toString());
//		return epi;
//	}
	
}
