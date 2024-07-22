import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateController {

    @FXML
    private TextArea newBio;

    @FXML
    private TextField newEmail;

    @FXML
    private TextField newUsername;

    @FXML
    private Button submitBtn;

    // URL, username, and password for your database connection
    private static final String url = "jdbc:mysql://localhost:3306/witit";
    private static final String user = "root";
    private static final String password = "";

    // User ID (assuming it's set or retrieved somewhere in your application)
    private int userId;

    public void setUserData(int userId) {
        this.userId = userId;
    }

    private profileController profileController;

    public void setProfileController(profileController profileController) {
        this.profileController = profileController;
    }

    @FXML
    void saveDetails(ActionEvent event) {
        String bio = newBio.getText().trim();
        String email = newEmail.getText().trim();
        String username = newUsername.getText().trim();

        // Check if any of the fields are empty
        if (bio.isEmpty() && email.isEmpty() && username.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Changes", "No changes detected. Please enter details to update.");
            return;
        }

        StringBuilder updateQuery = new StringBuilder("UPDATE users SET ");
        boolean hasSet = false;

        if (!bio.isEmpty()) {
            updateQuery.append("bio = ?, ");
            hasSet = true;
        }
        if (!email.isEmpty()) {
            updateQuery.append("email = ?, ");
            hasSet = true;
        }
        if (!username.isEmpty()) {
            updateQuery.append("username = ?, ");
            hasSet = true;
        }

        // Remove the trailing ", " and complete the query
        if (hasSet) {
            updateQuery.setLength(updateQuery.length() - 2); // Remove the last ", "
            updateQuery.append(" WHERE id = ?");
        } else {
            showAlert(Alert.AlertType.WARNING, "No Changes", "No changes detected. Please enter details to update.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(updateQuery.toString())) {

            int parameterIndex = 1;
            if (!bio.isEmpty()) {
                pstmt.setString(parameterIndex++, bio);
            }
            if (!email.isEmpty()) {
                pstmt.setString(parameterIndex++, email);
            }
            if (!username.isEmpty()) {
                pstmt.setString(parameterIndex++, username);
            }
            pstmt.setInt(parameterIndex, userId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Update Successful", "Profile details updated successfully.");
                // Notify profileController of the update
                profileController.refreshProfile();
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update profile details.");
                // Handle the case where update failed, show error message or take appropriate action
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error updating profile details: " + e.getMessage());
            // Handle SQL exception (show error message, log, etc.)
        }
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}
