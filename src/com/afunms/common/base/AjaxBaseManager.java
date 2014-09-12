package com.afunms.common.base;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AjaxBaseManager 
{
	protected HttpServletRequest request;  
	protected HttpServletResponse response;
	protected HttpSession session;
	protected PrintWriter out;
	public void setRequest(HttpServletRequest req)
	{
	      request = req;
	      //response.setContentType("text/html;charset=GB2312");
	      //response.setContentType("text/html;charset=UTF-8");
	      session = request.getSession();
	      try{
	    	  out = response.getWriter();
	      }catch(Exception e){e.printStackTrace();}
	}
	public void setRequest(HttpServletRequest req,HttpServletResponse res)
	   {
	      request = req;
	      response = res;
	      response.setContentType("text/html;charset=GB2312");
	      //response.setContentType("text/html;charset=UTF-8");
	      session = request.getSession();
	      try{
	    	  out = response.getWriter();
	      }catch(Exception e){e.printStackTrace();}
	   }
	
	protected String getParaValue(String para)
	   {
	      return request.getParameter(para);
	   }
	 protected int getParaIntValue(String para)
	   {
	      int result = -1;
	      String temp = request.getParameter(para);
	      if(temp!=null)
	         result = Integer.parseInt(temp);
	      return result;
	   }
}
