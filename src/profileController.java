import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.util.Optional;

public class profileController {

        private int userId;

        public void setUserData(int userId) {
                this.userId = userId;
                displayProfilePicture();
                displayUserDetails();
        }

        @FXML
        private ImageView recordPage;

        @FXML
        private ImageView savingsPage;

        @FXML
        private ImageView dashboardPage;

        @FXML
        private ImageView debtPage;

        @FXML
        private ImageView walletPage;


        @FXML
        void handleDashboardPageClick() {
                try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                        Parent root = loader.load();

                        // Pass data to recordController if needed
                        dashboardController dashboardController = loader.getController();
                        dashboardController.setUserId(userId);

                        // Close current stage (dashboard stage)
                        Stage savingsStage = (Stage) dashboardPage.getScene().getWindow();
                        savingsStage.setScene(new Scene(root));

                } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to open dashboard page: " + e.getMessage());
                }
        }

        @FXML
        void handleRecordPageClick() {
                try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("record.fxml"));
                        Parent root = loader.load();

                        recordController recordController = loader.getController();
                        recordController.setUserData(userId);

                        Stage dashboardStage = (Stage) recordPage.getScene().getWindow();
                        dashboardStage.setScene(new Scene(root));

                } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to open record page: " + e.getMessage());
                }
        }

        @FXML
        void handleWalletPageClick() {
                try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("wallet.fxml"));
                        Parent root = loader.load();

                        walletController walletController = loader.getController();
                        walletController.setUserData(userId);

                        Stage dashboardStage = (Stage) walletPage.getScene().getWindow();
                        dashboardStage.setScene(new Scene(root));

                } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to open wallet page: " + e.getMessage());
                }
        }

        @FXML
        void handleSavingsPageClick() {
                try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("savings.fxml"));
                        Parent root = loader.load();

                        savingsController savingsController = loader.getController();
                        savingsController.setUserData(userId);

                        Stage dashboardStage = (Stage) savingsPage.getScene().getWindow();
                        dashboardStage.setScene(new Scene(root));

                } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to open savings page: " + e.getMessage());
                }
        }

        @FXML
        void handleDebtPageClick() {
                try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("debt.fxml"));
                        Parent root = loader.load();

                        debtController debtController = loader.getController();
                        debtController.setUserData(userId);

                        Stage dashboardStage = (Stage) debtPage.getScene().getWindow();
                        dashboardStage.setScene(new Scene(root));

                } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to open debt page: " + e.getMessage());
                }
        }

        private void showAlert(Alert.AlertType alertType, String title, String message) {
                Alert alert = new Alert(alertType);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
        }



        @FXML
        private Label bio;

        @FXML
        private Button completeButton;

        @FXML
        private Button deleteButton;

        @FXML
        private Label email;

        @FXML
        private Button logOut;

        @FXML
        private ImageView profileIcon;

        @FXML
        private ImageView profilePicture;

        @FXML
        private Button submitBtn;

        @FXML
        private Label userName;


        @FXML
        private Label usernameField;

        private void displayUserDetails() {
                String url = "jdbc:mysql://localhost:3306/witit";
                String user = "root";
                String password = "";
                String query = "SELECT username, email, bio, password FROM users WHERE id = ?";

                try (Connection conn = DriverManager.getConnection(url, user, password);
                     PreparedStatement pstmt = conn.prepareStatement(query)) {

                        pstmt.setInt(1, userId);
                        ResultSet rs = pstmt.executeQuery();

                        if (rs.next()) {
                                String username = rs.getString("username");
                                String emailText = rs.getString("email");
                                String bioText = rs.getString("bio").toUpperCase();

                                usernameField.setText(username);
                                userName.setText(username);
                                email.setText(emailText);
                                bio.setText(bioText);
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to load user details: " + e.getMessage());
                }
        }

        private void displayProfilePicture() {
                String url = "jdbc:mysql://localhost:3306/witit";
                String user = "root";
                String password = "";
                String query = "SELECT profile_picture FROM users WHERE id = ?";

                try (Connection conn = DriverManager.getConnection(url, user, password);
                     PreparedStatement pstmt = conn.prepareStatement(query)) {

                        pstmt.setInt(1, userId);
                        ResultSet rs = pstmt.executeQuery();

                        if (rs.next()) {
                                byte[] imageBytes = rs.getBytes("profile_picture");
                                if (imageBytes != null && imageBytes.length > 0) {
                                        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                                        Image image = new Image(bis);
                                        profilePicture.setImage(image);
                                        profileIcon.setImage(image);
                                }
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to load profile picture: " + e.getMessage());
                }
        }

        @FXML
        void changeProfilePicture(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
                File file = fileChooser.showOpenDialog(null);
                if (file != null) {
                        long fileSizeInBytes = file.length();
                        long maxFileSizeInBytes = 4L * 1024 * 1024 * 1024; // 4GB

                        if (fileSizeInBytes > maxFileSizeInBytes) {
                                showAlert(Alert.AlertType.ERROR, "File Size Error", "The selected file exceeds the 4GB size limit.");
                                return;
                        }

                        try (FileInputStream fis = new FileInputStream(file)) {
                                byte[] imageBytes = fis.readAllBytes();
                                saveProfilePictureToDatabase(imageBytes);
                                displayProfilePicture();
                        } catch (IOException e) {
                                e.printStackTrace();
                                showAlert(Alert.AlertType.ERROR, "Error", "Failed to read the image file: " + e.getMessage());
                        }
                }
        }

        private void saveProfilePictureToDatabase(byte[] imageBytes) {
                String url = "jdbc:mysql://localhost:3306/witit";
                String user = "root";
                String password = "";

                String query = "UPDATE users SET profile_picture = ? WHERE id = ?";

                try (Connection conn = DriverManager.getConnection(url, user, password);
                     PreparedStatement pstmt = conn.prepareStatement(query)) {

                        pstmt.setBytes(1, imageBytes);
                        pstmt.setInt(2, userId);
                        pstmt.executeUpdate();

                } catch (SQLException e) {
                        if (e instanceof com.mysql.cj.jdbc.exceptions.PacketTooBigException) {
                                showAlert(Alert.AlertType.ERROR, "File Size Error", "The selected file exceeds the maximum allowed packet size in MySQL. Please choose a smaller file.");
                        } else {
                                e.printStackTrace();
                                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save the profile picture to the database: " + e.getMessage());
                        }
                }
        }

        private String url = "jdbc:mysql://localhost:3306/witit";
        private String user = "root";
        private String password = "";

        private void deleteDependentRecords(int userId) throws SQLException {
                String deleteDebtsQuery = "DELETE FROM debts WHERE user_id = ?";
                String deleteExpensesQuery = "DELETE FROM expense WHERE user_id = ?";
                String deleteTransactionHistoryQuery = "DELETE FROM transaction_history WHERE user_id = ?";
                String deleteTransactionsQuery = "DELETE FROM transactions WHERE user_id = ?";
                String deleteSavingsQuery = "DELETE FROM savings_goals WHERE user_id = ?";

                try (Connection conn = DriverManager.getConnection(url, user, password);
                     PreparedStatement pstmtDebts = conn.prepareStatement(deleteDebtsQuery);
                     PreparedStatement pstmtExpenses = conn.prepareStatement(deleteExpensesQuery);
                     PreparedStatement pstmtTransactionHistory = conn.prepareStatement(deleteTransactionHistoryQuery);
                     PreparedStatement pstmtTransactions = conn.prepareStatement(deleteTransactionsQuery);
                     PreparedStatement pstmtSavings = conn.prepareStatement(deleteSavingsQuery)) {

                        // Delete debts
                        pstmtDebts.setInt(1, userId);
                        pstmtDebts.executeUpdate();

                        // Delete expenses
                        pstmtExpenses.setInt(1, userId);
                        pstmtExpenses.executeUpdate();

                        // Delete wallet
                        pstmtTransactionHistory.setInt(1, userId);
                        pstmtTransactionHistory.executeUpdate();

                        // Delete transactions
                        pstmtTransactions.setInt(1, userId);
                        pstmtTransactions.executeUpdate();

                        // Delete savings
                        pstmtSavings.setInt(1, userId);
                        pstmtSavings.executeUpdate();


                }
        }

        @FXML
        void deleteAccount(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Delete Account");
                alert.setHeaderText("Are you sure you want to delete your account?");
                alert.setContentText("This action cannot be undone.");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                                // Delete dependent records first
                                deleteDependentRecords(userId);

                                // Proceed with account deletion
                                String deleteQuery = "DELETE FROM users WHERE id = ?";
                                try (Connection conn = DriverManager.getConnection(url, user, password);
                                     PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

                                        pstmt.setInt(1, userId);
                                        pstmt.executeUpdate();

                                        // Show success message
                                        showAlert(Alert.AlertType.INFORMATION, "Account Deleted", "Your account has been deleted.");

                                        // Close the application after deletion or redirect to a login screen
                                        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                                        stage.close(); // Close the window after deleting the account

                                        // Open login.fxml
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
                                        Parent root = loader.load();
                                        Stage loginStage = new Stage();
                                        loginStage.setTitle("Login");
                                        loginStage.setScene(new Scene(root));
                                        loginStage.show();

                                } catch (SQLException e) {
                                        e.printStackTrace();
                                        showAlert(Alert.AlertType.ERROR, "Delete Account Error", "Failed to delete account: " + e.getMessage());
                                }
                        } catch (SQLException e) {
                                e.printStackTrace();
                                showAlert(Alert.AlertType.ERROR, "Delete Account Error", "Failed to delete dependent records: " + e.getMessage());
                        } catch (IOException e) {
                                e.printStackTrace();
                                showAlert(Alert.AlertType.ERROR, "FXML Error", "Failed to load login screen: " + e.getMessage());
                        }
                }
        }


        @FXML
        void logOut(ActionEvent event) {
                // Close the current window (profile window) and open a login window
                Stage stage = (Stage) logOut.getScene().getWindow();
                stage.close(); // Close the current window

                // Open a new login window
                // Example code to open a login window
                try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
                        Parent root = loader.load();

                        Stage loginStage = new Stage();
                        loginStage.setScene(new Scene(root));
                        loginStage.show();

                } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Logout Error", "Failed to logout: " + e.getMessage());
                }
        }

        @FXML
        private UpdateController updateController;

        public void setUpdateController(UpdateController updateController) {
                this.updateController = updateController;
        }

        // Add a method to refresh profile details
        public void refreshProfile() {
                displayUserDetails();
                displayProfilePicture();
        }

        @FXML
        void update(ActionEvent event) {
                try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("update.fxml"));
                        Parent root = loader.load();

                        UpdateController updateController = loader.getController();
                        updateController.setUserData(userId);
                        updateController.setProfileController(this); // Set profileController instance

                        Stage updateStage = new Stage();
                        updateStage.setTitle("Update Details");
                        updateStage.setScene(new Scene(root));
                        updateStage.show();

                } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to open update page: " + e.getMessage());
                }
        }

        @FXML
        void changePassword(ActionEvent event) {
                try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("password.fxml"));
                        Parent root = loader.load();

                        changePassController changePassController = loader.getController();
                        changePassController.setUserData(userId);

                        Stage updateStage = new Stage();
                        updateStage.setTitle("Change Password");
                        updateStage.setScene(new Scene(root));
                        updateStage.show();

                } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to open update page: " + e.getMessage());
                }
        }




}
