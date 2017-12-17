<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>New Note</title>
    <link href="${pageContext.request.contextPath}/resources/styles/main.css" rel="stylesheet" type="text/css">
</head>
<body>

<!-------------------- Begin Container -------------------->
<div id="container">

    <!-------------------- Begin Header -------------------->
    <div id="header">
        <div id="banner">
            <img id="banner-img" src="${pageContext.request.contextPath}/resources/images/spring-by-pivotal.png" alt=""/>
            <h1>My Spring App</h1>
        </div>
        <nav id="header-nav">
            <a href="<c:url value="/"/>">Home</a>
            <a class="active" href="<c:url value="/note/entries/recent"/>">Notes</a>
        </nav>
    </div>

    <!-------------------- Begin Body -------------------->
    <div id="body">
        <h3>Note Created:</h3>
        <dl>
            <dt>Title:</dt>
            <dd><c:out value="${note.title}"/></dd>
            <dt>Date:</dt>
            <dd><c:out value="${note.createdAt}"/></dd>
            <dt>Body:</dt>
            <dd><c:out value="${note.body}"/></dd>
        </dl>
        <form method="get">
            <input type="button" onClick="window.location.href='<c:url value="/note/entries/recent"/>'" value="View Notes">
            &nbsp;
            <input type="button" onClick="window.location.href='<c:url value="/note/add"/>'" value="Add Another">
        </form>
    </div>

    <!-------------------- Begin Footer -------------------->
    <div id="footer">
        <p>&copy; 2017 My Spring App. All rights reserved.</p>
    </div>

</div>
</body>
</html>
