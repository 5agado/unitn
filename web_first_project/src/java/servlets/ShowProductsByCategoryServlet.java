package servlets;

import db.DBManager;
import db.Photo;
import db.Product;
import db.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static utilities.Constants.*;

public class ShowProductsByCategoryServlet extends HttpServlet {
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
        Object category = request.getAttribute(CATEGORY_PARAM_NAME);   
        showHtmlPage(request, response);

    }
    
    private void showHtmlPage(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("    <head>");
        out.println("        <meta charset='utf-8'>");
        out.println("        <title>ProductsPage</title>");
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
        out.println("                            Logged in as <b>"+ username +"</b>&nbsp;&nbsp;");
        out.println("                        </div>");
        out.println("                        <div class='navbar-text, brand' >GreenMarket</div>");
        out.println("                    </div>");
        out.println("                </div>");
        out.println("            </div>");
        out.println("        </div>");
        out.println("        <div class='container-fluid'>");
        out.println("            <div class='row-fluid'>");
        out.println("                <div class=container>");
        out.println("                    <div class='row-fluid'>");
        out.println("                        <div class='span11 green-text'><h2>PRODUCTS</h2></div>");
        out.println("                        <h2 class='span1'><a class='btn btn-primary btn-success' href='"+ SM_LANDING_PAGE_BUYER +"'>RETURN</a>");
        out.println("                        </h2>");
        out.println("                    </div>");
        out.println("                    <form action='"+ SM_CONFIRMATION_PAGE +"'>                ");
        out.println("                    <table class='table'>");
        out.println("                        <tr>");
        out.println("                                <th></th>");
        out.println("                                <th>Seller</th>");
        out.println("                                <th>Product</th>");
        out.println("                                <th>Unit price</th>");
        out.println("                                <th>UM</th>");
        out.println("                                <th>Quantity</th>");
        out.println("                                <th>Photo</th>");
        out.println("                        </tr>");
         //ciclo sui vari prodotti della specifica categoria
        String category = request.getParameter("category");
        Boolean activatedRadio = true;
        if (category == null){
            throw new RuntimeException("ERROR: category attribute not present");
        }        
        try {
            List<Product> products = manager.getProductsByCategory(Integer.valueOf(category));
            DecimalFormat df = new DecimalFormat("0.00");
            for (Product p:products){
                out.println("                        <tr>");
                if (activatedRadio){
                    out.println("<td> <input clas='actice' type='radio' name='"+ PRODUCT_ID_PARAM_NAME +"' value='"+ p.getId() +"' checked></td>");
                    activatedRadio = false;
                }
                else {
                    out.println("<td> <input type='radio' name='"+ PRODUCT_ID_PARAM_NAME +"' value='"+ p.getId() +"'></td>");
                }
                User seller = manager.getUserById(p.getSeller());
                out.println("                                <td>"+ seller.getUsername() +"</td>");
                out.println("                                <td>"+ p.getName() +"</td>");
                out.println("                                <td>"+ df.format(p.getPrice()) +"</td>");
                out.println("                                <td>"+ p.getUm() +"</td>");
                out.println("                                <td>"+ p.getQuantity() +"</td>");
                String photoPath;
                Photo photo = manager.getPhotoById(p.getPhoto());
                if (photo!=null) {
                    photoPath = photo.getUrl();
                } else {
                    photoPath = "null.gif";
                }
                out.println("                                <td><img src='/WEB_first_project/imgs/"+ photoPath +"'></td>");
                out.println("                        </tr>");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ShowProductsByCategoryServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        out.println("                    </table>");
        out.println("                    <button class='btn btn-primary btn-success' type='submit'>CONFIRM</button>");
        out.println("                    <a class='btn btn-primary btn-success' href='"+ SM_LANDING_PAGE_BUYER +"'>RETURN </a>");
        out.println("                    </form>");
        out.println("                </div>");
        out.println("                <hr>");
        out.println("                <footer>");
        out.println("                    <p>&copy; GreenMarket 2012</p>");
        out.println("                </footer>");
        out.println("            </div>");
        out.println("        </div>");
        out.println("</body>");
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
