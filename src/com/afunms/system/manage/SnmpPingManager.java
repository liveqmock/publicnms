package com.afunms.system.manage;

import java.io.IOException;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;

public class SnmpPingManager extends BaseManager implements ManagerInterface{
	
	   private Snmp snmp = null;
       private Address targetAddress = null;
       public void initComm(String ip) throws IOException {

              // 设置Agent方的IP和端口

              targetAddress = GenericAddress.parse("udp:"+ip+"/161");

              TransportMapping transport = new DefaultUdpTransportMapping();

              snmp = new Snmp(transport);

              transport.listen();

       }
       public String sendPDU(String name,int version) throws IOException {
    	   
    	      String snmpping=null;

              // 设置 target
             
              CommunityTarget target = new CommunityTarget();

              target.setCommunity(new OctetString(name));

              target.setAddress(targetAddress);

              // 通信不成功时的重试次数

              target.setRetries(2);

              // 超时时间

              target.setTimeout(1500);

              target.setVersion(version);

              // 创建 PDU

              PDU pdu = new PDU();

              pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1, 1, 1, 0 })));

              // MIB的访问方式

              pdu.setType(PDU.GET);

              // 向Agent发送PDU，并接收Response

              ResponseEvent respEvnt = snmp.send(pdu, target);

              // 解析Response

              if (respEvnt != null && respEvnt.getResponse() != null) {
            	  snmpping="SNMP服务已启动...";
                     Vector recVBs = respEvnt.getResponse().getVariableBindings();

                     for (int i = 0; i < recVBs.size(); i++) {

                            VariableBinding recVB = (VariableBinding)recVBs.elementAt(i);

                            String snmp=recVB.getOid() + " : " + recVB.getVariable();
                     }

              }else
              {
            	  snmpping="SNMP服务未启动！";
              }
              return snmpping;
        
       }
   
       /**
        * 检测是否开启SNMP服务
        * @return
        */
	public String snmpPing()
	{  
		String ip = getParaValue("ipaddress");
		String name = getParaValue("name");
		int version = getParaIntValue("version");
		SnmpPingManager ping = new SnmpPingManager();
		try {
			ping.initComm(ip);
			String snmpping=ping.sendPDU(name,version);
			request.setAttribute("ipaddress", ip);
			request.setAttribute("name", name);
			request.setAttribute("snmpping", snmpping);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "/tool/snmppinglist.jsp?version="+version;
	}
	public String execute(String action) {
		// TODO Auto-generated method stub
		if(action.equals("ping"))
            return snmpPing();
		return null;
	}
	

}
