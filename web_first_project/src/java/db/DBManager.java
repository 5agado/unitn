package db;

import db.User.Role;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import utilities.Pair;

public class DBManager implements Serializable {
    // sorgente del pool di connessioni
    private transient DataSource dataSource;

    //costruttore
    public DBManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // restituisce l'utente corrispondente a user e password specificati (o null se non esiste)
    public User getUserByUsernamePassword(String username, String password) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement queryStm = connection.prepareStatement("select * from users where username = ? and password = ?");
        ResultSet rs = null;
        User user = null;
        try {
            queryStm.setString(1, username);
            queryStm.setString(2, password);
            rs = queryStm.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id_u"));
                user.setUsername(username);
                char role = rs.getString("role").charAt(0);
                switch (role) {
                    case 'b':
                        user.setRole(Role.BUYER);
                        break;
                    case 's':
                        user.setRole(Role.SELLER);
                }
            }
            return user;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection!=null) {
                connection.close();
            }
        }
    }

    // restituisce l'utente corrispondente all'id specificato (o null se non esiste)
    public User getUserById(int id) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement queryStm = connection.prepareStatement("select * from users where id_u = ?");
        ResultSet rs = null;
        User user = null;
        try {
            queryStm.setInt(1, id);
            rs = queryStm.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id_u"));
                user.setUsername(rs.getString("username"));
                char role = rs.getString("role").charAt(0);
                switch (role) {
                    case 'b':
                        user.setRole(Role.BUYER);
                        break;
                    case 's':
                        user.setRole(Role.SELLER);
                }
            }
            return user;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection!=null) {
                connection.close();
            }
        }
    }
    
    // restituisce il prodotto corrispondente all'id specificato (o null se non esiste)
    public Product getProductById(int id) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement queryStm = connection.prepareStatement("select * from products where id_pr = ?");
        ResultSet rs = null;
        Product prod = null;
        try {
            queryStm.setInt(1, id);
            rs = queryStm.executeQuery();
            if (rs.next()) {
                prod = new Product();
                prod.setId(rs.getInt("id_pr"));
                prod.setName(rs.getString("name"));
                prod.setQuantity(rs.getInt("quantity"));
                prod.setUm(rs.getString("um"));
                prod.setPrice(rs.getDouble("price"));
                prod.setPhoto(rs.getInt("id_photo"));
                prod.setCategory(rs.getInt("id_cat"));
                prod.setSeller(rs.getInt("id_seller"));
                prod.setOnSale(rs.getBoolean("on_sale"));
            }
            return prod;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection!=null) {
                connection.close();
            }
        }
    }
    
    // restituisce la categoria corrispondente all'id specificato (o null se non esiste)
    public Category getCategoryById(int id) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement queryStm = connection.prepareStatement("select * from categories where id_cat = ?");
        ResultSet rs = null;
        Category cat = null;
        try {
            queryStm.setInt(1, id);
            rs = queryStm.executeQuery();
            if (rs.next()) {
                cat = new Category();
                cat.setId(rs.getInt("id_cat"));
                cat.setName(rs.getString("name_cat"));
            }
            return cat;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection!=null) {
                connection.close();
            }
        }
    }
    
    // restituisce la foto corrispondente all'id specificato (o null se non esiste)
    public Photo getPhotoById(int id) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement queryStm = connection.prepareStatement("select * from photos where id_photo = ?");
        ResultSet rs = null;
        Photo photo = null;
        try {
            queryStm.setInt(1, id);
            rs = queryStm.executeQuery();
            if (rs.next()) {
                photo = new Photo();
                photo.setId(rs.getInt("id_photo"));
                photo.setUrl(rs.getString("url"));
                photo.setName(rs.getString("name"));
            }
            return photo;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection!=null) {
                connection.close();
            }
        }
    }
    
    // restituisce l'elenco dei prodotti della categoria specificata
    public List<Product> getProductsByCategory(int catId) throws SQLException {
        Connection connection = dataSource.getConnection();
        String query = "select * " +
                       "from products " +
                       "where id_cat = ? and on_sale = true";
        PreparedStatement queryStm = connection.prepareStatement(query);
        ResultSet rs = null;
        List<Product> list = new ArrayList<Product>();
        try {
            queryStm.setInt(1, catId);
            rs = queryStm.executeQuery();
            while (rs.next()) {
                Product prod = new Product();
                prod.setId(rs.getInt("id_pr"));
                prod.setName(rs.getString("name"));
                prod.setQuantity(rs.getInt("quantity"));
                prod.setUm(rs.getString("um"));
                prod.setPrice(rs.getDouble("price"));
                prod.setPhoto(rs.getInt("id_photo"));
                prod.setCategory(rs.getInt("id_cat"));
                prod.setSeller(rs.getInt("id_seller"));
                prod.setOnSale(rs.getBoolean("on_sale"));
                list.add(prod);
            }
            return list;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection!=null) {
                connection.close();
            }
        }
    }

    // restituisce l'elenco dei prodotti messi in vendita dal seller specificato
    public List<Product> getProductsBySeller(int sellerId) throws SQLException {
        Connection connection = dataSource.getConnection();
        String query = "select * " +
                       "from products " +
                       "where id_seller = ?";
        PreparedStatement queryStm = connection.prepareStatement(query);
        ResultSet rs = null;
        List<Product> list = new ArrayList<Product>();
        try {
            queryStm.setInt(1, sellerId);
            rs = queryStm.executeQuery();
            while (rs.next()) {
                Product prod = new Product();
                prod.setId(rs.getInt("id_pr"));
                prod.setName(rs.getString("name"));
                prod.setQuantity(rs.getInt("quantity"));
                prod.setUm(rs.getString("um"));
                prod.setPrice(rs.getDouble("price"));
                prod.setPhoto(rs.getInt("id_photo"));
                prod.setCategory(rs.getInt("id_cat"));
                prod.setSeller(sellerId);
                prod.setOnSale(rs.getBoolean("on_sale"));
                list.add(prod);
            }
            return list;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection!=null) {
                connection.close();
            }
        }

    }

    // restituisce l'elenco di tutte le categorie disponibili
    public List<Category> getCategories() throws SQLException {
        Connection connection = dataSource.getConnection();
        String query = "select * " 
                + "from categories";
        PreparedStatement queryStm = connection.prepareStatement(query);
        ResultSet rs = null;
        List<Category> list = new ArrayList<Category>();
        try {
            rs = queryStm.executeQuery();
            while (rs.next()) {
                Category cat = new Category();
                cat.setId(rs.getInt("id_cat"));
                cat.setName(rs.getString("name_cat"));

                list.add(cat);

            }
            return list;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection!=null) {
                connection.close();
            }
        }

    }

    
    // restituisce l'elenco di tutte le fotografie disponibili
    public List<Photo> getPhotos() throws SQLException {
        Connection connection = dataSource.getConnection();
        String query = "select * " 
                + "from photos";
        PreparedStatement queryStm = connection.prepareStatement(query);
        ResultSet rs = null;
        List<Photo> list = new ArrayList<Photo>();
        try {
            rs = queryStm.executeQuery();
            while (rs.next()) {
                Photo photo = new Photo();
                photo.setId(rs.getInt("id_photo"));
                photo.setUrl(rs.getString("url"));
                photo.setName(rs.getString("name"));
                list.add(photo);

            }
            return list;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection!=null) {
                connection.close();
            }
        }

    }
    
    // restituisce le coppie (url_pdf, timestamp) associate all'utente specificato
    public List<Pair<String, String>> getPdfUrlsAndTimestampsByBuyer(int buyerId) throws SQLException {
        Connection connection = dataSource.getConnection();
        String query = "select url_pdf, time_stamp " +
                       "from purchases " +
                       "where id_buyer = ? " +
                       "order by time_stamp desc";
        PreparedStatement queryStm = connection.prepareStatement(query);
        ResultSet rs = null;
        List<Pair<String, String>> list = new ArrayList<Pair<String, String>>();
        try {
            queryStm.setInt(1, buyerId);
            rs = queryStm.executeQuery();
            while (rs.next()) {
                Pair<String, String> pair = new Pair<String, String>();
                pair.setFirst(rs.getString("url_pdf"));
                pair.setSecond(rs.getString("time_stamp"));
                list.add(pair);
            }
            return list;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection!=null) {
                connection.close();
            }
        }

    }
    
    
    // restituisce tutte le url dei pdf appartenenti all'utente specificato
    public List<String> getPdfUrlsByBuyer(int buyerId) throws SQLException {
        Connection connection = dataSource.getConnection();
        String query = "select url_pdf " +
                       "from purchases " +
                       "where id_buyer = ?";
        PreparedStatement queryStm = connection.prepareStatement(query);
        ResultSet rs = null;
        List<String> list = new ArrayList<String>();
        try {
            queryStm.setInt(1, buyerId);
            rs = queryStm.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("url_pdf"));
            }
            return list;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection!=null) {
                connection.close();
            }
        }

    }
    

    // aggiunge al db l'oggetto purchase passato come parametro
    public void addPurchase(Purchase purchase) throws SQLException {
        Connection connection = dataSource.getConnection();
        String query = "insert into purchases(quantity, url_pdf, id_buyer, id_product) "
                + "values (?, ?, ?, ?)";
        PreparedStatement queryStm = connection.prepareStatement(query);
        try {
            queryStm.setInt(1, purchase.getQuantity());
            queryStm.setString(2, purchase.getUrlPdf());
            queryStm.setInt(3, purchase.getBuyer());
            queryStm.setInt(4, purchase.getProduct());
            int updatedRows = queryStm.executeUpdate();
            if (updatedRows != 1) {
                throw new RuntimeException("Error inserting new purchase");
            }
            query = "update products set on_sale = false where id_pr = ?";
            queryStm.close();
            queryStm = connection.prepareStatement(query);
            queryStm.setInt(1, purchase.getProduct());
            updatedRows = queryStm.executeUpdate();
            if (updatedRows != 1) {
                throw new RuntimeException("Error inserting new purchase");
            }
        } finally {
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection!=null) {
                connection.close();
            }
        }
    }

    // aggiunge al db l'oggetto product passato come parametro
    public void addProduct(Product product) throws SQLException {
        Connection connection = dataSource.getConnection();
        String query = "insert into products(name, quantity, um, price, id_photo, id_cat, id_seller) "
                + "values (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement queryStm = connection.prepareStatement(query);
        try {
            queryStm.setString(1, product.getName());
            queryStm.setInt(2, product.getQuantity());
            queryStm.setString(3, product.getUm());
            queryStm.setDouble(4, product.getPrice());
            int photoId = product.getPhoto();
            if (photoId==0) {
                queryStm.setNull(5, Types.INTEGER);
            }
            else {
                queryStm.setInt(5, photoId);
            }
            queryStm.setInt(6, product.getCategory());
            queryStm.setInt(7, product.getSeller());

            int addedRows = queryStm.executeUpdate();
            if (addedRows != 1) {
                throw new RuntimeException("Error inserting new product");
            }

        } finally {
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection!=null) {
                connection.close();
            }
        }
    }
    
    
}
