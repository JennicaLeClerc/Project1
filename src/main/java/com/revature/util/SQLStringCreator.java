package com.revature.util;

import com.revature.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class SQLStringCreator {
    /**
     * Printing out all the fields and all the fields annotations to understand how to get everything.
     * @param clazz - Generic class
     */
    public static void ListAllFields(Class<?> clazz){
        System.out.println("Printing public fields of: " + clazz.getSimpleName());
        Field[] fields = clazz.getFields();
        if(fields.length == 0){
            System.out.println("\tThere are no public fields in: " + clazz.getSimpleName());
        } else {
            for(Field field : fields){
                System.out.println("\tField name: " + field.getName());
                System.out.println("\tField type: " + field.getType().getSimpleName());
                System.out.println("\tIs primitive?: " + field.getType().isPrimitive());
                System.out.println("\tModifiers: " + field.getModifiers());
                Annotation[] annotations = field.getDeclaredAnnotations();
                for(Annotation annotation : annotations){
                    if(annotation instanceof PKey){
                        System.out.println("\t\tIsSerial: "+ ((PKey) annotation).isSerial());
                        System.out.println("\t\tIsUnique: "+ ((PKey) annotation).isUnique());
                        System.out.println("\t\tIsNotNull: "+ ((PKey) annotation).isNotNull());
                    }
                    if(annotation instanceof Column){
                        System.out.println("\t\tIsUnique: "+ ((Column) annotation).isUnique());
                        System.out.println("\t\tIsNotNull: "+ ((Column) annotation).isNotNull());
                    }
                }
                System.out.println();
            }
        }
    }

    public static void ListAllAnnotatedMethods(Class<?> clazz){
        System.out.println("Printing annotated methods of: " + clazz.getSimpleName());
        Method[] methods = clazz.getMethods();
        if(methods.length == 0){
            System.out.println("\tThere are no annotated methods in: " + clazz.getSimpleName());
        } else {
            for(Method method: methods){
                // Just getting those with annotations
                if(Arrays.toString(method.getDeclaredAnnotations()) != "[]") {
                    System.out.println("\tMethod name: " + method.getName());
                    System.out.println("\tReturn type: " + method.getReturnType());

                    Annotation[] annotations = method.getDeclaredAnnotations();
                    for(Annotation annotation : annotations){
                        if(annotation instanceof Getter){
                            System.out.println("\tColumn Name: " + ((Getter) annotation).columnName());
                            System.out.println("\tGetter Annotation");
                        }
                        if(annotation instanceof Setter){
                            System.out.println("\tColumn Name: " + ((Setter) annotation).columnName());
                            System.out.println("\tSetter Annotation");
                        }
                    }
                    System.out.println();
                }
            }
        }
    }

    /**
     * Creates an SQL string for a given class (clazz) to create a table for the given class. The class's name
     * (lower case) is used for the table name. Appends the correct SQL formatting for all fields with the PKey
     * and Column Annotations.
     * @param clazz - takes in any class
     */
    public static void StreamTest(Class<?> clazz){
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
            String PKeyName = f.getName();
            System.out.println("\tPKey name: " + f.getName());
            if(counter == 0){
                PKeyCreateCommand.append("\t");
            }else{
                PKeyCreateCommand.append("\t,");
            }

            PKeyCreateCommand.append(PKeyName);

            String dataType = DataType(f);
            Annotation[] PKeyAnnotation = f.getDeclaredAnnotations();
            for (Annotation annotation : PKeyAnnotation) {
                PKey primarykey = (PKey) annotation;
                boolean isSerial = primarykey.isSerial();
                boolean isUnique = primarykey.isUnique();
                boolean isNotNull = primarykey.isNotNull();
                System.out.println("\t\tIsSerial: " + isSerial);
                System.out.println("\t\tIsUnique: " + isUnique);
                System.out.println("\t\tIsNotNull: " + isNotNull + "\n");
                if(isSerial){
                    PKeyCreateCommand.append(" serial");
                }else{
                    PKeyCreateCommand.append(dataType);
                }
                PKeyCreateCommand.append(Unique(isUnique));
                PKeyCreateCommand.append(NotNull(isNotNull));
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
            System.out.println("\tColumn name: " + f.getName());

            if(number_PK > 0){
                ColumnCreateCommand.append("\t,");
            }else{
                ColumnCreateCommand.append("\t");
            }
            ColumnCreateCommand.append(ColumnName);

            String dataType = DataType(f);
            Annotation[] ColumnAnnotation = f.getDeclaredAnnotations();
            for (Annotation annotation : ColumnAnnotation) {
                Column column = (Column) annotation;
                boolean isUnique = column.isUnique();
                boolean isNotNull = column.isNotNull();
                System.out.println("\t\tIsUnique: " + isUnique);
                System.out.println("\t\tIsNotNull: " + isNotNull + "\n");
                ColumnCreateCommand.append(dataType);
                ColumnCreateCommand.append(Unique(isUnique));
                ColumnCreateCommand.append(NotNull(isNotNull));
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

    /**
     * Returns a string if column has to be unique
     * @param isUnique - does the column have to be unique?
     * @return - if unique returns " unique" if not ""
     */
    public static String Unique(boolean isUnique){
        if(isUnique){
            return " unique";
        }
        return "";
    }

    /**
     * Returns a string if column is unique
     * @param isNotNull - is the column allowed to be null or not?
     * @return - if not allowed to be null returns " not null" if not ""
     */
    public static String NotNull(boolean isNotNull){
        if(isNotNull){
            return " not null";
        }
        return "";
    }
}
