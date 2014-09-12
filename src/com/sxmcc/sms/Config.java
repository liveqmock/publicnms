// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi 
// Source File Name:   Config.java

package com.sxmcc.sms;

import java.io.InputStream;
import java.util.Properties;

public class Config
{

    private static Properties pp;

    public Config()
    {
    }

    public static String getProp(String propName)
    {
        String retValue = "";
        try
        {
            if(pp == null)
            {
                pp = new Properties();
                InputStream is = (new Config()).getClass().getResourceAsStream("config.properties");
                pp.load(is);
                is.close();
            }
            retValue = pp.getProperty(propName);
            if(retValue != null)
                retValue = new String(retValue.getBytes("ISO-8859-1"), "GBK");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return retValue.trim();
    }
}
