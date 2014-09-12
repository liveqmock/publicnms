package com.afunms.toolService.traceroute;


public class TraceRouteManager {
	private String ip;
	private int timeout; 
	private int maxttl;
	private TraceRouteExecute tre=new TraceRouteExecute();
	
	public TraceRouteManager(){
		
	}
	
	public TraceRouteManager(String ip,int timeout,int maxtt1){
		this.ip=ip;
		this.timeout=timeout*1000;
		this.maxttl=maxtt1;
	}
	
	public void executeTraceRouteResult(String id){
		//∆¥√¸¡Ó
		String order="tracert  -h "+this.maxttl+"  -w "+this.timeout+" " + ip;
		tre.executeTracert(order, ip, id);
	}
}
