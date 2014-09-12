/*
 * Created on 2005-1-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.afunms.application.tomcatmonitor;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ServerInfo {
	Hashtable hServer = new Hashtable();
	Hashtable hServerInfo = new Hashtable();
	Hashtable hServerInfoFi = new Hashtable();
	HashMap map;
	int sNum;
	int eNum;
	public ServerInfo(){
		
	}
	
	public ServerInfo(HashMap map,int sNum,int eNum){
		this.map = map;
		this.sNum = sNum;
		this.eNum = eNum;
	}
	
	public Hashtable hServerTag(){
		int j=0;
		for(int i=0;i<map.size();i++){
			if(i<eNum+1&&i>sNum-1){
				String str = map.get(String.valueOf(i)).toString();
				hServer.put(String.valueOf(j),str);
				j++;
			}
		}
		return hServer;
	}
	
	//处理html标签后的服务器信息，待进一步处理
	public Hashtable hServerInfo(Hashtable hashTable){
		String tmpStr;
		String contextStr;
		int id=0;
		for(int i=0;i<hashTable.size();i++){
			tmpStr = hashTable.get(String.valueOf(i)).toString().trim();
			contextStr=getContext(tmpStr);
			if(!"".equalsIgnoreCase(contextStr)){
				hServerInfo.put(String.valueOf(id),contextStr);
				id++;
			}
		}
		return hServerInfo;
	}
	
	//将处理标签后的内容进一步处理，属性和值对应
	public Hashtable hServerInfoFi(Hashtable hashTable){
		hashTable.remove(String.valueOf(0));
		int i = hashTable.size();
		if(i%2!=0){
			System.err.println("内容有错误，不能继续进行！");
			System.exit(0);
		}
		for(int j=0;j<i/2;j++){   //hashTable.get(String.valueOf(j+1))+":"+
			//System.out.println(hashTable.get(String.valueOf(i/2+j+1))+"^^^^^^^^^^^^^^^^^^^^^^^");
			hServerInfoFi.put(String.valueOf(j),hashTable.get(String.valueOf(i/2+j+1)));
		}
		return hServerInfoFi;
	}
	//解析服务器信息html源代码，返回内容信息
	public String getContext(String str){
		String returnStr = "";
		StringBuffer tmpBuf = new StringBuffer(str);
		int pos;
		while(tmpBuf.toString().startsWith("<")){
			pos = tmpBuf.indexOf(">");
			tmpBuf.delete(0,pos+1);
		}
		while(tmpBuf.toString().endsWith(">")){
			pos = tmpBuf.indexOf("<");
			tmpBuf.delete(pos,tmpBuf.length()+1);
		}
		returnStr = tmpBuf.toString();
		return returnStr;
	}
}
