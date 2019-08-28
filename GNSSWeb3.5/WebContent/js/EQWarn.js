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
				pr.innerHTML="生成时间："+msg[i].T+"&nbsp;&nbsp;&nbsp;&nbsp;GPS震级："+msg[i].stGPSMag;
				warns.appendChild(pr);
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			alert("初始化预警信息出错了");
		}
	});
}
var redSymbol;
require(["esri/symbols/PictureMarkerSymbol"], function(PictureMarkerSymbol)  
		{
			redSymbol = new PictureMarkerSymbol("img/trip-32-orange.png", 17, 17);
			redSymbol.setOffset(5,-6);
		}); 
