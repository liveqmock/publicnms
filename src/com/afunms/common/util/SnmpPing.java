/**
 * <p>Description:snmp service</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-10
 */

package com.afunms.common.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.PDUFactory;


public class SnmpPing implements PDUFactory
{

	private TransportMapping transport;

	private Snmp snmp;

	private CommunityTarget target;

	private PDU pdu;

	private long timeout;

	private String address;
	
	
	/**
	 * 构造函数用来初始化参数
	 * 
	 * @param address
	 *            查询的机器的ip地址
	 * @param port_s
	 *            端口号
	 */

	public SnmpPing(String address, String port_s)
	{
		try
		{
			this.address = address;
			transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			snmp.listen();

			target = new CommunityTarget();
			target.setAddress(getAddress(address, port_s)); // 设置地址信息

			pdu = new PDU();

			setTimeout("5000");

		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void setTimeout(String timeout_s)
	{
		timeout = 10000L;
		try
		{
			timeout = Long.parseLong(timeout_s);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		target.setTimeout(timeout);
	}

	/**
	 * 设置团体字
	 * 
	 * @param community
	 */

	public void setCommunity(String community)
	{
		if (community == null || community.trim().length() == 0)
			community = "public";
		target.setCommunity(new OctetString(community));
	}

	/**
	 * 设置snmp版本号
	 * 
	 * @param version
	 */

	public void setVersion(int version)
	{
		target.setVersion(version);
	}

	public void setVersion(String version_s)
	{
		Integer version = 1;
		
		try
		{
			version = Integer.parseInt(version_s);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		if (version == null)
			version = 1;
		target.setVersion(version);
	}


	/**
	 * 得到snmp版本号
	 * 
	 * @return 版本号信息
	 */

	public int getVersion()
	{
		return target.getVersion();
	}
	
	
	public PDU createPDU(Target arg0)
	{
		PDU request;

		request = new PDU();

		request.setType(PDU.GETNEXT);

		return request;
	}
	
	

	/**
	 * 根据地址与端口号确定返回的地址
	 * 
	 * @param addr
	 *            传入的地址字符串
	 * @param port_s
	 *            传入的端口号字符串
	 * @return target需要设置的地址
	 * @throws UnknownHostException
	 */

	private Address getAddress(String addr, String port_s)
			throws UnknownHostException
	{
		Integer port = 161;
		try
		{
			port = Integer.parseInt(port_s);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		if (port == null)
			port = 161;
		return (Address) (new UdpAddress(InetAddress.getByName(addr), port));
	}
	
	
	private String get(String oid) throws Exception
	{
		if (oid == null)
		{
			return null;
		}

		pdu.setType(PDU.GET);
		pdu.clear();

		pdu.add(new VariableBinding(new OID(oid.trim())));

		try
		{
			ResponseEvent response = snmp.send(pdu, target);
			if (response != null)
			{
				if (response.getResponse() != null)
				{
					Vector v = response.getResponse().getVariableBindings();
					if (v != null && v.size() > 0)
					{
						VariableBinding vb = (VariableBinding) v.elementAt(0);
						if(vb.getOid() != null && vb.getVariable() != null)
						{
							return vb.getVariable().toString();
						}
						else
						{
							throw new Exception("can not get the value from the snmp device , please make sure your oid is right");
						}
						
					}
				} else
				{
					throw new Exception("there has no response from the snmp device");
				}

			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	
	public void close()
	{
		if (target != null)
		{
			target = null;
		}

		if (transport != null)
		{
			try
			{
				transport.close();
				transport = null;
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		if (snmp != null)
		{
			try
			{
				snmp.close();
				snmp = null;
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	

	private String set(String oid, String value , char type) throws Exception
	{
		pdu.setType(PDU.SET);
		Variable var = null;
		pdu.clear();

		
		if (oid != null && value != null )
		{
			if(type == 'x')
			{	
				byte[] bytes = ip2bytes(value);
				var = new OctetString(bytes);
			}
			else if(type == 's')
			{
				var = new OctetString(value);
			}
			else if(type == 'i')
			{
				var = new Integer32(Integer.parseInt(value));
			}
			

			pdu.add(new VariableBinding(new OID(oid), var));
			try
			{
				ResponseEvent response = snmp.send(pdu, target);
				if (response.getResponse() == null)
				{
					System.out.println("timeout" + " oid = " + oid + " , value = " + value + " , type " + type);
					throw new Exception( "timeout" + " oid = " + oid + " , value = " + value + " , type " + type);
				} else
				{
					if (response.getResponse().getErrorStatus() == 0) // set操作成功
					{
						return "set true" + " oid = " + oid + " , value = " + value + " , type " + type;
					} else
					{
						System.out.println("设置失败" + " oid = " + oid + " , value = " + value + " , type " + type);
						System.out.println("error : "+response.getResponse().getErrorStatusText());
						
						throw new Exception("set failed of " + " oid = " + oid + " , value = " + value + " , type " + type);
					}
				}
			} catch (Exception e)
			{
				throw e;
			}

		}

		return " expection has happend -----------  "+ " oid = " + oid + " , value = " + value + " , type " + type;
	}
	
	
	public String ping(String ip )
	{
		return ping(ip , true);
	}
	
	
	
	
	public String ping(String ip , boolean retry)
	{
		if(ip != null)
		{
			String index = String.valueOf(ip2long(ip) % (1 << 31));
			
			if(index.equals("0"))
			{
				index = "1";
			}

			try
			{
				set(".1.3.6.1.4.1.9.9.16.1.1.1.16." + index, "6", 'i');
				set(".1.3.6.1.4.1.9.9.16.1.1.1.16." + index, "5", 'i');
				set(".1.3.6.1.4.1.9.9.16.1.1.1.15." + index, "yiming2", 's');
				set(".1.3.6.1.4.1.9.9.16.1.1.1.2."  + index, "1", 'i');
				set(".1.3.6.1.4.1.9.9.16.1.1.1.3." + index, ip, 'x');
				set(".1.3.6.1.4.1.9.9.16.1.1.1.6." + index, "1000", 'i');
				set(".1.3.6.1.4.1.9.9.16.1.1.1.4." + index, "5", 'i');
				set(".1.3.6.1.4.1.9.9.16.1.1.1.5." + index, "111", 'i');
				set(".1.3.6.1.4.1.9.9.16.1.1.1.16." + index, "1", 'i');
			
				String result = get(".1.3.6.1.4.1.9.9.16.1.1.1.12." + index);
				return result;
			} catch (Exception e)
			{
				if(retry)
				{
					try
					{
						set(".1.3.6.1.4.1.9.9.16.1.1.1.16." + index, "6", 'i');
						set(".1.3.6.1.4.1.9.9.16.1.1.1.16." + index, "5", 'i');
						set(".1.3.6.1.4.1.9.9.16.1.1.1.15." + index, "yiming2", 's');
						set(".1.3.6.1.4.1.9.9.16.1.1.1.2."  + index, "1", 'i');
						set(".1.3.6.1.4.1.9.9.16.1.1.1.3." + index, ip, 'x');
						set(".1.3.6.1.4.1.9.9.16.1.1.1.6." + index, "1000", 'i');
						set(".1.3.6.1.4.1.9.9.16.1.1.1.4." + index, "5", 'i');
						set(".1.3.6.1.4.1.9.9.16.1.1.1.5." + index, "111", 'i');
						set(".1.3.6.1.4.1.9.9.16.1.1.1.16." + index, "1", 'i');
					
						String result = get(".1.3.6.1.4.1.9.9.16.1.1.1.12." + index);
						return result;
					} catch (Exception e2)
					{
						e2.printStackTrace();
						return "Uncertain";
					}
				}
				else
				{
					e.printStackTrace();
					return "Uncertain";
				}
			}
		}

		
		return null;
	}
	


	/**
	 * 将ip 转换成 long
	 * 
	 * @param ip
	 * @return
	 */
	public static long ip2long(String ip)
	{
		long result = 0L;
		try
		{
			for (StringTokenizer st = new StringTokenizer(ip, "."); st
					.hasMoreTokens();)
			{
				String token = st.nextToken();
				int part = Integer.parseInt(token);
				result = result * 256 + part;
			}

		} catch (Exception e)
		{
			result = 0L;
		}
		return result;
	}


	public static byte[] ip2bytes(String ip) {
		byte[] result = new byte[0];
		try {
			StringTokenizer st = new StringTokenizer(ip, ".");
			result = new byte[st.countTokens()];
			for (int i = 0; i < result.length; ++i) {
				String token = st.nextToken();
				Integer part = new Integer(token);
				result[i] = part.byteValue();
			}
		} catch (Exception e) {
			result = new byte[0];
		}
		return result;
	}
	
	
	
	
	

	public static void main(String[] args)
	{

		SnmpPing snmpPing = new SnmpPing("172.25.25.240", "161");
		snmpPing.setCommunity("dhcc");
		snmpPing.setTimeout("5000");

		snmpPing.setVersion(0);
		
		String result = snmpPing.ping("10.10.151.162" , true);
		System.out.println(result);
		

		result = snmpPing.ping("10.10.151.250");
		System.out.println(result);
		
		
		 
	}

	


}

