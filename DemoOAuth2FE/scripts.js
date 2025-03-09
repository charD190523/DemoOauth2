document.addEventListener("DOMContentLoaded", function () {
    checkOAuth2Success();
});

document.addEventListener("DOMContentLoaded", function () {
    checkOAuth2Success();
});

function checkOAuth2Success() {
    const urlParams = new URLSearchParams(window.location.search);
    const jwt = urlParams.get("jwt"); // Lấy JWT từ URL nếu có
    const accessToken = urlParams.get("accessToken"); // Lấy access token từ URL nếu có

    if (accessToken) {
        console.log("🔹 Access Token received:", accessToken);
        localStorage.setItem("accessToken", accessToken);
        window.history.replaceState({}, document.title, "home.html");
        fetchGoogleUserInfo();
    }

    if (jwt) {
        console.log("🔹 JWT received from URL:", jwt);
        localStorage.setItem("jwt", jwt); // Lưu JWT vào localStorage
        window.history.replaceState({}, document.title, "home.html"); // Xóa query params khỏi URL
        fetchUserInfo(); // Gọi API lấy thông tin người dùng
    } else {
        const storedToken = localStorage.getItem("jwt");

        if (storedToken) {
            console.log("🔹 JWT found in localStorage:", storedToken);
            if (window.location.pathname.includes("login.html")) {
                window.location.href = "http://127.0.0.1:5500/home.html"; // Chuyển hướng về home nếu đã đăng nhập
            } else {
                fetchUserInfo(); // Nếu ở home.html thì lấy thông tin user
            }
        } else {
            console.warn("⚠ No JWT found.");
            if (!window.location.pathname.includes("login.html")) {
                window.location.href = "http://127.0.0.1:5500/login.html"; // Chuyển hướng về login nếu không có JWT
            }
        }
    }
}


function fetchUserInfo() {
    const token = localStorage.getItem("jwt"); // Lấy token từ localStorage

    if (!token) {
        console.log("❌ No token found, staying on login page.");
        return; // Không redirect, giữ nguyên trang hiện tại
    }

    console.log("🔹 Fetching user info with token:", token);
    fetch("http://localhost:8081/api/user", {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        },
        credentials: "include"
    })
    .then(response => {
        console.log("🔹 Response status:", response.status);
        if (response.status === 401) {
            console.warn("⚠ Unauthorized! Redirecting to login.");
            localStorage.removeItem("jwt");
            window.location.href = "http://127.0.0.1:5500/login.html";
            return null;
        }
        if (!response.ok) {
            throw new Error("Failed to fetch user info: " + response.status);
        }
        return response.json();
    })
    .then(data => {
        if (!data || !data.name) {
            console.warn("⚠ Invalid user data received, possible error:", data);
            return;
        }

        console.log("✅ User data received:", data);
        document.getElementById("user-name").textContent = data.name || "Unknown";
        document.getElementById("user-email").textContent = data.email || "No email";
        document.getElementById("user-pic").src = data.picture || "";
    })
    .catch(error => {
        console.error("❌ Error fetching user info:", error);
    });
}

function fetchGoogleUserInfo() {
    const accessToken = localStorage.getItem("accessToken");
    if (!accessToken) {
        console.warn("⚠ No Access Token found.");
        return;
    }

    fetch("https://people.googleapis.com/v1/people/me?personFields=birthdays,genders", {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + accessToken
        }
    })
    .then(response => {
        console.log("🔹 Google API Response Status:", response.status);
        if (!response.ok) {
            throw new Error("Failed to fetch Google user info: " + response.status);
        }
        return response.json();
    })
    .then(data => {
        console.log("✅ Google User Info:", data);
        
        // Lấy ngày sinh từ response
        const birthdays = data.birthdays;
        let birthdayText = "Unknown";
        
        if (birthdays && birthdays.length > 0) {
            const birthday = birthdays[0].date;
            birthdayText = `${birthday.day}/${birthday.month}/${birthday.year || "N/A"}`; // Năm có thể bị ẩn
        }

        // Hiển thị ngày sinh lên UI
        document.getElementById("user-birthday").textContent = birthdayText;
    })
    .catch(error => console.error("❌ Error fetching Google user info:", error));
}



function loginWithGoogle() {
    console.log("🔹 Redirecting to Google login...");
    window.location.href = "http://localhost:8081/oauth2/authorization/google";
}

function logout() {
    console.log("🔹 Logging out...");
    localStorage.removeItem("jwt");
    window.location.href = "http://127.0.0.1:5500/login.html";
}
