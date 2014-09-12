/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.application.manage;

import java.util.*;
import java.text.*;

import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.common.base.*;
import com.afunms.common.*;
import com.afunms.common.util.*;
import com.afunms.config.dao.NodeToBusinessDao;
import com.afunms.config.model.NodeToBusiness;
import com.afunms.event.dao.EventListDao;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.loader.*;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.OracleEntity;
import com.afunms.application.model.Oraspaceconfig;
import com.afunms.system.model.User;
import com.afunms.topology.util.*;
import com.afunms.application.dao.*;
import com.afunms.application.util.DBPool;
import com.afunms.application.util.IpTranslation;
import com.afunms.topology.dao.DiscoverCompleteDao;
import com.afunms.polling.impl.*;
import com.afunms.polling.api.*;

import com.afunms.report.jfree.ChartCreator;

import org.jfree.data.general.DefaultPieDataset;

public class OraSpaceConfigManager extends BaseManager implements ManagerInterface {
	private String list() {
		List ips = new ArrayList();
		User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		String bids = operator.getBusinessids();
		DBVo vo = new DBVo();

		String bid[] = bids.split(",");
		Vector rbids = new Vector();
		if (bid != null && bid.length > 0) {
			for (int i = 0; i < bid.length; i++) {
				if (bid[i] != null && bid[i].trim().length() > 0)
					rbids.add(bid[i].trim());
			}
		}

		List oraList = new ArrayList();
		DBTypeDao typedao = new DBTypeDao();
		DBTypeVo typevo = null;
		try {
			typevo = (DBTypeVo) typedao.findByDbtype("oracle");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			typedao.close();
		}
		/* modif by zhao ------------- */
		DBDao dao = new DBDao();
		try {
			oraList = dao.getDbByType(typevo.getId());
			//dao.getdbby
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		// oracledao.findAll();
		/*-------------modify end-------------*/
		if (oraList != null && oraList.size() > 0) {
			for (int i = 0; i < oraList.size(); i++) {
				try {
					DBVo dbmonitorlist = (DBVo) oraList.get(i);
					ips.add(dbmonitorlist.getIpAddress() + ":" + dbmonitorlist.getDbName());
				} finally {
				}
			}
		}

		request.setAttribute("iplist", ips);
		OraspaceconfigDao configdao = new OraspaceconfigDao();
		setTarget("/application/db/oraspaceconfiglist.jsp");
		return list(configdao);
	}

	private String add() {
		DBVo vo = new DBVo();
		vo.setId(KeyGenerator.getInstance().getNextKey());
		vo.setUser(getParaValue("user"));
		vo.setPassword(getParaValue("password"));
		vo.setAlias(getParaValue("alias"));
		vo.setIpAddress(getParaValue("ip_address"));
		vo.setPort(getParaValue("port"));
		vo.setDbName(getParaValue("db_name"));
		vo.setCategory(getParaIntValue("category"));
		vo.setDbuse(getParaValue("dbuse"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		String allbid = "";
		String[] businessids = getParaArrayValue("checkbox");
		if (businessids != null && businessids.length > 0) {
			for (int i = 0; i < businessids.length; i++) {

				String bid = businessids[i];
				allbid = allbid + bid + ",";
			}
		}
		vo.setBid(allbid);
		vo.setManaged(getParaIntValue("managed"));
		vo.setDbtype(getParaIntValue("dbtype"));
		// 在数据库里增加被监控指标
		// DiscoverCompleteDao dcDao = new DiscoverCompleteDao();
		// dcDao.addDBMonitor(vo.getId(),vo.getIpAddress(),"mysql");

		// 在轮询线程中增加被监视节点
		// DBLoader loader = new DBLoader();
		// loader.loadOne(vo);
		// loader.close();

		DBDao dao = new DBDao();
		try {
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/db.do?action=list";
	}

	public String delete() {
		// String id = getParaValue("radio");
		// DBDao dao = new DBDao();
		// dao.delete(id);
		// int nodeId = Integer.parseInt(id);
		// PollingEngine.getInstance().deleteNodeByID(nodeId);
		// DBPool.getInstance().removeConnect(nodeId);
		//        
		return "/db.do?action=list";
	}

	private String update() {
		Oraspaceconfig vo = new Oraspaceconfig();
		DBVo dbvo = new DBVo();

		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress = getParaValue("ipaddress");
		System.out.println(ipaddress);
		vo.setId(getParaIntValue("id"));
		vo.setIpaddress(getParaValue("ipaddress"));
		vo.setSpacename(getParaValue("spacename"));
		vo.setLinkuse(getParaValue("linkuse"));
		vo.setAlarmvalue(getParaIntValue("alarmvalue"));
		vo.setBak(getParaValue("bak"));
		vo.setReportflag(getParaIntValue("reportflag"));
		vo.setSms(getParaIntValue("sms"));

		try {
			OraspaceconfigDao configdao = new OraspaceconfigDao();
			try {
				configdao.update(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}
			/*User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}

			DBDao dao = new DBDao();
			List dblist = new ArrayList();
			try {
				dblist = dao.getDbByTypeAndIpaddress(1, ipaddress);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if (dblist != null && dblist.size() > 0)
				dbvo = (DBVo) dblist.get(0);
			List oraList = new ArrayList();

			dao = new DBDao();
			try {
				oraList = dao.getDbByBID(dbvo.getBid());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

			if (oraList != null && oraList.size() > 0) {
				for (int i = 0; i < oraList.size(); i++) {
					DBVo dbmonitorlist = (DBVo) oraList.get(i);
					ips.add(dbmonitorlist.getIpAddress());
				}
			}

			configdao = new OraspaceconfigDao();
			try {
				configdao.fromLastToOraspaceconfig();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}
			if (ipaddress != null && ipaddress.trim().length() > 0) {
				configdao = new OraspaceconfigDao();
				try {
					list = configdao.getByIp(ipaddress);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
				if (list == null || list.size() == 0) {
					list = configdao.loadAll();
				}
			} else {
				configdao = new OraspaceconfigDao();
				try {
					list = configdao.loadAll();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			}

			if (list != null && list.size() > 0) {
				for (int k = 0; k < list.size(); k++) {
					Oraspaceconfig oraspaceconfig = (Oraspaceconfig) list.get(k);
					if (ips.contains(oraspaceconfig.getIpaddress()))
						conflist.add(oraspaceconfig);
				}
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}

		request.setAttribute("Oraspaceconfiglist", conflist);
		request.setAttribute("iplist", ips);
		request.setAttribute("ipaddress", ipaddress);
		//request.setAttribute("list", list);

		String showNodeList = getParaValue("showNodeList");
		System.out.println(showNodeList);
		if ("showNodeList".equals(showNodeList)) {
		    request.setAttribute("showNodeList", showNodeList);
		    return showNodeList();
		}
        
		return list();
	}

	private String cancelmanage() {
		// DBVo vo = new DBVo();
		// DBDao dao = new DBDao();
		// vo = (DBVo)dao.findByID(getParaValue("id"));
		// vo.setManaged(0);
		// dao = new DBDao();
		// dao.update(vo);
		return "/db.do?action=list";
	}

	private String createSpaceConfig() {
//		DBVo vo = new DBVo();

		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress = "";
		IpTranslation tranfer = new IpTranslation();
		try {
			ipaddress = getParaValue("ipaddress");// ipaddress
			User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}
			DBDao dao = new DBDao();
			List dblist = new ArrayList();
			try {
				dblist = dao.getDbByType(1);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			Hashtable oraHash = new Hashtable();

			OraspaceconfigDao configdao = new OraspaceconfigDao();
//			OraclePartsDao oracleDao = null;
			try {
				try{
					configdao.fromLastToOraspaceconfig();	
				}finally{
					if(configdao!=null)
						configdao.close();
				}
			    configdao=new OraspaceconfigDao();
				oraHash = configdao.getDistinctIp();
//				Set keys = oraHash.keySet();
				
				if (oraHash != null && oraHash.size() > 0 && dblist != null && dblist.size() > 0) {
					for (int i = 0; i < dblist.size(); i++) {
//						oracleDao = new OraclePartsDao();
						try{
							DBVo dbvo = (DBVo) dblist.get(i);		
//							List<OracleEntity> oracles = oracleDao.getOraclesByDbAndBid(dbvo.getId(),rbids);
							String ip1 = tranfer.formIpToHex(dbvo.getIpAddress());
							if (oraHash.containsKey(ip1 + ":" + dbvo.getId()))
								ips.add(dbvo.getIpAddress() + ":" + dbvo.getDbName());
						}catch(Exception e){
							e.printStackTrace();
						}finally{
//							oracleDao.close();
						}
						
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(configdao!=null)
					configdao.close();
				
			}

			ipaddress = (String) request.getAttribute("ipaddress");
			if (ipaddress != null && ipaddress.trim().length() > 0) {
				configdao = new OraspaceconfigDao();
				try {
					list = configdao.getByIp(ipaddress);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
				if (list == null || list.size() == 0) {
					try {
						list = configdao.loadAll();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						configdao.close();
					}
				}
			} else {
				configdao = new OraspaceconfigDao();
				try {
					list = configdao.loadAll();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			}

			if (list != null && list.size() > 0) {
				for (int k = 0; k < list.size(); k++) {
					Oraspaceconfig oraspaceconfig = (Oraspaceconfig) list.get(k);
					String ip2 = oraspaceconfig.getIpaddress();
					String[] iparr = ip2.split(":");
					String ip3 = iparr[0];
					String[] tip = tranfer.getIpFromHex(ip3);
					String mip = "";
					mip = tip[0] + "." + tip[1] + "." + tip[2] + "." + tip[3];

					if (ips.contains(mip + ":" + iparr[1]))
						conflist.add(oraspaceconfig);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("Oraspaceconfiglist", conflist);
		request.setAttribute("iplist", ips);
		
		String showNodeList = getParaValue("showNodeList");
		if ("showNodeList".equals(showNodeList)) {
		    return showNodeList();
		}
		
		OraspaceconfigDao configdao = new OraspaceconfigDao();
		setTarget("/application/db/oraspaceconfiglist.jsp");
		return list(configdao);
	}

	private String search() {
		List list = new ArrayList();
		List ips = new ArrayList();
		String ipaddress = "";
		ipaddress = getParaValue("ipaddress");
		IpTranslation tranfer = new IpTranslation();
		String[] ipaddr = ipaddress.split(":");
		DBDao dao = new DBDao();
		DBVo vo = null;
		try {
			List oracleList = dao.getDbByTypeAndIpaddress(1, ipaddr[0].trim());
			if (oracleList != null && oracleList.size() > 0) {
				vo = (DBVo) oracleList.get(0);
			}
		} finally {
			dao.close();
		}
		try {

			User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}
			dao = new DBDao();
			List dblist = new ArrayList();
			try {
				dblist = dao.getDbByType(1);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

			Hashtable oraHash = new Hashtable();
			OraspaceconfigDao configdao = new OraspaceconfigDao();
			try {
				configdao.fromLastToOraspaceconfig();
				if(configdao!=null)
					configdao.close();
				configdao=new OraspaceconfigDao();
				oraHash = configdao.getDistinctIp();
				Set keys = oraHash.keySet();
				if (oraHash != null && oraHash.size() > 0 && dblist != null && dblist.size() > 0) {
					for (int i = 0; i < dblist.size(); i++) {
						DBVo dbvo = (DBVo) dblist.get(i);
						if (oraHash.containsKey(dbvo.getIpAddress()))
							ips.add(dbvo.getIpAddress());
						String ip1 = tranfer.formIpToHex(dbvo.getIpAddress());
						if (oraHash.containsKey(ip1 + ":" + dbvo.getId()))
							ips.add(dbvo.getIpAddress() + ":" + dbvo.getDbName());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(configdao!=null)
			    	configdao.close();
			}

			if (ipaddress != null && ipaddress.trim().length() > 0) {
				configdao = new OraspaceconfigDao();
				try {
					String ip8 = tranfer.formIpToHex(ipaddr[0]);
					list = configdao.getByIp(ip8 + ":" + vo.getId());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
				if (list == null || list.size() == 0) {
					configdao = new OraspaceconfigDao();
					try {
						list = configdao.loadAll();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						configdao.close();
					}
				}
			} else {
				configdao = new OraspaceconfigDao();
				try {
					list = configdao.loadAll();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// reques

		// oracledao.get
		String hex = tranfer.formIpToHex(ipaddr[0]);
		request.setAttribute("iplist", ips);
		request.setAttribute("ipaddress", hex + ":" + vo.getId());// ipaddress
		System.out.println(hex + ":" + vo.getId() + "!!!!!!!!!!!!!!!!!!!!!");
		request.setAttribute("sid", ipaddr[1]);
		request.setAttribute("list", list);
		return "/application/db/oraspaceconfigsearchlist.jsp";
	}

	private String addalert() {
		Oraspaceconfig vo = new Oraspaceconfig();

		DBVo dbvo = new DBVo();

		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress = "";
		try {
			OraspaceconfigDao configdao = new OraspaceconfigDao();
			try {
				vo = (Oraspaceconfig) configdao.findByID(getParaValue("id"));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

			ipaddress = vo.getIpaddress();
			vo.setSms(1);

			configdao = new OraspaceconfigDao();
			try {
				configdao.update(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

			User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}
			IpTranslation tranfer = new IpTranslation();
			DBDao dao = new DBDao();

			List dblist = new ArrayList();
			/* ------modify by zhao ------------------------------------- */
			/*
			 * try{ dblist = dao.getDbByTypeAndIpaddress(1,
			 * tip[0]+"."+tip[1]+"."+tip[2]+"."+tip[3]); }catch(Exception e){
			 * e.printStackTrace(); }finally{ dao.close(); }
			 * 
			 * if(dblist != null && dblist.size()>0) dbvo = (DBVo)dblist.get(0);
			 * List oraList = new ArrayList(); dao = new DBDao(); try{ oraList =
			 * dao.getDbByBID(dbvo.getBid()); }catch(Exception e){
			 * e.printStackTrace(); }finally{ dao.close(); }
			 */
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByDbtype("oracle");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}
			List oraList = new ArrayList();
			DBDao bdao = new DBDao();
			try {
				oraList = bdao.getDbByType(typevo.getId());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
				bdao.close();
			}
			/*---------------------------------------*/
			/*
			 * if(oraList != null && oraList.size()>0){ for(int i=0;i<oraList.size();i++){
			 * DBVo dbmonitorlist = (DBVo)oraList.get(i);
			 * ips.add(dbmonitorlist.getIpAddress()); } }
			 */
			
			try {
				if (oraList != null && oraList.size() > 0) {
					for (int i = 0; i < oraList.size(); i++) {
						try{
							DBVo dbmonitorlist = (DBVo) oraList.get(i);
						
							ips.add(dbmonitorlist.getIpAddress() + ":" + dbmonitorlist.getDbName());
						}finally{
						}
						
					}
				}
			} finally {
				//oracledao.close();
			}

			configdao = new OraspaceconfigDao();
			try {
				configdao.fromLastToOraspaceconfig();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

			if (ipaddress != null && ipaddress.trim().length() > 0) {
				configdao = new OraspaceconfigDao();
				try {
					list = configdao.getByIp(ipaddress);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
				if (list == null || list.size() == 0) {
					configdao = new OraspaceconfigDao();
					try {
						list = configdao.loadAll();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						configdao.close();
					}
				}
			} else {
				configdao = new OraspaceconfigDao();
				try {
					list = configdao.loadAll();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			}

			if (list != null && list.size() > 0) {
				for (int k = 0; k < list.size(); k++) {
					Oraspaceconfig oraspaceconfig = (Oraspaceconfig) list.get(k);
					String[] iparr = oraspaceconfig.getIpaddress().split(":");
					String sid = iparr[1];
					String[] tip = tranfer.getIpFromHex(iparr[0]);
					if (ips.contains(tip[0] + "." + tip[1] + "." + tip[2] + "." + tip[3] + ":" + sid))
						conflist.add(oraspaceconfig);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("Oraspaceconfiglist", conflist);
		request.setAttribute("iplist", ips);
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("list", list);
		
		String showNodeList = getParaValue("showNodeList");
        if ("showNodeList".equals(showNodeList)) {
            return showNodeList();
        }
		return "/application/db/oraspaceconfigsearchlist.jsp";
	}

	private String cancelalert() {
		Oraspaceconfig vo = new Oraspaceconfig();

		DBVo dbvo = new DBVo();

		List list = new ArrayList();
		List conflist = new ArrayList();
		List ips = new ArrayList();
		String ipaddress = "";
		try {
			OraspaceconfigDao configdao = new OraspaceconfigDao();
			try {
				vo = (Oraspaceconfig) configdao.findByID(getParaValue("id"));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

			ipaddress = vo.getIpaddress();
			vo.setSms(0);

			configdao = new OraspaceconfigDao();
			try {
				configdao.update(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

			User operator = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			String bids = operator.getBusinessids();
			String bid[] = bids.split(",");
			Vector rbids = new Vector();
			if (bid != null && bid.length > 0) {
				for (int i = 0; i < bid.length; i++) {
					if (bid[i] != null && bid[i].trim().length() > 0)
						rbids.add(bid[i].trim());
				}
			}
			IpTranslation tranfer = new IpTranslation();
			DBDao dao = new DBDao();

			List dblist = new ArrayList();
			/* ------modify by zhao ------------------------------------- */
			/*
			 * try{ dblist = dao.getDbByTypeAndIpaddress(1,
			 * tip[0]+"."+tip[1]+"."+tip[2]+"."+tip[3]); }catch(Exception e){
			 * e.printStackTrace(); }finally{ dao.close(); }
			 * 
			 * if(dblist != null && dblist.size()>0) dbvo = (DBVo)dblist.get(0);
			 * List oraList = new ArrayList(); dao = new DBDao(); try{ oraList =
			 * dao.getDbByBID(dbvo.getBid()); }catch(Exception e){
			 * e.printStackTrace(); }finally{ dao.close(); }
			 */
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByDbtype("oracle");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}
			List oraList = new ArrayList();
			DBDao bdao = new DBDao();
			try {
				oraList = bdao.getDbByType(typevo.getId());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
				bdao.close();
			}
			/*---------------------------------------*/
			/*
			 * if(oraList != null && oraList.size()>0){ for(int i=0;i<oraList.size();i++){
			 * DBVo dbmonitorlist = (DBVo)oraList.get(i);
			 * ips.add(dbmonitorlist.getIpAddress()); } }
			 */
			
			try {
				if (oraList != null && oraList.size() > 0) {
					for (int i = 0; i < oraList.size(); i++) {
						try{
							DBVo dbmonitorlist = (DBVo) oraList.get(i);
							ips.add(dbmonitorlist.getIpAddress() + ":" + dbmonitorlist.getDbName());
						}catch(Exception e){
							e.printStackTrace();
						}finally{
						}
						
					}
				}
			} finally {
				//oracledao.close();
			}

			configdao = new OraspaceconfigDao();
			try {
				configdao.fromLastToOraspaceconfig();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}

			if (ipaddress != null && ipaddress.trim().length() > 0) {
				configdao = new OraspaceconfigDao();
				try {
					list = configdao.getByIp(ipaddress);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
				if (list == null || list.size() == 0) {
					configdao = new OraspaceconfigDao();
					try {
						list = configdao.loadAll();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						configdao.close();
					}
				}
			} else {
				configdao = new OraspaceconfigDao();
				try {
					list = configdao.loadAll();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			}

			if (list != null && list.size() > 0) {
				for (int k = 0; k < list.size(); k++) {
					Oraspaceconfig oraspaceconfig = (Oraspaceconfig) list.get(k);
					String[] iparr = oraspaceconfig.getIpaddress().split(":");
					String sid = iparr[1];
					String[] tip = tranfer.getIpFromHex(iparr[0]);
					if (ips.contains(tip[0] + "." + tip[1] + "." + tip[2] + "." + tip[3] + ":" + sid))
						conflist.add(oraspaceconfig);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("Oraspaceconfiglist", conflist);
		request.setAttribute("iplist", ips);
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("list", list);
		
		String showNodeList = getParaValue("showNodeList");
        if ("showNodeList".equals(showNodeList)) {
            return showNodeList();
        }
		return "/application/db/oraspaceconfigsearchlist.jsp";
	}

	/**
	 * @author hukelei add for
	 * @since 2010-01-21
	 * @return
	 */
	private String ready_edit() {
		String jsp = "/application/db/oraspaceconfigedit.jsp";
		OraspaceconfigDao dao = new OraspaceconfigDao();
		try {
			setTarget(jsp);
			jsp = readyEdit(dao);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		Oraspaceconfig config=(Oraspaceconfig)request.getAttribute("vo");
		DBDao oracledao=new DBDao();
		try{
			String ip1=config.getIpaddress();
			String[]ips=ip1.split(":");
			DBVo entity=(DBVo)oracledao.findByID(ips[1]);
			request.setAttribute("sid",entity.getDbName());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			oracledao.close();
		}
		String showNodeList = getParaValue("showNodeList");
		request.setAttribute("showNodeList", showNodeList);
		return jsp;
	}

	/**
	 * showNodeList:
	 * <p>获取单个库的表空间阀值设置
	 *
	 * @return {@link String}
	 *         - 返回单个库的表空间阀值设备
	 *
	 * @since   v1.01
	 */
	private String showNodeList() {
	    String ipaddress = getParaValue("ipaddress");
	    String nodeid = getParaValue("nodeid");
	    String showNodeList = getParaValue("showNodeList");
	    String ipaddressHEX = "";
	    if (!ipaddress.contains(":")) {
	        IpTranslation tranfer = new IpTranslation();
	        ipaddressHEX = tranfer.formIpToHex(ipaddress) + ":" + nodeid;
	    } else {
	        ipaddressHEX = ipaddress;
	    }
	    OraspaceconfigDao configdao = new OraspaceconfigDao();
	    List list = null;
        try {
            list = configdao.getByIp(ipaddressHEX);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            configdao.close();
        }

	    request.setAttribute("ipaddress", ipaddressHEX);// ipaddress
        request.setAttribute("sid", nodeid);
        request.setAttribute("list", list);
        System.out.println(showNodeList);
        request.setAttribute("showNodeList", showNodeList);
	    return "/application/db/oraspaceconfignodelist.jsp";
	}

	public String execute(String action) {
		if (action.equals("list"))
			return list();
		if (action.equals("ready_add"))
			return "/application/db/add.jsp";
		if (action.equals("add"))
			return add();
		if (action.equals("delete"))
			return delete();
		if (action.equals("ready_edit"))
			return ready_edit();
		if (action.equals("update"))
			return update();
		if (action.equals("cancelmanage"))
			return cancelmanage();
		if (action.equals("addalert"))
			return addalert();
		if (action.equals("cancelalert"))
			return cancelalert();
		if (action.equals("search"))
			return search();
		if (action.equals("createspaceconfig"))
			return createSpaceConfig();
		if ("showNodeList".equals(action)) {
		    return showNodeList();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

}