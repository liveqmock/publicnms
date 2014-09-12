
/*
 * 作者:wangxiangyong
 * 脚本名称：网络拓扑图
 * 运行 showTopology(ajaxurl); 获得拓扑 ajaxurl 返回对象为NodeList结构的json对象文本
 * 
 */
 
//信息框样式
var infoBgColor = "#F5F5F5";
var infoBorder = "solid black 1px";
var infoBorderColor = "#003399";
var infoBorderWidth = 1;
var infoDelay = 250000;          // 允许延迟的时间
var infoFontColor = "#000000";
var infoFontFace = "\u5b8b\u4f53,arial,helvetica,sans-serif";
var infoFontSize = "12px";
var infoFontWeight = "normal";     // alternative: "bold";
var infoPadding = 3;            // spacing between border and content
var infoTitleColor = "#ffffff";    // color of caption text
var infoWidth = 180;
//获得画板
var mainBoard;
var mbWidth;
var mbHeight;
var mbLeft;
var mbTop;

//xml数据
var xmldoc;
var clickObj = null;//右键事件
var clickLineObj=null;
var tempArray = new Array();


//显示拓扑图
function showTopology(ajaxurl) {
	getBoard();//获得画板
	loadDataSrc(ajaxurl, analysisData);//获得远程数据
}
	
//获得画板
function getBoard() {
	mainBoard = document.getElementById("board");
	mainBoard.style.position = "absolute";
	mbWidth = mainBoard.style.width.replace("px", "") * 1;//画板宽度
	mbHeight = mainBoard.style.height.replace("px", "") * 1;//画板高度
	mbLeft = mainBoard.style.left.replace("px", "") * 1;//画板横坐标
	mbTop = mainBoard.style.top.replace("px", "") * 1;//画板纵坐标
}

//获得远程数据
function loadDataSrc(ajaxurl) {
	$.ajax({
	    url:ajaxurl,
	    type:"POST", 
	    dataType:"html",
	    timeout:250000,
	    data:null,
	    error:function () {
		   alert("连接错误，请稍后再试！");
	    }, 
	    success:function (html) {
		   analysisData(html);
	    }});
}
var obj;
//解析并显示节点数据
function analysisData(data) {
	 obj = eval("(" + data + ")");
	var i, j;
	var dragSet = "";
	if(obj.nodeList.length==0){
	alert("暂无数据，请稍后再试");
	return;
	}
	//创建所有节点
	for (i = 0; i < obj.nodeList.length; i++) {
		var node = obj.nodeList[i];
		createNode( node.id, node.name, node.url, node.deviceInfo,node.nodeMenuInfo);
		//dragSet = dragSet + "$('#" + node.id + "').drag();";
		
		
		var nodeDiv= document.getElementById("node_"+node.id);
		nodeDiv.style.left =node.x+"px";
		nodeDiv.style.top = node.y+"px";
		 dragSet=dragSet+"$('#node_"+ node.id+"').dragDrop({fixarea:[0,"+(mbWidth-32)+",0,"+(mbHeight-32)+"]});";
		tempArray.push("node_"+node.id);
	}
	 eval(dragSet);
     //初始化所有连线
	for (i = 0; i < obj.linkList.length; i++) {
		var link = obj.linkList[i];
		initLinkLine(link.id, link.from,link.to, link.linkInfo,link.linkStatus,link.linkWeight,link.linkMenuInfo);
		
	}

}
//初始化连线
function initLinkLine(id,fromId,toId,linkInfo,linkStatus,linkWeight,lineMenuInfo) {
	try {
	
	//画线
		var nodeObj_1 = document.getElementById("node_"+fromId);
		var nodeObj_2 = document.getElementById("node_"+toId);
		var obj1Width = nodeObj_1.style.width.replace("px", "") * 1;
		var obj1Height = nodeObj_1.style.height.replace("px", "") * 1;
		var obj1Left = nodeObj_1.style.left.replace("px", "") * 1;
		var obj1Top = nodeObj_1.style.top.replace("px", "") * 1;
		var obj2Width = nodeObj_2.style.width.replace("px", "") * 1;
		var obj2Height = nodeObj_2.style.height.replace("px", "") * 1;
		var obj2Left = nodeObj_2.style.left.replace("px", "") * 1;
		var obj2Top = nodeObj_2.style.top.replace("px", "") * 1;
		
		var line = document.createElement("v:line");
		line.lineid = id;
		line.id = id;
		line.style.position = "absolute";
		line.style.zIndex = 0;
		line.from = (parseInt(obj1Left) + parseInt(obj1Width) / 2) + "," + (parseInt(obj1Top) + parseInt(obj1Height) / 2);
		line.to = (parseInt(obj2Left) + parseInt(obj2Width) / 2) + "," + (parseInt(obj2Top) + parseInt(obj2Height) / 2);
		
		line.strokecolor = linkStatus;
		
		line.strokeweight = linkWeight;// 1;
		document.all.board.appendChild(line);
		//链路的提示信息
		
	  var divLineInfo = document.createElement("div");
		divLineInfo.id ="linkinfo_"+fromId+"_"+toId;
		divLineInfo.style.position = "absolute";
		divLineInfo.style.border = infoBorder;
		divLineInfo.style.width = 220;
		divLineInfo.style.height = "auto";
		divLineInfo.style.color = infoFontColor;
		divLineInfo.style.padding = infoPadding;
		divLineInfo.style.display = "block";
		divLineInfo.style.lineHeight = "120%";
		divLineInfo.style.zIndex = 2;
		divLineInfo.style.backgroundColor = infoBgColor;
		divLineInfo.style.visibility = "hidden";
		divLineInfo.style.fontSize = "12px";
		divLineInfo.innerHTML = linkInfo;
		document.all.board.appendChild(divLineInfo);
		document.all(line.id.replace("line","linkinfo")).style.left = parseInt(obj1Left)/2 + parseInt(obj1Width) /4+parseInt(obj2Left)/2 + parseInt(obj2Width) / 4;
		document.all(line.id.replace("line","linkinfo")).style.top = parseInt(obj1Top)/2 + parseInt(obj1Height) / 4+parseInt(obj2Top)/2 + parseInt(obj2Height) /4;
		var getCoordInDocument = function (e) {// 获取鼠标当前位置
			e = e || window.event;
			var x = e.pageX || (e.clientX + (document.getElementById("board").scrollLeft || document.getElementById("board").scrollLeft));
			var y = e.pageY || (e.clientY + (document.getElementById("board").scrollTop || document.getElementById("board").scrollTop));
			return {"x":x, "y":y};
		};
		line.onmousemove = function (e) {
			var pos = getCoordInDocument();
			window.event.srcElement.style.cursor = "hand";
			document.all(line.id.replace("line","linkinfo")).style.left = parseInt(pos["x"]);
			document.all(line.id.replace("line","linkinfo")).style.top = parseInt(pos["y"])-28;
			document.all(line.id.replace("line","linkinfo")).style.visibility = "visible";
			if(parseInt(pos["x"])> document.body.clientWidth-infoWidth-40){
				divLineInfo.style.left =parseInt(pos["x"])-infoWidth-40;
			} 
			if(parseInt(pos["y"]) > document.body.clientHeight-divLineInfo.clientHeight-20){
				divLineInfo.style.top =parseInt(pos["y"])-divLineInfo.clientHeight-20;  
		    }
			
		
		};
		line.onmouseout = function () {
			window.event.srcElement.style.cursor = "default";
			document.all(line.id.replace("line","linkinfo")).style.visibility = "hidden";
			
		};
		// 右键根据类型显示菜单
			var lineMenu = document.createElement("div");
			lineMenu.id = "menu_"+fromId+"_"+toId ;
			lineMenu.style.position = "absolute";
			lineMenu.style.width = 120;
			lineMenu.style.height = "auto";
			lineMenu.style.zIndex = 2;
			lineMenu.style.visibility = "hidden";
			lineMenu.style.border = "solid #000066 1px";
			lineMenu.style.backgroundColor = "#F5F5F5";
			lineMenu.style.padding = "1px";
			lineMenu.style.lineHeight = "100%";
			lineMenu.style.fontSize = "12px";
			lineMenu.innerHTML = lineMenuInfo;
			document.all.board.appendChild(lineMenu);	
				// 增加链路菜单的触发事件
			line.oncontextmenu = function()
			{ 
			    var pos = getCoordInDocument();
				document.all(lineMenu.id).style.left = parseInt(pos['x']);
			    document.all(lineMenu.id).style.top = parseInt(pos['y'])- 28;
				if(clickLineObj != null)
				{
					document.all(lineMenu.id).style.visibility = "hidden";
					clickLineObj = line;
				}
				clickLineObj = line;
				document.all(lineMenu.id).style.visibility = "visible"; 		
		    };
		    document.all("line_" +fromId+"_"+toId).onmousedown = down;
	}
	catch (e) {
	}
}


//添加节点信息
function createNode(nodeId,nodeName,imgUrl,deviceInfo,nodeMenuInfo) {
	//在画板上添加新的节点
	var objHtml = "<div onMouseOver=\"showInfo('" + nodeId + "')\" onMouseOut=\"hiddenInfo('" + nodeId + "')\" onMouseMove=\"drawLinkLine('" + nodeId + "')\" oncontextmenu=\"createMenu('" + nodeId + "')\" id='node_" + nodeId +"' format='0'  ></div>";
	mainBoard.innerHTML = mainBoard.innerHTML + objHtml;
	
	//创建所有的节点
	
	//获得新创建的节点对象
	var nodeObj = document.getElementById("node_"+nodeId);
	try {
		nodeObj.style.position = "absolute";
		nodeObj.style.top = "0px";
		nodeObj.style.left = "0px";
		nodeObj.style.zIndex = 1;
		nodeObj.style.width = "32px";
		nodeObj.style.height = "32px";
		nodeObj.innerHTML = "<img src=\"" + imgUrl + "\"/><div style='font-size:12px;align:left;'>"+nodeName+"</div>";
	}
	catch (e) {
	
		//alert(nodeObj);
	}
	//创建设备的提示信息
	var divInfo = document.createElement("div");
	divInfo.id = "info_" + nodeId;
	divInfo.name = "ssssssss";// alias+"("+ip+")";
	divInfo.style.position = "absolute";
	divInfo.style.border = infoBorder;
	divInfo.style.width = infoWidth;
	divInfo.style.height = "auto";
	divInfo.style.color = infoFontColor;
	divInfo.style.padding = infoPadding;
	divInfo.style.lineHeight = "120%";
	divInfo.style.zIndex = 5;
	divInfo.style.backgroundColor = infoBgColor;
	divInfo.style.left = parseInt(nodeObj.style.left.replace("px", ""), 10) + 32;
	divInfo.style.top = parseInt(nodeObj.style.top.replace("px", ""), 10);
	divInfo.style.visibility = "hidden";
	divInfo.style.fontSize = "12px";
	divInfo.innerHTML = deviceInfo;
	document.all.board.appendChild(divInfo);
	
	// 右键根据类型显示菜单

			var divMenu = document.createElement("div");
			divMenu.id = "menu_" + nodeId;
			divMenu.style.position = "absolute";
			divMenu.style.width = 120;
			divMenu.style.height = "auto";
			divMenu.style.zIndex = 2;
			//divMenu.style.left = parseInt(nodeObj.style.left.replace("px", ""), 10) + 28;
			//divMenu.style.top = parseInt(nodeObj.style.top.replace("px", ""), 10);
			divMenu.style.visibility = "hidden";
			divMenu.style.border = "solid #000066 1px";
			divMenu.style.backgroundColor = "#F5F5F5";
			divMenu.style.padding = "1px";
			divMenu.style.lineHeight = "100%";
			divMenu.style.fontSize = "12px";
			divMenu.innerHTML = nodeMenuInfo;
			document.all.board.appendChild(divMenu);		
			
}

//显示设备信息
function showInfo(nodeId) {
	var nodeObj = document.getElementById("node_"+nodeId);
	
	document.all("info_" + nodeId).style.left = parseInt(nodeObj.style.left.replace("px", ""), 10) + 52;
	document.all("info_" + nodeId).style.top = parseInt(nodeObj.style.top.replace("px", ""), 10) + 10;
	document.all("info_" + nodeId).style.visibility = "visible";
	var getCoordInDocument = function (e) {// 获取鼠标当前位置
			e = e || window.event;
			var x = e.pageX || (e.clientX + (document.getElementById("board").scrollLeft || document.getElementById("board").scrollLeft));
			var y = e.pageY || (e.clientY + (document.getElementById("board").scrollTop || document.getElementById("board").scrollTop));
			return {"x":x, "y":y};
		};
		var pos = getCoordInDocument();
			if(parseInt(pos["x"])> document.body.clientWidth-infoWidth-40){
				document.all("info_" + nodeId).style.left =parseInt(pos["x"])-infoWidth-40;
			} 
			if(parseInt(pos["y"]) > document.body.clientHeight-nodeObj.clientHeight-20){
				document.all("info_" + nodeId).style.top =parseInt(pos["y"])-document.all("info_" + nodeId).clientHeight-20;  
		    }
}
function hiddenInfo(nodeId) {
nodeId=eval(nodeId);
	document.all("info_" + nodeId).style.visibility = "hidden";
}
//创建菜单
function createMenu(nodeId){
var nodeObj=document.getElementById("node_"+nodeId);
var divMenu=document.getElementById("menu_"+nodeId);
	divMenu.style.left = parseInt(nodeObj.style.left.replace("px", ""), 10) + 28;
	divMenu.style.top = parseInt(nodeObj.style.top.replace("px", ""), 10);
	     if(clickObj != null)
		      {
					document.all(nodeObj.id.replace("node", "menu")).style.visibility = "hidden";
					clickObj = null;
				}	
				document.all(nodeObj.id.replace("node", "info")).style.visibility = "hidden";
				document.all(nodeObj.id.replace("node", "menu")).style.visibility = "visible"; 	
					
		      clickObj = nodeObj;
			document.all("node_" + nodeId).onmousedown = down;
}
//处理鼠标键按下事件
function down()
{

	if (clickObj != null)
	{ // 隐藏右键单击图标后的菜单
		document.all(clickObj.id.replace("node", "menu")).style.visibility = "hidden";
		clickObj = null;
		
	}
	if (clickLineObj != null )
	{
		document.all(clickLineObj.id.replace("line", "menu")).style.visibility = "hidden"; 
		clickLineObj = null;
	}

	
}

//画连接节点的层
function drawLinkLine(nodeId) {
	try {
	for (i = 0; i < obj.linkList.length; i++) {
		var link = obj.linkList[i];
		if(link.from==nodeId||link.to==nodeId){
		//变更与父结点之间的关系
		
		var nodeObj_1 = document.getElementById("node_"+link.from);
		var nodeObj_2 = document.getElementById("node_"+link.to);
		var obj1Width = nodeObj_1.style.width.replace("px", "") * 1;
		var obj1Height = nodeObj_1.style.height.replace("px", "") * 1;
		var obj1Left = nodeObj_1.style.left.replace("px", "") * 1;
		var obj1Top = nodeObj_1.style.top.replace("px", "") * 1;
		var obj2Width = nodeObj_2.style.width.replace("px", "") * 1;
		var obj2Height = nodeObj_2.style.height.replace("px", "") * 1;
		var obj2Left = nodeObj_2.style.left.replace("px", "") * 1;
		var obj2Top = nodeObj_2.style.top.replace("px", "") * 1;
		//if(obj2Left>900||obj1Left<12) $("#"+nodeId).jqResize();
		var line = document.getElementById(link.id);
		line.from = (parseInt(obj1Left) + parseInt(obj1Width) / 2) + "," + (parseInt(obj1Top) + parseInt(obj1Height) / 2);
		line.to = (parseInt(obj2Left) + parseInt(obj2Width) / 2) + "," + (parseInt(obj2Top) + parseInt(obj2Height) / 2);
		var ids=link.id.replace("line","linkinfo");
	    document.all(ids).style.left = parseInt(obj1Left)/2 + parseInt(obj1Width) /4+parseInt(obj2Left)/2 + parseInt(obj2Width) / 4;
		document.all(ids).style.top = parseInt(obj1Top)/2 + parseInt(obj1Height) / 4+parseInt(obj2Top)/2 + parseInt(obj2Height) /4;
			
		}
	
	}
	
	
	}
	catch (e) {
	}
}



//初始化
function initXML(url)
{
	var http = new ActiveXObject("Microsoft.XMLHTTP");
	xmldoc = new ActiveXObject("Microsoft.XMLDOM");
	http.open("POST", url, false);
	http.send();
	xmldoc.async = false;
	xmldoc.loadXML(http.responseText);
	maxWidth = maxHeight = minWidth = minHeight = 0;

	return xmldoc;
}
 //保存拓扑图
function save(rootPath,type)
{
var url=rootPath+"/resource/xml/"+type+".jsp";
	xmldoc=initXML(url);
	var nodes = xmldoc.getElementsByTagName("node");
	if(tempArray.length==0){
	  alert("无法保存，暂无数据！");
	  return;
	}
	for (var i = 0; i < tempArray.length; i += 1)
	{
		for (var j = 0; j < nodes.length; j += 1)
		{
			var node = nodes[j];
			var id = node.getElementsByTagName("id")[0].text;
             id="node_"+id;
			if (tempArray[i] == id)
			{
			
				node.getElementsByTagName("x")[0].text = document.getElementById(tempArray[i]).style.left;
				node.getElementsByTagName("y")[0].text =  document.getElementById(tempArray[i]).style.top;
			}
		}
	}
	
	saveData(rootPath,type,xmldoc.xml);
}
//保存拓扑图数据
function saveData(rootPath,type,dataxml){
var ajaxurl=rootPath+"/serviceQualityAjaxManager.ajax?action=saveData"
	$.ajax({
	    url:ajaxurl,
	    type:"POST", 
	    dataType:"json",
	    timeout:25000,
	    data:"data="+dataxml+"&slatype="+type,
	    error:function () {
		   alert("暂时无法连通，请稍后再试！");
	    }, 
	    success:function (data) {
		  alert(data.flag)
	    }});
}
 function createWindow(url){
	    window.open(url,"protypeWindow","toolbar=no,width=650,height=380,left=180,top=150,directories=no,status=no,scrollbars=no,menubar=no")
 } 
 function createAuitWindow(url){
	    window.open(url,"protypeWindow","toolbar=no,width=850,height=480,left=100,top=100,directories=no,status=no,scrollbars=no,menubar=no")
 } 
 