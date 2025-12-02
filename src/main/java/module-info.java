module com.biblioteca.bibliotecadecorreos {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.biblioteca.bibliotecadecorreos to javafx.fxml;
    exports com.biblioteca.bibliotecadecorreos;
}