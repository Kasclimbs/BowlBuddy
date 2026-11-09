module edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto to javafx.fxml;
    opens edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.controller to javafx.fxml;
    exports edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto;
}