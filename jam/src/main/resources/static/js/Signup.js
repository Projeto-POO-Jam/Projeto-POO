$(document).ready(function() {
    var form = $("#signupForm");
    var usernameInput = $("#username");
    var emailInput = $("#email");
    var passwordInput = $("#password");
    var passwordConfirmInput = $("#passwordConfirm");
    var submitBtn = $("#submitButton");

    var usernameError = $("#usernameError");
    var emailError = $("#emailError");
    var passwordError = $("#passwordError");
    var passwordConfirmError = $("#passwordConfirmError");

    var avatarInput = $('#avatarInput');
    var avatarPreview = $('#avatarPreview');
    var avatarError = $('#avatarError');
    var defaultAvatarSrc = avatarPreview.attr('src');

    var emailRegex= /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    var passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;

    avatarInput.on('change', function() {
        var file = this.files[0];
        if (!file) {
            avatarError.text('');
            avatarPreview.attr('src', defaultAvatarSrc);
            return;
        }

        if (!file.type.match(/^image\//)) {
            avatarError.text('Selecione um arquivo de imagem válido.');
            avatarPreview.attr('src', defaultAvatarSrc);
            return;
        }
        avatarError.text('');

        const reader = new FileReader();
        reader.onload = function(e) {
            avatarPreview.attr('src', e.target.result);
        };
        reader.readAsDataURL(file);
    });

    function validateField(fieldName) {
        if (fieldName === "username") {
            const val = $.trim(usernameInput.val());
            if (!val) {
                usernameError.text("Nome de usuário é obrigatório.");
                usernameInput.addClass("error");
                return false;
            } else if (/\s/.test(val)) {
                usernameError.text("O nome de usuário não pode conter espaços.");
                usernameInput.addClass("error");
                return false;
            } else {
                usernameError.text("");
                usernameInput.removeClass("error");
                return true;
            }
        }

        if (fieldName === "email") {
            const val = $.trim(emailInput.val());
            if (!val) {
                emailError.text("O e-mail é obrigatório.");
                return false;
            } else if (!emailRegex.test(val)) {
                emailError.text("Formato de e-mail inválido.");
                emailInput.addClass("error");
                return false;
            } else {
                emailError.text("");
                emailInput.removeClass("error");
                return true;
            }
        }

        if (fieldName === "password") {
            const val = $.trim(passwordInput.val());
            if (!val) {
                passwordError.text("A senha é obrigatória.");
                return false;
            } else if (!passwordRegex.test(val)) {
                passwordError.text(
                    "Senha deve ter no mínimo 8 caracteres, conter ao menos uma letra maiúscula, uma minúscula e um número."
                );
                passwordInput.addClass("error");
                return false;
            } else {
                passwordError.text("");
                passwordInput.removeClass("error");
                return true;
            }
        }

        if (fieldName === "passwordConfirm") {
            const pass = $.trim(passwordInput.val());
            const confirm = $.trim(passwordConfirmInput.val());
            if (!confirm) {
                passwordConfirmError.text("A confirmação de senha é obrigatória.");
                return false;
            } else if (pass !== confirm) {
                passwordConfirmError.text("As senhas não coincidem.");
                return false;
            } else {
                passwordConfirmError.text("");
                return true;
            }
        }
    }

    usernameInput.on("blur", function() { validateField("username"); });
    emailInput.on("blur", function() { validateField("email"); });
    passwordInput.on("blur", function() { validateField("password"); });
    passwordConfirmInput.on("blur", function() { validateField("passwordConfirm"); });

    form.on("submit", function(event) {
        event.preventDefault();

        const validUsername = validateField("username");
        const validEmail = validateField("email");
        const validPassword = validateField("password");
        const validPasswordConfirm = validateField("passwordConfirm");

        if (!validUsername || !validEmail || !validPassword || !validPasswordConfirm) {
            return;
        }

        const userData = {
            userName: $.trim(usernameInput.val()),
            userEmail: $.trim(emailInput.val()),
            userPassword: passwordInput.val(),
        };

        const formData = new FormData();
        formData.append('user', new Blob([JSON.stringify(userData)], {
            type: 'application/json'
        }));

        const avatarFile = avatarInput[0].files[0];
        if (avatarFile) {
            formData.append('userPhoto', avatarFile);
        }

        submitBtn.prop("disabled", true);

        apiRequest('POST', 'api/users', formData)
            .then(function(resultado) {
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
                submitBtn.prop("disabled", false);

                usernameError.text('');
                emailError.text('');

                if (err.status === 409 && err.data && Array.isArray(err.data.errors)) {
                    err.data.errors.forEach(code => {
                        switch (code) {
                            case 'USERNAME_EXISTS':
                                usernameError.text('Esse nome de usuário já está em uso.');
                                break;
                            case 'EMAIL_EXISTS':
                                emailError.text('Esse e-mail já está cadastrado.');
                                break;
                        }
                    });
                } else {
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
                        icon: "error",
                        title: "Erro ao cadastrar"
                    });
                }
            });
    });
});
