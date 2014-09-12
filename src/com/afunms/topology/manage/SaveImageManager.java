/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.manage;

import com.afunms.common.base.*;
import com.afunms.polling.*;
import com.afunms.polling.node.*;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.util.*;

import java.awt.Color; 
import java.awt.Font; 
import java.awt.Graphics; 
import java.awt.image.BufferedImage; 
import java.io.OutputStream; 
import java.util.Random; 


/**
 * 增加一个节点，要在server.jsp增加一个节点;
 * 删除或更新一个节点信息，在这则不直接操作server.jsp,而是让任务updateXml来更新xml.
 */
public class SaveImageManager extends BaseManager implements ManagerInterface
{
	private static String charsLong = "23456789abcdefghjklmnpqrstuvwxyzABCDEFGHIJKLMNPQRSTUVWXYZ"; 

	private static String charsShort = "0123456789"; 



	private static String chars = charsLong; 

	private void saveImage(){
	
		
		int width = 70, height = 20; 
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); //网管网www_bitscn_com 

		Graphics g = image.getGraphics(); 


		Random random = new Random(); 

		int charsLength = chars.length(); 
		g.setColor(getRandColor(200, 250)); 
		g.fillRect(0, 0, width, height); 



		g.setFont(new Font("Times New Roman", Font.ITALIC, height)); 

		g.setColor(getRandColor(160, 200)); 
		for (int i = 0; i < 35; i++) { 
		int x = random.nextInt(width); 
		int y = random.nextInt(height); 
		int xl = random.nextInt(12); 
		int yl = random.nextInt(12); 
		g.drawLine(x, y, x + xl, y + yl); 
		} 

		StringBuilder sRand = new StringBuilder(); 
		String[] fontNames = { "Times New Roman", "Arial", "Book antiqua", "" }; 

		for (int i = 0; i < 4; i++) { 
			g.setFont(new Font(fontNames[random.nextInt(3)], Font.ITALIC, height)); 
			char rand = chars.charAt(random.nextInt(charsLength)); 
			sRand.append(rand); 




			g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110))); 
			g.drawString(String.valueOf(rand), 16 * i + random.nextInt(6) + 3, height - random.nextInt(4)); 
		} 




		g.setColor(getRandColor(160, 200)); 
		for (int i = 0; i < 30; i++) { 
			int x = random.nextInt(width); 
			int y = random.nextInt(height); 
			int xl = random.nextInt(width); 
			int yl = random.nextInt(width); 
			g.drawLine(x, y, x + xl, y + yl); 
		} 

		
		
	}
	
	private static Color getRandColor(int fc, int bc) { 
		Random random = new Random(); 
		if (fc > 255) 
		fc = 255; 
		if (bc > 255) 
		bc = 255; 
		int r = fc + random.nextInt(bc - fc); 
		int g = fc + random.nextInt(bc - fc); 
		int b = fc + random.nextInt(bc - fc); 
		return new Color(r, g, b); 
		} 

	private String list()
	{
		HostNodeDao dao = new HostNodeDao();
		request.setAttribute("list",dao.loadNetwork(0));	     
	    return "/topology/network/list.jsp";
	}
	
    private String read()
    {
	    DaoInterface dao = new HostNodeDao();
	    setTarget("/topology/network/read.jsp");
        return readyEdit(dao);
    }
    
	private String readyEdit()
	{
	    DaoInterface dao = new HostNodeDao();
	    setTarget("/topology/network/edit.jsp");
        return readyEdit(dao);
	}
	
	private String update()
	{         	  
		HostNode vo = new HostNode();
        vo.setId(getParaIntValue("id"));
        vo.setAlias(getParaValue("alias"));
 	    
        //更新内存
 	    Host host = (Host)PollingEngine.getInstance().getNodeByID(vo.getId()); 	   
 	    host.setAlias(vo.getAlias());
 	     	    
        //更新数据库
 	    DaoInterface dao = new HostNodeDao(); 	    
	    setTarget("/network.do?action=list");
        return update(dao,vo);
    } 
	
	private String refreshsysname()
	{         	  
		HostNodeDao dao = new HostNodeDao();
		String sysName = "";
		sysName = dao.refreshSysName(getParaIntValue("id"));
 	    
        //更新内存
 	    Host host = (Host)PollingEngine.getInstance().getNodeByID(getParaIntValue("id")); 	   
 	    if(host != null){
 	    	host.setSysName(sysName);
 	    	host.setAlias(sysName);
 	    }

 	   return "/network.do?action=list";
    }
	
    private String delete()
    {
        String id = getParaValue("radio"); 
        
        PollingEngine.getInstance().deleteNodeByID(Integer.parseInt(id));        
        HostNodeDao dao = new HostNodeDao();
        dao.delete(id);       
        return "/network.do?action=list";
    }

    private String add() 
    {  	 	  
	    String ipAddress = getParaValue("ip_address");
	    String alias = getParaValue("alias");
	    String community = getParaValue("community");
	    String writecommunity = getParaValue("writecommunity");
	    int type = getParaIntValue("type");
	    
	    TopoHelper helper = new TopoHelper(); //包括更新数据库和更新内存
	    int addResult = helper.addHost(ipAddress,alias,community,writecommunity,type); //加入一台服务器
	    if(addResult==0)
	    {	  
	        setErrorCode(ErrorMessage.ADD_HOST_FAILURE);
	        return null;      
	    }   
	    if(addResult==-1)
	    {	  
	        setErrorCode(ErrorMessage.IP_ADDRESS_EXIST);
	        return null;      
	    }   	    
	    if(addResult==-2)
	    {	  
	        setErrorCode(ErrorMessage.PING_FAILURE);
	        return null;      
	    }   	    
	    if(addResult==-3)
	    {	  
	        setErrorCode(ErrorMessage.SNMP_FAILURE);
	        return null;      
	    }      
	    
 	    //2.更新xml
	    XmlOperator opr = new XmlOperator();
	    opr.setFile("network.jsp");
	    opr.init4updateXml();
	    opr.addNode(helper.getHost());   
        opr.writeXml();
        
        return "/network.do?action=list";
    }  

    private String find()
    {
	   String key = getParaValue("key");
	   String value = getParaValue("value");	 
	   HostNodeDao dao = new HostNodeDao();
       request.setAttribute("list",dao.findByCondition(key,value));
     
       return "/topology/network/find.jsp";
   }

   private String save()
   {
	   String xmlString = request.getParameter("hidXml");	
	   String vlanString = request.getParameter("vlan");	
	   xmlString = xmlString.replace("<?xml version=\"1.0\"?>", "<?xml version=\"1.0\" encoding=\"GB2312\"?>");		
	   XmlOperator xmlOpr = new XmlOperator();
	   if(vlanString != null && vlanString.equals("1")){
		   xmlOpr.setFile("networkvlan.jsp");
	   }else
		   xmlOpr.setFile("network.jsp");
	   xmlOpr.saveImage(xmlString);
	   
	   return "/topology/network/save.jsp";   
   }
   
   private String savevlan()
   {
	   String xmlString = request.getParameter("hidXml");			
	   xmlString = xmlString.replace("<?xml version=\"1.0\"?>", "<?xml version=\"1.0\" encoding=\"GB2312\"?>");		
	   XmlOperator xmlOpr = new XmlOperator();
	   xmlOpr.setFile("networkvlan.jsp");
	   xmlOpr.saveImage(xmlString);
	   
	   return "/topology/network/save.jsp";   
   }
    
   public String execute(String action)
   {
	  if(action.equals("list"))
	     return list();     
      if(action.equals("read"))
         return read();      
	  if(action.equals("ready_edit"))
         return readyEdit();
      if(action.equals("update"))
         return update();  
      if(action.equals("refreshsysname"))
          return refreshsysname();
	  if(action.equals("delete"))
	     return delete();     
      if(action.equals("find"))
         return find();           
	  if(action.equals("ready_add"))
	     return "/topology/network/add.jsp";
      if(action.equals("add"))
         return add();
      if(action.equals("save"))
         return save();
      setErrorCode(ErrorMessage.ACTION_NO_FOUND);
      return null;
   }
}
