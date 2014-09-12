package com.afunms.config.manage;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.config.dao.DistrictDao;
import com.afunms.config.dao.IPDistrictDao;
import com.afunms.config.dao.MacconfigDao;
import com.afunms.config.model.DistrictConfig;
import com.afunms.topology.dao.IpDistrictMatchConfigDao;

public class DistrictManager extends BaseManager implements ManagerInterface {
	
	/**
	 * 查询所有的方法
	 * @return
	 */
	private String list() {
		DistrictDao dao = new DistrictDao();
		setTarget("/config/district/list.jsp"); 
        return list(dao);
	}
    
	/**
	 * 删除方法
	 * @return
	 */
	public String delete()
	{
		String id = getParaValue("radio");
		DistrictDao dao = new DistrictDao();
		try
		{
			dao.delete(id);	
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			dao.close();
		}
		
		MacconfigDao macconfigDao = new MacconfigDao();
		
		try {
			macconfigDao.deleteByDistrictId(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			macconfigDao.close();
		}
		
		IpDistrictMatchConfigDao ipDistrictMatchConfigDao = new IpDistrictMatchConfigDao();
		try {
			ipDistrictMatchConfigDao.deleteByDistrictId(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			ipDistrictMatchConfigDao.close();
		}
		
		IPDistrictDao ipDistrictDao = new IPDistrictDao();
		try {
			ipDistrictDao.delete(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			ipDistrictDao.close();
		}
			
        return "/district.do?action=list";
	}
	/**
	 * 修改方法
	 * @return
	 */
	   private String update()
       {    	   
		    DistrictConfig vo=new DistrictConfig();
		vo.setId(getParaIntValue("id"));
    	vo.setName(getParaValue("name"));
    	vo.setDesc(getParaValue("desc")); 
    	vo.setDescolor(getParaValue("descolor"));
    	DistrictDao dao = new DistrictDao();
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
          
        return "/district.do?action=list";
    }
	/**
	 * 添加方法
	 * @return
	 */
	public String add()
    {   
		DistrictConfig vo=new DistrictConfig();
		DistrictDao dao = new DistrictDao();
         vo.setName(getParaValue("name"));
         vo.setDesc(getParaValue("desc"));
         vo.setDescolor(getParaValue("descolor"));
	        try{
	        	dao.save(vo);
	        }catch(Exception e){
	        	e.printStackTrace();
	        }finally{
	        	dao.close();
	        }
        return "/district.do?action=list";
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
			DaoInterface dao = new DistrictDao();
    	    setTarget("/config/district/edit.jsp");
            return readyEdit(dao);
		}
		if(action.equals("ready_add"))
        	return "/config/district/add.jsp";
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}


}
