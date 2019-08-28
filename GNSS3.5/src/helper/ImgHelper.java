package helper;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import com.esri.arcgis.datasourcesfile.ShapefileWorkspaceFactory;
import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureWorkspace;
import com.esri.arcgis.geodatabase.IWorkspace;
import com.esri.arcgis.geometry.GeometryEnvironment;
import com.esri.arcgis.geometry.IGeometryBridge2;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.IPointCollection4;
import com.esri.arcgis.geometry.IPolyline;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.geometry.Polyline;
import com.esri.arcgis.server.json.JSONArray;
import com.esri.arcgis.server.json.JSONObject;
import com.esri.arcgis.system._WKSPoint;

import arcgis.ArcgisHelper;
import arcgis.ArcgisServiceHelper;
import beans.EQInfo;
import beans.Extent;
import beans.slip.GifHelper;
import metaData.StaticMetaData;
import utils.Config;

public class ImgHelper {

	public static void main(String[] args) {
//		getImgLayers("1503727528394");
//		ExportEQImg("1551256347593");
	}
//	public static void ExportEQImgByMxd(String eqID) {
//		String folder = Config.filePath+eqID+"/img/";
//		File imgDir = new File(folder);
//		if (!imgDir.exists()) {
//			imgDir.mkdirs();
//		}
//		Map<String, String> imgLayers =getImgLayersByService(serviceName);
//		Iterator<Entry<String, String>> iterator = imgLayers.entrySet().iterator();
//		while (iterator.hasNext()) {
//			Entry<String, String> layers = (Entry<String, String>)iterator.next();
//			String img = folder+layers.getKey()+".png";
//			ArcgisServiceHelper.exportImg(uri+"&layers=show:"+layers.getValue(), img);
////			overlapImg_TrueColor(BLImg, img, img);
////			System.out.println(layers.getKey()+"::"+layers.getValue());
//		}
//		boolean arrowsGifResult = GifHelper.ExportArrowsGif(serviceName);//serviceName即为EQID
//		System.out.println("生成ArrowsGif结果："+arrowsGifResult);
//	}
	public static void ExportEQImgByService(EQInfo eqInfo) {
		String serviceName = eqInfo.getEQID();
		String uri = Config.servicePath+Config.serviceFolder+"/"+serviceName
				+"/MapServer/export?format=png&transparent=false&f=image&mapScale=2500000";
		Extent stBBox = getBBox(serviceName);
		if (stBBox == null) {
			System.out.println("获取stBBox出错, return.");
			return;
		}
//		uri=uri+"&bbox="+stBBox.toBBox()+"&size="+(stBBox.maxX-stBBox.minX)*100+","+(stBBox.maxY-stBBox.minY)*100;
		uri=uri+"&bbox="+stBBox.toBBox()+"&size=768,1024";
		System.out.println(uri);
		String folder = Config.filePath+serviceName+"/img/";
		File imgDir = new File(folder);
		if (!imgDir.exists()) {
			imgDir.mkdirs();
		}
//		String BLImg = folder+"ImgBLBoundary.png";
//		ArcgisServiceHelper.exportImg(Config.servicePath+Config.serviceFolder+"/"+serviceName+"/MapServer/export?format=png&transparent=true&f=image&mapScale=2500000&bbox=135.5,33.5,142.5,42.5&size=700.0,900.0&layers=show:2,3", BLImg);
		Map<String, String> imgLayers =getImgLayersByService(serviceName);
		Iterator<Entry<String, String>> iterator = imgLayers.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> layers = (Entry<String, String>)iterator.next();
			String img = folder+layers.getKey()+".png";
			ArcgisServiceHelper.exportImg(uri+"&layers=show:"+layers.getValue(), img);
			addTitleImg_TrueColor(eqInfo, img);
//			System.out.println(layers.getKey()+"::"+layers.getValue());
		}
		boolean arrowsGifResult = GifHelper.ExportArrowsGif(serviceName);//serviceName即为EQID
		System.out.println("生成ArrowsGif结果："+arrowsGifResult);
	}
	private static void addTitleImg_TrueColor(EQInfo eqInfo, String imgPath) {
		int titleHeight = 100;
		File imgFile = new File(imgPath);
		if (!imgFile.exists()) {
			return;
		}
		try {
			BufferedImage smallImage = ImageIO.read(imgFile);
			int imageWidth = smallImage.getWidth();
			int smallImageHeight = smallImage.getHeight();
			int bigImageHeight = smallImageHeight+titleHeight;
			int[] titleImageArray = new int[imageWidth*titleHeight];
			for (int i = 0; i < titleImageArray.length; i++) {
				titleImageArray[i] = 16645629;//白色
			}
			int[] smallImageArray = new int[imageWidth*smallImageHeight];
			smallImageArray = smallImage.getRGB(0, 0, imageWidth, smallImageHeight, smallImageArray, 0, imageWidth);
			BufferedImage outImg = new BufferedImage(imageWidth, bigImageHeight, BufferedImage.TYPE_INT_RGB);
			outImg.setRGB(0, 0, imageWidth, titleHeight, titleImageArray, 0, imageWidth);
			outImg.setRGB(0, titleHeight,imageWidth, smallImageHeight, smallImageArray, 0, imageWidth);
			Graphics2D graphics2d = (Graphics2D)outImg.getGraphics();
			graphics2d.setPaint(Color.black);
			graphics2d.setFont(new Font("Serif", Font.ROMAN_BASELINE, 20));
			graphics2d.drawString(StaticMetaData.formatS.format(eqInfo.getEqTime()), 20, 30);
			graphics2d.drawString(eqInfo.getBLString()+" GPS"+eqInfo.getGpsMag()+"级 MEMS"+eqInfo.getMemsMag()+"级", 20, 50);
			ImageIO.write(outImg, imgPath.split("\\.")[1], imgFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param serviceName 服务名称，即相应的EQID
	 * @param outDir 输出图片的存储目录,图片存储至outDir/serviceName目录下，outDir以/结尾
	 */
	public static void ExportEQImg(String serviceName, String outDir) {
		String uri = Config.servicePath+Config.serviceFolder+"/"+serviceName
				+"/MapServer/export?format=png&transparent=false&f=image&mapScale=5000000";
		Extent stBBox = getBBox(serviceName);
		if (stBBox == null) {
			System.out.println("获取stBBox出错, return.");
			return;
		}
		uri=uri+"&bbox="+stBBox.toBBox()+"&size="+(stBBox.maxX-stBBox.minX)*100+","+(stBBox.maxY-stBBox.minY)*100;
		System.out.println(uri);
		File imgDir = new File(outDir+serviceName);
		if (!imgDir.exists()) {
			imgDir.mkdirs();
		}
		Map<String, String> imgLayers =getImgLayersByService(serviceName);
		Iterator<Entry<String, String>> iterator = imgLayers.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> layers = (Entry<String, String>)iterator.next();
			String img = outDir+serviceName+"/"+layers.getKey()+".png";
			ArcgisServiceHelper.exportImg(uri+"&layers=show:"+layers.getValue(), img);
		}
		boolean arrowsGifResult = GifHelper.ExportFolderGif(outDir+serviceName);//serviceName即为EQID
		System.out.println("生成ArrowsGif结果："+arrowsGifResult);
	}


	private static void overlapImg_TrueColor(String bigImgPath, String smallImgPath, String outImgPath) {
		try {
			BufferedImage bigImage = ImageIO.read(new File(bigImgPath));
			BufferedImage smallImage = ImageIO.read(new File(smallImgPath));
			int bigImageWidth = bigImage.getWidth();
			int bigImageHeight = bigImage.getHeight();
			int smallImageWidth = smallImage.getWidth();
			int smallImageHeight = smallImage.getHeight();
			int[] bigImageArray = new int[bigImageWidth*bigImageHeight];
			bigImageArray = bigImage.getRGB(0, 0, bigImageWidth, bigImageHeight, bigImageArray, 0, bigImageWidth);
			int[] smallImageArray = new int[smallImageWidth*smallImageHeight];
			smallImageArray = smallImage.getRGB(0, 0, smallImageWidth, smallImageHeight, smallImageArray, 0, smallImageWidth);
			BufferedImage outImg = new BufferedImage(bigImageWidth, bigImageHeight, BufferedImage.TYPE_INT_RGB);
			outImg.setRGB(0, 0, bigImageWidth, bigImageHeight, bigImageArray, 0, bigImageWidth);
			int x = (bigImageWidth - smallImageWidth)/2;
			int y = (bigImageHeight - smallImageHeight)/2;
			outImg.setRGB(x, y,smallImageWidth, smallImageHeight, smallImageArray, 0, smallImageWidth);
			ImageIO.write(outImg, outImgPath.split("\\.")[1], new File(outImgPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
//	public static void ExportEQImg(String serviceName) {
//		String uri = Config.servicePath+Config.serviceFolder+"/"+serviceName
//				+"/MapServer/export?size=600,900&format=png&transparent=true&f=image&mapScale=2500000";
//		String bbox = "&bbox="+getBBox(serviceName);
//		uri+=bbox;
//		String folder = Config.filePath+serviceName+"/img/";
//		File imgDir = new File(folder);
//		if (!imgDir.exists()) {
//			imgDir.mkdirs();
//		}
//		Map<String, String> imgLayers =getImgLayers(serviceName);
//		Iterator<Entry<String, String>> iterator = imgLayers.entrySet().iterator();
//		while (iterator.hasNext()) {
//			Entry<String, String> layers = (Entry<String, String>)iterator.next();
//			ArcgisServiceHelper.exportImg(uri+"&layers=show:"+layers.getValue(), folder+layers.getKey()+".png");
////			System.out.println(layers.getKey()+"::"+layers.getValue());
//		}
//		boolean arrowsGifResult = GifHelper.ExportArrowsGif(serviceName);//serviceName即为EQID
//		System.out.println("生成ArrowsGif结果："+arrowsGifResult);
//	}

	
	/**
	 * 获取第一个图层的Extent范围，并大值向上取整，小值向下取整
	 * @param serviceName
	 * @return
	 */
	public static Extent getBBox(String serviceName) {
		Extent stBBox = ArcgisServiceHelper.getLayerExtent(Config.serviceFolder, serviceName, 0);
		if (stBBox == null) {
			return null;
		}
		System.out.println(stBBox.toBBox());
		stBBox.maxX = Math.ceil(stBBox.maxX);
		stBBox.maxY = Math.ceil(stBBox.maxY);
		stBBox.minX = Math.floor(stBBox.minX);
		stBBox.minY = Math.floor(stBBox.minY);
		System.out.println("Extent:"+stBBox.toString());
		System.out.println("BBox:::"+stBBox.toBBox());
		return stBBox;
	}
	public static Map<String, String> getImgLayersByService(String serviceName) {
		Map<String, String> layersMap = new HashMap<>();
		String layersString = ArcgisServiceHelper.getLayersJson(Config.serviceFolder, serviceName);
		JSONObject jsonObject = new JSONObject(layersString);
		try {
			JSONArray jsonArray = jsonObject.getJSONArray("layers");
			int layersLength = jsonArray.length();
			int lastLayerIdx = layersLength-1;
			for (int i = 0; i < layersLength; i++) {
				JSONObject layer = jsonArray.getJSONObject(i);
				String name = layer.getString("name");
				if (name.startsWith("EQArrows_")) {
					layersMap.put(name, "0,2,3,"+layer.getString("id")+","+lastLayerIdx);
				}else if (name.startsWith("eqcontours_")) {
					String suffix = name.substring(name.indexOf("_"));
					String str = "0,2,3,"+layer.getString("id");
//				添加对应的raster图层
					for (int j = i+1; j < layersLength; j++) {
						JSONObject object = jsonArray.getJSONObject(j);
						String tName = object.getString("name");
						if (tName.startsWith("EQRaster_") && tName.toLowerCase().endsWith(suffix)) {
							str += ","+object.getString("id");
							break;
						}
					}
					layersMap.put(name, str+=","+lastLayerIdx);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println(layersMap);
		return layersMap;
	}
	
	public static String imgToBase64(File imgFile) {
		InputStream inputStream = null;
		byte[] data = null;
		try {
			inputStream = new FileInputStream(imgFile);
			data = new byte[inputStream.available()];
			inputStream.read(data);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Base64.getEncoder().encodeToString(data);
	}
}
