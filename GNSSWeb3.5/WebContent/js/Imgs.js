function imgsClick(){
	initImgs();
}
//initVisibleEQLayers时，initImgs
function initImgs(){
	
	$.ajax({
		type:"post",
//		url:"http://"+parent.serverIP+":8090/GNSS/GetEQImgs",
		url:"servlet/GetEQImgsServlet",
		data:{
			eqID:eqID,
		},
		dataType:'json',
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
					strNote+="<a href='http://"+parent.serverIP+":8080/GNSSEQImg/"+eqID+"/img/"+name+".png' download='"+name+".png'>点击下载</a>";
					var imgNote = $("<div class='imgNote'>"+strNote+"</div>");
					imgDiv.append(imgNote);
					var img = $("<img id='"+name+"'>");
					img.attr("src","http://"+parent.serverIP+":8080/GNSSEQImg/"+eqID+"/img/"+name+".png");
					img.attr("title",name);
					img.attr("width", "200px");
					img.attr("height", "200px");
					img.attr("onclick", "openImg(this.src)");
					imgDiv.append(img);
					imgLDiv.append(imgDiv);
				}
			}
			//next EQArrowsGif
			var EQArrowsGif = msg.EQArrowsGif;
			for(var i=0;i<EQArrowsGif.length;i++,k++){
				var imgLDiv;
				if(k%4==0){
					imgLDiv = $("<div class='imgLDiv'></div>");
					imgsDiv.append(imgLDiv);
				}
				var imgDiv = $("<div class='imgDiv'></div>")
				var name = EQArrowsGif[i].name;
				var strNote = "&nbsp;&nbsp;";
				
				if(name.indexOf("_H")>=0 || name.indexOf("_h")>=0){
					strNote+="&nbsp;&nbsp;水平方向gif&nbsp;&nbsp;";
				}else if(name.indexOf("_V")>=0 || name.indexOf("_v")>=0){
					strNote+="&nbsp;&nbsp;垂直方向gif&nbsp;&nbsp;";
				}
				strNote+="<a href='http://"+parent.serverIP+":8080/GNSSEQImg/"+eqID+"/img/"+name+".gif' download='"+name+".gif'>点击下载</a>";
				var imgNote = $("<div class='imgNote'>"+strNote+"</div>");
				imgDiv.append(imgNote);
				
				var img = $("<img id='"+name+"'>");
				img.attr("src","http://"+parent.serverIP+":8080/GNSSEQImg/"+eqID+"/img/"+name+".gif");
				img.attr("title",name);
				img.attr("width", "200px");
				img.attr("height", "200px");
				img.attr("onclick", "openImg(this.src)");
				imgDiv.append(img);
				imgLDiv.append(imgDiv);
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
					strNote+="<a href='http://"+parent.serverIP+":8080/GNSSEQImg/"+eqID+"/img/"+name+".png' download='"+name+".png'>点击下载</a>";
					var imgNote = $("<div class='imgNote'>"+strNote+"</div>");
					imgDiv.append(imgNote);
					
					var img = $("<img id='"+name+"'>");
					img.attr("src","http://"+parent.serverIP+":8080/GNSSEQImg/"+eqID+"/img/"+name+".png");
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
