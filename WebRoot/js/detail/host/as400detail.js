/*******************************************************************************
 * netdetail.js
 * 
 * @Author: 聂林
 * 
 * @Date: 2010-12-10
 * 
 * @Function:
 * 该 js 的主要功能是完成服务器设备详细信息页面的数据展示。
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
	useLoadingMessage("Loading"); 
	var nodeid = $("nodeid").value;
	var type = $("type").value;
	var subtype = $("subtype").value;
	var hostDetail= new HostDetail(nodeid, type, subtype);
	hostDetail.show();
}


function HostDetail(nodeid, type, subtype){ 
	
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
	
	// 系统信息当中 可利用率 SWF 文件名
	this.Pie_ComponentSWFName = "Pie_Component";
	
	// 系统信息当中 CPU SWF 文件名
	this.DHCCGaugeSWFName = "DHCCGauge";
	
	// 性能信息当中 响应时间 Response_time SWF 文件名
	this.responseTimeSWFName = "Response_time";
	
	// 性能信息当中 连通率 Ping SWF 文件名
	this.pingSWFName = "Ping";
	
	// 性能信息当中 CPU Line_CPU SWF 文件名
	this.Line_CPUSWFName = "Line_CPU";
	
	// 性能信息当中 内存 Area_Memory SWF 文件名
	this.Area_MemorySWFName = "Area_Memory";
	
	// 详细信息中 数据列表中 排列的顺序 
	// 递增 -- asc
	// 递减 -- desc
	this.dataTROrder = null;
	
	// 从后台异步获取的数据
	this.data = null;
		
	// HostDetail 类本身 该变量为此类的全局变量用于回调反转时方便
	ObjectSelf = this;
	
	
}

HostDetail.prototype = {
	
	
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
		
		if(!this.detailDataDIV){
			alert("请在页面中创建一个 id=detailDataDIV 的 div 用于存放当前标签页的详细数据");
			return;
		}
		
		var freshSystemInfo = $('freshSystemInfo');
		if(freshSystemInfo){
			freshSystemInfo.onclick = function (){
				AS400RemoteService.getHostNodeInfo(ObjectSelf.nodeid, ObjectSelf.type, ObjectSelf.subtype, {
					callback:function(data){
						ObjectSelf.hostNode =  data;
						ObjectSelf.getSystemInfo();
					}
				});
			}
		}
		
		
		AS400RemoteService.getHostNodeInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				ObjectSelf.hostNode =  data;
				ObjectSelf.getSystemInfo();
			}
		});
		
		this.getTabInfo();
		
	},
	
	// 获取设备系统信息
	getSystemInfo: function (){
		AS400RemoteService.getSystemInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				ObjectSelf.callbackgetSystemInfo(data);
			}
		});
	},
	
	// 获取设备系统信息后处理的回调函数
	callbackgetSystemInfo: function (data){ 
		this.data = data;
		this.createSystemInfo();
	},
	
	// 获取设备流速信息后 创建设备流速信息
	createSystemInfo: function (){ 
		
		var systemInfoData = this.data;
		
		var sysDescr = "";
		var sysUpTime = "";
		var MacAddr = "";
		var sysCollectTime = "";
		for(var i = 0; i < systemInfoData.length; i++){
			var systemInfo = systemInfoData[i];
			if( systemInfo.sindex && "sysDescr" == systemInfo.sindex){
					sysDescr = systemInfo.thevalue;
			} else if( systemInfo.sindex && "sysUpTime" == systemInfo.sindex){
					sysUpTime = systemInfo.thevalue;
					sysCollectTime = systemInfo.collecttime;
			} else if( systemInfo.sindex && "MacAddr" == systemInfo.sindex){
					MacAddr = systemInfo.thevalue;
			}
		}
		
		AS400RemoteService.getCurrDayPingAvgInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				var currDayPingAvgInfo_str = data;
				var currDayPingAvgInfo_float = parseFloat(currDayPingAvgInfo_str);
				if(isNaN(currDayPingAvgInfo_float)){
					currDayPingAvgInfo_float = 0;
				}
				var currDayUnPingAvgInfo_float = 100 - currDayPingAvgInfo_float;
				var Pie_ComponentSWFPath = ObjectSelf.SWFPath + ObjectSelf.Pie_ComponentSWFName + ".swf?" +
				"percent1=" + new String(currDayPingAvgInfo_str) + 
				"&percentStr1=可用" +
				"&percent2=" + new String(currDayUnPingAvgInfo_float) + 
				"&percentStr2=不可用";
				var currDayPingAvgInfoTD = $('currDayPingAvgInfo');
				ObjectSelf.removeAllChild(currDayPingAvgInfoTD);
				ObjectSelf.createSWFTABLE(currDayPingAvgInfoTD, Pie_ComponentSWFPath, ObjectSelf.Pie_ComponentSWFName, "160", "160", "8", "#ffffff");
			}
		});
		
		AS400RemoteService.getCurrCpuAvgInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				var currCpuAvgInfo_str = data;
				var currCpuAvgInfo_float = parseFloat(currCpuAvgInfo_str);
				if(isNaN(currCpuAvgInfo_float)){
					currCpuAvgInfo_float = 0;
				}
				var DHCCGaugeSWFPath = ObjectSelf.SWFPath + ObjectSelf.DHCCGaugeSWFName + ".swf?" +
				"percent=" + new String(currCpuAvgInfo_float);
				
				var currCpuAvgInfoTD = $('currCpuAvgInfo');
				ObjectSelf.removeAllChild(currCpuAvgInfoTD);
				ObjectSelf.createSWFTABLE(currCpuAvgInfoTD, DHCCGaugeSWFPath, ObjectSelf.DHCCGaugeSWFName, "160", "160", "8", "#ffffff");
			}
		});
		AS400RemoteService.getStautsInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				var statusImg = "";
				if(data){
					statusImg = data;
				}
				$('systemInfo_stauts').innerHTML = "<img src=\"" + ObjectSelf.rootPath + "/resource/" + statusImg+ "\">";
			}
		});
		AS400RemoteService.getCategoryInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				var category = "";
				if(data){
					category = data;
				}
				$('systemInfo_category').innerHTML = category;
			}
		});
		
		AS400RemoteService.getSupperInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				var supperInfo = "";
				if(data){
					supperInfo = data;
				}
				$('systemInfo_supper').innerHTML = supperInfo;
			}
		});
		
		$('systemInfo_alias').innerHTML = this.hostNode.alias;
		$('systemInfo_sysName').innerHTML = this.hostNode.sysName;
		$('systemInfo_ipaddress').innerHTML = this.hostNode.ipAddress;
		$('systemInfo_netMask').innerHTML = this.hostNode.netMask;
		$('systemInfo_type').innerHTML = this.hostNode.type;
		$('systemInfo_sysdescr').innerHTML = sysDescr;
		$('systemInfo_sysuptime').innerHTML = sysUpTime;
		$('systemInfo_collecttime').innerHTML = sysCollectTime;
		$('systemInfo_mac').innerHTML = MacAddr;
		$('systemInfo_sysOid').innerHTML = this.hostNode.sysOid;
		
	},
	
	// 获取详细信息页面上标签页的信息
	getTabInfo: function (){
		AS400RemoteService.getTabInfo(this.nodeid, this.type, this.subtype, {
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
		this.detailTabDIV.appendChild(tabTable);
		
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
					ObjectSelf.setCurrTab(tab);
				};
			})(tab);
			tabTd.insertAdjacentHTML("afterBegin", tab.name);
			tabTd.id = tab.id;
			tr.appendChild(tabTd);
			
			if(i == 0){
				this.setCurrTab(tab);
			}
		}
	},
	
	// 处理当前标签页的函数
	setCurrTab: function(tab){
		if(this.currTab){
			$(this.currTab.id).className = this.getTabUnSelectedClassName();
			$(this.currTab.id).onmouseover = function () {
				this.className = ObjectSelf.getTabOnmouseoverClassName();
			};
			$(this.currTab.id).onmouseout = function () {
				this.className = ObjectSelf.getTabOnmouseoutClassName();
			};
		}
		this.currTab = tab;
		this.titleList = this.currTab.titleList;
		$(this.currTab.id).className = this.getTabSelectedClassName();
		$(this.currTab.id).onmouseover = function () {
			this.className = ObjectSelf.getTabOnmouseoverClassName();
		};
		$(this.currTab.id).onmouseout = function () {
			this.className = ObjectSelf.getTabOnmouseoverClassName();
		};
		this.createDetailDataTABLE();
		this.createTitleInfo();
		eval("this." + this.currTab.action + "()");
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
	
	// 创建详细信息页面数据的表格
	createDetailDataTABLE: function(){
		if(!this.detailDataTABLE){
			// 如果详细信息页面数据的表格为 null
			this.detailDataTABLE = this.createElement("detailDataTABLE", "table");
			this.detailDataDIV.appendChild(this.detailDataTABLE);
		}
		this.removeAllChild(this.detailDataTABLE);
		
		var titleBODY = this.createElement("detailDataTABLE-TBODY-Title", "tbody");
		this.detailDataTABLE.appendChild(titleBODY);
		
		var listBODY = this.createElement("detailDataTABLE-TBODY-List", "tbody");
		this.detailDataTABLE.appendChild(listBODY);
	},
	
	
	// 创建详细信息页面数据的标题表格
	createTitleInfo: function (titleList){
		if(titleList){
			this.titleList = titleList;
		}
		
		var titleTBODY = this.detailDataTABLE.tBodies[0];
		if(!titleTBODY){
			this.createDetailDataTABLE();
		}
		this.removeAllChild(titleTBODY);
		
		if(this.titleList && this.titleList.length > 0){
			// 如果获取的标题不为空并且个数大于 0
			
			var titleTRRow = 0;
			
			var titleTR = this.createElement("titleTR-" + titleTRRow, "tr")
			titleTBODY.appendChild(titleTR);
			
			for(var i = 0; i < this.titleList.length; i++){
				// 循环每一个表格标题
				var title = this.titleList[i];
				
				if(title.rowNum > titleTRRow){
					titleTRRow = titleTRRow + 1;
					titleTR = this.createElement(this.currTab.id + "-titleTR-" + titleTRRow, "tr")
					titleTBODY.appendChild(titleTR);
				}
				
				var titleId = "";
				if(title.id){
					titleId = this.currTab.id + "-titleTD-" + title.id;
				}
				var titleTD = this.createElement(titleId, "td");
				titleTD.className = "body-data-title";
				if(title.cols){
					titleTD.colSpan = title.cols;
				}
				titleTD.style.height = 25;
				titleTD.insertAdjacentHTML("afterBegin", title.content);
				titleTR.appendChild(titleTD);
			}
			
		}
	},
	
	// 获取设备详细信息数据后 创建设备详细信息数据列表每一行的 td ， 并返回这些 td 的数组集合
	// length  		--- 需要创建 td 的个数 
	// TRContain	--- 每一行 td 的容器 即 tr ， 如果未空， 则这些 td 将不会加入到 tr 中 ，
	// 					需要调用者自己加入到 tr 中，否则将不会显示在页面上
	createDetailDataTD : function(length, TRContain){
		var detailDataTDArray = new Array();
		if(!length || length == 0){ 
			return detailDataTDArray;
		}
		for(var i = 0; i < length; i++){
			var td = this.createElement("", "td");
			td.className = 'detail-data-body-list';
			detailDataTDArray.push(td);
			if(TRContain){
				TRContain.appendChild(td);
			}
		}
		return detailDataTDArray;
	},
	
	// 获取设备详细信息标题后 创建设备详细信息数据标题行的 td ， 并返回这些 td 的数组集合
	// length  		--- 需要创建 td 的个数 
	// TRContain	--- 每一行 td 的容器 即 tr ， 如果未空， 则这些 td 将不会加入到 tr 中 ，
	// 					需要调用者自己加入到 tr 中，否则将不会显示在页面上
	createDetailTitleTD : function(length, TRContain){
		var detailTitileTDArray = new Array();
		if(!length || length == 0){ 
			return detailTitileTDArray;
		}
		for(var i = 0; i < length; i++){
			var td = this.createElement("", "td");
			td.className = 'detail-data-body-title';
			detailTitileTDArray.push(td);
			if(TRContain){
				TRContain.appendChild(td);
			}
		}
		return detailTitileTDArray;
	},
	
	// -------------------------------------------------------------------------------
	// ------------			以下为各个 tab 页所需要的信息			----------------------
	// -------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------
	// ------------			设备系统状态信息    START					----------------------
	// 获取设备系统状态信息
	getSystemStatusInfo: function(){
		AS400RemoteService.getSystemValueInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				ObjectSelf.callbackgetSystemValueInfo(data);
			}
		});
	},
	
	// 获取设备系统状态信息后处理的回调函数
	callbackgetSystemValueInfo: function(data){
		this.data = data;
		this.createSystemValueInfo();
	},
	
	// 获取设备系统状态信息后 创建设备连通率信息
	createSystemValueInfo: function(){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现这种情况)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		
		var systemValueInfoList = this.data;
		var length = 0;
		if(systemValueInfoList){
			length = systemValueInfoList.length;
		}
		var i = 0;
		var cpu = "";
		var DBCapability = "";
		var JobsInSystem = "";
		var SystemASP = "";
		var PercentSystemASPUsed = "";
		var CurrentUnprotectedStorageUsed = "";
		var MaximumUnprotectedStorageUsed = "";
		var PercentPermanentAddresses = "";
		var PercentTemporaryAddresses = "";
		while(i < length){
			var systemValueInfo = systemValueInfoList[i];
			i++;
			var category = systemValueInfo.category;
			var value = systemValueInfo.value;
			if(category == "cpu") {
				cpu = value;
			} else if(category == "DBCapability") {
				DBCapability = value;
			} else if(category == "PercentSystemASPUsed") {
				PercentSystemASPUsed = value;
			} else if(category == "JobsInSystem") {
				JobsInSystem = value;
			} else if(category == "SystemASP") {
				SystemASP = value;
			} else if(category == "CurrentUnprotectedStorageUsed") {
				CurrentUnprotectedStorageUsed = value;
			} else if(category == "MaximumUnprotectedStorageUsed") {
				MaximumUnprotectedStorageUsed = value;
			} else if(category == "PercentPermanentAddresses") {
				PercentPermanentAddresses = value;
			} else if(category == "PercentTemporaryAddresses") {
				PercentTemporaryAddresses = value;
			} 
		}
		var tr = this.createElement("", "tr");
		listTBODY.appendChild(tr);
		var DetailDataTDArray = this.createDetailDataTD(4, tr);
		DetailDataTDArray[0].innerHTML = "cpu使用率";
		DetailDataTDArray[1].innerHTML = cpu;
		DetailDataTDArray[2].innerHTML = "数据库性能";
		DetailDataTDArray[3].innerHTML = DBCapability;
		
		var tr = this.createElement("", "tr");
		listTBODY.appendChild(tr);
		var DetailDataTDArray = this.createDetailDataTD(4, tr);
		DetailDataTDArray[0].innerHTML = "系统中的任务";
		DetailDataTDArray[1].innerHTML = JobsInSystem;
		DetailDataTDArray[2].innerHTML = "系统ASP";
		DetailDataTDArray[3].innerHTML = SystemASP;
		
		var tr = this.createElement("", "tr");
		listTBODY.appendChild(tr);
		var DetailDataTDArray = this.createDetailDataTD(4, tr);
		DetailDataTDArray[0].innerHTML = "系统ASP百分比";
		DetailDataTDArray[1].innerHTML = PercentSystemASPUsed;
		DetailDataTDArray[2].innerHTML = "当前无保护状态下的使用情况";
		DetailDataTDArray[3].innerHTML = CurrentUnprotectedStorageUsed;
		
		var tr = this.createElement("", "tr");
		listTBODY.appendChild(tr);
		var DetailDataTDArray = this.createDetailDataTD(4, tr);
		DetailDataTDArray[0].innerHTML = "最大非保护状态";
		DetailDataTDArray[1].innerHTML = MaximumUnprotectedStorageUsed;
		DetailDataTDArray[2].innerHTML = "永久地址百分比";
		DetailDataTDArray[3].innerHTML = PercentPermanentAddresses;
		
		var tr = this.createElement("", "tr");
		listTBODY.appendChild(tr);
		var DetailDataTDArray = this.createDetailDataTD(4, tr);
		DetailDataTDArray[0].innerHTML = "临时地址百分比";
		DetailDataTDArray[1].innerHTML = PercentTemporaryAddresses;
		DetailDataTDArray[2].innerHTML = "";
		DetailDataTDArray[3].innerHTML = "";
		
	},
	
	// ------------			设备系统状态信息    END				----------------------
	// -------------------------------------------------------------------------------
	
	
	// -------------------------------------------------------------------------------
	// ------------			设备性能信息    START					----------------------
	// 获取设备性能信息
	getPerformaceInfo: function(){
		AS400RemoteService.getHostNodeInfo(this.nodeid, this.type, this.subtype, {
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
	
	// 获取设备性能信息后 创建设备性能信息
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
		
		// 创建 responseTime 所在 td
		var td = this.createElement("", "td");
		tr.appendChild(td);
		// 创建 responseTime 所在 table
		var responseTimePath = this.SWFPath + this.responseTimeSWFName + ".swf?ipadress=" + this.hostNode.ipAddress; 
		var responseTimeSWFTABLE = this.createSWFTABLE(td, responseTimePath, this.responseTimeSWFName, "346", "250", this.SWFVersion, this.SWFbackgroundclor);
		// 创建 Ping 所在 td
		var td = this.createElement("", "td");
		tr.appendChild(td);
		// 创建 Ping 所在 table
		var pingSWFPath = this.SWFPath + this.pingSWFName + ".swf?ipadress=" + this.hostNode.ipAddress; 
		var pingSWFTABLE = this.createSWFTABLE(td, pingSWFPath, this.pingSWFName, "346", "250", this.SWFVersion, this.SWFbackgroundclor);
		
		if(this.hostNode.collecttype == 3 || this.hostNode.collecttype == 4 
		|| this.hostNode.collecttype == 8 || this.hostNode.collecttype == 9){
			return;			
		}
		
		// -------------------换一行-------------------------
		
		var tr = this.createElement("", "tr");
		tbody.appendChild(tr);
		
		// 创建 Line_CPU 所在 td
		var td = this.createElement("", "td");
		tr.appendChild(td);
		// 创建 Line_CPU 所在 table
		var Line_CPUPath = this.SWFPath + this.Line_CPUSWFName + ".swf?ipadress=" + this.hostNode.ipAddress; 
		var Line_CPUSWFTABLE = this.createSWFTABLE(td, Line_CPUPath, this.Line_CPUSWFName, "346", "250", this.SWFVersion, this.SWFbackgroundclor);
		
		// 创建 Area_Memory 所在 td
		var td = this.createElement("", "td");
		tr.appendChild(td);
		// 创建 Area_Memory 所在 table
		var Area_MemoryPath = this.SWFPath + this.Area_MemorySWFName + ".swf?ipadress=" + this.hostNode.ipAddress; 
		var Area_MemorySWFTABLE = this.createSWFTABLE(td, Area_MemoryPath, this.Area_MemorySWFName, "346", "250", this.SWFVersion, this.SWFbackgroundclor);
	},
	
	// ------------			设备性能信息    END					----------------------
	// -------------------------------------------------------------------------------
	
	
	// -------------------------------------------------------------------------------
	// ------------			设备系统信息    START					----------------------
	// 获取设备系统信息
	getSysInfo: function (){
		// 因为有多个数据 暂时没有放在这里做
		// 而是在 createSysInfo 方法里完成
		this.callbackgetSysInfo(null);
	},
	
	// 获取设备系统信息后处理的回调函数
	callbackgetSysInfo: function(data){
		this.data = data;
		this.createSysInfo();
	},
	
	// 获取设备系统信息后 创建设备系统信息
	createSysInfo: function(){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		
		// cpu性能
		var tr = this.createElement("", "tr");
		listTBODY.appendChild(tr);
		
		var td = this.createElement("", "td");
		tr.appendChild(td);
		
		var table = this.createElement(this.currTab.id + "-cpuPerf-dataTABLE", "table");
		td.appendChild(table);
		
		var cpuPerfTBODY = this.createElement(this.currTab.id + "-cpuPerf-dataTBODY", "tbody");
		table.appendChild(cpuPerfTBODY);
		
		var detailTitleArray = ["%用户", "%系统", "%io等待", "%空闲", "物理"];
		var length = detailTitleArray.length;
		var tr = this.createElement(this.currTab.id + "-cpuPerf-titleTR-0", "tr");
		tr.className = "detail-data-body-title";
		cpuPerfTBODY.appendChild(tr);
		
		var td = this.createElement("", "td");
		tr.appendChild(td);
		td.colSpan = length;
		td.insertAdjacentHTML("afterBegin", "<table><tr><td style='height:25px;text-align:left;' class='detail-data-body-title'>&nbsp;&nbsp;cpu性能</td></tr></table>");
		
		var tr = this.createElement(this.currTab.id + "-cpuPerf-titleTR-1", "tr");
		cpuPerfTBODY.appendChild(tr);
		
		
		var detailTitleTDArray = this.createDetailTitleTD(length, tr);
		for(var i = 0 ; i < length; i++){
			var detailTitleTD = detailTitleTDArray[i];
			detailTitleTD.style.height = '25px';
			detailTitleTD.insertAdjacentHTML("afterBegin", detailTitleArray[i]);
		}
		
				
		// 磁盘性能
		var tr = this.createElement("", "tr");
		listTBODY.appendChild(tr);
		
		var td = this.createElement("", "td");
		tr.appendChild(td);
		
		var table = this.createElement(this.currTab.id + "-diskPerf-dataTABLE", "table");
		td.appendChild(table);
		
		var diskPerfTBODY = this.createElement(this.currTab.id + "-diskPerf-dataTBODY", "tbody");
		table.appendChild(diskPerfTBODY);
		
		var detailTitleArray = ["磁盘名", "繁忙(%)", "平均深度", "读写块数/秒", "读写字节（K）/秒", "平均等待时间(ms)", "平均执行时间(ms)"];
		var tr = this.createElement(this.currTab.id + "-diskPerf-titleTR-0", "tr");
		tr.className = "detail-data-body-title";
		diskPerfTBODY.appendChild(tr);
		
		var length = detailTitleArray.length;
		var td = this.createElement("", "td");
		tr.appendChild(td);
		td.colSpan = length;
		td.insertAdjacentHTML("afterBegin", "<table><tr><td style='height:25px;text-align:left;' class='detail-data-body-title'>&nbsp;&nbsp;磁盘性能</td></tr></table>");
		
		var tr = this.createElement(this.currTab.id + "-diskPerf-titleTR-1", "tr");
		diskPerfTBODY.appendChild(tr);
		
		
		var detailTitleTDArray = this.createDetailTitleTD(length, tr);
		for(var i = 0 ; i < length; i++){
			var detailTitleTD = detailTitleTDArray[i];
			detailTitleTD.style.height = '25px';
			detailTitleTD.insertAdjacentHTML("afterBegin", detailTitleArray[i]);
		}
		
		// 页面性能 
		var tr = this.createElement("", "tr");
		listTBODY.appendChild(tr);
		
		var td = this.createElement("", "td");
		tr.appendChild(td);
		
		var table = this.createElement(this.currTab.id + "-pageperf-dataTABLE", "table");
		td.appendChild(table);
		
		var pagePerfTBODY = this.createElement(this.currTab.id + "-pagePerf-dataTBODY", "tbody");
		table.appendChild(pagePerfTBODY);
		
		var detailTitleArray = ["页面调度程序输入/输出列表", "内存页面调进数", 
								"内存页面调出数", "释放的页数", "扫描的页", "时钟周期"];
		var tr = this.createElement(this.currTab.id + "-pagePerf-titleTR-0", "tr");
		tr.className = "detail-data-body-title";
		pagePerfTBODY.appendChild(tr);
		
		var length = detailTitleArray.length;
		var td = this.createElement("", "td");
		tr.appendChild(td);
		td.colSpan = length;
		td.insertAdjacentHTML("afterBegin", "<table><tr><td style='height:25px;text-align:left;' class='detail-data-body-title'>&nbsp;&nbsp;页面性能</td></tr></table>");
		
		var tr = this.createElement(this.currTab.id + "-pagePerf-titleTR-1", "tr");
		pagePerfTBODY.appendChild(tr);
		
		
		var detailTitleTDArray = this.createDetailTitleTD(length, tr);
		for(var i = 0 ; i < length; i++){
			var detailTitleTD = detailTitleTDArray[i];
			detailTitleTD.style.height = '25px';
			detailTitleTD.insertAdjacentHTML("afterBegin", detailTitleArray[i]);
		}
		
		// 页面交换
		var tr = this.createElement("", "tr");
		listTBODY.appendChild(tr);
		
		var td = this.createElement("", "td");
		tr.appendChild(td);
		
		var table = this.createElement(this.currTab.id + "-pageSpace-dataTABLE", "table");
		td.appendChild(table);
		
		var pageSpaceTBODY = this.createElement(this.currTab.id + "-pageSpace-dataTBODY", "tbody");
		table.appendChild(pageSpaceTBODY);
		
		var detailTitleArray = ["Total_Paging_Space", "Percent_Used"];
		var tr = this.createElement(this.currTab.id + "-pageSpace-titleTR-0", "tr");
		tr.className = "detail-data-body-title";
		pageSpaceTBODY.appendChild(tr);
		
		var length = detailTitleArray.length;
		var td = this.createElement("", "td");
		tr.appendChild(td);
		td.colSpan = length;
		td.insertAdjacentHTML("afterBegin", "<table><tr><td style='height:25px;text-align:left;' class='detail-data-body-title'>&nbsp;&nbsp;Paging Space利用率</td></tr></table>");
		
		var tr = this.createElement(this.currTab.id + "-pageSpace-titleTR-1", "tr");
		pageSpaceTBODY.appendChild(tr);
		
		var detailTitleTDArray = this.createDetailTitleTD(length, tr);
		for(var i = 0 ; i < length; i++){
			var detailTitleTD = detailTitleTDArray[i];
			detailTitleTD.style.height = '25px';
			detailTitleTD.insertAdjacentHTML("afterBegin", detailTitleArray[i]);
		}
		
		// 获取cpu性能信息
		AS400RemoteService.getCpuPerfInfo(this.nodeid, this.type, this.subtype,{
			callback: function(data){
				ObjectSelf.createCpuPerfInfoData(cpuPerfTBODY, data);
			}
		});
		
		// 获取磁盘性能信息
		AS400RemoteService.getDiskPerfInfo(this.nodeid, this.type, this.subtype,{
			callback: function(data){
				ObjectSelf.createDiskPerfInfoData(diskPerfTBODY, data);
			}
		});
		
		// 获取页面性能信息
		AS400RemoteService.getPagePerfInfo(this.nodeid, this.type, this.subtype,{
			callback: function(data){
				ObjectSelf.createPagePerfInfoData(pagePerfTBODY, data);
			}
		});
		
		// 获取页面交换信息
		AS400RemoteService.getPageSpaceInfo(this.nodeid, this.type, this.subtype,{
			callback: function(data){
				ObjectSelf.createPageSpaceInfoData(pageSpaceTBODY, data);
			}
		});
	},
	
	// 获取cpu性能信息后 创建cpu性能信息数据
	createCpuPerfInfoData: function(cpuPerfTBODY, data){
		if(!cpuPerfTBODY){
			return;
		}
		var cpuPerfInfoList = data;
		if(!cpuPerfInfoList){
			return;
		}
		var length = cpuPerfInfoList.length;
		var i = 0;
		while(i < length){
			var cpuPerfInfo = cpuPerfInfoList[i];
			i++;
			var user = cpuPerfInfo.user;					// %用户
			var sysRate = cpuPerfInfo.sysRate;				// %系统
			var wioRate = cpuPerfInfo.wioRate;				// %io等待
			var idleRate = cpuPerfInfo.idleRate;			// %空闲
			var physc = cpuPerfInfo.physc;					// 物理 
			
			
			var tr = this.createElement("", "tr");
			tr.height = 25;
			cpuPerfTBODY.appendChild(tr);
			
			var detailDataArray = [user, sysRate, wioRate, idleRate, physc];
			var detailDataArrayLength = detailDataArray.length;
			var detailDataTDArray = this.createDetailDataTD(detailDataArrayLength, tr);
			
			for(var j = 0; j < detailDataArrayLength; j++){
				detailDataTDArray[j].insertAdjacentHTML("afterBegin", detailDataArray[j]);
			}
		}
		
	},
	
	// 获取磁盘性能信息后 创建磁盘性能信息数据
	createDiskPerfInfoData: function(diskPerfTBODY, data){
		if(!diskPerfTBODY){
			return;
		}
		var diskPerfInfoList = data;
		if(!diskPerfInfoList){
			return;
		}
		var length = diskPerfInfoList.length;
		var i = 0;
		while(i < length){
			var diskPerfInfo = diskPerfInfoList[i];
			i++;
			var disklebel = diskPerfInfo.disklebel;			// 磁盘名称
			var busyRate = diskPerfInfo.busyRate;			// 繁忙(%)
			var avque = diskPerfInfo.avque;					// 平均深度
			var readAndWriteBlockPerSecond = diskPerfInfo.readAndWriteBlockPerSecond;	// 读写块数/秒
			var readAndWriteBytePerSecond = diskPerfInfo.readAndWriteBytePerSecond;		// 读写字节（K）/秒
			var avwait = diskPerfInfo.avwait;				// 平均等待时间(ms)
			var avserv = diskPerfInfo.avserv;				// 平均执行时间(ms)
			
			
			var tr = this.createElement("", "tr");
			tr.height = 25;
			diskPerfTBODY.appendChild(tr);
			
			var detailDataArray = [disklebel, busyRate, avque, readAndWriteBlockPerSecond, 
									readAndWriteBytePerSecond, avwait, avserv];
			var detailDataArrayLength = detailDataArray.length;
			var detailDataTDArray = this.createDetailDataTD(detailDataArrayLength, tr);
			
			for(var j = 0; j < detailDataArrayLength; j++){
				detailDataTDArray[j].insertAdjacentHTML("afterBegin", detailDataArray[j]);
			}
		}
		
	},
	
	// 获取页面性能信息后 创建页面性能信息数据
	createPagePerfInfoData: function(pagePerfTBODY, data){
		if(!pagePerfTBODY){
			return;
		}
		var pagePerfInfoList = data;
		if(!pagePerfInfoList){
			return;
		}
		var length = pagePerfInfoList.length;
		var i = 0;
		while(i < length){
			var pagePerfInfo = pagePerfInfoList[i];
			i++;
			
			var re = pagePerfInfo.re;				// 页面调度程序输入/输出列表
			var pi = pagePerfInfo.pi;				// 内存页面调进数
			var po = pagePerfInfo.po;				// 内存页面调出数
			var fr = pagePerfInfo.fr;				// 释放的页数
			var sr = pagePerfInfo.sr;				// 扫描的页
			var cy = pagePerfInfo.cy;				// 时钟周期
			
			
			var tr = this.createElement("", "tr");
			tr.height = 25;
			pagePerfTBODY.appendChild(tr);
			
			var detailDataArray = [re, pi, po, fr, sr, cy];
			var detailDataArrayLength = detailDataArray.length;
			var detailDataTDArray = this.createDetailDataTD(detailDataArrayLength, tr);
			
			for(var j = 0; j < detailDataArrayLength; j++){
				detailDataTDArray[j].insertAdjacentHTML("afterBegin", detailDataArray[j]);
			}
		}
		
	},
	
	// 获取页面交换信息后 创建页面交换信息数据
	createPageSpaceInfoData: function(pageSpaceTBODY, data){
		if(!pageSpaceTBODY){
			return;
		}
		var pageSpaceInfoList = data;
		if(!pageSpaceInfoList){
			return;
		}
		var length = pageSpaceInfoList.length;
		var i = 0;
		while(i < length){
			var pageSpaceInfo = pageSpaceInfoList[i];
			i++;
			var total_Paging_Space = pageSpaceInfo.total_Paging_Space;	// Total Paging Space
			var percent_Used = pageSpaceInfo.percent_Used;				// Percent Used
			
			
			var tr = this.createElement("", "tr");
			tr.height = 25;
			pageSpaceTBODY.appendChild(tr);
			
			var detailDataArray = [total_Paging_Space, percent_Used];
			var detailDataArrayLength = detailDataArray.length;
			var detailDataTDArray = this.createDetailDataTD(detailDataArrayLength, tr);
			
			for(var j = 0; j < detailDataArrayLength; j++){
				detailDataTDArray[j].insertAdjacentHTML("afterBegin", detailDataArray[j]);
			}
		}
		
	},
	
	// ------------			设备系统信息    END					----------------------
	// -------------------------------------------------------------------------------
	
	
	// -------------------------------------------------------------------------------
	// ------------			设备配置信息    START					----------------------
	// 获取设备配置信息
	getConfigInfo: function (){
		// 因为有多个数据 暂时没有放在这里做 
		// 而是在 createConfigInfo 方法里单独完成
		this.callbackgetConfigInfo(null);
	},
	
	// 获取设备配置信息后处理的回调函数
	callbackgetConfigInfo: function(data){
		this.data = data;
		this.createConfigInfo();
	},
	
	// 获取设备配置信息后 创建设备配置信息
	createConfigInfo: function(){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		
		// 内存配置
		var tr = this.createElement("", "tr");
		listTBODY.appendChild(tr);
		
		var td = this.createElement("", "td");
		tr.appendChild(td);
		
		var table = this.createElement(this.currTab.id + "-memoryConfig-dataTABLE", "table");
		td.appendChild(table);
		
		var memoryConfigTBODY = this.createElement(this.currTab.id + "-memoryConfig-dataTBODY", "tbody");
		table.appendChild(memoryConfigTBODY);
		
		var detailTitleArray = ["类型", "大小"];
		var length = detailTitleArray.length;
		var tr = this.createElement(this.currTab.id + "-memoryConfig-titleTR-0", "tr");
		tr.className = "detail-data-body-title";
		memoryConfigTBODY.appendChild(tr);
		
		var td = this.createElement("", "td");
		tr.appendChild(td);
		td.colSpan = length;
		td.insertAdjacentHTML("afterBegin", "<table><tr><td style='height:25px;text-align:left;' class='detail-data-body-title'>&nbsp;&nbsp;内存配置</td></tr></table>");
		
		var tr = this.createElement(this.currTab.id + "-memoryConfig-titleTR-1", "tr");
		memoryConfigTBODY.appendChild(tr);
		
		
		var detailTitleTDArray = this.createDetailTitleTD(length, tr);
		for(var i = 0 ; i < length; i++){
			var detailTitleTD = detailTitleTDArray[i];
			detailTitleTD.style.height = '25px';
			detailTitleTD.insertAdjacentHTML("afterBegin", detailTitleArray[i]);
		}
		
				
		// 网卡配置
		var tr = this.createElement("", "tr");
		listTBODY.appendChild(tr);
		
		var td = this.createElement("", "td");
		tr.appendChild(td);
		
		var table = this.createElement(this.currTab.id + "-netmediaConfig-dataTABLE", "table");
		td.appendChild(table);
		
		var netmediaConfigTBODY = this.createElement(this.currTab.id + "-netmediaConfig-dataTBODY", "tbody");
		table.appendChild(netmediaConfigTBODY);
		
		var detailTitleArray = ["名称", "MAC", "速率", "状态"];
		var tr = this.createElement(this.currTab.id + "-netmediaConfig-titleTR-0", "tr");
		tr.className = "detail-data-body-title";
		netmediaConfigTBODY.appendChild(tr);
		
		var length = detailTitleArray.length;
		var td = this.createElement("", "td");
		tr.appendChild(td);
		td.colSpan = length;
		td.insertAdjacentHTML("afterBegin", "<table><tr><td style='height:25px;text-align:left;' class='detail-data-body-title'>&nbsp;&nbsp;网卡配置</td></tr></table>");
		
		var tr = this.createElement(this.currTab.id + "-netmediaConfig-titleTR-1", "tr");
		netmediaConfigTBODY.appendChild(tr);
		
		
		var detailTitleTDArray = this.createDetailTitleTD(length, tr);
		for(var i = 0 ; i < length; i++){
			var detailTitleTD = detailTitleTDArray[i];
			detailTitleTD.style.height = '25px';
			detailTitleTD.insertAdjacentHTML("afterBegin", detailTitleArray[i]);
		}
		
		// 用户配置
		var tr = this.createElement("", "tr");
		listTBODY.appendChild(tr);
		
		var td = this.createElement("", "td");
		tr.appendChild(td);
		
		var table = this.createElement(this.currTab.id + "-userConfig-dataTABLE", "table");
		td.appendChild(table);
		
		var userConfigTBODY = this.createElement(this.currTab.id + "-userConfig-dataTBODY", "tbody");
		table.appendChild(userConfigTBODY);

		var detailTitleArray = ["名称", "用户组"];
		var tr = this.createElement(this.currTab.id + "-userConfig-titleTR-0", "tr");
		tr.className = "detail-data-body-title";
		userConfigTBODY.appendChild(tr);
		
		var length = detailTitleArray.length;
		var td = this.createElement("", "td");
		tr.appendChild(td);
		td.colSpan = length;
		td.insertAdjacentHTML("afterBegin", "<table><tr><td style='height:25px;text-align:left;' class='detail-data-body-title'>&nbsp;&nbsp;页面性能</td></tr></table>");
		
		var tr = this.createElement(this.currTab.id + "-userConfig-titleTR-1", "tr");
		userConfigTBODY.appendChild(tr);
		
		
		var detailTitleTDArray = this.createDetailTitleTD(length, tr);
		for(var i = 0 ; i < length; i++){
			var detailTitleTD = detailTitleTDArray[i];
			detailTitleTD.style.height = '25px';
			detailTitleTD.insertAdjacentHTML("afterBegin", detailTitleArray[i]);
		}
		
		
		// 获取内存配置信息
		AS400RemoteService.getMemoryConfigInfo(this.nodeid, this.type, this.subtype,{
			callback: function(data){
				ObjectSelf.createMemoryConfigInfoData(memoryConfigTBODY, data);
			}
		});
		
		// 获取网卡配置信息
		AS400RemoteService.getNetmediaConfigInfo(this.nodeid, this.type, this.subtype,{
			callback: function(data){
				ObjectSelf.createNetmediaConfigInfoData(netmediaConfigTBODY, data);
			}
		});
		
		// 获取用户配置信息
		AS400RemoteService.getUserConfigInfo(this.nodeid, this.type, this.subtype,{
			callback: function(data){
				ObjectSelf.createUserConfigInfoData(userConfigTBODY, data);
			}
		});
		
	},
	
	// 获取内存配置信息后 创建内存配置信息数据
	createMemoryConfigInfoData: function(memoryConfigTBODY, data){
		if(!memoryConfigTBODY){
			return;
		}
		var memoryConfigInfoList = data;
		if(!memoryConfigInfoList){
			return;
		}
		var length = memoryConfigInfoList.length;
		var i = 0;
		while(i < length){
			var memoryConfigInfo = memoryConfigInfoList[i];
			i++;
			var descr_cn = memoryConfigInfo.descr_cn;			// 中文描述
			var size = memoryConfigInfo.size;					// 大小
			var unit = memoryConfigInfo.unit;					// 大小的单位
			//var type = memoryConfigInfo.type;					// 类型
			//var sindex = memoryConfigInfo.sindex;				// 索引
			
			
			var tr = this.createElement("", "tr");
			tr.height = 25;
			memoryConfigTBODY.appendChild(tr);
			
			//var detailDataArray = [descr_cn, size];
			//var detailDataArrayLength = detailDataArray.length;
			var detailDataTDArray = this.createDetailDataTD(2, tr);
			
			// 类型
			var td = detailDataTDArray[0];
			td.insertAdjacentHTML("afterBegin", descr_cn);
			
			// 大小
			var td = detailDataTDArray[1];
			td.insertAdjacentHTML("afterBegin", size + unit);
		}
		
	},
	
	// 获取网卡配置信息后 创建网卡配置信息数据
	createNetmediaConfigInfoData: function(netmediaConfigTBODY, data){
		if(!netmediaConfigTBODY){
			return;
		}
		var NetmediaConfigInfoList = data;
		if(!NetmediaConfigInfoList){
			return;
		}
		var length = NetmediaConfigInfoList.length;
		var i = 0;
		while(i < length){
			var netmediaConfig = NetmediaConfigInfoList[i];
			i++;
			var desc = netmediaConfig.desc;				// 名称
			var mac = netmediaConfig.mac;				// MAC
			var speed = netmediaConfig.speed;			// 速率
			var status = netmediaConfig.status;			// 状态
			
			
			var tr = this.createElement("", "tr");
			tr.height = 25;
			netmediaConfigTBODY.appendChild(tr);
			
			var detailDataArray = [desc, mac, speed, status];
			var detailDataArrayLength = detailDataArray.length;
			var detailDataTDArray = this.createDetailDataTD(detailDataArrayLength, tr);
			
			for(var j = 0; j < detailDataArrayLength; j++){
				detailDataTDArray[j].insertAdjacentHTML("afterBegin", detailDataArray[j]);
			}
		}
		
	},
	
	// 获取用户配置信息后 创建用户配置信息数据
	createUserConfigInfoData: function(userConfigTBODY, data){
		if(!userConfigTBODY){
			return;
		}
		var userConfigInfoList = data;
		if(!userConfigInfoList){
			return;
		}
		var length = userConfigInfoList.length;
		var i = 0;
		while(i < length){
			var userConfig = userConfigInfoList[i];
			i++;
			var name = userConfig.name;				// 名称
			var userGroup = userConfig.userGroup;	// 用户组
			
			
			var tr = this.createElement("", "tr");
			tr.height = 25;
			userConfigTBODY.appendChild(tr);
			
			var detailDataArray = [name, userGroup];
			var detailDataArrayLength = detailDataArray.length;
			var detailDataTDArray = this.createDetailDataTD(detailDataArrayLength, tr);
			
			for(var j = 0; j < detailDataArrayLength; j++){
				detailDataTDArray[j].insertAdjacentHTML("afterBegin", detailDataArray[j]);
			}
		}
		
	},
	
	// ------------			设备配置信息    END					----------------------
	// -------------------------------------------------------------------------------
	
	
	// -------------------------------------------------------------------------------
	// ------------			设备进程信息    START					----------------------
	// 获取设备进程信息 
	getProcessInfo: function(){
		AS400RemoteService.getProcessInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				ObjectSelf.callbackgetProcessInfo(data);
			}
		});
	},
	
	// 获取设备进程信息后处理的回调函数
	callbackgetProcessInfo: function(data){
		this.data = data;
		this.createProcessInfo();
	},
	
	// 获取设备进程信息后 创建设备进程信息
	createProcessInfo: function(){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		var processInfoList = this.data;
		if(processInfoList == null){
			return;
		}
		
		var currTabId = this.currTab.id;
		var name_cellIndex = $(currTabId + "-titleTD-" + "name").cellIndex;
		var count_cellIndex = $(currTabId + "-titleTD-" + "count").cellIndex;
		var type_cellIndex = $(currTabId + "-titleTD-" + "type").cellIndex;
		var cpuTime_cellIndex = $(currTabId + "-titleTD-" + "cpuTime").cellIndex;
		var memoryUtilization_cellIndex = $(currTabId + "-titleTD-" + "memoryUtilization").cellIndex;
		var memory_cellIndex = $(currTabId + "-titleTD-" + "memory").cellIndex;
		var status_cellIndex= $(currTabId + "-titleTD-" + "status").cellIndex;
		var showDetail_cellIndex = $(currTabId + "-titleTD-" + "showDetail").cellIndex;
		
		var length = processInfoList.length;
		var i = 0;
		while(i < length){
			var processInfo = processInfoList[i];
			i++;
			var index = i;								// 序号
			var name = processInfo.name;				// 进程名称
			var count = processInfo.count;				// 进程个数
			var type = processInfo.type;				// 进程类型
			var cpuTime = processInfo.cpuTime;			// CPU时间
			var memoryUtilization = processInfo.memoryUtilization;			// 内存占用率
			var memory = processInfo.memory;			// 内存占用量
			var status = processInfo.status;			// 当前状态
			
			var tr = this.createElement("", "tr");
			tr.height = 25;
			listTBODY.appendChild(tr);
			var detailDataTDArray = this.createDetailDataTD(8, tr);
			
			// 进程名称
			var td = detailDataTDArray[name_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "name";
			td.insertAdjacentHTML("afterBegin", name);
			
			// 进程个数
			var td = detailDataTDArray[count_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "count";
			td.insertAdjacentHTML("afterBegin", count);
			
			// 进程类型
			var td = detailDataTDArray[type_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "type";
			td.insertAdjacentHTML("afterBegin", type);
			
			// CPU时间
			var td = detailDataTDArray[cpuTime_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "cpuTime";
			td.insertAdjacentHTML("afterBegin", cpuTime);
			
			// 内存占用率
			var td = detailDataTDArray[memoryUtilization_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "memoryUtilization";
			td.insertAdjacentHTML("afterBegin", memoryUtilization);
			
			// 内存占用量
			var td = detailDataTDArray[memory_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "memory";
			td.insertAdjacentHTML("afterBegin", memory);
			
			// 当前状态
			var td = detailDataTDArray[status_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "status";
			td.insertAdjacentHTML("afterBegin", status);
			
			// 查看详情
			var td = detailDataTDArray[showDetail_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "showDetail";
			var button = this.createElement("", "input");
			button.type = "button";
			button.value = "查看详情";
			td.appendChild(button);
			button.onclick = function(name){
					return function(){
						ObjectSelf.showProcessInfoDetail(name);
					};
			}(name);
		}
		
	},
	
	// 获取设备进程信息后 创建设备进程信息后 点击查看进程详情
	showProcessInfoDetail: function(name){
		AS400RemoteService.getProcessDetailInfo(this.nodeid, this.type, this.subtype, name, {
			callback:function(data){
				ObjectSelf.createProcessDetailInfo(data);
			}
		});
		var titleTBODY = this.detailDataTABLE.tBodies[0];
		if(!titleTBODY){
			this.createDetailDataTABLE();
		}
		this.removeAllChild(titleTBODY);
		
		var titleTR = this.createElement(this.currTab.id + "titleTR-0", "tr")
		titleTBODY.appendChild(titleTR);
		var titleArray = ["进程ID", "进程名称", "进程类型", "cpu时间", "内存占用率", "内存占用量", "当前状态"];
		var titleIDArray = ["pid", "name", "type", "cpuTime", "memoryUtilization", "memory", "status"];
		var length = titleArray.length;
		var i = 0;
		while(i < length){
			var titleTD = this.createElement(titleIDArray[i], "td");
			titleTD.style.height = 25;
			titleTD.className = "body-data-title";
			titleTD.insertAdjacentHTML("afterBegin", titleArray[i]);   // 进程ID
			titleTR.appendChild(titleTD);
			i++;
		}
		
	},
	
	// 获取设备进程信息后 创建设备进程信息后 创建进程详情信息
	createProcessDetailInfo: function(data){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		
		var processDetailInfoList = data;
		var length = processDetailInfoList.length;
		if(processDetailInfoList == null || length == 0){
			return;
		}
		var i = 0;
		while(i < length){
			var processInfo = processDetailInfoList[i];
			i++;
			var index = i + 1;							// 序号
			var pid = processInfo.pid;					// 进程ID
			var name = processInfo.name;				// 进程名称
			var type = processInfo.type;				// 进程类型
			var cpuTime = processInfo.cpuTime;			// CPU时间
			var memoryUtilization = processInfo.memoryUtilization;			// 内存占用率
			var memory = processInfo.memory;			// 内存占用量
			var status = processInfo.status;			// 当前状态
			
			var tr = this.createElement("", "tr");
			tr.height = 25;
			listTBODY.appendChild(tr);
			
			// 进程ID
			var td = this.createElement("", "td");
			td.className = 'detail-data-body-list';
			td.insertAdjacentHTML("afterBegin", pid);
			tr.appendChild(td);
			
			// 进程名称
			var td = this.createElement("", "td");
			td.className = 'detail-data-body-list';
			td.insertAdjacentHTML("afterBegin", name);
			tr.appendChild(td);
			
			// 进程类型
			var td = this.createElement("", "td");
			td.className = 'detail-data-body-list';
			td.insertAdjacentHTML("afterBegin", type);
			tr.appendChild(td);
			
			// CPU时间
			var td = this.createElement("", "td");
			td.className = 'detail-data-body-list';
			td.insertAdjacentHTML("afterBegin", cpuTime);
			tr.appendChild(td);
			
			// 内存占用率
			var td = this.createElement("", "td");
			td.className = 'detail-data-body-list';
			td.insertAdjacentHTML("afterBegin", memoryUtilization);
			tr.appendChild(td);
			
			// 内存占用量
			var td = this.createElement("", "td");
			td.className = 'detail-data-body-list';
			td.insertAdjacentHTML("afterBegin", memory);
			tr.appendChild(td);
			
			// 当前状态
			var td = this.createElement("", "td");
			td.className = 'detail-data-body-list';
			td.insertAdjacentHTML("afterBegin", status);
			tr.appendChild(td);
		}
		
	},
	
	// ------------			设备进程信息    END					----------------------
	// -------------------------------------------------------------------------------
	
	
	// -------------------------------------------------------------------------------
	// ------------			设备系统池信息    START					----------------------
	// 获取设备系统池信息
	getSystemPoolInfo: function (){
		AS400RemoteService.getSystemPoolInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				ObjectSelf.callbackgetSystemPoolInfo(data);
			}
		});
	},
	
	// 获取设备系统池信息后处理的回调函数
	callbackgetSystemPoolInfo: function(data){
		this.data = data;
		this.createSystemPoolInfo();
	},
	
	// 获取设备系统池信息后 创建设备系统池信息
	createSystemPoolInfo: function(){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		var systemPoolInfoList = this.data;
		
		var length = 0;
		if(systemPoolInfoList){
			length = systemPoolInfoList.length;
		}
		
		var currTabId = this.currTab.id;
		var systemPool_cellIndex = $(currTabId + "-titleTD-" + "systemPool").cellIndex;
		var name_cellIndex = $(currTabId + "-titleTD-" + "name").cellIndex;
		var size_cellIndex = $(currTabId + "-titleTD-" + "size").cellIndex;
		var reservedSize_cellIndex = $(currTabId + "-titleTD-" + "reservedSize").cellIndex;
		var maximumActiveThreads_cellIndex = $(currTabId + "-titleTD-" + "maximumActiveThreads").cellIndex;
		
		
		var i = 0
		while(i < length){
			var systemPoolInfo = systemPoolInfoList[i];
			i++;
			var index = i;														// 序号
			var systemPool = systemPoolInfo.systemPool;							// 系统池自身Id
			var name = systemPoolInfo.name;										// 系统池名称
			var size = systemPoolInfo.size;										// 系统池大小
			var reservedSize = systemPoolInfo.reservedSize;						// 系统池的保留池大小
			var maximumActiveThreads = systemPoolInfo.maximumActiveThreads;		// 系统池最大活动数
			
			var tr = this.createElement(this.currTab.id + "-dataTR-" + index, "tr");
			tr.height = 25;
			listTBODY.appendChild(tr);
			var detailDataTDArray = this.createDetailDataTD(5, tr);
			
			// 系统池自身Id
			var td = detailDataTDArray[systemPool_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "systemPool";
			td.insertAdjacentHTML("afterBegin", systemPool);
			
			// 系统池名称
			var td = detailDataTDArray[name_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "name";
			td.insertAdjacentHTML("afterBegin", name);
			
			// 系统池大小
			var td = detailDataTDArray[size_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "size";
			td.insertAdjacentHTML("afterBegin", size);
			
			// 系统池的保留池大小
			var td = detailDataTDArray[reservedSize_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "reservedSize";
			td.insertAdjacentHTML("afterBegin", reservedSize);
			
			// 系统池最大活动数
			var td = detailDataTDArray[maximumActiveThreads_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "maximumActiveThreads";
			td.insertAdjacentHTML("afterBegin", maximumActiveThreads);
			
			
			
		}
		
	},
	
	// ------------			设备系统池信息    END					----------------------
	// -------------------------------------------------------------------------------
	
	
	// -------------------------------------------------------------------------------
	// ------------			设备磁盘信息    START				----------------------
	// 获取设备磁盘信息 
	getDiskInfo: function (){
		AS400RemoteService.getDiskInfo(this.nodeid, this.type, this.subtype, {
				callback:function(data){
					ObjectSelf.callbackgetDiskInfo(data);
				}
		});
	},
	
	// 获取设备磁盘信息后处理的回调函数
	callbackgetDiskInfo: function(data){
		this.data = data;
		this.createDiskInfo();
	},
	
	// 获取设备磁盘信息后 创建设备磁盘信息
	createDiskInfo: function(){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		
		var diskInfoList = this.data;
		var length = 0;
		if(diskInfoList){
			length = diskInfoList.length;
		}
		
		var currTabId = this.currTab.id;
		var unit_cellIndex = $(currTabId + "-titleTD-" + "unit").cellIndex;
		var type_cellIndex = $(currTabId + "-titleTD-" + "type").cellIndex;
		var size_cellIndex = $(currTabId + "-titleTD-" + "size").cellIndex;
		var used_cellIndex = $(currTabId + "-titleTD-" + "used").cellIndex;
		var ioRqs_cellIndex = $(currTabId + "-titleTD-" + "ioRqs").cellIndex;
		var requestSize_cellIndex = $(currTabId + "-titleTD-" + "requestSize").cellIndex;
		var readRqs_cellIndex = $(currTabId + "-titleTD-" + "readRqs").cellIndex;
		var writeRqs_cellIndex = $(currTabId + "-titleTD-" + "writeRqs").cellIndex;
		var read_cellIndex = $(currTabId + "-titleTD-" + "read").cellIndex;
		var write_cellIndex = $(currTabId + "-titleTD-" + "write").cellIndex;
		var busy_cellIndex = $(currTabId + "-titleTD-" + "busy").cellIndex;
		
		var i = 0
		while(i < length){
			var diskInfo = diskInfoList[i];
			i++;
			var index = i;
			var unit = diskInfo.unit;					// 单元
			var diskInfoType = diskInfo.type;			// 类型
		 	var size = diskInfo.size;					// 大小
			var used = diskInfo.used;					// 磁盘利用百分比
			var ioRqs = diskInfo.ioRqs;					// IO Rqs
			var requestSize = diskInfo.requestSize;		// 被请求的大小
			var readRqs = diskInfo.readRqs;				// 读 Rqs
			var writeRqs = diskInfo.writeRqs;				// 写 Rqs
			var read = diskInfo.read;					// 读(K)
			var write = diskInfo.write;					// 写(K)
			var busy = diskInfo.busy;					// 忙碌
			
			var tr = this.createElement(this.currTab.id + "-dataTR-" + index, "tr");
			tr.height = 25;
			listTBODY.appendChild(tr);
			var detailDataTDArray = this.createDetailDataTD(11, tr);
			
			// 单元
			var td = detailDataTDArray[unit_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "unit";
			td.insertAdjacentHTML("afterBegin", unit);
			
			// 类型
			var td = detailDataTDArray[type_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "type";
			td.insertAdjacentHTML("afterBegin", diskInfoType);
			
			// 大小
			var td = detailDataTDArray[size_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "size";
			td.insertAdjacentHTML("afterBegin", size);
			
			// 磁盘利用百分比
			var td = detailDataTDArray[used_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "used";
			td.insertAdjacentHTML("afterBegin", used);
			
			// IO Rqs
			var td = detailDataTDArray[ioRqs_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "ioRqs";
			td.insertAdjacentHTML("afterBegin", ioRqs);
			
			// 被请求的大小
			var td = detailDataTDArray[requestSize_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "requestSize";
			td.insertAdjacentHTML("afterBegin", requestSize);
			
			// 读 Rqs
			var td = detailDataTDArray[readRqs_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "readRqs";
			td.insertAdjacentHTML("afterBegin", readRqs);
			
			// 写 Rqs
			var td = detailDataTDArray[writeRqs_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "writeRqs";
			td.insertAdjacentHTML("afterBegin", writeRqs);
			
			// 读(K)
			var td = detailDataTDArray[read_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "read";
			td.insertAdjacentHTML("afterBegin", read);
			
			// 写(K)
			var td = detailDataTDArray[write_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "write";
			td.insertAdjacentHTML("afterBegin", write);
			
			// 忙碌
			var td = detailDataTDArray[busy_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "busy";
			td.insertAdjacentHTML("afterBegin", busy);
		}
		
	},
	
	// ------------			设备磁盘信息    END					----------------------
	// -------------------------------------------------------------------------------
	
	
	// -------------------------------------------------------------------------------
	// ------------			设备作业信息    START					----------------------
	// 获取设备作业信息
	getJobInfo: function (){
		
		var jobTypeElement = $('jobType');
		var jobSubtypeElement = $('jobSubtype');
		var jobActivestatusElement = $('jobActivestatus');
		var jobSubsystemElement = $('jobSubsystem');
		var searchJobInfoElement = $('searchJobInfo');
		
		if(!searchJobInfoElement.onclick){
			searchJobInfoElement.onclick = function(){
				ObjectSelf.getJobInfo();
			}
		}
		
		if(jobTypeElement.options.length == 0){
			var getJobTypeInfo = function (){
				jobTypeElement.options[0] = new Option("不限", "-1");
				AS400RemoteService.getJobTypeInfo(this.nodeid, this.type, this.subtype, {
					callback:function(data){
						var jobTypeInfoList = data;
						var length = 0;
						if(jobTypeInfoList){
							length = jobTypeInfoList.length;
						}
						var i = 0;
						while(i < length){
							var jobTypeInfo = jobTypeInfoList[i];
							i++;
							jobTypeElement.options[i] = new Option(jobTypeInfo[1], jobTypeInfo[0]);
						}
					}
				});
			};
			getJobTypeInfo();
			jobTypeElement.value = "-1";
		}
		
		if(jobSubtypeElement.options.length == 0){
			var getJobSubtypeInfo = function (){
				jobSubtypeElement.options[0] = new Option("不限", "-1");
				AS400RemoteService.getJobSubtypeInfo(this.nodeid, this.type, this.subtype, {
					callback:function(data){
						var jobSubtypeInfoList = data;
						var length = 0;
						if(jobSubtypeInfoList){
							length = jobSubtypeInfoList.length;
						}
						var i = 0;
						while(i < length){
							var jobSubtypeInfo = jobSubtypeInfoList[i];
							i++;
							jobSubtypeElement.options[i] = new Option(jobSubtypeInfo[1], jobSubtypeInfo[0]);
						}
					}
				});
			};
			getJobSubtypeInfo();
			jobSubtypeElement.value = "-1";
		}
		
		if(jobActivestatusElement.options.length == 0){
			var getJobActivestatusInfo = function (){
				jobActivestatusElement.options[0] = new Option("不限", "-1");
				AS400RemoteService.getJobActiveStatusInfo(this.nodeid, this.type, this.subtype, {
					callback:function(data){
						var jobActiveStatusInfoList = data;
						var length = 0;
						if(jobActiveStatusInfoList){
							length = jobActiveStatusInfoList.length;
						}
						var i = 0;
						while(i < length){
							var jobActiveStatusInfo = jobActiveStatusInfoList[i];
							i++;
							jobActivestatusElement.options[i] = new Option(jobActiveStatusInfo[1], jobActiveStatusInfo[0]);
						}
					}
				});
			};
			getJobActivestatusInfo();
			jobActivestatusElement.value = "-1";
		}
		
		var jobTypeValue = jobTypeElement.value;
		var jobSubtypeValue = jobSubtypeElement.value;
		var jobActivestatusValue = jobActivestatusElement.value;
		var jobSubsystemValue = "";
		if(jobSubsystemElement){
			jobSubsystemValue = jobSubsystemElement.value;
			jobSubsystemElement.value = null;
		}
		
		AS400RemoteService.getJobInfo(this.nodeid, this.type, this.subtype, jobTypeValue, jobSubtypeValue, jobActivestatusValue, jobSubsystemValue, {
			callback:function(data){
				ObjectSelf.callbackgetJobInfo(data);
			}
		});
	},
	
	// 获取设备作业信息后处理的回调函数
	callbackgetJobInfo: function(data){
		this.data = data;
		this.createJobInfo();
	},
	
	// 获取设备作业信息后 创建设备作业信息
	createJobInfo: function(){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		
		var jobInfoList = this.data;
		var length = 0;
		if(jobInfoList){
			length = jobInfoList.length;
		}
		
		var currTabId = this.currTab.id;
		
		
		var index_cellIndex = $(currTabId + "-titleTD-" + "index").cellIndex;
		var name_cellIndex = $(currTabId + "-titleTD-" + "name").cellIndex;
		var user_cellIndex = $(currTabId + "-titleTD-" + "user").cellIndex;
		var type_cellIndex = $(currTabId + "-titleTD-" + "type").cellIndex;
		var subtype_cellIndex = $(currTabId + "-titleTD-" + "subtype").cellIndex;
		var CPUUsedTime_cellIndex = $(currTabId + "-titleTD-" + "CPUUsedTime").cellIndex;
		var activeStatus_cellIndex = $(currTabId + "-titleTD-" + "activeStatus").cellIndex;
		
		var i = 0;
		while(i < length){
			var jobInfo = jobInfoList[i];
			i++;
			var index = i;									// 序号
			var name = jobInfo.name;						// 子系统/任务
			var user = jobInfo.user;						// 用户
			var jobType = jobInfo.type;						// 类型
			var jobSubtype = jobInfo.subtype;				// 子类型
			var CPUUsedTime = jobInfo.CPUUsedTime;			// 占用CPU时间(毫秒)
			var activeStatus = jobInfo.activeStatus;		// 活动状态
			
			var tr = this.createElement(this.currTab.id + "-dataTR-" + index, "tr");
			tr.height = 25;
			listTBODY.appendChild(tr);
			var detailDataTDArray = this.createDetailDataTD(7, tr);
			
			// 序号
			var td = detailDataTDArray[index_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "index";
			td.insertAdjacentHTML("afterBegin", index);
			
			// 子系统/任务
			var td = detailDataTDArray[name_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "name";
			td.insertAdjacentHTML("afterBegin", name);
			
			// 用户
			var td = detailDataTDArray[user_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "user";
			td.insertAdjacentHTML("afterBegin", user);
			
			// 类型
			var td = detailDataTDArray[type_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "type";
			td.insertAdjacentHTML("afterBegin", jobType);
			
			// 子类型
			var td = detailDataTDArray[subtype_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "subtype";
			td.insertAdjacentHTML("afterBegin", jobSubtype);
			
			// 占用CPU时间(毫秒)
			var td = detailDataTDArray[CPUUsedTime_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "CPUUsedTime";
			td.insertAdjacentHTML("afterBegin", CPUUsedTime);
			
			// 活动状态
			var td = detailDataTDArray[activeStatus_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "activeStatus";
			td.insertAdjacentHTML("afterBegin", activeStatus);
			
		}
		
	},
	
	// ------------			设备作业信息    END					----------------------
	// -------------------------------------------------------------------------------
	
	
	// -------------------------------------------------------------------------------
	// ------------			设备子系统信息    START				----------------------
	// 获取设备子系统信息
	getSubsystemInfo: function (){
		AS400RemoteService.getSubsystemInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				ObjectSelf.callbackgetSubsystemInfo(data);
			}
		});
	},
	
	// 获取设备子系统信息后处理的回调函数
	callbackgetSubsystemInfo: function(data){
		this.data = data;
		this.createSubsystemInfo();
	},
	
	// 获取设备子系统信息后 创建设备子系统信息
	createSubsystemInfo: function(){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		
		var subsystemInfoList = this.data;
		var length = 0;
		if(subsystemInfoList){
			length = subsystemInfoList.length;
		}
		
		var currTabId = this.currTab.id;
		
		
		var index_cellIndex = $(currTabId + "-titleTD-" + "index").cellIndex;
		var name_cellIndex = $(currTabId + "-titleTD-" + "name").cellIndex;
		var jobNum_cellIndex = $(currTabId + "-titleTD-" + "jobNum").cellIndex;
		var path_cellIndex = $(currTabId + "-titleTD-" + "path").cellIndex;
		
		var i = 0;
		while(i < length){
			var subsystemInfo = subsystemInfoList[i];
			i++;
			var index = i;									// 序号
			var name = subsystemInfo.name;					// 名称
			var jobNum = subsystemInfo.jobNum;				// 作业数
			var path = subsystemInfo.path;					// 路径
			
			var tr = this.createElement(this.currTab.id + "-dataTR-" + index, "tr");
			tr.height = 25;
			listTBODY.appendChild(tr);
			var detailDataTDArray = this.createDetailDataTD(4, tr);
			
			// 序号
			var td = detailDataTDArray[index_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "index";
			td.insertAdjacentHTML("afterBegin", index);
			
			// 名称
			var td = detailDataTDArray[name_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "name";
			td.style.cursor = "hand";
			td.insertAdjacentHTML("afterBegin", "<a>" + name + "</a>");
			td.onclick = function(path){
				return function(){
					var titleList = null;
					for(var i = 0 ; i < ObjectSelf.tabList.length; i++){
						var tab = ObjectSelf.tabList[i];
						if(tab.id == "JobInfo"){
							titleList = tab.titleList;
						}
					}
					var jobSubsystem = $("jobSubsystem");
					if(!jobSubsystem){
						jobSubsystem = ObjectSelf.createElement("jobSubsystem", "input");
						jobSubsystem.type = "hidden";
					}
					jobSubsystem.value = path;
					ObjectSelf.detailDataTABLE.appendChild(jobSubsystem);
					ObjectSelf.createTitleInfo(titleList);
					ObjectSelf.getJobInfo();
				}
			}(path)
			
			// 作业数
			var td = detailDataTDArray[jobNum_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "jobNum";
			td.insertAdjacentHTML("afterBegin", jobNum);
			
			// 路径
			var td = detailDataTDArray[path_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "path";
			td.insertAdjacentHTML("afterBegin", path);
		}
		
	},
	
	// ------------			设备设备子系统信息  END					----------------------
	// -------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------
	// ------------			设备Syslog信息    START				----------------------
	// 获取设备Syslog信息
	getSyslogInfo: function (){
		
		var startdateElement = $('startdate');				// 获取开始日期
		var todateElement = $('todate');					// 获取截止日期	
		var prioritynameElement = $('priorityname');		// 获取日志等级
		var searchSyslogInfoElement = $('searchSyslogInfo');
		
		if(!searchSyslogInfoElement.onclick){
			searchSyslogInfoElement.onclick = function(){
				ObjectSelf.getSyslogInfo();
			}
		}
		
		var date = new Date();
		if(!startdateElement.value){
			startdateElement.value = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
		}
		
		if(!todateElement.value){
			 todateElement.value = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
		}
		
		var startdateValue = startdateElement.value;
		var todateValue = todateElement.value;
		var prioritynameValue = null;
		
		if(prioritynameElement){
			 prioritynameValue = prioritynameElement.value;
		}
		
		AS400RemoteService.getSyslogInfo(this.nodeid, this.type, this.subtype, 
			startdateValue, todateValue, prioritynameValue, {
				callback:function(data){
					ObjectSelf.callbackgetSyslogInfo(data);
				}
		});
	},
	
	// 获取设备Syslog信息后处理的回调函数
	callbackgetSyslogInfo: function(data){
		this.data = data;
		this.createSyslogInfo();
	},
	
	// 获取设备Syslog信息后 创建设备Syslog信息
	createSyslogInfo: function(){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		var syslogInfoList = this.data;
		
		if(!syslogInfoList || !syslogInfoList.length){
			return;
		}
		
		var currTabId = this.currTab.id;
		var index_cellIndex = $(currTabId + "-titleTD-" + "index").cellIndex;
		var priorityName_cellIndex = $(currTabId + "-titleTD-" + "priorityName").cellIndex;
		var recordtime_cellIndex = $(currTabId + "-titleTD-" + "recordtime").cellIndex;
		var facility_cellIndex = $(currTabId + "-titleTD-" + "facility").cellIndex;
		var processname_cellIndex = $(currTabId + "-titleTD-" + "processname").cellIndex;
		var eventid_cellIndex = $(currTabId + "-titleTD-" + "eventid").cellIndex;
		var username_cellIndex = $(currTabId + "-titleTD-" + "username").cellIndex;
		var hostname_cellIndex = $(currTabId + "-titleTD-" + "hostname").cellIndex;
		var showDetail_cellIndex = $(currTabId + "-titleTD-" + "showDetail").cellIndex;
		
		var length = syslogInfoList.length;
		var i = 0
		while(i < length){
			var syslogInfo = syslogInfoList[i];
			i++;
			var id = syslogInfo.id;									// ID
			var ipaddress = syslogInfo.ipaddress;					// IP
			var index = i;											// 序号
			var priorityName = syslogInfo.priorityName;				// 类型
			var recordtime = syslogInfo.recordtime;					// 日期
			var facility = syslogInfo.facility;						// 来源
			var processname = syslogInfo.processname;				// 分类
			var eventid = syslogInfo.eventid;						// 事件
			var username = syslogInfo.username;						// 用户
			var hostname = syslogInfo.hostname;						// 计算机
			var showDetail = null;									// 查看
			
			var tr = this.createElement(this.currTab.id + "-dataTR-" + index, "tr");
			tr.height = 25;
			listTBODY.appendChild(tr);
			var detailDataTDArray = this.createDetailDataTD(9, tr);
			
			// 序号
			var td = detailDataTDArray[index_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "index";
			td.insertAdjacentHTML("afterBegin", index);
			
			// 类型
			var td = detailDataTDArray[priorityName_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "priorityName";
			td.insertAdjacentHTML("afterBegin", priorityName);
			
			// 日期
			var recordtime = new Date(recordtime);
			var td = detailDataTDArray[recordtime_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "recordtime";
			td.insertAdjacentHTML("afterBegin", recordtime.getFullYear() + '-' + (recordtime.getMonth() + 1) + '-' + recordtime.getDate()+ 
				' ' + recordtime.getHours() + ':' + recordtime.getMinutes() + ':' + recordtime.getSeconds());
			
			// 来源
			var td = detailDataTDArray[facility_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "facility";
			td.insertAdjacentHTML("afterBegin", facility);
			
			// 分类
			var td = detailDataTDArray[processname_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "processname";
			td.insertAdjacentHTML("afterBegin", processname);
			
			// 事件
			var td = detailDataTDArray[eventid_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "eventid";
			td.insertAdjacentHTML("afterBegin", eventid);
			
			// 用户
			var td = detailDataTDArray[username_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "username";
			td.insertAdjacentHTML("afterBegin", username);
			
			// 计算机
			var td = detailDataTDArray[hostname_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "hostname";
			td.insertAdjacentHTML("afterBegin", hostname);
			
			// 查看详情
			var td = detailDataTDArray[showDetail_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "showDetail";
			var button = this.createElement("", "input");
			button.type = "button";
			button.value = "查看详情";
			td.appendChild(button);
			button.onclick = function(id, ipaddress){
					return function(){
						ObjectSelf.showSyslogInfoDetail(id, ipaddress);
					};
			}(id, ipaddress);
		}
		
	},
	
	showSyslogInfoDetail: function(id, ipaddress){
		window.open(this.rootPath + '/monitor.do?action=hostsyslogdetail' +
				'&id=' + id + '&ipaddress=' + ipaddress,
				"protypeWindow",
				"toolbar=no,width=500,height=400,directories=no,status=no,scrollbars=yes,menubar=no");
	},
	
	// ------------			设备Syslog信息    END					----------------------
	// -------------------------------------------------------------------------------
	
	
	// -------------------------------------------------------------------------------
	// ------------			设备设备信息    START				----------------------
	// 获取设备设备信息
	getDeviceInfo: function (){
		AS400RemoteService.getDeviceInfo(this.nodeid, this.type, this.subtype, {
				callback:function(data){
					ObjectSelf.callbackgetDeviceInfo(data);
				}
		});
	},
	
	// 获取设备设备信息后处理的回调函数
	callbackgetDeviceInfo: function(data){
		this.data = data;
		this.createDeviceInfo();
	},
	
	// 获取设备设备信息后 创建设备设备信息
	createDeviceInfo: function(){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		var deviceInfoList = this.data;
		if(!deviceInfoList || !deviceInfoList.length){
			return;
		}
		var currTabId = this.currTab.id;
		var index_cellIndex = $(currTabId + "-titleTD-" + "index").cellIndex;
		var dtype_cellIndex = $(currTabId + "-titleTD-" + "dtype").cellIndex;
		var name_cellIndex = $(currTabId + "-titleTD-" + "name").cellIndex;
		var status_cellIndex = $(currTabId + "-titleTD-" + "status").cellIndex;
		
		var length = deviceInfoList.length;
		var i = 0
		while(i < length){
			var deviceInfo = deviceInfoList[i];
			i++;
			var index = i;									// 序号
			//var deviceindex = deviceInfo.deviceindex;		//
			var dtype = deviceInfo.dtype;					// 设备类型
			var name = deviceInfo.name;						// 描述
			var status = deviceInfo.status;					// 状态
			
			var tr = this.createElement(this.currTab.id + "-dataTR-" + index, "tr");
			tr.height = 25;
			listTBODY.appendChild(tr);
			var detailDataTDArray = this.createDetailDataTD(4, tr);
			
			// 序号
			var td = detailDataTDArray[index_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "index";
			td.insertAdjacentHTML("afterBegin", index);
			
			// 设备类型
			var td = detailDataTDArray[dtype_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "dtype";
			td.insertAdjacentHTML("afterBegin", dtype);
			
			// 描述
			var td = detailDataTDArray[name_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "name";
			td.insertAdjacentHTML("afterBegin", name);
			
			// 状态
			var td = detailDataTDArray[status_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "status";
			td.insertAdjacentHTML("afterBegin", status);
			
		}
		
	},
	
	// ------------			设备设备信息    END					----------------------
	// -------------------------------------------------------------------------------
	
	
	// -------------------------------------------------------------------------------
	// ------------			设备告警信息     START					----------------------
	// 获取设备告警信息
	getAlarmInfo: function(){
		var startdateElement = $('startdate');
		var todateElement = $('todate');
		var level1Element = $('level1');
		var stautsElement = $('event_status');
		var searchAlarmInfoElement = $('searchAlarmInfo');
		
		if(!searchAlarmInfoElement.onclick){
			searchAlarmInfoElement.onclick = function(){
				ObjectSelf.getAlarmInfo();
			}
			
		}
		
		var date = new Date();
		if(!startdateElement.value){
			startdateElement.value = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
		}
		
		if(!todateElement.value){
			 todateElement.value = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
		}
		
		var level1Value = null;
		var stautsValue = null;
		var startdateValue = startdateElement.value;
		var todateValue = todateElement.value
		
		if(level1Element){
			 level1Value = level1Element.value;
		}
		
		if(stautsElement){
			stautsElement.value = "-1";
			stautsValue = stautsElement.value;
		}
		
		AS400RemoteService.getAlarmInfo(this.nodeid, this.type, this.subtype, 
			startdateValue, todateValue, level1Value, null, {
				callback:function(data){
					ObjectSelf.callbackgetAlarmInfo(data);
				}
		});
	},
	
	// 获取设备告警信息后处理的回调函数
	callbackgetAlarmInfo: function(data){
		this.data = data;
		this.createAlarmInfo();
		
	},
	
	// 获取设备告警信息后创建告警信息
	createAlarmInfo: function(){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		
		
		var eventList = this.data;
		if(!eventList || !eventList.length){
			return;
		}
		var currTabId = this.currTab.id;
		var index_cellIndex = $(currTabId + "-titleTD-" + "index").cellIndex;
		var level1_cellIndex = $(currTabId + "-titleTD-" + "level1").cellIndex;
		var content_cellIndex = $(currTabId + "-titleTD-" + "content").cellIndex;
		//var recordtime_cellIndex = $(currTabId + "-titleTD-" + "recordtime").cellIndex;
		//var eventtype_cellIndex = $(currTabId + "-titleTD-" + "eventtype").cellIndex;
		//var managesign_cellIndex = $(currTabId + "-titleTD-" + "managesign").cellIndex;
		var maxtime_cellIndex = $(currTabId + "-titleTD-" + "maxtime").cellIndex;
		var count_cellIndex = $(currTabId + "-titleTD-" + "count").cellIndex;
		var operation_cellIndex = $(currTabId + "-titleTD-" + "operation").cellIndex;
		
		var length = eventList.length;
		var i = 0;
		while(i < length){
			var eventListInfo = eventList[i];
			i++;
			
			var event = eventListInfo[0];
			var maxtime = eventListInfo[2];				// 最新时间
			var count = eventListInfo[1];				// 次数
			var eventId = event.id;						// 事件ID
			var index = i;								// 序号
			var level1 = event.level1;					// 事件等级
			var content = event.content;				// 事件描述
			var eventlocation = event.eventlocation;	// 事件来源
			var subentity = event.subentity;			// 事件指标
			var operation = null;						// 根据处理状态来获取操作
			
			var tr = this.createElement("", "tr");
			tr.height = 25;
			listTBODY.appendChild(tr);
			var detailDataTDArray = this.createDetailDataTD(6, tr);
			
			// 序号
			var td = detailDataTDArray[index_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "index";
			td.insertAdjacentHTML("afterBegin", index);
			
			// 事件等级
			var td = detailDataTDArray[level1_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "level1";
			var level_str = "";
			if("3" == level1){
				level_str = "紧急事件";
				td.style.backgroundColor = "red"
			} else if("2" == level1){
				level_str = "严重事件";
				td.style.backgroundColor = "orange";
			} else if("1" == level1){
				level_str = "普通事件";
				td.style.backgroundColor = "yellow";
			}
			td.insertAdjacentHTML("afterBegin", level_str);
			
			// 事件描述
			var td = detailDataTDArray[content_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "content";
			td.insertAdjacentHTML("afterBegin", content);
			
			// 最新时间
			var td = detailDataTDArray[maxtime_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "maxtime";
			td.insertAdjacentHTML("afterBegin", maxtime);
			
			// 次数
			var td = detailDataTDArray[count_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "count";
			td.insertAdjacentHTML("afterBegin", count);
			
			// 操作
			var td = detailDataTDArray[operation_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "operation";
			var button = this.createElement("", "input");
			button.type = "button";
			td.appendChild(button);
			button.value = "查看详情";
			button.onclick = function(eventlocation, level1, subentity){
				return function(){
					var searchAlarmInfoElement = $('searchAlarmInfo');
					var level1Element = $('level1');
					level1Element.value = level1;
					if(searchAlarmInfoElement){
						searchAlarmInfoElement.onclick = function(){
							ObjectSelf.showAlarmDetailInfo(eventlocation, subentity);
						}
					}
					ObjectSelf.showAlarmDetailInfo(eventlocation, subentity);
				};
			}(eventlocation, level1, subentity);
		}
	},
	
	showAlarmDetailInfo: function(eventlocation, subentity){
		var startdateElement = $('startdate');
		var todateElement = $('todate');
		var level1Element = $('level1');
		var stautsElement = $('event_status');
		var startdateValue = startdateElement.value;
		var todateValue = todateElement.value;
		var level1Value = level1Element.value;
		var stautsValue = stautsElement.value;
		
		var titleTBODY = this.detailDataTABLE.tBodies[0];
		if(!titleTBODY){
			this.createDetailDataTABLE();
		}
		titleTBODY.deleteRow(1);
		var titleTR = this.createElement(this.currTab.id + "titleTR-1", "tr")
		titleTBODY.appendChild(titleTR);
		var titleArray = ["序号", "事件等级", "事件描述", "登记日期", "登记人", "处理状态", "操作"];
		var titleIDArray = ["index", "level1", "content", "recordtime", "eventtype", "managesign", "operation"];
		var length = titleArray.length;
		titleTBODY.rows[0].cells[0].colSpan = length;
		var i = 0;
		while(i < length){
			var titleTD = this.createElement(this.currTab.id + "-titleTD-" + titleIDArray[i], "td");
			titleTD.style.height = 25;
			titleTD.className = "body-data-title";
			titleTD.insertAdjacentHTML("afterBegin", titleArray[i]);  
			titleTR.appendChild(titleTD);
			i++;
		}
		
		AS400RemoteService.getAlarmDetailInfo(this.nodeid, this.type, this.subtype, 
			startdateValue, todateValue, level1Value, eventlocation, subentity, stautsValue, {
				callback:function(data){
					ObjectSelf.data = data;
					ObjectSelf.createAlarmDetailInfo();
				}
		});
	},
	
	
	createAlarmDetailInfo: function(){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		
		
		var eventList = this.data;
		if(!eventList || !eventList.length){
			return;
		}
		var currTabId = this.currTab.id;
		var index_cellIndex = $(currTabId + "-titleTD-" + "index").cellIndex;
		var level1_cellIndex = $(currTabId + "-titleTD-" + "level1").cellIndex;
		var content_cellIndex = $(currTabId + "-titleTD-" + "content").cellIndex;
		var recordtime_cellIndex = $(currTabId + "-titleTD-" + "recordtime").cellIndex;
		var eventtype_cellIndex = $(currTabId + "-titleTD-" + "eventtype").cellIndex;
		var managesign_cellIndex = $(currTabId + "-titleTD-" + "managesign").cellIndex;
		var operation_cellIndex = $(currTabId + "-titleTD-" + "operation").cellIndex;
		
		var length = eventList.length;
		var i = 0;
		while(i < length){
			var event = eventList[i];
			i++;
			var eventId = event.id;						// 事件ID
			var index = i;								// 序号
			var level1 = event.level1;					// 事件等级
			var content = event.content;				// 事件描述
			var recordtime = event.recordtime;			// 登记日期
			var eventtype = event.eventtype;			// 登记人
			var managesign = event.managesign;			// 处理状态
			var operation = null;						// 根据处理状态来获取操作
			
			var tr = this.createElement("", "tr");
			tr.height = 25;
			listTBODY.appendChild(tr);
			var detailDataTDArray = this.createDetailDataTD(7, tr);
			
			// 序号
			var td = detailDataTDArray[index_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "index";
			td.insertAdjacentHTML("afterBegin", index);
			
			// 事件等级
			var td = detailDataTDArray[level1_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "level1";
			if("3" == level1){
				level1 = "紧急事件";
				td.style.backgroundColor = "red"
			} else if("2" == level1){
				level1 = "严重事件";
				td.style.backgroundColor = "orange";
			} else if("1" == level1){
				level1 = "普通事件";
				td.style.backgroundColor = "yellow";
			}
			td.insertAdjacentHTML("afterBegin", level1);
			
			// 事件描述
			var td = detailDataTDArray[content_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "content";
			td.insertAdjacentHTML("afterBegin", content);
			
			// 登记日期
			var recordtime = new Date(recordtime);
			var td = detailDataTDArray[recordtime_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "recordtime";
			td.insertAdjacentHTML("afterBegin", 
				recordtime.getFullYear() + '-' + (recordtime.getMonth() + 1) + '-' + recordtime.getDate()+ 
				' ' + recordtime.getHours() + ':' + recordtime.getMinutes() + ':' + recordtime.getSeconds());
			
			// 登记人
			var td = detailDataTDArray[eventtype_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "eventtype";
			td.insertAdjacentHTML("afterBegin", eventtype);
			
			// 处理状态
			var operationStatus = "";
			if("0" == managesign){
		  		operationStatus = "未处理";
		  	}else if("1" == managesign){
		  		operationStatus = "处理中";  	
		  	}else if("2" == eventStatus){
		  	  	operationStatus = "处理完成";
		  	}
			var td = detailDataTDArray[managesign_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "managesign";
			td.insertAdjacentHTML("afterBegin", operationStatus);
			
			// 操作
			var td = detailDataTDArray[operation_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "operation";
			var button = this.createElement("", "input");
			button.type = "button";
			
			if("0" == managesign){
				button.value = "接受处理";
				button.onclick = function(eventId){
					return function(){
						alert("接受处理==eventId" + eventId)
						};
				}(eventId);
		  	}else if("1" == managesign){
		  		button.value = "填写报告";
		  		button.onclick = function(eventId){
					return function(){
						alert("填写报告==eventId" + eventId)
						};
				}(eventId);
		  	}else if("2" == managesign){
		  		button.value = "查看报告";
		  		button.onclick = function(eventId){
					return function(){
						alert("查看报告==eventId" + eventId)
						};
				}(eventId);
		  	}
		}
	},
	
	// ------------			设备告警信息    END					----------------------
	// -------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------
	// ------------			以上为各个 tab 页所需要的信息			----------------------
	// -------------------------------------------------------------------------------
	
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
	},
	
	
	// 获取设备的信息后 将设备信息按照字段排序
	// titleTD -- 标题的 td
	sortDataTR: function(titleTD){
		var dataTRRows = this.detailDataTABLE.tBodies[1].rows;
		var dataTRArray = new Array();
		var title_cellIndex = titleTD.cellIndex;
		for(var i = 0; i < dataTRRows.length; i++){
			dataTRArray.push(dataTRRows[i]);
		}
		
		if( !this.dataTROrder || this.dataTROrder == "desc"){
			// 如果当前 排列顺序 this.dataTROrder 为 null  
			// 或则当前 排列顺序 this.dataTROrder 为 desc (递减) 则将赋为 asc (递增)
			this.dataTROrder = "asc";
		} else if (this.dataTROrder == "asc"){
			// 如果当前 排列顺序 this.dataTROrder 为 asc (递增) 则将赋为 desc (递减)
			this.dataTROrder = "desc";
		}
		
		var new_dataTRArray = dataTRArray.sort(function(previousDataTR, dataTR){
			
			var previousDataTR_value = previousDataTR.cells(title_cellIndex).innerText;
			var dataTR_value = dataTR.cells(title_cellIndex).innerText;
			
			var previousDataTR_value_int = parseInt(previousDataTR_value);
			if(isNaN(previousDataTR_value_int)){
				previousDataTR_value_int = 0;
			}
			
			var dataTR_value_int = parseInt(dataTR_value);
			if(isNaN(dataTR_value_int)){
				dataTR_value_int = 0;
			}
			
			if(!ObjectSelf.dataTROrder || ObjectSelf.dataTROrder == "asc"){
				// 如果当前 排列顺序 this.dataTROrder 为 null  
				// 或则当前 排列顺序 this.dataTROrder 为 asc 为 (递增)
				return previousDataTR_value_int - dataTR_value_int;
			} else if (ObjectSelf.dataTROrder == "desc"){
				// 如果当前 排列顺序 this.dataTROrder 为 desc (递减)
				return dataTR_value_int - previousDataTR_value_int;
			}
		});
		this.removeAllChild(this.detailDataTABLE.tBodies[1]);
		
		for(var i = 0; i < new_dataTRArray.length; i++){
			this.detailDataTABLE.tBodies[1].appendChild(new_dataTRArray[i]);
		}
	}
}

function useLoadingMessage(message) { 
	var loadingMessage; 
	if (message) loadingMessage = message; 
	else loadingMessage = "Loading"; 
	DWREngine.setPreHook(function() {
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
		$('disabledZone').style.visibility = 'hidden';
	}); 
}