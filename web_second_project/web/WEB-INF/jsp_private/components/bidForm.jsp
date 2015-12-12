<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un"%>
<un:useConstants var="Constants" className="utilities.Constants" />

<div class="container span3 well">
    <div>
        <p>Current bid: <b>$${p.getPrice()}</b>
            <a href="<c:url value="/${Constants.SM_GET_PRODUCT}"/>?${Constants.IDPRODUCT_PARAM_NAME}=${p.getIdProd()}&${Constants.ACTION_PARAM_NAME}=1">[${requestScope[Constants.LIST_BIDS].size()} bids]</a>
        </p>
        <c:choose>
            <c:when  test="${p.getCanceled()}">
                <span class="label label-important">DELETED</span>
            </c:when>
            <c:when  test="${p.getExpired()}">
                <span class="label label-important">EXPIRED</span>
            </c:when>
            <c:when test="${user.getRole() != 'ADMIN'}">
                <form class="form-inline" action="<c:url value="/${Constants.SM_NEWBID}"/>">
                    <input type='hidden' name='${Constants.IDPRODUCT_PARAM_NAME}' value="${p.getIdProd()}">
                    <input type='text' name='${Constants.BID_PARAM_NAME}' class='input-small'>
                    <button class='btn btn-primary' type='submit'>Place bid</button>
                </form>
                <span class="label label-important"><b>Closing:</b> ${p.getExpirationTime()}</span>
            </c:when>
            <c:when test="${user.getRole() == 'ADMIN'}"> 
                <span class="label label-important"><b>Closing:</b> ${p.getExpirationTime()}</span>
            </c:when> 
        </c:choose>
        <br><br>
        <c:choose>
            <c:when test="${message!= null && message != Constants.SUCCESSFULLY_BID}">
                <div class='alert alert-error'>
                    ${message}
                </div>
            </c:when>
            <c:when test="${message == Constants.SUCCESSFULLY_BID}">
                <div class='alert alert-success'>
                    ${message}
                </div>
            </c:when>
        </c:choose>
    </div>
    <p><b>Starting bid:</b> $${p.getInitPrice()}</p>
    <p><b>Min increment:</b> $${p.getMinIncrement()}</p>
    <p><b>Delivery price:</b> $${p.getDeliveryPrice()}</p>
    <p><b>Seller:</b> ${p.getSeller().getUsername()}</p>
    <a class='btn btn-primary' href='<c:url value="/${Constants.JSP_LOGIN}"/> '>RETURN</a>
</div>
