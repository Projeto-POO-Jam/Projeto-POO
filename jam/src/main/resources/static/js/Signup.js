$(document).ready(function() {
    var $form = $("#signupForm");
    var $usernameInput = $("#username");
    var $emailInput = $("#email");
    var $passwordInput = $("#password");
    var $submitBtn = $("#submitButton");

    var $usernameError = $("#usernameError");
    var $emailError = $("#emailError");
    var $passwordError = $("#passwordError");

    var emailRegex= /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    var passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;

    function validateField(fieldName) {
        if (fieldName === "username") {
            var val = $.trim($usernameInput.val());
            if (!val) {
                $usernameError.text("Nome de usuário é obrigatório.");
                return false;
            } else {
                $usernameError.text("");
                return true;
            }
        }

        if (fieldName === "email") {
            var val = $.trim($emailInput.val());
            if (!val) {
                $emailError.text("O e-mail é obrigatório.");
                return false;
            } else if (!emailRegex.test(val)) {
                $emailError.text("Formato de e-mail inválido.");
                return false;
            } else {
                $emailError.text("");
                return true;
            }
        }

        if (fieldName === "password") {
            var val = $.trim($passwordInput.val());
            if (!val) {
                $passwordError.text("A senha é obrigatória.");
                return false;
            } else if (!passwordRegex.test(val)) {
                $passwordError.text(
                    "Senha deve ter no mínimo 8 caracteres, conter ao menos uma letra maiúscula, uma minúscula e um número."
                );
                return false;
            } else {
                $passwordError.text("");
                return true;
            }
        }
    }

    $usernameInput.on("blur", function() { validateField("username"); });
    $emailInput.on("blur", function() { validateField("email"); });
    $passwordInput.on("blur", function() { validateField("password"); });

    $form.on("submit", function(event) {
        event.preventDefault();

        var validUsername = validateField("username");
        var validEmail = validateField("email");
        var validPassword = validateField("password");

        if (!validUsername || !validEmail || !validPassword) {
            return;
        }

        $submitBtn.prop("disabled", true);

        apiRequest('POST', 'api/users', {
            userNameLogin: $.trim($usernameInput.val()),
            userEmail: $.trim($emailInput.val()),
            userPassword: $passwordInput.val(),
            userName: "Nome Completo do Usuário"
        })
            .then(function(resultado) {
                console.log('Cadastro bem-sucedido:', resultado.data);

                const Toast = Swal.mixin({
                    toast: true,
                    position: "top-end",
                    showConfirmButton: false,
                    timer: 2000,
                    timerProgressBar: true,
                    didOpen: (toast) => {
                        toast.onmouseenter = Swal.stopTimer;
                        toast.onmouseleave = Swal.resumeTimer;
                    }
                });
                Toast.fire({
                    icon: "success",
                    title: "Cadastro realizado com Sucesso"
                });

                setTimeout(function() {
                    window.location.href = '/login';
                }, 2000);
            })
            .catch(function(err) {
                console.error('Erro ao chamar /signup:', err);
                $emailError.text("Erro ao cadastrar. Tente novamente.");
                $submitBtn.prop("disabled", false);
            });
    });
});
