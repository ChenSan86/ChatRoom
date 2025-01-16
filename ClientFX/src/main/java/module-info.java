module ClientFX {
    requires javafx.controls;
    requires javafx.fxml;
    opens com.chensan.client.viewFX to javafx.fxml;
    exports com.chensan.client.viewFX;
    opens com.chensan.common to javafx.base;
}