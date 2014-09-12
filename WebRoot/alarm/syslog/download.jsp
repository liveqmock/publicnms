
<%@ page  import="com.afunms.common.util.CEIString"%>
<%@ page  import="com.jspsmart.upload.SmartUpload"%>
<%
  String rootPath = request.getContextPath();
%>
<html>
<head>
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="utf-8" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="utf-8"/>
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="utf-8"></script>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 
<script language="javascript">	 
  
  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 50);	
	
});
</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa">
<%    
    String fileName = (String)request.getAttribute("filename");
	try
	{
	    SmartUpload download = new SmartUpload();    
	    download.initialize(pageContext);   
	    download.downloadFile(CEIString.native2Unicode(fileName));
	    	
	}
	catch(Exception e)
	{
		e.printStackTrace();
		System.out.println("Error in download!");
	}finally{
		out.clear();
    	out = pageContext.pushBody();
	}
%>
<div id="loading">
	<div class="loading-indicator">
	<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif" width="32" height="32" style="margin-right: 8px;" align="middle" />Loading...</div>
</div>
<div id="loading-mask" style=""></div>
<div align=center>
<input type=button value="¹Ø±Õ´°¿Ú" onclick="window.close()">
</div>

</BODY>
</HTML>