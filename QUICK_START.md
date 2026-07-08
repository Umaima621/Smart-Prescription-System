# QUICK START - Replace MainApp.java

## How to Complete the Integration

### Option 1: Copy-Paste (Easiest)
1. Open `src/ui/MainApp_Unified.java`
2. Select all content (Ctrl+A)
3. Copy (Ctrl+C)
4. Open `src/ui/MainApp.java`
5. Select all content (Ctrl+A)
6. Paste (Ctrl+V)
7. Remove the `// UNIFIED MAINAPP - READY TO REPLACE...` comment from line 1
8. Save

### Option 2: Delete & Rename
1. Delete `src/ui/MainApp.java`
2. Rename `src/ui/MainApp_Unified.java` to `MainApp.java`

---

## Test the Integration

### 1. Compile
```bash
cd c:\Users\atcom\Desktop\Smart_Prescription
javac -cp "lib/javafx-sdk-17.0.10/lib/*:." src/**/*.java
```

### 2. Run
```bash
java -cp "lib/javafx-sdk-17.0.10/lib/*:src" --module-path lib/javafx-sdk-17.0.10/lib --add-modules javafx.controls,javafx.fxml ui.MainApp
```

### 3. Test Login
**First time:**
- Click "Get Started with MediCare"
- Select role (Doctor or Patient)
- Enter any ID (e.g., DR-00142 or PT-9920)
- Enter any password

---

## Apply UITheme to Remaining Views

Follow this pattern for CalendarView, ConfirmIntakeView, and ReportView:

### Find & Replace
1. **Colors**: 
   - `Color.web("#ffffff")` → `Color.web(UITheme.BG_SURFACE)`
   - `"#2D7A6B"` → `UITheme.TEAL_PRIMARY`
   - Any hardcoded hex color → UITheme constant

2. **Fonts**:
   - `Font.font("System", FontWeight.BOLD, 18)` → `UITheme.fontHeading4()`
   - `Font.font("System", 13)` → `UITheme.fontSmall()`
   - `Font.font("System", 14)` → `UITheme.fontBody()`

3. **Styles**:
   - `-fx-background-color: #f8f9fa;` → UITheme.getMainContainerStyle()
   - `-fx-background-color: #ffffff;` → UITheme.BG_SURFACE

### Example Changes

**Before:**
```java
Label title = new Label("Calendar");
title.setFont(Font.font("System", FontWeight.BOLD, 18));
title.setTextFill(Color.web("#1a1a2e"));
```

**After:**
```java
Label title = new Label("Calendar");
title.setFont(UITheme.fontHeading4());
title.setTextFill(UITheme.colorTextPrimary());
```

---

## Import Statements Needed

Add to top of each View file:
```java
import ui.UITheme;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
```

---

## All 14 Use Cases - Access Paths

### Patient Login
1. **UC06** - Confirm Intake: Dashboard → "Confirm Intake"
2. **UC08** - Reports: Dashboard → "Reports"
3. **UC09** - Calendar: Dashboard → "Calendar Schedule"
4. **UC10** - Daily Checklist: Dashboard → "Daily Checklist" (default)

### Doctor Login
1. **UC01** - Account: Sidebar → "Account Settings"
2. **UC11** - Missed Medicines: Sidebar → "Missed Medicines"
3. **UC12** - Prescribe: Sidebar → "Prescribe Medicine"
4. **UC13** - Send Prescription: (Included in UC12)
5. **UC14** - Patient Records: Sidebar → "Patient Records"

### Shared
- Welcome screen with animated design
- Login with role selection
- Role-based dashboard
- Account management for both roles

---

## Expected File Structure After Completion

```
Smart_Prescription/
├── src/
│   ├── ui/
│   │   ├── UITheme.java                    ✅ NEW
│   │   ├── MainApp.java                    ✅ UPDATED (from MainApp_Unified)
│   │   ├── MainApp_Unified.java            (can delete after copying)
│   │   ├── DailyChecklistView.java         ✅ THEMED
│   │   ├── CalendarView.java               (needs UITheme)
│   │   ├── ConfirmIntakeView.java          (needs UITheme)
│   │   ├── ReportView.java                 (needs UITheme)
│   │   ├── MissedMedicinesView.java        ✅ NEW
│   │   ├── PrescribeMedicineView.java      ✅ NEW
│   │   ├── PatientRecordView.java          ✅ NEW
│   │   ├── AccountManagementView.java      ✅ NEW
│   ├── controller/
│   │   ├── NotificationService.java
│   │   ├── ReportController.java
│   │   └── ...
│   ├── model/
│   │   ├── MedicineIntake.java
│   │   ├── Doctor.java                     ✅ NEW
│   │   ├── Patient.java (if needed)
│   │   └── ...
│   ├── dao/
│   │   ├── BaseDAO.java
│   │   ├── IntakeLogDAO.java
│   │   └── ...
│   └── db/
│       └── DatabaseConnection.java
├── lib/
│   ├── javafx-sdk-17.0.10/
│   ├── javafx-sdk-26.0.1/
│   └── ...
├── INTEGRATION_GUIDE.md                    ✅ NEW
└── Schema.sql
```

---

## Success Indicators

After completing integration, you should see:
- ✅ Welcome screen with MediCare branding
- ✅ Login with Doctor/Patient toggle
- ✅ Professional teal color scheme (#2D7A6B)
- ✅ Consistent Segoe UI font throughout
- ✅ Role-specific dashboards with stats
- ✅ All sidebar navigation works
- ✅ All screens follow the same theme
- ✅ Database connection works (or graceful error message)

---

## Need Help?

Refer to:
1. `INTEGRATION_GUIDE.md` - Complete integration details
2. `src/ui/UITheme.java` - All available theme constants
3. `src/ui/DailyChecklistView.java` - Example of themed view
4. `src/ui/MainApp_Unified.java` - Example of unified structure

---

**Ready? Let's merge it all together! 🎉**
