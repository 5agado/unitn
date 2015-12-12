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
import static utilities.Constants.*;

public class LoadDataServlet extends HttpServlet {
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
        String action = request.getParameter(ACTION_PARAM_NAME);
        User user = (User)request.getSession().getAttribute(USER_ATTRIBUTE_NAME);
        String username = user.getUsername();
        String forwardUrl;
        try {
            if (action==null || action.equals(ACTION_SHOW_ALL)) {
                showAll(request, response, username);
                forwardUrl = JSP_LANDING_PAGE_USER;
            }
            else if (action.equals(ACTION_SHOW_PURCHASES)) {
                showPurchases(request, response, username);
                forwardUrl = JSP_TABLE_PAGE;
            }
            else if (action.equals(ACTION_SHOW_BIDS)) {
                showBids(request, response, username);
                forwardUrl = JSP_TABLE_PAGE;
            }
            else if (action.equals(ACTION_SHOW_LOST_AUCTIONS)) {
                showLostAuctions(request, response, username);
                forwardUrl = JSP_TABLE_PAGE;
            }
            else if (action.equals(ACTION_SHOW_SALES)) {
                showSales(request, response, username);
                forwardUrl = JSP_TABLE_PAGE;
            }
            else {
                response.sendError(404);
                return;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServletException(ex.getMessage());
        }
        RequestDispatcher rd = request.getRequestDispatcher("/" +forwardUrl);
        rd.forward(request, response);
    }
    
    private void showAll(HttpServletRequest request, HttpServletResponse response, String username) throws SQLException, ServletException {
        final int LIMIT = 10;        
        List<Product> purchases = manager.getPurchasesByBuyer(username, LIMIT);
        List<Product> lostAuctions = manager.getLostAuctionsByBidder(username, LIMIT);
        List<Bid> bids = manager.getBidsByBidder(username, LIMIT);
        request.setAttribute(LIST_PURCHASES, purchases);
        request.setAttribute(LIST_LOST_AUCTIONS, lostAuctions);
        request.setAttribute(LIST_BIDS, bids);
    }
    
    private void showPurchases(HttpServletRequest request, HttpServletResponse response, String username) throws SQLException, ServletException {
        List<Product> purchases = manager.getPurchasesByBuyer(username, DBManager.NO_LIMITS);
        request.setAttribute(LIST_PURCHASES, purchases);
    }
    
    private void showLostAuctions(HttpServletRequest request, HttpServletResponse response, String username) throws SQLException, ServletException {
        List<Product> lostAuctions = manager.getLostAuctionsByBidder(username, DBManager.NO_LIMITS);
        request.setAttribute(LIST_LOST_AUCTIONS, lostAuctions);
    }
    
    private void showBids(HttpServletRequest request, HttpServletResponse response, String username) throws SQLException, ServletException {
        List<Bid> bids = manager.getBidsByBidder(username, DBManager.NO_LIMITS);
        request.setAttribute(LIST_BIDS, bids);
    }
    
    private void showSales(HttpServletRequest request, HttpServletResponse response, String username) throws SQLException, ServletException {
        List<Product> sales = manager.getProductsBySeller(username);
        request.setAttribute(LIST_SALES, sales);
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
