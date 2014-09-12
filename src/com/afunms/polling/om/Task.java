package com.afunms.polling.om;

import java.io.Serializable;
  

/** @author Hibernate CodeGenerator */
public class Task implements Serializable {
	private String taskname;
	private String startsign="1";//1表示已启动，0未启动
	private String modify="0";//0表示未修改，1表示修改
	private Float polltime;
    private String polltimeunit="m";//d天，h小时，m分钟，s秒
	public Task(){
	}
	public Task(String taskname,String modify,String startsign,Float polltime ) {
		this.taskname =taskname;
		this.startsign = startsign;
		this.modify = modify;
		this.polltime = polltime;
   }
	/**
	 * @return
	 */
	public String getModify() {
		return modify;
	}



	/**
	 * @return
	 */
	public String getStartsign() {
		return startsign;
	}

	/**
	 * @return
	 */
	public String getTaskname() {
		return taskname;
	}

	/**
	 * @param string
	 */
	public void setModify(String string) {
		modify = string;
	}


	/**
	 * @param string
	 */
	public void setStartsign(String string) {
		startsign = string;
	}

	/**
	 * @param string
	 */
	public void setTaskname(String string) {
		taskname = string;
	}



	/**
	 * @return
	 */
	public Float getPolltime() {
		return polltime;
	}

	/**
	 * @return
	 */
	public String getPolltimeunit() {
		return polltimeunit;
	}

	/**
	 * @param float1
	 */
	public void setPolltime(Float float1) {
		polltime = float1;
	}

	/**
	 * @param string
	 */
	public void setPolltimeunit(String string) {
		polltimeunit = string;
	}

}