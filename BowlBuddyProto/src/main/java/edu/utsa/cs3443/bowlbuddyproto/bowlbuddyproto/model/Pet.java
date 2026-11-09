package edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model;

/**
 * Stores a pet's profile and its owner's user ID.
 * Weight is measured in kilograms.
 */
public class Pet {

    private final String petId;
    private final String userId;
    private String animalType;
    private String name;
    private String breed;
    private int ageYears;
    private float weightKg;
    private int dailyCalorieGoal;
    private int mealsPerDay;
    private String medication;
    private String notes;

    public Pet(String petId, String userId, String animalType,
               String name, String breed, int ageYears, float weightKg,
               int dailyCalorieGoal, int mealsPerDay,
               String medication, String notes) {

        this.petId = petId;
        this.userId = userId;
        this.animalType = animalType;
        this.name = name;
        this.breed = breed;
        this.ageYears = ageYears;
        this.weightKg = weightKg;
        this.dailyCalorieGoal = dailyCalorieGoal;
        this.mealsPerDay = mealsPerDay;
        this.medication = medication;
        this.notes = notes;
    }

    public String getPetId() {
        return petId;
    }

    public String getUserId() {
        return userId;
    }

    public String getAnimalType() {
        return animalType;
    }

    public String getName() {
        return name;
    }

    public String getBreed() {
        return breed;
    }

    public int getAgeYears() {
        return ageYears;
    }

    public float getWeightKg() {
        return weightKg;
    }

    public int getDailyCalorieGoal() {
        return dailyCalorieGoal;
    }

    public int getMealsPerDay() {
        return mealsPerDay;
    }

    public String getMedication() {
        return medication;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return name + " — " + animalType + " — " + breed;
    }
}