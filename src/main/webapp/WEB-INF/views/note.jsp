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
                <li><a class="active" href="<c:url value="/notes/view/entries"/>">Notes</a></li>
                <li>
                    <a href="<c:url value="/account/view?username=${user.username}"/>">Account</a>
                    <ul>
                        <li><a href="<c:url value="/logout"/>">Log Out</a></li>
                        <li><a href="<c:url value="/account/view?username=${user.username}"/>">Settings</a></li>
                    </ul>
                </li>
            </ul>
        </nav>
    </div>

    <!-------------------- Begin Body -------------------->
    <div id="body">
        <c:choose>
            <c:when test="${param.confirmation == 'added'}">
                <h3>New Note</h3>
                <div id="note-success-message" class="success-message">
                    Note created successfully.
                </div>
            </c:when>
            <c:when test="${param.confirmation == 'edited'}">
                <h3>Updated Note</h3>
                <div class="success-message">
                    Note updated successfully.
                </div>
            </c:when>
            <c:otherwise>
                <h3>View Note</h3>
            </c:otherwise>
        </c:choose>
        <div class="box-container">
            <table class="text-table">
                <tr>
                    <th class="text-table-header note-table-header">Title:</th>
                    <td class="text-table-data"><c:out value="${note.title}"/></td>
                </tr>
                <tr>
                    <th class="text-table-header note-table-header">Date:</th>
                    <td class="text-table-data"><c:out value="${note.createdAt}"/></td>
                </tr>
                <tr>
                    <th class="text-table-header note-table-header">Body:</th>
                    <td class="text-table-data"><c:out value="${note.body}"/></td>
                </tr>
            </table>
            <table class="text-table">
                <tr>
                    <td class="text-table-option"><a
                            href="${pageContext.request.contextPath}/notes/edit?noteId=${note.id}">Edit</a></td>
                    <td class="text-table-option"><a
                            href="${pageContext.request.contextPath}/notes/delete?noteId=${note.id}">Delete</a></td>
                </tr>
            </table>
        </div>
        <form method="get">
            <input type="button" onClick="window.location.href='<c:url value="/notes/view/entries"/>'"
                   value="View Notes">
            &nbsp;
            <input type="button" onClick="window.location.href='<c:url value="/notes/add"/>'" value="Add Another">
        </form>
    </div>

    <!-------------------- Begin Footer -------------------->
    <div id="footer">
        <p id="footer-message">&copy; 2017 My Spring App. All rights reserved.</p>
    </div>

</div>
</body>
</html>
