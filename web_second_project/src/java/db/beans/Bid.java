package db.beans;

import java.io.Serializable;
import java.sql.Timestamp;

public class Bid implements Serializable {
    private Integer idBid = null;
    private Double bid = null;
    private User bidder = null;
    private Product product = null;
    private Timestamp timestamp = null;

    /**
     * @return the idBid
     */
    public Integer getIdBid() {
        return idBid;
    }

    /**
     * @param idBid the idBid to set
     */
    public void setIdBid(Integer idBid) {
        this.idBid = idBid;
    }

    /**
     * @return the bid
     */
    public Double getBid() {
        return bid;
    }

    /**
     * @param bid the bid to set
     */
    public void setBid(Double bid) {
        this.bid = bid;
    }

    /**
     * @return the bidder
     */
    public User getBidder() {
        return bidder;
    }

    /**
     * @param bidder the bidder to set
     */
    public void setBidder(User bidder) {
        this.bidder = bidder;
    }
    
    /**
     * 
     * @param username the username of the bidder
     */
    public void setBidder(String username) {
        bidder = new User();
        bidder.setUsername(username);
    }
    

    /**
     * @return the product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * @param product the product to set
     */
    public void setProduct(Product product) {
        this.product = product;
    }
    
    /**
     * @param idProd the id of the product
     * 
     */
    public void setProduct(Integer idProd) {
        product = new Product();
        product.setIdProd(idProd);
    }

    /**
     * @return the timestamp
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    

}
