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
import java.util.UUID;

/**
 * Controls the Add Pet form.
 * Validates input and delegates file storage to DataManager.
 */
public class AddPetController {

    // These controls are injected from add-pet-view.fxml.
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

    // Prevents saving the same form twice if navigation fails after saving.
    private boolean petSaved;

    /**
     * Validates the form and saves a pet belonging to the current user.
     */
    @FXML
    private void handleSavePet() {
        if (petSaved) {
            messageLabel.setText(
                    "Your pet is already saved. Click Cancel to return.");
            return;
        }

        // Associate the new pet with the logged-in user's ID.
        User user = SessionManager.getInstance().getCurrentUser();

        if (user == null) {
            messageLabel.setText("Please log in before adding a pet.");
            return;
        }

        String name = nameField.getText().trim();
        String animalType = animalTypeField.getText().trim();
        String breed = breedField.getText().trim();

        // Require the basic identifying information.
        if (name.isEmpty() || animalType.isEmpty() || breed.isEmpty()) {
            messageLabel.setText(
                    "Please enter the pet's name, animal type, and breed.");
            return;
        }

        int age;
        float weight;
        int calorieGoal;
        int mealsPerDay;

        // Convert text input into the numeric types used by Pet.
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

        // Reject invalid ranges, including NaN and infinite weights.
        if (age < 0 || !Float.isFinite(weight) || weight <= 0
                || calorieGoal <= 0 || mealsPerDay <= 0) {
            messageLabel.setText(
                    "Age must be zero or greater. Weight, calories, "
                            + "and meals must be greater than zero.");
            return;
        }

        // Medication is optional; use a readable default when left blank.
        String medication = medicationField.getText().trim();
        if (medication.isEmpty()) {
            medication = "None";
        }

        // Generate a unique ID rather than using the pet's name as an ID.
        Pet pet = new Pet(
                "P-" + UUID.randomUUID(),
                user.getUserId(),
                animalType,
                name,
                breed,
                age,
                weight,
                calorieGoal,
                mealsPerDay,
                medication,
                notesField.getText().trim());

        // All CSV access stays in DataManager to maintain MVC separation.
        try {
            dataManager.addPet(pet);
            petSaved = true;
        } catch (IOException exception) {
            messageLabel.setText(
                    "Unable to save pet: " + exception.getMessage());
            exception.printStackTrace();
            return;
        }

        // Reload the pet list so it includes the newly saved pet.
        returnToPetList();
    }

    /**
     * Returns to the pet list without saving additional changes.
     */
    @FXML
    private void handleCancel() {
        returnToPetList();
    }

    /**
     * Opens the pet list in the existing scene, preserving window size.
     */
    private void returnToPetList() {
        try {
            Parent petListView = FXMLLoader.load(
                    HelloApplication.class.getResource(
                            "view/pet-list-view.fxml"));

            nameField.getScene().setRoot(petListView);
        } catch (IOException exception) {
            // Distinguish a navigation failure from a failed save.
            messageLabel.setText(petSaved
                    ? "Pet saved. Unable to open the pet list. "
                    + "Click Cancel to try again."
                    : "Unable to open the pet list.");
            exception.printStackTrace();
        }
    }
}
