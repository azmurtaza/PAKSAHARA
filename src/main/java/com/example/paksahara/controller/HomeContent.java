package com.example.paksahara.controller;
import com.example.paksahara.model.Product;
import com.example.paksahara.db.DBUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.fxml.FXMLLoader;
import java.io.File;
import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import com.example.paksahara.session.SessionManager;
import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.User;
import java.io.IOException;
import com.example.paksahara.db.DBUtils;
import com.example.paksahara.model.Product;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import com.example.paksahara.model.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.function.Consumer;
public class HomeContent implements Initializable {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private GridPane productsGrid;
    @FXML private ComboBox<String> typeFilter;
    @FXML private ComboBox<String> departmentFilter;
    @FXML private GridPane itemsGrid;

    @FXML private ImageView cardImage;
    @FXML private Label cardTitle;
    @FXML private Label cardCategory;
    @FXML private Label cardPrice;
    @FXML private Label cardStatus;
    @FXML private Button actionBtn;
    private int currentUserId;

    public void setCurrentUserId(int id) {
        this.currentUserId = id;
        loadProducts();
    }
    private Consumer<Product> onAddToCart;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCategoryFilter();
        searchField.textProperty().addListener((obs, o, n) -> loadProducts());
    }

    private void setupCategoryFilter() {
        categoryFilter.getItems().add("All Categories");
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name FROM category ORDER BY name");
             ResultSet rs = ps.executeQuery())
        {
            while (rs.next()) {
                categoryFilter.getItems().add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        categoryFilter.setValue("All Categories");
        categoryFilter.valueProperty().addListener((obs, o, n) -> loadProducts());
    }

    private void loadProducts() {
        productsGrid.getChildren().clear();
        String search = searchField.getText().toLowerCase(), cat = categoryFilter.getValue();

        String sql = """
      SELECT p.product_id, p.title, p.description, p.image_url,
             p.price, p.stock, p.category_id, c.name AS category_name, p.status
      FROM product p
      JOIN category c ON p.category_id=c.category_id
      WHERE p.status='ACTIVE'
      """ + (cat!=null && !cat.equals("All Categories") ? " AND c.name=? " : "")
                + "ORDER BY p.date_added DESC";

        try (var conn=DBUtils.getConnection();
             var ps=conn.prepareStatement(sql)) {

            if (cat!=null && !cat.equals("All Categories"))
                ps.setString(1, cat);

            var rs = ps.executeQuery();
            int row=0, col=0, maxCols=3;

            while (rs.next()) {
                String title=rs.getString("title"),
                        desc =rs.getString("description");
                if (!search.isEmpty()
                        && !title.toLowerCase().contains(search)
                        && !desc.toLowerCase().contains(search)) continue;

                Product p = new Product(
                        rs.getInt("product_id"),
                        title, desc,
                        rs.getString("image_url"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("status")
                );

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/paksahara/fxml/user_product_card.fxml")
                );
                AnchorPane card = loader.load();
                ProductCardController ctrl = loader.getController();

                // now pass a Pair<Product,qty>
                ctrl.setDataForUser(p, pair -> {
                    int uid = SessionManager.getCurrentUserId();
                    DBUtils.addToCart(uid, pair.getKey().getId(), pair.getValue());
                    // optionally refresh cart view here
                    new Alert(Alert.AlertType.INFORMATION,
                            pair.getValue() + " x " + pair.getKey().getTitle()
                                    + " added to cart.")
                            .showAndWait();
                });

                productsGrid.add(card, col, row);
                if (++col>=maxCols) { col=0; row++; }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error loading products", ex.getMessage());
        }
    }




    private VBox createProductCard(ResultSet rs) throws SQLException {
        int           id    = rs.getInt("product_id");
        String        title = rs.getString("title");
        String        desc  = rs.getString("description");
        String        img   = rs.getString("image_url");
        double        price = rs.getDouble("price");
        int           stock = rs.getInt("stock");
        String        cat   = rs.getString("category_name");
        String  dateStr = rs.getTimestamp("date_added")
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));

        // Image
        ImageView iv = new ImageView();
        iv.setFitWidth(100); iv.setFitHeight(100); iv.setPreserveRatio(true);
        try {
            Image image = img.startsWith("file:")
                    ? new Image(img)
                    : new Image(new File(img).toURI().toString());
            iv.setImage(image);
        } catch (Exception ignore) { /* leave blank */ }

        // Labels
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-weight:bold; -fx-wrap-text:true;");
        Label lblPrice = new Label("₨ " + price);
        Label lblStock = new Label(stock>0 ? "In Stock" : "Out of Stock");
        Label lblCat   = new Label(cat);
        Label lblDate  = new Label(dateStr);

        // Buttons
        Button btnDetail = new Button("View Details");
        btnDetail.setOnAction(e -> showDetailDialog(id));
        Button btnCart   = new Button("Add to Cart");
        btnCart.setDisable(stock<=0);
        btnCart.setOnAction(e -> {
            try {
                CartContent.addToCart(currentUserId, id);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            showAlert("Added to Cart", title + " has been added.");
        });

        VBox info = new VBox(4, lblTitle, lblPrice, lblStock, lblCat, lblDate, new HBox(5, btnDetail, btnCart));
        info.setPadding(new Insets(8));

        HBox card = new HBox(10, iv, info);
        card.getStyleClass().add("product-card");
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER_LEFT);

        return new VBox(card);
    }

    private void showDetailDialog(int productId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/paksahara/productDetail.fxml"));
            DialogPane pane = loader.load();
            ProductDetailController ctrl = loader.getController();
            ctrl.setProductId(productId);
            Dialog<Void> dlg = new Dialog<>();
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.getDialogPane().setContent(pane);
            dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dlg.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load product details.");
        }
    }

    private void addToCart(Product p) {
        try {
            CartContent.addToCart(currentUserId, p.getId());
            showAlert("Success", p.getTitle() + " has been added to your cart.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not add to cart.");
        }
    }


    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void loadItems() {
        itemsGrid.getChildren().clear();
        // Fetch products/posts matching filters
        List<Product> posts = DBUtils.fetchPosts(
                categoryFilter.getValue(),
                typeFilter.getValue(),
                departmentFilter.getValue(),
                searchField.getText()
        );
        int col = 0, row = 0;
        for (Product p : posts) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/paksahara/fxml/item_card.fxml"));
                Node card = loader.load();
                ItemCardController controller = loader.getController();
                controller.setData(p);
                itemsGrid.add(card, col, row);
                col++;
                if (col == 3) { col = 0; row++; }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setData(Product p, Consumer<Product> onAddToCart) {
        this.onAddToCart = onAddToCart;

        cardTitle.setText(p.getTitle());
        cardCategory.setText(p.getCategoryName());
        cardPrice.setText("₨ " + p.getPrice());
        cardStatus.setText(p.getStatus());

        // Load image
        try {
            String path = p.getImageUrl();
            if (!path.startsWith("file:")) {
                path = new File(path).toURI().toString();
            }
            Image image = new Image(path, 180, 120, true, true);
            cardImage.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
            // fallback image
            cardImage.setImage(new Image(getClass().getResource("/icon.png").toExternalForm()));
        }

        // Change button text to Add to Cart
        actionBtn.setText("Add to Cart");
        actionBtn.setOnAction(e -> {
            if (this.onAddToCart != null) this.onAddToCart.accept(p);
        });
    }

    @FXML
    private void handlePostItem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/paksahara/post_item.fxml"));
            Node postForm = loader.load();
            // Assume parent is a StackPane
            StackPane parent = (StackPane) searchField.getScene().lookup("#contentArea");
            parent.getChildren().setAll(postForm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


