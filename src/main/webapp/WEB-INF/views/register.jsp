<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register</title>
    <link href="${pageContext.request.contextPath}/resources/styles/main.css" rel="stylesheet" type="text/css">
</head>
<body>

<!-------------------- Begin Container -------------------->
<div id="container">

    <!-------------------- Begin Header -------------------->
    <div id="header">
        <div id="banner">
            <img id="banner-img" src="${pageContext.request.contextPath}/resources/images/spring-by-pivotal.png"
                 alt=""/>
            <h1>My Spring App</h1>
        </div>
        <nav id="header-nav">
            <ul>
                <li><a href="<c:url value="/login"/>">Login</a></li>
                <li><a class="active" href="<c:url value="/register"/>">Register</a></li>
            </ul>
        </nav>
    </div>

    <!-------------------- Begin Body -------------------->
    <div id="body">
        <h3>Register</h3>
        <div class="form-container">
            <sf:form method="post" name="register-form" commandName="user">
                <sf:errors path="*" element="div" cssClass="error-message"/>
                <table class="form-table">
                    <tr>
                        <td class="table-left">
                            <sf:label path="firstName" cssErrorClass="error-field-label">First Name:</sf:label>
                        </td>
                        <td class="table-right">
                            <sf:input path="firstName" cssErrorClass="error-field-input" size="30"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="table-left">
                            <sf:label path="lastName" cssErrorClass="error-field-label">Last Name:</sf:label>
                        </td>
                        <td class="table-right">
                            <sf:input path="lastName" cssErrorClass="error-field-input" size="30"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="table-left">
                            <sf:label path="username" cssErrorClass="error-field-label">Username:</sf:label>
                        </td>
                        <td class="table-right">
                            <sf:input path="username" cssErrorClass="error-field-input" size="30"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="table-left">
                            <sf:label path="password" cssErrorClass="error-field-label">Password:</sf:label>
                        </td>
                        <td class="table-right">
                            <sf:input path="password" cssErrorClass="error-field-input" size="30"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="table-left">
                            <sf:label path="confirmPassword" cssErrorClass="error-field-label">Confirm Password:</sf:label>
                        </td>
                        <td class="table-right">
                            <sf:input path="confirmPassword" cssErrorClass="error-field-input" size="30"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="table-left"></td>
                        <td class="table-right form-buttons">
                            <input type="submit" value="Register">
                            &nbsp;
                            <input type="button" onClick="history.back()" value="Cancel">
                        </td>
                    </tr>
                </table>
            </sf:form>
        </div>
    </div>

    <!-------------------- Begin Footer -------------------->
    <div id="footer">
        <p id="footer-message">&copy; 2017 My Spring App. All rights reserved.</p>

    </div>

</div>
</body>
</html>