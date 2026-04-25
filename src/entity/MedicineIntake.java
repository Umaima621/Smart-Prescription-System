package entity;

public class MedicineIntake {
    private String patientName;
    private String medicineName;
    private String timeMissed;
    private String patientNote;
    private boolean missed;
    public MedicineIntake(String pname, String mname, String tmissed,String pNote,boolean m) {
    	patientName=pname;
        medicineName=mname;
        timeMissed=tmissed;
        patientNote=pNote;
        missed=m;
    }

    public String getPatientName()  { return patientName; }
    public String getMedicineName() { return medicineName; }
    public String getTimeMissed()   { return timeMissed; }
    public String getPatientNote()  { return patientNote; }
    public boolean isMissed()       { return missed; }

    public void setPatientName(String patientName)   { this.patientName  = patientName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public void setTimeMissed(String timeMissed)     { this.timeMissed   = timeMissed; }
    public void setPatientNote(String patientNote)   { this.patientNote  = patientNote; }
    public void setMissed(boolean missed)            { this.missed       = missed; }
}