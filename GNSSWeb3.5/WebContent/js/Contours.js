var mapCH,mapCV;  
var mapCHEqLayer,mapCVEqLayer;
function contoursClick(){
	if(mapCH == undefined || mapCV == undefined){
		initContours();
	}
	showContours();
}
function initContours(){
	require(["esri/map"], function(Map){
		mapCH = new Map("mapCH", {logo:false,slider: true});  
		var mapCHBLyr = new esri.layers.ArcGISDynamicMapServiceLayer(baseLyrUrl);
		mapCHBLyr.setVisibleLayers([2,17,20]);//[0,2,5,10,11]
		mapCH.addLayer(mapCHBLyr, 0);
		mapCHEqLayer = new esri.layers.ArcGISDynamicMapServiceLayer(eqLyrUrl);
		mapCHEqLayer.setVisibleLayers([]);
		mapCH.addLayer(mapCHEqLayer, 1);
		mapCH.setScale(2500000);
		mapCH.on("load", function(){
			mapCH.centerAt(mapCenter);
	    });
		
		mapCV = new Map("mapCV", {logo:false,slider: true}); 
		var mapCVBLyr = new esri.layers.ArcGISDynamicMapServiceLayer(baseLyrUrl);
		mapCVBLyr.setVisibleLayers([2,17,20]);//[0,2,5,10,11]
		mapCV.addLayer(mapCVBLyr, 0);
		mapCVEqLayer = new esri.layers.ArcGISDynamicMapServiceLayer(eqLyrUrl);
		mapCVEqLayer.setVisibleLayers([]);
		mapCV.addLayer(mapCVEqLayer, 1);
		mapCV.setScale(2500000);
		mapCV.on("load", function(){
			mapCV.centerAt(mapCenter);
	    });
	});
}
function showContours(){
	var lysH = [];
	var lysV = [];
	lysH.push(lyrsMap["EQPoints"]);
	lysV.push(lyrsMap["EQPoints"]);
	for(var prop in lyrsMap){
		if(lyrsMap.hasOwnProperty(prop)){
			if(prop.indexOf("eqcontours_h_")==0){
				lysH.push(lyrsMap[prop]);
				var tt = prop.substring(prop.lastIndexOf("_")+1);
				lysH.push(lyrsMap["EQRaster_H_"+tt]);
				continue;
			}else if(prop.indexOf("eqcontours_v_")==0){
				lysV.push(lyrsMap[prop]);
				var tt = prop.substring(prop.lastIndexOf("_")+1);
				lysV.push(lyrsMap["EQRaster_V_"+tt]);
				continue;
			}
		}
	}
	if(lysH.length <= 1 || lysV.length <= 1){
		alert("当前地震暂无水平/垂直振幅等值线，请稍后查看。");
	}
	mapCHEqLayer.setVisibleLayers(lysH);
	mapCHEqLayer.setOpacity(0.5);
	mapCVEqLayer.setVisibleLayers(lysV);
	mapCVEqLayer.setOpacity(0.5);
	var lysDef = [];
	lysDef[lyrsMap["EQPoints"]] = "num=1 and type='epicenter'";
	mapCHEqLayer.setLayerDefinitions(lysDef);
	mapCVEqLayer.setLayerDefinitions(lysDef);
	mapCH.centerAt(mapCenter);
	mapCV.centerAt(mapCenter);
}