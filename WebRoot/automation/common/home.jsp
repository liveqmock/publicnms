<%@page language="java" contentType="text/html;charset=GB2312"%>
<%
  
  String rType = request.getParameter("rType");
String lType = request.getParameter("lType");
String rootPath=request.getContextPath();
String src="";
String rightFrameSrc="";
if(lType!=null&&lType.equals("tree")){
	src="tree.jsp?rType="+rType;
	rightFrameSrc="#";
}else{
	String name="";
	if(rType.equals("strategyList")||rType.equals("showAllDevice")){
		name="configRule";
	}else if(rType.equals("ready_multi_modify")||rType.equals("deviceList")||rType.equals("ready_multi_audit")){
		name="remoteDevice";
	}else if(rType.equals("ready_backupForBatch")||rType.equals("ready_deployCfgForBatch")||rType.equals("ready_timingBackup")){
		name="netCfgFile";
	}else if(rType.equals("inspectionList")||rType.equals("deviceCfg")){
		name="autoControl";
	}
	rightFrameSrc=rootPath+"/"+name+".do?action="+rType+"&flag=1";
	src="menu.html?menuFlag="+name;
}
%>
<html>
<head>
<title>设备性能</title>  
<script type="text/javascript">
	/**
	*初始化按钮颜色
	*/
	
</script>
</head>  
	<frameset  id="search"  rows="*" cols="208,15,*" framespacing="0" rows="*" frameborder="no">
	  <frame name="leftFrame" id="leftFrame"  src="<%=src%>">
	  <frame name="hiddenFrame"  scrolling="no" src="hiddenframe.jsp">
	  <frame name="rightFrame"  id="rightFrame" src="<%=rightFrameSrc %>">
	</frameset>
</html>
