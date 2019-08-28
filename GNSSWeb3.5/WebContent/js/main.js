var serverIP = "192.168.1.94";//192.168.137.1 127.0.0.1
var baseUrl = "http://"+serverIP+":6080/arcgis/rest/services/GNSSBase3_2/MapServer";
var wkid = 3857;
//日本：139.5,38， 四川：103.2,29.2
var mapCLon = 103.2;
var mapCLat = 29.2;
var stationLayer = 0;
var greySymbol;
//require(["esri/symbols/PictureMarkerSymbol"], function(PictureMarkerSymbol) {
//	greySymbol = new PictureMarkerSymbol("img/trip-32-grey.png", 16, 16);
//}); 
var stationsGeometryMap = new Map();//记录台站的位置信息
initAllStationsGeometry();	
function initAllStationsGeometry(){
	require(["esri/SpatialReference"], function(SpatialReference)  {
		if(greySymbol == undefined){
			require(["esri/symbols/PictureMarkerSymbol"], function(PictureMarkerSymbol) {
				greySymbol = new PictureMarkerSymbol("img/trip-32-grey.png", 16, 16);
			}); 
		}
		$.ajax({
			type:"get",
			url:"servlet/GetStationsInfoServlet",
			dataType:'json',
			success:function(msg){
				for(var i = 0; i < msg.length; i++){
					var curSt = msg[i];
					var point = new esri.geometry.Point(curSt.x,curSt.y, new SpatialReference({wkid:parseInt(wkid)}))
					stationsGeometryMap.set(curSt.StationID, point);
				}
	    	},
	    	error:function(XMLHttpRequest, textStatus, errorThrown){
	    		
	    	}
		});
//		//实例化查询参数
//		var query = new esri.tasks.Query();
//		query.outFields = ["stationID"];
//		query.outSpatialReference = new SpatialReference(parent.wkid);
//		query.spatialRelationship = esri.tasks.Query.SPATIAL_REL_INTERSECTS;
//		query.returnGeometry = true;
//		query.where = "1=1";
//		//实例化查询对象
//		var queryTask = new esri.tasks.QueryTask(baseUrl + "/" + stationLayer);
//		//进行查询
//		queryTask.execute(query, initStationsGeometry);
		});
}
//		function initStationsGeometry(result){
//			if(result.features == 0){
//				return;
//			}
//			for(var i = 0; i < result.features.length; i++){
//				//获得该图形的形状
//				var feature = result.features[i];
//				var attr = feature.attributes;
//				var geometry = feature.geometry;
//				//记录台站位置信息
////				stationsGeometryMap[attr.StationID] = geometry;
//				stationsGeometryMap.set(attr.StationID, geometry);
//			}
//		}
		function drawStations(curMap){
			if(stationsGeometryMap == undefined || stationsGeometryMap.size <= 0){
//				alert("stationsGeometryMap == undefined || stationsGeometryMap.size <= 0, wait 1s ...");
				initAllStationsGeometry();
				setTimeout(function(){drawStations(curMap);}, 1000);
				return;
			}else{
				require(["esri/symbols/TextSymbol", "esri/symbols/Font", "esri/Color"], function(TextSymbol, Font, Color)  {
					var labelFont = new Font("10pt", Font.STYLE_ITALIC, Font.VARIANT_NORMAL, Font.WEIGHT_BOLD,"Courier");
					var labelColor = new Color("black");
					stationsGeometryMap.forEach(function (item, key, mapObj) {
						var stID = key;
						//获得该图形的形状
						var geometry = stationsGeometryMap.get(key);
						var attr = {"ID":stID,"type":"Station"};
						//创建客户端图形
						var infoGraphic = new esri.Graphic(geometry, greySymbol, attr);
						//将客户端图形添加到map中
						curMap.graphics.add(infoGraphic);
						//添加台站名称标注
						var labelID = stID+"_Label";
						var labelAttr = {"ID":labelID,"type":"STLabel"};
						var stLabelSymbol = new TextSymbol(stID, labelFont, labelColor);
						stLabelSymbol.setOffset(0, 5);
						var labelGraphic = new esri.Graphic(geometry, stLabelSymbol, labelAttr);
						curMap.graphics.add(labelGraphic);
						curMap.graphics.redraw();
					});
				}); 
			}
		}
		function initMapCenter(curMap, lon, lat){
			require(["esri/geometry/Point", "esri/SpatialReference", "esri/geometry/webMercatorUtils"], function(Point, SpatialReference, webMercatorUtils) {
			 var point = new esri.geometry.Point(parseFloat(lon),parseFloat(lat),new SpatialReference({wkid:parseInt(4326)}));//这里面需要定义输入点的坐标系，不写默认会是4326
			 var geom = webMercatorUtils.geographicToWebMercator(point);
//			 alert(geom.x+", "+geom.y);
			 curMap.centerAt(new esri.geometry.Point(geom.x,geom.y, new SpatialReference({wkid:parseInt(wkid)})));
			});
		}
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
	    setInterval(function(){  
	          var box=document.getElementById("time");  
	          box.innerHTML=new Date().Format("yyyy年MM月dd日-hh:mm:ss");  
	        },1000);   
	    
		//alert(window.screen.height+"\n"+$("#mainDiv").outerHeight()+"\n"+$("#mainDiv").innerHeight());
		function getQueryString(name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
            var r = window.location.search.substr(1).match(reg);
            if (r != null) return unescape(r[2]); return null;
         }
		function locMain(target){
			$("#"+target).parent().children().removeClass("navBarActive").addClass("navBar");
			if($("#"+target).hasClass("navPBar")){
				$("#"+target).addClass("navBarActive");
				return;
			}
	        $("#"+target).addClass("navBarActive");
			$("#main").attr("src", target+".html");
			if($("#"+target).hasClass("navCBar")){
				return;
			}else{
				$("div.subBar").children().addClass("navBar").removeClass("navBarActive");
			}
		}
//若重新载入页面，则重定位iframe为查看数据页面
		//$('#main').attr('src', "monitor.html");
		//$('#main').attr('src', "EQInfo.html?EQID=1507620657614");
		//$('#main').attr('src', "stationEQInfo.html?EQID=1507620657614&SID=51PJD");
		function getClientHeight()
		{
		  var clientHeight=0;
		  if(document.body.clientHeight&&document.documentElement.clientHeight)
		  {
			  clientHeight = (document.body.clientHeight<document.documentElement.clientHeight)?document.body.clientHeight:document.documentElement.clientHeight;
		  }else{
			  clientHeight = (document.body.clientHeight>document.documentElement.clientHeight)?document.body.clientHeight:document.documentElement.clientHeight;
		  }
		  return clientHeight;
		}