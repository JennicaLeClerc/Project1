package com.revature;

import com.revature.model.User;
import com.revature.util.*;

// Main class
public class Driver {

    // Main driver method
    public static void main(String[] args) {

        //ListAll.Testing(User.class);
        //ListAll.ListAllAnnotatedMethods(User.class);

        //SQLStringCreator.CreateTableString(User.class);
        SQLStringCreator.AddRowString(User.class);
    }

}
