document.addEventListener("DOMContentLoaded", () => {
  const backButton = document.getElementById("back-btn");
  const submitButton = document.getElementById("submit-btn");
  const form = document.querySelector("form");
  const usernameInput = document.getElementById("username");
  const usernameError = document.getElementById("username-error");
  const loadingIndicator = document.getElementById("loading-indicator");

  const passwordInput = document.getElementById("password");
  const confirmPasswordInput = document.getElementById("confirmPassword");
  const passwordError = document.getElementById("password-error");
  const confirmPasswordError = document.getElementById(
    "confirm-password-error"
  );

  // Go back
  backButton?.addEventListener("click", (e) => {
    e.preventDefault();
    history.back();
  });

  function showError(message) {
    usernameInput.classList.add(
      "border-red-500",
      "focus:border-red-500",
      "focus:ring-red-500"
    );
    usernameError.textContent = message;
    usernameError.classList.remove("hidden");
    submitButton.disabled = true;
  }

  function clearError() {
    usernameInput.classList.remove(
      "border-red-500",
      "focus:border-red-500",
      "focus:ring-red-500"
    );
    usernameError.textContent = "";
    usernameError.classList.add("hidden");
    submitButton.disabled = false;
  }

  usernameInput.addEventListener("input", () => {
    const input = usernameInput.value.trim();

    if (input.length < 6) {
      showError("Username must be at least 6 characters long.");
    } else if (usernames.includes(input)) {
      showError("Username already exists.");
    } else {
      clearError();
    }
  });

  form?.addEventListener("submit", (e) => {
    const input = usernameInput.value.trim();

    if (input.length < 6) {
      showError("Username must be at least 6 characters long.");
      e.preventDefault();
      return;
    }

    if (usernames.includes(input)) {
      showError("Username already exists.");
      e.preventDefault();
      return;
    }

    clearError();
    submitButton.disabled = true;
    submitButton.textContent = "Processing...";
    submitButton.classList.add("opacity-60", "cursor-not-allowed");
    loadingIndicator?.classList.remove("hidden");
  });
  function showPasswordError(inputElement, errorElement, message) {
    inputElement.classList.add(
      "border-red-500",
      "focus:border-red-500",
      "focus:ring-red-500"
    );
    errorElement.textContent = message;
    errorElement.classList.remove("hidden");
    submitButton.disabled = true;
  }

  function clearPasswordError(inputElement, errorElement) {
    inputElement.classList.remove(
      "border-red-500",
      "focus:border-red-500",
      "focus:ring-red-500"
    );
    errorElement.textContent = "";
    errorElement.classList.add("hidden");
    submitButton.disabled = false;
  }

  // Password input validation
  passwordInput.addEventListener("input", () => {
    const password = passwordInput.value.trim();

    if (password.length < 6) {
      showPasswordError(
        passwordInput,
        passwordError,
        "Password must be at least 6 characters long."
      );
    } else {
      clearPasswordError(passwordInput, passwordError);
    }

    // Check if confirmPassword matches
    const confirm = confirmPasswordInput.value.trim();
    if (confirm && password !== confirm) {
      showPasswordError(
        confirmPasswordInput,
        confirmPasswordError,
        "Passwords do not match."
      );
    } else if (confirm) {
      clearPasswordError(confirmPasswordInput, confirmPasswordError);
    }
  });

  // Confirm password input validation
  confirmPasswordInput.addEventListener("input", () => {
    const password = passwordInput.value.trim();
    const confirm = confirmPasswordInput.value.trim();

    if (password !== confirm) {
      showPasswordError(
        confirmPasswordInput,
        confirmPasswordError,
        "Passwords do not match."
      );
    } else {
      clearPasswordError(confirmPasswordInput, confirmPasswordError);
    }
  });
});
