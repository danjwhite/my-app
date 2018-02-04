<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Account Info</title>
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
                <li><a href="<c:url value="/"/>">Home</a></li>
                <li><a href="<c:url value="/notes/view"/>">Notes</a></li>
                <li>
                    <a class="active" href="<c:url value="/user/${user.username}/view"/>">Account</a>
                    <ul>
                        <li><a href="<c:url value="/logout"/>">Log Out</a></li>
                        <li><a href="<c:url value="/user/${user.username}/view"/>">Settings</a></li>
                    </ul>
                </li>
            </ul>
        </nav>
    </div>

    <!-------------------- Begin Body -------------------->
    <div id="body">
        <h3>Edit Account Info</h3>
        <div class="form-container">
            <sf:form method="post" name="account-form" modelAttribute="user">
                <sf:hidden path="username"/>
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
                        <td class="table-left"></td>
                        <td class="table-right form-buttons">
                            <input type="submit" value="Save">
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