package controller.strategy;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;

public class WeeklyReportStrategy implements ReportPeriodStrategy {

    @Override
    public LocalDate getStartDate(LocalDate referenceDate) {
        return referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    @Override
    public LocalDate getEndDate(LocalDate referenceDate) {
        return referenceDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    @Override
    public String getPeriodType() {
        return "weekly";
    }
}
