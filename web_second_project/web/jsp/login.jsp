<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un"%>
<un:useConstants var="Constants" className="utilities.Constants" />
<!-- delete --> <%@page import="utilities.Constants" %>

<!DOCTYPE html>
<html lang='en'>
    <head>
        <meta charset='utf-8'>
        <title>Login</title>
        <meta name='viewport' content='width=device-width, initial-scale=1.0'>
        <meta name='description' content=''>
        <meta name='author' content=''> 

        <link rel='stylesheet' type='text/css' href="<c:url value="${Constants.CSS_BOOTSTRAP}"/>" > 
        <link rel='stylesheet' type='text/css' href="<c:url value="${Constants.CSS_CUSTOMIZATION}"/>" >
    </head>
    <body>
        <div class='container'>
            <form class='form-signin' action='<c:url value="/${Constants.SM_LOGIN}"/>'>
                <h2 class='form-signin-heading'>Sign In</h2>
                <input type='text' name='${Constants.USERNAME_PARAM_NAME}' class='input-block-level' placeholder='Username'>
                <input type='password' name='${Constants.PASSWORD_PARAM_NAME}' class='input-block-level' placeholder='Password'>
                <c:if test="${message != null}"> 
                    <c:choose>
                        <c:when test="${message != Constants.SUCCESSFULLY_SIGN_IN}">
                            <div class='alert alert-error'>
                                ${message}
                            </div>
                        </c:when>
                        <c:when test="${message == Constants.SUCCESSFULLY_SIGN_IN}">
                            <div class='alert alert-success'>
                                ${message}
                            </div>
                        </c:when>
                    </c:choose>
                </c:if>                    
                <button class='btn btn-primary' type='submit'>SIGN IN</button>
                <br><br>
                <p><b>If you don’t already have an account</b>  </p>
                <a class='btn' href='<c:url value="/${Constants.JSP_REGISTER}"/> '>Register</a>
            </form>
        </div>
    </body>
</html>
