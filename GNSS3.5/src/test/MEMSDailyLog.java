package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;

import com.esri.arcgis.carto.CenterAndScale;
import com.esri.arcgis.geoprocessing.tools.analyst3dtools.Int;

import dbHelper.DBHelper;
import dbHelper.GNSSDBHelper;
import utils.Config;

public class MEMSDailyLog {

	private MEMSDailyLog() {
	}
	static DateFormat format = new SimpleDateFormat("yyyyMMdd");
	static DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
	static Set<String> allSts = new HashSet<>();
	static Date lastDay = null;
	static String lastDay1 = "";
	static String nowDay2 = "";
	static String lastDay2 = "";
	static String demoXML = "E:\\2019\\GNSS\\监控日志\\GNSS-log.xml";
	static Element InNumElement = null;
	static Element InStsElement = null;
	static Element OutNumElement = null;
	static Element OutStsElement = null;
	static Element MEMSMMPElement = null;
	static Element MEMSTotalElement = null;
	static Element dayTitle = null;
	static String recorder = "魏闫艳";

	public static void main(String[] args) {
//		executeMEMSDaily();
//		executeMEMSDailyNDaysBefore(4);
		exportMEMSDailyLog();
	}
	public static void exportMEMSDailyLog() {
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
			fillMEMSInfo("E:\\2019\\GNSS\\Data\\MEMSFolder",lastDay1);
			writer(document);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void initInfo() {
		allSts.add("SMTWX");
		allSts.add("SMFYX");
		allSts.add("SMXJZ");
		allSts.add("SMCLX");
		allSts.add("SMMYX");
		allSts.add("SMPGX");
		allSts.add("SMBTX");
		allSts.add("SMHGZ");
		allSts.add("SMZSZ");
		allSts.add("SMMNX");
		Calendar now = Calendar.getInstance();
		nowDay2 = format2.format(now.getTime());
		now.add(Calendar.DAY_OF_YEAR, -1);
		lastDay = now.getTime();
		lastDay1 = format.format(lastDay);
		lastDay2 = format2.format(lastDay);
	}
	  /** 
     * 把document对象写入新的文件 
     *  
     * @param document 
     * @throws Exception 
     */  
    public static void writer(Document document) {  
        // 紧凑的格式  
        // OutputFormat format = OutputFormat.createCompactFormat();  
        // 排版缩进的格式  
        OutputFormat format = OutputFormat.createPrettyPrint();  
        // 设置编码  
        format.setEncoding("UTF-8");  
        // 创建XMLWriter对象,指定了写出文件及编码格式  
		try {
			XMLWriter writer = new XMLWriter(new FileWriter(new File("E:\\2019\\GNSS\\监控日志\\"+lastDay1+".xml")),format);
			// 写入  
			writer.write(document);  
			// 立即写入  
			writer.flush();  
			// 关闭操作  
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
								tElement.setText("40M");
							}else if (nodeText.equalsIgnoreCase("CPUPercent")) {
								tElement.setText("7%");
							}else if (nodeText.equalsIgnoreCase("InNum")) {
								InNumElement = tElement;
							}else if (nodeText.equalsIgnoreCase("InSts")) {
								InStsElement = tElement;
							}else if (nodeText.equalsIgnoreCase("OutNum")) {
								OutNumElement = tElement;
							}else if (nodeText.equalsIgnoreCase("OutSts")) {
								OutStsElement = tElement;
							}else if (nodeText.equalsIgnoreCase("MEMSMM")) {
								MEMSMMPElement = tElement.getParent().getParent();
							}else if (nodeText.equalsIgnoreCase("MEMSTotal")) {
								MEMSTotalElement = tElement;
							}
						}
					}
				}
			}
		}
		
		// 当前节点下面子节点迭代器  
	    Iterator<Element> it = node.elementIterator();  
	    // 遍历  
	    while (it.hasNext()) {  
	       // 获取某个子节点对象  
	       Element e = it.next();  
	       // 对子节点进行遍历  
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
				inSts += stFolder.getName()+",";
				allSts.remove(stFolder.getName());
				System.out.println(stFolder.getName()+"—"+String.format("%.1f", size/1024)+"M");
				addMEMSM(fatherNodeTC, stFolder.getName()+"—"+String.format("%.1f", size/1024)+"M");
			}
		}
		MEMSTotalElement.setText(String.format("%.1f", sum)+"M");
		if (sum <= 0) {
			addMEMSM(fatherNodeTC, "");
		}
		InNumElement.setText(inNum+"");
		if (inSts.endsWith(",")) {
			inSts = inSts.substring(0, inSts.lastIndexOf(","));
		}
		InStsElement.setText(inSts);
		OutNumElement.setText(allSts.size()+"");
		OutStsElement.setText(allSts.toString().replace("[", "").replace("]", ""));
		System.out.println(day+":"+String.format("%.1f", sum)+"M");
	}

	private static void addMEMSM(Element fatherNode, String text) {
		Element wPElement = fatherNode.addElement("w:p");
		Element pPr = wPElement.addElement("w:pPr");
		Element jc = pPr.addElement("w:jc");
		jc.addAttribute("w:val", "center");
		Element rPr = pPr.addElement("w:rPr");
		Element sz = rPr.addElement("w:sz");
		sz.addAttribute("w:val", "24");
		Element szCs = rPr.addElement("w:szCs");
		szCs.addAttribute("w:val", "24");
		Element wrElement = wPElement.addElement("w:r");
		wrElement.addAttribute("w:rsidRPr", "009455E1");
		Element rPr2 = wrElement.addElement("w:rPr");
		Element sz2 = rPr2.addElement("w:sz");
		sz2.addAttribute("w:val", "24");
		Element szCs2 = rPr2.addElement("w:szCs");
		szCs2.addAttribute("w:val", "24");
		Element wtElement = wrElement.addElement("w:t");
		wtElement.setText(text);
	}
	/**
	 * 输出台站PT
	 * @param day1 指定日期yyyy-MM-dd
	 * @param lastDay2 指定日期yyyy-MM-dd
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
							if ("stName".equals(element.attribute("id"))) {
								Element stName = element.element("p").element("r").element("t");
								stName.setText(station);
							}else if ("stPts".equals(element.attribute("id"))) {
								stPtsElement = element;
								stPt = stPtsElement.element("p");
								stPtsElement.remove(stPt);
							}
						}
					}
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
