package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.esri.arcgis.framework.Accelerator;
import com.esri.arcgis.geodatabase.NetWeight;

import event.ButterWorthFilter;
import event.ButterWorthFilter;
import mathUtil.DoubleUtil;
import utils.Config;

public class TcMagTest {

	public static void main(String[] args) {
		File folder = new File("E:\\2019\\GNSS\\JP\\JPTest\\强震仪数据");
		if (!folder.exists() || !folder.isDirectory()) {
			return;
		}
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			String stationID = file.getName().substring(0, 6);
//			if (stationID.equals("MYG001")) {
//				st(file,14.44);
////				st(file,14.44);
////				st(file,10.01);
//			}
			if (stationID.equals("MYG011")) {
				st(file,10.98);
//				st(file,14.98);
			}
			
		}
	}
	private static void st(File file, double startT) {
		String stationID = file.getName().substring(0, 6);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String tmpLine = null;
		List<Double> udList = new ArrayList<>();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		int count = 0;
		int lineNum = (int)(startT*100);
		try {
			while ((tmpLine = reader.readLine())!= null) {
				count++;
//				System.out.println(tmpLine);
				String[] items = tmpLine.split(",");
//				System.out.println(ud);
//				Date curDate = format.parse(items[1]);
//				double t = Double.parseDouble(items[1]);
				if (count>=lineNum && count < lineNum+3*100) {
//					System.out.println(tmpLine);
					double ud = Double.parseDouble(items[4]);
//					System.out.println("add ud: "+count+" "+ud);
					udList.add(ud);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("udList:"+udList.size());
		List<Double> vList = new ArrayList<>();//对加速度积分，获取速度
		List<Double> dList = new ArrayList<>();//对速度积分，获取位移
		Double[] dBWHighPass = null;//对位移滤波，获取滤波后的位移数组
		List<Double> vList2 = new ArrayList<>();//对滤波后的位移进行微分，获取速度序列
		double memsHz = (double)Config.MEMSHz;
		int scale = 15;
		double sumDBW = 0;
		double sumV2 = 0;
		for (int i = 0; i < udList.size(); i++) {
			if (i == 0) {
				double v = DoubleUtil.div(udList.get(0), memsHz, scale);
				vList.add(v);
				double d = DoubleUtil.div(v, memsHz, scale);
				dList.add(d);
				System.out.println(i+" "+d);
			}else {
				double v = DoubleUtil.add(vList.get(i-1), DoubleUtil.div(udList.get(i), memsHz, scale));
				vList.add(v);
				double d = DoubleUtil.add(dList.get(i-1), DoubleUtil.div(vList.get(i), memsHz, scale));
				dList.add(d);
				System.out.println(i+" "+d);
			}
		}
		dBWHighPass = ButterWorthFilter.highpassFilter(dList);
		for (int i = 0; i < dBWHighPass.length; i++) {
			if (i == 0) {
				double v2 = DoubleUtil.mul(dBWHighPass[0], memsHz);
				vList2.add(v2);
				sumDBW+=(DoubleUtil.mul(dBWHighPass[0], dBWHighPass[0]));
				sumV2+=(DoubleUtil.mul(v2, v2));
			}else {
				double v2 = DoubleUtil.mul(DoubleUtil.sub(dBWHighPass[i], dBWHighPass[i-1]), memsHz);
				vList2.add(v2);
				sumDBW+=(DoubleUtil.mul(dBWHighPass[i], dBWHighPass[i]));
				sumV2+=(DoubleUtil.mul(v2, v2));
			}
		}
		double r = DoubleUtil.div(sumV2, sumDBW, scale);
		double tc = DoubleUtil.div(2*Math.PI, Math.sqrt(r), scale);
		double memsMag = calMEMSMag(tc);
		System.out.println("udList - "+udList);
		System.out.println("vList - "+vList);
		System.out.println("dList - "+dList);
		System.out.println("dBWHighPass:"+dBWHighPass);
		System.out.println("vList2:"+vList2);
		System.out.println("sumV2:"+sumV2);
		System.out.println("sumDBW:"+sumDBW);
		System.out.println("r:" + r);
		System.out.println("tc:" + tc);
		System.out.println(stationID+" MEMSMag:"+memsMag);
//		System.out.println(stationID+" MEMSMag:"+memsMag1+" - "+memsMag2);
	}
	private static double calMEMSMag(double tc) {
		if (tc<=0) {
			return 0;
		}
		return DoubleUtil.mul(3.57, Math.log10(tc))+5.29;
	}
	private static double calMEMSMag1(double tc) {
		if (tc<=0) {
			return 0;
		}
		return DoubleUtil.mul(3.57, Math.log10(tc))+5.29-0.50;
	}
	private static double calMEMSMag2(double tc) {
		if (tc<=0) {
			return 0;
		}
		return DoubleUtil.mul(3.57, Math.log10(tc))+5.29+0.50;
	}
}
