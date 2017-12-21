<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>My App</title>
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
            <a class="active" href="<c:url value="/"/>">Home</a>
            <a href="<c:url value="/note/entries/recent"/>">Notes</a>
        </nav>
    </div>

    <!-------------------- Begin Body -------------------->
    <div id="body">
        <h3>Create a new note or view existing notes.</h3>

    </div>

    <!-------------------- Begin Footer -------------------->
    <div id="footer">
        <p id="footer-message">&copy; 2017 My Spring App. All rights reserved.</p>

    </div>

</div>
</body>
</html>