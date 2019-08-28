# 调用点图层，生成等值线，并发布服务 sys.argv[1],sys.argv[2],sys.argv[3],sys.argv[4]
# 参数：py目录，台站shp路径, 输出栅格目录，等值线间距，mxd名称，mxd路径，服务路径，服务文件夹
# 参数1：py目录 "E:/2017/GNSS/Arcpy/"
# 参数2：工作空间 "E:/2017/GNSS/ArcGISServer/file/"
# 参数3：台站shp路径 "demo/Export_Stations.shp"或绝对路径
# 参数4：输出栅格、等值线、mxd的目录 "test"
# 参数5：等值线间距 "10"
# 参数6：demoMXD "demo/demo.mxd"
# 参数7：生成的MXD "newMXD.mxd"
# 参数8：服务路径 "http://localhost:6080/ArcGIS/rest/services"
# 参数9：服务文件夹 "Test"
# 参数10：num ,表示该地震第几次调用脚本

import arcpy,sys,os
sys.path.append(sys.argv[1])
from arcpy import env
env.workspace = sys.argv[2]

# Check out the ArcGIS 3D extension license
arcpy.CheckOutExtension("3D")
arcpy.CheckOutExtension("Spatial")

folder = sys.argv[2]+sys.argv[4]
#if os.path.exists(folder) == True:
#    __import__('shutil').rmtree(folder)
if os.path.exists(folder) == False:
    os.makedirs(folder)
# 生成Raster
outVRaster = sys.argv[4]+"/EQRaster_V_"+sys.argv[10]+".tif"
outHRaster = sys.argv[4]+"/EQRaster_H_"+sys.argv[10]+".tif"
arcpy.TopoToRaster_3d(sys.argv[3]+" VMaxDis PointElevation",outVRaster)
arcpy.TopoToRaster_3d(sys.argv[3]+" HMaxDis PointElevation",outHRaster)
print "生成Raster..."

# 生成等值线图
arcpy.sa.Contour(outVRaster,sys.argv[4]+"/EQContours_V_"+sys.argv[10]+".shp",sys.argv[5])
arcpy.sa.Contour(outHRaster,sys.argv[4]+"/EQContours_H_"+sys.argv[10]+".shp",sys.argv[5])
print "生成等值线图..."

# 重构mxd，包括EQPoint,EQPolygon,Raster,Contours,
print "重构mxd..."
from pubHelper import createMxdDocument
print "from pubHelper import createMxdDocument..."
createMxdDocument(sys.argv[2]+sys.argv[6],sys.argv[2]+sys.argv[4]+"/",sys.argv[7])
print "重构mxd结束..."

# 发布服务
print "发布服务"
from pubHelper import PublishMxd
PublishMxd(sys.argv[7],sys.argv[2]+sys.argv[4]+"/"+sys.argv[7],sys.argv[8],sys.argv[9])
