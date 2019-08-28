package baseObject;

import beans.GPSData;
import metaData.StationMetaData;

public abstract class BaseStation {

	public String ID = "" ;
	public GPSData myLocation = null ;
//	public DataCache    myDataCache = null ;//注意 这里全部的位移量都是相对初始点的绝对位移量
	public StationMetaData  myMetaData = null ;
	public boolean isActive = false ;
	//now
	public BaseStation(String id){
		this.ID = id ;
	}
	public void setActive(boolean s){
		this.isActive = s ;
	}
	public abstract boolean init();
	public abstract void tickAction();
	public abstract boolean handleMyMessage(String message) ;

    public static BaseStation getStationInstance(String id){
    	GNSSStation gs = new GNSSStation(id) ;
    	return gs ;
    	
    }
    public  void setLocation(String[] messages){
    	
    }
		
	 
}
