package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.esri.arcgis.schematic.SchematicAttributeConstant;
import com.mysql.jdbc.StringUtils;


public class Config {

	static Properties prop = new Properties();
	static {   
        InputStream in = Config.class.getResourceAsStream("../config.properties");
        try {   
            prop.load(in);
            
//        	MEMSFolder = getStringProperty("MEMSFolder");
//        	GPSFolder = getStringProperty("GPSFolder");
        	filePath = getStringProperty("filePath");
        	ImgPath = getStringProperty("ImgPath");
        	fileDir = getStringProperty("fileDir");
        	noticeUrl = getStringProperty("noticeUrl");
        	MEMSFolder = getStringProperty("MEMSFolder");
        	GPSFolder = getStringProperty("GPSFolder");
        	timeExt = getIntProperty("timeExt");
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
    } 
	
//	public static String MEMSFolder;
//	public static String GPSFolder;
	public static String filePath;
	public static String ImgPath;
	public static String fileDir;
	public static String noticeUrl;
	public static String MEMSFolder;
	public static String GPSFolder;
	public static int timeExt;
	
	private static boolean getBooleanProperty(String key) {
		return Boolean.parseBoolean(prop.getProperty(key));
	}
	private static String getStringProperty(String key){
		return prop.getProperty(key);
	}
	private static int getIntProperty(String key){
		return Integer.parseInt(prop.getProperty(key));
	}
	private static long getLongProperty(String key) {
		return Long.parseLong(prop.getProperty(key));
	}
	private static double getDoubleProperty(String key) {
		return Double.parseDouble(prop.getProperty(key));
	}
	public static void main(String[] args) {
		System.out.println(Config.getStringProperty("jdbcDriver"));
		System.out.println(Config.getBooleanProperty("isMEMSSysOut"));
		System.out.println(Boolean.getBoolean("true"));
		System.out.println(Boolean.parseBoolean("true"));
	}
}
