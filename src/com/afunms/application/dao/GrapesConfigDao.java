/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.GrapesConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.*;
import com.afunms.system.model.User;

public class GrapesConfigDao extends BaseDao implements DaoInterface {

	public GrapesConfigDao() {
		super("nms_grapesconfig");
	}
	public boolean delete(String []ids){
		return super.delete(ids);
	}
	
	   public List getGrapesByFlag(int flag){
		   List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   sql.append("select * from nms_grapesconfig where mon_flag = "+flag);
		   return findByCriteria(sql.toString());
	   }
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		
		GrapesConfig vo=new GrapesConfig();
		
		try {
			vo.setId(rs.getInt("id"));
			vo.setName(rs.getString("name"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setSupperdir(rs.getString("supperdir"));
			vo.setSubdir(rs.getString("subdir"));
			vo.setSubfilesum(rs.getString("subfilesum"));
			vo.setFilesize(rs.getInt("filesize"));
			vo.setSendmobiles(rs.getString("sendmobiles"));
			vo.setMon_flag(rs.getInt("mon_flag"));
			vo.setNetid(rs.getString("netid"));
			vo.setSendemail(rs.getString("sendemail"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	
	return vo;
	}

	public boolean save(BaseVo vo) {
		GrapesConfig vo1=(GrapesConfig)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into nms_grapesconfig(name,ipaddress,supperdir,subdir,subfilesum,filesize,sendmobiles,mon_flag,netid,sendemail) values('");
		sql.append(vo1.getName());
		sql.append("','");
		sql.append(vo1.getIpaddress());
		sql.append("','");
		sql.append(vo1.getSupperdir());
		sql.append("','");
		sql.append(vo1.getSubdir());
		sql.append("','");
		sql.append(vo1.getSubfilesum());
		sql.append("','");
		sql.append(vo1.getFilesize());
		sql.append("','");
		sql.append(vo1.getSendmobiles());
		sql.append("','");
		sql.append(vo1.getMon_flag());
		sql.append("','");
		sql.append(vo1.getNetid());
		sql.append("','");
		sql.append(vo1.getSendemail());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		GrapesConfig vo1=(GrapesConfig)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("update nms_grapesconfig set name='");
		sql.append(vo1.getName());
		sql.append("',ipaddress='");
		sql.append(vo1.getIpaddress());
		sql.append("',supperdir='");
		sql.append(vo1.getSupperdir());
		sql.append("',subdir='");
		sql.append(vo1.getSubdir());
		sql.append("',subfilesum='");
		sql.append(vo1.getSubfilesum());
		sql.append("',filesize='");
		sql.append(vo1.getFilesize());
		sql.append("',sendmobiles='");
		sql.append(vo1.getSendmobiles());
		sql.append("',mon_flag='");
		sql.append(vo1.getMon_flag());
		sql.append("',netid='");
		sql.append(vo1.getNetid());
		sql.append("',sendemail='");
		sql.append(vo1.getSendemail());
		sql.append("' where id="+vo1.getId());
		//SysLogger.info(sql.toString());
		return saveOrUpdate(sql.toString());
	}
	
	   public List getGrapesByBID(Vector bids){
		   List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   String wstr = "";
		   if(bids != null && bids.size()>0){
			   for(int i=0;i<bids.size();i++){
				   if(wstr.trim().length()==0){
					   wstr = wstr+" where ( netid like '%,"+bids.get(i)+",%' "; 
				   }else{
					   wstr = wstr+" or netid like '%,"+bids.get(i)+",%' ";
				   }
				   
			   }
			   wstr=wstr+")";
		   }
		   sql.append("select * from nms_grapesconfig "+wstr);
		   //SysLogger.info(sql.toString());
		   return findByCriteria(sql.toString());
	   }
	   
	   public GrapesConfig getGrapesByIp(String ip){
		   GrapesConfig config = null;
		   List list = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   sql.append("select * from nms_grapesconfig where ipaddress='"+ip+"'");
		   list = findByCriteria(sql.toString());
		   if(list != null && list.size()>0){
			   config = (GrapesConfig)list.get(0);
		   }
		   //SysLogger.info(sql.toString());
		   return config;
	   }

}