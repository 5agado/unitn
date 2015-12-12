/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db.beans;

import java.beans.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author paolo
 */
public class Product implements Serializable {

    private Integer idProd = null;
    private Integer quantity = null;
    private String description = null;
    private Double initPrice = null;
    private Double minPrice = null;
    private Double minIncrement = null;
    private Double deliveryPrice = null;
    private String urlPhoto = null;
    private Date expirationTime = null;
    private Boolean expired = null;
    private Boolean canceled = null;
    private User seller = null;
    private Category category = null;
    private Double price = null;
    private Double tax = null;
    private User buyer = null;

    /**
     * @return the idProd
     */
    public Integer getIdProd() {
        return idProd;
    }

    /**
     * @param idProd the idProd to set
     */
    public void setIdProd(Integer idProd) {
        this.idProd = idProd;
    }

    /**
     * @return the quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the initPrice
     */
    public Double getInitPrice() {
        return initPrice;
    }

    /**
     * @param initPrice the initPrice to set
     */
    public void setInitPrice(Double initPrice) {
        this.initPrice = initPrice;
    }

    /**
     * @return the minPrice
     */
    public Double getMinPrice() {
        return minPrice;
    }

    /**
     * @param minPrice the minPrice to set
     */
    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    /**
     * @return the minIncrement
     */
    public Double getMinIncrement() {
        return minIncrement;
    }

    /**
     * @param minIncrement the minIncrement to set
     */
    public void setMinIncrement(Double minIncrement) {
        this.minIncrement = minIncrement;
    }

    /**
     * @return the deliveryPrice
     */
    public Double getDeliveryPrice() {
        return deliveryPrice;
    }

    /**
     * @param deliveryPrice the deliveryPrice to set
     */
    public void setDeliveryPrice(Double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    /**
     * @return the urlPhoto
     */
    public String getUrlPhoto() {
        return urlPhoto;
    }

    /**
     * @param urlPhoto the urlPhoto to set
     */
    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    /**
     * @return the expirationTime
     */
    public Date getExpirationTime() {
        return expirationTime;
    }

    /**
     * @param expirationTime the expirationTime to set
     */
    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * @return the seller
     */
    public User getSeller() {
        return seller;
    }

    /**
     * @param seller the seller to set
     */
    public void setSeller(User seller) {
        this.seller = seller;
    }

    /**
     *
     * @param username the username of the seller
     */
    public void setSeller(String username) {
        seller = new User();
        seller.setUsername(username);
    }

    /**
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     *
     * @param idCat the identifier of the category
     */
    public void setCategory(Integer idCat) {
        category = new Category();
        category.setIdCat(idCat);
    }


    /**
     * @return the expired
     */
    public Boolean getExpired() {
        return expired;
    }

    /**
     * @param expired the expired to set
     */
    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    /**
     * @return the canceled
     */
    public Boolean getCanceled() {
        return canceled;
    }

    /**
     * @param canceled the canceled to set
     */
    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    /**
     * @return the price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * @return the tax
     */
    public Double getTax() {
        if (!canceled && price != null) {
            tax = price * 0.0125;
            long rndPrice = tax.intValue();
            if (tax > rndPrice) {
                tax = tax - rndPrice <= 0.5 ? rndPrice + 0.5 : rndPrice + 1;
            }
        }
        if (!canceled) {
            tax = Math.max(1.23, tax);
        }
        return tax;
    }

    /**
     * @param tax the tax to set
     */
    public void setTax(Double tax) {
        this.tax = tax;
    }

    /**
     * @return the buyer
     */
    public User getBuyer() {
        return buyer;
    }

    /**
     * @param buyer the buyer to set
     */
    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public void setBuyer(String username) {
        buyer = new User();
        buyer.setUsername(username);
    }
}
