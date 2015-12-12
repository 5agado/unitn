<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un"%>
<un:useConstants var="Constants" className="utilities.Constants"/> 

<script>
    $(document).ready(function() {
        $("#sales").dataTable();
    });
</script>

<table id="sales" >
    <thead>
        <tr>
            <th>Photo</th>
            <th>Product</th>
            <th>Quantity</th>
            <th>Price</th>
            <th>Delivery price</th>
            <c:if test="${user.getRole() == 'ADMIN'}">
            <th>Buyer</th>
            <th>Seller</th>
            </c:if>
            <th>Tax</th>
            <th>Status</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="s" items="${requestScope[Constants.LIST_SALES]}">
            <tr>
                <td><img width="100" height="100" src='/WEB_second_project/img/${s.getUrlPhoto()}'></td>
                <td><a href="<c:url value="/${Constants.SM_GET_PRODUCT}"/>?${Constants.IDPRODUCT_PARAM_NAME}=${s.getIdProd()}">${s.getDescription()}</a></td>
                <td>${s.getQuantity()}</td>
                <td>${s.getPrice()}</td>
                <td>${s.getDeliveryPrice()}</td>
                <c:if test="${user.getRole() == 'ADMIN'}">
                <td>${s.getBuyer().getUsername()}</td>
                <td>${s.getSeller().getUsername()}</td>
                </c:if>
                <td>${s.getTax()}</td>
                <c:choose>
                            <c:when test="${s.getCanceled()}">
                                <td><i class="icon-remove" title="Canceled"></i></td>
                            </c:when>
                            <c:when test="${s.getExpired()}">
                                <td><img src="<c:url value="/img/ico_noAV.png"/>" title="Expired"/></td>
                            </c:when>
                            <c:when test="${!s.getExpired()}">
                                <td><img src="<c:url value="/img/ico_AV.png"/>" title="Closing: ${s.getExpirationTime()}"/></td>
                            </c:when>
                </c:choose>
            </tr>
        </c:forEach>

    </tbody>
</table>
