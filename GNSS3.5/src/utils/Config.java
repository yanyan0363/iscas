package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.esri.arcgis.schematic.SchematicAttributeConstant;
import com.mysql.jdbc.StringUtils;


public class Config {
	public static String SDURL;
	public static String SDMsgName;
	public static String SDToken;
	public static String SDTriggerSt;
	public static Set<String> SDStInlineSet;
//	public static String SDUsername;
//	public static String SDPassword;
//	public static String SDClientID;
//	public static String SDClientKey;
	
	public static int httpServerPort;
	public static int netErrTime;
	public static int GNSSDataAcceptServerPort;
	public static int MEMSDataAcceptServerPort;
	public static boolean isMEMSSysOut;
	public static boolean isGPSSysOut;
	public static boolean isMEMSFiled;
	public static boolean isGPSFiled;
	public static String MEMSFolder;
	public static String GPSFolder;
	public static long fileIOClearTime;
	public static double EQEndTime;
	public static double EQWarnTime;
	public static String slipParamFolder;
	public static String slipServiceFolder;
	public static String slipServiceName;
	public static String slipImgFolder;
	public static String dspgrn;
	public static String smooth;
//	public static String GNSSToolsPath;
	public static int stIDLength;
//	public static String SDIP;
//	public static int SDPort;
//	public static String GreenTimeLine;
//	public static String StationsShp;
	public static String demoFolder;
	public static String filePath;
	public static String ImgPath;
	public static long ArrowsIntervalTime;
	public static String ArcpyFolder;
	public static double contoursInterval;
	public static String demoMXD;
	public static String servicePath;
	public static String serviceFolder;
//	public static String vCLayers;
//	public static String hCLayers;
//	public static String vALayers;
//	public static String hALayers;
	public static int multiLine;
	public static double maxArrow;
//	public static double minVDis;
//	public static double minVRelDis;
	public static int spatialExtent;
	public static long timeExtent;
	public static int epiCalTime;
//	public static String gdbPath;
//	public static String baseMXDPath;
//	public static String updateMXDPath;
//	public static int provLayer;
//	public static int railLayer;
//	public static int countiesLayer;
//	public static int stationsLayer;
	public static double v;
	public static long timeWindow;
	public static String timeLine;
	public static long timeExt;
	public static double stw;
	public static double ltw;
	public static double SNR;
	public static boolean isGreen;
	public static double Vs;
	public static int MEMSHz;
	static Properties prop = new Properties();
	static { 
//		PropertyConfigurator.configure("log4j.properties");
        InputStream in = Config.class.getResourceAsStream("/config.properties");
        try {   
            prop.load(in);
            SDURL = getStringProperty("SDURL");
            SDMsgName = getStringProperty("SDMsgName");
            SDToken = getStringProperty("SDToken");
            SDTriggerSt = getStringProperty("SDTriggerSt");
            String SDStInlineString = getStringProperty("SDStInline").replaceAll(" ", "");
            if (SDStInlineString == null || SDStInlineString.equals("")) {
            	SDStInlineSet = new HashSet<String>();				
			}else {
				SDStInlineSet = new HashSet<String>(Arrays.asList(SDStInlineString.split(",")));
			}
            System.out.println(SDStInlineSet.size()+":" +SDStInlineSet);
//            SDUsername = getStringProperty("SDUsername");
//            SDPassword = getStringProperty("SDPassword");
//            SDClientID = getStringProperty("SDClientID");
//            SDClientKey = getStringProperty("SDClientKey");
            isGreen = getBooleanProperty("isGreen");
            httpServerPort = getIntProperty("httpServerPort");
            netErrTime = getIntProperty("netErrTime");
            GNSSDataAcceptServerPort = getIntProperty("GNSSDataAcceptServerPort");
            MEMSDataAcceptServerPort = getIntProperty("MEMSDataAcceptServerPort");
        	isMEMSSysOut = getBooleanProperty("isMEMSSysOut");
        	isGPSSysOut = getBooleanProperty("isGPSSysOut");
        	isMEMSFiled = getBooleanProperty("isMEMSFiled");
        	isGPSFiled = getBooleanProperty("isGPSFiled");
        	MEMSFolder = getStringProperty("MEMSFolder");
        	GPSFolder = getStringProperty("GPSFolder");
        	fileIOClearTime = getLongProperty("fileIOClearTime");
        	EQEndTime = getDoubleProperty("EQEndTime");
        	EQWarnTime = getDoubleProperty("EQWarnTime");
        	slipParamFolder = getStringProperty("slipParamFolder");
        	slipServiceFolder = getStringProperty("slipServiceFolder");
        	slipServiceName = getStringProperty("slipServiceName");
        	slipImgFolder = getStringProperty("slipImgFolder");
        	dspgrn = getStringProperty("dspgrn");
        	smooth = getStringProperty("smooth");
//        	GNSSToolsPath = getStringProperty("GNSSToolsPath");
        	stIDLength = getIntProperty("stIDLength");
//        	SDIP = getStringProperty("SDIP");
//        	SDPort = getIntProperty("SDPort");
//        	GreenTimeLine = getStringProperty("GreenTimeLine");
//        	StationsShp = getStringProperty("StationsShp");
        	demoFolder = getStringProperty("demoFolder");
        	filePath = getStringProperty("filePath");
        	ImgPath = getStringProperty("ImgPath");
        	ArrowsIntervalTime = getLongProperty("ArrowsIntervalTime");
        	ArcpyFolder = getStringProperty("ArcpyFolder");
        	contoursInterval = getDoubleProperty("contoursInterval");
        	demoMXD = getStringProperty("demoMXD");
        	servicePath = getStringProperty("servicePath");
        	serviceFolder = getStringProperty("serviceFolder");
//        	vCLayers = getStringProperty("vCLayers");
//        	hCLayers = getStringProperty("hCLayers");
//        	vALayers = getStringProperty("vALayers");
//        	hALayers = getStringProperty("hALayers");
        	multiLine = getIntProperty("multiLine");
        	maxArrow = getDoubleProperty("maxArrow");
//        	minVDis = getDoubleProperty("minVDis");
//        	minVRelDis = getDoubleProperty("minVRelDis");
        	spatialExtent = getIntProperty("spatialExtent");
        	timeExtent = getLongProperty("timeExtent");
        	epiCalTime = getIntProperty("epiCalTime");
//        	gdbPath = getStringProperty("gdbPath");
//        	baseMXDPath = getStringProperty("baseMXDPath");
//        	updateMXDPath = getStringProperty("updateMXDPath");
//        	provLayer = getIntProperty("provLayer");
//        	railLayer = getIntProperty("railLayer");
//        	countiesLayer = getIntProperty("countiesLayer");
//        	stationsLayer = getIntProperty("stationsLayer");
        	v = getDoubleProperty("v");
        	timeWindow = getLongProperty("timeWindow");
        	timeLine = getStringProperty("timeLine");
        	timeExt = getLongProperty("timeExt");
        	stw = getDoubleProperty("stw");
        	ltw = getDoubleProperty("ltw");
        	SNR = getDoubleProperty("SNR");
        	Vs = getDoubleProperty("Vs");
        	MEMSHz = getIntProperty("MEMSHz");
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
    } 

	
	
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
