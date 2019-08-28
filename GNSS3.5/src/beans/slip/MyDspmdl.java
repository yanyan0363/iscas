package beans.slip;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class MyDspmdl extends Structure{
	public int subfaultid = 0;
	public int num = 0;
	public double dspmdl_str = 0;
	public double dspmdl_dip = 0;
	
	public static class ByReference extends grid implements Structure.ByReference {}  
    public static class ByValue extends grid implements Structure.ByValue {}  
    
	@Override
	protected List getFieldOrder() {
		// TODO Auto-generated method stub
		return Arrays.asList(new String[]{"subfaultid", "num", "dspmdl_str", "dspmdl_dip"});
	}

}
