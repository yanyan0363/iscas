package event;

import baseObject.BaseStation;
import beans.MEMSData;

public class MEMSEvent {

	public MEMSData snapshot;//数据快照
	public BaseStation station;//台站
	
	public MEMSEvent(MEMSData snapshot,BaseStation station) {
		this.snapshot = snapshot;
		this.station = station;
	}
	
	public String generateSceneKey() {
		return station.ID+"-"+snapshot.time.getTime();
	}
	
}
