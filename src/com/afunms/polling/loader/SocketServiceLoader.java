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

import com.afunms.application.dao.PSTypeDao;
import com.afunms.application.model.PSTypeVo;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.SocketService;

public class SocketServiceLoader extends NodeLoader {
	public void loading() {
		PSTypeDao dao = new PSTypeDao();
		List list = dao.loadAll();
		if(list == null)list = new ArrayList();
		ShareData.setPslist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			PSTypeVo vo = (PSTypeVo) list.get(i);
			loadOne(vo);
		}
		close();
	}

	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getSocketList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof SocketService) {
				SocketService node = (SocketService) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						PSTypeVo hostNode = (PSTypeVo) baseVoList.get(j);
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
		com.afunms.application.model.PSTypeVo vo = (com.afunms.application.model.PSTypeVo) baseVo;
		SocketService socketservice = new SocketService();
		socketservice.setId(vo.getId());
		socketservice.setIpaddress(vo.getIpaddress());
		socketservice.setPort(vo.getPort());
		socketservice.setPortdesc(vo.getPortdesc());
		socketservice.setMonflag(vo.getMonflag());
		socketservice.setFlag(vo.getFlag());
		socketservice.setTimeout(vo.getTimeout());
		socketservice.setSendemail(vo.getSendemail());
		socketservice.setSendmobiles(vo.getSendmobiles());
		socketservice.setSendphone(vo.getSendphone());
		socketservice.setAlias(vo.getPortdesc());
		socketservice.setIpAddress(vo.getIpaddress());
		socketservice.setBid(vo.getBid());
		if (vo.getMonflag() == 0) {
			socketservice.setManaged(false);
		} else
			socketservice.setManaged(true);
		socketservice.setCategory(68);
		socketservice.setStatus(0);
		socketservice.setType("Socket服务");

		// ---------------(3)加载被监视对象-------------------

		// sPollingEngine.getInstance().addSocket(socketservice);

		Node node = PollingEngine.getInstance().getSocketByID(
				socketservice.getId());
		if (node != null) {
			PollingEngine.getInstance().getSocketList().remove(node);
		}
		PollingEngine.getInstance().addSocket(socketservice);
	}
}