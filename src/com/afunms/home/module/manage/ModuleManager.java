package com.afunms.home.module.manage;

import java.util.List;

import com.afunms.home.module.dao.ModuleDao;
import com.afunms.home.module.model.ModuleModel;

public class ModuleManager {
    public void gog() {

	ModuleDao dao = new ModuleDao();
	List list = dao.findByCondition("");
	if (list != null) {
	    for (int i = 0; i < list.size(); i++) {
		ModuleModel model=(ModuleModel)list.get(i);
		model.getEnName();
		model.getChName();
	    }
	}

    }
}
