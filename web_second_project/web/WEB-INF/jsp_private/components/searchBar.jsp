<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un"%>
<un:useConstants var="Constants" className="utilities.Constants" />

<div class="container">
    <form action="<c:url value="/${Constants.SM_SEARCH_PRODUCTS}"/>" class="input-append">
        <input class="span9" name="${Constants.REGEXP_PARAM_NAME}" type="text" placeholder="I'm looking for..">
        <select class="span2" name="${Constants.CATEGORY_PARAM_NAME}">
            <option value="0" selected>All</option>
            <c:forEach var="c" items="${applicationScope[Constants.LIST_CATEGORIES]}">
                <option value="${c.getIdCat()}">${c.getName()}</option>
            </c:forEach>
        </select>
        <button class="btn btn-primary" type="submit">Search</button>
    </form>
</div>