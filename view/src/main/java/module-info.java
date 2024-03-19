module view {
    requires javafx.controls;
    requires javafx.fxml;
    requires Model;


    opens org.example.demo to javafx.fxml;
    exports org.example.demo;
}