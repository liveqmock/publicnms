package com.afunms.detail.service.fibreInfo;

import java.util.List;

import com.afunms.detail.reomte.model.FibreCapabilityInfo;
import com.afunms.temp.dao.ChannelTempDao;

public class FibreCapabilityInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public FibreCapabilityInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	
	public List<FibreCapabilityInfo> getFibreCapabilityInfo(){
		return getFibreCapabilityInfo(null);
	}
	
	public List<FibreCapabilityInfo> getFibreCapabilityInfo(String[] subentities){
		ChannelTempDao channelTempDao = new ChannelTempDao();
		List<FibreCapabilityInfo> fibreCapabilityInfoList = null;
		try {
			fibreCapabilityInfoList = (List<FibreCapabilityInfo>)channelTempDao.getFibreCapabilityInfoList(nodeid, type, subtype, subentities);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			channelTempDao.close();
		}
		return fibreCapabilityInfoList;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
}
