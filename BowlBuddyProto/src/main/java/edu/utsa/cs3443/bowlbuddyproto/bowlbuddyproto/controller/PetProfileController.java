package edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.controller;

import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.HelloApplication;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.Pet;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.SessionManager;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.data.DataManager;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;

import java.io.IOException;

/**
 * Displays the selected pet's profile.
 */
public class PetProfileController {

    @FXML private Label petNameLabel;
    @FXML private Label animalTypeLabel;
    @FXML private Label breedLabel;
    @FXML private Label ageLabel;
    @FXML private Label weightLabel;
    @FXML private Label calorieGoalLabel;
    @FXML private Label mealsPerDayLabel;
    @FXML private Label medicationLabel;
    @FXML private Label notesLabel;
    @FXML private Label messageLabel;

    @FXML
    private void initialize() {
        SessionManager session = SessionManager.getInstance();
        User user = session.getCurrentUser();
        Pet pet = session.getCurrentPet();

        if (user == null || pet == null
                || !pet.getUserId().equals(user.getUserId())) {
            messageLabel.setText("Please select one of your pets.");
            return;
        }

        petNameLabel.setText(pet.getName());
        animalTypeLabel.setText(pet.getAnimalType());
        breedLabel.setText(pet.getBreed());
        ageLabel.setText(pet.getAgeYears() + " years");
        weightLabel.setText(pet.getWeightKg() + " kg");
        calorieGoalLabel.setText(pet.getDailyCalorieGoal() + " kcal");
        mealsPerDayLabel.setText(
                Integer.toString(pet.getMealsPerDay()));
        medicationLabel.setText(pet.getMedication());
        notesLabel.setText(pet.getNotes());
    }

    @FXML
    private void handleBackToPets() {
        try {
            Parent petListView = FXMLLoader.load(
                    HelloApplication.class.getResource(
                            "view/pet-list-view.fxml"));

            petNameLabel.getScene().setRoot(petListView);

        } catch (IOException exception) {
            messageLabel.setText("Unable to open your pet list.");
            exception.printStackTrace();
        }
    }

    /**
     * Opens the edit form for the pet selected in the current session.
     */
    @FXML
    private void handleEditPet() {
        try {
            Parent editPetView = FXMLLoader.load(
                    HelloApplication.class.getResource(
                            "view/edit-pet-view.fxml"));

            petNameLabel.getScene().setRoot(editPetView);
        } catch (IOException exception) {
            messageLabel.setText("Unable to open the Edit Pet form.");
            exception.printStackTrace();
        }
    }

    /**
     * Confirms removal of the current pet, deletes its CSV record,
     * and returns to the pet list.
     */
    @FXML
    private void handleRemovePet() {
        SessionManager session = SessionManager.getInstance();
        User user = session.getCurrentUser();
        Pet pet = session.getCurrentPet();

        // Only allow removal of a pet owned by the logged-in user.
        if (user == null || pet == null
                || !pet.getUserId().equals(user.getUserId())) {
            messageLabel.setText("Please select one of your pets first.");
            return;
        }

        // Require confirmation before deleting the pet's record.
        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Remove " + pet.getName()
                        + " from your pets? This cannot be undone.",
                ButtonType.CANCEL,
                ButtonType.OK);

        confirmation.setTitle("Remove Pet");
        confirmation.setHeaderText("Remove " + pet.getName() + "?");
        confirmation.initOwner(petNameLabel.getScene().getWindow());

        // Cast the dialog controls to Button to configure the default action.
        Button okButton = (Button) confirmation.getDialogPane()
                .lookupButton(ButtonType.OK);

        Button cancelButton = (Button) confirmation.getDialogPane()
                .lookupButton(ButtonType.CANCEL);

// Make Cancel the default to prevent accidental confirmation.
        okButton.setDefaultButton(false);
        cancelButton.setDefaultButton(true);

        // Closing the dialog has the same effect as clicking Cancel.
        ButtonType result = confirmation.showAndWait()
                .orElse(ButtonType.CANCEL);

        if (result != ButtonType.OK) {
            return;
        }

        // Delegate all file access to DataManager.
        try {
            DataManager dataManager = new DataManager();
            dataManager.removePet(pet.getPetId(), user.getUserId());
        } catch (IOException exception) {
            messageLabel.setText(
                    "Unable to remove pet: " + exception.getMessage());
            exception.printStackTrace();
            return;
        }

        // Clear the selected pet only after deletion succeeds.
        session.setCurrentPet(null);

        // Reload the list so the removed pet no longer appears.
        try {
            Parent petListView = FXMLLoader.load(
                    HelloApplication.class.getResource(
                            "view/pet-list-view.fxml"));

            petNameLabel.getScene().setRoot(petListView);
        } catch (IOException exception) {
            messageLabel.setText(
                    "Pet removed. Click Back to My Pets to return.");
            exception.printStackTrace();
        }
    }


}