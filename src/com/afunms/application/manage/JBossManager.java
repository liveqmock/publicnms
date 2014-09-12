/*
 * @(#)JBossManager.java     v1.01, Feb 23, 2013
 *
 * Copyright (c) 2011, TNT All Rights Reserved.
 */

package com.afunms.application.manage;

import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.service.NodeAlarmService;
import com.afunms.application.dao.HostApplyDao;
import com.afunms.application.dao.IISConfigDao;
import com.afunms.application.dao.JBossConfigDao;
import com.afunms.application.dao.WebLoginConfigDao;
import com.afunms.application.jbossmonitor.HttpClientJBoss;
import com.afunms.application.model.IISConfig;
import com.afunms.application.model.IISVo;
import com.afunms.application.model.JBossConfig;
import com.afunms.application.model.webloginConfig;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.DateE;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.detail.service.jbossInfo.JBossInfoService;
import com.afunms.event.dao.EventListDao;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.node.service.NodeService;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.loader.JBossLoader;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.node.WebLogin;
import com.afunms.polling.snmp.IISSnmp;
import com.afunms.polling.snmp.jboss.JBossPerformanceIndicatorGather;
import com.afunms.polling.task.IISDataCollector;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.system.model.User;
import com.afunms.topology.dao.LineDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NodeDependDao;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NodeDepend;
import com.afunms.topology.util.KeyGenerator;
import com.lowagie.text.DocumentException;

/**
 * ClassName:   JBossManager.java
 * <p>{@link JBossManager} 逻辑控制类
 *
 * @author      聂林
 * @version     v1.01
 * @since       v1.01
 * @Date        Feb 23, 2013 4:24:11 PM
 */
public class JBossManager extends BaseManager implements ManagerInterface {

    /**
     * execute:
     *
     * @param action
     * @return
     *
     * @since   v1.01
     * @see com.afunms.common.base.ManagerInterface#execute(java.lang.String)
     */
	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");

	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	DateE datemanager = new DateE();
	
	
	
    public String execute(String action) {
        if ("list".equals(action)) {
            return list();
        } else if ("add".equals(action)) {
            return add();
        } else if ("save".equals(action)) {
            return save();
        } else if ("edit".equals(action)) {
            return edit();
        } else if ("update".equals(action)) {
            return update();
        } else if ("delete".equals(action)) {
            return delete();
        } else if ("detail".equals(action)) {
            return detail();
        } else if("sychronizeData".equalsIgnoreCase(action)){
        	return sychronizeData();
        } else if("isOK".equalsIgnoreCase(action)){
        	return isOK();
        }else if("alarm".equalsIgnoreCase(action)){
        	return alarm();
        }else  if(action.equalsIgnoreCase("showPingReport")){
        	return showPingReport();
        }else if("eventReport".equalsIgnoreCase(action)){
        	return eventReport();
        }else if("downloadEventReport".equalsIgnoreCase(action)){
        	return downloadEventReport();
        }else if("perReport".equalsIgnoreCase(action)){
        	return perReport();
        }else if("downloadReport".equalsIgnoreCase(action)){
        	return downloadReport();
        }else if("changeMonflag".equalsIgnoreCase(action)){
        	return changeMonflag();
        }
        return null;
    }

    /**
     * list:
     * <p>处理 {@link JBossConfig} 列表请求
     *
     * @return  {@link String}
     *          - 列表页面
     *
     * @since   v1.01
     */
    @SuppressWarnings("unchecked")
    private String list() {
        String bidSql = getBidSql("bid");
        JBossConfigDao dao = new JBossConfigDao();
        try {
            if (bidSql == null) {
                // 如果为 null 则为超级用户，不需要检查 bid
                list(dao);
            } else if (bidSql.trim().length() > 0) {
                list(dao, "where " + bidSql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dao.close();
        }
        List<JBossConfig> list = (List<JBossConfig>) request.getAttribute("list");
        if (list == null) {
            list = new ArrayList<JBossConfig>();
        }
        NodeAlarmService service = new NodeAlarmService();
        Hashtable<Integer, Integer> statusHashtable = new Hashtable<Integer, Integer>();
        for (JBossConfig jBossConfig : list) {
            int status = service.getMaxAlarmLevel(jBossConfig);
            statusHashtable.put(jBossConfig.getId(), status);
        }
        request.setAttribute("statusHashtable", statusHashtable);
        return "/application/jboss/list.jsp";
    }

    /**
     * add:
     * <p>处理添加请求
     *
     * @return  {@link String}
     *          - 添加页面
     *
     * @since   v1.01
     */
    public String add() {
        return "/application/jboss/add.jsp";
    }

    /**
     * save:
     * <p>处理保存请求
     *
     * @return  {@link String}
     *          - 列表页面
     *
     * @since   v1.01
     */
    public String save() {
        JBossConfig vo = createFromReqeust();
        vo.setId(KeyGenerator.getInstance().getNextKey());
        JBossConfigDao dao = new JBossConfigDao();
        try{
             dao.save(vo);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            dao.close();
        }
        //在轮询线程中增加被监视节点
        JBossLoader loader = new JBossLoader();
        try{
            loader.loadOne(vo);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            loader.close();
        }
        dao = new JBossConfigDao();
        try{
            List<JBossConfig> list = dao.loadAll();
            if (list == null) {
                list = new ArrayList<JBossConfig>();
            }
            ShareData.setJbosslist(list);
            loader.clearRubbish(list);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            loader.close();
            dao.close();
        }
        NodeService service = new NodeService();
        service.addNode(vo);
        return list();
    }

    /**
     * edit:
     * <p>处理编辑请求
     *
     * @return  {@link String}
     *          - 编辑页面
     *
     * @since   v1.01
     */
    public String edit() {
        JBossConfigDao dao = new JBossConfigDao();
        try {
            readyEdit(dao);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dao.close();
        }
        return "/application/jboss/edit.jsp";
    }

    /**
     * update:
     * <p>处理更新请求
     *
     * @return  {@link String}
     *          - 列表页面
     *
     * @since   v1.01
     */
    public String update() {
        JBossConfig vo = createFromReqeust();
        vo.setId(getParaIntValue("id"));
        JBossConfigDao dao = new JBossConfigDao();
        try {
            dao.update(vo);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dao.close();
        }
        // 在轮询线程中增加被监视节点
        JBossLoader loader = new JBossLoader();
        try{
            loader.loadOne(vo);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            loader.close();
        }
        dao = new JBossConfigDao();
        try{
            List<JBossConfig> list = dao.loadAll();
            if (list == null) {
                list = new ArrayList<JBossConfig>();
            }
            ShareData.setJbosslist(list);
            loader.clearRubbish(list);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            loader.close();
            dao.close();
        }
        return list();
    }
    
    /**
     * delete:
     * <p>处理删除请求
     *
     * @return  {@link String}
     *          - 列表页面
     *
     * @since   v1.01
     */
    public String delete() {
        String[] ids = getParaArrayValue("checkbox");
        if (ids != null && ids.length > 0) {
            NodeService service = new NodeService();
            for (String id : ids) {
                JBossConfigDao dao = new JBossConfigDao();
                JBossConfig baseVo = null;
                try {
                    baseVo = (JBossConfig) dao.findByID(id);
                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    dao.close();
                }
                if (baseVo == null) {
                    continue;
                }
                service.deleteNode(baseVo);
                
                NodeDependDao nodedependao = new NodeDependDao();
                List list = nodedependao.findByNode("jbo" + id);
                if (list != null && list.size() >0 ){
                    for(int j = 0; j < list.size(); j++){
                        NodeDepend nodeDepend = (NodeDepend) list.get(j);
                        if (nodeDepend != null) {
                            LineDao lineDao = new LineDao();
                            lineDao.deleteByidXml("jbo"+id, nodeDepend.getXmlfile());
                            NodeDependDao nodeDependDao = new NodeDependDao();
                            if(nodeDependDao.isNodeExist("jbo"+id, nodeDepend.getXmlfile())){
                                nodeDependDao.deleteByIdXml("jbo"+id, nodeDepend.getXmlfile());
                            }
                            //yangjun
                            User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
                            ManageXmlDao mXmlDao = new ManageXmlDao();
                            List xmlList = new ArrayList();
                            try{
                                xmlList = mXmlDao.loadByPerAll(user.getBusinessids());
                            }catch(Exception e){
                                e.printStackTrace();
                            }finally{
                                mXmlDao.close();
                            }
                            try{
                                ChartXml chartxml;
                                chartxml = new ChartXml("tree");
                                chartxml.addViewTree(xmlList);
                            }catch(Exception e){
                                e.printStackTrace();    
                            }
                            
                            ManageXmlDao subMapDao = new ManageXmlDao();
                            ManageXml manageXml = (ManageXml) subMapDao.findByXml(nodeDepend.getXmlfile());
                            if(manageXml!=null){
                                NodeDependDao nodeDepenDao = new NodeDependDao();
                                try{
                                    List lists = nodeDepenDao.findByXml(nodeDepend.getXmlfile());
                                    ChartXml chartxml;
                                    chartxml = new ChartXml("NetworkMonitor","/"+nodeDepend.getXmlfile().replace("jsp", "xml"));
                                    chartxml.addBussinessXML(manageXml.getTopoName(),lists);
                                    ChartXml chartxmlList;
                                    chartxmlList = new ChartXml("NetworkMonitor","/"+nodeDepend.getXmlfile().replace("jsp", "xml").replace("businessmap", "list"));
                                    chartxmlList.addListXML(manageXml.getTopoName(),lists);
                                }catch(Exception e){
                                    e.printStackTrace();    
                                }finally{
                                    nodeDepenDao.close();
                                }
                            }
                        }
                    }
                }
            }
            JBossConfigDao dao = new JBossConfigDao();
            try {
                dao.delete(ids);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dao.close();
            }
            JBossLoader loader = new JBossLoader();
            dao = new JBossConfigDao();
            try{
                List<JBossConfig> list = dao.loadAll();
                if (list == null) {
                    list = new ArrayList<JBossConfig>();
                }
                ShareData.setJbosslist(list);
                loader.clearRubbish(list);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                loader.close();
                dao.close();
            }
        }
        return list();
    }

    public JBossConfig createFromReqeust() {
        JBossConfig jBossConfig = new JBossConfig();
        jBossConfig.setAlias(getParaValue("alias"));
        jBossConfig.setUsername(getParaValue("username"));
        jBossConfig.setPassword(getParaValue("password"));
        jBossConfig.setIpaddress(getParaValue("ipaddress"));
        jBossConfig.setPort(getParaIntValue("port"));
        jBossConfig.setFlag(getParaIntValue("flag1"));
        jBossConfig.setSendmobiles(getParaValue("sendmobiles"));
        jBossConfig.setSendemail(getParaValue("sendemail"));
        jBossConfig.setSendphone(getParaValue("sendphone"));
        jBossConfig.setNetid(getParaValue("bid"));  
        return jBossConfig;
    }

    public String detail() {
        String id = getParaValue("id");
        String flag = request.getParameter("flag");
		   request.setAttribute("flag", flag);
        
        JBossConfig jBossConfig = null;
        JBossConfigDao dao = new JBossConfigDao();
        try {
            jBossConfig = (JBossConfig) dao.findByID(id);
        } catch (RuntimeException e1) {
            e1.printStackTrace();
        } finally{
            dao.close();
        }
        NodeAlarmService nodeAlarmService = new NodeAlarmService();
        int status = nodeAlarmService.getMaxAlarmLevel(jBossConfig);
        
        NodeUtil util = new NodeUtil();
        NodeDTO nodeDTO = util.conversionToNodeDTO(jBossConfig);
        
        JBossInfoService service = new JBossInfoService();
//        Hashtable<String, String> hashtable = service.getJBossData(nodeDTO.getNodeid());
        Hashtable<String, String> hashtable = (Hashtable<String, String>) ShareData.getJbossdata().get(id);
        String pingAvg = service.getPingInfo(nodeDTO.getNodeid());
        if (hashtable == null) {
            hashtable = new Hashtable<String, String>();
        }
        
        
        String version = (String) hashtable.get("version") == null ? "" : hashtable.get("version");
        String date = (String) hashtable.get("date") == null ? "" : hashtable.get("date");
        String versionname = (String) hashtable.get("versionname") == null ? "" : hashtable.get("versionname");
        String builton = (String) hashtable.get("builton") == null ? "" : hashtable.get("builton");
        String startdate = (String) hashtable.get("startdate") == null ? "" : hashtable.get("startdate");
        String host = (String) hashtable.get("host") == null ? "" : hashtable.get("host");
        String baselocation = (String) hashtable.get("baselocation") == null ? "" : hashtable.get("baselocation");
        String baselocationlocal = (String) hashtable.get("baselocationlocal") == null ? "" : hashtable.get("baselocationlocal");
        String runconfig = (String) hashtable.get("runconfig") == null ? "" : hashtable.get("runconfig");
        String threads = (String) hashtable.get("threads") == null ? "" : hashtable.get("threads");
        String os = (String) hashtable.get("os") == null ? "" : hashtable.get("os");
        String jvmversion = (String) hashtable.get("jvmversion") == null ? "" : hashtable.get("jvmversion");
        String jvmname = (String) hashtable.get("jvmname") == null ? "" : hashtable.get("jvmname");
        String freememory = (String) hashtable.get("freememory") == null ? "" : hashtable.get("freememory");
        String totalmemory = (String) hashtable.get("totalmemory") == null ? "" : hashtable.get("totalmemory");
        double JVM = 0;
        if(!"".equals(freememory) && !"".equals(totalmemory)){
        	JVM = (double) Math.round(((Double.parseDouble(freememory)/Double.parseDouble(totalmemory))*100));
        }
        
        String ajp = (String) hashtable.get("ajp") == null ? "" : hashtable.get("ajp");
        String ajp_maxthreads = (String) hashtable.get("ajp_maxthreads") == null ? "" : hashtable.get("ajp_maxthreads");
        String ajp_thrcount = (String) hashtable.get("ajp_thrcount") == null ? "" : hashtable.get("ajp_thrcount");
        String ajp_thrbusy = (String) hashtable.get("ajp_thrbusy") == null ? "" : hashtable.get("ajp_thrbusy");
        String ajp_maxtime = (String) hashtable.get("ajp_maxtime") == null ? "" : hashtable.get("ajp_maxtime");
        String ajp_processtime = (String) hashtable.get("ajp_processtime") == null ? "" : hashtable.get("ajp_processtime");
        String ajp_requestcount = (String) hashtable.get("ajp_requestcount") == null ? "" : hashtable.get("ajp_requestcount");
        String ajp_errorcount = (String) hashtable.get("ajp_errorcount") == null ? "" : hashtable.get("ajp_errorcount");
        String ajp_bytereceived = (String) hashtable.get("ajp_bytereceived") == null ? "" : hashtable.get("ajp_bytereceived");
        String ajp_bytessent = (String) hashtable.get("ajp_bytessent") == null ? "" : hashtable.get("ajp_bytessent");
        request.setAttribute("version", version);
        request.setAttribute("date", date);
        request.setAttribute("versionname", versionname);
        request.setAttribute("builton", builton);
        request.setAttribute("startdate", startdate);
        request.setAttribute("host", host);
        request.setAttribute("baselocation", baselocation);
        request.setAttribute("baselocationlocal", baselocationlocal);
        request.setAttribute("runconfig", runconfig);
        request.setAttribute("threads", threads);
        request.setAttribute("os", os);
        request.setAttribute("jvmversion", jvmversion);
        request.setAttribute("jvmname", jvmname);
        
        request.setAttribute("ajp", ajp);
        request.setAttribute("ajp_maxthreads", ajp_maxthreads);
        request.setAttribute("ajp_thrcount", ajp_thrcount);
        request.setAttribute("ajp_thrbusy", ajp_thrbusy);
        request.setAttribute("ajp_maxtime", ajp_maxtime);
        request.setAttribute("ajp_processtime", ajp_processtime);
        request.setAttribute("ajp_requestcount", ajp_requestcount);
        request.setAttribute("ajp_errorcount", ajp_errorcount);
        request.setAttribute("ajp_bytereceived", ajp_bytereceived);
        request.setAttribute("ajp_bytessent", ajp_bytessent);

        request.setAttribute("pingAvg", pingAvg);
        request.setAttribute("JVM", JVM);
        
        request.setAttribute("jBossConfig", jBossConfig);
        request.setAttribute("status", status);
        return "/application/jboss/system.jsp";
    }
    
    
    
    private String sychronizeData()
    {    
		
		int queryid = getParaIntValue("id");
		String dbpage = getParaValue("dbPage");
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>(); 	
    	Hashtable<String,Hashtable<String,NodeGatherIndicators>> urlHash = new Hashtable<String,Hashtable<String,NodeGatherIndicators>>();

    	try{
    		//获取被启用的所有被监视指标
    		monitorItemList = indicatorsdao.getByNodeId(queryid+"", 1,"middleware","jboss");
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		indicatorsdao.close();
    	}
    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();
    	Hashtable gatherHash = new Hashtable();
    	for(int i=0;i<monitorItemList.size();i++){
    		NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(i);
			gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
    	}
    	
//		JBossConfig jbossconf = new JBossConfig();
//		JBossConfigDao dao = new JBossConfigDao();
//		try{
//			jbossconf = (JBossConfig)dao.findByID(queryid+"");
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			dao.close();
//		}
		
        try {                	
        	JBossPerformanceIndicatorGather jboss = new JBossPerformanceIndicatorGather();
        	NodeGatherIndicators vo = new NodeGatherIndicators();
        	vo.setNodeid(queryid+"");
        	jboss.collect_Data(vo);
        }catch(Exception exc){
        	exc.printStackTrace();
        }
        if("detail".equalsIgnoreCase(dbpage)){
        	return "/jboss.do?action=detail&id="+queryid;
        }else if("alarm".equalsIgnoreCase(dbpage)){
            return "/jboss.do?action=alarm&id="+queryid;
        }else
        	return "/jboss.do?action=detail&id="+queryid;
    }
    
    
    
    
    private String isOK()
    {    
		
		int queryid = getParaIntValue("id");
		JBossConfig jbossconf = new JBossConfig();
		JBossConfigDao dao = new JBossConfigDao();
		try{
			jbossconf = (JBossConfig)dao.findByID(queryid+"");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>(); 	
    	Hashtable<String,Hashtable<String,NodeGatherIndicators>> urlHash = new Hashtable<String,Hashtable<String,NodeGatherIndicators>>();//存放需要监视的IIS指标  <dbid:Hashtable<name:NodeGatherIndicators>>

    	try{
    		//获取被启用的所有被监视指标
    		monitorItemList = indicatorsdao.getByNodeId(queryid+"", 1,"middleware","jboss");
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		indicatorsdao.close();
    	}
    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();
    	Hashtable gatherHash = new Hashtable();
    	for(int i=0;i<monitorItemList.size();i++){
    		NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(i);
    		if(nodeGatherIndicators.getName().equalsIgnoreCase("domain"))
    			gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
    	}
		
    	String flag = "";
        String ipaddress = jbossconf.getIpaddress();
        HttpClientJBoss jboss = new HttpClientJBoss();
        String src = null;
        try {
            src = jboss.getGetResponseWithHttpClient("http://" + ipaddress + ":"+jbossconf.getPort()+"/web-console/ServerInfo.jsp", "GBK");
        } catch (Exception e) {
        	flag = "JBOSS服务不可用";
        }
        if (src != null && src.contains("Version")) {
        	flag = "JBOSS服务可用";
        } else {
        	flag = "JBOSS服务不可用";
        }
    	
    	
//		// 实时数据
//    	
//    	Hashtable hash = new Hashtable();
//    	try{
//    	  JBossPerformanceIndicatorGather jboss = new JBossPerformanceIndicatorGather();
//    	  NodeGatherIndicators vo = new NodeGatherIndicators();
//    	  vo.setNodeid(queryid+"");
//    	  hash = jboss.collect_Data(vo);
//    	}catch(Exception e){
//    		flag = "JBOSS服务不可用";
//    		e.printStackTrace();
//    	}
//    	if(!hash.containsKey("jboss:" +queryid)){
//    		flag = "JBOSS服务不可用";
//    	}

		request.setAttribute("isOK", flag);
		request.setAttribute("name", jbossconf.getAlias());
		request.setAttribute("str", jbossconf.getIpaddress());
        return "/tool/ftpisok.jsp";
    }
    
    public String alarm(){
		String id=request.getParameter("id");
		JBossConfigDao jbosscconfigdao=new JBossConfigDao();
		JBossConfig jbossconf=null;
    	List list = new ArrayList();
    	String flag = getParaValue("flag");
    	try{
    		jbossconf = (JBossConfig) jbosscconfigdao.findByID(id);
    	}catch(Exception e){
			e.printStackTrace();
		}finally{
			jbosscconfigdao.close();
		}
    	try{
    	
    		int	status = getParaIntValue("status");
	    	int	level1 = getParaIntValue("level1");
	    	if(status == -1)status=99;
	    	if(level1 == -1)level1=99;
	    	request.setAttribute("status", status);
	    	request.setAttribute("level1", level1);
	    	
	    	String	b_time = getParaValue("startdate");
	    	String	t_time = getParaValue("todate");
	    	
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			if (b_time == null){
				b_time = sdf1.format(new Date());
			}
			if (t_time == null){
				t_time = sdf1.format(new Date());
			}
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";
			request.setAttribute("startdate", b_time);
			request.setAttribute("todate", t_time);
			try{
				User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
				EventListDao eventdao = new EventListDao();
				list = eventdao.getQuery(starttime1,totime1,"jboss",status+"",level1+"",
						user.getBusinessids(),jbossconf.getId());				
			}catch(Exception ex){
				ex.printStackTrace();
			}
	        
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			//weblogicconfigdao.close();
		}
		
		com.afunms.polling.node.JBossConfig jboss = (com.afunms.polling.node.JBossConfig)PollingEngine.getInstance().getJBossByID(Integer.parseInt(id)); 
		int alarmLevel = 0;
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		/*===========for status start==================*/
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(jboss);
		if(nodeDTO!=null){
			String chexkname = id+":"+nodeDTO.getType()+":"+nodeDTO.getSubtype()+":";
			//System.out.println(chexkname);
			if(checkEventHashtable!=null){
				for(Iterator it = checkEventHashtable.keySet().iterator();it.hasNext();){ 
			        String key = (String)it.next(); 
			        if(key.startsWith(chexkname)){
			        	if(alarmLevel < (Integer) checkEventHashtable.get(key)){
			        		alarmLevel = (Integer) checkEventHashtable.get(key); 
			        	}
			        }
				}
			}
		}
		request.setAttribute("alarmLevel",alarmLevel);
    	
    	request.setAttribute("id", id);
    	request.setAttribute("jBossConfig", jbossconf);
    	request.setAttribute("list", list);
    	request.setAttribute("flag", flag);
    	return "/application/jboss/alarm.jsp";
		
	}
    
    
    private String showPingReport() {
		Date d = new Date();
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		String startdate = getParaValue("startdate");
		Hashtable reporthash = new Hashtable();
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";

		String newip = "";
		String ip = "";
		Integer queryid = getParaIntValue("id");
		com.afunms.polling.node.JBossConfig jboss = (com.afunms.polling.node.JBossConfig)PollingEngine.getInstance().getJBossByID(queryid);
		try {
//			ip = getParaValue("ipaddress");
            ip = jboss.getIpAddress();
			newip = SysUtil.doip(ip);

			JBossConfigDao jbossdao = new JBossConfigDao();

			Hashtable ConnectUtilizationhash = jbossdao.getPingDataById(ip,
					queryid, starttime, totime);
			String curPing = "";
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgPing") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgPing");
			String minPing = "";

			if (ConnectUtilizationhash.get("minPing") != null) {
				minPing = (String) ConnectUtilizationhash.get("minPing");
			}
			// Ftpmonitor_realtimeDao realDao=new Ftpmonitor_realtimeDao();
			// List curList=realDao.getByFTPId(queryid);
			// Ftpmonitor_realtime ftpReal=new Ftpmonitor_realtime();
			// ftpReal=(Ftpmonitor_realtime) curList.get(0);
			// int ping=ftpReal.getIs_canconnected();
			// if (ping==1) {
			// curPing="100";
			// }else{
			// curPing="0";
			// }
			if (ConnectUtilizationhash.get("curPing") != null) {
				curPing = (String) ConnectUtilizationhash.get("curPing"); // 取当前连通率可直接从
																			// nms_ftp_history表获取,没必要再从nms_ftp_realtime表获取
			}

			// 画图----------------------
			String timeType = "minute";
			PollMonitorManager pollMonitorManager = new PollMonitorManager();
			pollMonitorManager.chooseDrawLineType(timeType,
					ConnectUtilizationhash, "连通率", newip + "jbosspingConnect",
					740, 150);

			// 画图-----------------------------
			reporthash.put("servicename",jboss.getAlias() );
			reporthash.put("Ping", curPing);
			reporthash.put("ip", ip);
			reporthash.put("ping", ConnectUtilizationhash);
			reporthash.put("starttime", startdate);
			reporthash.put("totime", totime);
			request.setAttribute("id", String.valueOf(queryid));
			request.setAttribute("pingmax", minPing);// 最小连通率(pingmax 暂时定义)
			request.setAttribute("Ping", curPing);
			request.setAttribute("avgpingcon", pingconavg);
			request.setAttribute("newip", newip);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", todate);
			request.setAttribute("type", "tftp");
			session.setAttribute("reporthash", reporthash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/application/jboss/showPingReport.jsp";
	}
    
    
    private String doip(String ip){
		 ip =  SysUtil.doip(ip);
		 return ip;
	}
//	    public Hashtable getCategory(
//				String ip,
//				String category,
//				String subentity,
//				String starttime,
//				String endtime)
//				throws Exception {
//				Hashtable hash = new Hashtable();
//			 	DBManager dbmanager = new DBManager();
//			 	ResultSet rs = null;
//				try{
//					//con=DataGate.getCon();
//					if (!starttime.equals("") && !endtime.equals("")) {
//						String allipstr = SysUtil.doip(ip);
////						
//						String sql = "";
//						StringBuffer sb = new StringBuffer();
//						 if (category.equals("IISPing")){
//							 if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
//								 sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from iisping"+allipstr+" h where ");
//							 }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
//								 sb.append(" select h.thevalue,to_char(h.collecttime,'YYYY-MM-DD HH24:MI:SS') as collecttime,h.unit from iisping"+allipstr+" h where ");
//							 }
//							
//						 }
//						sb.append(" h.category='");
//						sb.append(category);
//						sb.append("' and h.subentity='");
//						sb.append(subentity);
//						sb.append("' and h.collecttime >= ");
//						if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
//							sb.append("'");
//							sb.append(starttime);
//							sb.append("'");
//						}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
//							sb.append("to_date('"+starttime+"','YYYY-MM-DD HH24:MI:SS')");
//						}			
//						sb.append(" and h.collecttime <= ");
//						if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
//							sb.append("'");
//							sb.append(endtime);
//							sb.append("'");
//						}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
//							sb.append("to_date('"+endtime+"','YYYY-MM-DD HH24:MI:SS')");
//						}						
//						sb.append(" order by h.collecttime");
//						sql = sb.toString();
//						SysLogger.info(sql);
//						
//						rs = dbmanager.executeQuery(sql);
//						List list1 =new ArrayList();
//						String unit = "";
//						String max = "";
//						double tempfloat=0;
//						double pingcon = 0;
//						double tomcat_jvm_con = 0;
//						int downnum = 0;
//						int i=0;
//				        while (rs.next()) {
//				        	i=i+1;
//				        	Vector v =new Vector();		        	
//				            String thevalue=rs.getString("thevalue");
//				            String collecttime = rs.getString("collecttime");		            
//				            v.add(0,emitStr(thevalue));
//				            v.add(1,collecttime);
//				            v.add(2,rs.getString("unit"));
//				            if (category.equals("IISPing")&&subentity.equalsIgnoreCase("ConnectUtilization")){
//				            	pingcon=pingcon+getfloat(thevalue);
//				            	if(thevalue.equals("0")){
//				            		downnum = downnum + 1;
//				            	}	
//				            }
////				            if (category.equals("tomcat_jvm")&&subentity.equalsIgnoreCase("ConnectUtilization")){
////				            	pingcon=pingcon+getfloat(thevalue);
////				            	if(thevalue.equals("0")){
////				            		downnum = downnum + 1;
////				            	}	
////				            }
//				            if (subentity.equalsIgnoreCase("ConnectUtilization")) {
//				            	if (i==1)tempfloat = getfloat(thevalue);
//				            	if (tempfloat > getfloat(thevalue))tempfloat = getfloat(thevalue);
//				            }else{
//				            	if (tempfloat < getfloat(thevalue))tempfloat = getfloat(thevalue);
//				            }	
//				            list1.add(v);	
//				    }	
//				        rs.close();
//				        //stmt.close();
//				        
//						Integer size = new Integer(0);
//						hash.put("list", list1);
//						if (list1.size() != 0) {
//							size = new Integer(list1.size());
//							if (list1.get(0) != null) {
//								Vector tempV = (Vector)list1.get(0);
//								unit = (String)tempV.get(2);
//							}
//						}
//						if (category.equals("IISPing")&&subentity.equalsIgnoreCase("ConnectUtilization")){
//							if (list1 !=null && list1.size()>0){
//								hash.put("avgpingcon", CEIString.round(pingcon/list1.size(),2)+unit);						
//								hash.put("pingmax", tempfloat+"");
//								hash.put("downnum", downnum+"");
//							}else{ 
//								hash.put("avgpingcon", "0.0%");	
//								hash.put("pingmax", "0.0%");
//								hash.put("downnum", "0");
//							}
//						}
//						hash.put("size", size);			
//						hash.put("max", CEIString.round(tempfloat,2) + unit);
//						hash.put("unit", unit);
//				        }
//					} catch (Exception e) {
//					e.printStackTrace();
//				}finally{
//					if (rs != null)
//					rs.close();
//					dbmanager.close();
//				}
//				
//				return hash;
//			}
    
    
    public String eventReport(){
	 	Vector vector = new Vector();
		
		String ip="";
		String tmp ="";
		List list = new ArrayList();
		int status =99;
		int level1 = 99;
		String b_time ="";
		String t_time = "";
		
		try {
			
	    	tmp = request.getParameter("id");
	    	com.afunms.polling.node.JBossConfig jboss = (com.afunms.polling.node.JBossConfig)PollingEngine.getInstance().getJBossByID(Integer.parseInt(tmp));
			ip=jboss.getIpAddress();
			String newip=doip(ip);
	    	status = getParaIntValue("status");
	    	level1 = getParaIntValue("level1");
	    	if(status == -1)status=99;
	    	if(level1 == -1)level1=99;
	    	request.setAttribute("status", status);
	    	request.setAttribute("level1", level1);
	    	
	    	b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
	    	
			if (b_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";
	    	
		
			try{
				User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
				//SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao dao = new EventListDao();
				
				list = dao.getQuery(starttime1,totime1,status+"",level1+"",
						vo.getBusinessids(),Integer.parseInt(tmp),"jboss");
				
				//ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			}catch(Exception ex){
				ex.printStackTrace();
			}
//			try {
//				Hashtable hash1 = getCategory(ip,"IISPing","ConnectUtilization",starttime1,totime1);						
//				Hashtable hash = getCategory(ip,"tomcat_jvm","jvm_utilization",starttime1,totime1);						
//				if(hash1!=null)
//				request.setAttribute("pingcon", hash1);
//				if(hash!=null)
//					request.setAttribute("avgjvm", hash);	
//			} catch(Exception ex) {
//				ex.printStackTrace();
//			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		request.setAttribute("vector",vector);
		request.setAttribute("id", Integer.parseInt(tmp));
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return "/application/jboss/eventReport.jsp";
	}
 
    //event 报表
	 private String downloadEventReport() {
		 Date d = new Date();
			String startdate = getParaValue("startdate");
			if (startdate == null) {
				startdate = sdf0.format(d);
			}
			String todate = getParaValue("todate");
			if (todate == null) {
				todate = sdf0.format(d);
			}
			String starttime = startdate + " 00:00:00";
			String totime = todate + " 23:59:59";

			String id = request.getParameter("id");

		    Hashtable reporthash = new Hashtable();
			// 按排序标志取各端口最新记录的列表
			String orderflag = "ipaddress";
			if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
				orderflag = getParaValue("orderflag");
			}
					Node iisNode = PollingEngine.getInstance().getJBossByID(Integer.parseInt(id));
					EventListDao eventdao = new EventListDao();
					// 得到事件列表
					StringBuffer s = new StringBuffer();
					s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='"
							+ totime + "' ");
					s.append(" and nodeid=" + iisNode.getId());

					List infolist = eventdao.findByCriteria(s.toString());
					reporthash.put("eventlist", infolist);
					
			// 画图-----------------------------
			ExcelReport1 report = new ExcelReport1(new IpResourceReport(),
					reporthash);
			String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
			if ("0".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
						reporthash);
				String file = "temp/JBOSSEventReport.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				try {
					report1.createReport_midEventDoc(fileName,starttime,totime,"JBOSS");
				} catch (IOException e) {
					e.printStackTrace();
				}// word事件报表分析表
				request.setAttribute("filename", fileName);
			} else if ("1".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
						reporthash);
				String file = "temp/JBOSSEventReport.xls";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				try {
					report1.createReport_TomcatEventExc(file,id,starttime,totime,"JBOSS");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// xls事件报表分析表
				request.setAttribute("filename", fileName);
			} else if ("2".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
						reporthash);
				String file = "temp/JBOSSEventReport.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				try {
					report1.createReport_midEventPdf(fileName,starttime,totime,"JBOSS");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// pdf事件报表分析表
				request.setAttribute("filename", fileName);
			}
			return "/capreport/service/download.jsp";
		}
	 
	 public String perReport(){
		 
		   String id= request.getParameter("id");
		   String flag = request.getParameter("flag");
		   request.setAttribute("flag", flag);
		   
		   
		   JBossConfig jBossConfig = null;
	        JBossConfigDao dao = new JBossConfigDao();
	        try {
	            jBossConfig = (JBossConfig) dao.findByID(id);
	        } catch (RuntimeException e1) {
	            e1.printStackTrace();
	        } finally{
	            dao.close();
	        }
	        NodeAlarmService nodeAlarmService = new NodeAlarmService();
	        int status = nodeAlarmService.getMaxAlarmLevel(jBossConfig);
	        
	        NodeUtil util = new NodeUtil();
	        NodeDTO nodeDTO = util.conversionToNodeDTO(jBossConfig);
	        
	        JBossInfoService service = new JBossInfoService();
//	        Hashtable<String, String> hashtable = service.getJBossData(nodeDTO.getNodeid());
	        Hashtable<String, String> hashtable = (Hashtable<String, String>) ShareData.getJbossdata().get(id);
	        String pingAvg = service.getPingInfo(nodeDTO.getNodeid());
	        if (hashtable == null) {
	            hashtable = new Hashtable<String, String>();
	        }
	        
	        
	        String version = (String) hashtable.get("version") == null ? "" : hashtable.get("version");
	        String date = (String) hashtable.get("date") == null ? "" : hashtable.get("date");
	        String versionname = (String) hashtable.get("versionname") == null ? "" : hashtable.get("versionname");
	        String builton = (String) hashtable.get("builton") == null ? "" : hashtable.get("builton");
	        String startdate = (String) hashtable.get("startdate") == null ? "" : hashtable.get("startdate");
	        String host = (String) hashtable.get("host") == null ? "" : hashtable.get("host");
	        String baselocation = (String) hashtable.get("baselocation") == null ? "" : hashtable.get("baselocation");
	        String baselocationlocal = (String) hashtable.get("baselocationlocal") == null ? "" : hashtable.get("baselocationlocal");
	        String runconfig = (String) hashtable.get("runconfig") == null ? "" : hashtable.get("runconfig");
	        String threads = (String) hashtable.get("threads") == null ? "" : hashtable.get("threads");
	        String os = (String) hashtable.get("os") == null ? "" : hashtable.get("os");
	        String jvmversion = (String) hashtable.get("jvmversion") == null ? "" : hashtable.get("jvmversion");
	        String jvmname = (String) hashtable.get("jvmname") == null ? "" : hashtable.get("jvmname");
	        
	        String ajp = (String) hashtable.get("ajp") == null ? "" : hashtable.get("ajp");
	        String ajp_maxthreads = (String) hashtable.get("ajp_maxthreads") == null ? "" : hashtable.get("ajp_maxthreads");
	        String ajp_thrcount = (String) hashtable.get("ajp_thrcount") == null ? "" : hashtable.get("ajp_thrcount");
	        String ajp_thrbusy = (String) hashtable.get("ajp_thrbusy") == null ? "" : hashtable.get("ajp_thrbusy");
	        String ajp_maxtime = (String) hashtable.get("ajp_maxtime") == null ? "" : hashtable.get("ajp_maxtime");
	        String ajp_processtime = (String) hashtable.get("ajp_processtime") == null ? "" : hashtable.get("ajp_processtime");
	        String ajp_requestcount = (String) hashtable.get("ajp_requestcount") == null ? "" : hashtable.get("ajp_requestcount");
	        String ajp_errorcount = (String) hashtable.get("ajp_errorcount") == null ? "" : hashtable.get("ajp_errorcount");
	        String ajp_bytereceived = (String) hashtable.get("ajp_bytereceived") == null ? "" : hashtable.get("ajp_bytereceived");
	        String ajp_bytessent = (String) hashtable.get("ajp_bytessent") == null ? "" : hashtable.get("ajp_bytessent");
	        request.setAttribute("version", version);
	        request.setAttribute("date", date);
	        request.setAttribute("versionname", versionname);
	        request.setAttribute("builton", builton);
	        request.setAttribute("startdate", startdate);
	        request.setAttribute("host", host);
	        request.setAttribute("baselocation", baselocation);
	        request.setAttribute("baselocationlocal", baselocationlocal);
	        request.setAttribute("runconfig", runconfig);
	        request.setAttribute("threads", threads);
	        request.setAttribute("os", os);
	        request.setAttribute("jvmversion", jvmversion);
	        request.setAttribute("jvmname", jvmname);
	        
	        request.setAttribute("ajp", ajp);
	        request.setAttribute("ajp_maxthreads", ajp_maxthreads);
	        request.setAttribute("ajp_thrcount", ajp_thrcount);
	        request.setAttribute("ajp_thrbusy", ajp_thrbusy);
	        request.setAttribute("ajp_maxtime", ajp_maxtime);
	        request.setAttribute("ajp_processtime", ajp_processtime);
	        request.setAttribute("ajp_requestcount", ajp_requestcount);
	        request.setAttribute("ajp_errorcount", ajp_errorcount);
	        request.setAttribute("ajp_bytereceived", ajp_bytereceived);
	        request.setAttribute("ajp_bytessent", ajp_bytessent);

	        request.setAttribute("pingAvg", pingAvg);
	        
	        request.setAttribute("jBossConfig", jBossConfig);
	        request.setAttribute("status", status);
			
		   return  "/application/jboss/perReport.jsp";
	   }
	 
	 private String downloadReport() {
		    Hashtable reporthash = (Hashtable) session.getAttribute("reporthash");
			// 画图-----------------------------
			ExcelReport1 report = new ExcelReport1(new IpResourceReport(),
					reporthash);
			String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
			String id = request.getParameter("id");
			String flag = request.getParameter("flag");
			
			
			if("per".equalsIgnoreCase(flag)){
				if ("0".equals(str)) {
					// report.createReport_host("temp/hostnms_report.xls");// excel综合报表
					report.createReportxls_jboss_per("temp/jboss_PerReport.xls",id);
					request.setAttribute("filename", report.getFileName());
				} else if ("1".equals(str)) {
					ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
							reporthash);
					try {
						String file = "temp/jboss_PerReport.doc";// 保存到项目文件夹下的指定文件夹
						String fileName = ResourceCenter.getInstance().getSysPath()
								+ file;// 获取系统文件夹路径
						report1.createReportDoc_jboss_per(fileName,"doc",id);// word综合报表
						request.setAttribute("filename", fileName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if ("2".equals(str)) {
					ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
							reporthash);
					try {
						String file = "temp/jboss_PerReport.pdf";// 保存到项目文件夹下的指定文件夹
						String fileName = ResourceCenter.getInstance().getSysPath()
								+ file;// 获取系统文件夹路径
						// report1.createReport_hostPDF(fileName);// pdf综合报表
						report1.createReportDoc_jboss_per(fileName,"pdf",id);
						request.setAttribute("filename", fileName);
					} catch (DocumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
			}
			return "/capreport/service/download.jsp";

		}
	 
	 
	 
	 private String changeMonflag() {
			boolean result = false;
			JBossConfig loginConfig = new JBossConfig();
			JBossConfigDao loginConfigDao = null;
			try {
				String id = getParaValue("id");
				int monflag = Integer.parseInt(getParaValue("value"));
				loginConfigDao = new JBossConfigDao();
				loginConfig = (JBossConfig) loginConfigDao.findByID(id);
				loginConfig.setFlag(monflag);
				result = loginConfigDao.update(loginConfig);
//				Node web =  (JBossConfig)PollingEngine.getInstance().getJBossByID(Integer.parseInt(id));
//				web.setFlag(monflag);
				 JBossLoader loader = new JBossLoader();
			        try{
			            loader.loadOne(loginConfig);
			        } catch (Exception e){
			            e.printStackTrace();
			        } finally {
			            loader.close();
			        }
			        loginConfigDao = new JBossConfigDao();
			        try{
			            List<JBossConfig> list = loginConfigDao.loadAll();
			            if (list == null) {
			                list = new ArrayList<JBossConfig>();
			            }
			            ShareData.setJbosslist(list);
			            loader.clearRubbish(list);
			        } catch (Exception e) {
			            e.printStackTrace();
			        } finally {
			            loader.close();
			            loginConfigDao.close();
			        }
			} catch (Exception e) {
				e.printStackTrace();
				result = false;
			} finally {
				loginConfigDao.close();
			}
			if (result) {
				return list();
			} else {
				return "/application/webLogin/savefail.jsp";
			}
		}
	 
	 
}

