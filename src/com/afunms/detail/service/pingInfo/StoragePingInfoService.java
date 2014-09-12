package com.afunms.detail.service.pingInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.afunms.application.dao.StoragePingDao;
import com.afunms.application.model.Storage;

/**
 * 存储设备连通率
 * @author nielin
 *
 */
public class StoragePingInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public StoragePingInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	public String getCurrDayPingAvgInfo(String ipaddress){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String currDay = simpleDateFormat.format(new Date());
		String startTime = currDay + " 00:00:00";
		String toTime = currDay + " 23:59:59";
		return getPingAvgInfo(ipaddress, startTime, toTime);
	}
	
	public String getPingAvgInfo(String ipaddress, String startTime, String toTime){
		String pingconavg = "0";
		StoragePingDao storagePingDao = new StoragePingDao();
		try {
			String avgpingconstr = storagePingDao.findAverageByTime(ipaddress, startTime, toTime);
			try {
				pingconavg = Double.valueOf(avgpingconstr).intValue() + "";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				pingconavg = "0";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			storagePingDao.close();
		}
		return pingconavg;
	}
	
	
	public int connectivityRate(Storage storage)
    {    	   
		int avgpingcon=0;
		
		StoragePingDao storagePingDao = new StoragePingDao();
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			String avgpingconstr = storagePingDao.findAverageByTime(storage.getIpaddress(), starttime1, totime1);
			try {
				avgpingcon = Double.valueOf(avgpingconstr).intValue();
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				avgpingcon = 0;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			storagePingDao.close();
		}
		return avgpingcon;
    }
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
