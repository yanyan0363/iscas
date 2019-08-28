var stNumEle = document.getElementById("stNum");
var stInNumEle = document.getElementById("stInNum");
var eqNumEle = document.getElementById("eqNum");
var eqInNumEle = document.getElementById("eqInNum");

function initNums(){
	$.ajax({
		type:"get",
		url:"http://192.168.1.94:8090/GNSS/getIndexNums",
		dataType:'jsonp',
		jsonp:'callback',
		timeout:1000,
		success:function(msg){
			if(msg == undefined){
				alert("暂无数据");
				return;
			}
			stNumEle.innerHTML=msg.stNum;
			stInNumEle.innerHTML=msg.stInNum;
			eqNumEle.innerHTML=msg.eqNum;
			eqInNumEle.innerHTML=msg.eqInNum;
			setTimeout(initNums,2000);
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
//			alert("出错了，请联系管理员");
			setTimeout(initNums,2000);
		}
	});
}

setTimeout(initNums,1000);