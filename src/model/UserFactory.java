package model;

public class UserFactory {

    public static Object createUser(String role, int id,
                                     String name, String email,
                                     String extra) {
        switch (role) {
            case "Doctor":
                Doctor doctor = new Doctor(id, name, extra);
                doctor.setEmail(email);
                System.out.println("[UserFactory] Created Doctor: " + name);
                return doctor;

            case "Patient":
                System.out.println("[UserFactory] Created Patient: " + name);
                return new PatientModel(id, name, email, extra);

            default:
                System.err.println("[UserFactory] Unknown role: " + role);
                return null;
        }
    }
}