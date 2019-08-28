package beans.slip;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class stationdata extends Structure{

	public double lat = 0;
	public double lon = 0;
	public double ns = 0;
	public double ew = 0;
	public double ud = 0;
	
	public static class ByReference extends stationdata implements Structure.ByReference {}  
    public static class ByValue extends stationdata implements Structure.ByValue {}  
    
	@Override
	protected List getFieldOrder() {
		// TODO Auto-generated method stub
		return Arrays.asList(new String[]{"lat", "lon", "ns", "ew", "ud"});
	}
	
}
