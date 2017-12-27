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
            <img id="banner-img" src="${pageContext.request.contextPath}/resources/images/spring-by-pivotal.png"
                 alt=""/>
            <h1>My Spring App</h1>
        </div>
        <nav id="header-nav">
            <a href="<c:url value="/"/>">Home</a>
            <a class="active" href="<c:url value="/note/entries/recent"/>">Notes</a>
        </nav>
    </div>

    <!-------------------- Begin Body -------------------->
    <div id="body">
        <c:choose>
            <c:when test="${param.confirmation == 'added'}">
                <h3>New Note</h3>
                <div class="success-message">
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
        <div class="note-container">
            <table class="note-list-table">
                <tr>
                    <th class="note-list-table-header">Title:</th>
                    <td class="note-list-table-data"><c:out value="${note.title}"/></td>
                </tr>
                <tr>
                    <th class="note-list-table-header">Date:</th>
                    <td class="note-list-table-data"><c:out value="${note.createdAt}"/></td>
                </tr>
                <tr>
                    <th class="note-list-table-header">Body:</th>
                    <td class="note-list-table-data"><c:out value="${note.body}"/></td>
                </tr>
            </table>
            <table class="note-list-table">
                <tr>
                    <td class="note-list-table-option"><a href="/note/edit/${note.id}">Edit</a></td>
                    <td class="note-list-table-option"><a href="/note/delete?noteId=${note.id}">Delete</a></td>
                </tr>
            </table>
        </div>
        <form method="get">
            <input type="button" onClick="window.location.href='<c:url value="/note/entries/recent"/>'"
                   value="View Notes">
            &nbsp;
            <input type="button" onClick="window.location.href='<c:url value="/note/add"/>'" value="Add Another">
        </form>
    </div>

    <!-------------------- Begin Footer -------------------->
    <div id="footer">
        <p id="footer-message">&copy; 2017 My Spring App. All rights reserved.</p>
    </div>

</div>
</body>
</html>
