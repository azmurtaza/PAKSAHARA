package com.example.paksahara.model;

import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class AdminUsers {
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;

    @FXML public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        loadUsers();
    }

    @FXML private void handleDeleteUser() {
        User u = usersTable.getSelectionModel().getSelectedItem();
        if (u==null || "ADMIN".equalsIgnoreCase(u.getRole())) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete user?");
        confirm.showAndWait().filter(b->b==ButtonType.OK).ifPresent(b->{
            DBUtils.deleteUserById(u.getId());
            loadUsers();
        });
    }

    private void loadUsers() {
        List<User> list = DBUtils.fetchAllUsers();
        ObservableList<User> data = FXCollections.observableArrayList(list);
        usersTable.setItems(data);
    }
}
