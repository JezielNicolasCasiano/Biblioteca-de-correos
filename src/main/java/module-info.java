module com.biblioteca.bibliotecadecorreos {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.zaxxer.hikari;
    requires java.sql;
    requires java.desktop;


    opens com.library.emaillibrary to javafx.fxml;
    exports com.library.emaillibrary;
    exports com.library.emaillibrary.controller;
    opens com.library.emaillibrary.controller to javafx.fxml;
}