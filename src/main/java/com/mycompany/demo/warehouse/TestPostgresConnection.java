package com.mycompany.demo.warehouse;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class TestPostgresConnection {

    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/postgres";
        String username = "postgres";
        String password = "postgres";
        String dbName = "sensordb";

        log.info("Connecting to PostgreSQL at {} ...", url);

        try (Connection conn = DriverManager.getConnection(url, username, password)) {

            if (conn != null) {
                log.info("✅ SUCCESS: Connected to PostgreSQL server!");
            } else {
                log.error("❌ ERROR: Connection returned null");
                return;
            }

            String checkQuery =
                    "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(checkQuery)) {

                if (rs.next()) {
                    log.info("ℹ️ Database '{}' already exists.", dbName);
                } else {
                    String createQuery = "CREATE DATABASE " + dbName;
                    stmt.executeUpdate(createQuery);
                    log.info("🎉 Database '{}' created successfully!", dbName);
                }

            }

        } catch (SQLException e) {
            log.error("❌ Failed to connect or execute SQL: {}", e.getMessage(), e);
        }
    }
}
