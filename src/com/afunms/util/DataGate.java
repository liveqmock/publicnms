
package com.afunms.util;

import com.afunms.util.connectionPool.DBConnectPoolManager;
//import com.database.DBConnectionManager;

import java.sql.Connection;

public class DataGate {

    /**
     * @roseuid 3AD1A51103A6
     */
    public static Connection getCon() throws Exception {
//    	DBConnectionManager db=new DBConnectionManager();
// 		return db.getConnection();
        return DBConnectPoolManager.getInstance().getConnection();
    }

    /**
     * @roseuid 3AD1A5180270
     */
    public static void freeCon(Connection con) throws Exception {
        DBConnectPoolManager.getInstance().freeConnection(con);
    }    
}
