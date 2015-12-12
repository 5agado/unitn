<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un"%>
<un:useConstants var="Constants" className="utilities.Constants" />
<!-- delete --> <%@page import="utilities.Constants" %>
<%@ page import="net.tanesha.recaptcha.ReCaptcha" %>
<%@ page import="net.tanesha.recaptcha.ReCaptchaFactory" %>

<!DOCTYPE html>
<html lang='en'>
    <head>
        <meta charset='utf-8'>
        <title>Register</title>
        <meta name='viewport' content='width=device-width, initial-scale=1.0'>
        <meta name='description' content=''>
        <meta name='author' content=''> 

        <link rel='stylesheet' type='text/css' href="<c:url value="${Constants.CSS_BOOTSTRAP}"/>" > 
        <link rel='stylesheet' type='text/css' href="<c:url value="${Constants.CSS_CUSTOMIZATION}"/>" >
    </head>
    <body>
        <div class="container">
            <form class='form-signin form-width-adjustment' action='<c:url value="/${Constants.SM_SIGN_IN}"/>'>
                <h2 class='form-signin-heading'>Registration</h2>
                Username
                <div class='controls'>
                    <input class='input-block-level' type='text' name="${Constants.USERNAME_PARAM_NAME}">
                </div>
                Email
                <div class='controls'>
                    <input class='input-block-level' type='text' name="${Constants.EMAIL_PARAM_NAME}">
                </div>
                Address
                <div class='controls'>
                    <input class='input-block-level' type='text' name="${Constants.ADDRESS_PARAM_NAME}">
                </div>
                Password
                <div class='controls'>
                    <input class='input-block-level' type='password' id='${Constants.PASSWORD_PARAM_NAME}' name="${Constants.PASSWORD_PARAM_NAME}">
                </div>
                <c:if test="${message != null}"> 
                    <div class='alert alert-error'>
                        ${message}
                    </div>
                </c:if>   
                <%
                    ReCaptcha c = ReCaptchaFactory.newReCaptcha("6LeMUdsSAAAAAHl1S9g9sZeEDAu4yAMBFs2YC3tR", "6LeMUdsSAAAAAPXozztyYzuM4axaQyHzrptWv29h", false);
                    out.print(c.createRecaptchaHtml(null, null));
                %>
                <br>
                <div class='controls'>
                    <button type='submit' class='btn btn-primary'>SUBMIT</button>
                    <a class='btn' href='<c:url value="/${Constants.JSP_LOGIN}"/> '>RETURN</a>
                </div>
            </form>
        </div>
    </body>
</html>
