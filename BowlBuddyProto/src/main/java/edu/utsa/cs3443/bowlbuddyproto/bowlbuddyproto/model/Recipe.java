package edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model;

/**
 * Represents one recipe from the recipe library.
 */
public class Recipe {

    private final String recipeId;
    private final String name;
    private final String brand;
    private final int caloriesPer100g;
    private final String tag;
    private final boolean favorite;
    private final String instructions;

    /**
     * Creates a recipe using the fields from one CSV record.
     */
    public Recipe(String recipeId, String name, String brand,
                  int caloriesPer100g, String tag, boolean favorite,
                  String instructions) {
        this.recipeId = recipeId;
        this.name = name;
        this.brand = brand;
        this.caloriesPer100g = caloriesPer100g;
        this.tag = tag;
        this.favorite = favorite;
        this.instructions = instructions;
    }

    public String getRecipeId() { return recipeId; }
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public int getCaloriesPer100g() { return caloriesPer100g; }
    public String getTag() { return tag; }
    public boolean isFavorite() { return favorite; }
    public String getInstructions() { return instructions; }

    /**
     * Calculates calories for a portion measured in grams.
     */
    public double caloriesFor(double grams) {
        return caloriesPer100g * grams / 100.0;
    }

    /**
     * Shows the recipe name when it appears in a list.
     */
    @Override
    public String toString() {
        return name;
    }
}