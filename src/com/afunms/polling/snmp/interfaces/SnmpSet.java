package com.afunms.polling.snmp.interfaces;

import java.io.IOException;
import java.net.InetAddress;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;

import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.VariableBinding;

public class SnmpSet {

	String ip = null;
	String comm = null;
	int port = 161;
	int timeOut = 5000;
	String oid = null; // ��ô�ӣ�
	int action = 1;
	boolean returnbool = false;

	public SnmpSet(String ip, String com, String oid,int action) {
		this.ip = ip;
		this.comm = com;
		this.oid = oid;
		this.action=action;
	}

	public void snmpSetPort() {

		try {
			TransportMapping transport = new DefaultUdpTransportMapping();
			Snmp snmp = new Snmp(transport);
			transport.listen();
			snmp.listen();
			CommunityTarget target = new CommunityTarget();

			target.setRetries(3);
			target.setAddress((Address) (new UdpAddress(InetAddress
					.getByName(ip), port))); // 设置地址信息
			target.setCommunity(new OctetString(comm));
			target.setVersion(org.snmp4j.mp.SnmpConstants.version2c);
			target.setTimeout(timeOut);

			PDU pdu = new PDU();
			pdu.clear();
			pdu.setType(PDU.SET);

			pdu.add(new VariableBinding(new OID(oid), new Integer32(action)));

			ResponseEvent response = snmp.send(pdu, target);
			System.out.println(response.getResponse());
				/*if (response.getResponse().getErrorStatus() == 0) // set�ɹ�
				{
					returnbool = true;
				} */
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		//return returnbool;
	}
}
