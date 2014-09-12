package com.afunms.application.manage;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.alarm.service.NodeAlarmService;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.HostApplyDao;
import com.afunms.application.model.ApacheConfig;
import com.afunms.application.model.CicsConfig;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.DHCPConfig;
import com.afunms.application.model.DnsConfig;
import com.afunms.application.model.DominoConfig;
import com.afunms.application.model.EmailMonitorConfig;
import com.afunms.application.model.FTPConfig;
import com.afunms.application.model.HostApply;
import com.afunms.application.model.HostApplyModel;
import com.afunms.application.model.IISConfig;
import com.afunms.application.model.JBossConfig;
import com.afunms.application.model.MQConfig;
import com.afunms.application.model.Resin;
import com.afunms.application.model.TFTPConfig;
import com.afunms.application.model.Tomcat;
import com.afunms.application.model.TuxedoConfig;
import com.afunms.application.model.WasConfig;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.Constant;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Oct 9, 2011 1:56:12 PM
 * 类说明 服务器应用设置
 */
public class HostApplyManager extends BaseManager implements ManagerInterface{
	/**
	 * 工程名
	 */
	private static String rootPath;
	/**
	 * 数据库类型map
	 */
	private static Map<Integer, String> dbTypeMap = new HashMap<Integer, String>();
	
	static{
		DBTypeDao typedao = new DBTypeDao();
		try {
			List list = typedao.loadAll();
			if(list != null){
				for(int i=0; i<list.size(); i++){
					DBTypeVo dbTypeVo = (DBTypeVo)list.get(i);
					dbTypeMap.put(dbTypeVo.getId(), dbTypeVo.getDbtype());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			typedao.close();
		}
	}

	public String execute(String action) {
		rootPath = request.getContextPath();
		if("list".equals(action)){
			return list();
		}else if("serverview".equals(action)){
			return serverview();
		}else if("show".equals(action)){
			return show();
		}else if("allList".equals(action)){
			return allList();
		}else if("allShow".equals(action)){
			return allShow();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String serverview() {
	    String freshTimeMinute = getParaValue("freshTimeMinute");
	    if(freshTimeMinute == null){
	        freshTimeMinute = "300";//页面默认为60秒刷新一次
	    }
	    Hashtable<String, HostApply> hostApplyHash = getHostApplyHash(getList());
	    request.setAttribute("freshTimeMinute", freshTimeMinute);
	    request.setAttribute("hostApplyHash", hostApplyHash);
	    return "/application/hostApply/serverview.jsp";
	}
	//	/**
//	 * 主机视图（主机和应用的二维列表显示图）
//	 * @return
//	 */
//	private String serverview(){
//		String nodeid = getParaValue("nodeid");
//		String type = getParaValue("type");
//		String ipaddress=getParaValue("ipaddress");
//		String subtype = getParaValue("subtype");
//		
//		String freshTimeMinute = getParaValue("freshTimeMinute");
//		if(freshTimeMinute == null){
//			freshTimeMinute = "300";//页面默认为60秒刷新一次
//		}
//		
//		//根据IP地址得到所有的应用
////		List<HostApplyModel> hostApplyList = getHostApplyList(ipaddress);
//		//得到所有的服务器
//		HostNodeDao hostNodeDao = null;
//		List<HostNode> hostNodeList = null;
//		try{
//			hostNodeDao = new HostNodeDao();
//			hostNodeList = hostNodeDao.findByCondition(" where managed=1 and category=4 " + getBidSql());
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			if(hostNodeDao != null){
//				hostNodeDao.close();
//			}
//		}
//		
//		List<HostApplyModel> hostApplyList = null;
//		HostApplyDao hostApplyDao = null;
//		try{
//			hostApplyDao = new HostApplyDao();
//			hostApplyList = hostApplyDao.findByCondition(" ");
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			if(hostApplyDao != null){
//				hostApplyDao.close();
//			}
//		}
//		Hashtable<String,HostApply> hostApplyHash = getHostApplyHash(hostApplyList, hostNodeList);
//
//		request.setAttribute("freshTimeMinute", freshTimeMinute);
//		request.setAttribute("hostApplyHash", hostApplyHash);
//		return "/application/hostApply/serverview.jsp";
//	}
	
	
	public String allList(){
        request.setAttribute("hostApplyHash", getHostApplyHash(getList()));
        return "/application/hostApply/allList.jsp";
    }

	//	/**
//	 * 所有的服务器应用的列表
//	 * @return
//	 */
//	public String allList(){
//		String type = getParaValue("type");
//		String subtype = getParaValue("subtype");
//		
//		//得到所有的服务器
//		HostNodeDao hostNodeDao = null;
//		List<HostNode> hostNodeList = null;
//		try{
//			hostNodeDao = new HostNodeDao();
//			hostNodeList = hostNodeDao.findByCondition(" where managed=1 and category=4 " + getBidSql());
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			if(hostNodeDao != null){
//				hostNodeDao.close();
//			}
//		}
//		
//		//根据IP地址得到所有的应用
////		List<HostApplyModel> hostApplyList = getHostApplyList(ipaddress);
//		List<HostApplyModel> hostApplyList = null;
//		HostApplyDao hostApplyDao = null;
//		try{
//			hostApplyDao = new HostApplyDao();
//			hostApplyList = hostApplyDao.findByCondition("");
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			if(hostApplyDao != null){
//				hostApplyDao.close();
//			}
//		}
//		
//		Hashtable<String,HostApply> hostApplyHash = getHostApplyHash(hostApplyList, hostNodeList);
//		
//		request.setAttribute("type", type);
//		request.setAttribute("subtype", subtype);
//		request.setAttribute("hostApplyHash", hostApplyHash);
//		return "/application/hostApply/allList.jsp";
//	}
	

	private Hashtable<String, HostApplyModel> getUserHostApplyModelHashtable() {
	    User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
	    List<HostApplyModel> list = null;
        HostApplyDao dao = new HostApplyDao();
        try {
            list = dao.findByUserId(user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dao.close();
        }
        Hashtable<String, HostApplyModel> userHostApplyHash = new Hashtable<String, HostApplyModel>();
        for (HostApplyModel hostApplyModel : list) {
            userHostApplyHash.put(hostApplyModel.getIpaddres() + ":" + hostApplyModel.getSubtype(), hostApplyModel);
        }
        return userHostApplyHash;
	}
	/**
	 * 获取该服务器的相关应用的列表
	 * @return
	 */
	public String list(){
		String nodeid = getParaValue("nodeid");
		String type = getParaValue("type");
		String ipaddress=getParaValue("ipaddress");
		String subtype = getParaValue("subtype");
		
		//得到当前的服务器
		List<HostNode> hostNodeList = new ArrayList<HostNode>();
		HostNode hostNode = new HostNode();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeid));
		//集合copy工具  
		try {
			BeanUtils.copyProperties(hostNode,node);
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} 
		node.setIpAddress(ipaddress);
		hostNodeList.add(hostNode);
		
		//根据IP地址得到所有的应用
//		List<HostApplyModel> hostApplyList = getHostApplyList(ipaddress);
		List<HostApplyModel> hostApplyList = null;
		HostApplyDao hostApplyDao = null;
		try{
			hostApplyDao = new HostApplyDao();
			hostApplyList = hostApplyDao.findByCondition(" where ipaddress = '"+ipaddress+"'");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(hostApplyDao != null){
				hostApplyDao.close();
			}
		}
		
		Hashtable<String,HostApply> hostApplyHash = getHostApplyHash(hostApplyList, hostNodeList);
		
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("type", type);
		request.setAttribute("subtype", subtype);
		request.setAttribute("hostApplyHash", hostApplyHash);
		request.setAttribute("nodeid", nodeid);
		return "/application/hostApply/list.jsp";
	}

    public Hashtable<String,HostApply> getHostApplyHash(List<BaseVo> list) {
        NodeAlarmService service = new NodeAlarmService();
        Hashtable<String, HostApply> hostApplyHash = new Hashtable<String, HostApply>();
        NodeUtil util = new NodeUtil();
        Hashtable<String, HostApplyModel> userHostApplyModelHashtable = getUserHostApplyModelHashtable();
        for (BaseVo baseVo : list) {
            NodeDTO node = util.conversionToNodeDTO(baseVo);
            String ipaddress = node.getIpaddress();
            HostApply hostApply = hostApplyHash.get(ipaddress);
            if (hostApply == null) {
                hostApply = new HostApply();
            }
            int maxAlarmLevel = service.getMaxAlarmLevel(baseVo);
            String subtype = node.getSubtype();
            String name = node.getName();
            HostApplyModel applyModel = userHostApplyModelHashtable.get(ipaddress + ":" + node.getSubtype());
            boolean isShow = true;//默认设置显示
            if (applyModel != null) {
                isShow = applyModel.isShow();
            }
            
            if (Constant.TYPE_DB_SUBTYPE_ORACLE.equals(subtype)) {
                hostApply.setOracleName(name);
                String status = hostApply.getOracleStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setOracleStatus(status);
                hostApply.setOracleIsShow(isShow);
            } else if (Constant.TYPE_DB_SUBTYPE_MYSQL.equals(subtype)) {
                hostApply.setMysqlName(name);
                String status = hostApply.getMysqlStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setMysqlStatus(status);
                hostApply.setMysqlIsShow(isShow);
            } else if (Constant.TYPE_DB_SUBTYPE_DB2.equals(subtype)) {
                hostApply.setDb2Name(name);
                String status = hostApply.getDb2Status();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setDb2Status(status);
                hostApply.setDb2IsShow(isShow);
            } else if (Constant.TYPE_DB_SUBTYPE_INFORMIX.equals(subtype)) {
                hostApply.setInformixName(name);
                String status = hostApply.getInformixStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setInformixStatus(status);
                hostApply.setInformixIsShow(isShow);
            } else if (Constant.TYPE_DB_SUBTYPE_SQLSERVER.equals(subtype)) {
                hostApply.setSqlserverName(name);
                String status = hostApply.getSqlserverStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setSqlserverStatus(status);
                hostApply.setSqlserverIsShow(isShow);
            } else if (Constant.TYPE_DB_SUBTYPE_SYBASE.equals(subtype)) {
                hostApply.setSybaseName(name);
                String status = hostApply.getSybaseStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setSybaseStatus(status);
                hostApply.setSybaseIsShow(isShow);
            } else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_MQ.equals(subtype)) {
                hostApply.setMqName(name);
                String status = hostApply.getMqStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setMqStatus(status);
                hostApply.setMqIsShow(isShow);
            } else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_DOMINO.equals(subtype)) {
                hostApply.setDominoName(name);
                String status = hostApply.getDominoStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setDominoStatus(status);
                hostApply.setDominoIsShow(isShow);
            } else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_WAS.equals(subtype)) {
                hostApply.setWasName(name);
                String status = hostApply.getWasStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setWasStatus(status);
                hostApply.setWasIsShow(isShow);
            } else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_TOMCAT.equals(subtype)) {
                hostApply.setTomcatName(name);
                String status = hostApply.getTomcatStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setTomcatStatus(status);
                hostApply.setTomcatIsShow(isShow);
            } else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_IIS.equals(subtype)) {
                hostApply.setIisName(name);
                String status = hostApply.getIisStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setIisStatus(status);
                hostApply.setIisIsShow(isShow);
            } else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_JBOSS.equals(subtype)) {
                hostApply.setJbossName(name);
                String status = hostApply.getJbossStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setJbossStatus(status);
                hostApply.setJbossIsShow(isShow);
            } else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_APACHE.equals(subtype)) {
                hostApply.setApacheName(name);
                String status = hostApply.getApacheStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setApacheStatus(status);
                hostApply.setApacheIsShow(isShow);
            } else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_TUXEDO.equals(subtype)) {
                hostApply.setTuxedoName(name);
                String status = hostApply.getTuxedoStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setTuxedoStatus(status);
                hostApply.setTuxedoIsShow(isShow);
            } else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_CICS.equals(subtype)) {
                hostApply.setCicsName(name);
                String status = hostApply.getCicsStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setCicsStatus(status);
                hostApply.setCicsIsShow(isShow);
            } else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_DNS.equals(subtype)) {
                hostApply.setDnsName(name);
                String status = hostApply.getDnsStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setDnsStatus(status);
                hostApply.setDnsIsShow(isShow);
            } else if (Constant.TYPE_MIDDLEWARE_SUBTYPE_WEBLOGIC.equals(subtype)) {
                hostApply.setWeblogicName(name);
                String status = hostApply.getWeblogicStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setWeblogicStatus(status);
                hostApply.setWeblogicIsShow(isShow);
            } else if (Constant.TYPE_SERVICE_SUBTYPE_FTP.equals(subtype)) {
                hostApply.setFtpName(name);
                String status = hostApply.getFtpStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setFtpStatus(status);
                hostApply.setFtpIsShow(isShow);
            } else if (Constant.TYPE_SERVICE_SUBTYPE_EMAIL.equals(subtype)) {
                hostApply.setEmailName(name);
                String status = hostApply.getEmailStatus();
                if (status == null || Integer.parseInt(status) < maxAlarmLevel) {
                    status = String.valueOf(maxAlarmLevel);
                }
                hostApply.setEmailStatus(status);
                hostApply.setEmailShow(isShow);
            }
            hostApplyHash.put(ipaddress, hostApply);
        }
        return hostApplyHash;
    }

    private List<BaseVo> getList() {
        User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
        NodeUtil util = new NodeUtil();
        if (!SessionConstant.isRoleAdmin(user.getRole())) {
            util.setBid(user.getBusinessids());
        }
        List<BaseVo> list = new ArrayList<BaseVo>();
        list.addAll(util.getNodeByTyeAndSubtype(Constant.TYPE_HOST, Constant.ALL_SUBTYPE));
        list.addAll(util.getNodeByTyeAndSubtype(Constant.TYPE_DB, Constant.ALL_SUBTYPE));
        list.addAll(util.getNodeByTyeAndSubtype(Constant.TYPE_MIDDLEWARE, Constant.ALL_SUBTYPE));
        list.addAll(util.getNodeByTyeAndSubtype(Constant.TYPE_SERVICE, Constant.ALL_SUBTYPE));
        return list;
    }
    /**
	 * 返回页面展示的应用模型列表
	 * @param list          存放了服务器应用模型列表
	 * @param hostNodeList  系统的服务器集合
	 * @return  
	 */
	public Hashtable<String,HostApply> getHostApplyHash(List<HostApplyModel> list, List<HostNode> hostNodeList){
		Hashtable<String,HostApply> retHash = new Hashtable<String,HostApply>();
		NodeAlarmService service = new NodeAlarmService();
		if(list != null){
			for(int i=0; i<list.size(); i++){
				HostApplyModel hostApplyModel = list.get(i);//系统的某一应用
				String ipaddress = hostApplyModel.getIpaddres();
				if(!retHash.containsKey(ipaddress)){//如果此map集合中不包含该设备的应用信息,新建应用
					HostApply hostApply = new HostApply();
					retHash.put(ipaddress, hostApply);
				}
				HostApply hostApply = retHash.get(ipaddress);
				int applyId = hostApplyModel.getNodeid();//应用的id
				String status = null;
				if(hostApplyModel.getSubtype().equalsIgnoreCase("oracle")){
					Node dbNode = PollingEngine.getInstance().getDbByID(applyId);
					if (dbNode == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(dbNode) + "";
						hostApply.setOracleName(dbNode.getAlias());
					}
					//oracle为多版本并存的情况，当前oracle应用的状态为告警级别最高的状态
					if(hostApply.getOracleStatus() == null){
						hostApply.setOracleStatus(status);
					} else if(Integer.parseInt(hostApply.getOracleStatus()) < Integer.parseInt(status)){
						hostApply.setOracleStatus(status);
					} 
					//设置当前服务器上的oracle是否显示
					hostApply.setOracleIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("sqlserver")){
					Node dbNode = PollingEngine.getInstance().getDbByID(applyId);
					if (dbNode == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(dbNode) + "";
						hostApply.setSqlserverName(dbNode.getAlias());
					}
					//sqlserver为多版本并存的情况，当前sqlserver应用的状态为告警级别最高的状态
					if(hostApply.getSqlserverStatus() == null){
						hostApply.setSqlserverStatus(status);
					} else if(Integer.parseInt(hostApply.getSqlserverStatus()) < Integer.parseInt(status)){
						hostApply.setSqlserverStatus(status);
					} 
					//设置当前服务器上的sqlserver是否显示
					hostApply.setSqlserverIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("db2")){
					Node dbNode = PollingEngine.getInstance().getDbByID(applyId);
					if (dbNode == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(dbNode) + "";
						hostApply.setDb2Name(dbNode.getAlias());
					}
					//db2为多版本并存的情况，当前db2应用的状态为告警级别最高的状态
					if(hostApply.getDb2Status() == null){
						hostApply.setDb2Status(status);
					} else if(Integer.parseInt(hostApply.getDb2Status()) < Integer.parseInt(status)){
						hostApply.setDb2Status(status);
					} 
					//设置当前服务器上的db2是否显示
					hostApply.setDb2IsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("sybase")){
					Node dbNode = PollingEngine.getInstance().getDbByID(applyId);
					if (dbNode == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(dbNode) + "";
						hostApply.setSybaseName(dbNode.getAlias());
					}
					//sybase为多版本并存的情况，当前sybase应用的状态为告警级别最高的状态
					if(hostApply.getSybaseStatus() == null){
						hostApply.setSybaseStatus(status);
					} else if(Integer.parseInt(hostApply.getSybaseStatus()) < Integer.parseInt(status)){
						hostApply.setSybaseStatus(status);
					} 
					//设置当前服务器上的sybase是否显示
					hostApply.setSybaseIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("mysql")){
					Node dbNode = PollingEngine.getInstance().getDbByID(applyId);
					if (dbNode == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(dbNode) + "";
						hostApply.setMysqlName(dbNode.getAlias());
					}
					//mysql为多版本并存的情况，当前mysql应用的状态为告警级别最高的状态
					if(hostApply.getMysqlStatus() == null){
						hostApply.setMysqlStatus(status);
					} else if(Integer.parseInt(hostApply.getMysqlStatus()) < Integer.parseInt(status)){
						hostApply.setMysqlStatus(status);
					} 
					//设置当前服务器上的mysql是否显示
					hostApply.setMysqlIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("informix")){
					Node dbNode = PollingEngine.getInstance().getDbByID(applyId);
					if (dbNode == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(dbNode) + "";
						hostApply.setInformixName(dbNode.getAlias());
					}
					//informix为多版本并存的情况，当前informix应用的状态为告警级别最高的状态
					if(hostApply.getInformixStatus() == null){
						hostApply.setInformixStatus(status);
					} else if(Integer.parseInt(hostApply.getInformixStatus()) < Integer.parseInt(status)){
						hostApply.setInformixStatus(status);
					} 
					//设置当前服务器上的informix是否显示
					hostApply.setInformixIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("ftp")){
					Node node = PollingEngine.getInstance().getFtpByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(node) + "";
						hostApply.setFtpName(node.getAlias());
					}
					//ftp为多版本并存的情况，当前ftp应用的状态为告警级别最高的状态
					if(hostApply.getFtpStatus() == null){
						hostApply.setFtpStatus(status);
					} else if(Integer.parseInt(hostApply.getFtpStatus()) < Integer.parseInt(status)){
						hostApply.setFtpStatus(status);
					} 
					//设置当前服务器上的ftp是否显示
					hostApply.setFtpIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("email")){
					Node node = PollingEngine.getInstance().getMailByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(node) + "";
						hostApply.setEmailName(node.getAlias());
					}
					//email为多版本并存的情况，当前email应用的状态为告警级别最高的状态
					if(hostApply.getEmailStatus() == null){
						hostApply.setEmailStatus(status);
					} else if(Integer.parseInt(hostApply.getEmailStatus()) < Integer.parseInt(status)){
						hostApply.setEmailStatus(status);
					} 
					//设置当前服务器上的email是否显示
					hostApply.setEmailShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("telnet")){
	//				hostApply.setTelnetName(alias);
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("mq")){
					Node node = PollingEngine.getInstance().getMqByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(node) + "";
						hostApply.setMqName(node.getAlias());
					}
					//mq为多版本并存的情况，当前mq应用的状态为告警级别最高的状态
					if(hostApply.getMqStatus() == null){
						hostApply.setMqStatus(status);
					} else if(Integer.parseInt(hostApply.getMqStatus()) < Integer.parseInt(status)){
						hostApply.setMqStatus(status);
					} 
					//设置当前服务器上的mq是否显示
					hostApply.setMqIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("domino")){
					Node node = PollingEngine.getInstance().getDominoByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(node) + "";
						hostApply.setDominoName(node.getAlias());
					}
					//domino为多版本并存的情况，当前domino应用的状态为告警级别最高的状态
					if(hostApply.getDominoStatus() == null){
						hostApply.setDominoStatus(status);
					} else if(Integer.parseInt(hostApply.getDominoStatus()) < Integer.parseInt(status)){
						hostApply.setDominoStatus(status);
					} 
					//设置当前服务器上的domino是否显示
					hostApply.setDominoIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("was")){
					Node node = PollingEngine.getInstance().getWasByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(node) + "";
						hostApply.setWasName(node.getAlias());
					}
					//was为多版本并存的情况，当前was应用的状态为告警级别最高的状态
					if(hostApply.getWasStatus() == null){
						hostApply.setWasStatus(status);
					} else if(Integer.parseInt(hostApply.getWasStatus()) < Integer.parseInt(status)){
						hostApply.setWasStatus(status);
					} 
					//设置当前服务器上的was是否显示
					hostApply.setWasIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("weblogic")){
					Node node = PollingEngine.getInstance().getWeblogicByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(node) + "";
						hostApply.setWeblogicName(node.getAlias());
					}
					//weblogic为多版本并存的情况，当前weblogic应用的状态为告警级别最高的状态
					if(hostApply.getWeblogicStatus() == null){
						hostApply.setWeblogicStatus(status);
					} else if(Integer.parseInt(hostApply.getWeblogicStatus()) < Integer.parseInt(status)){
						hostApply.setWeblogicStatus(status);
					} 
					//设置当前服务器上的weblogic是否显示
					hostApply.setWeblogicIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("tomcat")){
					Node node = PollingEngine.getInstance().getTomcatByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(node) + "";
						hostApply.setTomcatName(node.getAlias());
					}
					//tomcat为多版本并存的情况，当前tomcat应用的状态为告警级别最高的状态
					if(hostApply.getTomcatStatus() == null){
						hostApply.setTomcatStatus(status);
					} else if(Integer.parseInt(hostApply.getTomcatStatus()) < Integer.parseInt(status)){
						hostApply.setTomcatStatus(status);
					} 
					//设置当前服务器上的tomcat是否显示
					hostApply.setTomcatIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("iis")){
					Node node = PollingEngine.getInstance().getIisByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(node) + "";
						hostApply.setIisName(node.getAlias());
					}
					//iis为多版本并存的情况，当前iis应用的状态为告警级别最高的状态
					if(hostApply.getIisStatus() == null){
						hostApply.setIisStatus(status);
					} else if(Integer.parseInt(hostApply.getIisStatus()) < Integer.parseInt(status)){
						hostApply.setIisStatus(status);
					} 
					//设置当前服务器上的iis是否显示
					hostApply.setIisIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("cics")){
					Node node = PollingEngine.getInstance().getCicsByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(node) + "";
						hostApply.setCicsName(node.getAlias());
					}
					//cics为多版本并存的情况，当前iis应用的状态为告警级别最高的状态
					if(hostApply.getCicsStatus() == null){
						hostApply.setCicsStatus(status);
					} else if(Integer.parseInt(hostApply.getCicsStatus()) < Integer.parseInt(status)){
						hostApply.setCicsStatus(status);
					} 
					//设置当前服务器上的cics是否显示
					hostApply.setCicsIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("dns")){
					Node node = PollingEngine.getInstance().getDnsByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(node) + "";
						hostApply.setDnsName(node.getAlias());
					}
					//dns为多版本并存的情况，当前iis应用的状态为告警级别最高的状态
					if(hostApply.getDnsStatus() == null){
						hostApply.setDnsStatus(status);
					} else if(Integer.parseInt(hostApply.getDnsStatus()) < Integer.parseInt(status)){
						hostApply.setDnsStatus(status);
					} 
					//设置当前服务器上的dns是否显示
					hostApply.setDnsIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("jboss")){
					Node node = PollingEngine.getInstance().getJBossByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(node) + "";
						hostApply.setJbossName(node.getAlias());
					}
					//jboss为多版本并存的情况，当前iis应用的状态为告警级别最高的状态
					if(hostApply.getJbossStatus() == null){
						hostApply.setJbossStatus(status);
					} else if(Integer.parseInt(hostApply.getJbossStatus()) < Integer.parseInt(status)){
						hostApply.setJbossStatus(status);
					} 
					//设置当前服务器上的jboss是否显示
					hostApply.setJbossIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("apache")){
					Node node = PollingEngine.getInstance().getApacheByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(node) + "";
						hostApply.setApacheName(node.getAlias());
					}
					//apache为多版本并存的情况，当前iis应用的状态为告警级别最高的状态
					if(hostApply.getApacheStatus() == null){
						hostApply.setApacheStatus(status);
					} else if(Integer.parseInt(hostApply.getApacheStatus()) < Integer.parseInt(status)){
						hostApply.setApacheStatus(status);
					} 
					//设置当前服务器上的apache是否显示
					hostApply.setApacheIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("tuxedo")){
					Node node = PollingEngine.getInstance().getTuxedoById(applyId);
					if (node == null) { 
						status = "0";
					} else {
						status = service.getMaxAlarmLevel(node) + "";
						hostApply.setTuxedoName(node.getAlias()); 
					}
					//tuxdeo为多版本并存的情况，当前iis应用的状态为告警级别最高的状态
					if(hostApply.getTuxedoStatus() == null){
						hostApply.setTuxedoStatus(status);
					} else if(Integer.parseInt(hostApply.getTuxedoStatus()) < Integer.parseInt(status)){
						hostApply.setTuxedoStatus(status);
					} 
					//设置当前服务器上的tuxdeo是否显示 
					hostApply.setTuxedoIsShow(hostApplyModel.isShow());
				}
			}
		}
		
		//
		if(hostNodeList != null){
			for(int i=0; i<hostNodeList.size(); i++){
				HostNode hostNode = hostNodeList.get(i);
				if(!retHash.containsKey(hostNode.getIpAddress())){//如果此map集合中不包含该设备的应用信息,新建应用
					HostApply hostApply = new HostApply();
					retHash.put(hostNode.getIpAddress(), hostApply);
				}
			}
		}
		return retHash;
	}
	
	/**
	 * 节点状态标志
	 */
	public static String getCurrentStatusImage(int status) {
		String image = null;
		if (status == 0){
			image = "a_level_0.gif";
		}else if (status == 1){
			image = "a_level_1.gif";
		}else if (status == 2){
			image = "a_level_2.gif";
		}else if (status == 3){
			 image = "a_level_3.gif";
		}else{
			image = "small7.png";
			return null;
		}
		return rootPath+"/resource/image/topo/" + image;
	}
	
	/**
	 * 将对象转换为服务器对应的应用来保存
	 * @param obj  需要保存的对象
	 */
	public static synchronized void save(Object obj){
		if(obj == null){
			return;
		}
		HostApplyModel hostApplyModel = new HostApplyModel();
//		Node node = null;
		//数据库的情况
		if(obj instanceof DBVo){
			DBVo vo = (DBVo) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpAddress());
			hostApplyModel.setShow(true);
			String subtype = dbTypeMap.get(vo.getDbtype());
			hostApplyModel.setSubtype(subtype);
			hostApplyModel.setType("db");
		}
		//Tomcat中间件的情况
		if(obj instanceof Tomcat){
			Tomcat vo = (Tomcat) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpAddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("tomcat");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getTomcatByID(vo.getId());
		}
		//Tomcat中间件的情况
		if(obj instanceof Resin){
			Resin vo = (Resin) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpAddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("resin");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getTomcatByID(vo.getId());
		}
		//mq中间件的情况
		if(obj instanceof MQConfig){
			MQConfig vo = (MQConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("mq");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getMqByID(vo.getId());
		}
		//domino中间件的情况
		if(obj instanceof DominoConfig){
			DominoConfig vo = (DominoConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("domino");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getDominoByID(vo.getId());
		}
		//was中间件的情况
		if(obj instanceof WasConfig){
			WasConfig vo = (WasConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("was");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getWasByID(vo.getId());
		}
		//weblogic中间件的情况
		if(obj instanceof WeblogicConfig){
			WeblogicConfig vo = (WeblogicConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpAddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("weblogic");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getWeblogicByID(vo.getId());
		}
		//JBoss中间件的情况
		if(obj instanceof JBossConfig){
			JBossConfig vo = (JBossConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("jboss");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getJBossByID(vo.getId());
		}
		//apache中间件的情况
		if(obj instanceof ApacheConfig){
			ApacheConfig vo = (ApacheConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("apache");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getApacheByID(vo.getId());
		}
		//tuxedo中间件的情况
		if(obj instanceof TuxedoConfig){
			TuxedoConfig vo = (TuxedoConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpAddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("tuxedo");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getTuxedoById(vo.getId());
		}
		//IIS中间件的情况
		if(obj instanceof IISConfig){
			IISConfig vo = (IISConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("iis");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getIisByID(vo.getId());
		}
		//cics中间件的情况
		if(obj instanceof CicsConfig){
			CicsConfig vo = (CicsConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("cics");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getCicsByID(vo.getId());
		}
		//dns中间件的情况
		if(obj instanceof DnsConfig){
			DnsConfig vo = (DnsConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getHostip());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("dns");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getDnsByID(vo.getId());
		}
		//ftp服务
		if(obj instanceof FTPConfig){
			FTPConfig vo = (FTPConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("ftp");
			hostApplyModel.setType("service");
//			node = PollingEngine.getInstance().getFtpByID(vo.getId());
		}
		//tftp服务
		if(obj instanceof TFTPConfig){
			TFTPConfig vo = (TFTPConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("tftp");
			hostApplyModel.setType("service");
//			node = PollingEngine.getInstance().getFtpByID(vo.getId());
		}
		//DHCP服务
		if(obj instanceof DHCPConfig){
			DHCPConfig vo = (DHCPConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpAddress());
			hostApplyModel.setShow(true);
			if("windows".equalsIgnoreCase(vo.getDhcptype())){
				hostApplyModel.setSubtype("windhcp");
			}else if("cisco".equalsIgnoreCase(vo.getDhcptype())){
				hostApplyModel.setSubtype("ciscodhcp");
			}
			
			hostApplyModel.setType("service");
//			node = PollingEngine.getInstance().getFtpByID(vo.getId());
		}
		//email服务
		if(obj instanceof EmailMonitorConfig){
			EmailMonitorConfig vo = (EmailMonitorConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("email");
			hostApplyModel.setType("service");
//			node = PollingEngine.getInstance().getMailByID(vo.getId());
		}
		//telnet服务
		//保存服务器应用信息
		HostApplyDao hostApplyDao = null;
		try{
			hostApplyDao = new HostApplyDao();
			hostApplyDao.save(hostApplyModel);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(hostApplyDao != null){
				hostApplyDao.close();
			}
		}
	}
	
	/**
	 * 获得业务权限的 SQL 语句
	 * @return
	 */
	public String getBidSql(){
		User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null){
			if(current_user.getBusinessids() !="-1"){
				String[] bids = current_user.getBusinessids().split(",");
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
//		SysLogger.info("select * from topo_host_node where managed=1 "+s);
		String sql = "";
		if(current_user.getRole() == 0){
			sql = "";
		}else{
			sql = s.toString();
		}
		String treeBid = request.getParameter("treeBid");
		if(treeBid != null && treeBid.trim().length() > 0){
			treeBid = treeBid.trim();
			treeBid = "," + treeBid + ",";
			String[] treeBids = treeBid.split(",");
			if(treeBids != null){
				for(int i = 0; i < treeBids.length; i++){
					if(treeBids[i].trim().length() > 0){
						sql = sql + " and " + "bid" + " like '%," + treeBids[i].trim() + ",%'";
					}
				}
			}
		}
		request.setAttribute("treeBid", treeBid);
		return sql;
	}
	
	/**
	 * 显示/隐藏应用
	 * @return
	 */
	public String show(){
		String ipaddress = getParaValue("ipaddress");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		String nodeid = getParaValue("nodeid");
		
		//ip地址:类型 数组 
		String[] ipAndSubTypes = getParaArrayValue("checkbox");
		String modifyFlag = getParaValue("modifyFlag");
		boolean isShow = Boolean.parseBoolean(modifyFlag);
		HostApplyDao hostApplyDao = null;
		try{
			hostApplyDao = new HostApplyDao();
			hostApplyDao.batchUpdateSingleIsShow(ipAndSubTypes, isShow, ipaddress);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(hostApplyDao != null){
				hostApplyDao.close();
			}
		}
		request.setAttribute("type", type);
		request.setAttribute("subtype", subtype);
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("nodeid", nodeid);
		return list();
	}
	
	/**
	 * 显示/隐藏应用
	 * @return
	 */
	public String allShow(){
		//ip地址:类型 数组 
	    List<BaseVo> baseVoList = getList();
	    NodeUtil util = new NodeUtil();
	    Hashtable<String, NodeDTO> hashtable = new Hashtable<String, NodeDTO>();
	    for (BaseVo baseVo : baseVoList) {
	        NodeDTO node = util.conversionToNodeDTO(baseVo);
	        hashtable.put(node.getIpaddress() + ":" + node.getSubtype(), node);
        }
		String[] ipAndSubTypes = getParaArrayValue("checkbox");
		String modifyFlag = getParaValue("modifyFlag");
		boolean isShow = Boolean.parseBoolean(modifyFlag);
		User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		int userId = user.getId();
		List<HostApplyModel> list = new ArrayList<HostApplyModel>();
		if (ipAndSubTypes != null) {
		    for (String ipAndSubType : ipAndSubTypes) {
                NodeDTO node = hashtable.get(ipAndSubType);
                HostApplyModel model = new HostApplyModel();
                model.setIpaddres(node.getIpaddress());
                model.setNodeid(Integer.valueOf(node.getNodeid()));
                model.setShow(isShow);
                model.setSubtype(node.getSubtype());
                model.setType(node.getType());
                model.setUserId(userId);
                list.add(model);
            }
		}
		HostApplyDao hostApplyDao = new HostApplyDao();
        try{
            hostApplyDao.deleteByUserId(userId);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            hostApplyDao.close();
        }
		hostApplyDao = new HostApplyDao();
		try{
			hostApplyDao.save(list);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			hostApplyDao.close();
		}
		return allList();
	}
}
