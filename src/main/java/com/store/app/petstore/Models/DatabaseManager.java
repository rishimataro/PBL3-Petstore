package com.store.app.petstore.Models;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class DatabaseManager {
    public static Connection connect() {
        Dotenv dotenv = Dotenv.load();
        final String hostName = dotenv.get("DB_HOST_NAME");
        final String dbName = dotenv.get("DB_NAME");
        final String username = dotenv.get("DB_USER");
        final String password = dotenv.get("DB_PASSWORD");
        final String connectionURL = "jdbc:mysql://" + hostName + "/" + dbName + "?useSSL=false&allowPublicKeyRetrieval=true";

        System.out.println("Connecting to database...");
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
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection(PreparedStatement ps) {
        try {
            if (ps != null && !ps.isClosed()) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

