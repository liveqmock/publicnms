/*******************************************************************************
 * netdetail.js
 * 
 * @Author: 聂林
 * 
 * @Date: 2010-12-10
 * 
 * @Function:
 * 该 js 的主要功能是完成网络设备详细信息页面的数据展示。
 * 当页面引入该 js 后，此 js 会在页面加载完成后自动执行 init() 方法来
 * 初始化页面的数据。
 *
 ******************************************************************************/


if (window.addEventListener) { 
	window.addEventListener("load", init, false); 
} else if (window.attachEvent) { 
	window.attachEvent("onload", init); 
} else { 
	window.onload = init; 
}


// 页面初始化
function init() { 
	//DWRUtil.useLoadingMessage("Loading"); 
	var nodeid = $("nodeid").value;
	var type = $("type").value;
	var subtype = $("subtype").value;
	var netDetail= new NetDetail(nodeid, type, subtype);
	netDetail.show();
}


function NetDetail(nodeid, type, subtype){ 
	
	// 设备id
	this.nodeid = nodeid;
	
	// 设备类型
	this.type = type;
	
	// 设备子类型
	this.subtype = subtype;
	
	// 设备
	this.hostNode = null;
	
	// 标签页列表
	this.tabList = null;
	
	// 当前选中的标签页(即页面上显示的标签页)
	this.currTab = null;
	
	// 页面中标签页所在的 DIV
	this.detailTabDIV = null;
	
	// 当前选中的标签页中标题
	this.titleList = null;
	
	// 页面中数据所在的 DIV
	this.detailDataDIV = null;
	
	// 页面中数据所在的 table
	this.detailDataTABLE = null;
	
	// tab 页 选中 时的 CSS
	this.tabSelectedClassName = "detail-data-title";
	
	// tab 页 未选中 时的 CSS
	this.tabUnSelectedClassName = "detail-data-title-out";
	
	// tab 页 鼠标移在 时的 CSS
	this.tabOnmouseoverClassName = "detail-data-title";
	
	// tab 页 鼠标移出 时的 CSS
	this.tabOnmouseoutClassName = "detail-data-title-out";
	
	// 获取项目所在路径
	this.rootPath = $('rootPath').value;
	
	// SWF 文件的版本
	this.SWFVersion = 8;
	
	// SWF 文件的底色
	this.SWFbackgroundclor = "#ffffff";
	
	// SWF 路径
	this.SWFPath = this.rootPath + "/flex/";
	
	// 当前创建的一个 SWF 类
	this.currCreateSWF = null;
	
	// 性能信息当中 响应时间 Response_time SWF 文件名
	this.responseTimeSWFName = "Response_time";
	
	// 性能信息当中 连通率 Ping SWF 文件名
	this.pingSWFName = "Ping";
	
	// 性能信息当中 CPU Line_CPU SWF 文件名
	this.Line_CPUSWFName = "Line_CPU";
	
	// 性能信息当中 流速 Area_flux SWF 文件名
	this.Area_fluxSWFName = "Area_flux";
	
	// 性能信息当中 内存 Net_Memory SWF 文件名
	this.Net_MemorySWFName = "Net_Memory";
	
	// 性能信息当中 闪存 Net_flash_Memory SWF 文件名
	this.Net_flash_MemorySWFName = "Net_flash_Memory";
	
	// 动力环境信息当中所有 SWF 文件都是一个 Envoriment_component SWF 文件名，只是参数不同
	this.Envoriment_componentSWFName = "Envoriment_component";
	
	// 动力环境信息当中 电源 SWF 文件 关键字 power
	this.Envoriment_componentPowerSWFkey = "power";
	
	// 动力环境信息当中 电压 SWF 文件 关键字 vol
	this.Envoriment_componentVolSWFkey = "vol";
	
	// 动力环境信息当中 风扇 SWF 文件 关键字 fan
	this.Envoriment_componentFanSWFkey = "fan";
	
	// 动力环境信息当中 温度 SWF 文件 关键字 temper
	this.Envoriment_componentTemperSWFkey = "temper";
	
	// NetDetail 类本身 该变量为此类的全局变量用于回调反转时方便
	ObjectSelf = this;
	
	
}

NetDetail.prototype = {
	
	
	// 使用  id , 以及 tagName 来创建一个element
	createElement: function (id , tagName){
		var element = document.createElement(tagName);
		element.id = id;
		return element;
	},
	
	// 用于展示详细信息页面的函数
	show: function (){
		
		// 获取详细信息页面上标签页所在的 div
		this.detailTabDIV = $('detailTabDIV');
		
		if(!this.detailTabDIV){
			//alert("请在页面中创建一个 id=detailTabDIV 的 div 用于存放标签页");
			return;
		}
		
		this.detailDataDIV = $('detailDataDIV');
		
		if(!this.detailDataDIV){
			alert("请在页面中创建一个 id=detailDataDIV 的 div 用于存放当前标签页的详细数据");
			return;
		}
		this.getTabInfo();
	},
	
	// 获取设备系统信息
	getSystemInfo: function (){
		NetRemoteService.getSystemInfo(this.nodeid, this.type, this.subtype, callbackgetSystemInfo);
	},
	
	// 获取设备系统信息后处理的回调函数
	callbackgetSystemInfo: function (data){ 
		alert(data);
	},
	
	// 获取详细信息页面上标签页的信息
	getTabInfo: function (){
		NetRemoteService.getTabInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				ObjectSelf.callbackgetTabInfo(data);
			}
		});
	},
	
	// 获取详细信息页面上标签页的信息后处理的回调函数
	callbackgetTabInfo: function (data){
		if(data && data.length > 0){
			this.tabList = data;
			// 创建详细信息页面上标签页
			this.createTabInfo();
		}
	},
	
	// 创建详细信息页面上标签页
	createTabInfo:function (){
		
		var tabTable = this.createElement("", "table");
		
		var tabTbody = this.createElement("", "tbody");
		tabTable.appendChild(tabTbody);
		
		var tr = this.createElement("", "tr")
		tabTbody.appendChild(tr);
		
		var td = this.createElement("", "td");
		td.width = this.tabList.length * 72;
		tr.appendChild(td);
		
		var table = this.createElement("", "table");
		td.appendChild(table);
		
		var tbody = this.createElement("", "tbody");
		table.appendChild(tbody);
		
		tr = this.createElement("", "tr")
		tbody.appendChild(tr);
		
		for(var i = 0; i < this.tabList.length; i++){
			// 循环生成标签页
			var tab = this.tabList[i];
			var tabTd = this.createElement(tab.id, "td");
			tabTd.className = this.getTabUnSelectedClassName();
			tabTd.onmouseover = function () {
				this.className = ObjectSelf.getTabOnmouseoverClassName();
			};
			tabTd.onmouseout = function () {
				this.className= ObjectSelf.getTabOnmouseoutClassName();
			};
			tabTd.onclick = (function (tab){
				return function(){
					ObjectSelf.onclickTab(tab);
				};
			})(tab);
			tabTd.insertAdjacentHTML("afterBegin", tab.name);
			tabTd.id = tab.id;
			tr.appendChild(tabTd);
		}
		this.detailTabDIV.appendChild(tabTable);
	},
	
	// 处理单击标签页的函数
	onclickTab: function (tab){
		this.currTab = tab;
		this.titleList = this.currTab.titleList;
		for(var i = 0 ; i < this.tabList.length; i++){
			var tab_per = this.tabList[i];
			$(tab_per.id).className = this.getTabUnSelectedClassName();
			$(tab_per.id).onmouseover = function () {
				this.className = ObjectSelf.getTabOnmouseoverClassName();
			};
			$(tab_per.id).onmouseout = function () {
				this.className= ObjectSelf.getTabOnmouseoutClassName();
			};
		}
		$(this.currTab.id).className = this.getTabSelectedClassName();
		$(this.currTab.id).onmouseover = function () {
			this.className = ObjectSelf.getTabOnmouseoverClassName();
		};
		$(this.currTab.id).onmouseout = function () {
			this.className = ObjectSelf.getTabOnmouseoverClassName();
		};
		this.removeAllChild(this.detailDataTABLE);
		this.createTitleInfo();
		eval("this." + tab.action + "()");
	},
	
	// 删除一个 HTMLElement 的节点的所有子节点
	removeAllChild: function(HTMLElement){
		if(HTMLElement){
			// 如果 HTMLElement 节点不为空
			while (HTMLElement.hasChildNodes()) {
				// 如果 HTMLElement 节点还有子节点则删除其第一个子节点
				HTMLElement.removeChild(HTMLElement.firstChild);
			}
		}
	},
	
	// 创建详细信息页面数据的标题表格
	createTitleInfo: function (titleList){
		if(titleList){
			this.titleList = titleList;
		}
		if(this.titleList && this.titleList.length > 0){
			// 如果获取的标题不为空并且个数大于 0
			if(!this.detailDataTABLE){
				// 如果详细信息页面数据的表格为 null
				this.detailDataTABLE = this.createElement("detailDataTABLE", "table");
				this.detailDataDIV.appendChild(this.detailDataTABLE);
			}
			var tbody = this.createElement("", "tbody");
			this.detailDataTABLE.appendChild(tbody);
			
			var titleTR = this.createElement("", "tr")
			tbody.appendChild(titleTR);
			
			for(var i = 0; i < this.titleList.length; i++){
				// 循环每一个表格标题
				var title = this.titleList[i];
				var titleTD = this.createElement("", "td");
				titleTD.className = "body-data-title";
				titleTD.height = 29;
				titleTD.insertAdjacentHTML("afterBegin", title.content);
				titleTR.appendChild(titleTD);
			}
			
		}
	},
	
	// 获取 tab 页 选中 时的 CSS
	getTabSelectedClassName: function(){
		return this.tabSelectedClassName;
	},
	
	// 获取 tab 页 未选中 时的 CSS
	getTabUnSelectedClassName: function(){
		return this.tabUnSelectedClassName;
	},
	
	// 获取 tab 页 鼠标移在 时的 CSS
	getTabOnmouseoverClassName: function(){
		return this.tabOnmouseoverClassName;
	},
	
	// 获取 tab 页 鼠标移出 时的 CSS
	getTabOnmouseoutClassName: function(){
		return this.tabOnmouseoutClassName;
	},
	
	// 获取设备流速信息
	A: function(){
		
	},
	
	// 获取设备性能信息
	getPerformaceInfo: function(){
		NetRemoteService.getHostNodeInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				ObjectSelf.callbackgetPerformaceInfo(data);
			}
		});
	},
	
	// 获取设备性能信息后处理的回调函数
	callbackgetPerformaceInfo: function(data){
		this.hostNode = data;
		this.createPerformaceInfo();
	},
	
	createPerformaceInfo: function(){
		if(!this.detailDataTABLE){
			// 如果详细信息页面数据的表格为 null
			this.detailDataTABLE = this.createElement("detailDataTABLE", "table");
			this.detailDataDIV.appendChild(this.detailDataTABLE);
		}

		var tbody = this.createElement("", "tbody");
		this.detailDataTABLE.appendChild(tbody);

		var tr = this.createElement("", "tr");
		tbody.appendChild(tr);
		
		var td = this.createElement("", "td");
		tr.appendChild(td);
		// 创建 responseTime 所在 table
		var responseTimePath = this.SWFPath + this.responseTimeSWFName + ".swf?ipadress=" + this.hostNode.ipAddress; 
		var responseTimeSWFTABLE = this.createSWFTABLE(td, responseTimePath, this.responseTimeSWFName, "346", "250", this.SWFVersion, this.SWFbackgroundclor);

		var td = this.createElement("", "td");
		tr.appendChild(td);
		// 创建 Ping 所在 table
		var pingSWFPath = this.SWFPath + this.pingSWFName + ".swf?ipadress=" + this.hostNode.ipAddress; 
		var pingSWFTABLE = this.createSWFTABLE(td, pingSWFPath, this.pingSWFName, "346", "250", this.SWFVersion, this.SWFbackgroundclor);
		
		if(this.hostNode.collecttype == 3 || this.hostNode.collecttype ==4 
		|| this.hostNode.collecttype == 8 || this.hostNode.collecttype == 9){
			return;			
		}
		
		// -------------------换一行-------------------------
		var tr = this.createElement("", "tr");
		tbody.appendChild(tr);
		
		var td = this.createElement("", "td");
		tr.appendChild(td);
		// 创建 Line_CPU 所在 table
		var Line_CPUPath = this.SWFPath + this.Line_CPUSWFName + ".swf?ipadress=" + this.hostNode.ipAddress; 
		var Line_CPUSWFTABLE = this.createSWFTABLE(td, Line_CPUPath, this.Line_CPUSWFName, "346", "250", this.SWFVersion, this.SWFbackgroundclor);
		
		var td = this.createElement("", "td");
		tr.appendChild(td);
		// 创建 Area_flux 所在 table
		var Area_fluxSWFPath = this.SWFPath + this.Area_fluxSWFName + ".swf?ipadress=" + this.hostNode.ipAddress; 
		var Area_fluxSWFTABLE = this.createSWFTABLE(td, Area_fluxSWFPath, this.Area_fluxSWFName, "346", "250", this.SWFVersion, this.SWFbackgroundclor);
		
		// -------------------换一行-------------------------
		var tr = this.createElement("", "tr");
		tbody.appendChild(tr);
		
		var td = this.createElement("", "td");
		tr.appendChild(td);
		// 创建 Net_Memory 所在 table
		var Net_MemoryPath = this.SWFPath + this.Net_MemorySWFName + ".swf?ipadress=" + this.hostNode.ipAddress; 
		var Net_MemorySWFTABLE = this.createSWFTABLE(td, Net_MemoryPath, this.Net_MemorySWFName, "346", "250", this.SWFVersion, this.SWFbackgroundclor);
		
		var td = this.createElement("", "td");
		tr.appendChild(td);
		// 创建 Net_flash_Memory 所在 table
		var Net_flash_MemorySWFPath = this.SWFPath + this.Net_flash_MemorySWFName + ".swf?ipadress=" + this.hostNode.ipAddress; 
		var Net_flash_MemorySWFTABLE = this.createSWFTABLE(td, Net_flash_MemorySWFPath, this.Net_flash_MemorySWFName, "346", "250", this.SWFVersion, this.SWFbackgroundclor);
		
		
	},
	
	// 获取设备 FDB 信息
	getFDBInfo: function (){
		
	},
	
	// 获取设备 ARP 信息
	getARPInfo: function (){
		alert("ARP");
	},
	
	// 获取设备动力环境信息 
	getPowerEnvironmentInfo: function(){
		NetRemoteService.getHostNodeInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				ObjectSelf.callbackgetPowerEnvironmentInfo(data);
			}
		});
	},
	
	// 获取设备性能信息后处理的回调函数
	callbackgetPowerEnvironmentInfo: function(data){
		this.hostNode = data;
		this.createPowerEnvironmentInfo();
	},
	
	createPowerEnvironmentInfo: function(){
		if(!this.detailDataTABLE){
			// 如果详细信息页面数据的表格为 null
			this.detailDataTABLE = this.createElement("detailDataTABLE", "table");
			this.detailDataDIV.appendChild(this.detailDataTABLE);
		}

		var tbody = this.createElement("", "tbody");
		this.detailDataTABLE.appendChild(tbody);

		var tr = this.createElement("", "tr");
		tbody.appendChild(tr);
		
		// 创建 电源 power 所在 td
		var td = this.createElement("", "td");
		tr.appendChild(td);
		// 创建 电源 power 所在 table
		var Envoriment_componentPowerPath = this.SWFPath + this.Envoriment_componentSWFName + ".swf?" +
				"ipadress=" + this.hostNode.ipAddress + 
				"&key=" + this.Envoriment_componentPowerSWFkey + 
				"&table=" + this.Envoriment_componentPowerSWFkey +  
				"&type=" + this.subtype+
				"&title=电源";  
		var Envoriment_componentPowerSWFName = this.Envoriment_componentSWFName +  this.Envoriment_componentPowerSWFkey;
		// 创建 电源 power SWF
		var Envoriment_componentPowerSWFTABLE = this.createSWFTABLE(td, Envoriment_componentPowerPath, Envoriment_componentPowerSWFName, "346", "250", this.SWFVersion, this.SWFbackgroundclor);
		
		// 创建 电压 vol 所在 td
		var td = this.createElement("", "td");
		tr.appendChild(td);
		// 创建 电压 vol 所在 table
		var Envoriment_componentVolSWFPath = this.SWFPath + this.Envoriment_componentSWFName + ".swf?" +
				"ipadress=" + this.hostNode.ipAddress + 
				"&key=" + this.Envoriment_componentVolSWFkey + 
				"&table=" + this.Envoriment_componentVolSWFkey +  
				"&type=" + this.subtype+
				"&title=电压"; 
		var Envoriment_componentVolSWFName = this.Envoriment_componentSWFName + this.Envoriment_componentVolSWFkey;
		// 创建 电压 vol SWF
		var Envoriment_componentVolSWFTABLE = this.createSWFTABLE(td, Envoriment_componentVolSWFPath, Envoriment_componentVolSWFName, "346", "250", this.SWFVersion, this.SWFbackgroundclor);
		
		if(this.hostNode.collecttype == 3 || this.hostNode.collecttype ==4 
		|| this.hostNode.collecttype == 8 || this.hostNode.collecttype == 9){
			return;			
		}
		
		// -------------------换一行-------------------------
		var tr = this.createElement("", "tr");
		tbody.appendChild(tr);
		
		// 创建 风扇 fan 所在 td
		var td = this.createElement("", "td");
		tr.appendChild(td);
		// 创建 风扇 fan 所在 table
		var Envoriment_componentFanrPath = this.SWFPath + this.Envoriment_componentSWFName + ".swf?" +
				"ipadress=" + this.hostNode.ipAddress + 
				"&key=" + this.Envoriment_componentFanSWFkey + 
				"&table=" + this.Envoriment_componentFanSWFkey +  
				"&type=" + this.subtype+
				"&title=风扇"; 
		var Envoriment_componentFanSWFName = this.Envoriment_componentSWFName +  this.Envoriment_componentFanSWFkey;
		// 创建 风扇 fan SWF
		var Envoriment_componentFanSWFTABLE = this.createSWFTABLE(td, Envoriment_componentFanrPath, Envoriment_componentFanSWFName, "346", "250", this.SWFVersion, this.SWFbackgroundclor);
		
		// 创建 温度 temper 所在 td
		var td = this.createElement("", "td");
		tr.appendChild(td);
		// 创建 温度 temper 所在 table
		var Envoriment_componentTemperSWFPath = this.SWFPath + this.Envoriment_componentSWFName + ".swf?" +
				"ipadress=" + this.hostNode.ipAddress + 
				"&key=" + this.Envoriment_componentTemperSWFkey + 
				"&table=" + this.Envoriment_componentTemperSWFkey +  
				"&type=" + this.subtype +
				"&title=温度"; 
		var Envoriment_componentTemperSWFName = this.Envoriment_componentSWFName + this.Envoriment_componentTemperSWFkey;
		// 创建 电压 vol SWF
		var Envoriment_componentTemperSWFTABLE = this.createSWFTABLE(td, Envoriment_componentTemperSWFPath, Envoriment_componentTemperSWFName, "346", "250", this.SWFVersion, this.SWFbackgroundclor);
		
		
	},
	
	
	
	// 创建 SWF 并放入到 table 中，然后将该 table 放入到容器 SWFContain 内，返回该 table
	// SWFContain       -- SWF 的文件容器，该容器主要是首先放置 table ， 然后放入 SWF ， 
	// 注意：该容器必须已经显示于页面或加入到页面中，否则显示 SWF 会抛出 找不到对象异常
	// SWFPath          -- SWF 的文件路径，该路径应该包含有 SWF 的文件所需的参数
	// name             -- SWF 的文件名称，该名称将用于 SWF 文件所在页面的 div 的 id，故需要唯一;
	// width            -- SWF 的文件宽度，
	// height           -- SWF 的文件版本，播放的falsh插件的版本，
	// backgroundclor   -- SWF 的文件底色，
	createSWFTABLE: function(SWFContain, SWFPath, name, width, height, version, backgroundclor){
		var SWFTABLE = this.createElement("", "table");
		SWFTABLE.style.textAlign = "center";
		SWFContain.appendChild(SWFTABLE);

		var tbody = this.createElement("", "tbody");
		SWFTABLE.appendChild(tbody);

		var tr = this.createElement("", "tr");
		tbody.appendChild(tr);

		var td = this.createElement("", "td");
		tr.appendChild(td);

		var SWFDIV = this.createElement(name, "div");
		td.appendChild(SWFDIV);
		
		SWFDIV.insertAdjacentHTML("afterBegin", "<strong>请安装版本8以上的 Flash 插件</strong>");

		var so = new SWFObject(SWFPath, name, width, height, version, backgroundclor);
		so.write(name);
		
		return SWFTABLE;
	}
}

function useLoadingMessage(message) { 
	var loadingMessage; 
	if (message) loadingMessage = message; 
	else loadingMessage = "Loading"; 
	DWREngine.setPreHook(function() {
		alert('aaaaa'); 
		var disabledZone = $('disabledZone'); 
		if (!disabledZone) { 
			disabledZone = document.createElement('div'); 
			disabledZone.setAttribute('id', 'disabledZone'); 
			disabledZone.style.position = "absolute"; 
			disabledZone.style.zIndex = "1000"; 
			disabledZone.style.left = "0px"; 
			disabledZone.style.top = "0px"; 
			disabledZone.style.width = "100%"; 
			disabledZone.style.height = "100%"; 
			document.body.appendChild(disabledZone); 
			var messageZone = document.createElement('div'); 
			messageZone.setAttribute('id', 'messageZone'); 
			messageZone.style.position = "absolute"; 
			messageZone.style.top = "0px"; 
			messageZone.style.right = "0px"; 
			messageZone.style.background = "red"; 
			messageZone.style.color = "white"; 
			messageZone.style.fontFamily = "Arial,Helvetica,sans-serif"; 
			messageZone.style.padding = "4px"; 
			disabledZone.appendChild(messageZone); 
			var text = document.createTextNode(loadingMessage); 
			messageZone.appendChild(text); 
		} else { 
			$('messageZone').innerHTML = loadingMessage; 
			disabledZone.style.visibility = 'visible'; 
		} 
	}); 
	DWREngine.setPostHook(function() { 
		alert('bbbb'); 
		$('disabledZone').style.visibility = 'hidden';
	}); 
}