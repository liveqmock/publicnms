/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.application.manage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.dao.TomcatDao;
import com.afunms.application.dao.TuxedoConfigDao;
import com.afunms.application.model.TuxedoConfig;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ShareData;
import com.afunms.config.dao.BusinessDao;
import com.afunms.polling.loader.TomcatLoader;
import com.afunms.polling.loader.TuxedoLoader;
import com.afunms.polling.snmp.LoadTuxedoFile;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.topology.util.KeyGenerator;

public class TuxedoManager extends BaseManager implements ManagerInterface
{
	public String execute(String action){	
        if("list".equals(action)){
        	return list();
        }else if("ready_add".equals(action)){
        	return ready_add();
        }else if("add".equals(action)){
        	return add();
        }else if("ready_edit".equals(action)){
        	return ready_edit();
        }else if("edit".equals(action)){
        	return edit();
        }else if("delete".equals(action)){
        	return delete();
        }else if("changeMon_flag".equals(action)){
        	return changeMon_flag();
        }else if("toDetail".equals(action)){
        	return toDetail();
        }else if("pingDetail".equals(action)){
        	return pingDetail();
        }else if("serverDetail".equals(action)){
        	return serverDetail();
        }else if("serviceDetail".equals(action)){
        	return serviceDetail();
        }else if("queueDetail".equals(action)){
        	return queueDetail();
        }else if("clientDetail".equals(action)){
        	return clientDetail();
        }
         
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	
	private String list(){
		String jsp = "/application/tuxedo/list.jsp";
		setTarget(jsp);
		TuxedoConfigDao tuxedoConfigDao = new TuxedoConfigDao();
		list(tuxedoConfigDao);
		return jsp;
	}
	
	private String ready_add(){
		
		List allbuss = null;
		
		BusinessDao businessDao = new BusinessDao();
		try {
			allbuss = businessDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			businessDao.close();
		}
		request.setAttribute("allbuss", allbuss);
		return "/application/tuxedo/add.jsp";
	}
	
	private String add(){
		
		boolean result = false;
		
		int id = KeyGenerator.getInstance().getNextKey();
		
		TuxedoConfig tuxedoConfig = createTuxedoConfig();
		tuxedoConfig.setId(id);
		
		TuxedoConfigDao tuxedoConfigDao = new TuxedoConfigDao();
		try {
			result = tuxedoConfigDao.save(tuxedoConfig);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		} finally{
			tuxedoConfigDao.close();
			
		}
		if( result ){
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(id), timeShareConfigUtil.getObjectType("16"));
		}
//		
//		if( result ){
//			TuxedoLoader tuxedoLoader = new TuxedoLoader();
//			tuxedoLoader.loadOne(tuxedoConfig);
//	    	
//			//PollingEngine.getInstance().addTuxedo(node);
//		}
		try{
			tuxedoConfigDao = new TuxedoConfigDao();
	         List tempList = tuxedoConfigDao.findByCondition(" where ipaddress = '"+tuxedoConfig.getIpAddress()+"'");
	         if(tempList != null && tempList.size() > 0){
	        	 tuxedoConfig = (TuxedoConfig)tempList.get(0);
	         }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			tuxedoConfigDao.close();
		} 
		//保存应用
		HostApplyManager.save(tuxedoConfig);
		//在轮询线程中增加被监视节点
		TuxedoLoader loader = new TuxedoLoader();
        try{
        	loader.loadOne(tuxedoConfig);
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	loader.close();
        }
    	try{
    		tuxedoConfigDao =  new TuxedoConfigDao();
			List list = tuxedoConfigDao.loadAll();
			if(list == null)list = new ArrayList();
			ShareData.setTuxdolist(list);
			TuxedoLoader _loader = new TuxedoLoader();
			_loader.clearRubbish(list);
		}catch(Exception e){
				
		}finally{
			tuxedoConfigDao.close();
		}
		return list();
	}
	
	private String ready_edit(){
		String id = getParaValue("id");
		
		TuxedoConfig tuxedoConfig = null;
		
		TuxedoConfigDao tuxedoConfigDao = new TuxedoConfigDao();

		try {
			tuxedoConfig = (TuxedoConfig)tuxedoConfigDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			tuxedoConfigDao.close();
		}
		
		List allbuss = null;
		
		BusinessDao businessDao = new BusinessDao();
		try {
			allbuss = businessDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			businessDao.close();
		}
		request.setAttribute("allbuss", allbuss);
		
		request.setAttribute("tuxedoConfig", tuxedoConfig);
		
		TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
		
		List timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(id, timeShareConfigUtil.getObjectType("16"));
		
		request.setAttribute("timeShareConfigList", timeShareConfigList);
		
		return "/application/tuxedo/edit.jsp";
		
	}
	
	private String edit(){
		
		boolean result = false;
		
		String id = getParaValue("id");
		
		TuxedoConfig tuxedoConfig = createTuxedoConfig();
		tuxedoConfig.setId(Integer.parseInt(id));
		
		
		TuxedoConfigDao tuxedoConfigDao = new TuxedoConfigDao();
		try {
			result = tuxedoConfigDao.update(tuxedoConfig);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		} finally{
			tuxedoConfigDao.close();
			
		}
		if( result ){
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(id), timeShareConfigUtil.getObjectType("16"));
		}
		
		return list();
	}
	
	private String delete(){
		
		String[] ids = getParaArrayValue("checkbox");
		
		boolean result = false;
		
		if(ids != null && ids.length > 0){
			TuxedoConfigDao tuxedoConfigDao = new TuxedoConfigDao();
			try {
				result = tuxedoConfigDao.delete(ids);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = false;
			} finally{
				tuxedoConfigDao.close();
			}
			
			if(result){
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
				for(int i = 0 ; i < ids.length ; i++){
					timeShareConfigUtil.deleteTimeShareConfig(ids[i], timeShareConfigUtil.getObjectType("16"));
				}
			}
	
	  	try {
				tuxedoConfigDao = new TuxedoConfigDao();
				List list = tuxedoConfigDao.loadAll();

				if (list == null)
					list = new ArrayList();
				ShareData.setTuxdolist(list);
				TuxedoLoader _loader = new TuxedoLoader();
				_loader.clearRubbish(list);
			} catch (Exception e) {

			} finally {
				tuxedoConfigDao.close();
			}
		}
		return list();
	}
	
	private TuxedoConfig createTuxedoConfig(){
		String name = getParaValue("name");
		String ipaddress = getParaValue("ipaddress");
		String mon_flag = getParaValue("mon_flag");
		String[] businessbox = getParaArrayValue("businessbox");
		String sendemail = getParaValue("sendemail");
		
		TuxedoConfig tuxedoConfig = new TuxedoConfig();
		
		tuxedoConfig.setName(name);
		tuxedoConfig.setIpAddress(ipaddress);
		tuxedoConfig.setMon_flag(mon_flag);
		tuxedoConfig.setBid(getParaValue("bid"));
		tuxedoConfig.setSendemail(sendemail);
		return tuxedoConfig;
	}
	
	private String toDetail(){
		return serverDetail();
	}
	
	private String pingDetail(){
		return "/application/tuxedo/ping_detail.jsp";
	}
	
	private String serverDetail(){
		
		String id = getParaValue("id");
		
		TuxedoConfig tuxedoConfig = null;
		TuxedoConfigDao tuxedoConfigDao = new TuxedoConfigDao();
		try {
			tuxedoConfig = (TuxedoConfig)tuxedoConfigDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			tuxedoConfigDao.close();
		}
		
		request.setAttribute("tuxedoConfig", tuxedoConfig);
		
//		Hashtable allTuxedoData = ShareData.getAllTuxedodata();
		
		
		if(tuxedoConfig!=null ){
			Hashtable data_hashtable = null;
			//data_hashtable = (Hashtable)allTuxedoData.get(tuxedoConfig.getIpAddress());
			LoadTuxedoFile loadTuxedoFile = new LoadTuxedoFile();
			try {
				data_hashtable = loadTuxedoFile.getTuxedoInfo(tuxedoConfig.getIpAddress());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(data_hashtable!=null){
				tuxedoConfig.setStatus("1");
			}else{
				tuxedoConfig.setStatus("0");
			}
			
			List serverList  = (List)data_hashtable.get("Server");
			
			request.setAttribute("serverList", serverList);
			
		}
		
		return "/application/tuxedo/server_detail.jsp";
	}
	
	private String serviceDetail(){
		String id = getParaValue("id");
		
		TuxedoConfig tuxedoConfig = null;
		TuxedoConfigDao tuxedoConfigDao = new TuxedoConfigDao();
		try {
			tuxedoConfig = (TuxedoConfig)tuxedoConfigDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			tuxedoConfigDao.close();
		}
		
		request.setAttribute("tuxedoConfig", tuxedoConfig);
		
//		Hashtable allTuxedoData = ShareData.getAllTuxedodata();
		
		
		if(tuxedoConfig!=null ){
			Hashtable data_hashtable = null;
			//data_hashtable = (Hashtable)allTuxedoData.get(tuxedoConfig.getIpAddress());
			LoadTuxedoFile loadTuxedoFile = new LoadTuxedoFile();
			try {
				data_hashtable = loadTuxedoFile.getTuxedoInfo(tuxedoConfig.getIpAddress());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(data_hashtable!=null){
				tuxedoConfig.setStatus("1");
			}else{
				tuxedoConfig.setStatus("0");
			}
			
			List serviceList  = (List)data_hashtable.get("Service");
			
			request.setAttribute("serviceList", serviceList);
			
		}
		
		return "/application/tuxedo/service_detail.jsp";
	
	}
	
	private String queueDetail(){
		String id = getParaValue("id");
		
		TuxedoConfig tuxedoConfig = null;
		TuxedoConfigDao tuxedoConfigDao = new TuxedoConfigDao();
		try {
			tuxedoConfig = (TuxedoConfig)tuxedoConfigDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			tuxedoConfigDao.close();
		}
		
		request.setAttribute("tuxedoConfig", tuxedoConfig);
		
//		Hashtable allTuxedoData = ShareData.getAllTuxedodata();
		
		
		if(tuxedoConfig!=null ){
			Hashtable data_hashtable = null;
			//data_hashtable = (Hashtable)allTuxedoData.get(tuxedoConfig.getIpAddress());
			LoadTuxedoFile loadTuxedoFile = new LoadTuxedoFile();
			try {
				data_hashtable = loadTuxedoFile.getTuxedoInfo(tuxedoConfig.getIpAddress());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(data_hashtable!=null){
				tuxedoConfig.setStatus("1");
			}else{
				tuxedoConfig.setStatus("0");
			}
			
			List queueList  = (List)data_hashtable.get("Queue");
			
			request.setAttribute("queueList", queueList);
			
		}
		
		return "/application/tuxedo/queue_detail.jsp";
	
	}
	
	private String clientDetail(){
		String id = getParaValue("id");
		
		TuxedoConfig tuxedoConfig = null;
		TuxedoConfigDao tuxedoConfigDao = new TuxedoConfigDao();
		try {
			tuxedoConfig = (TuxedoConfig)tuxedoConfigDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			tuxedoConfigDao.close();
		}
		
		request.setAttribute("tuxedoConfig", tuxedoConfig);
		
//		Hashtable allTuxedoData = ShareData.getAllTuxedodata();
		
		
		if(tuxedoConfig!=null ){
			Hashtable data_hashtable = null;
			//data_hashtable = (Hashtable)allTuxedoData.get(tuxedoConfig.getIpAddress());
			LoadTuxedoFile loadTuxedoFile = new LoadTuxedoFile();
			try {
				data_hashtable = loadTuxedoFile.getTuxedoInfo(tuxedoConfig.getIpAddress());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(data_hashtable!=null){
				tuxedoConfig.setStatus("1");
			}else{
				tuxedoConfig.setStatus("0");
			} 
			
			List clientList  = (List)data_hashtable.get("Client");
			
			request.setAttribute("clientList", clientList);
			
		}
		
		return "/application/tuxedo/client_detail.jsp";
	}
	
	private String changeMon_flag(){
		String id = getParaValue("id");
		String mon_flag = getParaValue("mon_flag");
		
		TuxedoConfigDao tuxedoConfigDao = new TuxedoConfigDao();
		try {
			tuxedoConfigDao.updateMon_flagById(id, mon_flag);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			tuxedoConfigDao.close();
		}
		TuxedoLoader loader=new TuxedoLoader();
		loader.loading();
		return list();	
	}
	
	
	
}