import { fetchUserLikedGames } from "../../../services/perfilService.js";
import { applySkeleton, removeSkeleton } from '../../../common/skeleton.js';
import { showError } from "../../../common/notifications.js";

const PAGE_LIMIT = 10;
let currentPage = 0;
let isLoading = false;
let noMoreGames = false;
let currentUserId = null;

//Função para criar card do game
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

async function loadGames() {
    if (isLoading || noMoreGames || !currentUserId) return;

    isLoading = true;
    const container = $('#user-games-liked-list-container');
    const buttonContainer = $('#load-more-games-liked-container');
    buttonContainer.find('button').text('Carregando...').prop('disabled', true);

    try {
        const gamesData = await fetchUserLikedGames(currentUserId, PAGE_LIMIT, currentPage * PAGE_LIMIT);
        const games = gamesData.games;

        if (games && games.length > 0) {
            games.forEach(game => {
                const card = createGameCard(game);
                container.append(card);
            });
            currentPage++;
        }

        //Se a API retornar menos jogos que o limite tira o button
        if (!games || games.length < PAGE_LIMIT) {
            noMoreGames = true;
            buttonContainer.hide();
        }

    } catch (error) {
        console.error("Erro ao buscar mais jogos:", error);
        showError("Não foi possível carregar mais jogos.");
        noMoreGames = true;
        buttonContainer.hide();
    } finally {
        isLoading = false;
        removeSkeleton(container);
        buttonContainer.find('button').text('Carregar Mais').prop('disabled', false);
    }
}

function createLoadMoreButton() {
    const buttonContainer = $('#load-more-games-liked-container');
    buttonContainer.empty();

    const button = $('<button class="load-more-btn">Carregar Mais</button>');
    button.on('click', loadGames);
    buttonContainer.append(button);
}

export async function init(userId) {
    currentPage = 0;
    isLoading = false;
    noMoreGames = false;
    currentUserId = userId;

    const $container = $('#user-games-liked-list-container');
    $container.empty();
    applySkeleton($container);


    await loadGames();

    //Button carregar mais
    if (!noMoreGames) {
        createLoadMoreButton();
    }
}