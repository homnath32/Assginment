package Task3;

import java.time.LocalDate;

public class Salary {
    private String employeeName;
    private LocalDate joinDate;
    private double salary;

    public Salary(String employeeName, LocalDate joinDate, double salary) {
        this.employeeName = employeeName;
        this.joinDate = joinDate;
        this.salary = salary;
    }

    public String getEmployeeName() { return employeeName; }
    public LocalDate getJoinDate() { return joinDate; }
    public double getSalary() { return salary; }

    @Override
    public String toString() {
        return employeeName + " | " + joinDate + " | " + salary;
    }
}
