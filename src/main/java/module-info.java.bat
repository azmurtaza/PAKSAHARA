module com.example.paksahara {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.paksahara to javafx.fxml;
    exports com.example.paksahara;
}