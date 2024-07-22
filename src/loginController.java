import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.scene.control.Hyperlink;
import java.sql.SQLException;

public class loginController {

    @FXML
    private TextField emailFieldLogIn;

    @FXML
    private Hyperlink SignUp;

    @FXML
    private PasswordField passwordFieldLogIn;

    private Stage stage;
    private Scene scene;

    @FXML
    void LogInBtn(ActionEvent event) {
        String email = emailFieldLogIn.getText();
        String password = passwordFieldLogIn.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Form Error!", "Please fill in all the fields");
            return;
        }

        // Database connection details
        String url = "jdbc:mysql://localhost:3306/witit";
        String user = "root";
        String dbPassword = "";

        String selectQuery = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id"); // Assuming 'id' is the column name for userId

                // Load the dashboard scene
                FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                Parent root = loader.load();

                // Get the controller instance
                dashboardController dashboardController = loader.getController();

                // Pass the username to the dashboard controller
                dashboardController.setUsername(rs.getString("username"));
                dashboardController.setUserId(userId);

                // Show the dashboard scene
                stage = (Stage) emailFieldLogIn.getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } else {
                showAlert(AlertType.ERROR, "Login Failed", "Invalid email or password");
            }

        } catch (SQLException | IOException e) {
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

    @FXML
    void SignUpBtn(ActionEvent event) {
        try {
            // Load the signup scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("signup.fxml"));
            Parent root = loader.load();

            // Show the signup scene
            stage = (Stage) SignUp.getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Load Error", "An error occurred while loading the sign-up page");
        }
    }

}
