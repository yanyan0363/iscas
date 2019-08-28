package dataCache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import beans.Loc;
import beans.MEMSData;

public class DispWithMEMS extends Displacement {

	public List<MEMSData> memsDataList = new ArrayList<>();
	private DispWithMEMS() {
		
	}
	public void dispose(){
		super.dispose();
		if(memsDataList != null){
//			for (int i = 0; i < memsDataList.size(); i++) {
//				memsDataList.get(i).dispose();
//				memsDataList.remove(i);
//			}
			while(memsDataList.size()>0){
				MEMSData d = memsDataList.get(0);
				memsDataList.remove(0);
				d.dispose();
				d= null ;
			}
			memsDataList.clear();		    
		}
		memsDataList = null ;
	}
	public DispWithMEMS(DataCache c,Date time) {
		this.myCache = c;
		this.time = time;
		this.localTime = new Date();
		this.isDispExist = false;
	}
	
//	public DispWithMEMS(DataCache c, double xDisplacement, double yDisplacement, double zDisplacement, double zDisToLastDis, Date t, Date localTime){
//		this.myCache = c;
//		this.xDisplacement = xDisplacement;
//		this.yDisplacement = yDisplacement;
//		this.zDisplacement =  zDisplacement; 
//		this.zDisToLastDis = zDisToLastDis;
//		this.displacement2D = Math.sqrt(xDisplacement*xDisplacement+yDisplacement*yDisplacement) ;
//		this.time = t; 
////		this.loc = loc;
//		this.localTime = localTime;
////		if(c!= null )
////			c.addDisplacement(this);
//	}
//	public DispWithMEMS(Date time, MEMSData memsData){
//		this.time = time;
//		this.localTime = new Date();
//		this.memsDataList.add(memsData);
//		this.isDispExist = false;
//	}
	
}
