<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un"%>
<un:useConstants var="Constants" className="utilities.Constants" />

<div class="well sidebar-nav">
    <div class="nav nav-list">
        <p class="nav-header">Sell a new item now!</p>
        <a class="btn btn-primary" href="<c:url value="/${Constants.JSP_NEWPRODUCT}"/>">SELL IT! </a>
    </div>
</div>
<div class="well sidebar-nav">

    <ul class="nav nav-list">
        <li class="nav-header">Browse by categories</li>
        <c:forEach var="c" items="${applicationScope[Constants.LIST_CATEGORIES]}">
            <li><a href="<c:url value="/${Constants.SM_SEARCH_PRODUCTS}"/>?${Constants.CATEGORY_PARAM_NAME}=${c.getIdCat()}">${c.getName()}</a></li>
        </c:forEach>
    </ul>
</div>
