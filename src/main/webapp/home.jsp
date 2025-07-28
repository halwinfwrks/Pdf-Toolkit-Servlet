<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        Home - PDF Toolkit
    </title>
</head>

<body>
    <!-- import navbar -->
    <jsp:include page="navbar.jsp" />
    <c:if test="${not empty jwtToken}">
        <div class="error-message">
            <p>${jwtToken}</p>
        </div>
    </c:if>
        <h1>Welcome to the PDF Toolkit</h1>
</body>

</html>