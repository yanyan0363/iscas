var map;
var refreshTime = 2000;
var chartsDiv = $("#charts");
var chartMap= new Map();//{};//chartMap[stationID+"HC"] chartMap[stationID+"VC"] refreshChart(msg)
require(["esri/map","amap/AMapTiledMapServiceLayer","esri/geometry/Point", "esri/SpatialReference"], 
		function(Map, AMapTiledMapServiceLayer, Point, SpatialReference)  
{  
    map = new Map("map", {logo:false,slider: true});  
    var baseLayer = new AMapTiledMapServiceLayer();
	map.addLayer(baseLayer, 0);
	map.setScale(5000000);
	parent.drawStations(map);
	parent.initMapCenter(map,parent.mapCLon,parent.mapCLat);
	reDrawStationsInitCharts();
});
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
				setTimeout(function (){stDispVH(stID);},refreshTime);
				return;
			}
			refreshChart(msg);
			setTimeout(function (){stDispVH(stID);},refreshTime);
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			setTimeout(stDispVH(stID),refreshTime);
		}
	});
}
function clearChartData(stID){
	var option = chartMap.get(stID+"HC").getOption();
	option.series[0].data=null;
	chartMap.get(stID+"HC").setOption(option);
	option = chartMap.get(stID+"VC").getOption();
	option.series[0].data=null;
	chartMap.get(stID+"VC").setOption(option);
}
function reDrawStationsInitCharts(){
	require(["esri/symbols/PictureMarkerSymbol"], function(PictureMarkerSymbol)  {
		var greenSymbol = new PictureMarkerSymbol("img/trip-32-blue.png", 16, 16);
		for(var i = 0; i < map.graphics.graphics.length; i++){
			var graphic = map.graphics.graphics[i];
			var attr = graphic.attributes;
			if(attr ==undefined || attr.type != "Station"){
				continue;
			}else{
				graphic.setSymbol(greenSymbol);
				createChart(attr.ID);
				stDispVH(attr.ID);
			}
		}
	}); 
}

function createChart(stationID){
//	var stationID = msg.ID;
	var newDiv = $("<div id="+stationID+" class=stDiv></div>");
	var curSt;
	curSt = $("<div class='curStation btSt'>台站：<span>"+stationID+"</span></div>");
	newDiv.append(curSt);
	var charts = $("<div class='chartsDiv'></div>");
	var hChartDiv = $("<div id='"+stationID+"HCDiv' class='hChartDiv'>");
	var hCanvas=$("<div class='chartDiv' id='"+stationID+"HC'></div>");
	hChartDiv.append(hCanvas);
	charts.append(hChartDiv);
	var vChartDiv = $("<div id='"+stationID+"VCDiv' class='vChartDiv'>");
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
//	chartMap[stationID+"HC"] = horizonChart;
	chartMap.set(stationID+"HC",horizonChart);//[stationID+"HC"] = horizonChart;
//	alert(stationID+"HC");
	opt = setOpt();
	opt.yAxis[0].name="垂直位移(m)";
	opt.series[0].name='垂直位移';
	var verticalChart = echarts.init(document.getElementById(stationID+"VC"));
	verticalChart.setOption(opt);
//	chartMap[stationID+"VC"] = verticalChart;
	chartMap.set(stationID+"VC", verticalChart);//[] = verticalChart;
}
function showChart(msg){
	// 指定图表的配置项和数据
	var horizonChart = chartMap.get(msg.ID+"HC");
	var opt = horizonChart.getOption();
	opt.series[0].data=msg.value.disH;
	opt.xAxis[0].data=msg.value.time;
	horizonChart.setOption(opt);
	
	var verticalChart = chartMap.get(msg.ID+"VC");
	opt = verticalChart.getOption();
	opt.series[0].data=msg.value.disV;
	opt.xAxis[0].data=msg.value.time;
	verticalChart.setOption(opt);
}
function clearStCharts(){
	for(var key in chartMap){
		var option = chartMap.get(key).getOption();
		option.series[0].data=null;
		chartMap.get(key).setOption(option);
	}
}
function refreshStationCharts(msg){
	for(var i=0;i<msg.length;i++){
		refreshChart(msg[i]);
	}
}
function refreshChart(msg){
	if(chartMap.get(msg.ID+"HC")==undefined){
		createChart(msg.ID);
	}
	// 指定图表的配置项和数据
	var option = chartMap.get(msg.ID+"HC").getOption();
	option.series[0].data=msg.value.disH;
    option.xAxis[0].data=msg.value.time;
    chartMap.get(msg.ID+"HC").setOption(option);
	
    option = chartMap.get(msg.ID+"VC").getOption();
	option.series[0].data=msg.value.disV;
    option.xAxis[0].data=msg.value.time;
    chartMap.get(msg.ID+"VC").setOption(option);
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
