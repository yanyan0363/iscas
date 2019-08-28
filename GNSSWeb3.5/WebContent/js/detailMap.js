		var map, mapCenter,EQLayer,baseLayer;  
		var lyrsMap={};
        var clickPoint;
        var EQLyrDefinitions = [];//EQLayer的过滤条件
        var baseDefinitions = [];//baseLayer的过滤条件
        var eqPolygons = null;//存储当前页面的地震区域数据
        var stationLayer=2;
        var curInfoGraphic;//当前高亮显示的面状要素
        //var EQPolyIdx;//EQPolygons的层号
       // var EQPointIdx;//EQPoint的层号
        var Lon,Lat;//震中经度、纬度
        var baseLyrUrl = "http://"+parent.serverIP+":6080/arcgis/rest/services/GNSSBaseXian80E102/MapServer";
        var eqLyrUrl = "http://"+parent.serverIP+":6080/arcgis/rest/services/EQ/"+eqID+"/MapServer";
        require(["esri/map"], function(Map)  
        {  
            map = new Map("map", {logo:false,slider: true});  
            baseLayer = new esri.layers.ArcGISDynamicMapServiceLayer(baseLyrUrl);
            baseLayer.setVisibleLayers([10,17,20]);//[0,2,5,10,11]
            map.addLayer(baseLayer, 0);
            baseDefinitions[10] = "0=1";
            baseLayer.setLayerDefinitions(baseDefinitions);
            map.setScale(5000000);
            initEQLayer();
            //alert(map.getScale());
            map.on("load", function(){
            	map.graphics.clear();
            	//initStations(1);
            	addCenterPoint(map);
            });
            map.on("click", mapClick);
        });
        function initEQLayer(){
        	EQLayer = new esri.layers.ArcGISDynamicMapServiceLayer(eqLyrUrl);
            EQLayer.setOpacity(0.5);
            //EQLayer.setVisibleLayers([1,12]);
            initVisibleEQLayers(1);
        }
        function initVisibleEQLayers(reportNum){
        	//alert("暂未有地震图层信息，请稍后刷新");
        	var url = eqLyrUrl+"/?f=pjson";
            $.get(url,function(result){
            	var json = eval('('+result+')');
            	if(json.hasOwnProperty("error")&&json.error.code==500){
            		//alert(500);
            		return;
            	}
            	var length = json.layers.length;
            	var layers = json.layers;
            	var visibleLyrs = [];
            	
            	for(var i=0; i<length; i++){
            		lyrsMap[layers[i].name] = layers[i].id;
            		if(layers[i].name=="EQPoints"){
            			//EQPointIdx = layers[i].id
            			visibleLyrs.push(layers[i].id);
            			continue;
            		}
            		if(layers[i].name=="EQPolygons"){
            			//EQPolyIdx = layers[i].id
            			visibleLyrs.push(layers[i].id);
            			continue;
            		}
            	}
            	if(visibleLyrs == undefined || visibleLyrs.length < 2 ){
            		alert("暂未有地震图层信息，请稍后刷新");
            	}else{
            		EQLayer.setVisibleLayers(visibleLyrs);
            		EQLyrDefinitions[lyrsMap["EQPoints"]] = "num = "+reportNum+" and type='epicenter'";
            		EQLyrDefinitions[lyrsMap["EQPolygons"]] = "num = " + reportNum;
            		EQLayer.setLayerDefinitions(EQLyrDefinitions);
            		map.addLayer(EQLayer, 1);
            		EQLayer.refresh();
            	}
            	//初始化arrows的图层显示
            	initATimeLineFromService();
            	//初始化contours的图层显示
            	showContours();
            	//初始化Imgs
            	initImgs();
            });
        }
        function initStations(rNum){
        	//实例化查询参数
        	var query = new esri.tasks.Query();
        	query.outFields = ["stationID"];
        	query.outSpatialReference = map.spatialReference;
        	query.spatialRelationship = esri.tasks.Query.SPATIAL_REL_INTERSECTS;
        	query.returnGeometry = true;
        	query.where = "1=1";
        	//实例化查询对象
        	var queryTask = new esri.tasks.QueryTask(baseLyrUrl + "/" + stationLayer);
        	//进行查询
        	queryTask.execute(query, showStationsResult);
        	
        }
        function showStationsResult(result){
        	if(result.features == 0){
        		//alert("没有该元素");
        		return;
        	}
        	require(["esri/symbols/SimpleMarkerSymbol","esri/symbols/SimpleLineSymbol","esri/Color"], 
        			function(SimpleMarkerSymbol,SimpleLineSymbol,Color)  
        	{
        		//inSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.STYLE_X,7,new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID,new Color([0,139,0]),1),new Color([0,139,0]));
        		 inSymbol = new esri.symbol.PictureMarkerSymbol("img/fill-green-16.png", 16, 16);
        	}); 
        	var stationArray = new Array();
        	for(var i = 0; i < result.features.length; i++){
        		//获得该图形的形状
                var feature = result.features[i];
                var attr = feature.attributes;
                var geometry = feature.geometry;
                //创建客户端图形
                var infoGraphic = new esri.Graphic(geometry, inSymbol);
                infoGraphic.setAttributes({"ID":attr.StationID});
                stationArray.push("{\"ID\":\""+attr.StationID+"\"}");
                //将客户端图形添加到map中
                map.graphics.add(infoGraphic);
        	}
        	initCurReportStations(reportNum);
        }
        function initCurReportStations(rNum){
        	$.ajax({
        		type:"get",
        		url:"servlet/GetCurReportStationsServlet",
        		data:{
        			eqID:eqID,
        			reportNum:rNum
        		},
        		dataType:'json',
        		success:function(msg){
        			var stationsArray = new Array();
        			if(msg == undefined || msg.stations.length == 0){
        				//alert("暂无台站位移数据");
        				return;
        			}
        			var graphics = map.graphics.graphics;
        			//alert(msg.stations.length+"::"+msg.stations+"\ngraphics.length::"+graphics.length);
        			require(["esri/symbols/SimpleMarkerSymbol","esri/symbols/SimpleLineSymbol","esri/Color"], 
        					function(SimpleMarkerSymbol,SimpleLineSymbol,Color)  
        			{
        				overSymbol = new esri.symbol.PictureMarkerSymbol("img/fill-red-16.png", 16, 16);
        			});
        			for(var i=0;i<msg.stations.length;i++){
        				//alert("msg.stations[i]:::"+msg.stations[i]);
        				for(var j=0;j<graphics.length;j++){
        					//alert(msg.stations[i]+"\ngraphics[j].attributes.ID:::"+graphics[j].attributes.ID+"\n"+(msg.stations[i] == graphics[j].attributes.ID));
        					if(msg.stations[i] == graphics[j].attributes.ID){
        						graphics[j].setSymbol(overSymbol);
        						break;
        					}
        				}
        			}
        			map.graphics.redraw();
        		},
        		error:function(XMLHttpRequest, textStatus, errorThrown){
        			//alert(XMLHttpRequest.status+"\n"+XMLHttpRequest.statusText+"\n"+XMLHttpRequest.responseText +"\n"+XMLHttpRequest.readyState+"\n"+textStatus+"\n"+errorThrown);
        			alert("获取当前报表的台站列表出错了，请联系管理员");
        		}
        	});
        }
        function addRailShow(OID){
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
        	query.where = EQLyrDefinitions[lyrsMap["EQPolygons"]];
        	//实例化查询对象
        	var queryTask = new esri.tasks.QueryTask(eqLyrUrl+"/"+lyrsMap["EQPolygons"]);
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
        	$("#area").text("覆盖面积：" + area.toFixed(5) + " 平方公里");
        }
        	
        //根据时报序号刷新map
        function refreshMap(reportNum){
        	//首先，清除infoWindow和map.graphics
       	 	map.infoWindow.hide();
       	 	if(map.graphics!=undefined){
       	 		map.graphics.clear();
       	 	}
       	 	if(lyrsMap["EQPoints"] == undefined || lyrsMap["EQPolygons"] == undefined){
       	 		initVisibleEQLayers(reportNum);
       	 	}
       	 	EQLyrDefinitions[lyrsMap["EQPoints"]] = "num = " + reportNum + "  and type='epicenter'";
       	 	EQLyrDefinitions[lyrsMap["EQPolygons"]] = "num = " + reportNum;
            EQLayer.setLayerDefinitions(EQLyrDefinitions);
            baseDefinitions[10] = "0=1";
            baseLayer.setLayerDefinitions(baseDefinitions);
            //根据时报序号刷新当前时报的台站符号
            initStations(reportNum);
        }
        function infoWinHide(e){
        	map.infoWindow.hide();
        }
        function mapClick(e){
        	//首先，清除infoWindow和map.graphics中高亮显示的面状要素
        	map.infoWindow.hide();
        	map.graphics.remove(curInfoGraphic);
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
        	query.where = EQLyrDefinitions[lyrsMap["EQPolygons"]];
        	//实例化查询对象
        	var queryTask = new esri.tasks.QueryTask(eqLyrUrl+"/" + lyrsMap["EQPolygons"]);
        	//进行查询
        	queryTask.execute(query, showPolygonResult);
        }
        function showPolygonResult(queryResult){
        	if(queryResult.features == 0){
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
                curInfoGraphic = new esri.Graphic(geometry, polygonSymbol);
                //将客户端图形添加到map中
                map.graphics.add(curInfoGraphic);
                //添加信息窗口
                var attr = feature.attributes;
                area+=attr.SHAPE_Area;
                var attrInfos = {"EQID":eqID,"Lon":Lon,"Lat":Lat,"num":attr.num,"magnitude":attr.magnitude,"stationID":attr.stationID,"inTime":attr.inTime};
            	var infoTemplate = new esri.InfoTemplate("${EQID}", "地震ID:${EQID}<br>经度:${Lon}<br>纬度:${Lat}<br>时报:${num}<br>震级:${magnitude}<br>所属台站:${stationID}<br><a href='staticCharts.html?stations=${stationID}&time=${inTime}'>查看数据</a>");
            	var infoWin = new esri.Graphic(geometry, polygonSymbol, attrInfos, infoTemplate);
            	map.infoWindow.setContent(infoWin.getContent());
            	map.infoWindow.setTitle(infoWin.getTitle());
            	map.infoWindow.show(clickPoint);
               // alert("EQID:"+attr.EQID+"\nintensity:"+attr.intensity+"\nreportLevel:"+attr.reportLevel+"\nnote:"+attr.note);
        	}
        }
        function addCenterPoint(tarMap){
        	var mapCenter = new esri.geometry.Point(647399,3445475,tarMap.spatialReference);
        	tarMap.centerAt(mapCenter);
        	//var symbol = new esri.symbol.PictureMarkerSymbol("img/lc.png", 32, 32);
        	//var attr = {"0":"attr0","1":"attr1","2":"attr2","3":"attr3"};
        	//var infoTemplate = new esri.InfoTemplate("${0}", "${1}:attr1 value.<br/>${2}:attr2 value.<br/>${3}:attr3 value.<br/>");
        	//var graphic = new esri.Graphic(mapCenter, symbol, attr, infoTemplate);
        	//map.graphics.add(graphic);
        }
