package com.revature.persistence;

public class GenericGetDao<T> {
    // Read... Get by ID
    public Class<?> getBy(Class<?> clazz, T t){
        return clazz;
    }
}
