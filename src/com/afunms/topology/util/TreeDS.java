package com.afunms.topology.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.afunms.topology.dao.TreeNodeDao;
import com.afunms.topology.model.TreeNode;

public class TreeDS {

	public TreeDS() {

	}

	public String getTree() {
		
		TreeNodeDao TreeNodeDao = new TreeNodeDao();
		List mapList = new ArrayList();
		List poList = TreeNodeDao.findByFatherId("0"); // 得到所有父节点
		int totalCount = poList.size();
		if (totalCount > 0) {
			for (int i = 0; i < totalCount; i++) {
				Map map = new HashMap();
				TreeNode po = (TreeNode) poList.get(i);
				map.put("id", po.getName());
				map.put("text", po.getText());
				List subMapList = new ArrayList();
				List subList = TreeNodeDao.findByFatherId(po.getId() + "");// 得到该父节点的所有子节点
				if (subList.size() > 0) {
					for (int j = 0; j < subList.size(); j++) {
						Map submap = new HashMap();
						TreeNode subpo = (TreeNode) subList.get(j);
						submap.put("id", subpo.getName());
						submap.put("text", subpo.getText());
						submap.put("leaf", true);
						subMapList.add(submap);
					}
					map.put("children", subMapList);
				} else {
					map.put("leaf", true);
				}
				mapList.add(map);
			}
		}
		TreeNodeDao.close();
		JSONArray jsonArray = JSONArray.fromObject(mapList);
		String treeData = jsonArray.toString();
		return treeData;
	}
}
