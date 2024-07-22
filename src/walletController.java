import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class walletController {
    @FXML
    private Label userName;

    @FXML
    private ImageView profileIcon;

    private int userId;

    public void setUserData(int userId) {
        this.userId = userId;
        loadTransactionHistory();
        updateTotalLabels(); // Call updateTotalLabels when setting user data
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
    private ImageView savingsPage;

    @FXML
    private ImageView debtPage;

    @FXML
    private ImageView recordPage;

    @FXML
    private ImageView profilePage;



    @FXML
    void handleProfilePageClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile.fxml"));
            Parent root = loader.load();

            profileController profileController = loader.getController();
            profileController.setUserData(userId);

            Stage walletStage = (Stage) profilePage.getScene().getWindow();
            walletStage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open profile page: " + e.getMessage());
        }
    }

    @FXML
    void handleDashboardPageClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Parent root = loader.load();

            // Pass data to recordController if needed
            dashboardController dashboardController = loader.getController();
            dashboardController.setUserId(userId);

            // Close current stage (dashboard stage)
            Stage walletStage = (Stage) dashboardPage.getScene().getWindow();
            walletStage.setScene(new Scene(root));


        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open dashboard page: " + e.getMessage());
        }
    }

    @FXML
    void handleSavingsPageClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("savings.fxml"));
            Parent root = loader.load();

            // Pass data to recordController if needed
            savingsController savingsController = loader.getController();
            savingsController.setUserData(userId);

            // Close current stage (dashboard stage)
            Stage walletStage = (Stage) savingsPage.getScene().getWindow();
            walletStage.setScene(new Scene(root));


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

            // Pass data to recordController if needed
            debtController debtController = loader.getController();
            debtController.setUserData(userId);

            // Close current stage (dashboard stage)
            Stage walletStage = (Stage) debtPage.getScene().getWindow();
            walletStage.setScene(new Scene(root));


        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open debt page: " + e.getMessage());
        }
    }

    @FXML
    void handleRecordPageClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("record.fxml"));
            Parent root = loader.load();

            // Pass data to recordController if needed
            recordController recordController = loader.getController();
            recordController.setUserData(userId);

            // Close current stage (dashboard stage)
            Stage walletStage = (Stage) recordPage.getScene().getWindow();
            walletStage.setScene(new Scene(root));


        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open savings page: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private TextField amount;

    @FXML
    private TableView<Transaction> incomeTable;

    @FXML
    private TableColumn<Transaction, String> accountTypeColumn;

    @FXML
    private TableColumn<Transaction, Double> amountColumn;

    @FXML
    private TableColumn<Transaction, String> transactionTimeColumn;

    @FXML
    private Label totalCash;

    @FXML
    private Label totalGcash;

    @FXML
    private ComboBox<String> typeOfAccount;

    @FXML
    public void initialize() {
        typeOfAccount.getItems().addAll("Cash", "Gcash");

        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        transactionTimeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionTime"));

        amount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                amount.setText(oldValue);
            }
        });
    }

    @FXML
    void addCashIn(ActionEvent event) {
        String selectedAccount = typeOfAccount.getValue();
        String amountText = amount.getText();
        double amountValue;

        // Validate the input
        try {
            amountValue = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid amount.");
            return;
        }

        if (selectedAccount == null) {
            showAlert("Invalid Input", "Please select a type of account.");
            return;
        }

        // Database connection variables
        String url = "jdbc:mysql://localhost:3306/witit";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String updateQuery = "";
            if (selectedAccount.equals("Cash")) {
                updateQuery = "UPDATE users SET total_cash = total_cash + ? WHERE id = ?";
            } else if (selectedAccount.equals("Gcash")) {
                updateQuery = "UPDATE users SET total_gcash = total_gcash + ? WHERE id = ?";
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setDouble(1, amountValue);
                preparedStatement.setInt(2, userId);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    // Insert transaction into history table
                    insertTransactionHistory(connection, selectedAccount, amountValue);

                    // Insert transaction into transactions table
                    insertTransaction(connection, "Income", selectedAccount, amountValue);

                    // Update labels
                    updateTotalLabels();

                    // Reload transaction history
                    loadTransactionHistory();

                    showAlert("Success", "Amount added successfully.");
                } else {
                    showAlert("Error", "Failed to add amount.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while connecting to the database.");
        }
    }

    private void insertTransactionHistory(Connection connection, String accountType, double amount) throws SQLException {
        String insertQuery = "INSERT INTO transaction_history (user_id, account_type, amount) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, accountType);
            preparedStatement.setDouble(3, amount);
            preparedStatement.executeUpdate();
        }
    }

    private void updateTotalLabels() {
        // Database connection variables
        String url = "jdbc:mysql://localhost:3306/witit";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT total_cash, total_gcash FROM users WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        double totalCashValue = resultSet.getDouble("total_cash");
                        double totalGcashValue = resultSet.getDouble("total_gcash");
                        totalCash.setText(String.format("%.2f", totalCashValue));
                        totalGcash.setText(String.format("%.2f", totalGcashValue));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTransactionHistory() {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        // Database connection variables
        String url = "jdbc:mysql://localhost:3306/witit";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT account_type, amount, transaction_time FROM transaction_history WHERE user_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String accountType = resultSet.getString("account_type");
                        double amount = resultSet.getDouble("amount");
                        String transactionTime = resultSet.getString("transaction_time");
                        transactions.add(new Transaction(accountType, amount, transactionTime));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        incomeTable.setItems(transactions);
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Transaction class for the TableView
    public static class Transaction {
        private final String accountType;
        private final double amount;
        private final String transactionTime;

        public Transaction(String accountType, double amount, String transactionTime) {
            this.accountType = accountType;
            this.amount = amount;
            this.transactionTime = transactionTime;
        }

        public String getAccountType() {
            return accountType;
        }

        public double getAmount() {
            return amount;
        }

        public String getTransactionTime() {
            return transactionTime;
        }
    }
}
