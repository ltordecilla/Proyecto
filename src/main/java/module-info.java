
module com.ferreteria {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    requires com.github.librepdf.openpdf;

    exports com.ferreteria;
    opens com.ferreteria.controllers to javafx.fxml;
}
