var reportMap;
var reportBaseLayer,reportEQLayer;
var reportEQLyrDefinitions = [];//EQLayer的过滤条件
var reportInfoGraphic;
var reportClickPoint;
var reportBaseDef = [];
require(["esri/map"], function(Map) {
	reportMap = new Map("reportMap", {logo:false,slider: true});  
	reportBaseLayer = new esri.layers.ArcGISDynamicMapServiceLayer(baseLyrUrl);
	reportBaseLayer.setVisibleLayers([0,1,2,3]);
	reportBaseDef[1] = "0=1";
	reportBaseLayer.setLayerDefinitions(reportBaseDef);
	reportMap.addLayer(reportBaseLayer, 0);
	reportMap.setScale(5000000);
	reportMap.on("load", function(){
		reportMap.graphics.clear();
	});
	reportMap.on("click", reportMapClick);
	initReportEQLayer();
	initReportStations();
	initReportDetail();
	initReportChart();
	});
function initReportDetail(){
	$.ajax({
		type:"post",
		url:"servlet/GetReportDetail",
		data:{
			eqID:eqID
		},
		datatype:'json',
		success:function(msg){
			var json = JSON.parse(msg);
			//alert(json.rail+"\n"+json.info.totalArea);
			//totalArea
			$("#area").text("覆盖面积：" + json.info.totalArea + " 平方公里");
			//epi
			var epi="";
			if(json.info.epi.L>0){
				Lon=json.info.epi.L+"E";
			}else if(json.info.epi.L<0){
				Lon=json.info.epi.L+"W";
			}else{
				Lon=json.info.epi.L;
			}
			
			if(json.info.epi.B>0){
				Lat=json.info.epi.B+"N";
			}else if(json.info.epi.B<0){
				Lat=json.info.epi.L+"S";
			}else{
				Lat=json.info.epi.B;
			}
			$("#epi").text("震中位置：" + Lon + "," + Lat);
			
			//rail
			var rail = json.rail;
			$("#rail").empty();
			$("#rail").append('<div class="rails"><a>&nbsp</a></div>');
			for(var i=0; i<rail.length;i++){
				if(rail[i].name==" "){
					$("#rail").append('<div class="rails"><a onclick="railClick('+rail[i].OID+');">'+(i+1)+'. 铁路：未知名称</a></div>');
				}else{
					$("#rail").append('<div class="rails"><a onclick="railClick('+rail[i].OID+');">'+(i+1)+'. ' + rail[i].name+'</a></div>');
				}
			}
			//provinces proList
			$("#proList").empty();
			var provs = json.info.provinces;
			for(var i=0; i<provs.length;i++){
				$("#proList").append('<div class="provBold">地区：'+ provs[i].prov+'</div>');
				$("#proList").append('<div class="detail">灾区详情</div>');
				$("#proList").append('<table id="provTable'+i+'" class="table table-striped table-bordered table-hover"></table>');
				initProvTable('provTable'+i, provs[i].counties);
			}
		}
	});
}
function initProvTable(tableID, datas){
	$('#'+tableID).bootstrapTable({
        striped:true,
        cache:false,
        pagination:false,
        sortable:false,
        data:datas,
        showFooter:false,
        rowStyle:function rowStyle(row, index){
        	return {
        	    css: {"font-size":"12px"}
        	  };
        },
        columns:[
        	{
        		field:'name',
        		title:'区县'
        	},
        	{
        		field:'area',
        		title:'面积(平方公里)',
        	},
        	{
        		field:'mag',
        		title:'震级',
        	},
        	{
        		field:'pop',
        		title:'人口',
        	}
        ]
    });
}
function initReportEQLayer(){
	reportEQLayer = new esri.layers.ArcGISDynamicMapServiceLayer(eqLyrUrl);
	reportEQLayer.setOpacity(0.5);
	reportEQLayer.setVisibleLayers([]);
	reportMap.addLayer(reportEQLayer, 1);
	showReportEQLayers(1);
}
function showReportEQLayers(reportNum){
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
				reportEQLayer.setVisibleLayers(visibleEQLyrs);
				reportEQLyrDefinitions[lyrsMap["EQPoints"]] = "num = "+reportNum+" and type='epicenter'";
				reportEQLyrDefinitions[lyrsMap["EQPolygons"]] = "num = " + reportNum;
				reportEQLayer.setLayerDefinitions(reportEQLyrDefinitions);
				reportEQLayer.refresh();
			}
			//初始化mapCenter
			initReportMapCenter();
		});
	}else{
		reportEQLayer.setVisibleLayers(visibleEQLyrs);
		reportEQLyrDefinitions[lyrsMap["EQPoints"]] = "num = "+reportNum+" and type='epicenter'";
		reportEQLyrDefinitions[lyrsMap["EQPolygons"]] = "num = " + reportNum;
		reportEQLayer.setLayerDefinitions(reportEQLyrDefinitions);
		reportEQLayer.refresh();
		initReportMapCenter();
	}
}
function initReportMapCenter(){
	//实例化查询参数
	var qq = new esri.tasks.Query();
	qq.outFields = ["num"];
	qq.outSpatialReference = reportMap.spatialReference;
	qq.spatialRelationship = esri.tasks.Query.SPATIAL_REL_INTERSECTS;
	qq.returnGeometry = true;
	qq.where = reportEQLyrDefinitions[lyrsMap["EQPoints"]];
	//实例化查询对象
	var qTask = new esri.tasks.QueryTask(eqLyrUrl + "/" + lyrsMap["EQPoints"]);
	//进行查询
	qTask.execute(qq, initReportMapCenterPoint);
}
function initReportMapCenterPoint(result){
	if(result.features == undefined){
		return;
	}
	//alert(result.features[0].geometry.x+", "+result.features[0].geometry.y+","+lyrsMap["EQPoints"]);
	var mC = result.features[0].geometry;
	reportMap.centerAt(mC);
}
function initReportStations(){
	//实例化查询参数
	var query = new esri.tasks.Query();
	query.outFields = ["stationID"];
	query.outSpatialReference = reportMap.spatialReference;
	query.spatialRelationship = esri.tasks.Query.SPATIAL_REL_INTERSECTS;
	query.returnGeometry = true;
	query.where = "1=1";
	//实例化查询对象
	var queryTask = new esri.tasks.QueryTask(baseLyrUrl + "/" + stationLayer);
	//进行查询
	queryTask.execute(query, showReportStationsResult);
	
}
function showReportStationsResult(result){
	if(result.features == 0){
		return;
	}
	require(["esri/symbols/SimpleMarkerSymbol","esri/symbols/SimpleLineSymbol","esri/Color"], 
			function(SimpleMarkerSymbol,SimpleLineSymbol,Color)  
			{
		inSymbol = new esri.symbol.PictureMarkerSymbol("img/fill-green-16.png", 16, 16);
			}); 
	var stationArray = new Array();
	for(var i = 0; i < result.features.length; i++){
		//获得该图形的形状
		var feature = result.features[i];
		var attr = feature.attributes;
		var geometry = feature.geometry;
		//创建客户端图形
		var infoGraphic = new esri.Graphic(geometry, inSymbol);
		infoGraphic.setAttributes({"ID":attr.StationID});
		stationArray.push("{\"ID\":\""+attr.StationID+"\"}");
		//将客户端图形添加到map中
		reportMap.graphics.add(infoGraphic);
	}
	initCurReportStations(3);
}
function initCurReportStations(rNum){
	$.ajax({
		type:"get",
		url:"servlet/GetCurReportStationsServlet",
		data:{
			eqID:eqID,
			reportNum:rNum
		},
		dataType:'json',
		success:function(msg){
			var stationsArray = new Array();
			if(msg == undefined || msg.stations.length == 0){
				return;
			}
			var graphics = reportMap.graphics.graphics;
			require(["esri/symbols/SimpleMarkerSymbol","esri/symbols/SimpleLineSymbol","esri/Color"], 
					function(SimpleMarkerSymbol,SimpleLineSymbol,Color)  
					{
				overSymbol = new esri.symbol.PictureMarkerSymbol("img/fill-red-16.png", 16, 16);
					});
			for(var i=0;i<msg.stations.length;i++){
				for(var j=0;j<graphics.length;j++){
					if(msg.stations[i] == graphics[j].attributes.ID){
						graphics[j].setSymbol(overSymbol);
						break;
					}
				}
			}
			reportMap.graphics.redraw();
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			alert("获取当前报表的台站列表出错了，请联系管理员");
		}
	});
}
function reportMapClick(e){
	//首先，清除infoWindow和map.graphics中高亮显示的面状要素
	reportMap.infoWindow.hide();
	reportMap.graphics.remove(reportInfoGraphic);
	//获取用户点击的地图坐标
	var reportClickPoint = e.mapPoint;
	//alert(reportClickPoint.x+"::"+reportClickPoint.y);
	queryReportPolygon(reportClickPoint);
}
function queryReportPolygon(reportClickPoint){
	//实例化查询参数
	var query = new esri.tasks.Query();
	//合法的geometry类型是Extent, Point, Multipoint, Polyline, Polygon
	query.geometry = reportClickPoint;
	query.outFields = ["*"];
	query.outSpatialReference = map.spatialReference;
	query.spatialRelationship = esri.tasks.Query.SPATIAL_REL_INTERSECTS;
	query.returnGeometry = true;
	query.where = EQLyrDefinitions[lyrsMap["EQPolygons"]];
	//实例化查询对象
	var queryTask = new esri.tasks.QueryTask(eqLyrUrl+"/" + lyrsMap["EQPolygons"]);
	//进行查询
	queryTask.execute(query, showReportPolygonResult);
}
function showReportPolygonResult(queryResult){
	if(queryResult.features == 0){
		return;
	}
	for(var i = 0; i < queryResult.features.length; i++){
		//获得该图形的形状
        var feature = queryResult.features[i];
        var geometry = feature.geometry;
        //定义高亮图形的符号
        //1.定义面的边界线符号
        var outline= new esri.symbol.SimpleLineSymbol(esri.symbol.SimpleLineSymbol.STYLE_SOLID,new esri.Color([255, 0, 0]), 1);
        //2.定义面符号
        var polygonSymbol = new esri.symbol.SimpleFillSymbol(esri.symbol.SimpleFillSymbol.STYLE_SOLID, outline,new esri.Color([255, 250, 250, 0.5]));
        //创建客户端图形
        reportInfoGraphic = new esri.Graphic(geometry, polygonSymbol);
        //将客户端图形添加到map中
        reportMap.graphics.add(reportInfoGraphic);
        //添加信息窗口
        var attr = feature.attributes;
        area+=attr.SHAPE_Area;
        var attrInfos = {"EQID":eqID,"Lon":Lon,"Lat":Lat,"num":attr.num,"magnitude":attr.magnitude,"stationID":attr.stationID,"inTime":attr.inTime};
    	var infoTemplate = new esri.InfoTemplate("${EQID}", "地震ID:${EQID}<br>经度:${Lon}<br>纬度:${Lat}<br>时报:${num}<br>震级:${magnitude}<br>所属台站:${stationID}<br><a href='staticCharts.html?stations=${stationID}&time=${inTime}&eqID=${EQID}'>查看数据</a>");
    	var infoWin = new esri.Graphic(geometry, polygonSymbol, attrInfos, infoTemplate);
    	reportMap.infoWindow.setContent(infoWin.getContent());
    	reportMap.infoWindow.setTitle(infoWin.getTitle());
    	reportMap.infoWindow.show(reportClickPoint);
	}
}
function railClick(OID){
	reportBaseDef[1] +=(" or OBJECTID = "+OID);
	reportBaseLayer.setLayerDefinitions(reportBaseDef);
}
function initReportChart(){
	var reportChartDiv = document.getElementById("reportChartDiv");
	reportChartDiv.style.height='180px';
	reportChartDiv.style.width='1200px';
	var reportChar = echarts.init(reportChartDiv);
	var reportCharOption = {
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
					name:'震级',
					type:'line',
					data: [],
					markPoint: {
						symbolSize: 46,
						data: [
							{type: 'max', name: '最大值'},
							{type: 'min', name: '最小值'}
							]
					}
				}
				]
	};
	reportChar.setOption(reportCharOption);
	$.ajax({
		type:"post",
		url:"servlet/GetEQMag",
		data:{
			eqID:eqID
		},
		datatype:'json',
		success:function(msg){
			var mm = eval(msg);
			var labels = mm[1];
			var m = mm[0]+"";
			var mData = m.split(",");
			reportChar.setOption({
				xAxis:  {
					data: labels,
				},
				series: [
					{
						data: mData,
					}
					]
			});
		}
	});
}
/*
        //根据时报序号刷新map
        function refreshMap(reportNum){
        	//首先，清除infoWindow和map.graphics
       	 	map.infoWindow.hide();
       	 	if(map.graphics!=undefined){
       	 		map.graphics.clear();
       	 	}
       	 	if(lyrsMap["EQPoints"] == undefined || lyrsMap["EQPolygons"] == undefined){
       	 		initVisibleEQLayers(reportNum);
       	 	}
       	 	EQLyrDefinitions[lyrsMap["EQPoints"]] = "num = " + reportNum + "  and type='epicenter'";
       	 	EQLyrDefinitions[lyrsMap["EQPolygons"]] = "num = " + reportNum;
            EQLayer.setLayerDefinitions(EQLyrDefinitions);
            baseDefinitions[10] = "0=1";
            baseLayer.setLayerDefinitions(baseDefinitions);
            //根据时报序号刷新当前时报的台站符号
            initStations(reportNum);
        }
        function infoWinHide(e){
        	map.infoWindow.hide();
        }
        
        
*/