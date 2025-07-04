import { showSuccess, showError } from '../../common/notifications.js';
import { bindDataFields } from '../../common/bindDataFields.js';
import { applySkeleton, removeSkeleton } from '../../common/skeleton.js';
import { fetchUserData } from "../../services/perfilService.js";
import { initEditPerfilModal } from './commun/editPerfilModal.js';

import { init as initInicio } from '../fragments/perfil_tabs/inicio.js';
import { init as initGames } from '../fragments/perfil_tabs/games.js';
import { init as initJC } from '../fragments/perfil_tabs/jamsCreated.js';
import { init as initJR } from '../fragments/perfil_tabs/jamsRegistered.js';
import { init as initLG } from '../fragments/perfil_tabs/likedGames.js';

$(async function() {
    const pathSegments = window.location.pathname.split('/').filter(Boolean);
    const userId = pathSegments[pathSegments.length - 1];
    const root = 'main.container-perfil';
    const modal = $('#edit-perfil-modal');
    const $mainProfileImg = $('#img-user-perfil img');
    let userData = null;

    const initializedTabs = new Set();

    //Lógica para alternar abas
    $('.menu-perfil button').on('click', function(e) {
        e.preventDefault();

        const button = $(this);
        const tabId = button.data('tab');
        const targetTab = $('#tab-' + tabId);

        if (targetTab.is(':visible')) {
            return;
        }

        //Atualiza a classe 'active' nos botões imediatamente
        $('.menu-perfil button').removeClass('active');
        button.addClass('active');

        //Esconde a aba visível com um efeito de fadeOut
        $('.tab-pane-perfil:visible').fadeOut(300, function() {
            targetTab.fadeIn(300);
        });

        // Lógica para carregar o conteúdo da aba na primeira vez que for clicada
        if (!initializedTabs.has(tabId)) {
            if (tabId === 'games') {
                initGames(userId);
            }
            if (tabId === 'jams-criadas') {
                initJC(userId);
            }
            if (tabId === 'jams-inscritas') {
                initJR(userId);
            }
            if (tabId === 'jogos-curtidos') {
                initLG(userId);
            }

            initializedTabs.add(tabId);
        }
    });

    applySkeleton(root);

    try {
        const userData = await fetchUserData(userId);

        const photoUrl = userData.userPhoto;
        const defaultUrl = $mainProfileImg.data('default');

        // Garante que o skeleton será removido no sucesso ou falha
        $mainProfileImg.off('load error').on('load', function() {
            $(this).removeClass('skeleton');
        }).on('error', function() {
            $(this).attr('src', defaultUrl).removeClass('skeleton');
        });

        //Define a URL da imagem
        if (photoUrl) {
            $mainProfileImg.attr('src', photoUrl);
        } else {
            $mainProfileImg.attr('src', defaultUrl);
        }

        //Preencher campos
        bindDataFields(userData, root);

        //links
        const setLinkState = (selector, url) => {
            const $link = $(selector);
            if (url) {
                $link.attr('href', url).removeClass('disabled-link');
            } else {
                $link.addClass('disabled-link').removeAttr('href');
            }
        };

        setLinkState('#linkedin-link', userData.userLinkedIn);
        setLinkState('#github-link', userData.userGitHub);
        setLinkState('#instagram-link', userData.userInstagram);
        setLinkState('#facebook-link', userData.userFacebook);

        const bannerContainer = $('.details-user-perfil');
        if (userData.userBanner) {
            bannerContainer.css('background-image', `url(${userData.userBanner})`);
        }

        if (userData.userCurrent) {
            const editButton = $('.edit-button');
            editButton.show().on('click', () => {
                initEditPerfilModal(userData);
            });
        }

        initInicio(userData);
        initializedTabs.add('inicio');

    } catch (error) {
        console.error('Erro ao buscar dados do usuário:', error);
        window.location.href = '/404';
    } finally {
        removeSkeleton(root);
    }

    // Evento para fechar o modal
    $('#close-modal-btn').on('click', function() {
        modal.hide();
    });

    modal.on('click', function(e) {
        if ($(e.target).is(modal)) {
            modal.hide();
        }
    });
});