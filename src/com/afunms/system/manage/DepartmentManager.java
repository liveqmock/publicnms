/**
 * <p>Description:DepartmentManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.manage;

import java.io.IOException;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.afunms.common.base.*;
import com.afunms.common.util.SysLogger;
import com.afunms.system.dao.DepartmentDao;
import com.afunms.system.model.Department;
import com.afunms.common.util.*;

import java.util.*;

public class DepartmentManager extends BaseManager implements ManagerInterface
{
	public String execute(String action) 
	{	
        if (action.equals("list"))
        {
        	//getH3cConfig(3);
        	//getH3cConfig(6);

        	
        	
    	    DaoInterface dao = new DepartmentDao();
   	        setTarget("/system/department/list.jsp");
            return list(dao);
        }    
		if(action.equals("ready_add"))
			return "/system/department/add.jsp";
        if (action.equals("add"))
        {    	   
        	Department vo = new Department();
    	    vo.setDept(getParaValue("dept"));
    	    vo.setMan(getParaValue("man"));
    	    vo.setTel(getParaValue("tel"));
    	    DaoInterface dao = new DepartmentDao();    	   
   	        setTarget("/dept.do?action=list");
            return save(dao,vo);
        }    
  	    if (action.equals("delete"))
        {	  
		    DaoInterface dao = new DepartmentDao();
    	    setTarget("/dept.do?action=list");
            return delete(dao);
        }    
        if (action.equals("update"))
        {    	   
        	Department vo = new Department();
      	    vo.setId(getParaIntValue("id"));
   	        vo.setDept(getParaValue("dept"));
	        vo.setMan(getParaValue("man"));
	        vo.setTel(getParaValue("tel"));

     	    DaoInterface dao = new DepartmentDao();    	   
    	    setTarget("/dept.do?action=list");
            return update(dao,vo);
        }    
        if(action.equals("ready_edit"))
        {	  
 		    DaoInterface dao = new DepartmentDao();
    	    setTarget("/system/department/edit.jsp");
            return readyEdit(dao);
        }    
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	

	
}
