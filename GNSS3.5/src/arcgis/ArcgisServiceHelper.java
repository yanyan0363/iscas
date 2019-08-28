package arcgis;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.imageio.stream.FileImageOutputStream;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;

import com.esri.arcgis.server.json.JSONException;
import com.esri.arcgis.server.json.JSONObject;

import beans.Extent;
import slipHelper.SlipHelper;
import utils.Config;

public class ArcgisServiceHelper {

//	private static HttpClient client = new HttpClient();
	
	public static Extent getLayerExtent(String serviceFolder, String serviceName, int layerId) {
		String jsonStr = getLayerJson(serviceFolder, serviceName,layerId);
//		System.out.println(jsonStr);
		JSONObject object = new JSONObject(jsonStr);
//		System.out.println(object.toString());
		Extent extent = null;
		int count  = 0;
		while (count < 5) {
			try {
				JSONObject extentObject = object.getJSONObject("extent");
				extent = new Extent(extentObject.getDouble("xmin"), extentObject.getDouble("ymin"), extentObject.getDouble("xmax"), extentObject.getDouble("ymax"), 
						ArcgisHelper.spatialReferenceEnvironment.createSpatialReference(extentObject.getJSONObject("spatialReference").getInt("wkid")));
				break;
			} catch (JSONException | IOException e) {
				e.printStackTrace();
				count++;
			}
		}
		return extent;
	}
	public static String getLayerJson(String serviceFolder, String serviceName, int layerId) {
		String uri = Config.servicePath+serviceFolder+"/"+serviceName+"/MapServer/"+layerId+"?f=json";
		System.out.println(uri);
//		if (!client.isStarted()) {
//			try {
//				client.start();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		HttpClient client = new HttpClient();
		try {
			client.start();
			client.setConnectTimeout(10000);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		ContentResponse response = null;
		try {
			response = client.GET(uri);
			String reString = response.getContentAsString();
			client.stop();
			client = null;
			return reString;
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			client.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		client = null;
		return "{}";
	}
	public static String getLayersJson(String serviceFolder, String serviceName) {
		String uri = Config.servicePath+serviceFolder+"/"+serviceName+"/MapServer/layers?f=json";
		HttpClient client = new HttpClient();
		try {
			client.start();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		ContentResponse response = null;
		try {
			response = client.GET(uri);
			String reString = response.getContentAsString();
			try {
				client.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
			client = null;
			return reString;
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			try {
				client.stop();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			client = null;
			return "{}";
		}
	}
	
	/**
	 * 
	 * @param uri 请求路径
	 * @param exportImg 导出的img文件
	 */
	public static void exportImg(String uri, String exportImg) {
		System.out.println(uri);
		HttpClient client = new HttpClient();
		try {
			client.start();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			ContentResponse response = client.GET(uri);
			FileImageOutputStream imageOutputStream = new FileImageOutputStream(new File(exportImg));
			imageOutputStream.write(response.getContent());
			imageOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			client.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		client = null;
	}
	public static void main(String[] args) {
//		getLayersJson("EQ", "1503727528394");
//		String imgFolder = "E:/2018/GNSS/fortran/res/img/";
//		String serviceFolder = "Test";
//		String serviceName = "FortSlip";
//		String servicePath = "http://localhost:6080/arcgis/rest/services/";
//		File folderImgs = new File(imgFolder);
//		if (!folderImgs.exists()) {
//			folderImgs.mkdirs();
//		}
//		Extent extent = SlipHelper.getSlipLayerExtent(serviceFolder, serviceName, 2);
//		String uri = servicePath+serviceFolder+"/"+serviceName+"/MapServer/export?size=500,400&layers=show:0,1&bbox="+(extent.minX-0.5)+","+(extent.minY-0.5)+","+(extent.maxX+0.5)+","+(extent.maxY+0.5)
//				+"&format=png&transparent=true&f=image";
//		System.out.println(uri);
//		ArcgisServiceHelper.exportImg(uri, imgFolder+"/BL.png");
//		uri = servicePath+serviceFolder+"/"+serviceName+"/MapServer/export?size=400,300&bbox="+(extent.minX)+","+(extent.minY)+","+(extent.maxX)+","+(extent.maxY)
//				+"&format=png&transparent=true&f=image";
//		System.out.println(uri);
//		ArcgisServiceHelper.exportImg(uri, imgFolder+"/img.png");
//		try {
//			client.stop();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
