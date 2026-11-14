package edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.controller;

import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.HelloApplication;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.data.DataManager;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.Recipe;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

            // Show the first recipe's details when the library opens.
            if (!recipeListView.getItems().isEmpty()) {
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