package com.HospitalManagementSystem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;


    public class HospitalManagementUI extends JFrame {
        private Connection connection;
        private patient p;
        private Doctor d;

        private JTextField nameField, ageField, genderField;
        private JTable patientTable, doctorTable;

        public HospitalManagementUI(Connection connection) {
            this.connection = connection;
            this.p = new patient(connection, null); 
            this.d = new Doctor(connection);

            setTitle("Hospital Management System");
            setSize(800, 600);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            // Tabbed UI
            JTabbedPane tabs = new JTabbedPane();

            tabs.add("Add Patient", createAddPatientPanel());
            tabs.add("View Patients", createViewPatientPanel());
            tabs.add("View Doctors", createViewDoctorsPanel());
            tabs.add("Book Appointment", createBookAppointmentPanel());

            add(tabs);
        }

        private JPanel createAddPatientPanel() {
            JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

            panel.add(new JLabel("Name:"));
            nameField = new JTextField();
            panel.add(nameField);

            panel.add(new JLabel("Age:"));
            ageField = new JTextField();
            panel.add(ageField);

            panel.add(new JLabel("Gender:"));
            genderField = new JTextField();
            panel.add(genderField);

            JButton addBtn = new JButton("Add Patient");
            addBtn.addActionListener(e -> addPatient());
            panel.add(new JLabel(""));
            panel.add(addBtn);

            return panel;
        }

        private JPanel createViewPatientPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            patientTable = new JTable();
            panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);

            JButton refreshBtn = new JButton("Refresh Patients");
            refreshBtn.addActionListener(e -> loadPatients());
            panel.add(refreshBtn, BorderLayout.SOUTH);

            return panel;
        }

        private JPanel createViewDoctorsPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            doctorTable = new JTable();
            panel.add(new JScrollPane(doctorTable), BorderLayout.CENTER);

            JButton refreshBtn = new JButton("Refresh Doctors");
            refreshBtn.addActionListener(e -> loadDoctors());
            panel.add(refreshBtn, BorderLayout.SOUTH);

            return panel;
        }

        private JPanel createBookAppointmentPanel() {
            JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

            JTextField patientIdField = new JTextField();
            JTextField doctorIdField = new JTextField();
            JTextField dateField = new JTextField();

            panel.add(new JLabel("Patient ID:"));
            panel.add(patientIdField);
            panel.add(new JLabel("Doctor ID:"));
            panel.add(doctorIdField);
            panel.add(new JLabel("Appointment Date (YYYY-MM-DD):"));
            panel.add(dateField);

            JButton bookBtn = new JButton("Book Appointment");
            bookBtn.addActionListener(e -> {
                try {
                    int pid = Integer.parseInt(patientIdField.getText());
                    int did = Integer.parseInt(doctorIdField.getText());
                    String date = dateField.getText();

                    if (p.GetPatientById(pid) && d.GetDoctorById(did)) {
                        if (HospitalManagementSystem.checkDoctorAvailability(did, date, connection)) {
                            String query = "INSERT INTO appointments (patient_id, doctors_id, APPO_date) VALUES (?, ?, ?)";
                            PreparedStatement ps = connection.prepareStatement(query);
                            ps.setInt(1, pid);
                            ps.setInt(2, did);
                            ps.setDate(3, java.sql.Date.valueOf(date));

                            int rows = ps.executeUpdate();
                            JOptionPane.showMessageDialog(this, rows > 0 ? "Appointment Booked!" : "Booking Failed.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Doctor not available on this date.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid Patient ID or Doctor ID.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            });

            panel.add(new JLabel(""));
            panel.add(bookBtn);

            return panel;
        }

        private void addPatient() {
            try {
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String gender = genderField.getText();

                String query = "INSERT INTO patients(name, Age, Gender) VALUES (?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, name);
                ps.setInt(2, age);
                ps.setString(3, gender);

                int rows = ps.executeUpdate();
                JOptionPane.showMessageDialog(this, rows > 0 ? "Patient Added!" : "Failed to Add Patient");

                nameField.setText("");
                ageField.setText("");
                genderField.setText("");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }

        private void loadPatients() {
            try {
                String query = "SELECT * FROM patients";
                PreparedStatement ps = connection.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("ID");
                model.addColumn("Name");
                model.addColumn("Age");
                model.addColumn("Gender");

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("name"));
                    row.add(rs.getInt("age"));
                    row.add(rs.getString("gender"));
                    model.addRow(row);
                }
                patientTable.setModel(model);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage());
            }
        }

        private void loadDoctors() {
            try {
                String query = "SELECT * FROM doctors";
                PreparedStatement ps = connection.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("ID");
                model.addColumn("Name");
                model.addColumn("Specialization");

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("name"));
                    row.add(rs.getString("specialization"));
                    model.addRow(row);
                }
                doctorTable.setModel(model);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading doctors: " + e.getMessage());
            }
        }

        public static void main(String[] args) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/hospital", "root", "Vin@Y@2oo3");
                SwingUtilities.invokeLater(() -> new HospitalManagementUI(connection).setVisible(true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
