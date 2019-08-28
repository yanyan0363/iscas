//var inSt = ["51JLD","51GYZ","51GYS","51DXY","51CXQ","51BXY","51JZW","51JZG","51JZB","51JYC"];
//var overSt = ["51LSF","51YAL","51YBY","51JLD"];
//var outSt = ["51JZZ","51JZY","51JZW","51GYS","51CXQ","51MXD","51JYC","aa"];
var inSt, overSt, outSt=[];
var greySymbol;
var greenSymbol;
var redSymbol;
//var stationLayer=0;转至main.js设定
var totalNum=0;//台站数量
var inlineNum=0;//在线台站数量
var overNum=0;//异常台站数量
require(["esri/symbols/PictureMarkerSymbol"], function(PictureMarkerSymbol)  
{
	greySymbol = new PictureMarkerSymbol("img/trip-32-grey.png", 16, 16);
	greenSymbol = new PictureMarkerSymbol("img/trip-32-blue.png", 16, 16);
	redSymbol = new PictureMarkerSymbol("img/trip-32-orange.png", 16, 16);
	epiSymbol = new PictureMarkerSymbol("img/bling.gif", 16, 16);
}); 

var curStation;
var stationArray = new Array();
var map, mapCenter,baseLayer;  
require(["esri/map","esri/layers/ArcGISDynamicMapServiceLayer","esri/geometry/Point"], function(Map,ArcGISDynamicMapServiceLayer,Point)  
{  
    map = new Map("map", {logo:false,slider: true});  
//    baseLayer = new esri.layers.ArcGISDynamicMapServiceLayer("http://"+parent.serverIP+":6080/arcgis/rest/services/GNSSBase3_0/MapServer");
    baseLayer = new esri.layers.ArcGISDynamicMapServiceLayer(parent.baseUrl);
    baseLayer.setVisibleLayers([0,2]);//[2,10,11]
    map.addLayer(baseLayer, 0);
	mapCenter = new esri.geometry.Point(141,38);//(104,30.5);//四川
    map.centerAt(mapCenter);
    map.setScale(2500000);
    map.on("load", function(){
	    map.graphics.on("click", function(event){
	    	if(event.graphic.attributes.type=="EQPoint"){
	    		map.infoWindow.hide();
//	    		alert("this is an EQPoint::" + event.graphic.attributes.ID);
	    		curStation = undefined;
	    		location.href = "./EQInfo.html?EQID="+event.graphic.attributes.ID;
	    	}else{
	    		curStation = event.graphic.attributes.ID;//表示台站ID
	    		showLineCharts(curStation,event.mapPoint);
	    	}
	    });
	    drawStations();
    });
}); 
function searchStationGraphicByID(stationID){
	var graphics = map.graphics.graphics;
	for(var i=0;i<graphics.length;i++){
		if(stationID == graphics[i].attributes.ID){
			return graphics[i];
		}
	}
}
function showLineCharts(stationID,showPoint){
	map.infoWindow.hide();
	curStation = stationID;
	if(showPoint == undefined){
		var station = searchStationGraphicByID(stationID);
		if(station == undefined){
			alert("未能获取相应台站位置信息");
			return;
		}
		showPoint = station.geometry;
	}
	var lineChartDiv = document.createElement("div");
	lineChartDiv.id = "lineChartDiv";
	lineChartDiv.style = "width:630 ";
	var labelX = document.createElement("label");
	labelX.innerHTML = "东西向位移";
	lineChartDiv.appendChild(labelX);
	var canvasX = document.createElement("div");
	canvasX.id = "canvasX";
	//canvasX.width = 600;
	//canvasX.height = 150;
	lineChartDiv.appendChild(canvasX);
	var labelY = document.createElement("label");
	labelY.innerHTML = "南北向位移";
	lineChartDiv.appendChild(labelY);
	var canvasY = document.createElement("div");
	canvasY.id = "canvasY";
	canvasY.width = 600;
	canvasY.height = 150;
	lineChartDiv.appendChild(canvasY);
	var labelZ = document.createElement("label");
	labelZ.innerHTML = "地心向位移";
	lineChartDiv.appendChild(labelZ);
	var canvasZ = document.createElement("div");
	canvasZ.id = "canvasZ";
	canvasZ.width = 600;
	canvasZ.height = 150;
	lineChartDiv.appendChild(canvasZ);
	var moreDiv = document.createElement("div");
	moreDiv.style = "text-align:right;";
	var moreA = document.createElement("a");
	moreA.innerHTML = "查看更多";
	moreA.setAttribute("href", "StationDetail.html?SID="+stationID);
	moreDiv.appendChild(moreA);
	lineChartDiv.appendChild(moreDiv);
	/*
	 * 初始化台站位移数据
	 */
	$.ajax({
		type:"get",
//		url:"http://127.0.0.1:8090/GNSS/stationDisplacement",
		url:"http://"+parent.serverIP+":8090/GNSS/stLast2MinDisp",
		data:{
			station:stationID
		},
		dataType:'jsonp',
		jsonp:'callback',
		timeout:1000,
		success:function(msg){
			if(msg == undefined){
				curStation = undefined;
				alert("暂无数据");
				return;
			}
			map.infoWindow.setContent(lineChartDiv);
			map.infoWindow.setTitle(stationID);
			map.infoWindow.resize(640,800);
			map.infoWindow.show(showPoint);
			map.centerAt(showPoint);
			showFloatCharts(msg,canvasX,canvasY,canvasZ);
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
//			alert("出错了，请联系管理员");
		}
	});
}
function showFloatCharts(msg,canvasX,canvasY,canvasZ){
	// 指定图表的配置项和数据
    var option = setOpt(msg.EWGPS.time,msg.EWGPS.dis);
    XChart = echarts.init(canvasX);
    XChart.setOption(option);
    
    option = setOpt(msg.NSGPS.time,msg.NSGPS.dis);
    YChart = echarts.init(canvasY);
    YChart.setOption(option);
    
    option = setOpt(msg.ZGPS.time,msg.ZGPS.dis);
    ZChart = echarts.init(canvasZ);
    ZChart.setOption(option);
}
function setOpt(label,data){
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
    	    	type: 'category',
    	        data: label,
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
    	            name:'位移',
    	            type:'line',
    	            data:data,
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
function drawStations(){
	//实例化查询参数
	var query = new esri.tasks.Query();
	query.outFields = ["StationID"];
	query.outSpatialReference = map.spatialReference;
	query.spatialRelationship = esri.tasks.Query.SPATIAL_REL_INTERSECTS;
	query.returnGeometry = true;
	query.where = "1=1";
	//实例化查询对象
//	var queryTask = new esri.tasks.QueryTask("http://"+parent.serverIP+":6080/arcgis/rest/services/GNSSBase3_0/MapServer/" + stationLayer);
	var queryTask = new esri.tasks.QueryTask(parent.baseUrl + "/" +parent.stationLayer);
	//进行查询
	queryTask.execute(query, showStationsResult);
}
function showStationsResult(result){
	if(result.features == 0){
		return;
	}
	var stationsList = document.getElementById("stationsList");
	stationsList.innerHTML="";
	map.graphics.clear();
	totalNum = result.features.length;
	for(var i = 0; i < totalNum; i++){
		//获得该图形的形状
        var feature = result.features[i];
        var attr = feature.attributes;
        //创建客户端图形
        var divEle = document.createElement("div");
        divEle.id = attr.StationID+"InList";
        createStationInList(divEle, "grey", attr.StationID);
        stationsList.appendChild(divEle);
		//获得该图形的形状
        var geometry = feature.geometry;
        //创建客户端图形
        var infoGraphic = new esri.Graphic(geometry, greySymbol);
        infoGraphic.setAttributes({"ID":attr.StationID});
        //将客户端图形添加到map中
        map.graphics.add(infoGraphic);
	}
	map.graphics.redraw();
}
//ele为DOM节点，type为类型，包括grey，blue，orange，stationID为台站ID
function createStationInList(ele, type, stationID){
	if(type == "grey"){
		ele.innerHTML = "<div onclick=\"showLineCharts('"+stationID+"');\">&nbsp;&nbsp;<img src=\"img/trip-32-grey.png\" height=\"14\" width=\"14\">&nbsp;&nbsp;&nbsp;&nbsp;<span>"+stationID+"(离线)</span></div>";
	}else if(type == "blue"){
		ele.innerHTML = "<div onclick=\"showLineCharts('"+stationID+"');\">&nbsp;&nbsp;<img src=\"img/trip-32-blue.png\" height=\"14\" width=\"14\">&nbsp;&nbsp;&nbsp;&nbsp;<span>"+stationID+"(在线)</span></div>";
	}else if(type == "orange"){
		ele.innerHTML = "<div onclick=\"showLineCharts('"+stationID+"');\">&nbsp;&nbsp;<img src=\"img/trip-32-orange.png\" height=\"14\" width=\"14\">&nbsp;&nbsp;&nbsp;&nbsp;<span>"+stationID+"(异常)</span></div>";
	}
}

function refreshStations(){
//	var graphics = map.graphics.graphics;
//	totalNum = map.graphics.graphics.length;
	$.ajax({
		type:"get",
		url:"http://"+parent.serverIP+":8090/GNSS/getCurStationFamily",
		dataType:'jsonp',//异步
		jsonp:'callback',
		success:function(msg){
			inSt = msg.inSt;
			overSt = msg.overSt;
			overNum = overSt.length;
			outSt = [];
			inlineNum = inSt.length + overNum;
			for(var j=0;j<map.graphics.graphics.length;j++){
				if(map.graphics.graphics[j].attributes!=undefined 
						&& (map.graphics.graphics[j].attributes.type=="EQPoint"||map.graphics.graphics[j].attributes.type=="EQPolygon")){
					continue;
				}
				var flag = false;
				for(var i=0;i<inSt.length;i++){
					if(inSt[i] == map.graphics.graphics[j].attributes.ID){
						map.graphics.graphics[j].setSymbol(greenSymbol);
						flag = true;
						var divEle = document.getElementById(inSt[i]+"InList");
						createStationInList(divEle, "blue", inSt[i]);
						break;
					}
				}
				if(flag){
					continue;
				}
				for(var k=0;k<overNum;k++){
					if(map.graphics.graphics[j].attributes!=undefined && overSt[k] == map.graphics.graphics[j].attributes.ID){
						map.graphics.graphics[j].setSymbol(redSymbol);
						flag = true;
						var divEle = document.getElementById(overSt[k]+"InList");
						createStationInList(divEle, "orange", overSt[k]);
						break;
					}
				}
				if(flag){
					continue;
				}else{
					outSt.push(map.graphics.graphics[j].attributes.ID);
					map.graphics.graphics[j].setSymbol(greySymbol);
					var divEle = document.getElementById(map.graphics.graphics[j].attributes.ID+"InList");
					createStationInList(divEle, "grey", map.graphics.graphics[j].attributes.ID);
				}
				
			}
			map.graphics.redraw();
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			//alert(XMLHttpRequest.status+"\n"+XMLHttpRequest.statusText+"\n"+XMLHttpRequest.responseText +"\n"+XMLHttpRequest.readyState+"\n"+textStatus+"\n"+errorThrown);
//			alert("获取告警台站列表出错了，请联系管理员");
		}
	});
}
function refreshListStationsInInfo(){
	if(infoListTitle.innerHTML == "在线台站"){
		listStationsInInfo.innerHTML="";
		for(var i = 0; i < inSt.length; i++){
			var divEle = document.createElement("div");
			createStationInList(divEle, "blue", inSt[i]);
			listStationsInInfo.appendChild(divEle);
		}
		for(var i = 0; i < overSt.length; i++){
			var divEle = document.createElement("div");
			createStationInList(divEle, "orange", overSt[i]);
			listStationsInInfo.appendChild(divEle);
		}
	}else if(infoListTitle.innerHTML == "掉线台站"){
		listStationsInInfo.innerHTML="";
		for(var i = 0; i < outSt.length; i++){
			var divEle = document.createElement("div");
			createStationInList(divEle, "grey", outSt[i]);
			listStationsInInfo.appendChild(divEle);
		}
	}else if(infoListTitle.innerHTML == "异常台站"){
		listStationsInInfo.innerHTML="";
		for(var i = 0; i < overSt.length; i++){
			var divEle = document.createElement("div");
			createStationInList(divEle, "orange", overSt[i]);
			listStationsInInfo.appendChild(divEle);
		}
	}
}
var infoListTitle = document.getElementById("infoListTitle");
var listStationsInInfoD = document.getElementById("listStationsInInfo");
function listStationsInInfoFunc(status){
	if(status == "inlineS"){
		infoListTitle.innerHTML="在线台站";
		listStationsInInfo.innerHTML="";
		for(var i = 0; i < inSt.length; i++){
			var divEle = document.createElement("div");
			createStationInList(divEle, "blue", inSt[i]);
			listStationsInInfo.appendChild(divEle);
		}
		for(var i = 0; i < overSt.length; i++){
			var divEle = document.createElement("div");
			createStationInList(divEle, "orange", overSt[i]);
			listStationsInInfo.appendChild(divEle);
		}
	}else if(status == "outlineS"){
		infoListTitle.innerHTML="掉线台站";
		listStationsInInfo.innerHTML="";
		for(var i = 0; i < outSt.length; i++){
			var divEle = document.createElement("div");
			createStationInList(divEle, "grey", outSt[i]);
			listStationsInInfo.appendChild(divEle);
		}
	}else if(status == "abnormalS"){
		infoListTitle.innerHTML="异常台站";
		listStationsInInfo.innerHTML="";
		for(var i = 0; i < overSt.length; i++){
			var divEle = document.createElement("div");
			createStationInList(divEle, "orange", overSt[i]);
			listStationsInInfo.appendChild(divEle);
		}
	}
	listStationsInInfoD.style.display="block";
}
var clientHeight = parent.getClientHeight();
var tableHeight = clientHeight-165;
var chartsHeight = (clientHeight-100)*0.3-25;
$("#tableDiv").outerHeight(tableHeight);
$("#chartsDiv").outerHeight(chartsHeight);
var XChart,YChart,ZChart;

//刷新折线图
function refreshCharts(){
	if(!curStation){
		//alert("!curStation");
		return;
	}
	$.ajax({
		type:"get",
		url:"http://"+parent.serverIP+":8090/GNSS/stLast2MinDisp",
		data:{
			station:curStation
		},
		dataType:'jsonp',
		jsonp:'callback',
		success:function(msg){
			if(msg == undefined){
				//XChart refresh
			    option = XChart.getOption();
			    option.series[0].data='';
			    option.xAxis[0].data='';
			    XChart.setOption(option);
			    //YChart refresh
			    option = YChart.getOption();
			    option.series[0].data='';
			    option.xAxis[0].data='';
			    YChart.setOption(option);
			    YChart.refresh;
			    //ZChart refresh
			    option = ZChart.getOption();
			    option.series[0].data='';
			    option.xAxis[0].data='';
			    ZChart.setOption(option);
				return;
			}
		    //XChart refresh
		    option = XChart.getOption();
		    option.series[0].data=msg.EWGPS.dis;
		    option.xAxis[0].data=msg.EWGPS.time;
		    XChart.setOption(option);
		    //YChart refresh
		    option = YChart.getOption();
		    option.series[0].data=msg.NSGPS.dis;
		    option.xAxis[0].data=msg.NSGPS.time;
		    YChart.setOption(option);
		    YChart.refresh;
		    //ZChart refresh
		    option = ZChart.getOption();
		    option.series[0].data=msg.ZGPS.dis;
		    option.xAxis[0].data=msg.ZGPS.time;
		    ZChart.setOption(option);
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			//alert(XMLHttpRequest.status+"\n"+XMLHttpRequest.statusText+"\n"+XMLHttpRequest.responseText +"\n"+XMLHttpRequest.readyState+"\n"+textStatus+"\n"+errorThrown);
//			alert("图表刷新出错了，请联系管理员");
		}
	});
}
function refreshNums(){
	document.getElementById("totalNum").innerHTML = totalNum;
	document.getElementById("outlineNum").innerHTML = totalNum - inlineNum;
	document.getElementById("inlineNum").innerHTML = inlineNum;
	document.getElementById("overNum").innerHTML = overNum;
}
function handleStationResult(queryResult){
	if(queryResult.features == 0){
		//alert("没有该元素");
		return;
	}
	if(queryResult.features.length > 0){
		//获得该图形的形状
        var feature = queryResult.features[0];
        var showPoint = feature.geometry;
        var attr = feature.attributes;
		//alert(i+":"+attr.StationID);
		//加载台站的位移曲线图
		showLineCharts(attr.StationID,showPoint);
	}
}
setInterval(function(){
	refreshCharts();
	refreshStations();
	refreshNums();
	refreshListStationsInInfo();
	monitorEQ();
  },1000);  
