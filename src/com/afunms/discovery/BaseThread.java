/**
 * <p>Description:Discover Complete</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project chongqing electric power
 * @date 2006-12-17
 */

package com.afunms.discovery;

public class BaseThread extends Thread
{
   	protected boolean completed = false;

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}	
}