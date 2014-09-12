package com.afunms.subconfigcat.dao;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.subconfigcat.model.SubconfigCatConfig;

public class SubconfigCatDao extends BaseDao implements DaoInterface {

	public SubconfigCatDao() {
		super("nms_subconfig_category");
		// TODO Auto-generated constructor stub
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
	
		SubconfigCatConfig vo = new SubconfigCatConfig();
		 try
	      {
	          vo.setId(rs.getInt("id"));
	          vo.setName(rs.getString("name"));
	          vo.setDesc(rs.getString("subdesc"));}
	      catch(Exception e)
	      {
	          SysLogger.error("SubconfigCatDao.loadFromRS()",e);
	          vo = null;
	      }
	      return vo;
	}

	 /**
	    * 列出所有方法
	    */
	  public List loadAll()
	  {
	     List list = new ArrayList(5);
	     try
	     {
	         rs = conn.executeQuery("select * from nms_subconfig_category order by id");
	         while(rs.next())
	        	list.add(loadFromRS(rs)); 
	     }
	     catch(Exception e)
	     {
	         SysLogger.error("SubconfigCatDao:loadAll()",e);
	         list = null;
	     }
	     finally
	     {
	         conn.close();
	     }
	     return list;
	  }
	    /**
	    * 修改方法
	    */
	  public boolean update(BaseVo baseVo) {
		// TODO Auto-generated method stub
		  SubconfigCatConfig vo = (SubconfigCatConfig)baseVo;	 
		StringBuffer sql=new StringBuffer();
		sql.append("update nms_subconfig_category set name ='");
		sql.append(vo.getName());
		sql.append("',subdesc='");
		sql.append(vo.getDesc());
		sql.append("' where id="+vo.getId());
		return saveOrUpdate(sql.toString());
	}
	   /**
	    * 添加方法
	    */
	   public boolean save(BaseVo basevo) {
		// TODO Auto-generated method stub
		   SubconfigCatConfig vo = (SubconfigCatConfig)basevo;	  
		   StringBuffer sql = new StringBuffer();
		   sql.append("insert into nms_subconfig_category(name,subdesc)values(");
		   sql.append("'");
		   sql.append(vo.getName());
		   sql.append("','");
		   sql.append(vo.getDesc());
		   sql.append("'");
		   sql.append(")");
		  
		   return saveOrUpdate(sql.toString());
	}
	   

		 /**
		  * 根据id删除这条记录
		  * @param id
		  * @return
		  */
		  public boolean delete(String id)
		   {
			   boolean result = false;
			   try
			   {
				   conn.addBatch("delete from nms_subconfig_category where id=" + id);
				   //System.out.println("delete from nms_contract where id=" + id);
				   conn.executeBatch();
				   result = true;
			   }
			   catch(Exception e)
			   {
				   SysLogger.error("SubconfigCatDao.delete()",e); 
			   }
			   finally
			   {
				   conn.close();
			   }
			   return result;
		   }

}
