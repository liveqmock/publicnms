/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2009-10-29
 */

package com.afunms.application.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.jfree.data.general.DefaultPieDataset;

import com.afunms.application.dao.StorageDao;
import com.afunms.application.dao.StoragePingDao;
import com.afunms.application.dao.StorageTypeDao;
import com.afunms.application.dao.TomcatDao;
import com.afunms.application.model.Storage;
import com.afunms.application.model.Tomcat;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.BusinessDao;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.report.jfree.ChartCreator;
import com.afunms.sysset.dao.ProducerDao;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.topology.util.KeyGenerator;


public class StorageManager extends BaseManager implements ManagerInterface{

	public String execute(String action) {
		// TODO Auto-generated method stub
		
		if("list".equals(action)){
			return list();
		}
		else if("ready_add".equals(action)){
			return ready_add();
		}
		else if("add".equals(action)){
			return add();
		}
		else if("ready_edit".equals(action)){
			return ready_edit();
		}
		else if("edit".equals(action)){
			return edit();
		}
		else if("delete".equals(action)){
			return delete();
		}else if("changeMon_flag".equals(action)){
			return changeMon_flag();
		}
		else if("toDetail".equals(action)){
			return toDetail();
		}
		else if("pingDetail".equals(action)){
			return pingDetail();
		}
		else if("lsarraysiteDetail".equals(action)){
			return lsarraysiteDetail();
		}
		else if("lsarrayDetail".equals(action)){
			return lsarrayDetail();
		}
		else if("lsrankDetail".equals(action)){
			return lsrankDetail();
		}
		else if("lsextpoolDetail".equals(action)){
			return lsextpoolDetail();
		}
		else if("lsfbvolDetail".equals(action)){
			return lsfbvolDetail();
		}
		else if("lsvolgrpDetail".equals(action)){
			return lsvolgrpDetail();
		}
		else if("lsioportDetail".equals(action)){
			return lsioportDetail();
		}
		else if("lshostconnectDetail".equals(action)){
			return lshostconnectDetail();
		}
		return null;
	}
	
	public String list(){
		String jsp = "/application/storage/list.jsp";
		setTarget(jsp);
		StorageDao storageDao = new StorageDao();
		return list(storageDao);
	}
	
	public String ready_add(){
		List allbuss = null; // 业务权限
		BusinessDao businessDao = new BusinessDao();
		try {
			allbuss = businessDao.loadAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			businessDao.close();
		}
		
		request.setAttribute("allbuss", allbuss);
		
		List storageTypeList = null;  // 存储类型
		StorageTypeDao storageTypeDao = new StorageTypeDao();
		try {
			storageTypeList = storageTypeDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("storageTypeList", storageTypeList);
		
		List producerList = null;
		
		ProducerDao producerDao = new ProducerDao();
		try {
			producerList = producerDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(producerList.size());
		
		request.setAttribute("producerList", producerList);
		
		return "/application/storage/add.jsp";
		
	}
	
	public String add(){
		
		Storage storage = createStorage();
		
		storage.setId(KeyGenerator.getInstance().getNextKey());
		
		StorageDao storageDao = new StorageDao();
		try {
			storageDao.save(storage);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			storageDao.close();
		}
		
		TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
		timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(storage.getId()), timeShareConfigUtil.getObjectType("17"));
		
		/* nielin add at 2010-06-25 */
        TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
        try {
			boolean result2 = timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(storage.getId()), timeGratherConfigUtil.getObjectType("20"));
		} catch (Exception e) {
			e.printStackTrace();
		}
  		/* nielin add end*/
		
		return list();
	}
	
	public String ready_edit(){
		
		String id = getParaValue("id");
		
		Storage storage = null; // 需要修改的storage
		StorageDao storageDao = new StorageDao();
		try {
			 storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		request.setAttribute("storage", storage);
		
		
		List allbuss = null; // 业务权限
		BusinessDao businessDao = new BusinessDao();
		try {
			allbuss = businessDao.loadAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			businessDao.close();
		}
		
		request.setAttribute("allbuss", allbuss);
		
		List storageTypeList = null;  // 存储类型
		StorageTypeDao storageTypeDao = new StorageTypeDao();
		try {
			storageTypeList = storageTypeDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("storageTypeList", storageTypeList);
		
		
		List producerList = null;
		
		ProducerDao producerDao = new ProducerDao();
		try {
			producerList = producerDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(producerList.size());
		
		request.setAttribute("producerList", producerList);
		
		TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
		List timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(String.valueOf(storage.getId()), timeShareConfigUtil.getObjectType("17"));
		request.setAttribute("timeShareConfigList", timeShareConfigList);
		
		//提供已设置的采集时间信息
    	TimeGratherConfigUtil tg  = new TimeGratherConfigUtil();
    	List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("20"));
    	for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
    		timeGratherConfig.setHourAndMin();
		}
    	request.setAttribute("timeGratherConfigList", timeGratherConfigList);  
    	/* nielin end */
		
		return "/application/storage/edit.jsp";
		
	}
	
	public String edit(){
		
		Storage storage = createStorage();
		
		storage.setId(getParaIntValue("id"));
		
		StorageDao storageDao = new StorageDao();
		try {
			storageDao.update(storage);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			storageDao.close();
		}
		
		TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
		timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(storage.getId()), timeShareConfigUtil.getObjectType("17"));
		
		/* nielin add at 2010-06-25 */
        TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
        try {
			boolean result2 = timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(storage.getId()), timeGratherConfigUtil.getObjectType("20"));
		} catch (Exception e) {
			e.printStackTrace();
		}
  		/* nielin add end*/
		
		return list();
	}
	
	public String delete(){
		
		boolean result = false;
		
		String[] ids = getParaArrayValue("checkbox");
		
		StorageDao storageDao = new StorageDao();
		
		try {
			result = storageDao.delete(ids);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(result){
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			for(int i = 0 ; i < ids.length ; i ++){
				timeShareConfigUtil.deleteTimeShareConfig(ids[i], timeShareConfigUtil.getObjectType("17"));
				timeGratherConfigUtil.deleteTimeGratherConfig(ids[i], timeGratherConfigUtil.getObjectType("20"));
			}
			
			 
		}
		
		return list();
	}
	
	public String changeMon_flag(){
		String mon_flag = getParaValue("mon_flag");
		
		String id = getParaValue("id");
		
		StorageDao storageDao = new StorageDao();
		try {
			storageDao.updateMon_flag(mon_flag, id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list();
	}
	
	
	public String toDetail(){
		return pingDetail();
	}
	
	public String pingDetail(){
		
		String id = getParaValue("id");
		
		Storage storage = null;
		
		StorageDao storageDao = new StorageDao();
		
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("storage", storage);
		
		List storageTypeList = null;  // 存储类型
		StorageTypeDao storageTypeDao = new StorageTypeDao();
		try {
			storageTypeList = storageTypeDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("storageTypeList", storageTypeList);
		
		
		List producerList = null;
		
		ProducerDao producerDao = new ProducerDao();
		try {
			producerList = producerDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(producerList.size());
		
		request.setAttribute("producerList", producerList);
		int percent1 = 0;
		
		if(storage != null){
			//drawPingPieChart(storage);
			percent1 = connectivityRate(storage);
		}
		//int percent1 = Integer.parseInt(storageping+"");
		request.setAttribute("percent1", percent1+"");
		request.setAttribute("percent2", (100-percent1)+"");
		return "/application/storage/ping_detail.jsp";
	}
	
	public String lsarraysiteDetail(){
		
		String id = getParaValue("id");
		
		Storage storage = null;
		
		StorageDao storageDao = new StorageDao();
		
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List storageTypeList = null;  // 存储类型
		StorageTypeDao storageTypeDao = new StorageTypeDao();
		try {
			storageTypeList = storageTypeDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("storageTypeList", storageTypeList);
		
		
		List producerList = null;
		
		ProducerDao producerDao = new ProducerDao();
		try {
			producerList = producerDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(producerList.size());
		
		request.setAttribute("producerList", producerList);
		
		List lsarraysiteList = new ArrayList();
		
		Hashtable allTuxedoData = ShareData.getStoragedata();
		
		if(storage != null){
			
			drawPingPieChart(storage);
			
			
			Hashtable hashtable = (Hashtable)allTuxedoData.get(storage.getIpaddress());
			//System.out.println(hashtable.size()+"========hashtable=========");
			if(hashtable!=null){
				lsarraysiteList = (List)hashtable.get("lsarraysite");
				storage.setStatus("1");
				System.out.println(lsarraysiteList.size()+"===========================");
			}else{
				storage.setStatus("0");
			}
			
		}
		
		request.setAttribute("storage", storage);
		
		request.setAttribute("lsarraysiteList", lsarraysiteList);
		
		return "/application/storage/lsarraysite_detail.jsp";
	}
	
	public String lsarrayDetail(){
		
		String id = getParaValue("id");
		
		Storage storage = null;
		
		StorageDao storageDao = new StorageDao();
		
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List storageTypeList = null;  // 存储类型
		StorageTypeDao storageTypeDao = new StorageTypeDao();
		try {
			storageTypeList = storageTypeDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("storageTypeList", storageTypeList);
		
		
		List producerList = null;
		
		ProducerDao producerDao = new ProducerDao();
		try {
			producerList = producerDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(producerList.size());
		
		request.setAttribute("producerList", producerList);
		
		List lsarrayList = new ArrayList();
		
		Hashtable allTuxedoData = ShareData.getStoragedata();
		
		if(storage != null){
			
			drawPingPieChart(storage);
			
			Hashtable hashtable = (Hashtable)allTuxedoData.get(storage.getIpaddress());
			
			if(hashtable!=null){
				System.out.println(hashtable.size()+"========hashtable=========");
				lsarrayList = (List)hashtable.get("lsarray");
				storage.setStatus("1");
				System.out.println(lsarrayList.size()+"===========================");
			}else{
				storage.setStatus("0");
			}
			
		}
		
		request.setAttribute("storage", storage);
		
		request.setAttribute("lsarrayList", lsarrayList);
		
		return "/application/storage/lsarray_detail.jsp";
	}
	
	public String lsrankDetail(){
		
		String id = getParaValue("id");
		
		Storage storage = null;
		
		StorageDao storageDao = new StorageDao();
		
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List storageTypeList = null;  // 存储类型
		StorageTypeDao storageTypeDao = new StorageTypeDao();
		try {
			storageTypeList = storageTypeDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("storageTypeList", storageTypeList);
		
		
		List producerList = null;
		
		ProducerDao producerDao = new ProducerDao();
		try {
			producerList = producerDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(producerList.size());
		
		request.setAttribute("producerList", producerList);
		
		List lsrankList = new ArrayList();
		
		Hashtable allTuxedoData = ShareData.getStoragedata();
		
		if(storage != null){
			
			drawPingPieChart(storage);
			
			Hashtable hashtable = (Hashtable)allTuxedoData.get(storage.getIpaddress());
			
			if(hashtable!=null){
				System.out.println(hashtable.size()+"========hashtable=========");
				lsrankList = (List)hashtable.get("lsrank");
				storage.setStatus("1");
				System.out.println(lsrankList.size()+"===========================");
			}else{
				storage.setStatus("0");
			}
			
		}
		
		request.setAttribute("storage", storage);
		
		request.setAttribute("lsrankList", lsrankList);
		
		return "/application/storage/lsrank_detail.jsp";
	}
	
	public String lsextpoolDetail(){
		
		String id = getParaValue("id");
		
		Storage storage = null;
		
		StorageDao storageDao = new StorageDao();
		
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List storageTypeList = null;  // 存储类型
		StorageTypeDao storageTypeDao = new StorageTypeDao();
		try {
			storageTypeList = storageTypeDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("storageTypeList", storageTypeList);
		
		
		List producerList = null;
		
		ProducerDao producerDao = new ProducerDao();
		try {
			producerList = producerDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(producerList.size());
		
		request.setAttribute("producerList", producerList);
		
		List lsextpoolList = new ArrayList();
		
		Hashtable allTuxedoData = ShareData.getStoragedata();
		
		if(storage != null){
			
			drawPingPieChart(storage);
			
			Hashtable hashtable = (Hashtable)allTuxedoData.get(storage.getIpaddress());
			
			if(hashtable!=null){
				System.out.println(hashtable.size()+"========hashtable=========");
				lsextpoolList = (List)hashtable.get("lsextpool");
				storage.setStatus("1");
				System.out.println(lsextpoolList.size()+"===========================");
			}else{
				storage.setStatus("0");
			}
			
		}
		
		request.setAttribute("storage", storage);
		
		request.setAttribute("lsextpoolList", lsextpoolList);
		
		return "/application/storage/lsextpool_detail.jsp";
	}
	
	
	public String lsfbvolDetail(){
		
		String id = getParaValue("id");
		
		Storage storage = null;
		
		StorageDao storageDao = new StorageDao();
		
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List storageTypeList = null;  // 存储类型
		StorageTypeDao storageTypeDao = new StorageTypeDao();
		try {
			storageTypeList = storageTypeDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("storageTypeList", storageTypeList);
		
		
		List producerList = null;
		
		ProducerDao producerDao = new ProducerDao();
		try {
			producerList = producerDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(producerList.size());
		
		request.setAttribute("producerList", producerList);
		
		List lsfbvolList = new ArrayList();
		
		Hashtable allTuxedoData = ShareData.getStoragedata();
		
		if(storage != null){
			
			drawPingPieChart(storage);
			
			Hashtable hashtable = (Hashtable)allTuxedoData.get(storage.getIpaddress());
			
			if(hashtable!=null){
				System.out.println(hashtable.size()+"========hashtable=========");
				lsfbvolList = (List)hashtable.get("lsfbvol");
				storage.setStatus("1");
				System.out.println(lsfbvolList.size()+"===========================");
			}else{
				storage.setStatus("0");
			}
		}
		
		request.setAttribute("storage", storage);
		
		request.setAttribute("lsfbvolList", lsfbvolList);
		
		return "/application/storage/lsfbvol_detail.jsp";
	}
	
	
	public String lsvolgrpDetail(){
		
		String id = getParaValue("id");
		
		Storage storage = null;
		
		StorageDao storageDao = new StorageDao();
		
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List storageTypeList = null;  // 存储类型
		StorageTypeDao storageTypeDao = new StorageTypeDao();
		try {
			storageTypeList = storageTypeDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("storageTypeList", storageTypeList);
		
		
		List producerList = null;
		
		ProducerDao producerDao = new ProducerDao();
		try {
			producerList = producerDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(producerList.size());
		
		request.setAttribute("producerList", producerList);
		
		List lsvolgrpList = new ArrayList();
		
		Hashtable allTuxedoData = ShareData.getStoragedata();
		
		if(storage != null){
			
			drawPingPieChart(storage);
			
			Hashtable hashtable = (Hashtable)allTuxedoData.get(storage.getIpaddress());
			if(hashtable!=null){
				System.out.println(hashtable.size()+"========hashtable=========");
				lsvolgrpList = (List)hashtable.get("lsvolgrp");
				storage.setStatus("1");
				System.out.println(lsvolgrpList.size()+"===========================");
			}else{
				storage.setStatus("0");
			}
			
		}
		
		request.setAttribute("storage", storage);
		
		request.setAttribute("lsvolgrpList", lsvolgrpList);
		
		return "/application/storage/lsvolgrp_detail.jsp";
	}
	
	public String lsioportDetail(){
		
		String id = getParaValue("id");
		
		Storage storage = null;
		
		StorageDao storageDao = new StorageDao();
		
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List storageTypeList = null;  // 存储类型
		StorageTypeDao storageTypeDao = new StorageTypeDao();
		try {
			storageTypeList = storageTypeDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("storageTypeList", storageTypeList);
		
		
		List producerList = null;
		
		ProducerDao producerDao = new ProducerDao();
		try {
			producerList = producerDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(producerList.size());
		
		request.setAttribute("producerList", producerList);
		
		List lsioportList = new ArrayList();
		
		Hashtable allTuxedoData = ShareData.getStoragedata();
		
		if(storage != null){
			
			drawPingPieChart(storage);
			
			Hashtable hashtable = (Hashtable)allTuxedoData.get(storage.getIpaddress());
			if(hashtable!=null){
				System.out.println(hashtable.size()+"========hashtable=========");
				lsioportList = (List)hashtable.get("lsioport");
				storage.setStatus("1");
				System.out.println(lsioportList.size()+"===========================");
			}else{
				storage.setStatus("0");
			}
			
		}
		
		request.setAttribute("storage", storage);
		
		request.setAttribute("lsioportList", lsioportList);
		
		return "/application/storage/lsioport_detail.jsp";
	}
	
	public String lshostconnectDetail(){
		
		String id = getParaValue("id");
		
		Storage storage = null;
		
		StorageDao storageDao = new StorageDao();
		
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List storageTypeList = null;  // 存储类型
		StorageTypeDao storageTypeDao = new StorageTypeDao();
		try {
			storageTypeList = storageTypeDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("storageTypeList", storageTypeList);
		
		
		List producerList = null;
		
		ProducerDao producerDao = new ProducerDao();
		try {
			producerList = producerDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(producerList.size());
		
		request.setAttribute("producerList", producerList);
		
		request.setAttribute("storage", storage);
		
		List lshostconnectList = new ArrayList();
		
		Hashtable allTuxedoData = ShareData.getStoragedata();
		
		if(storage != null){
			
			drawPingPieChart(storage);
			
			Hashtable hashtable = (Hashtable)allTuxedoData.get(storage.getIpaddress());
			if(hashtable!=null){
				System.out.println(hashtable.size()+"========hashtable=========");
				lshostconnectList = (List)hashtable.get("lshostconnect");
				storage.setStatus("1");
				System.out.println(lshostconnectList.size()+"===========================");
			}else{
				storage.setStatus("0");
			}
			
		}
		
		request.setAttribute("lshostconnectList", lshostconnectList);
		
		return "/application/storage/lshostconnect_detail.jsp";
	}
	
	
	public void drawPingPieChart(Storage storage){
		
		double storageping = connectivityRate(storage);
		
		DefaultPieDataset dpd = new DefaultPieDataset();
		dpd.setValue("可用时间",storageping);
		dpd.setValue("不可用时间",100 - storageping);
		String chart1 = ChartCreator.createPieChart(dpd,"",120,120);
		request.setAttribute("chart1", chart1);
	}
	
	
	public int connectivityRate(Storage storage)
    {    	   
		int avgpingcon=0;
		
		StoragePingDao storagePingDao = new StoragePingDao();
		try{

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			String avgpingconstr = storagePingDao.findAverageByTime(storage.getIpaddress(), starttime1, totime1);
			try {
				avgpingcon = Double.valueOf(avgpingconstr).intValue();
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				avgpingcon = 0;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			storagePingDao.close();
		}
		return avgpingcon;
    }


	
	public Storage createStorage(){
		Storage storage = new Storage();
		String ipaddress = getParaValue("ipaddress");
		String name = getParaValue("name");
		String username = getParaValue("username");
		String password = getParaValue("password");
		String mon_flag = getParaValue("mon_flag");
		String collecttype = getParaValue("collecttype");
		String company = getParaValue("company");
		String type = getParaValue("type");
		String serialNumber = getParaValue("serialNumber");
		String collectTime = getParaValue("collectTime");
		String supperid = getParaValue("supperid");
		String sendemail = getParaValue("sendemail");
		String sendmobiles = getParaValue("sendmobiles");
		String sendphone = getParaValue("sendphone");
		String bid = getParaValue("bid");
		
		String[] businessboxes = getParaArrayValue("businessbox");
		
		storage.setIpaddress(ipaddress);
		storage.setName(name);
		storage.setUsername(username);
		storage.setPassword(password);
		storage.setMon_flag(mon_flag);
		storage.setCollecttype(collecttype);
		storage.setCompany(company);
		storage.setType(type);
		storage.setSerialNumber(serialNumber);
		storage.setBid(bid);
		storage.setCollectTime(collectTime);
		storage.setSupperid(supperid);
		storage.setSendemail(sendemail);
		storage.setSendmobiles(sendmobiles);
		storage.setSendphone(sendphone);
		
		return storage;
		
	}
	
	
}