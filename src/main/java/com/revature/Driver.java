package com.revature;

import com.revature.model.User;
import com.revature.util.*;
import com.revature.persistence.*;

// Main class
public class Driver {

    // Main driver method
    public static void main(String[] args) {

        //ListAll.ListAllAnnotatedFields(User.class);
        //ListAll.ListAllAnnotatedMethods(User.class);

        //SQLStringCreator.CreateTableString(User.class);
        //SQLStringCreator.CreateRowString(User.class);
        //SQLStringCreator.ReadString(User.class);
        //SQLStringCreator.UpdateString(User.class);
        //SQLStringCreator.DeleteString(User.class);
        GenericDao.createTable(User.class);
    }

}
