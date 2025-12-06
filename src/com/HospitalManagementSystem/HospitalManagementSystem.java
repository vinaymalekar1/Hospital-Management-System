package com.HospitalManagementSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.SwingUtilities;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "Vin@Y@2oo3";

    public static void main(String[] args) {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, username, password);

            SwingUtilities.invokeLater(() -> {
                HospitalManagementUI ui = new HospitalManagementUI(connection);
                ui.setVisible(true);
            });

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }
    public static boolean checkDoctorAvailability(int Doctorid, String AppointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctors_id = ? AND APPO_date = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, Doctorid);
            ps.setString(2, AppointmentDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
