package edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.controller;

import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.HelloApplication;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.SessionManager;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.User;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.data.DataManager;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.Pet;
import javafx.scene.control.ListView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.io.IOException;

/**
 * Displays the current user's pet list and handles logout.
 */
public class PetListController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private ListView<Pet> petListView;

    private final DataManager dataManager = new DataManager();

    @FXML
    private Label messageLabel;

    @FXML
    private void initialize() {
        User user = SessionManager.getInstance().getCurrentUser();

        if (user == null) {
            messageLabel.setText("Please log in to view your pets.");
            return;
        }

        welcomeLabel.setText("Welcome, " + user.getFirstName() + "!");

        try {
            petListView.getItems().setAll(
                    dataManager.loadPetsForUser(user.getUserId()));

            if (petListView.getItems().isEmpty()) {
                messageLabel.setText("You haven't added any pets yet.");
            }

        } catch (IOException exception) {
            messageLabel.setText(
                    "Unable to load pets. Check data/pets.csv.");
            exception.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Parent loginView = FXMLLoader.load(
                    HelloApplication.class.getResource(
                            "view/login-view.fxml"));

            SessionManager.getInstance().logout();
            welcomeLabel.getScene().setRoot(loginView);

        } catch (IOException exception) {
            messageLabel.setText("Unable to open the login screen.");
            exception.printStackTrace();
        }
    }

    @FXML
    private void handleSelectPet() {
        Pet selectedPet = petListView.getSelectionModel().getSelectedItem();

        if (selectedPet == null) {
            messageLabel.setText("Please select a pet first.");
            return;
        }

        SessionManager session = SessionManager.getInstance();
        Pet previousPet = session.getCurrentPet();
        session.setCurrentPet(selectedPet);

        try {
            Parent profileView = FXMLLoader.load(
                    HelloApplication.class.getResource(
                            "view/pet-profile-view.fxml"));

            petListView.getScene().setRoot(profileView);

        } catch (IOException exception) {
            session.setCurrentPet(previousPet);
            messageLabel.setText("Unable to open the pet profile.");
            exception.printStackTrace();
        }
    }

    /**
     * Opens the Add Pet form in the existing scene.
     */
    @FXML
    private void handleAddPet() {
        try {
            Parent addPetView = FXMLLoader.load(
                    HelloApplication.class.getResource(
                            "view/add-pet-view.fxml"));

            petListView.getScene().setRoot(addPetView);
        } catch (IOException exception) {
            messageLabel.setText("Unable to open the Add Pet form.");
            exception.printStackTrace();
        }
    }

    /**
     * Opens the signed-in user's profile in the current window.
     */
    @FXML
    private void handleOpenUserProfile() {
        try {
            Parent profileView = FXMLLoader.load(
                    HelloApplication.class.getResource("view/user-profile-view.fxml"));

            petListView.getScene().setRoot(profileView);
        } catch (IOException exception) {
            messageLabel.setText("Unable to open your profile.");
            exception.printStackTrace();
        }
    }

    /** Opens the recipe library in the current window. */
    @FXML
    private void handleOpenRecipeLibrary() {
        try {
            Parent recipeView = FXMLLoader.load(
                    HelloApplication.class.getResource("view/recipe-library-view.fxml"));

            petListView.getScene().setRoot(recipeView);
        } catch (IOException exception) {
            messageLabel.setText("Unable to open the recipe library.");
            exception.printStackTrace();
        }
    }
}