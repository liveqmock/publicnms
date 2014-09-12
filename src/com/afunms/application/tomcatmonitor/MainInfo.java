/*
 * Created on 2005-1-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.afunms.application.tomcatmonitor;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class MainInfo {
	Hashtable hMain = new Hashtable();

	HashMap map;

	String sMain;

	StringBuffer bufMain;

	int tNum;

	Hashtable hHead;

	Hashtable hContext;

	String sPortHead;

	String portInfo = "";

	public MainInfo() {

	}

	public MainInfo(HashMap map, int tNum) {
		this.map = map;
		this.tNum = tNum;
	}

	//取得主信息字符串，等待处理
	public String sMainTag() {
		sMain = map.get(String.valueOf(tNum)).toString();
		//System.out.println(sMain+"$$$$$$$$$$$$");
		return sMain;
	}

	//第一次处理，取得第一大类信息（tomcat5.0为JVM）
	public String JVMInfo(String sMain) {
		bufMain = new StringBuffer(sMain.trim());
		int i = bufMain.indexOf("</p>");
		bufMain.delete(i + 4, bufMain.length());
		return bufMain.toString();
	}

	//取得JVM小项信息 (类型：属性：值：单位)
	public Hashtable hJVMInfo(String sMain) {
		Hashtable returnVal = new Hashtable();
		String jvmInfo = JVMInfo(sMain.substring(sMain.indexOf("<h1>JVM</h1>")));

		String sHead = jvmInfo.substring(jvmInfo.indexOf("<h1>") + 4, jvmInfo
				.indexOf("</h1>"));
		String sContextRaw = jvmInfo.substring(jvmInfo.indexOf("<p>") + 3,
				jvmInfo.indexOf("MB</p>"));
		String[] sContext = sContextRaw.trim().split("MB");

		for (int i = 0; i < sContext.length; i++) {
			String[] tmp = sContext[i].trim().split(":");
			System.out.println(tmp[1]+"&&&&");
			returnVal.put(String.valueOf(i), tmp[1]);
		}
		return returnVal;
	}

	//取得服务器端口原始信息
	public String PORTInfo(String sMain, int flag) {
		int i = sMain.indexOf("</p>");
		int j = sMain.indexOf("</table>");

		if (flag == 0) { //web服务端口
			portInfo = sMain.substring(i + 4, j + 8);
		}
		if (flag == 1) { //web监控端口
			
			portInfo = sMain.substring(j + 8, sMain.length());
			//System.out.println("###"+portInfo+"###");
			try{
				portInfo = portInfo.substring(portInfo.indexOf("</p>") + 4,portInfo.indexOf("</table>"));
			}catch(Exception e){
				
			}
		}
		return portInfo;
	}

	//取得服务器端口总体信息(类型：属性：值：单位:行号)，这里行号设定为0
	public Hashtable hPORTInfoSum(String sMain, int flag) {
		Hashtable returnVal = new Hashtable();
		String portInfo = PORTInfo(sMain, flag);
		//System.out.println("---"+portInfo+"---");
		try{
			sPortHead = portInfo.substring(portInfo.indexOf("<h1>") + 4, portInfo.indexOf("</h1>"));
		}catch(Exception e){
			
		}
		String sContext = portInfo.substring(portInfo.indexOf("<p>") + 3,portInfo.indexOf("</p>"));
		//System.out.println("----------------"+sContext);
		//去除<br>
		sContext = sContext.replaceAll("<br>", " ");
		sContext = sContext.replaceAll("<br/>", " ");
		String[] tmp = sContext.trim().split(" ");

		StringBuffer sBuf = new StringBuffer(tmp[0]);
		for (int i = 1; i < tmp.length - 1; i++) {
			sBuf.append(" ");
			sBuf.append(tmp[i]);
			if (!tmp[i + 1].equals(tmp[i + 1].toLowerCase())) {
				if (!"MB".equals(tmp[i + 1])) {
					sBuf.append(";");
				}
			}
		}
		sBuf.append(" " + tmp[tmp.length - 1]); //得到用";"分隔的字符串
		tmp = null;
		//System.out.println("=========");
		//System.out.println(sBuf.toString());
		//System.out.println("=========");
		tmp = sBuf.toString().split(";");
		int hMainCurrentSize = hMain.size();

		for (int i = 0; i < tmp.length; i++) {
			String[] sTmp = tmp[i].split(":");
			String sPert = sTmp[0];
			String sValRaw = sTmp[1].trim();
			//System.out.println(sPert+"===="+sValRaw);
			String[] sTmp1 = sValRaw.split(" ");
			String sDw = "";
			if (sTmp1.length == 2) {
				sDw = sTmp1[1];
			}

			//String sContexts = sPortHead + ":" + sTmp1[0];
			String sContexts = sPert + ":" + sTmp1[0];
			//System.out.println("&&&&"+sContexts);
			returnVal.put(String.valueOf(i), sContexts);
		}
		return returnVal;
	}

	//取得服务器端口详细信息
	public Hashtable hPORTInfoDetail(String sMain, int flag) {
		Hashtable hMains = new Hashtable();
		hDetailPublic(portInfo);
		StringBuffer sb_raw = new StringBuffer();

		for (int i = 0; i < hContext.size(); i++) {
			String sContextRaw = hContext.get(String.valueOf(i)).toString();

			String[] sContextVal = sContextRaw.split(":");

			//分离值和单位
			String strDw = "null";
			String strVal = "";
			if (sContextVal.length > 0) {
				String[] valDw = sContextVal[0].split(" ");
				strVal = valDw[0];
				if (valDw.length > 1) {
					if (!valDw[1].startsWith("/")) {
						strDw = valDw[1];
					} else {
						strVal = valDw[0] + " " + valDw[1];
					}
				}
			}
			sb_raw.append(strVal);
			sb_raw.append(":");
		}
		sb_raw.append("huilet");
		sb_raw.toString();

		String[] tmp = sb_raw.toString().split(":");
		for (int i = 0; i < (tmp.length - 1) / 7; i++) {
			StringBuffer sub_buf = new StringBuffer();
			sub_buf.append(tmp[7 * i]);
			sub_buf.append(":");
			sub_buf.append(tmp[7 * i + 1]);
			sub_buf.append(":");
			sub_buf.append(tmp[7 * i + 2]);
			sub_buf.append(":");
			sub_buf.append(tmp[7 * i + 3]);
			sub_buf.append(":");
			sub_buf.append(tmp[7 * i + 4]);
			sub_buf.append(":");
			sub_buf.append(tmp[7 * i + 5]);
			sub_buf.append(":");
			sub_buf.append(tmp[7 * i + 6]);
			//System.out.println("$$$$$$$$$$"+sub_buf.toString());
			hMains.put(String.valueOf(i), sPortHead + ":" + sub_buf.toString());
		}

		return hMains;
	}

	public void hDetailPublic(String portInfo) {
		hHead = new Hashtable();
		hContext = new Hashtable();
		String sDetailRaw = portInfo.substring(portInfo.indexOf("<table"),
				portInfo.length());

		//分段
		String[] sSeg = sDetailRaw.split("</tr>");

		//处理表头
		int id = 0;
		StringBuffer bufHead = new StringBuffer(sSeg[0]);

		//去除标签，得到表头字段
		while (bufHead.toString().startsWith("<")) {
			if (bufHead.length() < 6)
				break;
			bufHead.delete(0, bufHead.indexOf(">") + 1);
			String tmp = bufHead.substring(0, bufHead.indexOf("<"));
			if (tmp == "" || "".equals(tmp)) {
			} else {
				hHead.put(String.valueOf(id), tmp);
				id++;
			}
			bufHead.delete(0, bufHead.indexOf("<"));
		}

		//处理内容{值：行号（1-N）：字段号（0-N）}
		int rid = 0;
		for (int i = 1; i < sSeg.length - 1; i++) {
			StringBuffer bufCont = new StringBuffer(sSeg[i]);
			int cid = 0;
			while (bufCont.toString().startsWith("<")) {
				if (bufCont.length() < 6)
					break;
				bufCont.delete(0, bufCont.indexOf(">") + 1);
				String tmp = bufCont.substring(0, bufCont.indexOf("<"));
				if (tmp == "" || "".equals(tmp)) {
				} else {
					hContext.put(String.valueOf(rid), tmp + ":"
							+ String.valueOf(i) + ":" + String.valueOf(cid));
					cid++;
					rid++;
				}
				bufCont.delete(0, bufCont.indexOf("<"));
			}

		}

	}

}