package com.bpm.system.utils;

import java.util.UUID;

/**
 * 
 * Description: 获取唯一主键
 * UUIDKey.java Create on 2012-10-16 下午2:07:31 
 * @author hexinlin
 * Copyright (c) 2012 DHCC Company,Inc. All Rights Reserved.
 */
public class UUIDKey {

	/**
	 * 
	 * Description: 获取UUID
	 * Date:2012-10-16
	 * @author hexinlin
	 * @return String
	 */
	public static String getKey() {
		
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
