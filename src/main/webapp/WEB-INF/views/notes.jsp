<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
        <h3>Notes</h3>
        <table width="1200" id="filter-form-table">
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${filter == 'recent' && fn:length(notes) > 0}">
                            <p id="filter-description">Showing Recent Notes</p>
                        </c:when>
                        <c:when test="${filter == 'all' && fn:length(notes) > 0}">
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
                                <c:when test="${filter == 'recent' && fn:length(notes) > 0}">
                                    <input type="button"
                                           onClick="window.location.href='<c:url value="/note/entries/all"/>'"
                                           value="Show All">
                                    &nbsp;
                                </c:when>
                                <c:when test="${filter == 'all' && fn:length(notes) > 0}">
                                    <input type="button"
                                           onClick="window.location.href='<c:url value="/note/entries/recent"/>'"
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
                        <td class="note-list-table-option"><a href="">View</a></td>
                        <td class="note-list-table-option"><a href="">Edit</a></td>
                        <td class="note-list-table-option"><a href="">Delete</a></td>
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