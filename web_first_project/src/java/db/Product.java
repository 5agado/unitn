package db;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String name;
    private int quantity;
    private String um;
    private double price; 
    private int photo;
    private int category;
    private int seller;
    private boolean onSale;

    @Override
    public String toString() {
        return "Product (id: " + id + " - name: " + name + ")";
    }
    
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUm() {
        return um;
    }

    public void setUm(String um) {
        this.um = um;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getSeller() {
        return seller;
    }

    public void setSeller(int seller) {
        this.seller = seller;
    }

    /**
     * @return the onSale
     */
    public boolean isOnSale() {
        return onSale;
    }

    /**
     * @param onSale the onSale to set
     */
    public void setOnSale(boolean onSale) {
        this.onSale = onSale;
    }
    
    
    
}
