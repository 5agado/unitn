package servlets;

import db.Category;
import db.DBManager;
import db.Product;
import db.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static utilities.Constants.*;

public class ConfirmationPageServlet extends HttpServlet {
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
    
     private void showHtmlPage(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("    <head>");
        out.println("        <meta charset='utf-8'>");
        out.println("        <title>ConfirmationPage</title>");
        out.println("        <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("        <meta name='description' content=''>");
        out.println("        <meta name='author' content=''>");
        out.println("");
        out.println("        <link rel='stylesheet' type='text/css' href='"+CSS_BOOTSTRAP+"'> ");
        out.println("        <link rel='stylesheet' type='text/css' href='"+CSS_PERSONALIZATION+"'> ");
        out.println("    </head>");
        out.println("    <body>");
        out.println("        <div class='navbar navbar-fixed-top'>");
        out.println("            <div class='navbar-inner'>");
        out.println("                <div class='container-fluid'>");
        out.println("                    <div class='nav-collapse collapse'>");
        out.println("                        <a class='btn btn-small pull-right' href='"+ SM_LOGOUT +"'>Sign Out</a>");
        out.println("                        <div class='navbar-text pull-right'>");
        User user = (User)request.getSession().getAttribute(USER_ATTRIBUTE_NAME);
        String username = user.getUsername();
        out.println("                            Logged in as <a href='#' class='navbar-link'><b>"+ username +"</b>&nbsp;&nbsp;</a>");
        out.println("                        </div>");
        out.println("                        <div class='navbar-text, brand' >GreenMarket</div>");
        out.println("                    </div>");
        out.println("                </div>");
        out.println("            </div>");
        out.println("        </div>");
        out.println("        <div class='container'>");
        out.println("<div><h1 class='green-text'>PURCHASE SUMMARY</h1></div>");
        out.println("            <br>");
        out.println("            <table class='table'>");        
        out.println("                        <tr>");
        out.println("                            <th>Seller</th>");
        out.println("                            <th>Product</th>");
        out.println("                            <th>Category</th>");
        out.println("                            <th>Unit Price</th>");
        out.println("                            <th>UM</th>");
        out.println("                            <th>Quantity</th>");
        out.println("                            <th>Total</th>");
        out.println("                        </tr>");
        //mostro il riepilogo dell'acquisto
        String productId = request.getParameter(PRODUCT_ID_PARAM_NAME);
        if (productId == null){
            throw new RuntimeException("ERROR: id attribute not present");
        }        
        try {
            Product p = manager.getProductById(Integer.valueOf(productId));
            DecimalFormat df = new DecimalFormat("0.00");
            out.println("                        <tr>");
            User seller = manager.getUserById(p.getSeller());
            out.println("                                <td>"+ seller.getUsername() +"</td>");
            out.println("                                <td>"+ p.getName() +"</td>");
            Category category = manager.getCategoryById(p.getCategory());
            out.println("                                <td>"+ category.getName() +"</td>");
            out.println("                                <td>"+ df.format(p.getPrice()) +"</td>");
            out.println("                                <td>"+ p.getUm() +"</td>");
            out.println("                                <td>"+ p.getQuantity() +"</td>");
            out.println("                                <td>"+ df.format(p.getQuantity() * p.getPrice()) +"</td>");
            out.println("                        </tr>");
            out.println("            </table>");
            out.println("            <br>");
            out.println("             <div class='row-fluid'>");
            out.println("            <form class='span1' action='"+ SM_ADD_PURCHASE +"'>                ");
            out.println("                <button class='btn btn-primary btn-success' type='submit'>CONFIRM</button>");
            out.println("                <input type='hidden' name='"+ PRODUCT_ID_PARAM_NAME +"' value='" + p.getId() + "'>");
            out.println("            </form>");
            //torno alla servlet ShowProductsByCategory, quindi rimetto l'id della
            //categoria nella request
            out.println("            <form class='span1' action='"+ SM_SHOW_PRODUCTS_BY_CATEGORY +"'>                ");
            out.println("                <button class='btn btn-primary btn-success  putSpace' type='submit'>CANCEL</button>");
            out.println("                <input type='HIDDEN' value='"+ p.getCategory() +"' name='category'>");
            out.println("            </form>");
        } catch (SQLException ex) {
            Logger.getLogger(ConfirmationPageServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        out.println("             </div>");
        out.println("                <hr>");
        out.println("                <footer>");
        out.println("                    <p>&copy; GreenMarket 2012</p>");
        out.println("                </footer>");
        out.println("            </div>");
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
