package com.afunms.alarm.send;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.common.util.SysLogger;
import com.afunms.event.model.EventList;
import com.afunms.system.dao.AlertInfoServerDao;
import com.afunms.system.model.AlertInfoServer;

public class SendSoundAlarm implements SendAlarm{

	public void sendAlarm(EventList eventList,AlarmWayDetail alarmWayDetail) {
		// TODO Auto-generated method stub
		SysLogger.info("==============发送声音告警==================");
//		//向客户端写告警信息
//		String info = "1&&"+op.getUserid()+"&&"+eventList.getContent()+"\n";
//		Socket socket = null;
//		List alertserverlist = new ArrayList();
//		AlertInfoServerDao alertserverdao = new AlertInfoServerDao();
//		try{
//			alertserverlist = alertserverdao.getByFlage(1);
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			alertserverdao.close();
//		}
//		if(alertserverlist != null && alertserverlist.size()>0){
//			//设置了信息服务器
//			AlertInfoServer vo = (AlertInfoServer)alertserverlist.get(0);
//			try
//			{
//				socket = new Socket(vo.getIpaddress() , Integer.parseInt(vo.getPort()));
//				java.io.OutputStream out = socket.getOutputStream();
//				   
//				   byte[] data = info.getBytes();
//				   out.write(data);
//				   out.flush();
//				   SysLogger.info("you have send your message : "+info);
//			} catch (UnknownHostException e)
//			{
//				e.printStackTrace();
//			} catch (IOException e)
//			{
//				e.printStackTrace();
//			}
//			finally
//			{
//				try
//				{
//					if(socket != null)socket.close();
//				} catch (IOException e)
//				{
//					e.printStackTrace();
//				}
//			}
//		}
	}

}
