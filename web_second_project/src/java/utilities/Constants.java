package utilities;

public class Constants {
    //SERVLET CONTEXT
    public static final String DB_ATTRIBUTE_NAME = "dbManager";
    public static final String DBURL_PARAM_NAME = "dbUrl";
    public static final String IMGSDIR_PARAM_NAME = "imgsDir";
    
    // JSP MAPPING
    public static final String JSP_LOGIN = "jsp/login.jsp";
    public static final String JSP_REGISTER = "jsp/register.jsp";
        //COMPONENTS
        public static final String JSP_NAVBAR = "WEB-INF/jsp_private/components/navbar.jsp";
        public static final String JSP_SEARCHBAR = "WEB-INF/jsp_private/components/searchBar.jsp";
        public static final String JSP_SIDEBAR = "WEB-INF/jsp_private/components/sidebar.jsp";
        public static final String JSP_FOOTER = "WEB-INF/jsp_private/components/footer.jsp";
        public static final String JSP_BIDFORM = "WEB-INF/jsp_private/components/bidForm.jsp";
        //TABLES
        public static final String JSP_PURCHASES_TABLE = "WEB-INF/jsp_private/tables/purchasesTable.jsp";
        public static final String JSP_BIDS_TABLE = "WEB-INF/jsp_private/tables/bidsTable.jsp";
        public static final String JSP_AUCTIONS_TABLE = "WEB-INF/jsp_private/tables/auctionsTable.jsp";
        public static final String JSP_SALES_TABLE = "WEB-INF/jsp_private/tables/salesTable.jsp";
        public static final String JSP_LOSTAUCTIONS_TABLE = "WEB-INF/jsp_private/tables/lostAuctionsTable.jsp";
        public static final String JSP_PRODUCTS_BY_SEARCH_TABLE = "WEB-INF/jsp_private/tables/productsBySearchTable.jsp";
        //ADMIN
        public static final String JSP_ADMIN_PRODUCT = "jsp/adminPages/product.jsp";
        public static final String JSP_LANDING_PAGE_ADMIN = "jsp/adminPages/landingPage.jsp";
        //USER
        public static final String JSP_PRODUCT = "jsp/userPages/product.jsp";
        public static final String JSP_SEARCH_PRODUCTS = "jsp/userPages/searchProducts.jsp";
        public static final String JSP_NEWPRODUCT = "jsp/userPages/newProduct.jsp";
        public static final String JSP_TABLE_PAGE = "jsp/userPages/tablePage.jsp";
        public static final String JSP_LANDING_PAGE_USER = "jsp/userPages/landingPage.jsp";
    
    // JS MAPPING
    public static final String JS_JQUERY = "/js/jquery.js";
    public static final String JS_DATATABLES = "/js/jquery.dataTables.min.js";
    
    //SERVLETS MAPPING
    public static final String SM_LOGIN = "Login";
    public static final String SM_LOGOUT = "Logout";
    public static final String SM_SIGN_IN = "SignIn";
    public static final String SM_NEWAUCTION = "user/NewAuction";
    public static final String SM_NEWBID = "user/NewBid";
    public static final String SM_INVALIDATE_AUCTION = "admin/InvalidateAuction";
    public static final String SM_LOAD_DATA = "user/LoadData";
    public static final String SM_GET_PRODUCT = "GetProduct";
    public static final String SM_ADMIN_CURRENT_AUCTIONS = "admin/AdminCurrentAuctions";
    public static final String SM_ADMIN_SALES = "admin/AdminSales";
    public static final String SM_SEARCH_PRODUCTS = "user/SearchProducts";
    public static final String SM_GENERATE_EXCEL = "admin/GenerateExcel";
    public static final String SM_UPDATE_EXPIRED_AUCTIONS = "admin/UpdateExpiredAuctions";
        
    //REQUEST PARAM NAME
    public static final String USERNAME_PARAM_NAME = "username";
    public static final String EMAIL_PARAM_NAME = "email";
    public static final String ADDRESS_PARAM_NAME = "address";
    public static final String PASSWORD_PARAM_NAME = "password";
    public static final String CATEGORY_PARAM_NAME = "category";
    public static final String IDPRODUCT_PARAM_NAME = "idProduct";
    public static final String DESCRIPTION_PARAM_NAME = "description";
    public static final String BID_PARAM_NAME = "bid";
    public static final String PRODUCT_PARAM_NAME = "product";  
    public static final String PHOTO_PARAM_NAME = "photo";
    public static final String QUANTITY_PARAM_NAME = "quantity";
    public static final String REGEXP_PARAM_NAME = "regexp";     
    public static final String INITPRICE_PARAM_NAME = "initPrice";
    public static final String MINPRICE_PARAM_NAME = "minPrice";
    public static final String MIN_INCREMENT_PARAM_NAME = "minIncrement";
    public static final String DELIVERY_PRICE_PARAM_NAME = "delPrice";
    public static final String EXPIRATION_TIME_PARAM_NAME = "expitationTime";
    public static final String ACTION_PARAM_NAME = "action";
    
    //LIST DB
    public static final String LIST_PURCHASES = "purchases";
    public static final String LIST_BIDS = "bids";
    public static final String LIST_LOST_AUCTIONS = "lostauctions";
    public static final String LIST_SALES = "sales";
    public static final String LIST_AUCTIONS = "auctions";
    public static final String LIST_CATEGORIES = "categories";
    public static final String LIST_PRODUCTS = "products";
    
    //ACTIONS
    public static final String ACTION_SHOW_ALL = "all";
    public static final String ACTION_SHOW_PURCHASES = "purchases";
    public static final String ACTION_SHOW_BIDS = "bids";
    public static final String ACTION_SHOW_ONLY_BIDS = "onlyBids";
    public static final String ACTION_SHOW_LOST_AUCTIONS = "lostauctions";
    public static final String ACTION_SHOW_SALES = "sales";
    public static final String ACTION_SHOW_AUCTIONS = "auctions";
    
    //SESSION 
    public static final String USER_ATTRIBUTE_NAME = "user";
    
    //ERROR MESSAGE
    public static final String ERROR_MESSAGE_ATTRIBUTE_NAME = "message";
    public static final String SUCCESS_MESSAGE_ATTRIBUTE_NAME = "message";
    
    public static final String NO_USERNAME_MESSAGE = "Enter your username";
    public static final String NO_PASSWORD_MESSAGE = "Enter your password";
    public static final String WRONG_CREDENTIALS = "Incorrect Username or Password.";
    public static final String EMPTY_FIELD = "cannot be empty";
    public static final String INVALID_VALUE = ": invalid value";
    public static final String SUCCESSFULLY_SIGN_IN = "Registration successful";
    public static final String USERNAME_ALREADY_EXISTS = "Sorry, that username already exists!";
    public static final String SUCCESSFULLY_BID = "Bid: successfully added";
    
    //CSS
    public static final String CSS_BOOTSTRAP = "/css/bootstrap.css";
    public static final String CSS_CUSTOMIZATION = "/css/customization.css";
    public static final String CSS_DATATABLES = "/css/jquery.dataTables.css";
}
