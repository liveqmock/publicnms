/**
 * <p>Description:operate topo xml</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2006-09-25
 */

package com.afunms.topology.util;

import java.io.*;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.*;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.common.util.*;
import com.afunms.config.dao.*;
import com.afunms.config.model.*;

public class PanelXmlOperator 
{
	private final String headBytes = "<%@page contentType=\"text/html; charset=GB2312\"%>\r\n";
	private SAXBuilder builder;
	private FileInputStream fis;
	private FileOutputStream fos;	
	private XMLOutputter serializer;
	private String fullPath;
	private String ipaddress;
	private String oid;
	
	/**
	 * @author nielin
	 * at 2010-01-08
	 * Use oid and imageType to get name of panelmodel 
	 */
	private String imageType;  
	
	protected Document doc;
	protected Element root;
	protected Element nodes;
	protected Document _doc;
	protected Element _root;
	protected Element _nodes;
	private SAXBuilder _builder;
	private FileInputStream _fis;
	private FileOutputStream _fos;

	public PanelXmlOperator()
	{
	}
	
	public void setFile(String fileName,int flag)
	{
		if(flag == 1){
			fullPath = ResourceCenter.getInstance().getSysPath() + "panel/model/" + fileName;
		}else{
			fullPath = ResourceCenter.getInstance().getSysPath() + "panel/xml/" + fileName;
		}
					
	}
	public void setIpaddress(String ipaddress)
	{
		this.ipaddress = ipaddress;			
	}
	public String getIpaddress(){
		return this.ipaddress;
	}
	public void setOid(String oid)
	{
		this.oid = oid;			
	}
	public String getOid(){
		return this.oid;
	}
	
	/**
	 * @return the imageType 2010-01-08
	 */
	public String getImageType() {
		return imageType;
	}

	/**
	 * @param imageType the imageType to set 2010-01-08
	 */
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	/**
	 * 更新所有info项和image项
	 */
	public void updateInfo(boolean isCustom)
	{		
		I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
		List list = nodes.getChildren();		
		for (int i = 0; i < list.size(); i++)
		{			
			Element eleNode = (Element) list.get(i);
			int id = Integer.valueOf(eleNode.getChildText("id")).intValue();
			int direction = Integer.valueOf(eleNode.getChildText("direction")).intValue();
			//Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(ipaddress);
            
	        Vector vector = new Vector();
	        String[] netInterfaceItem={"index","ifDescr","ifSpeed","ifOperStatus","OutBandwidthUtilHdx","InBandwidthUtilHdx"};
	        try{
	        	vector = hostlastmanager.getInterface_share(ipaddress,netInterfaceItem,"index","",""); 
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
	        SysLogger.info("######################################");
	        SysLogger.info("######### "+ipaddress+"   ##############");
	        SysLogger.info("######################################");
			if(vector != null && vector.size()>0){
				PortconfigDao dao = new PortconfigDao();
				try{
				for(int m=0;m<vector.size();m++){
					String[] strs = (String[])vector.get(m);
					String ifname = strs[1];
	                String index = strs[0];
	                String OutBandwidthUtilHdx =strs[4];
	                String InBandwidthUtilHdx =strs[5];
	                String portuse = "";
	                Portconfig portconfig = null;
	                try{
	                	portconfig = dao.getPanelByipandindex(ipaddress, index);
	                }catch(Exception e){
	                	
	                }
	                if(portconfig != null && portconfig.getLinkuse() != null && portconfig.getLinkuse().trim().length()>0){
	                	portuse = portconfig.getLinkuse();
	                }
	                if(Integer.parseInt(index) == id){
	                	//当前端口
	                	if(direction == 1){
	                		//向上方向的端口
		                	if(strs[3].equalsIgnoreCase("up")){
		                		//端口启动
		                		SysLogger.info(PanelNodeHelper.getUpUpImage(1));
		                		eleNode.getChild("img").setText(PanelNodeHelper.getUpUpImage(1));
		                	}else{
		                		//端口未启动
		                		SysLogger.info(PanelNodeHelper.getUpDownImage(1));
		                		eleNode.getChild("img").setText(PanelNodeHelper.getUpDownImage(1));
		                	}	                		
	                		
	                	}else{
	                		//向下方向的端口
		                	if(strs[3].equalsIgnoreCase("up")){
		                		//端口启动
		                		SysLogger.info(PanelNodeHelper.getDownUpImage(1));
		                		eleNode.getChild("img").setText(PanelNodeHelper.getDownUpImage(1));
		                	}else{
		                		//端口未启动
		                		SysLogger.info(PanelNodeHelper.getDownDownImage(1));
		                		eleNode.getChild("img").setText(PanelNodeHelper.getDownDownImage(1));
		                	}
	                	}

	        			eleNode.getChild("alias").setText(ifname);
	        			eleNode.getChild("ip").setText(ipaddress);	

	        			StringBuffer msg = new StringBuffer(200);
	        			msg.append("<font color='green'>索引:");		
	        			msg.append(id);
	        			msg.append("</font><br>");
	        			
	        			msg.append("描述:");
	        			msg.append(ifname);
	        			msg.append("<br>");
	        			
	        			msg.append("端口应用:");
	        			msg.append(portuse);
	        			msg.append("<br>");
	        			
	        			msg.append("入口流速:");
	        			msg.append(InBandwidthUtilHdx);
	        			msg.append("<br>");
	        			
	        			msg.append("出口流速:");
	        			msg.append(OutBandwidthUtilHdx);
	        			msg.append("<br>");
	        			
	        			SysLogger.info(msg.toString());
	        			eleNode.getChild("info").setText(msg.toString());
	        			eleNode.getChild("menu").setText(PanelNodeHelper.getMenuItem(index,ipaddress));
	        			
	                }
				}
				}catch(Exception e){
					
				}finally{
					dao.close();
				}
				
			}
			//nodes.addContent(i, eleNode);
		}
		
		if(isCustom)
		{
			writeXml();	
			return;
		}			
		writeXml();		
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
	    	e.printStackTrace();
	    	SysLogger.error("Error in XmlOperator.close()",e);
		}		
	}
	
	/**
	 * 创建面板xml
	 */
	/* nielin delete at 2010-01-08
	public void writeXml(int flag)
	{	
		int done = 1;
	    try
	    {
	    	I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(ipaddress);
            
	        Vector vector = new Vector();
	        String[] netInterfaceItem={"index","ifDescr","ifSpeed","ifAdminStatus","ifOperStatus","OutBandwidthUtilHdx","InBandwidthUtilHdx"};
	        try{
	        	vector = hostlastmanager.getInterface_share(ipaddress,netInterfaceItem,"index","",""); 
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
	        try{
			   _fis = new FileInputStream(ResourceCenter.getInstance().getSysPath() + "panel\\model\\"+oid+".jsp");	
	        }catch(Exception e){
	        	return;
	        }
			   _fis.skip(headBytes.getBytes().length);
			   _builder = new SAXBuilder();
			   _doc = _builder.build(_fis);  
			   _root = _doc.getRootElement();
			   _nodes = _root.getChild("nodes");
			   
				List list = _nodes.getChildren();		
				for (int i = 0; i < list.size(); i++)
				{			
					Element eleNode = (Element) list.get(i);
					if(eleNode.getChildText("index") == null)break;
					int index = Integer.valueOf(eleNode.getChildText("index")).intValue();
					String x = eleNode.getChildText("x");
					String y = eleNode.getChildText("y");
					String img = eleNode.getChildText("img");
					String direction = eleNode.getChildText("direction");
					String ifname = "";
					String OutBandwidthUtilHdx="0";
					String InBandwidthUtilHdx ="0";
					if(vector != null && vector.size()>0){
						for(int m=0;m<vector.size();m++){
							String[] strs = (String[])vector.get(m);
							String _ifname = strs[1];
			                String _index = strs[0];
			                String opstatus = strs[4];
			                OutBandwidthUtilHdx =strs[5];
			                InBandwidthUtilHdx =strs[6];

			                //SysLogger.info(ipaddress+"====ifOperStatus====="+strs[4]);
			                if(Integer.parseInt(_index) == index){			                	
			                	ifname = _ifname;
				                if("down".equalsIgnoreCase(opstatus)){
				                	//DOWN
				                	if("1".equalsIgnoreCase(direction)){
				                		//向上的端口
				                		img="image/up_down_gray.gif";
				                	}else{
				                		//向下的端口
				                		img="image/down_down_gray.gif";
				                	}
				                }else{
				                	//UP
				                	if("1".equalsIgnoreCase(direction)){
				                		//向上的端口
				                		img="image/up_up_green.gif";
				                	}else{
				                		//向下的端口
				                		img="image/down_up_green.gif";
				                	}
				                }			                	
			                	break;
			                }
						}
					}
					addNode(index+"",InBandwidthUtilHdx,OutBandwidthUtilHdx,img,ipaddress,ifname,x,y);
				}
		    Format format = Format.getCompactFormat();
		    format.setEncoding("GB2312");
		    format.setIndent("	");
		    serializer = new XMLOutputter(format);
		    //SysLogger.info("path==="+fullPath);
		    fos = new FileOutputStream(fullPath);
		    fos.write(headBytes.getBytes());
		    serializer.output(doc, fos);
		    fos.close();
		}
	    catch(Exception e)
		{
	    	done = 0;
	    	e.printStackTrace();
	    	SysLogger.error("Error in XmlOperator.close()",e);
		}
	    if(done == 1){
	    	//设置成功,将该信息插入数据库
	    	IpaddressPanelDao dao = new IpaddressPanelDao();
	    	IpaddressPanel panel = dao.loadIpaddressPanel(ipaddress);
	    	if(panel == null){
	    		//写数据库
	    		panel = new IpaddressPanel();
	    		panel.setIpaddress(ipaddress);
	    		panel.setStatus("1");
	    		try{
	    			dao = new IpaddressPanelDao();
	    			dao.save(panel);
	    		}catch(Exception e){
	    			e.printStackTrace();
	    		}
	    	}
	    }
	}
	*/
	
	/**
	 * @author nielin 
	 * modify at 2010-01-08
	 * 
	 */
	public int writeXml(int flag)
	{	
		int done = 0;
		//需要做分布式判断
		String runmodel = PollingEngine.getCollectwebflag(); 
	    try
	    {
	    	I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(ipaddress);
            
	        Vector vector = new Vector();
	        String[] netInterfaceItem={"index","ifDescr","ifSpeed","ifAdminStatus","ifOperStatus","OutBandwidthUtilHdx","InBandwidthUtilHdx"};
	        try{
	        	if("0".equals(runmodel)){
			       	//采集与访问是集成模式	
	        		vector = hostlastmanager.getInterface_share(ipaddress,netInterfaceItem,"index","",""); 
	        	}else{
					//采集与访问是分离模式
	        		vector = hostlastmanager.getInterface(ipaddress,netInterfaceItem,"index","",""); 
	        	}
	        }catch(Exception e){
	        	e.printStackTrace();
	        	return done;
	        }
	        try{
			   _fis = new FileInputStream(ResourceCenter.getInstance().getSysPath() + "panel/model/"+oid+"_"+imageType+".jsp");	
	        }catch(Exception e){
	        	e.printStackTrace();
	        	_fis.close();
	        	return done;
	        }
			   _fis.skip(headBytes.getBytes().length);
			   _builder = new SAXBuilder();
			   _doc = _builder.build(_fis);  
			   _root = _doc.getRootElement();
			   _nodes = _root.getChild("nodes");
			   
				List list = _nodes.getChildren();
				
				PortconfigDao dao = new PortconfigDao();
				
				
				try{
					
				
				for (int i = 0; i < list.size(); i++)
				{			
					Element eleNode = (Element) list.get(i);
					if(eleNode.getChildText("index") == null)break;
					int index = Integer.valueOf(eleNode.getChildText("index")).intValue();
					String x = eleNode.getChildText("x");
					String y = eleNode.getChildText("y");
					String img = eleNode.getChildText("img");
					String direction = eleNode.getChildText("direction");
					String ifname = "";
					String OutBandwidthUtilHdx="0";
					String InBandwidthUtilHdx ="0";
					String portuse = "";
					if(vector != null && vector.size()>0){
						for(int m=0;m<vector.size();m++){
							String[] strs = (String[])vector.get(m);
							String _ifname = strs[1];
			                String _index = strs[0];
			                String opstatus = strs[4];
			                OutBandwidthUtilHdx =strs[5];
			                InBandwidthUtilHdx =strs[6];
			                
			                
			                Portconfig portconfig = null;
			                try{
			                	portconfig = dao.getPanelByipandindex(ipaddress, index+"");
			                }catch(Exception e){
			                	
			                }
			                if(portconfig != null && portconfig.getLinkuse() != null && portconfig.getLinkuse().trim().length()>0){
			                	portuse = portconfig.getLinkuse();
			                }

			                //SysLogger.info(ipaddress+"====ifOperStatus====="+strs[4]);
			                if(Integer.parseInt(_index) == index){			                	
			                	ifname = _ifname;
				                if("down".equalsIgnoreCase(opstatus)){
				                	//DOWN
				                	if("1".equalsIgnoreCase(direction)){
				                		//向上的端口
				                		img="image/up_down_gray.gif";
				                	}else{
				                		//向下的端口
				                		img="image/down_down_gray.gif";
				                	}
				                }else{
				                	//UP
				                	if("1".equalsIgnoreCase(direction)){
				                		//向上的端口
				                		img="image/up_up_green.gif";
				                	}else{
				                		//向下的端口
				                		img="image/down_up_green.gif";
				                	}
				                }			                	
			                	break;
			                }
						}
					}
					addNode(index+"",InBandwidthUtilHdx,OutBandwidthUtilHdx,img,ipaddress,ifname,portuse,x,y);
				}
				
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					dao.close();
				}
				
		    Format format = Format.getCompactFormat();
		    format.setEncoding("GB2312");
		    format.setIndent("	");
		    serializer = new XMLOutputter(format);
		    //SysLogger.info("path==="+fullPath);
		    fos = new FileOutputStream(fullPath);
		    fos.write(headBytes.getBytes());
		    serializer.output(doc, fos);
		    fos.close();
		    done = 1;
		}
	    catch(Exception e)
		{
	    	done = 0;
	    	e.printStackTrace();
	    	SysLogger.error("Error in XmlOperator.close()",e);
		}
	    return done;
	}
	
	/**
	 * 保存文件
	 */
	public int writeModelXml(Hashtable has,Hashtable hat)
	{	
		int done = 1;
	    try
	    {
			   for (int i = 0; i < has.size(); i++) { 
				   String [] str1 =((String)has.get(i)).split(",");
				   System.out.println(str1.length);
		           // 创建节点 node; 
	               String index = str1[2];
	               String direction = (String)hat.get(i);
	               index = index.replaceAll("index", "");
	               addNodes(index,"image/up_green.gif",str1[0],str1[1],direction);
			   }
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
	    	done = 0;
	    	e.printStackTrace();
	    	SysLogger.error("Error in XmlOperator.close()",e);
		}
	    return done;
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
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
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
	}
	
	/**
	 * 创建一个新的xml
	 */
	public void createXml()
	{
		root.addContent(nodes);
		doc = new Document(root);
		writeXml();
	}
	
	/**
	 * 创建一个新的xml
	 */
	public int createXml(int flag)
	{
		root.addContent(nodes);
		doc = new Document(root);
		return writeXml(flag);
	}
	
	/**
	 * 创建一个新的xml
	 */
	public int createModelXml(Hashtable has,Hashtable hat)
	{
		root.addContent(nodes);
		doc = new Document(root);
		return writeModelXml(has,hat);
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
	public void addNode(String index,String InBandwidthUtilHdx,String OutBandwidthUtilHdx,String image,String ip,String alias,String portuse,String x,String y)
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
					
		eleId.setText(index);    			
		if(image==null)
		   eleImg.setText(PanelNodeHelper.getMenuItem(index,ipaddress));
		else
		   eleImg.setText(image);							
		eleX.setText(x);
		eleY.setText(y);	
		eleIp.setText(ip);
		eleAlias.setText(alias); 
		StringBuffer msg = new StringBuffer(200);
		msg.append("<font color='green'>索引:");		
		msg.append(index);
		msg.append("</font><br>");
		msg.append("描述:");
		msg.append(alias);
		msg.append("<br>");
		msg.append("应用:");
		msg.append(portuse);
		msg.append("<br>");
		msg.append("入口流速:");
		msg.append(InBandwidthUtilHdx);
		msg.append("<br>");
		
		msg.append("出口流速:");
		msg.append(OutBandwidthUtilHdx);
		msg.append("<br>");
		
		eleInfo.setText(msg.toString()); 		
		eleMenu.setText(PanelNodeHelper.getMenuItem(index,ip)); 
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
	
	/**
	 * 增加一个新的节点(用于发现之后，或者手动增加一个节点)
	 */
	public void addNodes(String index,String image,String ip,String alias,String x,String y)
	{
		Element eleNode = new Element("node");
		Element eleId = new Element("id");		
		Element eleImg = new Element("img");
		Element eleX = new Element("x");
		Element eleY = new Element("y");
		Element eleAlias = new Element("alias");
		Element eleInfo = new Element("info");
		Element eleMenu = new Element("menu");
					
		eleId.setText(index);    			
		eleImg.setText(image);							
		eleX.setText(x);
		eleY.setText(y);	
		eleAlias.setText(alias); 
		StringBuffer msg = new StringBuffer(200);
		msg.append("<font color='green'>索引:");		
		msg.append(index);
		msg.append("</font><br>");
		msg.append("描述:");
		msg.append(alias);
		msg.append("<br>");
		eleInfo.setText(msg.toString()); 		
		eleMenu.setText(PanelNodeHelper.getMenuItem(index,ip)); 
		eleNode.addContent(eleId);
		eleNode.addContent(eleImg);
		eleNode.addContent(eleX);
		eleNode.addContent(eleY);
		eleNode.addContent(eleAlias);
		eleNode.addContent(eleInfo);
		eleNode.addContent(eleMenu);
		nodes.addContent(eleNode);	
	}
	
	/**
	 * 增加一个新的节点(用于发现之后，或者手动增加一个节点)
	 */
	public void addNodes(String index,String image,String x,String y,String direction)
	{
		Element eleNode = new Element("node");
		Element eleId = new Element("index");		
		Element eleImg = new Element("img");
		Element eleDir = new Element("direction");
		Element eleX = new Element("x");
		Element eleY = new Element("y");
					
		eleId.setText(index);    			
		eleImg.setText(image);							
		eleX.setText(x);
		eleY.setText(y);
		eleDir.setText(direction);
		eleNode.addContent(eleId);
		eleNode.addContent(eleImg);
		eleNode.addContent(eleDir);
		eleNode.addContent(eleX);
		eleNode.addContent(eleY);
		nodes.addContent(eleNode);	
	}
	
	public void addNode(com.afunms.discovery.Host host)
	{
		String img = null;
		if(host.getCategory()==4)
		   img = NodeHelper.getServerTopoImage(host.getSysOid());
 		else
 		   img = NodeHelper.getTopoImage(host.getCategory());
        
		//addNode(String.valueOf(host.getId()),host.getCategory(),img,host.getIpAddress(),host.getAlias(),"30","30");
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
			    break;
			}     
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
