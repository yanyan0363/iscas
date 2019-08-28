package beans.slip;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class MyDcgrn extends Structure{
	public int subfaultid1 = 0;
	public int subfaultid2 = 0;
	public double dcgrn_str1 = 0;
	public double dcgrn_str2 = 0;
	public double dcgrn_str3 = 0;
	public double dcgrn_str4 = 0;
	public double dcgrn_str5 = 0;
	public double dcgrn_str6 = 0;
	public double dcgrn_dip1 = 0;
	public double dcgrn_dip2 = 0;
	public double dcgrn_dip3 = 0;
	public double dcgrn_dip4 = 0;
	public double dcgrn_dip5 = 0;
	public double dcgrn_dip6 = 0;
	
	public static class ByReference extends grid implements Structure.ByReference {}  
    public static class ByValue extends grid implements Structure.ByValue {}  
    
	@Override
	protected List getFieldOrder() {
		// TODO Auto-generated method stub
		return Arrays.asList(new String[]{"subfaultid1", "subfaultid2", "dcgrn_str1", "dcgrn_str2", "dcgrn_str3", "dcgrn_str4", "dcgrn_str5", "dcgrn_str6",
				"dcgrn_dip1", "dcgrn_dip2", "dcgrn_dip3", "dcgrn_dip4", "dcgrn_dip5", "dcgrn_dip6"});
	}

}
