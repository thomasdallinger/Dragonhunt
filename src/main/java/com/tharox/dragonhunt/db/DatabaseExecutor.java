package com.tharox.dragonhunt.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseExecutor {

    public static void executeDatabaseHandler(DatabaseHandler databaseHandler) {

        try (Connection con = DriverManager.getConnection(DbConfig.jdbc_url, DbConfig.user, DbConfig.password)) {
            databaseHandler.execute(con);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}