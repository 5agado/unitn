<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un"%>
<un:useConstants var="Constants" className="utilities.Constants" />

<script>
    $(document).ready(function() {
        $("#bids").dataTable({
            <c:if test="${!showOnlyBid && !showProd}">
                "sDom": '<"top">rt<"bottom"pi><"clear">',
                "iDisplayLength": 5
            </c:if>
    });
    });
</script>

<h3 class="blue-background text-center">BIDS</h3>
<table id="bids" >
    <thead>
        <tr>
            <th>Date</th>
            <c:if test="${showProd}">
                <th>Product</th>
            </c:if>
            <th>Value</th>
            <c:if test="${showBidder}">
                <th>Bidder</th>
            </c:if>
        </tr>
    </thead>
    <tbody>        
        <c:forEach var="b" items="${requestScope[Constants.LIST_BIDS]}">
            <tr>
                <td>${b.getTimestamp()}</td>
                <c:if test="${showProd}">
                    <td><a href="<c:url value="/${Constants.SM_GET_PRODUCT}"/>?${Constants.IDPRODUCT_PARAM_NAME}=${b.getProduct().getIdProd()}">${b.getProduct().getDescription()}</a></td>
                </c:if>
                <td>${b.getBid()}</td>
                <c:if test="${showBidder}">
                    <td>${b.getBidder().getUsername()}</td>
                </c:if>
            </tr>
        </c:forEach>

    </tbody>
</table>
