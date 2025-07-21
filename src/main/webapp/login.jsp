<!-- <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> -->
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login | PDF ToolKits</title>

    <!-- Tailwind CSS CDN -->
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/base.css">
    <script src="${pageContext.request.contextPath}/js/login.js" defer></script>
</head>

<body class="w-full min-h-screen flex bg-gray-100 font-sans">
    <!-- Left panel -->
    <div class="w-1/2 hidden lg:flex relative bg-cover bg-center"
        style="background-image: url('${pageContext.request.contextPath}/images/bg-login.jpg');">

        <!-- Fixed Logo -->
        <a href="${pageContext.request.contextPath}/home" class="absolute bottom-6 left-6 flex items-center z-10">
            <img src="${pageContext.request.contextPath}/images/logo-1-removebg.png" alt="Logo"
                class="w-8 h-8 drop-shadow-lg">
            <span class="text-gray-200 font-bold text-xl ml-2 mt-1">PDF ToolKits</span>
        </a>

        <button id="back-btn" class="absolute top-6 left-6 flex items-center z-10">
            <img src="${pageContext.request.contextPath}/images/back-icon.png" alt="Back"
                class="w-7 h-7 drop-shadow-lg">
            <span class="text-gray-200 font-bold text-xl ml-2 mt-1.5">Home</span>
        </button>

        <div class="absolute inset-0 bg-black bg-opacity-40"></div>
        <div class="relative z-10 max-w-lg ml-16 mt-[30%] mb-auto text-white">
            <p class="text-lg text-gray-200 leading-relaxed">
                Tackle PDFs the smart way to merge, split, compress, convert, and protect your files effortlessly and
                efficiently.
            </p>
            <a href="#" class="text-gray-200 underline hover:text-gray-300 transition-colors duration-200 font-medium">
                Learn More
            </a>
        </div>
    </div>

    <!-- Right panel: Login form -->
    <div class="relative w-full lg:w-1/2 bg-gray-50 flex flex-col justify-center items-center px-8 py-12 shadow-lg">

        <h2 class="text-3xl font-bold text-gray-800 mb-2">Welcome Back!</h2>
        <p class="text-sm text-gray-500 mb-6">Log in to continue using PDF ToolKits</p>

        <form action="${pageContext.request.contextPath}/login" method="post" class="w-full max-w-md">
            <div class="mb-4">
                <label for="username" class="block text-sm font-medium text-gray-700">Username</label>
                <input type="text" id="username" name="username" required
                    class="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-2 focus:ring-blue-500 focus:outline-none focus:border-blue-500 transition">
            </div>
            <div class="mb-6">
                <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
                <input type="password" id="password" name="password" required
                    class="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-2 focus:ring-blue-500 focus:outline-none focus:border-blue-500 transition">
            </div>
            <c:if test="${not empty error}">
                <div id="error-message"
                    class="bg-red-100 border border-red-400 text-red-700 px-4 py-2 rounded mb-4 w-full max-w-md text-center">
                    <c:out value="${error}" />
                </div>
            </c:if>

            <button type="submit" id="submit-btn"
                class="w-full bg-white text-gray-800 border border-gray-400 hover:bg-gray-800 hover:text-white font-semibold py-2 px-4 rounded-md shadow-sm transition duration-300 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-600">
                Log In
            </button>
        </form>

        <p class="mt-4 text-sm text-gray-600">
            Don't have an account?
            <a href="${pageContext.request.contextPath}/register"
                class="text-[#D39D55] hover:underline font-medium">
                Register now
            </a>
        </p>
    </div>
</body>

</html>