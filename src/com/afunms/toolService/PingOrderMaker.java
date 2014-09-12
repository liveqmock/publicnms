package com.afunms.toolService;




/**
 * */
 class PingOrderMaker {
	 

	 public String getPingOrder (PingProperty property ,String ip){			
		 String order = " ping "+ip;
		 boolean a = property.isA();
		 boolean f = property.isF();
		 int n = property.getN();
		 int l = property.getL();
		 int w = property.getW();

		 String os = System.getProperty("os.name").toLowerCase();
		 if (os.indexOf("windows") > -1){
			 
			 if(a){
				 order +=" -a ";
			 }
			 if(f){
				 order +=" -f ";
			 }
			  //次数
			 order +=" -n "+n ;
			 //包长
			 order +=" -l "+l ;
			 //超时
			 order +=" -w "+w*1000 ;
		 }
		 else if(os.indexOf("linux") > -1){

			 if(a){
				 order +=" -a ";
			 }
			 if(f){
				 order +=" -f ";
			 }
			 //次数
			 order +=" -c "+n ;
			 //包长
			 order +=" -s "+l ;
			 //超时
			 order +=" -w "+w ;
		 }
		 else{
			order = order.replace("ip", ip);
			order = order.replace("chaoshi", String.valueOf(w));
			order = order.replace("cishu", String.valueOf(n));
			order = order.replace("baochang", String.valueOf(l));
		 }
		 return order ;
		 
	 }
}