package com.afunms.subconfigcat.manage;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.config.dao.SupperDao;
import com.afunms.subconfigcat.dao.SubconfigCatDao;
import com.afunms.subconfigcat.model.SubconfigCatConfig;

public class SubconfigCatManager extends BaseManager implements ManagerInterface {
	
	/**
	 * 查询所有的方法
	 * @return
	 */
	private String list() {
		SubconfigCatDao dao = new SubconfigCatDao();
		setTarget("/config/subconfigcat/list.jsp"); 
        return list(dao);
	}
    
	/**
	 * 删除方法
	 * @return
	 */
	public String delete()
	{
		DaoInterface dao = new SupperDao();
		setTarget("/subconfigcat.do?action=list&jp=1");
		return delete(dao);
	}
	/**
	 * 修改方法
	 * @return
	 */
	   private String update()
       {    	   
		   SubconfigCatConfig vo=new SubconfigCatConfig();
		vo.setId(getParaIntValue("id"));
    	vo.setName(getParaValue("name"));
    	vo.setDesc(getParaValue("desc")); 
    	SubconfigCatDao dao = new SubconfigCatDao();
        try
        {
        	
        	  dao.update(vo);	
        }catch(Exception e)
        {
        	e.printStackTrace();
        }
        finally
        {
        	dao.close();
        }
          
        return "/subconfigcat.do?action=list";
    }
	/**
	 * 添加方法
	 * @return
	 */
	public String add()
    {   
		SubconfigCatConfig vo=new SubconfigCatConfig();
		SubconfigCatDao dao = new SubconfigCatDao();
         vo.setName(getParaValue("name"));
         vo.setDesc(getParaValue("desc"));
	        try{
	        	dao.save(vo);
	        }catch(Exception e){
	        	e.printStackTrace();
	        }finally{
	        	dao.close();
	        }
        return "/subconfigcat.do?action=list";
    }  
	 public String execute(String action) {
		// TODO Auto-generated method stub
		if(action.equals("list"))
		{
			return list();
		}
		if(action.equals("add"))
		{
			return add();
		}
		if(action.equals("delete"))
		{
			return delete();
		}
	      if(action.equals("update"))
	      {
	            return update();
	      }
		if(action.equals("ready_edit"))
		{
			DaoInterface dao = new SubconfigCatDao();
    	    setTarget("/config/subconfigcat/edit.jsp");
            return readyEdit(dao);
		}
		if(action.equals("ready_add"))
        	return "/config/subconfigcat/add.jsp";
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}


}
