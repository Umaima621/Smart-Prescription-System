package dao;

import model.Report;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO extends BaseDAO {

    public ReportDAO() {
        super();
    }

    @Override
    public String getTableName() {
        return "report";
    }

    public boolean save(Report report) {
        String sql = "INSERT INTO report " +
                     "(report_id, patient_id, report_type, generated_at, " +
                     " start_date, end_date, total_scheduled, total_taken, total_missed) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, report.getReportId());
            stmt.setString(2, report.getPatientId());
            stmt.setString(3, report.getReportType());
            stmt.setTimestamp(4, Timestamp.valueOf(report.getGeneratedAt()));
            stmt.setDate(5, Date.valueOf(report.getStartDate()));
            stmt.setDate(6, Date.valueOf(report.getEndDate()));
            stmt.setInt(7, report.getTotalScheduled());
            stmt.setInt(8, report.getTotalTaken());
            stmt.setInt(9, report.getTotalMissed());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ReportDAO] save() failed: " + e.getMessage());
            return false;
        }
    }

    public Report findById(String reportId) {
        String sql = "SELECT * FROM report WHERE report_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, reportId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[ReportDAO] findById() failed: " + e.getMessage());
        }
        return null;
    }

    public List<Report> findByPatient(String patientId) {
        String sql = "SELECT * FROM report WHERE patient_id = ? " +
                     "ORDER BY generated_at DESC";
        List<Report> results = new ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) results.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ReportDAO] findByPatient() failed: " + e.getMessage());
        }
        return results;
    }

    public List<Report> findByPatientAndType(String patientId, String reportType) {
        String sql = "SELECT * FROM report " +
                     "WHERE patient_id = ? AND report_type = ? " +
                     "ORDER BY generated_at DESC";
        List<Report> results = new ArrayList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, patientId);
            stmt.setString(2, reportType);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) results.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ReportDAO] findByPatientAndType() failed: " + e.getMessage());
        }
        return results;
    }

    public Report findLatestByPatient(String patientId) {
        String sql = "SELECT * FROM report WHERE patient_id = ? " +
                     "ORDER BY generated_at DESC LIMIT 1";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[ReportDAO] findLatestByPatient() failed: " + e.getMessage());
        }
        return null;
    }

    public boolean update(Report report) {
        String sql = "UPDATE report SET " +
                     "total_scheduled = ?, total_taken = ?, total_missed = ?, " +
                     "generated_at = ? WHERE report_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, report.getTotalScheduled());
            stmt.setInt(2, report.getTotalTaken());
            stmt.setInt(3, report.getTotalMissed());
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(5, report.getReportId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ReportDAO] update() failed: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String reportId) {
        String sql = "DELETE FROM report WHERE report_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, reportId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ReportDAO] delete() failed: " + e.getMessage());
            return false;
        }
    }

    private Report mapRow(ResultSet rs) throws SQLException {
        return new Report(
                rs.getString("report_id"),
                rs.getString("patient_id"),
                rs.getString("report_type"),
                rs.getTimestamp("generated_at").toLocalDateTime(),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getInt("total_scheduled"),
                rs.getInt("total_taken"),
                rs.getInt("total_missed")
        );
    }
}
