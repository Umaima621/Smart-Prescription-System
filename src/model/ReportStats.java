package model;

public class ReportStats {

    private final int   totalScheduled;
    private final int   totalTaken;
    private final int   totalMissed;
    private final float complianceRate;

    public ReportStats(int totalScheduled, int totalTaken, int totalMissed) {
        this.totalScheduled = totalScheduled;
        this.totalTaken     = totalTaken;
        this.totalMissed    = totalMissed;
        this.complianceRate = totalScheduled == 0 ? 0f
                : ((float) totalTaken / totalScheduled) * 100f;
    }

    public int   getTotalScheduled()  { return totalScheduled; }
    public int   getTotalTaken()      { return totalTaken; }
    public int   getTotalMissed()     { return totalMissed; }
    public float getComplianceRate()  { return complianceRate; }

    @Override
    public String toString() {
        return "ReportStats{scheduled=" + totalScheduled +
               ", taken=" + totalTaken +
               ", missed=" + totalMissed +
               ", compliance=" + String.format("%.1f", complianceRate) + "%}";
    }
}
