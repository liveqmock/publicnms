package com.afunms.initialize;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.DiscoverConfig;

public class PortConfigCenter {
	private static PortConfigCenter instance = new PortConfigCenter();

	public static PortConfigCenter getInstance() {
		return instance;
	}

	private Hashtable portHastable = new Hashtable();

	/**
	 * 获取需要添加到流量统计中的端口集合
	 * 
	 * @return
	 */
	public Hashtable getPortHastable() {
		return portHastable;
	}

	/**
	 * 设置全局变量 获取需要添加到流量统计中的端口集合
	 */
	public void setPortHastable() {
		portHastable = new Hashtable();
		DBManager dbManager = new DBManager();
		try {
			String sql = " select ipaddress,portindex,flag from system_portconfig where flag=1 ";
			ResultSet rs = dbManager.executeQuery(sql);
			try{
				while (rs.next()) {
					String ipaddress = rs.getString("ipaddress");
					int portindex = rs.getInt("portindex");
					String flag = rs.getString("flag");
					List list = new ArrayList();
					if (portHastable.containsKey(ipaddress)) {
						list = (List) portHastable.get(ipaddress);
						list.add("*"+portindex + ":" + flag);
					} else {
						list.add("*"+portindex + ":" + flag);
						portHastable.put(ipaddress, list);
					}
				}
			} catch (Exception ex) {
				SysLogger.error("PortConfigCenter.getData()", ex);
			}finally{
				rs.close();
			}
		} catch (Exception ex) {
			SysLogger.error("PortConfigCenter.getData()", ex);
		}finally{
			dbManager.close();
		}
		this.portHastable = portHastable;
	}

	public static void main(String args[]) {
		PortConfigCenter.getInstance().setPortHastable();
		Hashtable hs = PortConfigCenter.getInstance().getPortHastable();
		Enumeration keys = hs.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			System.out.println(key + "---" + hs.get(key));
		}
	}
}
