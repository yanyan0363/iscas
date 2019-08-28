var reportNum=0;
var reportLength = 0;
tableHeight = parent.LBHeight;
if(tableHeight < 300){
	tableHeight = 300;
}
initReportsTable();
initStationsTable();

function initReportsTable(){
	$('#reportsTable').bootstrapTable({
		height:tableHeight,
		method:'get',
		url:'servlet/GetReportsServlet',
		striped:true,
		queryParams:'eqID='+eqID,
		cache:false,
		pagination:false,
		sortable:false,
		dataField: 'rows',
		// pageSize:3,
		showFooter:false,
		rowStyle:function rowStyle(row, index){
			return {
				css: {"font-size":"12px"}
			};
		},
		columns:[
			{
				field:'num',
				title:'序号',
				formatter:function (value, row, index){
					return "第"+value+"报";
				}
			},
			{
				field:'magnitude',
				title:'震级',
			},
			{
				field:'inTime',
				title:'时刻',
			}
			],
			onClickRow:function (row,tr){
				reportNum = row.num;//表示时报序号
				reportClick(reportNum,tr);
			},
			onLoadSuccess:function(){
				if(reportNum==undefined){
					return;
				}
				if(reportNum == 0){
					if($('#reportsTable tbody tr.no-records-found').length == 1){
						//alert("reportNum == 0 && " + "$('#reportsTable tbody tr.no-records-found').length == 1\n"+$('#reportsTable tbody tr.no-records-found').length);
						return;
					}else{
						//alert("reportNum == 0 && " + "$('#reportsTable tbody tr .no-records-found').length != 1\n"+$('#reportsTable tbody tr.no-records-found').length);
						reportNum = 1;
						//tr here 
						var tr = $("tr:contains('第"+reportNum+"报')");
						reportClick(reportNum,tr);
					}
				}
				
				$("tr:contains('第"+reportNum+"报')").addClass("active");
				//reports的数量发生变化时，刷新界面的初始化信息并刷新imgs
				if($('#reportsTable tbody tr').length == reportLength){
					return;
				}else{
					reportLength = $('#reportsTable tbody tr').length;
					initVisibleEQLayers(reportNum);
					initImgs();
				}
				//若stationsTable为空，则初始化
				if($('#stationsTable tbody tr.no-records-found').length == 1){
					initStationsTable();
				}
				
			}
	});
}
function reportClick(reportNum,tr){
	$("#reportsTable tr.active").removeClass("active");
	tr.addClass('active');
	$('#stationsTable').bootstrapTable("refresh",{
		url:'servlet/GetReportsStationsServlet?'+'eqID='+eqID+"&reportNum="+reportNum,
	});
	//根据时报序号刷新map
	refreshMap(reportNum);
	//根据时报序号刷新info
	refreshInfo(reportNum);
}
function initStationsTable(){
	$('#stationsTable').bootstrapTable({
		height:tableHeight,
		method:'get',
		url:'servlet/GetReportsStationsServlet',
		striped:true,
		queryParams:'eqID='+eqID+"&reportNum="+reportNum,
		cache:false,
		pagination:false,
		sortable:false,
		dataField: 'rows',
		pageSize:3,
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
				formatter:function (value, row, index){
					if(row.isFirst==1){
						return value+" <span style='color:red;'>(首台)</span>";
					}
					return value;
				}
			},
			{
				field:'magnitude',
				title:'震级',
			},
			{
				field:'dis',
				title:'震中距(km)',
			}
			],
			onClickRow:function (row,tr){
				$("#stationsTable tr.active").removeClass("active");
				tr.addClass('active');
				var station = row.station;//表示台站ID
				refreshChart(eqID,station);
			}
	});
}

function refreshInfo(reportNum){
	$.ajax({
		type:"post",
		url:"servlet/GetReportDetail",
		data:{
			eqID:eqID,
			reportNum:reportNum
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
			$("#rail").append('<div class="lines"><a>&nbsp</a></div>');
			for(var i=0; i<rail.length;i++){
				if(rail[i].name==" "){
					$("#rail").append('<div class="lines"><a onclick="railClick('+rail[i].OID+');">'+(i+1)+'. 铁路：未知名称</a></div>');
				}else{
					$("#rail").append('<div class="lines"><a onclick="railClick('+rail[i].OID+');">'+(i+1)+'. ' + rail[i].name+'</a></div>');
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
function railClick(OID){
	addRailShow(OID);
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
function initAlarm(){
	$.ajax({
		type:"post",
		url:"servlet/GetAlarmTime",
		data:{
			eqID:eqID
		},
		datatype:'json',
		success:function(msg){
			document.getElementById("alarm").append(msg+" 发布预警信息 ");
		}
	});
}
setInterval(function(){  
	 $('#reportsTable').bootstrapTable('refresh',{
		 url:'servlet/GetReportsServlet?eqID='+eqID,
		 silent:true
	 });
},2000);