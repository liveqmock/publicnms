package com.afunms.topology.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.model.IpAlias;

public class IPAllotUtil {
	// key为ip前三位，value为此IP段的所有ip
	public Map<String, List<String>> sort() {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<IpAlias> ipList = null;
		IpAliasDao dao = new IpAliasDao();
		try {
			ipList = dao.loadAll();
		} catch (Exception e) {

		} finally {
			dao.close();
		}
		for (int i = 0; i < ipList.size(); i++) {
			IpAlias ipalias = (IpAlias) ipList.get(i);
			String str = ipalias.getAliasip();
			int k = str.lastIndexOf("."); // 得到最后一个 “.” 的索引
			String aa = str.substring(0, k);
			if (map.containsKey(aa)) {
				List<String> bb = map.get(aa);
				bb.add(str);
			} else {
				List<String> c = new ArrayList<String>();
				c.add(str);
				map.put(aa, c);
			}
		}
		return map;
	}

	/*
	 * ip升序排序
	 * 
	 * 类似ip这类的都可以用此方法排序
	 */
	public String[] ipsort(String[] ip) {
		// 将ip数组变为等长数组
		for (int i = 0; i < ip.length; i++) {
			String[] temp = ip[i].split("\\.");
			ip[i] = "";
			for (int j = 0; j < temp.length; j++) {
				if (Integer.parseInt(temp[j]) / 10 == 0) { // 当该ip段为0-9时，前面补“00”
					temp[j] = "00" + temp[j];
				} else if (Integer.parseInt(temp[j]) / 100 == 0) { // 当该ip段为10-99时，前面补“0”
					temp[j] = "0" + temp[j];
				}
				ip[i] += temp[j] + "."; // 重新组装数组
			}
			ip[i] = ip[i].substring(0, ip[i].length() - 1); // 去除最后一个多余的“.”
		}

		Arrays.sort(ip);//排序，升序

		// 还原ip
		for (int i = 0; i < ip.length; i++) {
			String[] temp = ip[i].split("\\.");
			ip[i] = "";
			for (int j = 0; j < temp.length; j++) {
				if (temp[j].startsWith("00")) { // 去除前面的"00"
					temp[j] = temp[j].substring(2);
				} else if (temp[j].startsWith("0")) { // 去除前面的"0"
					temp[j] = temp[j].substring(1);
				}
				ip[i] += temp[j] + ".";
			}
			ip[i] = ip[i].substring(0, ip[i].length() - 1);
		}
		return ip;
	}

}
