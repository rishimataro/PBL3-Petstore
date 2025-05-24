package com.store.app.petstore.DAO;

import com.store.app.petstore.Models.Entities.Product;

import java.sql.*;
import java.util.ArrayList;

public class ProductDAO {
    public static ProductDAO getInstance() {
        return new ProductDAO();
    }

    public static int insert(Product entity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO Products (name, category, stock, price, description, image_url) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getCategory());
            stmt.setInt(3, entity.getStock());
            stmt.setInt(4, entity.getPrice());
            stmt.setString(5, entity.getDescription());
            stmt.setString(6, entity.getImageUrl());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return 0;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    public static int update(Product entity) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE Products SET name = ?, category = ?, stock = ?, price = ?, description = ?, image_url = ? WHERE product_id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getCategory());
            stmt.setInt(3, entity.getStock());
            stmt.setInt(4, entity.getPrice());
            stmt.setString(5, entity.getDescription());
            stmt.setString(6, entity.getImageUrl());
            stmt.setInt(7, entity.getProductId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    public static int delete(Product entity) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "DELETE FROM Products WHERE product_id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, entity.getProductId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(stmt, conn);
        }
    }

    public static ArrayList<Product> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Product> products = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Products";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setCategory(rs.getString("category"));
                product.setStock(rs.getInt("stock"));
                product.setPrice(rs.getInt("price"));
                product.setDescription(rs.getString("description"));
                product.setImageUrl(rs.getString("image_url"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return products;
    }

    public static Product findById(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Products WHERE product_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setCategory(rs.getString("category"));
                product.setStock(rs.getInt("stock"));
                product.setPrice(rs.getInt("price"));
                product.setDescription(rs.getString("description"));
                product.setImageUrl(rs.getString("image_url"));
                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return null;
    }

    public static ArrayList<Product> findByCondition(String condition) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Product> products = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM Products WHERE " + condition;
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setCategory(rs.getString("category"));
                product.setStock(rs.getInt("stock"));
                product.setPrice(rs.getInt("price"));
                product.setDescription(rs.getString("description"));
                product.setImageUrl(rs.getString("image_url"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
        return products;
    }

    public static ArrayList<Product> findByCategory(String category) {
        return findByCondition("category = '" + category + "'");
    }

    public static ArrayList<Product> findByPriceRange(int minPrice, int maxPrice) {
        return findByCondition("price BETWEEN " + minPrice + " AND " + maxPrice);
    }

    // tim kiem theo ten, danh muc
    public static ArrayList<Product> searchProducts(String keyword) {
        String searchPattern = "'%" + keyword + "%'";
        return findByCondition("name LIKE " + searchPattern + " OR category LIKE " + searchPattern);
    }

    // so luong san pham
    public static int getProductCount() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT COUNT(*) as count FROM Products";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    // tong gia tri ton kho
    public static long getTotalStockValue() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT SUM(stock * price) as total_value FROM Products";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("total_value");
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }
}
