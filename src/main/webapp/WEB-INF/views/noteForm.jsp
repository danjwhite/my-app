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
            <img id="banner-img" src="${pageContext.request.contextPath}/resources/images/spring-by-pivotal.png"
                 alt=""/>
            <h1>My Spring App</h1>
        </div>
        <nav id="header-nav">
            <a href="<c:url value="/"/>">Home</a>
            <a class="active" href="<c:url value="/notes/view/entries"/>">Notes</a>
        </nav>
    </div>

    <!-------------------- Begin Body -------------------->
    <div id="body">
        <c:choose>
            <c:when test="${formType == 'add'}">
                <h3>New Note</h3>
            </c:when>
            <c:when test="${formType == 'edit'}">
                <h3>Edit Note</h3>
            </c:when>
        </c:choose>
        <div class="form-container">
            <sf:form method="post" name="note-form" commandName="note">
                <sf:hidden path="id"/>
                <sf:errors path="*" element="div" cssClass="error-message"/>
                <table class="form-table">
                    <tr>
                        <td class="table-left">
                            <sf:label path="title" cssErrorClass="error-field-label">Title:</sf:label>
                        </td>
                        <td class="table-right">
                            <c:choose>
                                <c:when test="${formType == 'add'}">
                                    <sf:input path="title" cssErrorClass="error-field-input" size="50"/>
                                </c:when>
                                <c:when test="${formType == 'edit'}">
                                    <sf:input path="title" cssErrorClass="error-field-input" size="50" value="${note.title}"/>
                                </c:when>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td class="table-left">
                            <sf:label path="body" cssErrorClass="error-field-label">Body:</sf:label>
                        </td>
                        <td class="table-right">
                            <c:choose>
                                <c:when test="${formType == 'add'}">
                                    <sf:textarea path="body" cssErrorClass="error-field-input" cols="80" rows="15"/>
                                </c:when>
                                <c:when test="${formType == 'edit'}">
                                    <sf:textarea path="body" cssErrorClass="error-field-input" cols="80" rows="15" value="${note.body}"/>
                                </c:when>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td class="table-left"></td>
                        <td class="table-right form-buttons">
                            <input type="submit" value="Save">
                            &nbsp;
                            <input type="button" onClick="history.back()" value="Cancel">
                        </td>
                    </tr>
                </table>
            </sf:form>
        </div>
    </div>

    <!-------------------- Begin Footer -------------------->
    <div id="footer">
        <p id="footer-message">&copy; 2017 My Spring App. All rights reserved.</p>
    </div>

</div>
</body>
</html>
