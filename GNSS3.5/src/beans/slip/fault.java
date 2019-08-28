package beans.slip;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class fault extends Structure{

//	public grid.ByValue[] gridArray = new grid.ByValue[250];
	public grid.ByReference[] gridArr = new grid.ByReference[40];
	
	public static class ByReference extends fault implements Structure.ByReference {}  
    public static class ByValue extends fault implements Structure.ByValue {}  
    
	@Override
	protected List getFieldOrder() {
		// TODO Auto-generated method stub
		return Arrays.asList(new String[]{"gridArr"});
	}
	
}
