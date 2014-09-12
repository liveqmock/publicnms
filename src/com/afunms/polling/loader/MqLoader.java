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
import com.afunms.application.dao.MQConfigDao;
import com.afunms.application.model.MQConfig;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.MQ;
import com.afunms.topology.model.NodeMonitor;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.*;

public class MqLoader extends NodeLoader {
	public void loading() {
		MQConfigDao dao = new MQConfigDao();
		List list = dao.loadAll();
		if(list == null)list = new ArrayList();
		ShareData.setMqlist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			MQConfig vo = (MQConfig) list.get(i);
			loadOne(vo);
		}
	}

	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getMqList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof MQ) {
				MQ node = (MQ) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						MQConfig hostNode = (MQConfig) baseVoList.get(j);
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
		MQConfig vo = (MQConfig) baseVo;
		MQ mq = new MQ();
		mq.setId(vo.getId());
		mq.setAlias(vo.getName());
		mq.setIpAddress(vo.getIpaddress());
		mq.setManagername(vo.getManagername());
		mq.setPortnum(vo.getPortnum());
		mq.setSendemail(vo.getSendemail());
		mq.setSendmobiles(vo.getSendmobiles());
		mq.setSendphone(vo.getSendphone());
		mq.setBid(vo.getNetid());
		mq.setMon_flag(vo.getMon_flag());
		mq.setCategory(61);
		mq.setStatus(0);
		mq.setType("MQ服务");
		// ---------------加载被监视对象-------------------

		// dbNode.setMoidList(moidList);
		// PollingEngine.getInstance().addNode(dbNode);
		// PollingEngine.getInstance().addMq(mq);

		Node node = PollingEngine.getInstance().getMqByID(mq.getId());
		if (node != null) {
			PollingEngine.getInstance().getMqList().remove(node);
		}
		PollingEngine.getInstance().addMq(mq);
	}
}