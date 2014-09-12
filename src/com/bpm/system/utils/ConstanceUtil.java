package com.bpm.system.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.persistence.entity.GroupEntity;

/**
 * 
 * Description:
 * ConstanceUtil.java Create on 2012-10-17 下午2:30:33 
 * @author hexinlin
 * Copyright (c) 2012 DHCC Company,Inc. All Rights Reserved.
 */
public class ConstanceUtil {

	public static final String AUTHKEY = "authkey"; //登陆验证
	public static final String USER = "user"; //存储用户在session的标示
	public static final String LOGINACTION = "controller/login.action";//用于登陆的action不需要进行权限验证
	
	
	
	public static final String PG="parallelGateway";//平行通道
	public static final String EG="exclusiveGateway";//分支通道
	public static final String START="startEvent";//开始节点
	public static final String UT="userTask";//用户任务节点
	public static final Map<String,String> map = new HashMap<String, String>(); 
	public static final String BEFORE_END = "流程发起人";//用于结束流程
	public static final String PRO_START="1"; //流程启动
	public static final String PRO_RUN="2"; //流程运行
	public static final String PRO_HALF="3"; //流程半截
	public static final String PRO_END="4"; //流程结束
	
	public static final String PRO_START_MESSAGE="启动"; //流程启动
	public static final String PRO_RUN_MESSAGE="运行中"; //流程运行
	public static final String PRO_HALF_MESSAGE="办结"; //流程半截
	public static final String PRO_END_MESSAGE="结束"; //流程结束
	
	public static final String FORM_WRITE="0";//表单仅写
	public static final String FORM_READ="1";//表单仅读
	public static final String FORM_READ_WRITE="2";//表单读和写
	
	public static final String Process_BANJIE="1";//要办结
	public static final String NO_Process_BANJIE="0";//不需要办结
	
	public static List<GroupEntity> bpmgroup  = new ArrayList<GroupEntity>();


	public static List<GroupEntity> getBpmgroup() {
		return bpmgroup;
	}


	public static void setBpmgroup(List<GroupEntity> bpmgroup) {
		ConstanceUtil.bpmgroup = bpmgroup;
	}
	
	static{
		map.put("taskFormType", "0");
	}

}

