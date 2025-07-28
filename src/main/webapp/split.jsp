<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Base64" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Split PDF Result</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        body {
            font-family: 'Inter', sans-serif;
        }
        .image-preview {
            max-width: 100%;
            margin-bottom: 1rem;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            border: 1px solid #e5e7eb;
            border-radius: 8px;
        }
    </style>
</head>
<body class="bg-gray-100 text-gray-800">

<div class="max-w-4xl mx-auto py-10 px-4">
    <h1 class="text-2xl font-bold mb-6 text-center">Split PDF Result</h1>

    <div class="grid gap-6">
        <c:forEach var="imgBase64" items="${imgs}">
            <img src="data:image/png;base64,${imgBase64}" alt="Split Page"
                 class="image-preview" />
        </c:forEach>
    </div>

    <div class="text-center mt-10">
        <a href="${pageContext.request.contextPath}/home" class="text-blue-600 hover:underline">← Back to Home</a>
    </div>
</div>

</body>
</html>
