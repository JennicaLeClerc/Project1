package com.revature.persistence;

import com.revature.annotations.PKey;
import com.revature.util.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class GenericDao<T>{

    /**
     * Creates a table for a generic class in the database. The TABLE NAME is <class name lower case>_table with the
     * Primary Keys columns with annotation of PKey and the rest of the columns with the annotation of Column.
     * @param clazz - Any generic class.
     */
    public void createTable(Class<?> clazz){
        String createTable = SQLStringCreator.CreateTableString(clazz);
        try(Connection connection = ConnectionCreator.getInstance()) {
            assert connection != null;
            PreparedStatement stmt = connection.prepareStatement(createTable);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(createTable);
    }

    /**
     * Creates a table for the generic class of the field if it doesn't exist. Then it creates a row with the given
     * values from the field in the database. All fields with annotations of PKey and Column are set using the info
     * from the field unless the PKey is serial.
     * @param t - Any instance of a generic class.
     */
    public void createRow(T t){
        Class<?> tClass = t.getClass();
        createTable(tClass);

        String createRow = SQLStringCreator.CreateRowString(tClass);
        try(Connection connection = ConnectionCreator.getInstance()){
            assert connection != null;
            PreparedStatement stmt = connection.prepareStatement(createRow);
            stmt = RowPrepStatement(t, stmt);
            stmt.executeUpdate();
            System.out.println(stmt);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Read
     * @param clazz - Any generic class.
     * @return - List of all instances of a generic class in the database.
     */
    public List<T> Read(Class<?> clazz){
        List<T> output = new ArrayList<>();

        String Read = SQLStringCreator.ReadString(clazz);
        try(Connection connection = ConnectionCreator.getInstance()){
            assert connection != null;
            PreparedStatement stmt = connection.prepareStatement(Read);
            System.out.println(stmt);

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                output.add(SetValues(rs, clazz));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(output);
        return output;
    }

    /**
     * @param clazz - Any generic class.
     * @param ids - List of ids of all PKeys in class.
     * @return - An instance of a generic class in the database with the specified PKeys of value ids.
     */
    public T ReadByPKey(Class<?> clazz, List<Integer> ids){
        String Read = SQLStringCreator.ReadByPKeyString(clazz);
        T retrieving = null;
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
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println(retrieving);
        return retrieving;
    }

    // Update
    public void update(T t){
        //String sql = "update users set username=?, password=? where account_no=?";
        String Update = SQLStringCreator.UpdateString(t.getClass());
        // Does the same thing as Create Row for input info for all before where
        try(Connection connection = ConnectionCreator.getInstance()){
            assert connection != null;
            PreparedStatement stmt = connection.prepareStatement(Update);
            stmt = RowPrepStatement(t, stmt);
            stmt.executeUpdate();
            System.out.println(stmt);
        }catch (Exception e){
            e.printStackTrace();
        }

        // Does the PKeys next (after where)
    }

    /**
     * Deletes all rows with PKey ids in the form of a List of Integers for the generic class of the input field.
     * @param clazz - Any generic class.
     * @param ids - List of ids of all PKeys in the generic class.
     */
    public void delete(Class<?> clazz, List<Integer> ids){
        String Delete = SQLStringCreator.DeleteString(clazz);
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

    /**
     * Gives the completed SQL Prepared Statement to create a row for the instance "t" of a generic class. If the
     * PKey is serial we do not want to have those values inputted into the database so those are skipped.
     * @param t - Any instance of a generic class.
     * @param stmt - The prepared statement to create a row in for the generic class of t.
     * @return - The Prepared Statement with the values inserted into where question marks were before.
     */
    public PreparedStatement RowPrepStatement(T t, PreparedStatement stmt){
        List<Field> AllColumnsFieldList = CreateFieldLists.AllColumnsFieldList(t.getClass());

        int index = 1;
        for(Field f: AllColumnsFieldList) {
            String columnName = f.getName().toLowerCase();
            boolean isSerial = false;

            Annotation[] annotations = f.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof PKey) {
                    isSerial = ((PKey) annotation).isSerial();
                }
            }
            if(!isSerial){
                stmt = InputValues(stmt, t, columnName, index);
                index ++;
            }
        }
        return stmt;
    }

    public PreparedStatement UpdatePrepStatement(T t, PreparedStatement stmt){
        List<Field> ColumnFieldList = CreateFieldLists.ColumnFieldList(t.getClass());
        return stmt;
    }

    /**
     * @param stmt - Prepared Statement to create a row.
     * @param t - Any instance of a generic class.
     * @param name - Column name in the generic class.
     * @param index - Index of Prepared Statement Question Mark.
     * @return - returns the Prepared statement with the value of t for column of name "name" replacing the
     *           question mark at index "index".
     */
    public PreparedStatement InputValues(PreparedStatement stmt, T t, String name, int index){
        Field field = null;
        try {
            field = t.getClass().getDeclaredField(name);
            field.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert field != null;
        String fieldType = field.getType().getSimpleName();
        try {
            switch (fieldType) {
                case "int":
                    stmt.setInt(index, (int) field.get(t));
                    break;
                case "double":
                    stmt.setDouble(index, (double) field.get(t));
                    break;
                case "String":
                    stmt.setString(index, (String) field.get(t));
                    break;
                case "boolean":
                    stmt.setBoolean(index, (boolean) field.get(t));
                    break;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return stmt;
    }

    /**
     * @param resultSet - The current result set.
     * @param clazz - Any generic class.
     * @return - Returns an instance of a generic class with all information within retrieved from the result set and
     *           in the correct format.
     */
    public T SetValues(ResultSet resultSet, Class<?> clazz) {
        Constructor<?> constructor = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(x->x.getParameterCount() == 0).findFirst().orElse(null);
        assert constructor != null;
        constructor.setAccessible(true);
        T t = null;
        try {
            t = (T) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int rs_number = 0;
        ResultSetMetaData rs_info = null;
        try {
            rs_info = resultSet.getMetaData();
            rs_number = rs_info.getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for(int i = 1; i <= rs_number; i++) {
            Field field = null;
            try {
                field = clazz.getDeclaredField(rs_info.getColumnName(i));
                field.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            assert field != null;
            String fieldType = field.getType().getSimpleName();
            try {
                switch (fieldType) {
                    case "int":
                        field.set(t, resultSet.getInt(i));
                        break;
                    case "double":
                        field.set(t, resultSet.getDouble(i));
                        break;
                    case "String":
                        field.set(t, resultSet.getString(i));
                        break;
                    case "boolean":
                        field.set(t, resultSet.getBoolean(i));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return t;
    }

}

