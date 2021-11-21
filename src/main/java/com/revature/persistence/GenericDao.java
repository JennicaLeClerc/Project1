package com.revature.persistence;

import com.revature.annotations.Column;
import com.revature.annotations.PKey;
import com.revature.util.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GenericDao<T>{

    /**
     * Creates a table for a generic class in the database. The TABLE NAME is <class name lower case>_table with the
     * Primary Keys columns with annotation of PKey and the rest of the columns with the annotation of Column.
     * @param clazz - takes in any instance of a class.
     */
    public void createTable(T clazz){
        // Create Table Method
        String createTable = SQLStringCreator.CreateTableString(clazz.getClass());
        try(Connection connection = ConnectionCreator.getInstance()) {
            assert connection != null;
            PreparedStatement stmt = connection.prepareStatement(createTable);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Create Row for Class T
    public void createRow(T clazz){
        // Create Table, if doesn't exist
        createTable(clazz);

        // Creates SQL generic string
        // Just assuming PKeys are serializable
        String createRow = SQLStringCreator.CreateRowString(clazz.getClass());
        try(Connection connection = ConnectionCreator.getInstance()){
            assert connection != null;
            PreparedStatement stmt = connection.prepareStatement(createRow);

            // Get all fields for the class
            Field[] fields = clazz.getClass().getFields();

            // Get all PKey and Column Field Annotations names from the class
            List<Field> PKeyFieldList = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(PKey.class)).collect(Collectors.toList());
            List<Field> ColumnFieldList = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(Column.class)).collect(Collectors.toList());
            List<Field> AColumnFieldList = new ArrayList<>();
            AColumnFieldList.addAll(PKeyFieldList);
            AColumnFieldList.addAll(ColumnFieldList);

            int index = 1;
            for(Field f: AColumnFieldList) {
                String columnName = f.getName().toLowerCase();
                boolean isSerial = false;
                //System.out.println("\tField : " + columnName);

                // if PKey is serial we don't set that number...
                Annotation[] annotations = f.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof PKey) {
                        isSerial = ((PKey) annotation).isSerial();
                    }
                }
                System.out.println(isSerial);
                if(!isSerial){
                    stmt = InputValues(stmt, clazz, columnName, index);
                    index ++;
                }
            }
            System.out.println(stmt);

            stmt.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // Read... Get by PKey
    public T getByPKey(T clazz){
        String Read = SQLStringCreator.ReadString(clazz.getClass());
        T retrieving = null;
        try(Connection connection = ConnectionCreator.getInstance()){
            assert connection != null;
            PreparedStatement stmt = connection.prepareStatement(Read);
            ResultSet rs = stmt.executeQuery();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return retrieving;
    }

    // Update
    public void update(T clazz){
        String Update = SQLStringCreator.UpdateString(clazz.getClass());
        // Does Columns first
        // Does PKey second
    }

    // Delete
    public void delete(T clazz){
        String Delete = SQLStringCreator.DeleteString(clazz.getClass());
        // Does PKey
    }

    public PreparedStatement InputValues(PreparedStatement stmt, T clazz, String name, int index){
        Field field = null;
        try {
            field = clazz.getClass().getDeclaredField(name);
            field.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String fieldType = field.getType().getSimpleName();

        switch(fieldType) {
            case "int":
                try {
                    stmt.setInt(index, (int) field.get(clazz));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "double":
                try {
                    stmt.setDouble(index, (double) field.get(clazz));
                }catch(Exception e){
                    System.out.println(e);
                }
                break;
            case "String":
                try {
                    stmt.setString(index, (String) field.get(clazz));
                }catch(Exception e){
                    System.out.println(e);
                }
                break;
            case "boolean":
                try {
                    stmt.setBoolean(index, (boolean) field.get(clazz));
                }catch(Exception e){
                    System.out.println(e);
                }
                break;
        }
        return stmt;
    }

}

