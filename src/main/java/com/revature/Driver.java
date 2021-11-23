package com.revature;

import com.revature.model.User;
import com.revature.util.*;
import com.revature.persistence.*;

import java.util.*;

// Main class
public class Driver {
    static GenericDao genericDao = new GenericDao();

    // Main driver method
    public static void main(String[] args) {
        User user = new User();
        user.setUsername("new1234");
        user.setPassword("Password");
        user.setPhone_number(1234251233);
        user.setAge(12);
        System.out.println(user);

        //ListAll.ListAllAnnotatedFields(User.class);
        //ListAll.ListAllAnnotatedMethods(User.class);

        SQLStringCreator.CreateTableString(User.class);
        SQLStringCreator.CreateRowString(User.class);
        SQLStringCreator.ReadString(User.class);
        SQLStringCreator.ReadByPKeyString(User.class);
        SQLStringCreator.UpdateString(User.class);
        SQLStringCreator.DeleteString(User.class);

        //genericDao.createRow(user);
        List<Integer> list = Arrays.asList(3);
        //genericDao.delete(user, list);
        List<User> AllUsers = genericDao.Read(User.class);
        User user1 = (User) genericDao.ReadByPKey(User.class,list);
        user1.setAge(15);
        genericDao.update(user1);
    }
}
