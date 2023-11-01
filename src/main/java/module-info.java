module com.example.expressionevaluator {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.expressionevaluator to javafx.fxml;
    exports com.example.expressionevaluator;
}