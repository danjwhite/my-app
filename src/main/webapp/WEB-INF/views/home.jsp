<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <link rel="stylesheet" href="<c:url value="/resources/styles/main.css"/> ">
    <title>My App</title>
</head>
<body>
    <h1>Welcome to My App</h1>

    <a href="<c:url value="/note/entries/recent" /> ">Notes</a> |
    <a href="<c:url value="/note/add" /> ">Add Note</a>
</body>
</html>
