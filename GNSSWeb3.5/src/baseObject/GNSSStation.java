package baseObject;

 

import metaData.StationMetaData;

public class GNSSStation extends BaseStation {

	public GNSSStation(String id) {
		//super(id, n, p);
		super(id);
		myLocation = StationMetaData.stationOriginalLocs.get(id);
	}

	@Override
	public void tickAction() {
	 
		
	}

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleMyMessage(String message) {
		// TODO Auto-generated method stub
		return false;
	}


}
