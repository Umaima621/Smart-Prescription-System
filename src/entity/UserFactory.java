package entity;

//model/UserFactory.java
public class UserFactory {
 public static Doctor createDoctor(String id, String name, String specialty) {
     return new Doctor(id, name, specialty);
 }
 public static Patient createPatient(String id, String name, String bloodType) {
     return new Patient(id, name, bloodType);
 }
}
