package com.afunms.system.util;

import java.util.ArrayList;
import java.util.List;

import com.afunms.system.model.Function;



public class CreateMenuTable2 {
	
	private StringBuffer table ;
	private String rootPath;
	private CreateRoleFunctionTable crft;
	public CreateMenuTable2(String rootPath){
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
		
		table.append("<style type=\"text/css\">" +
				"<!--" +
				"body {" +
				"margin-left: 0px;" +
				"margin-top: 0px;" +
				"background-color: #ababab;" +
				"}" +
				"-->" +
				"</style>");
		table.append("<div  style=\"height:100%;background-image:url(/afunms/common/images/leftbg.jpg)\">");
		for(int i = 0 ; i < menuSecond_list.size() ; i++){
			Function function = menuSecond_list.get(i);
			table.append("<table width=\"160\" " +
					"border=\"0\" cellspacing=\"0\" cellpadding=\"0\" " +
					"style=\"margin:3px 2px 2px 2px;\">");
			table.append("<tr>");
			table.append("<td background=\"/afunms/common/images/left_top_02.jpg\">");
			table.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			table.append("<tr>");
			table.append("<td align=\"left\">" +
							"<img src=\"/afunms/common/images/left_top_01.jpg\" width=\"5\" height=\"29\" />" +
						 "</td>");
			table.append("<td class=\"layout_title\">");
			
			table.append(
					"<div align=\"center\" class=\"tit\" id=\""+"menu"+function.getFunc_desc()+"\" title=\"菜单标题\" >" +
					"	<a href=\"#nojs\" title=\"折叠菜单\" target=\"\" class=\"on\" id=\"menu1_a\" tabindex=\"1\">" +
					function.getCh_desc()+"</a> " +
							"</div>");
			table.append("</td>");
			table.append("<td align=\"right\">" +
							"<img src=\"/afunms/common/images/left_top_03.jpg\" width=\"5\" height=\"29\" />" +
						 "</td>");
			table.append("</tr>");
			table.append("</table>");
			table.append("</td>");
			table.append("</tr>");
			table.append("<tr>");
			table.append("<td algin=\"left\" style=\"background-color: #FFFFFF;" +
									 "border-right-width: 1px;" +
									 "border-left-width: 1px;" +
									 "border-right-style: solid;" +
									 "border-left-style: solid;" +
									 "border-right-color: #03493c;" +
									 "border-left-color: #03493c;" +
									 "padding:0 10px;\" bgcolor=\"#ffffff\">" );
				//			"<img src=\"images/pic2.jpg\" height=\"148\" />" +
			
			table.append("<div align=\"left\" class=\"list\" id=\""+"menu"+function.getFunc_desc()+"child"+"\" title=\"菜单功能区\">");
			//-----------------------------------------------------------------------
			List<Function> menuThird_list = crft.getFunctionChild(function , menu_list);
			
			createThirdMenuTable(menuThird_list,menu_list);
			//-----------------------------------------------------------------------
			
			table.append("</div>");
			table.append("</td>");
			table.append("</tr>");
			table.append("<tr>");
			table.append("<td background=\"/afunms/common/images/left_b_02.jpg\">" );
			table.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			table.append("<tr>");
			table.append("<td align=\"left\" valign=\"bottom\">" +
							"<img src=\"/afunms/common/images/left_b_01.jpg\" width=\"5\" height=\"12\" />" +
						 "</td>");
			table.append("<td></td>");
			table.append("<td align=\"right\" valign=\"bottom\">" +
							"<img src=\"/afunms/common/images/left_b_03.jpg\" width=\"5\" height=\"12\" />" +
						 "</td>");
			table.append("</tr>");
			table.append("</table>");
			table.append("</td>");
			table.append("</tr>");
			table.append("</table>");
			table.append("</br>");
		}
		
		table.append("</div>");
		
		return null;
	}
	
	/**
	 *  拼接三级菜单
	 * @param menuThird_list
	 * @param menu_list
	 * @return
	 */
	private StringBuffer createThirdMenuTable(List<Function> menuThird_list,List<Function> menu_list){
		table.append("<ul style=\"list-style-type:none\">");
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
