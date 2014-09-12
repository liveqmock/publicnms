package com.afunms.polling.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.model.SlaNodeConfig;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.model.CmdResult;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.snmp.AS400Collection;
import com.afunms.polling.telnet.CiscoTelnet;
import com.afunms.polling.telnet.Huawei3comvpn;
import com.afunms.polling.telnet.TelnetWrapper;
import com.afunms.topology.dao.RemotePingHostDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.RemotePingHost;

public class SLATelnetDataCollector {
	
	public SLATelnetDataCollector()
	{
	}
	public Hashtable collect_data(Huaweitelnetconf telconf,List nodelist) {
    	Vector vector=null;
    	Hashtable allslahash = new Hashtable();
    	String[] result = new String[nodelist.size()];
		try {
            		try{
            			//  首先获取协议信息
            			SysLogger.info(" ######################## ");
            			SysLogger.info(" ### 开始采集SLA " + telconf.getIpaddress() + " by telnet");
            			SysLogger.info(" ######################## ");
            			Hashtable configNodeHash = new Hashtable();
            			
            			if(telconf.getDeviceRender().equalsIgnoreCase("h3c")){
            				Huawei3comvpn tvpn = new Huawei3comvpn();
            				tvpn.setDEFAULT_TELNET_PORT(telconf.getPort());// 端口
            				tvpn.setSuuser(telconf.getSuuser());// su
            				tvpn.setSupassword(telconf.getSupassword());// su密码
            				tvpn.setUser(telconf.getUser());// 用户
            				tvpn.setPassword(telconf.getPassword());// 密码
            				tvpn.setIp(telconf.getIpaddress());// ip地址
            				tvpn.setDEFAULT_PROMPT(telconf.getDefaultpromtp());// 结束标记符号
            				tvpn.setPort(telconf.getPort());
            				if(nodelist != null && nodelist.size()>0){
    							String[] commStr = new String[nodelist.size()];
    							String commstr = "";
    							for(int i=0;i<nodelist.size();i++){
    								SlaNodeConfig slaconfig = (SlaNodeConfig)nodelist.get(i);
    								commstr = "display nqa result "+slaconfig.getEntrynumber();
    								commStr[i] =  commstr;
    							}
    							try{
    								result = tvpn.getCommantValues(commStr);
    							}catch(Exception e){
    								e.printStackTrace();
    							}
    							
    							if(result != null && result.length>0){
    								//对结果进行解析    								
    								Pingcollectdata hostdata = null;
    								Calendar date=Calendar.getInstance();
//    								for(int i=0;i<result.length;i++){
//    									String content = result[i];
//    									SysLogger.info(content);
//    								}
    								for(int i=0;i<result.length;i++){
    									String RTTAvg = "";
    									String packageLostPerc = "";
    									String Min_positive_SD = "";
    									String Min_positive_DS = "";
    									String Max_positive_SD = "";
    									String Max_positive_DS = "";
    									String Positive_SD_number = "";
    									String Positive_DS_number = "";
    									String Positive_SD_sum = "";
    									String Positive_DS_sum = "";
    									String Positive_SD_average = "";
    									String Positive_DS_average = "";
    									String Max_negative_SD = "";
    									String Max_negative_DS = "";
    									String Negative_SD_number = "";
    									String Negative_DS_number = "";
    									String Negative_SD_sum = "";
    									String Negative_DS_sum = "";
    									String Negative_SD_average = "";
    									String Negative_DS_average = "";
    									
    									
    									String content = result[i];
    									String[] st = content.split("\r\n");
    									
    									SlaNodeConfig slaconfig = (SlaNodeConfig)nodelist.get(i);
    									if(slaconfig.getSlatype().equalsIgnoreCase("jitter")){
    										//针对抖动作业的内容解析
    										//SysLogger.info(content);
        									Hashtable dataRTTHash = new Hashtable();
        									Vector jitterV = new Vector();
        									if(st != null && st.length>0){
        										
        										for(int k=0;k<st.length;k++){
        											RTTAvg = "";
        											String linecontent = st[k];
        											//SysLogger.info(linecontent);
        											if(linecontent.contains("Min/Max/Average round trip time")){
        												String[] splits = linecontent.split(":");
        												String rttValues = splits[1].trim();
        												String[] rtts = rttValues.split("/");
        												RTTAvg = rtts[2];
        												//SysLogger.info("RTTAvg=============="+RTTAvg);
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Ping");
        												hostdata.setEntity("ResponseTime");
        												hostdata.setSubentity("ResponseTime");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("毫秒");
        												hostdata.setThevalue(RTTAvg);
        												dataRTTHash.put("rtt", hostdata);
        											}else if(linecontent.contains("Packet lost in test")){
            											String[] splits = linecontent.split(":");
            											packageLostPerc = splits[1].trim().replaceAll("%", "");
            											hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Package");
        												hostdata.setEntity("PackageLostPerc");
        												hostdata.setSubentity("PackageLostPerc");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("%");
        												hostdata.setThevalue(packageLostPerc);
        												dataRTTHash.put("pack", hostdata);
            											//SysLogger.info("packageLostPerc=============="+packageLostPerc);
        											}else if(linecontent.contains("Min positive SD")){
        												String[] _splits = linecontent.split(":");
        												if(_splits.length==3){
        													Min_positive_DS = _splits[2].trim();
        													String temp = _splits[1].trim();
        													String[] tempSplit = temp.trim().split("\\s++");
        													Min_positive_SD = tempSplit[0].trim();
        													//SysLogger.info("Min_positive_SD=="+Min_positive_SD+"------Min_positive_DS:"+Min_positive_DS);
        												}
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Min_positive_SD");
        												hostdata.setEntity("Min_positive_SD");
        												hostdata.setSubentity("Min_positive_SD");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("ms");
        												hostdata.setThevalue(Min_positive_SD);
        												jitterV.add(hostdata);
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Min_positive_DS");
        												hostdata.setEntity("Min_positive_DS");
        												hostdata.setSubentity("Min_positive_DS");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("ms");
        												hostdata.setThevalue(Min_positive_DS);
        												jitterV.add(hostdata);
        											}else if(linecontent.contains("Max positive SD")){
        												String[] _splits = linecontent.split(":");
        												if(_splits.length==3){
        													Max_positive_DS = _splits[2].trim();
        													String temp = _splits[1].trim();
        													String[] tempSplit = temp.trim().split("\\s++");
        													Max_positive_SD = tempSplit[0].trim();
        													//SysLogger.info("Max_positive_SD=="+Max_positive_SD+"------Max_positive_DS:"+Max_positive_DS);
        												}
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Max_positive_SD");
        												hostdata.setEntity("Max_positive_SD");
        												hostdata.setSubentity("Max_positive_SD");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("ms");
        												hostdata.setThevalue(Max_positive_SD);
        												jitterV.add(hostdata);
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Max_positive_DS");
        												hostdata.setEntity("Max_positive_DS");
        												hostdata.setSubentity("Max_positive_DS");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("ms");
        												hostdata.setThevalue(Max_positive_DS);
        												jitterV.add(hostdata);
        											}else if(linecontent.contains("Positive SD number")){
        												String[] _splits = linecontent.split(":");
        												if(_splits.length==3){
        													Positive_DS_number = _splits[2].trim();
        													String temp = _splits[1].trim();
        													String[] tempSplit = temp.trim().split("\\s++");
        													Positive_SD_number = tempSplit[0].trim();
        													//SysLogger.info("Positive_SD_number=="+Positive_SD_number+"-------Positive_DS_number:"+Positive_DS_number);
        												}
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Positive_SD_number");
        												hostdata.setEntity("Positive_SD_number");
        												hostdata.setSubentity("Positive_SD_number");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("个");
        												hostdata.setThevalue(Positive_SD_number);
        												jitterV.add(hostdata);
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Positive_DS_number");
        												hostdata.setEntity("Positive_DS_number");
        												hostdata.setSubentity("Positive_DS_number");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("个");
        												hostdata.setThevalue(Positive_DS_number);
        												jitterV.add(hostdata);
        											}else if(linecontent.contains("Positive SD sum")){
        												String[] _splits = linecontent.split(":");
        												if(_splits.length==3){
        													Positive_DS_sum = _splits[2].trim();
        													String temp = _splits[1].trim();
        													String[] tempSplit = temp.trim().split("\\s++");
        													Positive_SD_sum = tempSplit[0].trim();
        													//SysLogger.info("Positive_SD_sum=="+Positive_SD_sum+"-------Positive_DS_sum:"+Positive_DS_sum);
        												}
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Positive_SD_sum");
        												hostdata.setEntity("Positive_SD_sum");
        												hostdata.setSubentity("Positive_SD_sum");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("个");
        												hostdata.setThevalue(Positive_SD_sum);
        												jitterV.add(hostdata);
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Positive_DS_sum");
        												hostdata.setEntity("Positive_DS_sum");
        												hostdata.setSubentity("Positive_DS_sum");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("个");
        												hostdata.setThevalue(Positive_DS_sum);
        												jitterV.add(hostdata);
        											}else if(linecontent.contains("Positive SD average")){
        												String[] _splits = linecontent.split(":");
        												if(_splits.length==3){
        													Positive_DS_average = _splits[2].trim();
        													String temp = _splits[1].trim();
        													String[] tempSplit = temp.trim().split("\\s++");
        													Positive_SD_average = tempSplit[0].trim();
        													//SysLogger.info("Positive_SD_average=="+Positive_SD_average+"-------Positive_DS_average:"+Positive_DS_average);
        												}
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Positive_SD_average");
        												hostdata.setEntity("Positive_SD_average");
        												hostdata.setSubentity("Positive_SD_average");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("ms");
        												hostdata.setThevalue(Positive_SD_average);
        												jitterV.add(hostdata);
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Positive_DS_average");
        												hostdata.setEntity("Positive_DS_average");
        												hostdata.setSubentity("Positive_DS_average");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("ms");
        												hostdata.setThevalue(Positive_DS_average);
        												jitterV.add(hostdata);
        											}else if(linecontent.contains("Max negative SD")){
        												String[] _splits = linecontent.split(":");
        												if(_splits.length==3){
        													Max_negative_DS = _splits[2].trim();
        													String temp = _splits[1].trim();
        													String[] tempSplit = temp.trim().split("\\s++");
        													Max_negative_SD = tempSplit[0].trim();
        													//SysLogger.info("Max_negative_SD=="+Max_negative_SD+"-------Max_negative_DS:"+Max_negative_DS);
        												}
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Max_negative_SD");
        												hostdata.setEntity("Max_negative_SD");
        												hostdata.setSubentity("Max_negative_SD");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("ms");
        												hostdata.setThevalue(Max_negative_SD);
        												jitterV.add(hostdata);
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Max_negative_DS");
        												hostdata.setEntity("Max_negative_DS");
        												hostdata.setSubentity("Max_negative_DS");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("ms");
        												hostdata.setThevalue(Max_negative_DS);
        												jitterV.add(hostdata);
        											}else if(linecontent.contains("Negative SD number")){
        												String[] _splits = linecontent.split(":");
        												if(_splits.length==3){
        													Negative_DS_number = _splits[2].trim();
        													String temp = _splits[1].trim();
        													String[] tempSplit = temp.trim().split("\\s++");
        													Negative_SD_number = tempSplit[0].trim();
        													//SysLogger.info("Negative_SD_number=="+Negative_SD_number+"-------Negative_DS_number:"+Negative_DS_number);
        												}
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Negative_SD_number");
        												hostdata.setEntity("Negative_SD_number");
        												hostdata.setSubentity("Negative_SD_number");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("个");
        												hostdata.setThevalue(Negative_SD_number);
        												jitterV.add(hostdata);
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Negative_DS_number");
        												hostdata.setEntity("Negative_DS_number");
        												hostdata.setSubentity("Negative_DS_number");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("个");
        												hostdata.setThevalue(Negative_DS_number);
        												jitterV.add(hostdata);
        											}else if(linecontent.contains("Negative SD sum")){
        												String[] _splits = linecontent.split(":");
        												if(_splits.length==3){
        													Negative_DS_sum = _splits[2].trim();
        													String temp = _splits[1].trim();
        													String[] tempSplit = temp.trim().split("\\s++");
        													Negative_SD_sum = tempSplit[0].trim();
        													//SysLogger.info("Negative_SD_sum=="+Negative_SD_sum+"-------Negative_DS_sum:"+Negative_DS_sum);
        												}
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Negative_SD_sum");
        												hostdata.setEntity("Negative_SD_sum");
        												hostdata.setSubentity("Negative_SD_sum");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("ms");
        												hostdata.setThevalue(Negative_SD_sum);
        												jitterV.add(hostdata);
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Negative_DS_sum");
        												hostdata.setEntity("Negative_DS_sum");
        												hostdata.setSubentity("Negative_DS_sum");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("ms");
        												hostdata.setThevalue(Negative_DS_number);
        												jitterV.add(hostdata);
        											}else if(linecontent.contains("Negative SD average")){
        												String[] _splits = linecontent.split(":");
        												if(_splits.length==3){
        													Negative_DS_average = _splits[2].trim();
        													String temp = _splits[1].trim();
        													String[] tempSplit = temp.trim().split("\\s++");
        													Negative_SD_average = tempSplit[0].trim();
        													//SysLogger.info("Negative_SD_average=="+Negative_SD_average+"-------Negative_DS_average:"+Negative_DS_average);
        												}
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Negative_SD_average");
        												hostdata.setEntity("Negative_SD_average");
        												hostdata.setSubentity("Negative_SD_average");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("ms");
        												hostdata.setThevalue(Negative_SD_average);
        												jitterV.add(hostdata);
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Negative_DS_average");
        												hostdata.setEntity("Negative_DS_average");
        												hostdata.setSubentity("Negative_DS_average");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("ms");
        												hostdata.setThevalue(Negative_DS_average);
        												jitterV.add(hostdata);
        											}
        											
        										}
        									}
											dataRTTHash.put("jitter",jitterV);
        									allslahash.put(slaconfig.getId()+"", dataRTTHash);
        									ShareData.getSlaHash().put(slaconfig.getId()+"", dataRTTHash);
    									}else if(slaconfig.getSlatype().equalsIgnoreCase("icmp")
    											|| slaconfig.getSlatype().equalsIgnoreCase("tcp")
    											|| slaconfig.getSlatype().equalsIgnoreCase("udp")
    											|| slaconfig.getSlatype().equalsIgnoreCase("http")){
    										//针对抖动作业的内容解析
    										//SysLogger.info(content);
        									Hashtable dataRTTHash = new Hashtable();
        									Vector jitterV = new Vector();
        									if(st != null && st.length>0){
        										for(int k=0;k<st.length;k++){
        											RTTAvg = "";
        											String linecontent = st[k];
        											//SysLogger.info(linecontent);
        											if(linecontent.contains("Min/Max/Average round trip time")){
        												String[] splits = linecontent.split(":");
        												String rttValues = splits[1].trim();
        												String[] rtts = rttValues.split("/");
        												RTTAvg = rtts[2];
        												//SysLogger.info("RTTAvg=============="+RTTAvg);
        												hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Ping");
        												hostdata.setEntity("ResponseTime");
        												hostdata.setSubentity("ResponseTime");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("毫秒");
        												hostdata.setThevalue(RTTAvg);
        												dataRTTHash.put("rtt", hostdata);
        											}else if(linecontent.contains("Packet lost in test")){
            											String[] splits = linecontent.split(":");
            											packageLostPerc = splits[1].trim().replaceAll("%", "");
            											hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Package");
        												hostdata.setEntity("PackageLostPerc");
        												hostdata.setSubentity("PackageLostPerc");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("%");
        												hostdata.setThevalue(packageLostPerc);
        												dataRTTHash.put("status", hostdata);
        											}else if(linecontent.contains("Packet loss in test")){
        												String[] splits = linecontent.split(":");
            											packageLostPerc = splits[1].trim().replaceAll("%", "");
            											hostdata=new Pingcollectdata();
        												hostdata.setIpaddress(telconf.getIpaddress());
        												hostdata.setCollecttime(date);
        												hostdata.setCategory("Package");
        												hostdata.setEntity("PackageLostPerc");
        												hostdata.setSubentity("PackageLostPerc");
        												hostdata.setRestype("dynamic");
        												hostdata.setUnit("%");
        												try{
        													hostdata.setThevalue((100-Integer.parseInt(packageLostPerc))+"");
        												}catch(Exception e){
        													
        												}
        												dataRTTHash.put("status", hostdata);
        											}
        											
        											
        										}
        										SysLogger.info(slaconfig.getId()+"======采集完成"+slaconfig.getName());
    											allslahash.put(slaconfig.getId()+"", dataRTTHash);
            									ShareData.getSlaHash().put(slaconfig.getId()+"", dataRTTHash);
        									}
    									}
    									
    									
    									
    								}
    								
    							}
    							
    						}
            				
            			}else if(telconf.getDeviceRender().equalsIgnoreCase("cisco")){
            				CiscoTelnet telnet = new CiscoTelnet(telconf.getIpaddress(), telconf
        							.getUser(), telconf.getPassword(),telconf.getPort());
        					if (telnet.login()) {
        						if(nodelist != null && nodelist.size()>0){
        							String[] commStr = new String[nodelist.size()];
        							String commstr = "";
        							for(int i=0;i<nodelist.size();i++){
        								SlaNodeConfig slaconfig = (SlaNodeConfig)nodelist.get(i);
        								commstr = "show rtr op "+slaconfig.getEntrynumber();
        								commStr[i] =  commstr;
        								//configNodeHash.put(i, slaconfig);
        							}
        							try{
        								result = telnet.getSlaResult(telconf.getSupassword(), commStr);
        							}catch(Exception e){
        								e.printStackTrace();
        							}
        							
        							if(result != null && result.length>0){
        								//对结果进行解析
        								
        								Pingcollectdata hostdata = null;
        								Calendar date=Calendar.getInstance();
        								for(int i=0;i<result.length;i++){
        									String entrynumber = "";
        									String content = result[i];
        									String[] st = content.split("\r\n");
        									Hashtable dataHash = new Hashtable();
        									SlaNodeConfig slaconfig = (SlaNodeConfig)nodelist.get(i);
        									if(st != null && st.length>0){
        										
        										if(slaconfig.getSlatype().equalsIgnoreCase("jitter")){
        											
        										}else  if(slaconfig.getSlatype().equalsIgnoreCase("icmp")
            											|| slaconfig.getSlatype().equalsIgnoreCase("tcp")
            											|| slaconfig.getSlatype().equalsIgnoreCase("udp")
            											|| slaconfig.getSlatype().equalsIgnoreCase("http")){
        											for(int k=0;k<st.length;k++){
            											entrynumber = "";
            											String linecontent = st[k];
            											SysLogger.info(linecontent);
            											if(linecontent.contains("Entry Number")){
            												String[] splits = linecontent.split(":");
            												entrynumber = splits[1].trim();
            											}
            											if(linecontent.contains("Latest Completion Time (milliseconds)")){
            												String[] splits = linecontent.split(":");
            												String rtt = splits[1].trim();
            												//响应时间
            												hostdata=new Pingcollectdata();
            												hostdata.setIpaddress(telconf.getIpaddress());
            												hostdata.setCollecttime(date);
            												hostdata.setCategory("Ping");
            												hostdata.setEntity("ResponseTime");
            												hostdata.setSubentity("ResponseTime");
            												hostdata.setRestype("dynamic");
            												hostdata.setUnit("毫秒");
            												hostdata.setThevalue(rtt);
            												//SysLogger.info("====add rtt data ====="+rtt+"毫秒");
            												dataHash.put("rtt", hostdata);
            												//_vector.add(1, hostdata);
            												
            											}else if(linecontent.contains("Latest Operation Return Code")){
            												//状态
            												String[] splits = linecontent.split(":");
            												hostdata=new Pingcollectdata();
            						    					hostdata.setIpaddress(telconf.getIpaddress());
            						    					hostdata.setCollecttime(date);
            						    					hostdata.setCategory("Ping");
            						    					hostdata.setEntity("Utilization");
            						    					hostdata.setSubentity("ConnectUtilization");
            						    					hostdata.setRestype("dynamic");
            						    					hostdata.setUnit("%");
            												String status = splits[1].trim();
            												if("ok".equalsIgnoreCase(status)){
            													hostdata.setThevalue("100");
            												}else{
            													hostdata.setThevalue("0");
            												}
            												dataHash.put("status", hostdata);
            												//_vector.add(0, hostdata);
            											}
            										}
        										}
        										
        									}
        									//SlaNodeConfig slaconfig = (SlaNodeConfig)nodelist.get(i);
        									//SysLogger.info("id:"+slaconfig.getId()+" _vector size============="+dataHash.size());
        									allslahash.put(slaconfig.getId()+"", dataHash);
        									ShareData.getSlaHash().put(slaconfig.getId()+"", dataHash);
        								}
        								
        							}
        							
        						}
        					}
            			}
            			
    					
            			

            		}catch(Exception e){
            			e.printStackTrace();
            		}
        }catch(Exception exc){
        	
        }
        return allslahash;
	}
	
	/**
	 * 创建告警数据
	 * @author nielin
	 * @param vo
	 * @param collectingData
	 */
	public void updateData(Object vo , Object collectingData){
		HostNode node = (HostNode)vo;
		Host host = (Host)PollingEngine.getInstance().getNodeByID(node.getId());
		Hashtable datahashtable = (Hashtable)collectingData;
		List jobList = (List)datahashtable.get("Jobs");
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_HOST, "as400");
		for(int i = 0 ; i < list.size() ; i ++){
			AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(i);
			String indicators = alarmIndicatorsNode.getName();
			CheckEventUtil checkEventUtil = new CheckEventUtil();
			if("diskperc".equals(indicators)){
				//磁盘利用率
//				SysLogger.info("### 开始检测磁盘是否告警 ###");
				Vector diskVector = new Vector();
				if(datahashtable.get("disk") != null)diskVector = (Vector)datahashtable.get("disk");
				if(diskVector == null)diskVector = new Vector();
				checkEventUtil.checkDisk(host, diskVector, alarmIndicatorsNode);
			}else if("jobs".equals(indicators)){
				List jobForAS400EventList = checkEventUtil.createJobForAS400GroupEventList(node.getIpAddress() , jobList , alarmIndicatorsNode);
			}else {
				checkEventUtil.updateData(vo, collectingData, AlarmConstant.TYPE_HOST, "as400", alarmIndicatorsNode);
			}
		}
	}
}
