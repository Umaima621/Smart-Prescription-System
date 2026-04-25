package entity;

import java.util.ArrayList;
import java.util.List;

public class MedicineIntakeRepository {
    private List<MedicineIntake> intakes = new ArrayList<>();
    public List<MedicineIntake> getMissedDoses() { ... }
    public void save(MedicineIntake m) { ... }
}