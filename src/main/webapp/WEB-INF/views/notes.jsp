
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Notes</title>
</head>
<body>
    <div class="listTitle">
        <h1>Recent Notes</h1>
        <ul class="noteList">
            <c:forEach items="${notes}" var="note">
                <li>
                    <div class="noteTitle">
                        <c:out value="${note.title}"/>
                    </div>
                    <div class="noteCreateDate">
                        <c:out value="${note.createdAt}"/>
                    </div>
                    <div class="noteBody">
                        <c:out value="${note.body}"/>
                    </div>
                </li>
            </c:forEach>
        </ul>
    </div>
</body>
</html>
