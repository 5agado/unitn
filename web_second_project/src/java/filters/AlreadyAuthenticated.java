package filters;

import db.beans.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static utilities.Constants.*;

public class AlreadyAuthenticated implements Filter {
    private FilterConfig filterConfig = null;
    
    public AlreadyAuthenticated() {
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
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            User user = (User)(httpRequest.getSession().getAttribute(USER_ATTRIBUTE_NAME));
            if (user!=null) {
                HttpServletResponse httpResponse = (HttpServletResponse)response;
                if (user.getRole() == User.Role.ADMIN) {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/" + SM_ADMIN_SALES);
                }
                else if (user.getRole() == User.Role.USER) {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/" + SM_LOAD_DATA);
                }
            }
            else {
                chain.doFilter(request, response);
            }
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
    public void destroy() {        
    }

    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig) {        
        this.filterConfig = filterConfig;
    }
}
