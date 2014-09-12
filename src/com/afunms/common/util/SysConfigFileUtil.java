/**
 * <p>Description:utility class,includes some methods which are often used</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.util;

import java.text.ParseException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.afunms.config.dao.NetNodeCfgFileDao;
import com.afunms.config.model.*;
import com.afunms.common.base.BaseVo;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SysConfigFileUtil
{
   public SysConfigFileUtil()
   {	   
   }
	public void getH3cConfig(String ipaddress,String readcommunity,String writecommunity,int version,int operatetype,String tftpserver){
		Random random = new Random(System. currentTimeMillis()); 
		//SysLogger.info("开始获取"+ipaddress+"的配置文件....");
		int k = random.nextInt();
		String opt = Math.abs(k % 1000)+""; 
		Calendar cal = Calendar.getInstance();
		DateE datee = new DateE();
		String datestr = datee.getDateDetail(cal);
		SysLogger.info(datestr);
		datestr = datestr.replaceAll("-", "");
		datestr = datestr.replaceAll(" ", "");
		datestr = datestr.replaceAll(":", "");
		String filename = "";
		try{

			OID hh3cCfgOperateType = new OID(".1.3.6.1.4.1.2011.10.2.4.1.2.4.1.2."+opt);//3 running2Net 6 startup2Net
			OID hh3cCfgOperateProtocol = new OID(".1.3.6.1.4.1.2011.10.2.4.1.2.4.1.3."+opt);//1 FTP 2 TFTP
			OID hh3cCfgOperateFileName = new OID(".1.3.6.1.4.1.2011.10.2.4.1.2.4.1.4."+opt);//
			OID hh3cCfgOperateServerAddress = new OID("1.3.6.1.4.1.2011.10.2.4.1.2.4.1.5."+opt);//10.10.152.30
			OID hh3cCfgOperateRowStatus = new OID(".1.3.6.1.4.1.2011.10.2.4.1.2.4.1.9."+opt);//4
			Integer32 sourceOperateType = new Integer32(operatetype);//3:running2Net
			Integer32 protocolValue = new Integer32(2); //tftp(2)
			if(operatetype == 3){
				filename = datestr+"-runningcfg.cfg";
			}else if(operatetype == 6){
				filename = datestr+"-startupcfg.cfg";
			}else{
				return;
			}
			OctetString fileName = new OctetString(filename);
			IpAddress serverAddress = new IpAddress(tftpserver);
			Integer32 cfgRowStatus1 = new Integer32(4);//4:createAndGo
        	
        	VariableBinding[] values = new VariableBinding[5];
        	values[0] = new VariableBinding(hh3cCfgOperateType,sourceOperateType);//destroy
        	values[1] = new VariableBinding(hh3cCfgOperateProtocol,protocolValue);      	
        	values[2] = new VariableBinding(hh3cCfgOperateFileName,fileName);
        	values[3] = new VariableBinding(hh3cCfgOperateServerAddress,serverAddress);        	
        	values[4] = new VariableBinding(hh3cCfgOperateRowStatus,cfgRowStatus1); 	
        	TransportMapping transport = new DefaultUdpTransportMapping();

        	Snmp snmp = new Snmp(transport);
        	
    		CommunityTarget target = new CommunityTarget();
    		target.setCommunity(new OctetString(writecommunity));
    		target.setVersion(version);
 		
    		target.setAddress(GenericAddress.parse(ipaddress + "/" + new Integer(161))); 
    		target.setRetries(1);
    		target.setTimeout(5000);

    		PDU pdu = new PDU();
    		for(int i=0;i<values.length;i++)
    		{
    		    pdu.add(values[i]);
    		}    		
    		pdu.setType(PDU.SET);
    		ResponseEvent response = snmp.send(pdu, target);  
    		
    		SnmpUtil resultsnmp = new SnmpUtil(version);
    		List rlist = resultsnmp.getH3cConfigResultTable(ipaddress, readcommunity);
    		if(rlist != null && rlist.size()>0){
    			for(int i=0;i<rlist.size();i++){
    				List plist = (List)rlist.get(i);
    				if(plist != null && plist.size()>0){
						System.out.println(plist.get(0)+"########");
    					System.out.println(plist.get(1)+"########");       					
    					System.out.println(opt+"========"+(String)plist.get(0));
    					if((opt+"").equalsIgnoreCase((String)plist.get(0))){
    						SysLogger.info(opt+"的INDEX设置成功,现在清除该INDEX...");
        					System.out.println(plist.get(0)+"-----");
        					System.out.println(plist.get(1)+"-----");  
        					//关闭         					
        					VariableBinding[] setvalues = new VariableBinding[1];
        					setvalues[0] = new VariableBinding(new OID(".1.3.6.1.4.1.2011.10.2.4.1.2.4.1.9."+plist.get(0)),new Integer32(6));//destroy
        	            	pdu = new PDU();
        	        		for(int m=0;m<setvalues.length;m++)
        	        		{
        	        		    pdu.add(setvalues[m]);
        	        		}    		
        	        		pdu.setType(PDU.SET);
        	        		response = snmp.send(pdu, target);
        	        		NetNodeCfgFile cfgfile = new NetNodeCfgFile();
        	        		cfgfile.setIpaddress(ipaddress);
        	        		cfgfile.setName(filename);
        	        		cfgfile.setRecordtime(Calendar.getInstance());
        	        		NetNodeCfgFileDao cfgdao = new NetNodeCfgFileDao();
        	        		cfgdao.save(cfgfile);
    					}
    				}
    			}
    		}
    		
		}catch(Exception ex){
			ex.printStackTrace();
		}		
	}
	
	public void getCiscoConfig(String ipaddress,String readcommunity,String writecommunity,int version,int operatetype,String tftpserver){
		Random random = new Random(System. currentTimeMillis()); 
		int k = random.nextInt();
		String opt = Math.abs(k % 1000)+""; 
		Calendar cal = Calendar.getInstance();
		DateE datee = new DateE();
		String datestr = datee.getDateDetail(cal);
		//SysLogger.info(datestr);
		datestr = datestr.replaceAll("-", "");
		datestr = datestr.replaceAll(" ", "");
		datestr = datestr.replaceAll(":", "");
		String filename = "";
		try{

			OID ccCopyEntryRowStatus = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.14."+opt);
        	OID ccCopyProtocol = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.2."+opt);
        	OID ccCopySourceFileType = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.3."+opt);
        	OID ccCopyDestFileType = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.4."+opt);
        	OID ccCopyServerAddress = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.5."+opt);
        	OID ccCopyFileName = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.6."+opt);
        	
        	//Integer32 sourceFileType = new Integer32(4);
        	Integer32 protocolValue = new Integer32(1);
        	Integer32 entryRowStatus = new Integer32(4);
        	Integer32 destFileType = new Integer32(1);
        	IpAddress serverAddress = new IpAddress(tftpserver);
        	
        	
			Integer32 sourceFileType = new Integer32(operatetype);//3:running2Net
			if(operatetype == 4){
				filename = datestr+"-runningcfg.cfg";
			}else if(operatetype == 3){
				filename = datestr+"-startupcfg.cfg";
			}else{
				return;
			}
			OctetString fileName = new OctetString(filename);
			VariableBinding[] values = new VariableBinding[6];
        	values[0] = new VariableBinding(ccCopyProtocol,protocolValue);
        	values[1] = new VariableBinding(ccCopySourceFileType,sourceFileType);
        	values[2] = new VariableBinding(ccCopyDestFileType,destFileType);
        	values[3] = new VariableBinding(ccCopyServerAddress,serverAddress);
        	values[4] = new VariableBinding(ccCopyFileName,fileName);
        	values[5] = new VariableBinding(ccCopyEntryRowStatus,entryRowStatus);
 	
        	TransportMapping transport = new DefaultUdpTransportMapping();

        	Snmp snmp = new Snmp(transport);
        	
    		CommunityTarget target = new CommunityTarget();
    		target.setCommunity(new OctetString(writecommunity));
    		target.setVersion(version);
 		
    		target.setAddress(GenericAddress.parse(ipaddress + "/" + new Integer(161))); 
    		target.setRetries(1);
    		target.setTimeout(5000);

    		PDU pdu = new PDU();
    		for(int i=0;i<values.length;i++)
    		{
    		    pdu.add(values[i]);
    		}    		
    		pdu.setType(PDU.SET);
    		ResponseEvent response = snmp.send(pdu, target);  
    		
    		SnmpUtil resultsnmp = new SnmpUtil(version);
    		List rlist = resultsnmp.getCiscoConfigResultTable(ipaddress, readcommunity);
    		if(rlist != null && rlist.size()>0){
    			for(int i=0;i<rlist.size();i++){
    				List plist = (List)rlist.get(i);
    				if(plist != null && plist.size()>0){
						System.out.println(plist.get(0)+"########");
    					System.out.println(plist.get(1)+"########");       					
    					System.out.println(opt+"========"+(String)plist.get(1));
    					if((opt+"").equalsIgnoreCase((String)plist.get(1))){   						
        					if(plist.get(0) != null && plist.get(0).equals("3")){
        						SysLogger.info("...成功获取"+ipaddress+"的配置文件"+filename+",开始保存...");
        						NetNodeCfgFile cfgfile = new NetNodeCfgFile();
        						cfgfile.setIpaddress(ipaddress);
        						cfgfile.setName(filename);
        						cfgfile.setRecordtime(Calendar.getInstance());
        						NetNodeCfgFileDao cfgdao = new NetNodeCfgFileDao();
        						cfgdao.save(cfgfile);
        						SysLogger.info("保存"+ipaddress+"的配置文件"+filename+"成功");
        					}
    					}
    				}
    			}
    		}
    		
    		
		}catch(Exception ex){
			ex.printStackTrace();
		}		
	}
	
	  public boolean deleteArp(String ipaddress,String writecommunity,int version,String mac){
			try{
				
	        	OID ccMac = new OID(mac);
	        	//SysLogger.info("oid==="+mac+"===="+writecommunity+"----"+ipaddress+"==version=="+version);
	        	Integer32 macTypeValue = new Integer32(2);//delete
	        	org.snmp4j.smi.IpAddress serverAddress = new org.snmp4j.smi.IpAddress(ipaddress);
	        	//OctetString fileName = new OctetString(allipstr);
	        	
	        	VariableBinding[] values = new VariableBinding[1];
	        	values[0] = new VariableBinding(ccMac,macTypeValue);
	        	
	        	TransportMapping transport = new DefaultUdpTransportMapping();

	        	Snmp snmp = new Snmp(transport);
	        	
	    		CommunityTarget target = new CommunityTarget();
	    		target.setCommunity(new OctetString(writecommunity));
	    		target.setVersion(version);
	 		
	    		target.setAddress(GenericAddress.parse(ipaddress + "/" + new Integer(161))); 
	    		target.setRetries(1);
	    		target.setTimeout(5000);

	    		PDU pdu = new PDU();
	    		//pdu.clear();
	    		for(int k=0;k<values.length;k++)
	    		{
	    		    pdu.add(values[k]);
	    		}    		
	    		pdu.setType(PDU.SET);
	    		ResponseEvent response = snmp.send(pdu, target);
	    		//if(response != null)
	    		//SysLogger.info(response.getResponse().getErrorStatus()+"======");
		
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return true;
	  }
	  public boolean setSingleMibValue(String ipaddress,String writecommunity,int version,String oid,int intvalue){
			try{
				
	        	OID ccOid = new OID(oid);
	        	Integer32 theValue = new Integer32(intvalue);//delete
	        	org.snmp4j.smi.IpAddress serverAddress = new org.snmp4j.smi.IpAddress(ipaddress);
	        	//OctetString fileName = new OctetString(allipstr);
	        	
	        	VariableBinding[] values = new VariableBinding[1];
	        	values[0] = new VariableBinding(ccOid,theValue);
	        	
	        	TransportMapping transport = new DefaultUdpTransportMapping();

	        	Snmp snmp = new Snmp(transport);
	        	
	    		CommunityTarget target = new CommunityTarget();
	    		target.setCommunity(new OctetString(writecommunity));
	    		target.setVersion(version);
	 		
	    		target.setAddress(GenericAddress.parse(ipaddress + "/" + new Integer(161))); 
	    		target.setRetries(1);
	    		target.setTimeout(5000);

	    		PDU pdu = new PDU();
	    		//pdu.clear();
	    		for(int k=0;k<values.length;k++)
	    		{
	    		    pdu.add(values[k]);
	    		}    		
	    		pdu.setType(PDU.SET);
	    		ResponseEvent response = snmp.send(pdu, target);
	    		//SysLogger.info(response.getResponse().getErrorStatus()+"======");
		
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return true;
	  }	
	  
	  public boolean setMultiMibValue(String ipaddress,String writecommunity,int version,String[] oids,int[] intvalues){
			try{
				
				if(oids != null && oids.length>0 && intvalues != null && intvalues.length>0 && oids.length==intvalues.length){
					VariableBinding[] values = new VariableBinding[oids.length];
					for(int i=0;i<oids.length;i++){
						//OID ccOid = new OID(oids[i]);
						//Integer32 theValue = new Integer32(intvalues[i]);//delete
						//SysLogger.info(oids[i]+"==="+intvalues[i]+"==="+writecommunity+"==="+version);
						values[i] = new VariableBinding(new OID(oids[i]),new Integer32(intvalues[i]));
						TransportMapping transport = new DefaultUdpTransportMapping();

			        	Snmp snmp = new Snmp(transport);
			        	
			    		CommunityTarget target = new CommunityTarget();
			    		target.setCommunity(new OctetString(writecommunity));
			    		target.setVersion(version);
			 		
			    		target.setAddress(GenericAddress.parse(ipaddress + "/" + new Integer(161))); 
			    		target.setRetries(3);
			    		target.setTimeout(5000);

			    		PDU pdu = new PDU();
			    		//pdu.clear();
			    		//for(int k=0;k<values.length;k++)
			    		//{
			    		    pdu.add(values[i]);
			    		//}    		
			    		pdu.setType(PDU.SET);
			    		ResponseEvent response = snmp.send(pdu, target);
					}
					/*
					TransportMapping transport = new DefaultUdpTransportMapping();

		        	Snmp snmp = new Snmp(transport);
		        	
		    		CommunityTarget target = new CommunityTarget();
		    		target.setCommunity(new OctetString(writecommunity));
		    		target.setVersion(version);
		 		
		    		target.setAddress(GenericAddress.parse(ipaddress + "/" + new Integer(161))); 
		    		target.setRetries(3);
		    		target.setTimeout(3000000);

		    		PDU pdu = new PDU();
		    		//pdu.clear();
		    		for(int k=0;k<values.length;k++)
		    		{
		    		    pdu.add(values[k]);
		    		}    		
		    		pdu.setType(PDU.SET);
		    		ResponseEvent response = snmp.send(pdu, target);
		    		*/
				}else
					return false;
		
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return true;
	  }
}
