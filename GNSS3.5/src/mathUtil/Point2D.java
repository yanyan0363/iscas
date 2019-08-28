package mathUtil;

public class Point2D {
	private double x;//平面坐标系横轴
	private double y;//平面坐标系纵轴
	private float t;//时刻，单位：s
	
	@Override
	public String toString() {
		return "("+x+", "+y+", "+t+")";
	}
	public Point2D() {
		
	}
	public Point2D(double x, double y, float t){
		this.x = x;
		this.y = y;
		this.t = t;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public float getT() {
		return t;
	}
	public void setT(float t) {
		this.t = t;
	}
}
