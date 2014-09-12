
function setPort() {
	var dbType = $("#dbType").val();
	var port = $("#port");
	if (dbType == "MySQL") {
		port.attr("value", "3306");
		$("#server").hide();
	} else if (dbType == "Oracle") {
			port.attr("value", "1521");
			$("#server").hide();
	} else if (dbType == "SQL Server") {
				port.attr("value", "1433");
				$("#server").hide();
	} else if (dbType == "Sybase") {
		port.attr("value", "5000");
					$("#server").hide();
	} else if (dbType == "DB2") {
			port.attr("value", "50000");
	        $("#server").hide();
	}else if (dbType == "Informix") {
					port.attr("value", "9088");
					 $("#server").show();
	} 
	}		
function vertify() {
	var dbType = $("#dbType");
	var ip = $("#ip");
	var dbName = $("#dbName");
	var port = $("#port");
	var user = $("#user");
	var pwd = $("#pwd");
	var serverName = $("#serverName");
	
	$.ajax({type:"POST", 
	url:"../QueryServlet", 
	data:"dbType=" + dbType.val() + "&ip=" + ip.val() + "&dbName=" + dbName.val() + "&port=" + port.val() + "&user=" + user.val() + "&pwd=" + pwd.val() + "&serverName=" + serverName.val() + "&f=" + Math.random(),
	 success:callbackMsg
	 });
}
function callbackMsg(data) {
	var msg = $("#msg");
	msg.html(data);
}
function fade() {
	if ($("#table1").is(":hidden")) {
		$("#table1").fadeIn("slow");
		$("#show").val("\u9690 \u85cf");
	} else {
		$("#table1").fadeOut("slow");
		$("#show").val("\u663e \u793a");
	}
	$("#execute").attr("disabled", false);
}
function query() {
	var jqueryObj = $("#name");
	var name = jqueryObj.val();
	$.get("../tool/query.jsp?name=" + name + "&f=" + Math.random(), null, callback);
}
function querysql(path) {
	var jqueryObj = $("#name");
	var name = jqueryObj.val();
	if(name == ""){
		return;
	}
	//$.get("tool/query.jsp?name=" + name + "&f=" + Math.random(), null, callback);
	//使用iframe更改sql查询页面不能滚动的问题
	var frametestObj = document.getElementById("frametest");
	frametestObj.src = path+"/tool/query.jsp?name=" + name + "&f=" + Math.random();
}

function callback(data) {
	var resultobj = $("#result");
	resultobj.html(data);
}
function openNewWindow() {
	window.open("<%=path%>/query/home.jsp", "_blank", "toolbar=no, location=yes,directories=no, status=no, menubar=yes, scrollbars=yes, resizable=yes, copyhistory=yes, width=890, height=580");
}
var highlightcolor = "#c1ebff";
//此处clickcolor只能用win系统颜色代码才能成功,如果用#xxxxxx的代码就不行,还没搞清楚为什么:(
var clickcolor = "#51b2f6";
function changeto() {
	source = event.srcElement;
	if (source.tagName == "TR" || source.tagName == "TABLE") {
		return;
	}
	while (source.tagName != "TD") {
		source = source.parentElement;
	}
	source = source.parentElement;
	cs = source.children;
//alert(cs.length);
	if (cs[1].style.backgroundColor != highlightcolor && source.id != "nc" && cs[1].style.backgroundColor != clickcolor) {
		for (i = 0; i < cs.length; i++) {
			cs[i].style.backgroundColor = highlightcolor;
		}
	}
}
function changeback() {
	if (event.fromElement.contains(event.toElement) || source.contains(event.toElement) || source.id == "nc") {
		return;
	}
	if (event.toElement != source && cs[1].style.backgroundColor != clickcolor) {
//source.style.backgroundColor=originalcolor
	}
	for (i = 0; i < cs.length; i++) {
		cs[i].style.backgroundColor = "";
	}
}
function clickto() {
	source = event.srcElement;
	if (source.tagName == "TR" || source.tagName == "TABLE") {
		return;
	}
	while (source.tagName != "TD") {
		source = source.parentElement;
	}
	source = source.parentElement;
	cs = source.children;
//alert(cs.length);
	if (cs[1].style.backgroundColor != clickcolor && source.id != "nc") {
		for (i = 0; i < cs.length; i++) {
			cs[i].style.backgroundColor = clickcolor;
		}
	} else {
		for (i = 0; i < cs.length; i++) {
			cs[i].style.backgroundColor = "";
		}
	}
}

