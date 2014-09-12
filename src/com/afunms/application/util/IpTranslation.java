package com.afunms.application.util;

public class IpTranslation {

	/**
	 * 
	 * @return 从当前位置读取4个字节并以长整形的方式输出
	 */
	public static int readLong4(byte[]buf) {

		int ret = 0;
		try {
			//byte[]buf=readBytes(4);
			ret|=(buf[0]<<24)&0xff000000;
			ret|=(buf[1]<<16)&0xff0000;
			ret|=(buf[2]<<8)&0xff00;
			ret|=buf[3]&0xff;
		} catch (Exception e) {
			e.printStackTrace();
			ret = -1;
		}
		return ret;
	}
	

	/**
	 * 
	 * @param number将10进制的数字转换成16进制的字符串
	 * @return
	 */
	public static String fromDecimalToHex(int number) {
		int position = 28;
		StringBuilder builder = new StringBuilder();
		boolean isFirst=false;
		for (; position >= 0; position=position-4) {
			int h = number >>> position;
			int t = h & 15;
			if (t < 10){
				if(t!=0||isFirst){
					isFirst=true;
					builder.append(t);
				}
		}		
			else if (t >= 10 && t < 16) {
				isFirst=true;
				switch (t) {
				case 10:
					builder.append("a");
					break;
				case 11:
					builder.append("b");
					break;
				case 12:
					builder.append("c");
					break;
				case 13:
					builder.append("d");
					break;
				case 14:
					builder.append("e");
					break;
				case 15:
					builder.append("f");
					break;
				}
			}
		}
		return builder.toString();

	}
	/**
	 * 
	 * @param ip
	 * @return 将ip地址转换成字节数组返回
	 */
	public static byte[] getBytesFromIP(String ip){	
		byte[]buf=new byte[4];
		try{
			ip=ip.trim();
			String []ips=ip.split("\\.");
			for(int i=0;i<4;i++){
				int id=Integer.parseInt(ips[i]);
				buf[i]=(byte)(id&0xff);
			}
		}catch(Exception e){
			e.printStackTrace();
		  return null;	
		}
		return buf;
	}
	public static int fromHexToDecimal(String hex){
		if(hex==null&&hex.length()==0)
			return 0;
		if(hex.length()>8)
			hex=hex.substring(0,8);
		char c[]=hex.toCharArray();
		int begin=1;
		int sum =0;
		for(int i=c.length-1;i>=0;i--){
			if(c[i]>=48&&c[i]<=57){
				sum=sum+begin*(c[i]-48);
			}else if(c[i]>=65&&c[i]<=70){
				sum=sum+begin*(c[i]-55);
			}else if(c[i]>=97&&c[i]<=102){
				sum=sum+begin*(c[i]-87);
			}else {
				throw new RuntimeException("格式不正确");
			}
			begin=begin*16;
		}
		return sum;
	}
	public static String[] getIpFromHex(String hex){
		int i=fromHexToDecimal(hex);
		String[]ip=new String[4];
		ip[0]=String.valueOf((i&0xff000000)>>>24);
		ip[1]=String.valueOf((i&0xff0000)>>>16);
		ip[2]=String.valueOf((i&0xff00)>>>8);
		ip[3]=String.valueOf((i&0xff));
		return ip;
	}
	
	public static String formIpToHex(String ip){
		int i=readLong4(getBytesFromIP(ip));
		//System.out.println(Integer.toString(i,16));
		return fromDecimalToHex(i);
	}
	public static void main(String[]args){
		//getIpFromHex("ffffffff");
		System.out.println(formIpToHex("159.255.255.255"));
	}
}
