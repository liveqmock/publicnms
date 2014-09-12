package com.sxmcc.bwzy.util;


import java.net.URLEncoder;



 
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2008-12-20
 * Time: 15:06:31
 * To change this template use File | Settings | File Templates.
 */
public class SendAlertSoap {
    public static String sendSMS(String soapUri, String corpid, String password, String dest, String userid, String tmsg, String sendUserid, String sendSeqId) {

 
//    String URL="http://10.204.16.248:8081";
        try {
            tmsg = URLEncoder.encode(tmsg, "GBK");
        } catch (Exception e) {
            e.printStackTrace();
             return null;
            //return "<?xml version='1.0' encoding='utf-8'?><sxsms><MsgId>" + sendSeqId + "</MsgId><RspCode>00000010</RspCode><RspDesc>信息编码发生错误<RspDesc/></sxsms>";
        }
        String mtype = "0";   //发送类型，是否立即发送,0：立即  1:按用户配置
        String soapBody = "<msgSend><corpId>" + corpid + "</corpId><password>" + password + "</password><dest>" + dest + "</dest>"
                + "<userId>" + userid + "</userId><msg>" + tmsg + "</msg><mtype>" + mtype + "</mtype><sendUser>"
                + sendUserid + "</sendUser><sendSeqId>" + sendSeqId + "</sendSeqId></msgSend>";
        String soapURI = soapUri + "/WebService/OAToSMS";
        String soapAction = "OAToSMS";
        String result = SendSOAP.send(soapURI, soapBody, "");

        //返回值是一个xml符串:<?xml version='1.0' encoding='utf-8'?><sxsms><MsgId>ff8080811d17d52e011d1d1062ac000a</MsgId><RspCode>00000000</RspCode></sxsms>
        //其中rspCode='00000000' 表示成功，其它代码则为失败

        return result;


    }

    public static String sendPush(String soapUri, String corpid, String password, String dest, String userid, String title, String tmsg, String sendUserid, String sendSeqId) {

        try {
            tmsg = URLEncoder.encode(tmsg, "GBK");
            title = URLEncoder.encode(title, "GBK");
        } catch (Exception e) {
            e.printStackTrace();
             return null;
            //return "<?xml version='1.0' encoding='utf-8'?><sxsms><MsgId>" + sendSeqId + "</MsgId><RspCode>00000010</RspCode><RspDesc>信息编码发生错误<RspDesc/></sxsms>";
        }
        String docType = "1";

        String soapBody = "<wapPush><corpId>" + corpid + "</corpId><password>" + password + "</password><dest>" + dest + "</dest>"
                + "<userId>" + userid + "</userId><title>" + title + "</title><msgUrl>" + tmsg + "</msgUrl><sendUser>"
                + sendUserid + "</sendUser><sendSeqId>" + sendSeqId + "</sendSeqId></wapPush>";
        String soapURI = soapUri + "/WebService/OAToWapPush";
        String soapAction = "OAToWapPush";
        String result = SendSOAP.send(soapURI, soapBody, "");
        //返回值是一个xml符串:<?xml version='1.0' encoding='utf-8'?><sxsms><MsgId>ff8080811d17d52e011d1d1062ac000a</MsgId><RspCode>00000000</RspCode></sxsms>
        //其中rspCode='00000000' 表示成功，其它代码则为失败

        return result;


    }

    public static String sendWapPush(String soapUri, String corpid, String password, String dest, String userid, String title, String tmsg, String sendUserid, String sendSeqId) {

        try {
            tmsg = URLEncoder.encode(tmsg, "GBK");
            title = URLEncoder.encode(title, "GBK");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
           // return "<?xml version='1.0' encoding='utf-8'?><sxsms><MsgId>" + sendSeqId + "</MsgId><RspCode>00000010</RspCode><RspDesc>信息编码发生错误<RspDesc/></sxsms>";
        }
        String docType = "1";

        String soapBody = "<wapPush><corpId>" + corpid + "</corpId><password>" + password + "</password><dest>" + dest + "</dest>"
                + "<userId>" + userid + "</userId><title>" + title + "</title><msgUrl>" + tmsg + "</msgUrl><sendUser>"
                + sendUserid + "</sendUser><sendSeqId>" + sendSeqId + "</sendSeqId></wapPush>";
        String soapURI = soapUri + "/WebService/AlertWapPush";
        String soapAction = "AlertWapPush";
        String result = SendSOAP.send(soapURI, soapBody, "");
        //返回值是一个xml符串:<?xml version='1.0' encoding='utf-8'?><sxsms><MsgId>ff8080811d17d52e011d1d1062ac000a</MsgId><RspCode>00000000</RspCode></sxsms>
        //其中rspCode='00000000' 表示成功，其它代码则为失败

        return result;


    }

    public static void main(String[] args) {

        String tmsg = "短信测试";



        String corpid = "99991"; //需要无线平台分配号码 ,第三方可放到本环境的配置文件中，方便修改
        String password = "12345678"; //需要无线平台分配密码
        String dest = "13643467507";  //接收人的手机号码
        String userid = "";   //如果用户没有手机号码，可以传递ＵＩＤ 如：maliang
        String mtype = "0";   //发送类型，是否立即发送,0：立即  1:按用户配置
        String sendUserid = "maliang"; //发送用户的姓名  ，可以为空;
        String sendSeqId = "ff8080811d17d52e011d1d1062ac000a";//发送方的序号，最长３２位
        String title = "测试短信soap方式";
 
        String soapuri = "http://10.204.16.246:8081";
        String result = SendAlertSoap.sendSMS(soapuri,corpid, password, dest,userid, tmsg,sendUserid,sendSeqId);
        // String result = SendAlertSoap.sendWapPush(soapuri,corpid, password, dest,userid, title,tmsg,sendUserid,sendSeqId);
            
       // System.out.println(result.getRspDesc());
//		System.out.println("客户端发送成功");

    }

   


}
