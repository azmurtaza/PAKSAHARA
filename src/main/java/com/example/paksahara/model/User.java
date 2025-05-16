package com.example.paksahara.model;

public abstract class User implements Authenticable {
    private int userID;
    private String name;
    private String email;
    private String password;
    private boolean isLoggedIn;        // primitive boolean

    // new setter
    public void setLoggedIn(boolean loggedIn) {
        this.isLoggedIn = loggedIn;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public User(){

    }

    public User(int userID, String name, String email, String password){
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isLoggedIn = false;
    }

    public int getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean login(String email, String password) {
        if (this.email.equals(email) && this.password.equals(password)) {
            isLoggedIn = true;
            System.out.println("Login successful for: " + name);
            return true;
        } else {
            System.out.println("Login failed. Incorrect email or password.");
            return false;
        }
    }

    @Override
    public void logout() {
        if (isLoggedIn) {
            isLoggedIn = false;
            System.out.println(name + " has logged out.");
        } else {
            System.out.println(name + " is not logged in.");
        }
    }
}
