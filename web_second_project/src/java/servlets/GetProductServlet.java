package servlets;

import db.DBManager;
import db.beans.Bid;
import db.beans.Product;
import db.beans.User;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utilities.AuxiliaryMethods;
import static utilities.AuxiliaryMethods.*;
import static utilities.Constants.*;

public class GetProductServlet extends HttpServlet {

    private DBManager manager;

    @Override
    public void init() throws ServletException {
        this.manager = (DBManager) super.getServletContext().getAttribute(DB_ATTRIBUTE_NAME);
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
        String prodParam = request.getParameter(IDPRODUCT_PARAM_NAME);
        Product p = null;
        List<Bid> bids = null;
        if (prodParam!=null) {
            Integer idProduct = Integer.valueOf(prodParam);
            try {
                p = manager.getProductById(idProduct);
                bids = getUpdatedList(manager, p);
            } catch (SQLException ex) {
                throw new ServletException(ex.getMessage());
            }
        }
        if (!p.getExpired() && p.getExpirationTime().getTime()<System.currentTimeMillis()) {
            AuxiliaryMethods.updateExpired(manager, p);
        }
        request.setAttribute(PRODUCT_PARAM_NAME, p);
        request.setAttribute(LIST_BIDS, bids);
        if (request.getParameter(ACTION_PARAM_NAME) != null){
            request.setAttribute(ACTION_SHOW_ONLY_BIDS, 1);
        }
        User user = (User) request.getSession(true).getAttribute(USER_ATTRIBUTE_NAME);
        String forwardUrl;
        if (user.getRole() == User.Role.ADMIN){
            forwardUrl = JSP_ADMIN_PRODUCT;
        }
        else {
            forwardUrl = JSP_PRODUCT;
        }
        RequestDispatcher rd = request.getRequestDispatcher("/" + forwardUrl);
        rd.forward(request, response);
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
