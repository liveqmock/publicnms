/**
 * <p>Description:get mysql information</p>
 * <p>Company: afunms</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-6
 */

package com.afunms.application.util;

import java.util.List;

import com.afunms.application.dao.ProcessGroupConfigurationDao;
import com.afunms.application.dao.ProcessGroupDao;
import com.afunms.application.model.ProcessGroup;



public class ProcessGroupConfigurationUtil
{
	public synchronized void saveProcessGroupAndConfiguration(ProcessGroup processGroup , List processGroupConfigurationList){
		ProcessGroupDao processGroupDao = new ProcessGroupDao();
		try {
			processGroup.setId(processGroupDao.getNextId());
			processGroupDao.save(processGroup);
			System.out.println(processGroup.getId() + "==================" + processGroupConfigurationList.size());
			saveProcessGoupConfigurationList(processGroup, processGroupConfigurationList);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			processGroupDao.close();
		}
	}
	
	public synchronized ProcessGroup getProcessGroup(String id){
		ProcessGroup processGroup = null;
		ProcessGroupDao processGroupDao = new ProcessGroupDao();
		try {
			processGroup = (ProcessGroup)processGroupDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			processGroupDao.close();
		}
		return processGroup;
	}
	
	public synchronized List getProcessGroupByIp(String ip){
		List list = null;
		ProcessGroupDao processGroupDao = new ProcessGroupDao();
		try {
			list = processGroupDao.findByIp(ip);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			processGroupDao.close();
		}
		return list;
	}
	
	public synchronized List getProcessGroupByIpAndMonFlag(String ip , String mon_flag){
		List list = null;
		ProcessGroupDao processGroupDao = new ProcessGroupDao();
		try {
			list = processGroupDao.findByCondition(" where ipaddress='" + ip + "' and mon_flag='" + mon_flag + "'");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			processGroupDao.close();
		}
		return list;
	}
	
	
	
	public synchronized void updateProcessGroupAndConfiguration(ProcessGroup processGroup , List processGroupConfigurationList){
		ProcessGroupDao processGroupDao = new ProcessGroupDao();
		try {
			processGroupDao.update(processGroup);
			saveProcessGoupConfigurationList(processGroup, processGroupConfigurationList);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			processGroupDao.close();
		}
	}
	
	/**
	 * 根据nodeid删除进程组信息等
	 * @param nodeid
	 */
	public synchronized void deleteProcessGroupAndConfigurationByNodeid(String nodeid){
		if(nodeid == null || nodeid.equals("")){
			return ;
		}
		ProcessGroupDao processGroupDao = new ProcessGroupDao();
		try {
			List<ProcessGroup> list = null;
			try {
				list = processGroupDao.findByNodeid(nodeid);
			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally{
				processGroupDao.close();
			}
			if(list == null){
				return ;
			}
			processGroupDao = new ProcessGroupDao();
			String[] groupIds = new String[list.size()];
			for(int i=0; i<list.size(); i++){
				groupIds[i] = String.valueOf(list.get(i).getId());
			}
			processGroupDao.delete(groupIds);
			for(int i = 0 ; i< groupIds.length ; i++){
				deleteProcessGroupConfigurationByGroupId(groupIds[i]);
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			processGroupDao.close();
		}
	} 
	
	public synchronized void deleteProcessGroupAndConfiguration(String[] groupIds){
		ProcessGroupDao processGroupDao = new ProcessGroupDao();
		try {
			processGroupDao.delete(groupIds);
			for(int i = 0 ; i< groupIds.length ; i++){
				deleteProcessGroupConfigurationByGroupId(groupIds[i]);
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			processGroupDao.close();
		}
	}
	
	public synchronized void deleteProcessGroupConfigurationByGroupId(String groupId){
		ProcessGroupConfigurationDao processGroupConfigurationDao = new ProcessGroupConfigurationDao();
		try {
			processGroupConfigurationDao.deleteByGroupId(groupId);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			processGroupConfigurationDao.close();
		}
	}
	
	public synchronized List getProcessGroupConfigurationByGroupId(String groupId){
		List list = null;
		ProcessGroupConfigurationDao processGroupConfigurationDao = new ProcessGroupConfigurationDao();
		try {
			list = processGroupConfigurationDao.getProcessGroupConfigurationByGroupId(groupId);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			processGroupConfigurationDao.close();
		}
		
		return list;
	}
	
	public synchronized void saveProcessGoupConfigurationList(ProcessGroup processGroup , List processGroupConfigurationList) {
		ProcessGroupConfigurationDao processGroupConfigurationDao = new ProcessGroupConfigurationDao();
		processGroupConfigurationDao.saveProcessGroupConfigurationList(String.valueOf(processGroup.getId()), processGroupConfigurationList);
	}
}