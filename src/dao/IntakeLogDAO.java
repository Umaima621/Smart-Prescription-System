package dao;

import model.MedicineIntake;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IntakeLogDAO extends BaseDAO {

    public IntakeLogDAO() {
        super();
    }

    @Override
    public String getTableName() {
        return "medicine_intake";
    }

    public boolean save(MedicineIntake intake) {
        String sql = "INSERT INTO medicine_intake " +
                     "(intake_id, notification_id, medicine_id, patient_id, " +
                     " scheduled_time, taken_at, is_taken) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, intake.getIntakeId());
            stmt.setString(2, intake.getNotificationId());
            stmt.setString(3, intake.getMedicineId());
            stmt.setString(4, intake.getPatientId());
            stmt.setTimestamp(5, toTimestamp(intake.getScheduledTime()));
            stmt.setTimestamp(6, toTimestamp(intake.getTakenAt()));
            stmt.setBoolean(7, intake.isTaken());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[IntakeLogDAO] save() failed: " + e.getMessage());
            return false;
        }
    }

    public MedicineIntake findById(String intakeId) {
        String sql = "SELECT * FROM medicine_intake WHERE intake_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, intakeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[IntakeLogDAO] findById() failed: " + e.getMessage());
        }
        return null;
    }
    
    public List<MedicineIntake> findByPatientAndDate(String patientId, LocalDate date) {
    	String sql = "SELECT * FROM medicine_intake " +
                "WHERE patient_id = ? AND CAST(scheduled_time AS DATE) = ? " +
                "ORDER BY scheduled_time ASC";
        List<MedicineIntake> results = new ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, patientId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) results.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[IntakeLogDAO] findByPatientAndDate() failed: " + e.getMessage());
        }
        return results;
    }

    public List<MedicineIntake> queryIntakeLogs(String patientId, LocalDate startDate, LocalDate endDate) {
    	String sql = "SELECT * FROM medicine_intake " +
                "WHERE patient_id = ? " +
                "AND CAST(scheduled_time AS DATE) BETWEEN ? AND ? " +
                "ORDER BY scheduled_time ASC";
        List<MedicineIntake> results = new ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, patientId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) results.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[IntakeLogDAO] queryIntakeLogs() failed: " + e.getMessage());
        }
        return results;
    }

    public List<MedicineIntake> findMissed(String patientId) {
    	String sql = "SELECT * FROM medicine_intake " +
                "WHERE patient_id = ? AND is_taken = 0 " +
                "AND scheduled_time < GETDATE() " +
                "ORDER BY scheduled_time DESC";
        List<MedicineIntake> results = new ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) results.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[IntakeLogDAO] findMissed() failed: " + e.getMessage());
        }
        return results;
    }

    public boolean update(MedicineIntake intake) {
        String sql = "UPDATE medicine_intake " +
                     "SET is_taken = ?, taken_at = ?, notification_id = ? " +
                     "WHERE intake_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setBoolean(1, intake.isTaken());
            stmt.setTimestamp(2, toTimestamp(intake.getTakenAt()));
            stmt.setString(3, intake.getNotificationId());
            stmt.setString(4, intake.getIntakeId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[IntakeLogDAO] update() failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTakenStatus(String intakeId, boolean isTaken) {
        String sql = "UPDATE medicine_intake SET is_taken = ?, taken_at = ? " +
                     "WHERE intake_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setBoolean(1, isTaken);
            stmt.setTimestamp(2, isTaken ? Timestamp.valueOf(LocalDateTime.now()) : null);
            stmt.setString(3, intakeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[IntakeLogDAO] updateTakenStatus() failed: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String intakeId) {
        String sql = "DELETE FROM medicine_intake WHERE intake_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, intakeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[IntakeLogDAO] delete() failed: " + e.getMessage());
            return false;
        }
    }

    public int countScheduled(String patientId, LocalDate startDate, LocalDate endDate) {
    	String sql = "SELECT COUNT(*) FROM medicine_intake " +
                "WHERE patient_id = ? AND CAST(scheduled_time AS DATE) BETWEEN ? AND ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, patientId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[IntakeLogDAO] countScheduled() failed: " + e.getMessage());
        }
        return 0;
    }

    public int countTaken(String patientId, LocalDate startDate, LocalDate endDate) {
    	String sql = "SELECT COUNT(*) FROM medicine_intake " +
                "WHERE patient_id = ? AND is_taken = 1 " +
                "AND CAST(scheduled_time AS DATE) BETWEEN ? AND ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, patientId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[IntakeLogDAO] countTaken() failed: " + e.getMessage());
        }
        return 0;
    }

    private MedicineIntake mapRow(ResultSet rs) throws SQLException {
        Timestamp scheduledTs = rs.getTimestamp("scheduled_time");
        Timestamp takenTs     = rs.getTimestamp("taken_at");
        return new MedicineIntake(
                rs.getString("intake_id"),
                rs.getString("notification_id"),
                rs.getString("medicine_id"),
                rs.getString("patient_id"),
                scheduledTs != null ? scheduledTs.toLocalDateTime() : null,
                takenTs != null ? takenTs.toLocalDateTime()     : null,
                rs.getBoolean("is_taken")
        );
    }

    private Timestamp toTimestamp(LocalDateTime ldt) {
        return ldt != null ? Timestamp.valueOf(ldt) : null;
    }
}
