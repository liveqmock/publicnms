package com.afunms.detail.service;

import java.util.List;

import com.afunms.application.dao.StorageDao;
import com.afunms.application.model.Storage;
import com.afunms.detail.reomte.model.DetailTabRemote;
import com.afunms.detail.service.pingInfo.StoragePingInfoService;
import com.afunms.detail.service.storageInfo.StorageArrayInfoService;
import com.afunms.detail.service.storageInfo.StorageArraySiteInfoService;
import com.afunms.detail.service.storageInfo.StorageExtpoolInfoService;
import com.afunms.detail.service.storageInfo.StorageFbvolInfoService;
import com.afunms.detail.service.storageInfo.StorageHostConnectInfoService;
import com.afunms.detail.service.storageInfo.StorageIOPortInfoService;
import com.afunms.detail.service.storageInfo.StorageRankInfoService;
import com.afunms.detail.service.storageInfo.StorageVolgrpInfoService;
import com.afunms.detail.service.systemInfo.SystemInfoService;
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
 * 由于存储设备 还没有  做  type 和 subtype 故其
 * 继承于 detailService 后重写 init() 方法
 */

public class StorageService extends DetailService {
	
	protected Storage storage;

	public StorageService(String nodeid, String type, String subtype) {
		super(nodeid, type, subtype);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the storage
	 */
	public Storage getStorage() {
		return storage;
	}

	/**
	 * @param storage the storage to set
	 */
	public void setStorage(Storage storage) {
		this.storage = storage;
	}
	
	public void init(){
		StorageDao storageDao = new StorageDao();
		try {
			storage = (Storage)storageDao.findByID(nodeid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			storageDao.close();
		}
	}
	
	/**
	 * 获取网络设备的 tab 页信息
	 * @return
	 */
	public List<DetailTabRemote> getTabInfo(){
		String file = "/detail/storage/storagedetailtab.xml";
		// 调用父类的解析 tab 也信息
		return praseDetailTabXML(file);
	}
	
	/**
	 * 获取设备系统信息
	 * @return 返回 NodeTemp 列表
	 */
	public List<NodeTemp> getSystemInfo(){
		return null;
	}
	
	/**
	 * 获取存储设备的型号信息
	 * @return	返回 型号 的 String 形式
	 */
	public String getProducerInfo(){
		SystemInfoService systemInfoService = new SystemInfoService(this.nodeid, this.type, this.subtype);
		return systemInfoService.getStorageProducerInfo(this.storage.getType());

	}
	
	/**
	 * 获取当天连通率平均值的信息
	 * @return	返回 连通率平均值的 String 形式
	 */
	public String getCurrDayPingAvgInfo(){
		StoragePingInfoService storagePingInfoService = new StoragePingInfoService(this.nodeid, this.type, this.subtype);
		return storagePingInfoService.getCurrDayPingAvgInfo(this.storage.getIpaddress());
	}
	
	
	/**
	 * 获取存储设备的 ArraySiteInfo 信息
	 * @return	返回 StorageArraySiteNodeTemp 的列表
	 */
	public List<StorageArraySiteNodeTemp> getArraySiteInfo(){
		StorageArraySiteInfoService storageArraySiteInfoService = new StorageArraySiteInfoService(this.nodeid, this.type, this.subtype);
		return storageArraySiteInfoService.getCurrArraySiteInfo();
	}
	
	/**
	 * 获取存储设备的 ArrayInfo 信息
	 * @return	返回 StorageArrayNodeTemp 的列表
	 */
	public List<StorageArrayNodeTemp> getArrayInfo(){
		StorageArrayInfoService storageArrayInfoService = new StorageArrayInfoService(this.nodeid, this.type, this.subtype);
		return storageArrayInfoService.getCurrArrayInfo();
	}

	
	/**
	 * 获取存储设备的 RankInfo 信息
	 * @return	返回 StorageRankNodeTemp 的列表
	 */
	public List<StorageRankNodeTemp> getRankInfo(){
		StorageRankInfoService storageRankInfoService = new StorageRankInfoService(this.nodeid, this.type, this.subtype);
		return storageRankInfoService.getCurrRankInfo();
	}
	
	/**
	 * 获取存储设备的 ExtpoolInfo 信息
	 * @return	返回 StorageExtpoolNodeTemp 的列表
	 */
	public List<StorageExtpoolNodeTemp> getExtpoolInfo(){
		StorageExtpoolInfoService storageExtpoolInfoService = new StorageExtpoolInfoService(this.nodeid, this.type, this.subtype);
		return storageExtpoolInfoService.getCurrExtpoolInfo();
	}
	
	
	/**
	 * 获取存储设备的 FbvolInfo 信息
	 * @return	返回 StorageFbvolNodeTemp 的列表
	 */
	public List<StorageFbvolNodeTemp> getFbvolInfo(){
		StorageFbvolInfoService storageFbvolInfoService = new StorageFbvolInfoService(this.nodeid, this.type, this.subtype);
		return storageFbvolInfoService.getCurrFbvolInfo();
	}
	
	/**
	 * 获取存储设备的 VolgrpInfo 信息
	 * @return	返回 StorageVolgrpNodeTemp 的列表
	 */
	public List<StorageVolgrpNodeTemp> getVolgrpInfo(){
		StorageVolgrpInfoService storageVolgrpInfoService = new StorageVolgrpInfoService(this.nodeid, this.type, this.subtype);
		return storageVolgrpInfoService.getCurrVolgrpInfo();
	}
	
	/**
	 * 获取存储设备的 IOPortInfo 信息
	 * @return	返回 StorageIOPortNodeTemp 的列表
	 */
	public List<StorageIOPortNodeTemp> getIOPortInfo(){
		StorageIOPortInfoService storageIOPortInfoService = new StorageIOPortInfoService(this.nodeid, this.type, this.subtype);
		return storageIOPortInfoService.getCurrIOPortInfo();
	}
	
	/**
	 * 获取存储设备的 HostConnectInfo 信息
	 * @return	返回 StorageHostConnectNodeTemp 的列表
	 */
	public List<StorageHostConnectNodeTemp> getHostConnectInfo(){
		StorageHostConnectInfoService storageHostConnectInfoService = new StorageHostConnectInfoService(this.nodeid, this.type, this.subtype);
		return storageHostConnectInfoService.getCurrHostConnectInfo();
	}
	
	/**
	 * 获取存储设备的 getVolgrpFbvolInfo 信息
	 * @return	返回 StorageFbvolNodeTemp 的列表
	 */
	public List<StorageFbvolNodeTemp> getVolgrpFbvolInfo(String volgrp_id){
		StorageFbvolInfoService storageFbvolInfoService = new StorageFbvolInfoService(this.nodeid, this.type, this.subtype);
		return storageFbvolInfoService.getCurrVolgrpFbvolInfo(volgrp_id);
	}
	
}
