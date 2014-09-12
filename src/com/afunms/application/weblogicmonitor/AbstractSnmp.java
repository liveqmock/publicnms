/*
 * Created on 2005-4-10
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.application.weblogicmonitor;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableUtils;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
//public abstract class AbstractSnmp extends Thread implements PDUFactory {
public abstract class AbstractSnmp implements PDUFactory {
	private String default_community = "public";
	private Integer default_port = new Integer(163);

	//public static final int default_version = SnmpConstants.version1;
	public static final int default_version = 0;
	//public static final int default_version = SnmpConstants.version2c;
	public static final int default_retries = 1;
	private int default_timeout = 500;
	public static final int default_pduType = PDU.GET;
	public static Hashtable Interface_IfType = null;
	static {
		Interface_IfType = new Hashtable();
		Interface_IfType.put("1", "other(1)");
		Interface_IfType.put("6", "ethernetCsmacd(6)");
		Interface_IfType.put("23", "ppp(23)");
		Interface_IfType.put("28", "slip(28)");
		Interface_IfType.put("33", "Console port");
		Interface_IfType.put("53", "propVirtual(53)");
		Interface_IfType.put("117", "gigabitEthernet(117)");

		Interface_IfType.put("131", "tunnel(131)");

		Interface_IfType.put("135", "others(135)");
		Interface_IfType.put("136", "others(136)");
		Interface_IfType.put("142", "others(142)");
		Interface_IfType.put("54", "others(54)");
		Interface_IfType.put("5", "others(5)");

	}
	public static Hashtable HOST_hrSWRun_hrSWRunType = null;
	static {
		HOST_hrSWRun_hrSWRunType = new Hashtable();
		HOST_hrSWRun_hrSWRunType.put("1", "未知");
		HOST_hrSWRun_hrSWRunType.put("2", "操作系统");
		HOST_hrSWRun_hrSWRunType.put("3", "设备驱动");
		HOST_hrSWRun_hrSWRunType.put("4", "应用程序");

	}
	public static Hashtable HOST_hrSWRun_hrSWRunStatus = null;
	static {
		HOST_hrSWRun_hrSWRunStatus = new Hashtable();
		HOST_hrSWRun_hrSWRunStatus.put("1", "正在运行");
		HOST_hrSWRun_hrSWRunStatus.put("2", "等待");
		HOST_hrSWRun_hrSWRunStatus.put("3", "运行等待结果");
		HOST_hrSWRun_hrSWRunStatus.put("4", "有问题");
	}
	public static Hashtable HOST_Type_Producter = null;
	static {
		HOST_Type_Producter = new Hashtable();
		HOST_Type_Producter.put("1.3.6.1.4.1.9.1.248", "cisco");
		HOST_Type_Producter.put("2", "等待");
		HOST_Type_Producter.put("3", "运行等待结果");
		HOST_Type_Producter.put("4", "有问题");
	}

	Vector vbs = new Vector();

	// table options
	OID lowerBoundIndex, upperBoundIndex;
	/**
	 * 
	 */
	public AbstractSnmp(String community, Integer port, int timeout) {
		super();
		if (community != null && !community.equals("")) {
			this.default_community = community;
		}
		if (port.intValue() != 0) {
			this.default_port = port;
		}
		if (timeout != 0) {
			this.default_timeout = timeout;
		}
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.snmp4j.util.PDUFactory#createPDU(org.snmp4j.Target)
	 */

	public Snmp createSnmpSession() throws IOException {

		TransportMapping transport = new DefaultUdpTransportMapping();

		Snmp snmp = new Snmp(transport);

		return snmp;
	}

	protected Target createTarget(String community) {

		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(community));
		return target;

	}

	public PDU createPDU(Target target) {
		PDU request = new PDU();
		request.setType(default_pduType);
		return request;
	}

	public PDU send(String address) throws IOException {
		Snmp snmp = createSnmpSession();
		Target target = createTarget(default_community);
		target.setVersion(default_version);
		target.setAddress(GenericAddress.parse(address + "/" + default_port));
		target.setRetries(default_retries);
		target.setTimeout(default_timeout);
		snmp.listen();

		PDU request = createPDU(target);

		for (int i = 0; i < vbs.size(); i++) {
			request.add((VariableBinding) vbs.get(i));
		}

		PDU response = null;
		try {
			response = snmp.send(request, target).getResponse();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			snmp.close();
		}
		return response;
	}
	public PDU send(String community, String address) throws IOException {

		Snmp snmp = createSnmpSession();
		Target target = createTarget(community);
		target.setVersion(default_version);
		target.setAddress(GenericAddress.parse(address + "/" + default_port));
		target.setRetries(default_retries);
		target.setTimeout(default_timeout);
		snmp.listen();

		PDU request = createPDU(target);

		for (int i = 0; i < vbs.size(); i++) {
			request.add((VariableBinding) vbs.get(i));
		}

		PDU response = null;
		try {

			response = snmp.send(request, target).getResponse();
			//System.out.println("list "+response.toString());
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			snmp.close();
		}

		return response;
	}

	public void setVariableBindings(String[] oids) {
		vbs = new Vector();
		for (int i = 0; i < oids.length; i++) {
			String oid = oids[i];
			VariableBinding vb = new VariableBinding(new OID(oid));
			vbs.add(vb);
		}
	}

	public void addVariableBindings(String oid) {
		vbs = new Vector();
		VariableBinding vb = new VariableBinding(new OID(oid));
		vbs.add(vb);
	}

	public List table(String community, String address) throws IOException {
		//System.out.println("Start collect data as ip "+address+"   "+community+"    "+default_port);

		List list = null;

		Snmp snmp = null;
		try {
			snmp = createSnmpSession();
			Target target = createTarget(community);
			target.setVersion(default_version);
			target.setAddress(
				GenericAddress.parse(address + "/" + default_port));			
			target.setRetries(default_retries);
			//System.out.println("default_timeout  "+default_timeout);
			target.setTimeout(default_timeout);
			snmp.listen();
			TableUtils tableUtils = new TableUtils(snmp, this);

			OID[] columns = new OID[vbs.size()];
			for (int i = 0; i < columns.length; i++) {
				columns[i] = ((VariableBinding) vbs.get(i)).getOid();
			}
			
			/*
			PDU response = this.send(this.getDefault_community(), address);
System.out.println(response);			
			if (response.getErrorIndex() == 1) return null;*/
			
			//System.out.println("collect   memory 2.4");
			list =
				tableUtils.getTable(
					target,
					columns,
					lowerBoundIndex,
					upperBoundIndex);
			//System.out.println("collect   memory 2.5");
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			snmp.close();
		}
		return list;
	}

	//public  abstract Vector collectData();
	public  abstract Hashtable collectData();
	/**
	 * @return
	 */
	public String getDefault_community() {
		return default_community;
	}

	/**
	 * @return
	 */
	public Integer getDefault_port() {
		return default_port;
	}

	/**
	 * @return
	 */
	public int getDefault_timeout() {
		return default_timeout;
	}

	/**
	 * @param string
	 */
	public void setDefault_community(String string) {
		default_community = string;
	}

	/**
	 * @param string
	 */
	public void setDefault_port(Integer string) {
		default_port = string;
	}

	/**
	 * @param i
	 */
	public void setDefault_timeout(int i) {
		default_timeout = i;
	}

	//transform hexString to Datetime
	public static String hexStringToDateTime(String hexString) {
		String dateTime = "";
		try {
			byte[] values = OctetString.fromHexString(hexString).getValue();
			int year, month, day, hour, minute, second, msecond;

			year = values[0] * 256 + 256 + values[1];
			month = values[2];
			day = values[3];
			hour = values[4];
			minute = values[5];
			second = values[6];
			msecond = values[7];

			char format_str[] = new char[22];
			int index = 3;
			int temp = year;
			for (; index >= 0; index--) {
				format_str[index] = (char) (48 + (temp - temp / 10 * 10));
				temp /= 10;
			}
			format_str[4] = '-';
			index = 6;
			temp = month;
			for (; index >= 5; index--) {
				format_str[index] = (char) (48 + (temp - temp / 10 * 10));
				temp /= 10;
			}
			format_str[7] = '-';
			index = 9;
			temp = day;
			for (; index >= 8; index--) {
				format_str[index] = (char) (48 + (temp - temp / 10 * 10));
				temp /= 10;
			}
			format_str[10] = ',';
			index = 12;
			temp = hour;
			for (; index >= 11; index--) {
				format_str[index] = (char) (48 + (temp - temp / 10 * 10));
				temp /= 10;
			}
			format_str[13] = ':';
			index = 15;
			temp = minute;
			for (; index >= 14; index--) {
				format_str[index] = (char) (48 + (temp - temp / 10 * 10));
				temp /= 10;
			}
			format_str[16] = ':';
			index = 18;
			temp = second;
			for (; index >= 17; index--) {
				format_str[index] = (char) (48 + (temp - temp / 10 * 10));
				temp /= 10;
			}
			format_str[19] = '.';
			index = 21;
			temp = msecond;
			for (; index >= 20; index--) {
				format_str[index] = (char) (48 + (temp - temp / 10 * 10));
				temp /= 10;
			}
			dateTime = new String(format_str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTime;
	}
	public String getResponse(String[] oid, String host) {
		String sResponse;
		try {

			this.setVariableBindings(oid);
			PDU response = this.send(this.getDefault_community(), host);
			String operat = response.getVariableBindings().get(0).toString();

			sResponse =
				operat
					.substring(operat.lastIndexOf("=") + 1, operat.length())
					.trim();
			if (sResponse.equalsIgnoreCase("Null"))
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return sResponse;
	}
	public String getResponse(String[] oid, String host, String community) {
		String sResponse = "";
		try {

			this.setVariableBindings(oid);
			PDU response = this.send(community, host);
			String operat = response.getVariableBindings().get(0).toString();
			sResponse =
				operat
					.substring(operat.lastIndexOf("=") + 1, operat.length())
					.trim();
		} catch (Exception e) {
			return null;
		}
		return sResponse;
	}
	public String[] getResponse(
		String[] oid,
		String host,
		String community,
		int length) {
		String sResponse[] = new String[length];
		try {

			this.setVariableBindings(oid);
			PDU response = this.send(community, host);
			if (response == null)
				return null;
			int listSize = response.getVariableBindings().size();
			for (int i = 0; i < listSize; i++) {

				String operat =
					response.getVariableBindings().get(i).toString();
				sResponse[i] =
					operat
						.substring(operat.lastIndexOf("=") + 1, operat.length())
						.trim();
				//System.out.println("response "+sResponse[i]);
			}

			//	sResponse=
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return sResponse;
	}
	public boolean testSnmp(String host) throws Exception {
		boolean b = false;
		try {
			String s_testSnmp =
				getResponse(new String[] { "1.3.6.1.2.1.1.1.0" }, host);
			if (s_testSnmp == null || s_testSnmp.equals("")) {
				return b;
			} else {
				b = true;
				return b;
			}

		} catch (Exception e) {
			//System.err.print(e);
			return b;
		}
	}

	public String hexToString(String hexString) {
		String ascArray[] = hexString.split(":");
		String s = "";
		for (int i = 0; i < ascArray.length; i++) {
			char c = (char) Integer.parseInt(ascArray[i], 16);
			s = s + c;
		}
		return s;
	}

}
