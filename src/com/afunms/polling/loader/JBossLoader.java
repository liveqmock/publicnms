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

import com.afunms.application.dao.JBossConfigDao;
import com.afunms.application.model.FTPConfig;
import com.afunms.application.model.JBossConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.Ftp;

public class JBossLoader extends NodeLoader {
	public void loading() {
		JBossConfigDao dao = new JBossConfigDao();
		List list = dao.loadAll();
		if(list == null)list = new ArrayList();
		ShareData.setJbosslist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			JBossConfig vo = (JBossConfig) list.get(i);
			loadOne(vo);
		}
	}

	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getJBossList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof com.afunms.polling.node.JBossConfig) {
				com.afunms.polling.node.JBossConfig node = (com.afunms.polling.node.JBossConfig) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						JBossConfig hostNode = (JBossConfig) baseVoList.get(j);
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
		JBossConfig vo = (JBossConfig) baseVo;
		com.afunms.polling.node.JBossConfig jboss = new com.afunms.polling.node.JBossConfig();
		jboss.setId(vo.getId());
		jboss.setUsername(vo.getUsername());
		jboss.setAlias(vo.getAlias());
		jboss.setFlag(vo.getFlag());
		jboss.setIpAddress(vo.getIpaddress());
		jboss.setNetid(vo.getNetid());
		jboss.setPassword(vo.getPassword());
		jboss.setPort(vo.getPort());
		jboss.setSendemail(vo.getSendemail());
		jboss.setSendmobiles(vo.getSendmobiles());
		jboss.setSendphone(vo.getSendphone());
		jboss.setCategory(70);
		jboss.setBid(vo.getNetid());

		// jboss.setType("jboss");

		// ---------------加载被监视对象-------------------

		// dbNode.setMoidList(moidList);
		// PollingEngine.getInstance().addNode(dbNode);
		// PollingEngine.getInstance().addJBoss(jboss);

		Node node = PollingEngine.getInstance().getJBossByID(jboss.getId());
		if (node != null) {
			PollingEngine.getInstance().getJBossList().remove(node);
		}
		PollingEngine.getInstance().addJBoss(jboss);
	}
}