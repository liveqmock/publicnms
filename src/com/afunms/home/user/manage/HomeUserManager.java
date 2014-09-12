package com.afunms.home.user.manage;

import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysLogger;
import com.afunms.home.module.dao.ModuleDao;
import com.afunms.home.role.dao.HomeRoleDao;
import com.afunms.home.role.model.HomeRoleModel;
import com.afunms.home.user.dao.HomeUserDao;
import com.afunms.home.user.model.HomeUserModel;
import com.afunms.system.dao.RoleDao;
import com.afunms.system.model.Role;
import com.afunms.system.model.User;

public class HomeUserManager extends BaseManager implements ManagerInterface {

    public String execute(String action) {
	// TODO Auto-generated method stub
	if ("update".equals(action)) {
	    return update();
	} else if ("save".equals(action)) {
	    return save();
	}
	setErrorCode(ErrorMessage.ACTION_NO_FOUND);
	return null;
    }

    @SuppressWarnings("unchecked")
    private String save() {
	String[] checkboxStrArray = request.getParameterValues("checkbox");// 得到页面上被选中的checkbox未选中的将得不到
	User uservo = (User) session.getAttribute(SessionConstant.CURRENT_USER);
	HomeUserDao homeUserDao = new HomeUserDao();
	DBManager db;
	try {
	    db = new DBManager();
	    String sql = "";
	    HomeRoleDao roleDao = new HomeRoleDao();
	    List homeRoleList = roleDao.findByCondition(" where role_id='"
		    + uservo.getRole() + "'");// 查找角色名下所有模块 包含了不显示的功能
	    sql = "delete from nms_home_module_user where user_id='"
		    + uservo.getUserid() + "'";
	    db.addBatch(sql);
	    for (int i = 0; i < homeRoleList.size(); i++) {
		HomeRoleModel roleModel = (HomeRoleModel) homeRoleList.get(i);
		HomeUserModel userModel = new HomeUserModel();
		userModel.setBusinessids(uservo.getBusinessids());
		userModel.setChName(roleModel.getChName());
		userModel.setDept_id(roleModel.getDept_id());
		userModel.setEnName(roleModel.getEnName());
		userModel.setName(uservo.getName());
		userModel.setNote(roleModel.getNote());
		userModel.setRole_id(uservo.getRole());
		userModel.setUser_id(uservo.getUserid());
		userModel.setType(roleModel.getType());// 种类设置
		userModel.setVisible(0); // 需要将所有的先置0
		if (checkboxStrArray != null) {// 需要判断checkboxStrArray不能为空
		    for (int j = 0; j < checkboxStrArray.length; j++) {
			if (userModel.getChName().equals(checkboxStrArray[j])) { // 根据是否被选中
			    // 来判断是否修改为1
			    userModel.setVisible(1);
			}
		    }
		}
		sql = homeUserDao.saveSql(userModel);
		db.addBatch(sql);
	    }
	    db.executeBatch();
	    db.close();
	} catch (Exception ex) {
	    SysLogger.error(ex.getMessage());
	} finally {
	}
	return "/user.do?action=home";
    }

    private String update() {
	String url = "/system/home/user/update.jsp";
	User uservo = (User) session.getAttribute(SessionConstant.CURRENT_USER);
	List homeUserModelList = new ArrayList();
	HomeUserDao homeUserDao = new HomeUserDao();
	homeUserModelList = homeUserDao.findByCondition(" where user_id='"
		+ uservo.getUserid() + "'");// 能控制的模块
	HomeRoleDao homeRoleDao = new HomeRoleDao();
	List homeRoleList = homeRoleDao.findByCondition(" where role_id='"
		+ uservo.getRole() + "' and visible='1'");// 能控制的模块
	if (homeUserModelList.isEmpty() || homeUserModelList == null) {
	    System.out.println("用户:" + uservo.getUserid() + "尚未进行首页模块个性显示设置！");
	    if (homeRoleList.isEmpty() || homeRoleList == null) {
		System.out.println("角色：" + uservo.getRole() + "模块分配(角色)"); // 跳转到主页
		return "/user.do?action=home";
	    } else {
		for (int i = 0; i < homeRoleList.size(); i++) {
		    HomeRoleModel roleModel = (HomeRoleModel) homeRoleList
			    .get(i);
		    HomeUserModel userModel = new HomeUserModel();
		    userModel.setBusinessids(uservo.getBusinessids());
		    userModel.setChName(roleModel.getChName());
		    userModel.setDept_id(roleModel.getDept_id());
		    userModel.setEnName(roleModel.getEnName());
		    userModel.setName(uservo.getName());
		    userModel.setNote(roleModel.getNote());
		    userModel.setRole_id(uservo.getRole());
		    userModel.setUser_id(uservo.getUserid());
		    userModel.setVisible(roleModel.getVisible());
		    userModel.setType(roleModel.getType());// 种类设置
		    homeUserModelList.add(userModel);
		}
	    }
	}
	// 在查询一次数据库||或者直接冲上面的方法中组织数据
	RoleDao rd = new RoleDao();
	Role role = (Role) rd.findByID("" + uservo.getRole());
	request.setAttribute("role", role);
	request.setAttribute("homeUserModelList", homeUserModelList);
	ModuleDao moduleDao = new ModuleDao();
	List moduleList = moduleDao.findByCondition("");
	request.setAttribute("homeRoleList", homeRoleList);
	return url;
    }
}
