<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un"%>
<un:useConstants var="Constants" className="utilities.Constants" />

<!DOCTYPE html>
<html lang='en'>
    <head>
        <meta charset='utf-8'>
        <title>Product</title>
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
        <c:set var="p" value="${requestScope[Constants.PRODUCT_PARAM_NAME]}" scope="request"></c:set>
        <c:set var="showProd" value="${false}" scope="request" />
        <c:set var="showBidder" value="${true}" scope="request"/>
        <c:set var="showOnlyBid" value="${requestScope[Constants.ACTION_SHOW_ONLY_BIDS] == 1}" scope="request"/>
        <jsp:include page='/${Constants.JSP_NAVBAR}' />
        <br>
        <div class="container">
            <jsp:include page='/${Constants.JSP_SEARCHBAR}' />
            <br>
            <div class="row">
                <div class="container"><h3>${p.getDescription()}</h3></div>
                <c:if test="${requestScope[Constants.ACTION_SHOW_ONLY_BIDS] != 1}">
                    <div class="span4 photo-padding">
                        <img src='/WEB_second_project/img/${p.getUrlPhoto()}'>
                    </div>
                    <jsp:include page='/${Constants.JSP_BIDFORM}' />
                    <div class="span4 well">
                        <jsp:include page='/${Constants.JSP_BIDS_TABLE}' /><br>
                    </div>
                </c:if>
                <c:if test="${showOnlyBid}">
                    <div class="well">
                        <jsp:include page='/${Constants.JSP_BIDS_TABLE}' /><br><br>
                    </div>
                    <a class='btn btn-primary pull-right' href='<c:url value="${Constants.SM_GET_PRODUCT}"/>?${Constants.IDPRODUCT_PARAM_NAME}=${p.getIdProd()}'>RETURN</a>
                </c:if>
            </div>

            <jsp:include page='/${Constants.JSP_FOOTER}' />

        </div>
    </body>
</html>
