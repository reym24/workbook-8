package com.pluralsight;

import java.sql.*;

public class Project {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/northwind?useSSL=false";
        String user = "root";
        String password = "new_password"; // your password here

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to database!");

            // Try different table name variations
            String[] tableNames = {"products", "product", "Products", "PRODUCTS"};

            for (String tableName : tableNames) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
                    System.out.println("Found table: " + tableName);
                    printProducts(conn, tableName);
                    return;
                } catch (SQLException e) {
                    // Try next table name
                }
            }

            System.err.println("Could not find products table. Available tables:");
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "%", null);
            while (tables.next()) {
                System.err.println("- " + tables.getString("TABLE_NAME"));
            }

        } catch (SQLException e) {
            System.err.println("Database error:");
            e.printStackTrace();
        }
    }

    private static void printProducts(Connection conn, String tableName) throws SQLException {
        String sql = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("%-5s %-30s %-10s %-5s%n",
                    "ID", "Name", "Price", "Stock");
            System.out.println("--------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-5d %-30s %-10.2f %-5d%n",
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getDouble("UnitPrice"),
                        rs.getInt("UnitsInStock"));
            }
        }
    }
}