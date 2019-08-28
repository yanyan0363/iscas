package beans;

import java.util.Date;

import mathUtil.DoubleUtil;

public class MEMSData {
//	public String stationID;
	public Date time = null;
	public double accE = 0;
	public double accN = 0;
	public double accH = 0;
	public String token = "";
	public Date localTime = null;
	private double E = 0;
	private double N = 0;
	private double H = 0;
	private boolean isTriggerCalculated = false;
	
	public MEMSData() {
		
	}
	public MEMSData(Date time, double accE, double accN, double accH, Date localTime){
		this.time = time;
		this.accE = accE;
		this.accN = accN;
		this.accH = accH;
		this.localTime = localTime;
	}
	public MEMSData(Date time, double accE, double accN, double accH, String token, Date localTime){
		this.time = time;
		this.accE = accE;
		this.accN = accN;
		this.accH = accH;
		this.token = token;
		this.localTime = localTime;
	}
	public void dispose() {
		this.time = null;
		this.token = null;
		this.localTime = null;
		this.accE = 0;
		this.accN = 0;
		this.accH = 0;
		this.E = 0;
		this.N = 0;
		this.H = 0;
	}
	/**
	 * 计算ENH
	 * @param lastMEMS前一数据时刻
	 */
	public void calENH(MEMSData lastMEMS) {
		long dt = this.time.getTime() - lastMEMS.time.getTime();//ms
		//S = S0 + 0.5 * acc * Math.pow(10, -9) * 9.8 * dt * dt;
		//首先计算0.5 * Math.pow(10, -9) * 9.8 * dt * dt = 4.9 * Math.pow(10, -9) * dt * dt 
		double halfTT = DoubleUtil.mul(4.9, DoubleUtil.mul(Math.pow(10, -9), Math.pow(dt, 2)));
		//EW方向
		this.E = DoubleUtil.add(lastMEMS.E, DoubleUtil.mul(halfTT, this.accE));
		//NS方向
		this.N = DoubleUtil.add(lastMEMS.N, DoubleUtil.mul(halfTT, this.accN));
		//H方向
		this.H = DoubleUtil.add(lastMEMS.H, DoubleUtil.mul(halfTT, this.accH));
//		System.out.println("ENH: "+this.E+", " + this.N + ", " + this.H);
	}
	/**	 
	 * 计算ENH
	 * @param lastE 前一数据时刻的E
	 * @param lastN 前一数据时刻的N
	 * @param lastH 前一数据时刻的H
	 * @param lastT 前一数据时刻
	 */
	public void calENH(double lastE, double lastN, double lastH, Date lastT) {
		long dt = this.time.getTime() - lastT.getTime();//ms
		//S = S0 + 0.5 * acc * Math.pow(10, -9) * 9.8 * dt * dt;
		//首先计算0.5 * Math.pow(10, -9) * 9.8 * dt * dt = 4.9 * Math.pow(10, -9) * dt * dt 
		double halfTT = DoubleUtil.mul(4.9, DoubleUtil.mul(Math.pow(10, -9), Math.pow(dt, 2)));
		//EW方向
		this.E = DoubleUtil.add(lastE, DoubleUtil.mul(halfTT, this.accE));
		//NS方向
		this.N = DoubleUtil.add(lastN, DoubleUtil.mul(halfTT, this.accN));
		//H方向
		this.H = DoubleUtil.add(lastH, DoubleUtil.mul(halfTT, this.accH));
//		System.out.println("ENH: "+this.E+", " + this.N + ", " + this.H);
	}
//	public void setENH(double E, double N, double H) {
//		this.E = E;
//		this.N = N;
//		this.H = H;
//	}
	
	public double getH() {
		return H;
	}
	public void setH(double h) {
		H = h;
	}
	public Date getTime() {
		return time;
	}
	public double getAccE() {
		return accE;
	}
	public double getAccN() {
		return accN;
	}
	public double getAccH() {
		return accH;
	}
	public String getToken() {
		return token;
	}
	public double getE() {
		return E;
	}
	public double getN() {
		return N;
	}
	public boolean isTriggerCalculated() {
		return isTriggerCalculated;
	}
	public void setTriggerCalculated(boolean isTriggerCalculated) {
		this.isTriggerCalculated = isTriggerCalculated;
	}
	@Override
		public String toString() {
			return time+","+accE+","+accN+","+accH+","+token+","+localTime;
		}
}
