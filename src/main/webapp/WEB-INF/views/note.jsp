<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Note</title>
    <link rel="stylesheet" href="<c:url value="/resources/styles/main.css"/> ">
</head>
<body>

    <h2 class="title"><span class="main-title">Entry created:</span></h2>
    <h3 class="note-title">${note.title}</h3>
    <p class="date">Added: ${note.createdAt}</p>
    <p class="body">${note.body}</p>


<a href="<c:url value="/note/entries/recent" /> ">Notes</a> |
<a href="<c:url value="/note/add" /> ">Add Another Note</a>
</body>
</html>
