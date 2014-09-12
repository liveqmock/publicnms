<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.system.model.Function"%>
<%@page import="java.util.List"%>

<%
	String rootPath = request.getContextPath();
	String menuTable = (String)request.getAttribute("menuTable");	
	List<Function> allFunction = (List<Function>)request.getAttribute("allFunction");
%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>

<!--nielin add for timeShareConfig at 2010-01-04 start-->
<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/timeShareConfigdiv.js" charset="gb2312"></script>
<!--nielin add for timeShareConfig at 2010-01-04 end-->




<script language="JavaScript" type="text/javascript">


  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
 Ext.get("process").on("click",function(){
  
     {      
            Ext.MessageBox.wait('数据加载中，请稍后.. ');
        	mainForm.action = "<%=rootPath%>/menu.do?action=addUpdate";
        	mainForm.submit();
     }
       // mainForm.submit();
 });	
	
});
//-- nielin modify at 2010-01-04 start ----------------
function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}    

function setReceiver(eventId){
	var event = document.getElementById(eventId);
	return CreateWindow('<%=rootPath%>/user.do?action=setReceiver&event='+event.id+'&value='+event.value);
}
//-- nielin modify at 2010-01-04 end ----------------


</script>

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
	timeShareConfiginit(); // nielin add for time-sharing at 2010-01-04
}

</script>


</head>
<body id="body" class="body" onload="initmenu();">



	<form id="mainForm" method="post" name="mainForm">
		
		<table id="body-container" class="body-container">
			<tr>
				<td class="td-container-menu-bar">
					<table id="container-menu-bar" class="container-menu-bar">
						<tr>
							<td>
								<%=menuTable%>
							</td>	
						</tr>
					</table>
				</td>
				<td class="td-container-main">
					<table id="container-main" class="container-main">
						<tr>
							<td class="td-container-main-add">
								<table id="container-main-add" class="container-main-add">
									<tr>
										<td>
											<table id="add-content" class="add-content">
												<tr>
													<td>
														<table id="add-content-header" class="add-content-header">
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title">系统管理 >> 系统设置 >> 编辑TFtp服务地址</td>
											                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
											       			</tr>
											        	</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-body" class="detail-content-body">
				        									<tr>
				        										<td>
				        										
				        												<table border="0" id="table1" cellpadding="0" cellspacing="1"
																	width="100%">
																
																	<tr style="background-color: #ECECEC;">
									<td align="right" height="24" width="20%">菜单名：&nbsp;</td>
									<td width="30%">&nbsp;<input type="text" id="ch_desc" name="ch_desc"><font color="red">&nbsp;*</font></td>
									<td align="right" height="24" width="20%">菜单等级：&nbsp;</td>
									<td width="30%">
										&nbsp;<select id="level_desc" name="level_desc" style="width: 130" onchange="unionFaterhMenuSelect();">
											<option value="1">1</option>
											<option value="2">2</option>
											<option value="3">3</option>
										</select><font color="red">&nbsp;*</font>
									</td>
								</tr>
									
								<tr>
									<td align="right" height="24" width="20%">父菜单名：&nbsp;</td>
									<td width="30%">
										&nbsp;<select id="fatherNode" name="fatherNode" style="width: 130">
										</select><font color="red">&nbsp;*</font>
									</td>
									<td align="right" height="24" width="20%">是否弹出新窗口：&nbsp;</td>
									<td width="30%">
										&nbsp;<select id="isCurrentWindow" name="isCurrentWindow" style="width: 130">
											<option value="0">否</option>
											<option value="1">是</option>
										</select><font color="red">&nbsp;*</font>
									</td>
								</tr>
								
								<tr >
									<td align="right" height="24" width="20%">弹出窗口宽度&nbsp;</td>
									<td width="30%">&nbsp;<input type="text" id="width" name="width"></td>
									<td align="right" height="24" width="20%">弹出窗口高度&nbsp;</td>
									<td width="30%">&nbsp;<input type="text" id="height" name="height"></td>
									
								</tr>	
								<tr >
									<td align="right" height="24" width="20%">弹出窗口左边距离&nbsp;</td>
									<td width="30%">&nbsp;<input type="text" id="clientX" name="clientX"></td>
									<td align="right" height="24" width="20%">弹出窗口上边距离&nbsp;</td>
									<td width="30%">&nbsp;<input type="text" id="clientY" name="clientY"></td>
									
								</tr>
								
								<tr id="url&tr" >
									<td align="right" height="24" width="20%">菜单URL：<%=rootPath %>/&nbsp;</td>
									<td width="30%">&nbsp;<input type="text" id="url" name="url"><font color="red">&nbsp;*</font></td>
									<td align="right" height="24" width="20%">菜单图片URL：<%=rootPath %>/&nbsp;</td>
									<td width="30%">&nbsp;<input type="text" id="img_url" name="img_url""><font color="red">&nbsp;*</font></td>
									
								</tr>
										
									            							
															<tr>
																<TD nowrap colspan="4" align=center>
																<br><input type="button" value="保 存" style="width:50" id="process" onclick="#">&nbsp;&nbsp;
																	<input type="reset" style="width:50" value="返回" onclick="javascript:history.back(1)">
																</TD>	
															</tr>	
							
						                        </TABLE>						
				        										</td>
				        									</tr>
				        								</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-footer" class="detail-content-footer">
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
									<tr>
										<td>
											
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>	
				</td>
			</tr>
		</table>
	</form>
</body>
<script>
	
	function unionFaterhMenuSelect(){
		var fatherNodeArray = new Array();
		var functionFlag = 0;
		var level_desc = document.all.level_desc.value ;
		var fatherNode = document.getElementById("fatherNode");
		fatherNode.length = 0;
		<%
			for(int i = 0 ; i< allFunction.size() ; i++){		
		%>
			if(level_desc == <%=allFunction.get(i).getLevel_desc()+1%>){
				fatherNodeArray[functionFlag] = new Array("<%=allFunction.get(i).getCh_desc()%>","<%=allFunction.get(i).getId()%>");
				functionFlag++;
			}
			
		<%
			}
		%>
		for(var j =0 ; j<functionFlag;j++){
			fatherNode.options[j] = new Option(fatherNodeArray[j][0],fatherNodeArray[j][1]); 
		}
		unionUrl();
	}
	unionFaterhMenuSelect();
	
</script>
<script>
	
	function unionUrl(){
		
		var level_desc = document.all.level_desc.value;
		var tr = document.getElementById("url&tr");
		if(level_desc==3){
			tr.style.display="";
		}else{
			tr.style.display="none";
		}
	}
	unionUrl();
</script>
</HTML>