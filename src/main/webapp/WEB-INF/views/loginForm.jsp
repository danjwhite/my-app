<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>
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
                <li><a class="active" href="<c:url value="/login"/>">Login</a></li>
                <li><a href="<c:url value="/register"/>">Register</a></li>
                --%>
            </ul>
        </nav>
    </div>

    <!-------------------- Begin Body -------------------->
    <div id="login-body">
        <div id="login-window-wrapper">
            <c:if test="${param.error != null}">
                <div id="login-error-message" class="error-message">
                    Incorrect username and password
                </div>
            </c:if>
            <div id="login-window-header-container">
                <h5>Account Login</h5>
            </div>
            <div id="login-form-container">
                <form method="post" id="login-form" name="login-form">
                    <table class="form-table" id="login-form-table">
                        <tr>
                            <c:choose>
                                <c:when test="${param.error != null}">
                                    <td class="login-table-left"><label>Username:</label></td>
                                    <td class="login-table-right"><input type="text" class="error-field-input"/></td>
                                </c:when>
                                <c:otherwise>
                                    <td class="login-table-left"><label>Username:</label></td>
                                    <td class="login-table-right"><input type="text"/></td>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                        <tr>
                            <c:choose>
                                <c:when test="${param.error != null}">
                                    <td class="login-table-left"><label>Password:</label></td>
                                    <td class="login-table-right"><input type="password" class="error-field-input"/></td>
                                </c:when>
                                <c:otherwise>
                                    <td class="login-table-left"><label>Password:</label></td>
                                    <td class="login-table-right"><input type="password"/></td>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                        <tr>
                            <td class="login-table-left"></td>
                            <td class="login-table-right form-buttons">
                                <div id="login-form-buttons">
                                    <input type="submit" class="button" value="Login"/>
                                    <input type="reset" class="button" value="Reset"/>
                                </div>
                            </td>
                        </tr>
                    </table>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                </form>
            </div>
            <div id="login-window-link-container">
                <a href="<c:url value="/register"/>">Create account</a>
            </div>
        </div>
    </div>

    <!-------------------- Begin Footer -------------------->
    <div id="footer">
        <p id="footer-message">&copy; 2017 My Spring App. All rights reserved.</p>

    </div>

</div>
</body>
</html>