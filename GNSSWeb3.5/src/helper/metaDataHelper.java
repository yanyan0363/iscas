package helper;

import java.util.Hashtable;

import metaData.StationMetaData;

public class metaDataHelper {

	public static Hashtable<String, StationMetaData> stationMetas = new Hashtable<String, StationMetaData> ();
	public static  void addStationMeta(StationMetaData m){
		if(m == null )
			return ;
		 stationMetas.put(m.producer,m) ;
		 
	}
}
