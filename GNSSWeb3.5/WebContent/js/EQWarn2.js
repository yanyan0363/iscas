var warnMap, warnMapCenter,warnEQLayer,warnBaseLayer;  
var warnClickPoint;
var stationLayer=0;
var warnEQLyrDef = [];
var graphicLayer;
require(["esri/map"], function(Map) {  
	warnMap = new Map("warnMap", {logo:false,slider: true});  
	warnBaseLayer = new esri.layers.ArcGISDynamicMapServiceLayer(baseLyrUrl);
	warnBaseLayer.setVisibleLayers([0,2,3]);//[0,2,5,10,11]
	warnMap.addLayer(warnBaseLayer, 0);
	warnMap.setScale(5000000);
	warnMap.on("load", function(){
		graphicLayer = warnMap.graphics;
		graphicLayer.clear();
	});
	initWarnEQLayer();
	initWarnStationsTable();
	initWarnReports();
	initSymbols();
});
function initWarnEQLayer(){
	warnEQLayer = new esri.layers.ArcGISDynamicMapServiceLayer(eqLyrUrl);
	warnEQLayer.setOpacity(0.5);
	warnEQLayer.setVisibleLayers([]);
    warnMap.addLayer(warnEQLayer, 1);
    initWarnVisibleEQLayers(1);
}
function initWarnVisibleEQLayers(reportNum){
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
				warnEQLayer.setVisibleLayers([EQPointIdx]);
				warnEQLyrDef[lyrsMap["EQPoints"]] = "num = "+reportNum+" and type='epicenter'";
				warnEQLyrDef[lyrsMap["EQPolygons"]] = "num = "+reportNum;
				warnEQLayer.setLayerDefinitions(warnEQLyrDef);
				warnEQLayer.refresh();
			}
			//初始化mapCenter
			initMapCenterPoint(initWarnCenterPoint);
		});
	}else{
		warnEQLayer.setVisibleLayers(visibleEQLyrs);
		EQLyrDefinitions[lyrsMap["EQPoints"]] = " type='epicenter'";
		EQLyrDefinitions[lyrsMap["EQPolygons"]] = "num = " + reportNum;
		warnEQLayer.setLayerDefinitions(EQLyrDefinitions);
		warnEQLayer.refresh();
		//初始化mapCenter
		initWarnCenter();
	}
}
function initWarnCenter(){
	//实例化查询参数
	var query = new esri.tasks.Query();
	query.outFields = ["num"];
	query.outSpatialReference = map.spatialReference;
	query.spatialRelationship = esri.tasks.Query.SPATIAL_REL_INTERSECTS;
	query.returnGeometry = true;
	query.where = EQLyrDefinitions[lyrsMap["EQPoints"]];
	//实例化查询对象
	var queryTask = new esri.tasks.QueryTask(eqLyrUrl + "/" + lyrsMap["EQPoints"]);
	//进行查询
	queryTask.execute(query, initWarnCenterPoint);
}
function initWarnCenterPoint(result){
	if(result.features == undefined){
		return;
	}
	warnMapCenter = result.features[0].geometry;
	warnMap.centerAt(warnMapCenter);
}

//var stationsInfo="{\"stationsInfo\":["
//	+"{\"ST\":\"2017-06-06 17:00:02.110\",\"PT\":\"2017-06-06 17:00:02.010\",\"stationID\":\"51PXZ\"},"
//	+"{\"ST\":\"2017-06-06 17:00:02.210\",\"PT\":\"2017-06-06 17:00:02.110\",\"stationID\":\"51DXY\"},"
//	+"{\"ST\":\"2017-06-06 17:00:02.310\",\"PT\":\"2017-06-06 17:00:02.210\",\"stationID\":\"51PJD\"},"
//	+"{\"ST\":\"2017-06-06 17:00:02.410\",\"PT\":\"2017-06-06 17:00:02.310\",\"stationID\":\"51BXY\"},"
//	+"{\"ST\":\"2017-06-06 17:00:02.510\",\"PT\":\"2017-06-06 17:00:02.410\",\"stationID\":\"51LSJ\"},"
//	+"{\"ST\":\"2017-06-06 17:00:02.610\",\"PT\":\"2017-06-06 17:00:02.510\",\"stationID\":\"51LSF\"},"
//	+"{\"ST\":\"2017-06-06 17:00:02.710\",\"PT\":\"2017-06-06 17:00:02.610\",\"stationID\":\"51YAL\"},"
//	+"{\"ST\":\"2017-06-06 17:00:02.810\",\"PT\":\"2017-06-06 17:00:02.710\",\"stationID\":\"51YBY\"},"
//	+"{\"ST\":\"2017-06-06 17:00:10.910\",\"PT\":\"2017-06-06 17:00:02.810\",\"stationID\":\"51JLD\"}],"
//	+"\"ID\":\"this is a test ID.\",\"info\":\"this is a test info\"}";
//var json = JSON.parse(stationsInfo);
	var circleP,circleS;
	var symbolP,symbolS;
	var pNum = 0, sNum=0;
	var pGraphic, sGraphic;
	function initSymbols(){
		require(["esri/units", "esri/symbols/SimpleFillSymbol", "esri/symbols/SimpleLineSymbol","esri/Color"], 
				function(Units, SimpleFillSymbol,SimpleLineSymbol,Color) {
			symbolP = new SimpleFillSymbol(SimpleLineSymbol.STYLE_SOLID,new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID,
				    new Color([255,0,0]), 2),new Color([255,255,0,0.25]));
			symbolS = new SimpleFillSymbol(SimpleLineSymbol.STYLE_SOLID,new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID,
				    new Color([255,0,0]), 2),new Color([255,100,0,0.25]));
		});
	}
	var eqCenter;
	var rads = [];
	var flag = false;//用作是否停止执行的标识，true表示停止执行，false表示可以执行play的状态
	var startTime = eqTime;//表示起始时间，即地震发生时刻
	var startT;
	var PSTMsg;//用于存储GetWarnPSTServlet获取的数据
function startPlay(){
	flag = false;
	pNum = 0;
	sNum = 0;
	warnMap.graphics.clear();
	eqCenter = warnMapCenter;
	if(startTime == undefined){
		startTime = eqTime;
		startT = startTime.getTime();
	}
	$.ajax({
		type:"get",
		url:"servlet/GetWarnPSDTServlet",
		data:{
			eqID:eqID
		},
		dataType:'json',
		success:function(msg){
			PSTMsg = msg;
			for(var i = 0; i < msg.length; i++){
				var sid = msg[i].stationID;
				var x = eqCenter.x-stationsGeometryMap[sid].x;
				var y = eqCenter.y-stationsGeometryMap[sid].y;
				var radP = Math.sqrt(x*x+y*y);
				rads[i] = radP;
			}
			drawCircles();
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			alert("初始化预警信息出错了");
		}
	});
}
function drawCircles(){
	setNextPCircle();
//	setNextSCircle();
}
function setNextPCircle(){
	if(flag){
		return;
	}
	if(pNum < rads.length){
		var to1;
		if(pNum == 0){
			to1 = new Date(PSTMsg[pNum].PT).getTime() - startT;
		}else{
			to1 = new Date(PSTMsg[pNum].PT).getTime() - new Date(PSTMsg[pNum-1].PT).getTime();
		}
		window.setTimeout(function(){
			drawPCircle();
		},to1);
	}
}
function setNextSCircle(){
	if(flag){
		return;
	}
	if(sNum < rads.length){
		var to2;
		if(sNum == 0){
			to2 = new Date(PSTMsg[sNum].ST).getTime() - startT;
		}else{
			to2 = new Date(PSTMsg[sNum].ST).getTime() - new Date(PSTMsg[sNum-1].ST).getTime();
		}
		window.setTimeout(function(){
			drawSCircle();
		},to2);
	}
}
function setT(t){
	document.getElementById("tt2").innerHTML=t;
	document.getElementById("tt1").innerHTML=parseFloat(new Date(t).getTime() - startT)/1000;
}
function setEpiDis(PSType, dis){
	document.getElementById(PSType+"EpiDis").innerHTML = dis;
}
function clearTEpiDis(){
	document.getElementById("tt2").innerHTML="*";
	document.getElementById("tt1").innerHTML=0;
	document.getElementById("PEpiDis").innerHTML = 0;
	document.getElementById("SEpiDis").innerHTML = 0;
}
function endPlay(){
	if(pGraphic != undefined){
		warnMap.graphics.remove(pGraphic);
	}
	if(sGraphic != undefined){
		warnMap.graphics.remove(sGraphic);
	}
	clearTEpiDis();
	flag = true;
	pNum = 0;
	sNum = 0;
	
}
	function drawPCircle(){
		var radP = rads[pNum];
		require(["esri/geometry/Circle","esri/graphic","esri/units", "esri/symbols/SimpleFillSymbol", "esri/symbols/SimpleLineSymbol","esri/Color"], 
				function(Circle,Graphic,Units, SimpleFillSymbol,SimpleLineSymbol,Color) {
			if(symbolP == undefined){
				symbolP = new SimpleFillSymbol(SimpleLineSymbol.STYLE_SOLID,new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID,
					    new Color([255,0,0]), 2),new Color([255,255,0,0.25]));
			}
			circleP = new Circle(eqCenter,{
				  radius: radP,
				  radiusUnit: Units.DECIMAL_DEGREES,
				  numberOfPoints:360
			 });
			var ppGraphic = new Graphic(circleP,symbolP); 
			if(pGraphic != undefined){
				graphicLayer.remove(pGraphic);
			}
			if(flag){//flag表示停止执行的状态，若停止执行，则直接停止执行
				return;
			}
			graphicLayer.add(ppGraphic);
			setT(PSTMsg[pNum].PT);
			setEpiDis("P", PSTMsg[pNum].EpiDis);
			pGraphic = ppGraphic;
			pNum++;
			if(pNum < rads.length){
				setNextPCircle();
			}
		});
	}
	
	function drawSCircle(){
		var radS = rads[sNum];
		require(["esri/geometry/Circle","esri/graphic","esri/units", "esri/symbols/SimpleFillSymbol", "esri/symbols/SimpleLineSymbol","esri/Color"], 
				function(Circle,Graphic,Units, SimpleFillSymbol,SimpleLineSymbol,Color) {
			if(symbolS == undefined){
				symbolS = new SimpleFillSymbol(SimpleLineSymbol.STYLE_SOLID,new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID,
					    new Color([255,0,0]), 2),new Color([255,100,0,0.5]));
			}
			circleS = new Circle(eqCenter,{
				  radius: radS,
				  radiusUnit: Units.DECIMAL_DEGREES,
				  numberOfPoints:360
			 });
			var ssGraphic = new Graphic(circleS,symbolS); 
			if(sGraphic != undefined){
				graphicLayer.remove(sGraphic);
			}
			if(flag){//flag表示停止执行的状态，若停止执行，则直接停止执行
				return;
			}
			graphicLayer.add(ssGraphic);
			setT(PSTMsg[sNum].ST);
			setEpiDis("S", PSTMsg[sNum].EpiDis);
			sGraphic = ssGraphic;
			sNum++;
			if(sNum < rads.length){
				setNextSCircle();
			}
		});
	}
function initWarnStationsTable(){
	$("#warnStations").bootstrapTable({
		height:200,
		method:'get',
		url:'servlet/GetAReportServlet',
		datatype:'json',
		striped:true,
//		queryParams:'eqID='+eqID+'&rNum=1',
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
				title:'台站',
			},
			{
				field:'M',
				title:'震级',
			},
			{
				field:'PT',
				title:'P波时刻',
			},
			{
				field:'ST',
				title:'S波时刻',
			}
			]
	});
}

function initWarnReports(){
	$.ajax({
		type:"get",
		url:"servlet/GetWarnTMServlet",
		data:{
			eqID:eqID
		},
		dataType:'json',
		success:function(msg){
			var warns = document.getElementById("warns");
			for(var i=0; i<msg.length; i++){
				var pt = document.createElement("p");
				pt.className = "WRTBold";
				pt.innerHTML="预警第"+msg[i].Idx+"报";
				warns.appendChild(pt);
				var pr = document.createElement("p");
				pr.className = "WR";
				pr.innerHTML="生成时间："+msg[i].T+"&nbsp;&nbsp;&nbsp;&nbsp;震级："+msg[i].M;
				warns.appendChild(pr);
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			alert("初始化预警信息出错了");
		}
	});
}