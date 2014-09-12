package com.afunms.automation.task;

import com.afunms.automation.dao.NetCfgFileNodeDao;
import com.afunms.automation.model.NetCfgFileNode;
import com.afunms.automation.telnet.BaseTelnet;
import com.afunms.automation.telnet.CiscoTelnet;
import com.afunms.automation.telnet.NetTelnetUtil;

public class BatchModifyTask implements Runnable {
	StringBuffer result;
	NetCfgFileNode hmo;
	String modifyuser;
	String newpassword;
	String threeA;
	int encrypt;

	public BatchModifyTask(StringBuffer result, NetCfgFileNode hmo, String modifyuser, String newpassword, String threeA, int encrypt) {
		this.result = result;
		this.hmo = hmo;
		this.modifyuser = modifyuser;
		this.newpassword = newpassword;
		this.threeA = threeA;
		this.encrypt = encrypt;
	}

	public void run() {
     String deviceRender=hmo.getDeviceRender();
		if (deviceRender.equals("h3c"))//
		{
			/*
			 * String modifyuser = this.getParaValue("modifyuser"); String
			 * newpassword = this.getParaValue("newpassword"); int encrypt =
			 * this.getParaIntValue("encrypt"); String threeA =
			 * this.getParaValue("threeA");
			 */

			NetTelnetUtil tvpn = new NetTelnetUtil();
			tvpn.setSuUser(hmo.getSuuser());// su
			tvpn.setSuPassword(hmo.getSupassword());// su密码
			tvpn.setUser(hmo.getUser());// 用户
			tvpn.setPassword(hmo.getPassword());// 密码
			tvpn.setIp(hmo.getIpaddress());// ip地址
//			tvpn.setDEFAULT_PROMPT(hmo.getDefaultpromtp());// 结束标记符号
			tvpn.setPort(hmo.getPort());
			// boolean b = tvpn.Modifypassowd(modifyuser, newpassword);
			boolean b = tvpn.modifypassowd(modifyuser, newpassword, encrypt, threeA, hmo.getOstype());
			
			if (b) {
				if (modifyuser.equals("su")) {
					hmo.setSupassword(newpassword);
				} else {
					hmo.setUser(modifyuser);
					hmo.setPassword(newpassword);
				}
				NetCfgFileNodeDao hdao = new NetCfgFileNodeDao();
				hdao.update(hmo);
				hdao.close();
			} else {
				synchronized (result) {
					result.append("," + hmo.getIpaddress());
				}

			}

		}else if(deviceRender.equals("zte")||deviceRender.equals("redgiant")||deviceRender.equals("huawei")){
			BaseTelnet telnet = new BaseTelnet(hmo.getIpaddress(), hmo.getUser(), hmo.getPassword(), hmo.getPort(), hmo.getSuuser(), hmo.getSupassword());
			String sign=telnet.login();
			if (sign.indexOf("成功")>-1) {
				if (telnet.modifyDevPasswd(deviceRender, modifyuser, newpassword,null)) {
					if(modifyuser!=null&&!modifyuser.equals(""))
					hmo.setUser(modifyuser);
					hmo.setPassword(newpassword);
					NetCfgFileNodeDao tdao = new NetCfgFileNodeDao();
					tdao.update(hmo);
					tdao.close();
				} else {
					synchronized (result) {
						result.append("," + hmo.getIpaddress());
					}
				}
			} else {
				synchronized (result) {
					result.append("," + hmo.getIpaddress());
				}
			}
		}else{
			NetCfgFileNode telnetcfg = new NetCfgFileNode();
			CiscoTelnet ciscoTelnet = new CiscoTelnet(hmo.getIpaddress(), hmo.getUser(), hmo.getPassword(), hmo.getPort());
			if (ciscoTelnet.login()) {
				if (ciscoTelnet.modifyPasswd(hmo.getSupassword(), modifyuser, newpassword)) {
					if(modifyuser!=null&&!modifyuser.equals(""))
					hmo.setUser(modifyuser);
					hmo.setPassword(newpassword);
					NetCfgFileNodeDao tdao = new NetCfgFileNodeDao();
					tdao.update(hmo);
					tdao.close();
				} else {
					synchronized (result) {
						result.append("," + hmo.getIpaddress());
					}
				}
			} else {
				synchronized (result) {
					result.append("," + hmo.getIpaddress());
				}
			}
		}
	}
}