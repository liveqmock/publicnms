package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.HintItem;

public class HintItemDao extends BaseDao implements DaoInterface{

	public HintItemDao() {
		super("topo_hint_meta");
	}

	private List<HintItem> loadByType(String topoTypeName) {
	
        List list = new ArrayList();
	    String sql = "select * from topo_hint_meta where sort_name='"+topoTypeName+"'";
	    
	    try {
		    rs = conn.executeQuery(sql);
		    while(rs.next()) {
		    	list.add(loadFromRS(rs));
		    }
		        
	   } catch(Exception e) {
		   SysLogger.error("HintItemDao.loadByType(String topoTypeName)",e); 
	   } 
	   return list;
    }	
	
	public List<String> getGalleryListing() {
		
	    List result = new ArrayList();
	    
	    String sql = "select distinct sort_name from topo_hint_meta where " +
	    		     "sort_name IS NOT NULL and sort_name<>''";
	    
	    try {
	    	rs = conn.executeQuery(sql);
			while(rs.next())
				result.add(rs.getString("sort_name"));
	    } catch (Exception e) {
	    	e.printStackTrace();
	 	    SysLogger.error("HintItemDao.getGalleryListing()",e); 
	    }
	    return result;
	}
	
	public Map<String, Object> getGallery(String typeName) {
	    Map result = new HashMap();
	    
	    List<HintItem> list = null;
	    
	    try {
	    	list = this.loadByType(typeName);
	    	
	    	result.put(typeName, list);
	    } catch (Exception e) {
	    	e.printStackTrace();
	 	    SysLogger.error("HintItemDao.getGallery(String typeName)",e); 
	    } 
	    return result;
	  }
	
	
	public BaseVo loadFromRS(ResultSet rs) {
        HintItem vo = new HintItem();
	    try {
		   vo.setIconId(rs.getString("icon_id"));
		   vo.setIconName(rs.getString("icon_name"));
		   vo.setIconPath(rs.getString("icon_path"));
		   vo.setId(rs.getString("id"));
		   vo.setSortName(rs.getString("sort_name"));
		   vo.setWebIconPath(rs.getString("web_icon_path"));
	   } catch(Exception e) {
    	   e.printStackTrace();
 	       SysLogger.error("HintItemDao.loadFromRS()",e); 
       }	   
       return vo;
	}

	public boolean save(BaseVo vo) {
		return false;
	}

	public boolean update(BaseVo vo) {
		return false;
	}

}
