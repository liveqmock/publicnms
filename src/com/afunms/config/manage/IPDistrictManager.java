package com.afunms.config.manage;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.config.dao.DistrictDao;
import com.afunms.config.dao.IPDistrictDao;
import com.afunms.config.model.DistrictConfig;
import com.afunms.config.model.IPDistrictConfig;

public class IPDistrictManager extends BaseManager implements ManagerInterface {
	
	/**
	 * 查询所有的方法
	 * @return
	 */
	private String list() {
		IPDistrictDao dao = new IPDistrictDao();
		setTarget("/config/ipdistrict/list.jsp"); 
        return list(dao);
	}
    
	/**
	 * 删除方法
	 * @return
	 */
	public String delete()
	{
		String id = getParaValue("radio");
		IPDistrictDao dao = new IPDistrictDao();
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
			
        return "/ipdistrict.do?action=list";
	}
	/**
	 * 修改方法
	 * @return
	 */
	   private String update()
       {    
		   
		   IPDistrictConfig vo=new IPDistrictConfig();
			IPDistrictDao dao = new IPDistrictDao();
			int id=Integer.parseInt(getParaValue("id"));
			int sign=Integer.parseInt(getParaValue("sign"));
			vo.setId(id);
			if(sign == 1)
			{
				vo.setDistrictid(getParaIntValue("district"));
		        vo.setStartip(getParaValue("ipaddress"));
			}else
			if(sign == 2)
			{
				 vo.setDistrictid(getParaIntValue("district"));
				 vo.setStartip(getParaValue("startip"));
				 vo.setEndip(getParaValue("endip"));
			}
	          
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
          
        return "/ipdistrict.do?action=list";
    }
	/**
	 * 添加方法
	 * @return
	 */
	public String add()
    {   
		IPDistrictConfig vo=new IPDistrictConfig();
		IPDistrictDao dao = new IPDistrictDao();
		int sign=Integer.parseInt(getParaValue("sign"));
		if(sign == 1)
		{
			vo.setDistrictid(getParaIntValue("district"));
	        vo.setStartip(getParaValue("ipaddress"));
	        dao.save(vo);
		}else
		if(sign == 2)
		{
			vo.setDistrictid(getParaIntValue("district"));
			 vo.setStartip(getParaValue("startip"));
			 vo.setEndip(getParaValue("endip"));
			 IPDistrictDao dao1 = new IPDistrictDao();
			 dao1.save(vo);
		}
          
	        try{
	        	
	        }catch(Exception e){
	        	e.printStackTrace();
	        }finally{
	        	dao.close();
	        }
        return "/ipdistrict.do?action=list";
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
			DaoInterface dao = new IPDistrictDao();
    	    setTarget("/config/ipdistrict/edit.jsp");
            return readyEdit(dao);
		}
		
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}


}
