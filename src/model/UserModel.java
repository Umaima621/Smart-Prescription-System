package model;

public abstract class UserModel {

    protected int id;
    protected String name;
    protected String email;
    protected String role;

    public UserModel(int id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public abstract String getDashboardTitle();

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return role + "{id=" + id + ", name=" + name + "}";
    }
}