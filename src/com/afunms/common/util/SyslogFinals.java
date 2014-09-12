/*
 * Created on 20011-8-31
 * zhangys
 */
package com.afunms.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class SyslogFinals {
	private static final Map eventMap = new HashMap();
	public static final Map<Integer, String> devCategoryMap = new HashMap<Integer, String>();
	
	public static final String USERLOGINSUCCESS = "userLoginSuccess";
	public static final String USERLOGINFAILURE = "userLoginFailure";
	public static final String USERLOGOUTSUCCESS = "userLogoutSuccess";
	
	public static final String NWDEVUSERLOGINSUCCESS = "nwDevUserLoginSuccess";
	public static final String NWDEVUSERLOGINFAILURE = "nwDevUserLoginFailure";
	public static final String NWDEVUSERLOGOUTSUCCESS = "nwDevUserLogoutSuccess";	
	
	public static final String QSUSERCHANGEPASSSUCCESS = "qsUserChangePassSuccess";
	public static final String QSUSERCHANGEPASSFAILURE = "qsUserChangePassFailure";
	public static final String QSUSERACCOUNTDISABLED = "qsUserAccountDisabled";
	public static final String QSUSERMODIFYLOG = "qsUserModifyLog";
	
	public static final String QSLOGINFAILURE = "qsLoginFailure";
	public static final String QSNWLOGINFAILURE = "qsNWLoginFailure";
	public static final String QSACCOUTABUSED = "qsAccoutAbused";
	
	private static final List<List> userLoginSuccessList = new ArrayList<List>();
	private static final List<List> userLoginFailureList = new ArrayList<List>();
	private static final List<List> userLogoutSuccessList = new ArrayList<List>();
	
	private static final List<List> nwDevUserLoginSuccessList = new ArrayList<List>();
	private static final List<List> nwDevUserLoginFailureList = new ArrayList<List>();
	private static final List<List> nwDevUserLogoutSuccessList = new ArrayList<List>();	
	
	private static final List<List> qsUserChangePassSuccessList = new ArrayList<List>();	//密码修改成功的用户 
	private static final List<List> qsUserChangePassFailureList = new ArrayList<List>();	//密码修改失败的用户 
	private static final List<List> qsUserAccountDisabledList = new ArrayList<List>();		//哪些账号被删除/禁用？
	private static final List<List> qsUserModifyLogList = new ArrayList<List>();			//哪些用户修改或者清除了安全审计日志
	
	private static final List<List> qsLoginFailureList = new ArrayList<List>();				//哪些机器的登录失败次数最多
	private static final List<List> qsNWLoginFailureList = new ArrayList<List>();			//网络中发生了多少登录失败的事件？
	private static final List<List> qsAccoutAbusedList = new ArrayList<List>();				//哪些用户账号被滥用事件比较多？
	
//	系统时间已更改
	
	static{
		devCategoryMap.put(1, "路由器");
		devCategoryMap.put(2, "路由交换机");
		devCategoryMap.put(3, "交换机");
		devCategoryMap.put(4, "服务器");
		devCategoryMap.put(5, "其他");
		devCategoryMap.put(6, "其他");
		devCategoryMap.put(7, "路由器");
		devCategoryMap.put(8, "防火墙");
		devCategoryMap.put(9, "ATM");
		devCategoryMap.put(13, "CMTS");
		devCategoryMap.put(14, "存储");
	
		//多个条件之间的逻辑关系，1: and 2：or 3: not
		//一条条件中的关键字是否有序，0：无序 sql如message like '%xxx%' and message like '%yyy%'，1：有序 sql如message like '%xxx%yyy%',性能问题
		userLoginSuccessList.add(new ArrayList(Arrays.asList(1, 1, "登录成功: 用户名:","登录 ID:","登录类型: 2")));
		userLoginSuccessList.add(new ArrayList(Arrays.asList(0, 1, "成功的网络登录: 用户名:","登录 ID:","登录类型: 3")));
		userLoginSuccessList.add(new ArrayList(Arrays.asList(0, 1, "登录成功: 用户名:","登录 ID:","登录类型: 5")));
		userLoginSuccessList.add(new ArrayList(Arrays.asList(0, 1, "登录成功: 用户名:","登录 ID:","登录类型: 7")));
		eventMap.put(USERLOGINSUCCESS, userLoginSuccessList);
		
		userLoginFailureList.add(new ArrayList(Arrays.asList(1, 0, "登录失败: 原因:","用户名:","登录类型: 2")));
		userLoginFailureList.add(new ArrayList(Arrays.asList(2, 0, "登录失败: 原因:","用户名:","登录类型: 3")));
		eventMap.put(USERLOGINFAILURE, userLoginFailureList);

		userLogoutSuccessList.add(new ArrayList(Arrays.asList(1, 0, "用户注销: 用户名:","登录 ID:","登录类型: 2")));
		userLogoutSuccessList.add(new ArrayList(Arrays.asList(1, 0, "用户注销: 用户名: ANONYMOUS LOGON","登录 ID:","登录类型: 3")));
		userLogoutSuccessList.add(new ArrayList(Arrays.asList(1, 0, "用户注销: 用户名: Guest","登录 ID:","登录类型: 3")));
		eventMap.put(USERLOGOUTSUCCESS, userLogoutSuccessList);
		
		nwDevUserLoginSuccessList.add(new ArrayList(Arrays.asList(1, 0, "/LOGIN(l):")));
		eventMap.put(NWDEVUSERLOGINSUCCESS, nwDevUserLoginSuccessList);
		
		nwDevUserLoginFailureList.add(new ArrayList(Arrays.asList(1, 0, "/LOGINFAIL(l):")));
		nwDevUserLoginFailureList.add(new ArrayList(Arrays.asList(2, 0, "failed to login")));
		eventMap.put(NWDEVUSERLOGINFAILURE, nwDevUserLoginFailureList);
		
		nwDevUserLogoutSuccessList.add(new ArrayList(Arrays.asList(1, 0, "/LOGOUT(l):")));
		eventMap.put(NWDEVUSERLOGOUTSUCCESS, nwDevUserLogoutSuccessList);
		
		//密码修改成功的用户 id 628
		//LEN-D3117189Administrator: 设置了用户帐户密码: 目标帐户名: zhangyashe 目标域: LEN-D3117189 目标帐户 ID: %{S-1-5-21-1891745410-3080488357-2315099178-1004} 呼叫方用户名: Administrator 呼叫方所属域: LEN-D3117189 呼叫方登录 ID: (0x0,0x214F6)		
		qsUserChangePassSuccessList.add(new ArrayList(Arrays.asList(1,0,"设置了用户帐户密码: 目标帐户名:","目标域:","目标帐户 ID:")));
		eventMap.put(QSUSERCHANGEPASSSUCCESS, qsUserChangePassSuccessList);
		
		//密码修改失败的用户 TODO 不清楚日志格式 先用以下方式测试
		qsUserChangePassFailureList.add(new ArrayList(Arrays.asList(1,0,"修改了用户帐户密码: 目标帐户名:","目标域:","目标帐户 ID:")));
		eventMap.put(QSUSERCHANGEPASSFAILURE, qsUserChangePassFailureList);
		
		//0B03哪些账号被删除/禁用？629
		//LEN-D3117189Administrator: 停用了用户帐户: 目标帐户名: zhangyashe 目标域: LEN-D3117189 目标帐户 ID: %{S-1-5-21-1891745410-3080488357-2315099178-1004} 呼叫方用户名: Administrator 呼叫方所属域: LEN-D3117189 呼叫方登录 ID: (0x0,0x2211B)		
		qsUserAccountDisabledList.add(new ArrayList(Arrays.asList(1,0,"停用了用户帐户: 目标帐户名:","目标域:","目标帐户 ID:")));
		qsUserAccountDisabledList.add(new ArrayList(Arrays.asList(2,0,"删除了用户帐户: 目标帐户名:","目标域:","目标帐户 ID:")));
		eventMap.put(QSUSERACCOUNTDISABLED, qsUserAccountDisabledList);
		
		//哪些用户修改或者清除了安全审计日志？517
		//Eventlog was cleared: 'Security' TODO 没有用户名啊 
		qsUserModifyLogList.add(new ArrayList(Arrays.asList(1,0,"Eventlog was cleared:")));
		eventMap.put(QSUSERMODIFYLOG, qsUserModifyLogList);
		
		//哪些机器的登录失败次数最多
		qsLoginFailureList.add(new ArrayList(Arrays.asList(1, 0, "登录失败: 原因:","用户名:","登录类型: 2")));
//		qsLoginFailureList.add(new ArrayList(Arrays.asList(2, 0, "登录失败: 原因:","用户名:","登录类型: 3")));
		qsLoginFailureList.add(new ArrayList(Arrays.asList(2, 0, "/LOGINFAIL(l):")));
		qsLoginFailureList.add(new ArrayList(Arrays.asList(2, 0, "failed to login")));
		//qsLoginFailureList.add(new ArrayList(Arrays.asList(3, 0, "failed to login")));
		eventMap.put(QSLOGINFAILURE, qsLoginFailureList);
		
		//网络中发生了多少登录失败的事件？
		qsNWLoginFailureList.add(new ArrayList(Arrays.asList(1, 0, "登录失败: 原因:","用户名:","登录类型: 3")));
		qsNWLoginFailureList.add(new ArrayList(Arrays.asList(2, 0, "/LOGINFAIL(l):")));
		qsNWLoginFailureList.add(new ArrayList(Arrays.asList(2, 0, "failed to login")));
		eventMap.put(QSNWLOGINFAILURE, qsNWLoginFailureList);
		
		//TODO 哪些用户账号被滥用事件比较多？
		qsAccoutAbusedList.add(new ArrayList(Arrays.asList(1, 0, "登录失败: 原因:","用户名:","登录类型: 3")));
		qsAccoutAbusedList.add(new ArrayList(Arrays.asList(2, 0, "/LOGINFAIL(l):")));
		qsAccoutAbusedList.add(new ArrayList(Arrays.asList(2, 0, "failed to login")));
		eventMap.put(QSACCOUTABUSED, qsAccoutAbusedList);
	}
	
	private static String getSQL(List list, int iSort){
		String sql = "(";
		if (iSort == 0) {
			for (int i = 2; i < list.size(); i++) {
				sql += "message like '%" + (String)list.get(i) + "%'";
				if (i < list.size()-1) sql += " and ";
			}
		}else if(iSort == 1){
			sql += "message like '%";
			for (int i = 2; i < list.size(); i++) {
				sql += (String)list.get(i) + "%";
			}
			sql += "'";
		}
		sql += ")";
		return sql;
	}
	
	public static String getMsgClause(String key){
		List list = (ArrayList)eventMap.get(key);
		String sql = "";
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				List cdtList = (ArrayList)list.get(i);
				int flag = (Integer)cdtList.get(0);
				int iSort = (Integer)cdtList.get(1);
				
				if(flag == 3){
					continue;
				}
				
				if (sql.length() == 0) {
					sql = getSQL(cdtList, iSort);
				}else{
					if (flag == 1) {
						sql = sql + " and " + getSQL(cdtList, iSort);
					}else if(flag == 2){
						sql = sql + " or " + getSQL(cdtList, iSort);
					}
				}
			}
		}
		return " (" + sql + ") ";
	}

}
