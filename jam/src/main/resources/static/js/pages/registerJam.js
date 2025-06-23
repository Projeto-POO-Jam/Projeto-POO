import { apiRequest } from '../common/api.js';
import { showSuccess, showError } from '../common/notifications.js';
import { setupValidation, isFormValid } from '../common/validation.js';

$(function() {
    const form = $('#createJamForm');

    //Função auxiliar para criar e configurar cada seletor de cor
    const createColorPicker = (elementSelector, inputSelector, displaySelector, defaultColor) => {
        const safeDefaultColor = defaultColor || '#42445A';

        const pickr = Pickr.create({
            el: elementSelector,
            theme: 'classic',
            default: safeDefaultColor,

            components: {
                preview: true,
                opacity: false,
                hue: true,
                interaction: {
                    hex: true,
                    rgba: true,
                    hsla: false,
                    hsva: false,
                    cmyk: false,
                    input: true,
                    clear: true,
                    save: true
                }
            },
            strings: {
                save: 'Salvar',
                clear: 'Limpar'
            }
        });

        // Função para atualizar os inputs
        const updateInputs = (color) => {
            if (!color) return; // Segurança extra
            const hexaColor = color.toHEXA().toString();

            $(inputSelector).val(hexaColor);

            if (displaySelector) {
                $(displaySelector).val(hexaColor);
            }
        };

        pickr.on('change', (color) => updateInputs(color));

        pickr.on('save', (color, instance) => {
            updateInputs(color);
            pickr.hide();
        });

        updateInputs(pickr.getColor());

        return pickr;
    };

    //Inicializa todos os seletores de cor da página
    createColorPicker('#backgroundColorPicker', '#backgroundColor', '#backgroundColorCode', '#F8F9FA');
    createColorPicker('#cardBackgroundColorPicker', '#cardBackgroundColor', '#cardBackgroundColorCode', '#FFFFFF');
    createColorPicker('#textColorPicker', '#textColor', '#textColorCode', '#212529');
    createColorPicker('#linkColorPicker', '#linkColor', '#linkColorCode', '#007BFF');

    //Inicializa editor WYSIWYG(Summernote)
    $('#content').summernote({
        height: 300,
        codemirror: { theme: 'default' }
    });


    //biblioteca de img
    FilePond.registerPlugin(
        FilePondPluginFileValidateType,
        FilePondPluginImageExifOrientation,
        FilePondPluginImagePreview
    );

    // Pega todos os inputs com a classe 'filepond' e os transforma
    const simpleInputs = document.querySelectorAll('input.filepond');
    simpleInputs.forEach(inputElement => {
        FilePond.create(inputElement, {
            labelIdle: `Arraste e solte seu arquivo ou <span class="filepond--label-action">Procure</span>`,
            imagePreviewHeight: 170,
            stylePanelLayout: 'integrated',
            styleLoadIndicatorPosition: 'center bottom',
            styleProgressIndicatorPosition: 'right bottom',
            styleButtonRemoveItemPosition: 'left bottom',
            styleButtonProcessItemPosition: 'right bottom',
        });
    });

    //biblioteca do input data:
    flatpickr(".calendario-custom", {
        // Opções básicas
        dateFormat: "d/m/Y H:i",
        enableTime: true,

        locale: {
            firstDayOfWeek: 1,
            weekdays: {
                shorthand: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"],
                longhand: ["Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado"],
            },
            months: {
                shorthand: ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"],
                longhand: ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"],
            },
        }
    });

    //Validação
    const validationRules = {
        jamName: [
            { validate: value => value.trim() !== '', message: 'Nome da Jam é obrigatório.' }
        ],
        startDate: [
            { validate: value => {
                    const now = new Date();
                    const date = new Date(value);
                    const max = new Date(); max.setFullYear(max.getFullYear() + 1);
                    return date > now && date <= max;
                },
                message: 'Data de início deve ser entre hoje e 1 ano a partir de hoje.'
            }
        ],
        endDate: [
            { validate: value => {
                    const start = new Date($('#startDate').val());
                    const end = new Date(value);
                    if (isNaN(start) || isNaN(end)) return false;
                    const maxEnd = new Date(start); maxEnd.setMonth(maxEnd.getMonth() + 1);
                    return end > start && end <= maxEnd;
                },
                message: 'Data final deve ser no máximo 1 mês após a data de início.'
            }
        ]
    };

    setupValidation(validationRules);

    //Formulário
    form.on('submit', async e => {
        e.preventDefault();
        if (!isFormValid(validationRules)) return;

        const formData = new FormData(form[0]);
        formData.set('jamTitle', $.trim($('#jamName').val()));
        formData.set('jamDescription', $.trim($('#descricao').val()));
        formData.set('jamStartDate', $('#startDate').val());
        formData.set('jamEndDate', $('#endDate').val());
        formData.set('jamContent', $('#content').val());
        formData.set('jamBackgroundColor', $('#backgroundColor').val());
        formData.set('jamBackgroundCardColor', $('#cardBackgroundColor').val());
        formData.set('jamTextColor', $('#textColor').val());
        formData.set('jamLinkColor', $('#linkColor').val());

        const cover = $('#coverImg')[0].files[0];
        if (cover) formData.set('jamCover', cover);

        const wallpaper = $('#wallpaperImg')[0].files[0];
        if (wallpaper) formData.set('jamWallpaper', wallpaper);

        const banner = $('#bannerImg')[0].files[0];
        if (banner) formData.set('jamBanner', banner);

        try {
            await apiRequest('POST', 'api/jams', formData);
            showSuccess('Jam criada com sucesso!');
            setTimeout(() => window.location.href = '/jams', 2000);
        } catch(err) {
            showError('Erro ao criar Jam.');
        }
    });
});
