package com.afunms.system.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.afunms.bpm.util.MenuConstance;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysLogger;
import com.afunms.initialize.ResourceCenter;
import com.afunms.system.model.Function;
import com.afunms.system.model.User;

public class CreateMenuTableUtil {
	private Map<String, String> map;
	/**
	 * 数字代表nms_func表中 一级菜单的ID
	 * 1为资源
	 * 23为告警
	 * 35为报表
	 * 39为应用
	 * 62为系统管理
	 * 
	 */
	public CreateMenuTableUtil(){
		//map = new java.util.Hashtable<String, String>();
//		map.put("topology", "1");
//		map.put("alarm", "23");
//		map.put("capreport", "35");
//		map.put("application", "39");
//		map.put("config", "62");
//		map.put("portconfig", "1");
//		map.put("diskconfig", "1");
//		map.put("ipmacchange", "1");
//		map.put("ipmacbase", "1");
//		map.put("maclocate", "1");
//		map.put("ipmac", "1");
//		map.put("detail", "1");
//		map.put("panel", "1");
//		map.put("system", "62");
//		map.put("sysset", "62");
//		map.put("common", "0");
//		map.put("db", "39");
//		map.put("flex", "1");
//		map.put("jianshi", "1");
		map = ResourceCenter.getInstance().getMenuMap();
	}
	
	/**
	 *  根据具体的jsp 和 request 来创建菜单
	 * @param jsp
	 * @param request
	 */
	public void createMenuTableUtil(String jsp,HttpServletRequest request){
//		System.out.println("============"+jsp);
		String flag = request.getParameter("flag");
		//System.out.println("----------------->>>>>"+Integer.parseInt(flag));
		if(request.getParameter("flag")!=null&&"1".equals(request.getParameter("flag"))){
			StringBuffer menuTable = new StringBuffer();
			menuTable.append("<script language=\"JavaScript\" type=\"text/JavaScript\">");
			menuTable.append("document.getElementById(\"container-menu-bar\").parentElement.style.display=\"none\";");
			menuTable.append("</script>");
			request.setAttribute("menuTable", menuTable.toString());
			MenuConstance.setMenuTable(menuTable.toString());

			request.setAttribute("flag", flag);
		} else {
			
			String rootNode = getRootNode(jsp);
		System.out.println("-------"+rootNode);
			if(rootNode!=null){
				createMenuTable(rootNode , request);
			}
		}
		//SysLogger.info("&&&&&&&&&&&&&&&&&&&&&777777777777777");
		return;
	}
	
	/**
	 *  根据jsp的目录来得到其所在的一级菜单
	 * @param jsp
	 * @return
	 */
	public String getRootNode(String jsp){
		//System.out.println("...................."+jsp);
		if(jsp.equals("/index.jsp")||jsp.equals("/login.jsp")){
			return null;
		}
		String rootNode = null;
		//遇见首页之间返回 0 
		if(jsp.contains("common/top.jsp")){
			return "0";
		}
		String[] menu_list = jsp.split("/");
		if(menu_list!=null&&menu_list.length>=1){
			for(int j = menu_list.length-1 ; j>=0;j--){
				rootNode = map.get(menu_list[j]);
				if(rootNode!=null ){
					return rootNode ;
				}
			}
		}
		return null;
	}
	
	/**
	 *  根据一级菜单 和 request 创建具体菜单
	 * @param rootNode
	 * @param request
	 */
	public void createMenuTable(String rootNode ,HttpServletRequest request){
		try{
			String rootPath = request.getContextPath(); 
			HttpSession session = request.getSession();
		    User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		    if(user!=null){
		    	CreateRoleFunctionTable crft = new CreateRoleFunctionTable(rootPath); 
		    	//0 代表此页面是否为头菜单页面(/common/top.jsp)
		    	if("0".equals(rootNode)){
		    		List<Function> list = crft.getRoleFunctionListByRoleId(String.valueOf(user.getRole()));
		    		List<Function> menuRoot_list = crft.getAllMenuRoot(list);
//		    		for(Function l : list){
//		    			System.out.println("CreateMenuTableUtil.............>>>>>>>>>>>>>>"+l);
//		    		}
//		    		System.out.println("com.afunms.system.util---------CreateMenuTableUtil.java------------128行---------->>>."+menuRoot_list);
				    
		    		request.setAttribute("menuRoot", menuRoot_list);
		  		    request.setAttribute("roleFunction", list);
		    	}else{
		    		String menuTable = crft.getPageFunctionTable(rootNode, String.valueOf(user.getRole()));
//		    System.out.println("com.afunms.system.util---------CreateMenuTableUtil.java------------128行---------->>>."+menuTable);
		    		request.setAttribute("menuTable", menuTable);
		    		MenuConstance.setMenuTable(menuTable.toString());

		    	}
		    }
			return;
		}catch(Exception e){
			e.printStackTrace();
		}
		return;
	}
}
