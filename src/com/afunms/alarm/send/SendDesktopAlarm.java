package com.afunms.alarm.send;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.common.util.SysLogger;
import com.afunms.event.model.EventList;
import com.afunms.system.dao.AlertInfoServerDao;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.AlertInfoServer;
import com.afunms.system.model.User;

public class SendDesktopAlarm implements SendAlarm{
	
	public void sendAlarm(EventList eventList,AlarmWayDetail alarmWayDetail){
		SysLogger.info("==============发送桌面告警==================");
		
		List alertserverlist = new ArrayList();
		AlertInfoServerDao alertserverdao = new AlertInfoServerDao();
		try{
			alertserverlist = alertserverdao.getByFlage(1);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			alertserverdao.close();
		}
		
		String userids = alarmWayDetail.getUserIds();
		UserDao userDao = new UserDao();
		List userList = new ArrayList();
		try{
			if(userids != null && !userids.equals("")){
				userList = userDao.findbyIDs(userids);
			}
		}catch(Exception e){
			
		}finally{
			userDao.close();
		}
		if(alertserverlist != null && alertserverlist.size()>0){
			//设置了信息服务器
			AlertInfoServer vo = (AlertInfoServer)alertserverlist.get(0);
			if(userList != null && userList.size()>0){
				Socket socket = null;
				java.text.SimpleDateFormat _sdf1 = new java.text.SimpleDateFormat("MM-dd HH:mm");
				try{
					//System.out.println(vo.getIpaddress()+"------"+ Integer.parseInt(vo.getPort()));
					socket = new Socket(vo.getIpaddress() , Integer.parseInt(vo.getPort()));
					java.io.OutputStream out = socket.getOutputStream();
					for(int i =0;i<userList.size();i++)
					{
						User op = (User)userList.get(i);
						try{
							Date cc = eventList.getRecordtime().getTime();
				  			String recordtime = _sdf1.format(cc);
							String info = "1&&"+op.getUserid()+"&&"+recordtime+" "+eventList.getContent()+"\n";
								try
								{
									   byte[] data = info.getBytes();
									   out.write(data);
									   out.flush();
									   SysLogger.info("you have send your message : "+info);
								} catch (UnknownHostException e)
								{
									e.printStackTrace();
								} catch (IOException e)
								{
									e.printStackTrace();
								}
								finally
								{
								}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					try
					{
						if(socket != null)socket.close();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		
		

	}
	
}
