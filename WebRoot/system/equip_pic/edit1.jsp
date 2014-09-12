<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.system.model.*"%>
<%@page import="com.afunms.system.util.UserView"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.config.dao.*"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.common.util.SessionConstant" %>
<%
	UserView view = new UserView();
   equip vo = (equip)request.getAttribute("vo");
   String rootPath = request.getContextPath();
   
       
        User operator = (User)session.getAttribute(SessionConstant.CURRENT_USER); 
%>
<html><head>
<title>Equip详细信息</title>

<script language="JavaScript" type="text/javascript">
function toAdd()
  {
     mainForm.action = "<%=rootPath%>/equip.do?action=update&id=<%=vo.getId()%>";
     mainForm.submit();
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
                    <td class="layout_title">系统管理 >> 系统配置 >> Equip详细信息</td>
                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
                  </tr>
              </table>
			  </td>
			  </tr>

			<tr>
			<td colspan="2">
				  <table cellpadding="0" cellspacing="1" width="100%" id="detail-content-body" class="detail-content-body">
						<tr style="background-color: #FFFFFF;">
						    <TD nowrap align="right" height="25" width="10%">类别:&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;<input type='text' name ='category' value='<%=vo.getCategory()%>' style="width: 200px;" readOnly ></TD>
							<TD nowrap align="right" height="25">中文名称:&nbsp;</TD>				
							<TD nowrap>&nbsp;<input type='text' name ='cn_name' value='<%=vo.getCn_name()%>' style="width: 200px;" readOnly ></TD>	
						</tr>
						<tr style="background-color: #ECECEC;">	
							<TD nowrap align="right" height="25">英文名称:&nbsp;</TD>				
							<TD nowrap>&nbsp;<input type='text' name ='cn_name' value='<%=vo.getEn_name()%>' style="width: 200px;" readOnly ></TD>						
							<TD nowrap align="right" height="25">Topo_image:&nbsp;</TD>
							<TD nowrap>&nbsp;<input type='text' name ='topo_image' value='<%=vo.getTopo_image()%>' style="width: 200px;" ></TD>	
						</tr>
						
						<tr style="background-color: #FFFFFF;">
							<TD nowrap align="right" height="25">Lost_image:&nbsp;</TD>				
							<TD nowrap>&nbsp;<input type='text' name ='lost_image' value='<%=vo.getLost_image()%>' style="width: 200px;"  ></TD>								
							<TD nowrap align="right" height="25">Alarm_image:&nbsp;</TD>				
							<TD nowrap>&nbsp;<input type='text' name ='alarm-image' value='<%=vo.getAlarm_image()%>' style="width: 200px;" ></TD>	
						</tr>
						
						<tr style="background-color: #ECECEC;">
							<td align="right" height="24">Path:&nbsp;</td>
							<td nowrap width="40%" colspan="3" >&nbsp;<input type='text' name ='cn_name' value='<%=vo.getPath()%>' style="width: 400px;"  ></td>
						</tr>
						
						
						<tr style="background-color: #FFFFFF;">
							<TD nowrap colspan="4" align="center">
								<br>
								<input type="button" value="保存" style="width:50" onclick="toAdd()">&nbsp;&nbsp;
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
