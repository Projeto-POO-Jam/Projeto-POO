$(document).ready(function() {
    var $form = $("#loginForm");
    var $usernameInput = $("#username");
    var $passwordInput = $("#password");

    $form.on("submit", function(event) {
        event.preventDefault();

        apiRequest('POST', 'perform_login', {
                username: $.trim($usernameInput.val()),
                password: $passwordInput.val(),
        }, 'application/x-www-form-urlencoded')
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
