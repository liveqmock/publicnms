/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.manage;

import java.util.*;

import com.afunms.common.base.*;
import com.afunms.topology.model.*;
import com.afunms.topology.dao.*;
import com.afunms.topology.util.*;
import com.afunms.polling.base.Node;
import com.afunms.polling.*;
import com.afunms.common.util.SessionConstant;
import com.afunms.polling.base.NodeCategory;

public class CustomViewManager extends BaseManager implements ManagerInterface
{	
	/**
	 * 预编辑xml中的结点
	 */
	private String readyEditNodes()
	{
		DaoInterface dao = new CustomXmlDao();
		CustomXml vo = (CustomXml)dao.findByID(getParaValue("id"));	
		
		List categorys = NodeHelper.getAllCategorys();
		List nodeList = PollingEngine.getInstance().getNodeList();		
		List selectTable = new ArrayList();
		for(int i=0;i<categorys.size();i++)
		{
			NodeCategory category = (NodeCategory)categorys.get(i);
			for(int j=0;j<nodeList.size();j++)
		    {
			    Node node = (Node)nodeList.get(j);	
			    if(node.getCategory() != category.getId()) continue;
			   
			    XmlInfo tableItem = new XmlInfo(); //下拉框中的一行
			    tableItem.setId(String.valueOf(node.getId()));
			    tableItem.setInfo(category.getCnName() + "[" + node.getIpAddress() + "_" + node.getAlias() + "]");
			    selectTable.add(tableItem);
		    }
		}
		request.setAttribute("vo",vo);
		request.setAttribute("table",selectTable);
		return "/topology/view/xml/edit_node.jsp";
	}
	
	/**
	 * 编辑xml中的结点
	 */	
	private String editNodes()
	{
		String[] values = getParaArrayValue("viewnodes");						
		CustomXmlOperator xmlOpr = new CustomXmlOperator();		
		xmlOpr.setFile(getParaValue("xml"));		
		xmlOpr.init4editNodes();
		
		int index = 0;
		for(int i=0;i<values.length;i++) 
		{
			if(!xmlOpr.isIdExist(values[i])) //no exist 
				xmlOpr.addNode(Integer.parseInt(values[i]),++index);				
		}
		xmlOpr.deleteNodes();
		xmlOpr.writeXml();
						
	    return "/customxml.do?action=list";
	}
		
	private String readyEditLines()
	{
		DaoInterface dao = new CustomXmlDao();
		CustomXml vo = (CustomXml)dao.findByID(getParaValue("id"));		       
		request.setAttribute("vo",vo);
		
		return "/topology/view/xml/edit_line.jsp";
	}

	private String editLines()
	{		
		CustomXmlOperator xmlOpr = new CustomXmlOperator();		
		xmlOpr.setFile(getParaValue("xml"));		
		xmlOpr.init4editLines();		
		
		String[] values = getParaArrayValue("viewlines");		
		for(int i=0;i<values.length;i++) //增加网络设备和服务器
		{
			int loc = values[i].indexOf("-");
			xmlOpr.addLine(values[i].substring(0,loc), values[i].substring(loc+1));
		}
		xmlOpr.writeXml();
		
		return "/customxml.do?action=list";
	}
	      
    //在视图上创建链路
	private String addLines()//yangjun add 
	{		
		CustomXmlOperator xmlOpr = new CustomXmlOperator();		
		String xml = getParaValue("xml");
		xmlOpr.setFile(xml);		
		xmlOpr.init4updateXml();		
		String id1 = getParaValue("id1");
		String id2 = getParaValue("id2");
	    xmlOpr.addLine(id1, id2);
		xmlOpr.writeXml();
		
		return "/topology/view/change.jsp?customview="+xml;
	}
	//删除视图上的链路
	private String deleteLines()
    {
        String sid = getParaValue("startId");
        String eid = getParaValue("endId");
        String xml = getParaValue("xml");
 	    //更新xml
        CustomXmlOperator opr = new CustomXmlOperator();
        opr.setFile(xml);
        opr.init4updateXml();
        opr.deleteLine(sid, eid);
        opr.writeXml();
        
        return "/topology/network/index.jsp";
    }
	
	private String save()
	{
		String fileName = (String)session.getAttribute(SessionConstant.CURRENT_CUSTOM_VIEW);
		String xmlString = request.getParameter("hidXml");//从showMap.jsp传入的数据信息字符串		
		
		xmlString = xmlString.replace("<?xml version=\"1.0\"?>", "<?xml version=\"1.0\" encoding=\"GB2312\"?>");		
		XmlOperator xmlOpr = new XmlOperator();		
		xmlOpr.setFile(fileName);		
		xmlOpr.saveImage(xmlString);	
		return "/topology/view/save.jsp";   
	}

    public String execute(String action)
    {  
	    if(action.equals("ready_edit_nodes"))
		   return readyEditNodes();
	    if(action.equals("edit_nodes"))
	       return editNodes();     	   
	    if(action.equals("ready_edit_lines"))
	       return readyEditLines();     
	    if(action.equals("addLines"))
		       return addLines();
		if(action.equals("deleteLines"))
		       return deleteLines();
	    if(action.equals("edit_lines"))
		   return editLines();   
        if(action.equals("save"))
           return save();   
        setErrorCode(ErrorMessage.ACTION_NO_FOUND);
        return null;
    }
}
