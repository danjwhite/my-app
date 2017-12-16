<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Note</title>
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
    <h3 class="confirmation">Note Created:</h3>
    <dl>
        <dt>Title:</dt>
        <dd><c:out value="${note.title}"/></dd>
        <dt>Date:</dt>
        <dd><c:out value="${note.createdAt}"/></dd>
        <dt>Body:</dt>
        <dd><c:out value="${note.body}"/></dd>
    </dl>
    <form method="get">
        <input type="button" onclick="window.location.href='<c:url value="/note/entries/recent"/>'" value="View Notes">
        &nbsp;
        <input type="button" onclick="window.location.href='<c:url value="/note/add"/>'" value="Add Another">
    </form>
</div>
<footer class="footer">&copy; 2017 My Spring App. All rights reserved.</footer>
</body>
</html>
