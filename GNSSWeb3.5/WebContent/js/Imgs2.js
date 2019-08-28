function imgsClick(){
	initImgs();
}
//initVisibleEQLayers时，initImgs
function initImgs(){
	
	$.ajax({
		type:"post",
		url:"http://"+parent.serverIP+":8090/GNSS/GetEQImgs",
		data:{
			eqID:eqID,
		},
		dataType:'jsonp',//异步
		jsonp:'callback',
		success:function(msg){
			var imgsArray = new Array();
			if(msg == undefined || msg.length == 0){
				alert("当前地震暂无图件，请稍后查看。");
				return;
			}
			var arrows = msg.EQArrows;
			var imgsDiv = $("#imgsDiv");
			imgsDiv.empty();
			var k = 0;
			for(var i=0;i<arrows.length;i++){
				var imgLDiv;
				for(var j=0; j<arrows[i].imgs.length;j++,k++){
					if(k%4==0){
						imgLDiv = $("<div class='imgLDiv'></div>");
						imgsDiv.append(imgLDiv);
					}
					var imgDiv = $("<div class='imgDiv'></div>")
					var name = arrows[i].imgs[j].name;
					var strNote = "&nbsp;&nbsp;"+arrows[i].timeNode;
					
					if(name.indexOf("_H_")>=0 || name.indexOf("_h_")>=0){
						strNote+="&nbsp;&nbsp;水平方向&nbsp;&nbsp;";
					}else if(name.indexOf("_V_")>=0 || name.indexOf("_v_")>=0){
						strNote+="&nbsp;&nbsp;垂直方向&nbsp;&nbsp;";
					}
					strNote+="<a href='data:application/octet-stream;base64,"+arrows[i].imgs[j].content+"' download='"+name+".png'>点击下载</a>";
					var imgNote = $("<div class='imgNote'>"+strNote+"</div>");
					imgDiv.append(imgNote);
					var img = $("<img id='"+name+"'>");
					img.attr("src","data:image/png;base64,"+arrows[i].imgs[j].content);
					img.attr("title",name);
					img.attr("width", "200px");
					img.attr("height", "200px");
					img.attr("onclick", "openImg(this.src)");
					imgDiv.append(img);
					imgLDiv.append(imgDiv);
				}
			}
			//next contours
			var contours = msg.EQContours;
			var contoursImgsDiv = $("#contoursImgsDiv");
			contoursImgsDiv.empty();
			k = 0;
			for(var i=0;i<contours.length;i++){
				var imgLDiv;
				for(var j=0; j<contours[i].imgs.length;j++,k++){
					if(k%4==0){
						imgLDiv = $("<div class='imgLDiv'></div>");
						contoursImgsDiv.append(imgLDiv);
					}
					var imgDiv = $("<div class='imgDiv'></div>")
					var name = contours[i].imgs[j].name;
					var strNote = "&nbsp;&nbsp;"+contours[i].timeNode;
					
					if(name.indexOf("_H_")>=0 || name.indexOf("_h_")>=0){
						strNote+="&nbsp;&nbsp;水平方向&nbsp;&nbsp;";
					}else if(name.indexOf("_V_")>=0 || name.indexOf("_v_")>=0){
						strNote+="&nbsp;&nbsp;垂直方向&nbsp;&nbsp;";
					}
					strNote+="<a href='data:application/octet-stream;base64,"+contours[i].imgs[j].content+"' download='"+name+".png'>点击下载</a>";
					var imgNote = $("<div class='imgNote'>"+strNote+"</div>");
					imgDiv.append(imgNote);
					
					var img = $("<img id='"+name+"'>");
					img.attr("src","data:image/png;base64,"+contours[i].imgs[j].content);
					img.attr("title",name);
					img.attr("width", "200px");
					img.attr("height", "200px");
					img.attr("onclick", "openImg(this.src)");
					imgDiv.append(img);
					imgLDiv.append(imgDiv);
				}
			}
			
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			alert("获取当前地震的图片出错了，请联系管理员");
		}
	});
}
function openImg(src){
	var imf = "<div style='text-align:center'><img style='vertical-align:middle;' src='" + src + "'></img></div>"
	window.open('about:blank').document.body.innerHTML=imf;
}
/**
function initImgs(){
	$.ajax({
		type:"post",
		url:"http://"+parent.serverIP+":8090/GNSS/GetEQImgs",
		data:{
			eqID:eqID,
		},
		dataType:'jsonp',//异步
		jsonp:'callback',
		success:function(msg){
			var imgsArray = new Array();
			if(msg == undefined || msg.length == 0){
				return;
			}
			var imgsDiv = $("#imgsDiv");
			imgsDiv.empty();
			for(var i=0;i<msg.length;i++){
				var tNodeDiv = $("<div class='tNodeDiv'></div>");
				var titleDiv = $("<div class='titleDiv'>"+msg[i].timeNode+"节点</div>");
				tNodeDiv.append(titleDiv);
				var imgDiv;
				for(var j=0; j<msg[i].imgs.length;j++){
					if(j%2==0){
						imgDiv = $("<div class='imgDiv'></div>");
						tNodeDiv.append(imgDiv);
					}
					var name = msg[i].imgs[j].name;
					var img = $("<img id='"+name+"'>");
					img.attr("src","data:image/png;base64,"+msg[i].imgs[j].content);
					img.attr("width", "300px");
					imgDiv.append(img);
					var strNote = "生成时刻："+msg[i].timeNode+"<br>类型：";
					if(name.indexOf("Arrows")>=0){
						strNote+="振幅示意图<br>";
					}else if(name.indexOf("contours")>=0){
						strNote+="等值线图<br>";
					}
					if(name.indexOf("_H_")>=0 || name.indexOf("_h_")>=0){
						strNote+="方向：水平方向<br>";
					}else if(name.indexOf("_V_")>=0 || name.indexOf("_v_")>=0){
						strNote+="方向：垂直方向<br>";
					}
					strNote+="<a href='data:application/octet-stream;base64,"+msg[i].imgs[j].content+"' download='"+name+".png'>download</a>";
					var imgNote = $("<div class='imgNote'>"+strNote+"</div>");
					imgDiv.append(imgNote);
				}
				imgsDiv.append(tNodeDiv);
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			alert("获取当前地震的图片出错了，请联系管理员");
		}
	});
}
**/