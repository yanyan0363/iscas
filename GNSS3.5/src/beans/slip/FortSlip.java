package beans.slip;

import beans.BLH;

public class FortSlip {

	private BLH[] blh = null;
	private double slp_strk_m = 0;
	private double slp_ddip_m = 0;
	private double slp_am_m = 0;
	private int tNode = 0;
	private String slip = null;
	private String gridid = null;
	
	private FortSlip() {
		
	}
	
	public FortSlip(BLH[] blh, double slp_strk_m, double slp_ddip_m, double slp_am_m, String gridid) {
		this.blh = blh;
		this.slp_strk_m = slp_strk_m;
		this.slp_ddip_m = slp_ddip_m;
		this.slp_am_m = slp_am_m;
		this.gridid = gridid;
	}

	public int gettNode() {
		return tNode;
	}

	public void settNode(int tNode) {
		this.tNode = tNode;
	}

	public String getSlip() {
		return slip;
	}

	public void setSlip(String slip) {
		this.slip = slip;
	}

	public BLH[] getBlh() {
		return blh;
	}

	public void setBlh(BLH[] blh) {
		this.blh = blh;
	}

	public double getSlp_strk_m() {
		return slp_strk_m;
	}
	
	public void setSlp_strk_m(double slp_strk_m) {
		this.slp_strk_m = slp_strk_m;
	}
	
	public double getSlp_ddip_m() {
		return slp_ddip_m;
	}
	
	public void setSlp_ddip_m(double slp_ddip_m) {
		this.slp_ddip_m = slp_ddip_m;
	}
	
	public double getSlp_am_m() {
		return slp_am_m;
	}
	
	public void setSlp_am_m(double slp_am_m) {
		this.slp_am_m = slp_am_m;
	}

	public String getGridid() {
		return gridid;
	}

	public void setGridid(String gridid) {
		this.gridid = gridid;
	}

	
	
}
