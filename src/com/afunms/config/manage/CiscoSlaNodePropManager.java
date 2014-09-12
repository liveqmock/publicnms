package com.afunms.config.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.dao.SlaNodePropDao;
import com.afunms.config.model.CmdResult;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.config.model.SlaNodeProp;
import com.afunms.polling.telnet.CiscoTelnet;
import com.afunms.polling.telnet.H3CTelnet;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;

public class CiscoSlaNodePropManager extends BaseManager implements ManagerInterface{

	public String execute(String action) {
		// TODO Auto-generated method stub
		if(action.equals("list")){
			return list();
		}
		if(action.equals("cancelrtr")){
			return cancelrtr();
		}
		
		return null;
	}
	
	public String list(){
		String where = "where 1=1 ";
		String slatype = getParaValue("slatype");
		String deviceType=getParaValue("deviceType");
		
		if(slatype == null){
			slatype = "-1";
		}
		if(!slatype.equals("-1")){
			where = where + "and slatype='" + slatype + "' ";
		}
		String telnet = getParaValue("telnet");
		if(telnet == null){
			telnet = "-1";
		}
		if(!telnet.equals("-1")){
			where = where + "and telnetconfigid=" + telnet + " ";
		}
		if (deviceType==null||deviceType.equalsIgnoreCase("null")){
			deviceType="h3c";
		}
		where = where + "and bak='" + deviceType + "' ";
		SlaNodePropDao csnpDao = null;
		csnpDao = new SlaNodePropDao();
		HashMap<Integer,String> telnetMap = csnpDao.findTelnetIP();
		csnpDao.close();
		csnpDao = new SlaNodePropDao();
		List slatypeList = csnpDao.findSlaType();
		csnpDao.close();
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		List configlist = new ArrayList();
		Hashtable telconfigHash = new Hashtable();
		try{
			configlist = dao.loadAll();
			if(configlist != null && configlist.size()>0){
				for(int i=0;i<configlist.size();i++){
					Huaweitelnetconf telconfig = (Huaweitelnetconf)configlist.get(i);
					telconfigHash.put(telconfig.getId(), telconfig.getIpaddress());
				}
			}
		}catch(Exception e){
			
		}finally{
			dao.close();
		}
		UserDao userdao = new UserDao();
		List userlist = new ArrayList();
		Hashtable userHash = new Hashtable();
		try{
			userlist = userdao.loadAll();
			if(userlist != null && userlist.size()>0){
				for(int i=0;i<userlist.size();i++){
					User user = (User)userlist.get(i);
					userHash.put(user.getId(), user.getName());
				}
			}
		}catch(Exception e){
			
		}finally{
			userdao.close();
		}
		request.setAttribute("deviceType", deviceType);
		request.setAttribute("mapTelnet", telnetMap);
		request.setAttribute("listSlaType", slatypeList);
		request.setAttribute("telconfigHash", telconfigHash);
		request.setAttribute("userHash", userHash);
		csnpDao = new SlaNodePropDao();
		setTarget("/config/slanodeprop/list.jsp");
		return list(csnpDao,where);
	}
	
	private String cancelrtr() {
		String id = getParaValue("id");
		//SysLogger.info("id==============="+id);
		SlaNodePropDao slapropDao = new SlaNodePropDao();
		HaweitelnetconfDao haweitelnetconfDao = new HaweitelnetconfDao();
		SlaNodeProp vo = null;
		Huaweitelnetconf telnetconfig = null;
		try{
			vo = (SlaNodeProp)slapropDao.findByID(id);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			slapropDao.close();
		}
		try{
			telnetconfig = (Huaweitelnetconf)haweitelnetconfDao.findByID(vo.getTelnetconfigid()+"");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			haweitelnetconfDao.close();
		}
		
		
		List deviceList = null;
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
//		try {
//			deviceList = haweitelnetconfDao.getAllTelnetConfig();
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			haweitelnetconfDao.close();
//		}
//		request.setAttribute("list", deviceList);
		User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
//		// String ip=getParaValue("netip");
		String[] ips = getParaArrayValue("checkbox");
//
//		String commands = getParaValue("commands");
//		String cmdid = getParaValue("cmdid");
//		String slatype = getParaValue("slatype");
//		String isReturn = getParaValue("isReturn");
//		if (isReturn == null)
//			isReturn = "0";
//		String result = "";
//		HaweitelnetconfDao dao = new HaweitelnetconfDao();
//		Huaweitelnetconf vo = null;
//		List<Huaweitelnetconf> list = new ArrayList<Huaweitelnetconf>();
		StringBuffer sBuffer = new StringBuffer();
		List<CmdResult> resultList = new ArrayList<CmdResult>();
//		CiscoSlaCfgCmdFile slaconfig = null;
//		SlaCfgCmdFileDao slaconfigdao = new SlaCfgCmdFileDao();
//		try{
//			slaconfig = (CiscoSlaCfgCmdFile)slaconfigdao.findByID(cmdid);
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			slaconfigdao.close();
//		}
//		if (ips != null && ips.length > 0) {
//
//			try {
//				list = (List) dao.loadByIps(ips);
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				dao.close();
//			}
//			String[] commStr = new String[commands.split("\r\n").length];
//			commStr = commands.split("\r\n");
//
			//for (int i = 0; i < list.size(); i++) {
				//vo = slapropDao.findByID(id);

				if (telnetconfig.getDeviceRender().equals("h3c")) {// h3c
//					Huawei3comvpn tvpn = new Huawei3comvpn();
//					tvpn.setSuuser(vo.getSuuser());// su
//					tvpn.setSupassword(vo.getSupassword());// suÃÜÂë
//					tvpn.setUser(vo.getUser());// ÓÃ»§
//					tvpn.setPassword(vo.getPassword());// ÃÜÂë
//					tvpn.setIp(vo.getIpaddress());// ipµØÖ·
//					tvpn.setDEFAULT_PROMPT(vo.getDefaultpromtp());// ½áÊø±ê¼Ç·ûºÅ
//					tvpn.setPort(vo.getPort());
//					if (isReturn.equals("0")) {
//						tvpn.getCommantValue(commStr, resultList, ips[i]);
//					} else if (isReturn.equals("1")) {
//						result = tvpn.BackupConfFile(commStr);
//					}
					H3CTelnet tvpn = new H3CTelnet(telnetconfig.getIpaddress(), telnetconfig.getUser(), telnetconfig.getPassword(), telnetconfig.getPort(), telnetconfig.getSuuser(), telnetconfig.getSupassword(), telnetconfig.getDefaultpromtp());
					tvpn.cancelNqaCommantValue(resultList, telnetconfig.getIpaddress(), user, vo);
				} else if (telnetconfig.getDeviceRender().equals("cisco")) {// cisco
					CiscoTelnet telnet = new CiscoTelnet(telnetconfig.getIpaddress(), telnetconfig
							.getUser(), telnetconfig.getPassword(),telnetconfig.getPort());
					if (telnet.login()) {
//						if (isReturn.equals("0")) {
							telnet.cancelSlaCommantValue(telnetconfig.getSupassword(),
									resultList, telnetconfig,user,vo);
//						} else if (isReturn.equals("1")) {
//							result = telnet.getFileCfg(vo.getSupassword(), commStr);
//						}	
					} else {
						CmdResult cmdResult = new CmdResult();
						cmdResult.setIp(telnetconfig.getIpaddress());
						cmdResult.setCommand("------");
						cmdResult.setResult("µÇÂ¼Ê§°Ü!");
						resultList.add(cmdResult);
					}

				}
				//sBuffer.append(result + "\r\n");
			//}
//		}
//
//		request.setAttribute("commands", commands);
//		request.setAttribute("isReturn", isReturn);
//		request.setAttribute("ips", ips);
		request.setAttribute("content", sBuffer.toString()+"");
		request.setAttribute("resultList", resultList);
		return "/ciscoslaproperty.do?action=list&jp=1";
	}

}
