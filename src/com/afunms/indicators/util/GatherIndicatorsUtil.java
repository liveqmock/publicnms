package com.afunms.indicators.util;

import java.util.ArrayList;
import java.util.List;

import com.afunms.indicators.dao.GatherIndicatorsDao;
import com.afunms.indicators.model.GatherIndicators;


/**
 * 此类为 当设备添加时 给设备进行添加默认地监控指标
 * @author Administrator
 *
 */

public class GatherIndicatorsUtil {
	
//	/**
//	 * 通过 设备类型 ， 子类型和设备id 添加默认监控指标
//	 * @param nodeid
//	 * @param type
//	 * @param subtype
//	 */
//	public void addIndicatorsForNode(String nodeid , String type , String subtype){
//		
//		
//	}
	
	/**
	 * 通过类型 ， 子类型 来获取默认监控指标列表
	 * @param type
	 * @param subtype
	 * @return
	 */
	public List<GatherIndicators> getGatherIndicatorsByTypeAndSubtype(String type , String subtype){
		List<GatherIndicators> list = null;
		GatherIndicatorsDao gatherIndicatorsDao = new GatherIndicatorsDao();
		try {
			list = gatherIndicatorsDao.getByTypeAndSubtype(type , subtype);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			gatherIndicatorsDao.close();
		}
		return list;
	}
	
	
	
	/**
	 * 通过类型 ， 子类型 来获取默认监控指标列表
	 * @param type 类型
	 * @param subtype 子类型
	 * @param Collecttype 采集方式
	 * @return 采集指标列表
	 */
	public List<GatherIndicators> getGatherIndicatorsByTypeAndSubtype(String type , String subtype,String flag,int Collecttype){
		List<GatherIndicators> list = null;
		GatherIndicatorsDao gatherIndicatorsDao = new GatherIndicatorsDao();
		try {
			list = gatherIndicatorsDao.getByTypeAndSubtype(type , subtype,flag,Collecttype);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			gatherIndicatorsDao.close();
		}
		return list;
	}
	
	
	/**
	 * 通过类型 ， 子类型 来获取默认监控指标列表
	 * @param type
	 * @param subtype
	 * @return
	 */
	public List<GatherIndicators> getGatherIndicatorsByTypeAndSubtype(String type , String subtype,String flag){
		List<GatherIndicators> list = null;
		GatherIndicatorsDao gatherIndicatorsDao = new GatherIndicatorsDao();
		try {
			list = gatherIndicatorsDao.getByTypeAndSubtype(type , subtype,flag);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			gatherIndicatorsDao.close();
		}
		return list;
	}
	
	/**
	 * 通过类型 ， 子类型,是否默认,指标名称 来获取默认监控指标列表
	 * @param type
	 * @param subtype
	 * @param flag
	 * @param indename
	 * @return
	 */
	public List<GatherIndicators> getGatherIndicatorsByTypeAndSubtype(String type , String subtype,String flag,String indename){
		List<GatherIndicators> list = null;
		GatherIndicatorsDao gatherIndicatorsDao = new GatherIndicatorsDao();
		try {
			list = gatherIndicatorsDao.getByTypeAndSubtype(type , subtype,flag,indename);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			gatherIndicatorsDao.close();
		}
		return list;
	}
	
	
	/**
	 * 通过类型 ， 子类型 来获取默认监控指标列表
	 * @param type
	 * @param subtype
	 * @return
	 */
	public List<GatherIndicators> getGatherIndicatorsByIds(String[] ids){
		List<GatherIndicators> list = new ArrayList<GatherIndicators>();
		GatherIndicatorsDao gatherIndicatorsDao = new GatherIndicatorsDao();
		try {
			for(int i = 0 ; i < ids.length ; i ++){
				GatherIndicators gatherIndicators = (GatherIndicators)gatherIndicatorsDao.findByID(ids[i]);
				list.add(gatherIndicators);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			gatherIndicatorsDao.close();
		}
		return list;
	}
	
}
