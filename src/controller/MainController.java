package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.CityLodgeGUI;
import model.entities.Room;

import javafx.scene.control.ListView;

public class MainController {
    private CityLodgeGUI cityLodgeGUI;
    @FXML
    private ListView<Room> roomList;

    public void setCityLodgeGUI(CityLodgeGUI cityLodgeGUI) {
        this.cityLodgeGUI = cityLodgeGUI;
        roomList.setPrefWidth(1000.0);
        roomList.setPrefHeight(600.0);
        roomList.setItems(cityLodgeGUI.getRoomData());
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        roomList.setCellFactory(param -> new ListCell<Room>() {
            @Override
            protected void updateItem(Room room, boolean empty){
                super.updateItem(room, empty);
                if(room==null || empty){
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox();
                    ImageView imageView = new ImageView();
                    String imgUri = room.getImgUrl() == null ? "/assets/notfound.png" : room.getImgUrl();
                    try {
                        imageView.setImage(new Image(imgUri));
                    } catch (Exception e){
                        imageView.setImage(new Image("/assets/notfound.png"));
                    }
                    VBox vBox = new VBox();
                    vBox.setAlignment(Pos.CENTER_LEFT);
                    vBox.getChildren().add(new Label(String.format("Room ID:             %s", room.getRoomId())));
                    vBox.getChildren().add(new Label(String.format("Room Type:           %s", room.get_roomType())));
                    vBox.getChildren().add(new Label(String.format("Number of bedrooms:  %s", room.getNumBedRooms())));
                    vBox.getChildren().add(new Label(String.format("Room Status:         %s", room.get_roomStatus())));
                    vBox.getChildren().add(new Label(String.format("Number of bedrooms:  %s", room.getFeatureSummary())));
                    Button viewButton = new Button("View Room");
                    viewButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            cityLodgeGUI.showRoomDetails(room);
                        }
                    });

                    vBox.getChildren().add(viewButton);
                    hbox.getChildren().add(imageView);
                    hbox.getChildren().add(vBox);
                    setGraphic(hbox);
                }
            }
        });
    }
}
