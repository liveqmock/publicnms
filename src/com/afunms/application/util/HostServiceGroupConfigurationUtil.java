/**
 * <p>Description:get mysql information</p>
 * <p>Company: afunms</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-6
 */

package com.afunms.application.util;

import java.util.List;

import com.afunms.application.dao.HostServiceGroupConfigurationDao;
import com.afunms.application.dao.HostServiceGroupDao;
import com.afunms.application.model.HostServiceGroup;



public class HostServiceGroupConfigurationUtil
{
	public synchronized void savehostservicegroupAndConfiguration(HostServiceGroup hostservicegroup , List hostservicegroupConfigurationList){
		HostServiceGroupDao hostservicegroupDao = new HostServiceGroupDao();
		try {
			hostservicegroup.setId(hostservicegroupDao.getNextId());
			hostservicegroupDao.save(hostservicegroup);
//			System.out.println(hostservicegroup.getId() + "==================" + hostservicegroupConfigurationList.size());
			saveHostServiceGroupConfigurationList(hostservicegroup, hostservicegroupConfigurationList);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			hostservicegroupDao.close();
		}
	}
	
	public synchronized HostServiceGroup gethostservicegroup(String id){
		HostServiceGroup hostservicegroup = null;
		HostServiceGroupDao hostservicegroupDao = new HostServiceGroupDao();
		try {
			hostservicegroup = (HostServiceGroup)hostservicegroupDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			hostservicegroupDao.close();
		}
		return hostservicegroup;
	}
	
	public synchronized List gethostservicegroupByIp(String ip){
		List list = null;
		HostServiceGroupDao hostservicegroupDao = new HostServiceGroupDao();
		try {
			list = hostservicegroupDao.findByIp(ip);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			hostservicegroupDao.close();
		}
		return list;
	}
	
	
	public synchronized List gethostservicegroupByIpAndMonFlag(String ip , String monFlag){
		List list = null;
		HostServiceGroupDao hostservicegroupDao = new HostServiceGroupDao();
		try {
			list = hostservicegroupDao.findByCondition(" where ipaddress='" + ip + "' and mon_flag='" + monFlag + "'");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			hostservicegroupDao.close();
		}
		return list;
	}
	
	
	
	public synchronized void updatehostservicegroupAndConfiguration(HostServiceGroup hostservicegroup , List hostservicegroupConfigurationList){
		HostServiceGroupDao hostservicegroupDao = new HostServiceGroupDao();
		try {
			hostservicegroupDao.update(hostservicegroup);
			saveHostServiceGroupConfigurationList(hostservicegroup, hostservicegroupConfigurationList);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			hostservicegroupDao.close();
		}
	}
	
	public synchronized void deletehostservicegroupAndConfiguration(String[] groupIds){
		HostServiceGroupDao hostservicegroupDao = new HostServiceGroupDao();
		try {
			hostservicegroupDao.delete(groupIds);
			for(int i = 0 ; i< groupIds.length ; i++){
				deletehostservicegroupConfigurationByGroupId(groupIds[i]);
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			hostservicegroupDao.close();
		}
	}
	
	public synchronized void deletehostservicegroupConfigurationByGroupId(String groupId){
		HostServiceGroupConfigurationDao hostservicegroupConfigurationDao = new HostServiceGroupConfigurationDao();
		try {
			hostservicegroupConfigurationDao.deleteByGroupId(groupId);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			hostservicegroupConfigurationDao.close();
		}
	}
	
	public synchronized List gethostservicegroupConfigurationByGroupId(String groupId){
		List list = null;
		HostServiceGroupConfigurationDao hostservicegroupConfigurationDao = new HostServiceGroupConfigurationDao();
		try {
			list = hostservicegroupConfigurationDao.getHostServiceGroupConfigurationByGroupId(groupId);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			hostservicegroupConfigurationDao.close();
		}
		
		return list;
	}
	
	public synchronized void saveHostServiceGroupConfigurationList(HostServiceGroup hostservicegroup , List hostservicegroupConfigurationList) {
		HostServiceGroupConfigurationDao hostservicegroupConfigurationDao = new HostServiceGroupConfigurationDao();
		hostservicegroupConfigurationDao.saveHostServiceGroupConfigurationList(String.valueOf(hostservicegroup.getId()), hostservicegroupConfigurationList);
	}
}