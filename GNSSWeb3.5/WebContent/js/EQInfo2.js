	var eqID = getQueryString("EQID");
	$("#outDiv").outerWidth(parent.rightWidth);
      //截取URL参数
        function getQueryString(name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
            var r = window.location.search.substr(1).match(reg);
            if (r != null) return unescape(r[2]); return null;
         }		
		var lyrsMap={};
        
       // var eqPolygons = null;//存储当前页面的地震区域数据
        var stationLayer=0;
        var curInfoGraphic;//当前高亮显示的面状要素
        var EQPolyIdx;//EQPolygons的层号
        var EQPointIdx;//EQPoint的层号
//        var Lon,Lat;//震中经度、纬度
        var baseLyrUrl = "http://"+parent.serverIP+":6080/arcgis/rest/services/GNSSBase3_0/MapServer";
        var eqLyrUrl = "http://"+parent.serverIP+":6080/arcgis/rest/services/EQ/"+eqID+"/MapServer";
        var visibleEQLyrs = [];
        var stationsGeometryMap = {};//记录台站的位置信息
        var eqTime;//表示地震发生时刻
        initAllStationsGeometry();
        initEQInfo();
        
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
        			$("#oTimeInIdx").text(msg.originTime);
        			$("#epiInIdx").text(msg.epi);
        			$("#magInIdx").text(msg.mag);
        			//warnTab
        			$("#oTInWarn").text(msg.originTime);
        			$("#epiInWarn").text(msg.epi);
        			$("#magInWarn").text(msg.mag);
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
        function initMapCenterPoint(callbackFunc){
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
        	queryTask.execute(query, callbackFunc);
        }
        function initAllStationsGeometry(){
        	//实例化查询参数
        	var query = new esri.tasks.Query();
        	query.outFields = ["stationID"];
        	query.outSpatialReference = map.spatialReference;
        	query.spatialRelationship = esri.tasks.Query.SPATIAL_REL_INTERSECTS;
        	query.returnGeometry = true;
        	query.where = "1=1";
        	//实例化查询对象
        	var queryTask = new esri.tasks.QueryTask(baseLyrUrl + "/" + stationLayer);
        	//进行查询
        	queryTask.execute(query, initStationsGeometry);
        }
        function initStationsGeometry(result){
        	if(result.features == 0){
        		return;
        	}
        	for(var i = 0; i < result.features.length; i++){
        		//获得该图形的形状
                var feature = result.features[i];
                var attr = feature.attributes;
                var geometry = feature.geometry;
                //记录台站位置信息
                stationsGeometryMap[attr.StationID] = geometry;
        	}
        }
