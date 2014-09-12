
package com.database.config;
import java.util.*;

//∂¡»°≈‰÷√Œƒº˛
public class SystemConfig {
	
	
    //static String configFile = "SystemConfigResources";
    public static String getConfigInfomation(String finename,String itemIndex) {
        try {
            ResourceBundle resource = ResourceBundle.getBundle(finename);
            return resource.getString(itemIndex);
        } catch (Exception e) {
            return "";
        }
    }
    
    public static void main(String arg[])
    {
    	SystemConfig config=new SystemConfig();
    	
    	System.out.println(config.getConfigInfomation("SystemConfigResources","Acquire_Increment"));
    }
}


