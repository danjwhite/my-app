<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Spring App</title>
    <link rel="stylesheet" href="<c:url value="/resources/styles/main.css"/> ">
</head>
<body>
<header>
    <div class="header-title">
        <img src="${pageContext.request.contextPath}/resources/images/spring-by-pivotal.png" alt=""/>
        <h1>My Spring App</h1>
    </div>
    <nav>
        <a class="active" href="<c:url value="/"/> ">Home</a>
        <a href="<c:url value="/note/entries/recent"/>">Notes</a>
    </nav>
</header>
<br>
<div class="container">
    <h3>Create a new note or view existing notes.</h3>
</div>
<footer class="footer">&copy; 2017 My Spring App. All rights reserved.</footer>
</body>
</html>