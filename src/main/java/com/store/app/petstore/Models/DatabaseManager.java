package com.store.app.petstore.Models;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class DatabaseManager {
    private static Connection connection = null;

    public static Connection connect() {
        try {
            if (connection == null || connection.isClosed()) {
                Dotenv dotenv = Dotenv.load();
                final String hostName = dotenv.get("DB_HOST_NAME");
                final String dbName = dotenv.get("DB_NAME");
                final String username = dotenv.get("DB_USER");
                final String password = dotenv.get("DB_PASSWORD");
                final String connectionURL = "jdbc:mysql://" + hostName + "/" + dbName
                        + "?useSSL=false&allowPublicKeyRetrieval=true";

                connection = DriverManager.getConnection(connectionURL, username, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Disconnected from MySQL!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeStatement(PreparedStatement ps) {
        try {
            if (ps != null && !ps.isClosed()) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeStatement(Statement stmt) {
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeResultSet(ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
