<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.system.util.UserView"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%
   UserView view = new UserView();
   User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);
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


  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
 Ext.get("process").on("click",function(){
 
      if(mainForm.password0.value!=mainForm.password.value)
     {
        alert("输入密码与确认密码不同，请重新输入!");
        mainForm.password.focus();
        return false;
     }

     var chk1 = checkinput("name","string","姓名",50,false);
     var chk2 = checkinput("password","string","密码",15,true);
     var chk3 = checkinput("password0","string","密码",15,true);
     var chk4 = checkinput("phone","string","电话",30,true);
     var chk5 = checkinput("mobile","string","手机",30,true);
     var chk6 = checkinput("email","string","Email",30,true);

     if(chk1&&chk2&&chk3&&chk4&&chk6)
     {   
         mainForm.action = "saveok.jsp";
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
											                	<td class="add-content-title">资源管理 >> 修改个人设置</td>
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
				        										 <input type="hidden" name="id" value="<%=vo.getId()%>">
					                                             <input type="hidden" name="category" value="25">
					                                             <input type="hidden" name="code" value="DATABASE">
				        												<table border="0" id="table1" cellpadding="0" cellspacing="1"
																	width="100%">
																
							<tr style="background-color: #ECECEC;">
						    <TD nowrap align="right" height="24" width="10%">帐号&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;<%=vo.getUserid()%></TD>
							<TD nowrap align="right" height="24">姓名&nbsp;</TD>				
							<TD nowrap>
								&nbsp;<input type="text" name="name" value="<%=vo.getName()%>" size="16" class="formStyle"><font color="red">&nbsp;*</font>
							</TD>	
						</tr>
						<tr>	
							<TD nowrap align="right" height="24">密码&nbsp;</TD>				
							<TD nowrap><input name="password_0" type="hidden" value="<%=vo.getPassword() %>">
								&nbsp;<input name="password" type="password" size="16"  class="formStyle"><font color="red">&nbsp;(不填表示不修改)</font>
							</TD>						
							<TD nowrap align="right" height="24">确认密码&nbsp;</TD>				
							<TD nowrap>&nbsp;<input name="password0" type="password" size="16" class="formStyle">														
							</TD>	
						</tr>
						<tr style="background-color: #ECECEC;">
							<TD nowrap align="right" height="24">性别&nbsp;</TD>				
							<TD nowrap>&nbsp;<% if(vo.getSex()==1) out.print("男"); else out.print("女"); %></TD>								
							<td align="right" height="20">角色&nbsp;</td>
							<td colspan="3" align="left">&nbsp;<%=view.getRole(vo.getRole())%></td>
						</tr>
						<tr>
							<TD nowrap align="right" height="24">部门&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=view.getDept(vo.getDept())%></TD>								
							<td align="right" height="20">职务&nbsp;</td>
							<td colspan="3" align="left">&nbsp;<%=view.getPosition(vo.getPosition())%></td>
						</tr>
						<tr style="background-color: #ECECEC;">
							<TD nowrap align="right" height="24">电话&nbsp;</TD>				
							<TD nowrap>&nbsp;<input name="phone" type="text" size="16" class="formStyle" value="<%=SysUtil.ifNull(vo.getPhone())%>"></TD>								
							<td align="right" height="20">手机&nbsp;</td>
							<td colspan="3" align="left">&nbsp;<input name="mobile" value="<%=SysUtil.ifNull(vo.getMobile())%>" type="text" size="16" class="formStyle"></td>
						</tr>
						<tr>
							<TD nowrap align="right" height="24">Email&nbsp;</TD>				
							<TD nowrap>&nbsp;<input name="email" type="text" size="16" class="formStyle" value="<%=SysUtil.ifNull(vo.getEmail())%>"></TD>								
							<td align="right" height="20">&nbsp;</td>
							<td colspan="3" align="left">&nbsp;</td>
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
</HTML>