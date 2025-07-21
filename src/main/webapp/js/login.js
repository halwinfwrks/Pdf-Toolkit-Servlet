document.addEventListener("DOMContentLoaded", () => {
  const backButton = document.getElementById("back-btn");
  const errorMessageSpan = document.getElementById("error-message");
  const submitButton = document.getElementById("submit-btn");
  const form = document.querySelector("form");

  // Quay lại trang trước
  backButton?.addEventListener("click", (e) => {
    e.preventDefault();
    history.back();
  });

  // Xử lý khi submit
  form?.addEventListener("submit", () => {
    // Không preventDefault để form gửi bình thường
    if (submitButton) {
      submitButton.disabled = true;
    submitButton.textContent = "Processing...";
      submitButton.classList.add("opacity-60", "cursor-not-allowed");
    }

    // Hiện loading indicator nếu có
    const loadingIndicator = document.getElementById("loading-indicator");
    loadingIndicator?.classList.remove("hidden");
  });
});
