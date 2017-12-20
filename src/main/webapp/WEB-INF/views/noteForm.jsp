<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
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
        <h3>New Note</h3>
        <sf:form method="post" name="note-form" commandName="note">
            <sf:errors path="*" element="div" cssClass="error-messages"/>
            <table id="note-form-table">
                <tr>
                    <td class="note-form-left">
                        <sf:label path="title" cssErrorClass="error-field-label">Title:</sf:label>
                    </td>
                    <td class="note-form-right">
                        <sf:input path="title" cssErrorClass="error-field-input" size="50"/>
                    </td>
                </tr>
                <tr>
                    <td class="note-form-left">
                        <sf:label path="body" cssErrorClass="error-field-label">Body:</sf:label>
                    </td>
                    <td class="note-form-right">
                        <sf:textarea path="body" cssErrorClass="error-field-input" cols="80" rows="15"/>
                    </td>
                </tr>
                <tr>
                    <td class="note-form-left"></td>
                    <td class="note-form-right">
                        <input type="submit" value="Add">
                        &nbsp;
                        <input type="button" onClick="" value="Cancel">
                    </td>
                </tr>
            </table>
        </sf:form>
    </div>

    <!-------------------- Begin Footer -------------------->
    <div id="footer">
        <p>&copy; 2017 My Spring App. All rights reserved.</p>
    </div>

</div>
</body>
</html>
