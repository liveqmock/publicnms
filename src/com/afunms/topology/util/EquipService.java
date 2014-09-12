package com.afunms.topology.util;

import java.util.HashMap;
import java.util.List;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.topology.dao.EquipImageDao;
import com.afunms.topology.model.EquipImage;

public class EquipService {
	
	private HashMap EquipMap;
	
	public EquipService(){
		EquipMap = new HashMap();
		EquipMap = ShareData.getAllequpimgs();
//		try {
//			EquipImageDao equipImageDao = new EquipImageDao();
//			List list = equipImageDao.loadAll();
//			for(int i = 0;i<list.size();i++){
//				EquipImage equipImage = (EquipImage)list.get(i);
//				EquipMap.put(equipImage.getId(), equipImage);
//			}
//		} catch (Exception e) {
//			SysLogger.error("EquipService.static",e);
//		}
	}

	private EquipImage getEquipImage(int id)
    {
    	if(EquipMap.get(id)!=null)
    	   return (EquipImage)EquipMap.get(id);
    	else	
    	{
    		System.out.println("EquipImage is not exist,id=" + id);
    		return null;
    	}
    }
	
	public String getTopoImage(int id)
    {    	
	    return getEquipImage(id).getTopoImage();
    }
	
	public String getAlarmImage(int id)
    {
    	return getEquipImage(id).getAlarmImage();
    } 
}
