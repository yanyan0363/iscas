package utils;

public class SystemDefines {

	public enum EQPointType {evt, epicenter};
	
	public enum pointTypeDef {none, LonLat, Coordinate} ;
	public enum cacheType{none, increment, full} ;
	public static int timeWindow = 60 * 10 ;//单位：s
	public static int barGramTickCounter = 60 ;
	
	//单位都是米
	public static double eventDistance = 50* 1000;//50公里范围内都看做是一个震区
	public static double eventTimeSpan = 1 * 60 * 60 ;//一天之内的都算一个灾情
	public static double followEventTimespan = 72* 60 * 60 ;//余震，三天内同一区域内的都算余震
	public static float PVelocity= 4000; 
	public static float SVelocity= 2000;
	
	//单位都是米
	public static double zDisplacementReconValue = 0.001; 
	public static double hDisplacementReconValue = 0.001; 
	
	public static double VSmoothBorder = 0.05; //判断当前点是否为位移量稳定极值点的约束边界值（当前点与后面点的位移量差都小于这个边界）
	public static double HSmoothBorder = 0.05; //判断当前点是否为位移量稳定极值点的约束边界值（当前点与后面点的位移量差都小于这个边界）

     public static String getPhaseID(long seconds){
    	 if(seconds<60)
    		 return "zero" ;
    	 if(  seconds < 60 * 15 && seconds >= 60 * 15-2)
    		 return "1st" ;
    	 
    	 if(seconds < 60 * 30 && seconds >= 60 * 30-2)
    		 return "2nd" ;
    	  
    	 if(seconds< 60 * 60 * 3 && seconds >= 60 * 60 * 3-2)
    		 return "3rd" ;
    	 if(seconds< 60 * 60 * 6&& seconds >= 60 * 60 * 6-2)
    		 return "4th" ;
    	 if(seconds < 60 * 60 * 12&& seconds >= 60 * 60 * 12-2)
    		 return "12th" ;
    	 if(seconds < 60 * 60 * 24&& seconds >= 60 * 60 * 24-2)
    		 return "1day" ;
    	 if(seconds < 60 * 60 * 48&& seconds >= 60 * 60 * 48-2)
    		 return "2day" ;
    	 if(seconds < 60 * 60 * 72&& seconds >= 60 * 60 * 72-2)
    		 return "3day" ;
    	 if(seconds > 60 * 60 * 72 )
    	  return "" ;
    	 return "_blank" ;
     }
}
