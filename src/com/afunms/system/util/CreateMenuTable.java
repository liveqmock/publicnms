package com.afunms.system.util;

import java.util.ArrayList;
import java.util.List;

import com.afunms.system.model.Function;



public class CreateMenuTable {
	
	private StringBuffer table ;
	private String rootPath;
	private CreateRoleFunctionTable crft;
	public CreateMenuTable(String rootPath){
		table = new StringBuffer();
		this.rootPath = rootPath+"/";
		crft = new CreateRoleFunctionTable();
	}
	
	/**
	 *  根据menu_list来拼接菜单
	 * @param menu_list
	 * @return
	 */
	public String createMenuTable(List<Function> menu_list){	
		Function root = crft.getFunctionRoot(menu_list);
		List<Function> menuSecond_list = crft.getFunctionChild(root, menu_list);
		getMenuTable(menuSecond_list,menu_list);
		return table.toString();
	}
	

	/**
	 * 	拼接二级菜单
	 * @param menuSecond_list
	 * @param menu_list
	 * @return
	 */
	private StringBuffer getMenuTable(List<Function> menuSecond_list,List<Function> menu_list){
		for(int i = 0 ; i < menuSecond_list.size() ; i++){
			Function function = menuSecond_list.get(i);
			table.append("<table id=\""+function.getFunc_desc()+"\" valign=\"top\" algin=\"left\"" +
					" cellpadding=\"0\" cellspacing=\"0\">");
			table.append("<tr valign=\"top\" align=\"center\">");
			table.append("<td id=\""+"\" valign=\"top\" align=\"center\" >");
			table.append(
					"<div  class=\"tit\" id=\""+"menu"+function.getFunc_desc()+"\" title=\"菜单标题\" >" +
					"	<a href=\"#nojs\" title=\"折叠菜单\" target=\"\" class=\"on\" id=\"menu1_a\" tabindex=\"1\">" +
					function.getCh_desc()+"</a> " +
							"</div>");
				
			table.append("</td>");
			table.append("</tr>");
			table.append("<tr align=\"left\" width=\"100%\">");
			table.append("<td valign=\"top\" align=\"left\" width=\"100%\">");
			table.append("<div align=\"left\" class=\"list\" id=\""+"menu"+function.getFunc_desc()+"child"+"\" title=\"菜单功能区\">");
		//-----------------------------------------------------------------------
			List<Function> menuThird_list = crft.getFunctionChild(function , menu_list);
			
			createThirdMenuTable(menuThird_list,menu_list);
		//-----------------------------------------------------------------------
			table.append("</div>");
			table.append("</td>");
			table.append("</tr>");
			table.append("</table>");
			table.append("</br>");
		}
		return null;
	}
	
	/**
	 *  拼接三级菜单
	 * @param menuThird_list
	 * @param menu_list
	 * @return
	 */
	private StringBuffer createThirdMenuTable(List<Function> menuThird_list,List<Function> menu_list){
		table.append("<ul>");
		for(int i = 0 ; i < menuThird_list.size() ; i++){
			table.append("<li>");
			if(menuThird_list.get(i).getIsCurrentWindow()==1){
				table.append("<img src="+rootPath+menuThird_list.get(i).getImg_url()+
					" width=18 border=0 >&nbsp;"+"<a href=\"javascript:void(null)\""
					+"onClick='window.open(\""+rootPath+menuThird_list.get(i).getUrl()+
					"\",\"fullScreenWindow\", \"toolbar=no,height=\"+window.screen.height + \",\"+\"width=\" + " +
					"(window.screen.width-8)+ \",scrollbars=no\"+\"screenX=0,screenY=0\")'>&nbsp;"+
					menuThird_list.get(i).getCh_desc()+"</a>");
			}else{				
				table.append("<img src="+rootPath+menuThird_list.get(i).getImg_url()+
						" width=18 border=0>&nbsp;"+"<a href="+rootPath+menuThird_list.get(i).getUrl()+
						">&nbsp;"+menuThird_list.get(i).getCh_desc()+"</a>");
			}
			table.append("</li>");
		}
		table.append("</ul>");
		return table;
	}
	
	
	
	
}
