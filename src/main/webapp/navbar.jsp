<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Navigation Bar | PDF ToolKits</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="${pageContext.request.contextPath}/js/navbar.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/base.css" />
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet" />
    <style>
        body {
            font-family: 'Inter', sans-serif;
        }

        #mobileMenu {
            transform: translateX(100%);
            transition: transform 0.3s ease-in-out;
        }

        #mobileMenu.show {
            transform: translateX(0);
        }

        #overlay {
            opacity: 0;
            pointer-events: none;
            transition: opacity 0.3s ease-in-out;
        }

        #overlay.show {
            opacity: 0.5;
            pointer-events: auto;
        }

        .dropdown-container:hover .dropdown-menu {
            display: flex;
        }

        .dropdown-menu {
            display: none;
        }
    </style>
</head>

<body class="bg-gray-100 text-gray-800">
    <!-- Navbar -->
    <nav class="relative top-0 inset-x-0 bg-white shadow-md z-50">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
            <!-- Logo -->
            <a href="${pageContext.request.contextPath}/home" class="flex items-center gap-2">
                <img src="${pageContext.request.contextPath}/images/logo-removebg.png" alt="Logo" class="w-16 h-16 rounded" />
                <span class="text-xl font-bold text-gray-800">PDF ToolKits</span>
            </a>

            <!-- Desktop Menu -->
            <div class="hidden md:flex items-center gap-6 lg:gap-10 relative">
                <a href="${pageContext.request.contextPath}/home" class="text-sm font-medium hover:text-blue-600 transition">Home</a>

                <!-- Fixed Hover Dropdown -->
                <div class="relative dropdown-container">
                    <button class="text-sm font-medium hover:text-blue-600 transition py-4">
                        Tools
                    </button>

                    <div
                        class="dropdown-menu absolute flex-col top-full right-0 w-44 bg-white shadow-lg rounded-md overflow-hidden z-30 border border-gray-200">
                        <a href="${pageContext.request.contextPath}/merge-pdf" class="px-4 py-3 text-sm hover:bg-gray-100 border-b border-gray-100">Merge
                            PDF</a>
                        <a href="${pageContext.request.contextPath}/split-pdf" class="px-4 py-3 text-sm hover:bg-gray-100 border-b border-gray-100">Split
                            PDF</a>
                        <a href="${pageContext.request.contextPath}/compress-pdf"
                            class="px-4 py-3 text-sm hover:bg-gray-100 border-b border-gray-100">Compress PDF</a>
                        <a href="${pageContext.request.contextPath}/pdf-to-word" class="px-4 py-3 text-sm hover:bg-gray-100 border-b border-gray-100">PDF
                            to Word</a>
                        <a href="${pageContext.request.contextPath}/pdf-to-image" class="px-4 py-3 text-sm hover:bg-gray-100 border-b border-gray-100">PDF
                            to Image</a>
                        <a href="${pageContext.request.contextPath}/protect-pdf" class="px-4 py-3 text-sm hover:bg-gray-100">Protect PDF</a>
                    </div>
                </div>

                <a href="${pageContext.request.contextPath}/about" class="text-sm font-medium hover:text-blue-600 transition">About</a>
                <a href="${pageContext.request.contextPath}/contact" class="text-sm font-medium hover:text-blue-600 transition">Contact</a>

                <div class="relative">
                    <button id="profileButton"
                        class="w-9 h-9 rounded-full overflow-hidden border border-gray-300 hover:ring-2 ring-blue-500 transition">
                        <img src="${pageContext.request.contextPath}/images/default-user.jpg" alt="Profile"
                            class="w-full h-full object-cover rounded-full" />
                    </button>
                    <div id="profileMenu"
                        class="absolute right-0 mt-2 w-48 bg-white shadow-lg rounded-md border border-gray-200 hidden z-50">
                        <a href="${pageContext.request.contextPath}/profile"
                            class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition">Profile</a>
                        <a href="${pageContext.request.contextPath}/settings"
                            class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition">Settings</a>
                        <a href="${pageContext.request.contextPath}/logout" class="block px-4 py-2 text-sm text-red-700 hover:bg-red-100 transition">Log
                            out</a>
                    </div>
                </div>
            </div>

            <!-- Mobile Hamburger -->
            <button id="hamburger" class="md:hidden focus:outline-none">
                <svg class="w-6 h-6 text-gray-700" fill="none" stroke="currentColor" stroke-width="2"
                    viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
                </svg>
            </button>
        </div>
    </nav>

    <!-- Mobile Menu -->
    <div id="mobileMenu" class="fixed top-16 right-0 h-[calc(100vh-64px)] w-64 bg-white shadow-xl z-40 flex flex-col">
        <div class="flex-1 overflow-y-auto p-5 space-y-2">
            <a href="${pageContext.request.contextPath}/home" class="block px-3 py-2 font-medium rounded hover:bg-gray-100 transition">Home</a>

            <div>
                <button id="mobileToolsToggle"
                    class="w-full flex justify-between items-center px-3 py-2 rounded hover:bg-gray-100 transition">
                    <span class="font-medium text-gray-800">Tools</span>
                    <svg class="w-5 h-5 text-gray-600" id="mobileToolsArrow" fill="none" stroke="currentColor"
                        viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                    </svg>
                </button>
                <div id="mobileDropdownContent" class="hidden ml-4 mt-2 space-y-1">
                    <a href="${pageContext.request.contextPath}/merge-pdf" class="block px-3 py-1 text-sm rounded hover:bg-gray-100">Merge PDF</a>
                    <a href="${pageContext.request.contextPath}/split-pdf" class="block px-3 py-1 text-sm rounded hover:bg-gray-100">Split PDF</a>
                    <a href="${pageContext.request.contextPath}/compress-pdf" class="block px-3 py-1 text-sm rounded hover:bg-gray-100">Compress PDF</a>
                    <a href="${pageContext.request.contextPath}/pdf-to-word" class="block px-3 py-1 text-sm rounded hover:bg-gray-100">PDF to Word</a>
                    <a href="${pageContext.request.contextPath}/pdf-to-image" class="block px-3 py-1 text-sm rounded hover:bg-gray-100">PDF to Image</a>
                    <a href="${pageContext.request.contextPath}/protect-pdf" class="block px-3 py-1 text-sm rounded hover:bg-gray-100">Protect PDF</a>
                </div>
            </div>

            <a href="${pageContext.request.contextPath}/about" class="block px-3 py-2 font-medium rounded hover:bg-gray-100 transition">About</a>
            <a href="${pageContext.request.contextPath}/contact" class="block px-3 py-2 font-medium rounded hover:bg-gray-100 transition">Contact</a>
        </div>

        <div class="relative border-b p-2 border-gray-100 bg-gray-100 rounded-lg m-2">
            <a href="${pageContext.request.contextPath}/profile" class="flex items-center gap-3">
                <img src="${pageContext.request.contextPath}/images/default-user.jpg" alt="Profile"
                    class="w-9 h-9 rounded-full object-cover ring-2 ring-blue-200" />
                <div class="flex flex-col">
                    <span class="font-semibold text-gray-900 truncate">Minh Duc</span>
                    <span class="font-normal text-sm text-gray-500 truncate">Nguyen Viet Minh Duc</span>
                </div>
            </a>
            <button id="moreButtonMobileMenu" class="absolute top-2 right-2 w-5 h-5 ">
                <svg class="w-full h-full" fill="currentColor" viewBox="0 0 24 24">
                    <path
                        d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z" />
                </svg>
            </button>
            <div id="logoutMenuMobile"
                class="absolute -top-11 right-2 bg-white shadow-lg rounded-md border border-gray-200 w-36 hidden z-50">
                <a href="${pageContext.request.contextPath}/logout" class="block px-4 py-2 text-sm text-red-700 transition duration-150">
                    Log out
                </a>
            </div>
        </div>
    </div>

    <!-- Overlay for mobile -->
    <div id="overlay" class="fixed inset-0 bg-black z-30"></div>
</body>

</html>