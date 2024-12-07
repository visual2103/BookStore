package org.example.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import org.example.laucher.EmployeeComponentFactory;
import org.example.laucher.*;
import org.example.model.CurrentUser;
import org.example.model.Role;
import org.example.model.User;
import org.example.model.validator.Notification;
import org.example.service.security.RightsRolesService;
import org.example.service.user.AuthenticationService;
import org.example.view.LoginView;
import org.example.laucher.AdminComponentFactory;


import java.util.List;


public class LoginController {
 
    private final LoginView loginView;
    private final AuthenticationService authenticationService;
    private final RightsRolesService rightsRolesService;

    public LoginController(LoginView loginView, AuthenticationService authenticationService , RightsRolesService rightsRolesService) {
        this.loginView = loginView;
        this.authenticationService = authenticationService;
        this.rightsRolesService = rightsRolesService ;
        this.loginView.addLoginButtonListener(new LoginButtonListener());
        this.loginView.addRegisterButtonListener(new RegisterButtonListener());
    }

    private class LoginButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(javafx.event.ActionEvent event) {
            System.out.println("Login button clicked.");
            String username = loginView.getUsername();
            String password = loginView.getPassword();
            System.out.println("Attempting to login with username: " + username);

            Notification<User> loginNotification = authenticationService.login(username, password);

            if (loginNotification.hasErrors()){
                loginView.setActionTargetText(loginNotification.getFormattedErrors());
                System.out.println("Login failed: " + loginNotification.getFormattedErrors());

            }else{
                User user = loginNotification.getResult();
                System.out.println("Login successful for user: " + user.getUsername());
                CurrentUser.set(user);
                // Presupunem că utilizatorul poate avea mai multe roluri; le vom verifica pe toate
                List<Role> roles = user.getRoles();
                System.out.println("User roles: " + roles);

                Stage primaryStage = (Stage) loginView.getRoot().getScene().getWindow();

                if (roles.stream().anyMatch(role -> role.getRole().equals("administrator"))) {
                    AdminComponentFactory.getInstance(primaryStage);
                } else if (roles.stream().anyMatch(role -> role.getRole().equals("employee"))) {
                    EmployeeComponentFactory.getInstance(LoginComponentFactory.getComponentsForTests(),primaryStage);
                } else if (roles.stream().anyMatch(role -> role.getRole().equals("customer"))) {
                    CustomerComponentFactory.getInstance(primaryStage);
                } else {
                    loginView.setActionTargetText("User has no valid role assigned.");
                    System.out.println("User has no valid role assigned.");
                }
            }
        }
    }

    private class RegisterButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            String username = loginView.getUsername();
            String password = loginView.getPassword();

            Notification<Boolean> registerNotification = authenticationService.register(username, password);

            if (registerNotification.hasErrors()) {
                loginView.setActionTargetText(registerNotification.getFormattedErrors());
            } else {
                loginView.setActionTargetText("Register successful!");
            }
        }
    }
}