import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class changePassController {

    @FXML
    private PasswordField currentPass;

    @FXML
    private PasswordField newPass;

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

    @FXML
    void changePass(ActionEvent event) {
        String currentPassword = currentPass.getText();
        String newPassword = newPass.getText();

        try {
            Connection connection = DriverManager.getConnection(url, user, password);

            // Query to retrieve the current password from the database
            String selectQuery = "SELECT password FROM users WHERE id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setInt(1, userId);

            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");

                // Check if current password matches the stored password
                if (storedPassword.equals(currentPassword)) {
                    // Update the password in the database
                    String updateQuery = "UPDATE users SET password = ? WHERE id = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setString(1, newPassword);
                    updateStatement.setInt(2, userId);

                    int rowsAffected = updateStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        // Password updated successfully
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Password Updated");
                        alert.setHeaderText(null);
                        alert.setContentText("Your password has been successfully updated.");
                        alert.showAndWait();

                        // Optionally, you can reset the fields after successful update
                        currentPass.clear();
                        newPass.clear();


                    } else {
                        // Handle failure to update password
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Password Update Failed");
                        alert.setHeaderText(null);
                        alert.setContentText("Failed to update your password. Please try again later.");
                        alert.showAndWait();
                    }
                } else {
                    // Passwords do not match
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Incorrect Password");
                    alert.setHeaderText(null);
                    alert.setContentText("Current password entered is incorrect. Please try again.");
                    alert.showAndWait();
                }
            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exception
        }
    }
}
