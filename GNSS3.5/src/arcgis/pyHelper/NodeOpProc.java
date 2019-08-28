package arcgis.pyHelper;

import utils.StringHelper;
import utils.SystemHelper;

public class NodeOpProc {
			
	private String pyFolder;
	private String workspace;
	private String workFolder;
	private String demoMXD;
	private String newMXD;
	private String servicePath;
	private String serviceFolder;
	/**
	 * 
	 * @param pyFolder py目录, eg. "E:/2017/GNSS/Arcpy/"
	 * @param workspace 工作空间 , eg. "E:/2017/GNSS/ArcGISServer/file/"
	 * @param workFolder 工作空间中的工作文件夹，生成的栅格、等值线、mxd等文件的存放目录. eg. "test"
	 * @param demoMXD 模板mxd的路径，相对于工作空间的相对路径. eg. "demo/demo.mxd"
	 * @param newMXD 生成的mxd的名称. eg. "newMXD.mxd"
	 * @param servicePath 服务路径. eg. "http://localhost:6080/ArcGIS/rest/services"
	 * @param serviceFolder 服务文件夹
	 */
	public NodeOpProc(String pyFolder, String workspace, String workFolder,
			String demoMXD, String newMXD, String servicePath, String serviceFolder) {
		this.pyFolder = pyFolder;
		this.workspace = workspace;
		this.workFolder = workFolder;
		this.demoMXD = demoMXD;
		this.newMXD = newMXD;
		this.servicePath = servicePath;
		this.serviceFolder = serviceFolder;
	}
	public boolean exec() {
		if (!isParamsValid()) {
			return false;
		}
		String[] paras = new String[]{
				"python",
				pyFolder+"/nodeTimeOp.py",
				pyFolder,
				workspace,
				workFolder,
				demoMXD,
				newMXD,
				servicePath,
				serviceFolder,
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
		if (!StringHelper.isFileExist(workspace+demoMXD) || !demoMXD.endsWith(".mxd")) {
			SystemHelper.writeLog("ContoursProc: demoMXD is invalid.");
			return false;
		}
		//servicePath, serviceFolder暂时未进行校验
		return true;
	}
}
