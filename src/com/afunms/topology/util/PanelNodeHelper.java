/**
 * <p>Description:node helper</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-11-25
 */

package com.afunms.topology.util;

import java.util.*;
import java.io.File;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.afunms.common.util.SysLogger;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.base.NodeCategory;
import com.afunms.polling.base.PanelNodeCategory;

public class PanelNodeHelper
{
	private static HashMap categoryMap;
	private static List categoryList;
    static
    {	
       categoryMap = new HashMap(); 
       categoryList = new ArrayList(); 
       try
	   {
    	   SAXBuilder builder = new SAXBuilder();	
           Document doc = builder.build(new File(ResourceCenter.getInstance().getSysPath() + "WEB-INF/classes/panel-category.xml"));
           List list = doc.getRootElement().getChildren("category");
           Iterator it = list.iterator();
           while(it.hasNext())
           {
           	   Element ele = (Element)it.next();
           	   PanelNodeCategory category = new PanelNodeCategory();
           	   String id = ele.getAttributeValue("id");
           	   category.setId(Integer.parseInt(id));
           	   category.setCnName(ele.getChildText("cn_name"));
           	   category.setEnName(ele.getChildText("en_name"));
           	   category.setUpUpImage("image/" + ele.getChildText("up_up_image"));
           	   category.setDownUpImage("image/" + ele.getChildText("down_up_image"));
           	   category.setUpDownImage("image/" + ele.getChildText("up_down_image"));
        	   category.setDownDownImage("image/" + ele.getChildText("down_down_image"));
           	   categoryMap.put(id, category);
           	   categoryList.add(category);
           }
	   }
       catch(Exception e)
       {
    	   SysLogger.error("NodeHelper.static",e);
       }
    }
    
    private static PanelNodeCategory getCategory(int id)
    {
    	if(categoryMap.get(String.valueOf(id))!=null)
    	   return (PanelNodeCategory)categoryMap.get(String.valueOf(id));
    	else	
    	{
    		System.out.println("category is not exist,id=" + id);
    		return (PanelNodeCategory)categoryMap.get("1000");
    	}
    }
    
    public static List getAllCategorys()
    {
        return categoryList;
    }
    
	/**
	 * 节点类别(中文描述)
	 */
    public static String getNodeCategory(int category)
    {
    	return getCategory(category).getCnName();    	
    }
    
	/**
	 * 节点类别(英文描述)
	 */
    public static String getNodeEnCategory(int category)
    {
    	return getCategory(category).getEnName();  
    }  
    
    /**
     * 节点在拓扑图上的图标 
     */    
    public static String getUpUpImage(int category)
    {    	
	    return getCategory(category).getUpUpImage();
    }           
    public static String getUpDownImage(int category)
    {
    	return getCategory(category).getUpDownImage();
    }
    public static String getDownUpImage(int category)
    {    	
	    return getCategory(category).getDownUpImage();
    }           
    public static String getDownDownImage(int category)
    {
    	return getCategory(category).getDownDownImage();
    }
    
    /**
     * 节点在拓扑图上的报警时的图标 
     */            
    public static String getLostImage(int category)
    {
    	return getCategory(category).getLostImage();
    } 
    
	/**
	 * 节点状态描述
	 */
    public static String getStatusDescr(int status)
    {
        String descr = null; 
    	if(status==1)
    	   descr = "正常";
		else if(status==2)
		   descr = "设备忙"; 
		else if(status==3)
		   descr = "关机";
		else
		   descr = "不被管理";
    	return descr;
    }
    
	/**
	 * 节点状态标志
	 */
    public static String getStatusImage(int status)
    {
        String image = null; 
    	if(status==1)
    	   image = "status_ok.gif";
		else if(status==2)
		   image = "status_busy.gif"; 
		else if(status==3)
		   image = "status_down.gif";
		else
		   image = "unmanaged.gif";
    	return "image/topo/" + image;
    }
        
    /**
     * 根据sysOid得到服务器在拓扑图上的图标 
     */
    public static String getServerTopoImage(String sysOid)
    {
        String fileName = null;	
        if(sysOid == null){
        	fileName = "server.gif";
        	return "image/topo/" + fileName;
        }
	    if(sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.1"))//win_xp
		   fileName = "win_xp.gif";
	    else if(sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.2")||sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.3"))//win_2000
		   fileName = "win_2000.gif";
	    else if(sysOid.equals("1.3.6.1.4.1.311.1.1.3.1"))//win_nt
		   fileName = "win_nt.gif";	   
	    else if(sysOid.equals("1.3.6.1.4.1.2021.250.10")||sysOid.equals("1.3.6.1.4.1.8072.3.2.10"))//linux				
		   fileName = "linux.gif";
	    else if(sysOid.startsWith("1.3.6.1.4.1.42."))  //sun_solaris
		   fileName = "solaris.gif";
	    else if(sysOid.startsWith("1.3.6.1.4.1.2."))  //ibm_aix
		   fileName = "ibm.gif";
	    else if(sysOid.startsWith("1.3.6.1.4.1.11."))  //hp_ux
		   fileName = "hp.gif";
	    else if(sysOid.startsWith("1.3.6.1.4.1.36."))  //compaq
		   fileName = "compaq.gif";
	    else
		   fileName = "server.gif";
	    return "image/topo/" + fileName;
    }

    /**
     * 根据sysOid得到服务器在拓扑图上的报警时图标 
     */
    public static String getServerAlarmImage(String sysOid)
    {
    	String fileName = null;	
    	if(sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.1"))//win_xp
		   fileName = "win_xp_alarm.gif";
	    else if(sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.2")||sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.3"))//win_2000
		   fileName = "win_2000_alarm.gif";
	    else if(sysOid.equals("1.3.6.1.4.1.311.1.1.3.1"))//win_nt
		   fileName = "win_nt_alarm.gif";	   
	    else if(sysOid.equals("1.3.6.1.4.1.2021.250.10")||sysOid.equals("1.3.6.1.4.1.8072.3.2.10"))//linux				
		   fileName = "linux_alarm.gif";
	    else if(sysOid.startsWith("1.3.6.1.4.1.42."))  //sun_solaris
		   fileName = "solaris_alarm.gif";
	    else if(sysOid.startsWith("1.3.6.1.4.1.2."))  //ibm_aix
		   fileName = "ibm_alarm.gif";
	    else if(sysOid.startsWith("1.3.6.1.4.1.11."))  //hp_ux
		   fileName = "hp_alarm.gif";
	    else if(sysOid.startsWith("1.3.6.1.4.1.36."))  //compaq
		   fileName = "compaq_alarm.gif";    	
	    else
		   fileName = "server_alarm.gif";
	    return "image/topo/" + fileName;
    }

    public static String getHostOS(String sysOid)
    {
    	String os = null;	
    	if(sysOid.startsWith("1.3.6.1.4.1.311.")) //win
    	   os = "windows";
	    else if(sysOid.equals("1.3.6.1.4.1.2021.250.10")||sysOid.equals("1.3.6.1.4.1.8072.3.2.10"))//linux				
	       os = "linux";
	    else if(sysOid.startsWith("1.3.6.1.4.1.42."))  //sun_solaris
	       os = "solaris";
	    else if(sysOid.startsWith("1.3.6.1.4.1.2."))  //ibm_aix
	       os = "aix";
	    else if(sysOid.startsWith("1.3.6.1.4.1.36."))  //compaq
	       os = "tru64";    	
	    else if(sysOid.startsWith("1.3.6.1.4.1.9."))  //cisco
	       os = "cisco";
	    else 
	       os = "";	
	    return os;
    }
            
	/**
	 * 系统快照状态标志
	 */
    public static String getSnapStatusImage(int status)
    {
        String image = null; 
    	if(status==1)
    	   image = "status5.png";
		else if(status==2)
		   image = "status2.png"; 
		else if(status==3)
		   image = "status1.png";
    	return "image/topo/" + image;
    }
    
	/**
	 * 系统快照状态标志
	 */
    public static String getSnapStatusImage(int status,int category)
    {
        String image = null; 
        if(category == 1){
        	//路由器
        	if(status==2)
        		image = "Drouter-R-24.gif";
        	else
        		image = "Drouter-B-24.gif";
        	
        }else if(category == 2){
        	//路由器
        	if(status==2)
        		image = "Switch-R-32.gif";
        	else
        		image = "Switch-B-32.gif";
        }else if(category == 3){
        	//路由器
        	if(status==2)
        		image = "server-R-24.gif";
        	else
        		image = "server-B-24.gif";        	
        }
    	return "image/topo/" + image;
    }
    
	/**
	 * 报警级别标志
	 */
    public static String getAlarmLevelImage(int level)
    {
        String image = null; 
    	if(level==1)
    	   image = "alarm_level_1.gif";
		else if(level==2)
		   image = "alarm_level_2.gif"; 
		else if(level==3)
		   image = "alarm_level_3.gif";
    	return "image/topo/" + image;
    }

	/**
	 * 报警级别描述
	 */
    public static String getAlarmLevelDescr(int level)
    {
        String descr = null; 
    	if(level==1)
    	   descr = "注意";
		else if(level==2)
		   descr = "故障"; 
		else if(level==3)
		   descr = "严重";
    	return descr;
    }
    
    public static String getMenuItem(String index,String ip)
    {
	    String menuItem = 
			
			"<a class=\"panel_manage_menu_out\" onmouseover=\"panelmanageMenuOver();\" onmouseout=\"panelmanageMenuOut();\" " +
			"onclick=\"javascript:window.open('/afunms/panel.do?action=show_portreset&ifindex="+index+"&ipaddress=" + ip + "','window', " +
			"'toolbar=no,height=300,width=600,scrollbars=yes,center=yes,screenY=0')\"" +
			">&nbsp;&nbsp;&nbsp;&nbsp;管理端口</a><br/>"+
			
			"<a class=\"panel_detail_menu_out\" onmouseover=\"paneldetailMenuOver();\" onmouseout=\"paneldetailMenuOut();\" " +
			"onclick=\"javascript:window.open('/afunms/monitor.do?action=show_utilhdx&ifindex="+index+"&ipaddress=" + ip + "','window', " +
			"'toolbar=no,height=500,width=600,scrollbars=yes,center=yes,screenY=0')\"" +
			">&nbsp;&nbsp;&nbsp;&nbsp;端口实时信息</a><br/>";
   
	    
	    return menuItem;
    }
}