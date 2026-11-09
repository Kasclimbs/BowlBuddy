package edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model;

/**
 * Stores the currently logged-in user during this app session.
 */
public final class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private User currentUser;
    private Pet currentPet;


    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public Pet getCurrentPet() {
        return currentPet;
    }

    public void setCurrentPet(Pet pet) {
        currentPet = pet;
    }

    public void logout() {
        currentUser = null;
        currentPet = null;
    }
}