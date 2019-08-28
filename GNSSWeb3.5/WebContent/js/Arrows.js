var mapAH,mapAV;  
var mapAHEqLayer,mapAVEqLayer;
function arrowsClick(){
	if(mapAH == undefined || mapAV == undefined){
		initArrows();
	}
	initATimeLineFromService();
	showArrows();
}
function initArrows(){
	require(["esri/map"], function(Map){
		mapAH = new Map("mapAH", {logo:false,slider: true});  
		mapAH.on("load", function(){
			mapAH.centerAt(mapCenter);
		});
		var mapAHBLyr = new esri.layers.ArcGISDynamicMapServiceLayer(baseLyrUrl);
		mapAHBLyr.setVisibleLayers([2,17,20]);//[0,2,5,10,11]
		mapAH.addLayer(mapAHBLyr, 0);
		mapAHEqLayer = new esri.layers.ArcGISDynamicMapServiceLayer(eqLyrUrl);
		mapAHEqLayer.setVisibleLayers([]);
		mapAH.addLayer(mapAHEqLayer, 1);
		mapAH.setScale(2500000);
		
		mapAV = new Map("mapAV", {logo:false,slider: true}); 
		var mapAVBLyr = new esri.layers.ArcGISDynamicMapServiceLayer(baseLyrUrl);
		mapAVBLyr.setVisibleLayers([2,17,20]);//[0,2,5,10,11]
		mapAV.addLayer(mapAVBLyr, 0);
		mapAVEqLayer = new esri.layers.ArcGISDynamicMapServiceLayer(eqLyrUrl);
		mapAVEqLayer.setVisibleLayers([]);
		mapAV.addLayer(mapAVEqLayer, 1);
		mapAV.setScale(2500000);
		mapAV.on("load", function(){
			mapAV.centerAt(mapCenter);
        });
	});
}
function initATimeLineFromService(){
	var times = [];
	var tNode = $("#tNode");
	tNode.empty();
	for(var prop in lyrsMap){
		if(lyrsMap.hasOwnProperty(prop)){
			if(prop.indexOf("EQArrows_")==0){
				var val = parseInt(prop.substring(prop.lastIndexOf('_')+1, prop.length-1));
				if(times.indexOf(val) == -1){
    				times.push(val);
    			}
			}
		}
	}
	if(times.length <= 0){
		alert("当前地震暂无振幅示意图，请稍后查看。");
		return;
	}
	times.sort(sortNumber);
	for(var i=0; i<times.length; i++){
		var opt = $("<option value =\""+times[i]+"s\">"+times[i]+"s</option>");
		tNode.append(opt);
	}
}
//数值型array的排序函数
function sortNumber(a, b){
	return a - b;
}
function showArrows(){
	var nameH = "EQArrows_H_"+$("#tNode").val();
	var nameV = "EQArrows_V_"+$("#tNode").val();
	var lysH = [];
	lysH.push(lyrsMap["EQPoints"]);
	lysH.push(lyrsMap[nameH]);
	mapAHEqLayer.setVisibleLayers(lysH);
	var lysDefH = [];
	lysDefH[lyrsMap["EQPoints"]] = "num=1 and type='epicenter'";
	mapAHEqLayer.setLayerDefinitions(lysDefH);
	mapAH.centerAt(mapCenter);
	//mapAHEqLayer.refresh();
	var lysV = [];
	lysV.push(lyrsMap["EQPoints"]);
	lysV.push(lyrsMap[nameV]);
	mapAVEqLayer.setVisibleLayers(lysV);
	var lysDefV = [];
	lysDefV[lyrsMap["EQPoints"]] = "num=1 and type='epicenter'";
	mapAVEqLayer.setLayerDefinitions(lysDefV);
	mapAV.centerAt(mapCenter);
	//mapAVEqLayer.refresh();
}
