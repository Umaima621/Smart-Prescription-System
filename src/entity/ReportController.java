package entity;

public class ReportController {
    private MedicineIntakeRepository intakeRepo = new MedicineIntakeRepository();

    public List<MedicineIntake> getMissedDoses() {
        return intakeRepo.getMissedDoses();
    }
}