/**
 * <p>Description:loading db nodes</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-8
 */

package com.afunms.polling.loader;

import java.util.ArrayList;
import java.util.List;

import com.afunms.application.dao.DnsConfigDao;
import com.afunms.application.model.DnsConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.DNS;
import com.afunms.polling.node.IIS;

public class DNSLoader extends NodeLoader {
	public void loading() {
		DnsConfigDao dao = new DnsConfigDao();
		List list = dao.loadAll();
		if(list == null)list = new ArrayList();
		ShareData.setDnslist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			DnsConfig vo = (DnsConfig) list.get(i);
			loadOne(vo);
		}
	}

	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getDnsList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof IIS) {
				IIS node = (IIS) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						DnsConfig hostNode = (DnsConfig) baseVoList.get(j);
						if (node.getId() == hostNode.getId()) {
							flag = true;
						}
					}
					if (!flag) {
						nodeList.remove(node);
					}
				}
			}
		}
	}

	public void loadOne(BaseVo baseVo) {
		DnsConfig vo = (DnsConfig) baseVo;
		DNS dns = new DNS();
		dns.setId(vo.getId());
		dns.setAlias(vo.getDnsip()); 
		dns.setName(vo.getDnsip()+"_dns");
		dns.setIpAddress(vo.getDnsip());
//		dns.setCommunity(vo.getCommunity());
		dns.setSendemail(vo.getSendemail());
		dns.setSendmobiles(vo.getSendmobiles());
		dns.setSendphone(vo.getSendphone());
		dns.setBid(vo.getNetid());
		dns.setMon_flag(1);
//		dns.setCategory(67);
		dns.setStatus(0);
		dns.setType("dns");
		// ---------------加载被监视对象-------------------

		// dbNode.setMoidList(moidList);
		// PollingEngine.getInstance().addNode(dbNode);
		// PollingEngine.getInstance().adddns(dns);

		Node node = PollingEngine.getInstance().getDnsByID(dns.getId());
		if (node != null) {
			PollingEngine.getInstance().getDnsList().remove(node);
		}
		PollingEngine.getInstance().addDns(dns);  
	}
}