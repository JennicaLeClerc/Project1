package com.revature.util;

import com.revature.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class SQLStringCreator {
    /**
     * Creates an SQL string for a given class (clazz) to create a table for the given class. The class's name
     * (lower case) is used for the table name. Appends the correct SQL formatting for all fields with the PKey
     * and Column Annotations.
     * @param clazz - takes in any class
     */
    public static void CreateTableString(Class<?> clazz){
        System.out.println("Testing using Streams");
        String table_name = clazz.getSimpleName().toLowerCase() + "_table";
        StringBuilder createCommand = new StringBuilder("create table if not exists " + table_name + "(\n");
        System.out.println("\tTable Name: " + table_name + "\n");

        List<Field> PKeyFieldList;
        List<Field> ColumnFieldList;
        Field[] fields = clazz.getFields();

        // Creates Array of Fields that have PKey and Column annotations
        PKeyFieldList = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(PKey.class)).collect(Collectors.toList());
        ColumnFieldList = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(Column.class)).collect(Collectors.toList());

        // Appends all Primary Keys and the rest of the Columns to command
        createCommand.append(PKeyCommand(PKeyFieldList));
        createCommand.append(ColumnCommand(ColumnFieldList, PKeyFieldList.size()));
        createCommand.append(");\n");
        System.out.println(createCommand);
    }

    /**
     * Takes the Fields with PKey annotation and creates an SQL string for each Field. Puts the PKey name, data
     * type, and constraints in the correct SQL format for each Field. Returns the full SQL string for all fields in
     * PKey_FieldList.
     * @param PKeyFieldList - List of the Fields with the PKey Annotation
     * @return - the StringBuilder of the SQL command for all PKey Annotation Fields
     */
    public static StringBuilder PKeyCommand(List<Field> PKeyFieldList){
        StringBuilder PKeyCreateCommand = new StringBuilder("");
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
     * @param ColumnFieldList - List of the Fields with the Column Annotation
     * @param number_PK - The number of Primary Keys
     * @return - the StringBuilder of the SQL command for all Column Annotation Fields
     */
    public static StringBuilder ColumnCommand(List<Field> ColumnFieldList, int number_PK){
        StringBuilder ColumnCreateCommand = new StringBuilder("");
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
     * Takes a field and returns a string of what data type the string is. This is only used for the Column Annotation.
     * @param field - takes in a field
     * @return - a string of what data type the field is
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
