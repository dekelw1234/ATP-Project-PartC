module com.example.atpprojectpartcgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.atpprojectpartcgui to javafx.fxml;
    exports com.example.atpprojectpartcgui;
}