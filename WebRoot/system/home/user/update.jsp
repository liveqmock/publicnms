<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="java.util.List"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.system.model.Role"%>
<%@page import="com.afunms.common.util.SessionConstant"%>   
<%@page import="com.afunms.home.user.model.HomeUserModel"%> 
<%@page import="com.afunms.home.role.model.HomeRoleModel"%>  


<%

    System.out.println("-----------user个性修改页面-----------");
    String rootPath = request.getContextPath(); 
    List homeUserModelList = (List) request.getAttribute("homeUserModelList");//用户模块列表
    User uservo = (User) session.getAttribute(SessionConstant.CURRENT_USER);
    List homeRoleList = (List) request.getAttribute("homeRoleList");//角色对应模块
    Role role =(Role )request.getAttribute("role"); 
    String menuTable = (String) request.getAttribute("menuTable");
    request.setAttribute("homeUserModelList",homeUserModelList);
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
	     mainForm.action = "<%=rootPath%>/homeuser.do?action=save&&roleId=<%=role.getId()%>"; 
	     mainForm.submit();
	  }
	  
  </script>
	</head>
	<body class="body" id="body">
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
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
								                	<tr>
														<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
									                	<td class="content-title">用户首页模块个性配置： </td>
									                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
									       			</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table id="content-body" class="content-body">
													<tr>
														<td>
															<table>
																<tr>
																<%
																    if (homeRoleList != null) {
																		for (int i = 0; i < homeRoleList.size(); i++) {
																		    HomeRoleModel model = (HomeRoleModel) homeRoleList.get(i);
																%>
																	<td align="left" class="body-data-list">
																		<input id="<%=model.getEnName()%>" name="checkbox" type="checkbox"
																		value="<%=model.getChName()%>"  /><%=model.getChName()%>
																	</td>
																</tr>
																<%
																    	}
																    }
																%>
															</table>
														</td>
													</tr>
												</table>
											</td>
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
			<%for(int m = 0 ; m < homeUserModelList.size(); m ++){%>
				if(checkbox.id=="<%=((HomeUserModel) homeUserModelList.get(m)).getEnName()%>"
					&&<%=((HomeUserModel) homeUserModelList.get(m)).getVisible() == 1%>){ 
					checkbox.checked = true;
				}
			<%}%>
		}
	}
	
	checkedbox(); 
	  
  </script>
</html>
