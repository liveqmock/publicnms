package com.afunms.detail.service.jbossInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.dao.JBossConfigDao;
import com.afunms.node.dao.PerformaceInfoTableDao;
import com.afunms.node.model.PerformanceInfo;

/**
 * <p>JBoss采集信息service</p>
 * @author HONGLI  Mar 7, 2011
 */
public class JBossInfoService {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    /**
	 *  <p>获取数据库中存储的采集的JBoss数据信息</p>
	 * @param nodeid
	 * @return
	 */
	public Hashtable<String, String> getJBossData(String nodeid){
		Hashtable<String, String> jbossData = null;
		JBossConfigDao jBossConfigDao = null;
		try{
			jBossConfigDao = new JBossConfigDao();
			jbossData = jBossConfigDao.getJBossData(nodeid);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			jBossConfigDao.close();
		}
		return jbossData;
	}

	public String getPingInfo(String nodeid) {
	    Date date = new Date();
	    String startTime = format.format(date) + " 00:00:00";
	    String endTime = format.format(date) + " 23:59:59";
	    PerformaceInfoTableDao dao = new PerformaceInfoTableDao("jbossping" + nodeid);
	    List<PerformanceInfo> list = dao.findByCollectTime(startTime, endTime);
	    dao.close();
	    Double max = 0D;
	    Double avg = 0D;
	    Double total = 0D;
	    if (list != null) {
	        for (PerformanceInfo performanceInfo : list) {
	            Double thevalue = Double.valueOf(performanceInfo.getThevalue());
	            if (max < thevalue) {
	                max = thevalue;
	            }
	            total += thevalue;
	        }
	        if (list.size() > 0) {
	            avg = total / list.size();
	        }
	    }
	    return String.valueOf(avg);
	}
}
