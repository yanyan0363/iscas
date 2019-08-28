package beans;

public class XYZ {

	public double X;
	public double Y;
	public double Z;
	
	public XYZ() {
		
	}
	public XYZ(double X, double Y, double Z) {
		this.X = X;
		this.Y = Y;
		this.Z = Z;
	}
	@Override
	public String toString() {
		return "XYZ:"+X+","+Y+","+Z;
	}
}
