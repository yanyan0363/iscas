package event;

import baseObject.BaseStation;
import dataCache.Displacement;

public class GPSEvent {

	public Displacement snapshot;//数据快照
	public BaseStation station;//台站
	
	public GPSEvent(Displacement snapshot,BaseStation station) {
		this.snapshot = snapshot;
		this.station = station;
	}
	
	public String generateSceneKey() {
		return station.ID+"-"+snapshot.time.getTime();
	}
	
}
