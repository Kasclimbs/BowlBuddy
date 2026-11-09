package edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.controller;

import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.HelloApplication;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.Pet;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.SessionManager;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

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


}