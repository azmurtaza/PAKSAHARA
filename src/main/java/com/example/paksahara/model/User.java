// src/main/java/com/example/paksahara/model/User.java
package com.example.paksahara.model;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String password;
    private String imageUrl;          // NEW

    // Full constructor (with image)
    public User(int id,
                String firstName,
                String lastName,
                String email,
                String role,
                String imageUrl) {
        this.id = id;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.email     = email;
        this.role      = role;
        this.imageUrl  = imageUrl;
    }

    // Legacy constructor (no image)
    public User(int id,
                String firstName,
                String lastName,
                String email,
                String role) {
        this(id, firstName, lastName, email, role, null);
    }

    public User(int id,
                String name,
                String email,
                String role) {
        this(id, name, email, role, null);
    }

    public User() { }

    // Getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }           // NEW
    public void setFirstName(String fn) { this.firstName = fn; } // NEW

    public String getLastName() { return lastName; }             // NEW
    public void setLastName(String ln) { this.lastName = ln; }   // NEW

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String pw) { this.password = pw; }

    public String getImageUrl() { return imageUrl; }            // NEW
    public void setImageUrl(String url) { this.imageUrl = url; } // NEW

    /** Convenience: full name */
    public String getName() { return firstName + " " + lastName; }
}
