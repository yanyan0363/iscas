package arcgis.pyHelper;

import utils.StringHelper;
import utils.SystemHelper;

public class ContoursProc {
			
//	String[] paras = new String[] { "python", "E:\\2017\\GNSS\\Arcpy\\proc.py", 
//			"E:/2017/GNSS/Arcpy", "E:/2017/GNSS/ArcGISServer/file/", 
//			"/demo/Export_Stations.shp", "test", "5", "demo/demo.mxd",
//			"newMXD.mxd","http://localhost:6080/ArcGIS/rest/services","Test"};
	private String pyFolder;
	private String workspace;
	private String stationShp;
	private String workFolder;
	private String interval;
	private String demoMXD;
	private String newMXD;
	private String servicePath;
	private String serviceFolder;
	private String timeNode;
	/**
	 * 
	 * @param pyFolder py目录, eg. "E:/2017/GNSS/Arcpy/"
	 * @param workspace 工作空间 , eg. "E:/2017/GNSS/ArcGISServer/file/"
	 * @param stationShp 台站shp路径,绝对路径  //或相对于工作空间的相对路径, eg. "demo/Export_Stations.shp"(暂弃)
	 * @param workFolder 工作空间中的工作文件夹，生成的栅格、等值线、mxd等文件的存放目录. eg. "test"
	 * @param interval 等值线间距,取任意正值. eg. "0.01"
	 * @param demoMXD 模板mxd的路径，相对于工作空间的相对路径. eg. "demo/demo.mxd"
	 * @param newMXD 生成的mxd的名称. eg. "newMXD.mxd"
	 * @param servicePath 服务路径. eg. "http://localhost:6080/ArcGIS/rest/services"
	 * @param serviceFolder 服务文件夹
	 * @param timeNode 时间节点
	 */
	public ContoursProc(String pyFolder, String workspace, String stationShp, String workFolder, String interval, 
			String demoMXD, String newMXD, String servicePath, String serviceFolder, String timeNode) {
		this.pyFolder = pyFolder;
		this.workspace = workspace;
		this.stationShp = stationShp;
		this.workFolder = workFolder;
		this.interval = interval;
		this.demoMXD = demoMXD;
		this.newMXD = newMXD;
		this.servicePath = servicePath;
		this.serviceFolder = serviceFolder;
		this.timeNode = timeNode;
	}
	public boolean exec() {
		if (!isParamsValid()) {
			return false;
		}
		String[] paras = new String[]{
				"python",
				pyFolder+"/proc.py",
				pyFolder,
				workspace,
				stationShp,
				workFolder,
				interval,
				demoMXD,
				newMXD,
				servicePath,
				serviceFolder,
				timeNode
		};
		//待添加，从ArcPyHelper.execPy(paras)中接收返回数据
		
		PyHelper.execPy(paras);
		return true;
	}
	private boolean isParamsValid() {
		if (!StringHelper.isFolderExist(pyFolder)) {
			SystemHelper.writeLog("ContoursProc: pyFolder is invalid.");
			return false;
		}
		if (!StringHelper.isFolderExist(workspace)) {
			SystemHelper.writeLog("ContoursProc: workspace is invalid.");
			return false;
		}
		if (!StringHelper.isFileExist(stationShp) || !stationShp.endsWith(".shp")) {
			SystemHelper.writeLog("ContoursProc: stationShp is invalid.");
			return false;
		}
//		if (!StringHelper.isFolderExist(workFolder)) {
//			SystemHelper.writeLog("ContoursProc: workFolder is invalid.");
//			return false;
//		}
		if (!StringHelper.isPositiveNumeric(interval)) {
			SystemHelper.writeLog("ContoursProc: interval is invalid.");
			return false;
		}
		if (!StringHelper.isFileExist(workspace+demoMXD) || !demoMXD.endsWith(".mxd")) {
			SystemHelper.writeLog("ContoursProc: demoMXD is invalid.");
			return false;
		}
		//servicePath, serviceFolder暂时未进行校验
		return true;
	}
}
