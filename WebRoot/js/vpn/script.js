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

 function loadFile(rootPath){
		var deviceType=$("#devicetype").val();
		window.open(rootPath+"/vpn.do?action=loadFileFromMenu&deviceType="+deviceType,"oneping", "height=500, width= 800, top=100, left=100,scrollbars=yes");		
  }
  //保存文件
  function saveFile(rootPath,type,devicetype){
		  var commands=$("#commands").text();
		   if(commands==null||commands==""){
		    alert("命令不允许为空！");
		    return;
		   }
		reg = new RegExp("(\r)","g");
		var value=commands.replace(reg,";");
		var dataUrl="commands="+value+"&type="+type+"&devicetype="+devicetype;
		
			 $.ajax({
			type:"POST",
			dataType:"json",
			url:rootPath+"/vpnAjaxManager.ajax?action=saveCmdCfg",
			data:dataUrl,
			success:function(data){
			    alert(data.isSucess);  
			}
		});
	}
	//保存vpn文件
 function saveVpnFile(rootPath,type,deviceType){
		  var commands=$("#commands").text();
		   if(commands==null||commands==""){
		    alert("命令不允许为空！");
		    return;
		   }
		
		reg = new RegExp("(\r)","g");
		var value=commands.replace(reg,";") 
		window.open(rootPath+"/vpn.do?action=saveVpnFile&commands="+value+"&type="+type+"&deviceType="+deviceType,"oneping", "height=400, width= 800, top=100, left=100,scrollbars=yes");
	}
//由第二步返回第一步
function secondFra2(){
	$('#fragment-0').show();
	$('#fragment-1').hide();
}
//执行网络设备配置操作命令
  function exeCmd(rootPath,id,type,deviceType){

	 var commands=$("[name='commands']").val();
	 var deviceType="";
	 if(commands==undefined||commands==""){
	   alert("请输入命令!!!");
	   return;
	 }
	 reg = new RegExp("(\n)","g");
	 var command=commands.replace(reg,";");
 $("#BgDiv").css({ display:"block",height:$(document).height()});
			var yscroll=document.documentElement.scrollTop;
			$("#DialogDiv").css("top","150px");
			$("#DialogDiv").css("display","block");
			document.documentElement.scrollTop=0;
			var dataUrl="";
			var checkbox=""; 
			dataUrl=dataUrl+"&command="+command+"&id="+id+"&type="+type+"&deviceType="+deviceType+"&command="+command;
			
			 $.ajax({
			type:"POST",
			dataType:"json",
			url:rootPath+"/vpnAjaxManager.ajax?action=exeVpnCmd",
			data:dataUrl,
			success:function(data){
			$("#BgDiv").css("display","none");
	        $("#DialogDiv").css("display","none");
			$('#fragment-0').hide();
	        $('#fragment-1').show();
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


 