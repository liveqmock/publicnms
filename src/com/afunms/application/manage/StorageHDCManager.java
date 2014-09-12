/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2009-10-29
 */

package com.afunms.application.manage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.polling.snmp.hdc.HdcDFMessage;
import com.afunms.polling.om.HdcMessage;

import org.jfree.data.general.DefaultPieDataset;

import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.StorageHDCDao;
import com.afunms.application.dao.StoragePingDao;
import com.afunms.application.dao.StorageTypeDao;
import com.afunms.application.dao.TomcatDao;
import com.afunms.application.model.Storage;
import com.afunms.application.model.Tomcat;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.BusinessDao;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Interfacecollectdata;

import com.afunms.report.jfree.ChartCreator;
import com.afunms.sysset.dao.ProducerDao;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.util.KeyGenerator;
import com.afunms.application.dao.StorageHDECTopohostNode;


public class StorageHDCManager extends BaseManager implements ManagerInterface{

	public String execute(String action) {
		// TODO Auto-generated method stub
		
		if("list".equals(action)){
			return list();
		}else if("pingDetail_s".equals(action)){
			return pingDetail_s();
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
		}else if("toDetailBoracde".equals(action)){
			return toDetailBoracde();
		}else if("lsarraysiteDetailBorcade".equals(action)){
			return lsarraysiteDetailBorcade();
		}else if("lsarrayDetailBorcade".equals(action)){
			return lsarrayDetailBorcade();
		}else if("lsrankDetailBorcade".equals(action)){
			return lsrankDetailBorcade();
		}else if("lsextpoolDetailBorcade".equals(action)){
			return lsextpoolDetailBorcade();
		}else if("lsfbvolDetailBorcade".equals(action)){
			return lsfbvolDetailBorcade();
		}else if("lsvolgrpDetailBorcade".equals(action)){
			return lsvolgrpDetailBorcade();
		}else if("pingDetailBorcade".equals(action)){
			return pingDetailBorcade();
		}else if(action.equals("environment")){
			return environment();
		}else if(action.equals("running")){
			return running();
		}
		else if(action.equals("system")){
			return system();
		}else if(action.equals("event")){
			return event();
		}else if(action.equals("syslist")){
			return syslist();
		}else if(action.equals("lun")){
			return lun();
		}else if(action.equals("wwn")){
			return wwn();
		}else if(action.equals("slun")){
			return slun();
		}
		return null;
	}
	
	public String list(){
		String jsp = "/application/storagehdc/list.jsp";
		setTarget(jsp);
		StorageHDCDao storageDao = new StorageHDCDao();
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
		
		return "/application/storagehdc/add.jsp";
		
	}
	
	public String add(){
		
//		Storage storage = createStorage();
//		Storage	storagepoto = createStoragetopo(); 
//		storage.setId(KeyGenerator.getInstance().getNextKey());
//		
//		//System.out.println("====storage11111===="+storage);
//		//System.out.println("====storagepoto22222222===="+storagepoto);
//		StorageHDCDao storageDao = new StorageHDCDao();
//		StorageHDECTopohostNode topostorageDao = new StorageHDECTopohostNode();
//		//Host node = (Host)PollingEngine.getInstance().getNodeByIP(list());
//		try {
//			storageDao.save(storage);
//			topostorageDao.topohostsave(storage);
//			//System.out.println("---------=================-=----=-=");
//			NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
//			//System.out.println("---------========nodeGatherIndicatorsUtil=========-=----=-="+nodeGatherIndicatorsUtil);
//			nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(storage.getId()+"", AlarmConstant.TYPE_STORAGEHD, storage.getType(),"1");
//			//System.out.println("---------========nodeGatherIndicatorsUtil====222222=====-=----=-="+nodeGatherIndicatorsUtil.toString());
//			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
//			alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(storage.getId()+"", AlarmConstant.TYPE_STORAGEHD, "hdc2980");
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			storageDao.close();
//		}
//		
//		TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
//		timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(storage.getId()), timeShareConfigUtil.getObjectType("17"));
//		timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(storagepoto.getId()), timeShareConfigUtil.getObjectType("17"));
//		
//		/* nielin add at 2010-06-25 */
//        TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
//        try {
//			boolean result2 = timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(storage.getId()), timeGratherConfigUtil.getObjectType("20"));
//			boolean result3 = timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(storagepoto.getId()), timeGratherConfigUtil.getObjectType("20"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//  		/* nielin add end*/
//		
		return list();
	}
	
	public String ready_edit(){
		
		String id = getParaValue("id");
		
		Storage storage = null; // 需要修改的storage
		StorageHDCDao storageDao = new StorageHDCDao();
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
		
		return "/application/storagehdc/edit.jsp";
		
	}
	
	public String edit(){
		
		Storage storage = createStorage();
		
		storage.setId(getParaIntValue("id"));
		
		StorageHDCDao storageDao = new StorageHDCDao();
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
		
		StorageHDCDao storageDao = new StorageHDCDao();
		
		
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
		
		StorageHDCDao storageDao = new StorageHDCDao();
		StorageHDECTopohostNode topostorageDao = new StorageHDECTopohostNode();
		try {
			storageDao.updateMon_flag(mon_flag, id);
			topostorageDao.updateMon_flagtopo(mon_flag, id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list();
	}
	
	
	public String toDetail(){
		return pingDetail();
	}
	public String toDetailBoracde(){
		
		String id = getParaValue("id");
		Storage storage = null;
		
		StorageHDCDao storageDao = new StorageHDCDao();
		
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("storage", storage);
		
//		List storageTypeList = null;  // 存储类型
//		StorageTypeDao storageTypeDao = new StorageTypeDao();
//		try {
//			storageTypeList = storageTypeDao.loadAll();
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		request.setAttribute("storageTypeList", storageTypeList);
//		
//		
//		List producerList = null;
//		
//		ProducerDao producerDao = new ProducerDao();
//		try {
//			producerList = producerDao.loadAll();
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(producerList.size());
//		
//		request.setAttribute("producerList", producerList);
//		int percent1 = 0;
//		
//		if(storage != null){
//			//drawPingPieChart(storage);
//			percent1 = connectivityRate(storage);
//		}
//		//int percent1 = Integer.parseInt(storageping+"");
//		request.setAttribute("percent1", percent1+"");
//		request.setAttribute("percent2", (100-percent1)+"");
		return "/application/storagehdc/ping_detailborcade.jsp";
	}
	public String pingDetail(){
		
		String id = getParaValue("id");
		Storage storage = null;
		
		StorageHDCDao storageDao = new StorageHDCDao();
		
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("storage", storage);
		
		

		int percent1 = 0;
		return "/application/storagehdc/ping_detail.jsp";
	}
//	public String pingDetail(){
//		
//		String id = getParaValue("id");
//		
//		Storage storage = null;
//		
//		StorageHDCDao storageDao = new StorageHDCDao();
//		
//		try {
//			storage = (Storage)storageDao.findByID(id);
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		request.setAttribute("storage", storage);
//		
//		List storageTypeList = null;  // 存储类型
//		StorageTypeDao storageTypeDao = new StorageTypeDao();
//		try {
//			storageTypeList = storageTypeDao.loadAll();
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		request.setAttribute("storageTypeList", storageTypeList);
//		
//		
//		List producerList = null;
//		
//		ProducerDao producerDao = new ProducerDao();
//		try {
//			producerList = producerDao.loadAll();
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(producerList.size());
//		
//		request.setAttribute("producerList", producerList);
//		int percent1 = 0;
//		
//		if(storage != null){
//			//drawPingPieChart(storage);
//			percent1 = connectivityRate(storage);
//		}
//		//int percent1 = Integer.parseInt(storageping+"");
//		request.setAttribute("percent1", percent1+"");
//		request.setAttribute("percent2", (100-percent1)+"");
//		return "/application/storagehdc/ping_detail.jsp";
//	}
	
	public String lsarraysiteDetail(){
		System.out.println("wo  zou le ma ********************");
		String id = getParaValue("id");
		
		Storage storage = null;
		
		StorageHDCDao storageDao = new StorageHDCDao();
		
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
	
	public String lsarraysiteDetailBorcade(){
		//BrocadeMessage
		String id = getParaValue("id");
		HdcDFMessage brocadeMessage;
		Storage storage = null;
		List lsarraysiteList = new ArrayList();
		StorageHDCDao storageDao = new StorageHDCDao();
		
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql = "select * from brocade_sys_info where nodeid=" + id;
		
		
		try {
			DBManager db =  new DBManager();
			ResultSet rs = (ResultSet) db.executeQuery(sql);
			while(rs.next()){
				brocadeMessage = new HdcDFMessage();
				String dfSystemProductName = rs.getString("dfSystemProductName");
				String dfSystemMicroRevision = rs.getString("dfSystemMicroRevision");
				String dfSystemSerialNumber = rs.getString("dfSystemSerialNumber");

				brocadeMessage.setDfSystemProductName(dfSystemProductName);
				brocadeMessage.setDfSystemMicroRevision(dfSystemMicroRevision);
				brocadeMessage.setDfSystemSerialNumber(dfSystemSerialNumber);
				lsarraysiteList.add(brocadeMessage);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		request.setAttribute("storage", storage);		
		request.setAttribute("lsarraysiteList", lsarraysiteList);		
		return "/application/storagehdc/lsarraysiteborcade_detail.jsp";
	}
	
//	public String lsarrayDetail(){
//		
//		String id = getParaValue("id");
//		
//		Storage storage = null;
//		
//		StorageHDCDao storageDao = new StorageHDCDao();
//		
//		try {
//			storage = (Storage)storageDao.findByID(id);
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		List storageTypeList = null;  // 存储类型
//		StorageTypeDao storageTypeDao = new StorageTypeDao();
//		try {
//			storageTypeList = storageTypeDao.loadAll();
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		request.setAttribute("storageTypeList", storageTypeList);
//		
//		
//		List producerList = null;
//		
//		ProducerDao producerDao = new ProducerDao();
//		try {
//			producerList = producerDao.loadAll();
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(producerList.size());
//		
//		request.setAttribute("producerList", producerList);
//		
//		List lsarrayList = new ArrayList();
//		
//		Hashtable allTuxedoData = ShareData.getStoragedata();
//		
//		if(storage != null){
//			
//			drawPingPieChart(storage);
//			
//			Hashtable hashtable = (Hashtable)allTuxedoData.get(storage.getIpaddress());
//			
//			if(hashtable!=null){
//				System.out.println(hashtable.size()+"========hashtable=========");
//				lsarrayList = (List)hashtable.get("lsarray");
//				storage.setStatus("1");
//				System.out.println(lsarrayList.size()+"===========================");
//			}else{
//				storage.setStatus("0");
//			}
//			
//		}
//		
//		request.setAttribute("storage", storage);
//		
//		request.setAttribute("lsarrayList", lsarrayList);
//		
//		return "/application/storage/lsarray_detail.jsp";
//	}
	
	
	
	public String lsarrayDetailBorcade(){
		
		//BrocadeMessage
		String id = getParaValue("id");
		HdcDFMessage brocadeMessage;
		Storage storage = null;
		List lsarrayList = new ArrayList();
		StorageHDCDao storageDao = new StorageHDCDao();
	
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql = "select * from brocade_luns_switch where nodeid=" + id;
	
	
		try {
			DBManager db =  new DBManager();
			ResultSet rs = (ResultSet) db.executeQuery(sql);
			while(rs.next()){
				brocadeMessage = new HdcDFMessage();
				String dfSwitchSerialNumber = rs.getString("dfSwitchSerialNumber");
				String dfSwitchPortID = rs.getString("dfSwitchPortID");
				String dfSwitchOnOff = rs.getString("dfSwitchOnOff");
				String dfSwitchControlStatus = rs.getString("dfSwitchControlStatus");


				brocadeMessage.setDfSwitchSerialNumber(dfSwitchSerialNumber);
				brocadeMessage.setDfSwitchPortID(dfSwitchPortID);
				brocadeMessage.setDfSwitchOnOff(dfSwitchOnOff);
				brocadeMessage.setDfSwitchControlStatus(dfSwitchControlStatus);
				lsarrayList.add(brocadeMessage);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
			request.setAttribute("storage", storage);
		
			request.setAttribute("lsarrayList", lsarrayList);
		
			return "/application/storagehdc/lsarrayborcade_detail.jsp";
	}
	
	public String lsrankDetailBorcade(){
		
		//BrocadeMessage
		String id = getParaValue("id");
		HdcDFMessage brocadeMessage;
		Storage storage = null;
		List lsrankList = new ArrayList();
		StorageHDCDao storageDao = new StorageHDCDao();
	
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql = "select * from brocade_luns_wwn where nodeid=" + id;
	
	
		try {
			DBManager db =  new DBManager();
			ResultSet rs = (ResultSet) db.executeQuery(sql);
			while(rs.next()){
				brocadeMessage = new HdcDFMessage();
				String dfWWNSerialNumber = rs.getString("dfWWNSerialNumber");
				String dfWWNPortID = rs.getString("dfWWNPortID");
				String dfWWNControlIndex = rs.getString("dfWWNControlIndex");
				String dfWWNWWN = rs.getString("dfWWNWWN");
				String dfWWNID = rs.getString("dfWWNID");
				String dfWWNNickname = rs.getString("dfWWNNickname");
				String dfWWNUseNickname = rs.getString("dfWWNUseNickname");
				String dfWWNControlStatus = rs.getString("dfWWNControlStatus");


				brocadeMessage.setDfWWNSerialNumber(dfWWNSerialNumber);
				brocadeMessage.setDfWWNPortID(dfWWNPortID);
				brocadeMessage.setDfWWNControlIndex(dfWWNControlIndex);
				brocadeMessage.setDfWWNWWN(dfWWNWWN);
				brocadeMessage.setDfWWNID(dfWWNID);
				brocadeMessage.setDfWWNNickname(dfWWNNickname);
				brocadeMessage.setDfWWNUseNickname(dfWWNUseNickname);
				brocadeMessage.setDfWWNControlStatus(dfWWNControlStatus);
				lsrankList.add(brocadeMessage);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		request.setAttribute("storage", storage);
		
		request.setAttribute("lsrankList", lsrankList);
		
		return "/application/storagehdc/lsrankborcade_detail.jsp";
	}
	public String lsextpoolDetailBorcade(){
		
		//BrocadeMessage
		String id = getParaValue("id");
		HdcDFMessage brocadeMessage;
		Storage storage = null;
		List lsextpoolList = new ArrayList();
		StorageHDCDao storageDao = new StorageHDCDao();
	
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql = "select * from brocade_luns_wwn_group where nodeid=" + id;
	
	
		try {
			DBManager db =  new DBManager();
			ResultSet rs = (ResultSet) db.executeQuery(sql);
			while(rs.next()){
				brocadeMessage = new HdcDFMessage();
				String dfWWNGroupSerialNumber = rs.getString("dfWWNGroupSerialNumber");
				String dfWWNGroupPortID = rs.getString("dfWWNGroupPortID");
				String dfWWNGroupControlIndex = rs.getString("dfWWNGroupControlIndex");
				String dfWWNGroupID = rs.getString("dfWWNGroupID");
				String dfWWNGroupNickname = rs.getString("dfWWNGroupNickname");
				String dfWWNGroupedWWNs = rs.getString("dfWWNGroupedWWNs");
				String dfWWNGroupControlStatus = rs.getString("dfWWNGroupControlStatus");
				
				brocadeMessage.setDfWWNGroupSerialNumber(dfWWNGroupSerialNumber);
				brocadeMessage.setDfWWNGroupPortID(dfWWNGroupPortID);
				brocadeMessage.setDfWWNGroupControlIndex(dfWWNGroupControlIndex);
				brocadeMessage.setDfWWNGroupID(dfWWNGroupID);
				brocadeMessage.setDfWWNGroupNickname(dfWWNGroupNickname);
				brocadeMessage.setDfWWNGroupedWWNs(dfWWNGroupedWWNs);
				brocadeMessage.setDfWWNGroupControlStatus(dfWWNGroupControlStatus);
				lsextpoolList.add(brocadeMessage);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		request.setAttribute("storage", storage);
		
		request.setAttribute("lsextpoolList", lsextpoolList);
		
		return "/application/storagehdc/lsextpoolborcade_detail.jsp";
	}
	public String lsfbvolDetailBorcade(){
		
		//BrocadeMessage
		String id = getParaValue("id");
		HdcDFMessage brocadeMessage;
		Storage storage = null;
		List lsfbvolList = new ArrayList();
		StorageHDCDao storageDao = new StorageHDCDao();
	
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql = "select * from brocade_luns_lun where nodeid=" + id;
	
	
		try {
			DBManager db =  new DBManager();
			ResultSet rs = (ResultSet) db.executeQuery(sql);
			while(rs.next()){
				brocadeMessage = new HdcDFMessage();
				String dfLUNSerialNumber = rs.getString("dfLUNSerialNumber");
				String dfLUNPortID = rs.getString("dfLUNPortID");
				String dfLUNLUN = rs.getString("dfLUNLUN");
				String dfLUNWWNSecurity = rs.getString("dfLUNWWNSecurity");
				String dfLUNWWNGroupSecurity = rs.getString("dfLUNWWNGroupSecurity");
				String dfLUNControlStatus = rs.getString("dfLUNControlStatus");
			
				brocadeMessage.setDfLUNSerialNumber(dfLUNSerialNumber);
				brocadeMessage.setDfLUNPortID(dfLUNPortID);
				brocadeMessage.setDfLUNLUN(dfLUNLUN);
				brocadeMessage.setDfLUNWWNSecurity(dfLUNWWNSecurity);
				brocadeMessage.setDfLUNWWNGroupSecurity(dfLUNWWNGroupSecurity);
				brocadeMessage.setDfLUNControlStatus(dfLUNControlStatus);
				lsfbvolList.add(brocadeMessage);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		request.setAttribute("storage", storage);
		
		request.setAttribute("lsfbvolList", lsfbvolList);
		
		return "/application/storagehdc/lsfbvolborcade_detail.jsp";
	}
	
	public String lsvolgrpDetailBorcade(){
		
		//BrocadeMessage
		String id = getParaValue("id");
		HdcDFMessage brocadeMessage;
		Storage storage = null;
		List lsvolgrpList = new ArrayList();
		StorageHDCDao storageDao = new StorageHDCDao();
	
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql = "select * from brocade_luns_lun_group where nodeid=" + id;
	
	
		try {
			DBManager db =  new DBManager();
			ResultSet rs = (ResultSet) db.executeQuery(sql);
			while(rs.next()){
				brocadeMessage = new HdcDFMessage();
				String dfLUNGroupSerialNumber = rs.getString("dfLUNGroupSerialNumber");
				String dfLUNGroupPortID = rs.getString("dfLUNGroupPortID");
				String dfLUNGroupControlIndex = rs.getString("dfLUNGroupControlIndex");
				String dfLUNGroupID = rs.getString("dfLUNGroupID");
				String dfLUNGroupNickname = rs.getString("dfLUNGroupNickname");
				String dfLUNGroupedLUNs = rs.getString("dfLUNGroupedLUNs");
				String dfLUNGroupWWNSecurity = rs.getString("dfLUNGroupWWNSecurity");
				String dfLUNGroupWWNGroupSecurity = rs.getString("dfLUNGroupWWNGroupSecurity");
				String dfLUNGroupControlStatus = rs.getString("dfLUNGroupControlStatus");
			
				brocadeMessage.setDfLUNGroupSerialNumber(dfLUNGroupSerialNumber);
				brocadeMessage.setDfLUNGroupPortID(dfLUNGroupPortID);
				brocadeMessage.setDfLUNGroupControlIndex(dfLUNGroupControlIndex);
				brocadeMessage.setDfLUNGroupID(dfLUNGroupID);
				brocadeMessage.setDfLUNGroupNickname(dfLUNGroupNickname);
				brocadeMessage.setDfLUNGroupedLUNs(dfLUNGroupedLUNs);
				brocadeMessage.setDfLUNGroupWWNSecurity(dfLUNGroupWWNSecurity);
				brocadeMessage.setDfLUNGroupWWNGroupSecurity(dfLUNGroupWWNGroupSecurity);
				brocadeMessage.setDfLUNGroupControlStatus(dfLUNGroupControlStatus);
				lsvolgrpList.add(brocadeMessage);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		request.setAttribute("storage", storage);
		
		request.setAttribute("lsvolgrpList", lsvolgrpList);
		
		return "/application/storagehdc/lsvolgrpBorcade_detail.jsp";
	}
	


	public String pingDetailBorcade(){
	
		//BrocadeMessage
		String id = getParaValue("id");
		HdcDFMessage brocadeMessage;
		Storage storage = null;
		List lsvolgrpList = new ArrayList();
		StorageHDCDao storageDao = new StorageHDCDao();
	
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql = "select * from brocade_Portinf where nodeid=" + id;
	
	
		try {
			DBManager db =  new DBManager();
			ResultSet rs = (ResultSet) db.executeQuery(sql);
			while(rs.next()){
				brocadeMessage = new HdcDFMessage();
				String dfPortSerialNumber = rs.getString("dfPortSerialNumber");
				String dfPortID = rs.getString("dfPortID");
				String dfPortKind = rs.getString("dfPortKind");
				String dfPortHostMode = rs.getString("dfPortHostMode");
				String dfPortFibreAddress = rs.getString("dfPortFibreAddress");
				String dfPortFibreTopology = rs.getString("dfPortFibreTopology");
				String dfPortControlStatus = rs.getString("dfPortControlStatus");
				String dfPortDisplayName = rs.getString("dfPortDisplayName");
				String dfPortWWN = rs.getString("dfPortWWN");
			
				brocadeMessage.setDfPortSerialNumber(dfPortSerialNumber);
				brocadeMessage.setDfPortID(dfPortID);
				brocadeMessage.setDfPortKind(dfPortKind);
				brocadeMessage.setDfPortHostMode(dfPortHostMode);
				brocadeMessage.setDfPortFibreAddress(dfPortFibreAddress);
				brocadeMessage.setDfPortFibreTopology(dfPortFibreTopology);
				brocadeMessage.setDfPortControlStatus(dfPortControlStatus);
				brocadeMessage.setDfPortDisplayName(dfPortDisplayName);
				brocadeMessage.setDfPortWWN(dfPortWWN);
				lsvolgrpList.add(brocadeMessage);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		request.setAttribute("storage", storage);
		
		request.setAttribute("lsvolgrpList", lsvolgrpList);
		
		return "/application/storagehdc/pingborcade_detail.jsp";
}
	
	/**
	 *jhl
	 * 环境信息
	 * @return
	 */
	public String lsarrayDetail(){
		//HdcMessage
		String id = getParaValue("id");
		HdcMessage hdcManage;
		Storage storage = null;
		List list = new ArrayList();
		StorageHDCDao storageDao = new StorageHDCDao();
		
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql = "select * from hdc_environment_state where dkuHWPS !='RAID600' and nodeid=" + id;
		System.out.println("###############"+sql);
		
		try {
			DBManager db =  new DBManager();
			ResultSet rs = (ResultSet) db.executeQuery(sql);
			while(rs.next()){
				hdcManage = new HdcMessage();
				String dkuRaidListIndexSerialNumber = rs.getString("dkuRaidListIndexSerialNumber");
				String dkuHWPS = rs.getString("dkuHWPS");
				String dkuHWFan = rs.getString("dkuHWFan");
				String dkuHWEnvironment = rs.getString("dkuHWEnvironment");
				String dkuHWDrive = rs.getString("dkuHWDrive");
				
				hdcManage.setDkuRaidListIndexSerialNumber(dkuRaidListIndexSerialNumber);
				hdcManage.setDkuHWPS(dkuHWPS);
				hdcManage.setDkuHWFan(dkuHWFan);
				hdcManage.setDkuHWEnvironment(dkuHWEnvironment);
				hdcManage.setDkuHWDrive(dkuHWDrive);
				list.add(hdcManage);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		request.setAttribute("storage", storage);
		request.setAttribute("storagelist", list);
		return "/application/storagehdc/lsarray_detail.jsp";
	}
	/**
	 * jhl
	 * 运行状态
	 * @return
	 * 
	 */
	public String lsrankDetail(){
		//HdcMessage
		String id = getParaValue("id");
		HdcMessage hdcManage;
		Storage storage = null;
		List list = new ArrayList();
		String sql = "select * from hdc_run_state where nodeid=" + id;
		StorageHDCDao storageDao = new StorageHDCDao();
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		try {
			DBManager db =  new DBManager();
			ResultSet rs = (ResultSet) db.executeQuery(sql);
			while(rs.next()){
				hdcManage = new HdcMessage();
				String dkcRaidListIndexSerialNumber = rs.getString("dkcRaidListIndexSerialNumber");
				String dkcHWProcessor = rs.getString("dkcHWProcessor");
				String dkcHWCSW = rs.getString("dkcHWCSW");
				String dkcHWCache = rs.getString("dkcHWCache");
				String dkcHWSM = rs.getString("dkcHWSM");
				String dkcHWPS = rs.getString("dkcHWPS");
				String dkcHWBattery = rs.getString("dkcHWBattery");
				String dkcHWFan = rs.getString("dkcHWFan");
				String dkcHWEnvironment = rs.getString("dkcHWEnvironment");
				
				hdcManage.setDkcRaidListIndexSerialNumber(dkcRaidListIndexSerialNumber);
				hdcManage.setDkcHWProcessor(dkcHWProcessor);
				hdcManage.setDkcHWCSW(dkcHWCSW);
				hdcManage.setDkcHWCache(dkcHWCache);
				hdcManage.setDkcHWSM(dkcHWSM);
				
				hdcManage.setDkcHWPS(dkcHWPS);
				hdcManage.setDkcHWBattery(dkcHWBattery);
				hdcManage.setDkcHWFan(dkcHWFan);
				hdcManage.setDkcHWEnvironment(dkcHWEnvironment);
				list.add(hdcManage);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		request.setAttribute("storage", storage);
		request.setAttribute("storagelist", list);
		return "/application/storagehdc/lsrank_detail.jsp";
	}
/**
	 * 系统信息
	 * @return
	 * 
	 */
	public String lsextpoolDetail(){//HdcMessage
		String id = getParaValue("id");
		HdcMessage hdcManage;
		Storage storage = null;
		List list = new ArrayList();
		String sql = "select * from hdc_sys_info where nodeid=" + id;
		StorageHDCDao storageDao = new StorageHDCDao();
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		try {
			DBManager db =  new DBManager();
			ResultSet rs = (ResultSet) db.executeQuery(sql);
			while(rs.next()){
				hdcManage = new HdcMessage();
				String raidlistSerialNumber = rs.getString("raidlistSerialNumber");
				String raidlistMibNickName = rs.getString("raidlistMibNickName");
				String raidlistDKCMainVersion = rs.getString("raidlistDKCMainVersion");
				String raidlistDKCProductName = rs.getString("raidlistDKCProductName");
				
				
				hdcManage.setRaidlistSerialNumber(raidlistSerialNumber);
				hdcManage.setRaidlistMibNickName(raidlistMibNickName);
				hdcManage.setRaidlistDKCMainVersion(raidlistDKCMainVersion);
				hdcManage.setRaidlistDKCProductName(raidlistDKCProductName);
				list.add(hdcManage);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		request.setAttribute("storage", storage);
		request.setAttribute("storagelist", list);
		return "/application/storagehdc/lsextpool_detail.jsp";
	}
/**
	 * jhl
	 * @return
	 */
	public String pingDetail_s(){
		//HdcMessage
		String id = getParaValue("id");
		HdcMessage hdcManage;
		Storage storage = null;
		List list = new ArrayList();
		StorageHDCDao storageDao = new StorageHDCDao();
		
		try {
			storage = (Storage)storageDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql = "select * from hdc_eventlist where nodeid=" + id;
		try {
			DBManager db =  new DBManager();
			ResultSet rs = (ResultSet) db.executeQuery(sql);
			while(rs.next()){
				hdcManage = new HdcMessage();
				String eventListIndexSerialNumber = rs.getString("eventListIndexSerialNumber");
				String eventListNickname = rs.getString("eventListNickname");
				String eventListIndexRecordNo = rs.getString("eventListIndexRecordNo");
				String eventListREFCODE = rs.getString("eventListREFCODE");
				String eventListDate = rs.getString("eventListDate");
				String eventListTime = rs.getString("eventListTime");
				String eventListDescription = rs.getString("eventListDescription");
				hdcManage.setEventListIndexSerialNumber(eventListIndexSerialNumber);
				hdcManage.setEventListNickname(eventListNickname);
				hdcManage.setEventListIndexRecordNo(eventListIndexRecordNo);
				hdcManage.setEventListREFCODE(eventListREFCODE);
				hdcManage.setEventListDate(eventListDate);
				hdcManage.setEventListTime(eventListTime);
				hdcManage.setEventListDescription(eventListDescription);
				list.add(hdcManage);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		request.setAttribute("storage", storage);
		request.setAttribute("storagelist", list);
		return "/application/storagehdc/lsarray_detail_event.jsp";
	
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	public String lsrankDetail(){
//		
//		String id = getParaValue("id");
//		
//		Storage storage = null;
//		
//		StorageHDCDao storageDao = new StorageHDCDao();
//		
//		try {
//			storage = (Storage)storageDao.findByID(id);
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		List storageTypeList = null;  // 存储类型
//		StorageTypeDao storageTypeDao = new StorageTypeDao();
//		try {
//			storageTypeList = storageTypeDao.loadAll();
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		request.setAttribute("storageTypeList", storageTypeList);
//		
//		
//		List producerList = null;
//		
//		ProducerDao producerDao = new ProducerDao();
//		try {
//			producerList = producerDao.loadAll();
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(producerList.size());
//		
//		request.setAttribute("producerList", producerList);
//		
//		List lsrankList = new ArrayList();
//		
//		Hashtable allTuxedoData = ShareData.getStoragedata();
//		
//		if(storage != null){
//			
//			drawPingPieChart(storage);
//			
//			Hashtable hashtable = (Hashtable)allTuxedoData.get(storage.getIpaddress());
//			
//			if(hashtable!=null){
//				System.out.println(hashtable.size()+"========hashtable=========");
//				lsrankList = (List)hashtable.get("lsrank");
//				storage.setStatus("1");
//				System.out.println(lsrankList.size()+"===========================");
//			}else{
//				storage.setStatus("0");
//			}
//			
//		}
//		
//		request.setAttribute("storage", storage);
//		
//		request.setAttribute("lsrankList", lsrankList);
//		
//		return "/application/storage/lsrank_detail.jsp";
//	}
	
	
	
	
	
	
	
	
	
//	public String lsextpoolDetail(){
//		
//		String id = getParaValue("id");
//		
//		Storage storage = null;
//		
//		StorageHDCDao storageDao = new StorageHDCDao();
//		
//		try {
//			storage = (Storage)storageDao.findByID(id);
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		List storageTypeList = null;  // 存储类型
//		StorageTypeDao storageTypeDao = new StorageTypeDao();
//		try {
//			storageTypeList = storageTypeDao.loadAll();
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		request.setAttribute("storageTypeList", storageTypeList);
//		
//		
//		List producerList = null;
//		
//		ProducerDao producerDao = new ProducerDao();
//		try {
//			producerList = producerDao.loadAll();
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(producerList.size());
//		
//		request.setAttribute("producerList", producerList);
//		
//		List lsextpoolList = new ArrayList();
//		
//		Hashtable allTuxedoData = ShareData.getStoragedata();
//		
//		if(storage != null){
//			
//			drawPingPieChart(storage);
//			
//			Hashtable hashtable = (Hashtable)allTuxedoData.get(storage.getIpaddress());
//			
//			if(hashtable!=null){
//				System.out.println(hashtable.size()+"========hashtable=========");
//				lsextpoolList = (List)hashtable.get("lsextpool");
//				storage.setStatus("1");
//				System.out.println(lsextpoolList.size()+"===========================");
//			}else{
//				storage.setStatus("0");
//			}
//			
//		}
//		
//		request.setAttribute("storage", storage);
//		
//		request.setAttribute("lsextpoolList", lsextpoolList);
//		
//		return "/application/storage/lsextpool_detail.jsp";
//	}
	
	
	public String lsfbvolDetail(){
		
		String id = getParaValue("id");
		
		Storage storage = null;
		
		StorageHDCDao storageDao = new StorageHDCDao();
		
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
		
		StorageHDCDao storageDao = new StorageHDCDao();
		
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
		
		StorageHDCDao storageDao = new StorageHDCDao();
		
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
		
		StorageHDCDao storageDao = new StorageHDCDao();
		
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
		String snmpversion = getParaValue("snmpversion");
		String mon_flag = getParaValue("mon_flag");
		String collecttype = getParaValue("collecttype");
		String community = getParaValue("community");
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
		storage.setSnmpversion(snmpversion);
		storage.setMon_flag(mon_flag);
		storage.setCollecttype(collecttype);
		storage.setCommunity(community);
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
	public Storage createStoragetopo(){
		Storage storage = new Storage();
		String ipaddress = getParaValue("ipaddress");
		String name = getParaValue("name");
		String mon_flag = getParaValue("mon_flag");
		String collecttype = getParaValue("collecttype");
		String community = getParaValue("community");
		String type = getParaValue("type");
		String serialNumber = getParaValue("serialNumber");
		String sendemail = getParaValue("sendemail");
		String bid = getParaValue("bid");
		storage.setIpaddress(ipaddress);
		storage.setName(name);
		storage.setMon_flag(mon_flag);
		storage.setCollecttype(collecttype);
		storage.setCommunity(community);
		storage.setType(type);
		storage.setSerialNumber(serialNumber);
		storage.setBid(bid);
		storage.setSendemail(sendemail);		
		return storage;
		
	}
	/**
	 * 
	 * 根据 oids 来判断子类型
	 * @param oids 设备oids
	 * @return 返回子类型
	 */
	public String getSutType(String oids)
	{
		String subtype="";
			if(oids.startsWith("1.3.6.1.4.1.311."))
			{
				subtype="windows";
			}else if(oids.startsWith("1.3.6.1.4.1.2021") || oids.startsWith("1.3.6.1.4.1.8072"))
			 {
				subtype="linux";
			 }else if(oids.startsWith("as400"))
			 {
				subtype="as400"; 
				 
			 }else if(oids.startsWith("1.3.6.1.4.1.42.2.1.1"))
			 {
					subtype="solaris";  
		     }else if(oids.startsWith("1.3.6.1.4.1.2.3.1.2.1.1"))
			 {
					subtype="aix";  
		     }else if(oids.startsWith("1.3.6.1.4.1.11.2.3.10.1"))
			 {
					subtype="hpunix";  
		     }else if(oids.startsWith("1.3.6.1.4.1.9."))
			 {
					subtype="cisco";  
		     }
		     else if(oids.startsWith("1.3.6.1.4.1.25506.") || oids.startsWith("1.3.6.1.4.1.2011."))
			 {
					subtype="h3c";  
		     }else if(oids.startsWith("1.3.6.1.4.1.4881."))
			 {
					subtype="redgiant";  
		     }else if(oids.startsWith("1.3.6.1.4.1.5651."))
			 {
					subtype="maipu";  
		     }
		     else if(oids.startsWith("1.3.6.1.4.1.171."))
			 {
					subtype="dlink";  
		     }
		     else if(oids.startsWith("1.3.6.1.4.1.2272."))
			 {
					subtype="northtel";  
		     }
		     else if(oids.startsWith("1.3.6.1.4.1.89."))
			 {
					subtype="radware";  
		     }
		     else if(oids.startsWith("1.3.6.1.4.1.3320."))
			 {
					subtype="bdcom";  
		     }
		     else if(oids.startsWith("1.3.6.1.4.1.1588.2.1."))
			 {
					subtype="brocade";  
		     }
			
	        return subtype;
		
	}
	
	public String environment(){
		Hashtable returnHash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		Hashtable ipAllData = new Hashtable();
		String ip="";
		String tmp ="";
		String name="";
		int manager=0;
		String _rfan = "";
	    String _rcpu = "";
	    String _rcable = "";
	    String _rcache = "";
	    String _memory = "";
	    String _rpower = "";
	    String _renv = "";
	    String _sys="";
	    String index="";
	    String _rbutter="";
	    String name1="";
		try {
			
	    	tmp = request.getParameter("id");
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	    	
			ip=host.getIpAddress();	
			name=host.getAlias();
		    manager=host.getStatus();
		    
		    
		    ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
//		    System.out.println("########################"+ipAllData.size()+ipAllData.containsKey("efan"));
		    if(ipAllData != null){
		    	vector = (Vector) ipAllData.get("rfan");
//		    	System.out.println("########################"+vector);
		    	if(vector != null && vector.size()>0){
		    		Interfacecollectdata rfan = (Interfacecollectdata)vector.get(0);
		    		_rfan = rfan.getThevalue();
				}
		    	Vector rcpuVector = (Vector) ipAllData.get("rcpu");
		    	if(rcpuVector != null && rcpuVector.size()>0){
		    		Interfacecollectdata rcpu = (Interfacecollectdata)rcpuVector.get(0);
		    		_rcpu = rcpu.getThevalue();
		    		index = rcpu.getEntity();
				}
		    	Vector rcableVector = (Vector) ipAllData.get("rcable");
		    	if(rcableVector != null && rcableVector.size()>0){
		    		Interfacecollectdata rcable = (Interfacecollectdata)rcableVector.get(0);
		    		_rcable = rcable.getThevalue();
				}
		    	Vector rcacheVector = (Vector) ipAllData.get("rcache");
		    	if(rcacheVector != null && rcacheVector.size()>0){
		    		Interfacecollectdata rcache = (Interfacecollectdata)rcacheVector.get(0);
		    		_rcache = rcache.getThevalue();
				}
		    	Vector meVector = (Vector) ipAllData.get("rmemory");
		    	if(meVector != null && meVector.size()>0){
		    		Interfacecollectdata memory = (Interfacecollectdata)meVector.get(0);
		    		_memory = memory.getThevalue();
				}
		    	Vector rpowerVector = (Vector) ipAllData.get("rpower");
		    	if(rpowerVector != null && rpowerVector.size()>0){
		    		Interfacecollectdata rpower = (Interfacecollectdata)rpowerVector.get(0);
		    		_rpower = rpower.getThevalue();
				}
		    	Vector rbutterVector = (Vector) ipAllData.get("rbutter");
		    	if(rbutterVector != null && rbutterVector.size()>0){
		    		Interfacecollectdata rbutter = (Interfacecollectdata)rbutterVector.get(0);
		    		_rbutter = rbutter.getThevalue();
				}
		    	Vector renvVector = (Vector) ipAllData.get("renv");
		    	if(renvVector != null && renvVector.size()>0){
		    		Interfacecollectdata renv = (Interfacecollectdata)renvVector.get(0);
		    		_renv = renv.getThevalue();
				}
		    	Vector sysVector = (Vector) ipAllData.get("sysinfo");
		   // 	SysLogger.info("################################sysVector:"+sysVector);
		    	if(sysVector != null && sysVector.size()>0){
		    		HdcMessage sys = (HdcMessage)sysVector.get(0);
		 //   		System.out.println("#######################"+sys);
		    		_sys = sys.getRaidlistDKCProductName();
		    		name1 = sys.getRaidlistMibNickName();
				}
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("name", name);
		request.setAttribute("manager", manager);
		request.setAttribute("_rfan", _rfan);
		request.setAttribute("_rcpu", _rcpu);
		request.setAttribute("_rcable", _rcable);
		request.setAttribute("_rcache", _rcache);
		request.setAttribute("_memory", _memory);
		request.setAttribute("_rpower", _rpower);
		request.setAttribute("_renv", _renv);
		request.setAttribute("_sys", _sys);
		request.setAttribute("index", index);
		request.setAttribute("_rbutter", _rbutter);
	    return "/application/storage/environment.jsp";
	}
	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
	
	public String running(){
		Hashtable returnHash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		Hashtable ipAllData = new Hashtable();
		String ip="";
		String tmp ="";
		String name="";
		int manager=0;
		String _rfan = "";
	    String _rcpu = "";
	    String _rcable = "";
	    String _rcache = "";
	    String _memory = "";
	    String _rpower = "";
	    String _renv = "";
	    String _sys="";
	    String index="";
	    
	    String _edrive="";
		try {
			
	    	tmp = request.getParameter("id");
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	    	
			ip=host.getIpAddress();	
			name=host.getAlias();
		    manager=host.getStatus();
		    
		    
		    ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
//		    System.out.println("########################"+ipAllData.size()+ipAllData.containsKey("efan"));
		    if(ipAllData != null){
		    	vector = (Vector) ipAllData.get("rfan");
//		    	System.out.println("########################"+vector);
		    	if(vector != null && vector.size()>0){
		    		Interfacecollectdata rfan = (Interfacecollectdata)vector.get(0);
		    		_rfan = rfan.getThevalue();
				}
		    	Vector rcpuVector = (Vector) ipAllData.get("rcpu");
		    	if(rcpuVector != null && rcpuVector.size()>0){
		    		Interfacecollectdata rcpu = (Interfacecollectdata)rcpuVector.get(0);
		    		_rcpu = rcpu.getThevalue();
		    		index = rcpu.getEntity();
				}
		    	Vector edriveVector = (Vector) ipAllData.get("edrive");
		    	if(edriveVector != null && edriveVector.size()>0){
		    		Interfacecollectdata edrive = (Interfacecollectdata)edriveVector.get(0);
		    		_edrive = edrive.getThevalue();
				}
		    	Vector rpowerVector = (Vector) ipAllData.get("rpower");
		    	if(rpowerVector != null && rpowerVector.size()>0){
		    		Interfacecollectdata rpower = (Interfacecollectdata)rpowerVector.get(0);
		    		_rpower = rpower.getThevalue();
				}
		    	Vector renvVector = (Vector) ipAllData.get("renv");
		    	if(renvVector != null && renvVector.size()>0){
		    		Interfacecollectdata renv = (Interfacecollectdata)renvVector.get(0);
		    		_renv = renv.getThevalue();
				}
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("name", name);
		request.setAttribute("manager", manager);
		request.setAttribute("_rfan", _rfan);
		request.setAttribute("_edrive", _edrive);
		request.setAttribute("_rpower", _rpower);
		request.setAttribute("_renv", _renv);
		request.setAttribute("index", index);
	    return "/application/storage/running.jsp";
	}
	
	
	public String system(){
		Hashtable returnHash=new Hashtable();
		Vector vector = new Vector();
		Hashtable bandhash = new Hashtable();
		Hashtable ipAllData = new Hashtable();
		String ip="";
		String tmp ="";
		String name="";
		int manager=0;
	    String NiName = "";
	    String proName = "";
	    String version="";
	    String index="";
	    
	    String _edrive="";
		try {
			
	    	tmp = request.getParameter("id");
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	    	
			ip=host.getIpAddress();	
			name=host.getAlias();
		    manager=host.getStatus();
		    
		    
		    ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
//		    System.out.println("########################"+ipAllData.size()+ipAllData.containsKey("efan"));
		    if(ipAllData != null){
		    	vector = (Vector) ipAllData.get("sysinfo");
//		    	System.out.println("########################"+vector);
		    	if(vector != null && vector.size()>0){
		    		HdcMessage sysinfo = (HdcMessage)vector.get(0);
		    		index = sysinfo.getRaidlistSerialNumber();
		    		NiName = sysinfo.getRaidlistMibNickName();
		    		version = sysinfo.getRaidlistDKCMainVersion();
		    		proName = sysinfo.getRaidlistDKCProductName();
				}
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("name", name);
		request.setAttribute("manager", manager);
		request.setAttribute("version", version);
		request.setAttribute("proName", proName);
		request.setAttribute("index", index);
		request.setAttribute("NiName", NiName);
	    return "/application/storage/systeminfo.jsp";
	}
	
	
	public String event(){
		Hashtable returnHash=new Hashtable();
		Vector vector = new Vector();
		List list = new ArrayList();
		Hashtable bandhash = new Hashtable();
		Hashtable ipAllData = new Hashtable();
		String ip="";
		String tmp ="";
		String name="";
		int manager=0;
	    String NiName = "";
	    String proName = "";
	    String version="";
	    String index="";
	    
	    String _edrive="";
		try {
			
	    	tmp = request.getParameter("id");
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	    	
			ip=host.getIpAddress();	
			name=host.getAlias();
		    manager=host.getStatus();
		    
		    
		    ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
//		    System.out.println("########################"+ipAllData.size()+ipAllData.containsKey("efan"));
		    if(ipAllData != null){
		    	vector = (Vector) ipAllData.get("eventlist");
//		    	System.out.println("########################"+vector);
		    	if(vector != null && vector.size()>0){
		    	  for(int i = 0 ;i<vector.size();i++){
//		    		  System.out.println("###########eventlist#############"+vector.size());
		    		HdcMessage sysinfo = (HdcMessage)vector.get(i);
		    		list.add(sysinfo);
		    	  }
				}
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("name", name);
		request.setAttribute("manager", manager);
		request.setAttribute("list", list);
	    return "/application/storage/event.jsp";
	}
	
	
	public String syslist(){
		Hashtable returnHash=new Hashtable();
		Vector vector = new Vector();
		List list = new ArrayList();
		Hashtable bandhash = new Hashtable();
		Hashtable ipAllData = new Hashtable();
		String ip="";
		String tmp ="";
		String name="";
		int manager=0;
	    String pro = "";
	    String version="";
	    String index="";
	    
	    String _edrive="";
		try {
			
	    	tmp = request.getParameter("id");
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	    	
			ip=host.getIpAddress();	
			name=host.getAlias();
		    manager=host.getStatus();
		    
		    
		    ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
//		    System.out.println("########################"+ipAllData.size()+ipAllData.containsKey("efan"));
		    if(ipAllData != null){
		    	vector = (Vector) ipAllData.get("syslist");
//		    	System.out.println("########################"+vector);
		    	if(vector != null && vector.size()>0){
		    		HdcDFMessage sysinfo = (HdcDFMessage)vector.get(0);
		    		pro = sysinfo.getDfSystemProductName();
		    		version = sysinfo.getDfSystemMicroRevision();
		    		index = sysinfo.getDfSystemSerialNumber();
				}
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("name", name);
		request.setAttribute("manager", manager);
		request.setAttribute("pro", pro);
		request.setAttribute("version", version);
		request.setAttribute("index", index);
	    return "/application/storage/syslist.jsp";
	}
	
	public String lun(){
		Hashtable returnHash=new Hashtable();
		Vector vector = new Vector();
		Vector vector1 = new Vector();
		List list = new ArrayList();
		Hashtable bandhash = new Hashtable();
		Hashtable ipAllData = new Hashtable();
		String ip="";
		String tmp ="";
		String name="";
		int manager=0;
	    String pointId = "";
	    String turn="";
	    String index="";
	    String state="";
	    
		try {
			
	    	tmp = request.getParameter("id");
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	    	
			ip=host.getIpAddress();	
			name=host.getAlias();
		    manager=host.getStatus();
		    
		    
		    ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
//		    System.out.println("########################"+ipAllData.size()+ipAllData.containsKey("efan"));
		    if(ipAllData != null){
		    	vector1 =(Vector) ipAllData.get("syslist");
//		    	System.out.println("########################"+vector);
		    	if(vector1 != null && vector1.size()>0){
		    		HdcDFMessage sysinfo1 = (HdcDFMessage)vector1.get(0);
		    		index = sysinfo1.getDfSwitchSerialNumber();
		    		pointId = sysinfo1.getDfSwitchPortID();
		    	}
		    	Vector rswitchVector = (Vector) ipAllData.get("rswitch");
		    	if(rswitchVector != null && rswitchVector.size()>0){
		    		Interfacecollectdata rswitch = (Interfacecollectdata)rswitchVector.get(0);
		    		turn = rswitch.getThevalue();
				}
		    	Vector rlunconVector = (Vector) ipAllData.get("rluncon");
		    	if(rlunconVector != null && rlunconVector.size()>0){
		    		Interfacecollectdata rluncon = (Interfacecollectdata)rlunconVector.get(0);
		    		state = rluncon.getThevalue();
				}
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("name", name);
		request.setAttribute("manager", manager);
		request.setAttribute("pointId", pointId);
		request.setAttribute("turn", turn);
		request.setAttribute("index", index);
		request.setAttribute("state", state);
	    return "/application/storage/lun.jsp";
	}
	
	
	public String wwn(){
		Hashtable returnHash=new Hashtable();
		Vector vector = new Vector();
		Vector vector1 = new Vector();
		List list = new ArrayList();
		Hashtable bandhash = new Hashtable();
		Hashtable ipAllData = new Hashtable();
		String ip="";
		String tmp ="";
		String name="";
		int manager=0;
	    String pointId = "";
	    String conIndex="";
	    String index="";
	    String state="";
	    String conState="";
	    String wwnname="";
	    String wwnid="";
		try {
			
	    	tmp = request.getParameter("id");
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	    	
			ip=host.getIpAddress();	
			name=host.getAlias();
		    manager=host.getStatus();
		    
		    
		    ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
//		    System.out.println("########################"+ipAllData.size()+ipAllData.containsKey("efan"));
		    if(ipAllData != null){
//		    	vector = (Vector) ipAllData.get("wwn");
		    	vector1 =(Vector) ipAllData.get("syslist");
//		    	System.out.println("########################"+vector);
//		    	if(vector != null && vector.size()>0){
//		    		HdcDFMessage sysinfo = (HdcDFMessage)vector.get(0);
//		    		index = sysinfo.getDfWWNSerialNumber();
//		    		pointId = sysinfo.getDfWWNPortID();
//		    		conState = sysinfo.getDfWWNUseNickname();
//		    		state = sysinfo.getDfWWNControlStatus();
//		    		wwnname = sysinfo.getDfWWNNickname();
//		    		wwnid = sysinfo.getDfWWNID();
//		    		conIndex = sysinfo.getDfWWNControlIndex();
//				}
		    	if(vector1 != null && vector1.size()>0){
		    		HdcDFMessage sysinfo1 = (HdcDFMessage)vector1.get(0);
		    		index = sysinfo1.getDfWWNSerialNumber();
		    		pointId = sysinfo1.getDfWWNPortID();
		    		wwnname = sysinfo1.getDfWWNNickname();
		    		wwnid = sysinfo1.getDfWWNID();
		    		conIndex = sysinfo1.getDfWWNControlIndex();
		    	}
		    	Vector rwwnconVector = (Vector) ipAllData.get("rwwncon");
		    	if(rwwnconVector != null && rwwnconVector.size()>0){
		    		Interfacecollectdata rwwncon = (Interfacecollectdata)rwwnconVector.get(0);
		    		state = rwwncon.getThevalue();
				}
		    	Vector rnumberVector = (Vector) ipAllData.get("rnumber");
		    	if(rnumberVector != null && rnumberVector.size()>0){
		    		Interfacecollectdata rnumber = (Interfacecollectdata)rnumberVector.get(0);
		    		conState = rnumber.getThevalue();
				}
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("name", name);
		request.setAttribute("manager", manager);
		request.setAttribute("pointId", pointId);
		request.setAttribute("conState", conState);
		request.setAttribute("wwnname", wwnname);
		request.setAttribute("wwnid", wwnid);
		request.setAttribute("conIndex", conIndex);
		request.setAttribute("index", index);
		request.setAttribute("state", state);
	    return "/application/storage/wwn.jsp";
	}
	
	
	public String slun(){
		Hashtable returnHash=new Hashtable();
		Vector vector = new Vector();
		Vector vector1 = new Vector();
		List list = new ArrayList();
		Hashtable bandhash = new Hashtable();
		Hashtable ipAllData = new Hashtable();
		String ip="";
		String tmp ="";
		String name="";
		int manager=0;
	    String pointId = "";
	    String lun="";
	    String index="";
	    String state="";
	    String wwn = "";
		try {
			
	    	tmp = request.getParameter("id");
	    	
	    	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
	    	
			ip=host.getIpAddress();	
			name=host.getAlias();
		    manager=host.getStatus();
		    
		    
		    ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
//		    System.out.println("########################"+ipAllData.size()+ipAllData.containsKey("efan"));
		    if(ipAllData != null){
//		    	vector = (Vector) ipAllData.get("slun");
		    	vector1 =(Vector) ipAllData.get("syslist");
//		    	System.out.println("########################"+vector);
//		    	if(vector != null && vector.size()>0){
//		    		HdcDFMessage sysinfo = (HdcDFMessage)vector.get(0);
//		    		index = sysinfo.getDfLUNSerialNumber();
//		    		pointId = sysinfo.getDfLUNPortID();
//		    		lun = sysinfo.getDfLUNLUN();
//		    		state = sysinfo.getDfLUNControlStatus();
//		    		wwn = sysinfo.getDfLUNWWNSecurity();
//				}
		    	if(vector1 != null && vector1.size()>0){
		    		HdcDFMessage sysinfo1 = (HdcDFMessage)vector1.get(0);
		    		index = sysinfo1.getDfLUNSerialNumber();
		    		pointId = sysinfo1.getDfLUNPortID();
		    		lun = sysinfo1.getDfLUNLUN();
		    	}
		    	Vector rslunconVector = (Vector) ipAllData.get("rsluncon");
		    	if(rslunconVector != null && rslunconVector.size()>0){
		    		Interfacecollectdata rsluncon = (Interfacecollectdata)rslunconVector.get(0);
		    		state = rsluncon.getThevalue();
				}
		    	Vector rsafetyVector = (Vector) ipAllData.get("rsafety");
		    	if(rsafetyVector != null && rsafetyVector.size()>0){
		    		Interfacecollectdata rsafety = (Interfacecollectdata)rsafetyVector.get(0);
		    		wwn = rsafety.getThevalue();
				}
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//request.setAttribute("vector",vector);
		request.setAttribute("ipaddress",ip);
		request.setAttribute("id", tmp);
		request.setAttribute("name", name);
		request.setAttribute("manager", manager);
		request.setAttribute("pointId", pointId);
		request.setAttribute("lun", lun);
		request.setAttribute("wwn", wwn);
		request.setAttribute("index", index);
		request.setAttribute("state", state);
	    return "/application/storage/slun.jsp";
	}
}