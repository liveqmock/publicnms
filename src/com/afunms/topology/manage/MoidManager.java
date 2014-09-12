package com.afunms.topology.manage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysLogger;
import com.afunms.initialize.ResourceCenter;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.node.Host;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.NodeMonitorDao;
import com.afunms.topology.model.NodeMonitor;
import com.lowagie.text.DocumentException;

public class MoidManager extends BaseManager implements ManagerInterface
{
   private String stopmoid()
   {
	   int nodeId = getParaIntValue("id");
	   //int value = getParaIntValue("value");
	   String moid = getParaValue("moid");
	   String ipaddress = getParaValue("ipaddress");
	   Host host = (Host)PollingEngine.getInstance().getNodeByID(nodeId);
	   //MonitoredItem item = (MonitoredItem)host.getItemByMoid(moid);
       //item.setThreshold(value);
       DBManager conn = new DBManager();
       try{
       conn.executeUpdate("update topo_node_monitor set enabled=0 where node_id=" + nodeId + " and id='" + moid + "'");  
       }catch(Exception e){
    	   e.printStackTrace();
       }finally{
    	   conn.close();
       }
       //conn.close();
       NodeMonitorDao nmdao = new NodeMonitorDao();
       List nmlist = nmdao.loadByNodeID(nodeId);
       nmdao.close();
	   host.setMoidList(nmlist);
	   
	   request.setAttribute("list", nmlist);
	   request.setAttribute("id", nodeId+"");
	   request.setAttribute("host", host);
	   request.setAttribute("ipaddress", ipaddress);
	   return "/topology/network/moids.jsp";
   }
   
   private String startmoid()
   {
	   int nodeId = getParaIntValue("id");
	   //int value = getParaIntValue("value");
	   String moid = getParaValue("moid");
	   String ipaddress = getParaValue("ipaddress");
	   Host host = (Host)PollingEngine.getInstance().getNodeByID(nodeId);
       DBManager conn = new DBManager();
       try{
       conn.executeUpdate("update topo_node_monitor set enabled=1 where node_id=" + nodeId + " and id='" + moid + "'");      
       }catch(Exception e){
    	   e.printStackTrace();
       }finally{
    	   conn.close();
       }
       NodeMonitorDao nmdao = new NodeMonitorDao();
       List nmlist = nmdao.loadByNodeID(nodeId);
	   host.setMoidList(nmlist);
	   
	   request.setAttribute("list", nmlist);
	   request.setAttribute("id", nodeId+"");
	   request.setAttribute("host", host);
	   request.setAttribute("ipaddress", ipaddress);
	   return "/topology/network/moids.jsp";
   }
   
   private String update()
   {
	   int nodeId = getParaIntValue("id");
	   int value = getParaIntValue("value");
	   String moid = getParaValue("moid");
	   Host host = (Host)PollingEngine.getInstance().getNodeByID(nodeId);
	   MonitoredItem item = (MonitoredItem)host.getItemByMoid(moid);
       item.setThreshold(value);
       DBManager conn = new DBManager();
       try{
       conn.executeUpdate("update topo_node_monitor set threshold=" + value + " where node_id=" + nodeId + " and moid='" + moid + "'");      
       }catch(Exception e){
    	   e.printStackTrace();
       }finally{
    	   conn.close();
       }
       
       return "/detail/dispatcher.jsp?id=" + nodeId;
   }
   
   private String updatemoid()
   {
	   int nodeId = getParaIntValue("id");
	   //int value = getParaIntValue("value");
	   String moid = getParaValue("moid");
	   Host host = (Host)PollingEngine.getInstance().getNodeByID(nodeId);
	   NodeMonitorDao nmdao = new NodeMonitorDao();
	   NodeMonitor nodemointor = (NodeMonitor)nmdao.findByID(moid);
	   if(getParaIntValue("enabled")==0){
		   nodemointor.setEnabled(false);
	   }else{
		   nodemointor.setEnabled(true);
	   }
	   
	   nodemointor.setLimenvalue0(getParaIntValue("limenvalue0"));
	   nodemointor.setTime0(getParaIntValue("time0"));
	   nodemointor.setSms0(getParaIntValue("sms0"));
	   nodemointor.setLimenvalue1(getParaIntValue("limenvalue1"));
	   nodemointor.setTime1(getParaIntValue("time1"));
	   nodemointor.setSms1(getParaIntValue("sms1"));
	   nodemointor.setLimenvalue2(getParaIntValue("limenvalue2"));
	   nodemointor.setTime2(getParaIntValue("time2"));
	   nodemointor.setSms2(getParaIntValue("sms2"));
	   
	   //MonitoredItem item = (MonitoredItem)host.getItemByMoid(moid);
       //item.setThreshold(value);
       DBManager conn = new DBManager();
       String sql = "update topo_node_monitor set limenvalue0=" + nodemointor.getLimenvalue0() + ",time0="+nodemointor.getTime0()+",sms0="+nodemointor.getSms0();
       sql = sql+", limenvalue1=" + nodemointor.getLimenvalue1() + ",time1="+nodemointor.getTime1()+",sms1="+nodemointor.getSms1();
       sql = sql+", limenvalue2=" + nodemointor.getLimenvalue2() + ",time2="+nodemointor.getTime2()+",sms2="+nodemointor.getSms2();
       sql = sql+",enabled="+getParaIntValue("enabled");
       sql = sql+" where id=" + moid ;
       SysLogger.info(sql);
       try{
    	   conn.executeUpdate(sql);      
       }catch(Exception e){
    	   e.printStackTrace();
       }finally{
    	   conn.close();
       }
       
       return "/";
   }
   
   private String readyeditmoids()
   {
	   int nodeId = getParaIntValue("id");
	   String ipaddress = getParaValue("ipaddress");
	   Host host = (Host)PollingEngine.getInstance().getNodeByID(nodeId);
	   
	   List moidlist = host.getMoidList();
	   
	   request.setAttribute("list", moidlist);
	   request.setAttribute("id", nodeId+"");
	   request.setAttribute("host", host);
	   request.setAttribute("ipaddress", ipaddress);
	   return "/topology/network/moids.jsp";
   }
   
   private String showeditmoids()
   {
	   int nodeId = getParaIntValue("id");
	   int moId = getParaIntValue("moid");
	   String ipaddress = getParaValue("ipaddress");
	   Host host = (Host)PollingEngine.getInstance().getNodeByID(nodeId);
	   NodeMonitorDao nmdao = new NodeMonitorDao();
	   NodeMonitor nodemointor = (NodeMonitor)nmdao.findByID(moId+"");
	   //List moidlist = host.getMoidList();
	   
	   request.setAttribute("nodemointor", nodemointor);
	   request.setAttribute("id", nodeId+"");
	   request.setAttribute("host", host);
	   request.setAttribute("ipaddress", ipaddress);
	   return "/topology/network/showeditmoid.jsp";
   }
   
   private String allmoidlist()
   {
	   User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
	   StringBuffer s = new StringBuffer();
	   int _flag = 0;
	   String businessid = current_user.getBusinessids();
		if (businessid != null){
			if(businessid !="-1"){
				String[] bids = businessid.split(",");
				if(bids.length>0){
					for(int i=0;i<bids.length;i++){
						if(bids[i].trim().length()>0){
							if(_flag==0){
								s.append(" and ( bid like '%,"+bids[i].trim()+",%' ");
								_flag = 1;
							}else{
								//flag = 1;
								s.append(" or bid like '%,"+bids[i].trim()+",%' ");
							}
						}
					}
					s.append(") ") ;
				}
				
			}	
		}
		
	   NodeMonitorDao nmdao = new NodeMonitorDao();
	   HostNodeDao _dao = new HostNodeDao();
	   List nodelist = new ArrayList();
	   try{
		   nodelist = (List)_dao.loadNetworkByBid(5,current_user.getBusinessids());
	   }catch(Exception e){
		   e.printStackTrace();
	   }finally{
		   _dao.close();
	   }   
	   request.setAttribute("nodelist", nodelist);
	   
	   setTarget("/topology/network/allmoidlist.jsp");
	   return list(nmdao," where node_id in(select id from topo_host_node where 1=1 "+ s.toString()+")");
	   //return list(nmdao);
   }
   
   private String allmoidsearch()
   {
	   NodeMonitorDao nmdao = new NodeMonitorDao();
	   String ipaddress = getParaValue("ipaddress");
	   List moidlist = nmdao.loadByNodeIp(ipaddress);
	   request.setAttribute("ipaddress", ipaddress);
	   request.setAttribute("list", moidlist);
	   User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
	   HostNodeDao _dao = new HostNodeDao();
	   List nodelist = new ArrayList();
	   try{
		   nodelist = (List)_dao.loadNetworkByBid(5,current_user.getBusinessids());
	   }catch(Exception e){
		   e.printStackTrace();
	   }finally{
		   _dao.close();
	   }   
	   request.setAttribute("nodelist", nodelist);
	   
	   
	   return "/topology/network/allmoidsearchlist.jsp";
   }
   
   private String showallmoids()
   {
	   int nodeId = getParaIntValue("id");
	   String ipaddress = getParaValue("ipaddress");
	   Host host = (Host)PollingEngine.getInstance().getNodeByID(nodeId);
	   NodeMonitorDao nmdao = new NodeMonitorDao();
	   host.setMoidList(nmdao.loadByNodeID(nodeId));
	   List moidlist = host.getMoidList();
	   
	   request.setAttribute("list", moidlist);
	   request.setAttribute("id", nodeId+"");
	   request.setAttribute("host", host);
	   request.setAttribute("ipaddress", ipaddress);
	   return "/topology/network/showeallmoids.jsp";
   }
 //zhushouzhi------------------------------
   private String downloadnetworklistfuck()
   {
	  // request.setAttribute("list", moidlist);
	   
		Hashtable reporthash = new Hashtable();
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		report.createReport_faworklist("/temp/faworklist_report.xls",current_user);
		request.setAttribute("filename", report.getFileName());
		return "/topology/network/downloadreport.jsp";
   }
   
   private String downloadnetworklistdoc()
   {
	  // request.setAttribute("list", moidlist);
	   
		Hashtable reporthash = new Hashtable();
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		String file = "temp/faworklist_report.doc";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		try {
			report.createReport_faworklistdoc(fileName,current_user);
			
			request.setAttribute("filename", fileName);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "/topology/network/downloadreport.jsp";
   }
   
   private String downloadnetworkpdf()
   {
	  // request.setAttribute("list", moidlist);
	   
		Hashtable reporthash = new Hashtable();
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		String file = "temp/faworklist_report.pdf";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		try {
			report.createReport_faworklistpdf(fileName,current_user);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/topology/network/downloadreport.jsp";
   }
   
   private String downloadnetworkpdfsearch()
   {
	  // request.setAttribute("list", moidlist);
	   
	   Hashtable reporthash = new Hashtable();
		 NodeMonitorDao nmdao = new NodeMonitorDao();
		 String ipaddress = (String)session.getAttribute("ipaddress");
			 List moidlist = nmdao.loadByNodeIp(ipaddress);
			  // request.setAttribute("ipaddress", ipaddress);
				
				reporthash.put("list", moidlist);
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		String file = "temp/faworklistsearch_report.pdf";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			report.createReport_faworklistsearchpdf(fileName);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/topology/network/downloadreport.jsp";
   }

    private String downloadnetworklistdocsearch()
   {
	  // request.setAttribute("list", moidlist);
	   
		Hashtable reporthash = new Hashtable();
		 NodeMonitorDao nmdao = new NodeMonitorDao();
		 String ipaddress = (String)session.getAttribute("ipaddress");
			 List moidlist = nmdao.loadByNodeIp(ipaddress);
			  // request.setAttribute("ipaddress", ipaddress);
				
				reporthash.put("list", moidlist);
		ExcelReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		String file = "temp/faworklistsearch_report.doc";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			report.createReport_faworklistsearchdoc(fileName);
			
			request.setAttribute("filename", fileName);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "/topology/network/downloadreport.jsp";
   }
   

   private String downloadnetworklistfucksearch()
   {
	  // request.setAttribute("list", moidlist);
	   String ipaddress = (String)session.getAttribute("ipaddress");
	   NodeMonitorDao nmdao = new NodeMonitorDao();
	   //String ipaddress = getParaValue("ipaddress");
	   List moidlist = nmdao.loadByNodeIp(ipaddress);
	  // request.setAttribute("ipaddress", ipaddress);
		Hashtable reporthash = new Hashtable();
		reporthash.put("list", moidlist);
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		
		report.createReport_faworklistsearch("/temp/faworklistsearch_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/topology/network/downloadreport.jsp";
   }
   
   public String execute(String action)
   {
	   if(action.equals("update"))
	      return update(); 
	   if(action.equals("updatemoid"))
		  return updatemoid(); 
	   if(action.equals("ready_editmoids"))
		   return readyeditmoids();
	   if(action.equals("showallmoids"))
		   return showallmoids();
	   if(action.equals("stopmoid"))
		   return stopmoid();
	   if(action.equals("startmoid"))
		   return startmoid();
	   if(action.equals("showeditmoids"))
		   return showeditmoids();
	   if(action.equals("allmoidlist"))
		   return allmoidlist();
	   if(action.equals("allmoidsearch"))
		   return allmoidsearch();
	   if(action.equals("downloadnetworklistfuck"))
		   return downloadnetworklistfuck();
	   
	   if(action.equals("downloadnetworklistdoc"))
		   return downloadnetworklistdoc();
	   
	   if(action.equals("downloadnetworkpdf"))
		   return downloadnetworkpdf();
	   if(action.equals("downloadnetworklistfucksearch"))
			  return downloadnetworklistfucksearch();
	   
	   if(action.equals("downloadnetworklistdocsearch"))
			  return downloadnetworklistdocsearch();
	   
	   if(action.equals("downloadnetworkpdfsearch"))
			  return downloadnetworkpdfsearch();
       setErrorCode(ErrorMessage.ACTION_NO_FOUND);
       return null;
   }
}
