<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un"%>
<un:useConstants var="Constants" className="utilities.Constants" />

<!DOCTYPE html>
<html lang='en'>
    <head>
        <meta charset='utf-8'>
        <title>SearchProducts</title>
        <meta name='viewport' content='width=device-width, initial-scale=1.0'>
        <meta name='description' content=''>
        <meta name='author' content=''> 

        <link rel='stylesheet' type='text/css' href="<c:url value="${Constants.CSS_BOOTSTRAP}"/>" > 
        <link rel='stylesheet' type='text/css' href="<c:url value="${Constants.CSS_CUSTOMIZATION}"/>" >

        <link rel="stylesheet" href="<c:url value="${Constants.CSS_DATATABLES}"/>" >
        <script type="text/javascript" src='<c:url value="${Constants.JS_JQUERY}"/>'></script>
        <script type="text/javascript" src='<c:url value="${Constants.JS_DATATABLES}"/>'></script>
    </head>
    <body class="white-background">
        <jsp:include page='/${Constants.JSP_NAVBAR}' />
        <br>
        <div class="container">
            <jsp:include page='/${Constants.JSP_SEARCHBAR}' />
            <br>
            <div class="row">
                <div class="span3">
                    <jsp:include page='/${Constants.JSP_SIDEBAR}' />
                </div>
                <div class="span9">
                    <div class="hero-padding hero-unit">
                        <jsp:include page='/${Constants.JSP_PRODUCTS_BY_SEARCH_TABLE}' /><br>
                    </div>
                </div>
            </div>

            <jsp:include page='/${Constants.JSP_FOOTER}' />

        </div>
    </body>
</html>
