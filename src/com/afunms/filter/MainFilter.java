package com.afunms.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.afunms.common.util.SessionConstant;
import com.afunms.system.model.Function;
import com.afunms.system.model.User;
import com.afunms.system.util.CreateMenuTableUtil;
import com.afunms.system.util.CreateRoleFunctionTable;

public class MainFilter implements Filter {
	
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)arg0;
		HttpServletResponse response = (HttpServletResponse)arg1;
		
		
		
		String jsp = request.getRequestURI();
		if(jsp!=null&&jsp.endsWith(".jsp")){
			CreateMenuTableUtil cmtu = new CreateMenuTableUtil();
			cmtu.createMenuTableUtil(jsp, request);
			
		}
		
		arg2.doFilter(request, response);

	}

	
	
	
	
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
