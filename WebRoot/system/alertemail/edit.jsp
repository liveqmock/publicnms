<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.system.model.*"%>
<%@page import="com.afunms.system.util.UserView"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.config.dao.*"%>
<%@page import="java.util.*"%>
<%
   AlertEmail vo = (AlertEmail)request.getAttribute("vo");
   String rootPath = request.getContextPath();
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
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
  function toAdd()
  {
     if(mainForm.password0.value!=mainForm.password.value)
     {
        alert("输入密码与确认密码不同，请重新输入!");
        mainForm.password0.focus();
        return false;
     }

     var chk1 = checkinput("username","string","用户名",50,false);
     var chk2 = checkinput("password","string","密码",15,false);
     var chk3 = checkinput("password0","string","密码",15,false);
     var chk4 = checkinput("smtp","string","SMTP",30,true);

     if(chk1&&chk2&&chk3&&chk4)
     {
        mainForm.action = "<%=rootPath%>/alertemail.do?action=update";
        mainForm.submit();
     }
  }
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

    <form method="post" name="mainForm">
          <input type=hidden name="id" value="<%=vo.getId()%>">
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
											                	<td class="add-content-title">系统管理 >> 告警邮箱设置 >> 编辑</td>
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
						    <TD nowrap align="right" height="24" width="10%">用户名&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;<input type="text" name="username" size="16" value="<%=vo.getUsername()%>" class="formStyle"><font color="red">&nbsp;*</font></TD>
							<TD nowrap align="right" height="24">SMTP&nbsp;</TD>				
							<TD nowrap>
								&nbsp;<input type="text" name="smtp" size="16" class="formStyle" value="<%=vo.getSmtp()%>"><font color="red">&nbsp;*</font>
							</TD>	
						</tr>
						<tr >	
							<TD nowrap align="right" height="24">密码&nbsp;</TD>				
							<TD nowrap>
								&nbsp;<input name="password" type="password" size="16"  class="formStyle" value="<%=vo.getPassword()%>"><font color="red">&nbsp;*</font>
							</TD>						
							<TD nowrap align="right" height="24">确认密码&nbsp;</TD>				
							<TD nowrap>&nbsp;<input name="password0" type="password" size="16" class="formStyle" value="<%=vo.getPassword()%>"><font color="red">&nbsp;*</font>														
							</TD>	
						</tr>
						<tr style="background-color: #ECECEC;">
							<TD nowrap align="right" height="24">邮箱地址&nbsp;</TD>				
							<TD nowrap>&nbsp;<input name="email_address" type="text" size="30" value="<%=vo.getMailAddress()%>" class="formStyle"><font color="red">&nbsp;*</font>														
							</TD>
							<TD nowrap align="right" height="24">是否启用&nbsp;</TD>				
							<TD nowrap colspan=3>
								<select   name="usedflag"  class="formStyle">
								<%
									if(vo.getUsedflag() == 0){
								%>
									<option value=0 selected>否</option>
									<option value=1>是</option>
								<%
									}else{
								%>
									<option value=0>否</option>
									<option value=1 selected>是</option>								
								<%
									}
								%>
								</select>							
							</TD>								

						</tr>										
															<tr align=center>
							<TD nowrap colspan="4">
								<br>
								<input type="button" value="保 存" style="width:50" class="formStylebutton" onclick="toAdd()" align=center>&nbsp;&nbsp;
								<input type=reset class="formStylebutton" style="width:50" value="返 回" onclick="javascript:history.back(1)" align=center>
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
</HTML>