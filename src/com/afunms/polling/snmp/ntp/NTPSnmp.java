package com.afunms.polling.snmp.ntp;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Ntp;
import com.afunms.system.util.TimeGratherConfigUtil;

public class NTPSnmp extends SnmpMonitor {java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

public NTPSnmp() {
}

public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {

    Hashtable returnHash = new Hashtable();
    Vector ndpVector = new Vector();
    SysLogger.info("######## NTP 开始采集NTP信息##########");
    Ntp node = (Ntp) PollingEngine.getInstance().getNtpByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
	if (node == null){
		return returnHash;
	}
		
	// 判断是否在采集时间段内
	if (ShareData.getTimegatherhash() != null) {
		if (ShareData.getTimegatherhash().containsKey(node.getId() + ":equipment")) {
			TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
			int _result = 0;
			_result = timeconfig.isBetween((List) ShareData.getTimegatherhash().get(node.getId() + ":equipment"));
			if (_result == 1) {
			} else if (_result == 2) {
			} else {
				// 清除之前内存中产生的告警信息
				try {
					NodeDTO nodedto = null;
					NodeUtil nodeUtil = new NodeUtil();
					nodedto = nodeUtil.creatNodeDTOByNode(node);
					CheckEventUtil checkutil = new CheckEventUtil();
					checkutil.deleteEvent(node.getId() + "", nodedto.getType(), nodedto.getSubtype(), "storage", null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return returnHash;
			}

		}
	}
   // SysLogger.info("######## NTP" + node.getIpAddress() + " 开始采集NTPNTPNTPNTPNTPNTP信息##########");
   		// Send request
    
    String date = "";
    String collecttime = "";
    String datestatus = "1";
    try{
    // 4.指定尝试的次数  
	    int tries = 0;  
	    int MAXTRIES =3;
	    boolean receivedResponse = false;    
      //	System.out.println("没执行么--" + node.getIpAddress());
      	DatagramPacket packet=null;
      	DatagramSocket socket =null;
   		do{
   			try{
   				// 构造UDP DatagramSocket对象  
   			    socket = new DatagramSocket();	
   			    //指定timeout时间，防止进入无限等待状态
   			    socket.setSoTimeout(3000);
   				InetAddress address = InetAddress.getByName(node.getIpAddress());
   				byte[] buf = new NtpMessage().toByteArray();
   				packet = new DatagramPacket(buf, buf.length, address,123);
   				NtpMessage.encodeTimestamp(packet.getData(), 40, (System.currentTimeMillis() / 1000.0) + 2208988800.0);
   		        socket.send(packet);
   		        // Get response
   			 //  System.out.println("NTP request sent, waiting for response...n");
   			   packet = new DatagramPacket(buf, buf.length);
   	           socket.receive(packet);
   	             
   	        if(!packet.getAddress().equals(address))  
            {  
                throw new IOException("Received packet from an unknown source");  
            }  
            receivedResponse = true;  
            
   			}catch(InterruptedIOException e)  
	        {  
	            tries += 1;  
	          //  System.out.println("Timed out, " + (MAXTRIES - tries) + "");  
	        }  
   		 }while((!receivedResponse) && (tries < MAXTRIES));     
   	          
   	           // Immediately record the incoming timestamp
   		    // 根据是否接收到报文进行反馈  
   		    if(receivedResponse)  
   		    {  
   		        //System.out.println("Received: 接收到报文进行反馈" );  
   		       double destinationTimestamp = (System.currentTimeMillis() / 1000.0) + 2208988800.0;	 

   		       // Process response
   		       NtpMessage msg = new NtpMessage(packet.getData());
   		       // Corrected, according to RFC2030 errata
   		       double roundTripDelay = (destinationTimestamp - msg.originateTimestamp) - (msg.transmitTimestamp - msg.receiveTimestamp);
   		       double localClockOffset = ((msg.receiveTimestamp - msg.originateTimestamp) + (msg.transmitTimestamp - destinationTimestamp)) / 2;
   		       double newSystemTime = destinationTimestamp + localClockOffset;  
   		       long ms = (long) (newSystemTime * 1000.0); 
   		   // System.out.println(ms + "--ms");
   		       date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(ms)).toString();
   		       collecttime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
   		       // Display response
//   		       System.out.println("NTP server: " + node.getIpAddress());
//   		       System.out.println(msg.toString());
//   		       System.out.println("Dest. timestamp:"+ NtpMessage.timestampToString(destinationTimestamp)); 
//   		       System.out.println("Round-trip delay: "+ new DecimalFormat("0.00").format(roundTripDelay * 1000)+ " ms");
//   		       System.out.println("Local clock offset: "+ new DecimalFormat("0.00").format(localClockOffset * 1000)+ " ms");
   		       socket.close();
   		    }  
   		    else  
   		    {  
   		       // System.out.println("No response -- giving up."+packet.getAddress());  
   		      if(date == "" || date.equals("")){
   		    	   datestatus = "-1";
   		       }
   		    }  
   		    
   		 try {
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil
						.conversionToNodeDTO(node);
				// 判断是否存在此告警指标
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list = alarmIndicatorsUtil
						.getAlarmInicatorsThresholdForNode(nodeDTO
								.getId()
								+ "", nodeDTO.getType(), nodeDTO
								.getSubtype());
				CheckEventUtil checkEventUtil = new CheckEventUtil();
				for (int i = 0; i < list.size(); i++) {
					AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list
							.get(i);
					if ("datestatus"
							.equalsIgnoreCase(alarmIndicatorsNode
									.getName())) {
							checkEventUtil.checkEvent(nodeDTO,
									alarmIndicatorsNode, datestatus, "ntp");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
   		      //System.out.println("server date:" + date);
		      String osname = System.getProperty("os.name");
	       //   System.out.println("操作系统是：" + osname);
   			} catch (Exception e) {
		           e.printStackTrace();		          
		       }
   		returnHash.put("date", date);
        returnHash.put("node", node);
        returnHash.put("collecttime", collecttime);
    		
	  
//	    	  String cmd="";
//	          if (osname.matches("^(?i)Windows.*$")) {// Window 系统 
//	        
//	        	    // 格式：yyyy-MM-dd  
//	        	    cmd = " cmd /c date "+date;   
//	        	    java.lang.Runtime.getRuntime().exec(cmd);
//	        		// 格式 HH:mm:ss 
//	        	    cmd = "  cmd /c time "+date;   
//	        	    java.lang.Runtime.getRuntime().exec(cmd);   
//	        	    System.out.println("修改后的系统时间是：" + date);	  
//	        	   
//	          }else {//Linux 系统 
//	        	// 格式：yyyyMMdd   
//	        	   cmd = "  date -s  "+date;     
//	        	   java.lang.Runtime.getRuntime().exec(cmd);   
//	        	   // 格式 HH:mm:ss   
//	        	   cmd = "  date -s  "+date;     
//	        	   java.lang.Runtime.getRuntime().exec(cmd); 
//	         }

	      new NTPSnmp().addTNP(returnHash);
	return returnHash;
}

public boolean addTNP(Hashtable returnhash){
	
	if(returnhash != null){
		String ipaddress = "";
		Ntp ntp = (Ntp)returnhash.get("node");
		ipaddress = ntp.getIpAddress();
		String date = (String)returnhash.get("date");
		String collecttime = (String)returnhash.get("collecttime");
		DBManager dbmanager = new DBManager();
			try{
				String sql1 = "delete from nms_ntp where ipaddress='" + ipaddress + "'";
				dbmanager.addBatch(sql1);
				
					String sql = "";
					try {
						 sql = "insert into nms_ntp(ipaddress,datetime,collecttime)"
						 + "values('"
							+ ntp.getIpAddress()
							+ "','"
							+ date
							+ "','"
							+ collecttime
							 + "')";
						dbmanager.addBatch(sql);
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					} 
				dbmanager.executeBatch();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dbmanager.close();
			}
	}
	return true;
}}