import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class dashboardController implements Initializable {

    @FXML
    private Label CurrentMoney;

    @FXML
    private Label totalExpenses;

    @FXML
    private Label totalBorrowed;

    @FXML
    private ImageView recordPage;

    @FXML
    private ImageView walletPage;

    @FXML
    private ImageView savingsPage;

    @FXML
    private ImageView debtPage;

    @FXML
    private ImageView profilePage;

    @FXML
    private Label userName;

    @FXML
    private ImageView profileIcon;


    @FXML
    private TableColumn<Transaction, String> AccountColumn;

    @FXML
    private TableView<Transaction> ActivityLog;

    @FXML
    private TableColumn<Transaction, Double> AmountColumn;

    @FXML
    private TableColumn<Transaction, String> DateColumn;

    @FXML
    private TableColumn<Transaction, String> TransactionColumn;



    @FXML
    private TableColumn<Savings, Double> targetAmountColumn;

    @FXML
    private TableColumn<Savings, Double> currentSavedColumn;

    @FXML
    private TableView<Savings> currentSavingsTable;

    @FXML
    private TableColumn<Savings, String> goalColumn;



    private int userId;

    public void setUsername(String username) {
        userName.setText(username);
    }

    public void setUserId(int userId) {
        this.userId = userId;
        updateCurrentMoney();
        updateTotalExpenses();
        updateActivityLog();
        updateTotalBorrowed();
        updateSavingLog();
        setUserNameAndProfile();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AccountColumn.setCellValueFactory(new PropertyValueFactory<>("account"));
        AmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        TransactionColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));

        targetAmountColumn.setCellValueFactory(new PropertyValueFactory<>("targetAmount"));
        currentSavedColumn.setCellValueFactory(new PropertyValueFactory<>("currentAmount"));
        goalColumn.setCellValueFactory(new PropertyValueFactory<>("goal"));

        // Set custom row factory to style rows based on transaction type
        ActivityLog.setRowFactory(tv -> new TableRow<Transaction>() {
            @Override
            protected void updateItem(Transaction item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    String transactionType = item.getTransactionType().toLowerCase();
                    if (transactionType.equals("income") || transactionType.equals("borrow") || transactionType.equals("savings")) {
                        setStyle("-fx-background-color: lightgreen;");
                    } else if (transactionType.equals("expense") || transactionType.equals("lend")) {
                        setStyle("-fx-background-color: lightcoral;");
                    } else if (transactionType.equals("debt payment")) {
                        setStyle("-fx-background-color: lightyellow;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        if (userId > 0) {
            updateCurrentMoney();
            updateTotalExpenses();
            updateActivityLog();
            updateTotalBorrowed();
            updateSavingLog();
            setUserNameAndProfile();
        }
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


    private void updateCurrentMoney() {
        String url = "jdbc:mysql://localhost:3306/witit";
        String user = "root";
        String password = "";

        String query = "SELECT total_cash + total_gcash AS current_money FROM users WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double currentMoney = resultSet.getDouble("current_money");
                CurrentMoney.setText(String.format("%.2f", currentMoney));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch current money: " + e.getMessage());
        }
    }

    private void updateTotalExpenses() {
        String url = "jdbc:mysql://localhost:3306/witit";
        String user = "root";
        String password = "";

        String query = "SELECT SUM(amount) AS total_expenses FROM transactions WHERE user_id = ? AND transaction_type = 'Expense'";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double totalExpensesAmount = resultSet.getDouble("total_expenses");
                totalExpenses.setText(String.format("%.2f", totalExpensesAmount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch total expenses: " + e.getMessage());
        }
    }

    private void updateTotalBorrowed() {
        String url = "jdbc:mysql://localhost:3306/witit";
        String user = "root";
        String password = "";

        String query = "SELECT SUM(remaining_balance) AS total_borrowed FROM debts WHERE user_id = ? AND debt_type = 'Borrow'";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double totalBorrowedAmount = resultSet.getDouble("total_borrowed");
                totalBorrowed.setText(String.format("%.2f", totalBorrowedAmount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch total borrowed: " + e.getMessage());
        }
    }

    private void updateActivityLog() {
        String url = "jdbc:mysql://localhost:3306/witit";
        String user = "root";
        String password = "";

        // Calculate the start and end dates of the current week
        LocalDate currentDate = LocalDate.now();
        LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = currentDate.with(DayOfWeek.SUNDAY);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String query = "SELECT * FROM transactions WHERE user_id = ? " +
                "AND transaction_date BETWEEN ? AND ?";

        ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, startOfWeek.format(formatter));
            preparedStatement.setString(3, endOfWeek.format(formatter));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String account = resultSet.getString("account_type");
                double amount = resultSet.getDouble("amount");
                String date = resultSet.getString("transaction_date");
                String transactionType = resultSet.getString("transaction_type");

                Transaction transaction = new Transaction(account, amount, date, transactionType);
                transactionList.add(transaction);
            }

            ActivityLog.setItems(transactionList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch activity log: " + e.getMessage());
        }
    }


    private void updateSavingLog() {
        String url = "jdbc:mysql://localhost:3306/witit";
        String user = "root";
        String password = "";

        String query = "SELECT * FROM savings_goals WHERE user_id = ?";

        ObservableList<Savings> savingsList = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String goal = resultSet.getString("goal_name");
                double targetAmount = resultSet.getDouble("goal_amount");
                double currentAmount = resultSet.getDouble("current_savings");

                Savings savings = new Savings(goal, targetAmount, currentAmount);
                savingsList.add(savings);
            }

            currentSavingsTable.setItems(savingsList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch savings log: " + e.getMessage());
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

    @FXML
    void handleProfilePageClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile.fxml"));
            Parent root = loader.load();

            profileController profileController = loader.getController();
            profileController.setUserData(userId);

            Stage dashboardStage = (Stage) profilePage.getScene().getWindow();
            dashboardStage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open profile page: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    public class Transaction {
        private String account;
        private double amount;
        private String date;
        private String transactionType;

        public Transaction(String account, double amount, String date, String transactionType) {
            this.account = account;
            this.amount = amount;
            this.date = date;
            this.transactionType = transactionType;
        }

        public String getAccount() {
            return account;
        }

        public double getAmount() {
            return amount;
        }

        public String getDate() {
            return date;
        }

        public String getTransactionType() {
            return transactionType;
        }
    }

    public class Savings {
        private String goal;
        private double targetAmount;
        private double currentAmount;

        public Savings(String goal, double targetAmount, double currentAmount) {
            this.goal = goal;
            this.targetAmount = targetAmount;
            this.currentAmount = currentAmount;
        }

        public String getGoal() {
            return goal;
        }

        public double getTargetAmount() {
            return targetAmount;
        }

        public double getCurrentAmount() {
            return currentAmount;
        }
    }




}
