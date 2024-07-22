import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class trialController {

    @FXML
    private BarChart<String, Double> WeeklyTransaction;

    @FXML
    public void initialize() {
        // Initialize axes
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Transaction Type");
        yAxis.setLabel("Total Amount");

        // Initialize BarChart
        WeeklyTransaction.setTitle("Weekly Transaction Summary");
        WeeklyTransaction.setLegendVisible(false); // Optional, hides legend

        // Create data series
        XYChart.Series<String, Double> series = new XYChart.Series<>();

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT transaction_type, SUM(amount) AS total_amount FROM transactions GROUP BY transaction_type")) {

            while (rs.next()) {
                String transactionType = rs.getString("transaction_type");
                double totalAmount = rs.getDouble("total_amount");

                series.getData().add(new XYChart.Data<>(transactionType, totalAmount));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add series to BarChart
        WeeklyTransaction.getData().add(series);
    }
}
