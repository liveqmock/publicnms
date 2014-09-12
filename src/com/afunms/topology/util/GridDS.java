package com.afunms.topology.util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray; 
import com.afunms.topology.dao.ConfigureDao;
import com.afunms.topology.model.ConfigureNode; 


public class GridDS {
	public GridDS(){
	
	}
	public String getJSONString(int pid){  //显示树
		ConfigureDao ConfigureDao = new ConfigureDao();
		List mapList = new ArrayList();
		List poList = ConfigureDao.findByFatherId(pid); // 得到所有父节点
		int totalCount = poList.size();
		if (totalCount > 0) {
			for (int i = 0; i < totalCount; i++) {
				Map map = new HashMap();
				ConfigureNode po = (ConfigureNode) poList.get(i);
				map.put("id", po.getId());
				map.put("text", po.getText());
				List subMapList = new ArrayList();
				List subList = ConfigureDao.findByFatherId(po.getId());// 得到该父节点的所有子节点
				if (subList.size() > 0) {
					for (int j = 0; j < subList.size(); j++) {
						Map submap = new HashMap();
						ConfigureNode subpo = (ConfigureNode) subList.get(j);
						submap.put("id", subpo.getId());
						submap.put("text", subpo.getText());
						submap.put("leaf", true);
						subMapList.add(submap);
					}
				} else {
					map.put("leaf", true);
			}
				mapList.add(map);
			}
		}
		ConfigureDao.close();
		JSONArray jsonArray = JSONArray.fromObject(mapList);
		String treeData = jsonArray.toString();
		return treeData;
	}
		


	public String getGridString(int index,int pageSize){  	 //分页
		ConfigureDao configureDao = new ConfigureDao();
		List gridList = configureDao.getAllString(); 
		int totalCount = gridList.size();
		int end=index+pageSize;
		if(end>totalCount) end=totalCount;
		String json = "{count:"+totalCount+",results:[";
		for (int i =index; i <end; i++) {
				ConfigureNode po = (ConfigureNode) gridList.get(i);
				json+="{id:"+po.getId()+",text:'"+po.getText()+"',descn:'"+po.getDescn()+"'}";
				if(i!=end-1){
					json +=",";
				}
				}
		json += "]}";
		configureDao.close();
		return json;		
	}
	
	public String getGridString(){  	 //下拉菜单列
		ConfigureDao configureDao = new ConfigureDao();
		List mapList = new ArrayList();
		List gridList = configureDao.getAllString(); 
		int totalCount = gridList.size();
		for (int i =0; i <totalCount; i++) {
				Map map = new HashMap();
				ConfigureNode po = (ConfigureNode) gridList.get(i);
				map.put("id", po.getId());
				map.put("text", po.getText());
				map.put("descn", po.getDescn());
				mapList.add(map);					
				}	
		configureDao.close();
		JSONArray jsonArray = JSONArray.fromObject(mapList);
		String gridData = jsonArray.toString();
		return gridData;
		
	}
	
	public String getNodeString(int pid){  	//每个节点的单独显示
		ConfigureDao configureDao = new ConfigureDao();
		List mapList = new ArrayList();
		List gridList = configureDao.getNodeString(pid); 
		int totalCount = gridList.size();
		if(totalCount!=0){
				Map map = new HashMap();
				ConfigureNode po = (ConfigureNode) gridList.get(0);
				map.put("id", po.getId());
				map.put("text", po.getText());
				map.put("descn", po.getDescn());
				mapList.add(map);
		}
		configureDao.close();
		JSONArray jsonArray = JSONArray.fromObject(mapList);
		String gridData = jsonArray.toString();
		return gridData;
		
	}

    public Boolean insertNode(String text,String descn ){
    	ConfigureDao configureDao = new ConfigureDao();
    	boolean insert = configureDao.insertNode(text,descn);
    	if(insert) return true;
    	else {return false;}
    }
    
   public Boolean insertChildNode(String text,String descn,int father_id){
	   ConfigureDao configureDao = new ConfigureDao();
   	boolean insert = configureDao.insertChildNode(text,descn,father_id);
   	if(insert) return true;
   	else {return false;}
   }
   
   public Boolean deleNode(int nodeid){
	   ConfigureDao configureDao = new ConfigureDao();
	   boolean insert = configureDao.deletenode(nodeid);
	   	if(insert) return true;
	   	else {return false;}
   }

   public Boolean modifyNode(String text,String descn,int nodeid){
	   ConfigureDao configureDao = new ConfigureDao();
	   boolean insert = configureDao.modifynode(text,descn,nodeid);
	   	if(insert) return true;
	   	else {return false;}
   }   
}
