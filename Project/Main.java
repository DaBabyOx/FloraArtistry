package Project;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import jfxtras.scene.control.window.Window;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage){
        new Login(primaryStage);}
    public static void main(String[]a){launch(a);}}

class DBConnect{
    private static final String URL = "jdbc:mysql://localhost:3306/floraartistry";
    private static final String USER = "-";
    private static final String PASSWORD ="-";

    public static Connection connect() throws SQLException{
        return DriverManager.getConnection(URL, USER, PASSWORD);}

    public static boolean emailCheck(String email) throws SQLException{
        String query = "SELECT UserEmail FROM MsUser WHERE UserEmail = ?";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1,email);
            ResultSet rs = ps.executeQuery();
            return rs.next();}}

    public static boolean validateLogin(String email, String password) throws SQLException{
        String query = "SELECT COUNT(*) FROM MsUser WHERE UserEmail = ? AND UserPassword = ?";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1)>0;}}
        return false;}

    public static String getUserRole(String email, String password) throws SQLException{
        String query = "SELECT UserRole FROM MsUser WHERE UserEmail = ? AND UserPassword = ?";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("UserRole");}}
        return null;}

    public static void saveUser(String username, String email, String password, String address, String phone) throws SQLException {
        String userID = generateNextID("MsUser", "UserID", "US");
        String query = "INSERT INTO MsUser(UserID, UserName, UserEmail, UserPassword, UserAddress, UserPhonenumber, UserRole) VALUES (?, ?, ?, ?, ?, ?, 'Customer')";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, userID);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, address);
            ps.setString(6, phone);
            ps.executeUpdate();} catch (SQLException e) {
            throw new RuntimeException(e);}}

    public static String generateNextID(String table, String column, String prefix) throws SQLException{
        String query = "SELECT MAX("+column+") FROM "+table;
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                String lastID = rs.getString(1);
                if(lastID != null){
                    int idn = Integer.parseInt(lastID.replace(prefix, ""));
                    return prefix + String.format("%03d", idn+1);}}}
        return prefix + "001";}

    public static ObservableList<Flowers> getFlowers() throws SQLException{
        ObservableList<Flowers> flowers = FXCollections.observableArrayList();
        String query = "SELECT FlowerName, FlowerType, FlowerPrice FROM MsFlower";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String name = rs.getString("FlowerName");
                String type = rs.getString("FlowerType");
                int price = rs.getInt("FlowerPrice");

                flowers.add(new Flowers(name, type, price));}}
    return flowers;}

    public static String getuName(String email) throws SQLException{
        String query = "SELECT UserName FROM MsUser WHERE UserEmail = ?";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("UserName");}}
        return null;}

    public static void updateFlower(Flowers flowers) throws SQLException{
        String query = "UPDATE MsFlower SET FlowerPrice = ? WHERE FlowerName = ?";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, flowers.getPrice());
            ps.setString(2, flowers.getName());
            ps.executeUpdate();}}

    public static void deleteFlower(Flowers flowers) throws SQLException{
        String query = "DELETE FROM MsFlower WHERE FlowerName = ?";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, flowers.getName());
            ps.executeUpdate();}}

    public static void addFlower(Flowers flowers) throws SQLException{
        String FlowerID = generateNextID("MsFlower", "FlowerID", "FL");
        String query = "INSERT INTO MsFlower(FlowerID, FlowerName, FlowerType, FlowerPrice) VALUES (?, ?, ?, ?)";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, FlowerID);
            ps.setString(2, flowers.getName());
            ps.setString(3, flowers.getType());
            ps.setInt(4, flowers.getPrice());
            ps.executeUpdate();} catch (SQLException e) {
            throw new RuntimeException(e);}}

    public static String getUID(String Email) throws SQLException{
        String query = "SELECT UserID FROM MsUser WHERE UserEmail = ?";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, Email);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("UserID");}}
        return null;}

    public static String getFID(String flowerName) throws SQLException{
        String query = "SELECT FlowerID FROM MsFlower WHERE FlowerName = ?";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, flowerName);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("FlowerID");}}
        return null;}

    public static void addCart(String UID, String FID, int Quantity) throws SQLException{
        String query = "INSERT INTO MsCart(UserID, FlowerID, Quantity) VALUES (?, ?, ?)";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, UID);
            ps.setString(2, FID);
            ps.setInt(3, Quantity);
            ps.executeUpdate();} catch (SQLException e) {
            throw new RuntimeException(e);}}

    public static ObservableList<Carts> getCarts(String CID) throws SQLException{
        ObservableList<Carts> carts = FXCollections.observableArrayList();
        String query = "SELECT MsFlower.FlowerName, MsFlower.FlowerType, MsFlower.FlowerPrice, MsCart.Quantity, (MsFlower.FlowerPrice * MsCart.Quantity) AS Subtotal FROM MsCart JOIN MsFlower ON MsCart.FlowerID = MsFlower.FlowerID WHERE MsCart.UserID = ?";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, CID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String name = rs.getString("FlowerName");
                String type = rs.getString("FlowerType");
                int price = rs.getInt("FlowerPrice");
                int quantity = rs.getInt("Quantity");
                int subtotal = rs.getInt("Subtotal");

                carts.add(new Carts(name, type, price, quantity, subtotal));}
        return carts;}}

    public static void createTID(String CID) throws SQLException{
        String TID = generateNextID("TransactionHeader", "TransactionID", "TR");
        String query = "INSERT INTO TransactionHeader (TransactionID, UserID) VALUES (?, ?)";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, TID);
            ps.setString(2, CID);
            ps.executeUpdate();} catch (SQLException e) {
            throw new RuntimeException(e);}
        String query2 = "INSERT INTO TransactionDetail (TransactionID, FlowerID, Quantity) SELECT ?, FlowerID, Quantity FROM MsCart WHERE UserID = ?";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query2)){
            ps.setString(1, TID);
            ps.setString(2, CID);
            ps.executeUpdate();}
        deleteCart(CID);}

    public static void deleteCart(String CID) throws SQLException{
        String query = "DELETE FROM MsCart WHERE UserID = ?";
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, CID);
            ps.executeUpdate();}}
}

class AppMenuBar{
    public static MenuBar createMenuBar(Stage primaryStage, String page){
        MenuBar mb = new MenuBar();

        String MenuTitle;
        if("AdminDashboard".equals(page)){
            MenuTitle = "Account";}
        else{
            MenuTitle = "Page";}

        Menu pagee = new Menu(MenuTitle);

        if("BuyFlower".equals(page) || "Cart".equals(page)){
            MenuItem buyFlower = new MenuItem("Buy Flower");
            MenuItem cart = new MenuItem("Cart");
            MenuItem logout = new MenuItem("Log Out");

            buyFlower.setOnAction(e -> new BuyFlower(primaryStage));
            cart.setOnAction(e -> new Cart(primaryStage));
            logout.setOnAction(e -> {new Login(primaryStage);});

            pagee.getItems().addAll(buyFlower, cart, logout);}

        else if("AdminDashboard".equals(page)){
            MenuItem logout = new MenuItem("Log Out");

            logout.setOnAction(e -> {new Login(primaryStage);});

            pagee.getItems().addAll(logout);}

        else if("Login".equals(page) || "Register".equals(page)){
            MenuItem login = new MenuItem("Login");
            MenuItem register = new MenuItem("Register");

            login.setOnAction(e -> {new Login(primaryStage);});
            register.setOnAction(e -> {new Register(primaryStage);});

            pagee.getItems().addAll(login, register);}

        mb.getMenus().add(pagee);

        return mb;}}

class Login{
    public Login(Stage primaryStage){
        MenuBar mb = AppMenuBar.createMenuBar(primaryStage, "Login");

        Label title = new Label("Login");
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

        Label emailLabel = new Label("Email");
        TextField emailField = new TextField();
        emailField.setMaxWidth(300);

        VBox emailLayout = new VBox(5, emailLabel, emailField);
        emailLayout.setAlignment(Pos.CENTER_LEFT);

        Label passwordLabel = new Label("Password");
        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(300);

        VBox passwordLayout = new VBox(5, passwordLabel, passwordField);
        passwordLayout.setAlignment(Pos.CENTER_LEFT);

        Button loginButton = new Button("Login");
        loginButton.setMinWidth(300);
        loginButton.setOnAction(e ->{
            String email = emailField.getText();
            String password = passwordField.getText();

            if(email.isEmpty() || password.isEmpty()){
                showFieldError("Email and Password must be filled");}
            else{
                try{
                    if(DBConnect.validateLogin(email, password)){
                        String userRole = DBConnect.getUserRole(email, password);
                        emailSession.getInstance().setEmail(email);
                        if(userRole != null){
                            if(userRole.equals("Admin")){
                                new AdminDashboard(primaryStage);}
                            else{
                                new BuyFlower(primaryStage);}}
                        else{
                            showError("Incorrect email or password!");}}
                    else{
                        showError("Incorrect email or password!");}
                }catch(SQLException ex){
                    showError("An error occurred!");}}});

        VBox formLayout = new VBox(10, emailLayout, passwordLayout, loginButton);
        formLayout.setAlignment(Pos.CENTER);

        HBox centering = new HBox(formLayout);
        centering.setAlignment(Pos.CENTER);

        VBox forms = new VBox(20, title, centering);
        forms.setAlignment(Pos.CENTER);

        VBox layout = new VBox (10, mb, forms);
        Scene scene = new Scene(layout, 1280, 720);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();}

    private void showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Error");
        alert.setHeaderText("Credential Error!");
        alert.show();}

    private void showFieldError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Error");
        alert.setHeaderText("Validation Error!");
        alert.show();}}

class Register{
    public Register(Stage primaryStage){
        MenuBar mb = AppMenuBar.createMenuBar(primaryStage, "Register");

        Label title = new Label("Register");
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

        Label EmailLabel = new Label("Email:");
        TextField EmailField = new TextField();

        Label UserName = new Label("Username:");
        TextField UserNameField = new TextField();

        Label PasswordLabel = new Label("Password:");
        PasswordField PasswordField = new PasswordField();

        Label ConfirmPasswordLabel = new Label("Confirm Password:");
        PasswordField ConfirmPasswordField = new PasswordField();

        Label AddressLabel = new Label("Address:");
        TextField AddressField = new TextField();
        AddressField.setMinHeight(75);

        Label PhoneLabel = new Label("Phone Number:");
        TextField PhoneField = new TextField();

        CheckBox agreement = new CheckBox("I agree to create an account");

        Button registerButton = new Button("Register");
        registerButton.setMinWidth(330);
        registerButton.setOnAction(e ->{
            String email = EmailField.getText();
            String username = UserNameField.getText();
            String password = PasswordField.getText();
            String confirmPassword = ConfirmPasswordField.getText();
            String address = AddressField.getText();
            String phone = PhoneField.getText();

            if(email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || address.isEmpty() || phone.isEmpty()){
                showFieldError("All fields must be filled");
                return;}

            if(!agreement.isSelected()){
                showFieldError("You must agree to create an account");
                return;}

            if(!password.equals(confirmPassword)){
                showFieldError("Password and Confirm Password must be the same");
                return;}

            if (username.length() < 4 || username.length() > 20) {
                showError("Username must be between 4 and 20 characters.");
                return;}

            if (password.length() < 8) {
                showError("Password must be 8 or more characters.");
                return;}

            if (!phone.matches("\\d+")) {
                showError("Phone Number must be numeric.");
                return;}

            if (phone.length() < 8 || phone.length() > 20) {
                showError("Phone Number must be between 8 and 20 numbers.");
                return;}

            if(!email.contains("@") || !email.contains(".")){
                showError("Invalid email format");
                return;}

            try{
                if(DBConnect.emailCheck(email)){
                    showFieldError("Email already registered");
                    return;}
                else{
                    DBConnect.saveUser(username, email, password, address, phone);
                    showSuccess("Registration Successful");
                    new Login(primaryStage);}
            }catch (SQLException ex){
                showError("Database error: "+ex.getMessage());}});

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);

        gp.add(EmailLabel, 0, 1);
        gp.add(EmailField, 1, 1);

        gp.add(UserName, 0, 2);
        gp.add(UserNameField, 1, 2);

        gp.add(PasswordLabel, 0, 3);
        gp.add(PasswordField, 1, 3);

        gp.add(ConfirmPasswordLabel, 0, 4);
        gp.add(ConfirmPasswordField, 1, 4);

        gp.add(AddressLabel, 0, 5);
        gp.add(AddressField, 1, 5);

        gp.add(PhoneLabel, 0, 6);
        gp.add(PhoneField, 1, 6);

        gp.add(agreement, 0, 7);

        HBox forms = new HBox(10, gp);
        forms.setAlignment(Pos.CENTER);
        VBox formsLabelButtonLayout = new VBox(10, title, forms, registerButton);
        formsLabelButtonLayout.setAlignment(Pos.CENTER);
        VBox layout = new VBox(10, mb, formsLabelButtonLayout);
        Scene scene = new Scene(layout, 1280, 720);
        primaryStage.setTitle("Registration");
        primaryStage.setScene(scene);
        primaryStage.show();}

    private void showFieldError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Error");
        alert.setHeaderText("Validation Error!");
        alert.show();}

    private void showSuccess(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle("Success");
        alert.setHeaderText("Registration Success!");
        alert.showAndWait();}

    private void showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Error");
        alert.setHeaderText("Credential Error!");
        alert.show();}}

class BuyFlower{
    private TableView<Flowers> FlowerTable;
    private String email;

    private StackPane sp;
    public BuyFlower(Stage primaryStage){
        email = emailSession.getInstance().getEmail();
        MenuBar mb = AppMenuBar.createMenuBar(primaryStage, "BuyFlower");

        Label title = new Label("Product List");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label welc = new Label();
        welc.setStyle("-fx-font-size: 16px;");
        try{
            String uName = DBConnect.getuName(email);
            welc.setText("Welcome, "+uName);
        }catch (SQLException ex){
            welc.setText("Welcome, Guest");}


        FlowerTable = createTable();
        FlowerTable.setPrefHeight(500);

        VBox productLayout = new VBox(10, title, welc, FlowerTable);
        productLayout.setAlignment(Pos.TOP_LEFT);
        productLayout.setPadding(new Insets(10));

        VBox flowerLayout = createDetailLayout();

        HBox mainLayout = new HBox(30, productLayout, flowerLayout);
        mainLayout.setPadding(new Insets(20));

        try{
            ObservableList<Flowers> flowers = DBConnect.getFlowers();
            FlowerTable.setItems(flowers);
        }catch(SQLException ex){
            showError("Error Loading: " + ex.getMessage());}

        sp = new StackPane();
        VBox root = new VBox(10, mb, mainLayout);
        sp.getChildren().add(root);
        Scene scene = new Scene(sp, 1280, 720);
        primaryStage.setTitle("Buy Flower");
        primaryStage.setScene(scene);
        primaryStage.show();}


    private TableView<Flowers> createTable(){
        TableView<Flowers> table = new TableView<>();
        TableColumn<Flowers, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMinWidth(150);

        TableColumn<Flowers, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setMinWidth(150);

        TableColumn<Flowers, Integer> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setMinWidth(100);

        table.getColumns().addAll(nameCol, typeCol, priceCol);
        return table;}


    private VBox createDetailLayout(){
        Label title = new Label("Flower Detail");
        title.setStyle("-fx-font-size: 24px;");

        Label flowerNameLabel = new Label("Flower Name:");
        Label flowerTypeLabel = new Label("Flower Type:");
        Label flowerPriceLabel = new Label("Flower Price:");

        Label flowerName = new Label();
        Label flowerType = new Label();
        Label flowerPrice = new Label();

        FlowerTable.setOnMouseClicked(e -> {
            Flowers flowers = FlowerTable.getSelectionModel().getSelectedItem();
            if (flowers != null) {
                flowerName.setText(flowers.getName());
                flowerType.setText(flowers.getType());
                flowerPrice.setText(String.valueOf(flowers.getPrice()));
            } else {
                System.out.println("Loading");}});

        Button addToCart = new Button("Add to Cart");
        addToCart.setOnAction(e -> {
            if (flowerName.getText().isEmpty()){
                showError("No flower selected.");}
            else{
                addtoCart(flowerName.getText(), flowerType.getText(), Integer.parseInt(flowerPrice.getText()));}
        });

        VBox flowerLayout = new VBox(10, title,new HBox(10, flowerNameLabel, flowerName), new HBox(10, flowerTypeLabel, flowerType), new HBox(10, flowerPriceLabel, flowerPrice), addToCart);
        flowerLayout.setAlignment(Pos.TOP_LEFT);
        flowerLayout.setPadding(new Insets(10));
        flowerLayout.setMinWidth(300);
        return flowerLayout;}

    private void addtoCart(String flowerName, String flowerType, int flowerPrice){

        Window popUp = new Window("Add to Cart");
        popUp.getLeftIcons().clear();
        popUp.setPrefSize(1280, 720);

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);

        Label NameLabel = new Label("Flower Name: "+ flowerName);
        Label TypeLabel = new Label("Flower Type: "+ flowerType);
        Label PriceLabel = new Label("Flower Price: "+ flowerPrice);
        Label quantityLabel = new Label("Quantity: ");
        Spinner<Integer> quantity = new Spinner<>(1, 100, 1);
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        quantity.setValueFactory(valueFactory);

        Button atc = new Button("Add to Cart");
        atc.setMinWidth(250);

        Button cancel = new Button("Cancel");
        cancel.setMinWidth(200);

        gp.add(NameLabel, 0, 0);
        gp.add(TypeLabel, 0, 1);
        gp.add(PriceLabel, 0, 2);
        gp.add(quantityLabel, 0, 3);
        gp.add(quantity, 1, 3);
        gp.add(atc, 0, 4);
        gp.add(cancel, 1, 4);

        atc.setOnAction(e -> {
            try{
                String UID = DBConnect.getUID(email);
                String FID = DBConnect.getFID(flowerName);
                int Quantity = quantity.getValue();
                DBConnect.addCart(UID, FID, Quantity);
                showSuccess("Flower added to cart successfully");
                sp.getChildren().remove(popUp);
            }catch(SQLException ex){
                showError("Add to Cart Failed: "+ex.getMessage());}});

        cancel.setOnAction(e -> sp.getChildren().remove(popUp));

        VBox layout = new VBox(10, gp);
        popUp.getContentPane().getChildren().add(layout);
        sp.getChildren().add(popUp);

       popUp.toFront();}

    private void showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Error");
        alert.setHeaderText("Add to Cart Failed");
        alert.showAndWait();}

    private void showSuccess(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle("Message");
        alert.setHeaderText("Add to Cart");
        alert.showAndWait();}}

class Cart{
    private TableView<Carts> CartTable;
    private String email;
    private Label grandTotal;

    public Cart(Stage primaryStage){
        email = emailSession.getInstance().getEmail();
        MenuBar mb = AppMenuBar.createMenuBar(primaryStage, "Cart");

        Label title = new Label("Your Cart List");
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

        CartTable = createTable();
        CartTable.setPrefHeight(500);

        Button checkout = new Button("Checkout");
        checkout.setMinWidth(600);
        checkout.setOnAction(e -> {
            try{
                String CID = DBConnect.getUID(email);
                ObservableList<Carts> carts = DBConnect.getCarts(CID);
                if(carts.isEmpty()){
                    showError("No items in cart.");}
                else{
                    showSuccess("All items checkout successfully.");
                    DBConnect.createTID(CID);
                    CartTable.setItems(DBConnect.getCarts(CID));
                    gt();}
            }catch(SQLException ex){
                showError("Checkout Failed: "+ex.getMessage());}});

        VBox mainlayout = new VBox(10, title, CartTable, checkout);

        VBox labelLayout = labelLayout();

        try{
            String CID = DBConnect.getUID(email);
            ObservableList<Carts> carts = DBConnect.getCarts(CID);
            CartTable.setItems(carts);
            gt();
        }catch (SQLException ex){
            showError("Error Loading: " + ex.getMessage());}

        HBox duallayout = new HBox(30, mainlayout, labelLayout);
        duallayout.setPadding(new Insets(20));

        VBox layout = new VBox(10, mb, duallayout);
        layout.setAlignment(Pos.TOP_LEFT);
        Scene scene = new Scene(layout, 1280, 720);
        primaryStage.setTitle("Cart");
        primaryStage.setScene(scene);
        primaryStage.show();}

    private TableView<Carts> createTable(){
        TableView<Carts> table = new TableView<>();
        TableColumn<Carts, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMinWidth(150);

        TableColumn<Carts, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setMinWidth(150);

        TableColumn<Carts, Integer> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setMinWidth(100);

        TableColumn<Carts, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setMinWidth(100);

        TableColumn<Carts, Integer> subtotalCol = new TableColumn<>("Total");
        subtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        subtotalCol.setMinWidth(100);

        table.getColumns().addAll(nameCol, typeCol, priceCol, quantityCol, subtotalCol);
        return table;}

    private VBox labelLayout(){
        Label flowerNameLabel = new Label("Name:");
        Label flowerTypeLabel = new Label("Type:");
        Label flowerPriceLabel = new Label("Price:");
        Label flowerQuantityLabel = new Label("Quantity:");
        Label flowerSubtotalLabel = new Label("Subtotal:");
        Label flowerGrandTotal = new Label("Grand Total:");

        Label flowerName = new Label();
        Label flowerType = new Label();
        Label flowerPrice = new Label();
        Label flowerQuantity = new Label();
        Label flowerSubtotal = new Label();
        grandTotal = new Label();

        CartTable.setOnMouseClicked(e -> {
            Carts carts = CartTable.getSelectionModel().getSelectedItem();
            if (carts != null) {
                flowerName.setText(carts.getName());
                flowerType.setText(carts.getType());
                flowerPrice.setText(String.valueOf(carts.getPrice()));
                flowerQuantity.setText(String.valueOf(carts.getQuantity()));
                flowerSubtotal.setText(String.valueOf(carts.getSubtotal()));
            } else {
                System.out.println("Loading");}});

        VBox flowerLayout = new VBox(10, new HBox(10, flowerNameLabel, flowerName), new HBox(10, flowerTypeLabel, flowerType), new HBox(10, flowerPriceLabel, flowerPrice), new HBox(10, flowerQuantityLabel, flowerQuantity), new HBox(10, flowerSubtotalLabel, flowerSubtotal), new HBox(10, flowerGrandTotal, grandTotal));
        flowerLayout.setAlignment(Pos.TOP_LEFT);
        flowerLayout.setPadding(new Insets(10));
        flowerLayout.setMinWidth(300);
        return flowerLayout;}

    private void gt(){
        try{
            String CID = DBConnect.getUID(email);
            ObservableList<Carts> carts = DBConnect.getCarts(CID);
            int gtv = carts.stream().mapToInt(Carts::getSubtotal).sum();
            grandTotal.setText(String.valueOf(gtv));
        }catch (SQLException ex){
            showError("Error Loading: " + ex.getMessage());}}

    private void showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Error");
        alert.setHeaderText("Checkout Failed");
        alert.showAndWait();}

    private void showSuccess(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle("Message");
        alert.setHeaderText("Checkout Successful");
        alert.showAndWait();}}

class AdminDashboard{
    private String email;
    private TableView<Flowers> FlowerTable;

    public AdminDashboard(Stage primaryStage){
        email = emailSession.getInstance().getEmail();
        MenuBar mb = AppMenuBar.createMenuBar(primaryStage, "AdminDashboard");

        Label title = new Label("Flower List");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label welc = new Label();
        welc.setStyle("-fx-font-size: 16px;");
        try{
            String uName = DBConnect.getuName(email);
            welc.setText("Welcome, "+uName);
        }catch (SQLException ex){
            welc.setText("Welcome, Admin");}

        FlowerTable = createTable();
        FlowerTable.setPrefHeight(500);

        VBox buttonLayout = createButtonLayout();

        VBox productLayout = new VBox(10, title, welc, FlowerTable);
        productLayout.setAlignment(Pos.TOP_LEFT);
        productLayout.setPadding(new Insets(10));

        try{
            ObservableList<Flowers> flowers = DBConnect.getFlowers();
            FlowerTable.setItems(flowers);
        }catch(SQLException ex){
            showError("Error Loading: " + ex.getMessage());}

        HBox mainLayout = new HBox(30, productLayout, buttonLayout);
        mainLayout.setPadding(new Insets(20));

        VBox layout = new VBox(10, mb, mainLayout);
        Scene scene = new Scene(layout, 1280, 720);
        primaryStage.setTitle("Manage Products");
        primaryStage.setScene(scene);
        primaryStage.show();}

    private TableView<Flowers> createTable(){
        TableView<Flowers> table = new TableView<>();
        TableColumn<Flowers, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMinWidth(150);

        TableColumn<Flowers, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setMinWidth(150);

        TableColumn<Flowers, Integer> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setMinWidth(100);

        table.getColumns().addAll(nameCol, typeCol, priceCol);
        return table;}

    private VBox createButtonLayout(){
        Label title = new Label("Flower Details");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label flowerNameLabel = new Label("Flower Name:");
        TextField flowerName = new TextField();

        Label flowerTypeLabel = new Label("Flower Type:");
        TextField flowerType = new TextField();

        Label flowerPriceLabel = new Label("Flower Price:");
        TextField flowerPrice = new TextField();

        FlowerTable.setOnMouseClicked(e -> {
            Flowers flowers = FlowerTable.getSelectionModel().getSelectedItem();
            if (flowers != null) {
                flowerName.setText(flowers.getName());
                flowerType.setText(flowers.getType());
                flowerPrice.setText(String.valueOf(flowers.getPrice()));
            } else {
                System.out.println("Loading");}});

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);

        gp.add(flowerNameLabel, 0, 1);
        gp.add(flowerName, 1, 1);

        gp.add(flowerTypeLabel, 0, 2);
        gp.add(flowerType, 1, 2);

        gp.add(flowerPriceLabel, 0, 3);
        gp.add(flowerPrice, 1, 3);

        Button updateButton = new Button("Update Flower");
        updateButton.setMinWidth(230);
        updateButton.setOnAction(e -> {
            if(flowerName.getText().isEmpty() || flowerType.getText().isEmpty() || flowerPrice.getText().isEmpty()){
                showError("No flower selected.");}
            else{
                try{
                    int price = Integer.parseInt(flowerPrice.getText());
                    if(price < 0){
                        showError("Price must be more than 0");}
                    else{
                        Flowers flowers = new Flowers(flowerName.getText(), "", price);
                        DBConnect.updateFlower(flowers);
                        showSuccess("Update Successful");
                        FlowerTable.setItems(DBConnect.getFlowers());}
                }catch(NumberFormatException ex){
                    showError("Flower Price must be a valid number.");
                }catch(SQLException ex){
                    showError("Update Failed: "+ex.getMessage());}}});

        Button deleteButton = new Button("Delete Flower");
        deleteButton.setMinWidth(230);
        deleteButton.setOnAction(e -> {
            if(flowerName.getText().isEmpty() || flowerType.getText().isEmpty() || flowerPrice.getText().isEmpty()){
                showError("No flower selected.");}
            else{
                try{
                    Flowers flowers = new Flowers(flowerName.getText(), "", 0);
                    DBConnect.deleteFlower(flowers);
                    showSuccess("Delete Successful");
                    FlowerTable.setItems(DBConnect.getFlowers());
                }catch(SQLException ex){
                    showError("Delete Failed: "+ex.getMessage());}}});

        Button addButton = new Button("Add Flower");
        addButton.setMinWidth(230);
        addButton.setOnAction(e -> {
            if(flowerName.getText().isEmpty() || flowerType.getText().isEmpty() || flowerPrice.getText().isEmpty()){
                showError("All fields must be filled");}
            else{
                try{
                    int price = Integer.parseInt(flowerPrice.getText());
                    if(price < 0){
                        showError("Price must be more than 0");}
                    else{
                        Flowers flowers = new Flowers(flowerName.getText(), flowerType.getText(), price);
                        DBConnect.addFlower(flowers);
                        showSuccess("Add Successful");
                        FlowerTable.setItems(DBConnect.getFlowers());}
                }catch(NumberFormatException ex){
                    showError("Flower Price must be a valid number.");
                }catch(SQLException ex){
                    showError("Add Failed: "+ex.getMessage());}}});


        VBox layouts = new VBox(10, title, gp, updateButton, deleteButton, addButton);
        layouts.setAlignment(Pos.TOP_LEFT);
        layouts.setPadding(new Insets(10));
        layouts.setMinWidth(300);

        return layouts;}

    private void showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Error");
        alert.setHeaderText("Update Failed");
        alert.showAndWait();}

    private void showSuccess(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle("Message");
        alert.setHeaderText("Add Successful");
        alert.showAndWait();}}