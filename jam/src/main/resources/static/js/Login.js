$(document).ready(function() {
    var form = $("#loginForm");
    var loginInput = $("#username");
    var passwordInput = $("#password");
    var emailError = $("#emailError");
    var passwordError = $("#passwordError");

    form.on("submit", function(event) {
        event.preventDefault();

        loginInput.removeClass("error");
        passwordInput.removeClass("error");
        emailError.text("");
        passwordError.text("");

        const formData = new FormData(this);
        formData.set('username', $.trim(loginInput.val()));
        formData.set('password', passwordInput.val());

        apiRequest('POST', 'perform_login', formData, 'application/x-www-form-urlencoded')
            .then(function(resultado) {
                window.location.href = '/home';
            })
            .catch(function(err) {
                loginInput.addClass("error");
                passwordInput.addClass("error");

                const Toast = Swal.mixin({
                    toast: true,
                    position: "top-end",
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true,
                    didOpen: (toast) => {
                        toast.onmouseenter = Swal.stopTimer;
                        toast.onmouseleave = Swal.resumeTimer;
                    }
                });
                Toast.fire({
                    icon: "error",
                    title: "Credenciais incorretas!"
                });
            });
    });

});
