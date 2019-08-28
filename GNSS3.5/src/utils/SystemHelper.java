package utils;

import java.util.Calendar;
import java.util.Date;

public class SystemHelper {

	public static void writeLog(String s){
		 Date time = Calendar.getInstance().getTime() ;
   
		System.out.println(StringHelper.getStringFromDate(time) + "::"+  s);
	}
}
