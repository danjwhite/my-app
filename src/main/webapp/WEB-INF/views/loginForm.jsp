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
            <img id="banner-img" src="${pageContext.request.contextPath}/resources/images/spring-by-pivotal.png"
                 alt=""/>
            <h1>My Spring App</h1>
        </div>
        <nav id="header-nav">
            <ul>
                <li><a class="active" href="<c:url value="/login"/>">Login</a></li>
                <li><a href="<c:url value="/register"/>">Register</a></li>
            </ul>
        </nav>
    </div>

    <!-------------------- Begin Body -------------------->
    <div id="body">
        <h3>Login</h3>
        <div class="form-container">
            <form method="post" action="<c:url value="/login"/>">
                <table class="form-table">
                    <tr>
                        <td class="table-left">
                            <label>Username:</label>
                        </td>
                        <td class="table-right">
                            <input name="username" type="text"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="table-left">
                            <label>Password:</label>
                        </td>
                        <td class="table-right">
                            <input name="password" type="password"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="table-left"></td>
                        <td class="table-right form-buttons">
                            <input type="submit" value="Login">
                        </td>
                    </tr>
                </table>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            </form>
        </div>
    </div>

    <!-------------------- Begin Footer -------------------->
    <div id="footer">
        <p id="footer-message">&copy; 2017 My Spring App. All rights reserved.</p>

    </div>

</div>
</body>
</html>