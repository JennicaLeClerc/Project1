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
    public static String CreateTableString(Class<?> clazz){
        String table_name = clazz.getSimpleName().toLowerCase() + "_table";
        StringBuilder createCommand = new StringBuilder("create table if not exists " + table_name + "(\n");
        //System.out.println("Table Name: " + table_name + "\n");

        Field[] fields = clazz.getFields();

        // Creates Array of Fields that have PKey and Column annotations
        List<Field> PKeyFieldList = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(PKey.class)).collect(Collectors.toList());
        List<Field> ColumnFieldList = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(Column.class)).collect(Collectors.toList());

        // Appends all Primary Keys and the rest of the Columns to command
        createCommand.append(PKeyCreateCommand(PKeyFieldList));
        createCommand.append(ColumnCreateCommand(ColumnFieldList, PKeyFieldList.size()));
        createCommand.append(");\n");
        System.out.println(createCommand);
        return String.valueOf(createCommand);
    }

    /**
     * Creates an SQL string for a generic class (clazz) to add a row to an existing table. The fields used for columns
     * must be Annotations of type PKey or Column.
     * @param clazz - takes in any class.
     * @return - SQL statement to add a row to a table for a given class.
     */
    public static String CreateRowString(Class<?> clazz){
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
        return String.valueOf(createCommand);
    }

    /**
     * Creates an SQL string for a generic class (clazz) to get the row with the specific PKeys.
     * @param clazz - takes in any class.
     * @return - SQL statement to write out all rows for a generic column for a given class.
     */
    public static String ReadString(Class<?> clazz){
        String table_name = clazz.getSimpleName().toLowerCase() + "_table";
        StringBuilder readCommand = new StringBuilder("select * from " + table_name + " ");

        // Get all fields for the class
        Field[] fields = clazz.getFields();

        // Get all PKey and Column Field Annotations names from the class
        List<Field> PKeyFieldList = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(PKey.class)).collect(Collectors.toList());

        readCommand.append(PKeyWhereCommand(PKeyFieldList));
        System.out.println(readCommand);
        return String.valueOf(readCommand);
    }

    /**
     * Creates an SQL string for a generic class (clazz) to update all rows with the specified PKeys to those of the
     * rest of the columns.
     * @param clazz - takes in any class
     * @return - SQL statement to update rows for the class's PKeys to the values in the Columns.
     */
    public static String UpdateString(Class<?> clazz){
        String table_name = clazz.getSimpleName().toLowerCase() + "_table";
        StringBuilder updateCommand = new StringBuilder("update " + table_name + " set ");

        Field[] fields = clazz.getFields();

        // Creates Array of Fields that have PKey and Column annotations
        List<Field> PKeyFieldList = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(PKey.class)).collect(Collectors.toList());
        List<Field> ColumnFieldList = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(Column.class)).collect(Collectors.toList());

        updateCommand.append(ColumnUpdateCommand(ColumnFieldList));
        updateCommand.append(PKeyWhereCommand(PKeyFieldList));

        System.out.println(updateCommand);
        return String.valueOf(updateCommand);
    }

    /**
     * Creates an SQL string for a generic class (clazz) to delete all rows with the specified PKeys.
     * @param clazz - takes in any class
     * @return - SQL statement to delete rows for the class's PKeys
     */
    public static String DeleteString(Class<?> clazz){
        // DELETE FROM table_name WHERE condition;
        String table_name = clazz.getSimpleName().toLowerCase() + "_table";
        StringBuilder deleteCommand = new StringBuilder("delete from " + table_name + " ");

        Field[] fields = clazz.getFields();

        // Creates Array of Fields that have PKey and Column annotations
        List<Field> PKeyFieldList = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(PKey.class)).collect(Collectors.toList());
        deleteCommand.append(PKeyWhereCommand(PKeyFieldList));

        System.out.println(deleteCommand);
        return String.valueOf(deleteCommand);
    }

    /**
     * Takes the Fields with PKey annotation and creates an SQL string for each Field. Puts the PKey name, data
     * type, and constraints in the correct SQL format for each Field. Returns the full SQL string for all fields in
     * PKey_FieldList.
     * @param PKeyFieldList - List of the Fields with the PKey Annotation.
     * @return - SQL string for all PKey Annotation Fields for a table to be created.
     */
    public static StringBuilder PKeyCreateCommand(List<Field> PKeyFieldList){
        StringBuilder createCommand = new StringBuilder();
        int counter = 0;
        for(Field f: PKeyFieldList) {
            String PKeyName = f.getName().toLowerCase();
            //System.out.println("\tPKey name: " + PKeyName);

            createCommand.append( counter == 0 ? "\t" : "\t," );
            createCommand.append(PKeyName);

            String dataType = DataType(f);
            Annotation[] PKeyAnnotation = f.getDeclaredAnnotations();
            for (Annotation annotation : PKeyAnnotation) {
                PKey primarykey = (PKey) annotation;
                createCommand.append( primarykey.isSerial() ? " serial" : dataType );
                createCommand.append( primarykey.isUnique() ? " unique" : "");
                createCommand.append( primarykey.isNotNull() ? " not null" : "");
                createCommand.append("\n");
                counter++;
            }
        }
        return createCommand;
    }

    /**
     * Takes the Fields with Column annotation and creates an SQL string for each Field. Puts the Column name, data
     * type, and constraints in the correct SQL format for each field. Returns the full SQL string for all fields in
     * Column_FieldList.
     * @param ColumnFieldList - List of the Fields with the Column Annotation.
     * @param number_PK - The number of Primary Keys.
     * @return - SQL string for all Column Annotation Fields for a table to be created.
     */
    public static StringBuilder ColumnCreateCommand(List<Field> ColumnFieldList, int number_PK){
        StringBuilder createCommand = new StringBuilder();
        for(Field f: ColumnFieldList) {
            String ColumnName = f.getName().toLowerCase();
            //System.out.println("\tColumn name: " + ColumnName);

            createCommand.append( number_PK > 0 ? "\t," : "\t");
            createCommand.append(ColumnName);

            String dataType = DataType(f);
            Annotation[] ColumnAnnotation = f.getDeclaredAnnotations();
            for (Annotation annotation : ColumnAnnotation) {
                Column column = (Column) annotation;
                createCommand.append(dataType);
                createCommand.append( column.isUnique() ? " unique" : "" );
                createCommand.append( column.isNotNull() ? " not null" : "");
                createCommand.append("\n");
            }
        }
        return createCommand;
    }

    /**
     * Takes the Fields with PKey and Column Annotation and creates an SQL string for each Field. Puts the Column name,
     * and number of question marks in the correct format for all fields in AllColumnsFieldList. Does not include PKeys
     * that are serial since SQL takes care of those.
     * @param AllColumnsFieldList - List of all Fields with the Column and PKey Annotation.
     * @return - SQL sting for all Columns (PKey and Column Annotations) for a row to be created.
     */
    public static StringBuilder AddRowCommand(List<Field> AllColumnsFieldList){
        StringBuilder createCommand = new StringBuilder();
        int totalColumns = AllColumnsFieldList.size();
        int totalColumnsQMarks = AllColumnsFieldList.size();

        for(Field f: AllColumnsFieldList) {
            String ColumnName = f.getName().toLowerCase();
            boolean isSerial = false;
            //System.out.println("\tField : " + ColumnName);

            // if PKey is serial we don't set that number...
            Annotation[] annotations = f.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof PKey) {
                    isSerial = ((PKey) annotation).isSerial();
                    // if it is serial, we don't set that number than we need one less ?
                }
            }
            if(isSerial){
                totalColumnsQMarks--;
            }else {
                createCommand.append(ColumnName);
                createCommand.append(totalColumns > 1 ? ", " : ") values(?");
            }
            totalColumns--;
        }

        for(int i = 1; i < totalColumnsQMarks; i++){
            createCommand.append(",?");
        }
        createCommand.append(");");
        return createCommand;
    }

    /**
     * Takes the Fields with Column Annotation and creates an SQL string for each Field. Puts the column name in the
     * correct format so that their info is updated.
     * @param ColumnFieldList - List of all Fields with the Column Annotation
     * @return - SQL string for all Column Annotations for a row to be updated
     */
    public static StringBuilder ColumnUpdateCommand(List<Field> ColumnFieldList){
        StringBuilder updateCommand = new StringBuilder();
        int totalColumns = ColumnFieldList.size();
        for(Field f: ColumnFieldList){
            String columnName = f.getName().toLowerCase();
            updateCommand.append(columnName + "=?");
            updateCommand.append( totalColumns > 1 ? ", " : " " );
            totalColumns--;
        }
        return updateCommand;
    }

    /**
     * Takes the Fields with PKey Annotation and creates an SQL string for ech Field. Puts the column name into the
     * correct format so that their info is the condition for the update. This is also the correct format for Deletion
     * based on the PKeys.
     * @param PKeyFieldList - List of all Fields with the PKey Annotation
     * @return - SQL string for all PKey Annotations for a row to be updated
     */
    public static StringBuilder PKeyWhereCommand(List<Field> PKeyFieldList){
        StringBuilder updateCommand = new StringBuilder("where ");
        int totalColumns = PKeyFieldList.size();
        for(Field f: PKeyFieldList){
            String columnName = f.getName().toLowerCase();
            updateCommand.append(columnName + "=?");
            updateCommand.append( totalColumns > 1 ? "and " : ";" );
            totalColumns--;
        }
        return updateCommand;
    }

    /**
     * Takes a field and returns a string of what data type the Field is. This is only used for the Column Annotation.
     * @param field - takes in a field.
     * @return - SQL string of what data type the field is.
     */
    public static String DataType(Field field){
        String fieldType = field.getType().getSimpleName();
        //System.out.println("\t\tData Type: " + fieldType);
        switch(fieldType){
            case "int": return " int";
            case "double": return " numeric";
            case "String": return " varchar(30)";
            case "boolean": return  " boolean";
            default: return "";
        }
    }
}
