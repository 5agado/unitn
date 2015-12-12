package servlets;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import db.DBManager;
import db.beans.Product;
import db.beans.User;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static utilities.Constants.*;

public class NewAuctionServlet extends HttpServlet {

    private DBManager manager;
    private String dirName;

    @Override
    public void init() throws ServletException {
        this.manager = (DBManager) super.getServletContext().getAttribute(DB_ATTRIBUTE_NAME);

        //init per upload foto
        dirName = getServletConfig().getInitParameter(IMGSDIR_PARAM_NAME);
        if (dirName == null) {
            throw new ServletException("Please supply uploadDir parameter");
        }
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
        Product newProduct = new Product();

        try {
            MultipartRequest multi = new MultipartRequest(request, getServletContext().getRealPath(dirName), 10 * 1024 * 1024,
                    "ISO-8859-1", new DefaultFileRenamePolicy());
            //Product description
            String description = multi.getParameter(DESCRIPTION_PARAM_NAME);
            if (description == null || description.isEmpty()) {
                request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Description " + EMPTY_FIELD);
            } else {
                newProduct.setDescription(description);
            }

            //Category
            String category = multi.getParameter(CATEGORY_PARAM_NAME);
            newProduct.setCategory(Integer.valueOf(category));

            //Photo
            File photo = multi.getFile(PHOTO_PARAM_NAME);
            String filename;
            if (photo == null) {
                filename = "null.gif";
            } else {
                filename = photo.getName();
                String[] res = filename.split("\\.");
                String extension = res[res.length - 1];
                if (!extension.equalsIgnoreCase("jpg") && !extension.equalsIgnoreCase("png")) {
                    request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Photo: invalid file extension");
                }
            }
            newProduct.setUrlPhoto(filename);


            //Quantity
            String quantityString = multi.getParameter(QUANTITY_PARAM_NAME);
            if (quantityString == null || quantityString.isEmpty()) {
                request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Quantity " + EMPTY_FIELD);
            } else {
                try {
                    int quantity = Integer.valueOf(quantityString);
                    if (quantity <= 0) {
                        throw new NumberFormatException();
                    }
                    newProduct.setQuantity(quantity);
                } catch (NumberFormatException ex) {
                    request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Quantity" + INVALID_VALUE);
                }
            }

            //Expiration Time
            String expirationTimeString = multi.getParameter(EXPIRATION_TIME_PARAM_NAME);
            if (expirationTimeString == null || expirationTimeString.isEmpty()) {
                request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Expiration time " + EMPTY_FIELD);
            } else {
                try {
                    int expirationTime = Integer.valueOf(expirationTimeString);
                    if (expirationTime <= 0) {
                        throw new NumberFormatException();
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, expirationTime);
                    newProduct.setExpirationTime(cal.getTime());
                } catch (NumberFormatException ex) {
                    request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Expiration time" + INVALID_VALUE);
                }
            }

            //Init Price
            String initPriceString = multi.getParameter(INITPRICE_PARAM_NAME);
            if (initPriceString == null || initPriceString.isEmpty()) {
                request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Init price " + EMPTY_FIELD);
            } else {
                try {
                    double price = Double.valueOf(initPriceString);
                    if (price <= 0) {
                        throw new NumberFormatException();
                    }
                    newProduct.setInitPrice(price);
                } catch (NumberFormatException ex) {
                    request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Init price" + INVALID_VALUE);
                }
            }

            //Delivery Price
            String delPriceString = multi.getParameter(DELIVERY_PRICE_PARAM_NAME);
            if (delPriceString == null || delPriceString.isEmpty()) {
                request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Delivery price " + EMPTY_FIELD);
            } else {
                try {
                    double price = Double.valueOf(delPriceString);
                    if (price <= 0) {
                        throw new NumberFormatException();
                    }
                    newProduct.setDeliveryPrice(price);
                } catch (NumberFormatException ex) {
                    request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Delivery price" + INVALID_VALUE);
                }
            }

            //Min Price
            String minPriceString = multi.getParameter(MINPRICE_PARAM_NAME);
            if (minPriceString == null || minPriceString.isEmpty()) {
                minPriceString = "0";
            }
            try {
                double price = Double.valueOf(minPriceString);
                if (price < 0) {
                    throw new NumberFormatException();
                }
                newProduct.setMinPrice(price);
            } catch (NumberFormatException ex) {
                request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Min price" + INVALID_VALUE);
            }

            //Min Increment
            String minIncString = multi.getParameter(MIN_INCREMENT_PARAM_NAME);
            if (minIncString == null || minIncString.isEmpty()) {
                request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Min increment " + EMPTY_FIELD);
            } else {
                try {
                    double increment = Double.valueOf(minIncString);
                    if (increment < 0) {
                        throw new NumberFormatException();
                    }
                    newProduct.setMinIncrement(increment);
                } catch (NumberFormatException ex) {
                    request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Min increment" + INVALID_VALUE);
                }
            }

        } catch (IOException lEx) {
            this.getServletContext().log(lEx, "error reading or saving file");
        }

        if (request.getAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME) == null) {
            //Seller
            User seller = (User) request.getSession().getAttribute(USER_ATTRIBUTE_NAME);
            String username = seller.getUsername();
            newProduct.setSeller(username);
            try {
                manager.createNewAuction(newProduct);
            } catch (SQLException ex) {
                Logger.getLogger(NewAuctionServlet.class.getName()).log(Level.SEVERE, null, ex);
            }

            request.setAttribute(SUCCESS_MESSAGE_ATTRIBUTE_NAME, "Product successfully added to your list of sales");
            RequestDispatcher reqDis = request.getRequestDispatcher("/" + SM_LOAD_DATA);
            reqDis.forward(request, response);
        } else {
            RequestDispatcher reqDis = request.getRequestDispatcher("/" + JSP_NEWPRODUCT);
            reqDis.forward(request, response);
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
