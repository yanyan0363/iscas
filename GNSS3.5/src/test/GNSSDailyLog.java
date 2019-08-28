package test;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import dbHelper.DBHelper;

public class GNSSDailyLog {

	private GNSSDailyLog() {
	}
	
	static String recorder = "魏闫艳";
	static String memoryM = "70M";
	static String CPUPercent = "25%";
	
	static DateFormat format = new SimpleDateFormat("yyyyMMdd");
	static DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
	static Set<String> allMEMSSts = new HashSet<>();
	static Set<String> allGPSSts = new HashSet<>();
	static Date lastDay = null;
	static String lastDay1 = "";
	static String nowDay2 = "";
	static String lastDay2 = "";
	static String demoXML = "E:\\2019\\GNSS\\监控日志\\GNSS-log.xml";
	static Element MEMSInNumElement = null;
	static Element MEMSInStsElement = null;
	static Element MEMSOutNumElement = null;
	static Element MEMSOutStsElement = null;
	static Element MEMSMMPElement = null;
	static Element MEMSTotalElement = null;
	static Element GPSInNumElement = null;
	static Element GPSInStsElement = null;
	static Element GPSOutNumElement = null;
	static Element GPSOutStsElement = null;
	static Element GPSMMPElement = null;
	static Element GPSTotalElement = null;
	static Element dayTitle = null;
	

	public static void main(String[] args) {
		exportGNSSDailyLog();
	}
	public static void exportGNSSDailyLog() {
		initInfo();
		File demo = new File(demoXML);
		SAXReader saxReader = new SAXReader();
		Document document;
		try {
			document = saxReader.read(demo);
			Element node = document.getRootElement();
			listNodes(node);
			if (dayTitle != null) {
				dayTitle.setText(lastDay2);
			}
			fillMEMSInfo("C:\\arcgis\\ArcGISServerData\\file\\MEMSFolder",lastDay1);
			fillGPSInfo("C:\\arcgis\\ArcGISServerData\\file\\GPSFolder",lastDay1);
			writer(document);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void initInfo() {
		allMEMSSts.add("SMTWX");
		allMEMSSts.add("SMFYX");
		allMEMSSts.add("SMXJZ");
		allMEMSSts.add("SMCLX");
		allMEMSSts.add("SMMYX");
		allMEMSSts.add("SMPGX");
		allMEMSSts.add("SMBTX");
		allMEMSSts.add("SMHGZ");
		allMEMSSts.add("SMZSZ");
		allMEMSSts.add("SMMNX");
		allGPSSts.add("SMTWX");
		allGPSSts.add("SMFYX");
		allGPSSts.add("SMXJZ");
		allGPSSts.add("SMCLX");
		allGPSSts.add("SMMYX");
		allGPSSts.add("SMPGX");
		allGPSSts.add("SMBTX");
		allGPSSts.add("SMHGZ");
		allGPSSts.add("SMZSZ");
		allGPSSts.add("SMMNX");
		//HBDGL,HBDMB,HBDLG,HBDXC,HBDZJ,ZBDHY,ZBDLW,ZBDYX,ZBDXD,ZBDMG
		allGPSSts.add("HBDGL");
		allGPSSts.add("HBDMB");
		allGPSSts.add("HBDLG");
		allGPSSts.add("HBDXC");
		allGPSSts.add("HBDZJ");
		allGPSSts.add("ZBDHY");
		allGPSSts.add("ZBDLW");
		allGPSSts.add("ZBDYX");
		allGPSSts.add("ZBDXD");
		allGPSSts.add("ZBDMG");
		Calendar now = Calendar.getInstance();
		nowDay2 = format2.format(now.getTime());
		now.add(Calendar.DAY_OF_YEAR, -1);
		lastDay = now.getTime();
		lastDay1 = format.format(lastDay);
		lastDay2 = format2.format(lastDay);
	}
	
    public static void writer(Document document) {  
        // 绱у噾鐨勬牸寮�  
        // OutputFormat format = OutputFormat.createCompactFormat();  
        // 鎺掔増缂╄繘鐨勬牸寮�  
        OutputFormat format = OutputFormat.createPrettyPrint();  
        // 璁剧疆缂栫爜  
        format.setEncoding("UTF-8");  
        // 鍒涘缓XMLWriter瀵硅薄,鎸囧畾浜嗗啓鍑烘枃浠跺強缂栫爜鏍煎紡  
		try {
			XMLWriter writer = new XMLWriter(new FileWriter(new File("C:\\arcgis\\监控日志\\"+lastDay1+".xml")),format);
			// 鍐欏叆  
			writer.write(document);  
			// 绔嬪嵆鍐欏叆  
			writer.flush();  
			// 鍏抽棴鎿嶄綔  
			writer.close();  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
    }

	public static void listNodes(Element node) {
		if (dayTitle == null && node.getName().equals("t") && node.getText().equals("DAY(yyyy-MM-dd)")) {
			dayTitle = node;
		}else if (node.getName().equals("tbl")) {
			List<Element> elements = node.elements("tr");
			for (int i = 0; i < elements.size(); i++) {
				Element element = elements.get(i);
//				System.out.println("id::"+element.attribute("id"));
				if (element.attribute("id")!=null && element.attribute("id").getStringValue().equals("sts")) {
					System.out.println("id='sts'... :: "+i);
					fillStPT(elements, i,lastDay2, nowDay2);
					break;
				}
			}
		}else if (node.getName().equals("tc")){
			System.out.println("----------------enter tc------------------");
			Element pElement = node.element("p");
			if (pElement != null) {
				List<Element> rElements = pElement.elements("r");
				for (Element rElement : rElements) {
					if (rElement != null) {
						Element tElement = rElement.element("t");
						if (tElement != null) {
							String nodeText = tElement.getText().replaceAll(" ", "");
							System.out.println("nodeText::"+nodeText);
							if (nodeText.equalsIgnoreCase("RecordDay")) {
								tElement.setText(lastDay2);
							}else if (nodeText.equalsIgnoreCase("Recorder")) {
								tElement.setText(recorder);
							}else if (nodeText.equalsIgnoreCase("memoryM")) {
								tElement.setText(memoryM);
							}else if (nodeText.equalsIgnoreCase("CPUPercent")) {
								tElement.setText(CPUPercent);
							}else if (nodeText.equalsIgnoreCase("MEMSInNum")) {
								MEMSInNumElement = tElement;
							}else if (nodeText.equalsIgnoreCase("MEMSInSts")) {
								MEMSInStsElement = tElement;
							}else if (nodeText.equalsIgnoreCase("MEMSOutNum")) {
								MEMSOutNumElement = tElement;
							}else if (nodeText.equalsIgnoreCase("MEMSOutSts")) {
								MEMSOutStsElement = tElement;
							}else if (nodeText.equalsIgnoreCase("MEMSMM")) {
								MEMSMMPElement = tElement.getParent().getParent();
							}else if (nodeText.equalsIgnoreCase("MEMSTotal")) {
								MEMSTotalElement = tElement;
							}else if (nodeText.equalsIgnoreCase("GPSInNum")) {
								GPSInNumElement = tElement;
							}else if (nodeText.equalsIgnoreCase("GPSInSts")) {
								GPSInStsElement = tElement;
							}else if (nodeText.equalsIgnoreCase("GPSOutNum")) {
								GPSOutNumElement = tElement;
							}else if (nodeText.equalsIgnoreCase("GPSOutSts")) {
								GPSOutStsElement = tElement;
							}else if (nodeText.equalsIgnoreCase("GPSMM")) {
								GPSMMPElement = tElement.getParent().getParent();
							}else if (nodeText.equalsIgnoreCase("GPSTotal")) {
								GPSTotalElement = tElement;
							}
						}
					}
				}
			}
		}
		
		// 褰撳墠鑺傜偣涓嬮潰瀛愯妭鐐硅凯浠ｅ櫒  
	    Iterator<Element> it = node.elementIterator();  
	    // 閬嶅巻  
	    while (it.hasNext()) {  
	       // 鑾峰彇鏌愪釜瀛愯妭鐐瑰璞�  
	       Element e = it.next();  
	       // 瀵瑰瓙鑺傜偣杩涜閬嶅巻  
	       listNodes(e);  
	    }
	}
	
	/**
	 * 
	 * @param folderPath
	 * @param day yyyyMMdd
	 * @return
	 */
	public static void fillMEMSInfo(String folderPath, String day) {
		System.out.println(folderPath);
		System.out.println(day);
		File folder = new File(folderPath);
		if (!folder.exists() || !folder.isDirectory()) {
			System.out.println("folder error.");
			return ;
		}
		File[] folders = folder.listFiles();
		double sum = 0;
		Element fatherNodeTC = MEMSMMPElement.getParent();
		fatherNodeTC.remove(MEMSMMPElement);
		int inNum = 0;
		String inSts = "";
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
				continue;
			}
			double size = 0;
			for (File stFile : stFiles) {
				long length = stFile.length();
				size+=(length/=1024);
			}
			sum+=(size/1024);
			if (size > 0) {
				inNum++;
				inSts += stFolder.getName()+", ";
				allMEMSSts.remove(stFolder.getName());
				System.out.println(stFolder.getName()+"—"+String.format("%.1f", size/1024)+"M");
				addStFileM(fatherNodeTC, stFolder.getName()+"—"+String.format("%.1f", size/1024)+"M");
			}
		}
		MEMSTotalElement.setText(String.format("%.1f", sum)+"M");
		if (sum <= 0) {
			addStFileM(fatherNodeTC, "");
		}
		MEMSInNumElement.setText(inNum+"");
		if (inSts.endsWith(", ")) {
			inSts = inSts.substring(0, inSts.lastIndexOf(", "));
		}
		MEMSInStsElement.setText(inSts);
		MEMSOutNumElement.setText(allMEMSSts.size()+"");
		MEMSOutStsElement.setText(allMEMSSts.toString().replace("[", "").replace("]", ""));
		System.out.println(day+":"+String.format("%.1f", sum)+"M");
	}
	/**
	 * 
	 * @param folderPath
	 * @param day yyyyMMdd
	 * @return
	 */
	public static void fillGPSInfo(String folderPath, String day) {
		System.out.println(folderPath);
		System.out.println(day);
		File folder = new File(folderPath);
		if (!folder.exists() || !folder.isDirectory()) {
			System.out.println("folder error.");
			return ;
		}
		File[] folders = folder.listFiles();
		double sum = 0;
		Element fatherNodeTC = GPSMMPElement.getParent();
		fatherNodeTC.remove(GPSMMPElement);
		int inNum = 0;
		String inSts = "";
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
				continue;
			}
			double size = 0;
			for (File stFile : stFiles) {
				long length = stFile.length();
				size+=(length/=1024);
			}
			sum+=(size/1024);
			if (size > 0) {
				inNum++;
				inSts += stFolder.getName()+", ";
				allGPSSts.remove(stFolder.getName());
				System.out.println(stFolder.getName()+"—"+String.format("%.1f", size/1024)+"M");
				addStFileM(fatherNodeTC, stFolder.getName()+"—"+String.format("%.1f", size/1024)+"M");
			}
		}
		GPSTotalElement.setText(String.format("%.1f", sum)+"M");
		if (sum <= 0) {
			addStFileM(fatherNodeTC, "");
		}
		GPSInNumElement.setText(inNum+"");
		if (inSts.endsWith(", ")) {
			inSts = inSts.substring(0, inSts.lastIndexOf(", "));
		}
		GPSInStsElement.setText(inSts);
		GPSOutNumElement.setText(allGPSSts.size()+"");
		GPSOutStsElement.setText(allGPSSts.toString().replace("[", "").replace("]", ""));
		System.out.println(day+":"+String.format("%.1f", sum)+"M");
	}

	private static void addStFileM(Element fatherNode, String text) {
		Element wPElement = MEMSMMPElement.createCopy();
		fatherNode.add(wPElement);
		Element wrElement = wPElement.element("r");
		Element wtElement = wrElement.element("t");
		wtElement.setText(text);
	}
//	private static void addMEMSM(Element fatherNode, String text) {
//		Element wPElement = fatherNode.addElement("w:p");
//		Element pPr = wPElement.addElement("w:pPr");
//		Element jc = pPr.addElement("w:jc");
//		jc.addAttribute("w:val", "center");
//		Element rPr = pPr.addElement("w:rPr");
//		Element sz = rPr.addElement("w:sz");
//		sz.addAttribute("w:val", "24");
//		Element szCs = rPr.addElement("w:szCs");
//		szCs.addAttribute("w:val", "24");
//		Element wrElement = wPElement.addElement("w:r");
//		wrElement.addAttribute("w:rsidRPr", "009455E1");
//		Element rPr2 = wrElement.addElement("w:rPr");
//		Element sz2 = rPr2.addElement("w:sz");
//		sz2.addAttribute("w:val", "24");
//		Element szCs2 = rPr2.addElement("w:szCs");
//		szCs2.addAttribute("w:val", "24");
//		Element wtElement = wrElement.addElement("w:t");
//		wtElement.setText(text);
//	}
	/**
	 * 杈撳嚭鍙扮珯PT
	 * @param day1 鎸囧畾鏃ユ湡yyyy-MM-dd
	 * @param lastDay2 鎸囧畾鏃ユ湡yyyy-MM-dd
	 */
	public static void fillStPT(List<Element> elements, int idx , String day1, String d2) {
		String sql = "select Station,PT from areport3_1 where PT>='"+day1+" 00:00:00.000' and PT<'"+d2+" 00:00:00.000' ORDER BY Station asc,PT asc;";
		System.out.println(sql);
		ResultSet resultSet=DBHelper.runQuerySql(sql);
		String station="";
		Element trElement = elements.get(idx);
		Element stPtsElement = null;
		Element stPt = null;
		try {
			if (resultSet!=null && !resultSet.isAfterLast()) {
				while (resultSet.next()) {
					if (!station.equalsIgnoreCase(resultSet.getString("Station"))) {
						if (station == "") {
							elements.remove(trElement);
							idx--;
						}
						station=resultSet.getString("Station");
						System.out.println(station);
						Element stPtElement = trElement.createCopy();
						elements.add(++idx, stPtElement);
						List<Element> tcList = stPtElement.elements("tc");
						for (Element element : tcList) {
							Attribute attribute = element.attribute("id");
							if (attribute==null) {
								continue;
							}
							System.out.println(attribute.getValue());
							if ("stName".equals(attribute.getValue())) {
								Element stName = element.element("p").element("r").element("t");
								stName.setText(station);
							}else if ("stPts".equals(attribute.getValue())) {
								stPtsElement = element;
								stPt = stPtsElement.element("p");
								stPtsElement.remove(stPt);
							}
						}
					}
					System.out.println(stPtsElement != null);
					System.out.println(stPt != null);
					if (stPtsElement != null && stPt != null) {
						Element newStPt = stPt.createCopy();
						newStPt.element("r").element("t").setText(resultSet.getString("PT"));
						stPtsElement.add(newStPt);
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
	
}
