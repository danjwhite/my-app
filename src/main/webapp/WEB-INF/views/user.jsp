<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>New Note</title>
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
                <li><a href="<c:url value="/notes/view/entries"/>">Notes</a></li>
                <li>
                    <a class="active" href="<c:url value="/account/view?username=${user.username}"/>">Account</a>
                    <ul>
                        <li><a href="<c:url value="/logout"/>">Log Out</a></li>
                        <li><a href="<c:url value="/account/view?username=${user.username}"/>">Settings</a></li>
                    </ul>
                </li>
            </ul>
        </nav>
    </div>

    <!-------------------- Begin Body -------------------->
    <div id="account-body">
        <h3>Account</h3>
        <c:choose>
            <c:when test="${param.confirmation == 'created'}">
                <div class="success-message">
                    Account created successfully.
                </div>
            </c:when>
            <c:when test="${param.confirmation == 'infoUpdated'}">
                <div class="success-message">
                    Account updated successfully.
                </div>
            </c:when>
            <c:when test="${param.confirmation == 'passwordUpdated'}">
                <div class="success-message">
                    Password updated successfully.
                </div>
            </c:when>
        </c:choose>
        <h4>Account Info</h4>
        <div class="box-container">
            <table id="account-table" class="text-table">
                <tr>
                    <th class="text-table-header account-table-header">First Name:</th>
                    <td class="text-table-data"><c:out value="${user.firstName}"/></td>
                </tr>
                <tr>
                    <th class="text-table-header account-table-header">Last Name:</th>
                    <td class="text-table-data"><c:out value="${user.lastName}"/></td>
                </tr>
                <tr>
                    <th class="text-table-header account-table-header">Username:</th>
                    <td class="text-table-data"><c:out value="${user.username}"/></td>
                </tr>
            </table>
            <table class="text-table">
                <tr>
                    <td class="text-table-option"><a
                            href="<c:url value="/account/edit/info?username=${user.username}"/>">Edit</a>
                    </td>
                </tr>
            </table>
        </div>
        <h4>Account Settings</h4>
        <div class="box-container">
            <table id="settings-table" class="text-table">
                <tr>
                    <td class="text-table-data"><a href="<c:url value="/account/edit/password?username=${user.username}"/>">Change
                        password</a></td>
                </tr>
                <tr>
                    <td class="text-table-data"><a href="<c:url value="/account/delete?username=${user.username}"/>">Delete
                        account</a></td>
                </tr>
            </table>
        </div>
    </div>

    <!-------------------- Begin Footer -------------------->
    <div id="footer">
        <p id="footer-message">&copy; 2017 My Spring App. All rights reserved.</p>
    </div>

</div>
</body>
</html>
