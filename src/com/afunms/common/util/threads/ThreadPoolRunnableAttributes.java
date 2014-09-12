/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.afunms.common.util.threads;

import java.util.Hashtable;

/** 
 * 用于给指定的线程带上属性和注释。
 * 
 * @author 聂林
 * @version 1.0 $Date: 2011-03-12 22:34:30 +0100 (Sat, Mar 12, 2011) $
 * 
 */
public class ThreadPoolRunnableAttributes {
	
	/**
	 * 线程的优先级，默认为 Thread.NORM_PRIORITY
	 */
	private int priority = Thread.NORM_PRIORITY;
	
	private Hashtable<Object, Object> attributes = new Hashtable<Object, Object>();
    

    public ThreadPoolRunnableAttributes() {
    	
    }
    
    public void setAttribute(String name, Object object){
    	if(name != null){
    		attributes.put(name, object);
    	}
    	
    }
    
    public Object getAttribute(String name){
    	if(name == null){
    		return null;
    	}
    	return attributes.get(name);
    }
    
	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}


	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
    
}
