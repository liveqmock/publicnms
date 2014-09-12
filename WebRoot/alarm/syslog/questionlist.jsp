<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>
<%@ page import="com.afunms.event.model.*"%>
<%@ page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.topology.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	session.removeAttribute("current_page");
%>

<html>
	<head>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet"
			type="text/css" />
		<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css"
			rel="stylesheet">
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		
		<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script>
		<script src="<%=rootPath%>/resource/js/dtree.js"
			type="text/javascript"></script>
		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css"
			charset="gb2312" />
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>

		<link href="<%=rootPath%>/include/dtree.css" type="text/css"
			rel="stylesheet" />

<script language="javascript">	
  var delAction = "<%=rootPath%>/netsyslog.do?action=delete";
  var listAction = "<%=rootPath%>/netsyslog.do?action=list";

function loadQuestionTitle(){
	var questionTitleSlt = document.getElementById("questionTitle");
	Ext.Ajax.request({
		url:'<%=rootPath%>/questionTitleServlet',
		success:function(response,options){
			var ret = eval("(" + response.responseText + ")");
			for ( var i = 0; i < ret.length; i++) {
				questionTitleSlt.options[i] = new Option(ret[i], ret[i]);
			}
			loadQuestionDetail(questionTitleSlt.options[0].value);
		}
	});
}

function loadQuestionDetail(value){
	Ext.Ajax.request({
		url:'<%=rootPath%>/questionDetailServlet',
		params:{question:value},
		success:function(response,options){
			var ret = eval("(" + response.responseText + ")");
			var questionDetailSlt = document.getElementById("questionid");
			questionDetailSlt.options.length = 0;
			if(ret.length > 0){
				for ( var i = 0; i < ret.length; i++) {
					var text = ret[i].slice(0,4);
					var val = ret[i].slice(4);
					questionDetailSlt.options[i] = new Option(val, text);
					if(i == 0){
						questionDetailSlt.options[i].selected = true;
					}
				}
			}
		},
		failure:function(response,options){
			alert("发送请求失败!");
		}
	});
} 

function query(){
   	mainForm.action = "<%=rootPath%>/netsyslog.do?action=questionlist&jp=1";
  	mainForm.submit();
}
</script>
	
	</head>
	<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa" onload="loadQuestionTitle();">

		<form method="post" name="mainForm">
			<input type=hidden name="eventid">
			<!-- <table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%> -->
			<table id="body-container" class="body-container">
				<tr>
					<td width="200" valign=top align=center>
						<%=menuTable%>
					</td>
					<td align="center" valign=top>
						<table cellpadding="0" cellspacing="0" algin="center" style="width:98%"><!--  width="98%"  -->
							<tr>
								<td background="<%=rootPath%>/common/images/right_t_02.jpg"
									width="100%">
									<table width="100%" cellspacing="0" cellpadding="0">
										<tr>
											<td align="left">
												<img src="<%=rootPath%>/common/images/right_t_01.jpg"
													width="5" height="29" />
											</td>
											<td class="layout_title">
												<b>告警 >> SYSLOG管理 >> 问题对应</b>
											</td>
											<td align="right">
												<img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							
							<tr>
								<td>
									<table width="100%" cellpadding="0" cellspacing="1">
										<tr height="30">
											<td bgcolor="#ECECEC" width="30%" style="padding-left:150px">
												问题模块：
											</td>
											<td bgcolor="#ECECEC" width="70%">
												<select id="questionTitle" onchange="loadQuestionDetail(this.value)"  style="width:480"></select>
											</td>
										</tr>
										<tr height="160">
											<td bgcolor="#ECECEC" width="30%" style="padding-left:150px">
												请选择具体问题：
											</td>
											<td bgcolor="#ECECEC" width="70%">
												<select id="questionid" name="questionid" size="10" style="width:480"></select>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							
							<tr>
								<td>
									<table width="100%" cellpadding="0" cellspacing="1">
										<tr height="30">
											<td bgcolor="#ECECEC" width="100%" align='center'>
												<input type="button" name="submitss" value="查询" onclick="query()">
											</td>
										</tr>
									</table>
								</td>
							</tr>


							<tr>
								<td background="<%=rootPath%>/common/images/right_b_02.jpg">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td align="left" valign="bottom">
												<img src="<%=rootPath%>/common/images/right_b_01.jpg"
													width="5" height="11" />
											</td>
											<td></td>
											<td align="right" valign="bottom">
												<img src="<%=rootPath%>/common/images/right_b_03.jpg"
													width="5" height="11" />
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
	</BODY>
</HTML>
