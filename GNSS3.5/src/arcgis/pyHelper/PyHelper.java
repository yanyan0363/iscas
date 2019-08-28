package arcgis.pyHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class PyHelper {

	public static void execPy(String[] paras) {
		Date startTime = new Date();
		try {
			Process proc = Runtime.getRuntime().exec(paras);
			//解析输出
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Date endTime = new Date();
		System.out.print("py start @ " + startTime.toLocaleString());
        System.out.print("  end @ " + endTime.toLocaleString());
        System.out.println("  py耗时： " + (endTime.getTime()-startTime.getTime())/1000+"s");
	}
}
