package filters;

import db.DBManager;
import db.User;
import db.User.Role;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utilities.Constants;

public class PdfAccessControl implements Filter { 
    private FilterConfig filterConfig = null;
    private DBManager manager;
    
    public PdfAccessControl() {
    }    
    
    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain)
            throws IOException, ServletException {
        try {
            if (request instanceof HttpServletRequest) {
                HttpServletRequest httpRequest = (HttpServletRequest)request;
                User user = (User)httpRequest.getSession().getAttribute(Constants.USER_ATTRIBUTE_NAME);
                if (user!=null && user.getRole() == Role.BUYER) {
                        List<String> urls = manager.getPdfUrlsByBuyer(user.getId());
                        String requestedPdf = httpRequest.getRequestURI();                        
                                                                      
                        String [] list = requestedPdf.split("/");
                        requestedPdf = list[list.length-1];                        
                        
                        if (urls.contains(requestedPdf)) {
                            chain.doFilter(request, response);
                            return;
                        }
                }
                HttpServletResponse httpResponse = (HttpServletResponse)response;
                httpResponse.sendRedirect("../"+Constants.SM_LOGIN);
            }
            
                
        } catch (SQLException ex) {
            Logger.getLogger(PdfAccessControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {        
    }

    /**
     * Init method for this filter
     */
    @Override
    public void init(FilterConfig filterConfig) {        
        this.filterConfig = filterConfig;
        this.manager = (DBManager)filterConfig.getServletContext().getAttribute(Constants.DB_ATTRIBUTE_NAME);
    }
}
