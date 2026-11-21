package edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.controller;

import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.HelloApplication;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.data.DataManager;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.Recipe;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controls the recipe form used for both adding and editing recipes.
 * Edits the session's current recipe, or adds a new one when none is set.
 */
public class RecipeFormController {

    // Controls injected from recipe-form-view.fxml.
    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextField brandField;
    @FXML private TextField caloriesField;
    @FXML private TextField tagField;
    @FXML private CheckBox favoriteCheckBox;
    @FXML private TextArea instructionsField;
    @FXML private Label messageLabel;

    private final DataManager dataManager = new DataManager();

    // Null when adding; otherwise the recipe whose ID must stay unchanged.
    private Recipe originalRecipe;
    private boolean recipeSaved;

    /**
     * Fills the form with the selected recipe's details when editing.
     */
    @FXML
    private void initialize() {
        originalRecipe = SessionManager.getInstance().getCurrentRecipe();

        if (originalRecipe == null) {
            return;
        }

        titleLabel.setText("Edit Recipe");
        nameField.setText(originalRecipe.getName());
        brandField.setText(originalRecipe.getBrand());
        caloriesField.setText(
                Integer.toString(originalRecipe.getCaloriesPer100g()));
        tagField.setText(originalRecipe.getTag());
        favoriteCheckBox.setSelected(originalRecipe.isFavorite());
        instructionsField.setText(originalRecipe.getInstructions());
    }

    /**
     * Validates the form and adds or updates the recipe.
     */
    @FXML
    private void handleSaveRecipe() {
        // A successful save should not be repeated if navigation failed.
        if (recipeSaved) {
            returnToLibrary();
            return;
        }

        String name = nameField.getText().trim();
        String brand = brandField.getText().trim();

        if (name.isEmpty() || brand.isEmpty()) {
            messageLabel.setText("Please enter the recipe's name and brand.");
            return;
        }

        int calories;

        // Convert calories and report invalid input.
        try {
            calories = Integer.parseInt(caloriesField.getText().trim());
        } catch (NumberFormatException exception) {
            messageLabel.setText("Use a whole number for calories.");
            return;
        }

        if (calories < 0) {
            messageLabel.setText("Calories must be zero or greater.");
            return;
        }

        // All CSV access stays in DataManager to maintain MVC separation.
        try {
            // Keep the original ID when editing; otherwise use the next R-number.
            String recipeId = originalRecipe != null
                    ? originalRecipe.getRecipeId()
                    : dataManager.nextRecipeId();

            Recipe recipe = new Recipe(
                    recipeId,
                    name,
                    brand,
                    calories,
                    tagField.getText().trim(),
                    favoriteCheckBox.isSelected(),
                    instructionsField.getText().trim());

            if (originalRecipe != null) {
                dataManager.updateRecipe(recipe);
            } else {
                dataManager.addRecipe(recipe);
            }

            // Let the library reselect the saved recipe when it reopens.
            SessionManager.getInstance().setCurrentRecipe(recipe);
            recipeSaved = true;
        } catch (IOException exception) {
            messageLabel.setText(
                    "Unable to save recipe: " + exception.getMessage());
            exception.printStackTrace();
            return;
        }

        returnToLibrary();
    }

    /**
     * Returns to the recipe library without saving additional changes.
     */
    @FXML
    private void handleCancel() {
        returnToLibrary();
    }

    /**
     * Reloads the recipe library in the existing scene.
     */
    private void returnToLibrary() {
        try {
            Parent libraryView = FXMLLoader.load(
                    HelloApplication.class.getResource(
                            "view/recipe-library-view.fxml"));

            nameField.getScene().setRoot(libraryView);
        } catch (IOException exception) {
            // Distinguish a navigation failure from a failed save.
            messageLabel.setText(recipeSaved
                    ? "Recipe saved. Unable to open the recipe library. "
                    + "Click Cancel to try again."
                    : "Unable to open the recipe library.");
            exception.printStackTrace();
        }
    }
}
