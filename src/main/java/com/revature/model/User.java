package com.revature.model;
import com.revature.annotations.*;

public class User {
    @PKey
    public int user_id; // Primary Key
    @Column(isUnique = true)
    public String username;
    @Column
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
    public boolean is_alive = true;

    public User() {
    }

    public User(int user_id, String username, String password, String first_name, String last_name) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public User setUser_id(int user_id) {
        this.user_id = user_id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getFirst_name() {
        return first_name;
    }

    public User setFirst_name(String first_name) {
        this.first_name = first_name;
        return this;
    }

    public String getLast_name() {
        return last_name;
    }

    public User setLast_name(String last_name) {
        this.last_name = last_name;
        return this;
    }

    public int getAge() {
        return age;
    }

    public User setAge(int age) {
        this.age = age;
        return this;
    }

    public double getPhone_number() {
        return phone_number;
    }

    public User setPhone_number(double phone_number) {
        this.phone_number = phone_number;
        return this;
    }

    public boolean isIs_alive() {
        return is_alive;
    }

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
                "}\n";
    }
}
