package com.revature.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class will create and manage a singleton of type Connection
 */
public class ConnectionCreator {
    private static final String url = "jdbc:postgresql://project0.cjt4bv63du5k.us-east-2.rds.amazonaws.com:5432/postgres?currentSchema=\"Project1\"";
    private static final String username = "postgres";
    private static final String password = "Jennica1";

    private static Connection instance;

    private ConnectionCreator(){}

    public static Connection getInstance() throws SQLException {
        if(instance == null || instance.isClosed()){
            try {
                Class.forName("org.postgresql.Driver");

                instance = DriverManager.getConnection(url, username, password);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}
