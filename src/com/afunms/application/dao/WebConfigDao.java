/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.util.*;

import com.afunms.application.model.*;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import java.sql.SQLException;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Web;

public class WebConfigDao extends BaseDao implements DaoInterface {

	public WebConfigDao() {
		
		super("nms_urlconfig");
		
	}
	
	public boolean delete(String []ids){
		if(ids != null && ids.length>0){
			TracertsDao dao = new TracertsDao();
			try{
				dao.deleteTracertsByTypeAndConfigIds("url", ids);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
			
			WebConfigDao webdao = new WebConfigDao();
			List list = webdao.loadAll();
			if(list == null)list = new ArrayList();
			ShareData.setWebconfiglist(list);
			clearRubbish(list);

		}
		return super.delete(ids);
	}
	
	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getWebList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof Web) {
				Web node = (Web) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						WebConfig hostNode = (WebConfig) baseVoList.get(j);
						if (node.getId() == hostNode.getId()) {
							flag = true;
						}
					}
					if (!flag) {
						nodeList.remove(node);
					}
				}
			}
		}
	}

	public BaseVo loadFromRS(ResultSet rs) {
		WebConfig vo=new WebConfig();
		
		try {
			vo.setId(rs.getInt("id"));
			vo.setStr(rs.getString("str"));
			vo.setUser_name(rs.getString("user_name"));
			vo.setUser_password(rs.getString("user_password"));
			vo.setQuery_string(rs.getString("query_string"));
			vo.setMethod(rs.getString("method"));
			vo.setAvailability_string(rs.getString("availability_string"));
			vo.setPoll_interval(rs.getInt("poll_interval"));
			vo.setUnavailability_string(rs.getString("unavailability_string"));
			vo.setTimeout(rs.getInt("timeout"));
			vo.setVerify(rs.getInt("verify"));
			vo.setFlag(rs.getInt("flag"));
			vo.setMon_flag(rs.getInt("mon_flag"));
			vo.setAlias(rs.getString("alias"));
			vo.setSendmobiles(rs.getString("sendmobiles"));
			vo.setNetid(rs.getString("netid"));
			vo.setSendemail(rs.getString("sendemail"));
			vo.setSendphone(rs.getString("sendphone"));
			vo.setIpAddress(rs.getString("ipAddress"));
			vo.setSupperid(rs.getInt("supperid"));
			vo.setKeyword(rs.getString("keyword"));
			vo.setPagesize_min(rs.getString("pagesize_min"));
			vo.setTracertflag(rs.getInt("tracertflag"));
		} catch (SQLException e) {
			
			SysLogger.error("WebConfigDao.loadFromRS()",e);
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		WebConfig vo1=(WebConfig)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into nms_urlconfig(id,alias,str,timeout,flag,netid,sendmobiles,sendemail,sendphone,ipaddress,supperid,keyword,pagesize_min,tracertflag) values(");
		sql.append(vo1.getId());
		sql.append(",'");
		sql.append(vo1.getAlias());
		sql.append("','");
		sql.append(vo1.getStr());
		sql.append("','");
		sql.append(vo1.getTimeout());
		sql.append("','");
		sql.append(vo1.getFlag());
		sql.append("','");
		sql.append(vo1.getNetid());
		sql.append("','");
		sql.append(vo1.getSendmobiles());
		sql.append("','");
		sql.append(vo1.getSendemail());
		sql.append("','");
		sql.append(vo1.getSendphone());
		sql.append("','");
		sql.append(vo1.getIpAddress());
		sql.append("','");
		sql.append(vo1.getSupperid());
		sql.append("','");
		sql.append(vo1.getKeyword());
		sql.append("','");
		sql.append(vo1.getPagesize_min());
		sql.append("',");
		sql.append(vo1.getTracertflag());
		sql.append(")");
		return saveOrUpdate(sql.toString());
		
	}
	   public List getWebByBID(Vector bids){
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
		   sql.append("select * from nms_urlconfig "+wstr);
		   //SysLogger.info(sql.toString());
		   return findByCriteria(sql.toString());
	   }
	   
	   public List getWebByFlag(int flag){
		   List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   sql.append("select * from nms_urlconfig where flag = "+flag);
		   return findByCriteria(sql.toString());
	   }
	
	public boolean update(BaseVo vo) {
		WebConfig vo1=(WebConfig)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("update nms_urlconfig set alias ='");
		sql.append(vo1.getAlias());
		sql.append("',str='");
		sql.append(vo1.getStr());
		sql.append("',timeout='");
		sql.append(vo1.getTimeout());
		sql.append("',flag='");
		sql.append(vo1.getFlag());
		sql.append("',netid='");
		sql.append(vo1.getNetid());
		sql.append("',sendmobiles='");
		sql.append(vo1.getSendmobiles());
		sql.append("',sendemail='");
		sql.append(vo1.getSendemail());
		sql.append("',sendphone='");
		sql.append(vo1.getSendphone());
		sql.append("',ipaddress='");
		sql.append(vo1.getIpAddress());
		sql.append("',supperid='");
		sql.append(vo1.getSupperid());
		sql.append("',pagesize_min='");
		sql.append(vo1.getPagesize_min());
		sql.append("',keyword='");
		sql.append(vo1.getKeyword());
		sql.append("',tracertflag=");
		sql.append(vo1.getTracertflag());
		sql.append(" where id="+vo1.getId());
		return saveOrUpdate(sql.toString());
	}

} 