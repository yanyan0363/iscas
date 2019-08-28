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
            //alert(map.getScale());
            initEQLayer();
            map.on("load", function(){
            	map.graphics.clear();
            	addCenterPoint(map);
            });
            map.on("click", mapClick);
        });
        function initEQLayer(){
        	EQLayer = new esri.layers.ArcGISDynamicMapServiceLayer(eqLyrUrl);
            EQLayer.setOpacity(0.5);
            EQLayer.setVisibleLayers([]);
            map.addLayer(EQLayer, 1);
            //initVisibleEQLayers(1);
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
            			EQPointIdx = layers[i].id
            			visibleLyrs.push(layers[i].id);
            			continue;
            		}
            		if(layers[i].name=="EQPolygons"){
            			EQPolyIdx = layers[i].id
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
            		EQLayer.refresh();
            	}
            	//初始化mapCenter
            	initMapCenterPoint();
            	//初始化Imgs
            	//initImgs();
            	//初始化地震基本信息
            	initBasicInfo();
            });
        }
        function initMapCenterPoint(){
        	//实例化查询参数
        	var query = new esri.tasks.Query();
        	query.outFields = ["num"];
        	query.outSpatialReference = map.spatialReference;
        	//query.spatialRelationship = esri.tasks.Query.SPATIAL_REL_INTERSECTS;
        	query.returnGeometry = true;
        	query.where = EQLyrDefinitions[lyrsMap["EQPoints"]];
        	//实例化查询对象
        	var queryTask = new esri.tasks.QueryTask(eqLyrUrl + "/" + lyrsMap["EQPoints"]);
        	//进行查询
        	queryTask.execute(query, initCenterPoint);
        }
        function initCenterPoint(result){
        	if(result.features == 0){
        		//alert("result.features == 0");
        		return;
        	}
        	mapCenter = result.features[0].geometry;
        	addCenterPoint(map);
        	//mapAH,mapAV
        	if(mapAH != undefined){
        		addCenterPoint(mapAH);
        	}
        	if(mapAV != undefined){
        		addCenterPoint(mapAV);
        	}
        	//mapCH,mapCV
        	if(mapCH != undefined){
        		addCenterPoint(mapCH);
        	}
        	if(mapCV != undefined){
        		addCenterPoint(mapCV);
        	}
        		
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
            	var infoTemplate = new esri.InfoTemplate("${EQID}", "地震ID:${EQID}<br>经度:${Lon}<br>纬度:${Lat}<br>时报:${num}<br>震级:${magnitude}<br>所属台站:${stationID}<br><a href='staticCharts.html?stations=${stationID}&time=${inTime}&eqID=${EQID}'>查看数据</a>");
            	var infoWin = new esri.Graphic(geometry, polygonSymbol, attrInfos, infoTemplate);
            	map.infoWindow.setContent(infoWin.getContent());
            	map.infoWindow.setTitle(infoWin.getTitle());
            	map.infoWindow.show(clickPoint);
               // alert("EQID:"+attr.EQID+"\nintensity:"+attr.intensity+"\nreportLevel:"+attr.reportLevel+"\nnote:"+attr.note);
        	}
        }
        function addCenterPoint(tarMap){
        	if(mapCenter == undefined){
        		//mapCenter = new esri.geometry.Point(647399,3445475,tarMap.spatialReference);
        		return;
        	}
        	//alert(mapCenter.spatialReference.wkid);
        	//var centerPP = new esri.geometry.Point(mapCenter.x,mapCenter.y,mapCenter.spatialReference);
        	//alert(centerPP.x+"\n"+centerPP.y+"\n"+centerPP.spatialReference.wkid);
        	tarMap.centerAt(mapCenter);
        	//tarMap.centerAndZoom(centerPP, 1);
        	//var symbol = new esri.symbol.PictureMarkerSymbol("img/lc.png", 32, 32);
        	//var attr = {"0":"attr0","1":"attr1","2":"attr2","3":"attr3"};
        	//var infoTemplate = new esri.InfoTemplate("${0}", "${1}:attr1 value.<br/>${2}:attr2 value.<br/>${3}:attr3 value.<br/>");
        	//var graphic = new esri.Graphic(centerPP, symbol, attr, infoTemplate);
        	//tarMap.graphics.add(graphic);
        	//tarMap.graphics.redraw();
        }
        var reportNum=0;
        var reportLength = 0;
        tableHeight = parent.LBHeight;
        if(tableHeight < 300){
        	tableHeight = 300;
        }
        initReportsTable();
        initStationsTable();

        function initReportsTable(){
        	$('#reportsTable').bootstrapTable({
        		height:tableHeight,
        		method:'get',
        		//url:'servlet/GetReportsServlet',
        		url:'servlet/GetPublishedReportsServlet',
        		striped:true,
        		queryParams:'eqID='+eqID,
        		cache:false,
        		pagination:false,
        		sortable:false,
        		dataField: 'rows',
        		// pageSize:3,
        		showFooter:false,
        		rowStyle:function rowStyle(row, index){
        			return {
        				css: {"font-size":"12px"}
        			};
        		},
        		columns:[
        			{
        				field:'num',
        				title:'序号',
        				formatter:function (value, row, index){
        					return "第"+value+"报";
        				}
        			},
        			{
        				field:'magnitude',
        				title:'震级',
        			},
        			{
        				field:'inTime',
        				title:'时刻',
        			}
        			],
        			onClickRow:function (row,tr){
        				reportNum = row.num;//表示时报序号
        				reportClick(reportNum,tr);
        			},
        			onLoadSuccess:function(){
        				if(reportNum==undefined){
        					return;
        				}
        				if(reportNum == 0){
        					if($('#reportsTable tbody tr.no-records-found').length == 1){
        						//alert("reportNum == 0 && " + "$('#reportsTable tbody tr.no-records-found').length == 1\n"+$('#reportsTable tbody tr.no-records-found').length);
        						return;
        					}else{
        						//alert("reportNum == 0 && " + "$('#reportsTable tbody tr .no-records-found').length != 1\n"+$('#reportsTable tbody tr.no-records-found').length);
        						reportNum = 1;
        						//tr here 
        						var tr = $("tr:contains('第"+reportNum+"报')");
        						reportClick(reportNum,tr);
        					}
        				}
        				
        				$("tr:contains('第"+reportNum+"报')").addClass("active");
        				//reports的数量发生变化时，刷新界面的初始化信息并刷新imgs
        				if($('#reportsTable tbody tr').length == reportLength){
        					return;
        				}else{
        					reportLength = $('#reportsTable tbody tr').length;
        					initVisibleEQLayers(reportNum);
        				}
        				//若stationsTable为空，则初始化
        				if($('#stationsTable tbody tr.no-records-found').length == 1){
        					initStationsTable();
        				}
        				
        			}
        	});
        }
        function reportClick(reportNum,tr){
        	$("#reportsTable tr.active").removeClass("active");
        	tr.addClass('active');
        	$('#stationsTable').bootstrapTable("refresh",{
        		url:'servlet/GetReportsStationsServlet?'+'eqID='+eqID+"&reportNum="+reportNum,
        	});
        	//根据时报序号刷新map
        	refreshMap(reportNum);
        	//根据时报序号刷新info
        	refreshInfo(reportNum);
        }
        function initStationsTable(){
        	$('#stationsTable').bootstrapTable({
        		height:tableHeight,
        		method:'get',
        		url:'servlet/GetReportsStationsServlet',
        		striped:true,
        		queryParams:'eqID='+eqID+"&reportNum="+reportNum,
        		cache:false,
        		pagination:false,
        		sortable:false,
        		dataField: 'rows',
        		pageSize:3,
        		showFooter:false,
        		rowStyle:function rowStyle(row, index){
        			return {
        				css: {"font-size":"12px"}
        			};
        		},
        		columns:[
        			{
        				field:'station',
        				title:'台站',
        				formatter:function (value, row, index){
        					if(row.isFirst==1){
        						return value+" <span style='color:red;'>(首台)</span>";
        					}
        					return value;
        				}
        			},
        			{
        				field:'magnitude',
        				title:'震级',
        			},
        			{
        				field:'dis',
        				title:'震中距(km)',
        			}
        			],
        			onClickRow:function (row,tr){
        				$("#stationsTable tr.active").removeClass("active");
        				tr.addClass('active');
        				var station = row.station;//表示台站ID
        				refreshChart(eqID,station);
        			}
        	});
        }

        function refreshInfo(reportNum){
        	$.ajax({
        		type:"post",
        		url:"servlet/GetReportDetail",
        		data:{
        			eqID:eqID,
        			reportNum:reportNum
        		},
        		datatype:'json',
        		success:function(msg){
        			var json = JSON.parse(msg);
        			//alert(json.rail+"\n"+json.info.totalArea);
        			//totalArea
        			$("#area").text("覆盖面积：" + json.info.totalArea + " 平方公里");
        			//epi
        			var epi="";
        			if(json.info.epi.L>0){
        				Lon=json.info.epi.L+"E";
        			}else if(json.info.epi.L<0){
        				Lon=json.info.epi.L+"W";
        			}else{
        				Lon=json.info.epi.L;
        			}
        			
        			if(json.info.epi.B>0){
        				Lat=json.info.epi.B+"N";
        			}else if(json.info.epi.B<0){
        				Lat=json.info.epi.L+"S";
        			}else{
        				Lat=json.info.epi.B;
        			}
        			$("#epi").text("震中位置：" + Lon + "," + Lat);
        			
        			//rail
        			var rail = json.rail;
        			$("#rail").empty();
        			$("#rail").append('<div class="lines"><a>&nbsp</a></div>');
        			for(var i=0; i<rail.length;i++){
        				if(rail[i].name==" "){
        					$("#rail").append('<div class="lines"><a onclick="railClick('+rail[i].OID+');">'+(i+1)+'. 铁路：未知名称</a></div>');
        				}else{
        					$("#rail").append('<div class="lines"><a onclick="railClick('+rail[i].OID+');">'+(i+1)+'. ' + rail[i].name+'</a></div>');
        				}
        			}
        			//provinces proList
        			$("#proList").empty();
        			var provs = json.info.provinces;
        			for(var i=0; i<provs.length;i++){
        				$("#proList").append('<div class="provBold">地区：'+ provs[i].prov+'</div>');
        				$("#proList").append('<div class="detail">灾区详情</div>');
        				$("#proList").append('<table id="provTable'+i+'" class="table table-striped table-bordered table-hover"></table>');
        				initProvTable('provTable'+i, provs[i].counties);
        			}
        		}
        	});
        }
        function railClick(OID){
        	addRailShow(OID);
        }
        function initProvTable(tableID, datas){
        	$('#'+tableID).bootstrapTable({
                striped:true,
                cache:false,
                pagination:false,
                sortable:false,
                data:datas,
                showFooter:false,
                rowStyle:function rowStyle(row, index){
                	return {
                	    css: {"font-size":"12px"}
                	  };
                },
                columns:[
                	{
                		field:'name',
                		title:'区县'
                	},
                	{
                		field:'area',
                		title:'面积(平方公里)',
                	},
                	{
                		field:'mag',
                		title:'震级',
                	},
                	{
                		field:'pop',
                		title:'人口',
                	}
                ]
            });
        }
        function initAlarm(){
        	$.ajax({
        		type:"post",
        		url:"servlet/GetAlarmTime",
        		data:{
        			eqID:eqID
        		},
        		datatype:'json',
        		success:function(msg){
        			document.getElementById("alarm").append(msg+" 发布预警信息 ");
        		}
        	});
        }
        setInterval(function(){  
        	 $('#reportsTable').bootstrapTable('refresh',{
        		 url:'servlet/GetPublishedReportsServlet?eqID='+eqID,
        		 silent:true
        	 });
        },2000);
        
        var lineChart = document.getElementById("lineChartDiv");
        lineChart.style.height=screen.height*0.2+'px';
        lineChart.style.width=screen.width*0.4+'px';
        var cchart = echarts.init(lineChart);
        var option = {
        		title: {
        			show:false,
        			text:'水平位移',
        			textAlign:'right',
        			right:0,
        			top:0
        		},
        		grid: {
        			show: true,
        			top: 40,
        			bottom: 20,
        			left:65,
        			right: 60
        		},
        	    tooltip: {
        	        trigger: 'axis'
        	    },
        	    xAxis:  {
        	        name: '时间',
        	        nameLocation: 'end',
        	        nameGap: 10,
        	        nameTextStyle: {
        	        	fontSize:10
        	        },
        	        axislabel:{
        	        	textStyle:{
        	        		fontSize:6
        	        	}
        	        },
        	        boundaryGap: false,
        	    	type: 'category',
        	        data: [],
        	    },
        	    yAxis: {
        	        type: 'value',
        	    	name: '震级',
        	        nameLocation: 'end',
        	        nameGap: 10,
        	        nameTextStyle: {
        	        	fontSize:10
        	        },
        	        axisLabel: {
        	            formatter: '{value}',
        	        }
        	    },
        	    series: [
        	        {
        	            name:'水平位移',
        	            type:'line',
        	            data: [],
        	            markPoint: {
        	            	symbolSize: 46,
        	            	data: [
        	                    {type: 'max', name: '最大值'},
        	                    {type: 'min', name: '最小值'}
        	                ]
        	            }
        	        }
        	    ]
        	};
        cchart.setOption(option);

        function refreshChart(eqid,station){
        		$.ajax({
        			type:"post",
        			url:"servlet/GetMagByEQStation",
        			data:{
        				eqID:eqid,
        				station:station
        			},
        			datatype:'json',
        			success:function(msg){
        				var mm = eval(msg);
        				var labels = mm[1];
        				var m = mm[0]+"";
        				var mData = m.split(",");
        				cchart.setOption({
        					xAxis:  {
        				        data: labels,
        				    },
        				    series: [
        				        {
        				           data: mData,
        				        }
        				    ]
        				});
        			}
        		});
        	}