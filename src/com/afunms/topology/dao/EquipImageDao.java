/**
 * <p>Description:operate table nms_custom_view_ip and nms_custom_view_xml</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project 聂成海
 * @date 2006-10-21
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.afunms.common.base.*;
import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.EquipImage;

public class EquipImageDao extends BaseDao implements DaoInterface
{
    public EquipImageDao() {
    	super("topo_equip_pic");
	}
    
    //根据设备类型查找图元图片
    private List<EquipImage> loadByCategory(int category) {
    	
        List list = new ArrayList();
	    String sql = "select * from topo_equip_pic where category="+category;
	    
	    try {
		    rs = conn.executeQuery(sql);
		    while(rs.next()) {
		    	list.add(loadFromRS(rs));
		    }
		        
	   } catch(Exception e) {
		   SysLogger.error("EquipImageDao.loadByCategory(int category)",e); 
	   }
	   return list;
    }	
	
    //查询所有类型设备
    public List<String> getGalleryListing() {
		
	    List result = new ArrayList();
	    
	    String sql = "select distinct category,cn_name from topo_equip_pic where " +
	    		     "cn_name IS NOT NULL and cn_name<>''";
	    
	    try {
	    	rs = conn.executeQuery(sql);
			while(rs.next())
				result.add(rs.getInt("category")+","+rs.getString("cn_name"));
	    } catch (Exception e) {
	    	e.printStackTrace();
	 	    SysLogger.error("EquipImageDao.getGalleryListing()",e); 
	    } 
	    return result;
	}
	
    //获取图元图片
    public Map<Integer, Object> getGallery(int category) {
	    Map result = new HashMap();
	    
	    List<EquipImage> list = null;
	    
	    try {
	    	list = this.loadByCategory(category);
	    	
	    	result.put(category, list);
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	 	    SysLogger.error("EquipImageDao.getGallery(int category)",e); 
	    } 
	    return result;
	  }
	
    public boolean save(BaseVo baseVo) 
    {
    	EquipImage vo = (EquipImage)baseVo;		
	    StringBuffer sql = new StringBuffer(300);
	    sql.append("insert into topo_equip_pic(id,category,cn_name,en_name,topo_image,lost_image,alarm_image,path)values(");
	    sql.append(getNextID());
	    sql.append(",");
	    sql.append(vo.getCategory());
	    sql.append(",'");
	    sql.append(vo.getCnName());
	    sql.append("','");
	    sql.append(vo.getEnName());
	    sql.append("','");
	    sql.append(vo.getTopoImage());
	    sql.append("','");
	    sql.append(vo.getLostImage());
	    sql.append("','");
	    sql.append(vo.getAlarmImage());
	    sql.append("','");
	    sql.append(vo.getPath());
	    sql.append("')");
	   
	    return saveOrUpdate(sql.toString());
    }
	
	public boolean update(BaseVo baseVo) 
	{
		EquipImage vo = (EquipImage)baseVo;		
	    StringBuffer sql = new StringBuffer(300);
	    sql.append("update topo_equip_pic set category=");
	    sql.append(vo.getCategory());
	    sql.append(",cn_name='");
	    sql.append(vo.getCnName());
	    sql.append("',en_name='");
	    sql.append(vo.getEnName());
	    sql.append("',topo_image='");
	    sql.append(vo.getTopoImage());
	    sql.append("',lost_image='");
	    sql.append(vo.getLostImage());
	    sql.append("',alarm_image='");
	    sql.append(vo.getAlarmImage());
	    sql.append("',path='");
	    sql.append(vo.getPath());
	    sql.append("' where id=");
	    sql.append(vo.getId());
	    
	    return saveOrUpdate(sql.toString());
	}
	
	/**
	    * 根据id找一条记录
	    */
    public BaseVo findImageById(int id)
    {
	    BaseVo vo = null;
        try {
		    rs = conn.executeQuery("select * from topo_equip_pic where id=" + id); 
		    if(rs.next())
		        vo = loadFromRS(rs);
	    } catch (Exception ex)
	    {
		    SysLogger.error("EquipImageDao.findImageById(int id)",ex);
	    } 
        return vo;
    }
			
	public BaseVo loadFromRS(ResultSet rs)
	{
		EquipImage vo = new EquipImage();
		try {
			vo.setId(rs.getInt("id"));
			vo.setCategory(rs.getInt("category"));
			vo.setCnName(rs.getString("cn_name"));
			vo.setEnName(rs.getString("en_name"));
			vo.setTopoImage(rs.getString("topo_image"));
			vo.setLostImage(rs.getString("lost_image"));
			vo.setAlarmImage(rs.getString("alarm_image"));
			vo.setPath(rs.getString("path"));
		} catch(Exception e) {
			SysLogger.error("EquipImageDao.loadFromRS()",e);
		} 
	    return vo;
	}	
}   