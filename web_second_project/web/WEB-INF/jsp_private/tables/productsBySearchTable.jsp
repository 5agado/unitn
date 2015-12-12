<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un"%>
<un:useConstants var="Constants" className="utilities.Constants" />

<script>
    $(document).ready(function() {
        $("#products").dataTable();
    });
</script>

<h3 class="blue-background text-center">PRODUCTS</h3>
<table id="products" >
    <thead>
        <tr>
            <th>Photo</th>
            <th>Description</th>
            <th>Bid to beat</th>
            <th>N°</th>
            <th>Delivery price</th>
            <th>Expiration</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="p" items="${requestScope[Constants.LIST_PRODUCTS]}">
            <tr>
                <td><img width="100" height="100" src='/WEB_second_project/img/${p.getUrlPhoto()}'></td>
                <td><a href="<c:url value="/${Constants.SM_GET_PRODUCT}"/>?${Constants.IDPRODUCT_PARAM_NAME}=${p.getIdProd()}">${p.getDescription()}</a></td>
                <td>${p.getPrice()}</td>
                <td>${p.getQuantity()}</td>
                <td>${p.getDeliveryPrice()}</td>
                <td>${p.getExpirationTime()}</td>
            </tr>
        </c:forEach>

    </tbody>
</table>
