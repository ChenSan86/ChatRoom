module com.example.serverfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;


    opens com.chensan.server.wechatframe to javafx.fxml;
    exports com.chensan.server.wechatframe;
}