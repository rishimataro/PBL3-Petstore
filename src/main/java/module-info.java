module com.store.app.petstore {
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires mysql.connector.java;
    requires transitive javafx.controls;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.desktop;
    requires javafaker;
    requires transitive java.sql;
    requires jbcrypt;
    requires io.github.cdimascio.dotenv.java;
    requires junit;
    requires itextpdf;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    // requires org.testng;

    opens com.store.app.petstore to javafx.fxml;

    opens com.store.app.petstore.Models.Records to javafx.base;
    exports com.store.app.test to junit;
    exports com.store.app.petstore;
    exports com.store.app.petstore.Views;
    exports com.store.app.petstore.Controllers;
    exports com.store.app.petstore.Models;
    exports com.store.app.petstore.Models.Entities;
    exports com.store.app.petstore.Controllers.Admin.Statistic;

    opens com.store.app.petstore.Controllers.Admin.Statistic to javafx.fxml;
    opens com.store.app.petstore.Controllers to javafx.fxml;
    opens com.store.app.petstore.Views to javafx.fxml;
    opens com.store.app.petstore.Models to javafx.fxml;
    opens com.store.app.petstore.Controllers.Admin to javafx.fxml;
    opens com.store.app.petstore.Controllers.Staff to javafx.fxml;

    exports com.store.app.petstore.Models.Seeder;

    opens com.store.app.petstore.Models.Seeder to javafx.fxml;
    opens com.store.app.test to org.testng;

    exports com.store.app.petstore.Controllers.Admin; // Added to allow TestNG access
}
