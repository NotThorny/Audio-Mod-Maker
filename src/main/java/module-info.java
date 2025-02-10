module dev.thorny {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires jave.core;
    requires com.google.gson;
    requires java.desktop;
    requires javafx.graphics;
    requires org.hildan.fxgson;
    requires javafx.base;
    requires org.apache.commons.io;
    requires zip4j;

    opens dev.thorny to javafx.fxml;
    opens dev.thorny.user to com.google.gson;
    opens dev.thorny.data to com.google.gson;

    exports dev.thorny.user;
    exports dev.thorny.data;
    exports dev.thorny;
}
