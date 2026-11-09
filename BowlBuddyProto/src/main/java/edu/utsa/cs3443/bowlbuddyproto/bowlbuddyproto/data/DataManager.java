package edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.data;

import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.User;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.PasswordUtil;
import edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model.Pet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages application data and CSV file operations.
 */
public class DataManager {

    private final Path usersFile;
    private final List<User> users;

    public DataManager() {
        usersFile = Path.of("data", "users.csv");
        users = new ArrayList<>();
    }

    public List<User> getUsers() {
        return List.copyOf(users);
    }
    /**
     * Loads the supplied user CSV into memory.
     * The Password column contains the sample passwords.
     * PasswordHash is supported for files saved with hashed passwords.
     */
    public void loadUsers() throws IOException {
        List<String> lines =
                Files.readAllLines(usersFile, StandardCharsets.UTF_8);

        if (lines.isEmpty()) {
            throw new IOException("The user CSV is empty.");
        }

        // Remove a possible UTF-8 byte order mark from the header.
        String header = lines.get(0);
        if (header.startsWith("\uFEFF")) {
            header = header.substring(1);
        }

        boolean containsHashes;

        if (header.equals(
                "UserId,First Name,Last Name,Email,Username,Password")) {
            containsHashes = false;
        } else if (header.equals(
                "UserId,First Name,Last Name,Email,Username,PasswordHash")) {
            containsHashes = true;
        } else {
            throw new IOException("The user CSV has an unexpected header.");
        }

        List<User> loadedUsers = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.isBlank()) {
                continue;
            }

            // This initial loader supports the supplied unquoted CSV.
            if (line.contains("\"")) {
                throw new IOException(
                        "Quoted CSV fields are not supported yet. Row " + (i + 1));
            }

            String[] fields = line.split(",", -1);

            if (fields.length != 6) {
                throw new IOException(
                        "Expected six columns at row " + (i + 1));
            }

            String passwordHash = containsHashes
                    ? fields[5]
                    : PasswordUtil.hashPassword(fields[5]);

            User user = new User(
                    fields[0].trim(),
                    fields[1].trim(),
                    fields[2].trim(),
                    fields[3].trim(),
                    fields[4].trim(),
                    passwordHash
            );

            loadedUsers.add(user);
        }

        // Replace the current list only after loading succeeds.
        users.clear();
        users.addAll(loadedUsers);
    }
    /**
     * Loads pets belonging to the specified user.
     */
    public List<Pet> loadPetsForUser(String userId) throws IOException {
        Path petsFile = Path.of("data", "pets.csv");
        List<String> lines =
                Files.readAllLines(petsFile, StandardCharsets.UTF_8);

        if (lines.isEmpty()) {
            throw new IOException("The pet CSV is empty.");
        }

        String header = lines.get(0);
        if (header.startsWith("\uFEFF")) {
            header = header.substring(1);
        }

        String expectedHeader =
                "PetId,UserId,Animal Type,Name,Breed,Age (yrs),"
                        + "Weight (kg),Daily Calorie Goal,Meals Per Day,"
                        + "Medication,Notes";

        if (!header.equals(expectedHeader)) {
            throw new IOException("The pet CSV has an unexpected header.");
        }

        List<Pet> pets = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.isBlank()) {
                continue;
            }

            String[] fields = parseCsvLine(line);



            if (fields.length != 11) {
                throw new IOException(
                        "Expected eleven columns at row " + (i + 1));
            }

            // Only include pets owned by this user.
            if (!fields[1].trim().equals(userId)) {
                continue;
            }

            try {
                Pet pet = new Pet(
                        fields[0].trim(),
                        fields[1].trim(),
                        fields[2].trim(),
                        fields[3].trim(),
                        fields[4].trim(),
                        Integer.parseInt(fields[5].trim()),
                        Float.parseFloat(fields[6].trim()),
                        Integer.parseInt(fields[7].trim()),
                        Integer.parseInt(fields[8].trim()),
                        fields[9].trim(),
                        fields[10].trim()
                );

                pets.add(pet);

            } catch (NumberFormatException exception) {
                throw new IOException(
                        "Invalid number in pet CSV at row " + (i + 1),
                        exception);
            }
        }

        return pets;
    }
    /**
     * Reads a CSV row, preserving commas inside quoted fields
     * and converting doubled quotes into literal quotes.
     */
    private String[] parseCsvLine(String line) throws IOException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);

            if (character == '"') {
                if (insideQuotes
                        && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {

                    field.append('"');
                    i++;
                } else {
                    insideQuotes = !insideQuotes;
                }

            } else if (character == ',' && !insideQuotes) {
                fields.add(field.toString());
                field.setLength(0);

            } else {
                field.append(character);
            }
        }

        if (insideQuotes) {
            throw new IOException("CSV row contains an unclosed quoted field.");
        }

        fields.add(field.toString());
        return fields.toArray(new String[0]);
    }


    /**
     * Adds a new pet to the CSV file while preserving existing records.
     *
     * @param pet the pet to save
     * @throws IOException if the file is invalid or the pet cannot be saved
     */
    public void addPet(Pet pet) throws IOException {
        Path petsFile = Path.of("data", "pets.csv");

        String contents = Files.readString(
                petsFile, StandardCharsets.UTF_8);

        String expectedHeader =
                "PetId,UserId,Animal Type,Name,Breed,Age (yrs),"
                        + "Weight (kg),Daily Calorie Goal,Meals Per Day,"
                        + "Medication,Notes";

        List<String> lines = Files.readAllLines(
                petsFile, StandardCharsets.UTF_8);

        // Check the file structure before adding another record.
        if (lines.isEmpty()) {
            throw new IOException("The pet CSV is empty.");
        }

        String header = lines.get(0);

        // Some spreadsheet programs place a byte order mark before the header.
        if (header.startsWith("\uFEFF")) {
            header = header.substring(1);
        }

        if (!header.equals(expectedHeader)) {
            throw new IOException("The pet CSV has an unexpected header.");
        }

        // Validate existing rows and prevent duplicate pet IDs.
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                continue;
            }

            String[] fields = parseCsvLine(lines.get(i));

            if (fields.length != 11) {
                throw new IOException(
                        "Invalid pet record at row " + (i + 1));
            }

            if (fields[0].trim().equals(pet.getPetId())) {
                throw new IOException("That pet ID already exists.");
            }
        }

        // Store values in the same order as the CSV header.
        String row = String.join(",",
                csvField(pet.getPetId()),
                csvField(pet.getUserId()),
                csvField(pet.getAnimalType()),
                csvField(pet.getName()),
                csvField(pet.getBreed()),
                Integer.toString(pet.getAgeYears()),
                Float.toString(pet.getWeightKg()),
                Integer.toString(pet.getDailyCalorieGoal()),
                Integer.toString(pet.getMealsPerDay()),
                csvField(pet.getMedication()),
                csvField(pet.getNotes()));

        // Separate the new record if the existing file has no final newline.
        String separator =
                contents.endsWith("\n") || contents.endsWith("\r")
                        ? "" : System.lineSeparator();

        // Append the new pet without overwriting existing records.
        Files.writeString(
                petsFile,
                separator + row + System.lineSeparator(),
                StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.APPEND);
    }

    /**
     * Formats a text value for storage in a single CSV field.
     *
     * @param value the text to format
     * @return the value with CSV quotation marks escaped when necessary
     * @throws IOException if the value contains a line break
     */
    private String csvField(String value) throws IOException {
        if (value == null) {
            return "";
        }

        // The current CSV reader expects each record on one physical line.
        if (value.contains("\n") || value.contains("\r")) {
            throw new IOException(
                    "Please enter each field on a single line.");
        }

        // Quote fields containing commas and double any embedded quotes.
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }


    /**
     * Updates an existing pet's CSV record.
     * Matches both pet ID and owner ID to preserve ownership.
     *
     * @param pet the pet containing the updated details
     * @throws IOException if the file is invalid or cannot be updated
     */
    public void updatePet(Pet pet) throws IOException {
        Path petsFile = Path.of("data", "pets.csv");

        List<String> lines = Files.readAllLines(
                petsFile, StandardCharsets.UTF_8);

        String expectedHeader =
                "PetId,UserId,Animal Type,Name,Breed,Age (yrs),"
                        + "Weight (kg),Daily Calorie Goal,Meals Per Day,"
                        + "Medication,Notes";

        if (lines.isEmpty()) {
            throw new IOException("The pet CSV is empty.");
        }

        // Remove an optional byte order mark when checking the header.
        String header = lines.get(0);
        if (header.startsWith("\uFEFF")) {
            header = header.substring(1);
        }

        if (!header.equals(expectedHeader)) {
            throw new IOException("The pet CSV has an unexpected header.");
        }

        int matchingRow = -1;

        // Validate existing records and locate the pet being edited.
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                continue;
            }

            String[] fields = parseCsvLine(lines.get(i));

            if (fields.length != 11) {
                throw new IOException(
                        "Invalid pet record at row " + (i + 1));
            }

            if (fields[0].trim().equals(pet.getPetId())) {
                if (!fields[1].trim().equals(pet.getUserId())) {
                    throw new IOException("The pet's owner does not match.");
                }

                if (matchingRow != -1) {
                    throw new IOException("Duplicate pet ID in the CSV.");
                }

                matchingRow = i;
            }
        }

        if (matchingRow == -1) {
            throw new IOException("The selected pet could not be found.");
        }

        // Replace only the selected pet's record, keeping its IDs unchanged.
        String updatedRow = String.join(",",
                csvField(pet.getPetId()),
                csvField(pet.getUserId()),
                csvField(pet.getAnimalType()),
                csvField(pet.getName()),
                csvField(pet.getBreed()),
                Integer.toString(pet.getAgeYears()),
                Float.toString(pet.getWeightKg()),
                Integer.toString(pet.getDailyCalorieGoal()),
                Integer.toString(pet.getMealsPerDay()),
                csvField(pet.getMedication()),
                csvField(pet.getNotes()));

        lines.set(matchingRow, updatedRow);

        // Write a temporary file before replacing the original CSV.
        Path temporaryFile = Files.createTempFile(
                petsFile.toAbsolutePath().getParent(), "pets-", ".tmp");

        try {
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);

            try {
                // Replace the original in one operation when supported.
                Files.move(
                        temporaryFile,
                        petsFile.toAbsolutePath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                        java.nio.file.StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException exception) {
                // Use a regular replacement if atomic moves are unavailable.
                Files.move(
                        temporaryFile,
                        petsFile.toAbsolutePath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            // Remove the temporary file if it remains after a failed update.
            Files.deleteIfExists(temporaryFile);
        }
    }


}