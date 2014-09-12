/**
 * <p>Description:Error Mapping</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.base;

public class ErrorMessage
{
  private ErrorMessage()
  {
  }
  public static final int INCORRECT =1000;
  public static final int NO_LOGIN = 1001;
  public static final int NO_RIGHT = 1002;
  public static final int INCORRECT_PASSWORD = 1003;
  public static final int USER_EXIST = 1004;
  public static final int NO_ROLE = 1005;
  public static final int NO_DEPARTMENT = 1006;
  public static final int NO_POSITION = 1007;
  
  public static final int ACTION_NO_FOUND = 2001;
  public static final int CAN_NOT_CONNECT_DB = 2002;
  
  public static final int SYS_OID_EXIST = 3001;
  public static final int MOID_EXIST = 3002;
  public static final int SERVICE_EXIST = 3003;
  public static final int PING_FAILURE = 3004;
  public static final int SNMP_FAILURE = 3005;
  public static final int ADD_HOST_FAILURE = 3006;
  public static final int LINK_EXIST = 3007;  
  public static final int IP_ADDRESS_EXIST = 3008;
  public static final int IS_NOT_MGE_UPS = 3009;
  public static final int DOUBLE_LINKS = 3010;
  public static final int DEVICES_SAME = 3011;
  public static final int FILENAME_EXIST = 3012;//文件名已存在
  public static final int ERROR404 = 404;//资源无法访问
  public static final int ERROR500 = 500;//页面访问错误

  public static final int ADD_HOST_FAILURE_OUTNUMBER_MAX_SOURCE_NUMBER = 1000;//最大监控资源数
  public static final int ADD_HOST_FAILURE_OUTNUMBER_MAX_SOURCE = 3013;//超过最大监控资源数
  
  public static final int SCHEDULE_PERIOD_EXIST = 600;
  public static final int SCHEDULE_POSITION_EXIST = 601;

  
  
  public static String getErrorMessage(int messageCode)
  {
     switch(messageCode)
     {
     	case INCORRECT:return "密码不正确！";
        case NO_LOGIN: return "对不起,您没有登录,或已超时,请重新登录!";
        case NO_RIGHT: return "对不起,您没有权限执行该操作,请与系统管理员联系!";
        case INCORRECT_PASSWORD: return "对不起,用户名或密码不正确!";
        case USER_EXIST: return "对不起,该用户名已经存在,请重新输入!";
        case NO_ROLE: return "'角色'为空,请先在[系统管理->角色]中增加'角色'!";
        case NO_DEPARTMENT: return "'部门'为空,请先在[系统管理->部门]中增加'部门'!";
        case NO_POSITION: return "'职务'为空,请先在[系统管理->职务]中增加'职务'!";
        
        case ACTION_NO_FOUND: return "没有相应的操作!";
        
        case SYS_OID_EXIST: return "这个系统OID已经存在!";
        case MOID_EXIST: return "这个监视器ID已经存在!";
        case SERVICE_EXIST: return "这个服务已经存在!";
        case IP_ADDRESS_EXIST: return "这个IP地址已经存在!";
        case PING_FAILURE: return "设备Ping不通,增加失败!";
        case SNMP_FAILURE: return "设备不支持SNMP或者共同体不正确,增加失败!";
        case ADD_HOST_FAILURE: return "增加失败,未知错误!";
        case LINK_EXIST: return "链路已经存在,增加失败!";
        case DOUBLE_LINKS:  return "这两台设备已经存在双链路，不能再增加链路!";
        case DEVICES_SAME:  return "这两台设备为同一设备，不能增加链路!";
        case CAN_NOT_CONNECT_DB: return "对不起，不能连接数据库!";        
        case IS_NOT_MGE_UPS: return "该设备不是梅兰日兰UPS!";   
        case ERROR500 : return "页面访问错误";
        case ERROR404 : return "资源无法访问";
        case FILENAME_EXIST : return "该文件名已存在";
        case ADD_HOST_FAILURE_OUTNUMBER_MAX_SOURCE : return "超过最大监控资源数,当前最多监控"+ADD_HOST_FAILURE_OUTNUMBER_MAX_SOURCE_NUMBER+"个资源";
        
        case SCHEDULE_PERIOD_EXIST : return "该班次已存在";
        case SCHEDULE_POSITION_EXIST : return "该值班地点已存在";

        default:return "未知错误";
     }
  }
}
