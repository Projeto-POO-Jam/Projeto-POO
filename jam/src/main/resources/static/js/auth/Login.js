import { apiRequest } from '../common/AjaxUtils.js';
import { showError } from '../common/notifications.js';
import { setupValidation, isFormValid } from '../common/validation.js';

$(function() {
    const form = $('#loginForm');

    //Validação
    const validationRules = {
        username: [
            { validate: value => value.trim() !== '', message: 'E-mail ou usuário obrigatório.' }
        ],
        password: [
            { validate: value => value.trim() !== '', message: 'Senha obrigatória.' }
        ]
    };
    setupValidation(validationRules);

    //formulario
    form.on('submit', function(e) {
        e.preventDefault();
        if (!isFormValid(validationRules)) return;

        const formData = new FormData(this);
        formData.set('username', $.trim($('#username').val()));
        formData.set('password', $('#password').val());

        apiRequest('POST', 'perform_login', formData, 'application/x-www-form-urlencoded')
            .then(() => {
                window.location.href = '/home';
            })
            .catch(() => {
                $('#username, #password').addClass('error');
                showError('Credenciais incorretas!');
            });
    });
});
