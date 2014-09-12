function checkIPAddress(sIPAddress){
		var exp=/^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
		var reg = sIPAddress.match(exp);
		 if(reg==null)
	     {
	     return false;
	     }
	     return true;
	     }
function checkDomainName(domainName){
		var exp=/^[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\.?$/;
		var reg = domainName.match(exp);
		 if(reg==null)
	     {
	     return false;
	     }
	     return true;
	     }
	     var buttonid;
	   
	function ifExecute(kind,id){
	
		buttonid=id;
		var ip=document.getElementById("ipaddress").value;
		if(kind=="nslookup") {
			if (checkDomainName(ip)) {
				DWRUtil.ifExecute("nslookup",ip,nslookupExecuteMethod);
			} else {
				alert("您输入的域名有误");
			}
		}else if (checkIPAddress(ip)){
			if(kind=="ping") DWRUtil.ifExecute("ping",ip,pingExecuteMethod);
			if(kind=="traceroute") DWRUtil.ifExecute("traceroute",ip,tracerouteExecuteMethod);
		}else alert("您输入的Ip地址有误");
	}
	
	var pingreader;
	//处理dwr的返回结果
	function pingExecuteMethod(ifexecute){
		if(ifexecute){
			document.getElementById(buttonid).disable= true;
			document.getElementById("resultofping").value="";
			var ip=document.getElementById('ipaddress').value;
			if(checkIPAddress(ip)){
				//var delay=document.getElementById('delaytime').value;
				var delay="1";
				var executenumber=document.getElementById('executenumber').value;
				var packagelength=document.getElementById('packagelength').value;
				DWRUtil.executePingRsStr(ip,executenumber,packagelength,delay);
				pingreader = setInterval("readPing()",300);
			}
			else{
				alert("您输入的IP地址有误！");
				openButton();
			}
		}
	}
	/*traceroute执行*/
var traceroutereader;
function tracerouteExecuteMethod(ifexecute){
	if(ifexecute){
		document.getElementById(buttonid).disabled = true;
		//alert("锁住按钮！");
		//document.getElementById("resultofping").value="请稍候..."
		var ip=document.getElementById('ipaddress').value;
		if(checkIPAddress(ip)){
			//var delay=document.getElementById('delaytime').value;
			var delay="1";
			var maxjumpnumber=document.getElementById('maxjumpnumber').value;
			//var maxjumpnumber="10";
			DWRUtil.executeTRRsStr(ip,delay,maxjumpnumber);
			//alert("执行命令");
			traceroutereader = setInterval("readTraceRoute()",300);
			//alert("开始刷新页面");
		}
		else{
			//alert("您输入的IP地址有误！");
			openButton();
		}
	}
	else alert("TraceRoute命令正在执行，请稍候...");
} 

var nslookupreader = null;
function nslookupExecuteMethod(ifexecute){
	if(ifexecute){
		document.getElementById(buttonid).disabled = true;
		//alert("锁住按钮！");
		document.getElementById("resultofping").value="";
		var ip=document.getElementById('ipaddress').value;
		if(checkDomainName(ip)){
			DWRUtil.executeNslookupStr(ip);
			//alert("执行命令");
			nslookupreader = setInterval("readNslookup()",300);
			//alert("开始刷新页面");
		}
		else{
			//alert("您输入的IP地址有误！");
			openButton();
		}
	}
	else alert("Nslookup命令正在执行，请稍候...");
}

function readTraceRoute(){
	DWRUtil.readTRRsStr(document.getElementById('ipaddress').value,replyPingStr);
}


	function readPing(){
		DWRUtil.readPingRsStr(document.getElementById('ipaddress').value,replyPingStr);
	}
	
	function readNslookup(){
		DWRUtil.readNslookupRsStr(document.getElementById('ipaddress').value,replyPingStr);
	}

	function replyPingStr(data){
		if(data==null){
			if(pingreader!=null){
				if(document.getElementById("resultofping").value.length<10) document.getElementById("resultofping").value = "设备无响应！";
				clearInterval(pingreader);
				openButton();
			}
			
			if(traceroutereader!=null){
					if(document.getElementById("resultofping").value.length<10) document.getElementById("resultofping").value = "设备无响应！";
					clearInterval(traceroutereader);
					openButton();
			}
			if(nslookupreader!=null){
					if(document.getElementById("resultofping").value.length<10) document.getElementById("resultofping").value = "设备无响应！";
					clearInterval(nslookupreader);
					openButton();
			}
		}else{
		document.getElementById("resultofping").value =document.getElementById("resultofping").value +data;
		}
	}
	
	function openButton(){
		document.getElementById(buttonid).disabled = false;
	}
	
	

function closePage(kind,ip){
		//alert("ip===>"+ip);
		if(kind=="ping") DWRUtil.closePro("ping",ip);
		if(kind=="traceroute") DWRUtil.closePro("traceroute",ip);
		if(kind=="nslookup") DWRUtil.closePro("nslookup",ip);
	}