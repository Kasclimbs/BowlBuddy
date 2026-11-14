package edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.controller;

import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.HelloApplication;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.data.DataManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controls account registration.
 * Validates form input and delegates account storage to DataManager.
 */
public class CreateAccountController {

    // Controls injected from create-account-view.fxml.
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private final DataManager dataManager = new DataManager();
    private boolean accountCreated;

    /**
     * Validates the form and creates a new account.
     */
    @FXML
    private void handleCreateAccount() {
        // Prevent duplicate submissions after a successful registration.
        if (accountCreated) {
            messageLabel.setText(
                    "Account created. Click Back to Sign In.");
            return;
        }

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();

        // Preserve passwords exactly as entered, including spaces.
        String password = passwordField.getText();
        String confirmation = confirmPasswordField.getText();

        messageLabel.setStyle("-fx-text-fill: #B42318;");

        if (firstName.isEmpty() || lastName.isEmpty()
                || email.isEmpty() || username.isEmpty()
                || password.isEmpty() || confirmation.isEmpty()) {
            messageLabel.setText("Please complete every field.");
            return;
        }

        // Perform a basic format check; this does not verify email ownership.
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            messageLabel.setText("Please enter a valid email address.");
            return;
        }

        // Keep usernames predictable and compatible with the login form.
        if (!username.matches("[A-Za-z0-9_]{3,30}")) {
            messageLabel.setText(
                    "Use 3–30 letters, numbers, or underscores "
                            + "for your username.");
            return;
        }

        if (password.length() < 8) {
            messageLabel.setText(
                    "Your password must contain at least 8 characters.");
            return;
        }

        if (!password.equals(confirmation)) {
            messageLabel.setText("Your passwords do not match.");
            return;
        }

        // DataManager checks duplicates, hashes the password, and saves.
        try {
            dataManager.registerUser(
                    firstName, lastName, email, username, password);

            accountCreated = true;

            // Clear sensitive input after successful registration.
            passwordField.clear();
            confirmPasswordField.clear();

            messageLabel.setStyle("-fx-text-fill: #284D3B;");
            messageLabel.setText(
                    "Account created! Click Back to Sign In.");
        } catch (IOException exception) {
            messageLabel.setText(
                    "Unable to create account: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    /**
     * Opens the login screen without submitting the form.
     */
    @FXML
    private void handleBackToLogin() {
        try {
            Parent loginView = FXMLLoader.load(
                    HelloApplication.class.getResource(
                            "view/login-view.fxml"));

            passwordField.clear();
            confirmPasswordField.clear();
            firstNameField.getScene().setRoot(loginView);
        } catch (IOException exception) {
            messageLabel.setStyle("-fx-text-fill: #B42318;");
            messageLabel.setText("Unable to open the login screen.");
            exception.printStackTrace();
        }
    }
}