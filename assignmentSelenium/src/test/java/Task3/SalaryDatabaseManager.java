package Task3;

import java.io.*;
import java.sql.*;
import java.time.*;
import java.util.*;

public class SalaryDatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:salary.db";

    public void initializeDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            String createTableSQL = "CREATE TABLE IF NOT EXISTS Salary (" +
                    "employee_name TEXT NOT NULL, " +
                    "join_date TEXT NOT NULL, " +
                    "salary REAL NOT NULL)";
            stmt.execute(createTableSQL);
        }
    }

    public void insertRandomData(int count) throws SQLException {
        Random random = new Random();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Salary (employee_name, join_date, salary) VALUES (?, ?, ?)")) {

            for (int i = 1; i <= count; i++) {
                String name = "Employee_" + i;
                LocalDate joinDate = LocalDate.now().minusDays(random.nextInt(365));
                double salary = 5000 + random.nextDouble() * 20000;

                pstmt.setString(1, name);
                pstmt.setString(2, joinDate.toString());
                pstmt.setDouble(3, salary);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    public Salary getHighestPaidEmployee() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Salary ORDER BY salary DESC LIMIT 1")) {

            if (rs.next()) {
                return new Salary(rs.getString("employee_name"),
                        LocalDate.parse(rs.getString("join_date")),
                        rs.getDouble("salary"));
            }
        }
        return null;
    }

    public List<Salary> getEmployeesWithHighSalary(double minSalary) throws SQLException {
        List<Salary> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Salary WHERE salary > ?")) {
            pstmt.setDouble(1, minSalary);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Salary(rs.getString("employee_name"),
                        LocalDate.parse(rs.getString("join_date")),
                        rs.getDouble("salary")));
            }
        }
        return list;
    }

    public List<Salary> getEmployeesJoinedLastMonth() throws SQLException {
        List<Salary> list = new ArrayList<>();
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Salary WHERE join_date >= ?")) {
            pstmt.setString(1, oneMonthAgo.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Salary(rs.getString("employee_name"),
                        LocalDate.parse(rs.getString("join_date")),
                        rs.getDouble("salary")));
            }
        }
        return list;
    }

    public List<Salary> getEmployeesSortedByName() throws SQLException {
        List<Salary> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Salary ORDER BY employee_name ASC")) {
            while (rs.next()) {
                list.add(new Salary(rs.getString("employee_name"),
                        LocalDate.parse(rs.getString("join_date")),
                        rs.getDouble("salary")));
            }
        }
        return list;
    }

    public void exportToCSV(List<Salary> salaries, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Employee Name,Join Date,Salary\n");
            for (Salary s : salaries) {
                writer.write(s.getEmployeeName() + "," + s.getJoinDate() + "," + s.getSalary() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing CSV: " + e.getMessage());
        }
    }
}
