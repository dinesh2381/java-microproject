import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CollegeAdmissionSystem {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/data";
    private static final String USER = "root"; 
    private static final String PASSWORD = "12345";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            // Display options to the user
            System.out.println("\n--- College Admission System ---");
            System.out.println("1. Add Student");
            System.out.println("2. Update Application Status");
            System.out.println("3. Record Payment");
            System.out.println("4. Display Student Details");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1: 
                    addStudentMenu(scanner);
                    break;
                case 2: 
                    System.out.print("Enter Student ID to update status: ");
                    int studentIdForStatus = scanner.nextInt();
                    scanner.nextLine(); 
                    System.out.print("Enter New Application Status (e.g., 'Approved', 'Rejected'): ");
                    String status = scanner.nextLine();
                    updateApplicationStatus(studentIdForStatus, status);
                    break;
                case 3:
                    System.out.print("Enter Student ID to record payment: ");
                    int studentIdForPayment = scanner.nextInt();
                    scanner.nextLine(); 
                    recordPayment(studentIdForPayment);
                    break;
                case 4: 
                    System.out.print("Enter Student ID to display details: ");
                    int studentIdForDisplay = scanner.nextInt();
                    scanner.nextLine();
                    displayStudentDetails(studentIdForDisplay);
                    break;
                case 0: 
                    System.out.println("Exiting the system.");
                    running = false;
                    break;
                default: 
                    System.out.println("Invalid option selected.");
                    break;
            }
        }

        System.out.println("\n--- Process Complete. Thank you! ---");
        scanner.close();
    }

    private static void addStudentMenu(Scanner scanner) {
        System.out.println("\n--- Add Student Details ---");
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Contact Number: ");
        String contactNumber = scanner.nextLine();
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter Course Name: ");
        String courseName = scanner.nextLine();
        System.out.print("Enter Eligibility Criteria: ");
        String eligibility = scanner.nextLine();
        System.out.print("Enter Course Duration: ");
        String courseDuration = scanner.nextLine();
        
        double courseFee = 0.0;
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.print("Enter Course Fee: ");
                courseFee = scanner.nextDouble();
                validInput = true; 
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid numeric value for the course fee.");
                scanner.next(); 
            }
        }

        scanner.nextLine();

       
        int studentId = addStudent(name, email, contactNumber, address, courseName, eligibility, courseDuration, courseFee);
        System.out.println("Student added successfully with ID: " + studentId);
    }

    private static int addStudent(String name, String email, String contactNumber, String address, String courseName, String eligibility, String courseDuration, double courseFee) {
        String query = "INSERT INTO students (name, email, contact_number, address, course_name, eligibility, course_duration, course_fee) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        int generatedId = -1; 

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, contactNumber);
            pstmt.setString(4, address);
            pstmt.setString(5, courseName);
            pstmt.setString(6, eligibility);
            pstmt.setString(7, courseDuration);
            pstmt.setDouble(8, courseFee);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return generatedId;
    }

    private static void updateApplicationStatus(int studentId, String status) {
        String query = "UPDATE students SET application_status = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, studentId);
            pstmt.executeUpdate();

            System.out.println("Application status updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void recordPayment(int studentId) {
        String query = "UPDATE students SET application_status = 'Paid' WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            pstmt.executeUpdate();

            System.out.println("Payment recorded successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void displayStudentDetails(int studentId) {
        String query = "SELECT * FROM students WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("\n--- Student Details ---");
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Contact Number: " + rs.getString("contact_number"));
                System.out.println("Address: " + rs.getString("address"));
                System.out.println("Course Name: " + rs.getString("course_name"));
                System.out.println("Eligibility Criteria: " + rs.getString("eligibility"));
                System.out.println("Course Duration: " + rs.getString("course_duration"));
                System.out.println("Course Fee: " + rs.getDouble("course_fee"));
                System.out.println("Application Status: " + rs.getString("application_status"));
            } else {
                System.out.println("No student found with ID " + studentId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
