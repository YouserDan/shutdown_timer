module com.example.shutdown_timer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.shutdown_timer to javafx.fxml;
    exports com.example.shutdown_timer;
}