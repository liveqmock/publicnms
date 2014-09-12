/**
 * <p>Description:与nodedao都是操作表nms_topo_node,但nodedao主要用于发现</p>
 * <p>Description:而toponodedao主要用于页面操作</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-20
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

import com.afunms.common.base.*;
import com.afunms.common.util.SnmpUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.Business;
import com.afunms.discovery.Host;
import com.afunms.polling.node.*;
import com.afunms.polling.om.IpMacBase;
import com.afunms.polling.base.*;
import com.afunms.topology.model.IpDistrictMatch;
import com.afunms.topology.model.IpDistrictMatchConfig;

public class IpDistrictMatchConfigDao extends BaseDao implements DaoInterface
{
    public IpDistrictMatchConfigDao()
    {
    	super("nms_ip_district_match");	   	  
    }
    
    
    public BaseVo loadFromRS(ResultSet rs)
    {
    	IpDistrictMatchConfig ipDistrictMatchConfig = new IpDistrictMatchConfig();
	    try{
	    	ipDistrictMatchConfig.setId(rs.getInt("id"));
	    	ipDistrictMatchConfig.setRelateipaddr(rs.getString("relateipaddr"));
	    	ipDistrictMatchConfig.setNodeIp(rs.getString("node_ip"));
	    	ipDistrictMatchConfig.setNodeName(rs.getString("node_name"));
	    	ipDistrictMatchConfig.setIsOnline(rs.getString("is_online"));
	    	ipDistrictMatchConfig.setOriginalDistrict(rs.getString("original_district"));
	    	ipDistrictMatchConfig.setCurrentDistrict(rs.getString("current_district"));
	    	ipDistrictMatchConfig.setIsMatch(rs.getString("is_match"));
	    	ipDistrictMatchConfig.setTime(rs.getString("time"));
	    }catch(Exception e){
	 	    SysLogger.error("IpDistrictMatchDao.loadFromRS()",e); 
	 	    ipDistrictMatchConfig = null;
	    }	   
	    return ipDistrictMatchConfig;
   }	
    
    public boolean save(BaseVo baseVo)
    {
		return false;
    }
   
   
    public boolean saveBath(List list){
    	boolean result = false;
    	try {
			for(int i =0 ; i< list.size() ; i++){
				IpDistrictMatchConfig ipDistrictMatchConfig = (IpDistrictMatchConfig)list.get(i);
				StringBuffer sql = new StringBuffer(100);
				sql.append("insert into nms_ip_district_match(relateipaddr,node_ip,node_name,is_online,original_district,current_district,is_match,time)values(");
				sql.append("'");
				sql.append(ipDistrictMatchConfig.getRelateipaddr());
				sql.append("','");
				sql.append(ipDistrictMatchConfig.getNodeIp());
				sql.append("','");
				sql.append(ipDistrictMatchConfig.getNodeName());
				sql.append("','");
				sql.append(ipDistrictMatchConfig.getIsOnline());
				sql.append("','");
				sql.append(ipDistrictMatchConfig.getOriginalDistrict());
				sql.append("','");
				sql.append(ipDistrictMatchConfig.getCurrentDistrict());
				sql.append("','");
				sql.append(ipDistrictMatchConfig.getIsMatch());
				sql.append("','");
				sql.append(ipDistrictMatchConfig.getTime());
				sql.append("')");
				conn.addBatch(sql.toString());
			}
			conn.executeBatch();
			result = true;
		} catch (RuntimeException e) {
			result = false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
    }
    
    public List findByOriDistrictId(String districtId){
    	String sql = "select * from nms_ip_district_match where original_district='" + districtId + "'";
    	return findByCriteria(sql); 
    }
    
    public List findByCurDistrictId(String districtId){
    	String sql = "select * from nms_ip_district_match where current_district='" + districtId + "'";
    	return findByCriteria(sql); 
    }
    
    public boolean deleteAll(){
    	String sql = "delete from nms_ip_district_match";
    	return saveOrUpdate(sql);
    }
    
    public boolean deleteByDistrictId(String districtId){
    	String sql = "delete from nms_ip_district_match where current_district='" + districtId + "' or original_district='" + districtId + "'";
    	return saveOrUpdate(sql); 
    }
    
    
    




public boolean update(BaseVo vo) {
	// TODO Auto-generated method stub
	return false;
}
   
   
}
