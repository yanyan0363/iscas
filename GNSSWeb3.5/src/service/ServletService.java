package service;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager.NameMap;

import dbHelper.GNSSDBHelper;
import helper.ImgHelper;
import utils.Config;


public class ServletService {
	public static String getEQImgs(String eqID) {
		String folder = Config.filePath+eqID+"/img";
		File folderFile = new File(folder);
		if (!folderFile.exists() || !folderFile.isDirectory()) {
			return "{}";
		}
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".png")) {
					return true;
				}
				return false;
			}
		};
		File[] imgs = folderFile.listFiles(filter);
		if (imgs.length == 0) {
			return "{}";
		}
		Map<Integer, JSONArray> eqArrowsMap = new HashMap<>();
		Map<Integer, JSONArray> eqContoursMap = new HashMap<>();
		for (File img : imgs) {
			System.out.println(img.getName());
//			String imgBase64 = ImgHelper.imgToBase64(img);
			JSONObject imgObject = new JSONObject();
			imgObject.put("name", img.getName().replace(".png", ""));
//			imgObject.put("content", imgBase64);
			int time = getTimeFromFileName(img.getName());
			if (img.getName().startsWith("EQArrows_")) {
				if (eqArrowsMap.containsKey(time)) {
					eqArrowsMap.get(time).add(imgObject);
					
				}else{
					eqArrowsMap.put(time, new JSONArray());
					eqArrowsMap.get(time).add(imgObject);
				}
			}else if (img.getName().startsWith("eqcontours")) {
				if (eqContoursMap.containsKey(time)) {
					eqContoursMap.get(time).add(imgObject);
				}else{
					eqContoursMap.put(time, new JSONArray());
					eqContoursMap.get(time).add(imgObject);
				}
			}
		}
		System.out.println("eqArrowsMap.size()::"+eqArrowsMap.size());
		System.out.println("eqContoursMap.size()::"+eqContoursMap.size());
		//排序EQArrows
		String[] names = folderFile.list(filter);
		List<Integer> timeList = getTimeList(names);
		JSONArray imgArrowsArray = new JSONArray();
		JSONArray imgContoursArray = new JSONArray();
		for (int i = 0; i < timeList.size(); i++) {
			if (eqArrowsMap.containsKey(timeList.get(i))) {
				JSONObject resObject = new JSONObject();
				resObject.put("timeNode", timeList.get(i)+"s");
				resObject.put("imgs", eqArrowsMap.get(timeList.get(i)));
				imgArrowsArray.add(resObject);
			}
			if (eqContoursMap.containsKey(timeList.get(i))) {
				JSONObject resObject = new JSONObject();
				resObject.put("timeNode", timeList.get(i)+"s");
				resObject.put("imgs", eqContoursMap.get(timeList.get(i)));
				imgContoursArray.add(resObject);
			}
		}
		JSONObject resObject = new JSONObject();
		resObject.put("EQArrows", imgArrowsArray);
		resObject.put("EQContours", imgContoursArray);
		JSONArray gifArray = getEQArrowsGif(folderFile);
		resObject.put("EQArrowsGif", gifArray);
		System.out.println(resObject.isEmpty()+"||"+resObject.toString());
		if (resObject.isEmpty()) {
			return "{}";
		}
		return resObject.toString();
	}
	public static String getEQImgs_base64(String eqID) {
		String folder = Config.filePath+eqID+"/img";
		File folderFile = new File(folder);
		if (!folderFile.exists() || !folderFile.isDirectory()) {
			return "{}";
		}
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".png")) {
					return true;
				}
				return false;
			}
		};
		File[] imgs = folderFile.listFiles(filter);
		if (imgs.length == 0) {
			return "{}";
		}
		Map<Integer, JSONArray> eqArrowsMap = new HashMap<>();
		Map<Integer, JSONArray> eqContoursMap = new HashMap<>();
		for (File img : imgs) {
			System.out.println(img.getName());
			String imgBase64 = ImgHelper.imgToBase64(img);
			JSONObject imgObject = new JSONObject();
			imgObject.put("name", img.getName().replace(".png", ""));
			imgObject.put("content", imgBase64);
			int time = getTimeFromFileName(img.getName());
			if (img.getName().startsWith("EQArrows_")) {
				if (eqArrowsMap.containsKey(time)) {
					eqArrowsMap.get(time).add(imgObject);
					
				}else{
					eqArrowsMap.put(time, new JSONArray());
					eqArrowsMap.get(time).add(imgObject);
				}
			}else if (img.getName().startsWith("eqcontours")) {
				if (eqContoursMap.containsKey(time)) {
					eqContoursMap.get(time).add(imgObject);
				}else{
					eqContoursMap.put(time, new JSONArray());
					eqContoursMap.get(time).add(imgObject);
				}
			}
		}
		System.out.println("eqArrowsMap.size()::"+eqArrowsMap.size());
		System.out.println("eqContoursMap.size()::"+eqContoursMap.size());
		//排序EQArrows
		String[] names = folderFile.list(filter);
		List<Integer> timeList = getTimeList(names);
		JSONArray imgArrowsArray = new JSONArray();
		JSONArray imgContoursArray = new JSONArray();
		for (int i = 0; i < timeList.size(); i++) {
			if (eqArrowsMap.containsKey(timeList.get(i))) {
				JSONObject resObject = new JSONObject();
				resObject.put("timeNode", timeList.get(i)+"s");
				resObject.put("imgs", eqArrowsMap.get(timeList.get(i)));
				imgArrowsArray.add(resObject);
			}
			if (eqContoursMap.containsKey(timeList.get(i))) {
				JSONObject resObject = new JSONObject();
				resObject.put("timeNode", timeList.get(i)+"s");
				resObject.put("imgs", eqContoursMap.get(timeList.get(i)));
				imgContoursArray.add(resObject);
			}
		}
		JSONObject resObject = new JSONObject();
		resObject.put("EQArrows", imgArrowsArray);
		resObject.put("EQContours", imgContoursArray);
		JSONArray gifArray = getEQArrowsGif(folderFile);
		resObject.put("EQArrowsGif", gifArray);
		System.out.println(resObject.isEmpty()+"||"+resObject.toString());
		if (resObject.isEmpty()) {
			return "{}";
		}
		return resObject.toString();
	}
	private static JSONArray getEQArrowsGif(File folderFile) {
		if (!folderFile.exists() || !folderFile.isDirectory()) {
			return null;
		}
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".gif")&&name.startsWith("EQArrows_")) {
					return true;
				}
				return false;
			}
		};
		File[] gifs = folderFile.listFiles(filter);
		if (gifs.length == 0) {
			return null;
		}
		JSONArray jsonArray = new JSONArray();
		for (File gif : gifs) {
			System.out.println(gif.getName());
			JSONObject gifObject = new JSONObject();
			gifObject.put("name", gif.getName().replace(".gif", ""));
			jsonArray.add(gifObject);
		}
		return jsonArray;
	}
	private static JSONArray getEQArrowsGif_base64(File folderFile) {
		if (!folderFile.exists() || !folderFile.isDirectory()) {
			return null;
		}
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".gif")&&name.startsWith("EQArrows_")) {
					return true;
				}
				return false;
			}
		};
		File[] gifs = folderFile.listFiles(filter);
		if (gifs.length == 0) {
			return null;
		}
		JSONArray jsonArray = new JSONArray();
		for (File gif : gifs) {
			System.out.println(gif.getName());
			String imgBase64 = ImgHelper.imgToBase64(gif);
			JSONObject gifObject = new JSONObject();
			gifObject.put("name", gif.getName().replace(".gif", ""));
			gifObject.put("content", imgBase64);
			jsonArray.add(gifObject);
		}
		return jsonArray;
	}
	private static int getTimeFromFileName(String fileName) {
		return Integer.parseInt(fileName.substring(fileName.lastIndexOf("_")+1, fileName.indexOf("s.png")));
	}
	private static List<Integer> getTimeList(String[] fileNames) {
		if (fileNames == null || fileNames.length < 1) {
			return new ArrayList<>();
		}
		Set<Integer> set = new HashSet<>();
		for (String fName : fileNames) {
			String time = fName.substring(fName.lastIndexOf("_")+1, fName.indexOf("s.png"));
			set.add(Integer.parseInt(time));
		}
		List<Integer> timeList = new ArrayList<>();
		timeList.addAll(set);
		Collections.sort(timeList);
		System.out.println(timeList);
		return timeList;
	}
	public static String getReportDetail(String eqID) {
		if (eqID == null || eqID == "" ) {
			return "";
		}
		ResultSet resultSet = GNSSDBHelper.getReportDetail(eqID);
		if (resultSet != null) {
			JSONObject jsonObject;
			JSONObject info = new JSONObject();
			JSONArray provArray = new JSONArray();//provinces
			JSONArray provinces;
			JSONObject target;
			JSONArray rail;
			JSONArray railArray = new JSONArray();
//			Set<String> railSet = new HashSet<>();
			int totalArea = 0;
			double totalPop = 0.0;
			int school = 0;
			int reservior = 0;
			boolean flag = false;
			try {
				while(resultSet.next()){
					if (resultSet.getString("detail").equals("")) {
						continue;
					}
					jsonObject = JSONObject.parseObject(resultSet.getString("detail"));
					info = jsonObject.getJSONObject("info");
					totalArea += info.getIntValue("totalArea");
					totalPop += info.getDouble("totalPop");
					//info
					if (provArray.size() == 0) {
						provArray.addAll(JSONArray.parseArray(info.getJSONArray("provinces").toString()));
					}else {
						provinces = info.getJSONArray("provinces");
						for(int i = 0; i < provinces.size(); i++){
							for(int j = 0; j < provArray.size(); j++){
								if (provArray.getJSONObject(j).getString("prov").equals(provinces.getJSONObject(i).getString("prov"))) {
									provArray.getJSONObject(j).getJSONArray("counties").addAll(JSONArray.parseArray(provinces.getJSONObject(i).getJSONArray("counties").toString()));
									flag = true;
									break;
								}
							}
							if (flag) {
								flag = false;
								continue;
							}else {
								provArray.add(JSONObject.parse(provinces.getJSONObject(i).toString()));
								continue;
							}
						}
					}
					//target
					school += jsonObject.getJSONObject("target").getIntValue("school");
					reservior += jsonObject.getJSONObject("target").getIntValue("reservior");
					//rail
					rail = jsonObject.getJSONArray("rail");
					flag = false;
					if (railArray.size() == 0) {
						railArray.addAll(JSONArray.parseArray(rail.toJSONString()));
					}else {
						for(int i = 0; i < rail.size(); i++){
							for (int j = 0; j < railArray.size(); j++) {
								if (rail.getJSONObject(i).getString("OID").equals(railArray.getJSONObject(j).getString("OID"))) {
									flag = true;
									break;
								}
							}
							if (!flag) {
								railArray.add(JSONObject.parse(rail.getJSONObject(i).toJSONString()));
							}else {
								flag = false;
							}
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return "";
			}
			info = new JSONObject();
			info.put("provinces", provArray);
			info.put("totalArea", totalArea);
			info.put("totalPop", String.format("%.1f",totalPop));
			info.put("epi", JSONObject.parse(GNSSDBHelper.getEpiBL(eqID)));
			
			target = new JSONObject();
			target.put("school", school);
			target.put("reservior", reservior);
			
			JSONObject result = new JSONObject();
			result.put("info", info);
			result.put("target", target);
			result.put("rail", railArray);
//			System.out.println(result.toString());
			return result.toString();
		}
		return "";
	}
	
}
