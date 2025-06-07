$(document).ready(function() {
    var $form = $("#signupForm");
    var $usernameInput = $("#username");
    var $emailInput = $("#email");
    var $passwordInput = $("#password");
    var $submitBtn = $("#submitButton");

    var $usernameError = $("#usernameError");
    var $emailError = $("#emailError");
    var $passwordError = $("#passwordError");

    var $avatarInput   = $('#avatarInput');
    var $avatarPreview = $('#avatarPreview');
    var $avatarError   = $('#avatarError');
    var defaultAvatarSrc = $avatarPreview.attr('src');

    var emailRegex= /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    var passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;

    $avatarInput.on('change', function() {
        var file = this.files[0];
        if (!file) {
            $avatarError.text('');
            $avatarPreview.attr('src', defaultAvatarSrc);
            return;
        }

        if (!file.type.match(/^image\//)) {
            $avatarError.text('Selecione um arquivo de imagem válido.');
            $avatarPreview.attr('src', defaultAvatarSrc);
            return;
        }
        $avatarError.text('');

        var reader = new FileReader();
        reader.onload = function(e) {
            // e.target.result é a data URL da imagem
            $avatarPreview.attr('src', e.target.result);
        };
        reader.readAsDataURL(file);
    });

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

        var formData = new FormData(this);
        formData.set('userNameLogin', $.trim($usernameInput.val()));
        formData.set('userEmail',    $.trim($emailInput.val()));
        formData.set('userPassword', $passwordInput.val());
        formData.set('userName',     'Nome Completo do Usuário');

        $submitBtn.prop("disabled", true);

        apiRequest('POST', 'api/users', formData)
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
