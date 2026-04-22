/**
 * 
 */
/**
 * 
 */
module SPS {
	// 1. Frameworks your project depends on
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    

    // 2. Allow JavaFX to access your classes (especially for Controllers/Main)
    opens model to javafx.graphics, javafx.fxml, javafx.base;
    
    // 3. Export the package so other modules can see it
    exports model;
}