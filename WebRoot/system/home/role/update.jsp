<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="java.util.List"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.system.model.Role"%>
<%@page import="com.afunms.common.util.SessionConstant"%>   
<%@page import="com.afunms.home.role.model.HomeRoleModel"%> 
<%@page import="com.afunms.home.module.model.ModuleModel"%> 

<%
    String rootPath = request.getContextPath(); 
    List homeRoleList = (List) request.getAttribute("homeRoleList");
    User uservo = (User) session.getAttribute(SessionConstant.CURRENT_USER);
    List moduleList = (List) request.getAttribute("moduleList");
    Role role =(Role )request.getAttribute("role"); 
    String menuTable = (String) request.getAttribute("menuTable");
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>

		<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css"
			charset="gb2312" />
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="gb2312"></script>

		<!--nielin add for timeShareConfig at 2010-01-04 start-->
		<script type="text/javascript"
			src="<%=rootPath%>/application/resource/js/timeShareConfigdiv.js"
			charset="gb2312"></script>
		<!--nielin add for timeShareConfig at 2010-01-04 end-->

		<script language="JavaScript" type="text/javascript">

	  function toUpdate()
	  { 
	     mainForm.action = "<%=rootPath%>/homerole.do?action=save&&roleId=<%=role.getId()%>"; 
	     mainForm.submit();
	  }
	  
  </script>
	</head>
	<body>
		<form id="mainForm" method="post" name="mainForm">
			<table id="body-container" class="body-container">
				<tr>
					<td width="200" valign=top align=center rowspan="2">
						<%=menuTable%>

					</td>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-add">
									<table width="100%" background="<%=rootPath%>/common/images/right_t_02.jpg" cellspacing="0" cellpadding="0">
										<tr>
											<td align="left"> 
												<img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /> 
											</td>
											<td class="layout_title">
												<b>角色首页功能模块分配</b>
											</td>
											<td align="right"> 
												<img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /> 
											</td>
										</tr>
									</table>
									<table>
										<tr>
											<td>
												<table>
													<%
													    if (moduleList != null) {
															for (int i = 0; i < moduleList.size(); i++) {
															    ModuleModel model = (ModuleModel) moduleList.get(i);
													%>
													<tr valign='middle' height='30'>
														<td bgcolor="#ffffff" >
															<input id="<%=model.getEnName()%>" name="checkbox"
																type="checkbox" value="<%=model.getChName()%>"  /><%=model.getChName()%>
														</td>
													</tr>
													<%
													    }
													    }
													%>
													
												</table>
											</td>
										</tr>
										<tr>
										</tr>
										<tr>
											<td>
												<table>
													<tr valign='middle' height='30'>
														<td bgcolor="#ffffff" align='left'>
															<input id="write" type="button" class="button" value="&nbsp;确&nbsp;&nbsp;定&nbsp; "
																name="B2" onclick="toUpdate()">
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
		</form>
	</body>
	<script language="JavaScript" type="text/javascript"> 
	function checkedbox(){   
		var allcheckbox = document.getElementsByName("checkbox"); 
		for(var i = 0 ,j = allcheckbox.length; i < j ; i++){ 
			checkbox = allcheckbox[i];
			<%for(int m = 0 ; m < homeRoleList.size(); m ++){
				%>
				if(checkbox.id=="<%=((HomeRoleModel) homeRoleList.get(m))
				.getEnName()%>"
					&&<%=((HomeRoleModel) homeRoleList.get(m))
				.getVisible() == 1%>){ 
					checkbox.checked = true;
				}
				<%
			}%>
		}
	}
	
	checkedbox(); 
	  
  </script>
</html>
