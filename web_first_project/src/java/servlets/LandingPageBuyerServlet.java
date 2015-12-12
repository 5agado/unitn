package servlets;

import db.Category;
import db.DBManager;
import db.User;
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
import utilities.Pair;

public class LandingPageBuyerServlet extends HttpServlet {
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
        out.println("        <title>LandingPage</title>");
        out.println("        <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("        <meta name='description' content=''>");
        out.println("        <meta name='author' content=''> ");
        out.println("");
        out.println("        <link rel='stylesheet' type='text/css' href='"+CSS_BOOTSTRAP+"'> ");
        out.println("        <link rel='stylesheet' type='text/css' href='"+CSS_PERSONALIZATION+"'> ");
        out.println("        <style type='text/css'>");
        out.println("            body {");
        out.println("                padding-top: 60px;");
        out.println("                padding-bottom: 40px;");
        out.println("            }");
        out.println("            .sidebar-nav {");
        out.println("                padding: 9px 0;");
        out.println("            }");
        out.println("        </style>");
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
        out.println("                <div class='span3'>");
        out.println("                    <div class='well sidebar-nav'>");
        out.println("                        <ul class='nav nav-list'>");
        out.println("                            <li class='nav-header'>PDF_list</li>");
        //carico i pdf esistenti
        Object buyer = request.getSession().getAttribute(USER_ATTRIBUTE_NAME);
        Boolean activatedLink = true;
        if (buyer == null){
            throw new RuntimeException("ERROR: null user from session");
        }
        int buyerId = ((User)buyer).getId();
        //carico i pdf
        try {
            List<Pair<String, String>> urls = manager.getPdfUrlsAndTimestampsByBuyer(buyerId);
            if (urls.isEmpty()){
                out.println("                            <li>no purchases found</li>");
            }
            for (Pair<String, String> p: urls){
                if (activatedLink){
                    out.println("                            <li class='active'><a target='_blank' href='../pdf/"+ p.getFirst() +"'>" + p.getSecond() + "</a></li>");
                    activatedLink = false;
                }
                else {
                    out.println("                            <li><a target='_blank' href='../pdf/"+ p.getFirst() +"'>" + p.getSecond() + "</a></li>");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(LandingPageBuyerServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        out.println("                        </ul>");
        out.println("                    </div><!--/.well -->");
        out.println("                </div><!--/span-->");
        out.println("                <div class='span9'>");
        out.println("                    <div class='hero-unit'>");
        out.println("                        <h2>Welcome, " + username + "</h2>");
        out.println("                    </div>");
        out.println("                    <div class='hero-unit'>");
        out.println("                        <h1>Search for products!</h1>");
        out.println("                        <p>Select the desired category and find the product you are looking for.</p>");
        out.println("                        <form class='form-horizontal' action='"+ SM_SHOW_PRODUCTS_BY_CATEGORY +"'>");
        out.println("                            <select name='category'>");
        //carico le categorie esistenti
        try {
            List<Category> categories = manager.getCategories();
            for (Category c:categories){
                out.println("                    <option value='"+ c.getId() +"'>"+ c.getName() +"</option>");
            }
        } catch (SQLException ex) {
            Logger.getLogger(LandingPageBuyerServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        out.println("                            </select>  ");
        out.println("                            <button class='btn btn-primary btn-success' type='submit'>CONFIRM</button>");
        out.println("                        </form>");
        out.println("                    </div>");
        out.println("                </div><!--/span-->");
        out.println("            </div><!--/row-->");
        out.println("");
        out.println("            <hr>");
        out.println("");
        out.println("            <footer>");
        out.println("                <p>&copy; GreenMarket 2012</p>");
        out.println("            </footer>");
        out.println("        </div>");
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
