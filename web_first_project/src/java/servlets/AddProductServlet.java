package servlets;

import db.DBManager;
import db.Product;
import db.User;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static utilities.Constants.*;

public class AddProductServlet extends HttpServlet {
    private DBManager manager;
    
    @Override
    public void init() throws ServletException {
        this.manager = (DBManager)super.getServletContext().getAttribute(DB_ATTRIBUTE_NAME);
    }
    
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {        
        Product newProduct = getProductFromRequest(request);
        //se ottengo un prodotto corretto lo aggiungo al databse
        if (newProduct != null){
            Object seller = request.getSession().getAttribute(USER_ATTRIBUTE_NAME);
            if (seller == null){
                throw new RuntimeException("ERROR: null seller from session");
            }
            int userId = ((User)seller).getId();
            newProduct.setSeller(userId);
            try {
                manager.addProduct(newProduct);
            } catch (SQLException ex) {
                Logger.getLogger(AddProductServlet.class.getName()).log(Level.SEVERE, null, ex);
            }

            //redirezione alla servlet che mostra la lista dei prodotti
            response.sendRedirect(SM_SHOW_PRODUCTS);
        }
        //altrimenti rimando alla form che mostrarà relativo errore
        else{
            RequestDispatcher reqDis = request.getRequestDispatcher(SM_FORM_ADD_PRODUCT);
            reqDis.forward(request, response);
        }
    }
    
    private Product getProductFromRequest (HttpServletRequest request){
        Product newProduct = new Product();
        
        //Product name
        String product = request.getParameter(PRODUCTNAME_PARAM_NAME); 
        if (product.isEmpty()){
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Product " + EMPTY_FIELD); 
        }
        else{
            newProduct.setName(product);
        }
        
        //Category
        String category = request.getParameter(CATEGORY_PARAM_NAME);     
        newProduct.setCategory(Integer.valueOf(category));
        
        //Photo
        String photo = request.getParameter(PHOTO_PARAM_NAME);
        int photoId = Integer.valueOf(photo);
        newProduct.setPhoto(photoId);
        
        //Quantity
        String quantityString = request.getParameter(QUANTITY_PARAM_NAME);
        if (quantityString.isEmpty()){
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Quantity " + EMPTY_FIELD); 
        }
        else{
            try {
                int quantity = Integer.valueOf(quantityString);
                if (quantity <= 0){
                    throw new NumberFormatException();
                }
                newProduct.setQuantity(quantity);
            } catch (NumberFormatException ex) {
                request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Quantity" + INVALID_VALUE); 
            }
        }
        
        //UM
        String um = request.getParameter(UM_PARAM_NAME);
        newProduct.setUm(um);
        
        //Price
        String priceString = request.getParameter(PRICE_PARAM_NAME);
        if (priceString.isEmpty()){
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Price " + EMPTY_FIELD); 
        }
        else{
            try{
                double price = Double.valueOf(priceString);
                if (price <= 0){
                    throw new NumberFormatException();
                }
                newProduct.setPrice(price);
            } catch (NumberFormatException ex) {
                request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Price" + INVALID_VALUE); 
            }
        }
        
        //se ho settato il messaggio di errore rinornerò null
        if (request.getAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME) == null){                
            return newProduct;
        }
        else{
            return null;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
