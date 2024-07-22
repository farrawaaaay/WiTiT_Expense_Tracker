import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class recordController {

    private int userId;

    @FXML
    private ComboBox<String> categories;

    @FXML
    private ComboBox<String> walletType;

    @FXML
    private TextField amount;

    @FXML
    private TextArea description;

    @FXML
    private ImageView dashboardPage;

    @FXML
    private ImageView walletPage;

    @FXML
    private ImageView savingsPage;

    @FXML
    private ImageView debtPage;

    @FXML
    private ImageView profilePage;

    @FXML
    void handleDashboardPageClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Parent root = loader.load();

            // Pass data to recordController if needed
            dashboardController dashboardController = loader.getController();
            dashboardController.setUserId(userId);

            // Close current stage (dashboard stage)
            Stage recordStage = (Stage) dashboardPage.getScene().getWindow();
            recordStage.setScene(new Scene(root));

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

            // Pass data to recordController if needed
            walletController walletController = loader.getController();
            walletController.setUserData(userId);

            // Close current stage (dashboard stage)
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

            // Pass data to recordController if needed
            savingsController savingsController = loader.getController();
            savingsController.setUserData(userId);

            // Close current stage (dashboard stage)
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

            // Pass data to recordController if needed
            debtController debtController = loader.getController();
            debtController.setUserData(userId);

            // Close current stage (dashboard stage)
            Stage dashboardStage = (Stage) debtPage.getScene().getWindow();
            dashboardStage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open savings page: " + e.getMessage());
        }
    }

    @FXML
    void handleProfilePageClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile.fxml"));
            Parent root = loader.load();

            profileController profileController = loader.getController();
            profileController.setUserData(userId);

            Stage recordStage = (Stage) profilePage.getScene().getWindow();
            recordStage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open profile page: " + e.getMessage());
        }
    }

    @FXML
    private TableView<Expense> expenseTable; // TableView to display expenses

    // ObservableList to hold expenses
    private ObservableList<Expense> expenseList = FXCollections.observableArrayList();

    @FXML
    private TableColumn<Expense, String> AmountClmn;

    @FXML
    private TableColumn<Expense, String> CategoryClmn;

    @FXML
    private TableColumn<Expense, String> DateClmn;

    @FXML
    private TableColumn<Expense, String> DescriptionClmn;

    @FXML
    private TableColumn<Expense, String> WalletClmn;

    @FXML
    private Label userName;

    @FXML
    private ImageView profileIcon;

    public void setUserData(int userId) {
        this.userId = userId;
        loadExpenseData(); // Load expense data for this user
        setUserNameAndProfile();

    }

    @FXML
    public void initialize() {
        categories.getItems().addAll("Transportation", "Bills", "Food", "Shopping", "Entertainment", "Share", "Others");
        walletType.getItems().addAll("Cash", "Gcash");

        // Set up TableView columns
        CategoryClmn.setCellValueFactory(new PropertyValueFactory<>("category"));
        AmountClmn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        DateClmn.setCellValueFactory(new PropertyValueFactory<>("date"));
        DescriptionClmn.setCellValueFactory(new PropertyValueFactory<>("description"));
        WalletClmn.setCellValueFactory(new PropertyValueFactory<>("walletType"));

        // Bind the ObservableList with the TableView
        expenseTable.setItems(expenseList);

        // Add listener to amount TextField to only allow numbers
        amount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                amount.setText(oldValue);
            }
        });
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

    private double getCurrentBalance(String walletType) {
        String dbUrl = "jdbc:mysql://localhost:3306/witit";
        String dbUser = "root";
        String dbPassword = "";

        String query = "SELECT " + (walletType.equals("Cash") ? "total_cash" : "total_gcash") + " FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, this.userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to retrieve balance: " + e.getMessage());
        }

        return -1; // Indicate an error
    }


    @FXML
    void saveExpense(ActionEvent event) {
        String selectedCategory = categories.getValue();
        String selectedWalletType = walletType.getValue();
        String amountText = amount.getText();
        String descriptionText = description.getText();
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (selectedCategory == null || selectedWalletType == null || amountText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        double amountValue;
        try {
            amountValue = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid amount entered.");
            return;
        }

        double currentBalance = getCurrentBalance(selectedWalletType);
        if (currentBalance < 0) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to retrieve balance.");
            return;
        }

        if (currentBalance < amountValue) {
            showAlert(Alert.AlertType.ERROR, "Insufficient Funds", "Insufficient funds in " + selectedWalletType + ".");
            return;
        }

        String dbUrl = "jdbc:mysql://localhost:3306/witit";
        String dbUser = "root";
        String dbPassword = "";

        String insertExpenseQuery = "INSERT INTO expense (category, amount, date, description, user_id, wallet_type) VALUES (?, ?, ?, ?, ?, ?)";
        String updateUserQuery = "UPDATE users SET " + (selectedWalletType.equals("Cash") ? "total_cash" : "total_gcash") + " = " + (selectedWalletType.equals("Cash") ? "total_cash" : "total_gcash") + " - ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmtExpense = conn.prepareStatement(insertExpenseQuery);
             PreparedStatement pstmtUpdateUser = conn.prepareStatement(updateUserQuery)) {

            conn.setAutoCommit(false); // Begin transaction

            // Insert expense
            pstmtExpense.setString(1, selectedCategory);
            pstmtExpense.setDouble(2, amountValue);
            pstmtExpense.setString(3, currentDate);
            pstmtExpense.setString(4, descriptionText);
            pstmtExpense.setInt(5, this.userId);
            pstmtExpense.setString(6, selectedWalletType);
            pstmtExpense.executeUpdate();

            // Update user's total_cash or total_gcash
            pstmtUpdateUser.setDouble(1, amountValue);
            pstmtUpdateUser.setInt(2, this.userId);
            pstmtUpdateUser.executeUpdate();

            insertTransaction(conn, "Expense", selectedWalletType, amountValue);

            conn.commit(); // Commit transaction

            // Add expense to the observable list
            Expense expense = new Expense(selectedCategory, amountValue, currentDate, descriptionText, selectedWalletType);
            expenseList.add(expense);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Expense saved successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save expense: " + e.getMessage());
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


    private void loadExpenseData() {
        String dbUrl = "jdbc:mysql://localhost:3306/witit";
        String dbUser = "root";
        String dbPassword = "";

        String selectExpensesQuery = "SELECT category, amount, date, description, wallet_type FROM expense WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(selectExpensesQuery)) {

            pstmt.setInt(1, this.userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String category = rs.getString("category");
                    double amount = rs.getDouble("amount");
                    String date = rs.getString("date");
                    String description = rs.getString("description");
                    String walletType = rs.getString("wallet_type");

                    Expense expense = new Expense(category, amount, date, description, walletType);
                    expenseList.add(expense);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load expenses: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Expense class to hold expense data
    public static class Expense {
        private String category;
        private double amount;
        private String date;
        private String description;
        private String walletType;

        public Expense(String category, double amount, String date, String description, String walletType) {
            this.category = category;
            this.amount = amount;
            this.date = date;
            this.description = description;
            this.walletType = walletType;
        }

        public String getCategory() {
            return category;
        }

        public double getAmount() {
            return amount;
        }

        public String getDate() {
            return date;
        }

        public String getDescription() {
            return description;
        }

        public String getWalletType() {
            return walletType;
        }
    }

    @FXML
    private Button submitBtn;
}
