import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class savingsController {

    private int userId;
    private Connection connection;

    @FXML
    private TextField addGoalAmount;

    @FXML
    private TextField addGoalName;

    @FXML
    private TextField addToSavingsAmount;

    @FXML
    private ComboBox<String> chooseDisplayGoal;

    @FXML
    private ComboBox<String> chooseGoalToAddAmount;

    @FXML
    private Label currentSavingsView;

    @FXML
    private Label goalAmountView;

    @FXML
    private Label goalNameView;

    @FXML
    private Button completeButton;

    @FXML
    private Button deleteButton;

    public savingsController() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/witit", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle connection error as needed
        }
    }

    @FXML
    private Label userName;

    @FXML
    private ImageView profileIcon;

    public void setUserData(int userId) {
        this.userId = userId;
        initialize(); // Ensure initialization after user data is set
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

            Stage savingsStage = (Stage) profilePage.getScene().getWindow();
            savingsStage.setScene(new Scene(root));

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
            Stage savingsStage = (Stage) dashboardPage.getScene().getWindow();
            savingsStage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open dashboard page: " + e.getMessage());
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
            Stage savingsStage = (Stage) walletPage.getScene().getWindow();
            savingsStage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open wallet page: " + e.getMessage());
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
            Stage savingsStage = (Stage) debtPage.getScene().getWindow();
            savingsStage.setScene(new Scene(root));

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
            Stage savingsStage = (Stage) recordPage.getScene().getWindow();
            savingsStage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open record page: " + e.getMessage());
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
    public void initialize() {
        populateGoalChoiceBoxes();

        chooseDisplayGoal.setOnAction(event -> {
            String selectedGoal = chooseDisplayGoal.getValue();
            if (selectedGoal != null) {
                updateCurrentSavingsLabel(selectedGoal);
                completeButton.setVisible(true);
                deleteButton.setVisible(true);
            } else {
                completeButton.setVisible(false);
                deleteButton.setVisible(false);
            }
        });
    }

    private void populateGoalChoiceBoxes() {
        chooseDisplayGoal.getItems().clear();
        chooseGoalToAddAmount.getItems().clear();

        List<String> goalNames = fetchGoalNames();
        chooseDisplayGoal.getItems().addAll(goalNames);
        chooseGoalToAddAmount.getItems().addAll(goalNames);
    }

    private List<String> fetchGoalNames() {
        List<String> goalNames = new ArrayList<>();
        String query = "SELECT goal_name FROM savings_goals WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                goalNames.add(resultSet.getString("goal_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exception as needed
        }
        return goalNames;
    }

    @FXML
    void addToSavings(ActionEvent event) {
        String selectedGoal = chooseGoalToAddAmount.getValue();
        if (selectedGoal != null) {
            String addAmountStr = addToSavingsAmount.getText();
            if (!addAmountStr.isEmpty()) {
                try {
                    double addAmount = Double.parseDouble(addAmountStr);

                    // Retrieve goal amount from database
                    double goalAmount = fetchGoalAmount(selectedGoal);

                    // Check if addAmount exceeds goalAmount
                    if (addAmount <= goalAmount) {
                        updateCurrentSavings(selectedGoal, addAmount);
                        insertTransaction(connection, "Savings", addAmount);
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Amount added to savings successfully.");
                        chooseGoalToAddAmount.setValue(null);
                        addToSavingsAmount.clear();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Amount cannot exceed the goal amount.");
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid amount entered.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add amount to savings: " + e.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter an amount.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a goal.");
        }
    }

    private double fetchGoalAmount(String goalName) throws SQLException {
        String query = "SELECT goal_amount FROM savings_goals WHERE goal_name = ? AND user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, goalName);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("goal_amount");
            } else {
                throw new SQLException("Goal not found for the user.");
            }
        }
    }


    private void insertTransaction(Connection connection, String transactionType, double amount) throws SQLException {
        String insertQuery = "INSERT INTO transactions (user_id, transaction_type, amount, transaction_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, transactionType);
            preparedStatement.setDouble(3, amount);
            preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.executeUpdate();
        }
    }

    private void updateCurrentSavings(String goalName, double addAmount) {
        String query = "UPDATE savings_goals SET current_savings = current_savings + ? WHERE goal_name = ? AND user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, addAmount);
            statement.setString(2, goalName);
            statement.setInt(3, userId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                updateCurrentSavingsLabel(goalName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update current savings: " + e.getMessage());
        }
    }

    private void updateCurrentSavingsLabel(String goalName) {
        String query = "SELECT goal_name, current_savings, goal_amount FROM savings_goals WHERE goal_name = ? AND user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, goalName);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                goalNameView.setText(resultSet.getString("goal_name"));
                currentSavingsView.setText(String.format("%.2f", resultSet.getDouble("current_savings")));
                goalAmountView.setText(String.format("%.2f", resultSet.getDouble("goal_amount")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update savings label: " + e.getMessage());
        }
    }

    @FXML
    void saveNewGoal(ActionEvent event) {
        String goalName = addGoalName.getText();
        String goalAmountStr = addGoalAmount.getText();
        if (!goalName.isEmpty() && !goalAmountStr.isEmpty()) {
            try {
                double goalAmount = Double.parseDouble(goalAmountStr);
                saveGoalToDatabase(goalName, goalAmount);
                addGoalName.clear();
                addGoalAmount.clear();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid goal amount.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in both goal name and goal amount.");
        }
    }

    private void saveGoalToDatabase(String goalName, double goalAmount) {
        String query = "INSERT INTO savings_goals (user_id, goal_name, goal_amount, current_savings, status) VALUES (?, ?, ?, 0, 'ongoing')";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setString(2, goalName);
            statement.setDouble(3, goalAmount);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                populateGoalChoiceBoxes();
                showAlert(Alert.AlertType.INFORMATION, "Success", "New goal saved successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save new goal: " + e.getMessage());
        }
    }

    @FXML
    void completeGoal(ActionEvent event) {
        String selectedGoal = chooseDisplayGoal.getValue();
        if (selectedGoal != null) {
            // Query to fetch current savings and goal amount
            String query = "SELECT current_savings, goal_amount FROM savings_goals WHERE goal_name = ? AND user_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, selectedGoal);
                statement.setInt(2, userId);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    double currentSavings = resultSet.getDouble("current_savings");
                    double goalAmount = resultSet.getDouble("goal_amount");

                    if (currentSavings >= goalAmount) {
                        // Update the goal status to 'completed'
                        String updateQuery = "UPDATE savings_goals SET status = 'completed' WHERE goal_name = ? AND user_id = ?";
                        String deleteQuery = "DELETE FROM savings_goals WHERE goal_name = ? AND user_id = ?";

                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                             PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {

                            connection.setAutoCommit(false); // Start transaction

                            // Update the goal status to 'completed'
                            updateStatement.setString(1, selectedGoal);
                            updateStatement.setInt(2, userId);
                            int rowsUpdated = updateStatement.executeUpdate();

                            if (rowsUpdated > 0) {
                                // Show congratulations message
                                showAlert(Alert.AlertType.INFORMATION, "Success", "CONGRATULATIONS! Goal completed successfully.");

                                // Delete the completed goal from the database
                                deleteStatement.setString(1, selectedGoal);
                                deleteStatement.setInt(2, userId);
                                int rowsDeleted = deleteStatement.executeUpdate();

                                if (rowsDeleted > 0) {
                                    connection.commit(); // Commit transaction

                                    // Update UI components
                                    chooseDisplayGoal.getItems().remove(selectedGoal);
                                    chooseGoalToAddAmount.getItems().remove(selectedGoal);
                                    completeButton.setVisible(false);
                                    deleteButton.setVisible(false);
                                    clearLabels();
                                } else {
                                    connection.rollback(); // Rollback transaction
                                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete goal after completion.");
                                }
                            } else {
                                connection.rollback(); // Rollback transaction
                                showAlert(Alert.AlertType.ERROR, "Error", "Failed to complete goal.");
                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                            try {
                                connection.rollback(); // Rollback transaction on error
                            } catch (SQLException rollbackException) {
                                rollbackException.printStackTrace();
                            }
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to complete goal: " + e.getMessage());
                        } finally {
                            try {
                                connection.setAutoCommit(true); // Restore default auto-commit behavior
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Cannot complete goal. Current savings must be equal to or greater than the goal amount.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Goal not found for the user.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch goal details: " + e.getMessage());
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a goal.");
        }
    }



    @FXML
    void deleteGoal(ActionEvent event) {
        String selectedGoal = chooseDisplayGoal.getValue();
        if (selectedGoal != null) {
            String query = "DELETE FROM savings_goals WHERE goal_name = ? AND user_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, selectedGoal);
                statement.setInt(2, userId);
                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    chooseDisplayGoal.getItems().remove(selectedGoal);
                    chooseGoalToAddAmount.getItems().remove(selectedGoal);
                    completeButton.setVisible(false);
                    deleteButton.setVisible(false);
                    clearLabels();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Goal deleted successfully.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete goal: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a goal.");
        }
    }

    private void clearLabels() {
        goalNameView.setText("");
        currentSavingsView.setText("");
        goalAmountView.setText("");
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exception
        }
    }
}
