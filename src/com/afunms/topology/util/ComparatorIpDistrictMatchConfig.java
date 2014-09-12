package com.afunms.topology.util;

import java.util.Comparator;
import java.util.StringTokenizer;

import com.afunms.topology.model.IpDistrictMatchConfig;

public class ComparatorIpDistrictMatchConfig implements Comparator {

	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		IpDistrictMatchConfig ipDistrictMatchConfig_1 = (IpDistrictMatchConfig)o1;
		IpDistrictMatchConfig ipDistrictMatchConfig_2 = (IpDistrictMatchConfig)o2;
		
		String ipaddress_1 = ipDistrictMatchConfig_1.getNodeIp();
		String ipaddress_2 = ipDistrictMatchConfig_2.getNodeIp();
		
		if(ip2long(ipaddress_1)>ip2long(ipaddress_2)){
			return 1;
		}else if(ip2long(ipaddress_1)==ip2long(ipaddress_2)){
			return 0;
		}else {
			return -1;
		}
		
		//return 0;
	}
	
	private long ip2long(String ip) {
		long result = 0;
		try {
			StringTokenizer st = new StringTokenizer(ip, ".");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				int part = Integer.parseInt(token);
				result = result * 256 + part;
			}
		} catch (Exception e) {
			result = 0;
		}
		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
