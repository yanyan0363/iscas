var map, mapCenter,EQLayer,baseLayer; 

var EQLyrDefinitions = [];//EQLayer的过滤条件
var lyrsMap={};
var eqPolygons = null;//存储当前页面的地震区域数据
var stationLayer=0;
require(["esri/map", "amap/AMapTiledMapServiceLayer"], function(Map, AMapTiledMapServiceLayer)  
{  
	 map = new Map("map", {logo:false,slider: true});  
	 baseLayer = new AMapTiledMapServiceLayer();
	 map.addLayer(baseLayer, 0);
	 map.setScale(5000000);
//	    initMapCenter(103.2,29.2);//四川
//	 initMapCenter(139.5,38);//日本
//	 map.centerAt(mapCenter);
//	 parent.initMapCenter(map,parent.mapCLon,parent.mapCLat);
	 parent.drawStations(map);
	 initEQ();
});

$('#stMsTbl').bootstrapTable({
	silent:true,
	height:420,
	rowStyle:function rowStyle(row, index){
		return {
			css: {"font-size":"12px"}
		};
	},
	columns:[
		{
			field:'station',
			title:'台站<br>&nbsp;',
		},
		{
			field:'M',
			title:'GPS震级<br>&nbsp;',
		},
		{
			field:'maxHDis',
			title:'水平最大<br>位移(m)',
		},
		{
			field:'maxVDis',
			title:'垂直最大<br>位移(m)',
		},
		]
});
$('#EQStTbl').bootstrapTable({
	height:620,
	striped:true,
	pagination:false,
	sortable:false,
	showFooter:false,
	method:'get',
    url:'servlet/GetEQStPTMEpiDisServlet',
    queryParams:'eqID='+eqID,
	rowStyle:function rowStyle(row, index){
		return {
			css: {"font-size":"12px"}
		};
	},
	columns:[
	    {
	    	field:'station',
	    	title:'台站',
	    },
	    {
	    	field:'PT',
	    	title:'P波时刻',
	    },
	    {
	    	field:'gpsMag',
	    	title:'GPS震级',
	    },
	    {
	    	field:'memsMag',
	    	title:'MEMS震级',
	    },
	    {
	    	field:'EpiDis',
	    	title:'震中距离(km)'
	    }
	    ],
	onClickRow:function (row,tr){
		reportStationClick(row.station);
	}
});
var rps = document.getElementById("rps");
var stMsNum = document.getElementById("stMsNum");
var msgRecord;

function initEQ(){
	initEQAR();
	initMagChart();
}

function initEQAR(){
	$.ajax({
		type:"get",
		url:"servlet/GetEQIdx",
		data:{
			eqID:eqID
		},
		dataType:'json',
		success:function(msg){
			if(msg==null || msg=="[]" || msg.length==0){
				return;
			}
			msgRecord = msg;
			//刷新地震基本信息
			if(msg.hasOwnProperty("EQInfo")){
				//IndexTab
    			$("#oTimeInIdx").text(msg.EQInfo.eqTime);
    			$("#epiInIdx").text(msg.EQInfo.lon+"E,  "+msg.EQInfo.lat+"N");
    			$("#gpsMagInIdx").text(msg.EQInfo.gpsMag);
    			$("#memsMagInIdx").text(msg.EQInfo.memsMag);
    			$("#fStInIdx").text(msg.EQInfo.firstSt);
    			initEQPoint(msg.EQInfo.lon, msg.EQInfo.lat);
    			centerAtEpi(map);
			}
			//刷新速报信息
    		rps.innerHTML="";
    		if(msg.hasOwnProperty("EQARs")){
    			for(var i=0; i<msg.EQARs.length; i++){
    				var rp = document.createElement("div");
    				rp.className ="rpDiv";
    				var rpLeft = document.createElement("div");
    				rpLeft.className ="floatLeft";
    				var rpLeftLine = document.createElement("div");
    				rpLeftLine.className ="rpLeftLine";
    				rpLeft.appendChild(rpLeftLine);
    				var floatLeft = document.createElement("div");
    				floatLeft.className ="floatLeft mtop5";
    				rpLeft.appendChild(floatLeft);
    				rp.appendChild(rpLeft);
    				var rpRight = document.createElement("div");
    				rpRight.className ="floatLeft mtop5";
    				rpRight.onclick=function(e){
    					var nn = this.firstChild.childNodes[1].innerHTML;
    					stMsNum.innerHTML="第"+nn+"报";
    					$("#stMsTbl").bootstrapTable('load', msgRecord.EQARs[nn-1].stGPSMags);
    					if(nn==1 || nn==2){
    						clearEQPolygonInMap();
    						return;
    					}
    					drawStPOlygon(msgRecord.EQARs[nn-1].station);//参数修改为台站名称
    				};
    				var btR = document.createElement("div");
    				btR.className ="btR";
    				btR.innerHTML="第"+"<span>"+(i+1)+"</span>报";
    				rpRight.appendChild(btR);
    				var rpDiv = document.createElement("div");
    				rpDiv.className="glines rpB";
    				var stL = document.createElement("div");
    				stL.className = "rpLine";
    				stL.innerHTML="<span class='tBold'>台站：</span>"+msg.EQARs[i].station;
    				rpDiv.appendChild(stL);
    				var epiP = document.createElement("div");
    				epiP.className = "rpLine";
    				epiP.innerHTML="<span class='tBold'>震中：</span>"+msg.EQARs[i].EpiLon+"E "+ msg.EQARs[i].EpiLat + "N";
    				rpDiv.appendChild(epiP);
    				var PTP = document.createElement("div");
    				PTP.className = "rpLine";
    				PTP.innerHTML="<span class='tBold'>时间：</span>"+msg.EQARs[i].PT;
    				rpDiv.appendChild(PTP);
    				var Mgs = document.createElement("div");
    				Mgs.className = "rpLine";
    				Mgs.innerHTML="<span class='tBold'>GPS震级：</span>"+msg.EQARs[i].gpsMag + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='tBold'>MEMS震级：</span>"+msg.EQARs[i].memsMag;
    				rpDiv.appendChild(Mgs);
    				rpRight.appendChild(rpDiv);
    				rp.appendChild(rpRight);
    				rps.appendChild(rp);
    			}
    		}
	},
	error:function(XMLHttpRequest, textStatus, errorThrown){
	}
	});
}
var symbolP;
function drawStPOlygon(station){//参数修改为台站名称
	if(station==undefined){
		return;
	}
	require(["esri/geometry/Circle","esri/graphic","esri/units", "esri/symbols/SimpleFillSymbol", "esri/symbols/SimpleLineSymbol","esri/Color"], 
			function(Circle,Graphic,Units, SimpleFillSymbol,SimpleLineSymbol,Color) {
		if(symbolP == undefined){
			symbolP = new SimpleFillSymbol(SimpleLineSymbol.STYLE_SOLID,new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID,
				    new Color([255,0,0]), 1),new Color([255,255,0,0.25]));
		}
		if(eqPoint==undefined){
			return;
		}
		var stPP = parent.stationsGeometryMap.get(station);
		var dx = stPP.x - eqPoint.x;
		var dy = stPP.y - eqPoint.y;
		var radP = Math.sqrt(dx*dx + dy*dy);
		if(radP > 0){
			var circleP = new Circle(eqPoint,{
				radius: radP,
				radiusUnit: Units.METERS,
				numberOfPoints:720
			});
			var attr = {"ID":eqID,"type":"EQPolygon"};
			var ppGraphic = new Graphic(circleP,symbolP,attr); 
			clearEQPolygonInMap();
			map.graphics.add(ppGraphic);
		}
	});
}
function clearEQPolygonInMap(){
	var graphics = map.graphics.graphics;
	for(var j=0;j<graphics.length;j++){
		if(graphics[j].attributes!=undefined && graphics[j].attributes.type=="EQPolygon"){
			map.graphics.remove(graphics[j]);
			break;
		}
	}
}
//function initEQPoint(lon, lat){
//	require(["esri/geometry/Point", "esri/SpatialReference", "esri/geometry/webMercatorUtils"], function(Point, SpatialReference, webMercatorUtils) {
//		 var point = new esri.geometry.Point(parseFloat(lon),parseFloat(lat),new SpatialReference({wkid:parseInt(4326)}));//这里面需要定义输入点的坐标系，不写默认会是4326
//		 var geom = webMercatorUtils.geographicToWebMercator(point);
//		 eqPoint = new esri.geometry.Point(geom.x,geom.y, map.spatialReference);
//		 var symbol = new esri.symbol.PictureMarkerSymbol("img/ep.png", 32, 32);
//		 var attr = {"ID":eqID,"type":"EQPoint"};
//		 epiGraphic = new esri.Graphic(eqPoint, symbol, attr);
//		 map.graphics.add(epiGraphic);
//		 map.centerAt(eqPoint);
//	});
//}
function initMagChart(){
	$.ajax({
		type:"post",
		url:"servlet/GetMagByEQID",
		data:{
			eqID:eqID
		},
		datatype:'json',
		success:function(msg){
			var mm = eval(msg);
			var labels = mm[2];
			var gpsMags = mm[0]+"";
			var gpsData = gpsMags.split(",");
			var memsMags = mm[1]+"";
			var memsData = memsMags.split(",");
			cchart.setOption({
				xAxis:  {
					data: labels,
				},
				series: [
					{
						data: gpsData,
					},
					{
						data: memsData,
					}
				]
			});
		}
	});
}

function reportStationClick(stationID){
	window.open("MainForEQStInfo.html?EQID="+eqID+"&stationID="+stationID);
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
	legend: {
        data:[
        	{name:'GPS震级'},
        	{name:'MEMS震级'}
        ]
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

//function idxMapCenterAtEpi(){
////	alert(curMap.graphics.loaded);
//	if(map != undefined && map.loaded && map.graphics != undefined && map.graphics.loaded && epiGraphic != undefined){
//		map.centerAt(eqPoint);
//		map.graphics.add(epiGraphic);
//		map.graphics.redraw();
//	}else{
//		alert("!loaded or warnMap.graphics == undefined or !warnMap.graphics.loaded or epiGraphic == undefined");
//		setTimeout(function(){idxMapCenterAtEpi();}, 1000);
//		return;
//	}
//}