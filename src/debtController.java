import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import java.io.IOException;


public class debtController {

    @FXML
    private Label userName;

    @FXML
    private ImageView profileIcon;

    private int userId;

    public void setUserData(int userId) {
        this.userId = userId;
        setUserNameAndProfile();
    }

    private void setUserNameAndProfile() {
        String url = "jdbc:mysql://localhost:3306/witit";
        String user = "root";
        String password = "";

        String query = "SELECT username, profile_picture FROM users WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String username = resultSet.getString("username");
                userName.setText(username);

                // Assuming profile_picture is stored as BLOB and you have a method to set an ImageView
                byte[] profilePictureBytes = resultSet.getBytes("profile_picture");
                if (profilePictureBytes != null) {
                    ByteArrayInputStream bis = new ByteArrayInputStream(profilePictureBytes);
                    Image image = new Image(bis);
                    profileIcon.setImage(image);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch username and profile picture: " + e.getMessage());
        }
    }

    @FXML
    private ImageView dashboardPage;

    @FXML
    private ImageView walletPage;

    @FXML
    private ImageView recordPage;

    @FXML
    private ImageView savingsPage;

    @FXML
    private ImageView profilePage;

    @FXML
    void handleProfilePageClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile.fxml"));
            Parent root = loader.load();

            profileController profileController = loader.getController();
            profileController.setUserData(userId);

            Stage debtStage = (Stage) profilePage.getScene().getWindow();
            debtStage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open profile page: " + e.getMessage());
        }
    }

    @FXML
    void handleDashboardPageClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Parent root = loader.load();

            // Pass data to recordController if needed
            dashboardController dashboardController = loader.getController();
            dashboardController.setUserId(userId);

            // Close current stage (dashboard stage)
            Stage debtStage = (Stage) dashboardPage.getScene().getWindow();
            debtStage.setScene(new Scene(root));


        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open dashboard page: " + e.getMessage());
        }
    }

    @FXML
    void handleRecordPageClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("record.fxml"));
            Parent root = loader.load();

            // Pass data to recordController if needed
            recordController recordController = loader.getController();
            recordController.setUserData(userId);

            // Close current stage (dashboard stage)
            Stage debtStage = (Stage) recordPage.getScene().getWindow();
            debtStage.setScene(new Scene(root));


        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open record page: " + e.getMessage());
        }
    }

    @FXML
    void handleSavingsPageClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("savings.fxml"));
            Parent root = loader.load();

            // Pass data to recordController if needed
            savingsController savingsController = loader.getController();
            savingsController.setUserData(userId);

            // Close current stage (dashboard stage)
            Stage debtStage = (Stage) savingsPage.getScene().getWindow();
            debtStage.setScene(new Scene(root));


        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open savings page: " + e.getMessage());
        }
    }

    @FXML
    void handleWalletPageClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("wallet.fxml"));
            Parent root = loader.load();

            // Pass data to recordController if needed
            walletController walletController = loader.getController();
            walletController.setUserData(userId);

            // Close current stage (dashboard stage)
            Stage debtStage = (Stage) walletPage.getScene().getWindow();
            debtStage.setScene(new Scene(root));


        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open wallet page: " + e.getMessage());
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
    private ComboBox<String> selectAccountBorrow;

    @FXML
    private ComboBox<String> selectAccountLend;

    @FXML
    private ComboBox<String> selectAccountPayment;

    @FXML
    public void initialize() {
        selectAccountBorrow.getItems().addAll("Cash", "Gcash");
        selectAccountLend.getItems().addAll("Cash", "Gcash");
        selectAccountPayment.getItems().addAll("Cash", "Gcash");

        // Initialize chooseDebt options
        chooseDebt.getItems().addAll("Borrow", "Lend");
        chooseDebt.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateDebtNames(newValue);
        });

        // Add listener to chooseDebtName ComboBox
        chooseDebtName.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateDebtRemainingBalance(chooseDebt.getValue(), newValue); // Update remaining balance based on selection
        });

        // Numeric input validation for amounts
        lentAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                lentAmount.setText(oldValue);
            }
        });

        borrowedAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                borrowedAmount.setText(oldValue);
            }
        });

        debtPayAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                debtPayAmount.setText(oldValue);
            }
        });

        // Text input validation for names
        lendTo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z\\s]*")) {
                lendTo.setText(oldValue);
            }
        });

        borrowFrom.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z\\s]*")) {
                borrowFrom.setText(oldValue);
            }
        });

    }

    private void updateDebtNames(String debtType) {
        chooseDebtName.getItems().clear(); // Clear existing items

        // Database connection parameters
        String dbUrl = "jdbc:mysql://localhost:3306/witit";
        String dbUser = "root";
        String dbPassword = "";

        // SQL query to fetch debt names based on debt type
        String selectDebtNamesQuery = "SELECT debt_name FROM debts WHERE user_id = ? AND debt_type = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(selectDebtNamesQuery)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, debtType);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String debtName = rs.getString("debt_name");
                chooseDebtName.getItems().add(debtName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch debt names: " + e.getMessage());
        }
    }

    // Method to update debtRemainingBalance based on selected debtType and debtName
    private void updateDebtRemainingBalance(String debtType, String debtName) {

        String dbUrl = "jdbc:mysql://localhost:3306/witit";
        String dbUser = "root";
        String dbPassword = "";

        // SQL query to fetch remaining balance based on debt type and name
        String selectRemainingBalanceQuery = "SELECT remaining_balance FROM debts WHERE user_id = ? AND debt_type = ? AND debt_name = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(selectRemainingBalanceQuery)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, debtType);
            pstmt.setString(3, debtName);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double remainingBalance = rs.getDouble("remaining_balance");
                debtRemainingBalance.setText(String.format("%.2f", remainingBalance));
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Debt not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch remaining balance: " + e.getMessage());
        }
    }

    @FXML
    private TextField lendTo;

    @FXML
    private TextField lentAmount;

    @FXML
    void saveLend(ActionEvent event) {
        String lendName = lendTo.getText();
        String lendAmtStr = lentAmount.getText();
        String accountType = selectAccountLend.getValue();

        // Validate input
        if (lendName.isEmpty() || lendAmtStr.isEmpty() || accountType == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        double lendAmt;
        try {
            lendAmt = Double.parseDouble(lendAmtStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid amount entered.");
            return;
        }

        // Database connection parameters
        String dbUrl = "jdbc:mysql://localhost:3306/witit";
        String dbUser = "root";
        String dbPassword = "";

        // SQL query to insert into debts table
        String insertDebtQuery = "INSERT INTO debts (user_id, debt_type, debt_name, account_type, amount, remaining_balance, date) VALUES (?, ?, ?, ?, ?, ?, NOW())";

        // SQL queries to update total_cash or total_gcash
        String updateTotalCashQuery = "UPDATE users SET total_cash = total_cash - ? WHERE id = ?";
        String updateTotalGcashQuery = "UPDATE users SET total_gcash = total_gcash - ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmtDebt = conn.prepareStatement(insertDebtQuery);
             PreparedStatement pstmtUpdate = (accountType.equals("Cash")) ? conn.prepareStatement(updateTotalCashQuery) : conn.prepareStatement(updateTotalGcashQuery)) {

            conn.setAutoCommit(false); // Begin transaction

            // Insert into debts table
            pstmtDebt.setInt(1, userId);
            pstmtDebt.setString(2, "Lend");
            pstmtDebt.setString(3, lendName);
            pstmtDebt.setString(4, accountType);
            pstmtDebt.setDouble(5, lendAmt);
            pstmtDebt.setDouble(6, lendAmt); // Assuming remaining balance starts as the lent amount
            pstmtDebt.executeUpdate();

            // Update total_cash or total_gcash
            pstmtUpdate.setDouble(1, lendAmt);
            pstmtUpdate.setInt(2, userId);
            pstmtUpdate.executeUpdate();

            insertTransaction(conn, "Lend", accountType, lendAmt);


            conn.commit(); // Commit transaction

            showAlert(Alert.AlertType.INFORMATION, "Success", "Lend recorded successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to record lend: " + e.getMessage());
        }
    }


    @FXML
    private TextField borrowFrom;

    @FXML
    private TextField borrowedAmount;

    @FXML
    void saveBorrow(ActionEvent event) {
        String borrowName = borrowFrom.getText();
        String borrowAmtStr = borrowedAmount.getText();
        String accountType = selectAccountBorrow.getValue();

        // Validate input
        if (borrowName.isEmpty() || borrowAmtStr.isEmpty() || accountType == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        double borrowAmt;
        try {
            borrowAmt = Double.parseDouble(borrowAmtStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid amount entered.");
            return;
        }

        // Database connection parameters
        String dbUrl = "jdbc:mysql://localhost:3306/witit";
        String dbUser = "root";
        String dbPassword = "";

        // SQL query to insert into debts table
        String insertDebtQuery = "INSERT INTO debts (user_id, debt_type, debt_name, account_type, amount, remaining_balance, date) VALUES (?, ?, ?, ?, ?, ?, NOW())";

        // SQL queries to update total_cash or total_gcash
        String updateTotalCashQuery = "UPDATE users SET total_cash = total_cash + ? WHERE id = ?";
        String updateTotalGcashQuery = "UPDATE users SET total_gcash = total_gcash + ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmtDebt = conn.prepareStatement(insertDebtQuery);
             PreparedStatement pstmtUpdate = (accountType.equals("Cash")) ? conn.prepareStatement(updateTotalCashQuery) : conn.prepareStatement(updateTotalGcashQuery)) {

            conn.setAutoCommit(false); // Begin transaction

            // Insert into debts table
            pstmtDebt.setInt(1, userId);
            pstmtDebt.setString(2, "Borrow");
            pstmtDebt.setString(3, borrowName);
            pstmtDebt.setString(4, accountType);
            pstmtDebt.setDouble(5, borrowAmt);
            pstmtDebt.setDouble(6, borrowAmt); // Assuming remaining balance starts as the borrowed amount
            pstmtDebt.executeUpdate();

            // Update total_cash or total_gcash
            pstmtUpdate.setDouble(1, borrowAmt);
            pstmtUpdate.setInt(2, userId);
            pstmtUpdate.executeUpdate();

            insertTransaction(conn, "Borrow", accountType, borrowAmt);

            conn.commit(); // Commit transaction

            showAlert(Alert.AlertType.INFORMATION, "Success", "Borrow recorded successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to record borrow: " + e.getMessage());
        }
    }
    private void insertTransaction(Connection connection, String transactionType, String accountType, double amount) throws SQLException {
        String insertQuery = "INSERT INTO transactions (user_id, transaction_type, account_type, amount, transaction_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, transactionType);
            preparedStatement.setString(3, accountType);
            preparedStatement.setDouble(4, amount);
            preparedStatement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.executeUpdate();
        }
    }




    @FXML
    private ComboBox<String> chooseDebt;

    @FXML
    private ComboBox<String> chooseDebtName;

    private List<String> borrowNames = new ArrayList<>();
    private List<String> lendNames = new ArrayList<>();


    @FXML
    private Label debtRemainingBalance;

    @FXML
    private TextField debtPayAmount;

    @FXML
    private Button debtPaid;

    @FXML
    private Button deleteDebt;


    @FXML
    void debtPaid(ActionEvent event) {
        String debtType = chooseDebt.getValue();
        String debtName = chooseDebtName.getValue();
        String paymentAmtStr = debtPayAmount.getText();
        String accountType = selectAccountPayment.getValue();

        // Validate input
        if (debtType == null || debtName == null || paymentAmtStr.isEmpty() || accountType == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select debt type, debt name, account type, and enter payment amount.");
            return;
        }

        double paymentAmt;
        try {
            paymentAmt = Double.parseDouble(paymentAmtStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid payment amount entered.");
            return;
        }

        // Database connection parameters
        String dbUrl = "jdbc:mysql://localhost:3306/witit";
        String dbUser = "root";
        String dbPassword = "";

        // SQL query to update remaining balance in debts table
        String updateRemainingBalanceQuery = "UPDATE debts SET remaining_balance = remaining_balance - ? WHERE user_id = ? AND debt_type = ? AND debt_name = ?";

        // SQL query to fetch remaining balance after update
        String selectRemainingBalanceQuery = "SELECT remaining_balance FROM debts WHERE user_id = ? AND debt_type = ? AND debt_name = ?";

        // SQL queries to update total_cash or total_gcash in users table based on account type
        String updateTotalCashQuery = "UPDATE users SET total_cash = total_cash + ? WHERE id = ?";
        String updateTotalGcashQuery = "UPDATE users SET total_gcash = total_gcash + ? WHERE id = ?";

        // SQL query to delete record if remaining balance is 0
        String deleteDebtQuery = "DELETE FROM debts WHERE user_id = ? AND debt_type = ? AND debt_name = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmtUpdateBalance = conn.prepareStatement(updateRemainingBalanceQuery);
             PreparedStatement pstmtSelectBalance = conn.prepareStatement(selectRemainingBalanceQuery);
             PreparedStatement pstmtUpdateTotal = (accountType.equals("Cash")) ? conn.prepareStatement(updateTotalCashQuery) : conn.prepareStatement(updateTotalGcashQuery);
             PreparedStatement pstmtDeleteDebt = conn.prepareStatement(deleteDebtQuery)) {

            conn.setAutoCommit(false); // Begin transaction

            // Update remaining balance in debts table
            pstmtUpdateBalance.setDouble(1, paymentAmt);
            pstmtUpdateBalance.setInt(2, userId);
            pstmtUpdateBalance.setString(3, debtType);
            pstmtUpdateBalance.setString(4, debtName);
            pstmtUpdateBalance.executeUpdate();

            // Fetch remaining balance after update
            pstmtSelectBalance.setInt(1, userId);
            pstmtSelectBalance.setString(2, debtType);
            pstmtSelectBalance.setString(3, debtName);
            ResultSet rs = pstmtSelectBalance.executeQuery();
            if (rs.next()) {
                double remainingBalance = rs.getDouble("remaining_balance");

                // Update total_cash or total_gcash in users table
                pstmtUpdateTotal.setDouble(1, paymentAmt);
                pstmtUpdateTotal.setInt(2, userId);
                pstmtUpdateTotal.executeUpdate();

                // Check if remaining balance is 0 and delete record if true
                if (remainingBalance <= 0) {
                    pstmtDeleteDebt.setInt(1, userId);
                    pstmtDeleteDebt.setString(2, debtType);
                    pstmtDeleteDebt.setString(3, debtName);
                    pstmtDeleteDebt.executeUpdate();

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Debt fully paid.");
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Payment recorded successfully.");
                }

                // Update UI with remaining balance
                debtRemainingBalance.setText(String.format("%.2f", remainingBalance));
            }

            insertTransaction(conn, "Debt Payment", accountType, paymentAmt);

            conn.commit(); // Commit transaction

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to record payment: " + e.getMessage());
        }
    }


}
