Date.prototype.Format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
var warnMap,warnEQLayer,warnBaseLayer;  
var warnClickPoint;
var stationLayer=0;
var warnEQLyrDef = [];
var graphicLayer;
var eqCenter;
require(["esri/map","amap/AMapTiledMapServiceLayer","esri/geometry/Point", "esri/SpatialReference"], 
		function(Map, AMapTiledMapServiceLayer, Point, SpatialReference) {  
	warnMap = new Map("warnMap", {logo:false,slider: true});  
	warnBaseLayer = new AMapTiledMapServiceLayer();
	warnMap.addLayer(warnBaseLayer, 0);
	warnMap.setScale(5000000);
//    parent.initMapCenter(warnMap,parent.mapCLon,parent.mapCLat);
    graphicLayer = warnMap.graphics;
	parent.drawStations(warnMap);
//	warnMapCenterAtEpi();
	centerAtEpi(warnMap);
	if(eqPoint != undefined){
		eqCenter = new esri.geometry.Point(eqPoint.x,eqPoint.y, new SpatialReference({wkid:parseInt(parent.wkid)}));
	}else{
		setTimeout(function(){eqCenter = new esri.geometry.Point(eqPoint.x,eqPoint.y, new SpatialReference({wkid:parseInt(parent.wkid)}));}, 2000);
	}
	initWarnStationsTable();
	initWarnReports();
	initSymbols();
});

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
	
	var rads = [];//用于按照触发顺序记录台站震中距
	var stIDs = [];//用于按照触发顺序记录台站IDs
	var flag = false;//用作是否停止执行的标识，true表示停止执行，false表示可以执行play的状态
	var startTime = eqTime;//表示起始时间，即地震发生时刻
	var startT = startTime.getTime();
	var PSTMsg;//用于存储GetWarnPSTServlet获取的数据
function startPlay(){
	if(startTime==undefined){
		startTime = eqTime;//表示起始时间，即地震发生时刻
		startT = startTime.getTime();
	}
	flag = false;
	pNum = 0;
	sNum = 0;
	//warnMap.graphics.clear();
//	eqCenter = warnMapCenter;
	if(eqCenter==undefined){
		return;
	}
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
//				var sid = msg[i].stationID;
//				var x = eqCenter.x-stationsGeometryMap[sid].x;
//				var y = eqCenter.y-stationsGeometryMap[sid].y;
//				var radP = Math.sqrt(x*x+y*y);
//				rads[i] = radP;
				rads[i] = msg[i].EpiDis;
				stIDs[i] = msg[i].stationID;
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
		var to1,v,tmpStartTime;
		if(pNum == 0){
			to1 = new Date(PSTMsg[pNum].PT).getTime() - startT;
			v = rads[pNum]/to1;
			tmpStartTime = startTime.getTime();
		}else{
			to1 = new Date(PSTMsg[pNum].PT).getTime() - new Date(PSTMsg[pNum-1].PT).getTime();
			v = (rads[pNum]-rads[pNum-1])*1000/to1;
			tmpStartTime = new Date(PSTMsg[pNum-1].PT).getTime();
		}
		tmpCNum = 0;
		var times = to1/1000;
//		alert(pNum+"\nto1:"+to1+"\ntimes:"+times+"\nv:"+v+"\n"+tmpStartTime);
		if(times > 1 && rads[pNum] > 0){
			drawTmpCircle(v,tmpStartTime,times);
		}else if(times <= 1 && rads[pNum] > 0){
			window.setTimeout(function(){
				drawPCircle();
				putStActiveIcon();
			},to1);
		}else{
			putStActiveIcon();
			pNum++;
			setNextPCircle();
		}
	}
}
var tmpCNum = 0;
function drawTmpCircle(v,tmpStartTime,times){//v表示当前TmpCircle的扩张速度,times表示s数
	tmpCNum++;
	var tmpCR = 0;
	var tmpTime ;
	//rads[i] = msg[i].EpiDis;
	if(pNum == 0){
		tmpCR = v*tmpCNum;
	}else{
		tmpCR = parseFloat(v*tmpCNum)+parseFloat(rads[pNum-1]);
	}
//	alert(pNum+"\ntimes:"+times+"\ntmpCNum:"+tmpCNum+"\nv:"+v+"\ntmpCR:"+tmpCR);
	require(["esri/geometry/Circle","esri/graphic","esri/units", "esri/symbols/SimpleFillSymbol", "esri/symbols/SimpleLineSymbol","esri/Color"], 
			function(Circle,Graphic,Units, SimpleFillSymbol,SimpleLineSymbol,Color) {
		if(symbolP == undefined){
			symbolP = new SimpleFillSymbol(SimpleLineSymbol.STYLE_SOLID,new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID,
				    new Color([255,0,0]), 2),new Color([255,255,0,0.25]));
		}
		circleP = new Circle(eqCenter,{
			  radius: tmpCR,
//			  radiusUnit: Units.DECIMAL_DEGREES,
			  radiusUnit: Units.KILOMETERS,
			  numberOfPoints:720
		 });
		var ppGraphic = new Graphic(circleP,symbolP); 
		if(pGraphic != undefined){
			graphicLayer.remove(pGraphic);
		}
		if(flag){//flag表示停止执行的状态，若停止执行，则直接停止执行
			return;
		}
		graphicLayer.add(ppGraphic);
		var tmpTime = new Date();
		tmpTime.setTime(tmpStartTime+tmpCNum*1000);
		setT(tmpTime.Format("yyyy-MM-dd hh:mm:ss"));
		setEpiDis("P", Math.round(tmpCR*10000)/10000);
		pGraphic = ppGraphic;
	});
	if(tmpCNum < times-1){
		window.setTimeout(function(){
			drawTmpCircle(v,tmpStartTime,times);
		},1000);
	}else{
		window.setTimeout(function(){
//			putStActiveIcon();
			drawPCircle();
		},1000);
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
//	alert(new Date(t).getTime()+"\n"+startT+"\n"+((new Date(t).getTime() - startT)/1000));
	document.getElementById("tt1").innerHTML=parseFloat((new Date(t).getTime() - startT)/1000);
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
		graphicLayer.remove(pGraphic);
	}
	if(sGraphic != undefined){
		graphicLayer.remove(sGraphic);
	}
	clearTEpiDis();
	flag = true;
	pNum = 0;
	sNum = 0;
	clearStActiveIcon();
}
	function clearStActiveIcon(){
		for(var j=0;j<graphicLayer.graphics.length;j++){
			if(graphicLayer.graphics[j].attributes!=undefined && (graphicLayer.graphics[j].attributes.type=="STLabel" || graphicLayer.graphics[j].attributes.type=="EQPoint")){//震中点
				continue;
			}
			graphicLayer.graphics[j].setSymbol(parent.greySymbol);
		}
		graphicLayer.redraw();
	}
	function putStActiveIcon(){
		var stName = stIDs[pNum];
		for(var j=0;j<graphicLayer.graphics.length;j++){
			if(graphicLayer.graphics[j].attributes!=undefined && stName == graphicLayer.graphics[j].attributes.ID){
				graphicLayer.graphics[j].setSymbol(redSymbol);
				break;
			}
		}
		graphicLayer.redraw();
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
//				  radiusUnit: Units.DECIMAL_DEGREES,
				  radiusUnit: Units.KILOMETERS,
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
			putStActiveIcon();
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
//				  radiusUnit: Units.DECIMAL_DEGREES,
				  radiusUnit: Units.KILOMETERS,
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
		height:178,
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
				field:'stGPSMag',
				title:'GPS震级',
			},
			{
				field:'stMEMSMag',
				title:'MEMS震级',
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
//function initStations(){
//	//实例化查询参数
//	var query = new esri.tasks.Query();
//	query.outFields = ["StationID"];
//	query.outSpatialReference = warnMap.spatialReference;
//	query.spatialRelationship = esri.tasks.Query.SPATIAL_REL_INTERSECTS;
//	query.returnGeometry = true;
//	query.where = "1=1";
//	//实例化查询对象
////	var queryTask = new esri.tasks.QueryTask("http://"+parent.serverIP+":6080/arcgis/rest/services/GNSSBase3_0/MapServer/" + stationLayer);
//	var queryTask = new esri.tasks.QueryTask(parent.baseUrl + "/" +parent.stationLayer);
//	//进行查询
//	queryTask.execute(query, showStationsResult);
//}
//var greySymbol;
var redSymbol;
require(["esri/symbols/PictureMarkerSymbol"], function(PictureMarkerSymbol)  
		{
//			greySymbol = new PictureMarkerSymbol("img/trip-32-grey.png", 17, 17);
			redSymbol = new PictureMarkerSymbol("img/trip-32-orange.png", 17, 17);
//			greySymbol.setOffset(5,-6);
			redSymbol.setOffset(5,-6);
		}); 
//function showStationsResult(result){
//	if(result.features == 0){
//		return;
//	}
//	for(var i = 0; i < result.features.length; i++){
//		//获得该图形的形状
//        var feature = result.features[i];
//        var attr = feature.attributes;
//        // attr.StationID);
//		//获得该图形的形状
//        var geometry = feature.geometry;
//        //创建客户端图形
//        var infoGraphic = new esri.Graphic(geometry, greySymbol);
//        infoGraphic.setAttributes({"ID":attr.StationID});
//        //将客户端图形添加到map中
//        warnMap.graphics.add(infoGraphic);
//	}
//	warnMap.graphics.redraw();
//}

//function warnMapCenterAtEpi(){
////alert(curMap.graphics.loaded);
//if(warnMap != undefined && warnMap.loaded && warnMap.graphics != undefined && warnMap.graphics.loaded && epiGraphic != undefined){
//warnMap.centerAt(eqPoint);
//warnMap.graphics.add(epiGraphic);
//warnMap.graphics.redraw();
//}else{
//alert("!loaded or warnMap.graphics == undefined or !warnMap.graphics.loaded or epiGraphic == undefined");
//setTimeout(function(){warnMapCenterAtEpi();}, 1000);
//return;
//}
//}
//function initWarnEQLayer(){
//warnEQLayer = new esri.layers.ArcGISDynamicMapServiceLayer(eqLyrUrl);
//warnEQLayer.setOpacity(0.5);
//warnEQLayer.setVisibleLayers([]);
//warnMap.addLayer(warnEQLayer, 1);
//initWarnVisibleEQLayers(1);
//}
//function initWarnVisibleEQLayers(reportNum){
//if(visibleEQLyrs == undefined){
//var url = eqLyrUrl+"/?f=pjson";
//$.get(url,function(result){
//	var json = eval('('+result+')');
//	if(json.hasOwnProperty("error")&&json.error.code==500){
//		return;
//	}
//	var length = json.layers.length;
//	var layers = json.layers;
//	for(var i=0; i<length; i++){
//		lyrsMap[layers[i].name] = layers[i].id;
//		if(layers[i].name=="EQPoints"){
//			EQPointIdx = layers[i].id;
//			visibleEQLyrs.push(layers[i].id);
//			continue;
//		}
//		if(layers[i].name=="EQPolygons"){
//			EQPolyIdx = layers[i].id;
////			visibleEQLyrs.push(layers[i].id);
//			continue;
//		}
//	}
//	if(visibleEQLyrs == undefined){
//		alert("暂未有地震图层信息，请稍后刷新");
//	}else{
//		warnEQLyrDef[lyrsMap["EQPoints"]] = "type='epicenter'";//"num = "+reportNum+" and type='epicenter'";
//		warnEQLyrDef[lyrsMap["EQPolygons"]] = "num = "+reportNum;
//		warnEQLayer.setLayerDefinitions(warnEQLyrDef);
//		warnEQLayer.refresh();
//	}
//	//初始化mapCenter
//	initWarnCenter();
//});
//}else{
//EQLyrDefinitions[lyrsMap["EQPoints"]] = "type='epicenter'";
//warnEQLayer.setLayerDefinitions(EQLyrDefinitions);
//warnEQLayer.refresh();
////初始化mapCenter
//initWarnCenter();
//}
//}
//function initWarnCenter(){
////实例化查询参数
//var query = new esri.tasks.Query();
//query.outFields = ["num"];
//query.outSpatialReference = warnMap.spatialReference;
//query.spatialRelationship = esri.tasks.Query.SPATIAL_REL_INTERSECTS;
//query.returnGeometry = true;
//query.where = EQLyrDefinitions[lyrsMap["EQPoints"]];
////实例化查询对象
//var queryTask = new esri.tasks.QueryTask(eqLyrUrl + "/" + lyrsMap["EQPoints"]+"/");
////进行查询
//queryTask.execute(query, initWarnCenterPoint);
//}
//function initWarnCenterPoint(){
//if(eqPoint == undefined || warnMap.loaded == false){
//return;
//}
////地图上添加EQPoint
//var symbol = new esri.symbol.PictureMarkerSymbol("img/ep.png", 32, 32);
//var attr = {"ID":eqID,"type":"EQPoint"};
//warnMapCenter = result.features[0].geometry;
//var graphic = new esri.Graphic(warnMapCenter, symbol, attr);
//warnMap.graphics.add(graphic);
//warnMap.centerAt(warnMapCenter);
//}

//var stationsInfo="{\"stationsInfo\":["
//+"{\"ST\":\"2017-06-06 17:00:02.110\",\"PT\":\"2017-06-06 17:00:02.010\",\"stationID\":\"51PXZ\"},"
//+"{\"ST\":\"2017-06-06 17:00:02.210\",\"PT\":\"2017-06-06 17:00:02.110\",\"stationID\":\"51DXY\"},"
//+"{\"ST\":\"2017-06-06 17:00:02.310\",\"PT\":\"2017-06-06 17:00:02.210\",\"stationID\":\"51PJD\"},"
//+"{\"ST\":\"2017-06-06 17:00:02.410\",\"PT\":\"2017-06-06 17:00:02.310\",\"stationID\":\"51BXY\"},"
//+"{\"ST\":\"2017-06-06 17:00:02.510\",\"PT\":\"2017-06-06 17:00:02.410\",\"stationID\":\"51LSJ\"},"
//+"{\"ST\":\"2017-06-06 17:00:02.610\",\"PT\":\"2017-06-06 17:00:02.510\",\"stationID\":\"51LSF\"},"
//+"{\"ST\":\"2017-06-06 17:00:02.710\",\"PT\":\"2017-06-06 17:00:02.610\",\"stationID\":\"51YAL\"},"
//+"{\"ST\":\"2017-06-06 17:00:02.810\",\"PT\":\"2017-06-06 17:00:02.710\",\"stationID\":\"51YBY\"},"
//+"{\"ST\":\"2017-06-06 17:00:10.910\",\"PT\":\"2017-06-06 17:00:02.810\",\"stationID\":\"51JLD\"}],"
//+"\"ID\":\"this is a test ID.\",\"info\":\"this is a test info\"}";
//var json = JSON.parse(stationsInfo);