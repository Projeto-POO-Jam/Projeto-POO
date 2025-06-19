import { fetchViewJam } from '../services/jamService.js';
import { bindDataFields } from '../common/bindDataFields.js';
import { applySkeleton, removeSkeleton } from '../common/skeleton.js';
import { showError } from '../common/notifications.js';

import { init as initGeral } from './fragments/jam_fragments/geral.js';
// import { init as initRank } from './fragments/jam_fragments/rank.js';
// import { init as initGames } from './fragments/jam_fragments/games.js';

$(function() {
    //Lógica abas do menu
    $('.options-jam-card button').on('click', function() {
        const tabId = $(this).data('tab');

        // Controla a classe 'active' nos botões
        $('.options-jam-card button').removeClass('active');
        $(this).addClass('active');

        // Mostra/Esconde o conteúdo da aba
        $('.tab-pane').hide();
        $('#tab-' + tabId).show();
    });


    //Lógica para carregar a pagina
    const pathSegments = window.location.pathname.split('/').filter(Boolean);
    const jamId = pathSegments[pathSegments.length - 1];
    const root = 'main';

    applySkeleton(root);

    $.when(fetchViewJam(jamId))
        .done(data => {
            // Lógica de estilização
            const dynamicStyles = [];

            if (data.jamBackgroundColor) {
                $('body').css('background-color', data.jamBackgroundColor);
            }

            if (data.jamBackgroundCardColor) {
                $('#jam-content.card-view-jam-id, .details-jam-card').css('background-color', data.jamBackgroundCardColor);
            }

            if (data.jamTextColor) {
                dynamicStyles.push(`main { color: ${data.jamTextColor}; }`);
            }

            if (data.jamLinkColor) {
                dynamicStyles.push(`main a { color: ${data.jamLinkColor}; }`);
                dynamicStyles.push(`main a:hover { filter: brightness(0.8); }`);
            }

            if (dynamicStyles.length > 0) {
                const styleTag = `<style>${dynamicStyles.join('\n')}</style>`;
                $('head').append(styleTag);
            }

            const coverContainer = $('#jam-cover-container');
            const coverImg = $('img[data-field="jamCover"]');

            if (!data.jamCover) {
                coverContainer.remove();
            } else {
                const coverUrl = data.jamCover;
                coverImg.attr('src', coverUrl).removeClass('skeleton');
                $('body').css({
                    'background-image': `url(${coverUrl})`,
                    'background-size': 'cover',
                    'background-position': 'center',
                    'background-attachment': 'fixed'
                });
            }

            // Preenche os campos estáticos da página
            bindDataFields(data, root);

            //Inicializar Aba
            initGeral(data);
            // initRank(data);
            // initGames(data);

        })
        .fail(err => {
            console.error('Erro ao carregar Jam:', err);
            showError('Não foi possível carregar esta Jam.');
        })
        .always(() => {
            setTimeout(() => {
                removeSkeleton(root);
                $(root).find('.skeleton').removeClass('skeleton');
                $('.container-jam-card').removeClass('skeleton');
            }, 100);
        });
});