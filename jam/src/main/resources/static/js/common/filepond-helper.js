export function registerFilePondPlugins() {
    FilePond.registerPlugin(
        FilePondPluginFileValidateType,
        FilePondPluginImageExifOrientation,
        FilePondPluginImagePreview
    );
}

// Cria uma instância individual do FilePond com opções personalizadas.
export function createFilePondInstance(selector, options = {}) {
    const inputElement = document.querySelector(selector);
    if (!inputElement) {
        console.error(`Elemento FilePond não encontrado com o seletor: ${selector}`);
        return null;
    }

    const defaultOptions = {
        labelIdle: `Arraste e solte ou <span class="filepond--label-action">Procure</span>`,
        stylePanelLayout: 'integrated',
        stylePanelAspectRatio: '0.3',
        acceptedFileTypes: ['image/png', 'image/jpeg', 'image/gif'],
        labelFileTypeNotAllowed: 'Arquivo de tipo inválido',
        fileValidateTypeLabelExpectedTypes: 'Use arquivos do tipo: {allButLastType} ou {lastType}',
    };

    const finalOptions = { ...defaultOptions, ...options };
    return FilePond.create(inputElement, finalOptions);
}

/**
 * Cria uma instância do FilePond que pode carregar um arquivo existente a partir de um atributo data.
 * Ideal para formulários de edição server para a biblioteca de input imagem.
 * @param {string} selector - O seletor CSS para o elemento <input>.
 * @param {object} options - Opções do FilePond, que sobrescrevem os padrões.
 * @returns {FilePond} A instância do FilePond ou null.
 */
export function createFilePondInstanceWithOptions(selector, options = {}) {
    const element = document.querySelector(selector);
    if (!element) {
        console.error(`Elemento FilePond não encontrado com o seletor: ${selector}`);
        return null;
    }

    //Opções para carregar arquivos que já estão no servidor
    const serverFileOptions = {};
    const existingFileUrl = element.dataset.existingFileUrl;

    //Se o atributo existir e tiver um valor, configura o FilePond para mostrar esse arquivo
    if (existingFileUrl) {
        serverFileOptions.files = [{
            source: existingFileUrl,
            options: {
                type: 'local',
            },
        }];
    }

    //Mescla as opções: padrões, depois as recebidas, depois as do servidor
    const finalOptions = {
        ...{
            labelIdle: `Arraste e solte ou <span class="filepond--label-action">Procure</span>`
        },
        ...options,
        ...serverFileOptions
    };

    return FilePond.create(element, finalOptions);
}