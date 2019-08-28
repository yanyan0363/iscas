package beans;

public class BLH {

	public double B;
	public double L;
	public double H;
	
	public BLH() {
		
	}
	public BLH(double B, double L, double H) {
		this.B = B;
		this.L = L;
		this.H = H;
	}
	@Override
	public String toString() {
		return "BLH:"+B+","+L+","+H;
	}
}
