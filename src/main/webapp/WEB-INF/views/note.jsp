<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Note</title>
</head>
<body>
    <div class="noteView">
        <div class="noteTitle">
            <c:out value="${note.title}"/>
        </div>
        <div class="noteCreateDate">
            <c:out value="${note.createdAt}"/>
        </div>
        <div class="noteBody">
            <c:out value="${note.body}"/>
        </div>
    </div><br>
    <a href="<c:url value="/note/entries" /> ">Notes</a> |
    <a href="<c:url value="/note/add" /> ">Add Another Note</a>
</body>
</html>
