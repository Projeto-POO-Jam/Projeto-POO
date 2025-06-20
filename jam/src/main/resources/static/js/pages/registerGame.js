import { apiRequest } from '../common/api.js';
import { showSuccess, showError } from '../common/notifications.js';
import { setupValidation, isFormValid } from '../common/validation.js';

$(function() {
    //Obter o ID da Jam a partir da URL
    const urlParams = new URLSearchParams(window.location.search);
    const jamId = urlParams.get('jamId');

    // Se não houver um jamId, o formulário não pode ser enviado.
    if (!jamId) {
        //manda ele para 404
    }

    //Inicializa editor WYSIWYG(Summernote)
    $('#content').summernote({
        height: 300,
        codemirror: { theme: 'default' }
    });

    const form = $('#createGameForm');
    const submitButton = $('#submitButton');

    //Definir as regras de validação para o formulário do jogo
    const validationRules = {
        gameTitle: [
            { validate: value => value.trim() !== '', message: 'O nome do jogo é obrigatório.' }
        ],
        gameDescription: [
            { validate: value => value.trim() !== '', message: 'A descrição do jogo é obrigatória.' }
        ],
        gameFile: [
            {
                validate: (value) => {
                    const fileInput = $('#gameFile')[0];
                    return fileInput && fileInput.files.length > 0;
                },
                message: 'O envio do arquivo do jogo é obrigatório.'
            }
        ]
    };

    setupValidation(validationRules);

    //Configurar o evento de submissão do formulário
    form.on('submit', async e => {
        e.preventDefault();
        if (!isFormValid(validationRules)) {
            showError('Por favor, corrija os campos inválidos.');
            return;
        }

        submitButton.prop('disabled', true).text('Enviando...');

        //Montar o FormData com os dados do formulário
        const formData = new FormData(form[0]);

        formData.set('gameTitle', $('#gameTitle').val());
        formData.set('gameDescription', $('#gameDescription').val());

        const gamePhoto = $('#gamePhoto')[0].files[0];
        if (gamePhoto) formData.set('gamePhoto', gamePhoto);

        const gameFile = $('#gameFile')[0].files[0];
        if (gameFile) formData.set('gameFile', gameFile);

        formData.set('jamId', jamId);
        try {
            await apiRequest('POST', 'api/games', formData);
            showSuccess('Jogo postado com sucesso!');

            // Redireciona o usuário
            setTimeout(() => window.location.href = `/jams/${jamId}`, 2000);

        } catch(err) {
            showError('Ocorreu um erro ao postar o jogo. Tente novamente.');
            submitButton.prop('disabled', false).text('Postar Game');
        }
    });
});