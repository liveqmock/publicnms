/**
 * <p>Description:loading host node</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-11-28
 */

package com.afunms.polling.loader;

import java.util.ArrayList;
import java.util.List;

import com.afunms.application.dao.ResinDao;
import com.afunms.application.model.Resin;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.monitor.executor.base.MonitorFactory;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.topology.dao.NodeMonitorDao;
import com.afunms.topology.model.NodeMonitor;

public class ResinLoader extends NodeLoader {
	public void loading() {
		ResinDao dao = new ResinDao();
		List list = dao.loadOrderByIP();
		if(list == null)list = new ArrayList();
		ShareData.setResinlist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			Resin vo = (Resin) list.get(i);
			loadOne(vo);
		}
		close();
	}

	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getTomcatList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof com.afunms.polling.node.Resin) {
				com.afunms.polling.node.Resin node = (com.afunms.polling.node.Resin) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						com.afunms.application.model.Resin hostNode = (com.afunms.application.model.Resin) baseVoList.get(j);
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
		com.afunms.application.model.Resin vo = (com.afunms.application.model.Resin) baseVo;
		com.afunms.polling.node.Resin resin = new com.afunms.polling.node.Resin();
		boolean managed=false;
		if(vo.getMonflag()==1)managed=true;
		resin.setId(vo.getId());
		resin.setUser(vo.getUser());
		resin.setPassword(vo.getPassword());
		resin.setAlias(vo.getAlias());
		resin.setIpAddress(vo.getIpAddress());
		resin.setPort(vo.getPort());
		resin.setManaged(managed);
		resin.setVersion(vo.getVersion());
		resin.setJvmversion(vo.getJvmversion());
		resin.setJvmvender(vo.getJvmvender());
		resin.setOs(vo.getOs());
		resin.setOsversion(vo.getOsversion());
		resin.setStatus(0);
		resin.setBid(vo.getBid());
		resin.setType("Resin中间件");
		// ---------------(3)加载被监视对象-------------------
		

		Node node = PollingEngine.getInstance().getResinByID(resin.getId());
		if (node != null) {
			PollingEngine.getInstance().getResinList().remove(node);
		}
		PollingEngine.getInstance().addResin(resin);
	}
}