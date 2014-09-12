package com.afunms.initialize;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.base.ManagerFactory;

public class AjaxController extends HttpServlet 
{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String uri = req.getRequestURI();
		int lastSeparator = uri.lastIndexOf("/") + 1;
        int dotSeparator = uri.lastIndexOf(".");
        String manageClass = uri.substring(lastSeparator,dotSeparator);
        String action = req.getParameter("action");
        AjaxManagerInterface manager = ManagerFactory.getAjaxManager(manageClass);
        manager.setRequest(req, resp);
        manager.execute(action);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req,resp);
	}
	
}
