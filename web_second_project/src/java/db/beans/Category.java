package db.beans;

import java.io.Serializable;

public class Category implements Serializable {
    private Integer idCat;
    private String name;

    
    /**
     * @return the idCat
     */
    public Integer getIdCat() {
        return idCat;
    }

    /**
     * @param idCat the idCat to set
     */
    public void setIdCat(Integer idCat) {
        this.idCat = idCat;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
}
