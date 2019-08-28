var eqID = parent.EQID;
var stationID=parent.stationID;
var mapInStChart, baseLayer; 
var eqPoint;//震中点
var eqTime;
var stPoint;//当前台站点，geometry
var baseLyrUrl = parent.baseUrl;
var PT ;//P波到达时刻
init();
function init(){
	document.getElementById('stationID').innerHTML=stationID;
	document.getElementById('SID').innerHTML=stationID;
	initBaseMap();
	initMagChartInStChartDiv(stationID);
	initStInfoInEQ();
	initCharts(stationID);
	initEQInfo();
//	showStationCharts(stationID);
	//震级折线图
	//位移折线图
}
function initBaseMap(){
	require(["esri/map", "amap/AMapTiledMapServiceLayer"], function(Map, AMapTiledMapServiceLayer) {  
		mapInStChart = new Map("mapInStChart", {logo:false,slider: true});  
		baseLayer = new AMapTiledMapServiceLayer();
		mapInStChart.addLayer(baseLayer, 0);
		mapInStChart.setScale(5000000);
		parent.initMapCenter(mapInStChart,parent.mapCLon,parent.mapCLat);
		parent.drawStations(mapInStChart);
	});
}
function initEQInfo(){
	$.ajax({
		type:"post",
		url:"servlet/GetEQInfoByID",
		data:{
			eqID:eqID
		},
		dataType:'json',
		success:function(msg){
			$("#oTimeInIdx").text(msg.originTime);
			$("#epiInIdx").text(msg.epi);
			$("#gpsMagInIdx").text(msg.gpsMag);
			$("#memsMagInIdx").text(msg.memsMag);
			$("#fStInIdx").text(msg.firstSt);
			eqTime=new Date(msg.originTime);
			if($("#stPTT").text()=="*"&& $("#stPT").text()!="*"){
				$("#stPTT").text(parseFloat(new Date($("#stPT").text()).getTime() - eqTime.getTime())/1000);
				$("#stSTT").text(parseFloat(new Date($("#stST").text()).getTime() - eqTime.getTime())/1000);
			}
			
			initEQPointSt(msg.lon,msg.lat);
			if(PT!=undefined){
				initChartsData(stationID, PT);
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
		}
	});
}
function initEQPointSt(lon,lat){
	//地图上添加EQPoint
	require(["esri/geometry/Point", "esri/SpatialReference", "esri/geometry/webMercatorUtils"], function(Point, SpatialReference, webMercatorUtils) {
		 var point = new esri.geometry.Point(parseFloat(lon),parseFloat(lat),new SpatialReference({wkid:parseInt(4326)}));//这里面需要定义输入点的坐标系，不写默认会是4326
		 var geom = webMercatorUtils.geographicToWebMercator(point);
		 eqPoint = new esri.geometry.Point(geom.x,geom.y, new SpatialReference({wkid:parseInt(parent.wkid)}));
		 mapInStChart.centerAt(eqPoint);
		 var symbol = new esri.symbol.PictureMarkerSymbol("img/ep.png", 32, 32);
		 var attr = {"ID":eqID,"type":"EQPoint"};
		 var graphic = new esri.Graphic(eqPoint, symbol, attr);
		 mapInStChart.graphics.add(graphic);
		 initStationInMap();
		});
}
function initStationInMap(){
	stPoint = parent.stationsGeometryMap.get(stationID);
	mapInStChart.centerAt(stPoint);
	require(["esri/symbols/PictureMarkerSymbol", "esri/geometry/webMercatorUtils"], function(PictureMarkerSymbol, webMercatorUtils){
		var lonLat = webMercatorUtils.xyToLngLat(stPoint.x, stPoint.y, true);
		$("#stLoc").text(lonLat[0].toFixed(2)+"E, " + lonLat[1].toFixed(2)+"N");//修改成为BL
		var symbol = new PictureMarkerSymbol("img/trip-32-orange.png", 16, 16);
		var infoGraphic = new esri.Graphic(stPoint, symbol);
		mapInStChart.graphics.add(infoGraphic);
	});
	//创建客户端图形
	require(["esri/geometry/Polyline","esri/symbols/SimpleLineSymbol","esri/Color","esri/graphic"],function(Polyline,SimpleLineSymbol,Color,Graphic){
		var lSymbol = new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID,new Color([255,0,0]),2);
		var sPolyline = new Polyline(mapInStChart.spatialReference);
		//sPolyline.addPath([mapCenter, geometry]);
//		sPolyline.addPath([[eqPoint.x, eqPoint.y], [stPoint.x, stPoint.y]]);
		sPolyline.addPath([eqPoint, stPoint]);
		var lineGraphic = new Graphic(sPolyline, lSymbol);
		//将客户端图形添加到map中
		mapInStChart.graphics.add(lineGraphic);
		mapInStChart.graphics.redraw();
	});
}

//截取URL参数
function getQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
	var r = window.location.search.substr(1).match(reg);
	if (r != null) return unescape(r[2]); return null;
}
function initStInfoInEQ(){
//	$("#stLoc").text(stPoint.x.toFixed(2)+", " + stPoint.y.toFixed(2));
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
			PT= msg.PT;
			$("#stST").text(msg.ST);
			$("#stMaxH").text(msg.maxH);
			$("#stMaxV").text(msg.maxV);
			if(eqTime!=undefined){
				$("#stPTT").text(parseFloat(new Date(msg.PT).getTime() - eqTime.getTime())/1000);
				$("#stSTT").text(parseFloat(new Date(msg.ST).getTime() - eqTime.getTime())/1000);
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
		}
	});
}
function setMagChartOption(chart){
	var option = {
	title: {
		show:false,
		text:'水平位移',
		textAlign:'right',
		right:0,
		top:0
	},
	legend: {
        data:[
        	{name:'GPS震级'},
        	{name:'MEMS震级'}
        ]
    },
	grid: {
		show: true,
		top: 30,
		bottom: 30,
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
			name:'GPS震级',
			type:'line',
			data: [],
			markPoint: {
				symbolSize: 60,
				data: [
					{type: 'max', name: '最大值'},
					]
			}
		},
		{
			name:'MEMS震级',
			type:'line',
			data: [],
			markPoint: {
				symbolSize: 60,
				data: [
					{type: 'max', name: '最大值'},
					]
			}
		}
		]
	};
	chart.setOption(option);
}

//var XChart,YChart,ZChart;
//var EWGPScanvas = document.getElementById("EWGPS");
//var NSGPScanvas = document.getElementById("NSGPS");
//var ZGPScanvas = document.getElementById("ZGPS");
//var EWMEMScanvas = document.getElementById("EWMEMS");
//var NSMEMScanvas = document.getElementById("NSMEMS");
//var ZMEMScanvas = document.getElementById("ZMEMS");
var EWGPSChart, NSGPSChart, ZGPSChart;
var option;
var EWMEMSChart, NSMEMSChart, ZMEMSChart;

function initCharts(sid){
	/*
	 * 初始化台站位移数据
	 */
	var EWGPScanvas = document.getElementById("EWGPS");
	var NSGPScanvas = document.getElementById("NSGPS");
	var ZGPScanvas = document.getElementById("ZGPS");
	var EWMEMScanvas = document.getElementById("EWMEMS");
	var NSMEMScanvas = document.getElementById("NSMEMS");
	var ZMEMScanvas = document.getElementById("ZMEMS");
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
	
}
//刷新折线图
function initChartsData(sid,PT){
	if(!sid){
		alert("!sid");
		return;
	}
	if(!PT){
		alert("!PT");
		return ;
	}
	$.ajax({
		type:"get",
		url:"servlet/GetHisDispWithMEMSByEQSt",
		data:{
			stationID:sid,
//			eqTime:eqTime.getFullYear()+"-"+(eqTime.getMonth()+1)+"-"+eqTime.getDate()+" "+eqTime.getHours()+":"+eqTime.getMinutes()+":"+eqTime.getSeconds()
			PT:PT
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
			var labels = mm[2];
			var gpsMags = mm[0]+"";
			var gpsMagData = gpsMags.split(",");
			var memsMags = mm[1]+"";
			var memsMagData = memsMags.split(",");
			magChartInSt.setOption({
				xAxis:  {
					data: labels,
					    },
					series: [
						{
						   data: gpsMagData,
						},
						{
							data: memsMagData,
						}
					    ]
					});
				}
			});
}
