import { apiRequest } from '../common/api.js';
import { showSuccess, showError } from '../common/notifications.js';
import { setupValidation, isFormValid } from '../common/validation.js';

$(function() {
    const form = $('#createJamForm');

    //Função auxiliar para criar e configurar cada seletor de cor
    const createColorPicker = (elementSelector, inputSelector, defaultColor) => {
        const pickr = Pickr.create({
            el: elementSelector,
            theme: 'classic',
            default: defaultColor,

            swatches: [
                '#f44336', '#e91e63', '#9c27b0', '#673ab7',
                '#3f51b5', '#2196f3', '#03a9f4', '#00bcd4',
                '#009688', '#4caf50', '#8bc34a', '#cddc39',
                '#ffeb3b', '#ffc107', '#ff9800', '#795548'
            ],

            components: {
                preview: true,
                opacity: true,
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
                clear: 'Limpar',
                'swatches.recently-used': 'Usadas recentemente',
            }
        });

        //Atualiza o valor do input hidden sempre que a cor for alterada
        pickr.on('change', (color, source, instance) => {
            $(inputSelector).val(color.toHEXA().toString());
        }).on('save', (color, instance) => {
            pickr.hide();
        });

        $(inputSelector).val(pickr.getColor().toHEXA().toString());

        return pickr;
    };

    //Inicializa todos os seletores de cor da página
    createColorPicker('#backgroundColorPicker', '#backgroundColor', '#2a2f3b');
    createColorPicker('#cardBackgroundColorPicker', '#cardBackgroundColor', '#1c1e26');
    createColorPicker('#textColorPicker', '#textColor', '#ffffff');
    createColorPicker('#linkColorPicker', '#linkColor', '#4a90e2');

    //Inicializa editor WYSIWYG(Summernote)
    $('#content').summernote({
        height: 300,
        codemirror: { theme: 'default' }
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
