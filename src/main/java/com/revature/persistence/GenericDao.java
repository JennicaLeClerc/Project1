package com.revature.persistence;

import com.revature.util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GenericDao {

    // Create Table for Class T
    public static void createTable(Class<?> clazz){
        // Create Table Method
        StringBuilder createTable = SQLStringCreator.CreateTableString(clazz);
        try(Connection connection = ConnectionCreator.getInstance()) {
            assert connection != null;
            PreparedStatement stmt = connection.prepareStatement(String.valueOf(createTable));
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Create Row for Class T
    public static void createRow(Class<?> clazz){
        // Create Table Method
        createTable(clazz);
        // Create Row Method
        StringBuilder createRow = SQLStringCreator.CreateRowString(clazz);
    }

    // Read in GenericGetDao

    // Update
    public static void update(Class<?> clazz){

    }

    // Delete
    public static void deleteByID(Class<?> clazz){

    }

}

