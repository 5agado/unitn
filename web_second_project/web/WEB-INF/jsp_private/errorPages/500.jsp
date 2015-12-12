<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un"%>
<un:useConstants var="Constants" className="utilities.Constants" />

<!DOCTYPE html>
<html lang='en'>
    <head>
        <meta charset='utf-8'>
        <title>500</title>
        <meta name='viewport' content='width=device-width, initial-scale=1.0'>
        <meta name='description' content=''>
        <meta name='author' content=''>

        <link rel='stylesheet' type='text/css' href="<c:url value="${Constants.CSS_BOOTSTRAP}"/>"> 
        <link rel='stylesheet' type='text/css' href="<c:url value="${Constants.CSS_CUSTOMIZATION}"/>">
        <meta http-equiv='REFRESH' content='7; url=/WEB_second_project/'>
    </head>
    <body class="white-background">
        <div class="container">

            <img src="<c:url value="/img/500.png"/>" width="239" height="93">

            <h3>The server encountered an internal error.</h3>

            <h4>Please wait while we redirect you to the home page.
            </h4>

        </div>
    </body>
</html>