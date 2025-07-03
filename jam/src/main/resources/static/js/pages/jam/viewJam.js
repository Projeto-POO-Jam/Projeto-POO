import { fetchViewJam } from '../../services/jamService.js';
import { bindDataFields } from '../../common/bindDataFields.js';
import { applySkeleton, removeSkeleton } from '../../common/skeleton.js';
import { showError } from '../../common/notifications.js';

import { init as initGeral } from '../fragments/jam_fragments/geral.js';
import { init as initRank } from '../fragments/jam_fragments/rank.js';
import { init as initGames } from '../fragments/jam_fragments/games.js';

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

    try {
        const data = await fetchViewJam(jamId);
        const dynamicStyles = [];

        if (data.jamBackgroundColor) {
            $('body').css('background-color', data.jamBackgroundColor);
        }

        if (data.jamBackgroundCardColor) {
            $('#jam-content.card-view-jam-id, .details-jam-card').css('background-color', data.jamBackgroundCardColor);
        }

        if (data.jamTextColor) {
            const textColor = data.jamTextColor;
            dynamicStyles.push(`main { color: ${textColor}; }`);
            dynamicStyles.push(`.options-jam-card button { color: ${textColor}; }`);
            dynamicStyles.push(`.button-edit-jam .material-symbols-outlined { color: ${textColor}; }`);
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

        // Lógica para injetar conteúdo no iframe
        const jamFrame = document.getElementById('jam-content-frame');
        if (jamFrame && data.jamContent) {
            jamFrame.onload = function() {
                const iframeDoc = this.contentWindow.document;

                //Aplica as cores de texto e link definidas na Jam
                const style = iframeDoc.createElement('style');
                style.innerHTML = `
                    html {
                        /* Estilos para Firefox */
                        scrollbar-width: none; /* Esconde a barra de rolagem no Firefox */
                    }
                    /* Estilos para Chrome, Edge e Safari */
                    html::-webkit-scrollbar {
                        display: none; /* Esconde a barra de rolagem */
                    }
                    body { 
                        color: ${data.jamTextColor || '#000'}; 
                        margin: 0; 
                        padding: 1rem;
                        font-family: "League Spartan", sans-serif;
                        height: auto;
                        overflow: hidden; /* Previne scrollbars no body do iframe */
                    }
                    a { color: ${data.jamLinkColor || '#007bff'}; }
                `;
                iframeDoc.head.appendChild(style);

                //Usa requestAnimationFrame para garantir que o redimensionamento ocorra após a renderização
                this.contentWindow.requestAnimationFrame(() => {
                    const newHeight = iframeDoc.body.scrollHeight;
                    this.style.height = newHeight + 'px';
                });
            };

            // Escreve o conteúdo do usuário no iframe
            jamFrame.srcdoc = data.jamContent;
        } else if (jamFrame) {
            // Caso não haja conteúdo
            jamFrame.srcdoc = '<p>O organizador ainda não adicionou uma descrição para esta Jam.</p>';
        }

        //Lógica para exibir o botão de editar o Jam
        if (data.jamUser && data.jamUser.userCurrent) {
            const editButton = $('#edit-jam-btn');

            editButton.on('click', function(event) {
                event.preventDefault();
                window.location.href = `/updateJam/${jamId}`;
            });

            editButton.show();
        }

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

    }catch (err){
        console.error('Erro ao carregar Jam:', err);
        window.location.href = '/404';
    }finally {
        setTimeout(() => {
            removeSkeleton(root);
            $(root).find('.skeleton').removeClass('skeleton');
            $('.container-jam-card').removeClass('skeleton');
        }, 100);
    }
});