package com.store.app.petstore.Models;

import java.sql.*;

public class DatabaseManager {
    final static private String hostName = "125.212.231.184:6969";
    final static private String dbName = "PetStoreDB";
    final static private String username = "mintori09";
    final static private String password = "Mintory09@96";

    final private static String connectionURL = "jdbc:mysql://"+hostName+"/"+dbName+"?useSSL=false&allowPublicKeyRetrieval=true";

    public static Connection connect(){
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(connectionURL, username, password);
            System.out.println("Kết nối thành công");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Disconnected from MySQL!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void closeConnection(ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void closeConnection(PreparedStatement ps) {
        try {
            if (ps != null && !ps.isClosed()) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

