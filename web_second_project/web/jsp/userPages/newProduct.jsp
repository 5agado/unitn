<%@page import="org.eclipse.jdt.internal.compiler.impl.Constant"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un"%>
<un:useConstants var="Constants" className="utilities.Constants" />

<!DOCTYPE html>
<html lang='en'>
    <head>
        <meta charset='utf-8'>
        <title>NewProduct</title>
        <meta name='viewport' content='width=device-width, initial-scale=1.0'>
        <meta name='description' content=''>
        <meta name='author' content=''> 

        <link rel='stylesheet' type='text/css' href="<c:url value="${Constants.CSS_BOOTSTRAP}"/>" > 
        <link rel='stylesheet' type='text/css' href="<c:url value="${Constants.CSS_CUSTOMIZATION}"/>" >

        <link rel="stylesheet" href="<c:url value="${Constants.CSS_DATATABLES}"/>" >
        <link rel="stylesheet" href="<c:url value="${'/css/bootstrap-fileupload.css'}"/>" >

        <script type="text/javascript" src='<c:url value="${Constants.JS_JQUERY}"/>'></script>
        <script type="text/javascript" src='<c:url value="${Constants.JS_DATATABLES}"/>'></script>
        <script type="text/javascript" src='<c:url value="${'/js/bootstrap-fileupload.js'}"/>'></script>

        <script>
            $('.fileupload').fileupload()
        </script> 
    </head>
    <body>
        <div>
            <form class='container width-adjustment' action='<c:url value="/${Constants.SM_NEWAUCTION}"/>'
                  METHOD=POST ENCTYPE="multipart/form-data">
                <h2 class='form-signin-heading'>Sell your item</h2>
                <div class='controls'>
                    Description: <input class='input-block-level' type='text' name="${Constants.DESCRIPTION_PARAM_NAME}">
                </div>
                <div class='controls'>
                    Init price: <input class='input-block-level' type='text' name="${Constants.INITPRICE_PARAM_NAME}">
                </div>  
                Category
                <div class='controls'>
                    <select name='${Constants.CATEGORY_PARAM_NAME}' class='input-block-level' id='${Constants.CATEGORY_PARAM_NAME}'>
                        <c:forEach var="c" items="${applicationScope[Constants.LIST_CATEGORIES]}">
                            <option value='${c.getIdCat()}'>${c.getName()}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class='controls'>
                    Quantity: <input class='input-block-level' type='text' name='${Constants.QUANTITY_PARAM_NAME}'>
                </div>
                <div class='controls'>
                    Expiration time: <input class='input-block-level' type='text' name="${Constants.EXPIRATION_TIME_PARAM_NAME}">
                </div>
                <div class='controls'>
                    Min price: <input class='input-block-level' type='text' name="${Constants.MINPRICE_PARAM_NAME}">
                </div>
                <div class='controls'>
                    Min increment:
                    <input class='input-block-level' type='text' name="${Constants.MIN_INCREMENT_PARAM_NAME}">
                </div>
                Delivery price:
                <div class="input-prepend ">  
                    <input class='input-block-level' type='text' name="${Constants.DELIVERY_PRICE_PARAM_NAME}">
                </div>
                Photo:
                <div class="fileupload fileupload-new" data-provides="fileupload">
                    <div class="input-append">
                        <div class="uneditable-input span5">
                            <i class="icon-file fileupload-exists"></i> 
                            <span class="fileupload-preview"></span>
                        </div>
                        <span class="btn btn-file">
                            <span class="fileupload-new">Select file</span>
                            <span class="fileupload-exists">Change</span>
                            <input class='input-block-level' type='file' name="${Constants.PHOTO_PARAM_NAME}" /></span>
                        <a href="#" class="btn fileupload-exists" data-dismiss="fileupload">Remove</a>
                    </div>
                </div>
                <c:if test="${message != null}"> 
                    <div class='alert alert-error'>
                        ${message}
                    </div>
                </c:if> 
                <div class='controls'>
                    <button type='submit' class='btn btn-primary'>SUBMIT</button>
                    <a class='btn btn-primary' href='<c:url value="/${Constants.SM_LOAD_DATA}"/> '>RETURN</a>
                </div>
            </form>
        </div>
    </body>
</html>
