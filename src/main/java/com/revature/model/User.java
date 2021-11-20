package com.revature.model;
import com.revature.annotations.*;

public class User {
    @PKey
    public int user_id; // Primary Key

    @Column(isUnique = true)
    public String username;
    @Column(isUnique = true)
    public String password;
    @Column
    public String first_name;
    @Column
    public String last_name;
    @Column
    public int age;
    @Column
    public double phone_number;
    @Column
    public boolean is_alive;

    public User() {
    }

    public User(int user_id, String username, String password, String first_name, String last_name) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    @Getter(columnName = "user_id")
    public int getUser_id() {
        return user_id;
    }

    @Setter(columnName = "user_id")
    public User setUser_id(int user_id) {
        this.user_id = user_id;
        return this;
    }

    @Getter(columnName = "username")
    public String getUsername() {
        return username;
    }

    @Setter(columnName = "username")
    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    @Getter(columnName = "password")
    public String getPassword() {
        return password;
    }

    @Setter(columnName = "password")
    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    @Getter(columnName = "first_name")
    public String getFirst_name() {
        return first_name;
    }

    @Setter(columnName = "first_name")
    public User setFirst_name(String first_name) {
        this.first_name = first_name;
        return this;
    }

    @Getter(columnName = "last_name")
    public String getLast_name() {
        return last_name;
    }

    @Setter(columnName = "last_name")
    public User setLast_name(String last_name) {
        this.last_name = last_name;
        return this;
    }

    @Getter(columnName = "age")
    public int getAge() {
        return age;
    }

    @Setter(columnName = "age")
    public User setAge(int age) {
        this.age = age;
        return this;
    }

    @Getter(columnName = "phone_number")
    public double getPhone_number() {
        return phone_number;
    }

    @Setter(columnName = "phone_number")
    public User setPhone_number(double phone_number) {
        this.phone_number = phone_number;
        return this;
    }

    @Getter(columnName = "is_alive")
    public boolean isIs_alive() {
        return is_alive;
    }

    @Setter(columnName = "is_alive")
    public User setIs_alive(boolean is_alive) {
        this.is_alive = is_alive;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", age=" + age +
                ", phone_number=" + phone_number +
                ", isAlive=" + is_alive +
                '}';
    }
}
