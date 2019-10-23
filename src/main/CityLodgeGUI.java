package main;

import controller.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.CityLodge;
import model.entities.Room;
import view.ErrorPopup;

import java.io.IOException;

public class CityLodgeGUI extends Application {

    private ObservableList<Room> roomData = FXCollections.observableArrayList();
    private Stage primaryStage;
    private BorderPane rootLayout;

    public CityLodgeGUI() {
    }

    @Override
    public void start(Stage primaryStage){
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("City Lodge");
        primaryStage.show();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace();
            ErrorPopup.display(throwable.getMessage());
        });

        CityLodge.init();
        roomData.addAll(CityLodge.hsqlDataReader.getRoomData().values());

        initRootLayout();
        showRooms();
    }

    public ObservableList<Room> getRoomData() {
        return roomData;
    }

    /**
     * Initializes the root layout and tries to load the last opened
     * person file.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CityLodgeGUI.class
                    .getResource("../view/rootLayout.fxml"));
            rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // Give the controller access to the main app.
            RootLayoutController controller = loader.getController();
            controller.setCityLodgeGUI(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        // Try to load last opened person file.
//        File file = getPersonFilePath();
//        if (file != null) {
//            loadPersonDataFromFile(file);
//        }
    }

    public void showRooms() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CityLodgeGUI.class.getResource("../view/main.fxml"));
            VBox personOverview = loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(personOverview);

            // Give the controller access to the main app.
            MainController controller = loader.getController();
            controller.setCityLodgeGUI(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showRoomDetails(Room room) {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CityLodgeGUI.class.getResource("../view/roomDetails.fxml"));
            VBox roomDetails = loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(roomDetails);

            // Give the controller access to the main app.
            RoomDetailsController controller = loader.getController();
            controller.setCityLodgeGUI(this);
            controller.setRoom(room);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public AddRoomController showAddRoomDialog(Room room) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CityLodgeGUI.class.getResource("../view/addRoom.fxml"));
            AnchorPane page = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Standard Room");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            AddRoomController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setRoom(room);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public AddSuiteController showAddSuiteDialog(Room room) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CityLodgeGUI.class.getResource("../view/addSuite.fxml"));
            AnchorPane page = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Suite");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            AddSuiteController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setRoom(room);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
