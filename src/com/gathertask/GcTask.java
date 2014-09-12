package com.gathertask;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.TimerTask;

import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.Portconfig;
import com.afunms.polling.loader.HostLoader;

public class GcTask extends TimerTask{

	@Override
	public void run() {
		// TODO Auto-generated method stub
//		System.out.println("===垃圾回收===");
		System.gc();
		
		
		
		//-----维护端口配置----
		List portconfiglist = new ArrayList();
		PortconfigDao configdao = new PortconfigDao(); 			
		Portconfig portconfig = null;
		Hashtable portconfigHash = new Hashtable();
		try {
			portconfiglist = configdao.getAllBySms();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		if(portconfiglist != null && portconfiglist.size()>0){
			for(int i=0;i<portconfiglist.size();i++){
				portconfig = (Portconfig)portconfiglist.get(i);
				if(portconfigHash.containsKey(portconfig.getIpaddress())){
					List portlist = (List)portconfigHash.get(portconfig.getIpaddress());
					portlist.add(portconfig);
					portconfigHash.put(portconfig.getIpaddress(), portlist);
				}else{
					List portlist = new ArrayList();
					portlist.add(portconfig);
					portconfigHash.put(portconfig.getIpaddress(), portlist);
				}
			}
		} 
		ShareData.setPortConfigHash(portconfigHash);
	    //----------------------
		
		

		//加载业务hash
//		DBManager dbm =new DBManager();
//		Hashtable businessHash = new Hashtable();
//		try {
//			businessHash = dbm.executeQuerykeyoneListHashMap("select * from system_business", "id");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		finally{
//			dbm.close();
//		}
//		ShareData.setBusinessHash(businessHash);
		
		
		//--------------------------------------------------
//		System.out.println("=============开始定时加载节点=================");
		
		//下面代码会重新加载节点和链路的数据，导致原始数据的告警和状态信息丢掉
//		HostLoader hostloader=new HostLoader();
//		
//		hostloader.loading();
		
	}
	
	

}
