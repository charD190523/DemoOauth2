function login() {
    let email = document.getElementById("email").value;
    let password = document.getElementById("password").value;

    if (email === "" || password === "") {
        alert("Please enter email and password!");
        return;
    }

    alert(`Logging in as ${email}`);
}
