package utilities;

public class Constants {
    //SERVLET CONTEXT
    public static final String DB_ATTRIBUTE_NAME = "dbManager";
    public static final String DBURL_PARAM_NAME = "dbUrl";
    
    //SERVLETS MAPPING
    public static final String SM_LOGIN = "Login";
    public static final String SM_LOGOUT = "/WEB_first_project/Logout";
        //buyer
        public static final String SM_LANDING_PAGE_BUYER = "LandingPageBuyer";
        public static final String SM_SHOW_PRODUCTS_BY_CATEGORY = "ShowProductsByCategory";
        public static final String SM_CONFIRMATION_PAGE = "ConfirmationPage";
        public static final String SM_ADD_PURCHASE = "AddPurchase";
        //seller
        public static final String SM_SHOW_PRODUCTS = "ShowProducts";
        public static final String SM_ADD_PRODUCT = "AddProduct";
        public static final String SM_FORM_ADD_PRODUCT = "FormAddProduct";
        public static final String SM_LANDING_PAGE_SELLER = "LandingPageSeller";
        
    //REQUEST
    public static final String USERNAME_PARAM_NAME = "username";
    public static final String PASSWORD_PARAM_NAME = "password";
    public static final String CATEGORY_PARAM_NAME = "category";
    public static final String PRODUCTNAME_PARAM_NAME = "product";  
    public static final String PHOTO_PARAM_NAME = "photo";
    public static final String QUANTITY_PARAM_NAME = "quantity";
    public static final String UM_PARAM_NAME = "um";     
    public static final String PRICE_PARAM_NAME = "price";
    public static final String PRODUCT_ID_PARAM_NAME = "id";
    
    //SESSION 
    public static final String USER_ATTRIBUTE_NAME = "user";
    
    //ERROR MESSAGE
    public static final String ERROR_MESSAGE_ATTRIBUTE_NAME = "message";
    
    public static final String NO_USERNAME_MESSAGE = "Enter your username";
    public static final String NO_PASSWORD_MESSAGE = "Enter your password";
    public static final String WRONG_CREDENTIALS = "Incorrect Username or Password.";
    public static final String EMPTY_FIELD = "cannot be empty";
    public static final String INVALID_VALUE = ": invalid value";
    
    //CSS
    public static final String CSS_BOOTSTRAP = "/WEB_first_project/css/bootstrap.css";
    public static final String CSS_PERSONALIZATION = "/WEB_first_project/css/personalization.css";
    
}
