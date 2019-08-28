package utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class StringHelper {

	public static  String[] getStringArray(String s, String seperator){
		if(s == null )
			return null ;
		String s1 = s ;
		if(! s1.endsWith(seperator))
			s1 = s1 + seperator ;
		
		 String[] res = s.split(seperator) ;
		 return res ;
	}
	/**
	 * 根据字符串获取时间长度，单位s
	 * @param s 字符串
	 * @return
	 */
	public static int getSFromString(String s){
		if (s.endsWith("s")) {
			return Integer.parseInt(s.substring(0, s.indexOf("s")));
		}else if (s.endsWith("min")) {
			return 60 * Integer.parseInt(s.substring(0, s.indexOf("min")));
		}else if (s.endsWith("h")) {
			return 60 * 60 * Integer.parseInt(s.substring(0, s.indexOf("h")));
		}
		return 0;
	}
	public static String getStringFromDate(Date t){
	  try{
		String s = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(t); 
	   return s ;
	  }
	  catch(Exception ex){
		  ex.printStackTrace();
	  }
	  return "" ;
	}
	/**
	 * String 转 Date，用于解析GPS数据行的Date
	 * @param s yyyy/MM/dd HH:mm:ss.SSS
	 * @return
	 */
	public static Date getDateFromString(String s){
		if(s == null || s.equals(""))
			return null ;
		try  
		{  
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");  
		    return sdf.parse(s);  
		}  
		catch (Exception e)  
		{  
		    System.out.println(e.getMessage());  
		}  
		return null ;
	}
	/**
	 * 判断目标参数folder是否为已存在的文件夹
	 * @param folder
	 * @return
	 */
	public static boolean isFolderExist(String folder) {
		if (folder == null || folder == "") {
			return false;
		}
		File file = new File(folder);
		if (file.exists() && file.isDirectory()) {
			return true;
		}
		return false;
	}
	/**
	 * 判断目标参数filePath为已存在的文件
	 * @param filePath
	 * @return
	 */
	public static boolean isFileExist(String filePath) {
		if (filePath == null || filePath == "") {
			return false;
		}
		File file = new File(filePath);
		if (file.exists() && file.isFile()) {
			return true;
		}
		return false;
	}
	/**
	 * 判读str是否为数值型的字符串，包括正负、整数、小数
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		return str.matches("-?[0-9]+.*[0-9]*");
		//^(\-|\+)?\d+(\.\d+)?$
	}
	/**
	 * 判断str是否为double
	 * @param str
	 * @return
	 */
	public static boolean isDouble(String str) {  
		  
	    Pattern pattern = Pattern.compile("^[-//+]?//d+(//.//d*)?|//.//d+$");  
	  
	    return pattern.matcher(str).matches();  
	  
	  }  
	/**
	 * 判读str是否为正数的字符串
	 * @param str
	 * @return
	 */
	public static boolean isPositiveNumeric(String str) {
		return str.matches("[0-9]+.*[0-9]*");
	}
	public static void main(String[] args) {
		System.out.println("11:" + isNumeric("11"));
		System.out.println("11.0:" + isNumeric("11.0"));
		System.out.println("11.012:" + isNumeric("11.012"));
		System.out.println("-11.0:" + isNumeric("-11.0"));
		System.out.println("abs:" + isNumeric("abs"));
		System.out.println("-abs:" + isNumeric("-abs"));
		System.out.println();
		System.out.println("11:" + isPositiveNumeric("11"));
		System.out.println("11.0:" + isPositiveNumeric("11.0"));
		System.out.println("11.012:" + isPositiveNumeric("11.012"));
		System.out.println("-11.0:" + isPositiveNumeric("-11.0"));
		System.out.println("-11:" + isPositiveNumeric("-11"));
		System.out.println("abs:" + isPositiveNumeric("abs"));
		System.out.println("-abs:" + isPositiveNumeric("-abs"));
		System.out.println();
	}
}
