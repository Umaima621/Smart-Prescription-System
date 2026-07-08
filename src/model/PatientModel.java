package model;

public class PatientModel extends UserModel {

    private String bloodType;

    public PatientModel(int id, String name, String email, String bloodType) {
        super(id, name, email, "Patient");
        this.bloodType = bloodType != null ? bloodType : "Unknown";
    }

    @Override
    public String getDashboardTitle() {
        return "Patient Dashboard - " + name;
    }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    @Override
    public String toString() {
        return "Patient{id=" + id + ", name=" + name +
               ", bloodType=" + bloodType + "}";
    }
}