package edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.controller;

import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.HelloApplication;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.data.DataManager;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.SessionManager;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Displays and updates the signed-in user's profile.
 */
public class UserProfileController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private Label usernameLabel;
    @FXML private Label messageLabel;

    private final DataManager dataManager = new DataManager();

    /** Fills the form with the current account details. */
    @FXML
    private void initialize() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) {
            showError("Please sign in to view your profile.");
            return;
        }

        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        usernameLabel.setText(user.getUsername());
    }

    /** Validates the form and saves changes for the current account. */
    @FXML
    private void handleSaveProfile() {
        SessionManager session = SessionManager.getInstance();
        User user = session.getCurrentUser();

        if (user == null) {
            showError("Please sign in before saving changes.");
            return;
        }

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            showError("Please enter your first and last name.");
            return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            showError("Please enter a valid email address.");
            return;
        }

        try {
            User updated = dataManager.updateUserProfile(
                    user.getUserId(), firstName, lastName, email);

            // Refresh the session so My Pets uses the updated first name.
            session.setCurrentUser(updated);
            initialize();
            messageLabel.setStyle("-fx-text-fill: #284D3B;");
            messageLabel.setText("Profile saved.");
        } catch (IOException exception) {
            showError("Unable to save profile: " + exception.getMessage());
        }
    }

    /** Returns to the pet list in the same window. */
    @FXML
    private void handleBackToPets() {
        try {
            Parent petListView = FXMLLoader.load(
                    HelloApplication.class.getResource("view/pet-list-view.fxml"));

            firstNameField.getScene().setRoot(petListView);
        } catch (IOException exception) {
            showError("Unable to open the pet list.");
            exception.printStackTrace();
        }
    }

    /** Shows an error beneath the form. */
    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: #B42318;");
        messageLabel.setText(message);
    }
}