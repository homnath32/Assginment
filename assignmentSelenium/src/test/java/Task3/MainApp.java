package Task3;

import java.sql.SQLException;
import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        SalaryDatabaseManager dbManager = new SalaryDatabaseManager();

        try {
            dbManager.initializeDatabase();
            dbManager.insertRandomData(100);

            System.out.println("\n👑 Highest Paid Employee:");
            System.out.println(dbManager.getHighestPaidEmployee());

            List<Salary> highEarners = dbManager.getEmployeesWithHighSalary(10000);
            dbManager.exportToCSV(highEarners, "resources/salary_gt_10000.csv");

            List<Salary> recentJoiners = dbManager.getEmployeesJoinedLastMonth();
            dbManager.exportToCSV(recentJoiners, "resources/recent_joins.csv");

            System.out.println("\n📋 Employees Sorted by Name:");
            dbManager.getEmployeesSortedByName().forEach(System.out::println);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
