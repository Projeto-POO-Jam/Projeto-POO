import { apiRequest } from '../../common/api.js';
import { showSuccess, showError } from '../../common/notifications.js';
import { setupValidation, isFormValid } from '../../common/validation.js';
import { registerFilePondPlugins, createFilePondInstance } from '../../common/filepond-helper.js';

//Converte uma string de data/hora (dd/mm/aaaa HH:MM) para um objeto Date.
const parseDateString = (dateString) => {
    if (!dateString) return null;
    const [datePart, timePart] = dateString.split(' ');
    if (!datePart || !timePart) return null;
    const [day, month, year] = datePart.split('/').map(Number);
    const [hours, minutes] = timePart.split(':').map(Number);
    if (isNaN(day) || isNaN(month) || isNaN(year) || isNaN(hours) || isNaN(minutes)) return null;
    return new Date(year, month - 1, day, hours, minutes);
};

//Cria e configura uma instância do seletor de cores Pickr.
function createColorPicker(elementSelector, inputSelector, displaySelector, defaultColor) {
    const pickr = Pickr.create({
        el: elementSelector,
        theme: 'classic',
        default: defaultColor || '#FFFFFF',
        components: {
            preview: true,
            opacity: false,
            hue: true,
            interaction: { hex: true, input: true, clear: true, save: true }
        },
        strings: { save: 'Salvar', clear: 'Limpar' }
    });

    const origSetColor = pickr.setColor.bind(pickr);

    //Função auxiliar para atualizar seus inputs/displays
    function updateValues(hex) {
        $(inputSelector).val(hex);
        if (displaySelector) $(displaySelector).val(hex);
    }

    //Usa o original para setar o default sem recursão
    if (defaultColor) {
        origSetColor(defaultColor);
        updateValues(defaultColor);
    }

    //Sobrescreve o setColor
    pickr.setColor = (hex) => {
        if (!hex) return;
        origSetColor(hex);
        updateValues(hex);
    };

    pickr
        .on('save', (color) => {
            const hex = color.toHEXA().toString();
            updateValues(hex);
            pickr.hide();
        })
        .on('clear', () => updateValues(''));

    return pickr;
}

/**
 * Inicializa a lógica de um formulário de Jam (criação/edição).
 * @param {object} config - Objeto de configuração.
 * @param {string} config.mode - 'create' ou 'edit'.
 * @param {string} config.apiUrl - A URL da API para o submit.
 * @param {string} config.method - 'POST' ou 'PUT'.
 * @param {string} config.redirectUrl - URL para redirecionar em caso de sucesso.
 * @param {string} [config.entityId] - O ID da Jam (para modo 'edit').
 * @param {object} [config.initialData] - Dados iniciais para preencher o formulário (para modo 'edit').
 * @returns {object} Retorna as instâncias do FilePond para manipulação externa.
 */
export function initializeJamForm(config) {
    const initialData = config.initialData || {};
    const form = $('#jamForm');
    const submitButton = form.find('button[type="submit"]');

    registerFilePondPlugins();

    $('#content').summernote({
        height: 300,
        codemirror: { theme: 'default' }
    });

    const flatpickrInstances = {
        startDate: flatpickr("#startDate", {
            dateFormat: "d/m/Y H:i",
            enableTime: true,
            locale: {
                firstDayOfWeek: 1,
                weekdays: {
                    shorthand: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"],
                    longhand: ["Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado"] },
                months: {
                    shorthand: ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"],
                    longhand: ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"] },
            }
        }),
        endDate: flatpickr("#endDate", {
            dateFormat: "d/m/Y H:i",
            enableTime: true,
            firstDayOfWeek: 1,
            weekdays: {
                shorthand: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"],
                longhand: ["Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado"] },
            months: {
                shorthand: ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"],
                longhand: ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"] },
        })
    };

    const pickrInstances = {
        backgroundColor: createColorPicker('#backgroundColorPicker', '#backgroundColor', '#backgroundColorCode', initialData.backgroundColor || '#F8F9FA'),
        cardBackgroundColor: createColorPicker('#cardBackgroundColorPicker', '#cardBackgroundColor', '#cardBackgroundColorCode', initialData.backgroundCardColor || '#FFFFFF'),
        textColor: createColorPicker('#textColorPicker', '#textColor', '#textColorCode', initialData.textColor || '#212529'),
        linkColor: createColorPicker('#linkColorPicker', '#linkColor', '#linkColorCode', initialData.linkColor || '#007BFF')
    };

    const pondInstances = {
        jamCover: createFilePondInstance('#coverImg', { labelIdle: `Arraste a <strong>imagem de capa</strong> ou <span class="filepond--label-action">Procure</span>` }),
        jamWallpaper: createFilePondInstance('#wallpaperImg', { labelIdle: `Arraste o <strong>wallpaper</strong> ou <span class="filepond--label-action">Procure</span>` }),
        jamBanner: createFilePondInstance('#bannerImg', { labelIdle: `Arraste o <strong>banner</strong> ou <span class="filepond--label-action">Procure</span>` })
    };

    //Definição das Regras de Validação
    const validationRules = {
        jamName: [{ validate: value => value.trim() !== '', message: 'Nome da Jam é obrigatório.' }],
        startDate: [{
            validate: value => {
                const date = parseDateString(value);
                if (!date) return false;
                if (config.mode === 'create') {
                    const now = new Date();
                    const max = new Date(); max.setFullYear(max.getFullYear() + 1);
                    return date > now && date <= max;
                }
                return true; //Em modo de edição, a data pode ser no passado.
            },
            message: 'Data de início deve ser entre hoje e 1 ano a partir de hoje.'
        }],
        endDate: [{
            validate: value => {
                const startStr = $('#startDate').val();
                if (!startStr) return false;
                const start = parseDateString(startStr);
                const end = parseDateString(value);
                if (!start || !end) return false;
                const maxEnd = new Date(start); maxEnd.setMonth(maxEnd.getMonth() + 1);
                return end > start && end <= maxEnd;
            },
            message: 'Data final deve ser superior à data de início e no máximo 1 mês após.'
        }],
        coverImg: [{
            validate: () => !pondInstances.jamCover?.getFiles().some(file => file.status === 8),
            message: 'A imagem de capa possui um tipo de arquivo inválido.'
        }],
        wallpaperImg: [{
            validate: () => !pondInstances.jamWallpaper?.getFiles().some(file => file.status === 8),
            message: 'O wallpaper possui um tipo de arquivo inválido.'
        }],
        bannerImg: [{
            validate: () => !pondInstances.jamBanner?.getFiles().some(file => file.status === 8),
            message: 'O banner possui um tipo de arquivo inválido.'
        }]
    };

    if (config.mode === 'create') {
        validationRules.coverImg.unshift({
            validate: () => pondInstances.jamCover?.getFiles().length > 0,
            message: 'A imagem de capa é obrigatória.'
        });
    }

    setupValidation(validationRules);

    //Lógica de Submissão do Formulário
    submitButton.text(config.mode === 'create' ? 'Criar Jam' : 'Salvar Alterações');
    submitButton.prop('disabled', false);

    form.on('submit', async e => {
        e.preventDefault();
        if (!isFormValid(validationRules)) {
            showError('Por favor, corrija os campos inválidos.');
            return;
        }

        submitButton.prop('disabled', true).text('Enviando...');

        const formData = new FormData();

        //Adiciona o ID no modo de edição
        if (config.mode === 'edit' && config.entityId) {
            formData.append('jamId', config.entityId);
        }

        //Adiciona campos de texto e conteúdo
        formData.append('jamTitle', $('#jamName').val().trim());
        formData.append('jamDescription', $('#descricao').val().trim());
        formData.append('jamStartDate', $('#startDate').val());
        formData.append('jamEndDate', $('#endDate').val());
        formData.append('jamContent', $('#content').summernote('code'));
        formData.append('jamBackgroundColor', $('#backgroundColor').val());
        formData.append('jamBackgroundCardColor', $('#cardBackgroundColor').val());
        formData.append('jamTextColor', $('#textColor').val());
        formData.append('jamLinkColor', $('#linkColor').val());

        //Anexa arquivos apenas se forem novos
        const appendFileIfNew = (pondInstance, fieldName) => {
            if (pondInstance && pondInstance.getFile()?.origin === 1) {
                formData.append(fieldName, pondInstance.getFile().file);
            }
        };

        appendFileIfNew(pondInstances.jamCover, 'jamCover');
        appendFileIfNew(pondInstances.jamWallpaper, 'jamWallpaper');
        appendFileIfNew(pondInstances.jamBanner, 'jamBanner');

        try {
            await apiRequest(config.method, config.apiUrl, formData);
            showSuccess(`Jam ${config.mode === 'create' ? 'criada' : 'atualizada'} com sucesso!`);
            setTimeout(() => window.location.href = config.redirectUrl, 2000);
        } catch(err) {
            showError(`Erro ao ${config.mode === 'create' ? 'criar' : 'atualizar'} a Jam.`);
            submitButton.prop('disabled', false).text(config.mode === 'create' ? 'Criar Jam' : 'Salvar Alterações');
        }
    });

    return { pondInstances, pickrInstances, flatpickrInstances };
}