<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un"%>
<un:useConstants var="Constants" className="utilities.Constants" />

<div class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container-fluid">
            <div class='navbar-text, brand' ><span class="b">Best</span><span class="r">Bid</span></div>
            <ul class="nav">
                <c:choose>
                    <c:when test="${user.getRole() == 'USER'}">
                        <li><a href="<c:url value="/${Constants.SM_LOAD_DATA}"/>">Home</a></li>
                        <li><a href="<c:url value="/${Constants.SM_LOAD_DATA}?${Constants.ACTION_PARAM_NAME}=${Constants.ACTION_SHOW_PURCHASES}"/>">Purchases</a></li>
                        <li><a href="<c:url value="/${Constants.SM_LOAD_DATA}?${Constants.ACTION_PARAM_NAME}=${Constants.ACTION_SHOW_BIDS}"/>">MyBids</a></li>
                        <li><a href="<c:url value="/${Constants.SM_LOAD_DATA}?${Constants.ACTION_PARAM_NAME}=${Constants.ACTION_SHOW_SALES}"/>">MyProducts</a></li>
                        <li><a href="<c:url value="/${Constants.SM_LOAD_DATA}?${Constants.ACTION_PARAM_NAME}=${Constants.ACTION_SHOW_LOST_AUCTIONS}"/>">Lost Auctions</a></li>
                    </c:when>
                    <c:when test="${user.getRole() == 'ADMIN'}">
                        <li><a href="<c:url value="/${Constants.SM_ADMIN_SALES}?${Constants.ACTION_PARAM_NAME}=${Constants.ACTION_SHOW_SALES}"/>">Sales</a></li>
                        <li><a href="<c:url value="/${Constants.SM_ADMIN_CURRENT_AUCTIONS}?${Constants.ACTION_PARAM_NAME}=${Constants.ACTION_SHOW_AUCTIONS}"/>">Current Auctions</a></li>
                    </c:when>
                </c:choose>   
            </ul>
            <c:if test="${user.getRole() == 'ADMIN'}">
            <a class='btn btn-small' href='<c:url value="/${Constants.SM_GENERATE_EXCEL}"/>'>COMMISSION SHEET</a>
            <a class='btn btn-small' href='<c:url value="/${Constants.SM_UPDATE_EXPIRED_AUCTIONS}"/>'>UPDATE DATA</a>
            </c:if>
            <a class='btn btn-small pull-right' href='<c:url value="/${Constants.SM_LOGOUT}"/>'>Sign Out</a>
            <div class='navbar-text pull-right'>
                Logged in as <b>${user.getUsername()}</b>&nbsp;&nbsp;
            </div>
        </div>
    </div>
</div>
