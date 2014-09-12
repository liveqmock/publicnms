package com.afunms.polling.loader;

import java.util.ArrayList;
import java.util.List;

import com.afunms.application.dao.NtpConfigDao;
import com.afunms.application.model.NtpConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.Ntp;

public class NTPLoader extends NodeLoader{
	@Override
	public void loading() {
		NtpConfigDao dao = new NtpConfigDao();
		List list=null;
		try {
			list = dao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if(list == null)list = new ArrayList();
		ShareData.setNtpconfiglist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			NtpConfig vo = (NtpConfig) list.get(i);
			loadOne(vo);
		}
	}
	
	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getNtpList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof Ntp) {
				Ntp node = (Ntp) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						NtpConfig hostNode = (NtpConfig) baseVoList.get(j);
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

	@Override
	public void loadOne(BaseVo baseVo) {
		NtpConfig vo = (NtpConfig) baseVo;
		Ntp ntp = new Ntp();
		ntp.setId(vo.getId());
		ntp.setAlias(vo.getAlias());
		ntp.setPort(vo.getPort());
		ntp.setSendemail(vo.getSendemail());
		ntp.setSendmobiles(vo.getSendmobiles());
		ntp.setSendphone(vo.getSendphone());
		ntp.setBid(vo.getNetid());
		ntp.setMon_flag(vo.getMon_flag());
		ntp.setIpAddress(vo.getIpAddress());
		ntp.setCategory(123);
		ntp.setStatus(0);
		ntp.setType("Ntp状态监视");
		// ---------------加载被监视对象-------------------
		// PollingEngine.getInstance().addWeb(web);

		Node node = PollingEngine.getInstance().getNtpByID(ntp.getId());
		if (node != null) {
			PollingEngine.getInstance().getNtpList().remove(node);
		}
		PollingEngine.getInstance().addNtp(ntp);
	}

}
