$(document).ready(function() {
    var $form = $("#loginForm");
    var $emailInput = $("#email");
    var $passwordInput = $("#password");
    var $emailError = $("#emailError");

    var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    function validateField(fieldName) {
        if (fieldName === "email") {
            var emailValue = $.trim($emailInput.val());
            if (!emailValue) {
                $emailError.text("O e-mail é obrigatório.");
                return false;
            } else if (!emailRegex.test(emailValue)) {
                $emailError.text("Formato de e-mail inválido.");
                return false;
            } else {
                $emailError.text("");
                return true;
            }
        }

    }

    $emailInput.on("blur", function() {
        validateField("email");
    });

    $form.on("submit", function(event) {
        var isEmailValid = validateField("email");

        if (!isEmailValid) {
            event.preventDefault();
            return;
        }

        event.preventDefault();

        apiRequest('GET', 'login')
            .then(function(resultado) {
                console.log('Usuário logado:', resultado.data);
                window.location.href = '/home';
            })
            .catch(function(err) {
                console.error('Não foi possível obter /login:', err.status, err.error);
                alert('Falha ao fazer login.');
            });
    });
});
