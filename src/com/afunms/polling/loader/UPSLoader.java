/**
 * <p>Description:loading ups nodes</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2007-1-24
 */

package com.afunms.polling.loader;

import java.util.ArrayList;
import java.util.List;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.UPSNode;
import com.afunms.security.dao.MgeUpsDao;
import com.afunms.security.model.MgeUps;
import com.afunms.polling.base.NodeLoader;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;

public class UPSLoader extends NodeLoader
{    
    public void loading()
    {
    	MgeUpsDao dao = new MgeUpsDao();
    	List list = dao.loadByType("ups");
    	if(list == null)list = new ArrayList();
		ShareData.setUpslist(list);
    	for(int i=0;i<list.size();i++)    
    	{
    		MgeUps vo = (MgeUps)list.get(i);       
    		loadOne(vo);         
    	}   
    }
    
    public void loadOne(BaseVo baseVo)
    {      
    	MgeUps vo = (MgeUps)baseVo;       
    	UPSNode node = new UPSNode(); 
    	if("1".equals(vo.getIsmanaged())){
    		node.setManaged(true);
    	} else {
    		node.setManaged(false);
    	}
		node.setId(vo.getId());
		node.setAlias(vo.getAlias());
		node.setIpAddress(vo.getIpAddress());
		node.setCommunity(vo.getCommunity());
		node.setSysDescr(vo.getSysDescr());
		node.setLocation(vo.getLocation());
		node.setCategory(101); 
		node.setStatus(0);
		node.setAlarm(false);
		node.setAlarmlevel(0);
		node.setType("ups");
		node.setBid(vo.getBid());
		node.setSubtype(vo.getSubtype());
		PollingEngine.getInstance().addUps(node);
    }
}