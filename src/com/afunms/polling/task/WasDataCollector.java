package com.afunms.polling.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import com.afunms.application.dao.WasConfigDao;
import com.afunms.application.model.WasConfig;
import com.afunms.application.wasmonitor.UrlConncetWas;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.om.Pingcollectdata;

public class WasDataCollector {
	private Hashtable wasdata = ShareData.getWasdata();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	public WasDataCollector() {
	}
	
	public void collect_data(String id,Hashtable gatherHash) {
		
		
		
		WasConfig wasconf = null;
		try {                	
            int serverflag = 0;
            String ipaddress = "";
            WasConfigDao dao=new WasConfigDao();
    		try{
    			wasconf=(WasConfig)dao.findByID(id);
    		}catch(Exception e){
    			e.printStackTrace();
    		}finally{
    			dao.close();
    		}
    		SysLogger.info("#######################WebSphere 开始采集###################################");
    		SysLogger.info("###############WebSphere，名称:"+wasconf.getName()+" ipaddress:"+wasconf.getIpaddress()+"#####################");
            //AdminClient5 wasadmin = new AdminClient5();
            UrlConncetWas  conWas = new UrlConncetWas();
            Hashtable hst = new Hashtable();
     		com.afunms.polling.node.Was _tnode=(com.afunms.polling.node.Was)PollingEngine.getInstance().getWasByID(wasconf.getId());
			
     		Calendar _date=Calendar.getInstance();
				Date _cc = _date.getTime();
	 			String _tempsenddate = sdf.format(_cc);
	 			//初始化Was对象的状态
				_tnode.setLastTime(_tempsenddate);
				_tnode.setAlarm(false);
				_tnode.getAlarmMessage().clear();
				_tnode.setStatus(0);
				
				
				
				if(gatherHash.containsKey("ping")){
					//对可用性进行检测
					boolean collectWasIsOK = false;
					try{
						collectWasIsOK = conWas.connectWasIsOK(wasconf.getIpaddress(),wasconf.getPortnum());
					}catch(Exception e){
						//e.printStackTrace();
					}
					if(collectWasIsOK){
	         					//运行状态
	         					Pingcollectdata hostdata=null;
	     						hostdata=new Pingcollectdata();
	     						hostdata.setIpaddress(wasconf.getIpaddress());
	     						Calendar date=Calendar.getInstance();
	     						hostdata.setCollecttime(date);
	     						hostdata.setCategory("WasPing");
	     						hostdata.setEntity("Utilization");
	     						hostdata.setSubentity("ConnectUtilization");
	     						hostdata.setRestype("dynamic");
	     						hostdata.setUnit("%");
	     						hostdata.setThevalue("100");
	     						WasConfigDao wasconfigdao=new WasConfigDao();
	     						try{
	     							wasconfigdao.createHostData(wasconf,hostdata);
	     							if(wasdata.containsKey("was"+":"+wasconf.getIpaddress()))
	     								wasdata.remove("was"+":"+wasconf.getIpaddress());
	     						}catch(Exception e){
	     							e.printStackTrace();
	     						}finally{
	     							wasconfigdao.close();
	     						}
	     						//进行数据采集
	     			     		try{
	     			     			hst = conWas.ConncetWas(wasconf.getIpaddress(), String.valueOf(wasconf.getPortnum()), "", "", wasconf.getVersion(),gatherHash);
	     			     		}catch(Exception e){
	     			     			
	     			     		}
	         		}
				}
				if(hst != null){
					ShareData.getWasdata().put(wasconf.getIpaddress(), hst);
				}
				hst = null;
         }catch(Exception exc){
         	exc.printStackTrace();
         }
	}

//private  Runnable createTask(final WasConfig wasconf) {
//    return new Runnable() {
//        public void run() {
//            
//        }
//    };
//  }
//public void createSMS(String was,WasConfig wasconf){
//	
// 	//建立短信		 	
// 	//从内存里获得当前这个IP的PING的值
//		Calendar date=Calendar.getInstance();
//		try{
//			if (!sendeddata.containsKey(was+":"+wasconf.getId())){
//				//若不在，则建立短信，并且添加到发送列表里
// 			Smscontent smscontent = new Smscontent();
// 			String time = sdf.format(date.getTime());
// 			smscontent.setLevel("2");
// 			smscontent.setObjid(wasconf.getId()+"");
// 			if("wasserver".equals(was)){
// 				smscontent.setMessage("WAS"+" ("+wasconf.getName()+":"+wasconf.getIpaddress()+")"+"的服务停止！");
// 			}
// 			smscontent.setRecordtime(time);
// 			smscontent.setSubtype("wasserver");
// 			smscontent.setSubentity("ping");
// 			smscontent.setIp(wasconf.getIpaddress());
// 			//smscontent.setMessage("db&"+time+"&"+dbmonitorlist.getId()+"&"+db+"("+dbmonitorlist.getDbName()+" IP:"+dbmonitorlist.getIpAddress()+")"+"的数据库服务停止");
// 			//发送短信
// 			SmscontentDao smsmanager=new SmscontentDao();
// 			smsmanager.sendURLSmscontent(smscontent);	
// 			sendeddata.put(was+":"+wasconf.getId(),date);		 					 				
//			}else{
//				//若在，则从已发送短信列表里判断是否已经发送当天的短信
//				Calendar formerdate =(Calendar)sendeddata.get(was+":"+wasconf.getId());		 				
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
//	 			smscontent.setObjid(wasconf.getId()+"");
//	 			if("wasserver".equals(was)){
//	 				smscontent.setMessage("WAS"+" ("+wasconf.getName()+":"+wasconf.getIpaddress()+")"+"的服务停止！");
//	 			}
//	 			smscontent.setRecordtime(time);
//	 			smscontent.setSubtype("wasserver");
//	 			smscontent.setSubentity("ping");
//	 			smscontent.setIp(wasconf.getIpaddress());
//	 			//smscontent.setMessage("db&"+time+"&"+dbmonitorlist.getId()+"&"+db+"("+dbmonitorlist.getDbName()+" IP:"+dbmonitorlist.getIpAddress()+")"+"的数据库服务停止");
//	 			//发送短信
//	 			SmscontentDao smsmanager=new SmscontentDao();
//	 			smsmanager.sendURLSmscontent(smscontent);
//				//修改已经发送的短信记录	
//	 			sendeddata.put(was+":"+wasconf.getId(),date);	
//	 		}	
//			}	 			 			 			 			 	
// 	}catch(Exception e){
// 		e.printStackTrace();
// 	}
// }


}






