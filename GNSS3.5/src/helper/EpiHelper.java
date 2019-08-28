package helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import Jama.Matrix;
import arcgis.GisHelper;
import beans.BLH;
import beans.BLT;
import beans.GPSData;
import mathUtil.DoubleUtil;
import metaData.StaticMetaData;
import utils.Config;

public class EpiHelper {

//		1	14.98	38.3052	141.5044
//		2	18.44	38.9015	141.5684
//		3	19.34	39.0187	141.4031
//		4	19.74	38.7348	141.3106
//		5	23.42	39.3367	141.5378
//		6	26.01	39.5997	141.6789
//		7	28.06	39.6336	141.4376
//		8	28.67	37.3364	140.8132
//		9	29.46	39.8491	141.8034
//		10	30.01	37.0911	140.9035
//		11	31.01	39.1462	140.717
//		12	34.01	38.1502	140.2674
//		13	35.16	37.09	140.5563
	public static void main(String[] args) {
		double depth = 0;//这里忽略震源深度，设为0km
		BLT[] blts = new BLT[5];
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 14);
		now.set(Calendar.MINUTE, 46);
		now.set(Calendar.SECOND, 23);
		now.set(Calendar.MILLISECOND, 0);
		long nowLong = now.getTimeInMillis();
		blts[0] = new BLT(38.3052, 141.5044, new Date(nowLong+14980));
		blts[1] = new BLT(38.9015, 141.5684, new Date(nowLong+18440));
		blts[2] = new BLT(39.0187, 141.4031, new Date(nowLong+19340));
		blts[3] = new BLT(38.7348, 141.3106, new Date(nowLong+19740));
		blts[4] = new BLT(39.3367, 141.5378, new Date(nowLong+23420));
//		blts[5] = new BLT(39.5997, 141.6789, new Date(nowLong+26010));
//		blts[6] = new BLT(39.6336, 141.4376, new Date(nowLong+28060));
//		blts[7] = new BLT(37.3364, 140.8132, new Date(nowLong+28670));
//		blts[8] = new BLT(39.8491, 141.8034, new Date(nowLong+29460));
//		blts[9] = new BLT(37.0911, 140.9035, new Date(nowLong+30010));
//		blts[10] = new BLT(39.1462, 140.717, new Date(nowLong+31010));
//		blts[11] = new BLT(38.1502, 140.2674, new Date(nowLong+34010));
//		blts[12] = new BLT(37.09, 140.5563, new Date(nowLong+35160));
		Date startT = new Date();
		EpiHelper epiHelper = new EpiHelper(blts);
		GPSData gpsData = epiHelper.calEpi(depth, 1000);
		Date endT = new Date();
		System.out.println("Epi计算耗时：" + (endT.getTime()-startT.getTime())+"ms");
	}
	private BLT[] blts;
	private double B0 = 1;//初始震中位置，可以设成第一个触发的台站坐标
	private double L0 = 2;
	private double v0 = 4;//初始速度4公里/秒
	private Date EQTime;
//	private DecimalFormat df = new DecimalFormat("#.0000");
	
	public EpiHelper(BLT[] blts) {
		this.blts = blts;
	}
	private Date calEQT() {
		long sumT = 0;
		for (BLT blt : blts) {
//			sumT+=(blt.getT().getTime() - GisHelper.calDisByBL(blt.getL(), blt.getB(), L0, B0)/v0*1000);
			sumT+=(blt.getT().getTime() - GisHelper.calDisByBLProjTo3857(blt.getL(), blt.getB(), L0, B0)/v0*1000);
		}
		long tt = sumT/blts.length;
		return new Date(tt);
	}
	public GPSData calEpi(double depth, int calTimes) {
		int length = blts.length;
		if (length < 5) {
			return null;
		}
		for (int i = 0; i < blts.length; i++) {
			B0 = B0 + blts[0].getB();
			L0 = L0 + blts[0].getL();
		}
		B0 = B0/length;
		L0 = L0/length;
		System.out.println("B0:"+B0+", L0:"+L0+", v:"+v0);
		for (int i = 0; i < calTimes; i++) {
			double sinB0 = sinDegree(B0);
			double cosB0 = cosDegree(B0);
			double A[][] = new double[3][length-1];
			double w[][] = new double[length-1][1];
			double dbxs0 = dbxs(blts[0].getB(), blts[0].getL(), sinB0, cosB0, L0, depth);
			double dlxs0 = dlxs(blts[0].getB(), blts[0].getL(), sinB0, cosB0, L0, depth);
			for (int j = 0; j < length-1; j++) {
				A[0][j] = dbxs(blts[j+1].getB(), blts[j+1].getL(), sinB0, cosB0, L0, depth) - dbxs0;
				A[1][j] = dlxs(blts[j+1].getB(), blts[j+1].getL(), sinB0, cosB0, L0, depth) - dlxs0;
				A[2][j] = ((double)(blts[0].getT().getTime() - blts[j+1].getT().getTime()))/1000;//单位：s
				w[j][0] = -w(blts[j+1].getB(), blts[j+1].getL(), blts[0].getB(), blts[0].getL(), B0, L0, v0, -A[2][j], depth);
			}
			Matrix matrixA = new Matrix(A).transpose();
			
			Matrix WW = new Matrix(w);
			Matrix AT = matrixA.transpose();
			if (AT.rank() == 3) {
				Matrix vMatrix = AT.times(matrixA).inverse().times(AT).times(WW);
				B0 = B0 + vMatrix.get(0, 0);
				L0 = L0 + vMatrix.get(1, 0);
				v0 = v0 + vMatrix.get(2, 0);
				System.out.println("第"+(i+1)+"次计算 --> B0:"+B0+" , L0:"+L0+" , v0:"+v0);
			}
		}
		this.EQTime = calEQT();
		System.out.println("计算震中 B:"+B0+", L:"+L0+", v:"+v0);
		System.out.println("计算震时："+ StaticMetaData.formatMs.format(EQTime));
		//here start ---
//		B0 = B0%90;
//		L0 = L0%180;
//		if (B0 < 0) {
//			B0 = B0*(-1);
//		}
//		if (v0 < 0) {
//			v0 = v0*(-1);
//		}
//		System.out.println();
//		System.out.println("震中处理方式：B0 = B0%90;L0 = L0%180;");
//		System.out.println("处理后震中 B:"+B0+", L:"+L0);
//		this.EQTime = calEQT();
//		System.out.println("处理后震时："+ StaticMetaData.formatMs.format(EQTime));
		//here end ---
//		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
//		double sum = 0;
//		double T[] = new double[5];
//		Calendar now = Calendar.getInstance();
//		now.set(Calendar.HOUR_OF_DAY, 14);
//		now.set(Calendar.MINUTE, 46);
//		now.set(Calendar.SECOND, 23);
//		now.set(Calendar.MILLISECOND, 0);
//		long nowLong = now.getTimeInMillis();
//		for (int i = 0; i < blts.length; i++) {
//			BLT blt = blts[i];
//			double epiDis = GisHelper.calDisByBL(blt.getL(), blt.getB(), L0, B0);
//			Date st = new Date(blt.getT().getTime()+(long)(1000*DoubleUtil.div(epiDis, Config.Vs, 3)));
//			System.out.println("B:"+blt.getL()+", L:" + blt.getB()+", T:"+ format.format(blt.getT())+", ST:"+format.format(st));
//			T[i] = (blt.getT().getTime()-nowLong-epiDis*1000/v0)/1000;
//		}
//		for (int i = 0; i < T.length; i++) {
//			sum += DoubleUtil.mul(T[i], T[i]);
//		}
//		System.out.println(sum);
//		System.out.println("震中：： B:"+B0+", L:"+L0+", v:"+v0);
//		System.out.println("震时:: " + StaticMetaData.formatMs.format(EQTime));
		return new GPSData(new BLH(B0, L0, depth));
	}
	private double w(double Bii, double Lii, double Bi, double Li, double B0, double L0, double v0, double dt, double depth) {
		double sinBii = sinDegree(Bii);
		double sinB0 = sinDegree(B0);
		double cosBii = cosDegree(Bii);
		double sinBi = sinDegree(Bi);
		double cosB0 = cosDegree(B0);
		double cosBi = cosDegree(Bi);
		double cosLi0 = cosDegree(Li-L0);
		double cosLii0 = cosDegree(Lii - L0);
//		System.out.println("sinBii::"+sinBii);
//		System.out.println("sinB0:"+sinB0);
//		System.out.println("cosBii:"+cosBii);
//		System.out.println("sinBi::"+sinBi);
//		System.out.println("cosB0:"+cosB0);
//		System.out.println("cosBi:"+cosBi);
//		System.out.println("cosLi0:"+cosLi0);
//		System.out.println("cosLii0:"+cosLii0);
		
//		double d1 = sinBii*sinB0 + cosBii*cosB0*cosLii0;
		double d1 = DoubleUtil.add(DoubleUtil.mul(sinBii, sinB0), DoubleUtil.mul(DoubleUtil.mul(cosBii, cosB0), cosLii0));
//		System.out.println("d1:"+d1);
		double d2 = Math.acos(d1);
//		System.out.println("d2:"+d2);
		double d3 = DoubleUtil.mul(d2, 6371);
//		System.out.println("d3:"+d3);
		double d4 = DoubleUtil.mul(d3, d3);
		double d5 = d4 + Math.pow(depth, 2);
		double d6 = Math.sqrt(d5);
//		System.out.println("d6:"+d6);
		
		double d7 = sinBi*sinB0 + cosBi*cosB0*cosLi0;
		double d8 = Math.acos(d7);
		double d9 = DoubleUtil.mul(d8, 6371);
		double d10 = DoubleUtil.mul(d9, d9);
		double d11 = DoubleUtil.add(d10, Math.pow(depth, 2));
		double d12 = Math.sqrt(d11);
		
		double d = d6 - d12 - v0*dt;
		return d;
	}
	
	
	private double dlxs(double B1, double L1, double sinB0, double cosB0, double L0, double depth) {
		double sinB1 = sinDegree(B1);
		double cosB1 = cosDegree(B1);
		double L01 = degreeToRadian(L0-L1);
		double cosL01 = Math.cos(L01);
		double sinL01 = Math.sin(L01);
		
//		double u1 = cosB0*cosB1*sinL01;
//		double u2 = Math.acos(sinB0*sinB1+cosB0*cosB1*cosL01);
		double u1 = DoubleUtil.mul(DoubleUtil.mul(cosB0, cosB1), sinL01);
		double u0 = DoubleUtil.add(DoubleUtil.mul(sinB0, sinB1), DoubleUtil.mul(DoubleUtil.mul(cosB0, cosB1), cosL01));
		double u2 = Math.acos(u0);
		double u3 = DoubleUtil.mul(40589641, u1);
		double u = DoubleUtil.mul(u3, u2);
		
//		double d1 = Math.pow(sinB0*sinB1+cosB0*cosB1*cosL01, 2);
		double d1 = DoubleUtil.mul(u0, u0);
		double d2 = Math.pow(1-d1, 0.5);
//		double d3 = Math.acos(sinB0*sinB1+cosB0*cosB1*cosL01);
		double d3 = Math.acos(u0);
		double d4 = Math.pow(d3, 2);
		double d5 = DoubleUtil.mul(40589641, d4);
		double d6 = Math.pow(depth*depth+d5, 0.5);
		double d = DoubleUtil.mul(d2, d6);
		return DoubleUtil.div(u, d, 16);
	}

	private double dbxs(double B1, double L1, double sinB0, double cosB0, double L0, double depth) {
		double sinB1 = sinDegree(B1);
		double cosB1 = cosDegree(B1);
		double L01 = degreeToRadian(L0-L1);
		double cosL01 = Math.cos(L01);
//		System.out.println("L01:"+L01+", cosL01:"+cosL01);
		double u1 = Math.acos(DoubleUtil.add(DoubleUtil.mul(sinB0, sinB1), DoubleUtil.mul(DoubleUtil.mul(cosB0, cosB1), cosL01)));
//		System.out.println("u1:"+u1);
//		double u2 = cosB0*sinB1-cosB1*sinB0*cosL01;
		double u2 = DoubleUtil.sub(DoubleUtil.mul(cosB0, sinB1), DoubleUtil.mul(DoubleUtil.mul(cosB1, sinB0), cosL01));
		double u3 = DoubleUtil.mul(40589641, u1);
		double u = DoubleUtil.mul(u3, u2);
		
		double d0 = DoubleUtil.add(DoubleUtil.mul(sinB0, sinB1), DoubleUtil.mul(DoubleUtil.mul(cosB0, cosB1), cosL01));
		double d1 = 1-DoubleUtil.mul(d0, d0);
		double d2 = Math.pow(d1, 0.5);
		double d3 = Math.pow(depth, 2);
//		double d4 = Math.acos(sinB0*sinB1+cosB0*cosB1*cosL01);
		double d4 = Math.acos(d0);
		double d5 = Math.pow(d4, 2);
		double d6 = DoubleUtil.mul(40589641, d5);
		double d7 = DoubleUtil.add(d3, d6);
		double d8 = Math.pow(d7, 0.5);
		double d = DoubleUtil.mul(d2, d8);
//		System.out.println("u:"+u+", d:"+d);
		return (-1)*DoubleUtil.div(u, d, 16);
	}
	private double cosDegree(double degree) {
//		return Double.parseDouble(String.format("%.4f", Math.cos(degreeToRadian(degree))));
		return Math.cos(degreeToRadian(degree));
	}
	private double sinDegree(double degree) {
		return Math.sin(degreeToRadian(degree));
	}
	private double degreeToRadian(double degree) {
		return 3.1416*degree/180;
	}
	public Date getEQTime() {
		return EQTime;
	}
	
}
