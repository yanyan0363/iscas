package beans.slip;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import slipHelper.FortSlipHelper;
import utils.AnimatedGifEncoder;
import utils.Config;
import utils.StringHelper;

public class GifHelper {

	public static boolean ExportSlipGif(String eqID){
		Date startT = new Date();
		File slipFolder = new File(Config.slipParamFolder);
		if (!slipFolder.exists() || !slipFolder.isDirectory()) {
			System.out.println("slip文件夹不存在");
			return false;
		}
		FilenameFilter slipFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith("_slip")&&name.startsWith(eqID+"_")) {
					return true;
				}
				return false;
			}
		};
		File[] slipFiles = slipFolder.listFiles(slipFilter);
		String imgPath = Config.slipImgFolder+eqID+"/";
		for (File file : slipFiles) {
			String fileName = file.getName();
			int tNode = Integer.parseInt(fileName.substring(fileName.indexOf("_")+1, fileName.lastIndexOf("_")));
			boolean res = FortSlipHelper.ExportImg(file, tNode, Config.servicePath, Config.slipServiceFolder, Config.slipServiceName, imgPath);
		}
		File imgFolder = new File(imgPath);
		if (!imgFolder.exists() || !imgFolder.isDirectory()) {
			return false;
		}
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File arg0, String arg1) {
				if (arg1.endsWith(".png")) {
					return true;
				}
				return false;
			}
		};
		String[] imgs = imgFolder.list(filter);
		List<Integer> tNodes = new ArrayList<>();
		for (String img : imgs) {
			String tNode = img.substring(0, img.lastIndexOf(".png"));
			if (StringHelper.isNumeric(tNode)) {
				tNodes.add(Integer.parseInt(tNode));
			}
		}
		//文件排序
		Collections.sort(tNodes);
		AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
		gifEncoder.start(imgPath+"slip.gif");
		gifEncoder.setDelay(1000);
		gifEncoder.setRepeat(0);
		for (int i = 0; i < tNodes.size(); i++) {
			BufferedImage bufferedImage = null;
			File imgFile = new File(imgFolder.getAbsolutePath()+"\\"+tNodes.get(i)+".png");
			try {
				bufferedImage = ImageIO.read(imgFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (bufferedImage != null) {
				Graphics2D graphics2d = (Graphics2D)bufferedImage.getGraphics();
				graphics2d.setPaint(Color.RED);
				graphics2d.setFont(new Font("Serif", Font.ROMAN_BASELINE, 20));
				graphics2d.drawString("Time = " + tNodes.get(i) + " seconds", 50, 50);
				try {
					ImageIO.write(bufferedImage, "png", imgFile);
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
				gifEncoder.addFrame(bufferedImage);
			}
		}
		gifEncoder.finish();
		Date endT = new Date();
		long tt = endT.getTime() - startT.getTime();
		System.out.println("gif 耗时：：" + tt + "ms");
		return true;
	}
	public static boolean ExportArrowsGif(String eqID){
		Date startT = new Date();
		File folder = new File(Config.filePath+eqID+"/"+Config.ImgPath);
		if (!folder.exists() || !folder.isDirectory()) {
			System.out.println(eqID+" img 文件夹不存在");
			return false;
		}
		boolean VGif = createGif(folder, "V");
		System.out.println("EQArrows_V.gif 生成结果：" + VGif);
		boolean HGif = createGif(folder, "H");
		System.out.println("EQArrows_H.gif 生成结果：" + HGif);
		Date endT = new Date();
		long tt = endT.getTime() - startT.getTime();
		System.out.println("ExportArrowsGif 耗时：：" + tt/1000 + "s");
		return true;
	}
	/**
	 * 指定文件夹内的V/H图片生成gif
	 * @param imgDir img所在文件夹绝对路径
	 * @return
	 */
	public static boolean ExportFolderGif(String imgDir){
		Date startT = new Date();
		File folder = new File(imgDir);
		if (!folder.exists() || !folder.isDirectory()) {
			System.out.println(imgDir+" - img 文件夹不存在");
			return false;
		}
		boolean VGif = createGif(folder, "V");
		System.out.println("EQArrows_V.gif 生成结果：" + VGif);
		boolean HGif = createGif(folder, "H");
		System.out.println("EQArrows_H.gif 生成结果：" + HGif);
		Date endT = new Date();
		long tt = endT.getTime() - startT.getTime();
		System.out.println("ExportArrowsGif 耗时：：" + tt/1000 + "s");
		return true;
	}
	
	/**
	 * 
	 * @param imgFolder img目录
	 * @param type 图片类型，包括V和H
	 * @return
	 */
	private static boolean createGif(File imgFolder, String type) {
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.startsWith("EQArrows_"+type+"_") && name.endsWith(".png")) {
					return true;
				}
				return false;
			}
		};
		String[] imgs = imgFolder.list(filter);
		List<Integer> tNodes = new ArrayList<>();
		for (String img : imgs) {
			String tNode = img.substring(img.lastIndexOf("_")+1, img.lastIndexOf("s.png"));
			System.out.println("ExportArrowsGif tNode:" + tNode);
			if (StringHelper.isNumeric(tNode)) {
				tNodes.add(Integer.parseInt(tNode));
			}
		}
		//文件排序
		Collections.sort(tNodes);
		AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
		gifEncoder.start(imgFolder.getAbsolutePath()+"\\EQArrows_"+type+".gif");
		gifEncoder.setDelay(1000);
		gifEncoder.setRepeat(0);
		gifEncoder.setTransparent(new Color(Color.TRANSLUCENT));
		for (int i = 0; i < tNodes.size(); i++) {
			BufferedImage bufferedImage = null;
			File imgFile = new File(imgFolder.getAbsolutePath()+"\\EQArrows_"+type+"_"+tNodes.get(i)+"s.png");
			try {
				bufferedImage = ImageIO.read(imgFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (bufferedImage != null) {
				Graphics2D graphics2d = (Graphics2D)bufferedImage.getGraphics();
				graphics2d.setPaint(Color.black);
				graphics2d.setFont(new Font("Serif", Font.ROMAN_BASELINE, 20));
				graphics2d.drawString("Time = " + tNodes.get(i) + " seconds", 20, 70);
				try {
					ImageIO.write(bufferedImage, "png", imgFile);
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
				gifEncoder.addFrame(bufferedImage);
			}
		}
		gifEncoder.finish();
		return true;
	}
	public static void main(String[] args) {
		ExportArrowsGif("1544601918999");
	}
}
