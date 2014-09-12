/**
 * <p>Description:loading db nodes</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-8
 */

package com.afunms.polling.loader;

import com.afunms.polling.base.Node;
import java.util.ArrayList;
import java.util.List;

import com.afunms.monitor.executor.base.MonitorFactory;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.application.model.*;
import com.afunms.application.dao.WasConfigDao;
import com.afunms.application.model.WasConfig;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.Was;
import com.afunms.topology.model.NodeMonitor;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.*;

public class WasLoader extends NodeLoader {
	public void loading() {
		WasConfigDao dao = null;
		List list = null;
		try {
			dao = new WasConfigDao();
			list = dao.loadAll();
			clearRubbish(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
		if(list == null)list = new ArrayList();
		ShareData.setWaslist(list);
		for (int i = 0; i < list.size(); i++) {
			WasConfig vo = (WasConfig) list.get(i);
			loadOne(vo);
		}
	}

	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getWasList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof Was) {
				Was node = (Was) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						WasConfig hostNode = (WasConfig) baseVoList.get(j);
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
		WasConfig vo = (WasConfig) baseVo;
		Was was = new Was();
		was.setAlias(vo.getName());
		was.setId(vo.getId());
		was.setName(vo.getName());
		was.setIpaddress(vo.getIpaddress());
		was.setIpAddress(vo.getIpaddress());
		was.setCommunity(vo.getCommunity());
		was.setSendemail(vo.getSendemail());
		was.setSendmobiles(vo.getSendmobiles());
		was.setSendphone(vo.getSendphone());
		was.setBid(vo.getNetid());
		was.setMon_flag(vo.getMon_flag());
		was.setStatus(0);
		was.setCategory(63);
		was.setType("Was");
		// ---------------加载被监视对象-------------------

		// dbNode.setMoidList(moidList);
		// PollingEngine.getInstance().addNode(dbNode);
		// PollingEngine.getInstance().addWas(was);

		Node node = PollingEngine.getInstance().getWasByID(was.getId());
		if (node != null) {
			PollingEngine.getInstance().getWasList().remove(node);
		}
		PollingEngine.getInstance().addWas(was);
	}
}