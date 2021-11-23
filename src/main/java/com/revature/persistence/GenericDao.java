package com.revature.persistence;

import com.revature.annotations.Column;
import com.revature.annotations.PKey;
import com.revature.util.*;

import javax.xml.transform.Result;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GenericDao<T>{

    /**
     * Creates a table for a generic class in the database. The TABLE NAME is <class name lower case>_table with the
     * Primary Keys columns with annotation of PKey and the rest of the columns with the annotation of Column.
     * @param clazz - takes in any Field of a class.
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

    /**
     * Creates a table for the generic class of the field if it doesn't exist. Then it creates a row with the given
     * values from the field in the database. All fields with annotations of PKey and Column are set using the info
     * from the field unless the PKey is serial.
     * @param clazz - takes in any Field of a class.
     */
    public void createRow(T clazz){
        createTable(clazz);

        String createRow = SQLStringCreator.CreateRowString(clazz.getClass());
        try(Connection connection = ConnectionCreator.getInstance()){
            assert connection != null;
            PreparedStatement stmt = connection.prepareStatement(createRow);
            stmt = RowPrepStatement(clazz, stmt);
            stmt.executeUpdate();
            System.out.println(stmt);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Read
    public List<T> Read(T clazz){
        List<T> output = new ArrayList<>();

        // Creates SQL generic string
        String Read = SQLStringCreator.ReadString(clazz.getClass());
        try(Connection connection = ConnectionCreator.getInstance()){
            assert connection != null;
            PreparedStatement stmt = connection.prepareStatement(Read);
            System.out.println(stmt);

            ResultSet rs = stmt.executeQuery();
            int i = 1;
            while(rs.next()){
                System.out.println(rs.getObject(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return output;
    }

    // Read... Get by PKeys
    // PKeys ids are always ints.
    public Field ReadByPKey(T clazz, List<Integer> ids){
        String Read = SQLStringCreator.ReadByPKeyString(clazz.getClass());
        Field retrieving = null;
        try(Connection connection = ConnectionCreator.getInstance()){
            assert connection != null;
            PreparedStatement stmt = connection.prepareStatement(Read);
            for(int i = 1; i <= ids.size(); i++){
                stmt.setInt(i, ids.get(i-1));
            }
            System.out.println(stmt);
            ResultSet rs = stmt.executeQuery();
            System.out.println(rs.toString());
            if(rs.next()){

                retrieving = SetValues(rs, clazz);
                //T t = null;
                //List<Field> AllColumnsFieldList = CreateFieldLists.AllColumnsFieldList(clazz.getClass());

                /*person.setPersonID(rs.getInt(1));
                person.setFirstName(rs.getString(2));
                person.setLastName(rs.getString(3));*/
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println(retrieving);
        return retrieving;
    }

    // Update
    public void update(T clazz){
        String Update = SQLStringCreator.UpdateString(clazz.getClass());
        // Does Columns first
        // Does PKey second
    }

    /**
     * Deletes all rows with PKey ids in the form of a List of Integers for the generic class of the input field.
     * @param clazz - takes in any Field of a class.
     * @param ids - List of ids (of all PKeys in class)
     */
    public void delete(T clazz, List<Integer> ids){
        String Delete = SQLStringCreator.DeleteString(clazz.getClass());
        // Does PKey
        try(Connection connection = ConnectionCreator.getInstance()){
            assert connection != null;
            PreparedStatement stmt = connection.prepareStatement(Delete);
            for(int i = 0; i < ids.size(); i++){
                stmt.setInt(i+1, ids.get(i));
            }
            stmt.executeUpdate();
            System.out.println(stmt);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public PreparedStatement RowPrepStatement(T clazz, PreparedStatement stmt){
        List<Field> AColumnFieldList = CreateFieldLists.AllColumnsFieldList(clazz.getClass());

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
            if(!isSerial){
                stmt = InputValues(stmt, clazz, columnName, index);
                index ++;
            }
        }
        return stmt;

    }

    /**
     * Inputs the info from the given field of Column of name "name" of any type into the given
     * Prepared Statement (stmt) at the index of the question mark in the current stmt.
     * @param stmt - Prepared Statement to create a row
     * @param clazz - Field of generic class
     * @param name - Column name
     * @param index - Index of Prepared Statement ?
     * @return
     */
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

    // works just I'm an idiot and was trying to look at an id that didn't exist any more.
    public Field SetValues(ResultSet resultSet, T clazz) {
        int rs_number = 0;
        ResultSetMetaData rs_info = null;
        try {
            rs_info = resultSet.getMetaData();
            rs_number = rs_info.getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Field field = null;
        for(int i = 1; i <= rs_number; i++) {
            try {
                System.out.println(resultSet.getObject(i));
                field = clazz.getClass().getDeclaredField(rs_info.getColumnName(i));
                field.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String fieldType = field.getType().getSimpleName();

            switch (fieldType) {
                case "int":
                    try {
                        field.set(clazz, resultSet.getInt(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "double":
                    try {
                        field.set(clazz, resultSet.getDouble(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "String":
                    try {
                        field.set(clazz, resultSet.getString(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "boolean":
                    try {
                        field.set(clazz, resultSet.getBoolean(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        return field;
    }

}

