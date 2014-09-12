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

import com.afunms.application.dao.ApacheConfigDao;
import com.afunms.application.model.ApacheConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.ApachConfig;

public class ApacheLoader extends NodeLoader {
	public void loading() {
		ApacheConfigDao dao = new ApacheConfigDao();
		List list = dao.loadAll();
		if(list == null)list = new ArrayList();
		ShareData.setApachlist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			ApacheConfig vo = (ApacheConfig) list.get(i);
			loadOne(vo);
		}
	}

	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getApacheList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof ApachConfig) {
				ApachConfig node = (ApachConfig) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						ApacheConfig hostNode = (ApacheConfig) baseVoList.get(j);
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
		ApacheConfig vo = (ApacheConfig) baseVo;
		ApachConfig apach = new ApachConfig();
		apach.setId(vo.getId());
		apach.setAlias(vo.getAlias());
		apach.setIpAddress(vo.getIpaddress());
		apach.setUsername(vo.getUsername());
		apach.setPassword(vo.getPassword());
		apach.setPort(vo.getPort());

		apach.setSendemail(vo.getSendemail());
		apach.setSendmobiles(vo.getSendmobiles());
		apach.setBid(vo.getNetid());
		apach.setFlag(vo.getFlag());
		apach.setCategory(66);
		apach.setStatus(0);
		apach.setType("Apache");
		// ---------------加载被监视对象-------------------
		// PollingEngine.getInstance().addApache(apach);
		// 如果不考虑被删除内容
		
	    
		Node node=PollingEngine.getInstance().getApacheByID(apach.getId());
		if(node!=null){
			PollingEngine.getInstance().getApacheList().remove(node);
		}
		PollingEngine.getInstance().addApache(apach);
	}
}