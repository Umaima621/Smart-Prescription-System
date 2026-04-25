package entity;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private List<Doctor> doctors = new ArrayList<>();
    private List<Patient> patients = new ArrayList<>();

    public UserRepository() {
        // Sample doctors
        Doctor d = new Doctor("DR-00142", "Dr. Smith", "General Medicine");
        d.setPassword("1234");
        doctors.add(d);

        // Sample patients
        Patient p = new Patient("PT-9920", "John Doe", "O+");
        p.setPassword("abcd");
        patients.add(p);
    }

    public Doctor findDoctorById(String id) {
        for (Doctor d : doctors) {
            if (d.getId().equals(id)) return d;
        }
        return null;
    }
    
    public Doctor findDoctorByName(String name) {
        for (Doctor d : doctors) {
            if (d.getId().equals(name)) return d;
        }
        return null;
    }
    
    public Patient findPatientById(String id) {
        for (Patient p : patients) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }
    
    public Patient findPatientByName(String name) {
        for (Patient p : patients) {
            if (p.getName().equals(name)) return p;
        }
        return null;
    }
    
    public boolean validatePassword(String id, String password) {
        // Check doctors first, then patients
        Doctor d = findDoctorById(id);
        if (d != null) return d.getPassword().equals(password);

        Patient p = findPatientById(id);
        if (p != null) return p.getPassword().equals(password);

        return false;
    }

    public String getRole(String id) {
        if (findDoctorById(id) != null)  return "doctor";
        if (findPatientById(id) != null) return "patient";
        return null;
    }
}