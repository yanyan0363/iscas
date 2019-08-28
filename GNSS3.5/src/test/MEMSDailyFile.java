package test;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dbHelper.DBHelper;
import dbHelper.GNSSDBHelper;

public class MEMSDailyFile {

	private MEMSDailyFile() {
	}
	static DateFormat format = new SimpleDateFormat("yyyyMMdd");
	static DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
	public static void main(String[] args) {
//		executeMEMSDaily();
		executeMEMSDailyNDaysBefore(4);
	}
	public static void executeMEMSDaily() {
		Calendar now = Calendar.getInstance();
		if (now.get(Calendar.DAY_OF_WEEK)==2) {//周一
			//前三天：周五、周六、周日
			for(int i =0;i<3;i++){
				String s2 = format2.format(now.getTime());
				//前一天
				now.add(Calendar.DAY_OF_YEAR, -1);
				MEMSDailyFile.sysLength("C:\\arcgis\\ArcGISServerData\\file\\MEMSFolder", format.format(now.getTime()));//C:\\Users\\arcgis\\Desktop\\
				String s1 = format2.format(now.getTime());
				MEMSDailyFile.sysStPT(s1, s2);
			}
		}else {
			String s2 = format2.format(now.getTime());
			//前一天
			now.add(Calendar.DAY_OF_YEAR, -1);
			MEMSDailyFile.sysLength("C:\\arcgis\\ArcGISServerData\\file\\MEMSFolder", format.format(now.getTime()));
			String s1 = format2.format(now.getTime());
			MEMSDailyFile.sysStPT(s1, s2);
		}
	}
	/**
	 * 获取前n天数据统计
	 * @param nDays
	 */
	public static void executeMEMSDailyNDaysBefore(int nDays) {
		Calendar now = Calendar.getInstance();
		//前三天：周五、周六、周日
		for(int i =0;i<nDays;i++){
			String s2 = format2.format(now.getTime());
			//前一天
			now.add(Calendar.DAY_OF_YEAR, -1);
			MEMSDailyFile.sysLength("C:\\arcgis\\ArcGISServerData\\file\\MEMSFolder", format.format(now.getTime()));//C:\\Users\\arcgis\\Desktop\\
			String s1 = format2.format(now.getTime());
			MEMSDailyFile.sysStPT(s1, s2);
		}
	}
	
	/**
	 * 输出台站PT
	 * @param day1 指定日期yyyy-MM-dd
	 * @param day2 指定日期yyyy-MM-dd
	 */
	public static void sysStPT(String day1, String d2) {
		String sql = "select Station,PT from areport3_1 where PT>='"+day1+" 00:00:00.000' and PT<'"+d2+" 00:00:00.000' ORDER BY Station asc,PT asc;";
		System.out.println(sql);
		ResultSet resultSet=DBHelper.runQuerySql(sql);
		String station="";
		try {
			if (resultSet!=null && !resultSet.isAfterLast()) {
				while (resultSet.next()) {
					if (!station.equalsIgnoreCase(resultSet.getString("Station"))) {
						station=resultSet.getString("Station");
						System.out.println(station);
					}
					System.out.println(resultSet.getString("PT"));
				}
			}else{
				System.out.println("StPT is null.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 
	 * @param folderPath
	 * @param day yyyyMMdd
	 * @return
	 */
	public static void sysLength(String folderPath, String day) {
		System.out.println(folderPath);
		System.out.println(day);
		File folder = new File(folderPath);
		if (!folder.exists() || !folder.isDirectory()) {
			System.out.println("folder error.");
			return ;
		}
		File[] folders = folder.listFiles();
		double sum = 0;
		for (File stFolder : folders) {
			FilenameFilter filenameFilter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.contains(day)) {
						return true;
					}
					return false;
				}
			};
			File[] stFiles = stFolder.listFiles(filenameFilter);
			if (stFiles == null) {
//				System.out.println(stFolder.getName()+" stFiles == null, return;");
				continue;
			}
			double size = 0;
			for (File stFile : stFiles) {
				long length = stFile.length();
				size+=(length/=1024);
//				System.out.println(stFile.getName()+":"+length);
			}
			sum+=(size/1024);
			System.out.println(stFolder.getName()+"—"+String.format("%.1f", size/1024)+"M");
		}
		System.out.println(day+":"+String.format("%.1f", sum)+"M");
	}

}
