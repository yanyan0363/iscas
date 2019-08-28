package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class XMLSaveAsDoc {

	public static void main(String[] args) {
		String source = "E:\\2019\\GNSS\\监控日志\\GNSS预警系统运行监控日志_20190218-20190224.xml";
		String target = "E:\\2019\\GNSS\\监控日志\\GNSS预警系统运行监控日志_20190218-20190224.doc";
		CopyFile(new File(source), new File(target));
	}
	public static void CopyFile(File in, File out){
		try {
			FileInputStream fis = new FileInputStream(in);
			FileOutputStream fos = new FileOutputStream(out);
			byte[] buf = new byte[1024];
			int i = 0;
			while((i=fis.read(buf))!=-1) {
				fos.write(buf, 0, i);
			}
			fis.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}
