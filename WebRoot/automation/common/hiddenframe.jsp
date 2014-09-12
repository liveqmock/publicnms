<%@page language="java" contentType="text/html;charset=GB2312"%>
<%
String rootPath = request.getContextPath();
%>
<html>
  <head> 
  <script type="text/javascript">
     function hidedtree()
    {
       var psc = parent.search.cols;              
	   if(psc == "208,15,*"){	        
		    hidedtreeBar();
	   }else{
		    showdtreeBar();
	   }
     }
    function showdtreeBar(){	   
	   parent.search.cols = "208,15,*";	   
    }
   function hidedtreeBar(){	   
	   parent.search.cols = "0,15,*";
   }
   
  </script>
  <style>
  body{
  margin:0,padding:0;
  }
  </style>
   </head>
  
  <body>
    <table height="100%" width="100%">
       <tr>
         <td height="100%" width="3px" align="right" valign="middle">
           <img src="<%=rootPath%>/automation/images/arrow_close.jpg" onclick="hidedtree()">
         </td>
       </tr>
    </table>
  </body>
</html>
