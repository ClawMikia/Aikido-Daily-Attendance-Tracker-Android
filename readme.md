# Aikido Philippines Shusseki (出席)

**Aikido Philippines Shusseki** is a dedicated attendance tracking application designed for Aikido practitioners in the Philippines. "Shusseki" (出席) translates to "attendance" in Japanese, reflecting the martial art's heritage and the app's primary purpose.

## 🚀 Features

-   **One-Tap Attendance**: Quickly record your training session with a single click.
-   **Session History**: A clean, chronological list of all your training sessions.
-   **Payment Tracking**: Mark individual sessions as "Paid" or "Unpaid" to keep your dues in check.
-   **Member Management**: Customize the app with your name for personalized tracking.
-   **Data Portability**: 
    -   **Export**: Save your records as a JSON file to your device's Downloads folder for backup.
    -   **Import**: Restore your training history from a previously exported file.
-   **Live Counter**: Instantly see the total number of sessions you've completed.

## 🛠 Functionalities

-   **Local Storage**: Uses SQLite database (`DatabaseHelper`) to store all records securely on your device.
-   **JSON Serialization**: Utilizes GSON for robust data export and import operations.
-   **Real-time UI Updates**: The interface refreshes immediately upon adding or modifying records.
-   **Scoped Storage Support**: Implements modern Android MediaStore API for file exporting, ensuring compatibility with Android 10 (API 29) and above.

## 📱 User Interface (UI)

The app features a modern, dark-themed interface designed for clarity and ease of use during or after training:

-   **Toolbar**: Displays the app name and provides a consistent navigation anchor.
-   **Profile Section**: Located at the top, allowing users to set or edit their name.
-   **Action Buttons**:
    -   **SAVE**: Large, accessible button to log the current session.
    -   **EXPORT**: Backup data to local storage.
    -   **IMPORT**: Restore data from local storage.
-   **Session Counter**: A prominent text display showing "Total Sessions for [User]".
-   **Attendance List**: A scrollable `RecyclerView` showing:
    -   The date of the session.
    -   A toggle/indicator for payment status.
    -   Options to edit or delete individual entries.

## 📂 Project Structure

-   `MainActivity.kt`: Handles the main logic, UI interactions, and file I/O.
-   `UserAdapter.kt`: Manages the display and interactions within the attendance list.
-   `DatabaseHelper.java`: Manages the SQLite database, schema, and CRUD operations.
-   `activity_main.xml`: Defines the layout structure using Material Design components.

---
*Created for the Aikido community to help track their path on the Mat.*
