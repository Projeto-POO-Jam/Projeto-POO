import { fetchCompleteGameList } from '../../services/gameService.js';
import { showError } from '../../common/notifications.js';
import { applySkeleton, removeSkeleton } from '../../common/skeleton.js';

$(function() {
    const gamesContainer = $('#games-list-container');
    const loadMoreContainer = $('#load-more-container');
    let offset = 0;
    const limit = 20;
    let isLoading = false;
    let totalGames = 0;

    function createGameCard(game) {
        const cardHtml = `
            <a class="card-game-unic" href="/viewGame/${game.gameId}">
                <img class="game-card-photo"
                     src="${game.gamePhoto || '/img/imgCardGamePadrao.png'}"
                     alt="Capa do jogo ${game.gameTitle}">
                <div class="game-card-title-EXG">${game.gameTitle}</div>
            </a>
        `;
        return $(cardHtml);
    }

    async function loadGames() {
        if (isLoading) return;
        isLoading = true;

        const loadMoreButton = loadMoreContainer.find('button');
        loadMoreButton.text('Carregando...').prop('disabled', true);

        try {
            const response = await fetchCompleteGameList(limit, offset);
            const games = response.games;
            totalGames = response.total;

            if (offset === 0) {
                gamesContainer.empty();
            }

            if (games && games.length > 0) {
                games.forEach(game => {
                    const card = createGameCard(game);
                    gamesContainer.append(card);
                });
                offset += games.length;
            } else if (offset === 0) {
                gamesContainer.html('<p>Nenhum jogo encontrado.</p>');
            }

            updateLoadMoreButton();

        } catch (error) {
            console.error("Erro ao buscar jogos:", error);
            showError("Não foi possível carregar os jogos.");
        } finally {
            isLoading = false;
            removeSkeleton(gamesContainer);
            loadMoreContainer.find('button').text('Carregar Mais').prop('disabled', false);
        }
    }

    function updateLoadMoreButton() {
        loadMoreContainer.empty();
        if (offset < totalGames) {
            const button = $('<button class="load-more-btn">Carregar Mais</button>');
            button.on('click', loadGames);
            loadMoreContainer.append(button);
        }
    }

    //Carga inicial
    applySkeleton(gamesContainer);
    loadGames();
});