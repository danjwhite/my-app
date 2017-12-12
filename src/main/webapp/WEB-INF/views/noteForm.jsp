<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <link rel="stylesheet" href="<c:url value="/resources/styles/main.css"/> ">
    <title>New Note</title>
</head>
<body>
    <h1>Add New Note</h1>

    <form method="post" name="noteForm">
        <label class="pad-top">Title:</label>
        <input type="text" name="title"/><br>
        <label class="pad-top">Body:</label><br>
        <textarea name="body" cols="80" rows="5"></textarea><br>
        <input type="submit" value="Add"/>
    </form><br>
    <a href="<c:url value="/" /> ">Cancel</a>
</body>
</html>
