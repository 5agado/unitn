package db;

import db.beans.Bid;
import db.beans.Category;
import db.beans.Product;
import db.beans.User;
import db.beans.User.Role;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class DBManager implements Serializable {
    // sorgente del pool di connessioni

    private transient DataSource dataSource;
    public static final int BID_CORRECT = 0;
    public static final int BID_NOT_MAX = 1;
    public static final int BID_EXPIRED_AUCTION = 2;
    public static final int BID_BEATEN = 3;
    public static final int NO_LIMITS = -1;
    public static final String NO_WINNER = "";

    //costruttore
    public DBManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Product> getCurrentAuctions() throws SQLException {
        return getAuctionsByCategory(null);
    }

    public List<Product> getProductsToUpdate() throws SQLException {
        Connection connection = dataSource.getConnection();
        String query = "select * "
                + "from products "
                + "where products.expired_flag = false and products.id_prod not in (select id_prod from curr_auctions)";

        PreparedStatement queryStm = connection.prepareStatement(query);
        ResultSet rs = null;
        List<Product> list = new ArrayList<Product>();
        try {
            rs = queryStm.executeQuery();
            while (rs.next()) {
                Product p = extractProduct(rs, false);
                list.add(p);
            }
            return list;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public List<Product> getTerminatedAuctions() throws SQLException {
        Connection connection = dataSource.getConnection();
        String query = "select * "
                + "from products left outer join sales on products.id_prod = sales.id_prod "
                + "where products.id_prod not in (select id_prod from curr_auctions) "
                + "order by expiration_time desc";

        PreparedStatement queryStm = connection.prepareStatement(query);
        ResultSet rs = null;
        List<Product> list = new ArrayList<Product>();
        try {
            rs = queryStm.executeQuery();
            while (rs.next()) {
                Product p = extractSale(rs);
                list.add(p);
            }
            return list;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public void registerNewUser(User user, String password) throws SQLException {
        Connection connection = dataSource.getConnection();
        String query = "insert into users(username, password, address, email) "
                + "values (?, ?, ?, ?)";
        PreparedStatement queryStm = connection.prepareStatement(query);
        queryStm.setString(1, user.getUsername());
        queryStm.setString(2, password);
        queryStm.setString(3, user.getAddress());
        queryStm.setString(4, user.getEmail());
        try {
            queryStm.executeUpdate();
        } finally {
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    // restituisce l'utente corrispondente a user e password specificati (o null se non esiste)
    public User getUserByUsernamePassword(String username, String password) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement queryStm = connection.prepareStatement("select * from users "
                + "where username = ? and password = ?");
        ResultSet rs = null;
        User user = null;
        try {
            queryStm.setString(1, username);
            queryStm.setString(2, password);
            rs = queryStm.executeQuery();
            if (rs.next()) {
                user = extractUser(rs);
            }
            return user;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     *
     * @param usernameBuyer nome dell'utente
     * @param limit limita il numero di valori ritornati (se -1 ritorna tutti i
     * valori presenti)
     * @return restituisce gli acquisti fatti da un utente dal più recente al
     * più vecchio
     * @throws SQLException
     */
    public List<Product> getPurchasesByBuyer(String usernameBuyer, int limit) throws SQLException {
        Connection connection = dataSource.getConnection();
        String query;
        PreparedStatement queryStm;
        String queryHead = "select * from products join sales on products.id_prod = sales.id_prod where username_buyer = ? order by sales.timestamp desc ";
        if (limit == NO_LIMITS) {
            query = queryHead;
            queryStm = connection.prepareStatement(query);
        } else {
            query = queryHead + "limit ?";
            queryStm = connection.prepareStatement(query);
            queryStm.setInt(2, limit);
        }
        ResultSet rs = null;
        List<Product> list = new ArrayList<Product>();
        try {
            queryStm.setString(1, usernameBuyer);
            rs = queryStm.executeQuery();
            while (rs.next()) {
                Product sale = extractSale(rs);
                list.add(sale);
            }
            return list;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

    }

    public List<Product> getProductsBySeller(String username) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement queryStm;
        String query = "select * "
                + "from products left outer join sales on products.id_prod = sales.id_prod "
                + "where username_seller = ? "
                + "order by products.timestamp desc ";
        queryStm = connection.prepareStatement(query);
        queryStm.setString(1, username);

        ResultSet rs = null;
        List<Product> list = new ArrayList<Product>();
        try {
            rs = queryStm.executeQuery();
            while (rs.next()) {
                Product prod;
                if (rs.getBoolean("expired_flag")) {
                    prod = extractSale(rs);
                }
                else {
                    prod = extractProduct(rs, true);
                }
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
            if (connection != null) {
                connection.close();
            }
        }
    }

    public List<Bid> getBidsByBidder(String usernameBidder, int limit) throws SQLException {
        Connection connection = dataSource.getConnection();
        String query;
        PreparedStatement queryStm;
        String queryHead = "select * from bids join products on bids.id_prod = products.id_prod where username_bidder = ? order by bids.timestamp desc ";
        if (limit == NO_LIMITS) {
            query = queryHead;
            queryStm = connection.prepareStatement(query);
        } else {
            query = queryHead + "limit ? ";
            queryStm = connection.prepareStatement(query);
            queryStm.setInt(2, limit);
        }

        ResultSet rs = null;
        List<Bid> list = new ArrayList<Bid>();
        try {
            queryStm.setString(1, usernameBidder);
            rs = queryStm.executeQuery();
            while (rs.next()) {
                Bid bid = extractBid(rs);
                Product p = extractProduct(rs, false);
                bid.setProduct(p);
                list.add(bid);
            }
            return list;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

    }

    // se idCat è null cerca in tutte le categorie
    public List<Product> getAuctionsByCategory(Integer idCat) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement queryStm;
        if (idCat != null) {
            String query = "select * "
                    + "from curr_auctions "
                    + "where id_cat = ?";
            queryStm = connection.prepareStatement(query);
            queryStm.setInt(1, idCat);
        } else {
            String query = "select * "
                    + "from curr_auctions ";
            queryStm = connection.prepareStatement(query);
        }
        ResultSet rs = null;
        List<Product> list = new ArrayList<Product>();
        try {
            rs = queryStm.executeQuery();
            while (rs.next()) {
                Product prod = extractProduct(rs, true);
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
            if (connection != null) {
                connection.close();
            }
        }
    }

    public List<Product> searchAuctionsByCategory(Integer idCat, String regexp) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement queryStm;
        if (idCat != null) {
            String query = "select * "
                    + "from curr_auctions "
                    + "where id_cat = ? and description regexp ?";
            queryStm = connection.prepareStatement(query);
            queryStm.setInt(1, idCat);
            queryStm.setString(2, regexp);
        } else {
            String query = "select * "
                    + "from curr_auctions "
                    + "where description regexp ?";
            queryStm = connection.prepareStatement(query);
            queryStm.setString(1, regexp);
        }
        ResultSet rs = null;
        List<Product> list = new ArrayList<Product>();
        try {
            rs = queryStm.executeQuery();
            while (rs.next()) {
                Product prod = extractProduct(rs, true);
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
            if (connection != null) {
                connection.close();
            }
        }
    }

    public List<Product> searchAuctions(String regexp) throws SQLException {
        return searchAuctionsByCategory(null, regexp);
    }

    public List<Product> getLostAuctionsByBidder(String username, int limit) throws SQLException {
        Connection connection = dataSource.getConnection();

        String query;
        PreparedStatement queryStm;
        String queryHead = "select * from bidding_auctions where username_bidder = ? "
                + "and expired_flag = TRUE and "
                + "id_prod not in (select id_prod "
                + "                from sales "
                + "                where username_buyer = ?) "
                + "order by timestamp desc ";
        if (limit == NO_LIMITS) {
            query = queryHead;
            queryStm = connection.prepareStatement(query);
        } else {
            query = queryHead + "limit ? ";
            queryStm = connection.prepareStatement(query);
            queryStm.setInt(3, limit);
        }

        ResultSet rs = null;
        List<Product> list = new ArrayList<Product>();
        queryStm.setString(1, username);
        queryStm.setString(2, username);
        try {
            rs = queryStm.executeQuery();
            while (rs.next()) {
                Product prod = extractProduct(rs, true);
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
            if (connection != null) {
                connection.close();
            }
        }
    }

    public void createNewAuction(Product prod) throws SQLException {
        Connection connection = dataSource.getConnection();
        String query = "insert into products(quantity, description, init_price, min_price, min_increment, del_price, url_photo, expiration_time, username_seller, id_cat) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement queryStm = connection.prepareStatement(query);
        queryStm.setInt(1, prod.getQuantity());
        queryStm.setString(2, prod.getDescription());
        queryStm.setDouble(3, prod.getInitPrice());
        queryStm.setDouble(4, prod.getMinPrice());
        queryStm.setDouble(5, prod.getMinIncrement());
        queryStm.setDouble(6, prod.getDeliveryPrice());
        queryStm.setString(7, prod.getUrlPhoto());
        queryStm.setTimestamp(8, new Timestamp(prod.getExpirationTime().getTime()));
        queryStm.setString(9, prod.getSeller().getUsername());
        queryStm.setInt(10, prod.getCategory().getIdCat());
        try {
            queryStm.executeUpdate();
        } finally {
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public int makeNewBid(Bid bid) throws SQLException {
        int ris = BID_EXPIRED_AUCTION;
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        ResultSet rs = null;
        PreparedStatement queryStm = null;
        try {
            Double currPrice = getCurrentPrice(bid.getProduct().getIdProd());
            if (currPrice!=null) {
                Product p = getProductById(bid.getProduct().getIdProd());
                if (!p.getExpired()) {
                    if (bid.getBid() >= (currPrice + p.getMinIncrement())) {
                        String query = "select * from curr_auctions_prices where id_prod = ?";
                        queryStm = connection.prepareStatement(query);
                        queryStm.setInt(1, p.getIdProd());
                        rs = queryStm.executeQuery();
                        double maxPrice = 0;
                        if (rs.next()) {
                            maxPrice = rs.getDouble("curr_price");
                        }
                        query = "insert into bids (bid, username_bidder, id_prod) values (?,?,?)";
                        queryStm = connection.prepareStatement(query);
                        queryStm.setDouble(1, bid.getBid());
                        queryStm.setString(2, bid.getBidder().getUsername());
                        queryStm.setInt(3, bid.getProduct().getIdProd());
                        int affectedRows = queryStm.executeUpdate();
                        if (affectedRows != 1) {
                            throw new SQLException("Cannot insert new bid!!!");
                        }
                        if (bid.getBid()>maxPrice) {
                            ris = BID_CORRECT;
                        }
                        else {
                            ris = BID_BEATEN;
                        }
                    } else {
                        ris = BID_NOT_MAX;
                    }
                }
            }
            connection.commit();
        }
        catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection != null) {
                if (!connection.getAutoCommit()) {
                    connection.setAutoCommit(true);
                }
                connection.close();
            }
        }
        return ris;
    }
    
    public Double getCurrentPrice(int idProd) throws SQLException {
        Connection connection = dataSource.getConnection();
        String query =  "select bid, init_price, min_increment " +
                        "from products left outer join bids on products.id_prod = bids.id_prod " +
                        "where products.id_prod = ? " +
                        "order by bid desc, bids.timestamp asc " +
                        "limit 2 ";
        PreparedStatement queryStm = connection.prepareStatement(query);
        queryStm.setInt(1, idProd);
        ResultSet rs = null;
        Double maxPrice = null;
        Double maxPrice2 = null;
        Double minIncrement = null;
        Double initPrice = null;
        double currPrice = 0;
        try {
            rs = queryStm.executeQuery();
            if (rs.next()) {
                maxPrice = rs.getDouble("bid");
                if (maxPrice==0) {
                    maxPrice = null;
                }
                minIncrement = rs.getDouble("min_increment");
                initPrice = rs.getDouble("init_price");
                if (rs.next()) {
                    maxPrice2 = rs.getDouble("bid");
                    if (maxPrice2 == 0) {
                        maxPrice2 = null;
                    }
                }
            
                if (maxPrice2 != null) {
                    currPrice = Math.min(maxPrice2 + minIncrement, maxPrice);
                } 
                else if (maxPrice != null) {
                    currPrice = initPrice + minIncrement;
                }
                else {
                    currPrice = initPrice;
                }        
            }
            else {
                return null;
            }
            return currPrice;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

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
                Category cat = extractCategory(rs);
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
            if (connection != null) {
                connection.close();
            }
        }

    }

    public Product getProductById(int id) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement queryStm = connection.prepareStatement("select * from products natural join categories where id_prod = ?");
        ResultSet rs = null;
        Product prod = null;
        try {
            queryStm.setInt(1, id);
            rs = queryStm.executeQuery();
            if (rs.next()) {
                prod = extractProduct(rs, true);
            }
            return prod;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    // ritorna lo username dell'aggiudicatario dell'asta, o null se non ve ne sono
    public String updateExpiredAuction(Product prod) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement queryStm = null;
        ResultSet rs = null;
        String risult = null;
        if (prod.getExpired()) {
            return null;
        }
        if (prod.getExpirationTime().getTime() > System.currentTimeMillis()){
            return null;
        }
        try {
            connection.setAutoCommit(false);
            String query = "update products "
                    + "set expired_flag=TRUE "
                    + "where id_prod = ?";
            queryStm = connection.prepareStatement(query);
            queryStm.setInt(1, prod.getIdProd());
            queryStm.executeUpdate();
            queryStm.close();

            query = "select bid, bids.timestamp, bids.username_bidder " +
                    "from products join bids on products.id_prod = bids.id_prod " +
                    "where products.id_prod = ? " +
                    "order by bid desc, bids.timestamp asc " +
                    "limit 2 ";
            queryStm = connection.prepareStatement(query);
            queryStm.setInt(1, prod.getIdProd());
            rs = queryStm.executeQuery();
            Double maxPrice = null;
            Double maxPrice2 = null;
            if (rs.next()) {
                maxPrice = rs.getDouble("bid");
                risult = rs.getString("username_bidder");
                if (rs.next()) {
                    maxPrice2 = rs.getDouble("bid");
                }
            }
            if (maxPrice != null && maxPrice >= prod.getMinPrice()) {
                rs.close();
                queryStm.close();
                double salePrice;
                if (maxPrice2!=null) {
                    salePrice = Math.min(maxPrice2 + prod.getMinIncrement(), maxPrice);
                    salePrice = Math.max(salePrice, prod.getMinPrice());
                }
                else {
                    salePrice = Math.max(prod.getInitPrice() + prod.getMinIncrement(), prod.getMinPrice());
                }
                
                query = "insert into sales(price, del_price, tax, username_buyer, id_prod) "
                        + "values (?,?,?,?,?)";
                queryStm = connection.prepareStatement(query);
                queryStm.setDouble(1, salePrice);
                queryStm.setDouble(2, prod.getDeliveryPrice());
                double tax = salePrice * 0.0125;
                long rndPrice = (int)tax;
                if (tax>rndPrice) {
                    tax = tax - rndPrice <= 0.5 ? rndPrice + 0.5 : rndPrice + 1;
                }
                tax = Math.max(1.23, tax);
                queryStm.setDouble(3, tax);
                queryStm.setString(4, risult);
                queryStm.setInt(5, prod.getIdProd());
                queryStm.executeUpdate();
            }
            else {
                risult = NO_WINNER;
            }
            connection.commit();

        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection != null) {
                if (!connection.getAutoCommit()) {
                    connection.setAutoCommit(true);
                }
                connection.close();
            }
        }
        return risult;
    }

    // restituisce le offerte per una determinata asta dalla più alta alla più bassa
    public List<Bid> getBidsByProduct(int idProd) throws SQLException {
        Connection connection = dataSource.getConnection();
        String query = "select * "
                + "from bids join users on bids.username_bidder = users.username "
                + "where id_prod = ? "
                + "order by bid desc, bids.timestamp asc";
        PreparedStatement queryStm = connection.prepareStatement(query);
        ResultSet rs = null;
        List<Bid> list = new ArrayList<Bid>();
        try {
            queryStm.setInt(1, idProd);
            rs = queryStm.executeQuery();
            while (rs.next()) {
                Bid bid = extractBid(rs);
                User u = extractUser(rs);
                bid.setBidder(u);
                list.add(bid);
            }

            return list;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

    }

    // ritorna 1 se l'annullamento va a buon fine, 0 altrimenti (ad es. se l'asta era già scaduta)
    public int cancelAuction(int idProd) throws SQLException {
        Connection connection = dataSource.getConnection();
        String query = "update products "
                + "set canceled_flag = TRUE, expired_flag = TRUE "
                + "where id_prod = ? and expired_flag = FALSE";
        PreparedStatement queryStm = connection.prepareStatement(query);
        queryStm.setInt(1, idProd);
        try {
            int ris = queryStm.executeUpdate();
            return ris;
        } finally {
            if (queryStm != null) {
                queryStm.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private Product extractProduct(ResultSet rs, boolean currentPrice) throws SQLException {
        Product prod = new Product();
        prod.setIdProd(rs.getInt("id_prod"));
        prod.setDescription(rs.getString("description"));
        prod.setQuantity(rs.getInt("quantity"));
        prod.setInitPrice(rs.getDouble("init_price"));
        prod.setMinPrice(rs.getDouble("min_price"));
        prod.setMinIncrement(rs.getDouble("min_increment"));
        prod.setDeliveryPrice(rs.getDouble("del_price"));
        prod.setUrlPhoto(rs.getString("url_photo"));
        prod.setExpirationTime(rs.getTimestamp("expiration_time"));
        prod.setExpired(rs.getBoolean("expired_flag"));
        prod.setCanceled(rs.getBoolean("canceled_flag"));
        prod.setSeller(rs.getString("username_seller"));
        Category cat = new Category();
        cat.setIdCat(rs.getInt("id_cat"));
        prod.setCategory(cat);
        if (currentPrice) {
            prod.setPrice(getCurrentPrice(prod.getIdProd()));
        }
        return prod;
    }

    private Product extractSale(ResultSet rs) throws SQLException {
        Product prod = extractProduct(rs, false);
        prod.setPrice(rs.getDouble("price"));
        prod.setTax(rs.getDouble("tax"));
        prod.setBuyer(rs.getString("username_buyer"));
        return prod;
    }

    private Bid extractBid(ResultSet rs) throws SQLException {
        Bid bid = new Bid();
        bid.setIdBid(rs.getInt("id_bid"));
        bid.setBid(rs.getDouble("bid"));
        bid.setBidder(rs.getString("username_bidder"));
        bid.setProduct(rs.getInt("id_prod"));
        bid.setTimestamp(rs.getTimestamp("bids.timestamp"));
        return bid;
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUsername(rs.getString("username"));
        user.setAddress(rs.getString("address"));
        user.setEmail(rs.getString("email"));
        char role = rs.getString("role").charAt(0);
        switch (role) {
            case 'A':
                user.setRole(Role.ADMIN);
                break;
            case 'U':
                user.setRole(Role.USER);
        }
        return user;
    }

    private Category extractCategory(ResultSet rs) throws SQLException {
        Category cat = new Category();
        cat.setIdCat(rs.getInt("id_cat"));
        cat.setName(rs.getString("name"));
        return cat;
    }
}
