import { apiRequest } from './api.js';
import { showSuccess, showError } from './notifications.js';
import { setupValidation, isFormValid } from './validation.js';
import { registerFilePondPlugins, createFilePondInstance } from './filepond-helper.js';

/**
 * Inicializa a lógica de um formulário de jogo (criação/edição).
 * @param {object} config - Objeto de configuração.
 * @param {string} config.mode - 'create' ou 'edit'.
 * @param {string} config.apiUrl - A URL da API para o submit.
 * @param {string} config.method - 'POST' ou 'PUT'/'PATCH'.
 * @param {string} config.redirectUrl - URL para redirecionar em caso de sucesso.
 * @param {string} [config.entityId] - O ID da entidade (Jam ou Game).
 * @returns {object} Retorna as instâncias do FilePond para manipulação externa.
 */
export function initializeGameForm(config) {
    //Inicializa o Summernote
    $('#gameContent').summernote({ height: 300 });

    //Registra os plugins do FilePond
    registerFilePondPlugins();

    const pondInstances = {};

    // Inicializa a instância da foto, adicionando a propriedade 'server'
    pondInstances.gamePhoto = createFilePondInstance('#gamePhoto', {
        labelIdle: `Arraste a <strong>imagem de capa</strong> ou <span class="filepond--label-action">Procure</span>`,
        server: {}
    });

    //Inicializa a instância do arquivo do jogo com suas opções específicas
    pondInstances.gameFile = createFilePondInstance('#gameFile', {
        labelIdle: `Arraste o <strong>arquivo do jogo (.rar)</strong> ou <span class="filepond--label-action">Procure</span>`,
        stylePanelAspectRatio: '0.1',
        acceptedFileTypes: null,
        server: {}
    });

    if (pondInstances.gameFile) {
        pondInstances.gameFile.on('addfile', (error, file) => {
            if (error) {
                return;
            }
            const rootElement = pondInstances.gameFile.element;
            rootElement.style.height = 'auto';
        });

        pondInstances.gameFile.on('removefile', (error, file) => {
            if (error) {
                return;
            }
            if (pondInstances.gameFile.getFiles().length === 0) {
                const rootElement = pondInstances.gameFile.element;
                rootElement.style.height = '';
            }
        });
    }

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
                validate: () => {
                    const pond = pondInstances.gameFile;
                    if (!pond || pond.getFiles().length === 0) return true;
                    const file = pond.getFile();

                    if (file.origin !== 1) {
                        return true;
                    }

                    return file.file.name.toLowerCase().endsWith('.zip');
                },
                message: 'O tipo de arquivo é inválido. Apenas .zip é permitido.'
            }
        ]
    };

    if (config.mode === 'create') {
        validationRules.gameFile.unshift({
            validate: () => pondInstances.gameFile?.getFiles().length > 0,
            message: 'O envio de um arquivo .rar é obrigatório.'
        });
    }

    setupValidation(validationRules);

    //Envio do Formulário
    const form = $('#gameForm');
    const submitButton = $('#submitButton');

    form.on('submit', async (e) => {
        e.preventDefault();
        if (!isFormValid(validationRules)) {
            showError('Por favor, corrija os campos inválidos.');
            return;
        }

        submitButton.prop('disabled', true).text('Enviando...');

        const formData = new FormData();

        //Adiciona os dados de configuração
        if (config.mode === 'create' && config.entityId) {
            formData.append('jamId', config.entityId);
        } else if (config.mode === 'edit' && config.entityId) {
            formData.append('gameId', config.entityId);
        }

        formData.append('gameTitle', $('#gameTitle').val());
        formData.append('gameDescription', $('#gameDescription').val());
        formData.append('gameContent', $('#gameContent').summernote('code'));

        const photoPond = pondInstances.gamePhoto;
        if (photoPond && photoPond.getFiles().length > 0 && photoPond.getFile().origin === 1) {
            formData.append('gamePhoto', photoPond.getFile().file);
        }

        const filePond = pondInstances.gameFile;
        if (filePond && filePond.getFiles().length > 0 && filePond.getFile().origin === 1) {
            formData.append('gameFile', filePond.getFile().file);
        }

        try {
            await apiRequest(config.method, config.apiUrl, formData);
            showSuccess('Operação realizada com sucesso!');
            setTimeout(() => window.location.href = config.redirectUrl, 2000);
        } catch (err) {
            showError('Ocorreu um erro. Tente novamente.');
            submitButton.prop('disabled', false).text('Postar Game');
        }
    });

    return pondInstances;
}