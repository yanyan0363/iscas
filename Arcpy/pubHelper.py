import arcpy,os

#将mxd文档发布为服务：1.将mxd转为msd；2.分析msd；3.发布msd  
def PublishMxd(mxdName,mxdPath, serviceDir, serviceFolder):
#检查mxd文件是否存在  
# print "检查文件路径……"  
	if os.path.exists(mxdPath) == False:
		print "指定路径的mxd文档不存在！" 
		return
    # 打开mxd文档  
	try: 
		# print "正在打开mxd文档……"  
		mxd = arcpy.mapping.MapDocument(mxdPath)  
	except Exception, e:
		print "open mxd error: ", e
		return
	else:
		print "mxd文档打开成功……" 
	# 构造sddraft文档名称
	sddraft = mxdPath.replace(".mxd", ".sddraft")
	service=mxdName.replace(".mxd", "")
	sd=mxdPath.replace(".mxd", ".sd")
	con = 'C:/Users/arcgis/AppData/Roaming/ESRI/Desktop10.2/ArcCatalog/arcgis on localhost_6080 (admin).ags'
	copy_data_to_server=True
	print "正在将mxd文档转换为sddraft文档……"
	# Create service definition draft
	if os.path.exists(sd):
		os.remove(sd)
	if os.path.exists(sddraft):
		os.remove(sddraft)
	arcpy.mapping.CreateMapSDDraft(mxd, sddraft, service,'ARCGIS_SERVER',con,copy_data_to_server, serviceFolder)
	print "sddraft文档生成"
	# Analyze the service definition draft
	print "Analyze the service definition draft"
	analysis = arcpy.mapping.AnalyzeForSD(sddraft)
	# Print errors, warnings, and messages returned from the analysis  
	# print "The following information was returned during analysis of the MXD:"  
	# for key in ('messages', 'warnings', 'errors'):  
	# print '----' + key.upper() + '---'  
	# vars = analysis[key]  
	# for ((message, code), layerlist) in vars.iteritems():  
	# print '    ', message, ' (CODE %i)' % code  
	# print '       applies to:',  
	# for layer in layerlist:  
	# print layer.name,  
	# print  
	# Stage and upload the service if the sddraft analysis did not contain errors  
	if analysis['errors'] == {}:
		# Execute StageService. This creates the service definition.  
		arcpy.StageService_server(sddraft, sd)
		#Execute UploadServiceDefinition. This uploads the service definition and publishes the service.  
		arcpy.UploadServiceDefinition_server(sd, con)
		print "Service successfully published"
	else:
		print "Service could not be published because errors were found during analysis."
	# print arcpy.GetMessages() 
# demoMXDPath：模板mxd的路径，包含.mxd
# folder：tif文件,contours文件等的目录，新建的mxd文档也存储在该目录下
# newMXD：生成的mxd路径
def createMxdDocument(demoMXDPath,folder,newMXD):
	print "in createMxd..."
	if os.path.exists(demoMXDPath) == False:
		print "mxd document it's not exist!"
	else:
		try:
			print "opening mxd document……"
			mxd = arcpy.mapping.MapDocument(demoMXDPath)
			print "repair layer source"
			if os.path.isdir(folder) == False:
				print "invalid document path!"
				return
			print "reading layer document one by one......"
			files = os.listdir(folder)
			df = arcpy.mapping.ListDataFrames(mxd, "图层")[0]
			shpName = ""
			for f in files:
				if f.endswith(".tif"):
					layer = arcpy.mapping.Layer(folder+f)
					layer.transparency = 50
					layer.name = f.replace(".tif","")
					arcpy.mapping.AddLayer(df,layer)
				elif f.endswith(".shp"):
					layer = arcpy.mapping.Layer(folder+f)
					# shp文件，仅EQPolygons.shp的透明度置为50%
					if f == "EQPolygons.shp":
						layer.transparency = 50
					elif "contours" in f and layer.supports("showLabels"):
						layer.showLabels = True
						layer.labelClasses[0].expression  = '[CONTOUR]'
					elif "stations" in f and layer.supports("showLabels"):
						layer.showLabels = True
						layer.labelClasses[0].expression = '[StationID]'
					elif f == "EQImgBLNote.shp":
						layer.showLabels = True
						layer.labelClasses[0].expression = '[note]'
					arcpy.mapping.AddLayer(df,layer)
				else:
					continue
			# 添加底部的区县图层
			print "添加底部的区县图层"
			countiesLyr = arcpy.mapping.Layer(r"E:/GNSS/ArcGISServer/file/demo/counties_china.lyr")
			arcpy.mapping.AddLayer(df,countiesLyr,"BOTTOM")
			mxdPath = folder+newMXD
			if os.path.exists(mxdPath):
				os.remove(mxdPath)
			mxd.saveACopy(mxdPath)
			print mxd.filePath
			print mxdPath
			arrowHSymbolLyr = arcpy.mapping.Layer(r"E:/GNSS/ArcGISServer/file/demo/EQArrows_H_Symbol.lyr")
			arrowVSymbolLyr = arcpy.mapping.Layer(r"E:/GNSS/ArcGISServer/file/demo/EQArrows_V_Symbol.lyr")
			contourSymbolLyr = arcpy.mapping.Layer(r"E:/GNSS/ArcGISServer/file/demo/EQContours_Symbol.lyr")
			countiesSymbolLyr = arcpy.mapping.Layer(r"E:/GNSS/ArcGISServer/file/demo/counties_china_Symbol.lyr")
			eqPointsSymbolLyr = arcpy.mapping.Layer(r"E:/GNSS/ArcGISServer/file/demo/EQPoints_Symbol.lyr")
			EQImgBLNoteSymbolLyr = arcpy.mapping.Layer(r"E:/GNSS/ArcGISServer/file/demo/EQImgBLNote_Symbol.lyr")
			EQImgBLBoundarySymbolLyr = arcpy.mapping.Layer(r"E:/GNSS/ArcGISServer/file/demo/EQImgBLBoundary_Symbol.lyr")
			# eqStationsSymbolLyr = arcpy.mapping.Layer(r"E:/2017/GNSS/ArcGISServer/file/demo/EQStations_Symbol.lyr")
			mxd = arcpy.mapping.MapDocument(mxdPath)
			df = arcpy.mapping.ListDataFrames(mxd, "图层")[0]
			layers = arcpy.mapping.ListLayers(mxd, "*", df)
			print "updateLayer"
			for l in layers:
				if "EQArrows_H_" in l.name:
					arcpy.mapping.UpdateLayer(df,l, arrowHSymbolLyr, True)
				elif "EQArrows_V_" in l.name:
					arcpy.mapping.UpdateLayer(df,l, arrowVSymbolLyr, True)
				elif "eqcontours_" in l.name:
					arcpy.mapping.UpdateLayer(df,l, contourSymbolLyr, True)
				elif "counties_china_" in l.name:
					arcpy.mapping.UpdateLayer(df,l, countiesSymbolLyr, True)
				elif "EQPoints" in l.name:
					arcpy.mapping.UpdateLayer(df,l, eqPointsSymbolLyr, True)
				elif "EQImgBLNote" in l.name:
					arcpy.mapping.UpdateLayer(df,l, EQImgBLNoteSymbolLyr, True)
				elif "EQImgBLBoundary" in l.name:
					arcpy.mapping.UpdateLayer(df,l, EQImgBLBoundarySymbolLyr, True)
				# elif "stations_" in l.name:
					# arcpy.mapping.UpdateLayer(df,l, eqStationsSymbolLyr, True)
			mxd.save()
		except Exception, e:
			print "open mxd error: ", e
			return
def testImport():
	print "this is a test for import..."
