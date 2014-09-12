<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="wfm.encode.MD5"%>
<%
   User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
   if(user==null)
      response.sendRedirect("../../common/error.jsp?errorcode=3001");
   else
   {   
      MD5 md = new MD5();
      String pwd = md.getMD5ofStr(request.getParameter("pwd"));
      if(user.getPassword().equals(pwd))
         response.sendRedirect("person.jsp");
      else
         response.sendRedirect("../../common/error.jsp?errorcode=1000");   
   }      
%>