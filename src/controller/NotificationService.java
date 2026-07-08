package controller;

import dao.IntakeLogDAO;
import model.MedicineIntake;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class NotificationService {

    private final IntakeLogDAO intakeLogDAO;

    public NotificationService() {
        this.intakeLogDAO = new IntakeLogDAO();
    }

   
    public boolean confirmIntake(String intakeId) {
        MedicineIntake intake = intakeLogDAO.findById(intakeId);
        if (intake == null) {
            System.err.println("[NotificationService] confirmIntake: intake not found.");
            return false;
        }

        intake.markAsTaken(LocalDateTime.now());

        boolean updated = intakeLogDAO.update(intake);
        if (updated) {
            System.out.println("[NotificationService] Intake confirmed: " + intakeId);
        }
        return updated;
    }

    public boolean toggleIntakeStatus(String intakeId) {
        MedicineIntake intake = intakeLogDAO.findById(intakeId);
        if (intake == null) {
            System.err.println("[NotificationService] toggleIntakeStatus: intake not found.");
            return false;
        }

        intake.toggleStatus();

        boolean updated = intakeLogDAO.update(intake);
        if (updated) {
            System.out.println("[NotificationService] Toggled intake " + intakeId
                               + " → isTaken=" + intake.isTaken());
        }
        return updated;
    }

    public List<MedicineIntake> getTodayIntakes(String patientId) {
        return intakeLogDAO.findByPatientAndDate(patientId, LocalDate.now());
    }

    public List<MedicineIntake> getIntakesForDate(String patientId, LocalDate date) {
        return intakeLogDAO.findByPatientAndDate(patientId, date);
    }

    public MedicineIntake saveIntakeLog(String notificationId, String medicineId, String patientId, LocalDateTime scheduledTime, boolean isTaken) {
        MedicineIntake intake = new MedicineIntake(medicineId, patientId, scheduledTime);
        intake.setIntakeId(java.util.UUID.randomUUID().toString());
        intake.setNotificationId(notificationId);
        if (isTaken) intake.markAsTaken(LocalDateTime.now());

        boolean saved = intakeLogDAO.save(intake);
        return saved ? intake : null;
    }

    public void markAsDismissed(String notificationId) {
        System.out.println("[NotificationService] Notification dismissed: " + notificationId);
    }
}
