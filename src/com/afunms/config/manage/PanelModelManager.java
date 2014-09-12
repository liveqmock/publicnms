/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.config.manage;

import com.afunms.common.base.*;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.SysUtil;
import com.afunms.polling.*;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.node.*;
import com.afunms.system.dao.DepartmentDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.util.*;
import com.afunms.common.util.DBManager;
import com.afunms.config.model.*;
import com.afunms.config.dao.*;
import com.afunms.common.util.*;
import com.afunms.initialize.ResourceCenter;

import java.util.*;
import java.io.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
/**
 * 增加一个节点，要在server.jsp增加一个节点;
 * 删除或更新一个节点信息，在这则不直接操作server.jsp,而是让任务updateXml来更新xml.
 */
public class PanelModelManager extends BaseManager implements ManagerInterface
{
	private String list()
	{
		PortconfigDao dao = new PortconfigDao();
		List ips = dao.getIps();
		request.setAttribute("ips", ips);
		dao = new PortconfigDao();
		setTarget("/config/portconfig/list.jsp");
        return list(dao);
	}
	
	private String empty()
	{
		PortconfigDao dao = new PortconfigDao();
		dao.empty();
		dao = new PortconfigDao();
		List ips = dao.getIps();
		request.setAttribute("ips", ips);
		dao = new PortconfigDao();
		setTarget("/config/portconfig/list.jsp");
        return list(dao);
	}
	
	private String monitornodelist()
	{
		PortconfigDao dao = new PortconfigDao();	 
		setTarget("/config/portconfig/portconfiglist.jsp");
        return list(dao," where managed=1");
	}
	
	private String fromlasttoconfig()
	{
		PortconfigDao dao = new PortconfigDao();
		dao.fromLastToPortconfig();
		dao = new PortconfigDao();
		List ips = dao.getIps();
		request.setAttribute("ips", ips);
		dao = new PortconfigDao();
		setTarget("/config/portconfig/list.jsp");
        return list(dao);
	}

    
	private String readyEdit()
	{
		PortconfigDao dao = new PortconfigDao();
		Portconfig vo = new Portconfig();
	    vo = dao.loadPortconfig(getParaIntValue("id"));
	    request.setAttribute("vo", vo);
	    return "/config/portconfig/edit.jsp";
	}
	
	/**
	 * @author nielin
	 * modify 2010-01-14
	 * @return
	 */
	private String createpanel()
	{
		String rvalue="设置面板成功";
		int result = 0;
		try{
			String ipaddress = getParaValue("ipaddress");
			String imageType = getParaValue("imageType");
			IpaddressPanel ipaddressPanel = new IpaddressPanel();
			ipaddressPanel.setImageType(imageType);
			ipaddressPanel.setIpaddress(ipaddress);
			ipaddressPanel.setStatus("1");
			Host host = (Host)PollingEngine.getInstance().getNodeByIP(ipaddress);
			//PanelModelDao modeldao = new PanelModelDao();
		    //PanelModel panel = modeldao.loadPanelModel(host.getSysOid());
			String oid = host.getSysOid();
			oid = oid.replaceAll("\\.", "-");
			PanelXmlOperator panelxmlOpr = new PanelXmlOperator();
			String filename = SysUtil.doip(host.getIpAddress())+".jsp";
			panelxmlOpr.setFile(filename,2);
			panelxmlOpr.setOid(oid);
			panelxmlOpr.setImageType(imageType);
			panelxmlOpr.setIpaddress(host.getIpAddress());
			//写XML
			panelxmlOpr.init4createXml();
			result = panelxmlOpr.createXml(1);
			if(result==1){
				boolean flag = false;
				IpaddressPanelDao ipaddressPanelDao = new IpaddressPanelDao();
				try{
					flag = ipaddressPanelDao.save(ipaddressPanel);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					ipaddressPanelDao.close();
				}
				if(!flag) result = 0;
			}
		}catch(Exception e){
			e.printStackTrace();
			result = 0;
			rvalue="设置面板失败";
		}
		request.setAttribute("rvalue", rvalue);
		request.setAttribute("result", result);
	    return panelnodelist();
	}
	
	private String panelnodelist()
	{
		HostNodeDao dao = new HostNodeDao();	 
		setTarget("/topology/network/panelnodelist.jsp");
        return list(dao," where managed=1 and (category<4 or category=7 or category=8 )");
	}
	
	private String showportreset()
	{
		HostNode hostnode = null;
		
		String ip="";
		String index="";
		String ifname="";

		I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();						
        
        Hashtable hash = new Hashtable();
        
        String[] netInterfaceItem={"index","ifDescr","ifSpeed","ifOperStatus","OutBandwidthUtilHdx","InBandwidthUtilHdx"};
        try{
        	ip = getParaValue("ipaddress");//request.getParameter("ipaddress");
			index = getParaValue("ifindex");//request.getParameter("index");
			
			String[] netIfdetail={"index","ifDescr","ifname","ifType","ifMtu","ifSpeed","ifPhysAddress","ifOperStatus","ifAdminStatus"};
			hash = hostlastmanager.getIfdetail_share(ip,index,netIfdetail,"","");
			
			HostNodeDao hostdao = new HostNodeDao();
	    	List hostlist = hostdao.loadNetwork(1);
	    	if(hostlist != null && hostlist.size()>0){
	    		for(int i=0;i<hostlist.size();i++){
	    			HostNode tempHost = (HostNode)hostlist.get(i);
	    			if(tempHost.getIpAddress().equalsIgnoreCase(ip)){
	    				hostnode = tempHost;
	    				break;
	    			}
	    		}
	    	}
	    	hostdao.close();
        }catch(Exception e){
        	e.printStackTrace();
        }
        String alias="";
        if(hostnode!=null)alias=hostnode.getAlias();
		request.setAttribute("index",index);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("hostname",alias);
		
		request.setAttribute("hash", hash);
	    return "/panel/view/portreset.jsp";
	}
	
	/**
	 * @author nielin
	 * add 2010-01-14
	 * @return
	 */
	private String panelmodellist(){
		String jsp = "/panel/view/panelmodellist.jsp";
		PanelModelDao panelModelDao = new PanelModelDao();
		try{
			setTarget(jsp);
			jsp = list(panelModelDao, " order by oid,imagetype");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			panelModelDao.close();
		}
		return jsp;
	}
	
	/**
	 * @author nielin
	 * modify 2010-01-14
	 * @return
	 */
	private String showaddpanel()
	{
		// nielin add for panelmodel at 2010-01-05 start ---------------
		// From the database to find out all the network devices, 
		HostNodeDao hostdao = new HostNodeDao();
		List iplist = null;
		try{
			iplist = hostdao.loadNetwork(1); 
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			hostdao.close();
		}
		// Send network list to upload.jsp
		request.setAttribute("iplist", iplist);
		// nielin add for panelmodel at 2010-01-05 end ---------------
	    return "/panel/view/upload.jsp";
	}
	
	private int getImageType(String imgPath , String oid , int imageType){
		File file = new File(imgPath+oid+"_"+imageType+".jpg");
		if(file.exists()){
			imageType++;
			
			imageType = getImageType(imgPath , oid , imageType);
		}
		return imageType;
		
		
	}
	
	/**
	 * This method is used to handle the name of upload pictures
	 * @author nielin 
	 * add for panelmodel 
	 * 2010-01-07
	 * @return
	 */
	private String upload(){
		String ipaddress = request.getParameter("ipaddress");
		Host host = (Host)PollingEngine.getInstance().getNodeByIP(ipaddress);
		request.setAttribute("host", host);
		
		// Using the recursive method produces picture name
		// the picture name is oid_imageType
		String oid = host.getSysOid();
		String imgPath= ResourceCenter.getInstance().getSysPath() + "panel/view/image/";
		//virtualname = ".."+rootPath+"/panel/view/image/"+virtualname;
		oid = oid.replaceAll("\\.","-");
		int imageType = 1;
		imageType = getImageType(imgPath , oid , imageType);
		request.setAttribute("imageType", String.valueOf(imageType));
		String fileName = ".." + request.getContextPath()+"/panel/view/image/"+oid+"_"+imageType+".jpg";
		request.setAttribute("fileName", fileName);
		
		
		I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
        Vector vector = new Vector();
        String[] netInterfaceItem={"index","ifDescr","ifType","ifSpeed","ifOperStatus","OutBandwidthUtilHdx","InBandwidthUtilHdx"};
        try{
        	vector = hostlastmanager.getInterface_share(ipaddress,netInterfaceItem,"index","",""); 
        }catch(Exception e){
        	e.printStackTrace();
        }
        List indexlist = new ArrayList();
        Hashtable hs = new Hashtable();
        int p = 0;
        if(vector != null && vector.size()>0){
        	for(int k=0;k<vector.size();k++){
        		String[] strs = (String[])vector.get(k);
                	String ifname = strs[1];
                	String index = strs[0];
                	String iftype = strs[2];
                	SysLogger.info("ip:"+ipaddress+"---index:"+index+"---ifname:"+ifname+"---iftype:"+iftype);
                	if((iftype.equalsIgnoreCase("ethernetCsmacd(6)") || (iftype.equalsIgnoreCase("gigabitEthernet(117)"))
                			|| (iftype.equalsIgnoreCase("ppp(23)"))|| (iftype.equalsIgnoreCase("sonet(39)"))
                			|| (iftype.equalsIgnoreCase("0.0")))&&!"LoopBack0".equalsIgnoreCase(ifname)){
                		hs.put(p,index+";"+ifname);
                		SysLogger.info("ip:"+ipaddress+"===index:"+index+"===iftype:"+iftype);
                		indexlist.add(p,index+"");
                		p=p+1;
                	}
        	}	
        }
        request.setAttribute("hs", hs);
        request.setAttribute("indexlist", indexlist);
        return "/panel/view/panel.jsp";
	}
	
	/** 
	 * @author nielin 
	 * modify at 2010-01-07
	 * @return
	 */
	private String createpanelmodel()
	{
		String soid = getParaValue("soid");
		String height = getParaValue("height");
		String width = getParaValue("width");
		String addxyid = getParaValue("panelxml");
		String select = getParaValue("select");
		String imageType = getParaValue("imageType");
		String [] str = addxyid.split(";");
		String [] sel = select.split(";"); 
		Hashtable ht = new Hashtable();
		Hashtable hs = new Hashtable();
		for(int i = 0; i<str.length;i++){
			ht.put(i, str[i]);	 
			hs.put(i, sel[i]);
		}
		try{
			//creatXml(ht,soid,hs,width,height);
			String name = soid.replaceAll("\\.", "-");
			int falg = creatXml(name+"_"+imageType , ht , hs);
			// If create a panelmodel xml successful, the data added to the database
			if(falg == 1){
				PanelModel model = new PanelModel();
				model.setOid(soid);
				model.setImageType(imageType);
				model.setHeight(height);
				model.setWidth(width);
				boolean result = false ;
				PanelModelDao dao = new PanelModelDao();
				try{
					result = dao.save(model);
				}catch(Exception ex){
					ex.printStackTrace();
					result = false;
				}finally{
					dao.close();
				}
				//If the data added to the database successfully, and the type is 1, 
				//then the call defaultPanelModel()
				if(("1".equals(imageType))&&result){
					defaultPanelModel(soid,imageType);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("rvalue", "生成面板摸板成功！");
	    return panelmodellist();
	}
	
	/**
	 * This method is used to make the network device 
	 * with the same oid use a default template type
	 * 这个方法用于使具有相同oid的网络设备使用一个默认的模板类型
	 * @author nielin 
	 * add 2010-01-08
	 * @param oid
	 * @param imageType
	 * @return {@link Boolean}
	 */
	private boolean defaultPanelModel(String oid , String imageType){
		HostNodeDao hostNodeDao = new HostNodeDao();
		try{
			// 获取具有相同oid的网络设备 get
			List list = hostNodeDao.loadHostByOid(oid);
			if(list!=null && list.size()>0){
				List ipaddressPanelList = new ArrayList();
				for(int i = 0 ; i<list.size(); i++){
					HostNode host = (HostNode)list.get(i);
					IpaddressPanel ipaddressPanel = new IpaddressPanel();
					ipaddressPanel.setIpaddress(host.getIpAddress());
					ipaddressPanel.setStatus("1");
					ipaddressPanel.setImageType(imageType);
					ipaddressPanelList.add(ipaddressPanel);
				}
				IpaddressPanelDao ipaddressPanelDao = new IpaddressPanelDao(); 
				try{
					ipaddressPanelDao.save(ipaddressPanelList);
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}finally{
					ipaddressPanelDao.close();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			hostNodeDao.close();
		}
		return true;
	}
	
	private String update()
	{  
		Portconfig vo = new Portconfig();
		int id = getParaIntValue("id");
		PortconfigDao dao = new PortconfigDao(); 	
		vo = dao.loadPortconfig(id);
		dao.close();
		String linkuse = getParaValue("linkuse");
		int sms = getParaIntValue("sms");
		int reportflag = getParaIntValue("reportflag");
		vo.setLinkuse(linkuse);
		if(sms > -1)
			vo.setSms(sms);
		if(reportflag > -1)
			vo.setReportflag(reportflag);
		dao = new PortconfigDao();
		try{
			dao.update(vo);
		}catch(Exception e){
			e.printStackTrace();
		}
		dao = new PortconfigDao();
		List ips = dao.getIps();
		request.setAttribute("ips", ips);
		dao = new PortconfigDao();
		return "/portconfig.do?action=list";
    }
	
	private String updateport()
	{  
		Portconfig vo = new Portconfig();
		int id = getParaIntValue("id");
		PortconfigDao dao = new PortconfigDao(); 	
		vo = dao.loadPortconfig(id);
		dao.close();
		String linkuse = getParaValue("linkuse");
		int sms = getParaIntValue("sms");
		int reportflag = getParaIntValue("reportflag");
		vo.setLinkuse(linkuse);
		if(sms > -1)
			vo.setSms(sms);
		if(reportflag > -1)
			vo.setReportflag(reportflag);
		dao = new PortconfigDao();
		try{
			dao.update(vo);
		}catch(Exception e){
			e.printStackTrace();
		}
		dao = new PortconfigDao();
		List ips = dao.getIps();
		try{
			//request.setAttribute("ips", ips);
		}catch(Exception e){
			e.printStackTrace();
		}
		return "/portconfig.do?action=list";
    }
  

    private String updateselect()
    {
	   String key = getParaValue("key");
	   String value = getParaValue("value");	
	   PortconfigDao dao = new PortconfigDao();
	   request.setAttribute("key", key);
	   request.setAttribute("value", value);
	   int id = getParaIntValue("id");
	   Portconfig vo = new Portconfig();
	   vo = dao.loadPortconfig(id);
	   
		dao.close();
		String linkuse = getParaValue("linkuse");
		int sms = getParaIntValue("sms");
		int reportflag = getParaIntValue("reportflag");
		vo.setLinkuse(linkuse);
		
		vo.setSms(sms);
		vo.setReportflag(reportflag);
		dao = new PortconfigDao();
		try{
			dao.update(vo);
		}catch(Exception e){
			e.printStackTrace();
		}
		dao = new PortconfigDao();
	   setTarget("/config/portconfig/findlist.jsp");
       return list(dao," where "+key+" = '"+value+"'");
   }

    private String find()
    {
	   String ipaddress = getParaValue("ipaddress");	 
	   PortconfigDao dao = new PortconfigDao();
	   request.setAttribute("ipaddress", ipaddress);
		List ips = dao.getIps();
		request.setAttribute("ips", ips);
		dao = new PortconfigDao();
	   setTarget("/config/portconfig/findlist.jsp");
       return list(dao," where ipaddress = '"+ipaddress+"'");
   }
    
   public String execute(String action)
   {
	  if(action.equals("list"))
	     return list(); 
	  if(action.equals("createpanel"))
		 return createpanel();
	  if(action.equals("show_portreset"))
		 return showportreset(); 
	  if(action.equals("panelmodellist")){
		  return panelmodellist();
	  }
	  if(action.equals("showaddpanel"))
	     return showaddpanel();
	  if(action.equals("upload")){
		  return upload();
	  }
	  if(action.equals("createpanelmodel"))
		 return createpanelmodel();
	  if(action.equals("showedit"))
         return readyEdit();
      if(action.equals("update"))
         return update(); 
      if(action.equals("updateport"))
          return updateport();
      if(action.equals("find"))
         return find();  
      if(action.equals("updateselect"))
          return updateselect();
      if(action.equals("empty"))
          return empty();
	  if(action.equals("ready_add"))
	     return "/config/portconfig/add.jsp";
	  if (action.equals("delete"))
      {	  
		    DaoInterface dao = new PortconfigDao();
    	    setTarget("/portconfig.do?action=list");
            return delete(dao);
        }
	  
      setErrorCode(ErrorMessage.ACTION_NO_FOUND);
      return null;
   }
	//public static Hashtable portHash=new Hashtable();
	public void inputPort(String equiptype,List list){
		Hashtable portHash=new Hashtable();
		Hashtable porth=new Hashtable();
		Hashtable pp=new Hashtable();
		for(int i=0;i<list.size();i++){
			String []str=(String[]) list.get(i);
			pp.put(i, str[0]+";"+str[1]);
		}
		porth.put(equiptype, pp);
		portHash=porth;
	}
	
	public static void creatXml(Hashtable has,String name,Hashtable hat,String width,String height) {
		int flag = 0;
		PanelXmlOperator panelxmlOpr = new PanelXmlOperator();
		try{
			panelxmlOpr.setFile(name+".jsp",1);
			panelxmlOpr.setOid(name);
			//写XML
			panelxmlOpr.init4createXml();
			panelxmlOpr.createModelXml(has,hat);
			
			/*
			
			//panelxmlOpr.setIpaddress(node.getIpAddress());
		 	// 创建根节点 root 
		    Element root = new Element("root"); 
	        
	       // 根节点添加到文档中； 

	        Document Doc = new Document(root); 
	        Element element = new Element("nodes");
	        root.addContent(element);
	       for (int i = 0; i < has.size(); i++) { 
	    	  String [] str1 =((String)has.get(i)).split(",");
	           // 创建节点 node; 
               String index = str1[2];
               index = index.replaceAll("index", "");
	           Element elements = new Element("node"); 
	           // 给 node 节点添加子节点并赋值；
	           elements.addContent(new Element("turn").setText((String)hat.get(i)));
	           elements.addContent(new Element("img").setText("img")); 
	           elements.addContent(new Element("x").setText(str1[0])); 
	           elements.addContent(new Element("y").setText(str1[1])); 
	           elements.addContent(new Element("index").setText(index));
	           // 给父节点list添加node子节点; 

	           element.addContent(elements); 
	    
	       } 
	       Format format = Format.getPrettyFormat();
	        XMLOutputter XMLOut = new XMLOutputter(format); 
	          
	       // 输出 user.xml 文件； 
           //name = name.replaceAll("\\.", "-");
	        XMLOut.output(Doc, new FileOutputStream(ResourceCenter.getInstance().getSysPath()+"/panel/model/"+name+".jsp"));
	        */
		}catch(Exception e){
			e.printStackTrace();
			flag = 1;
		}
		if(flag == 0){
			//写入数据库
			PanelModelDao dao = new PanelModelDao();
			PanelModel model = new PanelModel();
			name = name.replaceAll("-", "\\.");
			model.setOid(name);
			model.setHeight(height);
			model.setWidth(width);
			try{
				/*
			//PanelModel panel = dao.loadPanelModel(name);
			if(panel == null){
				dao = new PanelModelDao();
				dao.save(model);
			
			}
				*/
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
	}
	
	/**
	 * The mothed is used for creating Xml
	 * Return flag == 1 is to create XML successful , else is false;
	 * @author nielin 
	 * add at 2010-01-07
	 * @param name,has,hat,width,height
	 * @return flag<code>{@link int}</code>
	 */
	public static int creatXml(String name, Hashtable has ,Hashtable hat) {
		int flag = 0;
		PanelXmlOperator panelxmlOpr = new PanelXmlOperator();
		try{
			panelxmlOpr.setFile(name+".jsp",1);
			//panelxmlOpr.setOid(name);
			//写XML
			panelxmlOpr.init4createXml();
			flag = panelxmlOpr.createModelXml(has,hat);
		}catch(Exception e){
			flag = 0;
			e.printStackTrace();
		}
		return flag; 
	}
}
