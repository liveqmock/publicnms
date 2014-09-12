package com.afunms.topology.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.model.IpAlias;

public class MapUtil {
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
			int k = str.lastIndexOf(".");
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
}
