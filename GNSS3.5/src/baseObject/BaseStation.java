package baseObject;

import java.util.Date;

import beans.Loc;
import beans.MEMSData;
import dataCache.DataCache;
import dataCache.Displacement;

public abstract class BaseStation {

	public String ID = "" ;
//	public String producer = "" ;
//	public String name = "" ;
//	public GPSData myLocation = null ;
	public Loc myLocation = null ;
//	protected double zDis = 0.0;//地心距离
	public DataCache myDataCache = new DataCache(this) ;//注意 这里全部的位移量都是相对初始点的绝对位移量
//	public StationMetaData  myMetaData = null ;
	public boolean isActive = false ;//是否触发地震
//	public boolean isMEMSMagCaled = false;//MEMS计算震级是否完成
	//now
	public BaseStation(String id){
		this.ID = id ;
	}
	public void setActive(boolean s){
		this.isActive = s ;
	}
	public abstract boolean init();
	public abstract Displacement addData(String[] dataline) ;
	public abstract boolean handleMyMessage(String[] dataline) ;
//	public abstract displacement getHStartUpDisplacement() ;
	public abstract Displacement getVStartUpDisplacement() ;
	public abstract String curDisplacement();
	public abstract String curLast2MinDisp();
	public abstract String curDispWithMEMS();
	public abstract String curLast2MinDispWithMEMS();
	public abstract String curLastXMinDispWithMEMS(int x);
//	public abstract String curLastXMinDispWithoutMEMS(int x);
	public abstract String curLastXMinDispWithMEMSWithoutFitting(int x);
	public abstract String curDis2DZ();
	public abstract String last30Dis2DZ();//前30个数据
	public abstract String last30sDis2DZ();//前30s数据
	public abstract String curDis2DZ(Date startTime, Date endTime);
	public abstract double getVMaxDis();

//	public abstract boolean insertNewStation(String[] messages);
	public abstract BaseStation getStationInstance();

	
	public Displacement getLastData(){
		return this.myDataCache.getLastData();
	}
}
