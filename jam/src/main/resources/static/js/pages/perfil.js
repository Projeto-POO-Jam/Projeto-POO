import { showSuccess, showError } from '../common/notifications.js';
import { bindDataFields } from '../common/bindDataFields.js';
import { applySkeleton, removeSkeleton } from '../common/skeleton.js';
import { fetchUserData } from "../services/perfilService.js";

$(async function() {
    //Pega id na URL
    const pathSegments = window.location.pathname.split('/').filter(Boolean);
    const userId = pathSegments[pathSegments.length - 1];
    const root = 'main.container-perfil';

    applySkeleton(root);

    //Função auxiliar para definir o estado de um link
    const setLinkState = (selector, url) => {
        const $link = $(selector);
        if (url) {
            $link.attr('href', url).removeClass('disabled-link');
        } else {
            //Se não houver URL, desativa o link e remove o href
            $link.addClass('disabled-link').removeAttr('href');
        }
    };

    try {
        const userData = await fetchUserData(userId);
        bindDataFields(userData, root);

        //Define o estado para cada link de rede social
        setLinkState('#linkedin-link', userData.userLinkedIn);
        setLinkState('#github-link', userData.userGitHub);
        setLinkState('#instagram-link', userData.userInstagram);
        setLinkState('#facebook-link', userData.userFacebook);

        //Mostra o botão de editar se for o dono do perfil
        if (userData.userCurrent) {
            $('.edit-button').show();
        }

        //Define o banner do usuário como background
        if (userData.userBanner) {
            $('main.container-perfil').css('background-image', `url(${userData.userBanner})`);
        }

    } catch (error) {
        console.error('Erro ao buscar dados do usuário:', error);
        showError('Não foi possível carregar os dados do perfil.');
    } finally {
        removeSkeleton(root);
    }
});