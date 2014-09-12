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

import com.afunms.application.dao.DHCPConfigDao;
import com.afunms.application.model.DHCPConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.DHCP;
import com.afunms.polling.node.Weblogic;

public class DHCPLoader extends NodeLoader {
	public void loading() {
		DHCPConfigDao dao = new DHCPConfigDao();
		List list = dao.loadAll();
		clearRubbish(list);
		if(list == null)list = new ArrayList();
		ShareData.setDhcplist(list);
		for (int i = 0; i < list.size(); i++) {
			DHCPConfig vo = (DHCPConfig) list.get(i);
			loadOne(vo);
		}
	}

	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getDHCPList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof DHCP) {
				DHCP node = (DHCP) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						DHCPConfig hostNode = (DHCPConfig) baseVoList.get(j);
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
		DHCPConfig vo = (DHCPConfig) baseVo;
		DHCP dhcp = new DHCP();
		dhcp.setId(vo.getId());
		dhcp.setAlias(vo.getAlias());
		dhcp.setIpAddress(vo.getIpAddress());
		dhcp.setDhcptype(vo.getDhcptype());
		dhcp.setCommunity(vo.getCommunity());
		dhcp.setBid(vo.getNetid());
		dhcp.setMon_flag(vo.getMon_flag());
		dhcp.setCategory(93);
		dhcp.setStatus(0);
		dhcp.setType("DHCP服务");
		// ---------------加载被监视对象-------------------

		Node node = PollingEngine.getInstance().getDHCPByID(
				dhcp.getId());
		if (node != null) {
			PollingEngine.getInstance().getDHCPList().remove(node);
		}
		PollingEngine.getInstance().addDHCP(dhcp);
	}
}