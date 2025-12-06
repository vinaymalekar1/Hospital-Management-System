package com.HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class patient {
    private Connection connection;
    private Scanner scanner;
    public patient(Connection connection,Scanner scanner){
        this.connection=connection;
        this.scanner=scanner;
    }
    public void AddPatient(){
        System.out.print("Enter Patient Name: ");
        String name=scanner.next();
        System.out.print("Enter Patient Age: ");
        int age=scanner.nextInt();
        System.out.print("Enter the Gender of Patient: ");
        String gender= scanner.next();


        try{
            String query="INSERT INTO patients(name,Age,Gender) VALUES (?,?,?)";
            PreparedStatement ps= connection.prepareStatement(query);
            ps.setString(1,name);
            ps.setInt(2,age);
            ps.setString(3,gender);
            int affectedRows= ps.executeUpdate();
            if(affectedRows>0)
                System.out.println("Patient Data Inserted Successfully! ");
            else
                System.out.println("Failed to add patient data! ");


        }catch (SQLException e){
            e.printStackTrace();
        }

    }
    public void viewPatient(){
        String query="SELECT * FROM patients";
        try{
            PreparedStatement ps= connection.prepareStatement(query);
            ResultSet rs=ps.executeQuery();
            System.out.println("Patients: ");
            System.out.println("+------------+------------------------+----------+----------------+");
            System.out.println("| Patient ID |  name                  |    age   |   gender       |");
            System.out.println("+------------+------------------------+----------+----------------+");
            while(rs.next()){
                int id=rs.getInt("id");
                String name=rs.getString("name");
                int age=rs.getInt("age");
                String gender=rs.getString("gender");
                System.out.printf("|%-12s|%-24s|%-10s|%-16s|\n",id,name,age,gender);
                System.out.println("+------------+------------------------+----------+----------------+");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public boolean GetPatientById(int id){
      String query="SELECT * FROm patients WHERE id=?";
      try{
         PreparedStatement ps= connection.prepareStatement(query);
         ps.setInt(1,id);
         ResultSet rs=ps.executeQuery();
         if(rs.next())
             return true;
         else
             return false;

      }catch (SQLException e){
          e.printStackTrace();
      }
      return false;
    }
}
