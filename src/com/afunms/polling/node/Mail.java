/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;

import com.afunms.common.base.BaseVo;
import com.afunms.polling.node.Application;

import com.afunms.common.base.BaseVo;

import com.afunms.common.base.BaseVo;

public class Mail extends Application {

		private int id;
		private String name;
		private String address;
		private String ipaddress;
		private String username;
		private String password;
		private String recivemail;
		private int timeout;
		private int flag;
		private int monflag;
		private String sendmobiles;
		private String bid;
		private String sendemail;
		private String sendphone;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getIpaddress() {
			return ipaddress;
		}
		public void setIpaddress(String ipaddress) {
			this.ipaddress = ipaddress;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getRecivemail() {
			return recivemail;
		}
		public void setRecivemail(String recivemail) {
			this.recivemail = recivemail;
		}
		public int getTimeout() {
			return timeout;
		}
		public void setTimeout(int timeout) {
			this.timeout = timeout;
		}
		public int getFlag() {
			return flag;
		}
		public void setFlag(int flag) {
			this.flag = flag;
		}
		public int getMonflag() {
			return monflag;
		}
		public void setMonflag(int monflag) {
			this.monflag = monflag;
		}

		public String getSendmobiles() {
			return sendmobiles;
		}
		public void setSendmobiles(String sendmobiles) {
			this.sendmobiles = sendmobiles;
		}
		public String getBid() {
			return bid;
		}
		public void setBid(String bid) {
			this.bid = bid;
		}
		public String getSendemail() {
			return sendemail;
		}
		public void setSendemail(String sendemail) {
			this.sendemail = sendemail;
		}
		public String getSendphone() {
			return sendphone;
		}
		public void setSendphone(String sendphone) {
			this.sendphone = sendphone;
		}
		
	}