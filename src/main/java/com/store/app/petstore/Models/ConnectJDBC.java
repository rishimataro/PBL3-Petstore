package com.store.app.petstore.Models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectJDBC {
    final static private String hostName = "125.212.231.184:6969";
    final static private String dbName = "PetStoreDB";
    final static private String username = "mintori09";
    final static private String password = "Mintory09@96";

    private static String connectionURL = "jdbc:mysql://"+hostName+"/"+dbName+"?useSSL=false&allowPublicKeyRetrieval=true";

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