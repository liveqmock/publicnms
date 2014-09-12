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
	useLoadingMessage("Loading"); 
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
	this.Envoriment_componentTemperSWFkey = "temperature";
	
	this.Envoriment_componentTemperSWFTable = "temper";
	
	// 详细信息中 数据列表中 排列的顺序 
	// 递增 -- asc
	// 递减 -- desc
	this.dataTROrder = null;
	
	// 从后台异步获取的数据
	this.data = null;
		
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
		
		if(!this.detailDataDIV){
			alert("请在页面中创建一个 id=detailDataDIV 的 div 用于存放当前标签页的详细数据");
			return;
		}
		
		var freshSystemInfo = $('freshSystemInfo');
		// 添加 刷性系统信息的方法
		if(freshSystemInfo){
			freshSystemInfo.onclick = function (){
				NetRemoteService.getHostNodeInfo(ObjectSelf.nodeid, ObjectSelf.type, ObjectSelf.subtype, {
					callback:function(data){
						ObjectSelf.hostNode =  data;
						ObjectSelf.getSystemInfo();
					}
				});
			}
		}
		
		NetRemoteService.getHostNodeInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				ObjectSelf.hostNode =  data;
				ObjectSelf.getSystemInfo();
			}
		});
		
		this.getTabInfo();
		
	},
	
	// 获取设备系统信息
	getSystemInfo: function (){
		NetRemoteService.getSystemInfo(this.nodeid, this.type, this.subtype, {
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
		
		NetRemoteService.getCurrDayPingAvgInfo(this.nodeid, this.type, this.subtype, {
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
		
		NetRemoteService.getCurrCpuAvgInfo(this.nodeid, this.type, this.subtype, {
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
		NetRemoteService.getStautsInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				var statusImg = "";
				if(data){
					statusImg = data;
				}
				$('systemInfo_stauts').innerHTML = "<img src=\"" + ObjectSelf.rootPath + "/resource/" + statusImg+ "\">";
			}
		});
		NetRemoteService.getCategoryInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				var category = "";
				if(data){
					category = data;
				}
				$('systemInfo_category').innerHTML = category;
			}
		});
		
		NetRemoteService.getSupperInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				var supperInfo = "";
				if(data){
					supperInfo = data;
				}
				//alert(supperInfo);
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
	
	// 处理单击标签页的函数
	setCurrTab: function (tab){
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
					titleTR = this.createElement(this.currTab.id + "titleTR-" + titleTRRow, "tr")
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
	
	// -------------------------------------------------------------------------------
	// ------------			以下为各个 tab 页所需要的信息			----------------------
	// -------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------
	// ------------			设备流速信息    START					----------------------
	// 获取设备流速信息
	getInterfaceInfo: function(){
		NetRemoteService.getInterfaceInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				ObjectSelf.callbackgetInterfaceInfo(data);
			}
		});
	},
	
	// 获取设备流速信息后处理的回调函数
	callbackgetInterfaceInfo: function(data){
		this.data = data;
		this.createInterfaceInfo();
	},
	
	// 获取设备流速信息后 创建设备流速信息
	createInterfaceInfo: function(){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现这种情况)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		
		var interfaceInfoList = this.data;
		var length = interfaceInfoList.length;
		if(!interfaceInfoList || interfaceInfoList.length == 0){
			return;
		}
		var currTabId = this.currTab.id;
		var index_cellIndex = $(currTabId + "-titleTD-" + "index").cellIndex;
		var ifDescr_cellIndex = $(currTabId + "-titleTD-" + "ifDescr").cellIndex;
		var ifSpeed_cellIndex = $(currTabId + "-titleTD-" + "ifSpeed").cellIndex;
		var ifOperStatus_cellIndex = $(currTabId + "-titleTD-" + "ifOperStatus").cellIndex;
		var ifOutBroadcastPkts_cellIndex = $(currTabId + "-titleTD-" + "ifOutBroadcastPkts").cellIndex;
		var ifInBroadcastPkts_cellIndex = $(currTabId + "-titleTD-" + "ifInBroadcastPkts").cellIndex;
		var ifOutMulticastPkts_cellIndex= $(currTabId + "-titleTD-" + "ifOutMulticastPkts").cellIndex;
		var ifInMulticastPkts_cellIndex = $(currTabId + "-titleTD-" + "ifInMulticastPkts").cellIndex;
		var OutBandwidthUtilHdx_titleTD = $(currTabId + "-titleTD-" + "OutBandwidthUtilHdx");
		var OutBandwidthUtilHdx_cellIndex = OutBandwidthUtilHdx_titleTD.cellIndex;
		var InBandwidthUtilHdx_titleTD = $(currTabId + "-titleTD-" + "InBandwidthUtilHdx");
		var InBandwidthUtilHdx_cellIndex = InBandwidthUtilHdx_titleTD.cellIndex;
		var showDetail_cellIndex = $(currTabId + "-titleTD-" + "showDetail").cellIndex;
		
		OutBandwidthUtilHdx_titleTD.style.cursor = "hand";
		OutBandwidthUtilHdx_titleTD.onclick = function(){
			// 添加 点击排序 事件
			ObjectSelf.sortDataTR(this);
		};
		
		InBandwidthUtilHdx_titleTD.style.cursor = "hand";
		InBandwidthUtilHdx_titleTD.onclick = function(){
			// 添加 点击排序 事件
			ObjectSelf.sortDataTR(this);
		}
		var i = 0;
		while(i < length){
			var interfaceInfo = interfaceInfoList[i];
			i++;
			var index = i;													// 序号
			var sindex = interfaceInfo.sindex;								// 索引
			var ifDescr = interfaceInfo.ifDescr;							// 描述
			var ifSpeed = interfaceInfo.ifSpeed;							// 每秒字节数(M)
			var ifOperStatus = interfaceInfo.ifOperStatus;					// 状态
			var ifOutBroadcastPkts = interfaceInfo.ifOutBroadcastPkts;		// 出口广播数据包
			var ifInBroadcastPkts = interfaceInfo.ifInBroadcastPkts;		// 入口广播数据包
			var ifOutMulticastPkts = interfaceInfo.ifOutMulticastPkts;		// 出口多播数据包
			var ifInMulticastPkts = interfaceInfo.ifInMulticastPkts;		// 入口多播数据包
			var OutBandwidthUtilHdx = interfaceInfo.outBandwidthUtilHdx;	// 出口流速
			var InBandwidthUtilHdx = interfaceInfo.inBandwidthUtilHdx;		// 入口流速		
			
			var tr = this.createElement(this.currTab.id + "-dataTR-" + index, "tr");
			tr.height = 25;
			listTBODY.appendChild(tr);
			var detailDataTDArray = this.createDetailDataTD(11, tr);
			
			// 索引
			var td = detailDataTDArray[index_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "sindex";
			td.insertAdjacentHTML("afterBegin", sindex);
			
			// 描述
			var td = detailDataTDArray[ifDescr_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "ifDescr";
			td.insertAdjacentHTML("afterBegin", ifDescr);
			
			// 每秒字节数(M)
			var td = detailDataTDArray[ifSpeed_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "ifSpeed";
			td.insertAdjacentHTML("afterBegin", ifSpeed);
			
			// 状态
			var td = detailDataTDArray[ifOperStatus_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "ifOperStatus";
			td.insertAdjacentHTML("afterBegin", ifOperStatus);
			
			// 出口广播数据包
			var td = detailDataTDArray[ifOutBroadcastPkts_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "ifOutBroadcastPkts";
			td.insertAdjacentHTML("afterBegin", ifOutBroadcastPkts);
			
			// 入口广播数据包
			var td = detailDataTDArray[ifInBroadcastPkts_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "ifInBroadcastPkts";
			td.insertAdjacentHTML("afterBegin", ifInBroadcastPkts);
			
			// 出口多播数据包
			var td = detailDataTDArray[ifOutMulticastPkts_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "ifOutMulticastPkts";
			td.insertAdjacentHTML("afterBegin", ifOutMulticastPkts);
			
			// 入口多播数据包
			var td = detailDataTDArray[ifInMulticastPkts_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "ifInMulticastPkts";
			td.insertAdjacentHTML("afterBegin", ifInMulticastPkts);
			
			// 出口流速
			var td = detailDataTDArray[OutBandwidthUtilHdx_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "OutBandwidthUtilHdx";
			td.insertAdjacentHTML("afterBegin", OutBandwidthUtilHdx);
			
			// 入口流速
			var td = detailDataTDArray[InBandwidthUtilHdx_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "InBandwidthUtilHdx";
			td.insertAdjacentHTML("afterBegin", InBandwidthUtilHdx);
			
			// 查看详情
			var td = detailDataTDArray[showDetail_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "showDetail";
			var img = this.createElement("", "img");
			td.appendChild(img);
			img.src = this.rootPath + "/resource/image/status.gif";
			img.oncontextmenu = function(sindex, ifDescr){
					return function(){
						ObjectSelf.showInterfaceInfoMenu(sindex, ifDescr);
					};
				}(sindex, ifDescr);
		}
		
		
	},
	
	// 获取设备流速信息后 右键详情
	// rowControlString			-- 行控制字符串，0表示不显示，1表示显示，如“101”，则表示第1、3行显示，第2行不显示 暂时写死
	// InterfaceInfoIndex 		-- 端口索引
	// InterfaceInfoIfDescr 	-- 端口描述
	showInterfaceInfoMenu: function(InterfaceInfoIndex, InterfaceInfoIfDescr){
		var showInterfaceInfoDetail = $('showInterfaceInfoDetail');
		if(showInterfaceInfoDetail){
			showInterfaceInfoDetail.onclick = function(){
				window.open(ObjectSelf.rootPath + "/monitor.do?action=show_utilhdx&" +
						"id=" + ObjectSelf.nodeid + 
						"&ipaddress=" + ObjectSelf.hostNode.ipAddress + 
						"&ifindex=" + InterfaceInfoIndex + "&ifname=" + InterfaceInfoIfDescr, 
						"newwindow", "height=350, width=840, toolbar= no, menubar=no, scrollbars=yes, resizable=yes, location=yes, status=yes");
			};
		}
		var showInterfaceInfoportset = $('showInterfaceInfoportset');
		if(showInterfaceInfoportset){
			$('showInterfaceInfoportset').onclick = function(){
				window.open(ObjectSelf.rootPath + "/panel.do?action=show_portreset&" +
						"id=" + ObjectSelf.nodeid + 
						"&ipaddress=" + ObjectSelf.hostNode.ipAddress + 
						"&ifindex=" + InterfaceInfoIndex + "&ifname="+ InterfaceInfoIfDescr, 
						"newwindow", "height=200, width=600, toolbar= no, menubar=no, scrollbars=yes, resizable=yes, location=yes, status=yes");
			};
		}
		popMenu("InterfaceInfo_itemMenu", 100, "11");
		return false;
	},
	
	// ------------			设备流速信息    END					----------------------
	// -------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------
	// ------------			设备性能信息    START					----------------------
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
	
	// ------------			设备性能信息    END					----------------------
	// -------------------------------------------------------------------------------
	
	
	// -------------------------------------------------------------------------------
	// ------------			设备ARP信息    START					----------------------
	// 获取设备 ARP 信息
	getARPInfo: function (){
		NetRemoteService.getARPInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				ObjectSelf.callbackgetARPInfo(data);
			}
		});
	},
	
	// 获取设备 ARP 信息后处理的回调函数
	callbackgetARPInfo: function(data){
		this.data = data;
		this.createARPInfo();
	},
	
	// 获取设备 ARP 信息后 创建设备 ARP 信息
	createARPInfo: function(){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		
		var ipMacInfoList = this.data;
		var length = ipMacInfoList.length;
		if(!ipMacInfoList || length == 0){
			return;
		}
		
		var currTabId = this.currTab.id;
		var index_cellIndex = $(currTabId + "-titleTD-" + "index").cellIndex;
		var ifindex_cellIndex = $(currTabId + "-titleTD-" + "ifindex").cellIndex;
		var ipaddress_cellIndex = $(currTabId + "-titleTD-" + "ipaddress").cellIndex;
		var mac_cellIndex = $(currTabId + "-titleTD-" + "mac").cellIndex;
		var collecttime_cellIndex = $(currTabId + "-titleTD-" + "collecttime").cellIndex;
		var ifband_cellIndex = $(currTabId + "-titleTD-" + "ifband").cellIndex;
		
		var i = 0;
		while(i < length){
			var ipMacInfo = ipMacInfoList[i];
			i++;
			var index = i;
		    var id = ipMacInfo.id;							// id
		    var relateipaddr = ipMacInfo.relateipaddr;		// 所属设备 ip
		    var ifindex = ipMacInfo.ifindex;				// 索引
		    var ipaddress = ipMacInfo.ipaddress;			// ipaddress
		    var mac = ipMacInfo.mac;						// MAC
		    var collecttime = ipMacInfo.collecttime;		// 扫描时间
		    var ifband = ipMacInfo.ifband;					// 基线
		    var ifsms = ipMacInfo.ifsms;					// ifsms
		    var bak = ipMacInfo.bak;						// bak
		    
		    var tr = this.createElement(this.currTab.id + "-dataTR-" + index, "tr");
			tr.height = 25;
			listTBODY.appendChild(tr);
			var detailDataTDArray = this.createDetailDataTD(7, tr);
			
			// 序号
			var td = detailDataTDArray[index_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "index";
			td.insertAdjacentHTML("afterBegin", index);
			
			// 索引
			var td = detailDataTDArray[ifindex_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "ifindex";
			td.insertAdjacentHTML("afterBegin", ifindex);
			
			// ipaddress
			var td = detailDataTDArray[ipaddress_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "ipaddress";
			td.insertAdjacentHTML("afterBegin", ipaddress);
			
			// MAC
			var td = detailDataTDArray[mac_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "mac";
			td.insertAdjacentHTML("afterBegin", mac);
			
			// 扫描时间
			var recordtime = new Date(collecttime);
			var td = detailDataTDArray[collecttime_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "collecttime";
			td.insertAdjacentHTML("afterBegin", recordtime.getFullYear() + '-' + (recordtime.getMonth() + 1) + '-' + recordtime.getDate()+ 
				' ' + recordtime.getHours() + ':' + recordtime.getMinutes() + ':' + recordtime.getSeconds());
		}
	},
	
	// ------------			设备ARP信息    END					----------------------
	// -------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------
	// ------------			设备FDB信息    START					----------------------
	// 获取设备 FDB 信息
	getFDBInfo: function (){
		NetRemoteService.getFDBInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				ObjectSelf.callbackgetFDBInfo(data);
			}
		});
	},
	
	// 获取设备 FDB 信息后处理的回调函数
	callbackgetFDBInfo: function(data){
		this.data = data;
		this.createFDBInfo();
	},
	
	// 获取设备 FDB 信息后 创建设备 FDB 信息
	createFDBInfo: function(){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		
		var fdbInfoList = this.data;
		var length = fdbInfoList.length;
		if(!fdbInfoList || length == 0){
			return;
		}
		
		var currTabId = this.currTab.id;
		var index_cellIndex = $(currTabId + "-titleTD-" + "index").cellIndex;
		var ifindex_cellIndex = $(currTabId + "-titleTD-" + "ifindex").cellIndex;
		var ipaddress_cellIndex = $(currTabId + "-titleTD-" + "ipaddress").cellIndex;
		var mac_cellIndex = $(currTabId + "-titleTD-" + "mac").cellIndex;
		var collecttime_cellIndex = $(currTabId + "-titleTD-" + "collecttime").cellIndex;
		
		var i = 0;
		while(i < length){
			var fdbInfo = fdbInfoList[i];
			i++;
			//var id = FdbNodeTemp.id;						// ID
			var index = i;									// 序号
			var ifindex = fdbInfo.ifindex;					// 索引
			var ipaddress = fdbInfo.ipaddress;				// ipaddress
			var mac = fdbInfo.mac;							// mac
			var collecttime = fdbInfo.collecttime;			// 采集时间
			
			var tr = this.createElement(this.currTab.id + "-dataTR-" + index, "tr");
			tr.height = 25;
			listTBODY.appendChild(tr);
			var detailDataTDArray = this.createDetailDataTD(9, tr);
			
			// 序号
			var td = detailDataTDArray[index_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "index";
			td.insertAdjacentHTML("afterBegin", index);
			
			// 索引
			var td = detailDataTDArray[ifindex_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "ifindex";
			td.insertAdjacentHTML("afterBegin", ifindex);
			
			// ipaddress
			var td = detailDataTDArray[ipaddress_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "ipaddress";
			td.insertAdjacentHTML("afterBegin", ipaddress);
			
			// MAC
			var td = detailDataTDArray[mac_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "mac";
			td.insertAdjacentHTML("afterBegin", mac);
			
			// 采集时间
			//var recordtime = new Date(collecttime);
			var td = detailDataTDArray[collecttime_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "collecttime";
			td.insertAdjacentHTML("afterBegin", collecttime);
			
		}
		
	},
	
	// ------------			设备FDB信息    END					----------------------
	// -------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------
	// ------------			设备路由信息    START					----------------------
	// 获取设备路由信息
	getRouterInfo: function (){
		NetRemoteService.getRouterInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				ObjectSelf.callbackgetRouterInfo(data);
			}
		});
	},
	
	// 获取设备路由信息后处理的回调函数
	callbackgetRouterInfo: function(data){
		this.data = data;
		this.createRouterInfo();
	},
	
	// 获取设备路由信息后 创建设备路由信息
	createRouterInfo: function(){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		
		var routerInfoList = this.data;
		var length = routerInfoList.length;
		if(!routerInfoList || length == 0){
			return;
		}
		var currTabId = this.currTab.id;
		var ifindex_cellIndex = $(currTabId + "-titleTD-" + "ifindex").cellIndex;
		var dest_cellIndex = $(currTabId + "-titleTD-" + "dest").cellIndex;
		var nexthop_cellIndex = $(currTabId + "-titleTD-" + "nexthop").cellIndex;
		var rtype_cellIndex = $(currTabId + "-titleTD-" + "rtype").cellIndex;
		var proto_cellIndex = $(currTabId + "-titleTD-" + "proto").cellIndex;
		var mask_cellIndex = $(currTabId + "-titleTD-" + "mask").cellIndex;
		
		var i = 0;
		while(i < length){
			var routerInfo = routerInfoList[i];
			i++;
			var index = i;									// 序号
			var nodeid = routerInfo.nodeid;					// 设备id
			var ip = routerInfo.ip;							// 设备ip
			var type = routerInfo.type;						// 设备类型
			var subtype = routerInfo.subtype;				// 设备子类型
			var ifindex = routerInfo.ifindex;				// 索引
			var nexthop = routerInfo.nexthop;				// 下一跳
			var proto = routerInfo.proto;					// 路由协议
			var rtype = routerInfo.rtype;					// 路由类型
			var mask = routerInfo.mask;						// 子网掩码
			var dest = routerInfo.dest;						// 目标地址
			var collecttime = routerInfo.collecttime;		// 采集时间
			
			var tr = this.createElement("", "tr");
			tr.height = 25;
			listTBODY.appendChild(tr);
			var detailDataTDArray = this.createDetailDataTD(6, tr);
			
			// 索引
			var td = detailDataTDArray[ifindex_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "ifindex";
			td.insertAdjacentHTML("afterBegin", ifindex);
			
			// 目标地址
			var td = detailDataTDArray[dest_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "dest";
			td.insertAdjacentHTML("afterBegin", dest);
			
			// 下一跳
			var td = detailDataTDArray[nexthop_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "nexthop";
			td.insertAdjacentHTML("afterBegin", nexthop);
			
			// 路由类型
			var td = detailDataTDArray[rtype_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "rtype";
			td.insertAdjacentHTML("afterBegin", rtype);
			
			// 路由协议
			var td = detailDataTDArray[proto_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "proto";
			td.insertAdjacentHTML("afterBegin", proto);
			
			// 子网掩码
			var td = detailDataTDArray[mask_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "mask";
			td.insertAdjacentHTML("afterBegin", mask);
		}
		
	},
	
	// ------------			设备路由信息    END					----------------------
	// -------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------
	// ------------			设备IP列表信息    START				----------------------
	// 获取设备 IP列表 信息
	getIpListInfo: function (){
		NetRemoteService.getIpListInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				ObjectSelf.callbackgetIpListInfo(data);
			}
		});
	},
	
	// 获取设备 FDB 信息后处理的回调函数
	callbackgetIpListInfo: function(data){
		this.data = data;
		this.createIpListInfo();
	},
	
	// 获取设备 FDB 信息后 创建设备 FDB 信息
	createIpListInfo: function(){
		var listTBODY = this.detailDataTABLE.tBodies[1];
		if(!listTBODY){
			// 如果详细信息页面数据表格中 数据列表的 TBODY 为 null 则重新创建 (一般不会出现)
			this.createDetailDataTABLE();
		}
		// 删除列表中所有的元素
		this.removeAllChild(listTBODY);
		
		var ipListInfoList = this.data;
		var length = ipListInfoList.length;
		if(!ipListInfoList || length == 0){
			return;
		}
		
		var currTabId = this.currTab.id;
		var indexs_cellIndex = $(currTabId + "-titleTD-" + "indexs").cellIndex;
		var descr_cellIndex = $(currTabId + "-titleTD-" + "descr").cellIndex;
		var speed_cellIndex = $(currTabId + "-titleTD-" + "speed").cellIndex;
		var aliasip_cellIndex = $(currTabId + "-titleTD-" + "aliasip").cellIndex;
		var status_cellIndex = $(currTabId + "-titleTD-" + "status").cellIndex;
		var types_cellIndex = $(currTabId + "-titleTD-" + "types").cellIndex;
		var showDetail_cellIndex = $(currTabId + "-titleTD-" + "showDetail").cellIndex;
		
		var i = 0;
		while(i < length){
			var ipListInfo = ipListInfoList[i];
			i++;
			var index = i;									// 序号
			var indexs = ipListInfo.indexs;					// 索引
			var descr = ipListInfo.descr;					// 描述
			var speed = ipListInfo.speed;					// 每秒字节数
			var aliasip = ipListInfo.aliasip;				// aliasip
			//var status = null;							// 状态
			var types = ipListInfo.types;					// 类型
			
			var tr = this.createElement("", "tr");
			tr.height = 25;
			listTBODY.appendChild(tr);
			var detailDataTDArray = this.createDetailDataTD(7, tr);
			
			// 索引
			var td = detailDataTDArray[indexs_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "indexs";
			td.insertAdjacentHTML("afterBegin", indexs);
			
			// 描述
			var td = detailDataTDArray[descr_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "descr";
			td.insertAdjacentHTML("afterBegin", descr);
			
			// 每秒字节数
			var td = detailDataTDArray[speed_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "speed";
			td.insertAdjacentHTML("afterBegin", speed);
			tr.appendChild(td);
			
			// aliasip
			var td = detailDataTDArray[aliasip_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "aliasip";
			td.insertAdjacentHTML("afterBegin", aliasip);
			
			// 状态
			var td = detailDataTDArray[aliasip_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "status";
			
			// 类型
			var td = detailDataTDArray[types_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "types";
			td.insertAdjacentHTML("afterBegin", types);
			
			// 查看
			var td = detailDataTDArray[showDetail_cellIndex];
			td.id = this.currTab.id + "-dataTD-" + index + "-" + "showDetail";
		}
		
	},
	
	// ------------			设备IP列表信息    END					----------------------
	// -------------------------------------------------------------------------------
	
	// -------------------------------------------------------------------------------
	// ------------			设备动力环境信息     START				----------------------
	// 获取设备动力环境信息 
	getPowerEnvironmentInfo: function(){
		NetRemoteService.getHostNodeInfo(this.nodeid, this.type, this.subtype, {
			callback:function(data){
				ObjectSelf.callbackgetPowerEnvironmentInfo(data);
			}
		});
	},
	
	// 获取设备动力环境信息后处理的回调函数
	callbackgetPowerEnvironmentInfo: function(data){
		this.hostNode = data;
		this.createPowerEnvironmentInfo();
	},
	
	
	// 获取设备动力环境信息后 创建设备动力环境信息
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
				"&table=" + this.Envoriment_componentTemperSWFTable +  
				"&type=" + this.subtype +
				"&title=温度"; 
		var Envoriment_componentTemperSWFName = this.Envoriment_componentSWFName + this.Envoriment_componentTemperSWFkey;
		// 创建 电压 vol SWF
		var Envoriment_componentTemperSWFTABLE = this.createSWFTABLE(td, Envoriment_componentTemperSWFPath, Envoriment_componentTemperSWFName, "346", "250", this.SWFVersion, this.SWFbackgroundclor);
		
		
	},
	
	// ------------			设备动力环境信息    END				----------------------
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
		
		NetRemoteService.getAlarmInfo(this.nodeid, this.type, this.subtype, 
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
		
		NetRemoteService.getAlarmDetailInfo(this.nodeid, this.type, this.subtype, 
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