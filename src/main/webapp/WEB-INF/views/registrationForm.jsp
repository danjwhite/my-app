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
            <h1>My Spring App</h1>
        </div>
        <nav id="header-nav">
            <ul>
                <%--
                <li><a href="<c:url value="/login"/>">Login</a></li>
                <li><a class="active" href="<c:url value="/register"/>">Register</a></li>
                --%>
            </ul>
        </nav>
    </div>

    <!-------------------- Begin Body -------------------->
    <div id="body">
        <h4>Register</h4>
        <div class="form-container">
            <sf:form method="post" name="register-form" modelAttribute="user">
                <table class="form-table">
                    <tr>
                        <td class="table-left">
                            <sf:label path="firstName">First Name:</sf:label>
                        </td>
                        <td class="table-right">
                            <sf:input path="firstName" cssErrorClass="error-field-input" size="30"/>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td class="error-cell"><sf:errors path="firstName"/></td>
                    </tr>
                    <tr>
                        <td class="table-left">
                            <sf:label path="lastName">Last Name:</sf:label>
                        </td>
                        <td class="table-right">
                            <sf:input path="lastName" cssErrorClass="error-field-input" size="30"/>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td class="error-cell"><sf:errors path="lastName"/></td>
                    </tr>
                    <tr>
                        <td class="table-left">
                            <sf:label path="username">Username:</sf:label>
                        </td>
                        <td class="table-right">
                            <sf:input path="username" cssErrorClass="error-field-input" size="30"/>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td class="error-cell"><sf:errors path="username"/></td>
                    </tr>
                    <tr>
                        <td class="table-left">
                            <sf:label path="password">Password:</sf:label>
                        </td>
                        <td class="table-right">
                            <sf:input path="password" cssErrorClass="error-field-input" size="30"/>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td class="error-cell"><sf:errors path="password"/></td>
                    </tr>
                    <tr>
                        <td class="table-left">
                            <sf:label path="confirmPassword">Confirm Password:</sf:label>
                        </td>
                        <td class="table-right">
                            <sf:input path="confirmPassword" cssErrorClass="error-field-input" size="30"/>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td class="error-cell"><sf:errors path="confirmPassword"/></td>
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