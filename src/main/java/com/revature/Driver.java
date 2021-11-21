package com.revature;

import com.revature.model.User;
import com.revature.util.*;
import com.revature.persistence.*;

// Main class
public class Driver {

    // Main driver method
    public static void main(String[] args) {
        User user = new User();
        user.setUsername("12345");
        user.setPassword("Password");
        user.setPhone_number(1234251233);
        user.setAge(12);

        final GenericDao genericDao = new GenericDao();

        //genericDao.GenericGetter(user, "username");

        //ListAll.ListAllAnnotatedFields(User.class);
        //ListAll.ListAllAnnotatedMethods(User.class);

        //SQLStringCreator.CreateTableString(User.class);
        //SQLStringCreator.CreateRowString(User.class);
        //SQLStringCreator.ReadString(User.class);
        //SQLStringCreator.UpdateString(User.class);
        //SQLStringCreator.DeleteString(User.class);
        genericDao.createRow(user);
        //SQLStringCreator.CreateRowString(User.class);
    }

}
