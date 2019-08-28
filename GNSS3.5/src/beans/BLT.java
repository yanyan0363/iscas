package beans;

import java.util.Date;

import metaData.StaticMetaData;

public class BLT {

	private double B = 0;
	private double L = 0;
	private Date T = null;
	private BLT() {
	}
	
	public BLT(double B, double L, Date T){
		this.B = B;
		this.L = L;
		this.T = T;
	}

	public double getB() {
		return B;
	}

	public double getL() {
		return L;
	}

	public Date getT() {
		return T;
	}
	@Override
	public String toString() {
		
		return B+", "+L+", "+StaticMetaData.formatMs.format(T);
	}
}
