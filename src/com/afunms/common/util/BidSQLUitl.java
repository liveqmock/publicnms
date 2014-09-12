/*
 * @(#)BidSQLUitl.java     v1.01, Jan 6, 2013
 *
 * Copyright (c) 2011, TNT All Rights Reserved.
 */

package com.afunms.common.util;

/**
 * ClassName:   BidSQLUitl.java
 * <p>
 *
 * @author      ÄôÁÖ
 * @version     v1.01
 * @since       v1.01
 * @Date        Jan 6, 2013 4:24:13 PM
 */
public class BidSQLUitl {

    public static final String DEFAULT_FIELD_NAME = "bid";

    public static final String HOST_NODE = DEFAULT_FIELD_NAME;

    public static final String DB = DEFAULT_FIELD_NAME;

    public static final String TOMCAT = DEFAULT_FIELD_NAME;
    
    public static final String RESIN = DEFAULT_FIELD_NAME;
    public static final String MQ = "netid";

    public static final String DOMINO = "netid";

    public static final String WAS = "netid";

    public static final String WEBLOGIC = "netid";

    public static final String IIS = "netid";

    public static final String CICS = "netid";

    public static final String DNS = "netid";

    public static final String JBOSS = "netid";

    public static final String APACHE = "netid";

    public static final String TUXEDO = DEFAULT_FIELD_NAME;

    public static final String EMAIL = DEFAULT_FIELD_NAME;

    public static final String FTP = DEFAULT_FIELD_NAME;

    public static final String TFTP = DEFAULT_FIELD_NAME;

    public static final String WEB = "netid";

    public static final String DHCP = "netid";

    public static final String PSTYPE = DEFAULT_FIELD_NAME;

    public static final String TOPO = DEFAULT_FIELD_NAME;
    
    public static String getBidSQL(String businessId, String fieldName) {
        StringBuffer s = new StringBuffer();
        int _flag = 0;
        if (businessId != null) {
            if (businessId != "-1") {
                String[] bids = businessId.split(",");
                if (bids.length > 0) {
                    for (int i = 0; i < bids.length; i++) {
                        if (bids[i].trim().length() > 0) {
                            if (_flag == 0) {
                                s.append(" and ( " + fieldName + " like '%,"
                                        + bids[i].trim() + ",%' ");
                                _flag = 1;
                            } else {
                                s.append(" or " + fieldName + " like '%,"
                                        + bids[i].trim() + ",%' ");
                            }
                        }
                    }
                    s.append(") ");
                }
            }
        }
        String sql = s.toString();
        return sql;
    }
}

