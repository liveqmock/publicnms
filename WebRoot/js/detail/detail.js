/*******************************************************************************
 * detail.js
 * 
 * @Author: 聂林
 * 
 * @Date: 2010-12-10
 * 
 * @Function:
 * 该 js 的主要功能是完成详细信息页面的数据展示。
 * 当页面引入该 js 后，此 js 会在页面加载完成后自动执行 init() 方法来
 * 初始化页面的数据。
 *
 ******************************************************************************/

// 设备id
var nodeid = "";

// 设备类型
var type = "";

// 设备子类型
var subtype = "";

if (window.addEventListener) { 
	window.addEventListener("load", init, false); 
} else if (window.attachEvent) { 
	window.attachEvent("onload", init); 
} else { 
	window.onload = init; 
}

// 页面初始化
function init() { 
	//DWRUtil.useLoadingMessage(); 
	
} 


// 获取设备系统信息
function getSystemInfo(){
	detailRemote.getSystemInfo(nodeid, type, subtype, callbackgetSystemInfo);
}

// 获取设备系统信息后处理的回调函数
function callbackgetSystemInfo(data){ 
	alert(data);
}

// 解析设备详细信息页面中系统信息的 XML
function parseSystemInfoXML(){
		
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
