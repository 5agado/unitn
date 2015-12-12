<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un"%>
<un:useConstants var="Constants" className="utilities.Constants" />

<!DOCTYPE html>
<html lang='en'>
    <head>
        <meta charset='utf-8'>
        <title>LandingPageAdmin</title>
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
            <div class="hero-padding hero-unit">
                <c:choose>
                    <c:when test="${param[Constants.ACTION_PARAM_NAME] == Constants.ACTION_SHOW_SALES}">
                        <h3 class="blue-background text-center">SALES</h3>
                        <jsp:include page='/${Constants.JSP_SALES_TABLE}' /><br>
                        <br><br>
                        <a class='btn btn-primary' href='<c:url value="/${Constants.SM_GENERATE_EXCEL}"/>'>COMMISSION SHEET</a>
                        <a class='btn btn-primary' href='<c:url value="/${Constants.SM_UPDATE_EXPIRED_AUCTIONS}"/>'>UPDATE DATA</a>
                    </c:when>
                    <c:when test="${param[Constants.ACTION_PARAM_NAME] == Constants.ACTION_SHOW_AUCTIONS}">
                        <jsp:include page='/${Constants.JSP_AUCTIONS_TABLE}' /><br>
                    </c:when>
                </c:choose>               
            </div>

            <jsp:include page='/${Constants.JSP_FOOTER}' />

        </div>
    </body>
</html>

