package com.store.app.petstore.Models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectJDBC {
    final static private String hostName = "127.0.0.1:3306";
    final static private String dbName = "petstore";
    final static private String username = "root";
    final static private String password = "123@Thang";

    private static String connectionURL = "jdbc:mysql://"+hostName+"/"+dbName+"?useSSL=false";

    public static Connection connect(){
        //Tạo đối tượng Connection
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(connectionURL, username, password);
            System.out.println("Kết nối thành công");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }
    public static void disconnect(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Disconnected from MySQL!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

