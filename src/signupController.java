import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class signupController {

    @FXML
    private Hyperlink LogIn;

    @FXML
    private PasswordField confPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    private Stage stage;
    private Scene scene;

    // Database connection details
    private String url = "jdbc:mysql://localhost:3306/witit";
    private String user = "root";
    private String dbPassword = "";

    @FXML
    void CreateBtn(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confPassword = confPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confPassword.isEmpty()) {
            showAlert(AlertType.ERROR, "Form Error!", "Please fill in all the fields");
            return;
        }

        if (!password.equals(confPassword)) {
            showAlert(AlertType.ERROR, "Password Mismatch", "Passwords do not match");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(AlertType.ERROR, "Invalid Email", "Please enter a valid email address");
            return;
        }

        String insertQuery = "INSERT INTO users (username, email, password, profile_picture) VALUES (?, ?, ?, NULL)";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(AlertType.INFORMATION, "Registration Successful", "User registered successfully");
                clearFields();
                redirectToLogin();
            } else {
                showAlert(AlertType.ERROR, "Registration Failed", "An error occurred during registration");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "An error occurred while connecting to the database");
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private void clearFields() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        confPasswordField.clear();
    }

    @FXML
    void LogInBtn(ActionEvent event) {
        redirectToLogin();
    }

    private void redirectToLogin() {
        try {
            // Load the login scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            // Show the login scene
            stage = (Stage) LogIn.getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Load Error", "An error occurred while loading the login page");
        }
    }
}
