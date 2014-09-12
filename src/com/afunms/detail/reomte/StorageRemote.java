/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author nielin
 * @project afunms
 * @date 2010-12-08
 */

package com.afunms.detail.reomte;

import java.util.List;

import com.afunms.application.model.Storage;
import com.afunms.detail.reomte.model.DetailTabRemote;
import com.afunms.detail.service.StorageService;
import com.afunms.temp.model.NodeTemp;
import com.afunms.temp.model.StorageArrayNodeTemp;
import com.afunms.temp.model.StorageArraySiteNodeTemp;
import com.afunms.temp.model.StorageExtpoolNodeTemp;
import com.afunms.temp.model.StorageFbvolNodeTemp;
import com.afunms.temp.model.StorageHostConnectNodeTemp;
import com.afunms.temp.model.StorageIOPortNodeTemp;
import com.afunms.temp.model.StorageRankNodeTemp;
import com.afunms.temp.model.StorageVolgrpNodeTemp;


/**
 * 此类用于存储设备详细信息页面远程调用
 */

public class StorageRemote extends DetailReomte{
	
	public Storage getStorageInfo(String nodeid, String type, String subtype){
		return new StorageService(nodeid, type, subtype).getStorage();
	}
	
	public List<NodeTemp> getSystemInfo(String nodeid, String type, String subtype){
		return new StorageService(nodeid, type, subtype).getSystemInfo();
	}
	
	public String getProducerInfo(String nodeid, String type, String subtype){
		return new StorageService(nodeid, type, subtype).getProducerInfo();
	}
	
	public String getCurrDayPingAvgInfo(String nodeid, String type, String subtype){
		return new StorageService(nodeid, type, subtype).getCurrDayPingAvgInfo();
	}
	
	public List<DetailTabRemote> getTabInfo(String nodeid, String type, String subtype){
		return new StorageService(nodeid, type, subtype).getTabInfo();
	}
	
	public List<StorageArraySiteNodeTemp> getArraySiteInfo(String nodeid, String type, String subtype){
		return new StorageService(nodeid, type, subtype).getArraySiteInfo();
	}
	
	public List<StorageArrayNodeTemp> getArrayInfo(String nodeid, String type, String subtype){
		return new StorageService(nodeid, type, subtype).getArrayInfo();
	}
	
	public List<StorageRankNodeTemp> getRankInfo(String nodeid, String type, String subtype){
		return new StorageService(nodeid, type, subtype).getRankInfo();
	}
	
	public List<StorageExtpoolNodeTemp> getExtpoolInfo(String nodeid, String type, String subtype){
		return new StorageService(nodeid, type, subtype).getExtpoolInfo();
	}
	
	public List<StorageFbvolNodeTemp> getFbvolInfo(String nodeid, String type, String subtype){
		return new StorageService(nodeid, type, subtype).getFbvolInfo();
	}
	
	public List<StorageVolgrpNodeTemp> getVolgrpInfo(String nodeid, String type, String subtype){
		return new StorageService(nodeid, type, subtype).getVolgrpInfo();
	}
	
	public List<StorageIOPortNodeTemp> getIOPortInfo(String nodeid, String type, String subtype){
		return new StorageService(nodeid, type, subtype).getIOPortInfo();
	}
	
	public List<StorageHostConnectNodeTemp> getHostConnectInfo(String nodeid, String type, String subtype){
		return new StorageService(nodeid, type, subtype).getHostConnectInfo();
	}
	public List<StorageFbvolNodeTemp> getVolgrpFbvolInfo(String nodeid, String type, String subtype, String volgrp_id){
		return new StorageService(nodeid, type, subtype).getVolgrpFbvolInfo(volgrp_id);
	}
}
