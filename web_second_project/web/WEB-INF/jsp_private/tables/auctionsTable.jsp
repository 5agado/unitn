<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un"%>
<un:useConstants var="Constants" className="utilities.Constants" />

<script>
    $(document).ready(function() {
        $("#products").dataTable();
    });
</script>

<h3 class="blue-background text-center">CURRENT AUCTIONS</h3>
<table id="products" >
    <thead>
        <tr>
            <th>ID</th>
            <th>Description</th>
            <th>Quantity</th>
            <th>Delivery price</th>
            <th>Expiration time</th>
            <th>Seller</th>
            <th></th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="p" items="${requestScope[Constants.LIST_AUCTIONS]}">
            <tr>
                <td>${p.getIdProd()}</td>
                <td><a href="<c:url value="/${Constants.SM_GET_PRODUCT}"/>?${Constants.IDPRODUCT_PARAM_NAME}=${p.getIdProd()}">${p.getDescription()}</a></td>
                <td>${p.getQuantity()}</td>
                <td>${p.getDeliveryPrice()}</td>
                <td>${p.getExpirationTime()}</td>
                <td>${p.getSeller().getUsername()}</td>
                <c:if test="${user.getRole() == 'ADMIN'}">
                    <td><a href="<c:url value="/${Constants.SM_INVALIDATE_AUCTION}"/>?${Constants.IDPRODUCT_PARAM_NAME}=${p.getIdProd()}">DELETE</a></td>    
                </c:if>
            </tr>
        </c:forEach>

    </tbody>
</table>
