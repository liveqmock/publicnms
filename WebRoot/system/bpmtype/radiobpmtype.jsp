<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.bpm.system.utils.StringUtil"%>
<%@ include file="/include/globe.inc"%>

<%
	String rootPath = request.getContextPath();
    String modelid = (String)request.getParameter("modelid");
    String key = (String)request.getParameter("key");
    if(StringUtil.isBlank(key)) {key = "";}
	
%>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=GB2312">
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script src="<%=rootPath%>/js/dynatree/jquery.js" type="text/javascript"></script>
	<script src="<%=rootPath%>/js/dynatree/jquery-ui.custom.js" type="text/javascript"></script>
	<script src="<%=rootPath%>/js/dynatree/jquery.cookie.js" type="text/javascript"></script>

	<link href="<%=rootPath%>/css/dynatree/ui.dynatree.css" rel="stylesheet" type="text/css">
	<script src="<%=rootPath%>/js/dynatree/jquery.dynatree.js" type="text/javascript"></script>

	<!-- jquery.contextmenu,  A Beautiful Site (http://abeautifulsite.net/) -->
	<script src="<%=rootPath%>/js/dynatree/contextmenu/jquery.contextMenu-custom.js" type="text/javascript"></script>
	<link href="<%=rootPath%>/js/dynatree/contextmenu/jquery.contextMenu.css" rel="stylesheet" type="text/css" >

	<!-- Start_Exclude: This block is not part of the sample code -->
	<link href="<%=rootPath%>/css/dynatree/prettify.css" rel="stylesheet">
	<script src="<%=rootPath%>/js/dynatree/prettify.js" type="text/javascript"></script>
	<link href="<%=rootPath%>/css/dynatree/sample.css" rel="stylesheet" type="text/css">
	<script src="<%=rootPath%>/js/dynatree/sample.js" type="text/javascript"></script>	
			
		
		<script language="JavaScript" type="text/JavaScript">
var show = true;
var hide = false;
//修改菜单的上下箭头符号
function my_on(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="on";
}
function my_off(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="off";
}

//添加菜单	
function initmenu()
{
	var idpattern=new RegExp("^menu");
	var menupattern=new RegExp("child$");
	var tds = document.getElementsByTagName("div");
	for(var i=0,j=tds.length;i<j;i++){
		var td = tds[i];
		if(idpattern.test(td.id)&&!menupattern.test(td.id)){					
			menu =new Menu(td.id,td.id+"child",'dtu','100',show,my_on,my_off);
			menu.init();		
		}
	}

}


</script>
<script type="text/javascript">

	
	// --- Init dynatree during startup ----------------------------------------

	$(function(){

		$("#tree").dynatree({
			persist: true,
            checkbox:true,
            selectMode:1,
            classNames: {checkbox: "dynatree-radio"},
            fx: { height: "toggle", duration: 200 },
			initAjax:{
			     url:"<%=rootPath%>/bpmnode.ajax?action=list"
			},
			onActivate: function(node) {
			},
			onSelect: function(flag, node) {
			if(1==node.getLevel()) {
			$("#keytext").val("");
			node.select(false);
			return ;
			}
			$("#keytext").val(node.data.key);
        },
     onPostInit: function(isReloading, isError) {
     try {
     $("#tree").dynatree("getTree").getNodeByKey("<%=key%>").select();
     }catch(ex) {
     var selectedNodes = $("#tree").dynatree("getTree").getSelectedNodes();
     $.each(selectedNodes, function(i, n){
        n.select(false);
        $("#keytext").val("");
     });
     }
         
            }
	});
	});
</script>
<SCRIPT   LANGUAGE="JavaScript">  
  function   centerWindow()   
  {  
          var   xMax   =   screen.width;
          var   yMax   =   screen.height;
         window.moveTo(xMax/3,yMax/2-100-80);
  }  
  centerWindow();  
 </SCRIPT>
 <script type="text/javascript">
 $(document).ready(function() {
$("#save").bind("click",function (){
var modelid = $("#modelid").val();
var keytext = $("#keytext").val();
if(""==$("#keytext").val()||null==$("#keytext").val()||"null"==$("#keytext").val()) {
alert("请选择类型");
return ;
}
$.ajax({
   type: "POST",
   url: "<%=rootPath%>/controller/modifycode.action",
   data: "keytext="+keytext+"&modelid="+modelid,
   success: function(msg){
     if("success"==msg) {
     alert("保存成功");
     window.returnValue="refresh";
     window.close();
     }else {
     alert("保存失败");
     }
   }
});


});
});
 </script>
	</head>
	<body id="body" class="body" onload="initmenu();">
			<table id="body-container" class="body-container">
				<tr>
					
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
								                	<tr>
									                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
									                	<td class="content-title"> 系统管理 >> 流程类型管理 >> 流程类型列表 </td>
									                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
									       			</tr>
									        	</table>
		        							</td>
										</tr>
										<tr>
										<td>
										<table border="0">
										<tr>
										<td align="center">
										<input type="submit" value="保存" id="save"/>
										<input type="hidden" name="keytext" id="keytext" value="<%=key%>">
										<input type="hidden" name="modelid" id="modelid" value="<%=modelid%>">
										</td>
										</tr>
										
										<tr>
										<td>
									
	<div id="tree">
		
	</div>
										
										</td>
										
										
										</tr>
										
										</table>
										</td>
										</tr>
										<tr>
		        							<td>
		        								<table id="content-footer" class="content-footer">
		        									<tr>
		        										<td>
		        											<table width="100%" border="0" cellspacing="0" cellpadding="0">
									                  			<tr>
									                    			<td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
									                    			<td></td>
									                    			<td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>
									                  			</tr>
									              			</table>
		        										</td>
		        									</tr>
		        								</table>
		        							</td>
		        						</tr>
		        					</table>
		        				</td>
		        			</tr>
						</table>
					</td>
				</tr>
			</table>
	</BODY>
</HTML>
