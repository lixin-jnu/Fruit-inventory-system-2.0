package com.lixin.myssm.basedao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnUtil {

    /*
     * 事务管理中要求开启事务/提交事务/回滚事务所使用的MySQL数据库连接为同一个Connection对象;
     * 所以这里借助本地线程ThreadLocal来存储这个Connection对象;
     * 可以理解为履带机上传送一套工具包,A/B/C三个工人依次使用这套工具包;
     */
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String URL = "jdbc:mysql://localhost:3306/fruitdb?useUnicode=true&characterEncoding=utf-8";
    public static final String USER = "root";
    public static final String PWD = "123456";

    public static Connection getConn() {
        Connection conn = threadLocal.get();
        if (conn == null) {
            conn = createConn();
            threadLocal.set(conn);
        }
        return conn;
    }

    public static void closeConn() throws SQLException {//关闭数据库失败需要回滚事务
        Connection conn = threadLocal.get();
        if (conn == null) {
            return;
        }
        if (!conn.isClosed()) {
            conn.close();
            threadLocal.remove();
        }
    }

    private static Connection createConn() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USER, PWD);
        } catch (ClassNotFoundException | SQLException e) {
            //这里就不再外抛异常了,如果数据库都没有连接上,何谈回滚事务
            e.printStackTrace();
        }
        return null;
    }

}