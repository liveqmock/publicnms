/**
 * <p>Description:get mysql information</p>
 * <p>Company: afunms</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-6
 */

package com.afunms.application.util;

import java.util.List;

import com.afunms.application.dao.JobForAS400GroupDao;
import com.afunms.application.dao.JobForAS400GroupDetailDao;
import com.afunms.application.dao.JobForAS400SubSystemDao;
import com.afunms.application.model.JobForAS400Group;
import com.afunms.topology.util.KeyGenerator;



public class JobForAS400GroupDetailUtil
{
	public synchronized void saveProcessGroupAndDetail(JobForAS400Group jobForAS400Group , List jobForAS400GroupDetailList){
		JobForAS400GroupDao jobForAS400GroupDao = new JobForAS400GroupDao();
		try {
			jobForAS400Group.setId(KeyGenerator.getInstance().getNextKey());
			jobForAS400GroupDao.save(jobForAS400Group);
			System.out.println(jobForAS400Group.getId() + "==================" + jobForAS400GroupDetailList.size());
			saveJobForAS400DetailList(jobForAS400Group, jobForAS400GroupDetailList);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			jobForAS400GroupDao.close();
		}
	}
	public synchronized List getJobForAS400SubSystemByIpAndMonFlag(String ip , String mon_flag){
		List list = null;
		JobForAS400SubSystemDao jobForAS400SubSystemDao = new JobForAS400SubSystemDao();
		try {
			list = jobForAS400SubSystemDao.findByCondition(" where ipaddress='" + ip + "' and mon_flag='" + mon_flag + "'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jobForAS400SubSystemDao.close();
		}
		return list;
	}
	public JobForAS400Group getJobForAS400Group(String id){
		JobForAS400Group jobForAS400Group = null;
		JobForAS400GroupDao jobForAS400GroupDao = new JobForAS400GroupDao();
		try {
			jobForAS400Group = (JobForAS400Group)jobForAS400GroupDao.findByID(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			jobForAS400GroupDao.close();
		}
		return jobForAS400Group;
	}
	
	public List getJobForAS400GroupByIp(String ip){
		List list = null;
		JobForAS400GroupDao jobForAS400GroupDao = new JobForAS400GroupDao();
		try {
			list = jobForAS400GroupDao.findByIp(ip);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			jobForAS400GroupDao.close();
		}
		return list;
	}
	
	public synchronized List getJobForAS400GroupByIpAndMonFlag(String ip , String mon_flag){
		List list = null;
		JobForAS400GroupDao jobForAS400GroupDao = new JobForAS400GroupDao();
		try {
			list = jobForAS400GroupDao.findByCondition(" where ipaddress='" + ip + "' and mon_flag='" + mon_flag + "'");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			jobForAS400GroupDao.close();
		}
		return list;
	}
	
	
	
	public synchronized void updateJobForAS400GroupAndDetail(JobForAS400Group jobForAS400Group , List list){
		JobForAS400GroupDao jobForAS400GroupDao = new JobForAS400GroupDao();
		try {
			jobForAS400GroupDao.update(jobForAS400Group);
			saveJobForAS400DetailList(jobForAS400Group, list);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			jobForAS400GroupDao.close();
		}
	}
	
	public synchronized void deleteJobForAS400GroupAndDetail(String[] groupIds){
		JobForAS400GroupDao jobForAS400GroupDao = new JobForAS400GroupDao();
		try {
			jobForAS400GroupDao.delete(groupIds);
			for(int i = 0 ; i< groupIds.length ; i++){
				deleteJobForAS400GroupDetailByGroupId(groupIds[i]);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			jobForAS400GroupDao.close();
		}
	}
	
	public synchronized void deleteJobForAS400GroupDetailByGroupId(String groupId){
		JobForAS400GroupDetailDao jobForAS400GroupDetailDao = new JobForAS400GroupDetailDao();
		try {
			jobForAS400GroupDetailDao.deleteByGroupId(groupId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			jobForAS400GroupDetailDao.close();
		}
	}
	
	public synchronized List getJobForAS400GroupDetailByGroupId(String groupId){
		List list = null;
		JobForAS400GroupDetailDao jobForAS400GroupDetailDao = new JobForAS400GroupDetailDao();
		try {
			list = jobForAS400GroupDetailDao.getJobForAS400GroupDetailByGroupId(groupId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			jobForAS400GroupDetailDao.close();
		}
		
		return list;
	}
	
	public synchronized void saveJobForAS400DetailList(JobForAS400Group jobForAS400Group , List list) {
		JobForAS400GroupDetailDao jobForAS400GroupDetailDao = new JobForAS400GroupDetailDao();
		jobForAS400GroupDetailDao.saveJobForAS400GroupDetailList(String.valueOf(jobForAS400Group.getId()), list);
	}
}