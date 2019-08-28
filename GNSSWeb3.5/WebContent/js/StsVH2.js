var map;
require(["esri/map"], function(Map)  
{  
    map = new Map("map", {logo:false,slider: true}); 
//    var baseLyrUrl = "http://"+parent.serverIP+":6080/arcgis/rest/services/GNSSBase3_0/MapServer";
    var baseLyrUrl = parent.baseUrl;
    var baseLayer = new esri.layers.ArcGISDynamicMapServiceLayer(baseLyrUrl);
    baseLayer.setVisibleLayers([0,2,3]);//[0,2,5,10,11]
    map.addLayer(baseLayer, 0);
    map.setScale(5000000);
    map.on("load",function(){
    	reDrawStationsInitCharts();
    	refreshCharts();
    });
});
function refreshCharts(){
//	chartMap.forEach(function (item) {
//	     alert(item.toString());
//	});

	for(var key in chartMap){
		alert(""+key);
		alert(key.length+"\n"+key.substr(key.length-2));
		if(key.substr(key.length-2)=='HC'){
			alert('end with HC');
		}else{
			alert('end with VC');
			continue;
		}
	}
}
function stDispVH(stID){
	$.ajax({
		type:"get",
		url:"http://"+parent.serverIP+":8090/GNSS/stDispVH",
		dataType:'jsonp',
		jsonp:'callback',
		data:{
			stID:stID
		},
		success:function(msg){
			if(msg == undefined || msg.length == 0){
				clearChartData(stID);
				return;
			}
			refreshChart(msg);
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
		}
	});
}
function clearChartData(stID){
	var option = chartMap[stID+"HC"].getOption();
	option.series[0].data=null;
	chartMap[stID+"HC"].setOption(option);
	option = chartMap[stID+"VC"].getOption();
	option.series[0].data=null;
	chartMap[stID+"VC"].setOption(option);
}
//setInterval(function(){
//$.ajax({
//	type:"get",
//	url:"http://127.0.0.1:8090/GNSS/stsDisp2DZ",
//	dataType:'jsonp',
//	jsonp:'callback',
////	timeout:1000,
//	success:function(msg){
//		if(msg == undefined || msg.length == 0){
////			alert("暂无数据");
//			clearStCharts();
//			return;
//		}
//		refreshStationCharts(msg);
//	},
//	error:function(XMLHttpRequest, textStatus, errorThrown){
////		alert("刷新图表出错了，请联系管理员");
//	}
//});
//},2000);
var chartsDiv = $("#charts");
var chartMap={};//chartMap[stationID+"HC"] chartMap[stationID+"VC"] refreshChart(msg)
//initCharts();
function reDrawStationsInitCharts(){
	//实例化查询参数
	var query = new esri.tasks.Query();
	query.outFields = ["StationID"];
	query.outSpatialReference = map.spatialReference;
	query.spatialRelationship = esri.tasks.Query.SPATIAL_REL_INTERSECTS;
	query.returnGeometry = true;
	query.where = "1=1";
	//实例化查询对象
//	var queryTask = new esri.tasks.QueryTask("http://127.0.0.1:6080/arcgis/rest/services/GNSSBase3_0/MapServer/0");
	var queryTask = new esri.tasks.QueryTask(parent.baseUrl+"/0");
	//进行查询
	queryTask.execute(query, showStationsResult);
}
function showStationsResult(result){
	if(result.features == 0){
		return;
	}
	require(["esri/symbols/PictureMarkerSymbol"], function(PictureMarkerSymbol)  {
//		var greySymbol = new PictureMarkerSymbol("img/trip-32-grey.png", 16, 16);
		var greenSymbol = new PictureMarkerSymbol("img/trip-32-blue.png", 16, 16);
		for(var i = 0; i < result.features.length; i++){
			var feature = result.features[i];
			var attr = feature.attributes;
			//获得该图形的形状
			var geometry = feature.geometry;
			//创建客户端图形
			var infoGraphic = new esri.Graphic(geometry, greenSymbol);
			infoGraphic.setAttributes({"ID":attr.StationID});
			//将客户端图形添加到map中
			map.graphics.add(infoGraphic);
			createChart(attr.StationID);
		}
	}); 
}
//
//function initCharts(){
//	chartsDiv.empty();
//	$.ajax({
//		type:"get",
//		url:"http://127.0.0.1:8090/GNSS/stsDisp2DZ",
//		dataType:'jsonp',
//		jsonp:'callback',
//		//timeout:1000,
//		success:function(msg){
//			if(msg == undefined){
//				alert("暂无数据");
//				return;
//			}
//			showStationCharts(msg);
//		},
//		error:function(XMLHttpRequest, textStatus, errorThrown){
////			alert("出错了，请联系管理员");
//		}
//	});
//}
//function showStationCharts(msg){
//	for(var i=0;i<msg.length;i++){
//		createChart(msg[i]);
//	}
//}
function createChart(stationID){
//	var stationID = msg.ID;
	var newDiv = $("<div id="+stationID+" class=stDiv></div>");
	var curSt;
//	if(msg.value.disH.length == 0 && msg.value.time.length == 0 
//			&& msg.value.disV.length == 0){
//		curSt = $("<div class='curStation btSt'>台站：<span>"+stationID+"，暂无数据</span></div>");
//	}else{
//		curSt = $("<div class='curStation btSt'>台站：<span>"+stationID+"</span></div>");
//	}
	curSt = $("<div class='curStation btSt'>台站：<span>"+stationID+"</span></div>");
	newDiv.append(curSt);
	var charts = $("<div class='chartsDiv'></div>");
	var hChartDiv = $("<div id='"+stationID+"HCDiv' class='hChartDiv'>");
//	var spanH = $("<span class='floatRight'>水平位移</span>");
//	hChartDiv.append(spanH);
	var hCanvas=$("<div class='chartDiv' id='"+stationID+"HC'></div>");
	hChartDiv.append(hCanvas);
	charts.append(hChartDiv);
	var vChartDiv = $("<div id='"+stationID+"VCDiv' class='vChartDiv'>");
//	var spanV = $("<span class='floatRight'>垂直位移</span>");
//	vChartDiv.append(spanV);
	var vCanvas=$("<div class='chartDiv' id='"+stationID+"VC'></div>");
	vChartDiv.append(vCanvas);
	charts.append(vChartDiv);
	newDiv.append(charts);
	chartsDiv.append(newDiv);
	// 指定图表的配置项和数据
	var opt = setOpt();
	opt.yAxis[0].name="水平位移(m)";
	opt.title.text='水平位移';
	opt.series[0].name='水平位移';
	var horizonChart = echarts.init(document.getElementById(stationID+"HC"));
	horizonChart.setOption(opt);
	chartMap[stationID+"HC"] = horizonChart;
	
	opt = setOpt();
	opt.yAxis[0].name="垂直位移(m)";
	opt.series[0].name='垂直位移';
	var verticalChart = echarts.init(document.getElementById(stationID+"VC"));
	verticalChart.setOption(opt);
	chartMap[stationID+"VC"] = verticalChart;
}
function showChart(msg){
	// 指定图表的配置项和数据
	var horizonChart = chartMap[msg.ID+"HC"];
	var opt = horizonChart.getOption();
	opt.series[0].data=msg.value.disH;
	opt.xAxis[0].data=msg.value.time;
	horizonChart.setOption(opt);
	
	var verticalChart = chartMap[msg.ID+"VC"];
	opt = verticalChart.getOption();
	opt.series[0].data=msg.value.disV;
	opt.xAxis[0].data=msg.value.time;
	verticalChart.setOption(opt);
}
//setInterval(function(){
//	$.ajax({
//		type:"get",
//		url:"http://127.0.0.1:8090/GNSS/stsDisp2DZ",
//		dataType:'jsonp',
//		jsonp:'callback',
////		timeout:1000,
//		success:function(msg){
//			if(msg == undefined || msg.length == 0){
////				alert("暂无数据");
//				clearStCharts();
//				return;
//			}
//			refreshStationCharts(msg);
//		},
//		error:function(XMLHttpRequest, textStatus, errorThrown){
////			alert("刷新图表出错了，请联系管理员");
//		}
//	});
//},2000);
function clearStCharts(){
	for(var key in chartMap){
		var option = chartMap[key].getOption();
		option.series[0].data=null;
		chartMap[key].setOption(option);
	}
}
function refreshStationCharts(msg){
	for(var i=0;i<msg.length;i++){
		refreshChart(msg[i]);
	}
}
function refreshChart(msg){
	if(chartMap[msg.ID+"HC"]==undefined){
		createChart(msg.ID);
	}
	// 指定图表的配置项和数据
	var option = chartMap[msg.ID+"HC"].getOption();
	option.series[0].data=msg.value.disH;
    option.xAxis[0].data=msg.value.time;
    chartMap[msg.ID+"HC"].setOption(option);
	
    option = chartMap[msg.ID+"VC"].getOption();
	option.series[0].data=msg.value.disV;
    option.xAxis[0].data=msg.value.time;
    chartMap[msg.ID+"VC"].setOption(option);
}
function setOpt(){
	var option = {
			title: {
				show:false,
				text:'位移',
				textAlign:'right',
				right:0,
				top:0
			},
			grid: {
				show: true,
				top: 20,
				bottom: 30,
				left:50,
				right: 50
			},
			tooltip: {
				trigger: 'axis'
			},
			xAxis:  [{
				name: '时间',
				nameLocation: 'end',
				nameGap: 10,
				nameTextStyle: {
					fontSize:10
				},
				axisLabel:{
					textStyle:{
						fontSize:10
					},
					margin:10
				},
				boundaryGap: false,
				type: 'category',
				data: [],//msg.value.dis2D.time

			}],
			yAxis: [{
				type: 'value',
				name: '位移(m)',
				nameLocation: 'end',
				nameGap: 3,
				nameTextStyle: {
					fontSize:10
				},
				axisLabel: {
					formatter: '{value}',
				},
				min:-10,
				max:10
//				minInterval: 0.1,
//				scale:true //是否是脱离 0 值比例。设置成 true 后坐标刻度不会强制包含零刻度。
			}],
			series: [
				{
					name:'位移',
					type:'line',
					data:[],//msg.value.dis2D.dis
					markPoint: {
						symbolSize: 30,
//	            	data: [
//	                    {type: 'max', name: '最大值'},
//	                    {type: 'min', name: '最小值'}
//	                ]
					}
				}
				]
	};
	return option;
}



//function initCharts(sid){
//	/*
//	 * 初始化台站位移数据
//	 */
//	var EWGPScanvas = document.getElementById("EWGPS");
//	var NSGPScanvas = document.getElementById("NSGPS");
//	EWGPSChart = echarts.init(EWGPScanvas);
//	NSGPSChart = echarts.init(NSGPScanvas);
////	option = setOpt2();
////	EWGPSChart.setOption(option);
////	option = setOpt2();
////	NSGPSChart.setOption(option);
//}
//initCharts("SMMNX");
//initChartsData("SMMNX");

//刷新折线图
//function initChartsData(sid){
//	if(!sid){
//		alert("!sid");
//		return;
//	}
//	/*
//	 * 初始化台站位移数据
//	 */
//	$.ajax({
//		type:"get",
//		url:"http://127.0.0.1:8090/GNSS/stsDisp",
//		data:{
//			station:sid
//		},
//		dataType:'jsonp',
//		jsonp:'callback',
//		timeout:1000,
//		success:function(msg){
//			if(msg == undefined){
//				option = EWGPSChart.getOption();
//			    EWGPSChart.setOption(null);
//			    
//			    option = NSGPSChart.getOption();
//			    NSGPSChart.setOption(null);
//			    
//			    return;
//			}
//			// 指定图表的配置项和数据
//		    var option = setOpt(msg.z.time,msg.z.dis);
//		    EWGPSChart.setOption(option);
//		    
//		    option = setOpt(msg.dis2D.time,msg.dis2D.dis);
//		    NSGPSChart.setOption(option);
//		},
//		error:function(XMLHttpRequest, textStatus, errorThrown){
////			alert("出错了，请联系管理员");
//		}
//	});
//}
