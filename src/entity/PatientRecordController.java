package entity;

public class PatientRecordController {
    private UserRepository repo = new UserRepository();

    public Patient searchPatient(String query) {
        // try ID first, then name
        Patient p = repo.findPatientById(query);
        return p != null ? p : repo.findPatientByName(query);
    }
}