package com.afunms.system.dao;

import java.sql.ResultSet;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

import com.afunms.system.model.Function;

/**
 * 此Functiondao类为对表"nms_func"操作
 * 在使用此类时 在最后一定要调用close方法来关闭connection释放资源
 * @author hkmw
 *
 */

public class FunctionDao extends BaseDao implements DaoInterface{

	public FunctionDao() {
		super("nms_func");
		// TODO Auto-generated constructor stub
	}

	
	
	@Override
	//载入所有记录
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
	   Function function = new Function();
	   try
	   {
		   function = createFucntionByRS(rs);
		   
	   }
	   catch (Exception e)
	   {	    
	        SysLogger.error("Error in FuctionDao.loadFromRS()",e);
	        e.printStackTrace();
	   }
		return function;
	}
	
	
	public Function findFunctionByFuncId(String funcId){
		Function function = new Function();
		try{
			
			String sql = "select * from nms_func where func_desc = '" + funcId + "'";
			rs = conn.executeQuery(sql);
			if(rs.next()){
				 function = createFucntionByRS(rs);
			}
			
		}catch(Exception e){
			SysLogger.error("FunctionDao.findFunctionByFuncId()",e);
			e.printStackTrace();
		}finally{
			close();
		}
		return function;
	}
	
	public boolean deletelist(String[] id)
	{
		boolean result = false;
		try{
			for(int i=0;i<id.length;i++){
				conn.addBatch("delete from nms_func where id=" + id[i]);
			    conn.addBatch("delete from nms_role_func where funcid =" + id[i]);
		    }  
		    conn.executeBatch();
		    result = true;
		}
		catch(Exception ex){
			conn.rollback();
			SysLogger.error("BaseDao.delete()",ex);
		    result = false;
		}finally{
			close();
		}
		return result;
		   
	}
	     

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		Function function = (Function)vo;
		boolean result = false;
		try{
			String sql = "insert into nms_func(id,func_desc,ch_desc,level_desc,father_node,url,img_url," +
					"is_current_window,width,height,clientX,clientY)values('" + getNextID() + "','" + function.getFunc_desc() + 
					"','" +function.getCh_desc()+ "','" +function.getLevel_desc() + "','" +function.getFather_node() + 
					"','" +function.getUrl() + "','" +function .getImg_url()+ "','" +function .getIsCurrentWindow()+ 
					"','" +function.getWidth() + "','"+ function.getHeight() + 
					"','" +function.getClientX() + "','"+ function.getClientY()+ "')";
			conn.executeUpdate(sql);
			conn.commit();
			result = true;
		}catch(Exception e){
			result = false;
			SysLogger.error("FunctionDao.findFunctionByFuncId()",e);
			e.printStackTrace();
		}finally{
			close();
		}
		return result;
	}

	public boolean update(BaseVo vo) {
		Function function = (Function)vo;
		boolean result = false;
		try{
			String sql = "update nms_func set func_desc='"+function.getFunc_desc() + "',ch_desc='"+function.getCh_desc()+
			"',level_desc='"+function.getLevel_desc()+"',father_node='"+function.getFather_node()+"',url='" +
			function.getUrl() +"',img_url='" +function.getImg_url()+"',is_current_window='"+function .getIsCurrentWindow()
			+"',width='"+function.getWidth()+"',height='"+function.getHeight()
			+"',clientX='"+function.getClientX()+"',clientY='"+function.getClientY() + "'"
			+"where id ='"+function.getId()+"'";
			conn.executeUpdate(sql);
			conn.commit();
			result = true;
		}catch(Exception e){
			result = false;
			SysLogger.error("FunctionDao.findFunctionByFuncId()",e);
			e.printStackTrace();
		}finally{
			close();
		}
		return result;
	}
	
	private Function createFucntionByRS(ResultSet rs){
		Function function = new Function();
		   try
		   {
			   function.setId(rs.getInt("id"));
			   
			   function.setFunc_desc(rs.getString("func_desc"));
			   
			   function.setCh_desc(rs.getString("ch_desc"));
			  
			   function.setFather_node(Integer.valueOf(rs.getString("father_node")));
			   
			   function.setLevel_desc(Integer.valueOf(rs.getString("level_desc")));
			   
			   function.setUrl(rs.getString("url"));
			   
			   function.setImg_url(rs.getString("img_url"));
			 
			   function.setIsCurrentWindow(Integer.valueOf(rs.getString("is_current_window")));
			   
			   function.setWidth(rs.getString("width"));
			   
			   function.setHeight(rs.getString("height"));
			   
			   function.setClientX(rs.getString("clientX"));
			   
			   function.setClientY(rs.getString("clientY"));
			   
		   }
		   catch (Exception e)
		   {	    
		        SysLogger.error("Error in FuctionDao.createFucntionByRS()",e);
		        e.printStackTrace();
		   }
		   
		  
			return function;
	}

}
