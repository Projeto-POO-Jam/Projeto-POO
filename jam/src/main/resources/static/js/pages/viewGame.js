import {fetchGame, isLike, toggleVote} from '../services/gameService.js';
import { bindDataFields } from '../common/bindDataFields.js';
import { applySkeleton, removeSkeleton } from '../common/skeleton.js';
import { showError } from '../common/notifications.js';

$(function() {
    //Pega o ID do game pela URL
    const pathSegments = window.location.pathname.split('/').filter(Boolean);
    const gameId = pathSegments[pathSegments.length - 1];
    const root = 'main.game-card';

    //Aplica o efeito de Skeleton
    applySkeleton(root);

    //Busca os dados do game na API

    $.when(isLike(gameId))
        .done(data => {
            //Lógica do botão de Like
            const likeButton = $('.button-like');

            //Adiciona uma classe se o usuário já curtiu
            if (data.voted) {
                likeButton.addClass('liked');
            }

            likeButton.on('click', function() {
                $(this).toggleClass('liked');
            });

        }) .fail(err => {
            console.error('Erro ao carregar Like:', err);
            showError('Não foi possível carregar o like.');
            //Pagina 404
        })
        .always(() => {
            setTimeout(() => {
                removeSkeleton(root);
                $(root).find('.skeleton').removeClass('skeleton');
            }, 150);
        });


    $.when(fetchGame(gameId))
        .done(gameData => {
            //Preenche campos simples
            bindDataFields(gameData, root);

            //Lógica do botão "Acessar game"
            $('.options-view-game button').on('click', function() {
                if (gameData.UrlDoGame) {
                    window.open(gameData.UrlDoGame, '_blank');
                } else {
                    showError('A URL do game não está disponível.');
                }
            });

            //Lógica para injetar o HTML do usuário
            const container = $('.container-view-game');
            const userHtml = gameData.gameContent?.trim();

            if (userHtml) {
                const sanitizedHtml = $('<div>').html(userHtml);
                // Remove todas as tags <script> para segurança
                sanitizedHtml.find('script').remove();
                container.html(sanitizedHtml.html());
            } else {
                container.html('<p class="text-center p-4">O desenvolvedor não adicionou conteúdo adicional.</p>');
            }
        })
        .fail(err => {
            console.error('Erro ao carregar o Game:', err);
            showError('Não foi possível carregar as informações deste game.');
            //Pagina 404
        })
        .always(() => {
            setTimeout(() => {
                removeSkeleton(root);
                $(root).find('.skeleton').removeClass('skeleton');
            }, 150);
        });
});