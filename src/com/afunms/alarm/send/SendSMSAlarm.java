package com.afunms.alarm.send;

import montnets.SmsDao;
import montnets.SmsServer;

import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.common.util.SysLogger;
import com.afunms.event.model.EventList;
import com.afunms.event.model.SendSmsConfig;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;

public class SendSMSAlarm implements SendAlarm{
	
	public  void   sendAlarm(EventList eventList,AlarmWayDetail alarmWayDetail){
		SysLogger.info("==============发送短信告警==================");
		//向客户端写告警信息
//		String userids = alarmWayDetail.getUserIds();
		String[] ids = alarmWayDetail.getUserIds().split(",");
	//	synchronized(ids){
		if (ids != null && ids.length > 0) {
			for (int j = 0; j < ids.length; j++) {
				
				String oid = ids[j];
				
				User op = null;
				UserDao userdao = new UserDao();    
				try {
					op = (User) userdao.findByID(oid);
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					userdao.close();
				}
				if (op == null) {
					continue;
				}
				
				//开始发送短信接口
//				 公司短信猫开始发送短信
//				SmsServer ss = new SmsServer();
//				int rc = -1;
//				try {
//					rc = ss.sendSMS(op.getMobile(), eventList.getContent());
//				} catch (Exception e) {
//                    e.printStackTrace();
//				}
//				if (rc >= 0) {
//					SendSmsConfig ssc = new SendSmsConfig();
//					ssc.setName(op.getName());
//					ssc.setMobilenum(op.getMobile());
//					ssc.setEventlist(eventList.getContent());
//					SmsDao dao = new SmsDao();
//					try {
//						dao.save(ssc);
//					} catch (Exception e) {
//						e.printStackTrace();
//					} finally {
//						dao.close();
//					}
//				}
				
//				// 公司短信猫结束发送短信

				// outboxdao.insert(op.getMobile(), message);
				// 把短信的信息存到文件里面
				// smsIntoFile.intoFile(op.getMobile(),
				// message);
			}
		}	
	//	}
	}
	
}
