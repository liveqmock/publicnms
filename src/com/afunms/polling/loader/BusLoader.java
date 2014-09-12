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

import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.ApachConfig;
import com.afunms.polling.node.Bussiness;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.model.ManageXml;
import com.afunms.application.model.ApacheConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;

public class BusLoader extends NodeLoader {
	public void loading() {
		ManageXmlDao dao = new ManageXmlDao();
		List list = dao.findByTopoType(1);
		if(list == null)list = new ArrayList();
		ShareData.setBusinesslist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			ManageXml vo = (ManageXml) list.get(i);
			loadOne(vo);
		}
	}

	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getBusList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof Bussiness) {
				Bussiness node = (Bussiness) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						ManageXml hostNode = (ManageXml) baseVoList.get(j);
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
		ManageXml vo = (ManageXml) baseVo;
		Bussiness bus = new Bussiness();
		bus.setId(vo.getId());
		bus.setBid(vo.getBid());
		bus.setName(vo.getTopoName());
		bus.setAlias(vo.getTopoName());
		bus.setCategory(80);
		bus.setStatus(0);
		bus.setType("业务");// yangjun add

		// ---------------加载被监视对象-------------------
		// PollingEngine.getInstance().addBus(bus);

		 Node node=PollingEngine.getInstance().getBusByID(bus.getId());
		 if(node!=null){
		 PollingEngine.getInstance().getBusList().remove(node);
		 }
		PollingEngine.getInstance().addBus(bus);
	}
}