package com.sxmcc.bwzy.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendSOAP
{

    public SendSOAP()
    {
    }

    protected static String send(String uri, String soapbody, String soapAction)
    {
        StringBuffer sb;
        HttpURLConnection connection = null;
        sb = new StringBuffer();
        OutputStream os = null;
        InputStream is = null;
        try
        {
            URL u = new URL(uri);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
            System.setProperty("sun.net.client.defaultReadTimeout", "30000");
            connection = (HttpURLConnection)u.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            connection.setRequestProperty("SOAPAction", soapAction);
            connection.setDoOutput(true);
            os = connection.getOutputStream();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><soapenv:Body>");
            sb.append(soapbody);
            sb.append("</soapenv:Body></soapenv:Envelope>");
            os.write(sb.toString().getBytes("UTF-8"));
            os.flush();
            sb = new StringBuffer();
            is = connection.getInputStream();
            String line;
            for(BufferedReader in = new BufferedReader(new InputStreamReader(is)); in.ready(); sb.append(line))
                line = in.readLine();

        }
        catch(IOException e)
        {
            
            //appendErrorStream(connection, sb);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeStream(is);
            closeStream(os);
            if(connection != null)
                connection.disconnect();
        }
        return sb.toString();
    }

    private static void closeStream(InputStream s)
    {
        if(s != null)
            try
            {
                s.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
    }

    private static void closeStream(OutputStream s)
    {
        if(s != null)
            try
            {
                s.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
    }

//    private static void appendErrorStream(HttpURLConnection connection, StringBuffer sb)
//    {
//        InputStream is = null;
//        Exception e1;
//        try
//        {
//            is = connection.getErrorStream();
//            if(is == null)
//                return;
//        }
//        finally
//        {
//            closeStream(is);
//            if(connection != null)
//                connection.disconnect();
//        }
//        String line;
//        for(BufferedReader ei = new BufferedReader(new InputStreamReader(is)); ei.ready(); sb.append(line))
//            line = ei.readLine();
//
//        break MISSING_BLOCK_LABEL_89;
//        e1;
//        e1.printStackTrace();
//        return;
//    }

    private static final String SOAP_HEAD = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><soapenv:Body>";
    private static final String SOAP_FOOT = "</soapenv:Body></soapenv:Envelope>";
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final int TIMEOUT = 60000;
}