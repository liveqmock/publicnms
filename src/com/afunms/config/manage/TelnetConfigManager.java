package com.afunms.config.manage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.bouncycastle.jce.provider.JDKKeyFactory.RSA;

import com.afunms.application.util.ReportExport;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.CreateAmLinePic;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.CfgBaseInfoDao;
import com.afunms.config.dao.GatherAclDao;
import com.afunms.config.dao.GatherTelnetConfigDao;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.dao.PolicyInterfaceDao;
import com.afunms.config.dao.QueueInfoDao;
import com.afunms.config.model.GatherAcl;
import com.afunms.config.model.GatherTelnetConfig;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.config.model.PolicyInterface;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;

public class TelnetConfigManager extends BaseManager implements
		ManagerInterface {

	public String missionList() {
		GatherTelnetConfigDao dao = new GatherTelnetConfigDao();
		List<GatherTelnetConfig> list = dao.loadAll();
		request.setAttribute("list", list);
		String jsp = "/config/vpntelnet/gatherConfig/missionList.jsp";
		return jsp;
	}

	public String readyAdd() {
		String jsp = "/config/vpntelnet/gatherConfig/add.jsp";
		return jsp;
	}

	public String add() {
		String jsp = "/config/vpntelnet/gatherConfig/missionList.jsp";
		String ips = getParaValue("ipaddress");
		String commands = getParaValue("commands");
		int status = getParaIntValue("status");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = sdf.format(date);

		GatherTelnetConfig vo = new GatherTelnetConfig();
		vo.setTelnetIps(ips);
		commands = commands.replaceAll("\r\n", ";");
		vo.setCommands(commands);
		vo.setCreate_time(time);
		vo.setStatus(status);
		GatherTelnetConfigDao dao = null;
		try {
			dao = new GatherTelnetConfigDao();
			dao.createTable(ips);
			dao.exeBatch();
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		return missionList();
	}

	public String delete() {
		String[] id = getParaArrayValue("checkbox");
		GatherTelnetConfigDao dao = null;
		try {
			dao = new GatherTelnetConfigDao();
			String ids = "";
			for (int i = 0; i < id.length; i++) {
				ids = ids + id[i] + ",";
			}
			List list = dao.findByCondition(" where id in("
					+ ids.substring(0, ids.length() - 1) + ")");
			dao = new GatherTelnetConfigDao();
			StringBuffer ips = new StringBuffer();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					GatherTelnetConfig config = (GatherTelnetConfig) list
							.get(i);
					ips.append(config.getTelnetIps());
				}
			}
			dao.dropTable(ips.toString());
			dao.exeBatch();
			dao.delete(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		return missionList();
	}

	public String update() {
		int id = getParaIntValue("id");
		String ips = getParaValue("ipaddress");
		String commands = getParaValue("commands");
		int status = getParaIntValue("status");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = sdf.format(date);

		GatherTelnetConfig vo = new GatherTelnetConfig();
		vo.setId(id);
		vo.setTelnetIps(ips);
		commands = commands.replaceAll("\r\n", ";");
		vo.setCommands(commands);
		vo.setCreate_time(time);
		vo.setStatus(status);
		GatherTelnetConfigDao dao = null;
		Vector ipVector=new Vector();
		String[] newIps=null;
		if(ips!=null){
			newIps=ips.split(",");
		}
		StringBuffer createIps=new StringBuffer();
		StringBuffer dropIps=new StringBuffer();
		try {
			dao = new GatherTelnetConfigDao();
			GatherTelnetConfig temp=(GatherTelnetConfig) dao.findByID(id+"");
			if(temp!=null){
				String[] telnetIps=temp.getTelnetIps().split(",");
				
				if(telnetIps!=null){
					for (int i = 0; i < telnetIps.length; i++) {
						if(!telnetIps[i].trim().equals("")&&telnetIps[i]!=null)
						   ipVector.add(telnetIps[i]);
					}
					for (int i = 0; i < newIps.length; i++) {
						if (!ipVector.contains(newIps[i])) {
							createIps.append(",").append(newIps[i]);
							
						}else {
							ipVector.remove(newIps[i]);
						}
					}
					for (int i = 0; i < ipVector.size(); i++) {
						dropIps.append(",").append(ipVector.get(i));
					}
				}
				
				dao.dropTable(dropIps.toString());
				dao.createTable(createIps.toString());
				dao.exeBatch();
			}
				
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return missionList();
	}

	public String ready_edit() {
		int id = getParaIntValue("id");
		GatherTelnetConfigDao dao = new GatherTelnetConfigDao();
		GatherTelnetConfig vo = (GatherTelnetConfig) dao.findByID(id + "");
		request.setAttribute("vo", vo);
		String jsp = "/config/vpntelnet/gatherConfig/edit.jsp";
		return jsp;
	}

	public String setStarting() {
		int id = getParaIntValue("id");
		GatherTelnetConfigDao dao = null;
		try {
			dao = new GatherTelnetConfigDao();
			dao.updateStatus(id, 1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return missionList();

	}

	public String setShutdown() {
		int id = getParaIntValue("id");
		GatherTelnetConfigDao dao = null;
		try {
			dao = new GatherTelnetConfigDao();
			dao.updateStatus(id, 0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		return missionList();

	}

	private String downloadCfgReport() {
		String exportType = getParaValue("exportType");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String id = request.getParameter("id");
		String startdate=getParaValue("startdate");
		String todate=getParaValue("todate");
		String starttime=startdate+ " 00:00:00";
		String totime=todate+" 23:59:59";
		String time1 = sdf.format(new Date());
		if (startdate==null) {
			startdate=time1;
			starttime = time1 + " 00:00:00";
		}
		if (todate==null) {
			todate=time1;
			totime = time1 + " 23:59:59";
		}
		String ip="";
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(id));
		ip=host.getIpAddress();	
		if (id.equals("null"))
			return null;
		String allipstr = SysUtil.doip(ip);
		ReportExport export = new ReportExport();
//		CfgBaseInfoDao baseInfoDao=null;
//		PolicyInterfaceDao interfaceDao=null;
//		
//		QueueInfoDao queueInfoDao=null;
//		List policyInterfaceList=null;
//		List queueList=null;
//		List classList=null;
//		List policyList=null;
//		
//		baseInfoDao=new CfgBaseInfoDao(allipstr);
//		classList=baseInfoDao.findByCondition(" where type='class'");
//		 baseInfoDao=new CfgBaseInfoDao(allipstr);
//		 policyList=baseInfoDao.findByCondition(" where type='policy' ");
//		
//		 interfaceDao=new PolicyInterfaceDao(allipstr);
//		 queueInfoDao=new QueueInfoDao(allipstr);
//		policyInterfaceList=interfaceDao.findByCondition(" where collecttime>='"+starttime+"' and collecttime<='"+totime+"' group by interfaceName,policyName,className,collecttime");
//		queueList=queueInfoDao.findByCondition(" where collecttime>='"+starttime+"' and collecttime<='"+totime+"'");
//		CreateAmLinePic linePic = new CreateAmLinePic();
//		List<List<PolicyInterface>> sortData = linePic.getSortClassData(policyInterfaceList);
//		String[] imgpaths=export.makeJfreeChartInterface(sortData, "队列匹配及丢包曲线图(端口名:类名:指标)", "", "");
//		//String imgPath=export.makeJfreeChartData(policyInterfaceList,"发送包及丢包率","x","y");
		String filename = "";
		if (exportType.equals("xls")) {
			filename = "/temp/telnetCfgreport.xls";
		} else if (exportType.equals("doc")) {
			filename = "/temp/telnetCfgreport.doc";
		} else if (exportType.equals("pdf")) {
			filename = "/temp/telnetCfgreport.pdf";
		}
		String filePath = ResourceCenter.getInstance().getSysPath() + filename;
		
		export.exportCfgReport(allipstr,filePath, starttime, totime, exportType);
		request.setAttribute("filename", filePath);
		return "/capreport/net/download.jsp";
	}
	private String telnetCfg_netip() {
		HaweitelnetconfDao haweitelnetconfDao = new HaweitelnetconfDao();
		GatherTelnetConfigDao cfgDao=new GatherTelnetConfigDao();
		List list = null;
		String type=getParaValue("type");
		String id=getParaValue("id");
		List<GatherTelnetConfig> cfgList=null;
		try {
			list = haweitelnetconfDao.getAllTelnetConfig();
			if (type.equals("add")) {
				cfgList=cfgDao.loadAll();
			}else if (type.equals("edit")) {
				cfgList=cfgDao.findByCondition(" where id!="+id);
			}
			
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			haweitelnetconfDao.close();
			cfgDao.close();
		}
		Vector<String> vector=new Vector<String>();
		if(cfgList!=null&&cfgList.size()>0){
			
			for (int i = 0; i < cfgList.size(); i++) {
				GatherTelnetConfig config=cfgList.get(i);
				if(config!=null){
					String[] ips=config.getTelnetIps().split(",");
					if(ips!=null){
						for (int j = 0; j < ips.length; j++) {
							if(ips[j]!=null&&!ips[j].trim().equals("")){
								vector.add(ips[j]);
							}
						}
						
					}
				}
				
			}
			
		}
		request.setAttribute("list", list);
		request.setAttribute("ipVector", vector);
		return "/config/vpntelnet/gatherConfig/telnetCfg_netip.jsp";
	}
	private String telnetCfgList(){
		List<Huaweitelnetconf> list=null;
		List<GatherAcl> aclList=null;
		GatherAclDao aclDao=null;
		HaweitelnetconfDao configDao=null;
		
		try {
			 configDao = new HaweitelnetconfDao();
			 aclDao=new GatherAclDao();
			 list=configDao.loadAll();
			 aclList=aclDao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			configDao.close();
			aclDao.close();
		}
		Vector<String> ipVec=new Vector<String>();
		if (aclList!=null) {
			for (int i = 0; i <aclList.size(); i++) {
				GatherAcl acl=aclList.get(i);
				if(acl!=null){
					ipVec.add(acl.getIpaddress());
				}
			}
		}
		request.setAttribute("ipVec", ipVec);
		request.setAttribute("list", list);
		return "/config/vpntelnet/gatherAclList/telnetCfgList.jsp";
	}
	public String aclStarting() {
		String ipaddress=getParaValue("ipaddress");
		String type=getParaValue("type");
		GatherAclDao dao = null;
		GatherAcl vo=new GatherAcl();
		vo.setIpaddress(ipaddress);
		vo.setIsMonitor(1);
		if (type.equals("h3c")) {
			vo.setCommand("display acl all");
		}else if (type.equals("cisco")) {
			vo.setCommand("show access-lists");
		}
		
		try {
			dao = new GatherAclDao();
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return telnetCfgList();

	}
	public String aclShutdown() {
		String ipaddress=getParaValue("ipaddress");
		
		GatherAclDao dao = null;
		
		try {
			dao = new GatherAclDao();
			dao.deleteByIp(ipaddress);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return telnetCfgList();
		
	}
	private String downloadAclReport() {
		String exportType = getParaValue("exportType");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String id = request.getParameter("id");
		String startdate=getParaValue("startdate");
		String todate=getParaValue("todate");
		String starttime=startdate+ " 00:00:00";
		String totime=todate+" 23:59:59";
		String time1 = sdf.format(new Date());
		if (startdate==null) {
			startdate=time1;
			starttime = time1 + " 00:00:00";
		}
		if (todate==null) {
			todate=time1;
			totime = time1 + " 23:59:59";
		}
		String ip="";
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(id));
		ip=host.getIpAddress();	
		if (id.equals("null"))
			return null;
		String allipstr = SysUtil.doip(ip);
		ReportExport export = new ReportExport();
		String filename = "";
		if (exportType.equals("xls")) {
			filename = "/temp/telnetAclreport.xls";
		} else if (exportType.equals("doc")) {
			filename = "/temp/telnetAclreport.doc";
		} else if (exportType.equals("pdf")) {
			filename = "/temp/telnetAclreport.pdf";
		}
		String filePath = ResourceCenter.getInstance().getSysPath() + filename;
		
		export.exportAclReport(ip,filePath, starttime, totime, exportType);
		request.setAttribute("filename", filePath);
		return "/capreport/net/download.jsp";
	}
	public String execute(String action) {
		if (action.equals("missionList"))// 
		{
			return missionList();
		}
		if (action.equals("ready_add"))// list.jsp页面 添加
		{
			return readyAdd();
		}
		if (action.equals("add")) // list.jsp页面->添加->添加按钮
		{
			return add();
		}
		if (action.equals("delete")) {
			return delete();
		}

		if (action.equals("ready_edit")) {// list.jsp页面 右键菜单 编辑

			return ready_edit();
		}
		if (action.equals("update")) {
			return update();
		}
		if (action.equals("setStarting")) {
			return setStarting();
		}
		if (action.equals("setShutdown")) {
			return setShutdown();
		}
		if (action.equals("downloadCfgReport"))
			return downloadCfgReport();
		if (action.equals("telnetCfg_netip"))
			return telnetCfg_netip();
		
		if (action.equals("telnetCfgList"))
			return telnetCfgList();
		
		if (action.equals("aclStarting")) {
			return aclStarting();
		}
		if (action.equals("aclShutdown")) {
			return aclShutdown();
		}
		if (action.equals("downloadAclReport")) {
			return downloadAclReport();
		}
		return null;
	}

}
