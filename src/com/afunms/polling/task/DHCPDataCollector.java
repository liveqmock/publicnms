
package com.afunms.polling.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DHCPConfigDao;
import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.DHCPConfig;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.application.weblogicmonitor.WeblogicNormal;
import com.afunms.application.weblogicmonitor.WeblogicServer;
import com.afunms.application.weblogicmonitor.WeblogicSnmp;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.Smscontent;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.snmp.dhcp.DhcpScopeSnmp;



/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DHCPDataCollector{
	/**
	 * 
	 */
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public DHCPDataCollector() {
	}

//	public void collect_data(String id,Hashtable gatherHash) {
	
    public void collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		
    	System.out.println("=======================dhcp cai ji===============================");
    	
		String queryid = alarmIndicatorsNode.getNodeid();
		DHCPConfig dhcpconf = new DHCPConfig();
		DHCPConfigDao dao = new DHCPConfigDao();
		try{
			dhcpconf = (DHCPConfig)dao.findByID(queryid+"");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		
		if(ShareData.getDhcplist() != null){
			List dhcpList = ShareData.getDhcplist();
			DHCPConfig vo = null;
			if(dhcpList != null && dhcpList.size()>0){
				for(int i=0;i<dhcpList.size();i++){
					vo = (DHCPConfig)dhcpList.get(i);
					if(vo.getMon_flag()== 0)continue;
					if(vo.getId() == Integer.parseInt(queryid))dhcpconf = vo;
					break;
				}
			}else
				{
				  return ;
				}
		}else{
			return ;
		}
		
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dhcpconf);
	
//		DHCPConfig dhcpconf1 = null;
        try {  
//        	DHCPConfigDao configdao = new DHCPConfigDao();
//        	try{
//        		dhcpconf1 = (DHCPConfig)configdao.findByID(queryid);
//        	}catch(Exception e){
//        		
//        	}finally{
//        		configdao.close();
//        	}
        	if (dhcpconf == null)return;
            int serverflag = 0;
            DhcpScopeSnmp dhcpsnmp=null;
         	Hashtable hash = null;
         	dhcpsnmp = new DhcpScopeSnmp(dhcpconf.getIpAddress(),dhcpconf.getCommunity(),161);
         	
         	Hashtable gatherHash = new Hashtable();
         	gatherHash.put("dhcpscope", "");
         	gatherHash.put("ping", "");
         	
         	
         	hash=dhcpsnmp.collectData(gatherHash,dhcpconf);
     		if(hash == null) {
     			hash = new Hashtable();
     		} 
     		com.afunms.polling.node.DHCP _tnode=(com.afunms.polling.node.DHCP)PollingEngine.getInstance().getDHCPByID(dhcpconf.getId());
				Calendar _date=Calendar.getInstance();
				Date _cc = _date.getTime();
	 			String _tempsenddate = sdf.format(_cc);
	 			//初始化DHCP对象的状态
				_tnode.setLastTime(_tempsenddate);
				_tnode.setAlarm(false);
				_tnode.getAlarmMessage().clear();
				_tnode.setStatus(0);
//     		if(hash.get("dhcpping") != null){
//			    try{
//					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
//					List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(dhcpconf.getId()), AlarmConstant.TYPE_SERVICE, "dhcp");
//					for(int k = 0 ; k < list.size() ; k ++){
//						AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(k);
//						//对邮件服务值进行告警检测
//						CheckEventUtil checkutil = new CheckEventUtil();
//						checkutil.updateData(_tnode,hash,"service","windhcp",alarmIndicatorsnode);
//						//}
//					}
//			    }catch(Exception e){
//			    	e.printStackTrace();
//			    }
//     		}
     		//根据配置判断是否告警
     		
     		if(hash.get("serverValue") != null){}
				try{
					ShareData.setDhcpdata(dhcpconf.getIpAddress(), hash);
				}catch(Exception ex){
					ex.printStackTrace();
				}            		
     		dhcpsnmp=null;
     		hash=null;            	 
         }catch(Exception exc){
         	exc.printStackTrace();
         }
	}

//	public void createSMS(String weblogic,WeblogicConfig weblogicconf){
// 	//建立短信		 	
// 	//从内存里获得当前这个IP的PING的值
//		Hashtable sendeddata = ShareData.getSendeddata();
//		Calendar date=Calendar.getInstance();
//		try{
//			if (!sendeddata.containsKey(weblogic+":"+weblogicconf.getId())){
//				//若不在，则建立短信，并且添加到发送列表里
// 			Smscontent smscontent = new Smscontent();
// 			String time = sdf.format(date.getTime());
// 			smscontent.setLevel("2");
// 			smscontent.setObjid(weblogicconf.getId()+"");
// 			if("weblogicDomain".equals(weblogic)){
// 				smscontent.setMessage(weblogicconf.getAlias()+" ("+weblogicconf.getIpAddress()+")"+"的Weblogic Server服务停止");
// 			}
// 			if("weblogicServer".equals(weblogic)){
// 				smscontent.setMessage(weblogicconf.getAlias()+" ("+weblogicconf.getIpAddress()+")"+"的活动域状态为 不活动");	
// 			}
// 			smscontent.setRecordtime(time);
// 			smscontent.setSubtype("weblogic");
// 			smscontent.setSubentity("ping");
// 			smscontent.setIp(weblogicconf.getIpAddress());
// 			
// 			//smscontent.setMessage("db&"+time+"&"+dbmonitorlist.getId()+"&"+db+"("+dbmonitorlist.getDbName()+" IP:"+dbmonitorlist.getIpAddress()+")"+"的数据库服务停止");
// 			//发送短信
// 			SmscontentDao smsmanager=new SmscontentDao();
// 			smsmanager.sendURLSmscontent(smscontent);	
//			sendeddata.put(weblogic+":"+weblogicconf.getId(),date);		 					 				
//			}else{
//				//若在，则从已发送短信列表里判断是否已经发送当天的短信
//				Calendar formerdate =(Calendar)sendeddata.get(weblogic+":"+weblogicconf.getId());		 				
// 			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
// 			Date last = null;
// 			Date current = null;
// 			Calendar sendcalen = formerdate;
// 			Date cc = sendcalen.getTime();
// 			String tempsenddate = formatter.format(cc);
// 			
// 			Calendar currentcalen = date;
// 			cc = currentcalen.getTime();
// 			last = formatter.parse(tempsenddate);
// 			String currentsenddate = formatter.format(cc);
// 			current = formatter.parse(currentsenddate);
// 			
// 			long subvalue = current.getTime()-last.getTime();			 			
// 			if (subvalue/(1000*60*60*24)>=1){
// 				//超过一天，则再发信息
//	 			Smscontent smscontent = new Smscontent();
//	 			String time = sdf.format(date.getTime());
//	 			smscontent.setLevel("2");
//	 			smscontent.setObjid(weblogicconf.getId()+"");
//	 			if("weblogicDomain".equals(weblogic)){
//	 				smscontent.setMessage(weblogicconf.getAlias()+" ("+weblogicconf.getIpAddress()+")"+"的活动域状态为 不活动");
//	 			}
//	 			if("weblogicServer".equals(weblogic)){
//	 				smscontent.setMessage(weblogicconf.getAlias()+" ("+weblogicconf.getIpAddress()+")"+"的Weblogic Server服务停止");	
//	 			}
//	 			smscontent.setRecordtime(time);
//	 			smscontent.setSubtype(weblogic);
//	 			smscontent.setSubentity("ping");
//	 			smscontent.setIp(weblogicconf.getIpAddress());
//	 			//smscontent.setMessage("db&"+time+"&"+dbmonitorlist.getId()+"&"+db+"("+dbmonitorlist.getDbName()+" IP:"+dbmonitorlist.getIpAddress()+")"+"的数据库服务停止");
//	 			//发送短信
//	 			SmscontentDao smsmanager=new SmscontentDao();
//	 			smsmanager.sendURLSmscontent(smscontent);
//				//修改已经发送的短信记录	
//	 			sendeddata.put(weblogic+":"+weblogicconf.getId(),date);	
//	 		}	
//			}	 			 			 			 			 	
// 	}catch(Exception e){
// 		e.printStackTrace();
// 	}
// }

}

