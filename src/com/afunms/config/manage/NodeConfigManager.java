/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.config.manage;

import java.io.IOException;
import java.util.List;
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
import org.snmp4j.smi.SMIConstants;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.discovery.RepairLink;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.IfEntity;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.dao.RepairLinkDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.Link;
import com.afunms.topology.util.XmlOperator;

public class NodeConfigManager extends BaseManager implements ManagerInterface
{
	private String list()
	{
		LinkDao dao = new LinkDao();
		setTarget("/topology/network/link_list.jsp");
		return list(dao);
	}
	
    private String readyAdd()
    {
    	HostNodeDao dao = new HostNodeDao();
    	List list = dao.loadNetwork(0);
    	
    	int startId = getParaIntValue("start_id");
    	int endId = getParaIntValue("end_id");
    	String startIndex = getParaValue("start_index");
    	String endIndex = getParaValue("end_index");
    	
    	if(startId==-1)
    	{
    		HostNode node = (HostNode)list.get(0);
    		startId = node.getId();
    		endId = node.getId();
    		startIndex = "";
    		endIndex = "";
    	}
    	Host host1 = (Host)PollingEngine.getInstance().getNodeByID(startId);
    	Host host2 = (Host)PollingEngine.getInstance().getNodeByID(endId);
    	
    	request.setAttribute("start_if",host1.getInterfaceHash().values().iterator());
    	request.setAttribute("end_if",host2.getInterfaceHash().values().iterator());
    	request.setAttribute("start_id",new Integer(startId));
    	request.setAttribute("end_id",new Integer(endId));
    	request.setAttribute("start_index",startIndex);
    	request.setAttribute("end_index",endIndex);    	
	    request.setAttribute("list",list);	
	    	    
        return "/topology/network/link_add.jsp";
    }
    
    private String readyEdit()
    {
    	HostNodeDao dao = new HostNodeDao();
    	List list = dao.loadNetwork(0);
    	
    	String id = getParaValue("radio");
    	
	    String startIndex = getParaValue("start_index");
	    String endIndex = getParaValue("end_index");
	    int startId = getParaIntValue("start_id");
	    int endId = getParaIntValue("end_id");
    	
    	//更新数据库
        LinkDao linkdao = new LinkDao();
        Link link = (Link)linkdao.findByID(id);
        
        
        if(startId == -1) startId = link.getStartId();
        if(endId == -1) endId = link.getEndId();
    	if(startIndex == null) startIndex = link.getStartIndex();
    	if(endIndex == null) endIndex = link.getEndIndex();
    	
    	if(startId==-1)
    	{
    		HostNode node = (HostNode)list.get(0);
    		startId = node.getId();
    		endId = node.getId();
    		startIndex = "";
    		endIndex = "";
    	}
    	Host host1 = (Host)PollingEngine.getInstance().getNodeByID(startId);
    	Host host2 = (Host)PollingEngine.getInstance().getNodeByID(endId);
    	
    	request.setAttribute("start_if",host1.getInterfaceHash().values().iterator());
    	request.setAttribute("end_if",host2.getInterfaceHash().values().iterator());
    	request.setAttribute("start_id",new Integer(startId));
    	request.setAttribute("end_id",new Integer(endId));
    	request.setAttribute("start_index",startIndex);
    	request.setAttribute("end_index",endIndex);   
    	request.setAttribute("id",id);
	    request.setAttribute("list",list);	
	    
	    
		try{
			
		    //SnmpValueFactory svf = new Snmp4jValueFactory();
			byte[] bDestIP = new byte[4];
			bDestIP[0] = (byte) 0xC0;
			bDestIP[1] = (byte) 0xA8;
			bDestIP[2] = (byte) 0x01;
			bDestIP[3] = (byte) 0xCA;
			//OctetString iphex = new OctetString(bDestIP);
			//svf.getOctetString(bDestIP);
			//System.out.println(svf.getValue(SMIConstants.SYNTAX_OCTET_STRING, bDestIP)+"----");
			
        	//OID ccCopyEntryRowStatus = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.14.333");
			OID pingCtlTargetAddressType = new OID(".1.3.6.1.2.1.80.1.2.1.3.1.1.1.1");
        	OID pingCtlTargetAddress = new OID(".1.3.6.1.2.1.80.1.2.1.4.1.1.1.1");
        	OID pingCtlFrequency = new OID(".1.3.6.1.2.1.80.1.2.1.10.1.1.1.1");
        	OID pingCtlRowStatus = new OID(".1.3.6.1.2.1.80.1.2.1.23.1.1.1.1");
        	
        	Integer32 AddressTypeValue = new Integer32(1);//ipv4(1)
        	OctetString targetAddress = new OctetString("10.10.1.1");//remote ip
        	Integer32 FrequencyValue = new Integer32(10);      	
        	Integer32 RowStatus = new Integer32(4);//	
        	Integer32 RowStatusDestory = new Integer32(6);//

        	
        	VariableBinding[] values = new VariableBinding[5];
        	
        	values[0] = new VariableBinding(pingCtlRowStatus,RowStatusDestory);
        	values[1] = new VariableBinding(pingCtlTargetAddressType,AddressTypeValue);//destroy
        	values[2] = new VariableBinding(pingCtlTargetAddress,targetAddress);//createAndWait
        	values[3] = new VariableBinding(pingCtlFrequency,FrequencyValue);
        	values[4] = new VariableBinding(pingCtlRowStatus,RowStatus);
        	
        	TransportMapping transport = new DefaultUdpTransportMapping();

        	Snmp snmp = new Snmp(transport);
        	
    		CommunityTarget target = new CommunityTarget();
    		target.setCommunity(new OctetString("dhcc_private"));
    		target.setVersion(org.snmp4j.mp.SnmpConstants.version2c);
 		
    		target.setAddress(GenericAddress.parse("10.10.1.2" + "/" + new Integer(161))); 
    		target.setRetries(1);
    		target.setTimeout(5000);

    		PDU pdu = new PDU();
    		for(int i=0;i<values.length;i++)
    		{
    		    pdu.add(values[i]);
    		}    		
    		pdu.setType(PDU.SET);
    		ResponseEvent response = snmp.send(pdu, target);  
    		
    		//判断是否设置成功
    		TransportMapping transport1 = new DefaultUdpTransportMapping();
    		Snmp snmp1 = new Snmp(transport1);
    		CommunityTarget target1 = new CommunityTarget();
    		target1.setCommunity(new OctetString("dhcc_public"));
    		target1.setVersion(org.snmp4j.mp.SnmpConstants.version2c);		
    		target1.setAddress(GenericAddress.parse("10.10.1.2" + "/" + new Integer(161))); 
    		target1.setRetries(1);
    		target1.setTimeout(5000);
    		snmp1.listen();
    		PDU request = new PDU();
    		request.add(new VariableBinding(new OID(".1.3.6.1.2.1.80.1.2.1.23.1.1.1.1")));
    		//request.add(new VariableBinding(new OID(".1.3.6.1.2.1.80.1.3.1.6.1.1.1.1")));
    		//1:waiting 2:running 3:successful (done, entry no longer write protected)
    		//4:failed (done, entry no longer write protected)
    		    		 
    		PDU response1 = null;
    		String sResponse=null;
    		try {
    			response1 = snmp1.send(request, target1).getResponse();
    			SysLogger.info(response1.toString());
    			VariableBinding recVB = (VariableBinding)response1.getVariableBindings().get(0);
    			String operat = response1.getVariableBindings().get(0).toString();
    			sResponse =
    				operat
    					.substring(operat.lastIndexOf("=") + 1, operat.length())
    					.trim();
    			SysLogger.info("AverageRTT返回值为 "+sResponse);

    			if (sResponse.equalsIgnoreCase("Null")){
    				SysLogger.info("设置返回值为 失败");
    				response1.clear();
    			}
    			
    		} catch (IOException e) {
    			throw e;
    		} catch (Exception e) {
    			e.printStackTrace();
    		} finally {
    			//snmp.close();
    			snmp1.close();
    		}
    		
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	    
	    
	    
	    /*
		try{
        	//OID ccCopyEntryRowStatus = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.14.333");
        	OID configOperationType = new OID(".1.3.6.1.4.1.2011.10.2.4.1.2.4.1.2.333");
        	//running2Startup(1),startup2Running(2),running2Net(3),net2Running(4),net2Startup(5),startup2Net(6)

        	OID cfgOperateProtocol = new OID(".1.3.6.1.4.1.25506.2.4.1.2.4.1.3.333");//ftp(1),tftp(2),clusterftp(3),clustertftp(4)
        	OID cfgOperateFileName = new OID(".1.3.6.1.4.1.25506.2.4.1.2.4.1.4.333");//"testh3c"
        	OID cfgOperateServerAddress = new OID(".1.3.6.1.4.1.25506.2.4.1.2.4.1.5.333");//"10.10.152.30"
        	OID cfgOperateUserName = new OID(".1.3.6.1.4.1.25506.2.4.1.2.4.1.6.333");//"hukelei"
        	OID cfgOperateUserPassword = new OID(".1.3.6.1.4.1.25506.2.4.1.2.4.1.7.333");//"hukelei"
        	OID cfgOperateRowStatus = new OID(".1.3.6.1.4.1.25506.2.4.1.2.4.1.9.333"); //1
        	
        	Integer32 OperationTypeValue = new Integer32(1);//running2Net,net2Running,net2Startup,startup2net
        	Integer32 protocolValue = new Integer32(2); //tftp(1)
        	OctetString OperateFileName = new OctetString("test");
        	IpAddress serverAddress = new IpAddress("10.10.152.30");
        	OctetString userName = new OctetString("hukelei");
        	OctetString passwordValue = new OctetString("hukelei");       	
        	Integer32 entryRowStatus = new Integer32(1);//	

        	
        	VariableBinding[] values = new VariableBinding[7];
        	
        	values[0] = new VariableBinding(configOperationType,OperationTypeValue);//destroy
        	values[1] = new VariableBinding(cfgOperateProtocol,protocolValue);//createAndWait
        	values[2] = new VariableBinding(cfgOperateFileName,OperateFileName);
        	values[3] = new VariableBinding(cfgOperateServerAddress,serverAddress);
        	values[4] = new VariableBinding(cfgOperateUserName,userName);       	
        	values[5] = new VariableBinding(cfgOperateUserPassword,passwordValue);
        	values[6] = new VariableBinding(cfgOperateRowStatus,entryRowStatus);        	
 	
        	
        	TransportMapping transport = new DefaultUdpTransportMapping();

        	Snmp snmp = new Snmp(transport);
        	
    		CommunityTarget target = new CommunityTarget();
    		target.setCommunity(new OctetString("dhcc_private"));
    		target.setVersion(org.snmp4j.mp.SnmpConstants.version1);
 		
    		target.setAddress(GenericAddress.parse("10.10.1.2" + "/" + new Integer(161))); 
    		target.setRetries(1);
    		target.setTimeout(5000);

    		PDU pdu = new PDU();
    		for(int i=0;i<values.length;i++)
    		{
    		    pdu.add(values[i]);
    		}    		
    		pdu.setType(PDU.SET);
    		ResponseEvent response = snmp.send(pdu, target);      
		}catch(Exception ex){
			ex.printStackTrace();
		}
		*/
		
		try{
        	OID ccCopyEntryRowStatus = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.14.333");
        	OID ccCopyProtocol = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.2.333");
        	OID ccCopySourceFileType = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.3.333");
        	OID ccCopyDestFileType = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.4.333");
        	OID ccCopyServerAddress = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.5.333");
        	OID ccCopyFileName = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.6.333");
        	Integer32 sourceFileType = new Integer32(4);//3:startupConfig 4:runningConfig 
        	Integer32 protocolValue = new Integer32(1); //tftp(1)
        	Integer32 entryRowStatus1 = new Integer32(6);//6:destroy
        	Integer32 entryRowStatus2 = new Integer32(5);//5:createAndWait	
        	Integer32 destFileType = new Integer32(1);//3:startupConfig 4:runningConfig
        	IpAddress serverAddress = new IpAddress("10.10.152.30");
        	OctetString fileName = new OctetString("test.cfg");
        	Integer32 entryRowStatus = new Integer32(4);//1:active 
        	
        	VariableBinding[] values = new VariableBinding[8];
        	
        	values[0] = new VariableBinding(ccCopyEntryRowStatus,entryRowStatus1);//destroy
        	values[1] = new VariableBinding(ccCopyEntryRowStatus,entryRowStatus2);//createAndWait
        	values[2] = new VariableBinding(ccCopyProtocol,protocolValue);
        	values[3] = new VariableBinding(ccCopySourceFileType,sourceFileType);
        	values[4] = new VariableBinding(ccCopyDestFileType,destFileType);       	
        	values[5] = new VariableBinding(ccCopyServerAddress,serverAddress);
        	values[6] = new VariableBinding(ccCopyFileName,fileName);        	
        	values[7] = new VariableBinding(ccCopyEntryRowStatus,entryRowStatus); 	
        	
        	TransportMapping transport = new DefaultUdpTransportMapping();

        	Snmp snmp = new Snmp(transport);
        	
    		CommunityTarget target = new CommunityTarget();
    		target.setCommunity(new OctetString("dhcc"));
    		target.setVersion(org.snmp4j.mp.SnmpConstants.version1);
 		
    		target.setAddress(GenericAddress.parse("10.10.151.240" + "/" + new Integer(161))); 
    		target.setRetries(1);
    		target.setTimeout(5000);

    		PDU pdu = new PDU();
    		for(int i=0;i<values.length;i++)
    		{
    		    pdu.add(values[i]);
    		}    		
    		pdu.setType(PDU.SET);
    		ResponseEvent response = snmp.send(pdu, target);  
    		
    		//判断是否设置成功
    		TransportMapping transport1 = new DefaultUdpTransportMapping();
    		Snmp snmp1 = new Snmp(transport1);
    		CommunityTarget target1 = new CommunityTarget();
    		target1.setCommunity(new OctetString("public"));
    		target1.setVersion(org.snmp4j.mp.SnmpConstants.version1);		
    		target1.setAddress(GenericAddress.parse("10.10.151.240" + "/" + new Integer(161))); 
    		target1.setRetries(1);
    		target1.setTimeout(5000);
    		snmp1.listen();
    		PDU request = new PDU();
    		request.add(new VariableBinding(new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.10.333")));
    		//1:waiting 2:running 3:successful (done, entry no longer write protected)
    		//4:failed (done, entry no longer write protected)
    		    		 
    		PDU response1 = null;
    		String sResponse=null;
    		try {
    			response1 = snmp1.send(request, target1).getResponse();
    			
    			VariableBinding recVB = (VariableBinding)response1.getVariableBindings().get(0);
    			String operat = response1.getVariableBindings().get(0).toString();
    			sResponse =
    				operat
    					.substring(operat.lastIndexOf("=") + 1, operat.length())
    					.trim();
    			SysLogger.info("设置返回值为 "+sResponse);
    			if("1".equalsIgnoreCase(sResponse)){
    				SysLogger.info("设置返回值为 等待...");
    			}else if("2".equalsIgnoreCase(sResponse)){
    				SysLogger.info("设置返回值为 运行...");
    			}else if("3".equalsIgnoreCase(sResponse)){
    				SysLogger.info("设置返回值为 成功");
    			}else if("4".equalsIgnoreCase(sResponse)){
    				SysLogger.info("设置返回值为 失败");
    			}
    			response1.clear();
    			if (sResponse.equalsIgnoreCase("Null")){
    				SysLogger.info("设置返回值为 失败");
    				response1.clear();
    			}
    			
    		} catch (IOException e) {
    			throw e;
    		} catch (Exception e) {
    			e.printStackTrace();
    		} finally {
    			//snmp.close();
    			snmp1.close();
    		}
        	
    		try{
        	values = null;
        	values = new VariableBinding[1];
        	values[0] = new VariableBinding(ccCopyEntryRowStatus,entryRowStatus1);//destroy	
        	transport.close();
        	transport = null;
        	transport = new DefaultUdpTransportMapping();
        	snmp.close();
        	snmp = null;
        	snmp = new Snmp(transport);
        	target = null;
    		target = new CommunityTarget();
    		target.setCommunity(new OctetString("dhcc"));
    		target.setVersion(org.snmp4j.mp.SnmpConstants.version1);		
    		target.setAddress(GenericAddress.parse("10.10.151.240" + "/" + new Integer(161))); 
    		target.setRetries(1);
    		target.setTimeout(5000);

    		pdu.clear();
    		pdu = new PDU();
    		for(int i=0;i<values.length;i++)
    		{
    		    pdu.add(values[i]);
    		}    		
    		pdu.setType(PDU.SET);
    		response = null;
    		response = snmp.send(pdu, target);  
    		}catch(Exception e){
    			e.printStackTrace();
    		} finally {
    			transport.close();
    			transport = null;
    			snmp.close();
    			snmp = null;
    			target = null;
    		}
    		
		}catch(Exception ex){
			ex.printStackTrace();
		}
	    
	   /* 
		try{
			
        	OID ccCopyEntryRowStatus = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.14.888");
        	OID ccCopyProtocol = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.2.888");
        	OID ccCopySourceFileType = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.3.888");
        	OID ccCopyDestFileType = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.4.888");
        	OID ccCopyServerAddress = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.5.888");
        	OID ccCopyFileName = new OID(".1.3.6.1.4.1.9.9.96.1.1.1.1.6.888");
        	Integer32 sourceFileType = new Integer32(4);
        	Integer32 protocolValue = new Integer32(1);
        	Integer32 entryRowStatus = new Integer32(4);
        	Integer32 destFileType = new Integer32(1);
        	IpAddress serverAddress = new IpAddress("10.10.152.30");
        	OctetString fileName = new OctetString("test1");
        	
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
    		target.setCommunity(new OctetString("dhcc"));
    		target.setVersion(0);
 		
    		target.setAddress(GenericAddress.parse("10.10.151.240" + "/" + new Integer(161))); 
    		target.setRetries(1);
    		target.setTimeout(5000);

    		PDU pdu = new PDU();
    		//pdu.clear();
    		for(int i=0;i<values.length;i++)
    		{
    		    pdu.add(values[i]);
    		}    		
    		pdu.setType(PDU.SET);
    		ResponseEvent response = snmp.send(pdu, target);
    		    		
System.out.println("-----------------------------");			
    		if (response.getResponse() == null)
    		{
System.out.println("Timeout");
    			//如果设置超时，那么应该将所有的值设定为失败
    			//return false;
    		} else
    		{
    			if (response.getResponse().getErrorStatus() == 0) // set操作成功
    			{
System.out.println("设置成功");
//    				return new String[]{"true"};
    				
    			} else if(response.getResponse().getErrorStatus() == 3){
System.err.println("Error : badvalue !!!!@setPublicValue over snmp stack");
    			}else
    			{
System.out.println(response.getResponse().getErrorStatusText()+":err INdex"+response.getResponse().getErrorIndex());
    				
    			}
    		}    		
    		
    		
		}catch(Exception ex){
				ex.printStackTrace();
		}
	    
	    */
	    
	    
	    //String remoteHexIp1 = CommonUtil.demoChangeStringToHex("219.150.128.1");
	    
	    //SnmpValueFactory svf = new Snmp4JValueFactory();

	    
	    String remoteHexIp = CommonUtil.demoChangeStringToHex("192.168.1.202");


	    /*
	    
		//实现REMOTE PING功能
		try{
			//OID sysname = new OID(".1.3.6.1.2.1.1.5.0");//6
			OID pingCtlEntryStatus = new OID(".1.3.6.1.4.1.9.9.16.1.1.1.16.888");//6
			OID pingCtlEntryStatus1 = new OID(".1.3.6.1.4.1.9.9.16.1.1.1.16.888");//5
			OID pingCtlOwnerIndex = new OID(".1.3.6.1.4.1.9.9.16.1.1.1.15.888");//hukelei
			OID pingCtlRemoteip = new OID(".1.3.6.1.4.1.9.9.16.1.1.1.3.888");//remote ip
			
			//OID pingCtlEntryStatus2 = new OID(".1.3.6.1.4.1.9.9.16.1.1.1.16.888");//1
			
			//OID sysname = new OID(".1.3.6.1.2.1.1.5.0");
			
			
			Integer32 entryStatusValue = new Integer32(6);
			Integer32 entryStatusValue1 = new Integer32(5);
        	OctetString Owner = new OctetString("hukelei");
        	OctetString serverAddress = new OctetString(remoteHexIp.toUpperCase());
        	Integer32 entryStatusValue2 = new Integer32(4);
        	
        	
        	OctetString sysnameValue = new OctetString("hukelei");

        	
        	VariableBinding[] values = new VariableBinding[4];
        	//values[0] = new VariableBinding(sysname,Owner);
        	values[0] = new VariableBinding(pingCtlEntryStatus,entryStatusValue);
        	values[1] = new VariableBinding(pingCtlEntryStatus1,entryStatusValue1);
        	values[2] = new VariableBinding(pingCtlOwnerIndex,Owner);
        	values[3] = new VariableBinding(pingCtlRemoteip,iphex);
        	//values[4] = new VariableBinding(pingCtlEntryStatus2,entryStatusValue2);
        	
        	//values[3] = new VariableBinding(pingCtlEntryStatus2,entryStatusValue2);
        	
        	//values[0] = new VariableBinding(sysname,sysnameValue);
        	
        	TransportMapping transport = new DefaultUdpTransportMapping();

        	Snmp snmp = new Snmp(transport);
        	
    		CommunityTarget target = new CommunityTarget();
    		target.setCommunity(new OctetString("dhcc"));
    		target.setVersion(org.snmp4j.mp.SnmpConstants.version1);
 		
    		target.setAddress(GenericAddress.parse("10.10.151.240" + "/" + new Integer(161))); 
    		target.setRetries(1);
    		target.setTimeout(5000);

    		PDU pdu = new PDU();
    		//pdu.clear();
    		for(int i=0;i<values.length;i++)
    		{
    		    pdu.add(values[i]);
    		}    		
    		pdu.setType(PDU.SET);
    		ResponseEvent response = null;
    		try{
    			response = snmp.send(pdu, target);
    		}catch(Exception e){
    			e.printStackTrace();
    		}    		    		
System.out.println("-----------------------------"+response.getError());			
    		if (response.getResponse() == null)
    		{
System.out.println("Timeout");
    			//如果设置超时，那么应该将所有的值设定为失败
    			//return false;
    		} else
    		{
    			if (response.getResponse().getErrorStatus() == 0) // set操作成功
    			{
System.out.println("设置成功");
    				
    			} else if(response.getResponse().getErrorStatus() == 3){
System.err.println("Error : badvalue !!!!@setPublicValue over snmp stack");
    			}else
    			{
System.out.println(response.getResponse().getErrorStatusText()+":err INdex"+response.getResponse().getErrorIndex());
    				
    			}
    		}
    		
    		TransportMapping transport1 = new DefaultUdpTransportMapping();
    		Snmp snmp1 = new Snmp(transport1);
    		CommunityTarget target1 = new CommunityTarget();
    		target1.setCommunity(new OctetString("public"));
    		target1.setVersion(org.snmp4j.mp.SnmpConstants.version1);
 		
    		target1.setAddress(GenericAddress.parse("10.10.151.240" + "/" + new Integer(161))); 
    		target1.setRetries(1);
    		target1.setTimeout(5000);
    		snmp1.listen();
    		PDU request = new PDU();
    		request.add(new VariableBinding(new OID(".1.3.6.1.4.1.9.9.16.1.1.1.16.888")));
    		//request.add(new VariableBinding(new OID(".1.3.6.1.2.1.1.5.0")));
    		PDU response1 = null;
    		String sResponse=null;
    		try {
    			response1 = snmp1.sendPDU(request, target1);
    			System.out.println("list "+response1.toString());
    			
    			VariableBinding recVB = (VariableBinding)response1.getVariableBindings().get(0);
    			String operat = response1.getVariableBindings().get(0).toString();
    			sResponse =
    				operat
    					.substring(operat.lastIndexOf("=") + 1, operat.length())
    					.trim();
    			System.out.println("sResponse==="+sResponse);
    			response1.clear();
    			if (sResponse.equalsIgnoreCase("Null")){
    				response1.clear();
    				return null;
    			}
    			
    		} catch (IOException e) {
    			throw e;
    		} catch (Exception e) {
    			e.printStackTrace();
    		} finally {
    			snmp.close();
    		}
		}catch(Exception ex){
			ex.printStackTrace();
	}	    
	    
	   */
	    
	    
	    
	    
	    
	    /*
	    
		//实现REMOTE PING功能
		try{
			OID pingCtlEntryStatus = new OID(".1.3.6.1.4.1.9.9.16.1.1.1.16.888");//6
			OID pingCtlEntryStatus1 = new OID(".1.3.6.1.4.1.9.9.16.1.1.1.16.888");//5
			OID pingCtlOwnerIndex = new OID(".1.3.6.1.4.1.9.9.16.1.1.1.15.888");//hukelei
			OID pingCtlProtocol = new OID(".1.3.6.1.4.1.9.9.16.1.1.1.2.888");//1
			OID pingCtlRemoteip = new OID(".1.3.6.1.4.1.9.9.16.1.1.1.3.888");//remote ip
			OID pingCtlEntryStatus2 = new OID(".1.3.6.1.4.1.9.9.16.1.1.1.16.888");//1

			Integer32 entryStatusValue = new Integer32(6);
			Integer32 entryStatusValue1 = new Integer32(5);
        	OctetString Owner = new OctetString("hukelei");
        	Integer32 protocolValue = new Integer32(1);
        	OctetString serverAddress = new OctetString(remoteHexIp.toUpperCase());
        	//IpAddress serverAddress = new IpAddress("192.168.1.202");
        	Integer32 entryStatusValue2 = new Integer32(1);
        	
        	//Integer32 frequency = new Integer32(0);
        	//Integer32 entryRowStatus = new Integer32(4);
        	
        	VariableBinding[] values = new VariableBinding[6];
        	values[0] = new VariableBinding(pingCtlEntryStatus,entryStatusValue);
        	values[1] = new VariableBinding(pingCtlEntryStatus1,entryStatusValue1);
        	values[2] = new VariableBinding(pingCtlOwnerIndex,Owner);
        	values[3] = new VariableBinding(pingCtlProtocol,protocolValue);
        	values[4] = new VariableBinding(pingCtlRemoteip,serverAddress);
        	values[5] = new VariableBinding(pingCtlEntryStatus2,entryStatusValue2);
        	
        	TransportMapping transport = new DefaultUdpTransportMapping();

        	Snmp snmp = new Snmp(transport);
        	
    		CommunityTarget target = new CommunityTarget();
    		target.setCommunity(new OctetString("netman"));
    		target.setVersion(0);
 		
    		target.setAddress(GenericAddress.parse("192.168.1.201" + "/" + new Integer(161))); 
    		target.setRetries(1);
    		target.setTimeout(5000);

    		PDU pdu = new PDU();
    		//pdu.clear();
    		for(int i=0;i<values.length;i++)
    		{
    		    pdu.add(values[i]);
    		}    		
    		pdu.setType(PDU.SET);
    		ResponseEvent response = null;
    		try{
    			response = snmp.send(pdu, target);
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		    		
System.out.println("-----------------------------");			
    		if (response.getResponse() == null)
    		{
System.out.println("Timeout");
    			//如果设置超时，那么应该将所有的值设定为失败
    			//return false;
    		} else
    		{
    			if (response.getResponse().getErrorStatus() == 0) // set操作成功
    			{
System.out.println("设置成功");
//    				return new String[]{"true"};
    				
    			} else if(response.getResponse().getErrorStatus() == 3){
System.err.println("Error : badvalue !!!!@setPublicValue over snmp stack");
    			}else
    			{
System.out.println(response.getResponse().getErrorStatusText()+":err INdex"+response.getResponse().getErrorIndex());
    				
    			}
    		}
    		//OID pingCtlOwnerIndex = new OID(".1.3.6.1.2.1.80.1.2.1.1.888");//"hukelei"
    		String[] oids =                
				  new String[] {
	  				"1.3.6.1.4.1.9.9.16.1.1.1.12.888"
					  };
    		
    		
    		
    		this.setVariableBindings(oids);
			List list=this.table(this.getDefault_community(),nethost);
			float allvalue=0.0f;
			for(int i=0;i<list.size();i++){
				TableEvent tbevent=(TableEvent)list.get(i);
				VariableBinding[] vb=tbevent.getColumns();
				float value=0.0f;
				System.out.println(vb[0].toString()+"======================AVGRTT value");
				String svb0=vb[0].toString();
				value=Float.parseFloat(svb0.substring(svb0.indexOf("=")+1,svb0.length()).trim());
				//allvalue = allvalue+value;
				//cpudata.setThevalue(Float.toString(value));								
			}
	    
		}catch(Exception ex){
			ex.printStackTrace();
	}
	 */   
	    
	    
        return "/topology/network/link_edit.jsp";
    }
    
    private String add() 
    {  	 	  
	    String startIndex = getParaValue("start_index");
	    String endIndex = getParaValue("end_index");
	    int startId = getParaIntValue("start_id");
	    int endId = getParaIntValue("end_id");
	    if(startId==endId)
	    {
	    	setErrorCode(ErrorMessage.DEVICES_SAME);
	    	return null;
	    }
	    
	    LinkDao dao = new LinkDao();
	    int exist = dao.linkExist(startId, startIndex,endId,endIndex);	    
	    if(exist==1)
	    {
	    	setErrorCode(ErrorMessage.LINK_EXIST);
	    	dao.close();
	    	return null;
	    }	
	    if(exist==2)
	    {
	    	setErrorCode(ErrorMessage.DOUBLE_LINKS);
	    	dao.close();
	    	return null;
	    }	
		Host startHost = (Host)PollingEngine.getInstance().getNodeByID(startId);
		IfEntity if1 = startHost.getIfEntityByIndex(startIndex);
		Host endHost = (Host)PollingEngine.getInstance().getNodeByID(endId);
		IfEntity if2 = endHost.getIfEntityByIndex(endIndex);
	    
	    Link link = new Link();
	    link.setStartId(startId);
	    link.setEndId(endId);
        link.setStartIndex(startIndex);
        link.setEndIndex(endIndex);
        link.setStartIp(if1.getIpAddress());
        link.setEndIp(if2.getIpAddress());
        link.setStartDescr(if1.getDescr());
        link.setEndDescr(if2.getDescr());
        link.setType(1);
	    Link newLink = dao.save(link);
	    
 	    //更新xml
	    XmlOperator opr = new XmlOperator();
	    opr.setFile("network.jsp");
	    opr.init4updateXml();
	    if(newLink.getAssistant()==0)
	       opr.addLine(String.valueOf(newLink.getId()),String.valueOf(startId),String.valueOf(endId));
	    else
	       opr.addAssistantLine(String.valueOf(newLink.getId()),String.valueOf(startId),String.valueOf(endId));	
	    opr.writeXml();
	    
		LinkRoad lr = new LinkRoad();
		lr.setId(newLink.getId());
		lr.setStartId(startId);
		if("".equals(if1.getIpAddress()))
		   lr.setStartIp(startHost.getIpAddress());
		else
		   lr.setStartIp(if1.getIpAddress());			
		lr.setStartIndex(startIndex);
		lr.setStartDescr(if1.getDescr());
		
	    if("".equals(if2.getIpAddress()))
		   lr.setEndIp(endHost.getIpAddress());
		else
		   lr.setEndIp(if2.getIpAddress());					
		lr.setEndId(endId);
		lr.setEndIp(if2.getIpAddress());
		lr.setEndIndex(endIndex);
		lr.setEndDescr(if2.getDescr());
		lr.setAssistant(newLink.getAssistant());
		PollingEngine.getInstance().getLinkList().add(lr);

        return "/link.do?action=list";
    }  
    
    private String edit() 
    {  	   	
    	String id = getParaValue("id");
	    String startIndex = getParaValue("start_index");
	    String endIndex = getParaValue("end_index");
	    int startId = getParaIntValue("start_id");
	    int endId = getParaIntValue("end_id");
	    if(startId==endId)
	    {
	    	setErrorCode(ErrorMessage.DEVICES_SAME);
	    	return null;
	    }
	    
	    LinkDao dao = new LinkDao();
	    RepairLinkDao repairdao = new RepairLinkDao();
	    Link formerLink = (Link)dao.findByID(id);
	    String formerStartIndex = formerLink.getStartIndex();
	    String formerEndIndex = formerLink.getEndIndex();

	    //需要判断原来是否已经是被修改过的连接
	    
	    
	    //对已经存在的连接进行修改,所以不需要判断是否存在
	    /*
	    int exist = dao.linkExist(startId, startIndex,endId,endIndex);	    
	    if(exist==1)
	    {
	    	setErrorCode(ErrorMessage.LINK_EXIST);
	    	dao.close();
	    	return null;
	    }	
	    if(exist==2)
	    {
	    	setErrorCode(ErrorMessage.DOUBLE_LINKS);
	    	dao.close();
	    	return null;
	    }
	    */
		Host startHost = (Host)PollingEngine.getInstance().getNodeByID(startId);
		IfEntity if1 = startHost.getIfEntityByIndex(startIndex);
		Host endHost = (Host)PollingEngine.getInstance().getNodeByID(endId);
		IfEntity if2 = endHost.getIfEntityByIndex(endIndex);
		
	    RepairLink repairLink = null;
	    repairLink = repairdao.loadLink(startHost.getIpAddress(), formerStartIndex, endHost.getIpAddress(), formerEndIndex);
		
		//formerLink.setStartId(startId);
		
	    //Link link = new Link();
	    formerLink.setStartId(startId);
	    formerLink.setEndId(endId);
	    formerLink.setStartIndex(startIndex);
	    formerLink.setEndIndex(endIndex);
	    formerLink.setStartIp(if1.getIpAddress());
	    formerLink.setEndIp(if2.getIpAddress());
	    formerLink.setStartDescr(if1.getDescr());
	    formerLink.setEndDescr(if2.getDescr());
	    formerLink.setType(1);
	    dao = new LinkDao();
	    dao.update(formerLink);
	    
	    //对新修改的连接关系进行原始备份
	    if(repairLink == null){
	    	//需要再判断该连接关系是否已经被修改过
	    	repairLink = repairdao.loadRepairLink(startHost.getIpAddress(), formerStartIndex, endHost.getIpAddress(), formerEndIndex);
	    	if(repairLink == null){
	    		//说明是第一次修改
		    	repairLink = new RepairLink();
		    	repairLink.setStartIp(startHost.getIpAddress());
		    	repairLink.setStartIndex(formerStartIndex);
		    	repairLink.setNewStartIndex(formerLink.getStartIndex());
		    	repairLink.setEndIp(endHost.getIpAddress());
		    	repairLink.setEndIndex(formerEndIndex);
		    	repairLink.setNewEndIndex(formerLink.getEndIndex());
		    	repairdao.save(repairLink);
	    	}else{
	    		//曾经被修改过
		    	repairLink.setNewStartIndex(formerLink.getStartIndex());
		    	repairLink.setNewEndIndex(formerLink.getEndIndex());
		    	System.out.println("修改连接关系!");
		    	repairdao.update(repairLink); 		
	    	}
	    }else{
	    	repairLink.setNewStartIndex(formerLink.getStartIndex());
	    	repairLink.setNewEndIndex(formerLink.getEndIndex());
	    	repairdao.update(repairLink);
	    }
	    
 	    //更新xml
	    XmlOperator opr = new XmlOperator();
	    opr.setFile("network.jsp");
	    opr.init4updateXml();
	    /*
	    if(formerLink.getAssistant()==0)
	       opr.addLine(String.valueOf(formerLink.getId()),String.valueOf(startId),String.valueOf(endId));
	    else
	       opr.addAssistantLine(String.valueOf(formerLink.getId()),String.valueOf(startId),String.valueOf(endId));	
	    */
	    opr.writeXml();
	    
		LinkRoad lr = new LinkRoad();
		lr.setId(formerLink.getId());
		lr.setStartId(startId);
		if("".equals(if1.getIpAddress()))
		   lr.setStartIp(startHost.getIpAddress());
		else
		   lr.setStartIp(if1.getIpAddress());			
		lr.setStartIndex(startIndex);
		lr.setStartDescr(if1.getDescr());
		
	    if("".equals(if2.getIpAddress()))
		   lr.setEndIp(endHost.getIpAddress());
		else
		   lr.setEndIp(if2.getIpAddress());					
		lr.setEndId(endId);
		lr.setEndIp(if2.getIpAddress());
		lr.setEndIndex(endIndex);
		lr.setEndDescr(if2.getDescr());
		lr.setAssistant(formerLink.getAssistant());
		PollingEngine.getInstance().deleteLinkByID(lr.getId());
		PollingEngine.getInstance().getLinkList().add(lr);
		
        return "/link.do?action=list";
    }
    	
    private String delete()
    {
        String id = getParaValue("radio"); 

        //更新数据库
        LinkDao dao = new LinkDao();
        dao.delete(id);
        
 	    //更新xml
        XmlOperator opr = new XmlOperator();
        opr.setFile("network.jsp");
        opr.init4updateXml();
        opr.deleteLineByID(id);
        opr.writeXml();
        
        //更新内存
        PollingEngine.getInstance().deleteLinkByID(Integer.parseInt(id));
        return "/link.do?action=list";
    }
      
   public String execute(String action)
   {	   
	  if(action.equals("list"))
	     return list();     
	  if(action.equals("delete"))
	     return delete();     
	  if(action.equals("ready_add"))
	     return readyAdd();
	  if(action.equals("ready_edit"))
		     return readyEdit();
      if(action.equals("add"))
         return add();
      if(action.equals("edit"))
          return edit();
      setErrorCode(ErrorMessage.ACTION_NO_FOUND);
      return null;
   }
}
