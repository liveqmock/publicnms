/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.config.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.Portconfig;
import com.afunms.initialize.PortConfigCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.snmp.interfaces.SnmpSet;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.portscan.dao.PortScanDao;
import com.afunms.system.model.User;
import com.afunms.temp.dao.InterfaceTempDao;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.dao.HostInterfaceDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.InterfaceNode;
import com.afunms.polling.node.IfEntity;

/**
 * 增加一个节点，要在server.jsp增加一个节点;
 * 删除或更新一个节点信息，在这则不直接操作server.jsp,而是让任务updateXml来更新xml.
 */
public class PortconfigManager extends BaseManager implements ManagerInterface {
	private String ipaddress;
	private String list() {
		PortconfigDao dao = new PortconfigDao();
		List ips = null;
		try {
			ips = dao.getIps();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (ipaddress == null && ips.size() > 0) {
			ipaddress = ips.get(0).toString();
			session.setAttribute("ipaddress", ipaddress);
		}
		request.setAttribute("ips", ips);
		dao = new PortconfigDao();
		setTarget("/config/portconfig/list.jsp");
		return list(dao, " where ipaddress = '" + ipaddress + "'");
	}

	private String empty() {
		PortconfigDao dao = new PortconfigDao();
		dao.empty();
		dao = new PortconfigDao();
		List ips = dao.getIps();
		request.setAttribute("ips", ips);
		dao = new PortconfigDao();
		setTarget("/config/portconfig/list.jsp");
		return list(dao);
	}

	private String monitornodelist() {
		PortconfigDao dao = new PortconfigDao();
		setTarget("/config/portconfig/portconfiglist.jsp");
		return list(dao, " where managed=1");
	}

	private String fromlasttoconfig() {
		PortconfigDao dao = new PortconfigDao();
		fromLastToPortconfig();
		List ips = new ArrayList();
		try {
			ips = dao.getIps();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("ips", ips);
		dao = new PortconfigDao();
		setTarget("/config/portconfig/list.jsp");
		return list(dao, " where ipaddress = '" + ipaddress + "'");
	}

	/**
	 * 
	 * @author gaoguangfei
	 * @date 2011-1-26 下午5:03:00
	 * @param
	 * @return String
	 * @Description: TODO(单个设备端口刷新)
	 */
	private String fromNodeLasttoconfig() {
		String ipaddress = getParaValue("ipaddress");
		String id = getParaValue("id");

		fromLastToPortconfig();// 先添加数据
		// System.out.println("ipaddress="+ipaddress+id);
		PortconfigDao dao = new PortconfigDao();
		request.setAttribute("id", id);
		List list = new ArrayList();
		try {
			list = dao.loadByIpaddress(ipaddress);
		} catch (Exception e) {

		} finally {
			dao.close();
		}
		request.setAttribute("list", list);
		request.setAttribute("ipaddress", ipaddress);
		return "//config/portconfig/nodeportlist.jsp";
	}

	private String showPortStatus() {
		Date d = new Date();
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		//String startdate = getParaValue("startdate");
		String index=getParaValue("index");
		
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = todate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip=getParaValue("ip");
		String newip = SysUtil.doip(ip);
		PortScanDao dao=new PortScanDao();
		
		String hourdata=dao.getHourData(newip,index,starttime,totime);
		 
//		Hashtable statusHash=dao.getPortStatusByIp(newip, starttime, totime);
//		// 画图----------------------
//		String timeType = "minute";
//		PollMonitorManager pollMonitorManager = new PollMonitorManager();
//		pollMonitorManager.chooseDrawLineType(timeType,
//				statusHash, "端口状态", newip + "portstatus",
//				740, 250);
		request.setAttribute("ip", ip);
		request.setAttribute("index", index);
		request.setAttribute("newip", newip);
	//	request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("hourData", hourdata);
		return "/config/portconfig/showPortStatus.jsp";
	}

	private String readyEdit() {
		PortconfigDao dao = new PortconfigDao();
		Portconfig vo = new Portconfig();
		try {
			vo = dao.loadPortconfig(getParaIntValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("vo", vo);
		return "/config/portconfig/edit.jsp";
	}

	private String update() {
		Portconfig vo = new Portconfig();
		int id = getParaIntValue("id");
		int sms = getParaIntValue("sms");
		int reportflag = getParaIntValue("reportflag");

		PortconfigDao dao = null;

		dao = new PortconfigDao();
		try {
			vo = dao.loadPortconfig(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		// String linkuse = getParaValue("linkuse");

		// vo.setLinkuse(linkuse);
		if (sms > -1)
			vo.setSms(sms);
		if (reportflag > -1)
			vo.setReportflag(reportflag);
		// String inportalarm = getParaValue("inportalarm");
		// vo.setInportalarm(inportalarm);
		// String outportalarm = getParaValue("outportalarm");
		// vo.setOutportalarm(outportalarm);
		dao = new PortconfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		dao = new PortconfigDao();
		List ips = null;
		try {
			ips = dao.getIps();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("ips", ips);
		// dao = new PortconfigDao();
		return "/portconfig.do?action=list";
	}

	private String updatenodeport() {
		Portconfig vo = new Portconfig();
		int id = getParaIntValue("id");
		int sms = getParaIntValue("sms");
		int reportflag = getParaIntValue("reportflag");
		String sflag = getParaValue("sflag");
		String jp = getParaValue("jp");

		PortconfigDao dao = null;

		dao = new PortconfigDao();
		try {
			vo = dao.loadPortconfig(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		if (sms > -1)
			vo.setSms(sms);
		if (reportflag > -1)
			vo.setReportflag(reportflag);
		// String inportalarm = getParaValue("inportalarm");
		// vo.setInportalarm(inportalarm);
		// String outportalarm = getParaValue("outportalarm");
		// vo.setOutportalarm(outportalarm);
		dao = new PortconfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		// dao = new PortconfigDao();
		if (sflag != null && "1".equalsIgnoreCase(sflag)) {
			return "/portconfig.do?action=list&flag=0&jp=" + jp;
		} else
			return "/portconfig.do?action=nodeportlist&ipaddress="
					+ vo.getIpaddress();
	}

	private String updateport() {
		Portconfig vo = new Portconfig();
		String id = getParaValue("id");
		PortconfigDao portconfigDao = new PortconfigDao();
		try {
			vo = portconfigDao.loadPortconfig(Integer.parseInt(id));
		} catch (RuntimeException e1) {
			e1.printStackTrace();
		} finally {
			portconfigDao.close();
		}
		String linkuse = getParaValue("linkuse");
		if (linkuse != null) {
			vo.setLinkuse(linkuse);
		}
		int sms = getParaIntValue("sms");
		int reportflag = getParaIntValue("reportflag");
		if (sms > -1) {
			vo.setSms(sms);
		}
		if (reportflag > -1) {
			vo.setReportflag(reportflag);
		}
		String inportalarm = getParaValue("inportalarm");
		if (inportalarm != null) {
			vo.setInportalarm(inportalarm);
		}
		String outportalarm = getParaValue("outportalarm");
		if (outportalarm != null) {
			vo.setOutportalarm(outportalarm);
		}
		PortconfigDao dao = new PortconfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		// dao = new PortconfigDao();
		// List ips = dao.getIps();
		//		
		// try{
		// //request.setAttribute("ips", ips);
		// }catch(Exception e){
		// e.printStackTrace();
		// }finally{
		// dao.close();
		// }
		return "/portconfig.do?action=list";
	}

	private String updateselect() {
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
		String inportalarm = getParaValue("inportalarm");
		vo.setInportalarm(inportalarm);
		String outportalarm = getParaValue("outportalarm");
		vo.setOutportalarm(outportalarm);
		dao = new PortconfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dao = new PortconfigDao();
		setTarget("/config/portconfig/list.jsp");
		return list(dao, " where " + key + " = '" + value + "'");
	}

	private String find() {
		String ipaddress = getParaValue("ipaddress");
		PortconfigDao dao = new PortconfigDao();
		session.setAttribute("ipaddress", ipaddress);
		List ips = dao.getIps();
		request.setAttribute("ips", ips);
		dao = new PortconfigDao();
		setTarget("/config/portconfig/list.jsp");
		return list(dao, " where ipaddress = '" + ipaddress + "'");
	}

	private String nodeportlist() {
		String ipaddress = getParaValue("ipaddress");
		String id = getParaValue("id");
		PortconfigDao dao = new PortconfigDao();
		request.setAttribute("id", id);
		List list = new ArrayList();
		try {
			list = dao.loadByIpaddress(ipaddress);
		} catch (Exception e) {

		} finally {
			dao.close();
		}
		request.setAttribute("list", list);
		request.setAttribute("ipaddress", ipaddress);
		return "//config/portconfig/nodeportlist.jsp";
	}

	public void fromLastToPortconfig() {
		String runmodel = PollingEngine.getCollectwebflag(); 
//		SysLogger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
//		SysLogger.info("&&&&&&&&   开始刷新内存端口   &&&"+runmodel);
//		SysLogger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		List list1 = new ArrayList();
		Hashtable porthash = new Hashtable();
		Hashtable porthash_topo = new Hashtable();
		Portconfig portconfig = null;
		InterfaceNode interfaceNode = null;
//		Vector configV = new Vector();
//		Hashtable hashSpeed = new Hashtable();
		try {
			// 从内存中得到所有端口的采集信息
//			Hashtable sharedata = ShareData.getSharedata();
			// 从数据库得到监视IP列表
//			HostNodeDao hostnodedao = new HostNodeDao();
//			shareList = hostnodedao.loadMonitorNet();
			User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			StringBuffer s = new StringBuffer();
//			int _flag = 0;
//			String st = "";
			if (current_user.getBusinessids() != null) {
				if (current_user.getBusinessids() != "-1") {
					String[] bids = current_user.getBusinessids().split(",");
					// System.out.println("------------aaa-----------"+current_user.getBusinessids());
					if (bids.length > 0) {
						for (int i = 0; i < bids.length; i++) {
							if (bids[i].trim().length() > 0) {
								s.append(" bid like '%").append(bids[i])
										.append("%' ");
								if (i != bids.length - 1)
									s.append(" or ");
							}
						}
					}

				}
			}
			Hashtable interfaceHash = new Hashtable();
			
			
			
			if("1".equals(runmodel)){
				//采集与访问是分离模式 
				InterfaceTempDao interfaceDao = new InterfaceTempDao();
				List interfaceList = new ArrayList();
				try {
					interfaceList = interfaceDao.findByCondition(" where (subentity = 'ifDescr' or subentity = 'ifSpeed') and nodeid in (select id from topo_host_node where "
									+ s.toString() + ") ");
				} catch (Exception e) {
					SysLogger.error("error8", e);
					e.printStackTrace();
				} finally {
					interfaceDao.close();
				}
				if (interfaceList != null && interfaceList.size() > 0) {
					for (int k = 0; k < interfaceList.size(); k++) {
						NodeTemp vo = (NodeTemp) interfaceList.get(k);
						if (interfaceHash.containsKey(vo.getIp() + ":" + vo.getSindex())) {
							Hashtable _porthash = (Hashtable) interfaceHash.get(vo.getIp() + ":" + vo.getSindex());
							if ("ifDescr".equalsIgnoreCase(vo.getSubentity())) {
								_porthash.put("ifDescr", vo.getThevalue());
							}
							if ("ifSpeed".equalsIgnoreCase(vo.getSubentity())) {
								_porthash.put("ifSpeed", vo.getThevalue());
							}
							interfaceHash.put(vo.getIp() + ":" + vo.getSindex(),_porthash);
						} else {

							Hashtable _porthash = new Hashtable();
							if ("ifDescr".equalsIgnoreCase(vo.getSubentity())) {
								_porthash.put("ifDescr", vo.getThevalue());
							}
							if ("ifSpeed".equalsIgnoreCase(vo.getSubentity())) {
								_porthash.put("ifSpeed", vo.getThevalue());
							}
							interfaceHash.put(vo.getIp() + ":" + vo.getSindex(),_porthash);
						}

					}
				}
			}else{
				//采集与访问是集成模式 
				//获取该设备在内存中的所有信息，如果未空 则返回
				Hashtable sharedata = ShareData.getSharedata();
				Enumeration portEnu = sharedata.keys();
					while (portEnu.hasMoreElements()) {
						// 判断采集到的端口信息是否已经在端口配置表里已经存在，若不存在则加入
						String ipaddress = (String) portEnu.nextElement(); // portstr==>ip:index
						Hashtable ipdata = (Hashtable)sharedata.get(ipaddress);
						if(ipdata != null){
							Vector vector = (Vector)ipdata.get("interface");
							if (vector !=null && vector.size()>0){
								for(int k=0;k<vector.size();k++){
									Interfacecollectdata vo = (Interfacecollectdata)vector.get(k);
									if (interfaceHash.containsKey(ipaddress + ":" + vo.getSubentity())) {
										Hashtable _porthash = (Hashtable) interfaceHash.get(ipaddress + ":" + vo.getSubentity());
										if ("ifDescr".equalsIgnoreCase(vo.getEntity())) {
											_porthash.put("ifDescr", vo.getThevalue());
										}
										if ("ifSpeed".equalsIgnoreCase(vo.getEntity())) {
											_porthash.put("ifSpeed", vo.getThevalue());
										}
										//SysLogger.info(ipaddress + ":" + vo.getSubentity()+"============");
										interfaceHash.put(ipaddress + ":" + vo.getSubentity(),_porthash);
									} else {

										Hashtable _porthash = new Hashtable();
										if ("ifDescr".equalsIgnoreCase(vo.getEntity())) {
											_porthash.put("ifDescr", vo.getThevalue());
										}
										if ("ifSpeed".equalsIgnoreCase(vo.getEntity())) {
											_porthash.put("ifSpeed", vo.getThevalue());
										}
										//SysLogger.info(ipaddress + ":" + vo.getSubentity()+"#############");
										interfaceHash.put(ipaddress + ":" + vo.getSubentity(),_porthash);
									}
								}
							}
						}
						
					}
				
				
				

			}
			
					
			


			// 从端口配置表里获取列表
			// Query query1=session.createQuery("from Portconfig portconfig
			// order by portconfig.ipaddress,portconfig.portindex");
			PortconfigDao portconfigdao = new PortconfigDao();
			try {
				list1 = portconfigdao.loadAll();
			} catch (Exception e) {
				SysLogger.error("error7", e);
				e.printStackTrace();
			} finally {
				portconfigdao.close();
			}
			if (list1 != null && list1.size() > 0) {
				for (int i = 0; i < list1.size(); i++) {
					portconfig = (Portconfig) list1.get(i);
					porthash.put(portconfig.getIpaddress() + ":" + portconfig.getPortindex(), portconfig);
				}
			}
//			拓扑图链路端口
			HostInterfaceDao hostInterfaceDao = new HostInterfaceDao();
			int id=1000;
			try {
				id = hostInterfaceDao.getNextID();
			} catch (Exception e1) {
				SysLogger.error("error10", e1);
				e1.printStackTrace();
			} 
			try {
				hostInterfaceDao = new HostInterfaceDao();
				list1 = new ArrayList();
				list1 = hostInterfaceDao.loadAll();
			} catch (Exception e) {
				SysLogger.error("error6", e);
				e.printStackTrace();
			} finally {
				hostInterfaceDao.close();
			}
			try {
				if (list1 != null && list1.size() > 0) {
					for (int i = 0; i < list1.size(); i++) {
						interfaceNode = (InterfaceNode) list1.get(i);
						com.afunms.polling.base.Node node = PollingEngine.getInstance().getNodeByID(interfaceNode.getNode_id());
						porthash_topo.put(node.getIpAddress() + ":" + interfaceNode.getEntity(), interfaceNode);
					}
				}
			} catch (Exception e1) {
				SysLogger.error(e1.getMessage());
				e1.printStackTrace();
			}
			
			Enumeration portEnu = interfaceHash.keys();
			DBManager dbmanager = new DBManager();
			try {
				while (portEnu.hasMoreElements()) {
					// 判断采集到的端口信息是否已经在端口配置表里已经存在，若不存在则加入
					String portstr = (String) portEnu.nextElement(); // portstr==>ip:index
					Hashtable p = (Hashtable) interfaceHash.get(portstr);

					try {
						if (!porthash.containsKey(portstr)) {
							// 若不存在,则添加
							String[] portindex = portstr.split(":");
							portconfig = new Portconfig();
							portconfig.setBak("");
							portconfig.setIpaddress(portindex[0]);
							portconfig.setLinkuse("");
							portconfig.setName((String) p.get("ifDescr"));
							portconfig.setPortindex(Integer.parseInt(portindex[1]));
							portconfig.setSms(new Integer(0));// 0：不发送短信
																// 1：发送短信，默认的情况是不发送短信
							portconfig.setReportflag(new Integer(0));// 0：不存在于报表
																		// 1：存在于报表，默认的情况是不存在于报表
							portconfig.setInportalarm("2000");// 默认入口流速阀值
							portconfig.setOutportalarm("2000");// 默认出口流速阀值
							portconfig.setAlarmlevel("1");
							portconfig.setFlag("1");
							StringBuffer sql = new StringBuffer(100);
							sql
									.append("insert into system_portconfig(ipaddress,name,portindex,linkuse,sms,bak,reportflag,inportalarm,outportalarm,speed,alarmlevel,flag)values(");
							sql.append("'");
							sql.append(portconfig.getIpaddress());
							sql.append("','");
							sql.append(portconfig.getName());
							sql.append("',");
							sql.append(portconfig.getPortindex());
							sql.append(",'");
							sql.append(portconfig.getLinkuse());
							sql.append("',");
							sql.append(portconfig.getSms());
							sql.append(",'");
							sql.append(portconfig.getBak());
							sql.append("',");
							sql.append(portconfig.getReportflag());
							sql.append(",'");
							sql.append(portconfig.getInportalarm());
							sql.append("','");
							sql.append(portconfig.getOutportalarm());
							sql.append("','");
							sql.append((String) p.get("ifSpeed"));
							sql.append("','");
							sql.append(portconfig.getAlarmlevel());
							sql.append("','");
							sql.append(portconfig.getFlag());
							sql.append("')");
							try {
								dbmanager.addBatch(sql.toString());
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					} catch (Exception e) {
						SysLogger.error("error5", e);
						e.printStackTrace();
					}
					//拓扑图链路端口同步
					try {
						if (!porthash_topo.containsKey(portstr)) {
							// 若不存在,则添加
							String[] portindex = portstr.split(":");
							portconfig = new Portconfig();
							portconfig.setIpaddress(portindex[0]);
							portconfig.setPortindex(Integer.parseInt(portindex[1]));
							com.afunms.polling.base.Node node = PollingEngine.getInstance().getNodeByIP(portindex[0]);
							StringBuffer sql = new StringBuffer(100);
							sql.append("insert into topo_interface(id,node_id,entity,descr,port,speed,phys_address,ip_address)values(");
							sql.append(id++);
							sql.append(",");
							sql.append(node.getId());
							sql.append(",'");
							sql.append(portconfig.getPortindex());
							sql.append("','");
							sql.append((String) p.get("ifDescr"));
							sql.append("','");
							sql.append("");
							sql.append("','");
							sql.append(2000);
							sql.append("','");
							sql.append("");
							sql.append("','");
							sql.append("')");
							try {
								SysLogger.info(sql.toString());
								dbmanager.addBatch(sql.toString());
							} catch (Exception ex) {
								SysLogger.info(ex.getMessage());
								ex.printStackTrace();
							}
						}
					} catch (Exception e) {
						SysLogger.error("error4", e);
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				SysLogger.error("error", e);
                e.printStackTrace();
			} finally {
				try {
					dbmanager.executeBatch();
				} catch (Exception e) {
					SysLogger.error("error3", e);
					e.printStackTrace();
				} finally {
					dbmanager.close();
				}
			}
			PortconfigDao configdao = new PortconfigDao();
			try{
				configdao.RefreshPortconfigs();
			}catch(Exception e){
				SysLogger.error("error2", e);
				e.printStackTrace();
			}finally{
				configdao.close();
			}
//			HostInterfaceDao hinterfaceDao = new HostInterfaceDao();
//			try{
//				hinterfaceDao.RefreshPortconfigs();
//			}catch(Exception e){
//				e.printStackTrace();
//			}finally{
//				hinterfaceDao.close();
//			}
		} catch (Exception e) {
			SysLogger.error("error1", e);
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
	}
	
	public String upordownPort() {
		String ip = getParaValue("ip");
		String oid = ".1.3.6.1.2.1.2.2.1.8" +"."+ getParaValue("index");
		String writeCommunity = getParaValue("writecommunity");
		int port_status = getParaIntValue("portflag");
		SnmpSet setPort=new SnmpSet(ip,writeCommunity,oid,port_status);
		setPort.snmpSetPort();
		return "/perform.do?action=monitornodelist";
	}
	public String updateportflag(){
		System.out.println("进入flag方法");
		String tempport = request.getParameter("portArray");
		tempport=tempport.substring(0, tempport.length()-1);
		String[] portArray=tempport.split(",");
		String ipaddress=request.getParameter("ipaddress");
		//更新数据库
		PortconfigDao dao=new PortconfigDao();
		try{
			dao.updateportflag(ipaddress, portArray);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		//更新内存
		Hashtable ht=PortConfigCenter.getInstance().getPortHastable();
		ArrayList list=new ArrayList();
		for(int i=0;i<portArray.length;i++){
			list.add("*"+portArray[i]+":1");
		}
		if(ht.containsKey(ipaddress)){	
			ht.remove(ipaddress);
			ht.put(ipaddress, list);
		}else{
			ht.put(ipaddress, list);
		}
		return list();
	}
	
	public String execute(String action) {
		Object obj = session.getAttribute("ipaddress");
		if (obj != null) {
			ipaddress = obj.toString();
		}
		if (action.equals("list"))
			return list();
		if (action.equals("monitornodelist"))
			return monitornodelist();
		if (action.equals("fromlasttoconfig"))
			return fromlasttoconfig();
		if (action.equals("fromnodelasttoconfig"))
			return fromNodeLasttoconfig();
		if (action.equals("showedit"))
			return readyEdit();
		if (action.equals("update"))
			return update();
		if (action.equals("updatenodeport")) {
			return updatenodeport();
		}
		if (action.equals("updateport"))
			return updateport();
		if (action.equals("find"))
			return find();
		if (action.equals("nodeportlist")) {
			return nodeportlist();
		}
		if (action.equals("updateselect"))
			return updateselect();
		if (action.equals("empty"))
			return empty();
		if (action.equals("ready_add"))
			return "/config/portconfig/add.jsp";
		if (action.equals("delete")) {
			DaoInterface dao = new PortconfigDao();
			setTarget("/portconfig.do?action=list");
			return delete(dao);
		}
		if (action.equals("showPortStatus")) {
			return showPortStatus();
		}
		if (action.equals("upordownPort"))return upordownPort();
		
		if(action.equals("updateportflag")){
			return  updateportflag();
		}
		
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}
