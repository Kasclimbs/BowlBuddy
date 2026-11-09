package edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model;

/**
 * Stores a BowlBuddy user's account information.
 * File reading and writing are handled by DataManager.
 */
public class User {

    private final String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String passwordHash;

    public User(String userId, String firstName, String lastName,
                String email, String username, String passwordHash) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}