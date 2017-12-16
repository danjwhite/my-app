<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Notes</title>
    <link rel="stylesheet" href="<c:url value="/resources/styles/main.css"/> ">
</head>
<body>
<header>
    <div class="header-title">
        <img src="${pageContext.request.contextPath}/resources/images/spring-by-pivotal.png" alt=""/>
        <h1>My Spring App</h1>
    </div>
    <nav>
        <a class="active" href="<c:url value="/"/>">Home</a>
        <a href="<c:url value="/note/entries/recent"/>">Notes</a>
    </nav>
</header>
<div class="container">
    <h3>Notes</h3>
    <form name="filter" class="align-left" method="get">
        <c:choose>
            <c:when test="${filter == 'recent' && fn:length(notes) > 0}">
                <p class="filter-title">Showing recent notes</p>
            </c:when>
            <c:when test="${filter == 'all' && fn:length(notes) > 0}">
                <p class="filter-title">Showing all notes</p>
            </c:when>
            <c:otherwise>
                <p class="filter-title">No notes found</p>
            </c:otherwise>
        </c:choose>
        <div class="align-right">
            <c:choose>
                <c:when test="${filter == 'recent' && fn:length(notes) > 0}">
                    <input type="button" onclick="window.location.href='<c:url value="/note/entries/all"/>'"
                           value="Show All">
                    &nbsp;
                </c:when>
                <c:when test="${filter == 'all' && fn:length(notes) > 0}">
                    <input type="button" onclick="window.location.href='<c:url value="/note/entries/recent"/>'"
                           value="Show Recent">
                    &nbsp;
                </c:when>
            </c:choose>
            <input type="button" onclick="window.location.href='<c:url value="/note/add"/>'" value="Create Note">
        </div>
    </form>
    <c:forEach items="${notes}" var="note">
        <dl>
            <dt>Title:</dt>
            <dd><c:out value="${note.title}"/></dd>
            <dt>Date:</dt>
            <dd><c:out value="${note.createdAt}"/></dd>
            <dt>Body:</dt>
            <dd><c:out value="${note.body}"/></dd>
        </dl>
    </c:forEach>
</div>
<footer class="footer">&copy; 2017 My Spring App. All rights reserved.</footer>
</body>
</html>