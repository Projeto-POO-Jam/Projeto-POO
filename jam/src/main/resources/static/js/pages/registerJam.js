import { apiRequest } from '../common/api.js';
import { showSuccess, showError } from '../common/notifications.js';
import { setupValidation, isFormValid } from '../common/validation.js';
// NOVO: Importa as funções auxiliares do FilePond
import { registerFilePondPlugins, createFilePondInstance } from '../common/filepond-helper.js';

$(function() {
    const form = $('#createJamForm');
    const submitButton = form.find('button[type="submit"]');

    //Inicializa plugins do FilePond (apenas uma vez)
    registerFilePondPlugins();

    //Inicializa editor WYSIWYG(Summernote)
    $('#content').summernote({
        height: 300,
        codemirror: { theme: 'default' }
    });

    //Funções Auxiliares para cor!
    const createColorPicker = (elementSelector, inputSelector, displaySelector, defaultColor) => {
        const safeDefaultColor = defaultColor || '#42445A';
        const pickr = Pickr.create({
            el: elementSelector,
            theme: 'classic',
            default: safeDefaultColor,
            components: { preview: true, opacity: false, hue: true, interaction: { hex: true, input: true, clear: true, save: true } },
            strings: { save: 'Salvar', clear: 'Limpar' }
        });

        const updateInputs = (color) => {
            if (!color) return;
            const hexaColor = color.toHEXA().toString();
            $(inputSelector).val(hexaColor);
            if (displaySelector) $(displaySelector).val(hexaColor);
        };

        pickr.on('change', (color) => updateInputs(color));
        pickr.on('save', (color, instance) => {
            updateInputs(color);
            pickr.hide();
        });

        updateInputs(pickr.getColor());
        return pickr;
    };

    createColorPicker('#backgroundColorPicker', '#backgroundColor', '#backgroundColorCode', '#F8F9FA');
    createColorPicker('#cardBackgroundColorPicker', '#cardBackgroundColor', '#cardBackgroundColorCode', '#FFFFFF');
    createColorPicker('#textColorPicker', '#textColor', '#textColorCode', '#212529');
    createColorPicker('#linkColorPicker', '#linkColor', '#linkColorCode', '#007BFF');

    flatpickr(".calendario-custom", {
        dateFormat: "d/m/Y H:i",
        enableTime: true,
        locale: {
            firstDayOfWeek: 1,
            weekdays: { shorthand: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"], longhand: ["Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado"] },
            months: { shorthand: ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"], longhand: ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"] },
        }
    });

    const parseDateString = (dateString) => {
        const [datePart, timePart] = dateString.split(' ');
        if (!datePart || !timePart) return null;
        const [day, month, year] = datePart.split('/').map(Number);
        const [hours, minutes] = timePart.split(':').map(Number);
        if (isNaN(day) || isNaN(month) || isNaN(year) || isNaN(hours) || isNaN(minutes)) return null;
        return new Date(year, month - 1, day, hours, minutes);
    };


    //FilePond
    const pondInstances = {};

    pondInstances.jamCover = createFilePondInstance('#coverImg', {
        labelIdle: `Arraste a <strong>imagem de capa</strong> ou <span class="filepond--label-action">Procure</span>`
    });

    pondInstances.jamWallpaper = createFilePondInstance('#wallpaperImg', {
        labelIdle: `Arraste o <strong>wallpaper</strong> ou <span class="filepond--label-action">Procure</span>`
    });

    pondInstances.jamBanner = createFilePondInstance('#bannerImg', {
        labelIdle: `Arraste o <strong>banner</strong> ou <span class="filepond--label-action">Procure</span>`
    });

    //Validação
    const validationRules = {
        jamName: [
            { validate: value => value.trim() !== '', message: 'Nome da Jam é obrigatório.' }
        ],
        startDate: [
            {
                validate: value => {
                    const now = new Date();
                    const date = parseDateString(value);
                    if (!date) return false;
                    const max = new Date(); max.setFullYear(max.getFullYear() + 1);
                    return date > now && date <= max;
                },
                message: 'Data de início deve ser entre hoje e 1 ano a partir de hoje.'
            }
        ],
        endDate: [
            {
                validate: value => {
                    const startStr = $('#startDate').val();
                    if (!startStr) return false;
                    const start = parseDateString(startStr);
                    const end = parseDateString(value);
                    if (!start || !end) return false;
                    const maxEnd = new Date(start); maxEnd.setMonth(maxEnd.getMonth() + 1);
                    return end > start && end <= maxEnd;
                },
                message: 'Data final deve ser no máximo 1 mês após a data de início.'
            }
        ],
        coverImg: [
            {
                validate: () => {
                    const pond = pondInstances.jamCover;
                    if (!pond || pond.getFiles().length === 0) return true;
                    return !pond.getFiles().some(file => file.status === 8);
                },
                message: 'A imagem de capa possui um tipo de arquivo inválido.'
            }
        ],
        wallpaperImg: [
            {
                validate: () => !pondInstances.jamWallpaper?.getFiles().some(file => file.status === 8),
                message: 'O wallpaper possui um tipo de arquivo inválido.'
            }
        ],
        bannerImg: [
            {
                validate: () => !pondInstances.jamBanner?.getFiles().some(file => file.status === 8),
                message: 'O banner possui um tipo de arquivo inválido.'
            }
        ]
    };

    setupValidation(validationRules);
    submitButton.prop('disabled', false);

    //Submissão do Formulário
    form.on('submit', async e => {
        e.preventDefault();
        if (!isFormValid(validationRules)) {
            showError('Por favor, corrija os campos inválidos.');
            return;
        }

        submitButton.prop('disabled', true).text('Enviando...');

        //Usar new FormData() sem o form[0] para começar limpo
        const formData = new FormData();

        //Adiciona os campos de texto e outros
        formData.append('jamTitle', $('#jamName').val().trim());
        formData.append('jamDescription', $('#descricao').val().trim());
        formData.append('jamStartDate', $('#startDate').val());
        formData.append('jamEndDate', $('#endDate').val());
        formData.append('jamContent', $('#content').val());
        formData.append('jamBackgroundColor', $('#backgroundColor').val());
        formData.append('jamBackgroundCardColor', $('#cardBackgroundColor').val());
        formData.append('jamTextColor', $('#textColor').val());
        formData.append('jamLinkColor', $('#linkColor').val());

        const coverPond = pondInstances.jamCover;
        if (coverPond && coverPond.getFiles().length > 0) {
            formData.append('jamCover', coverPond.getFile().file);
        }

        const wallpaperPond = pondInstances.jamWallpaper;
        if (wallpaperPond && wallpaperPond.getFiles().length > 0) {
            formData.append('jamWallpaper', wallpaperPond.getFile().file);
        }

        const bannerPond = pondInstances.jamBanner;
        if (bannerPond && bannerPond.getFiles().length > 0) {
            formData.append('jamBanner', bannerPond.getFile().file);
        }

        try {
            await apiRequest('POST', 'api/jams', formData);
            showSuccess('Jam criada com sucesso!');
            setTimeout(() => window.location.href = '/jams', 2000);
        } catch(err) {
            showError('Erro ao criar Jam.');
            submitButton.prop('disabled', false).text('Criar Jam');
        }
    });
});