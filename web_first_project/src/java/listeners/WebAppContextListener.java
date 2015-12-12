package listeners;

import db.DBManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import static utilities.Constants.*;

public class WebAppContextListener implements ServletContextListener{
    DataSource ds;
    
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try  {
            // ottiene il DataSource che gestisce il pool di connessioni
            InitialContext initialContext = new InitialContext();
            Context context = (Context)initialContext.lookup("java:comp/env");
            ds = (DataSource)context.lookup("connpool");
            
            DBManager manager = new DBManager(ds);
            servletContextEvent.getServletContext().setAttribute(DB_ATTRIBUTE_NAME, manager);
        } catch (NamingException ex) {
            Logger.getLogger(WebAppContextListener.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBManager manager = (DBManager)(sce.getServletContext().getAttribute(DB_ATTRIBUTE_NAME));
        if (manager!=null) {
            sce.getServletContext().removeAttribute(DB_ATTRIBUTE_NAME);
        }
        
    }

}
