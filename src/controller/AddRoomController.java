package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.CityLodgeGUI;
import model.Exceptions.RoomException;
import model.entities.CityLodge;
import model.entities.Room;

public class AddRoomController {

    @FXML
    public TextField numOfBedsField;
    @FXML
    public TextField roomIdField;
    private Stage dialogStage;
    private Room room;
    private boolean okClicked = false;
    private CityLodgeGUI cityLodgeGUI;

    public void setCityLodgeGUI(CityLodgeGUI cityLodgeGUI) {
        this.cityLodgeGUI = cityLodgeGUI;
    }

    public void setRoom(Room room) {
        this.room = room;

        roomIdField.setText(room.getRoomId());
        numOfBedsField.setText(String.valueOf(room.getNumBedRooms()));
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        try {
            room.setRoomId(roomIdField.getText());
            room.setNumBedRooms(Integer.parseInt(numOfBedsField.getText()));
            room = CityLodge.addStandardRoomGUI(room);

            okClicked = true;
            dialogStage.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(e.getMessage());

            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public Room getRoom() {
        return room;
    }
}
