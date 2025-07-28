document.addEventListener("DOMContentLoaded", function () {
  // Mobile menu functionality
  const hamburger = document.getElementById("hamburger");
  const mobileMenu = document.getElementById("mobileMenu");
  const overlay = document.getElementById("overlay");
  const mobileToolsToggle = document.getElementById("mobileToolsToggle");
  const mobileDropdownContent = document.getElementById(
    "mobileDropdownContent"
  );
  const mobileToolsArrow = document.getElementById("mobileToolsArrow");

  hamburger.addEventListener("click", () => {
    mobileMenu.classList.toggle("show");
    overlay.classList.toggle("show");
  });

  overlay.addEventListener("click", () => {
    mobileMenu.classList.remove("show");
    overlay.classList.remove("show");
  });

  mobileToolsToggle.addEventListener("click", () => {
    mobileDropdownContent.classList.toggle("hidden");
    mobileToolsArrow.style.transform = mobileDropdownContent.classList.contains(
      "hidden"
    )
      ? "rotate(0deg)"
      : "rotate(180deg)";
  });

  // Profile menu functionality
  const profileButton = document.getElementById("profileButton");
  const profileMenu = document.getElementById("profileMenu");

  profileButton.addEventListener("click", (e) => {
    e.stopPropagation();
    profileMenu.classList.toggle("hidden");
  });

  document.addEventListener("click", () => {
    profileMenu.classList.add("hidden");
  });

  // Mobile profile menu
  const moreButtonMobileMenu = document.getElementById("moreButtonMobileMenu");
  const logoutMenuMobile = document.getElementById("logoutMenuMobile");

  moreButtonMobileMenu.addEventListener("click", (e) => {
    e.stopPropagation();
    logoutMenuMobile.classList.toggle("hidden");
  });

  document.addEventListener("click", () => {
    logoutMenuMobile.classList.add("hidden");
  });
});
