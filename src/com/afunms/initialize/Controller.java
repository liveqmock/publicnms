/**
 * <p>Description:action center,at the same time, the control legal power</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.initialize;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.util.SessionConstant;

import com.afunms.system.model.Function;
import com.afunms.system.model.User;
import com.afunms.system.util.CreateMenuTableUtil;
import com.afunms.system.util.CreateRoleFunctionTable;
import com.afunms.system.dao.RoleFunctionDao;
import com.afunms.system.dao.AccreditDao;
import com.afunms.common.util.SysLogger;
import com.afunms.common.base.*;

public class Controller extends HttpServlet
{
   private static final long serialVersionUID = 541128324260833824L;
	
   public void init(ServletConfig config) throws ServletException
   {
	  
      super.init(config);
   }

   public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
   {
	   try{
		   processHttpRequest(request, response);
	   }catch(Exception e){
		   
	   }
   }

   public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      processHttpRequest(request, response);
   }

   //处理业务流程
   private void processHttpRequest(HttpServletRequest request,HttpServletResponse response)
   {
      String jsp = null;
      try
      {
    	 
         request.setCharacterEncoding("gb2312");
         response.setContentType("text/html;charset=gb2312");
         //SysLogger.info("==================################1111");
         String uri = request.getRequestURI();    
         StringBuffer url = request.getRequestURL();
         int lastSeparator = uri.lastIndexOf("/") + 1;
         int dotSeparator = uri.lastIndexOf(".");
         String manageClass = uri.substring(lastSeparator,dotSeparator);   //截取类名映射

       //int auth = 1;
         int auth = 0;
         if("alarm".equals(manageClass)) //遇到alarm不检查权限
        	auth = 1;
         else
            auth = authenticate(request);
         
         if(auth==-1)
            jsp = "/common/error.jsp?errorcode=" + ErrorMessage.NO_LOGIN;
//         else if(auth==0)
//            jsp = "/common/error.jsp?errorcode=" + ErrorMessage.NO_RIGHT;
         else  //有权限才能继续
         {
             String action = request.getParameter("action");
      
             ManagerInterface manager = ManagerFactory.getManager(manageClass);
             manager.setRequest(request);
             jsp = manager.execute(action);
             if(jsp == null)
                    jsp = "/common/error.jsp?errorcode=" + manager.getErrorCode();
             
//             System.out.println("com.afunms.initialize----Controller.java----92 hang====->"+jsp);
//             if(jsp == null)
//                jsp = "/common/error.jsp?errorcode=" + manager.getErrorCode();
         }
         //---------------------------------------
         /**
         *   根据具体页面创建菜单menu
         **/
         CreateMenuTableUtil cmtu = new CreateMenuTableUtil();
         try{
        	 cmtu.createMenuTableUtil(jsp, request);
         }catch(Exception e){
        	 //SysLogger.info("&&&&&&&&&&&&&&&&&&&&&1111");
        	 e.printStackTrace();
         }
         //cmtu.createMenuTableutil(jsp, request);
         
         //---------------------------------------
//        SysLogger.info(jsp+"  ####111######");
         RequestDispatcher disp = getServletContext().getRequestDispatcher(jsp);
         if(disp != null && request != null && response != null )
        	 disp.forward(request, response);
      }
      catch(Exception e)
      {
          //SysLogger.error("Controller.processHttpRequest()",e);
          e.printStackTrace();
      }
   }

   //操作权限检查:返回三个值 -1:没有登录,0:没有权限,1:有权限
   public int authenticate(HttpServletRequest request)
   {
      String action = request.getParameter("action");
      if(action == null)action="";
      if(action.equals("login")||action.equals("logout")) return 1;

      int result = -1;
      try
      {
         HttpSession session = request.getSession();
         User user = (User)session.getAttribute(SessionConstant.CURRENT_USER); //当前用户
         //System.out.println(user.getName()+"---------------------------------");
         String menu = request.getParameter("menu");
         if(menu==null)
            menu = (String)session.getAttribute(SessionConstant.CURRENT_MENU);
         else
         {
            session.setAttribute(SessionConstant.CURRENT_MENU,menu);
            session.setMaxInactiveInterval(1800);
         } 
         //System.out.println("menu===="+menu);
//         if(user==null||menu==null) return -1;  //没有登录 
           if(user==null) return -1;  //没有登录
//         if(user.getRole()==0) return 1; //对于admin,不检查权限
//         
     //    result = 0;
         //------------------先确定请求的操作是只读或可写-----------------------
    /*     int operate = 1;//默认为被屏蔽         
         Hashtable actionMap = ResourceCenter.getInstance().getActionMap();         
         try
		 {
         	operate = ((Integer)actionMap.get(action)).intValue();	
		 }
         catch(Exception e)
		 {
         	operate = 1;
         	System.out.println(action + ",不在action-config.xml中...");
		 }
		 */
     //    if(operate==1) return 0;
        
         result = 1;
      }
      catch(Exception e)
      {
         result = 0;
      }
      return result;
   }
   

}
