package beans;

public class BLH {

	public double B = 0;
	public double L = 0;
	public double H = 0;
	
	public BLH() {
		
	}
	public BLH(double B, double L, double H) {
		this.B = Double.parseDouble(String.format("%.2f", B));
		this.L = Double.parseDouble(String.format("%.2f", L));
		this.H = Double.parseDouble(String.format("%.2f", H));
	}
	public BLH copy() {
		BLH copyBLH = new BLH(this.B, this.L, this.H);
		return copyBLH;
	}
	@Override
	public String toString() {
		return "BLH:"+B+","+L+","+H;
	}
}
