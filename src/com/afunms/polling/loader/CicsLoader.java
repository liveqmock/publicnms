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

import com.afunms.application.dao.CicsConfigDao;
import com.afunms.application.model.CicsConfig;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.Bussiness;
import com.afunms.polling.node.Cics;
import com.afunms.topology.model.ManageXml;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;

public class CicsLoader extends NodeLoader {
	public void loading() {
		CicsConfigDao dao = new CicsConfigDao();
		List list = dao.loadAll();
		if(list == null)list = new ArrayList();
		ShareData.setCicslist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			CicsConfig vo = (CicsConfig) list.get(i);
			loadOne(vo);
		}
		// System.out.println("========"+PollingEngine.getInstance().getCicsList().size());
	}

	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getCicsList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof Cics) {
				Cics node = (Cics) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						CicsConfig hostNode = (CicsConfig) baseVoList.get(j);
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
		CicsConfig vo = (CicsConfig) baseVo;
		Cics cics = new Cics();
		cics.setId(vo.getId());
		cics.setAlias(vo.getAlias());
		cics.setRegion_name(vo.getRegion_name());
		cics.setIpAddress(vo.getIpaddress());
		cics.setPort_listener(vo.getPort_listener());
		cics.setNetwork_protocol(vo.getNetwork_protocol());
		cics.setGateway(vo.getGateway());

		cics.setSendemail(vo.getSendemail());
		cics.setSendmobiles(vo.getSendmobiles());
		cics.setConn_timeout(vo.getConn_timeout());
		cics.setBid(vo.getNetid());
		cics.setFlag(vo.getFlag());
		cics.setCategory(65);
		cics.setStatus(0);
		cics.setType("Cics");
		// ---------------加载被监视对象-------------------
		// PollingEngine.getInstance().addCics(cics);

		 Node node=PollingEngine.getInstance().getCicsByID(cics.getId());
		 if(node!=null){
		 PollingEngine.getInstance().getCicsList().remove(node);
		 }
		PollingEngine.getInstance().addCics(cics);
	}
}