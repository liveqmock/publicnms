
package com.afunms.polling.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.Hashtable;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.application.weblogicmonitor.WeblogicNormal;
import com.afunms.application.weblogicmonitor.WeblogicServer;
import com.afunms.application.weblogicmonitor.WeblogicSnmp;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Pingcollectdata;



/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WeblogicDataCollector{
	/**
	 * 
	 */
//	private Hashtable sendeddata = ShareData.getSendeddata();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public WeblogicDataCollector() {
	}

	public void collect_data(String id,Hashtable gatherHash) {
		
		com.afunms.polling.node.Weblogic _tnode=(com.afunms.polling.node.Weblogic)PollingEngine.getInstance().getWeblogicByID(Integer.parseInt(id));
		
		WeblogicConfig weblogicconf = null;
        try {  
    		int weblogicID = Integer.parseInt(id);
    		List weblogiclist = ShareData.getWeblogiclist();
    		if(weblogiclist != null && weblogiclist.size()>0){
    			WeblogicConfig vo = null;
    			for(int i=0;i<weblogiclist.size();i++){
    				vo = (WeblogicConfig) weblogiclist.get(i);
    				if(vo != null){
    					if(vo.getId() == weblogicID){
    						weblogicconf = vo;
    						break;
    					}
    				}
    			}
    		}
        	if (weblogicconf == null)return;
         	WeblogicSnmp weblogicsnmp=null;
         	Hashtable hash = null;
//         	System.out.println("weblogic=========="+weblogicconf.getIpAddress()+"=="+weblogicconf.getCommunity()+"==="+weblogicconf.getPortnum());
         	try {
				weblogicsnmp = new WeblogicSnmp(_tnode.getIpAddress(),_tnode.getCommunity(),_tnode.getPortnum());
				hash=weblogicsnmp.collectData(gatherHash);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         	//System.out.println("weblogic======================"+hash.size());
     		if(hash == null) {
     			hash = new Hashtable();
     		} 
			Calendar _date=Calendar.getInstance();
			Date _cc = _date.getTime();
			String _tempsenddate = sdf.format(_cc);
			//初始化Weblogic对象的状态
			_tnode.setLastTime(_tempsenddate);
			_tnode.setAlarm(false);
			_tnode.getAlarmMessage().clear();
			_tnode.setStatus(0);
//     		int pingValue = 0;
//			int flag = 0;
//     		if(hash.get("normalValue") != null){
//         		List normalValue = (List)hash.get("normalValue");
//         		if(normalValue != null && normalValue.size()>0){
//         			for(int i=0;i<normalValue.size();i++){
//         				WeblogicNormal normal = (WeblogicNormal)normalValue.get(i);
			//根据配置判断是否告警
//     		if(hash.get("serverValue") != null){
//         		List serverValue = (List)hash.get("serverValue");
//         		if(serverValue != null && serverValue.size()>0){
//         			for(int i=0;i<serverValue.size();i++){
//         				WeblogicServer server = (WeblogicServer)serverValue.get(i);
////         				normal.setDomainActive("2");
////         				System.out.println("weblogic=============normal.getDomainActive()========="+normal.getDomainActive());
//         				//if(!normal.getDomainActive().equals("2")){
//         				if(!server.getServerRuntimeState().equalsIgnoreCase("RUNNING")){
//         					flag = 1;
//         				}
//         			}
//         		}else {}
//     		}
     		//根据配置判断是否告警
//     		if(hash.get("serverValue") != null){
//         		List serverValue = (List)hash.get("serverValue");
//         		if(serverValue != null && serverValue.size()>0){
//         			for(int i=0;i<serverValue.size();i++){
//         				WeblogicServer server = (WeblogicServer)serverValue.get(i);
//         				if(server.getServerRuntimeState().equals("RUNNING")){
//         					pingValue = 100;
//         					//运行状态:正常
//         				}else{
//         					pingValue = 0;
//         				}
//         			}
//         		}else {
//         			pingValue = 0;
//         		}
//     		}
			try{
				ShareData.setWeblogicdata(_tnode.getIpAddress(), hash);
			}catch(Exception ex){
				ex.printStackTrace();
			}            		
			weblogicsnmp=null;
     		hash=null;            	 
         }catch(Exception exc){
         	exc.printStackTrace();
         }
	}

}

