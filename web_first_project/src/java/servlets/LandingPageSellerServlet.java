/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import db.DBManager;
import db.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static utilities.Constants.*;

/**
 *
 * @author dell
 */
public class LandingPageSellerServlet extends HttpServlet {
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
        out.println("        <title>LandingPage</title>");
        out.println("        <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("        <meta name='description' content=''>");
        out.println("        <meta name='author' content=''>");
        out.println("");
        out.println("        <link rel='stylesheet' type='text/css' href='"+ CSS_BOOTSTRAP +"'>");
        out.println("        <link rel='stylesheet' type='text/css' href='"+ CSS_PERSONALIZATION +"'> ");
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
        out.println("                <div class='span3'></div>");
        out.println("                <div class='span6'>");
        out.println("                    <div class='hero-unit'>");
        out.println("                        <h2>Welcome, " + username + "</h2>");
        out.println("                    </div>");
        out.println("                    <div class='hero-unit'>");
        out.println("                        <h2>Add a new product!</h2>");
        out.println("                        <a class='btn btn-primary btn-success' href='"+ SM_FORM_ADD_PRODUCT +"'>ADD PRODUCT</a>");
        out.println("                    </div>");
        out.println("                    <div class='hero-unit'>");
        out.println("                        <h2>Show products!</h2>");
        out.println("                        <a class='btn btn-primary btn-success' href='"+ SM_SHOW_PRODUCTS +"'>ON SALE</a>");
        out.println("                    </div>");
        out.println("                </div><!--/span-->");
        out.println("                <div class='span3'></div>");
        out.println("            </div><!--/row-->");
        out.println("            <hr>");
        out.println("            <footer>");
        out.println("                <p>&copy; GreenMarket 2012</p>");
        out.println("            </footer>");
        out.println("       </div>");
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
