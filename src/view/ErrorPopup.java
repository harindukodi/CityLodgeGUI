package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ErrorPopup {
    public static void display(String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Error");
        window.setWidth(260);
        window.setHeight(150);
        Label label = new Label(message);
        label.setPrefWidth(240);
        label.setFont(Font.font("Courier", 15));
        label.setPadding(new Insets(11, 12, 13, 14));
        label.setAlignment(Pos.TOP_CENTER);
        label.setWrapText(true);

        Pane pane = new Pane(label);
        Scene scene = new Scene(pane);
        window.setScene(scene);
        window.showAndWait();
    }
}