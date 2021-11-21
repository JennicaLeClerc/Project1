package com.revature.util;

import com.revature.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Creates the CRUD operation SQL Strings for a generic class.
 */
public class SQLStringCreator {
    /**
     * Creates an SQL string for a generic class (clazz) to create a table for the given class. The class's name
     * (lower case) is used for the table name. Appends the correct SQL formatting for all fields with the PKey
     * and Column Annotations.
     * @param clazz - takes in any class.
     * @return - SQL statement to create a table for the given class if one does not exist.
     */
    public static StringBuilder CreateTableString(Class<?> clazz){
        String table_name = clazz.getSimpleName().toLowerCase() + "_table";
        StringBuilder createCommand = new StringBuilder("create table if not exists " + table_name + "(\n");
        //System.out.println("Table Name: " + table_name + "\n");

        Field[] fields = clazz.getFields();

        // Creates Array of Fields that have PKey and Column annotations
        List<Field> PKeyFieldList = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(PKey.class)).collect(Collectors.toList());
        List<Field> ColumnFieldList = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(Column.class)).collect(Collectors.toList());

        // Appends all Primary Keys and the rest of the Columns to command
        createCommand.append(PKeyCommand(PKeyFieldList));
        createCommand.append(ColumnCommand(ColumnFieldList, PKeyFieldList.size()));
        createCommand.append(");\n");
        System.out.println(createCommand);
        return createCommand;
    }

    /**
     * Creates an SQL string for a generic class (clazz) to add a row to an existing table. The fields used for columns
     * must be Annotations of type PKey or Column.
     * @param clazz - takes in any class.
     * @return - SQL statement to add a row to a table for a given class.
     */
    public static StringBuilder AddRowString(Class<?> clazz){
        String table_name = clazz.getSimpleName().toLowerCase() + "_table";
        StringBuilder createCommand = new StringBuilder("insert into " + table_name + "(");
        //System.out.println("Table Name: " + table_name + "\n");

        // Get all fields for the class
        Field[] fields = clazz.getFields();

        // Get all PKey and Column Field Annotations names from the class
        List<Field> PKeyFieldList = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(PKey.class)).collect(Collectors.toList());
        List<Field> ColumnFieldList = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(Column.class)).collect(Collectors.toList());
        List<Field> AllColumnsFieldList = new ArrayList<>();
        AllColumnsFieldList.addAll(PKeyFieldList);
        AllColumnsFieldList.addAll(ColumnFieldList);

        // Appends field names and amount in values(?,?,....) in correct format
        createCommand.append(AddRowCommand(AllColumnsFieldList));

        System.out.println(createCommand);
        return createCommand;
    }

    /**
     * Creates an SQL string for a generic class (clazz) to read all rows for a generic column with a specific value.
     * @param clazz - takes in any class.
     * @return - SQL statement to write out all rows for a generic column for a given class.
     */
    public static StringBuilder ReadString(Class<?> clazz){
        String table_name = clazz.getSimpleName().toLowerCase() + "_table";
        StringBuilder readCommand = new StringBuilder("select * from " + table_name + " where ?=?;");
        System.out.println(readCommand);
        return readCommand;
    }

    /**
     * Takes the Fields with PKey annotation and creates an SQL string for each Field. Puts the PKey name, data
     * type, and constraints in the correct SQL format for each Field. Returns the full SQL string for all fields in
     * PKey_FieldList.
     * @param PKeyFieldList - List of the Fields with the PKey Annotation.
     * @return - SQL string for all PKey Annotation Fields for a table to be created.
     */
    public static StringBuilder PKeyCommand(List<Field> PKeyFieldList){
        StringBuilder PKeyCreateCommand = new StringBuilder();
        int counter = 0;
        for(Field f: PKeyFieldList) {
            String PKeyName = f.getName().toLowerCase();
            System.out.println("\tPKey name: " + PKeyName);

            PKeyCreateCommand.append( counter == 0 ? "\t" : "\t," );
            PKeyCreateCommand.append(PKeyName);

            String dataType = DataType(f);
            Annotation[] PKeyAnnotation = f.getDeclaredAnnotations();
            for (Annotation annotation : PKeyAnnotation) {
                PKey primarykey = (PKey) annotation;
                PKeyCreateCommand.append( primarykey.isSerial() ? " serial" : dataType );
                PKeyCreateCommand.append( primarykey.isUnique() ? " unique" : "");
                PKeyCreateCommand.append( primarykey.isNotNull() ? " not null" : "");
                PKeyCreateCommand.append("\n");
                counter++;
            }
        }
        return PKeyCreateCommand;
    }

    /**
     * Takes the Fields with Column annotation and creates an SQL string for each Field. Puts the Column name, data
     * type, and constraints in the correct SQL format for each field. Returns the full SQL string for all fields in
     * Column_FieldList.
     * @param ColumnFieldList - List of the Fields with the Column Annotation.
     * @param number_PK - The number of Primary Keys.
     * @return - SQL string for all Column Annotation Fields for a table to be created.
     */
    public static StringBuilder ColumnCommand(List<Field> ColumnFieldList, int number_PK){
        StringBuilder ColumnCreateCommand = new StringBuilder();
        for(Field f: ColumnFieldList) {
            String ColumnName = f.getName().toLowerCase();
            System.out.println("\tColumn name: " + ColumnName);

            ColumnCreateCommand.append( number_PK > 0 ? "\t," : "\t");
            ColumnCreateCommand.append(ColumnName);

            String dataType = DataType(f);
            Annotation[] ColumnAnnotation = f.getDeclaredAnnotations();
            for (Annotation annotation : ColumnAnnotation) {
                Column column = (Column) annotation;
                ColumnCreateCommand.append(dataType);
                ColumnCreateCommand.append( column.isUnique() ? " unique" : "" );
                ColumnCreateCommand.append( column.isNotNull() ? " not null" : "");
                ColumnCreateCommand.append("\n");
            }
        }
        return ColumnCreateCommand;
    }

    /**
     * Takes the Fields with PKey and Column Annotation and creates an SQL string for each Field. Puts the Column name,
     * and number of question marks in the correct format for all fields in AllColumnsFieldList.
     * @param AllColumnsFieldList - List of all Fields with the Column and PKey Annotation.
     * @return - SQL sting for all Columns (PKey and Column Annotations) for a row to be created.
     */
    public static StringBuilder AddRowCommand(List<Field> AllColumnsFieldList){
        StringBuilder createCommand = new StringBuilder();
        int totalColumns = AllColumnsFieldList.size();

        for(Field f: AllColumnsFieldList) {
            String ColumnName = f.getName().toLowerCase();
            //System.out.println("\tField : " + ColumnName);
            createCommand.append(ColumnName);
            createCommand.append( totalColumns > 1 ? ", " : ") values(?" );
            totalColumns--;
        }

        totalColumns = AllColumnsFieldList.size();
        for(int i = 1; i < totalColumns; i++){
            createCommand.append(",?");
        }
        createCommand.append(");");
        return createCommand;
    }

    /**
     * Takes a field and returns a string of what data type the Field is. This is only used for the Column Annotation.
     * @param field - takes in a field.
     * @return - SQL string of what data type the field is.
     */
    public static String DataType(Field field){
        String fieldType = field.getType().getSimpleName();
        System.out.println("\t\tData Type: " + fieldType);
        switch(fieldType){
            case "int": return " int";
            case "double": return " numeric";
            case "String": return " varchar(30)";
            case "boolean": return  " boolean";
            default: return "";
        }
    }
}
