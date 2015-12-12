package servlets;

import db.DBManager;
import db.beans.Bid;
import db.beans.Product;
import db.beans.User;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utilities.AuxiliaryMethods;
import static utilities.Constants.*;

public class NewBidServlet extends HttpServlet {

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
        Integer idProduct = Integer.valueOf(request.getParameter(IDPRODUCT_PARAM_NAME));
        Bid newBid = new Bid();

        //Bid value
        String bidString = request.getParameter(BID_PARAM_NAME);
        if (bidString.isEmpty()) {
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Bid " + EMPTY_FIELD);
        } else {
            try {
                double bid = Double.valueOf(bidString);
                if (bid <= 0) {
                    throw new NumberFormatException();
                }
                newBid.setBid(bid);
            } catch (NumberFormatException ex) {
                request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Bid" + INVALID_VALUE);
            }
        }

        newBid.setProduct(idProduct);

        if (request.getAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME) == null) {
            //Bidder
            User bidder = (User) request.getSession().getAttribute(USER_ATTRIBUTE_NAME);
            newBid.setBidder(bidder.getUsername());
            try {
                AuxiliaryMethods.updateExpired(manager, manager.getProductById(idProduct));
                int res = manager.makeNewBid(newBid);
                if (res == DBManager.BID_CORRECT){
                    request.setAttribute(SUCCESS_MESSAGE_ATTRIBUTE_NAME, SUCCESSFULLY_BID);
                    List<Bid> bids = manager.getBidsByProduct(idProduct);
                    if (bids.size()>1) {
                        Product prod = manager.getProductById(idProduct);
                        AuxiliaryMethods.sendMailBeatAuction(bids.get(1).getBidder(), prod);
                    }
                }
                else if (res == DBManager.BID_EXPIRED_AUCTION){
                    request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Sorry, auction is expired");
                }
                else if (res == DBManager.BID_NOT_MAX){
                    request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Bid" + INVALID_VALUE);
                }
                else {
                    request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Sorry, another user beat your offer");
                }
            } catch (SQLException ex) {
                Logger.getLogger(NewBidServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        RequestDispatcher reqDis = request.getRequestDispatcher("/" + SM_GET_PRODUCT + "?" + IDPRODUCT_PARAM_NAME + Integer.valueOf(request.getParameter(IDPRODUCT_PARAM_NAME)));
        reqDis.forward(request, response);
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
