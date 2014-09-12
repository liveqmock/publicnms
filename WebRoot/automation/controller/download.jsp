<%@ page language="java" contentType="text/html; charset=GBK" %>
<%@ page  import="com.afunms.common.util.CEIString"%>
<%@ page  import="com.jspsmart.upload.SmartUpload"%>
<%
  String rootPath = request.getContextPath();
%>
<html>
<head>
        <link rel="stylesheet" type="text/css" href="<%=rootPath%>/automation/css/ext/ext-all.css" charset="gb2312" />
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-base.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-lang-zh_CN.js" charset="utf-8"></script>
<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">

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
<table align="center" cellSpacing="0" cellPadding="0" id="tblListTitle" class="WorkPage_ListTable" width="90%" border="0" > 
<tr> 
<td  height="7" align=center >
<input type=button value="¹Ø±Õ´°¿Ú" onclick="window.close()">
</td>
</tr>
</table>

</BODY>
</HTML>