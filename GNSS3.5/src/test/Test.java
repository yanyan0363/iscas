package test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import arcgis.GisHelper;
import utils.Config;
import utils.StringHelper;

public class Test {

	public static void main(String[] args) {
		double dis = GisHelper.calDisByBLProjTo3857(104.90, 28.34, 100.61, 26.85);
		System.out.println(dis);
//		double d1 = 7.06302714347708E-5;
//		double d2 = 1.35683608054986E-5;
//		double sum =  d1+ d2;
//		System.out.println(sum/100);
//		System.out.println();
//		double dis = GisHelper.calDisByBL(136, 34, 136, 42)*1000*10;//cm
//		System.out.println("dis(cm):"+dis);
//		double scale = 2000000;
//		System.out.println(scale);
//		int pi = (int)(1080/2.54);//像素/cm
//		System.out.println(8894197.868163083/2500000*100);
//		System.out.println("dis:"+dis+", 左："+(dis*pi/scale));
//		dis = GisHelper.calDisByBL(142, 34, 142, 42)*1000*10;//cm
//		System.out.println("dis:"+dis+", 右："+dis*pi/scale);
//		dis = GisHelper.calDisByBL(136, 42, 142, 42)*1000*10;//cm
//		System.out.println("dis:"+dis+", 上："+dis*pi/scale);
//		dis = GisHelper.calDisByBL(136, 34, 142, 34)*1000*10;//cm
//		System.out.println("dis:"+dis+", 下："+dis*pi/scale);
//		String tt = "SMTWX,SMFYX,SMXJZ,SMCLX,SMMYX,SMPGX,SMBTX,SMHGZ,SMZSZ,SMMNX,HBDGL,HBDMB,HBDLG,HBDXC,HBDZJ,ZBDHY,ZBDLW,ZBDYX,ZBDXD,ZBDMG";
//		Set<String> set = new HashSet<>(Arrays.asList(tt.split(",")));
//		System.out.println(set.size()+":"+set.toString());
//		System.out.println(set.contains("SMTWX"));
//		int S = (int)(Config.MEMSHz * Config.stw);//短窗口
//		int L = (int)(Config.MEMSHz * Config.ltw);//长窗口
//		double dSNR = Math.pow(10, (Config.SNR/10));
//		double thresh = (dSNR+1)/(dSNR*Config.stw/Config.ltw+1);//阈值
//		System.out.println("dSNR::" + dSNR);
//		System.out.println("thresh::" + thresh);
//		double stw = 0.5;
//		double ltw = 5;
//		double SNR = 7;
//		double dSNR = Math.pow(10, (SNR/10));
//		System.out.println("dSNR:"+dSNR);
//		double thresh = (dSNR+1)/(dSNR*stw/ltw+1);
//		System.out.println("thresh:" + thresh);
//		System.out.println(Math.acos(0.9998));//9998184000000001
//		System.out.println(Math.acos(0.9998559600000001));
//		System.out.println(Math.acos(0.9997278627319952));
//		System.out.println(Math.acos(0.9998762111511164));//0.01573476424446656
//		double d1 = Math.toRadians(38.3052);
//		double d2 = Math.cos(d1);
//		double d3 = Math.acos(d2);
//		double d4 = Math.toDegrees(d3);
//		System.out.println("d1:"+d1);
//		System.out.println("d2:"+d2);
//		System.out.println("d3:"+d3);
//		System.out.println("d4:"+d4);
//		System.out.println(Math.toDegrees(Math.acos(Math.cos(Math.toRadians(38.3052)))));
		
//		System.out.println("\n");
//		Apfloat apfloat1 = ApfloatMath.toRadians(new Apfloat(38.3052));
//		Apfloat apfloat2 = ApfloatMath.cos(apfloat1);
//		Apfloat apfloat3 = ApfloatMath.acos(apfloat2);
//		Apfloat apfloat4 = ApfloatMath.toDegrees(apfloat3);
//		System.out.println("1:"+apfloat1.doubleValue());
//		System.out.println("2:"+apfloat2.doubleValue());
//		System.out.println("3:"+apfloat3.doubleValue());
//		System.out.println("4:"+apfloat4.doubleValue());
//		
//		System.out.println("\n");
//		Apfloat apfloat11 = ApfloatMath.toRadians(new Apfloat(38.3052));
//		Apfloat apfloat22 = ApfloatMath.sin(apfloat11);
//		Apfloat apfloat33 = ApfloatMath.asin(apfloat22);
//		Apfloat apfloat44 = ApfloatMath.toDegrees(apfloat33);
//		System.out.println("1:"+apfloat11.doubleValue());
//		System.out.println("2:"+apfloat22.doubleValue());
//		System.out.println("3:"+apfloat33.doubleValue());
//		System.out.println("4:"+apfloat44.doubleValue());
	}
}
