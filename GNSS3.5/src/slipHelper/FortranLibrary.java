package slipHelper;

import com.sun.jna.Library;
import com.sun.jna.ptr.IntByReference;

import beans.slip.MyDcgrn;
import beans.slip.MyDspmdl;
import beans.slip.grid;
import beans.slip.stationdata;

public interface FortranLibrary extends Library{

	void startdll(stationdata[] stationDataArray,grid[] gridArray, MyDspmdl[] dspmdls, MyDcgrn[] dcgrns, String fileName,IntByReference fNLen, String folder, IntByReference folderLen);
	
}
