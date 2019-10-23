package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import main.CityLodgeGUI;
import model.entities.CityLodge;
import model.entities.Room;
import model.entities.StandardRoom;
import model.entities.Suite;

public class RootLayoutController {
    @FXML
    public MenuItem returnHome;
    // Reference to the main application
    private CityLodgeGUI cityLodgeGUI;

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param cityLodgeGUI
     */
    public void setCityLodgeGUI(CityLodgeGUI cityLodgeGUI) {
        this.cityLodgeGUI = cityLodgeGUI;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
//        returnHome.setVisible(false);
//        returnHome.setDisable(true);
    }

    /**
     * Closes the application.
     */
    @FXML
    public void handleExit() {
        System.exit(0);
    }

    public void handleAddRoom(ActionEvent actionEvent) {
        Room tempRoom = new StandardRoom();
        AddRoomController addRoomController = cityLodgeGUI.showAddRoomDialog(tempRoom);
        if (addRoomController != null && addRoomController.isOkClicked()) {
            cityLodgeGUI.getRoomData().add(addRoomController.getRoom());
        }
    }

    public void handleAddSuite(ActionEvent actionEvent) {
        Room tempRoom = new Suite();
        AddSuiteController addSuiteController = cityLodgeGUI.showAddSuiteDialog(tempRoom);
        if (addSuiteController != null && addSuiteController.isOkClicked()) {
            cityLodgeGUI.getRoomData().add(addSuiteController.getRoom());
        }
    }

    public void handleImport(ActionEvent actionEvent) {
    }

    public void handleExport(ActionEvent actionEvent) {
    }

    public void enableReturnHome(){
        returnHome.setVisible(true);
        returnHome.setDisable(false);
    }

    public void goHome(ActionEvent actionEvent){
        cityLodgeGUI.showRooms();
    }

    public void handleSave(ActionEvent actionEvent) {
        CityLodge.hsqlDataReader.SaveState(CityLodge.roomCollection);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(cityLodgeGUI.getPrimaryStage());
        alert.setTitle("Save to Database");
        alert.setHeaderText("Operation Success");
        alert.setContentText("Successfully saved the current data to database.");

        alert.showAndWait();
    }
}
