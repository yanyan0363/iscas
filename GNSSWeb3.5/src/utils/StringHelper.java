package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringHelper {

	/**
	 * 判读str是否为数值型的字符串，包括正负、整数、小数
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		return str.matches("-?[0-9]+.*[0-9]*");
	}
	public static  String[] getStringArray(String s, String seperator){
		if(s == null )
			return null ;
		String s1 = s ;
		if(! s1.endsWith(seperator))
			s1 = s1 + seperator ;
		
		 String[] res = s.split(seperator) ;
		 return res ;
	}
	public static String getStringFromDate(Date t){
	  try{
		String s= 	(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(t); 
	   return s ;
	  }
	  catch(Exception ex){
		  ex.printStackTrace();
	  }
	  return "" ;
	}
	public static Date getDateFromString(String s){
		if(s == null || s.equals(""))
			return null ;
		try  
		{  
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss.SSS");  
		    return sdf.parse(s);  
		}  
		catch (Exception e)  
		{  
		    System.out.println(e.getMessage());  
		}  
		return null ;
	}
}
