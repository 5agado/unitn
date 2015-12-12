package servlets;

import db.Category;
import db.DBManager;
import db.Photo;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static utilities.Constants.*;

public class FormAddProductServlet extends HttpServlet {
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
        showHtmlPage(request, response);
    }

    private void showHtmlPage (HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();   
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("    <head>");
        out.println("        <meta charset='utf-8'>");
        out.println("        <title>AddProductPage</title>");
        out.println("        <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("        <meta name='description' content=''>");
        out.println("        <meta name='author' content=''>");
        out.println("");
        out.println("        <link rel='stylesheet' type='text/css' href='"+CSS_BOOTSTRAP+"'> ");
        out.println("        <link rel='stylesheet' type='text/css' href='"+CSS_PERSONALIZATION+"'> ");
        out.println("    </head>");
        out.println("    <body>");
        out.println("        <div>");
        out.println("            <form class='form-adding' action='"+ SM_ADD_PRODUCT +"'>");
        out.println("                <h2 class='form-adding-heading'>Add a new product!</h2>");
        out.println("                <div class='control-group'>");
        out.println("                    <label class='control-label' for='product'>PRODUCT</label>");
        out.println("                    <div class='controls'>");
        out.println("                        <input class='input-block-level' name='"+ PRODUCTNAME_PARAM_NAME +"' type='text' id='product' placeholder='Name'>");
        out.println("                    </div>");
        out.println("                </div>");
        out.println("                <div class='control-group'>");
        out.println("                    <label class='control-label'>CATEGORY</label>");
        out.println("                    <div class='controls'>");
        out.println("                        <select name='"+ CATEGORY_PARAM_NAME +"' class='input-block-level'>");
        //ciclo aggiungendo tutte le categorie alla select
        try {
            List<Category> categories = manager.getCategories();
            for (Category c:categories){
                out.println("                    <option value='"+ c.getId() +"'>"+ c.getName() +"</option>");
            }
        } catch (SQLException ex) {
            Logger.getLogger(FormAddProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        out.println("                        </select> ");
        out.println("                    </div>");
        out.println("                </div>");
        out.println("                <div class='control-group'>");
        out.println("                    <label class='control-label' for='price'>UNIT PRICE</label>");
        out.println("                    <div class='controls'>");
        out.println("                        <input class='input-block-level' name='"+ PRICE_PARAM_NAME +"' type='text' id='price' placeholder='Price'>");
        out.println("                    </div>");
        out.println("                </div>");
        out.println("                <div class='control-group'>");
        out.println("                    <label class='control-label' for='um'>UM</label>");
        out.println("                    <div class='controls'>");
        out.println("                        <input class='input-block-level' name='"+ UM_PARAM_NAME +"' type='text' id='um' placeholder='UM'>");
        out.println("                    </div>");
        out.println("                </div>");
        out.println("                <div class='control-group'>");
        out.println("                    <label class='control-label' for='quantity'>QUANTITY</label>");
        out.println("                    <div class='controls'>");
        out.println("                        <input class='input-block-level' name='"+ QUANTITY_PARAM_NAME +"' type='text' id='quantity' placeholder='Quantity'>");
        out.println("                    </div>");
        out.println("                </div>");
        out.println("                <div class='control-group'>");
        out.println("                    <label class='control-label'>PHOTO</label>");
        out.println("                    <div class='controls'>");
        out.println("                       <select name='"+ PHOTO_PARAM_NAME +"'>");
        out.println("                           <option value='0' selected='selected'>No photo</option>");
        //ciclo aggiungendo tutti i nomi delle foto presenti alla select
        try {
            List<Photo> photos = manager.getPhotos();
            for (Photo p:photos){
                out.println("                            <option value='" + p.getId() + "'>" + p.getName() + "</option>");
            }
        } catch (SQLException ex) {
            Logger.getLogger(FormAddProductServlet.class.getName()).log(Level.SEVERE, "Errore nella generazione dell'elenco delle foto", ex);
        }
        out.println("                        </select>");
        out.println("                    </div>");
        out.println("                </div>");
        //in caso sia settato l'attributo message, ovvero il messaggio di errato input dati
        Object message = request.getAttribute("message");
        if (message != null){
        out.println("                <div class='alert alert-error'>");
        out.println((String)message);
        out.println("                </div>");  
        }
        out.println("                <div class='controls'>");
        out.println("                    <button type='submit' class='btn btn-primary btn-success'>ADD</button>");
        out.println("                    <a class='btn btn-primary btn-success' href='"+ SM_LANDING_PAGE_SELLER +"'>RETURN</a>");
        out.println("                </div>");
        out.println("            </form>");
        out.println("        </div>");
        out.println("    </body>");
        out.println("</html>");
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
