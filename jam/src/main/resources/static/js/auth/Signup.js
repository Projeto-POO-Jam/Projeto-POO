import { apiRequest } from '../common/AjaxUtils.js';
import { showSuccess, showError } from '../common/notifications.js';
import { setupValidation, isFormValid } from '../common/validation.js';
import { attachImagePreview } from '../common/filePreview.js';

$(function(){
    const $form = $('#signupForm');
    //Preview de avatar
    attachImagePreview('#avatarInput', '#avatarPreview');

    //Validações
    const validationRules = {
        username: [
            {validate: value => value.trim() !== '',  message: 'Nome de usuário é obrigatório.'},
            {validate: value => !value.includes(' '), message: 'Não pode conter espaços.'}
        ],
        email: [
            { validate: value => value.trim() !== '', message: 'E-mail obrigatório.'},
            { validate: value => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value), message: 'Formato de e-mail inválido.'}
        ],
        password: [
            { validate: value => value.length >= 8, message: 'Mínimo 8 caracteres.' },
            { validate: value => /[A-Z]/.test(value), message: 'Pelo menos 1 letra maiúscula.' },
            { validate: value => /[a-z]/.test(value), message: 'Pelo menos 1 letra minúscula.' },
            { validate: value => /\d/.test(value), message: 'Pelo menos 1 número.' }
        ],
        passwordConfirm: [
            { validate: value => value === $('#password').val(), message: 'As senhas não coincidem.'}
        ]
    };

    setupValidation(validationRules);

    //formulario
    $form.on('submit', async e => {
        e.preventDefault();
        if (!isFormValid(validationRules)) return;

        const userData = {
            userName:  $('#username').val().trim(),
            userEmail: $('#email').val().trim(),
            userPassword: $('#password').val()
        };

        const formData  = new FormData();
        formData .append('user', new Blob([JSON.stringify(userData)], { type: 'application/json' }));

        const file = $('#avatarInput')[0].files[0];
        if (file) formData .append('userPhoto', file);

        apiRequest('POST', 'api/users', formData, false)
            .then(response => {
                showSuccess('Cadastro realizado com sucesso');
                setTimeout(() => window.location.href = '/login', 2000);
            })
            .catch(err => {
                if (err.status === 409 && Array.isArray(err.data.errors)) {
                    err.data.errors.forEach(code => {
                        if (code === 'USERNAME_EXISTS') $('#usernameError').text('Nome já em uso.');
                        if (code === 'EMAIL_EXISTS')    $('#emailError').text('E-mail já cadastrado.');
                    });
                } else {
                    showError('Erro ao cadastrar');
                }
            });
    });
});
