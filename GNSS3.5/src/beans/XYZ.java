package beans;

public class XYZ {

	public double X = 0;
	public double Y = 0;
	public double Z = 0;
	
	public XYZ() {
		
	}
	public XYZ(double X, double Y, double Z) {
		this.X = Double.parseDouble(String.format("%.2f", X));
		this.Y = Double.parseDouble(String.format("%.2f", Y));
		this.Z = Double.parseDouble(String.format("%.2f", Z));
	}
	public XYZ copy() {
		XYZ copyXYZ = new XYZ(this.X, this.Y, this.Z);
		return copyXYZ;
	}
	@Override
	public String toString() {
		return "XYZ:"+X+","+Y+","+Z;
	}
}
