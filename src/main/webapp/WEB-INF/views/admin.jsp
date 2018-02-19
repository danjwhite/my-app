<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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
                <li><a href="<c:url value="/"/>">Home</a></li>
                <li><a href="<c:url value="/notes/view"/>">Notes</a></li>
                <sec:authorize access="hasRole('ROLE_ADMIN')">
                    <li><a class="active" href="<c:url value="/admin"/>">Administration</a></li>
                </sec:authorize>
                <li>
                    <a href="<c:url value="/user/${user.username}/view"/>">Account</a>
                    <ul>
                        <li><a href="<c:url value="/logout"/>">Log Out</a></li>
                        <li><a href="<c:url value="/user/${user.username}/view"/>">Settings</a></li>
                    </ul>
                </li>
            </ul>
        </nav>
    </div>

    <!-------------------- Begin Body -------------------->
    <div id="admin-body">
        <h3>Administration</h3>

        <c:choose>
            <c:when test="${param.confirmation == 'created'}">
                <div id="admin-success-message" class="success-message">
                    User created successfully.
                </div>
            </c:when>
            <c:when test="${param.confirmation == 'edited'}">
                <div id="admin-success-message" class="success-message">
                    User created successfully.
                </div>
            </c:when>
        </c:choose>

        <table id="admin-header-table">
            <td>
                <p id="filter-description">User Accounts</p>
            </td>
            <td>
                <form name="filter" method="get">
                    <div id="filter-buttons">
                        <input type="button" onClick="window.location.href='<c:url value="/register?mode=admin"/>'"
                               value="Create User">
                    </div>
                </form>
            </td>
        </table>

        <div id="admin-table-container">
            <table id="admin-table">
                <tr class="header-row">
                    <th class="admin-table-header narrow-col">ID</th>
                    <th class="admin-table-header med-col">First Name</th>
                    <th class="admin-table-header med-col">Last Name</th>
                    <th class="admin-table-header med-col">Username</th>
                    <th class="admin-table-header wide-col"></th>
                </tr>
                <c:forEach items="${users}" var="account">
                    <tr>
                        <td class="admin-table-data narrow-col"><c:out value="${account.id}"/></td>
                        <td class="admin-table-data med-col"><c:out value="${account.firstName}"/></td>
                        <td class="admin-table-data med-col"><c:out value="${account.lastName}"/></td>
                        <td class="admin-table-data med-col"><c:out value="${account.username}"/></td>
                        <td class="button-cell wide-col">
                            <input type="button"
                                   onClick="window.location.href='<c:url value="/user/${account.username}/edit/info?mode=admin"/>'"
                                   value="Edit">
                            &nbsp;
                            <input type="button"
                                   onClick="window.location.href='<c:url value="/user/${account.username}/delete"/>'"
                                   value="Delete">
                        </td>
                    </tr>
                </c:forEach>
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