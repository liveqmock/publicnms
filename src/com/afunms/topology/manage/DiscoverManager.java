/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.manage;

import java.util.List;

import com.afunms.common.base.*;
import com.afunms.topology.dao.DiscoverConfigDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.DiscoverConfig;
import com.afunms.discovery.DiscoverComplete;
import com.afunms.discovery.DiscoverEngine;
import com.afunms.discovery.Seed;
import com.afunms.discovery.DiscoverInitialize;

public class DiscoverManager extends BaseManager implements ManagerInterface
{
  private String config()
  {
	  DiscoverConfigDao dao = new DiscoverConfigDao();
      List coreList = dao.listByFlag("core");
      List othercoreList = dao.listByFlag("othercore");
      List communityList = dao.listByFlag("community");
      List specialList = dao.listByFlag("specified");
      List shieldList = dao.listByFlag("shield"); 
      List netshieldList = dao.listByFlag("netshield");
      List includeList = dao.listByFlag("includenet");
      dao.close();
      
      request.setAttribute("core_list",coreList);
      request.setAttribute("othercore_list",othercoreList);
      request.setAttribute("community_list",communityList);
      request.setAttribute("specified_list",specialList); 
      request.setAttribute("shield_list",shieldList); 
      request.setAttribute("netshield_list",netshieldList);
      request.setAttribute("include_list",includeList);
      return "/common/config.jsp";
  }

  private String add() 
  {  	 
	  DiscoverConfig vo = new DiscoverConfig();
	  String flag = getParaValue("flag");
	  vo.setFlag(flag);
	  if("community".equals(flag))
	  {	  
         vo.setAddress("");
         vo.setCommunity(getParaValue("globe_community"));         
	  }
	  else if("specified".equals(flag))
	  {	  
         vo.setAddress(getParaValue("special_ip"));
         vo.setCommunity(getParaValue("special_community"));         
	  } 
	  else if("othercore".equals(flag))
	  {	  
         vo.setAddress(getParaValue("othercore_ip"));
         vo.setCommunity(getParaValue("othercore_community"));         
	  } 
	  else if("shield".equals(flag))
	  {	  
         vo.setAddress(getParaValue("net_address"));
         vo.setCommunity("");         
	  }
	  else if("netshield".equals(flag))
	  {	  
         vo.setShieldnetstart(getParaValue("shieldnetstart"));
         vo.setShieldnetend(getParaValue("shieldnetend"));
         vo.setCommunity("");         
	  }else if("includenet".equals(flag))
	  {	  
	         vo.setIncludenetstart(getParaValue("includenetstart"));
	         vo.setIncludenetend(getParaValue("includenetend"));
	         vo.setCommunity("");         
		  }
      DaoInterface dao = new DiscoverConfigDao();
      setTarget("/discover.do?action=config");
      return save(dao,vo);
  }  

  private String list()
  {
     DaoInterface dao = new HostNodeDao();
     setTarget("/topology/discover/list.jsp");
     return list(dao);
  }

  private String find()
  {
	 String key = getParaValue("key");
	 String value = getParaValue("value");
	 
	 if("category".equals(key))
	 {
		 if("路由器".equals(value))
			value = "1";
		 else if("路由交换机".equals(value))
			value = "2";
		 else if("交换机".equals(value))
			value = "3";
		 else if("服务器".equals(value))
			value = "4";
		 else if("打印机".equals(value))
			value = "5";		 		 
	 }	 
	 HostNodeDao dao = new HostNodeDao();
     request.setAttribute("list",dao.findByCondition(key,value));
     
     return "/topology/discover/find.jsp";
  }
  
  private String read()
  {
	  DaoInterface dao = new HostNodeDao();
	  setTarget("/topology/discover/read.jsp");
      return readyEdit(dao);
  }

  
  private String doDiscover()
  {
	  DiscoverInitialize discoverInit = new DiscoverInitialize();
      discoverInit.init();
      String ip = getParaValue("core_ip");
      String community = getParaValue("community");
      String writecommunity = getParaValue("writecommunity");
      int discovermodel = getParaIntValue("discovermodel"); 
      String bid = getParaValue("bid");
      DiscoverEngine.getInstance().setWritecommunity(writecommunity);
      DiscoverEngine.getInstance().setDiscover_bid(bid);
      Seed seed = new Seed(ip,community,discovermodel);
      
      seed.startDiscover();
      return "/discover.do?action=config";
  }
  
  private String stopDiscover()
  {
      DiscoverComplete dc = new DiscoverComplete();
      DiscoverEngine.getInstance().setStopStatus(1);
      Seed.discoverOK = true;
      dc.completed(Seed.discoverOK);
      return "/topology/discover/monitor.jsp";
  }
  
  public String execute(String action)
  {
     if(action.equals("config"))
        return config();
     if(action.equals("add"))
        return add();
     if(action.equals("list"))
        return list();     
     if(action.equals("read"))
        return read();      
     if(action.equals("find"))
        return find();
     if(action.equals("stop"))
         return stopDiscover();
	 if (action.equals("delete"))
     {	  
		 DiscoverConfigDao dao = new DiscoverConfigDao();
		 if(dao.delete(getParaValue("id")))    	    
            return "/discover.do?action=config";
		 else
			return null; 
     }    
     if(action.equals("do_discover"))
        return doDiscover();
     setErrorCode(ErrorMessage.ACTION_NO_FOUND);
     return null;
  }
}
