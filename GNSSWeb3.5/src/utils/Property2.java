package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Property2 {

	static Properties prop = new Properties();
	static {   
        System.out.println();
        InputStream in = Property2.class.getResourceAsStream("../config.properties");
        try {   
            prop.load(in);   
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
    }   
	
	public static String getStringProperty(String key){
		return prop.getProperty(key);
	}
	public static int getIntProperty(String key){
		return Integer.parseInt(prop.getProperty(key));
	}
	public static double getDoubleProperty(String key) {
		return Double.parseDouble(prop.getProperty(key));
	}
	public static void main(String[] args) {
		System.out.println(Property2.getStringProperty("jdbcDriver"));
	}
}
