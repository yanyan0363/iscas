		var map, mapCenter,updateLayer,baseLayer;  
        var visible=[], setLayerVisibility;
        var clickPoint;
        var layerDefinitions = [];//updateLayer的过滤条件
        var baseDefinitions = [];//baseLayer的过滤条件
        var eqPolygons = null;//存储当前页面的地震区域数据
        var identifyTask;
        var identifyParameters;
//        var find, params;
        require(["esri/map","esri/tasks/FindTask","esri/tasks/FindParameters","esri/tasks/IdentifyTask","esri/tasks/IdentifyParameters"], function(Map,FindTask,FindParameters,IdentifyTask,IdentifyParameters)  
        {  
            map = new Map("map", {logo:false,slider: true});  
            baseLayer = new esri.layers.ArcGISDynamicMapServiceLayer("http://"+parent.serverIP+":6080/arcgis/rest/services/GNSSBaseXian80E102/MapServer");
            baseLayer.setVisibleLayers([1,2,10,17,20]);//[0,2,5,10,11]
            map.addLayer(baseLayer, 0);
            baseDefinitions[10] = "0=1";
            baseLayer.setLayerDefinitions(baseDefinitions);
            updateLayer = new esri.layers.ArcGISDynamicMapServiceLayer("http://"+parent.serverIP+":6080/arcgis/rest/services/GNSSUpdateXian80E102/MapServer");
            updateLayer.setOpacity(0.5);
            //layerDefinitions[0] = "OBJECTID = 12 or OBJECTID = 13 or OBJECTID = 14";
            //layerDefinitions[2] = "intensity = 7 and reportLevel = 2";
            layerDefinitions[0] = "EQID='" + eqID+"' and num = 1";
            layerDefinitions[1] = "EQID='" + eqID+"' and num = 1";
            updateLayer.setLayerDefinitions(layerDefinitions);
            map.addLayer(updateLayer, 1);
            //center and zoom
          //alert("L:"+L+"\n"+"B:"+B);
        	//mapCenter = new esri.geometry.Point(L,B);
        	mapCenter = new esri.geometry.Point(L,B);
            map.centerAt(mapCenter);
            map.setScale(5000000);
            //alert(map.getScale());
            map.on("load", addCenterPoint);
            map.on("click", mapClick);
            //map.on("mouse-out", infoWinHide);
        }); 
        function addRailShow(OID){
//        	alert("addRailShow:" + OID);
        	baseDefinitions[10] +=(" or OBJECTID = "+OID);
            baseLayer.setLayerDefinitions(baseDefinitions);
        }
        //查询灾区总面积
        //根据当前map的图层定义，刷新area
        function initEQPolygons(){
        	//实例化查询参数
        	var query = new esri.tasks.Query();
        	query.outFields = ["*"];
        	query.outSpatialReference = map.spatialReference;
        	//是否返回查询结果的空间几何信息
        	query.returnGeometry = true;
        	query.where = layerDefinitions[1];
        	//实例化查询对象
        	var queryTask = new esri.tasks.QueryTask("http://"+parent.serverIP+":6080/arcgis/rest/services/GNSSUpdateXian80E102/MapServer/1");
        	//进行查询
        	queryTask.execute(query, handleEQPolygons);
        }
        function handleEQPolygons(eQPolygonsResult){
        	eqPolygons = eQPolygonsResult;
        }
        function refreshArea(){
        	if(eqPolygons == null){
        		return;
        	}
        	if(eqPolygons.features == 0){
        		alert("没有该元素");
        		return;
        	}
        	var area = 0.0;
        	for(var i = 0; i < eqPolygons.features.length; i++){
        		//获得该图形的形状
                var feature = eqPolygons.features[i];
                var attr = feature.attributes;
                area+=attr.SHAPE_Area;
        	}
//        	alert("sumArea:" + area);
        	$("#area").text("覆盖面积：" + area.toFixed(5) + " 平方公里");
        }
        //here 地震灾情——详细信息——求面元素交集，并提取属性信息，写入地震灾情详细列表
        //here... next...
        //查询灾区的详细信息
        var curPolygon;
        function refreshDetails(){
        	if(eqPolygons == null){
        		return ;
        	}
        	if(eqPolygons.features == 0){
        		alert("没有该元素");
        		return;
        	}
        	for(var i = 0; i < eqPolygons.features.length; i++){
        		//获得该图形的形状
                var feature = eqPolygons.features[i];
                curPolygon = feature.geometry;
                //polygonGeo[i] = geometry;
                //实例化查询参数
                var query = new esri.tasks.Query();
                //合法的geometry类型是Extent, Point, Multipoint, Polyline, Polygon
                query.geometry = curPolygon;
                query.outFields = ["*"];
               // query.outSpatialReference = map.spatialReference;
                query.spatialRelationship = esri.tasks.Query.SPATIAL_REL_INTERSECTS;
                query.returnGeometry = false;
                query.where = "1=1";
                //实例化查询对象
                var queryTask = new esri.tasks.QueryTask("http://"+parent.serverIP+":6080/arcgis/rest/services/GNSSBaseXian80E102/MapServer/20");
                //进行查询,获取范围内的省份
                queryTask.execute(query, handleProv);
        	}
        }
        var proList;
        //根据获取的省份交集，计算重叠区域
        //根据获取的省份，计算县区交集
        function handleProv(provResult){
        	alert("provResult.features.length:"+provResult.features.length);
        	var geometryService = new esri.tasks.GeometryService("http://localhost:6080/arcgis/rest/services/Utilities/Geometry/GeometryServer");
        	//定义高亮图形的符号
        	//1.定义面的边界线符号
        	var outline= new esri.symbol.SimpleLineSymbol(esri.symbol.SimpleLineSymbol.STYLE_SOLID,new esri.Color([255, 0, 0]), 1);
        	//2.定义面符号
        	var polygonSymbol = new esri.symbol.SimpleFillSymbol(esri.symbol.SimpleFillSymbol.STYLE_SOLID, outline,new esri.Color([255, 250, 0, 0.5]));
        	var geometries=[];
        	for(var i = 0; i < provResult.features.length; i++){
        		//获得该图形的形状
                var provFeature = provResult.features[i];
                var prov = provFeature.geometry;
                geometries.push(prov);
                alert("geometries.length:"+geometries.length);
        	}
        	var po = new esri.Graphic(curPolygon, polygonSymbol);
    		//将客户端图形添加到map中
    		map.graphics.add(curPolygon);
        	geometryService.intersect(geometries,curPolygon,function(result){
        		alert("intersect");
            	for(var idx in result){
            		//创建客户端图形
            		var infoGraphic = new esri.Graphic(result[idx], polygonSymbol);
            		//将客户端图形添加到map中
            		map.graphics.add(infoGraphic);
            		//投影，计算面积
//            		 this.outSR = new esri.SpatialReference({ wkid: 102113 });
//            	        geometryService.project([result[idx]], this.outSR, function (geometry) {
//            	            geometryService.simplify(geometry, function (simplifiedGeometries) {
//            	                areasAndLengthParams.polygons = simplifiedGeometries;
//            	                areasAndLengthParams.polygons[0].spatialReference = new esri.SpatialReference(102113);
//            	                geometryService.areasAndLengths(areasAndLengthParams);
//            	            });
//            	        });
//            	        MeasureGeometry(result[idx]);
            	        
            	}
            	
            });
        }
//        function outputAreaAndLength(result) {
//        	alert("area:" + result.areas[0]);
//        }
      //投影转换完成后调用方法  
        function MeasureGeometry(geometry) {  
           
            //如果为面类型需要先进行simplify操作在进行面积测算  
//           if (geometry.type == "polygon") {  
//                var areasAndLengthParams = new esri.tasks.AreasAndLengthsParameters();  
//                areasAndLengthParams.lengthUnit = esri.tasks.GeometryService.UNIT_METER;  
//                areasAndLengthParams.areaUnit = esri.tasks.GeometryService.UNIT_SQUARE_METERS;  
//                this.outSR = new esri.SpatialReference({ wkid: 102113 });  
//                geometryService.project([geometry], this.outSR, function (geometry) {  
//                    geometryService.simplify(geometry, function (simplifiedGeometries) {  
//                        areasAndLengthParams.polygons = simplifiedGeometries;  
//                        areasAndLengthParams.polygons[0].spatialReference = new esri.SpatialReference(102113);  
//                        geometryService.areasAndLengths(areasAndLengthParams);  
//                    });  
//                });  
//                dojo.connect(geometryService, "onAreasAndLengthsComplete", outputAreaAndLength);  
//            }  
        }  
        function showDetails(){
        	
        }
        	
        //根据时报序号刷新map
        function refreshMap(reportNum){
        	//首先，清除infoWindow和map.graphics
       	 	map.infoWindow.hide();
       	 	map.graphics.clear();
       	 	layerDefinitions[0] = "EQID='" + eqID+"' and num = " + reportNum;
            layerDefinitions[1] = "EQID='" + eqID+"' and num = " + reportNum;
            updateLayer.setLayerDefinitions(layerDefinitions);
            baseDefinitions[10] = "0=1";
            baseLayer.setLayerDefinitions(baseDefinitions);
        }
        function infoWinHide(e){
        	map.infoWindow.hide();
        }
        function mapClick(e){
        	//首先，清除infoWindow和map.graphics
        	map.infoWindow.hide();
        	map.graphics.clear();
        	//获取用户点击的地图坐标
        	clickPoint = e.mapPoint;
        	//alert(clickPoint.x+"::"+clickPoint.y);
        	queryPolygon(clickPoint);
        }
        function queryPolygon(clickPoint){
        	//实例化查询参数
        	var query = new esri.tasks.Query();
        	//合法的geometry类型是Extent, Point, Multipoint, Polyline, Polygon
        	query.geometry = clickPoint;
        	query.outFields = ["*"];
        	query.outSpatialReference = map.spatialReference;
        	query.spatialRelationship = esri.tasks.Query.SPATIAL_REL_INTERSECTS;
        	query.returnGeometry = true;
        	query.where = layerDefinitions[1];
        	//实例化查询对象
        	var queryTask = new esri.tasks.QueryTask("http://"+parent.serverIP+":6080/arcgis/rest/services/GNSSUpdateXian80E102/MapServer/1");
        	//进行查询
        	queryTask.execute(query, showPolygonResult);
        }
        function showPolygonResult(queryResult){
        	if(queryResult.features == 0){
        		//alert("没有该元素");
        		return;
        	}
        	for(var i = 0; i < queryResult.features.length; i++){
        		//获得该图形的形状
                var feature = queryResult.features[i];
                var geometry = feature.geometry;
                //定义高亮图形的符号
                //1.定义面的边界线符号
                var outline= new esri.symbol.SimpleLineSymbol(esri.symbol.SimpleLineSymbol.STYLE_SOLID,new esri.Color([255, 0, 0]), 1);
                //2.定义面符号
                var polygonSymbol = new esri.symbol.SimpleFillSymbol(esri.symbol.SimpleFillSymbol.STYLE_SOLID, outline,new esri.Color([255, 250, 250, 0.5]));
                //创建客户端图形
                var infoGraphic = new esri.Graphic(geometry, polygonSymbol);
                //将客户端图形添加到map中
                map.graphics.add(infoGraphic);
                //添加信息窗口
                var attr = feature.attributes;
                area+=attr.SHAPE_Area;
                var attrInfos = {"EQID":attr.EQID,"num":attr.num,"note":attr.note,"magnitude":attr.magnitude};
            	var infoTemplate = new esri.InfoTemplate("${EQID}", "地震ID:${EQID}<br>时报:${num}<br>震级:${magnitude}<br>注释:${note}");
            	var infoWin = new esri.Graphic(geometry, polygonSymbol, attrInfos, infoTemplate);
            	map.infoWindow.setContent(infoWin.getContent());
            	map.infoWindow.setTitle(infoWin.getTitle());
            	map.infoWindow.show(clickPoint);
               // alert("EQID:"+attr.EQID+"\nintensity:"+attr.intensity+"\nreportLevel:"+attr.reportLevel+"\nnote:"+attr.note);
        	}
        }
        function addCenterPoint(){
        	alert("addCenterPoint");
        	var symbol = new esri.symbol.PictureMarkerSymbol("img/lc.png", 32, 32);
        	var attr = {"0":"attr0","1":"attr1","2":"attr2","3":"attr3"};
        	var infoTemplate = new esri.InfoTemplate("${0}", "${1}:attr1 value.<br/>${2}:attr2 value.<br/>${3}:attr3 value.<br/>");
        	var graphic = new esri.Graphic(mapCenter, symbol, attr, infoTemplate);
        	map.graphics.add(graphic);
        }
