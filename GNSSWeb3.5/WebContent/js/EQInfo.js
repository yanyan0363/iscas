var eqID = getQueryString("EQID");
$("#outDiv").outerWidth(parent.rightWidth);
//截取URL参数
function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]); return null;
 }		
var lyrsMap={};
var stationLayer=0;
var curInfoGraphic;//当前高亮显示的面状要素
var EQPolyIdx;//EQPolygons的层号
var EQPointIdx;//EQPoint的层号
var baseLyrUrl = parent.baseUrl;
var eqLyrUrl = "http://"+parent.serverIP+":6080/arcgis/rest/services/EQ/"+eqID+"/MapServer";
var visibleEQLyrs = [];
//var stationsGeometryMap = {};//记录台站的位置信息
var stationsGeometryMap = new Map();//记录台站的位置信息
var eqTime;//表示地震发生时刻
var eqPoint;//震中点Point对象，在EQIdx中执行初始化操作
var epiGraphic;//震中点Graphic对象，在EQIdx中执行初始化操作
initEQInfo();
//parent.initAllStationsGeometry();
function centerAtEpi(MapToEpi){
	require(["esri/geometry/Point", "esri/SpatialReference", "esri/geometry/webMercatorUtils"], function(Point, SpatialReference, webMercatorUtils) {
		 if(MapToEpi != undefined && MapToEpi.loaded && MapToEpi.graphics != undefined && MapToEpi.graphics.loaded && epiGraphic != undefined){
			 var ep = new esri.geometry.Point(eqPoint.x,eqPoint.y, new SpatialReference({wkid:parseInt(parent.wkid)}));
			 var symbol = new esri.symbol.PictureMarkerSymbol("img/ep.png", 32, 32);
			 var attr = {"ID":eqID,"type":"EQPoint"};
			 var epG = new esri.Graphic(ep, symbol, attr);
			 MapToEpi.centerAt(ep);
			 MapToEpi.graphics.add(epG);
			 MapToEpi.graphics.redraw();
		 }else{
//			 alert("!loaded or curMap.graphics == undefined or !curMap.graphics.loaded or epiGraphic == undefined");
			 setTimeout(function(){centerAtEpi(MapToEpi);}, 1000);
			 return;
		 }
	});
}
function initEQPoint(lon, lat){
	require(["esri/geometry/Point", "esri/SpatialReference", "esri/geometry/webMercatorUtils"], function(Point, SpatialReference, webMercatorUtils) {
		 var point = new esri.geometry.Point(parseFloat(lon),parseFloat(lat),new SpatialReference({wkid:parseInt(4326)}));//这里面需要定义输入点的坐标系，不写默认会是4326
		 var geom = webMercatorUtils.geographicToWebMercator(point);
		 eqPoint = new esri.geometry.Point(geom.x,geom.y, new SpatialReference({wkid:parseInt(parent.wkid)}));
		 var symbol = new esri.symbol.PictureMarkerSymbol("img/ep.png", 32, 32);
		 var attr = {"ID":eqID,"type":"EQPoint"};
		 epiGraphic = new esri.Graphic(eqPoint, symbol, attr);
	});
}
function initEQInfo(){
	$.ajax({
		type:"post",
		url:"servlet/GetEQInfoByID",
		data:{
			eqID:eqID,
		},
		dataType:'json',
		success:function(msg){
			eqTime = new Date(msg.originTime);
			//IndexTab
//			$("#oTimeInIdx").text(msg.originTime);
//			$("#epiInIdx").text(msg.epi);
//			$("#magInIdx").text(msg.mag);
			//warnTab
			$("#oTInWarn").text(msg.originTime);
			$("#epiInWarn").text(msg.epi);
			$("#magInWarn").text(msg.gpsMag);
			//reportTab
//			$("#eqID").text(msg.EQID);
			//ImgsTab
			$("#eqName").text(msg.name);
			$("#basicID").text(msg.EQID);
			$("#basicName").text(msg.name);
			$("#basicTriggerTime").text(msg.createTime);
			$("#basicTime").text(msg.originTime);
			$("#basicLoc").text(msg.epi);
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			alert("获取当前地震的基本信息出错了，请联系管理员");
		}
	});
}

//function drawStations(curMap){
//	if(parent.stationsGeometryMap == undefined || parent.stationsGeometryMap.size <= 0){
//		setTimeout(function(){drawStations(curMap);}, 1000);
//		return;
//	}else{
//		require(["esri/symbols/TextSymbol", "esri/symbols/Font", "esri/Color"], function(TextSymbol, Font, Color)  {
//			var labelFont = new Font("10pt", Font.STYLE_ITALIC, Font.VARIANT_NORMAL, Font.WEIGHT_BOLD,"Courier");
//			var labelColor = new Color("black");
//			parent.stationsGeometryMap.forEach(function (item, key, mapObj) {
//				var stID = key;
//				//获得该图形的形状
//				var geometry = parent.stationsGeometryMap.get(key);
//				var attr = {"ID":stID,"type":"Station"};
//				//创建客户端图形
//				var infoGraphic = new esri.Graphic(geometry, greySymbol, attr);
//				//将客户端图形添加到map中
//				map.graphics.add(infoGraphic);
//				//添加台站名称标注
//				var labelID = stID+"_Label";
//				var labelAttr = {"ID":labelID,"type":"STLabel"};
//				var stLabelSymbol = new TextSymbol(stID, labelFont, labelColor);
//				stLabelSymbol.setOffset(0, 5);
//				var labelGraphic = new esri.Graphic(geometry, stLabelSymbol, labelAttr);
//				curMap.graphics.add(labelGraphic);
//				curMap.graphics.redraw();
//			});
//		}); 
//	}
//}
//function showStationsResult(result){
//	if(result.features == 0){
//		return;
//	}
//	var totalNum = result.features.length;
//	require(["esri/symbols/TextSymbol", "esri/symbols/Font", "esri/Color"], function(TextSymbol, Font, Color)  {
//		var labelFont = new Font("10pt", Font.STYLE_ITALIC, Font.VARIANT_NORMAL, Font.WEIGHT_BOLD,"Courier");
//		var labelColor = new Color("black");
//		
//		for(var i = 0; i < totalNum; i++){
//			//获得该图形的形状
//			var feature = result.features[i];
//			var attr = feature.attributes;
//			var stID = attr.StationID;
//			//获得该图形的形状
//			var geometry = feature.geometry;
//			var attr = {"ID":stID,"type":"Station"};
//			//创建客户端图形
//			var infoGraphic = new esri.Graphic(geometry, greySymbol, attr);
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

