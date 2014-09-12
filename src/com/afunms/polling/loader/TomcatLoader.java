/**
 * <p>Description:loading host node</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-11-28
 */

package com.afunms.polling.loader;

import com.afunms.polling.base.Node;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.NodeLoader;
import com.afunms.application.dao.TomcatDao;

import com.afunms.application.model.Tomcat;

public class TomcatLoader extends NodeLoader {
	public void loading() {
		TomcatDao dao = new TomcatDao();
		List list=null;
		try {
			list = dao.loadOrderByIP();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if(list == null)list = new ArrayList();
		ShareData.setTomcatlist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			Tomcat vo = (Tomcat) list.get(i);
			loadOne(vo);
		}
		close();
	}

	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getTomcatList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof com.afunms.polling.node.Tomcat) {
				com.afunms.polling.node.Tomcat node = (com.afunms.polling.node.Tomcat) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						com.afunms.application.model.Tomcat hostNode = (com.afunms.application.model.Tomcat) baseVoList.get(j);
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
		com.afunms.application.model.Tomcat vo = (com.afunms.application.model.Tomcat) baseVo;
		com.afunms.polling.node.Tomcat tomcat = new com.afunms.polling.node.Tomcat();
		tomcat.setId(vo.getId());
		tomcat.setUser(vo.getUser());
		tomcat.setPassword(vo.getPassword());
		tomcat.setAlias(vo.getAlias());
		tomcat.setIpAddress(vo.getIpAddress());
		tomcat.setPort(vo.getPort());
		if(vo.getMonflag() == 1)
			tomcat.setManaged(true);
		else
			tomcat.setManaged(false);
		tomcat.setVersion(vo.getVersion());
		tomcat.setJvmversion(vo.getJvmversion());
		tomcat.setJvmvender(vo.getJvmvender());
		tomcat.setOs(vo.getOs());
		tomcat.setOsversion(vo.getOsversion());
		tomcat.setStatus(0);
		tomcat.setBid(vo.getBid());
		tomcat.setType("Tomcat中间件");

		Node node = PollingEngine.getInstance().getTomcatByID(tomcat.getId());
		if (node != null) {
			PollingEngine.getInstance().getTomcatList().remove(node);
		}
		PollingEngine.getInstance().addTomcat(tomcat);
	}
}