package com.afunms.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

//import com.afunms.config.dao.VlanDao;
//import com.afunms.config.model.Vlan;
import com.afunms.indicators.dao.GatherIndicatorsDao;
//import com.afunms.indicators.model.GatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
//import com.afunms.polling.node.IfEntity;

/**
 * @description TODO
 * @author wangxiangyong
 * @date Jun 25, 2012 6:44:05 PM
 */
public class CommonHelper extends SnmpMonitor{
	/**
	 * 根据类型获取指标值
	 * @param type：类型（net,host）
	 * @param rq
	 * @param cx
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Map<String, String> getTypeIndex(String type,HttpServletRequest rq,ServletContext cx) {
		Map<String, String> map = null;
		GatherIndicatorsDao dao = new GatherIndicatorsDao();

		if (type != null) {

			try {
				map = dao.getTypeIndexList(type);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		}
		map.put("不限", "不限");
		return map;
	}
	public List<String> getTypeList(String type,HttpServletRequest rq,ServletContext cx) {
		List<String> list = new ArrayList<String>();
		GatherIndicatorsDao dao = new GatherIndicatorsDao();
           list.add("不限");
		if (type != null) {

			try {
				 dao.getTypeList(type,list);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		}
		
		return list;
	}
	@SuppressWarnings("deprecation")
	public Map<String, String> getsubTypeIndex(String type,String subtype,HttpServletRequest rq,ServletContext cx) {
		Map<String, String> map = null;
		GatherIndicatorsDao dao = new GatherIndicatorsDao();

		if (type != null) {

			try {
				map = dao.getsubTypeIndexList(type,subtype);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		}
//		map.put("不限", "不限");
//		Collections.sort(map, new Comparator<Map.Entry<String, Integer>>() {   
//		    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {      
//		        //return (o2.getValue() - o1.getValue()); 
//		        return (o1.getKey()).toString().compareTo(o2.getKey());
//		    }
//		});
		return map;
	}
//	@SuppressWarnings("deprecation")
//	public List<Vlan> getVlanList(String nodeId,HttpServletRequest rq,ServletContext cx) {
//		List<Vlan> list=new ArrayList<Vlan>();
//		VlanDao vlanDao=new VlanDao();
//		try {
//			list=vlanDao.findByCondition(" where nodeId="+nodeId);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			vlanDao.close();
//		}
//		return list;
//	}
//
//	public synchronized static List<IfEntity> getSortListByHash(Hashtable<String, IfEntity> orignalHash) {
//		if (orignalHash == null) {
//			return null;
//		}
//		List<IfEntity> retList = new ArrayList<IfEntity>();
//		Iterator<String> iterator = orignalHash.keySet().iterator();
//		while (iterator.hasNext()) {
//			String key = iterator.next();
//			retList.add(orignalHash.get(key));
//		}
//		Collections.sort(retList);
//		return retList;
//	}
	@SuppressWarnings("deprecation")
	public Map<String, String> setTimerFlag(String parm,HttpServletRequest rq,ServletContext cx) {
		
		String flag=ShareData.getControlVoice();
		String tempFlag="";
		if(parm.equals("1")&&flag.equals("1")){
			tempFlag="0";
			ShareData.setControlVoice("0");
		}else if(parm.equals("1")){
			tempFlag="1";
			ShareData.setControlVoice("1");
		}else{
			tempFlag="2";
		}
			
		Map map=new HashMap();
		map.put("flag", tempFlag);
		return map;
	}
}
