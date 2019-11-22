package helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import dbHelper.DBHelper;
import dbHelper.GNSSDBHelper;
import mathUtil.DoubleUtil;
import utils.Config;
import utils.StringHelper;

public class DispHelper {
	private static SimpleDateFormat formatMs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static SimpleDateFormat formatMs2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	private static SimpleDateFormat formatS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 台站被触发前后一分钟波形数据
	 * @param PT
	 * @param stationID
	 * @return
	 */
	public static String getDispByEQSt(String PT, String stationID) {
		Date eqT = null;
		try {
			eqT = formatMs.parse(PT);
		} catch (ParseException e1) {
			e1.printStackTrace();
			return "{}";
		}
		Date startT = new Date(eqT.getTime()-60*1000);//台站触发一分钟前
		Date endT = new Date(eqT.getTime()+60*1000);//台站触发一分钟后
		System.out.println("startT -- "+formatMs.format(startT));
		System.out.println("endT -- "+formatMs.format(endT));
		String res = initData(stationID, startT, endT);
		return res;
	}
	private static String initData(String stationID, Date startT, Date endT) {
		StringBuilder GPSEW = new StringBuilder("\"GPSEW\":[");
		StringBuilder GPSNS = new StringBuilder("\"GPSNS\":[");
		StringBuilder GPSZ = new StringBuilder("\"GPSZ\":[");
		StringBuilder MEMSEW = new StringBuilder("\"MEMSEW\":[");
		StringBuilder MEMSNS = new StringBuilder("\"MEMSNS\":[");
		StringBuilder MEMSZ = new StringBuilder("\"MEMSZ\":[");
		StringBuilder MEMSEWAcc = new StringBuilder("\"MEMSEWAcc\":[");
		StringBuilder MEMSNSAcc = new StringBuilder("\"MEMSNSAcc\":[");
		StringBuilder MEMSZAcc = new StringBuilder("\"MEMSZAcc\":[");
		BufferedReader readerGPS = null;
		FileReader fileReaderGPS = null;
		BufferedReader readerMEMS = null;
		FileReader fileReaderMEMS = null;
		String gpsFolder = Config.GPSFolder;
		String memsFolder = Config.MEMSFolder;
		boolean resGPS = checkStFolder(gpsFolder, stationID);
		boolean resMEMS = checkStFolder(gpsFolder, stationID);
		if (!resGPS) {
			System.out.println("GPS Folder does not exist, return false.");
//			return "{}";
		}else {
			String line = null;
			boolean endFlag = false;
			Date curFileT = startT;
			while (!endFlag) {
				readerGPS = getReader(gpsFolder, stationID, curFileT,fileReaderGPS);
				if (readerGPS == null) {
					if (curFileT != endT) {
						curFileT = endT;
						continue;
					}else {
						break;
					}
				}
				readerMEMS = getReader(memsFolder, stationID, curFileT,fileReaderMEMS);
				try {
					while ((line = readerGPS.readLine()) != null && line != "") {
						String[] items = line.split(",");
						if (items.length != 9) {
							continue;
						}
						String tString = items[1];//.substring(0, 19);
						try {
							Date curT = formatMs2.parse(tString);
							if (curT.before(startT)) {
								continue;
							}
							if (curT.after(endT)) {
								endFlag = true;
								break;
							}
							double dx = Double.parseDouble(items[5]);
							double dy = Double.parseDouble(items[6]);
							double dz = Double.parseDouble(items[7]);
							//GPS
							GPSEW.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dx)+"]},");
							GPSNS.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dy)+"]},");
							GPSZ.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dz)+"]},");
							//MEMS拟合
							boolean memsRes = getMemsNiheData(readerMEMS, curT, stationID, dx, dy, dz, MEMSEW, MEMSNS, MEMSZ);
							if (!memsRes) {
								System.out.println("读取MEMS数据拟合出错");
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
//					return "{}";
				}
				closeReader(fileReaderGPS, readerGPS);
				closeReader(fileReaderMEMS, readerMEMS);
				curFileT = endT;
			}
		}
		getMEMSData(stationID, memsFolder, startT, endT, MEMSEWAcc, MEMSNSAcc, MEMSZAcc);
		if (GPSEW.lastIndexOf(",")>0) {
			GPSEW.deleteCharAt(GPSEW.lastIndexOf(","));
			GPSNS.deleteCharAt(GPSNS.lastIndexOf(","));
			GPSZ.deleteCharAt(GPSZ.lastIndexOf(","));
		}
		if (MEMSEW.lastIndexOf(",")>0) {
			MEMSEW.deleteCharAt(MEMSEW.lastIndexOf(","));
			MEMSNS.deleteCharAt(MEMSNS.lastIndexOf(","));
			MEMSZ.deleteCharAt(MEMSZ.lastIndexOf(","));
		}
		if (MEMSEWAcc.lastIndexOf(",")>0) {
			MEMSEWAcc.deleteCharAt(MEMSEWAcc.lastIndexOf(","));
			MEMSNSAcc.deleteCharAt(MEMSNSAcc.lastIndexOf(","));
			MEMSZAcc.deleteCharAt(MEMSZAcc.lastIndexOf(","));
		}
		GPSEW.append("]");
		GPSNS.append("]");
		GPSZ.append("]");
		MEMSEW.append("]");
		MEMSNS.append("]");
		MEMSZ.append("]");
		MEMSEWAcc.append("]");
		MEMSNSAcc.append("]");
		MEMSZAcc.append("]");
		return "{"+GPSEW.toString()+","+GPSNS.toString()+","+GPSZ.toString()+","+MEMSEW.toString()+","+MEMSNS.toString()+","+MEMSZ.toString()+","
		+MEMSEWAcc.toString()+","+MEMSNSAcc.toString()+","+MEMSZAcc.toString()+"}";
	}
	/**
	 * 震后一分钟波形数据
	 * @param eqTime
	 * @param stationID
	 * @return
	 */
	public static String getDispByEQTSt(String eqTime, String stationID) {
		Date eqT = null;
		try {
			eqT = formatS.parse(eqTime);
		} catch (ParseException e1) {
			e1.printStackTrace();
			return "{}";
		}
		Date endT = new Date(eqT.getTime()+60*1000);//震后一分钟后
		String sql = "select * from records where stationID='"+stationID+"' and time>='"+formatS.format(eqT)+"' and time<'"+formatS.format(endT)+"' group by time order by time asc;";
		System.out.println(sql);
		StringBuilder GPSEW = new StringBuilder("\"GPSEW\":[");
		StringBuilder GPSNS = new StringBuilder("\"GPSNS\":[");
		StringBuilder GPSZ = new StringBuilder("\"GPSZ\":[");
		StringBuilder MEMSEW = new StringBuilder("\"MEMSEW\":[");
		StringBuilder MEMSNS = new StringBuilder("\"MEMSNS\":[");
		StringBuilder MEMSZ = new StringBuilder("\"MEMSZ\":[");
		StringBuilder MEMSEWAcc = new StringBuilder("\"MEMSEWAcc\":[");
		StringBuilder MEMSNSAcc = new StringBuilder("\"MEMSNSAcc\":[");
		StringBuilder MEMSZAcc = new StringBuilder("\"MEMSZAcc\":[");
		String memsFolder = Config.MEMSFolder;
		boolean res = checkStFolder(memsFolder, stationID);
		ResultSet resultSet = DBHelper.runQuerySql(sql);
		
		BufferedReader reader = null;
		FileReader fileReader = null;
		long curMinute = eqT.getTime()/60000;
		
		if (resultSet != null) {
			try {
				while(resultSet.next() && !resultSet.isAfterLast()){
					String tString = resultSet.getString("time");
					double dx = resultSet.getDouble("dx");
					double dy = resultSet.getDouble("dy");
					double dz = resultSet.getDouble("dz");
					GPSEW.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dx)+"]},");
					GPSNS.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dy)+"]},");
					GPSZ.append("{\"name\":\""+tString+"\",\"value\":[\""+tString+"\","+String.format("%.3f", dz)+"]},");
					if(res){
						//MEMS
						Date curT;
						try {
							curT = formatMs.parse(tString);
						} catch (ParseException e) {
							e.printStackTrace();
							continue;
						}
						long mint = curT.getTime()/60000;
						if (reader == null || mint != curMinute) {
							closeReader(fileReader, reader);
							reader = getReader(memsFolder, stationID, curT,fileReader);
							curMinute = mint;
						}
						boolean memsRes = getMemsData(reader, curT, stationID, dx, dy, dz, MEMSEW, MEMSNS, MEMSZ, MEMSEWAcc, MEMSNSAcc, MEMSZAcc);
//						boolean memsRes = getMemsData(memsFolder, curT, stationID, dx, dy, dz, MEMSEW, MEMSNS, MEMSZ);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return "{}";
			}
		}
		if (GPSEW.lastIndexOf(",")>0) {
			GPSEW.deleteCharAt(GPSEW.lastIndexOf(","));
			GPSNS.deleteCharAt(GPSNS.lastIndexOf(","));
			GPSZ.deleteCharAt(GPSZ.lastIndexOf(","));
		}
		if (MEMSEW.lastIndexOf(",")>0) {
			MEMSEW.deleteCharAt(MEMSEW.lastIndexOf(","));
			MEMSNS.deleteCharAt(MEMSNS.lastIndexOf(","));
			MEMSZ.deleteCharAt(MEMSZ.lastIndexOf(","));
		}
		if (MEMSEWAcc.lastIndexOf(",")>0) {
			MEMSEWAcc.deleteCharAt(MEMSEWAcc.lastIndexOf(","));
			MEMSNSAcc.deleteCharAt(MEMSNSAcc.lastIndexOf(","));
			MEMSZAcc.deleteCharAt(MEMSZAcc.lastIndexOf(","));
		}
		GPSEW.append("]");
		GPSNS.append("]");
		GPSZ.append("]");
		MEMSEW.append("]");
		MEMSNS.append("]");
		MEMSZ.append("]");
		MEMSEWAcc.append("]");
		MEMSNSAcc.append("]");
		MEMSZAcc.append("]");
		return "{"+GPSEW.toString()+","+GPSNS.toString()+","+GPSZ.toString()+","+MEMSEW.toString()+","+MEMSNS.toString()+","+MEMSZ.toString()+","
		+MEMSEWAcc.toString()+","+MEMSNSAcc.toString()+","+MEMSZAcc.toString()+"}";
//		return "{"+GPSEW.toString()+","+GPSNS.toString()+","+GPSZ.toString()+","+getMemsData(eqT, endT, stationID)+"}";
	}
	private static String getMemsData(Date eqT, Date endT, String stationID){
		StringBuilder MEMSEW = new StringBuilder("\"MEMSEW\":[");
		StringBuilder MEMSNS = new StringBuilder("\"MEMSNS\":[");
		StringBuilder MEMSZ = new StringBuilder("\"MEMSZ\":[");
//		String memsFolder = Property.getStringProperty("MEMSFolder");
		String memsFolder = Config.MEMSFolder;
		boolean res = checkStFolder(memsFolder, stationID);
		if (!res) {
			return "\"MEMSEW\":[],\"MEMSNS\":[],\"MEMSZ\":[]";
		}
		String stFile = memsFolder+stationID+"\\"+mkFileName(stationID, eqT);
		String stEndFile = memsFolder+stationID+"\\"+mkFileName(stationID, endT);
		long eqSecond = eqT.getTime()/1000;
		long lastSecond = eqSecond;
		double dx = 0, dy = 0, dz = 0;
		FileReader fileReader = null;
		BufferedReader reader = null;
		String line;
		while (true) {
			try {
				fileReader = new FileReader(stFile);
				reader = new BufferedReader(fileReader);
			} catch (FileNotFoundException e) {
				try {
					fileReader.close();
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return "\"MEMSEW\":[],\"MEMSNS\":[],\"MEMSZ\":[]";
			}
			try {
				line = reader.readLine();
				while (line != null && line != "") {
					String[] array = line.split(",");
					if (array.length != 6) {
						line = reader.readLine();
						continue;
					}
					if (!array[0].equals(stationID)) {
						line = reader.readLine();
						continue;
					}
					Date curDate = null;
					try {
						curDate = formatMs2.parse(array[1]);
					} catch (ParseException e) {
						e.printStackTrace();
						line = reader.readLine();
						continue;
					}
					if (curDate.before(eqT)) {
						line = reader.readLine();
						continue;
					}
					if (curDate.after(endT)) {
						break;
					}
					if (StringHelper.isNumeric(array[2])&&StringHelper.isNumeric(array[3])&&StringHelper.isNumeric(array[4])) {
						long curSecond = curDate.getTime()/1000;
						if(curSecond != lastSecond){
							ResultSet resultSet = GNSSDBHelper.getGPSByTSt(stationID, formatS.format(curDate));
							if (resultSet != null) {
								try {
									resultSet.next();
									dx = resultSet.getDouble("dx");
									dy = resultSet.getDouble("dy");
									dz = resultSet.getDouble("dz");
									lastSecond = curSecond;
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
						long dt = curDate.getTime()-eqT.getTime();
						dx = calDispWithMEMS(dx, dt, Double.parseDouble(array[2]));
						dy = calDispWithMEMS(dy, dt, Double.parseDouble(array[3]));
						dz = calDispWithMEMS(dz, dt, Double.parseDouble(array[4]));
						String tString = array[1];
						MEMSEW.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", dx)+"]},");
						MEMSNS.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", dy)+"]},");
						MEMSZ.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", dz)+"]},");
					}
					line = reader.readLine();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				try {
					fileReader.close();
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return "\"MEMSEW\":[],\"MEMSNS\":[],\"MEMSZ\":[]";
			}
			try {
				fileReader.close();
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (stFile.equals(stEndFile)) {
				break;
			}
			stFile = stEndFile;
		}
		if (MEMSEW.lastIndexOf(",")>0) {
			MEMSEW.deleteCharAt(MEMSEW.lastIndexOf(","));
			MEMSNS.deleteCharAt(MEMSNS.lastIndexOf(","));
			MEMSZ.deleteCharAt(MEMSZ.lastIndexOf(","));
		}
		MEMSEW.append("]");
		MEMSNS.append("]");
		MEMSZ.append("]");
		return MEMSEW.toString()+","+MEMSNS.toString()+","+MEMSZ.toString();
	}
	private static BufferedReader getReader(String folder, String stationID, Date curT,FileReader fileReader){
		String stFile = folder+stationID+"\\"+mkFileName(stationID, curT);
		try {
			fileReader = new FileReader(stFile);
			BufferedReader reader = new BufferedReader(fileReader);
			return reader;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	private static boolean closeReader(FileReader fileReader, BufferedReader bufferedReader){
		if (bufferedReader != null) {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		if (fileReader != null) {
			try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	/*
	 * 获取curT的当前s的MEMS数据
	 */
	private static boolean getMemsData(BufferedReader reader, Date curT, String stationID, double dx, double dy, double dz, 
			StringBuilder MEMSEW, StringBuilder MEMSNS, StringBuilder MEMSZ,
			StringBuilder MEMSEWAcc, StringBuilder MEMSNSAcc, StringBuilder MEMSZAcc){
		Calendar endTime = Calendar.getInstance();
		endTime.setTime(curT);
		endTime.add(Calendar.SECOND, 1);
		Date endT = endTime.getTime();
		try {
			String line = reader.readLine();
			while (line != null && line != "") {
				String[] array = line.split(",");
				if (array.length != 6) {
					line = reader.readLine();
					continue;
				}
				if (!array[0].equals(stationID)) {
					line = reader.readLine();
					continue;
				}
				Date curDate = null;
				try {
					curDate = formatMs2.parse(array[1]);
				} catch (ParseException e) {
					e.printStackTrace();
					line = reader.readLine();
					continue;
				}
				if (curDate.before(curT)) {
					line = reader.readLine();
					continue;
				}
				if (curDate.after(endT)) {
					break;
				}
				if (StringHelper.isNumeric(array[2])&&StringHelper.isNumeric(array[3])&&StringHelper.isNumeric(array[4])) {
					long dt = curDate.getTime()-curT.getTime();
					dx = calDispWithMEMS(dx, dt, Double.parseDouble(array[2]));
					dy = calDispWithMEMS(dy, dt, Double.parseDouble(array[3]));
					dz = calDispWithMEMS(dz, dt, Double.parseDouble(array[4]));
					String tString = array[1];
					MEMSEW.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", dx)+"]},");
					MEMSNS.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", dy)+"]},");
					MEMSZ.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", dz)+"]},");
					MEMSEWAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", Double.parseDouble(array[2]))+"]},");
					MEMSNSAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", Double.parseDouble(array[3]))+"]},");
					MEMSZAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", Double.parseDouble(array[4]))+"]},");
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * 初始化当前时间段内的台站MEMS数据
	 * 当前时间段内的数据分布在<=两个相邻的数据文件内
	 * @param stationID
	 * @param memsFolder
	 * @param startT
	 * @param endT
	 * @param MEMSEWAcc
	 * @param MEMSNSAcc
	 * @param MEMSZAcc
	 * @return
	 */
	private static boolean getMEMSData(String stationID, String memsFolder, Date startT, Date endT,
			StringBuilder MEMSEWAcc, StringBuilder MEMSNSAcc, StringBuilder MEMSZAcc) {
		FileReader fileReader = null;
		boolean isEnded = false;
		Date curT = startT;
		while (!isEnded) {
			BufferedReader reader = getReader(memsFolder, stationID, curT, fileReader);
			try {
				String line = reader.readLine();
				while (line != null && line != "") {
					String[] array = line.split(",");
					if (array.length != 6) {
						line = reader.readLine();
						continue;
					}
//					if (!array[0].equals(stationID)) {
//						line = reader.readLine();
//						continue;
//					}
					Date curDate = null;
					try {
						curDate = formatMs2.parse(array[1]);
					} catch (ParseException e) {
						e.printStackTrace();
						line = reader.readLine();
						continue;
					}
					if (curDate.before(startT)) {
						line = reader.readLine();
						continue;
					}
					if (curDate.after(endT)) {
						isEnded = true;
						break;
					}
					if (StringHelper.isNumeric(array[2])&&StringHelper.isNumeric(array[3])&&StringHelper.isNumeric(array[4])) {
						String tString = array[1];
						MEMSEWAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", Double.parseDouble(array[2]))+"]},");
						MEMSNSAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", Double.parseDouble(array[3]))+"]},");
						MEMSZAcc.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", Double.parseDouble(array[4]))+"]},");
					}
					line = reader.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			closeReader(fileReader, reader);
			curT = endT;
		}
		return true;
	}
	/*
	 * 获取curT的当前s的拟合MEMS数据
	 */
	private static boolean getMemsNiheData(BufferedReader reader, Date curT, String stationID, double dx, double dy, double dz, 
			StringBuilder MEMSEW, StringBuilder MEMSNS, StringBuilder MEMSZ){
		Calendar endTime = Calendar.getInstance();
		endTime.setTime(curT);
		endTime.add(Calendar.SECOND, 1);
		Date endT = endTime.getTime();
		try {
			String line = reader.readLine();
			while (line != null && line != "") {
				String[] array = line.split(",");
				if (array.length != 6) {
					line = reader.readLine();
					continue;
				}
				if (!array[0].equals(stationID)) {
					line = reader.readLine();
					continue;
				}
				Date curDate = null;
				try {
					curDate = formatMs2.parse(array[1]);
				} catch (ParseException e) {
					e.printStackTrace();
					line = reader.readLine();
					continue;
				}
				if (curDate.before(curT)) {
					line = reader.readLine();
					continue;
				}
				if (curDate.after(endT)) {
					break;
				}
				if (StringHelper.isNumeric(array[2])&&StringHelper.isNumeric(array[3])&&StringHelper.isNumeric(array[4])) {
					long dt = curDate.getTime()-curT.getTime();
					dx = calDispWithMEMS(dx, dt, Double.parseDouble(array[2]));
					dy = calDispWithMEMS(dy, dt, Double.parseDouble(array[3]));
					dz = calDispWithMEMS(dz, dt, Double.parseDouble(array[4]));
					String tString = array[1];
					MEMSEW.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", dx)+"]},");
					MEMSNS.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", dy)+"]},");
					MEMSZ.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", dz)+"]},");
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	private static boolean getMemsData(String memsFolder, Date curT, String stationID, double dx, double dy, double dz,StringBuilder MEMSEW, StringBuilder MEMSNS, StringBuilder MEMSZ){
		String stFile = memsFolder+stationID+"\\"+mkFileName(stationID, curT);
		Calendar endTime = Calendar.getInstance();
		endTime.setTime(curT);
		endTime.add(Calendar.SECOND, 1);
		Date endT = endTime.getTime();
		try {
			FileReader fileReader = new FileReader(stFile);
			BufferedReader reader = new BufferedReader(fileReader);
			try {
				String line = reader.readLine();
				while (line != null && line != "") {
					String[] array = line.split(",");
					if (array.length != 6) {
						line = reader.readLine();
						continue;
					}
					if (!array[0].equals(stationID)) {
						line = reader.readLine();
						continue;
					}
					Date curDate = null;
					try {
						curDate = formatMs2.parse(array[1]);
					} catch (ParseException e) {
						e.printStackTrace();
						line = reader.readLine();
						continue;
					}
					if (curDate.before(curT)) {
						line = reader.readLine();
						continue;
					}
					if (curDate.after(endT)) {
						break;
					}
					if (StringHelper.isNumeric(array[2])&&StringHelper.isNumeric(array[3])&&StringHelper.isNumeric(array[4])) {
						long dt = curDate.getTime()-curT.getTime();
						dx = calDispWithMEMS(dx, dt, Double.parseDouble(array[2]));
						dy = calDispWithMEMS(dy, dt, Double.parseDouble(array[3]));
						dz = calDispWithMEMS(dz, dt, Double.parseDouble(array[4]));
						String tString = array[1];
						MEMSEW.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", dx)+"]},");
						MEMSNS.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", dy)+"]},");
						MEMSZ.append("{\"name\":\""+tString+"\",\"value\":"+"[\""+tString+"\","+String.format("%.3f", dz)+"]},");
					}
					line = reader.readLine();
				}
				fileReader.close();
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
			return false;
		}
		return true;
	}
	private static boolean checkStFolder(String folder, String stationID){
		File folderFile = new File(folder);
		if (!folderFile.exists() || !folderFile.isDirectory()) {
			System.out.println("获取台站存储路径出错了，请检查路径：" + folderFile);
			return false;
		}
		String stDir = folder+stationID;
		File stFolder = new File(stDir);
		if (!stFolder.exists() || !stFolder.isDirectory()) {
			System.out.println("获取台站存储路径出错了，请检查路径：" + stDir);
			return false;
		}
		return true;
	}
	/**
	 * 计算加速度位移量
	 * @param lastDisp 前一数据时刻的位移量
	 * @param dt 时间差，单位ms
	 * @param acc 当前时刻的加速度
	 */
	private static double calDispWithMEMS(double lastDisp, long dt, double acc) {
		//S = S0 + 0.5 * acc * Math.pow(10, -9) * 9.8 * dt * dt;
		//首先计算0.5 * Math.pow(10, -9) * 9.8 * dt * dt = 4.9 * Math.pow(10, -9) * dt * dt 
		double halfTT = DoubleUtil.mul(4.9, DoubleUtil.mul(Math.pow(10, -9), Math.pow(dt, 2)));
		return DoubleUtil.add(lastDisp, DoubleUtil.mul(halfTT, acc));
	}
	private static String mkFileName(String stationID, Date time) {
		return stationID+"_"+initTimeSuffix(time);
	}
	private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	private static String initTimeSuffix(Date time) {
		int mIdx = time.getMinutes()/5;//每5min一个文件
		int hours = time.getHours();
		String mIdxStr = "";
		String hoursStr = "";
		if (mIdx < 10) {
			mIdxStr = "0" + mIdx;
		}else {
			mIdxStr = mIdx + "";
		}
		if (hours < 10) {
			hoursStr = "0" + hours;
		}else {
			hoursStr = hours + "";
		}
		return format.format(time)+"_"+hoursStr+"_"+mIdxStr+".txt";
	}
	public static void main(String[] args) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date eqT = null;
		try {
			eqT = format.parse("2017-08-22 14:20:19");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		getDispByEQSt(eqT, "51JLD");
	}
}
