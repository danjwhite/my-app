<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Note</title>
    <link rel="stylesheet" href="<c:url value="/resources/styles/main.css"/> ">
</head>
<body>

    <h1>Note created successfully.</h1>

    <p>Here is the information you entered:</p>

    <label>Title:</label>
    <span>${note.title}</span><br>
    <label>Date:</label>
    <span>${note.createdAt}</span><br>
    <label>Body:</label><br>
    <p>${note.body}</p>

    <a href="<c:url value="/note/entries/recent" /> ">Notes</a> |
    <a href="<c:url value="/note/add" /> ">Add Another Note</a>
</body>
</html>
