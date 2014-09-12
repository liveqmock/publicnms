/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.event.manage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.dao.NodeToBusinessDao;
import com.afunms.config.model.Business;
import com.afunms.config.model.NodeToBusiness;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.EventReportDao;
import com.afunms.event.model.EventList;
import com.afunms.event.model.EventReport;
import com.afunms.event.service.AlarmSummarize;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.loader.HostLoader;
import com.afunms.polling.node.Host;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.util.TopoHelper;
import com.afunms.topology.util.XmlOperator;

public class EventManager extends BaseManager implements ManagerInterface {
	
	private String todayList(){
		EventListDao dao = new EventListDao();
		try{
			List list = dao.loadByWhere(getSQL());
			request.setAttribute("list", list);
		}catch(Exception e){
		}finally{
			dao.close();
		}
		
		
		return "/alarm/event/todaylist.jsp";
	}
	private String getSQL(){
		int status = 99;
		int level1 = 99;
		int bid = 0;

		String subtype = "";
		String b_time = "";
		String t_time = "";
		status = getParaIntValue("status");
		level1 = getParaIntValue("level1");

		if (status == -1)
			status = 99;
		if (level1 == -1)
			level1 = 99;

		request.setAttribute("status", status);
		request.setAttribute("level1", level1);
		bid = getParaIntValue("businessid");
		request.setAttribute("businessid", bid);
		BusinessDao bdao = new BusinessDao();
		List businesslist = bdao.loadAll();
		request.setAttribute("businesslist", businesslist);
		b_time = getParaValue("startdate");
		t_time = getParaValue("todate");
		subtype = getParaValue("subtype");
		if (b_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		String starttime1 = b_time + " 00:00:00";
		String totime1 = t_time + " 23:59:59";
		String sql = "";
		try {
			User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
			StringBuffer s = new StringBuffer();
			s.append("where recordtime>= '" + starttime1 + "' "
					+ "and recordtime<='" + totime1 + "'");
			if (!"99".equals(level1 + "")) {
				s.append(" and level1=" + level1);
			}

			if (subtype != null && !"value".equals(subtype)) {
				s.append(" and subtype='" + subtype + "'");
			}
			if (!"99".equals(status + "")) {
				s.append(" and managesign=" + status);
			}
			String businessid = vo.getBusinessids();
			int flag = 0;
			if (bid > 0) {
				s.append(" and businessid like '%," + bid + ",%'");
			}

			sql = s.toString();
			sql = sql + " order by id desc";

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return sql;
	}
	private String list() {
		
		EventListDao dao = new EventListDao();
		setTarget("/alarm/event/list.jsp");
        return list(dao,getSQL());
	}

	private String equipmentlist() {
		List endList = new ArrayList();
		int status = 99;
		int level1 = 99;
		int bid = 0;
		String subtype = "";
		String b_time = "";
		String t_time = "";
		status = getParaIntValue("status");
		level1 = getParaIntValue("level1");

		if (status == -1)
			status = 99;
		if (level1 == -1)
			level1 = 99;

		request.setAttribute("status", status);
		request.setAttribute("level1", level1);
		bid = getParaIntValue("businessid");
		request.setAttribute("businessid", bid);
		BusinessDao bdao = new BusinessDao();
		List businesslist = bdao.loadAll();
		request.setAttribute("businesslist", businesslist);

		b_time = getParaValue("startdate");
		t_time = getParaValue("todate");
		subtype = getParaValue("subtype");
		if (b_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		String starttime1 = b_time + " 00:00:00";
		String totime1 = t_time + " 23:59:59";

		HostNodeDao dao = new HostNodeDao();

		List monitorlist = dao.loadIsMonitored(1);
		if (monitorlist != null && monitorlist.size() > 0) {
			for (int i = 0; i < monitorlist.size(); i++) {
				HostNode node = (HostNode) monitorlist.get(i);
				EventListDao eventdao = new EventListDao();
				User vo = (User) session
						.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				StringBuffer s = new StringBuffer();
				
				
				
//				s.append("select * from system_eventlist where recordtime>= '"
//						+ starttime1 + "' " + "and recordtime<='" + totime1
//						+ "'");
				if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					s.append("select * from system_eventlist where recordtime>= '"
							+ starttime1 + "' " + "and recordtime<='" + totime1
							+ "'");
				}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					s.append("select * from system_eventlist where recordtime>= to_date('"+starttime1+"','YYYY-MM-DD HH24:MI:SS') " + "and recordtime<=to_date('"+totime1+"','YYYY-MM-DD HH24:MI:SS')");
				}
				
				if (!"99".equals(level1 + "")) {
					s.append(" and level1=" + level1);
				}
				if (subtype != null && !"value".equals(subtype)) {
					s.append(" and subtype='" + subtype + "'");
				}
				if (!"99".equals(status + "")) {
					s.append(" and managesign=" + status);
				}
				String businessid = vo.getBusinessids();
				int flag = 0;
				if (bid > 0) {
					s.append(" and businessid like '%," + bid + ",%'");
				}

				s.append(" and nodeid = " + node.getId());

				int one = 0;
				int two = 0;
				int three = 0;
				int mone = 0;
				int mtwo = 0;
				int mthree = 0;
				SysLogger.info(s.toString());
				List eventlist = eventdao.findByCriteria(s.toString());
				if (eventlist != null && eventlist.size() > 0) {
					for (int k = 0; k < eventlist.size(); k++) {
						EventList el = (EventList) eventlist.get(k);
						if (el.getLevel1() == 1) {
							one = one + 1;
						} else if (el.getLevel1() == 2) {
							two = two + 1;
						} else {
							three = three + 1;
						}
						if (el.getManagesign() == 0) {
							mone = mone + 1;
						} else if (el.getManagesign() == 1) {
							mtwo = mtwo + 1;
						} else {
							mthree = mthree + 1;
						}
					}
				}
				List levellist = new ArrayList();
				levellist.add(0, one);
				levellist.add(1, two);
				levellist.add(2, three);
				List mlist = new ArrayList();
				mlist.add(0, mone);
				mlist.add(1, mtwo);
				mlist.add(2, mthree);
				List rlist = new ArrayList();
				rlist.add(0, node);
				rlist.add(1, levellist);
				rlist.add(2, mlist);
				endList.add(rlist);
				eventdao.close();
			}
		}

		request.setAttribute("list", endList);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return "/alarm/event/equipmentlist.jsp";
	}

	private String businesslist() {
		List endList = new ArrayList();
		int status = 99;
		int level1 = 99;
		int bid = 0;
		String subtype = "";
		String b_time = "";
		String t_time = "";
		status = getParaIntValue("status");
		level1 = getParaIntValue("level1");

		if (status == -1)
			status = 99;
		if (level1 == -1)
			level1 = 99;

		request.setAttribute("status", status);
		request.setAttribute("level1", level1);

		bid = getParaIntValue("businessid");
		request.setAttribute("businessid", bid);
		BusinessDao bdao = new BusinessDao();
		List businesslist = bdao.loadAll();
		request.setAttribute("businesslist", businesslist);

		b_time = getParaValue("startdate");
		t_time = getParaValue("todate");
		subtype = getParaValue("subtype");
		if (b_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		String starttime1 = b_time + " 00:00:00";
		String totime1 = t_time + " 23:59:59";

		if (businesslist != null && businesslist.size() > 0) {
			for (int i = 0; i < businesslist.size(); i++) {
				Business busi = (Business) businesslist.get(i);
				EventListDao dao = new EventListDao();
				User vo = (User) session
						.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名

				StringBuffer s = new StringBuffer();
				
				if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					s.append("select * from system_eventlist where recordtime>= '"
							+ starttime1 + "' " + "and recordtime<='" + totime1
							+ "'");
				}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					s.append("select * from system_eventlist where recordtime>= to_date('"+starttime1+"','YYYY-MM-DD HH24:MI:SS') " + "and recordtime<=to_date('"+totime1+"','YYYY-MM-DD HH24:MI:SS')");
				}
				
				
				
				if (!"99".equals(level1 + "")) {

					s.append(" and level1=" + level1);
				}
				if (subtype != null && !"value".equals(subtype)) {
					s.append(" and subtype='" + subtype + "'");
				}

				if (!"99".equals(status + "")) {
					s.append(" and managesign=" + status);
				}
				String businessid = vo.getBusinessids();
				int flag = 0;
				if (bid > 0) {
					s.append(" and businessid like '%," + bid + ",%'");
				}

				int one = 0;
				int two = 0;
				int three = 0;

				int sone = 0;
				int stwo = 0;
				int sthree = 0;
				int sfour = 0;

				int mone = 0;
				int mtwo = 0;
				int mthree = 0;
				SysLogger.info(s.toString());
				List eventlist = dao.findByCriteria(s.toString());
				if (eventlist != null && eventlist.size() > 0) {
					for (int k = 0; k < eventlist.size(); k++) {
						EventList el = (EventList) eventlist.get(k);
						if (el.getLevel1() == 1) {
							one = one + 1;
						} else if (el.getLevel1() == 2) {
							two = two + 1;
						} else {
							three = three + 1;
						}

						if (el.getManagesign() == 0) {
							mone = mone + 1;
						} else if (el.getManagesign() == 1) {
							mtwo = mtwo + 1;
						} else {
							mthree = mthree + 1;
						}
					}
				}
				List levellist = new ArrayList();
				levellist.add(0, one);
				levellist.add(1, two);
				levellist.add(2, three);
				List mlist = new ArrayList();
				mlist.add(0, mone);
				mlist.add(1, mtwo);
				mlist.add(2, mthree);
				List rlist = new ArrayList();
				rlist.add(0, busi);
				rlist.add(1, levellist);
				rlist.add(2, mlist);
				endList.add(rlist);
			}
		}
		request.setAttribute("list", endList);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);

		return "/alarm/event/businesslist.jsp";
	}

	private String read() {
		DaoInterface dao = new HostNodeDao();
		setTarget("/topology/network/read.jsp");
		return readyEdit(dao);
	}

	private String telnet() {
		request.setAttribute("ipaddress", getParaValue("ipaddress"));

		return "/tool/telnet.jsp";
	}

	private String readyEdit() {
		DaoInterface dao = new HostNodeDao();
		setTarget("/topology/network/edit.jsp");
		return readyEdit(dao);
	}

	private String readyEditAlias() {
		DaoInterface dao = new HostNodeDao();
		setTarget("/topology/network/editalias.jsp");
		return readyEdit(dao);
	}

	private String readyEditSysGroup() {
		DaoInterface dao = new HostNodeDao();
		setTarget("/topology/network/editsysgroup.jsp");
		return readyEdit(dao);
	}

	private String readyEditSnmp() {
		DaoInterface dao = new HostNodeDao();
		setTarget("/topology/network/editsnmp.jsp");
		return readyEdit(dao);
	}

	private String update() {
		HostNode vo = new HostNode();
		vo.setId(getParaIntValue("id"));
		vo.setAlias(getParaValue("alias"));
		vo.setManaged(getParaIntValue("managed") == 1 ? true : false);

		NodeToBusinessDao ntbdao = new NodeToBusinessDao();
		ntbdao.deleteallbyNE(vo.getId(), "equipment");
		ntbdao.close();
		String[] businessids = getParaArrayValue("checkbox");
		if (businessids != null && businessids.length > 0) {
			for (int i = 0; i < businessids.length; i++) {

				String bid = businessids[i];
				// SysLogger.info(bid+"====");
				NodeToBusiness ntb = new NodeToBusiness();
				ntb.setBusinessid(Integer.parseInt(bid));
				ntb.setNodeid(vo.getId());
				ntb.setElementtype("equipment");
				ntbdao = new NodeToBusinessDao();
				ntbdao.save(ntb);
				ntbdao.close();
			}
		}
		// 更新内存
		Host host = (Host) PollingEngine.getInstance().getNodeByID(vo.getId());
		if (host != null) {
			host.setAlias(vo.getAlias());
			host.setManaged(vo.isManaged());
		} else {
			if (getParaIntValue("managed") == 1) {
				HostNodeDao dao = new HostNodeDao();
				HostNode hostnode = dao.loadHost(vo.getId());
				hostnode.setAlias(getParaValue("alias"));
				hostnode.setManaged(getParaIntValue("managed") == 1 ? true
						: false);
				HostLoader loader = new HostLoader();
				loader.loadOne(hostnode);
				// PollingEngine.getInstance().addNode(node)
			}
		}

		// 更新数据库
		DaoInterface dao = new HostNodeDao();
		setTarget("/network.do?action=list");
		return update(dao, vo);
	}

	private String delete() {

		String[] ids = getParaArrayValue("checkbox");
		if (ids != null && ids.length > 0) {
			// 进行修改
			for (int i = 0; i < ids.length; i++) {
				// String id = getParaValue("radio");
				String id = ids[i];
				PollingEngine.getInstance()
						.deleteNodeByID(Integer.parseInt(id));
				HostNodeDao dao = new HostNodeDao();
				HostNode host = (HostNode) dao.findByID(id);
				dao.delete(id);

				String ip = host.getIpAddress();
//				String ip1 = "", ip2 = "", ip3 = "", ip4 = "";
//				String[] ipdot = ip.split(".");
//				String tempStr = "";
//				String allipstr = "";
//				if (ip.indexOf(".") > 0) {
//					ip1 = ip.substring(0, ip.indexOf("."));
//					ip4 = ip.substring(ip.lastIndexOf(".") + 1, ip.length());
//					tempStr = ip.substring(ip.indexOf(".") + 1, ip
//							.lastIndexOf("."));
//				}
//				ip2 = tempStr.substring(0, tempStr.indexOf("."));
//				ip3 = tempStr.substring(tempStr.indexOf(".") + 1, tempStr
//						.length());
//				allipstr = ip1 + ip2 + ip3 + ip4;
				String allipstr = SysUtil.doip(ip);

//				CreateTableManager ctable = new CreateTableManager();
//				DBManager conn = new DBManager();
				// dbmanager
				// 先删除网络设备表
				/*
				 * ctable.deleteTable(conn,"ping",allipstr,"ping");//Ping
				 * ctable.deleteTable(conn,"pinghour",allipstr,"pinghour");//Ping
				 * ctable.deleteTable(conn,"pingday",allipstr,"pingday");//Ping
				 * 
				 * 
				 * ctable.deleteTable(conn,"memory",allipstr,"mem");//内存
				 * ctable.deleteTable(conn,"memoryhour",allipstr,"memhour");//内存
				 * ctable.deleteTable(conn,"memoryday",allipstr,"memday");//内存
				 * 
				 * ctable.deleteTable(conn,"cpu",allipstr,"cpu");//CPU
				 * ctable.deleteTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
				 * ctable.deleteTable(conn,"cpuday",allipstr,"cpuday");//CPU
				 * 
				 * ctable.deleteTable(conn,"utilhdxperc",allipstr,"hdperc");
				 * ctable.deleteTable(conn,"utilhdx",allipstr,"hdx");
				 * ctable.deleteTable(conn,"hdxperchour",allipstr,"hdperchour");
				 * ctable.deleteTable(conn,"hdxpercday",allipstr,"hdpercday");
				 * ctable.deleteTable(conn,"utilhdxhour",allipstr,"hdxhour");
				 * ctable.deleteTable(conn,"utilhdxday",allipstr,"hdxday");
				 * 
				 * ctable.deleteTable(conn,"discardsperc",allipstr,"dcardperc");
				 * ctable.deleteTable(conn,"dcarperh",allipstr,"dcarperh");
				 * ctable.deleteTable(conn,"dcardpercday",allipstr,"dcardpercday");
				 * 
				 * ctable.deleteTable(conn,"errorsperc",allipstr,"errperc");
				 * ctable.deleteTable(conn,"errperch",allipstr,"errperch");
				 * ctable.deleteTable(conn,"errpercd",allipstr,"errpercd");
				 * 
				 * ctable.deleteTable(conn,"packs",allipstr,"packs");
				 * ctable.deleteTable(conn,"packshour",allipstr,"packshour");
				 * ctable.deleteTable(conn,"packsday",allipstr,"packsday");
				 * DiscoverCompleteDao dcDao = new DiscoverCompleteDao();
				 * dcDao.deleteMonitor(host.getId(), host.getIpAddress());
				 * conn.close();
				 */
			}

		}

		return "/network.do?action=list";
	}

	private String dodelete() {

		String[] ids = getParaArrayValue("checkbox");
		if (ids != null && ids.length > 0) {
			// 进行删除
			EventListDao edao = new EventListDao();
			edao.delete(ids);
			edao.close();

		}

		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		EventListDao dao = new EventListDao();
		status = getParaIntValue("status");
		level1 = getParaIntValue("level1");
		if (status == -1)
			status = 99;
		if (level1 == -1)
			level1 = 99;
		request.setAttribute("status", status);
		request.setAttribute("level1", level1);

		b_time = getParaValue("startdate");
		t_time = getParaValue("todate");

		if (b_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		String starttime1 = b_time + " 00:00:00";
		String totime1 = t_time + " 23:59:59";
		String sql = "";
		try {
			User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
			StringBuffer s = new StringBuffer();
			s.append("where recordtime>= '" + starttime1 + "' "
					+ "and recordtime<='" + totime1 + "'");
			if (!"99".equals(level1 + "")) {
				s.append(" and level1=" + level1);
			}
			if (!"99".equals(status + "")) {
				s.append(" and managesign=" + status);
			}
			String businessid = vo.getBusinessids();
			int flag = 0;
			if (businessid != null) {
				if (businessid != "-1") {
					String[] bids = businessid.split(",");
					if (bids.length > 0) {
						for (int i = 0; i < bids.length; i++) {
							if (bids[i].trim().length() > 0) {
								if (flag == 0) {
									s.append(" and ( businessid = ',"
											+ bids[i].trim() + ",' ");
									flag = 1;
								} else {
									// flag = 1;
									s.append(" or businessid = ',"
											+ bids[i].trim() + ",' ");
								}
							}
						}
						s.append(") ");
					}

				}
			}
			sql = s.toString();
			sql = sql + " order by id desc";
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		setTarget("/event.do?action=list");
		return list(dao, sql);
	}
	
	private String showdelete() {

		String[] ids = getParaArrayValue("checkbox");
		if (ids != null && ids.length > 0) {
			// 进行删除
			EventListDao edao = new EventListDao();
			edao.delete(ids);
			edao.close();

		}

		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		EventListDao dao = new EventListDao();
		status = getParaIntValue("status");
		level1 = getParaIntValue("level1");
		if (status == -1)
			status = 99;
		if (level1 == -1)
			level1 = 99;
		request.setAttribute("status", status);
		request.setAttribute("level1", level1);

		b_time = getParaValue("startdate");
		t_time = getParaValue("todate");

		if (b_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		String starttime1 = b_time + " 00:00:00";
		String totime1 = t_time + " 23:59:59";
		String sql = "";
		try {
			User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
			StringBuffer s = new StringBuffer();
			s.append("where recordtime>= '" + starttime1 + "' "
					+ "and recordtime<='" + totime1 + "'");
			if (!"99".equals(level1 + "")) {
				s.append(" and level1=" + level1);
			}
			if (!"99".equals(status + "")) {
				s.append(" and managesign=" + status);
			}
			String businessid = vo.getBusinessids();
			int flag = 0;
			if (businessid != null) {
				if (businessid != "-1") {
					String[] bids = businessid.split(",");
					if (bids.length > 0) {
						for (int i = 0; i < bids.length; i++) {
							if (bids[i].trim().length() > 0) {
								if (flag == 0) {
									s.append(" and ( businessid = ',"
											+ bids[i].trim() + ",' ");
									flag = 1;
								} else {
									// flag = 1;
									s.append(" or businessid = ',"
											+ bids[i].trim() + ",' ");
								}
							}
						}
						s.append(") ");
					}

				}
			}
			sql = s.toString();
			sql = sql + " order by id desc";
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		
		setTarget("/event.do?action=showDetails");
		return list(dao, sql);
	}
	private String cancelmanage() {
		String[] ids = getParaArrayValue("checkbox");
		if (ids != null && ids.length > 0) {
			// 进行修改
			for (int i = 0; i < ids.length; i++) {
				HostNodeDao dao = new HostNodeDao();
				HostNode host = (HostNode) dao.findByID(ids[i]);
				host.setManaged(false);
				dao = new HostNodeDao();
				dao.update(host);
				PollingEngine.getInstance().deleteNodeByID(
						Integer.parseInt(ids[i]));
			}

		}

		return "/network.do?action=monitornodelist";
	}

	private String add() {
		String ipAddress = getParaValue("ip_address");
		String alias = getParaValue("alias");
		String community = getParaValue("community");
		String writecommunity = getParaValue("writecommunity");
		int type = getParaIntValue("type");

		TopoHelper helper = new TopoHelper(); // 包括更新数据库和更新内存
		int addResult = helper.addHost(ipAddress, alias, community,
				writecommunity, type); // 加入一台服务器
		if (addResult == 0) {
			setErrorCode(ErrorMessage.ADD_HOST_FAILURE);
			return null;
		}
		if (addResult == -1) {
			setErrorCode(ErrorMessage.IP_ADDRESS_EXIST);
			return null;
		}
		if (addResult == -2) {
			setErrorCode(ErrorMessage.PING_FAILURE);
			return null;
		}
		if (addResult == -3) {
			setErrorCode(ErrorMessage.SNMP_FAILURE);
			return null;
		}

		// 2.更新xml
		XmlOperator opr = new XmlOperator();
		opr.setFile("network.jsp");
		opr.init4updateXml();
		opr.addNode(helper.getHost());
		opr.writeXml();

		return "/network.do?action=list";
	}

	private String find() {
		String key = getParaValue("key");
		String value = getParaValue("value");
		HostNodeDao dao = new HostNodeDao();
		request.setAttribute("list", dao.findByCondition(key, value));

		return "/topology/network/find.jsp";
	}

	private String save() {
		String xmlString = request.getParameter("hidXml");
		String vlanString = request.getParameter("vlan");
		xmlString = xmlString.replace("<?xml version=\"1.0\"?>",
				"<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		XmlOperator xmlOpr = new XmlOperator();
		if (vlanString != null && vlanString.equals("1")) {
			xmlOpr.setFile("networkvlan.jsp");
		} else
			xmlOpr.setFile("network.jsp");
		xmlOpr.saveImage(xmlString);

		return "/topology/network/save.jsp";
	}

	private String savevlan() {
		String xmlString = request.getParameter("hidXml");
		xmlString = xmlString.replace("<?xml version=\"1.0\"?>",
				"<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		XmlOperator xmlOpr = new XmlOperator();
		xmlOpr.setFile("networkvlan.jsp");
		xmlOpr.saveImage(xmlString);

		return "/topology/network/save.jsp";
	}

	/**
	 * nielin modify 2010-10-11
	 * 接受处理
	 * @return
	 */
	private String accit() {
		String eventId = getParaValue("eventId");
		EventList eventList = null;
		EventListDao dao = new EventListDao();
		try {
			eventList = (EventList) dao.findByID(eventId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			dao.close();
		}
		request.setAttribute("eventList", eventList);
		return "/alarm/event/accitevent.jsp";
	}
	
	/**
	 * nielin modify 2010-10-11
	 * 处理事件
	 * @return
	 */
	private String accfi() {
		String eventid = getParaValue("eventid");
		EventList event = null;
		EventListDao dao = new EventListDao();
		try {
			event = (EventList) dao.findByID(eventid);
			event.setManagesign(new Integer(1));
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			dao.close();
		}
		try {
			dao = new EventListDao();
			dao.update(event);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			dao.close();
		}
		return freshParentPage();
	}
	
	/**
	 * nielin modify 2010-10-11
	 * 填写报告
	 * @return
	 */
	private String doreport() {
		String eventid = getParaValue("eventid");
		System.out.println(eventid+"===eventid");
		try {
			EventListDao dao = new EventListDao();
			try {
				dao.updateManagesignById("2", eventid);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				dao.close();
			}
			
			EventReport eventreport = creatEventReport();
			eventreport.setEventid(Integer.valueOf(eventid));
			EventReportDao reportdao = new EventReportDao();
			try {
				reportdao.save(eventreport);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				reportdao.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return freshParentPage();
	}
	
	/**
	 * nielin modify 2010-10-11
	 * 创建事件报告
	 * @return
	 */
	private EventReport creatEventReport(){
		
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		//System.out.println(getParaValue("deal_time5")+"deal_time5");
		Date d = null;
		try {
			d = sdf0.parse(getParaValue("deal_time5"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			d = new Date();
		} 
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		EventReport eventreport = new EventReport();
		eventreport.setDeal_time(c);
		eventreport.setReport_content(getParaValue("report_content"));
		eventreport.setReport_man(getParaValue("report_man"));
		eventreport.setReport_time(Calendar.getInstance());
		return eventreport;
	}
	
	public String showReport(){
		String eventId = getParaValue("eventId");
		
		EventList eventList = null;
		EventListDao dao = new EventListDao();
		try {
			eventList = (EventList) dao.findByID(eventId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			dao.close();
		}
		request.setAttribute("eventList", eventList);
		System.out.println(eventId+"========eventId");
		EventReport eventreport = null;
		EventReportDao rdao = new EventReportDao();
		try {
			eventreport = (EventReport)rdao.findByEventId(eventId);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			rdao.close();
		}

		request.setAttribute("eventreport", eventreport);
		return "/alarm/event/accitevent.jsp";
	}
	
	/**
	 * 刷新父页面
	 * @return
	 */
	private String freshParentPage(){
		return "/alarm/event/fresh.jsp";
	}
	
	public String getSummary(){
		
		String where = getWhere();
		
		//System.out.println(where+"==============where============");
		
		List list = null;
		
		EventListDao eventListDao = new EventListDao();
		try {
			int perpage = getPerPagenum();
			int curpage = getCurrentPage();
			list = eventListDao.getSummary(where, curpage, perpage);
			request.setAttribute("page",eventListDao.getPage());
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			eventListDao.close();
		}
		
		Hashtable hashtable = new Hashtable();
		if(list!=null && list.size()>0){
			try {
				for(int i = 0 ; i < list.size(); i++){
					List tempList = (List)list.get(i);
					String eventsrc = "";
					EventList eventlist = (EventList)tempList.get(0);
			  		if(eventlist.getSubtype().equalsIgnoreCase("network") || eventlist.getSubtype().equalsIgnoreCase("host")){
			  			HostNode node = new HostNode();
						HostNodeDao dao = new HostNodeDao();
						try {
							node = dao.loadHost(eventlist.getNodeid());
						} catch (RuntimeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally{
							dao.close();
						}
						if(node!=null){
							eventsrc = node.getAlias()+"("+node.getIpAddress()+")";
						}
			  		}else{
			  			eventsrc = eventlist.getEventlocation();
			  		}
			  		hashtable.put(eventlist.getId()+"", eventsrc);
				}
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		
		String business = user.getBusinessids();
		
		List businessList = praseBusiness(business);
		
		Arraylistcomparator comparator = new Arraylistcomparator();
		Collections.sort(list,comparator);
		
		request.setAttribute("businessList", businessList);
		request.setAttribute("list", list);
		request.setAttribute("hashtable", hashtable);
		
		return "/alarm/event/list2.jsp";
	}
	
	private List praseBusiness(String business){
		List businessList = new ArrayList();
		if(business != null){
			String[] bids = business.split(",");
			
			if(bids !=null && bids.length > 0 ){
				BusinessDao businessDao = new BusinessDao();
				try {
					for(int i = 0 ; i < bids.length ; i++){
						Business bi = null ;
						
						if(bids[i].trim().length()>0){
							bi = (Business)businessDao.findBySuperID(bids[i].trim());
							
							if( bi != null ){
								businessList.add(bi);
							}
						}
						
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					businessDao.close();
				}
				
			}
		}
		return businessList;
	}
	
	private String getWhere(){
		
		String startDate = getParaValue("startdate");
		
		String toDate = getParaValue("todate");
		
		String subtype = getParaValue("subtype");
		
		String level1 = getParaValue("level1");
		
		String businessid = getParaValue("businessid");
		
		String nodeId = getParaValue("nodeId");
		
		String subentity = getParaValue("subentity");
		String ostype = getParaValue("ostype");
		String status = getParaValue("status");
		
		String managesign = getParaValue("managesign");
		if(managesign==null)
		{
			managesign = "0";
		}	
		String ipaddress = getParaValue("ipaddress");
		
		if(ipaddress!=null && ipaddress.trim().length() > 0 ){
//			Node node = PollingEngine.getInstance().getNodeByIP(ipaddress);
//			if(node != null ){
//				nodeId = String.valueOf(node.getId());
//			}else{
//				nodeId = "-1";
//			}
		}else {
			ipaddress = "";
		}
		
		request.setAttribute("ipaddress", ipaddress);
		
		StringBuffer whereSB = new StringBuffer();
		
		whereSB.append("where");
		
		whereSB.append(getTimeSql(startDate, toDate));
		
		whereSB.append(getLevel1Sql(level1));
		
		whereSB.append(getSubtypeSql(subtype));
		
		
		
		whereSB.append(getSubentitySql(subentity));
		whereSB.append(getOsTypeSql(ostype));
		whereSB.append(getStatusSql(status));
		
		whereSB.append(getBussinessId(businessid));
		
		whereSB.append(getManagesign(managesign));
		
		whereSB.append(getIpaddress(ipaddress));
		
		whereSB.append(getNodeIdSql(nodeId));
		
		return whereSB.toString();
	}
	
	private String getTimeSql(String startDate , String toDate){
		String startTime = getStarttime(startDate);
		
		String toTime = getTotime(toDate);
		
		StringBuffer sbSql = new StringBuffer();
		
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			sbSql.append(" recordtime>'" + startTime +"'");
			sbSql.append(" and recordtime<'" + toTime +"'");
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			sbSql.append(" recordtime>to_date('"+startTime+"','YYYY-MM-DD HH24:MI:SS') ");
			sbSql.append(" and recordtime<to_date('"+toTime+"','YYYY-MM-DD HH24:MI:SS')");
		}
		
		
		
		return sbSql.toString();
	}
	private String getOsTypeSql(String ostype){
		StringBuffer sbSql = new StringBuffer();

		if(ostype!=null&&!ostype.equals("不限")){
			sbSql.append(" and nodeid in(select distinct nodeid from nms_alarm_indicators_node where subtype='");
			sbSql.append(ostype).append("')");
			
		}
		return sbSql.toString();
	}
	private String getSubtypeSql(String subtype){
		StringBuffer sbSql = new StringBuffer();
		if(subtype==null || "value".equals(subtype)){
			sbSql.append("");
			subtype = "value";
		}else{
			if(subtype.equals("middleware")){
				sbSql.append(" and subtype in(").append("'weblogic','tomcat','apache','mq','apache')");
			}else{
			sbSql.append(" and subtype='"+ subtype +"'");
			}
		}
		
		request.setAttribute("subtype", subtype);
		
		return sbSql.toString();
	}
	
	private String getNodeIdSql(String nodeId){
		StringBuffer sbSql = new StringBuffer();
		if(nodeId == null || nodeId.trim().length() == 0){
			sbSql.append("");
		}else{
			sbSql.append(" and nodeid=").append(nodeId);
			
		}
		
		request.setAttribute("nodeId", nodeId);
		
		return sbSql.toString();
	}
	
//	private String getSubentitySql(String subentity){
//		StringBuffer sbSql = new StringBuffer();
//		if(subentity == null || subentity.trim().length() == 0){
//			sbSql.append("");
//		}else{
//			sbSql.append(" and subentity='"+ subentity +"'");
//			
//		}
//		
//		request.setAttribute("subentity", subentity);
//		
//		return sbSql.toString();
//	}
	
	private String getSubentitySql(String subentity){
		StringBuffer sbSql = new StringBuffer();
		if(subentity == null || subentity.trim().length() == 0||subentity.equals("不限")){
			sbSql.append("");
		}else{
			sbSql.append(" and subentity like '"+ subentity +"%'");
			
		}
		
		request.setAttribute("subentity", subentity);
		
		return sbSql.toString();
	}
	
	private String getStatusSql(String status){
		StringBuffer sbSql = new StringBuffer();
		if(status == null || status.trim().length() > 0){
			sbSql.append("");
			status = "-1";
		}else{
			sbSql.append(" and status='"+ status +"'");
			
		}
		
		request.setAttribute("status", status);
		
		return sbSql.toString();
	}
	
	private String getLevel1Sql(String level1){
		StringBuffer sbSql = new StringBuffer();
		if("0".equals(level1)){
			sbSql.append(" and level1='0'");
		}else if("1".equals(level1)){
			sbSql.append(" and level1='1'");
		}else if("2".equals(level1)){
			sbSql.append(" and level1='2'");
		}else if("3".equals(level1)){
			sbSql.append(" and level1='3'");
		}else{
			sbSql.append("");
			level1 = "-1";
		}
		
		request.setAttribute("level1", level1);
		
		return sbSql.toString();
	}
	
	public String getBussinessId(String businessid){
		
		User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		
		int flag = 0;
		StringBuffer sbSql = new StringBuffer();
		if(businessid == null || "-1".equals(businessid)){
			
			if(user.getRole() ==0){
				sbSql.append("");
			}else{
				String business = user.getBusinessids();
				
				String[] bids = business.split(",");
				if (bids != null && bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (flag == 0) {
							sbSql.append(" and ( businessid like '%," + bids[i].trim() + ",%'");
							flag = 1;
						} else {
							// flag = 1;
							sbSql.append(" or businessid like '%," + bids[i].trim() + ",%'");
						}
					}
					sbSql.append(") ");
				}
			
			}
			
			businessid = "-1";
		}else{
			sbSql.append(" and businessid like '%," + businessid + ",%'");
		}
		
		request.setAttribute("businessid", businessid);
		
		return sbSql.toString();
	}
	
	public String getManagesign(String managesign){
		
		StringBuffer sbSql = new StringBuffer();
		
		if("0".equals(managesign)){
			sbSql.append(" and managesign='0'");
		}else if("1".equals(managesign)){
			sbSql.append(" and managesign='1'");
		}else if("2".equals(managesign)){
			sbSql.append(" and managesign='2'");
		}else{
			sbSql.append("");
			managesign = "-1";
		}
		
		request.setAttribute("managesign", managesign);
		
		return sbSql.toString();
	}
	
public String getIpaddress(String ipaddress){
		
		//User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		
		int flag = 0;
		StringBuffer sbSql = new StringBuffer();
		if(ipaddress != null || ipaddress.trim().length()>0){
			sbSql.append(" and (eventlocation like '%" + ipaddress + "%' or content like '%"+ipaddress+"%')");
		}else{
			//sbSql.append(" and businessid like '%," + businessid + ",%'");
		}
		
		//request.setAttribute("businessid", businessid);
		
		return sbSql.toString();
	}
	
	
	
	public String getStarttime(String startDate){
		if(startDate == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			startDate = sdf.format(new Date());
		}
		String starttime = startDate + " 00:00:00";
		
		request.setAttribute("startdate", startDate);
		
		return starttime;
	}
public String summary(){
	String startDate = getParaValue("startdate");
	
	String toDate = getParaValue("todate");
	
	String subtype = getParaValue("subtype");
	
	String level1 = getParaValue("level1");
	
	String businessid = getParaValue("businessid");
	
	String nodeId = getParaValue("nodeId");
	
	String subentity = getParaValue("subentity");
	String ostype = getParaValue("ostype");
	String status = getParaValue("status");
	
	String managesign = getParaValue("managesign");
	if(managesign==null)
	{
		managesign = "0";
	}	
	String ipaddress = getParaValue("ipaddress");
	
	if(ipaddress!=null && ipaddress.trim().length() > 0 ){
//		Node node = PollingEngine.getInstance().getNodeByIP(ipaddress);
//		if(node != null ){
//			nodeId = String.valueOf(node.getId());
//		}else{
//			nodeId = "-1";
//		}
	}else {
		ipaddress = "";
	}
	
	request.setAttribute("ipaddress", ipaddress);
	
	StringBuffer whereSB = new StringBuffer();
	
	whereSB.append("where");
	
	whereSB.append(getTimeSql(startDate, toDate));
	whereSB.append(getManagesign(managesign));
	
	
		String where = whereSB.toString();
		
		//System.out.println(where+"==============where============");
		
		List list = null;
		
		EventListDao eventListDao = new EventListDao();
		try {
			int perpage = getPerPagenum();
			int curpage = getCurrentPage();
			list = eventListDao.getSummary(where, curpage, perpage);
			request.setAttribute("page",eventListDao.getPage());
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			eventListDao.close();
		}
		
		Hashtable hashtable = new Hashtable();
		if(list!=null && list.size()>0){
			try {
				for(int i = 0 ; i < list.size(); i++){
					List tempList = (List)list.get(i);
					String eventsrc = "";
					EventList eventlist = (EventList)tempList.get(0);
			  		if(eventlist.getSubtype().equalsIgnoreCase("network") || eventlist.getSubtype().equalsIgnoreCase("host")){
			  			HostNode node = new HostNode();
						HostNodeDao dao = new HostNodeDao();
						try {
							node = dao.loadHost(eventlist.getNodeid());
						} catch (RuntimeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally{
							dao.close();
						}
						if(node!=null){
							eventsrc = node.getAlias()+"("+node.getIpAddress()+")";
						}
			  		}else{
			  			eventsrc = eventlist.getEventlocation();
			  		}
			  		hashtable.put(eventlist.getId()+"", eventsrc);
				}
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		
		String business = user.getBusinessids();
		
		List businessList = praseBusiness(business);
		
		Arraylistcomparator comparator = new Arraylistcomparator();
		Collections.sort(list,comparator);
		
		request.setAttribute("businessList", businessList);
		request.setAttribute("list", list);
		request.setAttribute("hashtable", hashtable);
		
		return "/alarm/event/list2.jsp";
	}
private String clear(){

       String startDate = getParaValue("startdate");
		
		String toDate = getParaValue("todate");
	    StringBuffer whereSB = new StringBuffer();
		
		whereSB.append("where");
		
		whereSB.append(getTimeSql(startDate, toDate));
		String where = whereSB.toString();
		
		
		EventListDao eventListDao = new EventListDao();
		try {
			String sql = "delete from system_eventlist "+ where;
			//SysLogger.info(sql);
			eventListDao.saveOrUpdate(sql);
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally{
			eventListDao.close();
		}
		
		request.setAttribute("subtype", "value");
		request.setAttribute("level1", "-1");
		
		return summary();
	
	
	}
	private String deleteDetails(){

		String where = getWhere();
		
		//System.out.println(where+"==============where============");
		
		List list = null;
		String nodeId = getParaValue("nodeId");
		User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		
		String business = user.getBusinessids();
		
		List businessList = praseBusiness(business);
		
		request.setAttribute("businessList", businessList);
		
		EventListDao eventListDao = new EventListDao();
		System.out.println(nodeId+"----55555555555---"+where);
		try {
			String sql = "delete from system_eventlist "+ where;
			//SysLogger.info(sql);
			eventListDao.saveOrUpdate(sql);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			eventListDao.close();
		}
		
		request.setAttribute("subtype", "value");
		request.setAttribute("level1", "-1");
		
		
		return summary();
	
	}
	private String showDetails(){
		String where = getWhere();
		
		
		
		List list = null;
		
		User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		
		String business = user.getBusinessids();
		
		List businessList = praseBusiness(business);
		
		request.setAttribute("businessList", businessList);
		
		EventListDao eventListDao = new EventListDao();
//		try {
//			list = eventListDao.getSummary(where);
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally{
//			eventListDao.close();
//		}
		
		try {
			int perpage = getPerPagenum();
			list = eventListDao.listByPage(getCurrentPage(),where,perpage);
			request.setAttribute("page",eventListDao.getPage());
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			eventListDao.close();
		}
		
		Hashtable hashtable = new Hashtable();
		if(list!=null && list.size()>0){
			try {
				for(int i = 0 ; i < list.size(); i++){
					EventList eventlist = (EventList)list.get(i);
					String eventsrc = "";
			  		if(eventlist.getSubtype().equalsIgnoreCase("network") || eventlist.getSubtype().equalsIgnoreCase("host")){
			  			HostNode node = new HostNode();
						HostNodeDao dao = new HostNodeDao();
						try {
							node = dao.loadHost(eventlist.getNodeid());
						} catch (RuntimeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally{
							dao.close();
						}
						if(node!=null){
							eventsrc = node.getAlias()+"("+node.getIpAddress()+")";
						}
			  		}else{
			  			eventsrc = eventlist.getEventlocation();
			  		}
			  		hashtable.put(eventlist.getId()+"", eventsrc);
				}
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		EventListcomparator comparator = new EventListcomparator();
		Collections.sort(list,comparator);
		request.setAttribute("hashtable", hashtable);
		request.setAttribute("list", list);
		return "/alarm/event/showdetails.jsp";
	}
	
	public String getTotime(String toDate){
		if(toDate == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			toDate = sdf.format(new Date());
		}
		String totime = toDate + " 23:59:59";
		
		request.setAttribute("todate", toDate);
		
		return totime;
	}
	
	private String doEditAlarmLevel(){
		String[] ids = getParaArrayValue("checkbox");
		String alermlevel = getParaValue("alermlevel");
		EventListDao eventListDao = new EventListDao();
		if( ids != null && ids.length > 0 && alermlevel !=null){
			try{
				eventListDao.batchEditAlarmLevel(ids, alermlevel);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				eventListDao.close();
			}
		}
		return showDetails();
	}
	

	/**
	 * 批量处理事件
	 * @return
	 */
	private String batchAccfi() {
		String eventids = getParaValue("eventids");//12,13,14,
		String[] ids = eventids.split(",");
		
		boolean flag = false;
		EventListDao dao = new EventListDao();
		try {
			flag = dao.batchUpdataManagesignByIds("0","1",ids);
		} catch (Exception e) { 
			e.printStackTrace();
		} finally{
			dao.close();
		}
		return freshParentPage();
	}
	
	/**
	 * 批量填写报告
	 * @return
	 */
	private String batchDoReport() {
		String eventids = getParaValue("eventids");//12,13,14
		String[] ids = eventids.split(",");
		Hashtable updataHash = new Hashtable();
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		try {
			d = sdf0.parse(getParaValue("deal_time5"));
		} catch (ParseException e) {
			e.printStackTrace();
			d = new Date();
		} 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String deal_time = sdf.format(d);
		String reporttime = sdf.format(new Date());
//		EventReport eventreport = new EventReport();
//		eventreport.setDeal_time(c);
//		eventreport.setReport_content(getParaValue("report_content"));
//		eventreport.setReport_man(getParaValue("report_man"));
//		eventreport.setReport_time(Calendar.getInstance());
		String report_content = getParaValue("report_content");
		String report_man = getParaValue("report_man");
		updataHash.put("deal_time", deal_time);
		updataHash.put("report_content", report_content);
		updataHash.put("report_man", report_man);
		updataHash.put("report_time", reporttime);
		
		try {
			//过滤掉不是“处理中”状态下的id
			EventReportDao reportdao = new EventReportDao();
			try {//ids中都是“处理中”的id
				reportdao.batchAddEventReport(ids, updataHash);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				reportdao.close();
			}
			
			EventListDao dao = new EventListDao();
			boolean flag = false;
			try {
				flag = dao.batchUpdataManagesignByIds("1","2",ids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return freshParentPage();
	}
	
	/**
	 * 批量更改告警级别 
	 * @return
	 */
	private String batchEditAlarmLevel(){ 
		String eventids = getParaValue("eventids");//12,13,14
		String[] ids = eventids.split(",");
		String alermlevel = getParaValue("alermlevel");
		EventListDao eventListDao = new EventListDao();
		if( ids != null && ids.length > 0 && alermlevel !=null){
			try{
				eventListDao.batchEditAlarmLevel(ids, alermlevel);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				eventListDao.close();
			}
		}
		return freshParentPage();
	}
	
	@SuppressWarnings("unchecked")
	private String alarmsum(){//告警汇总 add by zcw
		User user=(User)session.getAttribute(SessionConstant.CURRENT_USER);
		HashMap datamap=new HashMap();
		AlarmSummarize alarmsum=new AlarmSummarize();
		datamap=alarmsum.getData(user.getUserid()+"_"+user.getId());
		request.setAttribute("datamap", datamap);
		return "/alarm/event/alarmsum.jsp";
		
	}
	
	private String chartForAlarmSummarize() {

		User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		HashMap datamap = new HashMap();
		AlarmSummarize alarmsum = new AlarmSummarize();
		datamap = alarmsum.getData(vo.getUserid() + "_" + vo.getId());
		request.setAttribute("datamap", datamap);// 告警汇总信息
		
		return "/alarm/event/chartForAlarmSummarize.jsp";
	}
	
	public String execute(String action) {
		if (action.equals("list"))
			return list();
		if (action.equals("equipmentlist"))
			return equipmentlist();
		if (action.equals("businesslist"))
			return businesslist();
		if (action.equals("read"))
			return read();
		if (action.equals("accit"))
			return accit();
		if (action.equals("accfi"))
			return accfi();
		if (action.equals("doreport"))
			return doreport();
		if (action.equals("ready_edit"))
			return readyEdit();
		if (action.equals("ready_editalias"))
			return readyEditAlias();
		if (action.equals("ready_editsysgroup"))
			return readyEditSysGroup();
		if (action.equals("ready_editsnmp"))
			return readyEditSnmp();
		if (action.equals("update"))
			return update();
		if (action.equals("cancelmanage"))
			return cancelmanage();
		if (action.equals("delete"))
			return delete();
		if (action.equals("dodelete"))
			return dodelete();
		if (action.equals("showdelete"))
			return showdelete();
		if (action.equals("find"))
			return find();
		if (action.equals("ready_add"))
			return "/topology/network/add.jsp";
		if (action.equals("add"))
			return add();
		if (action.equals("telnet"))
			return telnet();
		if (action.equals("save"))
			return save();
		if (action.equals("showReport"))
			return showReport();
		if(action.equals("todaylist")){
	    	  return todayList();
		}
		if("summary".equals(action)){
			return getSummary();
		}
		if("showDetails".equals(action)){
			return showDetails();
		}
		if("doEditAlarmLevel".equals(action)){
			return doEditAlarmLevel();
		}
		if("alarmsum".equals(action)){
			return alarmsum();
		}
		if("batchAccfi".equals(action)){   
			return batchAccfi();
		}
		if("batchDoReport".equals(action)){
			return batchDoReport();
		}
		if("batchEditAlarmLevel".equals(action)){
			return batchEditAlarmLevel();
		}
		if ("chartForAlarmSummarize".equals(action)) {
			return chartForAlarmSummarize();
		}
		if ("deleteDetails".equals(action)) {
			return deleteDetails();
		}
		if ("clear".equals(action)) {
			return clear();
		}
		
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}

class Arraylistcomparator implements Comparator {


	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		ArrayList l1 = (ArrayList) o1;
		ArrayList l2 = (ArrayList) o2;
		if(compare_date((String)l1.get(2),(String)l2.get(2))==1)
			return 1;
		else
			return 0;
	}

	public int compare_date(String date1, String date2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date dt1;
		try {
			dt1 = df.parse(date1);
			Date dt2 = df.parse(date2);
			if (dt1.getTime() <= dt2.getTime()) {
				return 1;
			} else{
				return 0;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
class EventListcomparator implements Comparator {


	public int compare(Object o1, Object o2) {
		EventList l1 = (EventList)o1;
		EventList l2 = (EventList)o2;
		if(l1.getRecordtime().compareTo(l2.getRecordtime())<=0)
			return 1;
		else
			return 0;
	}
	
}