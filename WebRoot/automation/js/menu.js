
var show = true;
var hide = false;
			//修改菜单的上下箭头符号
function my_on(head, body) {
	var tag_a;
	for (var i = 0; i < head.childNodes.length; i++) {
		if (head.childNodes[i].nodeName == "A") {
			tag_a = head.childNodes[i];
			break;
		}
	}
	tag_a.className = "on";
}
function my_off(head, body) {
	var tag_a;
	for (var i = 0; i < head.childNodes.length; i++) {
		if (head.childNodes[i].nodeName == "A") {
			tag_a = head.childNodes[i];
			break;
		}
	}
	tag_a.className = "off";
}
			//添加菜单	
function initmenu() {
	var idpattern = new RegExp("^menu");
	var menupattern = new RegExp("child$");
	var tds = document.getElementsByTagName("div");
	for (var i = 0, j = tds.length; i < j; i++) {
		var td = tds[i];
		if (idpattern.test(td.id) && !menupattern.test(td.id)) {
			menu = new Menu(td.id, td.id + "child", "dtu", "100", show, my_on, my_off);
			menu.init();
		}
	}
}
			
			
			//公共变量
var node = "";
var ipaddress = "";
var operate = "";
/**
			*根据传入的id显示右键菜单
			*/
function showMenu(id, nodeid, ip) {
	ipaddress = ip;
	node = nodeid;
				//operate=oper;
	if ("" == id) {
		popMenu(itemMenu, 100, "100");
	} else {
		popMenu(itemMenu, 100, "11111");
	}
	event.returnValue = false;
	event.cancelBubble = true;
	return false;
}
/**
			*显示弹出菜单
			*menuDiv:右键菜单的内容
			*width:行显示的宽度
			*rowControlString:行控制字符串，0表示不显示，1表示显示，如“101”，则表示第1、3行显示，第2行不显示
			*/
function popMenu(menuDiv, width, rowControlString) {
			    //创建弹出菜单
	var pop = window.createPopup();
			    //设置弹出菜单的内容
	pop.document.body.innerHTML = menuDiv.innerHTML;
	var rowObjs = pop.document.body.all[0].rows;
			    //获得弹出菜单的行数
	var rowCount = rowObjs.length;
			    //alert("rowCount==>"+rowCount+",rowControlString==>"+rowControlString);
			    //循环设置每行的属性
	for (var i = 0; i < rowObjs.length; i++) {
			        //如果设置该行不显示，则行数减一
		var hide = rowControlString.charAt(i) != "1";
		if (hide) {
			rowCount--;
		}
			        //设置是否显示该行
		rowObjs[i].style.display = (hide) ? "none" : "";
			        //设置鼠标滑入该行时的效果
		rowObjs[i].cells[0].onmouseover = function () {
			this.style.background = "#397DBD";
			this.style.color = "white";
		};
			        //设置鼠标滑出该行时的效果
		rowObjs[i].cells[0].onmouseout = function () {
			this.style.background = "#F1F1F1";
			this.style.color = "black";
		};
	}
			    //屏蔽菜单的菜单
	pop.document.oncontextmenu = function () {
		return false;
	};
			    //选择右键菜单的一项后，菜单隐藏
	pop.document.onclick = function () {
		pop.hide();
	};
			    //显示菜单
	pop.show(event.clientX - 1, event.clientY, width, rowCount * 25, document.body);
	return true;
}

