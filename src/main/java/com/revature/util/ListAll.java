package com.revature.util;

import com.revature.annotations.Column;
import com.revature.annotations.PKey;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;

public class ListAll {
    /**
     * Printing out all the fields with annotations and the annotation details.
     * @param clazz - Generic class
     */
    public static void ListAllAnnotatedFields(Class<?> clazz){
        System.out.println("Printing public fields of: " + clazz.getSimpleName());
        Field[] fields = clazz.getFields();
        if(fields.length == 0){
            System.out.println("\tThere are no public fields in: " + clazz.getSimpleName());
        } else {
            for(Field field : fields){
                if(Arrays.toString(field.getDeclaredAnnotations()) != "[]") {
                    System.out.println("\tField name: " + field.getName());
                    System.out.println("\tField type: " + field.getType().getSimpleName());
                    System.out.println("\tIs primitive?: " + field.getType().isPrimitive());
                    System.out.println("\tModifiers: " + field.getModifiers());
                    Annotation[] annotations = field.getDeclaredAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof PKey) {
                            System.out.println("\t\tIsSerial: " + ((PKey) annotation).isSerial());
                            System.out.println("\t\tIsUnique: " + ((PKey) annotation).isUnique());
                            System.out.println("\t\tIsNotNull: " + ((PKey) annotation).isNotNull());
                        }
                        if (annotation instanceof Column) {
                            System.out.println("\t\tIsUnique: " + ((Column) annotation).isUnique());
                            System.out.println("\t\tIsNotNull: " + ((Column) annotation).isNotNull());
                        }
                    }
                    System.out.println();
                }
            }
        }
    }
}
