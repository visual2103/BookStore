package org.example.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.event.*;

import org.example.view.model.UserDTO;

import java.util.List;

public class AdminView {
    private TableView<UserDTO> userTableView;
    private GridPane gridPane;
    private final ObservableList<UserDTO> usersObservableList;
    private TextField usernameTextField;
    //private TextField roleTextField;
    private Label usernameLabel;
    private Label roleLabel;
    private Button addUserButton;
    private Button deleteUserButton;
    private Button updateRoleButton;
    private Button libraryButton;
    private Button salesButton;
    private Button generatePDF;
    private Button logoutButton;
    private ComboBox<String> roleComboBox ;

    public AdminView(Stage primaryStage, List<UserDTO> users) {
        primaryStage.setTitle("Admin Panel");
        gridPane = new GridPane();
        initializeGridPage(gridPane);

        Scene scene = new Scene(gridPane, 720, 480);
        primaryStage.setScene(scene);
        usersObservableList = FXCollections.observableArrayList(users);
        initTableView(gridPane);
        initSaveOptions(gridPane);
        primaryStage.show();
    }

    private void initializeGridPage(GridPane gridPane) {
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
    }

    private void initTableView(GridPane gridPane) {
        userTableView = new TableView<>();
        userTableView.setPlaceholder(new Label("No users to display"));

        TableColumn<UserDTO, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<UserDTO, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        userTableView.getColumns().addAll(usernameColumn, roleColumn);
        userTableView.setItems(usersObservableList);
        gridPane.add(userTableView, 0, 0, 5, 1);
    }

    public void initSaveOptions(GridPane gridPane) {
        usernameLabel = new Label("Username:");
        gridPane.add(usernameLabel, 1, 1);

        usernameTextField = new TextField();
        gridPane.add(usernameTextField, 2, 1);

        roleLabel = new Label("Role:");
        gridPane.add(roleLabel, 1, 2);

        roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("administrator", "employee", "customer");
        gridPane.add(roleComboBox, 2, 2);


        addUserButton = new Button("  Add User  ");
        gridPane.add(addUserButton, 5, 1);

        deleteUserButton = new Button("Delete User ");
        gridPane.add(deleteUserButton, 6, 1);

        updateRoleButton = new Button("  Update Role ");
        gridPane.add(updateRoleButton, 7, 1);

        libraryButton = new Button("  LIBRARY   ");
        gridPane.add(libraryButton, 5, 2);

        salesButton = new Button("      SALES     ");
        gridPane.add(salesButton, 6, 2);

        generatePDF = new Button("Generate PDF");
        gridPane.add(generatePDF, 7, 2);
        logoutButton = new Button("Logout");
        gridPane.add(logoutButton, 4, 3);
    }

    public void addAddUserButtonListener(EventHandler<ActionEvent> addUserListener) {
        addUserButton.setOnAction(addUserListener);
    }

    public void addDeleteUserButtonListener(EventHandler<ActionEvent> deleteUserListener) {
        deleteUserButton.setOnAction(deleteUserListener);
    }

    public void addUpdateRoleButtonListener(EventHandler<ActionEvent> updateRoleListener) {
        updateRoleButton.setOnAction(updateRoleListener);
    }

    public void addLibraryButtonListener(EventHandler<ActionEvent> libraryListener) {
        libraryButton.setOnAction(libraryListener);
    }

    public void addSalesButtonListener(EventHandler<ActionEvent> salesListener) {
        salesButton.setOnAction(salesListener);
    }


    public void addLogoutButtonListener(EventHandler<ActionEvent> logoutListener) {
        logoutButton.setOnAction(logoutListener);
    }

    public void addGeneratePdfButtonListener(EventHandler<ActionEvent> generatePDFListener){
        generatePDF.setOnAction(generatePDFListener);
    }
    public void addDisplayAlertMessage(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public String getUsername() {
        return usernameTextField.getText();
    }

    public String getRole() {
        return roleComboBox.getValue();
    }

    public void addUserToObservableList(UserDTO userDTO) {
        this.usersObservableList.add(userDTO);
    }

    public void removeUserFromObservableList(UserDTO userDTO) {
        this.usersObservableList.remove(userDTO);
    }

    public TableView getUserTableView(){
        return userTableView ;
    }
    public void refreshUserInObservableList(UserDTO updatedUserDTO) {
        for (int i = 0; i < usersObservableList.size(); i++) {
            UserDTO userDTO = usersObservableList.get(i);
            if (userDTO.getUsername().equals(updatedUserDTO.getUsername())) {
                usersObservableList.set(i, updatedUserDTO);
                userTableView.refresh();
                return;
            }
        }
    }

    public Parent getRoot() {
        return gridPane;
    }
}
