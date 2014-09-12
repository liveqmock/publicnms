	var id=0;
	var path;
	var url;
	var menuFlag;
function init(rType,pathName){
    path=pathName;
    rightName=rType;
    $('#rotate ul').tabs({ fxFade: true });
	parent.document.getElementById("rightFrame").src=getRightFramePath(rType,pathName).rightFramePath;
	url=getRightFramePath(rType,pathName).url;
	initTree(url);
}

function getRightFramePath(rType,pathName){  
	var rightFramePath_val;
	var url_val;
	
	if(rType=="list"){
	rightFramePath_val=pathName+"/netCfgFile.do?action=list&jp=1&flag=1";
	url_val=pathName+"/automationTreeAjaxManager.ajax?action=getChildrenNodes";
	menuFlag="netCfgFile";
	}else if(rType=="configlist"){
	rightFramePath_val=pathName+"/netCfgFile.do?action=configlist&jp=1&flag=1";
	url_val=pathName+"/automationTreeAjaxManager.ajax?action=getConfiglistNodes";
	menuFlag="netCfgFile";
}else if(rType=="cmdCfgList"){
	rightFramePath_val=pathName+"/autoControl.do?action=cmdCfgList&jp=1&flag=1";
	url_val=pathName+"/automationTreeAjaxManager.ajax?action=getInspectionListNodes";
	menuFlag="autoControl";
}else if(rType=="inspectionList"){
	rightFramePath_val=pathName+"/autoControl.do?action=inspectionList&jp=1&flag=1";
	url_val=pathName+"/automationTreeAjaxManager.ajax?action=getInspectionListNodes";
	menuFlag="autoControl";
}else if(rType=="passwdList"){

	rightFramePath_val=pathName+"/remoteDevice.do?action=passwdList&jp=1&flag=1";
	url_val=pathName+"/automationTreeAjaxManager.ajax?action=getPwdListNodes";
	menuFlag="remoteDevice";
}else if(rType=="strategyList"){

	rightFramePath_val=pathName+"/configRule.do?action=strategyList&jp=1&flag=1";
	menuFlag="configRule";
}
return {
        rightFramePath: rightFramePath_val,
        url: url_val
    };
}
function initTree(url){
	var setting = { 
	data: {
		simpleData: { 
			enable: true 
		} 
	},
	async: { 
		enable: true, 
		url:url+"&id="+id, 
		autoParam:["id", "name"], 
		otherParam:{"otherParam":"zTreeAsyncTest"}, 
		// dataType: "text",//默认text 
		// type:"get",//默认post 
		dataFilter: filter //异步返回后经过Filter 
		
	},
	callback:{ 
		// beforeAsync: zTreeBeforeAsync, // 异步加载事件之前得到相应信息 
		asyncSuccess: zTreeOnAsyncSuccess,//异步加载成功的fun 
		asyncError: zTreeOnAsyncError, //加载错误的fun 
		beforeClick:beforeClick //捕获单击节点之前的事件回调函数 
	}
}; 
	var zNodes=[]; 
	$.fn.zTree.init($("#treeDemo"), setting, zNodes); 
}
function changeMenuAlias(){
	if(id==0){
	var zNodes2=[]; 
	var setting2 = { 

	data: {
		simpleData: { 
		   
			enable: true 
		} 
	},
	async: { 
		enable: true, 
		url:url+"&id=2", 
		autoParam:["id", "name"], 
		otherParam:{"otherParam":"zTreeAsyncTest"}, 
		// dataType: "text",//默认text 
		// type:"get",//默认post 
		dataFilter: filter //异步返回后经过Filter 
		
	},
	callback:{ 
		// beforeAsync: zTreeBeforeAsync, // 异步加载事件之前得到相应信息 
		asyncSuccess: zTreeOnAsyncSuccess,//异步加载成功的fun 
		asyncError: zTreeOnAsyncError, //加载错误的fun 
		beforeClick:beforeClick //捕获单击节点之前的事件回调函数 
	}
}; 
	$.fn.zTree.init($("#treeAias"), setting2, zNodes2); 
	id=1;
}
}

function changeMenu(){
 parent.document.getElementById("leftFrame").src=path+"/automation/common/menu.html?menuFlag="+menuFlag;
}

function filter(treeId, parentNode, childNodes) {
	if (!childNodes) return null; 
	for (var i=0, l=childNodes.length; i<l; i++) { 
	     childNodes[i].open = true;
		childNodes[i].name = childNodes[i].name.replace('',''); 
	} 
	return childNodes; 
}
function beforeClick(treeId,treeNode){
	parent.document.getElementById("rightFrame").src=treeNode.url_my;
} 
function zTreeOnAsyncError(event, treeId, treeNode){ 
	alert("异步加载失败!"); 
} 
function zTreeOnAsyncSuccess(event, treeId, treeNode, msg){ 
	
} 


function zTreeOnAsyncError2(event, treeId, treeNode){ 
	alert("异步加载失败!"); 
} 
function zTreeOnAsyncSuccess2(event, treeId, treeNode, msg){ 
	
} 