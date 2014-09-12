package com.afunms.detail.service.systemInfo;

import java.util.List;
import java.util.Vector;

import com.afunms.application.dao.StorageTypeDao;
import com.afunms.application.model.StorageTypeVo;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Supper;
import com.afunms.detail.reomte.model.DiskPerfInfo;
import com.afunms.inform.util.SystemSnap;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.sysset.dao.ProducerDao;
import com.afunms.sysset.model.Producer;
import com.afunms.temp.dao.SystemTempDao;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.util.NodeHelper;

public class SystemInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public SystemInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	public List<NodeTemp> getCurrAllSystemInfo(){
		return getCurrAllSystemInfo(null);
	}
	
	public List<NodeTemp> getCurrAllSystemInfo(String[] subentities){
		SystemTempDao systemTempDao = new SystemTempDao();
		List<NodeTemp> nodeTempList = null;
		try {
			nodeTempList = systemTempDao.getNodeTempList(nodeid, type, subtype, subentities);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			systemTempDao.close();
		}
		return nodeTempList;
	}
	
	public String getStautsInfo(){
		return  NodeHelper.getCurrentStatusImage(SystemSnap.getNetworkStatus(nodeid));
	}
	
	public String getCategoryInfo(int category){
		return NodeHelper.getNodeCategory(category);
	}
	
	public String getSupperInfo(String supperId){
		SupperDao supperdao = new SupperDao();
    	Supper supper = null;
    	String suppername = "";
    	ResourceCenter res = ResourceCenter.getInstance();	
    	try{
    		supper = (Supper)supperdao.findByID(supperId);
    		if(supper != null){
    			suppername = supper.getSu_name()+"("+supper.getSu_dept()+")";
    			suppername = "<a href=\"#\"  style=\"cursor:hand\" onclick=\"window.showModalDialog('/afunms/supper.do?action=read&id="+supper.getSu_id()+"',window,',dialogHeight:400px;dialogWidth:600px')\">"+suppername+"</a>";
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		supperdao.close();
    	}
		return suppername;
	}
	
	public String getStorageProducerInfo(String storageTypeId){
		String str = "";
		StorageTypeVo storageType = null;
		StorageTypeDao storageTypeDao = new StorageTypeDao();
		try {
			 storageType = (StorageTypeVo)storageTypeDao.findByID(storageTypeId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			storageTypeDao.close();
		}
		
		if(storageType == null){
			return str;
		}
		Producer producer = null;
		ProducerDao producerDao = new ProducerDao();
		try {
			producer = (Producer)producerDao.findByID(storageType.getProducer() + "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			producerDao.close();
		}
		
		if(producer == null) {
			return str;
		}
		
		str = producer.getProducer() + "(" + storageType.getModel() + ")";
		
		return str;
	}
	
	public Vector<Systemcollectdata> getSystemInfo(){
		Vector<Systemcollectdata> retVector = new Vector<Systemcollectdata>();
		SystemTempDao systemTempDao = new SystemTempDao();
		try {
			retVector = systemTempDao.getSystemInfo(nodeid, type, subtype);
		} catch (RuntimeException e) {    
			e.printStackTrace();
		} finally{
			systemTempDao.close();
		}
		return retVector;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
