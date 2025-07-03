import { apiRequest } from '../common/api.js';
import { showSuccess, showError } from '../common/notifications.js';
import { setupValidation, isFormValid } from '../common/validation.js';

$(function() {
    //Regras de validação
    const validationRules = {
        currentPassword: [
            { validate: value => value.trim() !== '', message: 'A senha atual é obrigatória.' }
        ],
        newPassword: [
            { validate: value => value.length >= 8, message: 'A nova senha deve ter no mínimo 8 caracteres.' },
            { validate: value => /[A-Z]/.test(value), message: 'Deve conter pelo menos uma letra maiúscula.' },
            { validate: value => /[a-z]/.test(value), message: 'Deve conter pelo menos uma letra minúscula.' },
            { validate: value => /\d/.test(value), message: 'Deve conter pelo menos um número.' }
        ],
        confirmNewPassword: [
            { validate: value => value === $('#newPassword').val(), message: 'As senhas não coincidem.' }
        ]
    };

    //Configura a validação dos campos
    setupValidation(validationRules);

    //Evento de submit do formulário
    $('#loginForm').on('submit', async function(e) {
        e.preventDefault();

        //Verifica se o formulário é válido
        if (!isFormValid(validationRules)) {
            showError('Por favor, corrija os erros no formulário.');
            return;
        }

        const submitButton = $('#submitButton');
        submitButton.prop('disabled', true).text('Alterando...');


        const formData = new FormData(this);
        formData.set('userOldPassword', $.trim($('#currentPassword').val()));
        formData.set('userNewPassword', $('#newPassword').val());


        try {
            //Faz a requisição para a API
            await apiRequest('PUT', 'api/users/changePassword', formData);
            showSuccess('Senha alterada com sucesso!');

            //Redireciona para a home após 2 segundos
            setTimeout(() => {
                window.location.href = '/home';
            }, 2000);

        } catch (err) {
            //Trata o erro de senha atual incorreta
            if (err.status === 400) {
                showError('A senha atual está incorreta.');
            } else {
                showError('Ocorreu um erro ao tentar alterar a senha.');
            }
            submitButton.prop('disabled', false).text('Alterar senha');
        }
    });
});