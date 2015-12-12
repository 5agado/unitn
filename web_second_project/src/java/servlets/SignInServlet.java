package servlets;

import db.DBManager;
import db.beans.User;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import static utilities.Constants.*;

public class SignInServlet extends HttpServlet {

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

        //controlli parametri
        String username = request.getParameter(USERNAME_PARAM_NAME);
        if (username.isEmpty()) {
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Username " + EMPTY_FIELD);
        }

        String email = request.getParameter(EMAIL_PARAM_NAME);
        if (email.isEmpty()) {
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Email " + EMPTY_FIELD);
        }

        String address = request.getParameter(ADDRESS_PARAM_NAME);
        if (address.isEmpty()) {
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Address " + EMPTY_FIELD);
        }

        String password = request.getParameter(PASSWORD_PARAM_NAME);
        if (password.isEmpty()) {
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "Password " + EMPTY_FIELD);
        }
        
        //CAPTCHA
        String remoteAddr = request.getRemoteAddr();
        ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
        reCaptcha.setPrivateKey("6LeMUdsSAAAAAPXozztyYzuM4axaQyHzrptWv29h");

        String challenge = request.getParameter("recaptcha_challenge_field");
        String uresponse = request.getParameter("recaptcha_response_field");
        ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);

        if (!reCaptchaResponse.isValid()) {
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, "CAPTCHA " + INVALID_VALUE);
        }


        //se parametri ok, creo user e aggiungo al database.
        //direzione il forward alla pagina di login
        boolean toLoginPage = false;
        if (request.getAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME) == null) {
            User user = new User();
            user.setUsername(request.getParameter(USERNAME_PARAM_NAME));
            user.setEmail(request.getParameter(EMAIL_PARAM_NAME));
            user.setAddress(request.getParameter(ADDRESS_PARAM_NAME));
            user.setRole(User.Role.USER);
            try {
                manager.registerNewUser(user, password);
                request.setAttribute(SUCCESS_MESSAGE_ATTRIBUTE_NAME, SUCCESSFULLY_SIGN_IN);
                toLoginPage = true;
            } catch (SQLException ex) {
                Logger.getLogger(SignInServlet.class.getName()).log(Level.SEVERE, null, ex);
                if (ex.getErrorCode() == 1062){
                    request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, USERNAME_ALREADY_EXISTS);
                }
            }
        }
        RequestDispatcher reqDis = request.getRequestDispatcher(toLoginPage? JSP_LOGIN:JSP_REGISTER);
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
