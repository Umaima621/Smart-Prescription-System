package model;

public class Doctor extends UserModel {

    private String specialty;
    private String licenseNumber;

    public Doctor(int id, String name, String specialty) {
        super(id, name, "", "Doctor");
        this.specialty = specialty;
    }

    @Override
    public String getDashboardTitle() {
        return "Doctor Dashboard - " + name;
    }

    public String getSpecialty() { return specialty; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
}