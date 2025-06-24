// Chame esta função uma vez no seu arquivo principal de script ou no início dos scripts que usam FilePond.
export function registerFilePondPlugins() {
    FilePond.registerPlugin(
        FilePondPluginFileValidateType,
        FilePondPluginImageExifOrientation,
        FilePondPluginImagePreview
    );
}

//Cria uma instância individual do FilePond com opções personalizadas.
export function createFilePondInstance(selector, options = {}) {
    const inputElement = document.querySelector(selector);
    if (!inputElement) {
        console.error(`Elemento FilePond não encontrado com o seletor: ${selector}`);
        return null;
    }

    //Opções padrão que podem ser sobrescritas
    const defaultOptions = {
        labelIdle: `Arraste e solte ou <span class="filepond--label-action">Procure</span>`,
        stylePanelLayout: 'integrated',
        stylePanelAspectRatio: '0.3',
        acceptedFileTypes: ['image/png', 'image/jpeg', 'image/gif'],
        labelFileTypeNotAllowed: 'Arquivo de tipo inválido',
        fileValidateTypeLabelExpectedTypes: 'Use arquivos do tipo: {allButLastType} ou {lastType}',
    };

    //Mescla as opções padrão com as opções específicas fornecidas
    const finalOptions = { ...defaultOptions, ...options };

    return FilePond.create(inputElement, finalOptions);
}