		var map, mapCenter,EQLayer,baseLayer;  
		var lyrsMap={};
        var clickPoint;
        var EQLyrDefinitions = [];//EQLayer的过滤条件
        var baseDefinitions = [];//baseLayer的过滤条件
        var eqPolygons = null;//存储当前页面的地震区域数据
        var stationLayer=0;
        var curInfoGraphic;//当前高亮显示的面状要素
        require(["esri/map"], function(Map)  
        {  
            map = new Map("map", {logo:false,slider: true});  
            baseLayer = new esri.layers.ArcGISDynamicMapServiceLayer(baseLyrUrl);
            baseLayer.setVisibleLayers([0,2,3]);//[0,2,5,10,11]
            map.addLayer(baseLayer, 0);
            map.setScale(5000000);
            map.on("load", function(){
            	map.graphics.clear();
            });
            map.on("click", mapClick);
            initEQLayer();
            initReports();
        });
        function initEQLayer(){
        	EQLayer = new esri.layers.ArcGISDynamicMapServiceLayer(eqLyrUrl);
            EQLayer.setOpacity(0.5);
            EQLayer.setVisibleLayers([1,10]);
            map.addLayer(EQLayer, 1);
            initVisibleEQLayers(1);
        }
        function initVisibleEQLayers(reportNum){
        	if(visibleEQLyrs == undefined || visibleEQLyrs.length < 2 ){
        		var url = eqLyrUrl+"/?f=pjson";
        		$.get(url,function(result){
        			var json = eval('('+result+')');
        			if(json.hasOwnProperty("error")&&json.error.code==500){
        				return;
        			}
        			var length = json.layers.length;
        			var layers = json.layers;
        			for(var i=0; i<length; i++){
        				lyrsMap[layers[i].name] = layers[i].id;
        				if(layers[i].name=="EQPoints"){
        					EQPointIdx = layers[i].id
        					visibleEQLyrs.push(layers[i].id);
        					continue;
        				}
        				if(layers[i].name=="EQPolygons"){
        					EQPolyIdx = layers[i].id
        					visibleEQLyrs.push(layers[i].id);
        					continue;
        				}
        			}
        			if(visibleEQLyrs == undefined || visibleEQLyrs.length < 2 ){
        				alert("暂未有地震图层信息，请稍后刷新");
        			}else{
        				EQLayer.setVisibleLayers(visibleEQLyrs);
        			}
//        			EQLayer.setVisibleLayers(visibleEQLyrs);
        			EQLyrDefinitions[lyrsMap["EQPoints"]] = "num = "+reportNum+" and type='epicenter'";
    				EQLyrDefinitions[lyrsMap["EQPolygons"]] = "num = " + reportNum;
//    				alert(lyrsMap["EQPoints"]+"::"+EQLyrDefinitions[lyrsMap["EQPoints"]]+"\n"+lyrsMap["EQPolygons"]+"::"+EQLyrDefinitions[lyrsMap["EQPolygons"]]);
    				EQLayer.setLayerDefinitions(EQLyrDefinitions);
    				EQLayer.refresh();
    				//初始化mapCenter
        			initMapCenterPoint(initCenterPoint);
        		});
        	}else{
        		EQLayer.setVisibleLayers(visibleEQLyrs);
				EQLyrDefinitions[lyrsMap["EQPoints"]] = "num = "+reportNum+" and type='epicenter'";
				EQLyrDefinitions[lyrsMap["EQPolygons"]] = "num = " + reportNum;
				EQLayer.setLayerDefinitions(EQLyrDefinitions);
				EQLayer.refresh();
				//初始化mapCenter
    			initMapCenterPoint(initCenterPoint);
        	}
        }
        function initCenterPoint(result){
        	if(result.features == undefined){
        		return;
        	}
        	mapCenter = result.features[0].geometry;
        	map.centerAt(mapCenter);
        }
        function initReports(){
        	$('#reportsTbl').bootstrapTable({
        		//height:600,
        		method:'get',
        		url:'servlet/GetAReportServlet',
        		datatype:'json',
        		striped:true,
        		queryParams:'eqID='+eqID,
        		cache:false,
        		pagination:false,
        		sortable:false,
        		showFooter:false,
        		rowStyle:function rowStyle(row, index){
        			return {
        				css: {"font-size":"12px"}
        			};
        		},
        		columns:[
            		{
            			field:'station',
            			title:'台站<br>&nbsp;&nbsp;',
            		},
            		{
            			field:'PT',
            			title:'P波时刻<br>&nbsp;&nbsp;',
            		},
            		{
            			field:'M',
            			title:'震级<br>&nbsp;&nbsp;',
            		},
            		{
            			field:'EpiDis',
            			title:'震中距离<br>(km)'
            		}
            		],
        		onClickRow:function (row,tr){
        			reportStationClick(row.station);
        		}
        	});
        }
        
        function reportStationClick(stationID){
        	$('#Reports').hide();
			$('#StationInfo').show();
			document.getElementById('SID').innerHTML=stationID;
			initStInEQ(stationID);
			stationReportsInit(stationID);
			initStationInMap(stationID);
        }
        function backToIndex(){
        	$("#left2").hide();
        	$("#left1").show();
        	$('#Reports').show();
			$('#StationInfo').hide();
			EQLayer.setVisibleLayers(visibleEQLyrs);
        	EQLayer.refresh();
        	map.graphics.clear();
        }
        function initStInEQ(stationID){
        	$("#stLoc").text(stationsGeometryMap[stationID].x.toFixed(2)+", " + stationsGeometryMap[stationID].y.toFixed(2));
        	$.ajax({
        		type:"post",
        		url:"servlet/GetStInfoByEQSt",
        		data:{
        			eqID:eqID,
        			stationID:stationID
        		},
        		dataType:'json',
        		success:function(msg){
        			$("#stEpiDis").text(msg.EpiDis);
        			$("#stPT").text(msg.PT);
        			$("#stST").text(msg.ST);
        			$("#stPTT").text(parseFloat(new Date(msg.PT).getTime() - eqTime.getTime())/1000);
        			$("#stSTT").text(parseFloat(new Date(msg.ST).getTime() - eqTime.getTime())/1000);
        			$("#stMaxH").text(msg.maxH);
        			$("#stMaxV").text(msg.maxV);
        		},
        		error:function(XMLHttpRequest, textStatus, errorThrown){
        			alert("获取当前地震的基本信息出错了，请联系管理员");
        		}
        	});
        }
        function initStationInMap(stationID){
        	EQLayer.setVisibleLayers([EQPointIdx]);
        	EQLayer.refresh();
        	//添加震中点到stationID的连接线，即mapCenter与stationID的连接线
        	//实例化查询参数,获取当前台站geometry
        	var query = new esri.tasks.Query();
        	query.outFields = ["stationID"];
        	query.outSpatialReference = map.spatialReference;
        	query.spatialRelationship = esri.tasks.Query.SPATIAL_REL_INTERSECTS;
        	query.returnGeometry = true;
        	query.where = "stationID='"+stationID+"'";
        	//实例化查询对象
        	var queryTask = new esri.tasks.QueryTask("http://localhost:6080/arcgis/rest/services/Base2_0/MapServer/0");
        	//进行查询
        	queryTask.execute(query, showCurStationResult);
        }
        function showCurStationResult(result){
        	if(result.features == 0){
        		return;
        	}
        	var feature = result.features[0];
        	var attr = feature.attributes;
        	//获得该图形的形状
        	var geometry = feature.geometry;
        	map.centerAt(geometry);
        	//创建客户端图形
        	require(["esri/geometry/Polyline","esri/symbols/SimpleLineSymbol","esri/Color","esri/graphic"],
        			function(Polyline,SimpleLineSymbol,Color,Graphic){
        		var lSymbol = new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID,new Color([255,0,0]),2);
        		var sPolyline = new Polyline(map.spatialReference);
        		//sPolyline.addPath([mapCenter, geometry]);
        		sPolyline.addPath([[mapCenter.x, mapCenter.y], [geometry.x, geometry.y]]);
        		var lineGraphic = new Graphic(sPolyline, lSymbol);
        		//将客户端图形添加到map中
        		map.graphics.add(lineGraphic);
        		map.graphics.redraw();
        	});
        }
        function stationReportsInit(stationID){
        	$('#stationReports').bootstrapTable({
        		//height:380,
        		method:'get',
        		url:'servlet/GetStationReportsServlet',
        		datatype:'json',
        		striped:true,
        		queryParams:'eqID='+eqID+'&SID='+stationID,
        		cache:false,
        		pagination:false,
        		sortable:false,
        		showFooter:false,
        		rowStyle:function rowStyle(row, index){
        			return {
        				css: {"font-size":"12px"}
        			};
        		},
        		columns:[
        			{
        				field:'num',
        				title:'序号'
        			},
        			{
        				field:'M',
        				title:'震级'
        			},
        			{
        				field:'PT',
        				title:'P波时刻'
        			},
        			{
        				field:'MaxDis',
        				title:'最大位移',
        				formatter:function (value, row, index){
        					
        					return "H: - m<br/>V: - m";
//        					return "H: "+row.maxH+"m<br/>V: "+row.maxV+"m";
        				}
        			}
        			]
        	});
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
            	var infoTemplate = new esri.InfoTemplate("${EQID}", "地震ID:${EQID}<br>经度:${Lon}<br>纬度:${Lat}<br>时报:${num}<br>震级:${magnitude}<br>所属台站:${stationID}<br><a onclick=\"showStationCharts('${stationID}');\">查看数据</a>");
            	var infoWin = new esri.Graphic(geometry, polygonSymbol, attrInfos, infoTemplate);
            	map.infoWindow.setContent(infoWin.getContent());
            	map.infoWindow.setTitle(infoWin.getTitle());
            	map.infoWindow.show(clickPoint);
        	}
        }
        var lineChart = document.getElementById("magChartDiv");
        var cchart = echarts.init(lineChart);
        setMagChartOption(cchart);
function setMagChartOption(chart){
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
					name:'震级',
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
	chart.setOption(option);
}
function refreshMagChart(eqid){
	$.ajax({
		type:"post",
		url:"servlet/GetMagByEQID",
		data:{
			eqID:eqid
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
//var XChart,YChart,ZChart;
var EWGPScanvas = document.getElementById("EWGPS");
var NSGPScanvas = document.getElementById("NSGPS");
var ZGPScanvas = document.getElementById("ZGPS");
var EWGPSChart, NSGPSChart, ZGPSChart;
var option;
var EWMEMScanvas = document.getElementById("EWMEMS");
var NSMEMScanvas = document.getElementById("NSMEMS");
var ZMEMScanvas = document.getElementById("ZMEMS");
var EWMEMSChart, NSMEMSChart, ZMEMSChart;
function showStationCharts(sid){
	if(sid!=undefined){
		$('#Reports').hide();
		$('#StationInfo').show();
		document.getElementById('SID').innerHTML=sid;
		stationReportsInit(sid);
	}else{
		sid = document.getElementById("SID").innerHTML;
	}
	$("#left1").hide();
	$("#left2").show();
	require(["esri/map"], function(Map){
		var mapInStChart = new Map("mapInStChart", {logo:false,slider: false}); 
		var baseLayerSt = new esri.layers.ArcGISDynamicMapServiceLayer(baseLyrUrl);
		baseLayerSt.setVisibleLayers([0,2,3]);//[0,2,5,10,11]
		mapInStChart.addLayer(baseLayerSt, 0);
		mapInStChart.setScale(5000000);
		mapInStChart.on("load", function(){
			var curStInSt = stationsGeometryMap[sid];
			mapInStChart.centerAt(curStInSt);
			require(["esri/symbols/PictureMarkerSymbol"], function(PictureMarkerSymbol){
				var symbol = new PictureMarkerSymbol("img/trip-32-orange.png", 32, 32);
				var infoGraphic = new esri.Graphic(curStInSt, symbol);
				mapInStChart.graphics.add(infoGraphic);
			});
		});
	});
	//震级折线图
	initMagChartInStChartDiv(sid);
	//位移折线图
	initCharts(sid);
}
function initCharts(sid){
	/*
	 * 初始化台站位移数据
	 */
	EWGPSChart = echarts.init(EWGPScanvas);
	NSGPSChart = echarts.init(NSGPScanvas);
	ZGPSChart = echarts.init(ZGPScanvas);
	EWMEMSChart = echarts.init(EWMEMScanvas);
	NSMEMSChart = echarts.init(NSMEMScanvas);
	ZMEMSChart = echarts.init(ZMEMScanvas);
	
	option = setOpt2();
	EWGPSChart.setOption(option);
	option = setOpt2();
	NSGPSChart.setOption(option);
	option = setOpt2();
	ZGPSChart.setOption(option);
	option = setOpt1();
	EWMEMSChart.setOption(option);
	option = setOpt1();
	NSMEMSChart.setOption(option);
	option = setOpt1();
	ZMEMSChart.setOption(option);
	
	initChartsData(sid);
}
//刷新折线图
function initChartsData(sid){
	if(!sid){
		alert("!sid");
		return;
	}
	$.ajax({
		type:"get",
		url:"servlet/GetHisDispWithMEMSByEQSt",
		data:{
			stationID:sid,
			eqTime:eqTime.getFullYear()+"-"+(eqTime.getMonth()+1)+"-"+eqTime.getDate()+" "+eqTime.getHours()+":"+eqTime.getMinutes()+":"+eqTime.getSeconds()
		},
		dataType:'json',
		success:function(msg){
			if(msg == undefined){
				option = EWGPSChart.getOption();
			    option.series[0].data=null;
			    option.series[1].data=null;
			    EWGPSChart.setOption(option);
			    
			    option = NSGPSChart.getOption();
			    option.series[0].data=null;
			    option.series[1].data=null;
			    NSGPSChart.setOption(option);
			    
			    option = ZGPSChart.getOption();
			    option.series[0].data=null;
			    option.series[1].data=null;
			    ZGPSChart.setOption(option);
			    //MEMS
			    option = EWMEMSChart.getOption();
			    option.series[0].data=null;
			    EWMEMSChart.setOption(option);
			    
			    option = NSMEMSChart.getOption();
			    option.series[0].data=null;
			    NSMEMSChart.setOption(option);
			    
			    option = ZMEMSChart.getOption();
			    option.series[0].data=null;
			    ZMEMSChart.setOption(option);
				return;
			}
			//EWGPSChart, NSGPSChart, ZGPSChart
		    option = EWGPSChart.getOption();
		    option.series[0].data=msg.GPSEW;
		    option.series[1].data=msg.MEMSEW;
		    EWGPSChart.setOption(option);
		    
		    option = NSGPSChart.getOption();
		    option.series[0].data=msg.GPSNS;
		    option.series[1].data=msg.MEMSNS;
		    NSGPSChart.setOption(option);
		    
		    option = ZGPSChart.getOption();
		    option.series[0].data=msg.GPSZ;
		    option.series[1].data=msg.MEMSZ;
		    ZGPSChart.setOption(option);
		    //MEMS
		    option = EWMEMSChart.getOption();
		    option.series[0].data=msg.MEMSEWAcc;
		    EWMEMSChart.setOption(option);
		    
		    option = NSMEMSChart.getOption();
		    option.series[0].data=msg.MEMSNSAcc;
		    NSMEMSChart.setOption(option);
		    
		    option = ZMEMSChart.getOption();
		    option.series[0].data=msg.MEMSZAcc;
		    ZMEMSChart.setOption(option);
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			alert("图表刷新出错了，请联系管理员");
		}
	});
}
function setOpt1(label,data1){
	// 指定图表的配置项和数据
	var option = {
			title: {
				show:false,
			},
			grid: {
				show: true,
				top: 40,
				bottom: 30,
				left:60,
				right: 45
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
				type: 'time',
			},
			yAxis: {
				type: 'value',
				name: '加速度(mg)',
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
					name:'位移',
					type:'line',
					data:data1,
					symbol: 'none',
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
	return option;
}
function setOpt2(label,data1, data2){
	var option = {
			title: {
				show:false,
			},
			grid: {
				show: true,
				top: 40,
				bottom: 30,
				left:40,
				right: 45
			},
			tooltip: {
				trigger: 'axis'//axis none
			},
			legend:{
				data:['GPS数据','拟合MEMS数据']
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
				type: 'time',
			},
			yAxis: {
				type: 'value',
				name: '位移(m)',
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
					name:'GPS数据',
					type:'line',
					data:data1,
					symbol: 'none',
					markPoint: {
						symbolSize: 60,
						data: [
							{type: 'max', name: '最大值'},
							{type: 'min', name: '最小值'}
							]
					}
				},
				{
					name:'拟合MEMS数据',
					type:'line',
					data:data2,
					symbol: 'none',
					markPoint: {
						symbolSize: 60,
						data: [
							{type: 'max', name: '最大值'},
							{type: 'min', name: '最小值'}
							]
					}
				}
				]
	};
	return option;
}
        
function initMagChartInStChartDiv(sid){
	var magChartInStDiv = document.getElementById("magChartInSt");
	var magChartInSt = echarts.init(magChartInStDiv);
	setMagChartOption(magChartInSt);
	$.ajax({
		type:"post",
		url:"servlet/GetMagByEQIDStID",
		data:{
			eqID:eqID,
			stationID:sid
		},
		datatype:'json',
		success:function(msg){
			var mm = eval(msg);
			var labels = mm[1];
			var m = mm[0]+"";
			var mData = m.split(",");
			magChartInSt.setOption({
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
setInterval(function(){
	refreshMagChart(eqID);
},1000);