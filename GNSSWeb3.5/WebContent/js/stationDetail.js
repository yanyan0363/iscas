var SID = getQueryString("SID");
document.getElementById("SID").innerHTML=SID;
var map; 
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

var chartReady = false ;
//var greySymbol;
//require(["esri/symbols/PictureMarkerSymbol"], function(PictureMarkerSymbol) {
//	greySymbol = new PictureMarkerSymbol("img/trip-32-grey.png", 16, 16);
//}); 
require(["esri/map","amap/AMapTiledMapServiceLayer"], function(Map,AMapTiledMapServiceLayer)  
{  
//    map = new Map("map", {logo:false,slider: true});  
//    var baseLayer = new esri.layers.ArcGISDynamicMapServiceLayer("http://"+parent.serverIP+":6080/arcgis/rest/services/GNSSBase3_0/MapServer");
//    var baseLayer = new esri.layers.ArcGISDynamicMapServiceLayer(parent.baseUrl);
//    baseLayer.setVisibleLayers([0,1,2]);//[2,10,11]
//    map.addLayer(baseLayer, 0);
//	mapCenter = new esri.geometry.Point(141,38);//四川(104,30.5)
//    map.centerAt(mapCenter);
//    map.setScale(5000000);
    map = new Map("map", {logo:false,slider: true});  
    var baseLayer = new AMapTiledMapServiceLayer();
    map.addLayer(baseLayer, 0);
    map.setScale(5000000);
//    initMapCenter(103.2,29.2);//四川
//    initMapCenter(139.5,38);//日本
//    map.centerAt(mapCenter);
    parent.initMapCenter(map,parent.mapCLon,parent.mapCLat);
    parent.drawStations(map);
    showCurStation();
    initCharts();
//    map.on("load", function(){
//    	showCurStation();
//	});
//    initCharts();
}); 
function initCharts(){
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
	
	/*setInterval(function(){
		
		refreshCharts();
	  },2000);*/
	
	updateChart() ;
}
function sleep(d){
	  for(var t = Date.now();Date.now() - t <= d;);
	}
function updateChart(){
	chartReady = false ;
	refreshCharts();
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
				axisLabel:{
					formatter:function (value, index) {
						var date = new Date(value);
						var hours = date.getHours();
						if(hours < 10){
							hours = '0'+hours;
						}
						var mins = date.getMinutes();
						if(mins < 10){
							mins = '0'+mins;
						}
						var secs = date.getSeconds();
						if(secs < 10){
							secs = '0'+secs;
						}
						return hours+':'+mins+":"+secs;
					}
				}
			},
			yAxis: {
				type: 'value',
				name: '加速度(m/s/s)',
				nameLocation: 'end',
				nameGap: 10,
				nameTextStyle: {
					fontSize:10
				},
				axisLabel: {
					formatter: '{value}',
				},
				min:-10,
				max:10
			},
			series: [
				{
					name:'位移',
					type:'line',
					data:data1,
					symbol: 'none',
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
				left:60,
				right: 45
			},
			tooltip: {
				trigger: 'axis'//axis none
			},
//			legend:{
//				data:['GPS数据','拟合MEMS数据']
//			},
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
				axisLabel:{
					formatter:function (value, index) {
						var date = new Date(value);
						var hours = date.getHours();
						if(hours < 10){
							hours = '0'+hours;
						}
						var mins = date.getMinutes();
						if(mins < 10){
							mins = '0'+mins;
						}
						var secs = date.getSeconds();
						if(secs < 10){
							secs = '0'+secs;
						}
						return hours+':'+mins+":"+secs;
					}
				}
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
				},
				min:-10,
				max:10
			},
			series: [
				{
					name:'GPS数据',
					type:'line',
					data:data1,
					symbol: 'none',
				}
				]
	};
	return option;
}
function showCurStation(){
	//获得该图形的形状
	var geometry = parent.stationsGeometryMap.get(SID);
	map.centerAt(geometry);
	require(["esri/symbols/PictureMarkerSymbol"], function(PictureMarkerSymbol){
		var symbol = new PictureMarkerSymbol("img/trip-32-orange.png", 24, 24);
		var infoGraphic = new esri.Graphic(geometry, symbol);
		infoGraphic.setAttributes({"ID":SID});
		//将客户端图形添加到map中
		map.graphics.add(infoGraphic);
	});
}
function showCurStationResult(result){
	if(result.features == 0){
		//alert("没有该元素");
		return;
	}
	var feature = result.features[0];
	var attr = feature.attributes;
	//获得该图形的形状
	var geometry = feature.geometry;
	map.centerAt(geometry);
	//创建客户端图形
	require(["esri/symbols/PictureMarkerSymbol"], function(PictureMarkerSymbol){
		var symbol = new PictureMarkerSymbol("img/trip-32-orange.png", 24, 24);
		var infoGraphic = new esri.Graphic(geometry, symbol);
		infoGraphic.setAttributes({"ID":attr.StationID});
		//将客户端图形添加到map中
		map.graphics.add(infoGraphic);
	}); 
}
//刷新折线图
function refreshCharts(){
	if(!SID){
		endChartRefresh();
		alert("!SID");
		return;
	}
	$.ajax({
		type:"get",
//		url:"http://127.0.0.1:8090/GNSS/stLast2MinDispWithMEMS",
//		url:"http://127.0.0.1:8090/GNSS/stLastXMinDispWithMEMS",
		url:"http://"+parent.serverIP+":8090/GNSS/stLastXMinDispWithMEMSWithoutFitting",
		data:{
			station:SID,
			x:1 //x值可变，单位：min，表示取前x分钟数据
		},
		dataType:'jsonp',
		jsonp:'callback',
		success:function(msg){
			try {
			if(msg == undefined){
				option = EWGPSChart.getOption();
			    option.series[0].data=null;
//			    option.series[1].data=null;
			    EWGPSChart.setOption(option);
			    
			    option = NSGPSChart.getOption();
			    option.series[0].data=null;
//			    option.series[1].data=null;
			    NSGPSChart.setOption(option);
			    
			    option = ZGPSChart.getOption();
			    option.series[0].data=null;
//			    option.series[1].data=null;
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
			    chartReady = true ;
			    endChartRefresh();
				return;
			}
			//EWGPSChart, NSGPSChart, ZGPSChart
		    option = EWGPSChart.getOption();
		    option.series[0].data=msg.GPSEW;
//		    option.series[1].data=msg.MEMSEW;
		    EWGPSChart.setOption(option);
		    
		    option = NSGPSChart.getOption();
		    option.series[0].data=msg.GPSNS;
//		    option.series[1].data=msg.MEMSNS;
		    NSGPSChart.setOption(option);
		    
		    option = ZGPSChart.getOption();
		    option.series[0].data=msg.GPSZ;
//		    option.series[1].data=msg.MEMSZ;
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
		    chartReady = true ;
		    endChartRefresh();
			}
			catch(err){
				  endChartRefresh();
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			  endChartRefresh();
			chartReady = true ;
		}
	});
}
function endChartRefresh(){
     setTimeout("updateChart()",1000);
}
  
function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]); return null;
 }
function initMapCenter(lon, lat){
	require(["esri/geometry/Point", "esri/SpatialReference", "esri/geometry/webMercatorUtils"], function(Point, SpatialReference, webMercatorUtils) {
	 var point = new esri.geometry.Point(parseFloat(lon),parseFloat(lat),new SpatialReference({wkid:parseInt(4326)}));//这里面需要定义输入点的坐标系，不写默认会是4326
	 var geom = webMercatorUtils.geographicToWebMercator(point);
//	 alert(geom.x+", "+geom.y);
	 mapCenter = new esri.geometry.Point(geom.x,geom.y, map.spatialReference);//(104,30.5);//四川
	});
}
//function drawStations(){
//	//实例化查询参数
//	var query = new esri.tasks.Query();
//	query.outFields = ["StationID"];
//	query.outSpatialReference = map.spatialReference;
//	query.spatialRelationship = esri.tasks.Query.SPATIAL_REL_INTERSECTS;
//	query.returnGeometry = true;
//	query.where = "1=1";
//	//实例化查询对象
//	var queryTask = new esri.tasks.QueryTask(parent.baseUrl + "/" +parent.stationLayer);
//	//进行查询
//	queryTask.execute(query, showStationsResult);
//}
//function showStationsResult(result){
//	if(result.features == 0){
//		return;
//	}
//	require(["esri/symbols/TextSymbol", "esri/symbols/Font", "esri/Color"], function(TextSymbol, Font, Color)  {
//		var labelFont = new Font("10pt", Font.STYLE_ITALIC, Font.VARIANT_NORMAL, Font.WEIGHT_BOLD,"Courier");
//		var labelColor = new Color("black");
//		
//		
//		for(var i = 0; i < result.features.length; i++){
//			//获得该图形的形状
//			var feature = result.features[i];
//			var attr = feature.attributes;
//			var stID = attr.StationID;
//			//获得该图形的形状
//			var geometry = feature.geometry;
//			var attr = {"ID":stID,"type":"Station"};
//			//创建客户端图形
//			var infoGraphic = new esri.Graphic(geometry, parent.greySymbol, attr);
//			//将客户端图形添加到map中
//			map.graphics.add(infoGraphic);
//			//添加台站名称标注
//			var labelID = stID+"_Label";
//			var labelAttr = {"ID":labelID,"type":"STLabel"};
//			var stLabelSymbol = new TextSymbol(stID, labelFont, labelColor);
//			stLabelSymbol.setOffset(0, 5);
//			var labelGraphic = new esri.Graphic(geometry, stLabelSymbol, labelAttr);
//			map.graphics.add(labelGraphic);
//			map.graphics.redraw();
//		}
//	}); 
//}