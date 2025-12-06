package com.HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url="jdbc:mysql://localhost:3306/hospital";
    private static final String username="root";
    private static final String password="Vin@Y@2oo3";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
    }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner=new Scanner(System.in);
        try{
            Connection connection= DriverManager.getConnection(url,username,password);
            patient p=new patient(connection,scanner);
            Doctor d=new Doctor(connection);
            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM: ");
                System.out.println("1. Add Patient: ");
                System.out.println("2.View Patients: ");
                System.out.println("3. View Doctors: ");
                System.out.println("4. Book Appointments: ");
                System.out.println("5. Exit");
                System.out.println("Enter Your Choice: ");
                int choice= scanner.nextInt();

                switch (choice){
                    case 1:
                        //add patient
                        p.AddPatient();
                        System.out.println();
                        break;
                    case 2:
                        //view patient
                        p.viewPatient();
                        System.out.println();
                        break;
                    case 3:
                        //view Doctors
                        d.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        // Book Appointments\
                        bookAppointments(p,d,connection,scanner);
                        System.out.println();
                    case 5:
                        return;
                    default:
                        System.out.println("Enter Valid Choice !");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void bookAppointments( patient patient,Doctor doctor,Connection connection,Scanner scanner){
        System.out.println("Enter Patient Id: ");
        int patientid=scanner.nextInt();
        System.out.println("Enter Doctor Id: ");
        int Doctorid=scanner.nextInt();
        System.out.println("Enter Appointment Date (YYYY-MM-DD): ");
        String AppointmentDate=scanner.next();
        if(patient.GetPatientById(patientid)&& doctor.GetDoctorById(Doctorid)){
           if(checkDoctorAvailability(Doctorid,AppointmentDate,connection)) {
               String Appointmentquery="INSERT INTO  appointments (patient_id,doctors_id,APPO_date) VALUES (?,?,?)";
               try{
                   PreparedStatement preparedStatement= connection.prepareStatement(Appointmentquery);
                   preparedStatement.setInt(1,patientid);
                   preparedStatement.setInt(2,Doctorid);
                   preparedStatement.setDate(3,java.sql.Date.valueOf(AppointmentDate));
                   int RowsAffectted=preparedStatement.executeUpdate();
                   if(RowsAffectted>0)
                       System.out.println("Appointment Booked");
                   else
                       System.out.println(" Failed to Book Appointment");
               }catch (SQLException e){
                   e.printStackTrace();
               }
           }
        }else {
            System.out.println("Either Doctor or Patient Doesn't Exist!");
        }
    }
    public  static boolean checkDoctorAvailability(int Doctorid, String AppointmentDate,Connection connection){
      String query="SELECT COUNT(*) FROM appointments WHERE doctors_id=? AND Appo_date=?";
      try{
          PreparedStatement preparedStatement=connection.prepareStatement(query);
          preparedStatement.setInt(1,Doctorid);
          preparedStatement.setString(2,AppointmentDate);
          ResultSet rs=preparedStatement.executeQuery();
          if(rs.next()){
              int count=rs.getInt(1);
              if(count==0)
                  return true;
              else
                  return false;
          }

      }catch (SQLException e){
          e.printStackTrace();
      }
      return false;
    }
}
