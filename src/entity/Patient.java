package entity;

public class Patient {
    private String id;       // "PT-9920"
    private String name;
    private String bloodType;
    private String lastAppointment;
    private String medicalHistory;
    private String password;
    
    public Patient(String id, String name, String bloodType) {
        this.id = id;
        this.name = name;
        this.bloodType = bloodType;
    }

    public String getId()              { return id; }
    public String getName()            { return name; }
    public String getBloodType()       { return bloodType; }
    public String getLastAppointment() { return lastAppointment; }
    public String getMedicalHistory()  { return medicalHistory; }
    public void setLastAppointment(String d) { this.lastAppointment = d; }
    public void setMedicalHistory(String h)  { this.medicalHistory = h; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}