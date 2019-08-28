package metaData;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import beans.GPSData;

public class StationMetaData {

	public String producer = "" ;
	public Element myE = null ;
	
	public int   xIdx = 2 ;
	public int   yIdx = 3 ;
	public int   zIdx = 4 ;
	public int timeIdx = 1 ;
	public String dataSeperator = "	" ;
	public static Map<String, GPSData> stationOriginalLocs = new HashMap<>();
	static{
		stationOriginalLocs.put("51BXY", new GPSData(-1228223.212991, 5362704.159926, 3218975.250421, utils.SystemDefines.pointTypeDef.Coordinate));
		stationOriginalLocs.put("51CXQ", new GPSData(-1489898.097401, 5221506.776842, 3335802.972503, utils.SystemDefines.pointTypeDef.Coordinate));
		stationOriginalLocs.put("51DXY", new GPSData(-1286793.798338, 5341724.878250, 3229209.471844, utils.SystemDefines.pointTypeDef.Coordinate));
		stationOriginalLocs.put("51GYS", new GPSData(-1474586.793864, 5199819.491592, 3376759.573996, utils.SystemDefines.pointTypeDef.Coordinate));
		stationOriginalLocs.put("51GYZ", new GPSData(-1491606.201238, 5166877.049366, 3420150.103630, utils.SystemDefines.pointTypeDef.Coordinate));
	}
}
