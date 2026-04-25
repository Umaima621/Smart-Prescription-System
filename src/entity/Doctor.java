package entity;

public class Doctor {
    private String id;       // "DR-00142"
    private String name;
    private String specialty;
    private String password;

    public Doctor(String id, String name, String specialty) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
    }

    public String getId()       { return id; }
    public String getName()     { return name; }
    public String getSpecialty(){ return specialty; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}