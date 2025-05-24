package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.DatabaseManager;

import java.sql.*;

public class DatabaseUtil {
    public static Connection getConnection() throws SQLException {
        return DatabaseManager.connect();
    }

    public static void closeResources(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeResources(PreparedStatement stmt, Connection conn) {
        closeResources(null, stmt, conn);
    }
} 