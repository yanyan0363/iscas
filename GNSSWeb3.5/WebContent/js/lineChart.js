var lineChart = document.getElementById("lineChartDiv");
lineChart.style.height=screen.height*0.2+'px';
lineChart.style.width=screen.width*0.43+'px';
var cchart = echarts.init(lineChart);
var option = {
		title: {
			show:false,
			text:'水平位移',
			textAlign:'right',
			right:0,
			top:0
		},
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
	            name:'水平位移',
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
cchart.setOption(option);

function refreshChart(eqid,station){
		$.ajax({
			type:"post",
			url:"servlet/GetMagByEQStation",
			data:{
				eqID:eqid,
				station:station
			},
			datatype:'json',
			success:function(msg){
				var mm = eval(msg);
				var labels = mm[1];
				var m = mm[0]+"";
				var mData = m.split(",");
				cchart.setOption({
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