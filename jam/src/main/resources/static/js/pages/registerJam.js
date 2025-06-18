import { apiRequest } from '../common/api.js';
import { showSuccess, showError } from '../common/notifications.js';
import { setupValidation, isFormValid } from '../common/validation.js';

$(function() {
    const form = $('#createJamForm');

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
