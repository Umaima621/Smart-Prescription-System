package controller.strategy;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class MonthlyReportStrategy implements ReportPeriodStrategy {

    @Override
    public LocalDate getStartDate(LocalDate referenceDate) {
        return referenceDate.with(TemporalAdjusters.firstDayOfMonth());
    }

    @Override
    public LocalDate getEndDate(LocalDate referenceDate) {
        return referenceDate.with(TemporalAdjusters.lastDayOfMonth());
    }

    @Override
    public String getPeriodType() {
        return "monthly";
    }
}
