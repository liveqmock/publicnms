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
import com.afunms.application.dao.DominoConfigDao;
import com.afunms.application.model.DominoConfig;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.node.Domino;
import com.afunms.topology.model.NodeMonitor;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.*;

public class DominoLoader extends NodeLoader {
	public void loading() {
		DominoConfigDao dao = new DominoConfigDao();
		List list = dao.loadAll();
		if(list == null)list = new ArrayList();
		ShareData.setDominolist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			DominoConfig vo = (DominoConfig) list.get(i);
			loadOne(vo);
		}
	}

	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getDominoList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof Domino) {
				Domino node = (Domino) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						DominoConfig hostNode = (DominoConfig) baseVoList
								.get(j);
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
		DominoConfig vo = (DominoConfig) baseVo;
		Domino domino = new Domino();
		domino.setId(vo.getId());
		domino.setAlias(vo.getName());
		domino.setName(vo.getName());
		domino.setIpAddress(vo.getIpaddress());
		domino.setCommunity(vo.getCommunity());
		domino.setSendemail(vo.getSendemail());
		domino.setSendmobiles(vo.getSendmobiles());
		domino.setSendphone(vo.getSendphone());
		domino.setBid(vo.getNetid());
		domino.setMon_flag(vo.getMon_flag());
		domino.setCategory(62);
		domino.setStatus(0);
		domino.setType("Domino");
		// ---------------加载被监视对象-------------------

		// dbNode.setMoidList(moidList);
		// PollingEngine.getInstance().addNode(dbNode);
		// PollingEngine.getInstance().addDomino(domino);

		 Node node=PollingEngine.getInstance().getDominoByID(domino.getId());
		 if(node!=null){
		 PollingEngine.getInstance().getDominoList().remove(node);
		 }
		PollingEngine.getInstance().addDomino(domino);
	}
}