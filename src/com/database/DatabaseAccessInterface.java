package com.database;
import java.sql.*;
import java.util.*;

/**
 * 
 *  数据库访问定义的接口
 * 
 * @author konglq
 *
 */

public interface DatabaseAccessInterface {
    public abstract void executeSQL(String sqlStatement) throws SQLException;//运行一个sql
    public abstract void executeSQL(String[] sqlStatement) throws SQLException;//运行多个sql
   // public abstract void executeSQL(String sqlStatement, Object parameters[]) throws SQLException;
    //public abstract void executeSQL(String sqlStatement, List parameters) throws SQLException;
    //public abstract void executeSQL(String[] sqlStatement, List parameters) throws SQLException;
   // public abstract Vector executeQuerySQL(String sqlStatement) throws SQLException;
   // public abstract Vector executeQuerySQL(String sqlStatement, Object parameters[]) throws SQLException;
    //public abstract Vector executeQuerySQL(String sqlStatement, List parameters) throws SQLException;
    //public abstract String getSequenceNum(String tableName) throws SQLException;
    
}

