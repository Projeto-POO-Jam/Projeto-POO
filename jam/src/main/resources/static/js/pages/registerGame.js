import { apiRequest } from '../common/api.js';
import { showSuccess, showError } from '../common/notifications.js';
import { setupValidation, isFormValid } from '../common/validation.js';
import { registerFilePondPlugins, createFilePondInstance } from '../common/filepond-helper.js';

$(function() {
    //Obter o ID da Jam a partir da URL
    const pathParts = window.location.pathname.split('/');
    const jamId = pathParts[pathParts.length - 1];

    if (!jamId) {
        showError('ID da Jam não encontrado. Redirecionando...');
        setTimeout(() => window.location.href = '/404', 2000); // Exemplo de redirecionamento
        return;
    }

    //Inicializa editor WYSIWYG(Summernote)
    $('#gameContent').summernote({
        height: 300,
        codemirror: { theme: 'default' }
    });

    //FilePond
    registerFilePondPlugins();

    const pondInstances = {};

    pondInstances.gamePhoto = createFilePondInstance('#gamePhoto', {
        labelIdle: `Arraste a <strong>imagem de capa</strong> ou <span class="filepond--label-action">Procure</span>`,
    });

    pondInstances.gameFile = createFilePondInstance('#gameFile', {
        labelIdle: `Arraste o <strong>arquivo do jogo (.rar)</strong> ou <span class="filepond--label-action">Procure</span>`,
        stylePanelAspectRatio: '0.1',
        acceptedFileTypes: null
    });

    const gameFilePond = pondInstances.gameFile;

    if (gameFilePond) {
        gameFilePond.on('addfile', (error, file) => {
            const rootElement = gameFilePond.element;
            rootElement.style.height = 'auto';
        });

        gameFilePond.on('removefile', (error, file) => {
            if (gameFilePond.getFiles().length === 0) {
                const rootElement = gameFilePond.element;
                rootElement.style.height = '';
            }
        });
    }

    //Validação
    const form = $('#createGameForm');
    const submitButton = $('#submitButton');

    const validationRules = {
        gameTitle: [
            { validate: value => value && value.trim() !== '', message: 'O nome do jogo é obrigatório.' }
        ],
        gameDescription: [
            { validate: value => value && value.trim() !== '', message: 'A descrição do jogo é obrigatória.' }
        ],
        gamePhoto: [
            {
                validate: () => {
                    const pond = pondInstances.gamePhoto;
                    if (!pond || pond.getFiles().length === 0) return true;
                    return !pond.getFiles().some(file => file.status === 8);
                },
                message: 'A imagem possui um tipo de arquivo inválido.'
            }
        ],
        gameFile: [
            {
                validate: () => pondInstances.gameFile?.getFiles().length > 0,
                message: 'O envio de um arquivo .rar é obrigatório.'
            },
            {
                validate: () => {
                    const pond = pondInstances.gameFile;
                    if (!pond || pond.getFiles().length === 0) return true;
                    const file = pond.getFile().file;
                    return file.name.toLowerCase().endsWith('.rar');
                },
                message: 'O tipo de arquivo é inválido. Apenas .rar é permitido.'
            }
        ]
    };

    setupValidation(validationRules);
    submitButton.prop('disabled', false);

    form.on('submit', async e => {
        e.preventDefault();

        if (!isFormValid(validationRules)) {
            showError('Por favor, corrija os campos inválidos.');
            return;
        }

        submitButton.prop('disabled', true).text('Enviando...');

        const formData = new FormData();
        formData.append('jamId', jamId);
        formData.append('gameTitle', $('#gameTitle').val());
        formData.append('gameDescription', $('#gameDescription').val());
        formData.append('gameContent', $('#gameContent').val());

        const photoPond = pondInstances.gamePhoto;
        if (photoPond && photoPond.getFiles().length > 0) {
            formData.append('gamePhoto', photoPond.getFile().file);
        }

        const filePond = pondInstances.gameFile;
        if (filePond && filePond.getFiles().length > 0) {
            formData.append('gameFile', filePond.getFile().file);
        }

        try {
            await apiRequest('POST', 'api/games', formData);
            showSuccess('Jogo postado com sucesso!');
            setTimeout(() => window.location.href = `/jams/${jamId}`, 2000);
        } catch(err) {
            showError('Ocorreu um erro ao postar o jogo. Tente novamente.');
            submitButton.prop('disabled', false).text('Postar Game');
        }
    });
});