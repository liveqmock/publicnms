package com.afunms.application.manage;


import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.afunms.application.model.Tomcat;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.config.dao.ProcsDao;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Procs;
import com.afunms.config.model.Supper;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Proces;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.util.KeyGenerator;

public class ProcessManager extends BaseManager implements ManagerInterface{

//	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private String list()
	{
		//request.setAttribute("action", "list");
		ProcsDao dao = new ProcsDao();
		List ipList = null;
		try {
			ipList = dao.groupByIp();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			dao.close();
		}
		request.setAttribute("ipList", ipList);
		
		String where = getWhere();
		//System.out.println(where + "==================where==================");
		
		 setTarget("/application/process/procslist.jsp");
		dao = new ProcsDao();
		return list(dao, where);
	}
	
	private String getWhere(){
		
		String wbstatus = getParaValue("wbstatus");
		
		String ipaddress = getParaValue("ipaddress");
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" where");
		
		sql.append(getWbstatusSql(wbstatus));
		
		sql.append(getIpaddressSql(ipaddress));
		
		return sql.toString();
	}
	
	private String getIpaddressSql(String ipaddress){
		StringBuffer sql = new StringBuffer();
		if( ipaddress == null || "-1".equals(ipaddress)){
			sql.append("");
			ipaddress = "-1";
		}else{
			sql.append(" and ipaddress = '" + ipaddress+"'");
		}
		request.setAttribute("ipaddress", ipaddress);
		return sql.toString();
	}
	
	private String getWbstatusSql(String wbstatus){
		StringBuffer sql = new StringBuffer();
		if( "1".equals(wbstatus) || "0".equals(wbstatus)){
			sql.append(" flag = 1 and wbstatus = '" + wbstatus + "'");
    	}else{
    		sql.append(" flag >=0");
    	}
		//System.out.println(wbstatus + "========================================");
		request.setAttribute("wbstatus", wbstatus);
		//System.out.println(wbstatus + "===================44444444=====================");
		return sql.toString();
	}
	
	private String ready_add(){
		List hostnodelist = null;
		HostNodeDao hostdao= new HostNodeDao();
		try {
			hostnodelist = hostdao.loadHostByFlag(new Integer(1));
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			hostdao.close();
		}
		/* snow add at 2010-5-27*/
		try {
			SupperDao supperdao = new SupperDao();
			List<Supper> allSupper = supperdao.loadAll();
			request.setAttribute("allSupper", allSupper);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*snow add end*/
    	
		request.setAttribute("hostnodelist", hostnodelist);
		return "/application/process/add.jsp";
	}
	
	private String add()
    {    	   
		Procs vo = createProcess();
		ProcsDao pdao = new ProcsDao();
		vo.setId(vo.getId());
		//vo.setId(KeyGenerator.getInstance().getNextKey());
		try{
			pdao.save(vo);	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			pdao.close();
		}
		Proces pro = new Proces();
    	pro.setId(vo.getId());
    	pro.setIpAddress(vo.getIpaddress());
    	pro.setName(vo.getBak());
    	pro.setAlias(vo.getProcname());
    	pro.setCategory(69);
    	pro.setStatus(0);
    	pro.setType("主机进程");
    	pro.setSupperid(vo.getSupperid());//snow add at 2010-5-27
		PollingEngine.getInstance().addPro(pro);
		return list();
    }  
	
	public String delete()
	{
		String[] ids = getParaArrayValue("checkbox");
		
    	if(ids != null && ids.length > 0){	
    		ProcsDao pdao = new ProcsDao();
    		try{
    			pdao.delete(ids);
    		}catch(Exception e){
    			e.printStackTrace();
    		}finally{
    			pdao.close();
    		}
			/* snow add 2010-5-27 */
			TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
			for (String str : ids) {
				tg.deleteTimeGratherConfig(str, tg.getObjectType("19"));
			}
			/* snow end*/
    		
    	}
    	
		return list();
	}
	
	/**
	 * 修改前查找供应商与采集时间
	 * @author snow
	 * @date 2010-5-27
	 * @return
	 */
	private String ready_edit(){
		DaoInterface dao = new ProcsDao();
 	    setTarget("/application/process/edit.jsp");
		String jsp = "";
    	try {
    		jsp = readyEdit(dao);
			//提供供应商信息
			SupperDao supperdao = new SupperDao();
	    	List<Supper> allSupper = supperdao.loadAll();
	    	request.setAttribute("allSupper", allSupper);
	    	//采集时间信息
			TimeGratherConfigUtil tg  = new TimeGratherConfigUtil();
			List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("19"));
			for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
				timeGratherConfig.setHourAndMin();
			}
			request.setAttribute("timeGratherConfigList", timeGratherConfigList);
		} catch (Exception e) {
			e.printStackTrace();
		}  
    	return jsp;
	}
	
	private String update()
    {    	   
		Procs vo = createProcess();
		vo.setId(getParaIntValue("id"));
		//vo.setId(KeyGenerator.getInstance().getNextKey());
		ProcsDao pdao = new ProcsDao();
		try{
			pdao.update(vo);
			 /* 增加采集时间设置 snow add at 2010-5-20 */
            TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
            timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("19"));
      		/* snow add end*/
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			pdao.close();
		}
             
		return "/process.do?action=list";
    }
	
	private String changeMonflag()
    {   
		Procs vo=new Procs();
		ProcsDao dao = null;
        try{
        	String id = getParaValue("id");
        	int monflag = getParaIntValue("value");
        	dao =new ProcsDao();
        	vo = (Procs)dao.findByID(id);
        	vo.setFlag(monflag);
        	vo.setWbstatus(monflag);
        	dao =new ProcsDao();
        	dao.update(vo);
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	dao.close();
        }
        return list();
    }
	
	private Procs createProcess(){
		
		
		Procs vo = new Procs();
		Calendar cal = new GregorianCalendar();
		//vo.setId(getParaIntValue("id"));
		vo.setId(KeyGenerator.getInstance().getNextKey());//snow add 2010-5-27
		vo.setFlag(getParaIntValue("_flag"));
		
		String wbstatus = getParaValue("wbstatus");
		
		if( wbstatus == null){
			wbstatus = getParaValue("flag");
		}
		
		String ipaddress = getParaValue("ipaddress");
		HostNode hostNode = null;
		HostNodeDao hostNodeDao = new HostNodeDao();
		try {
			hostNode = (HostNode)hostNodeDao.findByIpaddress(ipaddress);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			hostNodeDao.close();
		}
		
		vo.setNodeid(hostNode.getId());
		
		vo.setWbstatus(Integer.valueOf(wbstatus));
		
		vo.setIpaddress(ipaddress);
		
		vo.setProcname(getParaValue("pname"));
		vo.setBak(getParaValue("bak"));
		vo.setCollecttime(cal);
		
		vo.setSupperid(getParaIntValue("supperid"));//snow add at 2010-5-27
		
		return vo;
	}

	public String execute(String action) {
		 	if(action.equals("list")){
		 		return list();  
		 	}
	        if(action.equals("ready_add"))
	        	return ready_add();
	        if(action.equals("add"))
	        	return add();
	        if(action.equals("changeMonflag"))
	        	return changeMonflag();
	        if(action.equals("delete"))
	            return delete();
	        if(action.equals("ready_edit"))
	        	return ready_edit();
	        if(action.equals("update"))
	            return update();
	       
			setErrorCode(ErrorMessage.ACTION_NO_FOUND);
			
		return null;
	}

	
}
