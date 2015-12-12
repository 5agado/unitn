<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un"%>
<un:useConstants var="Constants" className="utilities.Constants" />

<script>
    $(document).ready(function() {
        $("#lostAuctions").dataTable();
    });
</script>

<h3 class="blue-background text-center">LOST AUCTIONS</h3>
<table id="lostAuctions" >
    <thead>
        <tr>
            <th>Photo</th>
            <th>Description</th>
            <th>Price</th>
            <th>Status</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="p" items="${requestScope[Constants.LIST_LOST_AUCTIONS]}">
            <tr>
                <td><img width="100" height="100" src='/WEB_second_project/img/${p.getUrlPhoto()}'></td>
                <td><a href="<c:url value="/${Constants.SM_GET_PRODUCT}"/>?${Constants.IDPRODUCT_PARAM_NAME}=${p.getIdProd()}">${p.getDescription()}</a></td>
                <td>${p.getPrice()}</td>
                <c:choose>
                            <c:when test="${p.getCanceled()}">
                            <td><i class="icon-remove" title="Canceled"></i></td>
                            </c:when>
                            <c:when test="${!p.getCanceled()}">
                            <td><img src="<c:url value="/img/ico_noAV.png"/>" title="Expired"/></td>
                            </c:when>
                </c:choose>
            </tr>
        </c:forEach>

    </tbody>
</table>
