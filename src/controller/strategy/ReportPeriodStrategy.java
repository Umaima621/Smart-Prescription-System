package controller.strategy;

import java.time.LocalDate;

public interface ReportPeriodStrategy {
    LocalDate getStartDate(LocalDate referenceDate);

    LocalDate getEndDate(LocalDate referenceDate);

    String getPeriodType();
}
