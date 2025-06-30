import {
    fetchUserCreatedGames,
    fetchUserCreatedJams,
    fetchUserRegisteredJams,
    fetchUserLikedGames
} from "../../../services/perfilService.js";
import { applySkeleton, removeSkeleton } from '../../../common/skeleton.js';

//Helper para criar o card de um JOGO
function createGameCard(game) {
    const cardHtml = `
        <a class="card-game-unic" href="/viewGame/${game.gameId}">
            <img class="game-card-photo"
                 src="${game.gamePhoto || '/img/imgCardGamePadrao.png'}"
                 alt="Capa do jogo ${game.gameTitle}">
            <div class="game-card-title title-game-perfil">${game.gameTitle}</div>
        </a>
    `;
    return $(cardHtml);
}

//Função auxiliar para tempo
function formatRelativeTime(diffMs) {
    const totalSeconds = Math.floor(diffMs / 1000);
    const totalDays = Math.floor(totalSeconds / 86400);
    const months = Math.floor(totalDays / 30);
    const days = totalDays % 30;
    const hours = Math.floor((totalSeconds % 86400) / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);

    if (months > 0) return `${months} mês${months > 1 ? 'es' : ''}`;
    if (days > 0) return `${days} dia${days > 1 ? 's' : ''}`;
    if (hours > 0) return `${hours} hora${hours > 1 ? 's' : ''}`;
    if (minutes > 0) return `${minutes} minuto${minutes > 1 ? 's' : ''}`;
    return 'agora';
}

//Helper para criar o card de uma JAM
function createJamCard(jam) {
    const statusMap = {
        SCHEDULED: 'Agendada',
        ACTIVE: 'Em andamento',
        FINISHED: 'Finalizada',
    };
    const statusText = statusMap[jam.jamStatus] || jam.jamStatus;

    const agora = new Date();
    const dataInicio = new Date(jam.jamStartDate);
    const dataFim = new Date(jam.jamEndDate);

    const diffStart = dataInicio - agora;
    const diffEnd = dataFim - agora;

    let durationHtml;

    if (diffStart > 0) {
        durationHtml = `
            <div class="duration-jam-card">
                <p>Começa em ${formatRelativeTime(diffStart)}</p>
                <p>Termina em ${formatRelativeTime(diffEnd)}</p>
            </div>
        `;
    } else if (diffEnd > 0) {
        durationHtml = `
            <div class="duration-jam-card">
                <p class="duration-solo-jam-card">Termina em ${formatRelativeTime(diffEnd)}</p>
            </div>
        `;
    } else {
        durationHtml = `
            <div class="duration-jam-card">
                <p>Essa jam acabou</p>
            </div>
        `;
    }

    const card = `
        <div class="jam-card-home" data-jamid="${jam.jamId}">
            <div class="header-jam-card-home">
                <h1 class="status-jam-card-home">${statusText}</h1>
                <div class="aling-qtd-members-jam-card-home">
                    <span class="material-symbols-outlined">account_circle</span>
                    <p>${jam.jamTotalSubscribers}</p>
                </div>
            </div>
            <div class="container-jam-card-home">
                <h1>${jam.jamTitle}</h1>
                ${durationHtml}
            </div>
            <div class="jam-btn-wrapper">
                 <button class="jam-btn-home" onclick="window.location.href='/jams/${jam.jamId}'">Ver Jam</button>
            </div>
        </div>
    `;

    return $(card);
}

/**
 * Renderiza uma lista de itens em um container.
 * @param {jQuery} container - O elemento jQuery onde os cards serão inseridos.
 * @param {Array} items - A lista de dados (jogos ou jams).
 * @param {function} createCardFn - A função que cria o card para um item.
 */
function renderItems(container, items, createCardFn) {
    const $listContainer = container.find('.list-cards-perfil');
    $listContainer.empty();

    if (items && items.length > 0) {
        items.forEach(item => {
            const card = createCardFn(item);
            $listContainer.append(card);
        });
    } else {
        $listContainer.html('<p class="no-items-message">Nenhum item encontrado.</p>');
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

    //Busca todos os dados em paralelo
    try {
        const [
            createdGamesData,
            createdJamsData,
            registeredJamsData,
            likedGamesData
        ] = await Promise.all([
            fetchUserCreatedGames(userId),
            fetchUserCreatedJams(userId),
            fetchUserRegisteredJams(userId),
            fetchUserLikedGames(userId)
        ]);

        //Renderiza cada seção
        renderItems(createdGamesContainer, createdGamesData.games, createGameCard);
        renderItems(createdJamsContainer, createdJamsData.jams, createJamCard);
        renderItems(registeredJamsContainer, registeredJamsData.jams, createJamCard);
        renderItems(likedGamesContainer, likedGamesData.games, createGameCard);

    } catch (error) {
        console.error("Erro ao carregar dados para a aba Início:", error);
        createdGamesContainer.html('<p class="error-message">Não foi possível carregar os jogos.</p>');
        createdJamsContainer.html('<p class="error-message">Não foi possível carregar as jams.</p>');
        registeredJamsContainer.html('<p class="error-message">Não foi possível carregar as inscrições.</p>');
        likedGamesContainer.html('<p class="error-message">Não foi possível carregar os jogos curtidos.</p>');
    }
}