package servlets;

import db.DBManager;
import db.beans.User;
import db.beans.User.Role;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import static utilities.Constants.*;

public class LoginServlet extends HttpServlet {

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
        String username = request.getParameter(USERNAME_PARAM_NAME);
        String password = request.getParameter(PASSWORD_PARAM_NAME);

        User user = (User) request.getSession(true).getAttribute(USER_ATTRIBUTE_NAME);

        //non esiste l'attributo di sessione User
        if (user == null) {
            //caso in cui arriva una richiesta di login con parametro username settato
            if (username != null) {

                //caso campi incompleti
                if (password.isEmpty() || username.isEmpty()) {
                    //campo password vuoto
                    if (password.isEmpty()) {
                        request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, NO_PASSWORD_MESSAGE);
                    }
                    //campo username vuoto. Il messaggi sovrascrive
                    //quello di eventuale password non presente
                    if (username.isEmpty()) {
                        request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, NO_USERNAME_MESSAGE);
                    }
                } //altrimenti procedo con l'autenticazione
                else {
                    //prendo l'user dal manager
                    try {
                        user = manager.getUserByUsernamePassword(username, password);
                    } catch (SQLException ex) {
                        throw new ServletException(ex);
                    }
                    //se null setto messaggio adeguato
                    if (user == null) {
                        request.setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, WRONG_CREDENTIALS);
                    } else {
                        // imposto l'utente connesso come attributo di sessione
                        HttpSession session = request.getSession(true);
                        session.setAttribute(USER_ATTRIBUTE_NAME, user);
                    }
                }
            }
        }

        //se user correttamente settato o già presente in sessione redireziono in base al ruolo
        if (user != null) {
            if (user.getRole() == Role.ADMIN) {
                response.sendRedirect(SM_ADMIN_SALES);
            } else {
                response.sendRedirect(SM_LOAD_DATA);
            }
            return;
        }

        //mostro pagina di login con eventuale relativo messaggio
        RequestDispatcher reqDis = request.getRequestDispatcher(JSP_LOGIN);
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
