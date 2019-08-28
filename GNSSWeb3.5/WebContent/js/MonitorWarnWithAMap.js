$('#stationsTbl').bootstrapTable({
	height:180,
	silent:true,
	rowStyle:function rowStyle(row, index){
		return {
			css: {"font-size":"12px"}
		};
	},
	columns:[
	{
		field:'station',
		title:'台站&nbsp;&nbsp;',
	},
	{
		field:'PT',
		title:'P波时刻',
	},
	{
		field:'MEMSMag',
		title:'触发震级',
	},
	{
		field:'EpiDis',
		title:'震中距离(km)'
	}
	],
	onClickRow:function (row){
		showLineCharts(row.station);
	}
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
			title:'台站',
		},
		{
			field:'M',
			title:'震级',
		}
		]
});
$('#EQsTbl').bootstrapTable({
	height:620,
	method:'get',
	url:'servlet/GetEQListServlet',
	striped:true,
	cache:false,
	pagination:false,
	sidePagination:"server",
	sortable:false,
	sortOrder:'desc',
	pageSize:10,
	dataField: 'rows',
	paginationLoop:false,
	rowStyle:function rowStyle(row, index){
		return {
			css: {"font-size":"14px"}
		};
	},
	columns:[
		{
			field:'originTime',
			title:'时间',
		},
		{
			field:'MEMSMag',
			title:'MEMS震级',
		},
		{
			field:'GPSMag',
			title:'GPS震级',
		},
		{
			field:'firstStation',
			title:'首台',
		}
		],
		onClickCell: function (field, value, row, $element){
			location.href = "./EQInfo.html?EQID="+row.EQID;
		}
});
var curState = document.getElementById("curState");
var firstSt = document.getElementById("firstSt");
var eqTime = document.getElementById("eqTime");
var curInfo = document.getElementById("curInfo");
var curInfoState = document.getElementById("curInfoState");
var curInfoTime = document.getElementById("curInfoTime");
var curInfoEpic = document.getElementById("curInfoEpic");
var curInfoGPSMag = document.getElementById("curInfoGPSMag");
var curInfoMEMSMag = document.getElementById("curInfoMEMSMag");
var rps = document.getElementById("rps");
var stMsDiv = document.getElementById("stMsDiv");
var stMsNum = document.getElementById("stMsNum");
var NoEQ = document.getElementById("NoEQ");
var curEQ = document.getElementById("curEQ");
var msgRecord;
function monitorEQ(){
	var graphics = map.graphics.graphics;
	$.ajax({
		type:"get",
		url:"http://"+parent.serverIP+":8090/GNSS/monitorEQEvents",
		dataType:'jsonp',//异步
		jsonp:'callback',
		success:function(msg){
			msgRecord = msg;
			if(msg==null || msg=="[]" || msg.length==0){
				curState.innerHTML="当前没有地震发生";
				curInfo.style.display="none";
				timeBox.style.display="block";
				curEQ.style.display = "none";
				NoEQ.style.display = "block";
				firstSt.innerHTML="";
	        	curInfoTime.innerHTML="";
	        	curInfoEpic.innerHTML="";
	        	curInfoGPSMag.innerHTML="";
	        	curInfoMEMSMag.innerHTML="";
	        	rps.innerHTML="";
	        	$("#stationsTbl").bootstrapTable('removeAll');
	        	$("#stMsTbl").bootstrapTable('removeAll');
	        	closeStMsDiv();
	        	for(var j=0;j<graphics.length;j++){
	        		if(graphics[j].attributes!=undefined &&
	        				(graphics[j].attributes.type=="EQPoint"||graphics[j].attributes.type=="EQPolygon")){
	        			map.graphics.remove(graphics[j]);
	        		}
	        	}
				return;
			}
			NoEQ.style.display = "none";
			curEQ.style.display = "block";
			for(var i = 0; i < msg.length; i++){
				var reFlag = true;
				for(var j=0;j<graphics.length;j++){
	        		if(graphics[j].attributes!=undefined 
	        				&& (graphics[j].attributes.type=="EQPoint"||graphics[j].attributes.type=="EQPolygon")){
	        			if(graphics[j].attributes.ID==msg[i].EQID && graphics[j].X == msg[i].lon && graphics[j].Y == msg[i].lat){
	        				reFlag = false;
	        				continue;
	        			}else{
	        				map.graphics.remove(graphics[j]);
	        				map.graphics.redraw();
	        			}
	        		}
	        	}
				if(reFlag){
					//地图上添加EQPoint
//					var eqPoint = new esri.geometry.Point(msg[i].lon,msg[i].lat,map.spatialReference);
					require(["esri/geometry/Point", "esri/SpatialReference", "esri/geometry/webMercatorUtils"], function(Point, SpatialReference, webMercatorUtils) {
						 var point = new esri.geometry.Point(msg[i].lon,msg[i].lat,new SpatialReference({wkid:parseInt(4326)}));//这里面需要定义输入点的坐标系，不写默认会是4326
						 var eqPoint = webMercatorUtils.geographicToWebMercator(point);
						 var symbol = new esri.symbol.PictureMarkerSymbol("img/ep.png", 32, 32);
						 var attr = {"ID":msg[i].EQID,"type":"EQPoint"};
						 var graphic = new esri.Graphic(eqPoint, symbol, attr);
						 map.graphics.add(graphic);
						 var arsLen=msg[i].ARs.length;
						 //地图上添加EQPolygon
						 var gr = map.graphics.graphics;
						 for(var k=0;k<gr.length;k++){
							 if(msg[i].ARs[arsLen-1].station == gr[k].attributes.ID){
								 var x = eqPoint.x-gr[k].geometry.x;
								 var y = eqPoint.y-gr[k].geometry.y;
								 var radP = Math.sqrt(x*x+y*y);
//								 var radP = msg[i].ARs[arsLen-1].EpiDis*1000;//单位由km转换为m,经图中显示验证，上一种方法计算radP显示效果更好
//								 alert(radP+'\n'+msg[i].ARs[arsLen-1].EpiDis*1000);
								 if(radP > 0){
									 drawPCircle(msg[i].EQID,eqPoint,radP);
								 }
								 break;
							 }
						 }
					});
				}
	        	//刷新左侧当前地震信息
	        	curState.innerHTML="当前发生地震";
	        	timeBox.style.display="none";
	        	curInfo.style.display="block"
//	        	firstSt.innerHTML="首台："+msg[i].firstSt;
//	        	curInfoTime.innerHTML="发震时刻：" + msg[i].eqTime;
//	        	curInfoEpic.innerHTML="震中位置："+msg[i].lon+"E "+msg[i].lat+"N";
//	        	curInfoM.innerHTML="震级："+msg[i].M;
	        	firstSt.innerHTML=msg[i].firstSt;
	        	curInfoTime.innerHTML=msg[i].eqTime;
	        	curInfoEpic.innerHTML=msg[i].lon+"E "+msg[i].lat+"N";
	        	curInfoGPSMag.innerHTML=msg[i].GPSMag;
	        	curInfoMEMSMag.innerHTML=msg[i].MEMSMag;
	        	//刷新速报信息
	        	var rs = rps.children.length;
	        	for(var j = 0; j < msg[i].ARs.length; j++){
	        		if(rs >= j+1){
	        			continue;
	        		}
	        		var ar = msg[i].ARs[j];
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
//	        			alert(this.className+"\n"+this.firstChild.childNodes[1].innerHTML);
//	        			stMsDiv.style.left=e.clientX+"px";
//	        			stMsDiv.style.top=e.clientY+"px";
	        			stMsDiv.style.display="block";
//	        			alert(e.clientX+", "+e.clientY);
	        			var nn = this.firstChild.childNodes[1].innerHTML;
	        			stMsNum.innerHTML="第"+nn+"报";
	        			var aa = msgRecord[i-1].ARs[nn-1];
	        			$("#stMsTbl").bootstrapTable('load', aa.stMs);
	        		};
	        		
	        		var btR = document.createElement("div");
	        		btR.className ="btR";
	        		btR.innerHTML="第"+"<span>"+(j+1)+"</span>报";
	        		rpRight.appendChild(btR);
	        		var rpDiv = document.createElement("div");
	        		rpDiv.className="glines rpB";
	        		var epiP = document.createElement("div");
	        		epiP.className = "rpLine";
	        		epiP.innerHTML="<span class='tBold'>震中：</span>"+ar.EpiLon+"E "+ ar.EpiLat + "N";
	        		rpDiv.appendChild(epiP);
	        		var PTP = document.createElement("div");
	        		PTP.className = "rpLine";
	        		PTP.innerHTML="<span class='tBold'>时间：</span>"+ar.PT;
	        		rpDiv.appendChild(PTP);
	        		var MP = document.createElement("div");
	        		MP.className = "rpLine";
	        		MP.innerHTML="<span class='tBold'>GPS震级：</span>"+ar.GPSMag+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='tBold'>MEMS震级：</span>"+ar.MEMSMag ;
	        		rpDiv.appendChild(MP);
	        		var stP = document.createElement("div");
	        		stP.className = "rpLine";
	        		stP.innerHTML="<span class='tBold'>台站：</span>"+ar.station;
	        		rpDiv.appendChild(stP);
	        		
//	        		var STP = document.createElement("div");
//	        		STP.className = "rpLine";
//	        		STP.innerHTML="台站："+ar.station;
//	        		rpDiv.appendChild(STP);
	        		rpRight.appendChild(rpDiv);
	        		rp.appendChild(rpRight);
	        		rps.appendChild(rp);
	        		$("#stationsTbl").bootstrapTable('append', ar);
	        		rps = document.getElementById("rps");
	        		rps.scrollTop = rps.scrollHeight;//滚动条置底
	        	}
	        	//刷新台站表
//	        	$("#stationsTbl").bootstrapTable('removeAll');
//	        	$("#stationsTbl").bootstrapTable('load', msg[i].ARs);
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
		}
	});
}
function closeStMsDiv(){
	$("#stMsTbl").bootstrapTable('removeAll');
	stMsDiv.style.display="none";
}
var symbolP;
function drawPCircle(EQID,eqCenter,radP){
//	var radP = 1;
	require(["esri/geometry/Circle","esri/graphic","esri/units", "esri/symbols/SimpleFillSymbol", "esri/symbols/SimpleLineSymbol","esri/Color"], 
			function(Circle,Graphic,Units, SimpleFillSymbol,SimpleLineSymbol,Color) {
		if(symbolP == undefined){
			symbolP = new SimpleFillSymbol(SimpleLineSymbol.STYLE_SOLID,new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID,
				    new Color([255,0,0]), 1),new Color([255,255,0,0.25]));
		}
		var circleP = new Circle(eqCenter,{
			  radius: radP,
			  radiusUnit: Units.METERS,//Units.DECIMAL_DEGREES,
			  numberOfPoints:720
		 });
		var attr = {"ID":EQID,"type":"EQPolygon"};
		var ppGraphic = new Graphic(circleP,symbolP,attr); 
		map.graphics.add(ppGraphic);
		map.graphics.redraw();
	});
}
var timeBox=document.getElementById("time");  
setInterval(function(){  
	          timeBox.innerHTML=new Date().Format("yyyy年MM月dd日-hh:mm:ss");  
	        },1000); 
setInterval(function(){  
	 $('#EQsTbl').bootstrapTable('refresh',{
		 url:'servlet/GetEQListServlet',
		 silent:true
	 });
},60000);
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