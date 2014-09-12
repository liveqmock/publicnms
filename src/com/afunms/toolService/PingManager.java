package com.afunms.toolService;

import java.util.Iterator;
import java.util.List;

public class PingManager {

	private String ip ;
	private PingProperty  pro = new PingProperty();
	private PingExecute pe = new PingExecute();
	
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public PingManager(String ip){
		this.ip = ip ;
	}
	public PingManager(String ip ,boolean a ,boolean f ,int n ,int l ,int w){
		
		this.pro.setA(a);
		this.pro.setF(f);
		this.ip = ip ;
		if(n <= 0){
			this.pro.setN(1);
		}else{
			this.pro.setN(n);
		}
		if(w <=0 ){
			this.pro.setW(1);
		}else{
			this.pro.setW(w);
		}
		if(l<=0 || l>65527){
			this.pro.setL(32);
		}else{
			this.pro.setL(l);
		}
	}
	
	public PingManager(String ip ,int n ,int l ,int w){
		this.ip = ip ;
		if(n <= 0){
			this.pro.setN(1);
		}else{
			this.pro.setN(n);
		}
		if(w <=0 ){
			this.pro.setW(1);
		}else{
			this.pro.setW(w);
		}
		if(l<=0 || l>65527){
			this.pro.setL(32);
		}else{
			this.pro.setL(l);
		}
	}
	
	public void executePingResult(String id){
		String order = new PingOrderMaker().getPingOrder(pro, ip);
		pe.executePing(order,ip,id);
	}
	
	public static void main(String[] args){

	}
	
	
	
}
