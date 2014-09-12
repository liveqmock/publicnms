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
import com.afunms.application.dao.WebConfigDao;
import com.afunms.application.model.WebConfig;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.Web;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;

public class WebLoader extends NodeLoader {
	public void loading() {
		WebConfigDao dao = new WebConfigDao();
		List list=null;
		try {
			list = dao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if(list == null)list = new ArrayList();
		ShareData.setWebconfiglist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			WebConfig vo = (WebConfig) list.get(i);
			loadOne(vo);
		}
	}

	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getWebList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof Web) {
				Web node = (Web) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						WebConfig hostNode = (WebConfig) baseVoList.get(j);
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
		WebConfig vo = (WebConfig) baseVo;
		Web web = new Web();
		web.setId(vo.getId());
		web.setAlias(vo.getAlias());
		web.setStr(vo.getStr());
		web.setUser_name(vo.getUser_name());
		web.setUser_password(vo.getUser_password());
		web.setQuery_string(vo.getQuery_string());
		web.setMethod(vo.getMethod());
		web.setAvailability_string(vo.getAvailability_string());
		web.setTimeout(vo.getTimeout());
		web.setPoll_interval(vo.getPoll_interval());
		web.setUnavailability_string(vo.getUnavailability_string());
		web.setVerify(vo.getVerify());
		web.setSendemail(vo.getSendemail());
		web.setSendmobiles(vo.getSendmobiles());
		web.setSendphone(vo.getSendphone());
		web.setBid(vo.getNetid());
		web.setMon_flag(vo.getMon_flag());
		web.setIpAddress(vo.getIpAddress());
		web.setKeyword(vo.getKeyword());
		web.setPagesize_min(vo.getPagesize_min());
		web.setTracertflag(vo.getTracertflag());
		web.setCategory(57);
		web.setStatus(0);
		web.setType("WEB访问服务");
		// ---------------加载被监视对象-------------------
		// PollingEngine.getInstance().addWeb(web);

		Node node = PollingEngine.getInstance().getWebByID(web.getId());
		if (node != null) {
			PollingEngine.getInstance().getWebList().remove(node);
		}
		PollingEngine.getInstance().addWeb(web);
	}
}