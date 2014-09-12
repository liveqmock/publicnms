  Ext.onReady(function()
		{  
		$('#fragment-1').hide();
		$('#fragment-2').hide();
		
		setTimeout(function(){
			        Ext.get('loading').remove();
			        Ext.get('loading-mask').fadeOut({remove:true});
			    }, 250);
		}
		
		);
		
 function CreateWindow(url){
	    msgWindow=window.open(url,"protypeWindow","toolbar=no,width=400,height=400,directories=no,status=no,scrollbars=no,menubar=no")
	 }   
 function setClass(){
	document.getElementById('scriptTitle-0').className='detail-data-title';
	document.getElementById("icmp").style.display='none';
	document.getElementById("icmppath").style.display='none';
	document.getElementById("udp").style.display='none';
	document.getElementById("tcpconnectwithresponder").style.display='none';
	document.getElementById("tcpconnectnoresponder").style.display='none';
	document.getElementById("jitter").style.display='none';
	document.getElementById("http").style.display='none';
	document.getElementById("dns").style.display='none';	
	document.getElementById("h3cIcmp").style.display='none';
	document.getElementById("h3cHttp").style.display='none';	
	document.getElementById("h3cNqa").style.display='none';	
 }
 function loadFile(rootPath){
		var devicetype=$("#devicetype").val();
		window.open(rootPath+"/slacmd.do?action=loadFileFromMenu&devicetype="+devicetype,"oneping", "height=500, width= 800, top=100, left=100,scrollbars=yes");		
  }
  function saveFile(rootPath){
		  var commands=$("#commands").text();
		   if(commands==null||commands==""){
		    alert("命令不允许为空！");
		    return;
		   }
		
		reg = new RegExp("(\r)","g");
		var value=commands.replace(reg,";;") 
		window.open(rootPath+"/slacmd.do?action=saveFile&commands="+value,"oneping", "height=400, width= 800, top=100, left=100,scrollbars=yes");
	}
 function firstFra(){
    document.getElementById('scriptTitle-0').className='detail-data-title';
	document.getElementById('scriptTitle-1').className='detail-data-title-out';
	document.getElementById('scriptTitle-2').className='detail-data-title-out';
	
	$('#fragment-0').show();
	$('#fragment-1').hide();
	$('#fragment-2').hide();
	//document.getElementById('fragment-0').style.display = "block";
    //document.getElementById('fragment-1').style.display = "none";
    //document.getElementById('fragment-2').style.display = "none";
 }

 function secondFra(rootPath){

   var command=$('#commands').val();
   if(command==null||command==""){
    alert("命令不允许为空！");
    return;
   }
   var slatype=$('#slatype').val();
   if(slatype==null||slatype==""){
    return;
   }
    var deviceType=$('#deviceType1').val();
    var reg=null;
    var exp=/^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
 
   if(deviceType=="cisco"){
   if(slatype=="icmp"){
   		var icmp_destip=$('#icmp_destip').val();
   		var icmp_datapacket=$('#icmp_datapacket').val();
   		var icmp_tos=$('#icmp_tos').val();
   		if(icmp_destip==null||icmp_destip==""){
   			alert("请填写目标IP地址！");
    		return;
   		}
   		if(icmp_datapacket==null||icmp_datapacket==""){
   			alert("请填写数据包大小！");
    		return;
   		}
   		if(icmp_tos==null||icmp_tos==""){
   			alert("请填写TOS！");
    		return;
   		}
   		reg = icmp_destip.match(exp);
   }
      if(slatype=="icmppath"){
   		var icmppath_destip=$('#icmppath_destip').val();
   		var icmppath_rate=$('#icmppath_rate').val();
   		var icmppath_history=$('#icmppath_history').val();
   		var icmppath_buckets=$('#icmppath_buckets').val();
   		if(icmppath_destip==null||icmppath_destip==""){
   			alert("请填写目标IP地址！");
    		return;
   		}
   		if(icmppath_rate==null||icmppath_rate==""){
   			alert("请填写频率！");
    		return;
   		}
   		if(icmppath_history==null||icmppath_history==""){
   			alert("请填写历史存活！");
    		return;
   		}
   		 if(icmppath_history==null||icmppath_history==""){
   			alert("请填写桶历史！");
    		return;
   		}
   		reg = icmppath_destip.match(exp);
   }
    if(slatype=="udp"){
   		var udp_destip=$('#udp_destip').val();
   		var udp_destport=$('#udp_destport').val();
   		if(udp_destip==null||udp_destip==""){
   			alert("请填写目标IP地址！");
    		return;
   		}
   		if(udp_destport==null||udp_destport==""){
   			alert("请填写目标端口！");
    		return;
   		}   
   		reg = udp_destip.match(exp);		
   }
   if(slatype=="jitter"){
   		var jitter_destip=$('#jitter_destip').val();
   		var jitter_destport=$('#jitter_destport').val();
   		var jitter_numpacket=$('#jitter_numpacket').val();
   		var jitter_interval=$('#jitter_interval').val();
   		if(jitter_destip==null||jitter_destip==""){
   			alert("请填写目标IP地址！");
    		return;
   		}
   		if(jitter_destport==null||jitter_destport==""){
   			alert("请填写目标端口！");
    		return;
   		}
   		if(jitter_numpacket==null||jitter_numpacket==""){
   			alert("请填写数据包！");
    		return;
   		}
   		 if(jitter_interval==null||jitter_interval==""){
   			alert("请填写间隔！");
    		return;
   		}
   			reg = jitter_destip.match(exp);	
   } 
     if(slatype=="tcpconnectwithresponder"){
   		var tcpconnectwithresponder_destip=$('#tcpconnectwithresponder_destip').val();
   		var tcpconnectwithresponder_destport=$('#tcpconnectwithresponder_destport').val();
   		var tcpconnectwithresponder_tos=$('#tcpconnectwithresponder_tos').val();
   		if(tcpconnectwithresponder_destip==null||tcpconnectwithresponder_destip==""){
   			alert("请填写目标IP地址！");
    		return;
   		}
   		if(tcpconnectwithresponder_destport==null||tcpconnectwithresponder_destport==""){
   			alert("请填写目标端口！");
    		return;
   		}
   		if(tcpconnectwithresponder_tos==null||tcpconnectwithresponder_tos==""){
   			alert("请填写TOS！");
    		return;
   		}  
   		reg = tcpconnectwithresponder_destip.match(exp);		
   }
   if(slatype=="tcpconnectnoresponder"){
   		var tcpconnectnoresponder_destip=$('#tcpconnectnoresponder_destip').val();
   		var tcpconnectnoresponder_destport=$('#tcpconnectnoresponder_destport').val();
   		if(tcpconnectnoresponder_destip==null||tcpconnectnoresponder_destip==""){
   			alert("请填写目标IP地址！");
    		return;
   		}
   		if(tcpconnectnoresponder_destport==null||tcpconnectnoresponder_destport==""){
   			alert("请填写目标端口！");
    		return;
   		}
   		reg = tcpconnectnoresponder_destip.match(exp);
   } 
   if(slatype=="http"){
   		var http_urlconnect=$('#http_urlconnect').val();
   		if(http_urlconnect==null||http_urlconnect==""){
   			alert("请填写URL连接！");
    		return;
   		}
   } 
  if(slatype=="dns"){
   		var dns_destip=$('#dns_destip').val();
   		var dns_dnsserver=$('#dns_dnsserver').val();
   		if(dns_destip==null||dns_destip==""){
   			alert("请填写目标IP地址！");
    		return;
   		}
   		reg = dns_destip.match(exp);
   		if(dns_dnsserver==null||dns_dnsserver==""){
   			alert("请填写域名服务器！");
    		return;
   		}
   		
   } 
    if(reg==null){
	  alert("IP不合法,请输入正确的地址！");
	  return;
	}
   document.getElementById('scriptTitle-0').className='detail-data-title-out';
	document.getElementById('scriptTitle-1').className='detail-data-title';
	document.getElementById('scriptTitle-2').className='detail-data-title-out';
	
	$('#fragment-0').hide();
	$('#fragment-1').show();
	$('#fragment-2').hide();
   }else if(deviceType=="h3c"){
   var h3c_admin="";
   var h3c_tag="";
   if(slatype=="icmp"){
   		 h3c_admin=$('#h3c_icmp_admin').val();
   		 h3c_tag=$('#h3c_icmp_tag').val();
   		var h3c_icmp_destip=$('#h3c_icmp_destip').val();
   		if(h3c_admin==null||h3c_admin==""){
   			alert("请填写管理名！");
    		return;
   		}
   		if(h3c_tag==null||h3c_tag==""){
   			alert("请填写操作标识！");
    		return;
   		}
   		if(h3c_icmp_destip==null||h3c_icmp_destip==""){
   			alert("请填写目标IP！");
    		return;
   		}
   		
   		reg = h3c_icmp_destip.match(exp);
   } else if(slatype=="http"){
   		 h3c_admin=$('#h3c_http_admin').val();
   		 h3c_tag=$('#h3c_http_tag').val();
   		var h3c_http_destip=$('#h3c_http_destip').val();
   		var h3c_http_url=$('#h3c_http_url').val();
   		if(h3c_admin==null||h3c_admin==""){
   			alert("请填写管理名！");
    		return;
   		}
   		if(h3c_tag==null||h3c_tag==""){
   			alert("请填写操作标识！");
    		return;
   		}
   		if(h3c_http_destip==null||h3c_http_destip==""){
   			alert("请填写目标IP！");
    		return;
   		}
   		
   		if(h3c_http_url==null||h3c_http_url==""){
   			alert("请填写URL！");
    		return;
   		}
   		
   		reg = h3c_http_destip.match(exp);
   } else if(slatype=="udp"||slatype=="tcpconnect-noresponder"||slatype=="jitter"){
   		 h3c_admin=$('#h3c_admin').val();
   		 h3c_tag=$('#h3c_tag').val();
   		var h3c_destip=$('#h3c_destip').val();
   		var h3c_destport=$('#h3c_destport').val();
   		if(h3c_admin==null||h3c_admin==""){
   			alert("请填写管理名！");
    		return;
   		}
   		if(h3c_tag==null||h3c_tag==""){
   			alert("请填写操作标识！");
    		return;
   		}
   		if(h3c_destip==null||h3c_destip==""){
   			alert("请填写目标IP！");
    		return;
   		}
   		if(h3c_destport==null||h3c_destport==""){
   			alert("请填写目标端口！");
    		return;
   		}
   		
   		reg = h3c_destip.match(exp);
   } 
    $.ajax({
			type:"POST",
			dataType:"json",
			url:rootPath+"/serviceQualityAjaxManager.ajax?action=checkUser",
			data:"h3c_admin="+h3c_admin+"&h3c_tag="+h3c_tag,
			success:function(data){
			if(data.isSccess=="1"){
			alert("管理名和操作标识已同时存在！");
			
			 }else{
	if(reg==null){
	  alert("IP不合法,请输入正确的地址！");
	  return;
	}	 
	document.getElementById('scriptTitle-0').className='detail-data-title-out';
	document.getElementById('scriptTitle-1').className='detail-data-title';
	document.getElementById('scriptTitle-2').className='detail-data-title-out';
	
	$('#fragment-0').hide();
	$('#fragment-1').show();
	$('#fragment-2').hide();
			 }
			}
			});
			
  
   }
	//document.getElementById('fragment-0').style.display = "none";
    //document.getElementById('fragment-1').style.display = "block";
   // document.getElementById('fragment-2').style.display = "none";
}
//由第三步返回第二步
function secondFra2(){
document.getElementById('scriptTitle-0').className='detail-data-title-out';
	document.getElementById('scriptTitle-1').className='detail-data-title';
	document.getElementById('scriptTitle-2').className='detail-data-title-out';
	
	$('#fragment-0').hide();
	$('#fragment-1').show();
	$('#fragment-2').hide();
}
//执行网络设备配置操作命令
  function exeCmd(rootPath){

	 document.getElementById('scriptTitle-1').className='detail-data-title-out';
	 document.getElementById('scriptTitle-2').className='detail-data-title';
	 var ip=$("[name='checkbox'][checked]").val();
	 if(ip==undefined||ip==""){
	   alert("请选择设备IP!!!");
	   return;
	 }
 // Ext.MessageBox.wait('数据加载中，请稍后.. '); 
 //  mainForm.action="<%=rootPath%>/slacmd.do?action=exeCmd";
//	mainForm.submit();
 $("#BgDiv").css({ display:"block",height:$(document).height()});
			var yscroll=document.documentElement.scrollTop;
			$("#DialogDiv").css("top","150px");
			$("#DialogDiv").css("display","block");
			document.documentElement.scrollTop=0;
			var dataUrl="";
			var checkbox=""; 
			 $("[name='checkbox'][checked]").each(function(){  
			 checkbox+=$(this).val()+","; 
			  })  
			
			
			var cmdid=$("#cmdid").val();
			var slatype=$("#slatype").val();
			var deviceType1=$("#deviceType1").val();
			dataUrl=dataUrl+"checkbox="+checkbox+"&cmdid="+cmdid+"&slatype="+slatype+"&deviceType="+deviceType1;
			if(deviceType1=="cisco"){
			if(slatype=="icmp"){
			var icmp_destip=$("#icmp_destip").val();
			var icmp_datapacket=$("#icmp_datapacket").val();
			var icmp_tos=$("#icmp_tos").val();
			dataUrl=dataUrl+"&icmp_destip="+icmp_destip+"&icmp_datapacket="+icmp_datapacket+"&icmp_tos="+icmp_tos;
			}else if(slatype=="icmppath"){
			var icmppath_destip=$("#icmppath_destip").val();
			var icmppath_rate=$("#icmppath_rate").val();
			var icmppath_history=$("#icmppath_history").val();
			var icmppath_buckets=$("#icmppath_buckets").val();
			var icmppath_life=$("#icmppath_life").val();
			dataUrl=dataUrl+"&icmppath_destip="+icmppath_destip+"&icmppath_rate="+icmppath_rate+"&icmppath_history="+icmppath_history+"&icmppath_buckets="+icmppath_buckets+"&icmppath_life="+icmppath_life;
			}else if(slatype=="udp"){
			var udp_destip=$("#udp_destip").val();
			var udp_destport=$("#udp_destport").val();
			dataUrl=dataUrl+"&udp_destip="+udp_destip+"&udp_destport="+udp_destport;
			
			}else if(slatype=="jitter"){
			var jitter_destip=$("#jitter_destip").val();
			var jitter_destport=$("#jitter_destport").val();
			var jitter_numpacket=$("#jitter_numpacket").val();
			var jitter_interval=$("#jitter_interval").val();
			dataUrl=dataUrl+"&jitter_destip="+jitter_destip+"&jitter_destport="+jitter_destport+"&jitter_numpacket="+jitter_numpacket+"&jitter_intervals="+jitter_interval;
			}else if(slatype=="tcpconnectwithresponder"){
			
			var tcpconnectwithresponder_destip=$("#tcpconnectwithresponder_destip").val();
			var tcpconnectwithresponder_destport=$("#tcpconnectwithresponder_destport").val();
			var tcpconnectwithresponder_tos=$("#tcpconnectwithresponder_tos").val();
			dataUrl=dataUrl+"&tcpconnectwithresponder_destip="+tcpconnectwithresponder_destip+"&tcpconnectwithresponder_destport="+tcpconnectwithresponder_destport+"&tcpconnectwithresponder_tos="+tcpconnectwithresponder_tos;
			}else if(slatype=="tcpconnectnoresponder"){
			
			var tcpconnectnoresponder_destip=$("#tcpconnectnoresponder_destip").val();
			var icmp_datapacket=$("#icmp_datapacket").val();
			var tcpconnectnoresponder_destport=$("#tcpconnectnoresponder_destport").val();
			dataUrl=dataUrl+"&tcpconnectnoresponder_destip="+tcpconnectnoresponder_destip+"&tcpconnectnoresponder_destport="+tcpconnectnoresponder_destport;
			
			}else if(slatype=="http"){
			var http_urlconnect=$("#http_urlconnect").val();
			dataUrl=dataUrl+"&http_urlconnect="+http_urlconnect;
			}else if(slatype=="dns"){
			 var dns_destip=$("#dns_destip").val();
			 var dns_dnsserver=$("#dns_dnsserver").val();
			var icmp_tos=$("#icmp_tos").val();
			dataUrl=dataUrl+"&dns_destip="+dns_destip+"&dns_dnsserver="+dns_dnsserver;
			}
			}else if(deviceType1=="h3c"){
			if(slatype=="icmp"){
			var h3c_icmp_admin=$("#h3c_icmp_admin").val();
			var h3c_icmp_tag=$("#h3c_icmp_tag").val();
			var h3c_icmp_destip=$("#h3c_icmp_destip").val();
			dataUrl=dataUrl+"&h3c_icmp_admin="+h3c_icmp_admin+"&h3c_icmp_tag="+h3c_icmp_tag+"&h3c_icmp_destip="+h3c_icmp_destip;
			}else if(slatype=="http"){
			var h3c_http_admin=$("#h3c_http_admin").val();
			var h3c_http_tag=$("#h3c_http_tag").val();
			var h3c_http_destip=$("#h3c_http_destip").val();
			var h3c_http_url=$("#h3c_http_url").val();
			dataUrl=dataUrl+"&h3c_http_admin="+h3c_http_admin+"&h3c_http_tag="+h3c_http_tag+"&h3c_http_destip="+h3c_http_destip+"&h3c_http_url="+h3c_http_url;
			}else if(slatype=="udp"||slatype=="tcpconnect-noresponder"||slatype=="jitter"){
			var h3c_admin=$("#h3c_admin").val();
			var h3c_tag=$("#h3c_tag").val();
			var h3c_destip=$("#h3c_destip").val();
			var h3c_destport=$("#h3c_destport").val();
			dataUrl=dataUrl+"&h3c_admin="+h3c_admin+"&h3c_tag="+h3c_tag+"&h3c_destip="+h3c_destip+"&h3c_destport="+h3c_destport;
			}
			}
			
			 $.ajax({
			type:"POST",
			dataType:"json",
			url:rootPath+"/serviceQualityAjaxManager.ajax?action=exeCmd",
			data:dataUrl,
			success:function(data){
			$("#BgDiv").css("display","none");
	        $("#DialogDiv").css("display","none");
			$('#fragment-0').hide();
	        $('#fragment-1').hide();
	        $('#fragment-2').show();
			        var items=data.list; 
			        var temp="";
			         var ip="";
			        for(var i =0;i<items.length;i++)         {  
			                var item=items[i];
			               
			                if(temp==item.ip){
			                ip="...";
			                }else{
			                 ip=item.ip;
			                }
			                 temp=item.ip;
			                var row="<tr height=28><td class='report-data-body-list' align=center>"+ip+"</td><td class='report-data-body-list' align=center>"+ item.time+"</td>";
			                row=row+ "<td class='report-data-body-list' align=center>"+item.command+"</td><td class='report-data-body-list' align=center>"+item.result+"</td><tr>";     
			                 $(row).appendTo("#result"); 
			              }; 
			}
		});
 }


 