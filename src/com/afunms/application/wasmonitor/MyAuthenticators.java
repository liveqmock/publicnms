package com.afunms.application.wasmonitor;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class MyAuthenticators extends Authenticator {
	private String userName ;
	private String password ;
	public MyAuthenticators(String userName,String password){
		this.userName = userName;
		this.password = password;
	}
	protected PasswordAuthentication getPasswordAuthentication() {
		char pwd [] = new char[password.length()];
		for(int i=0;i<password.length();i++){
			pwd[i] = password.charAt(i);
		}
		return new PasswordAuthentication(userName, pwd);// 此处参数为用户名和密码
	}
}
