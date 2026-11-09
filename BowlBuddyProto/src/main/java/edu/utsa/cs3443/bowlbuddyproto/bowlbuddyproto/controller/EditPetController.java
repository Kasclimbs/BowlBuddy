package edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.controller;

import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.HelloApplication;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.data.DataManager;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.Pet;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.SessionManager;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controls the Edit Pet form.
 * Displays existing details and delegates CSV updates to DataManager.
 */
public class EditPetController {

    // Controls injected from edit-pet-view.fxml.
    @FXML private TextField nameField;
    @FXML private TextField animalTypeField;
    @FXML private TextField breedField;
    @FXML private TextField ageField;
    @FXML private TextField weightField;
    @FXML private TextField calorieGoalField;
    @FXML private TextField mealsPerDayField;
    @FXML private TextField medicationField;
    @FXML private TextArea notesField;
    @FXML private Label messageLabel;

    private final DataManager dataManager = new DataManager();

    // Keep the original pet so editing never changes its identity or owner.
    private Pet originalPet;
    private boolean changesSaved;

    /**
     * Fills the form with the selected pet's existing details.
     */
    @FXML
    private void initialize() {
        SessionManager session = SessionManager.getInstance();
        User user = session.getCurrentUser();
        Pet pet = session.getCurrentPet();

        // Only allow editing a pet owned by the current user.
        if (user == null || pet == null
                || !pet.getUserId().equals(user.getUserId())) {
            messageLabel.setText("Please select one of your pets first.");
            return;
        }

        originalPet = pet;

        nameField.setText(pet.getName());
        animalTypeField.setText(pet.getAnimalType());
        breedField.setText(pet.getBreed());
        ageField.setText(Integer.toString(pet.getAgeYears()));
        weightField.setText(Float.toString(pet.getWeightKg()));
        calorieGoalField.setText(
                Integer.toString(pet.getDailyCalorieGoal()));
        mealsPerDayField.setText(
                Integer.toString(pet.getMealsPerDay()));
        medicationField.setText(pet.getMedication());
        notesField.setText(pet.getNotes());
    }

    /**
     * Validates the form and saves changes to the selected pet.
     */
    @FXML
    private void handleSaveChanges() {
        // A successful save should not be repeated if navigation failed.
        if (changesSaved) {
            returnToProfile();
            return;
        }

        SessionManager session = SessionManager.getInstance();
        User user = session.getCurrentUser();

        // Check ownership again before writing any changes.
        if (originalPet == null || user == null
                || !originalPet.getUserId().equals(user.getUserId())) {
            messageLabel.setText("Please select one of your pets first.");
            return;
        }

        String name = nameField.getText().trim();
        String animalType = animalTypeField.getText().trim();
        String breed = breedField.getText().trim();

        if (name.isEmpty() || animalType.isEmpty() || breed.isEmpty()) {
            messageLabel.setText(
                    "Please enter the pet's name, animal type, and breed.");
            return;
        }

        int age;
        float weight;
        int calorieGoal;
        int mealsPerDay;

        // Convert numeric fields and report invalid input.
        try {
            age = Integer.parseInt(ageField.getText().trim());
            weight = Float.parseFloat(weightField.getText().trim());
            calorieGoal = Integer.parseInt(
                    calorieGoalField.getText().trim());
            mealsPerDay = Integer.parseInt(
                    mealsPerDayField.getText().trim());
        } catch (NumberFormatException exception) {
            messageLabel.setText(
                    "Use whole numbers for age, calories, and meals. "
                            + "Weight may include a decimal.");
            return;
        }

        // Reject negative values and non-finite weights.
        if (age < 0 || !Float.isFinite(weight) || weight <= 0
                || calorieGoal <= 0 || mealsPerDay <= 0) {
            messageLabel.setText(
                    "Age must be zero or greater. Weight, calories, "
                            + "and meals must be greater than zero.");
            return;
        }

        String medication = medicationField.getText().trim();
        if (medication.isEmpty()) {
            medication = "None";
        }

        // Build a replacement object while preserving the original IDs.
        // The original object stays unchanged if saving fails.
        Pet updatedPet = new Pet(
                originalPet.getPetId(),
                originalPet.getUserId(),
                animalType,
                name,
                breed,
                age,
                weight,
                calorieGoal,
                mealsPerDay,
                medication,
                notesField.getText().trim());

        try {
            dataManager.updatePet(updatedPet);
        } catch (IOException exception) {
            messageLabel.setText(
                    "Unable to save changes: " + exception.getMessage());
            exception.printStackTrace();
            return;
        }

        // Update the session only after the CSV update succeeds.
        session.setCurrentPet(updatedPet);
        changesSaved = true;
        returnToProfile();
    }

    /**
     * Returns to the profile without saving additional changes.
     */
    @FXML
    private void handleCancel() {
        returnToProfile();
    }

    /**
     * Reloads the profile in the existing scene.
     */
    private void returnToProfile() {
        try {
            Parent profileView = FXMLLoader.load(
                    HelloApplication.class.getResource(
                            "view/pet-profile-view.fxml"));

            nameField.getScene().setRoot(profileView);
        } catch (IOException exception) {
            messageLabel.setText(changesSaved
                    ? "Changes saved. Unable to open the profile. "
                    + "Click Cancel to try again."
                    : "Unable to open the pet profile.");
            exception.printStackTrace();
        }
    }
}