<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Notes</title>
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
                <li><a class="active" href="<c:url value="/notes/view"/>">Notes</a></li>
                <sec:authorize access="hasRole('ROLE_ADMIN')">
                    <li><a href="<c:url value="/admin"/>">Administration</a></li>
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
    <div id="body">
        <h3>Notes</h3>
        <table width="1200" id="filter-form-table">
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${display == 'recent' && fn:length(notes) > 0}">
                            <p id="filter-description">Showing Recent Notes</p>
                        </c:when>
                        <c:when test="${display == 'all' && fn:length(notes) > 0}">
                            <p id="filter-description">Showing All Notes</p>
                        </c:when>
                        <c:otherwise>
                            <p id="filter-description">No Notes Found</p>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <td>
                    <form name="filter" method="get">
                        <div id="filter-buttons">
                            <c:choose>
                                <c:when test="${display == 'recent' && fn:length(notes) > 0}">
                                    <input type="button"
                                           onClick="window.location.href='<c:url
                                                   value="/notes/view?display=all"/>'"
                                           value="Show All">
                                    &nbsp;
                                </c:when>
                                <c:when test="${display == 'all' && fn:length(notes) > 0}">
                                    <input type="button"
                                           onClick="window.location.href='<c:url
                                                   value="/notes/view?display=recent"/>'"
                                           value="Show Recent">
                                    &nbsp;
                                </c:when>
                            </c:choose>
                            <input type="button" onClick="window.location.href='<c:url value="/note/add"/>'"
                                   value="Create Note">
                        </div>
                    </form>
                </td>
            </tr>
        </table>
        <c:forEach items="${notes}" var="note">
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
                                href="${pageContext.request.contextPath}/note/${note.id}/view">View</a>
                        </td>
                        <td class="text-table-option"><a
                                href="${pageContext.request.contextPath}/note/${note.id}/edit">Edit</a></td>
                        <td class="text-table-option"><a
                                href="${pageContext.request.contextPath}/note/${note.id}/delete">Delete</a></td>
                    </tr>
                </table>
            </div>
        </c:forEach>

    </div>

    <!-------------------- Begin Footer -------------------->
    <div id="footer">
        <p id="footer-message">&copy; 2017 My Spring App. All rights reserved.</p>
    </div>

</div>
</body>
</html>