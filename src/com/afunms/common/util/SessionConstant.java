/**
 * <p>Description:constant class</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.util;

public class SessionConstant
{
   private SessionConstant()
   {
   }

   public static final String CURRENT_USER = "current_user";  //当前用户
   public static final String CURRENT_MENU = "current_menu";  //当前用户
   public static final String ERROR_INFO = "error_info";      //错误信息
   public static final int NO_USED_IP = 30;      //长时间不使用的ip
   public static final String HOME_TOPO_VIEW = "home_topo_view"; //首页要显示的拓扑图
   public static final String CURRENT_TOPO_VIEW = "current_topo_view"; //当前用户正看的拓扑图
   public static final String CURRENT_CUSTOM_VIEW = "current_custom_view"; //当前用户正看的定制视图
   public static final String CURRENT_AREA = "current_AREA"; //当前用户正看的拓扑图所在的区
   public static final String CURRENT_ADMIN = "current_admin"; //当前用户是超级管理员
   public static final String CURRENT_SUBMAP_VIEW = "current_submap_view"; //当前用户正看的子图yangjun add
   public static final int ROLE_ADMIN = 0;

   public static boolean isRoleAdmin(int role) {
       return ROLE_ADMIN == role;
   }
}
