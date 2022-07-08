package com.tharox.dragonhunt.db;

import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class DbConfig {
    public static String jdbc_url;
    public static String user;
    public static String password;

    static {
        String fileName = "db.txt";

        ClassLoader classLoader = DbConfig.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(fileName);
             InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            jdbc_url = reader.readLine();
            user = reader.readLine();
            password = reader.readLine();
            Bukkit.getLogger().severe(DbConfig::getString);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getString() {
        return "DbConfig{" +
                "jdbc_url='" + jdbc_url + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
