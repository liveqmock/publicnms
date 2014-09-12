/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2009-10-29
 */

package com.afunms.application.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;

import com.afunms.application.dao.SerialNodeDao;
import com.afunms.application.dao.TemperatureHumidityDao;
import com.afunms.application.dao.TemperatureHumidityThresholdDao;
import com.afunms.application.model.SerialNode;
import com.afunms.application.model.TemperatureHumidityConfig;
import com.afunms.application.model.TemperatureHumidityThresholdConfig;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.config.dao.BusinessDao;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.topology.util.KeyGenerator;


public class TemperatureHumidityManager extends BaseManager implements ManagerInterface
{
	public String execute(String action) 
	{	
        if("list".equals(action)){
        	return list();    
        }else if("list2".equals(action)){
        	return list2();
        }else if("readyAdd".equals(action)){
        	return readyAdd();
        }else if("add".equals(action)){
        	return add();
        }else if("readyEdit".equals(action)){
        	return readyEdit();
        }else if("edit".equals(action)){
        	return edit();
        }else if("delete".equals(action)){
        	return delete();
        }else if("showCurvGraph".equals(action)){
        	return showCurvGraph();
        }
//        else if("listImage".equals(action)){
//        	return listImage();
//        }
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	
	/**
	 * 返回温湿度参数配置的列表界面
	 * @return
	 */
	private String list(){
		String jsp = "/application/environment/temperaturehumidity/list.jsp";
		List list = null;
		
		// 用于存放最新的温湿度数据
		Hashtable temperatureHumidityHashtable = new Hashtable();
		
		// 用于存放温湿度告警阀值配置
		Hashtable temperatureHumidityThresholdHashtable = new Hashtable();
		List temperatureHumidityList = null;
		
		SerialNodeDao serialNodeDao = new SerialNodeDao();
		try{
			// 获取分页的记录数
			int perpage = getPerPagenum();
			// 有条件的分页
		    list = serialNodeDao.listByPage(getCurrentPage(),"",perpage);
		    
		    request.setAttribute("page",serialNodeDao.getPage());
		    
		    if(list!=null&&list.size()>0){
		    	TemperatureHumidityDao temperatureHumidityDao = null;
		    	TemperatureHumidityThresholdDao temperatureHumidityThresholdDao = null;
		    	for(int i = 0 ; i < list.size() ; i++){
		    		try {
		    			temperatureHumidityDao = new TemperatureHumidityDao();
		    			temperatureHumidityThresholdDao = new TemperatureHumidityThresholdDao();
						SerialNode serialNode = (SerialNode)list.get(i);
						
						// 根据传感器id获取其温湿度数据列表
						temperatureHumidityList = temperatureHumidityDao.findByNodeId(String.valueOf(serialNode.getId()));
						
						// 根据传感器id获取器温湿度告警阀值配置
						TemperatureHumidityThresholdConfig temperatureHumidityThresholdConfig
									=	temperatureHumidityThresholdDao.findByNodeId(String.valueOf(serialNode.getId()));
						
						// 若数据库中没有温湿度数据则显示为0 时间为"-- --"
						if(temperatureHumidityList == null || temperatureHumidityList.size()==0){
							temperatureHumidityList = new ArrayList();
							TemperatureHumidityConfig temperatureHumidityConfig = new TemperatureHumidityConfig();
							temperatureHumidityConfig.setId(0);
							temperatureHumidityConfig.setNode_id(String.valueOf(serialNode.getId()));
							temperatureHumidityConfig.setTemperature("0");
							temperatureHumidityConfig.setHumidity("0");
							temperatureHumidityConfig.setTime("-- --");
							temperatureHumidityList.add(temperatureHumidityConfig);
						}
						// 将此传感器的最新温湿度数据放入到 temperatureHumidityHashtable 中
						temperatureHumidityHashtable.put(serialNode.getId(), temperatureHumidityList.get(0));
						
						// 将此传感器的温湿度告警阀值 temperatureHumidityThresholdHashtable 中
						temperatureHumidityThresholdHashtable.put(serialNode.getId(), temperatureHumidityThresholdConfig);
					} catch (RuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						temperatureHumidityDao.close();
						temperatureHumidityThresholdDao.close();
					}
		    	}
		    }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			serialNodeDao.close();
		}
		request.setAttribute("list", list);
		request.setAttribute("temperatureHumidityHashtable", temperatureHumidityHashtable);
		request.setAttribute("temperatureHumidityThresholdHashtable", temperatureHumidityThresholdHashtable);
		return jsp;
	}
	
	/**
	 * 返回温湿度监测界面
	 * @return
	 */
	private String list2(){
		String jsp = "/application/environment/temperaturehumidity/list2.jsp";
		
		List list = null;
		Hashtable temperatureHumidityHashtable = new Hashtable();
		Hashtable imageHashtable = new Hashtable();
		Hashtable temperatureHumidityStatisticsHashtable = new Hashtable();
		
		SerialNodeDao serialNodeDao = new SerialNodeDao();
		try {
			list = serialNodeDao.loadAll();
			if(list == null || list.size() == 0){
				request.setAttribute("message", "没有温湿度传感器 请添加温湿度传感器！！！");
				return "/application/environment/temperaturehumidity/savefailed.jsp";
			}
//			// 查找出所有的传感器设备
//			list = serialNodeDao.loadAll();
//			if(list == null || list.size() == 0){
//				list = new ArrayList();
//			}
//			TemperatureHumidityDao temperatureHumidityDao = null;
//			List temperatureHumidityList = null;
//			for(int i = 0 ; i < list.size() ; i++){
//	    		try {
//	    			SerialNode serialNode = (SerialNode) list.get(i);
//
//	    			temperatureHumidityDao = new TemperatureHumidityDao();
//	    			
//	    			// 根据传感器id来查找出其温湿度数据
//	    			temperatureHumidityList = temperatureHumidityDao.findByNodeId(String.valueOf(serialNode.getId()));
//	    			
//	    			// 若数据库中没有温湿度数据则显示为0 时间为"-- --"
//	    			if(temperatureHumidityList == null || temperatureHumidityList.size() == 0){
//	    				TemperatureHumidityConfig temperatureHumidityConfig = new TemperatureHumidityConfig();
//	    				temperatureHumidityConfig.setId(-1);
//	    				temperatureHumidityConfig.setNode_id(String.valueOf(serialNode.getId()));
//	    				temperatureHumidityConfig.setTemperature("0");
//	    				temperatureHumidityConfig.setHumidity("0");
//	    				temperatureHumidityConfig.setTime("-- --");
//	    				
//	    				temperatureHumidityList = new ArrayList();
//	    				temperatureHumidityList.add(temperatureHumidityConfig);
//	    			}
//	    			
//	    			// 将此传感器的最新温湿度数据放入到 temperatureHumidityHashtable 中
//	    			temperatureHumidityHashtable.put(serialNode.getId()+"", temperatureHumidityList.get(0));
//	    			
//	    			// 将此传感器统计的数据放入到 temperatureHumidityStatisticsHashtable
//	    			temperatureHumidityStatisticsHashtable.put(serialNode.getId()+"", getStatistics(String.valueOf(serialNode.getId())));
//	    			
//	    			// 将绘制的曲线图放入到 imageHashtable 
//	    			imageHashtable.put(serialNode.getId()+"", drawDefaultImage(String.valueOf(serialNode.getId())));
//	    			
//	    			
//	    		} catch (RuntimeException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}finally{
//					temperatureHumidityDao.close();
//				}
//	    	}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			serialNodeDao.close();
		}
		request.setAttribute("list", list);
//		request.setAttribute("temperatureHumidityHashtable", temperatureHumidityHashtable);
//		request.setAttribute("temperatureHumidityStatisticsHashtable", temperatureHumidityStatisticsHashtable);
//		request.setAttribute("imageHashtable", imageHashtable);
		return jsp;
	}
	
	/**
	 * 返回添加页面
	 * @return
	 */
	
	private String readyAdd(){
		String jsp = "/application/environment/temperaturehumidity/add.jsp";
		List allbuss = null;
		BusinessDao bussdao = new BusinessDao();
		try {
			allbuss = bussdao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			bussdao.close();
		}
		request.setAttribute("allbuss", allbuss);
		return jsp;
	}
	
	/**
	 * 
	 * @author nielin
	 * create on 2010-02-20
	 * @return
	 */
	private String add(){
		boolean result = false;
		
		// 根据页面参数创建 serialNode
		SerialNode serialNode = createSerialNode();
		
		// 设置主键id
		serialNode.setId(KeyGenerator.getInstance().getNextKey());
		
		// 根据页面参数创建温湿度告警阀值配置
		TemperatureHumidityThresholdConfig temperatureHumidityThresholdConfig = 
			createTemperatureHumidityThresholdConfig();
		
		// 设置serialNodeId
		temperatureHumidityThresholdConfig.setNode_id(String.valueOf(serialNode.getId()));
		SerialNodeDao serialNodeDao = new SerialNodeDao();
		try {
			result = serialNodeDao.save(serialNode);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			serialNodeDao.close();
		}
		if(result){
			TemperatureHumidityThresholdDao temperatureHumidityThresholdDao = new TemperatureHumidityThresholdDao();
			try {
				temperatureHumidityThresholdDao.save(temperatureHumidityThresholdConfig);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				temperatureHumidityThresholdDao.close();
			}
		}
		
		// 保存告警分时
		TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
		timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(serialNode.getId()), timeShareConfigUtil.getObjectType("15"));
		return list();
	}
	
	
	/**
	 * 返回编辑页面
	 * @author nielin
	 * create on 2010-02-21
	 * @return
	 */
	private String readyEdit(){
		String jsp = "/application/environment/temperaturehumidity/edit.jsp";
		String id = request.getParameter("serialNodeId");
		SerialNode serialNode = null;
		SerialNodeDao serialNodeDao = new SerialNodeDao();
		try {
			serialNode = (SerialNode) serialNodeDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			serialNodeDao.close();
		}
		TemperatureHumidityThresholdConfig temperatureHumidityThresholdConfig = null;
		TemperatureHumidityThresholdDao temperatureHumidityThresholdDao = new TemperatureHumidityThresholdDao();
		try {
			temperatureHumidityThresholdConfig = temperatureHumidityThresholdDao.findByNodeId(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			temperatureHumidityThresholdDao.close();
		}
		if(serialNode==null||temperatureHumidityThresholdConfig==null){
			saveFailed();
		}
		TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
		List timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(id, timeShareConfigUtil.getObjectType("15"));
		List allbuss = null;
		BusinessDao bussdao = new BusinessDao();
		try {
			allbuss = bussdao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			bussdao.close();
		}
		request.setAttribute("allbuss", allbuss);
		request.setAttribute("serialNode", serialNode);
		request.setAttribute("temperatureHumidityThresholdConfig", temperatureHumidityThresholdConfig);
		request.setAttribute("timeShareConfigList", timeShareConfigList);
		return jsp;
	}
	
	/**
	 * handle edit sensor parameters 
	 * 
	 * @author nielin
	 * create on 2010-02-03
	 * @return
	 */
	private String edit(){
		boolean result = false;
		// 获取需要编辑的传感器id
		String serialNodeId = request.getParameter("serialNodeId");
		if(serialNodeId == null || "".equals(serialNodeId)){
			System.out.println("serialNodeId is null");
			return null;
		}
		
		// 根据页面参数创建传感器
		SerialNode serialNode = createSerialNode();
		serialNode.setId(Integer.valueOf(serialNodeId));
		
		// 根据页面参数创建温湿度传感器阀值类
		TemperatureHumidityThresholdConfig temperatureHumidityThresholdConfig = 
			createTemperatureHumidityThresholdConfig();
		
		String temperatureHumidityThresholdConfigId = request.getParameter("serialNodeThresoldId");
		
		if(temperatureHumidityThresholdConfigId == null || "".equals(temperatureHumidityThresholdConfigId)){
			return null;
		}
		
		temperatureHumidityThresholdConfig.setId(Integer.valueOf(temperatureHumidityThresholdConfigId));
		temperatureHumidityThresholdConfig.setNode_id(serialNodeId);
		
		SerialNodeDao serialNodeDao = new SerialNodeDao();
		try {
			// 将修改的传感器参数存入数据库
			result = serialNodeDao.update(serialNode);
			if(result){
				// 如果成功 则将修改传感器阀值参数存入数据库
				TemperatureHumidityThresholdDao temperatureHumidityThresholdDao = 
					new TemperatureHumidityThresholdDao();
				try {
					result = temperatureHumidityThresholdDao.update(temperatureHumidityThresholdConfig);
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					result = false;
					e.printStackTrace();
				}finally{
					temperatureHumidityThresholdDao.close();
				}
				
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
				timeShareConfigUtil.saveTimeShareConfigList(request, serialNodeId, timeShareConfigUtil.getObjectType("15"));
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			result = false;
			e.printStackTrace();
		}finally{
			serialNodeDao.close();
		}
		if(result){
			// 成功返回列表页面
			return list();
		}
		return saveFailed();
	}
	
	
	/**
	 * @author nielin
	 * create on 2010-02-20
	 * @return
	 */
	private String delete(){
		String[] ids = getParaArrayValue("checkbox");
		if(ids == null || ids.length == 0){
			return saveFailed();
		}
		SerialNodeDao serialNodeDao = new SerialNodeDao();
		try {
			serialNodeDao.delete(ids);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally{
			serialNodeDao.close();
		}
		TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
		TemperatureHumidityThresholdDao temperatureHumidityThresholdDao = new TemperatureHumidityThresholdDao();
		TemperatureHumidityDao temperatureHumidityDao = new TemperatureHumidityDao();
		
		try {
			for(int i = 0 ; i < ids.length ; i ++){
				// 删除此传感器的温湿度告警阀值配置
				temperatureHumidityThresholdDao.deleteByNodeId(ids[i]);
				
				// 删除此传感器温湿度数据
				temperatureHumidityDao.deleteByNodeId(ids[i]);
				
				// 删除此传感器分时告警数据
				timeShareConfigUtil.deleteTimeShareConfig(ids[i], timeShareConfigUtil.getObjectType("15"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			temperatureHumidityThresholdDao.close();
			temperatureHumidityDao.close();
		}
		return list();
	}
	
	/**
	 * 显示曲线图
	 * @return
	 */
	private String showCurvGraph(){
		
		// 获取开始日期
		String startDate = getStartDate();
		// 获取结束日期
		String endDate = getEndDate();
		String timeType = request.getParameter("timeType");
		String serialNodeId = request.getParameter("serialNodeId");
		String startTime = startDate + " 00:00:00";
		String endTime = endDate + " 23:59:59";
		if(timeType == null || "".equals(timeType)){
			timeType = "minute";
		}
		TemperatureHumidityDao temperatureHumidityDao = new TemperatureHumidityDao();
		List list = null;
		try {
			list = temperatureHumidityDao.findByNodeIdAndTime(serialNodeId, startTime, endTime);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 图片名称
		String imageName = "temperature_humidity_query";
		
		// 绘制曲线图
		drawTemperatureHumidity(list, timeType, "温湿度", imageName, 500, 200);
		
		
		Hashtable queryImageHashtable = new Hashtable();
		queryImageHashtable.put("imageName", imageName+".png");
		queryImageHashtable.put("timeType", timeType);
		queryImageHashtable.put("startDate", startDate);
		queryImageHashtable.put("endDate", endDate);
		queryImageHashtable.put("serialNodeId", serialNodeId);
		request.setAttribute("queryImageHashtable", queryImageHashtable);
		
		String jsp = request.getParameter("jsp");
		
		if(jsp!=null && "list".equals(jsp)){
			return list();
		}
		return list2();
	}
	
	/**
	 * 返回列表界面
	 * @return
	 */
	private String listImage(){
		String jsp = "/application/environment/temperaturehumidity/listImage.jsp";
		
		// 获取选中的id 
		// get select id
		String selcetId = request.getParameter("selectId"); 
		
		SerialNode selectSerialNode = new SerialNode();
		
		SerialNode serialNode = new SerialNode();
		
		List list = null;
		Hashtable temperatureHumidityHashtable = new Hashtable();
		Hashtable temperatureHumidityStatisticsHashtable = new Hashtable();
		TemperatureHumidityThresholdConfig temperatureHumidityThresholdConfig = new TemperatureHumidityThresholdConfig() ;
		
		SerialNodeDao serialNodeDao = new SerialNodeDao();
		try {
			// 查找出所有的传感器设备
			list = serialNodeDao.loadAll();
			if(list == null || list.size() == 0){
				return null;
			}
			TemperatureHumidityDao temperatureHumidityDao = null;
			List temperatureHumidityList = null;
			for(int i = 0 ; i < list.size() ; i++){
	    		try {
	    			serialNode = (SerialNode) list.get(i);
	    			if(selcetId == null || "".equals(selcetId)){
	    				selcetId = String.valueOf(((SerialNode) list.get(0)).getId());
	    			}
	    			temperatureHumidityDao = new TemperatureHumidityDao();
	    			
	    			// 根据传感器id来查找出其温湿度数据
	    			temperatureHumidityList = temperatureHumidityDao.findByNodeId(String.valueOf(serialNode.getId()));
	    			
	    			if(temperatureHumidityList == null || temperatureHumidityList.size() == 0){
	    				TemperatureHumidityConfig temperatureHumidityConfig = new TemperatureHumidityConfig();
	    				temperatureHumidityConfig.setId(-1);
	    				temperatureHumidityConfig.setNode_id(String.valueOf(serialNode.getId()));
	    				temperatureHumidityConfig.setTemperature("0");
	    				temperatureHumidityConfig.setHumidity("0");
	    				temperatureHumidityConfig.setTime("-- --");
	    				
	    				temperatureHumidityList = new ArrayList();
	    				temperatureHumidityList.add(temperatureHumidityConfig);
	    			}
	    			
	    			// 将最近一次测试的数据放入hashtable
	    			temperatureHumidityHashtable.put(serialNode.getId(), temperatureHumidityList.get(0));
	    			//
	    			temperatureHumidityStatisticsHashtable.put(serialNode.getId()+"", getStatistics(String.valueOf(serialNode.getId())));
	    			
	    			if(selcetId.equals(String.valueOf(((SerialNode) list.get(i)).getId()))){
	    				
	    				selectSerialNode = serialNode;
	    				
	    				// 绘制选中的传感器的温湿度图片
	    				drawImage(selcetId);
	    				
	    				
	    				TemperatureHumidityThresholdDao temperatureHumidityThresholdDao =
	    					new TemperatureHumidityThresholdDao();
	    				temperatureHumidityThresholdConfig =
	    				temperatureHumidityThresholdDao.findByNodeId(selcetId);
	    			}
	    			
	    			
	    		} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					temperatureHumidityDao.close();
				}
	    	}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			serialNodeDao.close();
		}
		request.setAttribute("selectSerialNode", selectSerialNode);
		request.setAttribute("selcetId", selcetId);
		request.setAttribute("list", list);
		request.setAttribute("temperatureHumidityHashtable", temperatureHumidityHashtable);
		request.setAttribute("temperatureHumidityStatisticsHashtable", temperatureHumidityStatisticsHashtable);
		request.setAttribute("temperatureHumidityThresholdConfig", temperatureHumidityThresholdConfig);
		return jsp;
	}
	
	/**
	 * 
	 * Get statistical data on the same day temperature and humidity by serialNodeId
	 * @param serialNodeId
	 * @return
	 * 
	 * @author nielin 
	 * create on 2010-02-03
	 */
	private Hashtable getStatistics(String serialNodeId){
		if(serialNodeId == null || "".equals(serialNodeId)){
			return null;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());
		String startTime = date + " 00:00:00";
		String endTime = date + " 23:59:59";
		// 使用默认的时间类获取统计数据
		return getStatistics( serialNodeId ,  startTime ,  endTime);
	}
	
	/**
	 * Get statistical data by serialNodeId , start time , end time.
	 * @param serialNodeId
	 * @return
	 * 
	 * @author nielin 
	 * create on 2010-02-03
	 */
	public Hashtable getStatistics(String serialNodeId , String startTime , String endTime){
		if(serialNodeId == null || "".equals(serialNodeId)){
			return null;
		}
		TemperatureHumidityDao temperatureHumidityDao = new TemperatureHumidityDao();
		
		List list = null;
		try {
			list = temperatureHumidityDao.findByNodeIdAndTime(serialNodeId, startTime, endTime);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			temperatureHumidityDao.close();
		}
		return getStatistics(list);
	}
	
	/**
	 * This function is used to obtain statistical data by list
	 * 
	 * @author nielin 
	 * create on 2010-02-03
	 * 
	 * @param list
	 * @return
	 */
	private Hashtable getStatistics(List list){
		Hashtable hashtable = new Hashtable();
		String countTmeperature = "0";
		String countHumidity = "0";
		String maxTemperature = "0";
		String avgTemperature = "0";
		String minTemperature = "0";
		String maxHumidity = "0";
		String avgHumidity = "0";
		String minHumidity = "0";
		if(list != null && list.size()>0){
			List temperatureList = new ArrayList();
			List humidityList = new ArrayList();
			for(int i = 0 ; i < list.size() ; i++){
				TemperatureHumidityConfig temperatureHumidityConfig  = (TemperatureHumidityConfig) list.get(i);
				String temperature = temperatureHumidityConfig.getTemperature() ; 
				String humidity = temperatureHumidityConfig.getHumidity();
				temperatureList.add(temperature);
				humidityList.add(humidity);
			}
			
			// 获取温度的最大值
			maxTemperature = getMax(temperatureList);
			// 获取温度的最小值
			minTemperature = getMin(temperatureList);
			// 获取温度的总和
			countTmeperature = getCount(temperatureList);
			
			// 获取湿度的最大值
			maxHumidity = getMax(humidityList);
			// 获取湿度的最小值
			minHumidity = getMin(humidityList);
			// 获取湿度的总和
			countHumidity = getCount(humidityList);
			
			// 获取温度平均值
			avgTemperature = Double.valueOf(Double.valueOf(countTmeperature)/(temperatureList.size()))+"";
			// 获取湿度平均值
			avgHumidity = Double.valueOf(Double.valueOf(countHumidity)/(humidityList.size()))+"";
		}
		
		hashtable.put("maxTemperature", formate(maxTemperature));
		hashtable.put("avgTemperature", formate(avgTemperature));
		hashtable.put("minTemperature", formate(minTemperature));
		hashtable.put("maxHumidity", formate(maxHumidity));
		hashtable.put("avgHumidity", formate(avgHumidity));
		hashtable.put("minHumidity", formate(minHumidity));
		
		
		return hashtable;
	}
	
	/**
	 * 此函数用于将数据格式化,保留两位小数
	 * This function is used to format the data to retain two decimal places
	 * 
	 * @author nielin 
	 * create on 2010-02-03
	 * @param number
	 * @return
	 */
	private String formate(String number){
		java.text.NumberFormat   formate   =   java.text.NumberFormat.getNumberInstance();   
		formate.setMaximumFractionDigits(2);//设定小数最大为数   ，那么显示的最后会四舍五入的  
		return formate.format(Double.valueOf(number));
	}
	
	/**
	 * This function is used to obtain the maximum of the list 
	 * 
	 * @author nielin 
	 * create on 2010-02-03
	 * @param list
	 * @return
	 */
	private String getMax(List list){
		String max = (String) list.get(0);
		for(int i = 0 ; i < list.size() ; i++){
			if(Double.valueOf(max) < Double.valueOf((String)list.get(i))){
				max = (String) list.get(i);
			}
		}
		return max;
	}
	
	/**
	 * This function is used to obtain the minimum of the list 
	 * 
	 * @author nielin 
	 * create on 2010-02-03
	 * @param list
	 * @return
	 */
	private String getMin(List list){
		String min = (String) list.get(0);
		for(int i = 0 ; i < list.size() ; i++){
			if(Double.valueOf(min) > Double.valueOf((String)list.get(i))){
				min = (String) list.get(i);
			}
		}
		return min;
	}
	
	/**
	 * This function is used to obtain the sum of the list 
	 * 
	 * @author nielin 
	 * create on 2010-02-03
	 * @param list
	 * @return
	 */
	private String getCount(List list){
		double count = 0;
		for(int i = 0 ; i < list.size() ; i++){
			count = count + Double.valueOf((String)list.get(i));
		}
		return count+"";
	}
	
	/** 
	 * According to sensor id to draw the curve of temperature and humidity
	 * @param serialNodeId
	 * @return
	 * 
	 * @author nielin 
	 * create on 2010-02-03
	 * 
	 */
	private Hashtable drawImage(String serialNodeId){
		String startDate = getStartDate();
		String endDate = getEndDate();
		String timeType = request.getParameter("timeType");
		String startTime = startDate + " 00:00:00";
		String endTime = endDate + " 23:59:59";
		if(timeType == null || "".equals(timeType)){
			timeType = "minute";
		}
		TemperatureHumidityDao temperatureHumidityDao = new TemperatureHumidityDao();
		List list = temperatureHumidityDao.findByNodeIdAndTime(serialNodeId, startTime, endTime);
		String imageName = "temperature_humidity" + "_" + serialNodeId;
		drawTemperatureHumidity(list, timeType, "温湿度曲线图", imageName, 480, 200);
		Hashtable<String, String> imageHashtable = new Hashtable<String, String>();
		imageHashtable.put("imageName", imageName + ".png");
		imageHashtable.put("timeType", timeType);
		imageHashtable.put("startDate", startDate);
		imageHashtable.put("endDate", endDate);
		imageHashtable.put("serialNodeId", serialNodeId);
		
		//request.setAttribute("imageHashtable", imageHashtable);
		return imageHashtable;
	}
	
	/**
	 * This function is used to draw the default graph by serialNodeId
	 * @param serialNodeId
	 * @return
	 * 
	 * @author nielin 
	 * create on 2010-02-03
	 */
	private Hashtable drawDefaultImage(String serialNodeId){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(new Date());
		String startTime = date + " 00:00:00";
		String endTime = date + " 23:59:59";
		String timeType = "minute";
		List list = null;
		
		TemperatureHumidityDao temperatureHumidityDao = new TemperatureHumidityDao();
		try {
			list = temperatureHumidityDao.findByNodeIdAndTime(serialNodeId, startTime, endTime);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			temperatureHumidityDao.close();
		}
		if(list == null){
			list = new ArrayList();
		}
		String imageName = "temperature_humidity" + "_" + serialNodeId;
		drawTemperatureHumidity(list, timeType, "温湿度曲线图", imageName, 480, 200);
		Hashtable<String, String> imageHashtable = new Hashtable<String, String>();
		imageHashtable.put("imageName", imageName + ".png");
		imageHashtable.put("timeType", timeType);
		imageHashtable.put("startDate", date);
		imageHashtable.put("endDate", date);
		imageHashtable.put("serialNodeId", serialNodeId);
		
		//request.setAttribute("imageHashtable", imageHashtable);
		return imageHashtable;
	}
	
	
	/**
	 * Created by TemperatureHumidityThresholdConfig by the page parameters 
	 * 
	 * @author nielin
	 * create on 2010-02-02
	 * @return
	 */
	private TemperatureHumidityThresholdConfig createTemperatureHumidityThresholdConfig(){
		String minTemperature = request.getParameter("minTemperature");
		String maxTemperature = request.getParameter("maxTemperature");
		String minHumidity = request.getParameter("minHumidity");
		String maxHumidity = request.getParameter("maxHumidity");
		
		TemperatureHumidityThresholdConfig temperatureHumidityThresholdConfig =
			new TemperatureHumidityThresholdConfig();
		temperatureHumidityThresholdConfig.setMinTemperature(minTemperature);
		temperatureHumidityThresholdConfig.setMaxTemperature(maxTemperature);
		temperatureHumidityThresholdConfig.setMinHumidity(minHumidity);
		temperatureHumidityThresholdConfig.setMaxHumidity(maxHumidity);
		
		return temperatureHumidityThresholdConfig;
	}
	

	/**
	 * Created by SerialNode by the page parameters 
	 * 
	 * @author nielin
	 * create on 2010-02-02
	 * @return
	 */
	private SerialNode createSerialNode(){
		String name = request.getParameter("name");
		String address = request.getParameter("address");
		String description = request.getParameter("description");
		String monflag = request.getParameter("monflag");
		String serialPortId = request.getParameter("serialPortId");
		String baudRate = request.getParameter("baudRate");
		String databits = request.getParameter("databits");
		String stopbits = request.getParameter("stopbits");
		String parity = request.getParameter("parity");
		String sendMail = request.getParameter("sendemail");
		String bid = request.getParameter("bid");
        
		SerialNode serialNode = new SerialNode();
		serialNode.setName(name);
		serialNode.setAddress(address);
		serialNode.setDescription(description);
		serialNode.setMonflag(monflag);
		serialNode.setSerialPortId(serialPortId);
		serialNode.setBaudRate(baudRate);
		serialNode.setDatabits(databits);
		serialNode.setStopbits(stopbits);
		serialNode.setParity(parity);
		serialNode.setBid(bid);
		serialNode.setSendMail(sendMail);
		return serialNode;
	}
	
	
	
	/**
	 * According to  time types and the current time, create a time period
	 * @param timeType
	 * @param time
	 * @return
	 * @author nielin
	 * create on 2010-02-02
	 */
	private RegularTimePeriod createTimePeriod(String timeType , Calendar time){
		if(timeType == null){
			throw new NullPointerException("timeType is null");
		}else if("minute".equals(timeType)){
			return new Minute(time.get(Calendar.MINUTE),time.get(Calendar.HOUR_OF_DAY),
					time.get(Calendar.DAY_OF_MONTH),time.get(Calendar.MONTH)+1,time.get(Calendar.YEAR));
		}
		else if("hour".equals(timeType)){
			return new Hour(time.get(Calendar.HOUR_OF_DAY),
					time.get(Calendar.DAY_OF_MONTH),time.get(Calendar.MONTH)+1,time.get(Calendar.YEAR));
		}
		else if("day".equals(timeType)){
			return new Day(time.get(Calendar.DAY_OF_MONTH),time.get(Calendar.MONTH)+1,time.get(Calendar.YEAR));
		}
		else if("month".equals(timeType)){
			return new Month(time.get(Calendar.MONTH)+1,time.get(Calendar.YEAR));
		}else{
			throw new IllegalArgumentException("timeType is IllegalArgument , it must be minute , hour" +
					" , day or month .");
		}
		
	}
	
	/**
	 * get start date
	 * @author nielin
	 * create on 2010-02-02
	 * @return
	 */
	private String getStartDate(){
		String startDate = request.getParameter("startDate");
		if(startDate == null || "".equals(startDate)){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			startDate = simpleDateFormat.format(new Date());
		}
		return startDate;
	}
	
	/**
	 * get end date
	 * @author nielin
	 * create on 2010-02-02
	 * @return
	 */
	private String getEndDate(){
		String endDate = request.getParameter("endDate");
		if(endDate == null || "".equals(endDate)){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			endDate = simpleDateFormat.format(new Date());
		}
		return endDate;
	}
	
	/**
	 * 
	 * @param list
	 * @param timeType
	 * @param title1
	 * @param title2
	 * @param w
	 * @param h
	 */
	private void drawTemperatureHumidity(List list,String timeType , String title1,String title2,int w,int h){
		if(list != null ){
			String[] arrays = {"温度" , "湿度"};
			if (list.size() == 0){
				draw_blank(title1,title2,w,h);
				return;
			}
			ChartGraph cg = new ChartGraph();
			TimeSeries[] s=new TimeSeries[2];
			try{
				for(int i=0; i<arrays.length; i++){
					TimeSeries ss = new TimeSeries(arrays[i]);
					for(int j=0; j<list.size(); j++){
						TemperatureHumidityConfig temperatureHumidityConfig = (TemperatureHumidityConfig)list.get(j);
						String value = null ; 
						if(i == 0){
							value = temperatureHumidityConfig.getTemperature();
						}else{
							value = temperatureHumidityConfig.getHumidity();
						}
						Double	v=new Double(value);
						String time = temperatureHumidityConfig.getTime();			
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date time1 = sdf.parse(time);				
						Calendar temp = Calendar.getInstance();
						temp.setTime(time1);	
						RegularTimePeriod regularTimePeriod = createTimePeriod(timeType , temp);
						//Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
						ss.addOrUpdate(regularTimePeriod,v);				
					}
					s[i]=ss;
					//timeSeriesCollection.addSeries(ss);
				}
				cg.timeCurve(s, "x(时间)","值",title1,title2,w,h);
				list = null;
				}catch(Exception e){
				e.printStackTrace();
			}
			}
			else{
				draw_blank(title1,title2,w,h);
			}
	}
	
	 private void draw_blank(String title1,String title2,int w,int h){
	    	ChartGraph cg = new ChartGraph();
	    	TimeSeries ss = new TimeSeries(title1,Minute.class);
	    	TimeSeries[] s = {ss};
	    	try{
	    		Calendar temp = Calendar.getInstance();
	    		Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
	    		ss.addOrUpdate(minute,null);
	    		cg.timewave(s,"x(时间)","y",title1,title2,w,h);
	    	}
	    	catch(Exception e){e.printStackTrace();}
	    }
	
	
	
	
	
	
	private String saveFailed(){
		return null;
	}
	
}