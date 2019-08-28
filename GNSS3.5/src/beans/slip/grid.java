package beans.slip;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class grid extends Structure{

	public String gridid = null;
	public int faultid = 0;
	public int subfaultid = 0;
	public double lat = 0;
	public double lon = 0;
	public double depth = 0;
	public double square = 0;
	public double strike = 0;
	public double dip = 0;
	
	public static class ByReference extends grid implements Structure.ByReference {}  
    public static class ByValue extends grid implements Structure.ByValue {}  
    
	@Override
	protected List getFieldOrder() {
		// TODO Auto-generated method stub
		return Arrays.asList(new String[]{"faultid", "subfaultid", "lat", "lon", "depth", "square", "strike", "dip", "gridid"});
	}
	
}
