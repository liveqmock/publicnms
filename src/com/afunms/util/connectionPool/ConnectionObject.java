package com.afunms.util.connectionPool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 崭ConnectionObject质旨蠢钩矣档断?
 */
public class ConnectionObject {
    private Connection con;
    private boolean inUse;
    private long lastAccess;
    private int useCount;
    private long startTime;

    public ConnectionObject() {
    }

    /**
     * 检查ConnectionObject对象中的Connection对象con是否可用
     *
     * @roseuid 39FB9661004D
     */
    public boolean isAvailable() throws SQLException {
        boolean available = false;
        try {
            // To be available, the connection cannot be in use
            // and must be open
            if (con != null) {
                if (!inUse && !con.isClosed()) {
/*            try
            {
              conn.getMetaData();
            }catch (Exception e)
            {
              available = false;
            }
          */
                    available = true;
                }
            }
        } catch (SQLException ex) {
            throw ex;
        }

        return available;
    }


    public void setCon(java.sql.Connection newCon) {
        con = newCon;
    }

    public java.sql.Connection getCon() {
        return con;
    }

    public void setInUse(boolean newInUse) {
        inUse = newInUse;
    }

    public boolean isInUse() {
        return inUse;
    }


    public void setLastAccess(long newLastAccess) {
        lastAccess = newLastAccess;
    }

    public long getLastAccess() {
        return lastAccess;
    }

    public void setUseCount(int newUseCount) {
        useCount = newUseCount;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setStartTime(long newStartTime) {
        startTime = newStartTime;
    }

    public long getStartTime() {
        return startTime;
    }


}
