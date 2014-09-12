<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.system.util.UserView"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.config.dao.*"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.common.util.SessionConstant" %>
<%
	UserView view = new UserView();
   User vo = (User)request.getAttribute("vo");
   String rootPath = request.getContextPath();
   
         BusinessDao bussdao = new BusinessDao();
       List allbuss = bussdao.loadAll();   
       
       String businessids = vo.getBusinessids();
       if(businessids == null)businessids = "";
       String[] bids = businessids.split(","); 
       List bussList = new ArrayList();
       if(bids.length>0){
    	   for(int i=0;i<bids.length;i++){
    	   	if(bids[i].trim().length()==0)continue;
    	   	bussList.add(bids[i]);
    	   }	   
       }
       
        User operator = (User)session.getAttribute(SessionConstant.CURRENT_USER); 
%>
<html><head>
<title>用户详细信息</title>

<script language="JavaScript" type="text/javascript">
  function toAdd()
  {
     var args = window.dialogArguments;
     args.xx="<%=vo.getUserid()%>";
     args.toedit();
  	 window.close();
  }
</script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<meta http-equiv="Pragma" content="no-cache">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

</head>
<BODY  id="body" class="body" background="<%=rootPath%>/resource/image/bg6.jpg">
<form id="mainForm" method="post" name="mainForm">
<table>
	<tr>
		<td width="16">　</td>
		<td align="center">
		<br>
		<table width="100%" border=0 cellpadding=0 cellspacing=0>
			<tr>
				<td background="<%=rootPath%>/common/images/right_t_02.jpg" width="100%">
				<table width="100%" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
                    <td class="layout_title">系统管理 >> 用户管理 >> 用户详细信息</td>
                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
                  </tr>
              </table>
			  </td>
			  </tr>

			<tr>
			<td colspan="2">
				  <table cellpadding="0" cellspacing="1" width="100%" id="detail-content-body" class="detail-content-body">
						<tr style="background-color: #FFFFFF;">
						    <TD nowrap align="right" height="25" width="10%">账号&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;<%=vo.getId()%></TD>
							<TD nowrap align="right" height="25">姓名&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=vo.getName()%></TD>	
						</tr>
						<%
						String sex = null;
						if(vo.getSex()==1) sex="男";
						else sex="女";
						%>	
						<tr style="background-color: #ECECEC;">	
							<TD nowrap align="right" height="25">性别&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=sex%></TD>						
							<TD nowrap align="right" height="25">角色&nbsp;</TD>
							<TD nowrap>&nbsp;<%=view.getRole(vo.getRole())%></TD>	
						</tr>
						
						<tr style="background-color: #FFFFFF;">
							<TD nowrap align="right" height="25">部门&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=view.getDept(vo.getDept())%></TD>								
							<TD nowrap align="right" height="25">职务&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=view.getPosition(vo.getPosition())%></TD>	
						</tr>
						<tr style="background-color: #ECECEC;">
						    <TD nowrap align="right" height="25" width="10%">电话&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;<%=SysUtil.ifNull(vo.getPhone())%></TD>
							<TD nowrap align="right" height="25">手机&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=SysUtil.ifNull(vo.getMobile())%></TD>	
						</tr>
						<tr style="background-color: #FFFFFF;">
							<td align="right" height="24">Email&nbsp;</td>
							<td nowrap width="40%" colspan="3" >&nbsp;<%=SysUtil.ifNull(vo.getEmail())%></td>
						</tr>
						<tr style="background-color: #ECECEC;">
							<TD nowrap align="right" height="25">所属业务&nbsp;</TD>				
							<% 
								String bussName = "";
								int k = 0;
								if( allbuss.size()>0){
									for(int i=0;i<allbuss.size();i++){
										Business buss = (Business)allbuss.get(i);
										if(bussList.contains(buss.getId()+"")){
										    k++;
										    if (k!=1){ bussName = bussName + ',' + buss.getName();}
										    else{
											bussName = bussName + buss.getName();}
										}										
					 				}
								}
							%>
							<td nowrap width="40%" colspan="3" >&nbsp;<%=bussName%></td>
							</TD>
						</tr>
						
						<tr style="background-color: #FFFFFF;">
							<TD nowrap colspan="4" align="center">
								<br>
								<!--<input type="button" value="编辑" style="width:50" onclick="toAdd()">&nbsp;&nbsp;-->
								<input type="reset"  style="width:50" value="关闭" onclick="javascript:window.close()">
							</TD>	
						</tr>						
				</TABLE>
				</td>
			</tr>
			<tr>
              <td background="<%=rootPath%>/common/images/right_b_02.jpg" ><table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
                    <td></td>
                    <td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>
                  </tr>
              </table></td>
            </tr>	
		</table>
		</td>
	</tr>
</table>
</form>	
</body>
</html>
