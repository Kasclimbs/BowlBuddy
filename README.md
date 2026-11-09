# BowlBuddy

BowlBuddy is a JavaFX desktop application that helps pet owners manage pet profiles and feeding information.

Developed by **Team ByteMe** for **CS 3443 – Application Programming at UTSA**.

## Current Features

- Log in using accounts loaded from CSV.
- Validate missing or incorrect login information.
- Maintain the current user session and support logout.
- Display pets belonging to the logged-in user.
- View pet profiles, including feeding goals, medication, and notes.
- Add pets and save them to CSV.
- Edit pet information and persist changes.
- Reload saved pet information after restarting the application.

## Planned Features

- Remove pets with confirmation.
- Create accounts.
- View and edit user profiles.
- Browse and add recipes.
- Log meals and portions.
- Schedule feedings through a calendar.
- Track calories against each pet's daily goal.

## Technologies

- Java 21
- JavaFX 21.0.6
- Maven
- FXML
- CSV file storage
- IntelliJ IDEA

Dependencies are configured in `pom.xml`.

## Running the Application

1. Clone this repository.
2. Open the project folder containing `pom.xml` in IntelliJ IDEA.
3. Set the project SDK to **JDK 21**.
4. Load or reload the Maven project to download dependencies.
5. Confirm the project root contains:
   - `data/users.csv`
   - `data/pets.csv`
6. Run `Launcher.java` in:
   `edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto`

Set the run configuration's **working directory** to the project root—the folder containing `pom.xml` and `data`.

Use a sample account from `data/users.csv` to log in. New account registration is not implemented yet.

## Project Organization

- `model`: Application data classes, password utilities, and session state.
- `controller`: Handles user actions, input validation, and screen navigation.
- `data`: Contains `DataManager`, which handles CSV reading and writing.
- `src/main/resources`: Contains FXML views.
- Root-level `data`: Contains the application's CSV files.

The application follows the **Model–View–Controller (MVC)** pattern. CSV file operations are handled by `DataManager`.

## Data Files

### users.csv

Contains sample account information used for login.

### pets.csv

Contains pet information linked to owners through user IDs. Adding or editing a pet updates this file.

Use sample data only. Do not commit real account credentials or personal information.

## Known Limitations

- Features listed under Planned Features are not implemented yet.
- CSV files must have the expected headers and column order.
- Pet text fields support commas and escaped quotation marks, but not embedded line breaks.
- Sample accounts may use plaintext passwords in the source CSV; the loader hashes these in memory for verification.
- Multiple application instances writing to the same CSV file are not supported.

## Team Members

- Brian Caloca
- Add remaining team members here.

## Development Status

The current prototype supports login, pet browsing, pet profiles, and persistent pet creation and editing. Feeding management features are under development.
