package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.CityLodgeGUI;
import model.entities.*;

import javafx.scene.control.ListView;

public class RoomDetailsController {
    @FXML
    public ImageView roomImg;
    @FXML
    public Button RentBtn;
    @FXML
    public Button MaintBtn;
    @FXML
    public Button FinMaintBtn;
    @FXML
    public Button ReturnBtn;
    @FXML
    public ListView<HiringRecord> rentalList;

    public ObservableList<HiringRecord> hiringRecords = FXCollections.observableArrayList();

    private CityLodgeGUI cityLodgeGUI;
    private Room room;

    public void setCityLodgeGUI(CityLodgeGUI cityLodgeGUI) {
        this.cityLodgeGUI = cityLodgeGUI;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

    }

    @FXML
    private void startMaintenance(javafx.event.ActionEvent actionEvent){
        room.set_roomStatus(Room.roomStatus.Maintenance);
        CityLodge.getRooms().put(room.getRoomId(), room);
        processStatus(room);
    }

    @FXML
    private void finishMaintenance(javafx.event.ActionEvent actionEvent){
        if(room instanceof StandardRoom){
            room.set_roomStatus(Room.roomStatus.Available);
        } else {
            ((Suite) room).setLastMaintenanceDate(new DateTime());
            room.set_roomStatus(Room.roomStatus.Available);
        }
        CityLodge.getRooms().put(room.getRoomId(), room);
        processStatus(room);
    }

    public void setRoom(Room room) {
        this.room = room;
        String imgUri = room.getImgUrl() == null ? "/assets/notfound.png" : room.getImgUrl();
        try {
            this.roomImg.setImage(new Image(imgUri));
        } catch (Exception e){
            this.roomImg.setImage(new Image("/assets/notfound.png"));
        }
        this.hiringRecords.addAll(room.getHiringRecords());
        this.rentalList.setItems(hiringRecords);
        this.rentalList.setCellFactory(lv -> new ListCell<HiringRecord>() {
            @Override
            public void updateItem(HiringRecord item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.getDetails());
                }
            }
        });

        processStatus(room);
    }

    private void processStatus(Room room) {
        if (room.get_roomStatus() == Room.roomStatus.Maintenance) {
            RentBtn.setDisable(true);
            MaintBtn.setDisable(true);
            ReturnBtn.setDisable(true);
            FinMaintBtn.setDisable(false);
        } else {
            FinMaintBtn.setDisable(true);
            RentBtn.setDisable(false);
            MaintBtn.setDisable(false);
            ReturnBtn.setDisable(false);
        }
    }
}
