package com.afunms.report.amchart;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.afunms.common.util.CommonUtil;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Sep 30, 2011 2:31:41 PM
 * 类说明
 */
public class AmChartTool {
	/**
	 * 组合amcharts数据格式
	 * @param dataList
	 * @param ipList
	 * @return
	 */
	public String makeAmChartData(List dataList,List ipList){
		StringBuffer sb = new StringBuffer();
		String data = "";
		if (dataList != null && dataList.size() > 0) {
			sb.append("<chart><series>");
			List eachDataList = (List) dataList.get(0);
			for (int k = 0; k < eachDataList.size(); k++) {
				Vector v = new Vector();
				v = (Vector) eachDataList.get(k);
				sb.append("<value xid='");
				sb.append(k);
				sb.append("'>");
				sb.append(v.get(1));
				sb.append("</value>");
			}
			sb.append("</series><graphs>");
			for (int j = 0; j < dataList.size(); j++) {
				List dataList1 = (List) dataList.get(j);
				sb.append("<graph title='" + (String) ipList.get(j) + "' bullet='round_outlined' bullet_size='4'>");
				if (dataList1 != null && dataList1.size() > 0) {
					for (int m = 0; m < dataList1.size(); m++) {
						Vector v = new Vector();
						v = (Vector) dataList1.get(m);
						sb.append("<value xid='");
						sb.append(m);
						sb.append("'>");
						sb.append(v.get(0));
						sb.append("</value>");
					}
				}
				sb.append("</graph>");
			}
			sb.append("</graphs></chart>");
			data = sb.toString();

		} else {
			data = "0";
		}
		return data;
	}
	
	/**
	 * 得到网络设备流速图
	 * @param netflowHash
	 * @return
	 */
	public String getNetNetFlowChart(Hashtable netflowHash){
		StringBuffer sBuffer = new StringBuffer();
		if(netflowHash.isEmpty()){
			return null;
		}
		String[] colorStr = new String[] { "#33CCFF", "#003366", "#33FF33", "#FF0033", "#9900FF", "#FFFF00", "#333399", "#0000FF", "#A52A2A", "#23f266" };
		sBuffer.append("<chart><series>");
		Iterator tempIterator = netflowHash.keySet().iterator();
		while(tempIterator.hasNext()){
			String srcip = (String)tempIterator.next();
			double thevalue = (Double) netflowHash.get(srcip);
			thevalue = Double.parseDouble(CommonUtil.format(thevalue, 3));
			sBuffer.append("<value xid='");
			sBuffer.append(srcip);
			sBuffer.append("'>");
			sBuffer.append(srcip);
			sBuffer.append("</value>");
		}
		sBuffer.append("</series><graphs><graph gid='0'>");
		Iterator tempIteratorTemp = netflowHash.keySet().iterator();
		int i=0;
		while(tempIteratorTemp.hasNext()){
			i++;
			if(i == colorStr.length -1){
				i = 0;
			}
			String srcip = (String)tempIteratorTemp.next();
			double thevalue = (Double) netflowHash.get(srcip);
			thevalue = Double.parseDouble(CommonUtil.format(thevalue, 3));
			sBuffer.append("<value xid='");
			sBuffer.append(srcip);
			sBuffer.append("' color='");
			sBuffer.append(colorStr[i]);
			sBuffer.append("'>");
			sBuffer.append(thevalue);
			sBuffer.append("</value>");
		}
		sBuffer.append("</graph></graphs></chart>");
//		System.out.println(sBuffer);
		return sBuffer.toString();
	}
	
	/**
	 * 得到网络设备流速曲线图
	 * @param netflowdata
	 * @return
	 */
	public String getNetNetFlowLineChart(List netflowdata){
		StringBuffer sBuffer = new StringBuffer();
		if(netflowdata == null || netflowdata.size() == 0){
			return null;
		}
		//构建日期的List集合
		List dateList = new ArrayList();
		//源IP地址的集合
		Set srcipSet = new HashSet();
		for(int i=0; i<netflowdata.size(); i++){
			Vector vec = (Vector) netflowdata.get(i);
			if(!srcipSet.contains(vec.get(0))){
				srcipSet.add(vec.get(0));
			}
			if(dateList.contains(vec.get(2))){
				continue;
			}else{
				dateList.add(vec.get(2));
			}
		}
		//构建每条曲线的集合 Hashtable<srcip,List<string>> 按源IP来分的曲线
		Hashtable netflowHash = new Hashtable();
		Iterator iterator = srcipSet.iterator();
		System.out.println("srcipSet=="+srcipSet);
		while(iterator.hasNext()){
			String srcip = (String)iterator.next();
			List netflowlineList = new ArrayList();
			for(int i=0; i<netflowdata.size(); i++){
				Vector vec = (Vector) netflowdata.get(i);
				String srciptemp = (String)vec.get(0); 
				String thevalue = (String)vec.get(1);
				if(srcip.equals(srciptemp)){
					netflowlineList.add(thevalue);
				}
			}
			System.out.println("netflowlineList===="+netflowlineList);
			netflowHash.put(srcip, netflowlineList);
		}
		sBuffer.append("<chart><series>");
		for(int i=0; i<dateList.size(); i++){
			sBuffer.append("<value xid='");
			sBuffer.append(i);//从0开始计数
			sBuffer.append("'>");
			sBuffer.append(dateList.get(i));
			sBuffer.append("</value>");
		}
		sBuffer.append("</series><graphs>");//
		Iterator tempIterator = netflowHash.keySet().iterator();
		int i=0;
		while(tempIterator.hasNext()){
			i++;
			String srcip = (String)tempIterator.next();
			List tempdateList = (ArrayList)netflowHash.get(srcip);
			sBuffer.append("<graph gid='");
			sBuffer.append(i);
			sBuffer.append("' title='");
			sBuffer.append(srcip);
			sBuffer.append("'>");
			for(int num=0; num<tempdateList.size(); num++){
				sBuffer.append("<value xid='");
				sBuffer.append(num);
				sBuffer.append("'>");
				sBuffer.append(tempdateList.get(num));
				sBuffer.append("</value>");
			}
			sBuffer.append("</graph>");
		}
		sBuffer.append("</graphs></chart>");
		System.out.println(sBuffer);
		return sBuffer.toString();
	}
	
	/**
	 * 得到网络设备详细流速图
	 * @param netflowList
	 * @return
	 */
	public String getNetNetFlowChart(List netflowList){
		StringBuffer sBuffer = new StringBuffer();
		if(netflowList == null || netflowList.size() == 0){
			return null;
		}
		String[] colorStr = new String[] { "#33CCFF", "#003366", "#33FF33", "#FF0033", "#9900FF", "#FFFF00", "#333399", "#0000FF", "#A52A2A", "#23f266" };
		sBuffer.append("<chart><series>");
		for(int i=0; i<netflowList.size(); i++){
			Vector vec = (Vector)netflowList.get(i);
//			String srcip = (String)vec.get(0);
			String destip = (String) vec.get(1);
//			String thevalue = (String) vec.get(2);
			sBuffer.append("<value xid='");
			sBuffer.append(destip);
			sBuffer.append("'>");
			sBuffer.append(destip);
			sBuffer.append("</value>");
		}
		sBuffer.append("</series><graphs><graph gid='0'>");
		for(int i=0; i<netflowList.size(); i++){
			Vector vec = (Vector)netflowList.get(i);
			String srcip = (String)vec.get(0);
			String destip = (String) vec.get(1);
			String thevalue = (String) vec.get(2);
			sBuffer.append("<value xid='");
			sBuffer.append(destip);
			sBuffer.append("' color='");
			sBuffer.append(colorStr[i]);
			sBuffer.append("'>");
			sBuffer.append(thevalue);
			sBuffer.append("</value>");
		}
		sBuffer.append("</graph></graphs></chart>");
//		System.out.println(sBuffer);
		return sBuffer.toString();
	}
}
