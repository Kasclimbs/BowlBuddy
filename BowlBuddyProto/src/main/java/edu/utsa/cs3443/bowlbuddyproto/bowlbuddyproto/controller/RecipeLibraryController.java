package edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.controller;

import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.HelloApplication;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.data.DataManager;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.Recipe;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.IOException;

/** Controls the recipe list and its details panel. */
public class RecipeLibraryController {

    @FXML private ListView<Recipe> recipeListView;
    @FXML private Label recipeNameLabel;
    @FXML private Label recipeDetailsLabel;
    @FXML private TextArea instructionsArea;
    @FXML private Label messageLabel;

    private final DataManager dataManager = new DataManager();

    /** Loads the recipes and listens for selection changes. */
    @FXML
    private void initialize() {
        recipeListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, previous, selected) ->
                        showRecipe(selected));

        try {
            recipeListView.getItems().setAll(dataManager.loadRecipes());

            // Reselect a recipe that was just added or edited, if any.
            SessionManager session = SessionManager.getInstance();
            Recipe savedRecipe = session.getCurrentRecipe();
            session.setCurrentRecipe(null);

            if (savedRecipe != null) {
                for (Recipe recipe : recipeListView.getItems()) {
                    if (recipe.getRecipeId().equals(savedRecipe.getRecipeId())) {
                        recipeListView.getSelectionModel().select(recipe);
                        recipeListView.scrollTo(recipe);
                        break;
                    }
                }
            }

            // Otherwise show the first recipe's details when the library opens.
            if (recipeListView.getSelectionModel().isEmpty()
                    && !recipeListView.getItems().isEmpty()) {
                recipeListView.getSelectionModel().selectFirst();
            }
        } catch (IOException exception) {
            messageLabel.setText(
                    "Unable to load recipes: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    /** Displays the selected recipe or clears the panel. */
    private void showRecipe(Recipe recipe) {
        if (recipe == null) {
            recipeNameLabel.setText("Select a recipe");
            recipeDetailsLabel.setText("");
            instructionsArea.clear();
            return;
        }

        recipeNameLabel.setText(recipe.getName());
        recipeDetailsLabel.setText(
                "Brand: " + recipe.getBrand()
                        + "\nCalories: " + recipe.getCaloriesPer100g() + " kcal per 100 g"
                        + "\nTag: " + recipe.getTag()
                        + "\nFavorite: " + (recipe.isFavorite() ? "Yes" : "No"));
        instructionsArea.setText(recipe.getInstructions());
    }

    /** Opens an empty recipe form in the current window. */
    @FXML
    private void handleAddRecipe() {
        // No current recipe tells the form to add instead of edit.
        SessionManager.getInstance().setCurrentRecipe(null);
        openRecipeForm();
    }

    /** Opens the recipe form filled with the highlighted recipe. */
    @FXML
    private void handleEditRecipe() {
        Recipe selectedRecipe =
                recipeListView.getSelectionModel().getSelectedItem();

        if (selectedRecipe == null) {
            messageLabel.setText("Please select a recipe first.");
            return;
        }

        SessionManager.getInstance().setCurrentRecipe(selectedRecipe);
        openRecipeForm();
    }

    /** Asks for confirmation, then deletes the highlighted recipe. */
    @FXML
    private void handleDeleteRecipe() {
        Recipe selectedRecipe =
                recipeListView.getSelectionModel().getSelectedItem();

        if (selectedRecipe == null) {
            messageLabel.setText("Please select a recipe first.");
            return;
        }

        // Require confirmation before deleting the recipe's record.
        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Delete " + selectedRecipe.getName()
                        + " from the recipe library? This cannot be undone.",
                ButtonType.CANCEL,
                ButtonType.OK);

        confirmation.setTitle("Delete Recipe");
        confirmation.setHeaderText("Delete " + selectedRecipe.getName() + "?");
        confirmation.initOwner(recipeListView.getScene().getWindow());

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
            dataManager.removeRecipe(selectedRecipe.getRecipeId());
        } catch (IOException exception) {
            messageLabel.setText(
                    "Unable to delete recipe: " + exception.getMessage());
            exception.printStackTrace();
            return;
        }

        // Update the list only after deletion succeeds.
        recipeListView.getItems().remove(selectedRecipe);
        messageLabel.setText("");
    }

    /** Opens the add/edit recipe form in the current window. */
    private void openRecipeForm() {
        try {
            Parent formView = FXMLLoader.load(
                    HelloApplication.class.getResource("view/recipe-form-view.fxml"));

            recipeListView.getScene().setRoot(formView);
        } catch (IOException exception) {
            SessionManager.getInstance().setCurrentRecipe(null);
            messageLabel.setText("Unable to open the recipe form.");
            exception.printStackTrace();
        }
    }

    /** Returns to My Pets in the current window. */
    @FXML
    private void handleBackToPets() {
        try {
            Parent petListView = FXMLLoader.load(
                    HelloApplication.class.getResource("view/pet-list-view.fxml"));

            recipeListView.getScene().setRoot(petListView);
        } catch (IOException exception) {
            messageLabel.setText("Unable to open the pet list.");
            exception.printStackTrace();
        }
    }
}