package org.example.controller;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;


import javafx.stage.FileChooser;
import org.example.laucher.AdminComponentFactory;
import org.example.model.Sales;
import org.example.service.user.AuthenticationServiceMySQL;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.example.database.DataBaseConnectionFactory;
import org.example.mapper.BookMapper;
import org.example.mapper.SalesMapper;
import org.example.model.Book;
import org.example.model.Role;
import org.example.model.User;
import org.example.model.builder.UserBuilder;
import org.example.model.validator.Notification;
import org.example.repository.security.RightsRolesRepository;
import org.example.repository.security.RightsRolesRepositoryMySQL;
import org.example.repository.user.UserRepository;
import org.example.repository.user.UserRepositoryMySQL;
import org.example.service.book.BookService;
import org.example.service.sales.SalesService;
import org.example.service.security.RightsRolesServiceImplementation;
import org.example.service.user.AuthenticationService;
import org.example.service.user.UserService;
import org.example.service.user.UserServiceImplementation;
import org.example.view.AdminView;
import org.example.view.BookView;
import org.example.view.LoginView;
import org.example.view.SalesView;
import org.example.view.model.BookDTO;
import org.example.view.model.SalesDTO;
import org.example.view.model.UserDTO;
import org.example.view.model.builder.UserDTOBuilder;
import org.example.service.security.RightsRolesService;
import org.example.laucher.EmployeeComponentFactory;
import org.example.laucher.LoginComponentFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;



import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


public class AdminController {
    private final AdminView adminView ;
    private final SalesView salesView ;
    private final UserService userService ;
    private final SalesService salesService ;
    private final BookService bookService ;
    private final AuthenticationService authenticationService;
    private final RightsRolesService rightsRolesService;
    // e bai mare ? e repositoritoy si nu am voie sa l accsez , service ar trebui sa se ocupe de asta

    public AdminController ( AdminView adminView , AuthenticationService authenticationService, UserService userService ,  SalesService salesService ,BookService bookService , SalesView salesView , RightsRolesService rightsRolesService){
        this.adminView = adminView ;
        this.userService = userService ;
        this.authenticationService = authenticationService;
        this.adminView.addAddUserButtonListener(new AddButtonListener());
        this.adminView.addDeleteUserButtonListener(new RemoveButtonListener());
        this.adminView.addUpdateRoleButtonListener(new UpdateRoleButtonListener());
        this.adminView.addLibraryButtonListener(new LibraryButtonListener());
        this.adminView.addSalesButtonListener(new SalesButtonListener());
        this.adminView.addLogoutButtonListener(new LogoutButtonListener());
        this.adminView.addGeneratePdfButtonListener(new GeneratePdfButtonListener());

        this.salesService = salesService ;
        this.bookService = bookService ;
        this.salesView = salesView ;
        this.rightsRolesService = rightsRolesService;


    }

    public class AddButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            String username = adminView.getUsername();
            String roleName = adminView.getRole();

            if (username.isEmpty() || roleName.isEmpty()){
                adminView.addDisplayAlertMessage("Error", "Incomplete fields", "Please fill in all fields.");
                return;
            }

            // verific daca rolul exista
            Role role = rightsRolesService.findRoleByTitle(roleName);
            if (role == null) {
                adminView.addDisplayAlertMessage("Error", "Invalid Role", "Selected role does not exist.");
                return;
            }

            List<Role> roles = new ArrayList<>();
            roles.add(role);

            User user = new UserBuilder()
                    .setUsername(username)
                    .setPassword("defaultPassword") // 'change password' future development !!!!!!
                    .setRoles(roles)
                    .build();

            Notification<Boolean> saveNotification = userService.addUser(user);

            if (saveNotification.hasErrors()) {
                adminView.addDisplayAlertMessage("Error", "Add failed", saveNotification.getFormattedErrors());
            } else {
                adminView.addDisplayAlertMessage("Success", "User added", "User added successfully.");

                // Actualizăm interfața
                UserDTO userDTO = new UserDTOBuilder()
                        .setId(user.getId())
                        .setUsername(user.getUsername())
                        .setRole(roleName)
                        .build();
                adminView.addUserToObservableList(userDTO);
            }
        }
    }

    public class RemoveButtonListener implements EventHandler<ActionEvent>{

        @Override
        public void handle(ActionEvent event) {
            UserDTO selectedUser = (UserDTO) adminView.getUserTableView().getSelectionModel().getSelectedItem() ;
            if (selectedUser != null){
                Notification<Boolean> deleteNotification = userService.deleteUser(selectedUser.getId());
                if (deleteNotification.hasErrors()) {
                    adminView.addDisplayAlertMessage("Error", "Problem at Deletion", deleteNotification.getFormattedErrors());
                } else {
                    adminView.addDisplayAlertMessage("Success", "User deleted", "The user was successfully deleted.");
                    adminView.removeUserFromObservableList(selectedUser);
                }
            } else {
                adminView.addDisplayAlertMessage("Delete Error", "No Selection", "Please select a user to delete.");
                return;
            }
        }
    }
    public class UpdateRoleButtonListener implements EventHandler<ActionEvent>{

        @Override
        public void handle(ActionEvent event) {
            UserDTO selectedUser = (UserDTO) adminView.getUserTableView().getSelectionModel().getSelectedItem() ;
            String newRole = adminView.getRole();
            if (selectedUser == null || newRole.isEmpty()) {
                adminView.addDisplayAlertMessage("Error", "Incomplete information", "Please select a user and enter the new role.");
                return;
            }

            Notification<Boolean> updateNotification = userService.updateUserRole(selectedUser.getId(), newRole);

            if (updateNotification.hasErrors()) {
                adminView.addDisplayAlertMessage("Error ", "Update failed", updateNotification.getFormattedErrors());
            } else {
                adminView.addDisplayAlertMessage("Success", "Updated Role ", "User role updated successfully.");
                selectedUser.setRole(newRole);
                adminView.refreshUserInObservableList(selectedUser);
            }
        }
    }
    private class LibraryButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {

            Stage primaryStage = new Stage() ;
            List<Book> books = bookService.findAll();
            List<BookDTO> bookDTOs = BookMapper.convertBookListToBookDTOList(books);

            BookView bookView = new BookView(primaryStage, bookDTOs);

            new BookController(bookView, bookService,salesView, rightsRolesService,userService, salesService, authenticationService);

            Parent root = bookView.getRoot();
            System.out.println("LibraryButtonListener: root is " + (root != null ? "not null" : "null"));
            primaryStage.getScene().setRoot(root);
        }

    }

    private class SalesButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Stage salesStage = new Stage();
            List<SalesDTO> salesList = SalesMapper.convertSaleListToSalesDTOList(salesService.getAllSales());

            SalesView salesView = new SalesView(salesStage, salesList);
            new SalesController(salesView, salesService ,bookService , authenticationService,userService , rightsRolesService);
        }
    }

    private class LogoutButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            authenticationService.logout();
            Stage stage = (Stage) adminView.getRoot().getScene().getWindow();
            System.out.println("alina2");

            LoginView loginView = new LoginView(stage);
            System.out.println("alina3");
            //
            Connection connection = DataBaseConnectionFactory.getConnectionWrapper(false).getConnection();
            if (connection == null) {
                System.out.println("error connection");
            }


            RightsRolesRepository rightsRolesRepository = new RightsRolesRepositoryMySQL(connection);
            RightsRolesService rightsRolesService = new RightsRolesServiceImplementation(rightsRolesRepository);
            UserRepository userRepository = new UserRepositoryMySQL(connection, rightsRolesRepository);
            UserService userService = new UserServiceImplementation(userRepository , rightsRolesRepository);

            AuthenticationService authenticationService = new AuthenticationServiceMySQL(userRepository, rightsRolesRepository);

            new LoginController(loginView, authenticationService,rightsRolesService);
            stage.getScene().setRoot(loginView.getRoot());
            System.out.println("alina6");
        }
    }

    private class GeneratePdfButtonListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            String username = adminView.getUsername();

            if (username == null || username.isEmpty()) {
                adminView.addDisplayAlertMessage("Input Error", "Username field is empty", "Please enter a username.");
                return;
            }

            // Obține utilizatorul pe baza numelui de utilizator
            Notification<User> userNotification = userService.findByUsername(username);
            if (userNotification.hasErrors()) {
                adminView.addDisplayAlertMessage("User Not Found", "The username does not exist.", userNotification.getFormattedErrors());
                return;
            }

            User user = userNotification.getResult();

            // Verifică dacă utilizatorul are rolul de "employee"
            boolean isEmployee = user.getRoles().stream()
                    .anyMatch(role -> role.getRole().equalsIgnoreCase("employee"));

            if (!isEmployee) {
                adminView.addDisplayAlertMessage("Invalid User", "The specified user is not an employee.", null);
                return;
            }

            // Obține vânzările efectuate de acest angajat
            List<Sales> sales = salesService.findSalesByUserID(user.getId());

            if (sales.isEmpty()) {
                adminView.addDisplayAlertMessage("No Sales", "This employee has not made any sales.", null);
                return;
            }

            // Generează PDF-ul cu vânzările
            try {
                generateSalesPdf(user, sales);
                adminView.addDisplayAlertMessage("PDF Generated", "The sales report has been generated successfully.", null);
            } catch (Exception e) {
                e.printStackTrace();
                adminView.addDisplayAlertMessage("PDF Generation Error", "An error occurred while generating the PDF.", e.getMessage());
            }
        }
    }

    // Metodă pentru a genera PDF-ul
    private void generateSalesPdf(User user, List<Sales> sales) throws Exception {
        // Folosește iText 7 pentru generarea PDF-ului
        String pdfFileName = "SalesReport_" + user.getUsername() + ".pdf";

        // Creează un FileChooser pentru a permite utilizatorului să aleagă locația de salvare
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Sales Report");
        fileChooser.setInitialFileName(pdfFileName);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File pdfFile = fileChooser.showSaveDialog(adminView.getRoot().getScene().getWindow());

        if (pdfFile == null) {
            // Utilizatorul a anulat salvarea
            return;
        }

        // Creează PDF-ul
        PdfWriter writer = new PdfWriter(pdfFile);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Adaugă titlul
        document.add(new Paragraph("Sales Report for Employee: " + user.getUsername())
                .setBold()
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph(" ")); // Linie goală

        // Crează un tabel pentru a afișa vânzările
        float[] columnWidths = {3, 3, 1, 2, 2};
        Table table = new Table(columnWidths);
        table.setWidth(UnitValue.createPercentValue(100));

        // Adaugă antetul tabelului
        table.addHeaderCell(new Cell().add(new Paragraph("Book Title").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Author").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Qty").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Total Price").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Sale Date").setBold()));

        // Adaugă datele vânzărilor în tabel
        for (Sales sale : sales) {
            table.addCell(sale.getBookTitle());
            table.addCell(sale.getBookAuthor());
            table.addCell(String.valueOf(sale.getQuantity()));
            table.addCell(String.format("%.2f", sale.getTotalPrice()));
            table.addCell(sale.getSaleDate().toString());
        }

        // Adaugă tabelul în document
        document.add(table);

        // Închide documentul
        document.close();
    }



}