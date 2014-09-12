/**
 * <p>Description:logger,writes error and debug information within system running</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.polling.om.Pingcollectdata;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PingUtil {
		 private String ipaddress="1.1.1.1";
		 private String osname;
         private Float[] time=null;
         private Hashtable allpingdata = ShareData.getAllpingdata();
         private Hashtable sendeddata = ShareData.getSendeddata();
         private java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         /*
         private I_Equipment equipmanager=new EquipmentManager();   
         private I_Smscontent smsmanager=new SmscontentManager();
         private I_Eventlist eventmanager=new EventlistManager();
         private I_Firewallconf firewallconfManager = new FirewallconfManager();
         private I_Upsconf upsconfManager = new UpsconfManager();
         private I_Aircondconf aircondconfManager = new AircondconfManager();
         */
         
		 public String  initCMD(){
		 String PING_CMD="";
		 String s1;		 
				 if(osname.startsWith("SunOS") || osname.startsWith("Solaris"))
				 {
					 PING_CMD = "/usr/sbin/ping ";
					 s1 = new String(PING_CMD + " " );
				 }
				 else  if(osname.startsWith("Linux"))
				 {
					 if(System.getProperty("pingcommand") != null)
						 PING_CMD = System.getProperty("pingcommand");
					 else
						 PING_CMD = "/bin/ping -c 3 -i 10 ";
					 s1 = new String(PING_CMD + " "  );
				 }
				 else  if(osname.startsWith("FreeBSD"))
				 {
					 PING_CMD = "/sbin/ping -c 3";
					 s1 = new String(PING_CMD + " " );
				 }
				 else if(osname.startsWith("Windows"))
				 {
					 PING_CMD = "ping -n 3 -w 10000 ";
					 s1 = new String(PING_CMD + " " );
				 }
				 else
				 {
					 s1 = new String(PING_CMD + " " );
				 }
				// cmd=new String(s1);
				// System.out.println("initcmd----"+cmd);
			return s1;
	   }
	   
		public Vector addhis(Integer[] packet){//向历史表添加
			Vector returnVector=new Vector();
		   try{
					Pingcollectdata hostdata=null;
					Calendar date=Calendar.getInstance();
					hostdata=new Pingcollectdata();
					hostdata.setIpaddress(ipaddress);
					hostdata.setCollecttime(date);
					hostdata.setCategory("Ping");
					hostdata.setEntity("Utilization");
					hostdata.setSubentity("ConnectUtilization");
					hostdata.setRestype("dynamic");
					hostdata.setUnit("%");
					if(packet[0]==null){	
						hostdata.setThevalue("0");						
					}
					else{						
						hostdata.setThevalue(packet[0].toString());
					}	
					//hostdata.setThevalue("0");		
				    returnVector.addElement(hostdata);
				    //begin 进行短信处理
				    //createSMS(hostdata);
				    //end 进行短信处理
					hostdata=new Pingcollectdata();
					hostdata.setIpaddress(ipaddress);
					hostdata.setCollecttime(date);
					hostdata.setCategory("Ping");
					hostdata.setEntity("ResponseTime");
					hostdata.setSubentity("ResponseTime");
					hostdata.setRestype("dynamic");
					hostdata.setUnit("毫秒");
					if(packet[1]!=null){
						hostdata.setThevalue(packet[1].toString());
					}
					else{
						hostdata.setThevalue("0");
					}
					returnVector.addElement(hostdata);
				}
		   catch(Exception e){
			   e.printStackTrace();
			   System.err.println("create pinghis error"+e.getMessage());
		   }
		   //returnHash.put("ping",returnVector);		   
		   return returnVector;
		}



		 public Integer[] manageLine(String line){
		 	String[] lines=line.split(",");
			Integer[] packet=new Integer[lines.length];
				if(lines.length>=3){				
					int connect=0;String responseTime="";
					if(osname.startsWith("Windows")){
							String  values0=lines[0].substring(lines[0].indexOf("=")+1,lines[0].length()).trim();
							String  values1=lines[1].substring(lines[1].indexOf("=")+1,lines[1].length()).trim();
							connect=Integer.parseInt(values1)*100/Integer.parseInt(values0);
							packet[0]=new Integer(connect);
					}
					if(osname.startsWith("Linux")){
							String  values0=lines[0].substring(0,lines[0].indexOf("packets")-1).trim();
							String  values1=lines[1].substring(0,lines[1].indexOf("received")-1).trim();
							connect=Integer.parseInt(values1)*100/Integer.parseInt(values0);
							packet[0]=new Integer(connect);
					}
					
				}
			 return packet;
		 }
		 public Integer[] manageResponseLine(String line){
			 	String[] lines=line.split(",");
				Integer[] packet=new Integer[lines.length];
				
					if(lines.length>=3){				
						int connect=0;int responseTime=0;
						if(osname.startsWith("Windows")){
								String  values2=lines[2].substring(lines[2].indexOf("=")+1,lines[2].length()).trim();
								if(values2 != null){
									values2 = values2.replaceAll("ms", "");
									responseTime=Integer.parseInt(values2.trim());
								}
								packet[0]=new Integer(responseTime);
						}
						if(osname.startsWith("Linux")){
								String  values0=lines[0].substring(0,lines[0].indexOf("packets")-1).trim();
								String  values1=lines[1].substring(0,lines[1].indexOf("received")-1).trim();
								connect=Integer.parseInt(values1)*100/Integer.parseInt(values0);
								packet[0]=new Integer(connect);
								//responseTime=line.substring(line.indexOf("time")+4,line.indexOf("ms")).trim();
								//packet[1]=new Integer(responseTime);
						}
						
					}
				 return packet;
			 }

		 /*
		 public void createSMS(Pingcollectdata p_pingcollectdata){
		 	//建立短信		 	
		 	//从内存里获得当前这个IP的PING的值
		 	Pingcollectdata pingcollectdata = (Pingcollectdata)allpingdata.get(p_pingcollectdata.getIpaddress());		 	
		 	try{
		 		
		 	if (pingcollectdata==null){		 		
		 		allpingdata.put(p_pingcollectdata.getIpaddress(),p_pingcollectdata);
		 		//判断PING的值，若为0，则发送短信，同时将发送短信的PING的值放到已经发送的列表里		 		
		 		if (p_pingcollectdata.getThevalue().equals("0")){
		 			Smscontent smscontent = new Smscontent();
		 			String time = sdf.format(p_pingcollectdata.getCollecttime().getTime());
		 			Equipment equipment = equipmanager.getByip(p_pingcollectdata.getIpaddress());
		 			//smscontent.setMessage(equipment.getEquipname()+":"+equipment.getIpaddress()+":连通率:"+time+":"+p_pingcollectdata.getThevalue()+"%");
		 			String mailopers = "";
		 			if (equipment == null){
		 				Firewallconf firewallconf= firewallconfManager.getByIP(p_pingcollectdata.getIpaddress());
		 				if (firewallconf == null){
		 					Upsconf upsconf = upsconfManager.getByIP(ipaddress);
		 					if(upsconf == null){
		 						Aircondconf aircondconf = aircondconfManager.getByIP(ipaddress);
		 						if(aircondconf == null){
		 							return;
		 						}else
		 							smscontent.setMessage(time+"&"+aircondconf.getAircondname()+"&"+aircondconf.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%&level=2");		 							
		 					}else
		 						smscontent.setMessage(time+"&"+upsconf.getUpsname()+"&"+upsconf.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%&level=2");
		 				}else{
		 					smscontent.setMessage(time+"&"+firewallconf.getFirewallname()+"&"+firewallconf.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%&level=2");
		 				}
		 			}else
		 				smscontent.setMessage(time+"&"+"设备"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%&level=2");
		 			//发送短信
		 			Vector tosend = new Vector();
		 			tosend.add(smscontent);		 			
		 			smsmanager.sendSmscontent(tosend);
		 			sendeddata.put(p_pingcollectdata.getIpaddress(),p_pingcollectdata);		 			
		 		}		 		
		 	}else{
		 		allpingdata.put(p_pingcollectdata.getIpaddress(),p_pingcollectdata);
		 		//若内存中存在这个IP的PING的值
		 		if (p_pingcollectdata.getThevalue().equals("0")){
		 			//判断上一次是否为PING通，若上一次为通，则建立短信
		 			if (!pingcollectdata.getThevalue().equals("0")){
		 				//建立短信
			 			Smscontent smscontent = new Smscontent();
			 			String time = sdf.format(p_pingcollectdata.getCollecttime().getTime());
			 			Equipment equipment = equipmanager.getByip(p_pingcollectdata.getIpaddress());
			 			//if (equipment == null)return;
			 			//smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%");
			 			if (equipment == null){
			 				Firewallconf firewallconf= firewallconfManager.getByIP(p_pingcollectdata.getIpaddress());
			 				if (firewallconf == null){
			 					Upsconf upsconf = upsconfManager.getByIP(ipaddress);
			 					if(upsconf == null){
			 						Aircondconf aircondconf = aircondconfManager.getByIP(ipaddress);
			 						if(aircondconf == null){
			 							return;
			 						}else
			 							smscontent.setMessage(time+"&"+aircondconf.getAircondname()+"&"+aircondconf.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%&level=2");		 							
			 					}else
			 						smscontent.setMessage(time+"&"+upsconf.getUpsname()+"&"+upsconf.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%&level=2");
			 				}else{
			 					smscontent.setMessage(time+"&"+firewallconf.getFirewallname()+"&"+firewallconf.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%&level=2");
			 				}
			 			}else
			 				smscontent.setMessage(time+"&"+"服务器"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%&level=2");			 			
			 			//发送短信
			 			Vector tosend = new Vector();
System.out.println(smscontent);			 			
			 			tosend.add(smscontent);		 			
			 			smsmanager.sendSmscontent(tosend);				 			
			 			sendeddata.put(p_pingcollectdata.getIpaddress(),p_pingcollectdata);		 					 				
		 			}else{
		 				//若上一次也为不通
		 			//判断这个设备是否已经在已经发送短信息列表里
		 			if (sendeddata.get(p_pingcollectdata.getIpaddress())==null){
		 				//若不在，则建立短信，并且添加到发送列表里
			 			Smscontent smscontent = new Smscontent();
			 			String time = sdf.format(p_pingcollectdata.getCollecttime().getTime());
			 			Equipment equipment = equipmanager.getByip(p_pingcollectdata.getIpaddress());
			 			//smscontent.setMessage(equipment.getEquipname()+":"+equipment.getIpaddress()+":连通率:"+time+":"+p_pingcollectdata.getThevalue()+"%");
			 			//if (equipment == null)return;
			 			//smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%");
			 			if (equipment == null){
			 				Firewallconf firewallconf= firewallconfManager.getByIP(p_pingcollectdata.getIpaddress());
			 				if (firewallconf == null){
			 					Upsconf upsconf = upsconfManager.getByIP(ipaddress);
			 					if(upsconf == null){
			 						Aircondconf aircondconf = aircondconfManager.getByIP(ipaddress);
			 						if(aircondconf == null){
			 							return;
			 						}else
			 							smscontent.setMessage(time+"&"+aircondconf.getAircondname()+"&"+aircondconf.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%&level=2");		 							
			 					}else
			 						smscontent.setMessage(time+"&"+upsconf.getUpsname()+"&"+upsconf.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%&level=2");
			 				}else{
			 					smscontent.setMessage(time+"&"+firewallconf.getFirewallname()+"&"+firewallconf.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%&level=2");
			 				}
			 			}else
			 				smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%&level=2");			 			
			 			//发送短信
			 			Vector tosend = new Vector();
			 			tosend.add(smscontent);
			 			smsmanager.sendSmscontent(tosend);				 			
			 			sendeddata.put(p_pingcollectdata.getIpaddress(),p_pingcollectdata);		 					 				
		 			}else{
		 				//若在，则从已发送短信列表里判断是否已经发送当天的短信
		 				Pingcollectdata pingdata =(Pingcollectdata)sendeddata.get(p_pingcollectdata.getIpaddress());		 				
			 			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			 			Date last = null;
			 			Date current = null;
			 			Calendar sendcalen = (Calendar)pingdata.getCollecttime();
			 			Date cc = sendcalen.getTime();
			 			String tempsenddate = formatter.format(cc);
			 			
			 			Calendar currentcalen = (Calendar)p_pingcollectdata.getCollecttime();
			 			cc = currentcalen.getTime();
			 			last = formatter.parse(tempsenddate);
			 			String currentsenddate = formatter.format(cc);
			 			current = formatter.parse(currentsenddate);
			 			
			 			long subvalue = current.getTime()-last.getTime();			 			
			 			if (subvalue/(1000*60*60*24)>=1){
			 				//超过一天，则再发信息
				 			Smscontent smscontent = new Smscontent();
				 			String time = sdf.format(p_pingcollectdata.getCollecttime().getTime());
				 			Equipment equipment = equipmanager.getByip(p_pingcollectdata.getIpaddress());
				 			//if (equipment == null)return;
				 			//smscontent.setMessage(equipment.getEquipname()+":"+equipment.getIpaddress()+":连通率:"+time+":"+p_pingcollectdata.getThevalue()+"%");
				 			//smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%");
				 			if (equipment == null){
				 				Firewallconf firewallconf= firewallconfManager.getByIP(p_pingcollectdata.getIpaddress());
				 				if (firewallconf == null){
				 					Upsconf upsconf = upsconfManager.getByIP(ipaddress);
				 					if(upsconf == null){
				 						Aircondconf aircondconf = aircondconfManager.getByIP(ipaddress);
				 						if(aircondconf == null){
				 							return;
				 						}else
				 							smscontent.setMessage(time+"&"+aircondconf.getAircondname()+"&"+aircondconf.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%&level=2");		 							
				 					}else
				 						smscontent.setMessage(time+"&"+upsconf.getUpsname()+"&"+upsconf.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%&level=2");
				 				}else{
				 					smscontent.setMessage(time+"&"+firewallconf.getFirewallname()+"&"+firewallconf.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%&level=2");
				 				}
				 			}else
				 				smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&连通率&"+p_pingcollectdata.getThevalue()+"%&level=2");
				 			
				 			//发送短信
				 			Vector tosend = new Vector();
				 			tosend.add(smscontent);
				 			smsmanager.sendSmscontent(tosend);				 			
				 			//修改已经发送的短信记录	
				 			sendeddata.put(p_pingcollectdata.getIpaddress(),p_pingcollectdata);	
				 		}	
		 			}
		 			}
		 		}else{
		 			//若为PING通的设备,则从已经发送的短信列表里删除该IP
		 			sendeddata.remove(p_pingcollectdata.getIpaddress());
		 		}
		 		
		 	}
		 	}catch(Exception e){
		 		e.printStackTrace();
		 	}
		 }
		*/
		
		/**
		 *
		 */
		public PingUtil(String ip) {
			super();
			ipaddress=ip;
			osname=System.getProperty("os.name");
		}

	public Integer[] ping(){
		Integer[] packet=null;
		BufferedReader  br  =null;
		Runtime runtime;
		Process process;
		InputStream  is  =null;
		InputStreamReader  isr=null;
		String cmd=initCMD();
		String pingCommand=cmd+ipaddress;
//		long starttime = 0;
//		long endtime = 0;
//		long condelay = 0;
//		SysLogger.info("开始采集 "+ipaddress+" PING 数据");
		try{
			runtime  =  Runtime.getRuntime();
//			starttime = System.currentTimeMillis();	
			process  =runtime.exec(pingCommand);
//			endtime = System.currentTimeMillis();
//			condelay = endtime-starttime;
			
			is  =  process.getInputStream();
			isr=new  InputStreamReader(is);
			br  =new  BufferedReader(isr);						
			String  line=null;
			int sign = 0;
//			String line ="2 packets transmitted, 2 received, 0% packet loss, time 1001ms"+
//                          "rtt min/avg/max/mdev = 0.132/0.214/0.296/0.082 ms";
			int j=0;

			
			try{
				while(  (line  =  br.readLine())  !=  null  ){
				
					line = line.trim();
					//SysLogger.info(ipaddress+"===="+line);
					if(line.length()==0)continue;
					line = line.trim().replaceAll("数据包","Packets").replaceAll("已发送","Sent").replaceAll("已接收","Received").replaceAll("丢失","Lost")
					.replaceAll("最短","Minimum").replaceAll("最长","Maximum").replaceAll("平均","Average").replaceAll("，",",")
					.replaceAll("往返行程的估计时间(以毫秒为单位):", "Approximate round trip times in milli-seconds:").replaceAll("TTL 传输中过期。", "TTL expired in transit.")
					.replaceAll("目标网无法访问。", "Destination net unreachable.").replaceAll("请求超时。", "Request timed out.").replaceAll("无法访问目标主机。", "Destination net unreachable.");
					//System.out.println("======="+ipaddress+" "+line);
					if(line.indexOf("expired in transit") != -1 || line.indexOf("unreachable") != -1){
						sign = 1;
					}
					if(line.toLowerCase().indexOf("packets")!=-1){
						if(sign == 1){
							int a = line.indexOf("Received = ");
							String str = line.substring(0,a+10);
							String str1 = line.substring(a+12);
							line = str +"0"+str1;
							
						}
						packet=manageLine(line);
					}else{
						int connect = 0;
						//SysLogger.info(line);
						String[] packetLine = line.split(" "); 
						if(packetLine.length>3&&packetLine[1].equalsIgnoreCase("packets")&&packetLine[2].equalsIgnoreCase("transmitted,")){
							connect=Integer.parseInt(packetLine[3])*100/Integer.parseInt(packetLine[0]);
							packet[0]=new Integer(connect);
						}
					}
					if(line.contains("=")){
					String[] lines=line.split(",");
					if(lines.length>=3){
						String  values0=lines[0].substring(0,lines[0].indexOf("=")).trim();
						
						if(values0.equalsIgnoreCase("Minimum")){
							Integer[] _packet=null;
							_packet=manageResponseLine(line);
							packet[1] = new Integer(_packet[0]+"");
						}				
					}
				}else {
					String[] lines=line.split(" ");
					if(lines.length>=3){						
						if(lines[0].trim().equalsIgnoreCase("rtt")){
							String[] avgtime = lines[3].trim().split("/");
							//System.out.println("========quzhi========="+avgtime[1]);
							packet[1] = new Integer(avgtime[1]+"");
						}				
					}
					
				}
				}							
			}catch(Exception e){
				e.printStackTrace();
				//System.out.println("error in crtparam1"+e.getMessage());
				//runtime.exit(1);
			}finally{
				process.destroy();
				is.close();
				isr.close();
				br.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		if(packet == null){
			SysLogger.info(ipaddress+"======PING数据为空");
			SysLogger.info(ipaddress+"====PING命令"+pingCommand);
		}
//		SysLogger.info("结束采集 "+ipaddress+" PING 数据");

		
		return packet;
	} 
	

	public static void main(String [] args){
		PingUtil pingU=new PingUtil("10.40.30.133");
				Integer[] packet=pingU.ping();
				//pingU.addhis(packet);
	}                           
	
}
