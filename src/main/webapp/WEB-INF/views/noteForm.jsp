<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Note</title>
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
    <h3>New Note</h3>
    <form method="post" name="noteForm">
        <table>
            <tr>
                <th>Title:</th>
                <td><input type="text" size="50" name="title"></td>
            </tr>
            <tr>
                <th>Body:</th>
                <td><textarea name="body" cols="80" rows="15"></textarea></td>
            </tr>
            <tr>
                <th></th>
                <td>
                    <input type="submit" value="Add">
                    &nbsp;
                    <input type="button" onclick="window.location.href='/'" value="Cancel">
                </td>
            </tr>
        </table>
    </form>
</div>
<footer class="footer">&copy; 2017 My Spring App. All rights reserved.</footer>
</body>
</html>
