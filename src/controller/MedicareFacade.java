package controller;

import dao.IntakeLogDAO;
import dao.ReportDAO;
import db.DatabaseConnection;
import model.MedicineIntake;
import model.Report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicareFacade {

    private final ReportController reportController;
    private final NotificationService notificationService;
    private final IntakeLogDAO intakeLogDAO;
    private final ReportDAO reportDAO;

    private static MedicareFacade instance;

    private MedicareFacade() {
        this.reportController = new ReportController();
        this.notificationService = new NotificationService();
        this.intakeLogDAO = new IntakeLogDAO();
        this.reportDAO = new ReportDAO();
    }

  
    public static MedicareFacade getInstance() {
        if (instance == null) {
            instance = new MedicareFacade();
        }
        return instance;
    }

    public int getMissedMedicinesCount(String patientEmail) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT COUNT(*) FROM medicine_intake " + "WHERE patient_id = ? AND is_taken = 0 " + "AND scheduled_time < GETDATE()";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientEmail);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[Facade] getMissedMedicinesCount: " + e.getMessage());
        }
        return 0;
    }

    public int getMedicineCount(String patientEmail) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT COUNT(*) FROM Medicines WHERE userID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientEmail);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[Facade] getMedicineCount: " + e.getMessage());
        }
        return 0;
    }

    public Report generateWeeklyReport(String patientEmail) {
        return reportController.generateReport(patientEmail, "weekly");
    }

    public Report generateMonthlyReport(String patientEmail) {
        return reportController.generateReport(patientEmail, "monthly");
    }


    public List<MedicineIntake> getTodayIntakes(String patientEmail) {
        try {
            return notificationService.getTodayIntakes(patientEmail);
        } catch (Exception e) {
            System.err.println("[Facade] getTodayIntakes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean confirmIntake(String intakeId) {
        return notificationService.confirmIntake(intakeId);
    }

    public List<Report> getReportHistory(String patientEmail) {
        return reportController.getReportHistory(patientEmail);
    }

    public List<MedicineIntake> getMissedMedicines(String patientEmail, String period) {
        return reportController.viewMissedMedicines(patientEmail, period);
    }
}