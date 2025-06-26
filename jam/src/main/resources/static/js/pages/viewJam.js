import { fetchViewJam } from '../services/jamService.js';
import { bindDataFields } from '../common/bindDataFields.js';
import { applySkeleton, removeSkeleton } from '../common/skeleton.js';
import { showError } from '../common/notifications.js';

import { init as initGeral } from './fragments/jam_fragments/geral.js';
import { init as initRank } from './fragments/jam_fragments/rank.js';
import { init as initGames } from './fragments/jam_fragments/games.js';

$(async function() {
    //Lógica abas do menu
    $('.options-jam-card button').on('click', function(e) {
        e.preventDefault();

        const button = $(this);
        const tabId = button.data('tab');
        const targetTab = $('#tab-' + tabId);

        //Encontra o painel que está atualmente visível para poder escondê-lo.
        const activeTab = $('.tab-pane:visible');

        //Se a aba clicada já for a ativa, não faz nada.
        if (targetTab.is(activeTab)) {
            return;
        }

        //Controla a classe 'active' nos botões.
        $('.options-jam-card button').removeClass('active');
        button.addClass('active');

        //Define uma duração padrão para a animação.
        const animationDuration = 400;

        activeTab.slideUp(animationDuration, 'swing', function() {
            $(this).removeClass('active');

            //Deslize para baixo
            targetTab.slideDown(animationDuration, 'swing', function() {
                $(this).addClass('active');
            });
        });
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
                dynamicStyles.push(`.options-jam-card button { color: ${data.jamTextColor}; }`);
            }

            if (data.jamLinkColor) {
                dynamicStyles.push(`main a { color: ${data.jamLinkColor}; }`);
                dynamicStyles.push(`
                  .bg-jam-color {
                    background-color: ${data.jamLinkColor} !important;
                    color: #FFFFFF !important;
                  }
                `);
            }

            if (dynamicStyles.length > 0) {
                const styleTag = `<style>${dynamicStyles.join('\n')}</style>`;
                $('head').append(styleTag);
            }

            const coverContainer = $('#jam-cover-container');
            const coverImg = $('img[data-field="jamCover"]');

            if (data.jamCover) {
                const coverUrl = data.jamCover;
                coverImg.attr('src', coverUrl).removeClass('skeleton');

                $('.details-jam-card').css('margin-top', '-2rem');
            } else {
                coverContainer.remove();
            }

            if (data.jamWallpaper) {
                // Se o papel de parede existe, define o background do <body>
                const wallpaperUrl = data.jamWallpaper;
                $('body').css({
                    'background-image': `url(${wallpaperUrl})`,
                    'background-size': 'cover',
                    'background-position': 'center',
                    'background-attachment': 'fixed'
                });
            }

            // Preenche os campos estáticos da página
            bindDataFields(data, root);

            const rankButton = $('button[data-tab="rank"]');
            const rankTabContent = $('#tab-rank');
            const jamEndDate = new Date(data.jamEndDate);
            const now = new Date();

            //Verifica se a Jam já terminou
            if (jamEndDate < now) {
                initRank(data, jamId);
            } else {
                rankButton.prop('disabled', true);
                rankButton.addClass('disabled');
            }

            //Inicializar Aba
            initGeral(data, jamId);
            initGames(data, jamId);

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