/**
 * <p>Description:operate topo xml</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2006-09-25
 */

package com.afunms.topology.util;

import java.io.*;
import java.util.List;

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.*;
import com.afunms.common.util.*;

public class PXmlOperator 
{
	private final String headBytes = "<%@page contentType=\"text/html; charset=GB2312\"%>\r\n";
	private SAXBuilder builder;
	private FileInputStream fis;
	private FileOutputStream fos;	
	private XMLOutputter serializer;
	private String fullPath;
	
	protected Document doc;
	protected Element root;
	protected Element nodes;
	protected Element lines;
	protected Element assistantLines;
	public PXmlOperator()
	{
	}
	
	public void setFile(String fileName)
	{
		fullPath = ResourceCenter.getInstance().getSysPath() + "panel/xml/" + fileName;			
	}

	/**
	 * 更新所有info项和image项
	 */
	public void updateInfo(boolean isCustom)
	{		
		List list = nodes.getChildren();		
		for (int i = 0; i < list.size(); i++)
		{			
			Element eleNode = (Element) list.get(i);
			int id = Integer.valueOf(eleNode.getChildText("id")).intValue();
			com.afunms.polling.base.Node node = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByID(id);			
			if(node==null)
			{
				SysLogger.info("发现一个被删除的节点，ID=" + id);
				deleteNodeByID(String.valueOf(id));
				continue;
			}
			eleNode.getChild("alias").setText(node.getAlias());
			eleNode.getChild("ip").setText(node.getIpAddress());
//System.out.println("IP : "+node.getIpAddress()+" info : "+node.getShowMessage());			
			eleNode.getChild("info").setText(node.getShowMessage());
			if(node.getCategory()==4)
			{				   
			   if(node.isAlarm()) //报警
				  eleNode.getChild("img").setText(NodeHelper.getServerAlarmImage(((com.afunms.polling.node.Host)node).getSysOid()));
			   else
				  eleNode.getChild("img").setText(NodeHelper.getServerTopoImage(((com.afunms.polling.node.Host)node).getSysOid()));	   
			}
			else
			{	
				//SysLogger.info(node.getIpAddress()+" discoverstatus: " +node.getDiscoverstatus());
				if(node.getDiscoverstatus() > 0){
					//没有被发现的设备
					eleNode.getChild("img").setText(NodeHelper.getLostImage(node.getCategory()));
				}else{
					if(node.isAlarm()) //报警
						eleNode.getChild("img").setText(NodeHelper.getAlarmImage(node.getCategory()));   
					else
						eleNode.getChild("img").setText(NodeHelper.getTopoImage(node.getCategory()));
				}
		    }			
		}
		
		if(isCustom)
		{
			writeXml();	
			return;
		}
		//----------------更新链路(2007.2.27)-------------------
		List linkList = lines.getChildren();
		for (int i = 0; i < linkList.size(); i++)
		{			
			Element eleLine = (Element) linkList.get(i);
			int id = Integer.valueOf(eleLine.getAttributeValue("id")).intValue();
			com.afunms.polling.base.LinkRoad lr = (com.afunms.polling.base.LinkRoad)PollingEngine.getInstance().getLinkByID(id);
			//这里主要用TRAP进行告警确定
		    if(lr.isAlarm()){
		    	if(lr.getLevels()>0){
					eleLine.getChild("color").setText("yellow");
    		    } else if(lr.getLevels()>1){
    		    	eleLine.getChild("color").setText("red");
    		    }
		    } else {
		    	if(lr.getAssistant()==0)
		    	   eleLine.getChild("color").setText("green");
		    	else
		    	   eleLine.getChild("color").setText("blue");			    		
		    }
		}
		
		
		List alinkList = assistantLines.getChildren();
		for (int i = 0; i < alinkList.size(); i++)
		{			
			Element eleLine = (Element) alinkList.get(i);
			int id = Integer.valueOf(eleLine.getAttributeValue("id")).intValue();
			com.afunms.polling.base.LinkRoad lr = (com.afunms.polling.base.LinkRoad)PollingEngine.getInstance().getLinkByID(id);
		    if(lr.isAlarm()){
		    	if(lr.getLevels()>0){
					eleLine.getChild("color").setText("yellow");
    		    } else if(lr.getLevels()>1){
    		    	eleLine.getChild("color").setText("red");
    		    }
		    } else {
		    	if(lr.getAssistant()==0)
		    	   eleLine.getChild("color").setText("green");
		    	else
		    	   eleLine.getChild("color").setText("blue");			    		
		    }
		}				
		writeXml();		
	}
		
	/**
	 * 保存xml文件(用于拓扑图上的"保存"按钮)
	 */
	public void saveImage(String content)
	{
		FileOutputStream fos = null;
	    OutputStreamWriter osw = null;	   
	    try
	    {
	        fos = new FileOutputStream(fullPath);	        
	        osw = new OutputStreamWriter(fos, "GB2312");	        
	        osw.write("<%@page contentType=\"text/html; charset=GB2312\"%>\r\n" + content);	        
	    }
	    catch(Exception e)
	    {   
	        SysLogger.error("XmlOperator.imageToXml()",e);
	    }
	    finally
	    {
	       try
	       {
	           osw.close();
	       }
	       catch(Exception ee)
	       {}
	    }
	}
	
	/**
	 * 保存文件
	 */
	public void writeXml()
	{		
	    try
	    {
		    Format format = Format.getCompactFormat();
		    format.setEncoding("GB2312");
		    format.setIndent("	");
		    serializer = new XMLOutputter(format);
		    fos = new FileOutputStream(fullPath);
		    fos.write(headBytes.getBytes());
		    serializer.output(doc, fos);
		    fos.close();
		}
	    catch(Exception e)
		{
	    	SysLogger.error("Error in XmlOperator.close()",e);
		}		
	}
	
	/**
	 * 准备更新一个新的xml
	 */
	public void init4updateXml()
	{
 	   try
	   {				
		   fis = new FileInputStream(fullPath);		
		   fis.skip(headBytes.getBytes().length);	
		   builder = new SAXBuilder();
		   doc = builder.build(fis);
		   
		   root = doc.getRootElement();
		   nodes = root.getChild("nodes");		
		   lines = root.getChild("lines");	
		   assistantLines = root.getChild("assistant_lines");
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("Error in XmlOperator.init4updateXml(),file=" + fullPath);
	   }   
	}
	 
	/**
	 * 准备创建一个新的xml
	 */
	public void init4createXml()
	{
		root = new Element("root");
		nodes = new Element("nodes");
		lines = new Element("lines");
		assistantLines = new Element("assistant_lines");
	}
	
	/**
	 * 创建一个新的xml
	 */
	public void createXml()
	{
		root.addContent(nodes);
		root.addContent(lines);
		root.addContent(assistantLines);
		doc = new Document(root);
		writeXml();
	}
	
	/**
	 * 删除一个xml
	 */
	public void deleteXml()
	{
		try
		{
			File delFile = new File(fullPath);
			delFile.delete();
		}
		catch (Exception e)
		{
			SysLogger.error("删除文件操作出错" + fullPath,e);
		}
	}
	
	/**
	 * 增加一个新的节点(用于发现之后，或者手动增加一个节点)
	 */
	public void addNode(String nodeId,int categroy,String image,String ip,String alias,String x,String y)
	{
		Element eleNode = new Element("node");
		Element eleId = new Element("id");		
		Element eleImg = new Element("img");
		Element eleX = new Element("x");
		Element eleY = new Element("y");
		Element eleIp = new Element("ip");
		Element eleAlias = new Element("alias");
		Element eleInfo = new Element("info");
		Element eleMenu = new Element("menu");
					
		eleId.setText(nodeId);    			
		eleId.setAttribute("category",NodeHelper.getNodeEnCategory(categroy));
		if(image==null)
		   eleImg.setText(NodeHelper.getTopoImage(categroy));
		else
		   eleImg.setText(image);							
		eleX.setText(x);
		eleY.setText(y);
		SysLogger.info("id: "+nodeId+"  ip---"+ip+" 类型:"+categroy+" 图片:"+image);
		eleIp.setText(ip);		
		eleAlias.setText(alias); 
		eleInfo.setText("设备标签:" + alias + "<br>IP地址:" + ip); 		
		eleMenu.setText(NodeHelper.getMenuItem(nodeId,ip)); 
		eleNode.addContent(eleId);
		eleNode.addContent(eleImg);
		eleNode.addContent(eleX);
		eleNode.addContent(eleY);
		eleNode.addContent(eleIp);
		eleNode.addContent(eleAlias);
		eleNode.addContent(eleInfo);
		eleNode.addContent(eleMenu);
		nodes.addContent(eleNode);	
	}
	
	public void addNode(com.afunms.discovery.Host host)
	{
		String img = null;
		if(host.getCategory()==4)
		   img = NodeHelper.getServerTopoImage(host.getSysOid());
 		else
 		   img = NodeHelper.getTopoImage(host.getCategory());
        
		addNode(String.valueOf(host.getId()),host.getCategory(),img,host.getIpAddress(),host.getAlias(),"30","30");
	}
	
	/**
	 * 增加一条链路
	 */	
	public void addLine(String lineId,String startId,String endId)
	{
  	    Element line = new Element("line");
	    Element a = new Element("a");
	    Element b = new Element("b");
	    Element color = new Element("color");
	    Element dash = new Element("dash");
	    
	    line.setAttribute("id",lineId);
	    a.setText(startId);
	    b.setText(endId);
	    color.setText("green");
	    dash.setText("Solid");
		
	    line.addContent(a);
	    line.addContent(b);
	    line.addContent(color);
	    line.addContent(dash);
	    lines.addContent(line);	
	}

	/**
	 * 增加一条辅助链路
	 */	
	public void addAssistantLine(String lineId,String startId,String endId)
	{
  	    Element line = new Element("assistant_line");
	    Element a = new Element("a");
	    Element b = new Element("b");
	    Element color = new Element("color");
	    Element dash = new Element("dash");
	    
	    line.setAttribute("id",lineId);
	    a.setText(startId);
	    b.setText(endId);
	    color.setText("blue"); //辅助链路用蓝色表示
	    dash.setText("Solid");
		
	    line.addContent(a);
	    line.addContent(b);
	    line.addContent(color);
	    line.addContent(dash);
	    assistantLines.addContent(line);	
	}
	
	/**
	 * 按xmlid删除一个结点
	 */
	public void deleteNodeByID(String nodeId)
	{
		List eleNodes = nodes.getChildren();
		int len = eleNodes.size() - 1;
		for (int i = len; i >=0; i--) 
		{
			Element node = (Element) eleNodes.get(i);
			if(node.getChildText("id").equals(nodeId)) 
			{
				node.getParentElement().removeContent(node);
				deleteLineByNodeID(nodeId); //删除结点,必然删除与它相关的连线
			    break;
			}     
		}
	}
	
	/**
	 * delete line whose startid or endid equals "nodeId"
	 */
	public void deleteLineByNodeID(String nodeId)
	{
		List eleLines = lines.getChildren();
		int len = eleLines.size() - 1;
		for(int i=len; i>=0; i--) //这里只能用降序，升序可能出错
		{			
			Element line = (Element)eleLines.get(i);
			if(line.getChildText("a").equals(nodeId))		
			   line.getParentElement().removeContent(line);			
			else if(line.getChildText("b").equals(nodeId))
			   line.getParentElement().removeContent(line);				
		}				
	}
	
	/**
	 * delete line whose id equals "id"(line id)
	 */
	public void deleteLineByID(String id)
	{
		List eleLines = lines.getChildren();
		int len = eleLines.size() - 1;
		for(int i=len; i>=0; i--) //这里只能用降序，升序可能出错
		{			
			Element line = (Element)eleLines.get(i);
			if(line.getAttributeValue("id").equals(id))		
			   line.getParentElement().removeContent(line);			
		}				
	}
	
	public boolean isNodeExist(String nodeId)
	{
	     boolean result = false;
		 List nodeList = nodes.getChildren();
	     for(int i=0;i<nodeList.size();i++)
	     {
	    	 Element ele = (Element)nodeList.get(i);
	    	 if(ele.getChildText("id").equals(nodeId))
	    	 {
	    		 result = true;
	    		 break;
	    	 }
	     }
	     return result;
	}
}
