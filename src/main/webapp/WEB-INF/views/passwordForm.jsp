<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Change Password</title>
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
            <ul>
                <li><a href="<c:url value="/"/>">Home</a></li>
                <li><a href="<c:url value="/notes/view/entries"/>">Notes</a></li>
                <li>
                    <a class="active" href="<c:url value="#"/>">Account</a>
                    <ul>
                        <li><a href="<c:url value="#"/>">Log Out</a></li>
                        <li><a href="<c:url value="#"/>">Settings</a></li>
                    </ul>
                </li>
            </ul>
        </nav>
    </div>

    <!-------------------- Begin Body -------------------->
    <div id="body">
        <h3>Change Password</h3>
        <div class="form-container">
            <sf:form method="post" name="password-form" commandName="user">
                <sf:errors path="*" element="div" cssClass="error-message"/>
                <table class="form-table">
                    <tr>
                        <td class="table-left">
                            <sf:label path="password" cssErrorClass="error-field-label">Current Password:</sf:label>
                        </td>
                        <td class="table-right">
                            <sf:input path="password" cssErrorClass="error-field-input" size="30"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="table-left">
                            <sf:label path="newPassword" cssErrorClass="error-field-label">New Password</sf:label>
                        </td>
                        <td class="table-right">
                            <sf:input path="newPassword" cssErrorClass="error-field-input" size="30"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="table-left">
                            <sf:label path="confirmNewPassword" cssErrorClass="error-field-label">Confirm New Password</sf:label>
                        </td>
                        <td class="table-right">
                            <sf:input path="confirmNewPassword" cssErrorClass="error-field-input" size="30"/>
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