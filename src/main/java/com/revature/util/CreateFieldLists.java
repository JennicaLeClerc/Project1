package com.revature.util;

import com.revature.annotations.Column;
import com.revature.annotations.PKey;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class CreateFieldLists {
    public static List<Field> AllColumnsFieldList(Class<?> clazz){
        // Get all PKey and Column Field Annotations names from the class
        List<Field> AllColumnsFieldList = new ArrayList<>();
        AllColumnsFieldList.addAll(PKeyFieldList(clazz));
        AllColumnsFieldList.addAll(ColumnFieldList(clazz));
        return AllColumnsFieldList;
    }

    public static List<Field> PKeyFieldList(Class<?> clazz){
        // Get all fields for the class
        Field[] fields = clazz.getFields();
        List<Field> PKeyFieldList = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(PKey.class)).collect(Collectors.toList());
        return PKeyFieldList;
    }

    public static List<Field> ColumnFieldList(Class<?> clazz){
        // Get all fields for the class
        Field[] fields = clazz.getFields();
        List<Field> ColumnFieldList = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(Column.class)).collect(Collectors.toList());
        return ColumnFieldList;
    }
}
