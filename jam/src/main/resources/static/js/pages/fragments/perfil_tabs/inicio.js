import {
    fetchUserCreatedGames,
    fetchUserCreatedJams,
    fetchUserRegisteredJams,
    fetchUserLikedGames
} from '../../../services/perfilService.js';
import { applySkeleton, removeSkeleton } from '../../../common/skeleton.js';
import { createGameCard, createJamCardTemplate, populateJamCard } from '../../../common/cardBuilder.js';

/**
 * Renderiza uma lista de itens em um container, agora usando uma mensagem customizada.
 * @param {jQuery} container - O elemento jQuery onde os cards serão inseridos.
 * @param {Array} items - A lista de dados (jogos ou jams).
 * @param {function} createCardFn - A função que cria o card para um item.
 * @param {string} noItemsMessage - Mensagem para exibir se não houver itens.
 */
function renderItems(container, items, createCardFn, noItemsMessage) {
    const $listContainer = container.find('.list-cards-perfil');
    $listContainer.empty();

    if (items && items.length > 0) {
        items.forEach((item, index) => { //Adicionamos o 'index' para o atraso
            let card;
            //Lógica para usar o novo card builder
            if (createCardFn === populateJamCard) {
                card = createJamCardTemplate();
                populateJamCard(card, item);
            } else {
                card = createCardFn(item);
            }

            card.css('opacity', 0); //Garante que o card comece invisível
            $listContainer.append(card);

            //Aplica a animação com um pequeno atraso escalonado
            setTimeout(() => {
                card.addClass('animate-in');
            }, index * 100); //Atraso de 100ms por item
        });
    } else {
        $listContainer.html(`<p class="no-items-message">${noItemsMessage}</p>`);
    }
    removeSkeleton(container);
}

export async function init(userData) {
    const userId = userData.userId;
    if (!userId) return;

    const createdGamesContainer = $('.details-list-inicio').eq(0);
    const createdJamsContainer = $('.details-list-inicio').eq(1);
    const registeredJamsContainer = $('.details-list-inicio').eq(2);
    const likedGamesContainer = $('.details-list-inicio').eq(3);

    // Aplica o efeito de skeleton
    applySkeleton(createdGamesContainer);
    applySkeleton(createdJamsContainer);
    applySkeleton(registeredJamsContainer);
    applySkeleton(likedGamesContainer);

    //Busca todos os dados em paralelo para uma pré-visualização na aba início
    try {
        const [
            createdGamesData,
            createdJamsData,
            registeredJamsData,
            likedGamesData
        ] = await Promise.all([
            fetchUserCreatedGames(userId, 4, 0),
            fetchUserCreatedJams(userId, 4, 0),
            fetchUserRegisteredJams(userId, 4, 0),
            fetchUserLikedGames(userId, 4, 0)
        ]);

        //Renderiza cada seção usando os card builders importados
        renderItems(createdGamesContainer, createdGamesData.games, createGameCard, "Nenhum jogo criado ainda.");
        renderItems(createdJamsContainer, createdJamsData.jams, populateJamCard, "Nenhuma Jam criada ainda.");
        renderItems(registeredJamsContainer, registeredJamsData.jams, populateJamCard, "Não está inscrito em nenhuma Jam.");
        renderItems(likedGamesContainer, likedGamesData.games, createGameCard, "Nenhum jogo curtido ainda.");

    } catch (error) {
        console.error("Erro ao carregar dados para a aba Início:", error);
        createdGamesContainer.html('<p class="error-message">Não foi possível carregar os jogos.</p>');
        createdJamsContainer.html('<p class="error-message">Não foi possível carregar as jams.</p>');
        registeredJamsContainer.html('<p class="error-message">Não foi possível carregar as inscrições.</p>');
        likedGamesContainer.html('<p class="error-message">Não foi possível carregar os jogos curtidos.</p>');
    }
}