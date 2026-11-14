package edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.controller;

import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.data.DataManager;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.PasswordUtil;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.User;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.SessionManager;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.HelloApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


import java.io.IOException;

/**
 * Loads account data and handles login form actions.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final DataManager dataManager = new DataManager();
    private boolean usersLoaded;

    @FXML
    private void initialize() {
        try {
            dataManager.loadUsers();
            usersLoaded = true;
        } catch (IOException exception) {
            messageLabel.setText(
                    "Unable to load accounts. Check data/users.csv.");
            exception.printStackTrace();
        }
    }

    @FXML
    private void handleLogin() {
        if (!usersLoaded) {
            messageLabel.setText(
                    "Accounts could not be loaded. Please restart after fixing the CSV.");
            return;
        }

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter your username and password.");
            return;
        }

        for (User user : dataManager.getUsers()) {
            if (user.getUsername().equals(username)
                    && PasswordUtil.verifyPassword(
                    password, user.getPasswordHash())) {
                SessionManager.getInstance().setCurrentUser(user);

                try {
                    Parent petListView = FXMLLoader.load(
                            HelloApplication.class.getResource(
                                    "view/pet-list-view.fxml"));

                    passwordField.clear();
                    usernameField.getScene().setRoot(petListView);

                } catch (IOException exception) {
                    SessionManager.getInstance().logout();
                    messageLabel.setStyle("-fx-text-fill: #B42318;");
                    messageLabel.setText("Unable to open the pet list.");
                    exception.printStackTrace();
                }

                return;
            }
        }

        messageLabel.setStyle("-fx-text-fill: #B42318;");
        messageLabel.setText("Invalid username or password.");
        passwordField.clear();
    }

    /**
     * Opens the registration form in the current window.
     */
    @FXML
    private void handleOpenCreateAccount() {
        try {
            Parent createAccountView = FXMLLoader.load(
                    HelloApplication.class.getResource(
                            "view/create-account-view.fxml"));

            // Clear the sign-in password before leaving the screen.
            passwordField.clear();
            usernameField.getScene().setRoot(createAccountView);
        } catch (IOException exception) {
            messageLabel.setStyle("-fx-text-fill: #B42318;");
            messageLabel.setText("Unable to open the registration form.");
            exception.printStackTrace();
        }
    }


}