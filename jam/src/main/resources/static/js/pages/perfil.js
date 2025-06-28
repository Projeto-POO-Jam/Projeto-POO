import { showSuccess, showError } from '../common/notifications.js';
import { bindDataFields } from '../common/bindDataFields.js';
import { applySkeleton, removeSkeleton } from '../common/skeleton.js';
import { fetchUserData } from "../services/perfilService.js";
import { initEditPerfilModal } from './editPerfilModal.js';

$(async function() {
    const pathSegments = window.location.pathname.split('/').filter(Boolean);
    const userId = pathSegments[pathSegments.length - 1];
    const root = 'main.container-perfil';
    const modal = $('#edit-perfil-modal');
    const $mainProfileImg = $('#img-user-perfil img');

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

        if (userData.userBanner) {
            $('main.container-perfil').css('background-image', `url(${userData.userBanner})`);
        }

        if (userData.userCurrent) {
            const editButton = $('.edit-button');
            editButton.show().on('click', () => {
                initEditPerfilModal(userData);
            });
        }

    } catch (error) {
        console.error('Erro ao buscar dados do usuário:', error);
        showError('Não foi possível carregar os dados do perfil.');
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