<%@page language="java" contentType="text/html;charset=GB2312"%>

<%
	String rootPath = request.getContextPath();
  //首页时跳转的页面链接
	String rType =request.getParameter("rType");
	if(request.getParameter("rType") == null || request.getParameter("rType").equals("null")){
		// 点击性能菜单时可以查询出所有设备
		rType = "list";
	}
%>
<html>
	<head>

		<TITLE></TITLE>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="<%=rootPath%>/automation/css/ui.tabs.css" type="text/css" media="print, projection, screen">
		<link rel="stylesheet" href="<%=rootPath%>/automation/css/zTreeStyle.css" type="text/css">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="<%=rootPath%>/automation/js/jquery-1.4.4.min.js"></script>
		
		<script type="text/javascript" src="<%=rootPath%>/automation/js/swfobject.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/jquery.ztree.core-3.5.js"></script>
        <script type="text/javascript" src="<%=rootPath%>/automation/js/ui.tabs.pack.js" type="text/javascript"></script>
        <script type="text/javascript" src="<%=rootPath%>/automation/js/ui.tabs.ext.pack.js" type="text/javascript"></script>
        <script type="text/javascript" src="<%=rootPath%>/automation/js/autoTree.js" type="text/javascript"></script>
        
		<style>
body {
    font-size:12px;
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
	border: 0px;
	scrollbar-face-color: #E0E3EB;
	scrollbar-highlight-color: #E0E3EB;
	scrollbar-shadow-color: #E0E3EB;
	scrollbar-3dlight-color: #E0E3EB;
	scrollbar-arrow-color: #7ED053;
	scrollbar-track-color: #ffffff;
	scrollbar-darkshadow-color: #9D9DA1;
}
</style>

	

	</head>

	<body style="background-image: url('<%=rootPath%>/resource/image/global/bg6.jpg')" onload="init('<%=rType%>','<%=rootPath%>');">
		<div id="rotate" >
            <ul id="tabId">
                <li><a href="#fragment-1" ><span>IP</span></a></li>
                <li><a href="#fragment-2" onclick="changeMenuAlias()"><span>别名</span></a></li>
                <li><a href="#fragment-3" onclick="changeMenu()"><span>菜单</span></a></li>
            </ul>
            <div id="fragment-1" style="background-image: url('<%=rootPath%>/resource/image/global/bg6.jpg')">
              <div id="treeDemo" class="ztree" style="width: 100%; height: 50%;"></div>
            </div>
             <div id="fragment-2" style="background-image: url('<%=rootPath%>/resource/image/global/bg6.jpg')">
              <div id="treeAias" class="ztree" style="width: 100%; height: 50%;"></div>
            </div>
             <div id="fragment-3" >
            </div>
        </div>
		
		
	</body>
</html>