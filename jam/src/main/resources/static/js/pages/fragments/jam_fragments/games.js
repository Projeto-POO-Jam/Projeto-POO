import { fetchJamGames } from '../../../services/jamService.js';
import { showError } from '../../../common/notifications.js';
import { applySkeleton, removeSkeleton } from '../../../common/skeleton.js';
import { bindDataFields } from '../../../common/bindDataFields.js';

let currentJamId = null;
const limitPerPage = 20;
let currentGameOffset = 0;
let totalGames = 0;
let isLoading = false;

//Cria o templete para um card de jogo
function createGameCardTemplate() {
    const cardHtml = `
        <a class="card-game-unic">
            <img class="game-card-photo" data-field="gamePhoto" alt="Capa do jogo">
            <div class="game-card-title" data-field="gameTitle"></div>
        </a>  
    `;
    return $(cardHtml);
}


//Preenche um template de card com dados
function populateCard(card, game) {
    bindDataFields(game, card);

    const image = card.find('img[data-field="gamePhoto"]');

    if (game.gamePhoto) {
        image.attr('src', game.gamePhoto);
    } else {
        image.attr('src', '../../../../img/imgCardGamePadrao.png');
    }

    card.attr('href', `/viewGame/${game.gameId}`);
    image.attr('alt', `Capa do jogo ${game.gameTitle}`);
    removeSkeleton(card);
}


//Gerencia a visibilidade e o estado do botão "Carregar mais".
function updateLoadMoreButton() {
    $('#load-more-games-btn').remove();

    if (currentGameOffset < totalGames) {
        const btnLoadMore = $('<button>')
            .attr('id', 'load-more-games-btn')
            .addClass('load-more')
            .text('Carregar mais')
            .on('click', loadGames);

        $('#games-list-container').after(btnLoadMore);
    }
}
//Função principal para buscar e renderizar os jogos.
async function loadGames() {
    if (isLoading) return;
    isLoading = true;

    const gamesContainer = $('#games-list-container');
    const loadMoreButton = $('#load-more-games-btn');

    //Lógica de Carregamento
    if (currentGameOffset === 0) {
        gamesContainer.empty();

        //Criar templete para o skeleton
        for (let i = 0; i < 3; i++) {
            gamesContainer.append(createGameCardTemplate());
        }
        applySkeleton(gamesContainer);

    } else {
        loadMoreButton.text('Carregando...').prop('disabled', true);
    }

    try {
        //Busca na API
        const { games, total } = await fetchJamGames(currentJamId, currentGameOffset, limitPerPage);

        totalGames = total;

        if (currentGameOffset === 0) {
            gamesContainer.empty();
        }

        if (games.length === 0 && currentGameOffset === 0) {
            gamesContainer.html(`<p>Nenhum jogo foi enviado para esta Jam ainda.</p>`);
            return;
        }

        games.forEach(game => {
            const newCard = createGameCardTemplate();
            populateCard(newCard, game);
            gamesContainer.append(newCard);
        });

        currentGameOffset += games.length;

        updateLoadMoreButton();

    } catch (err) {
        showError('Não foi possível carregar a lista de jogos.');
        gamesContainer.html('<p class="error-message">Ocorreu um erro ao carregar os jogos.</p>');
    } finally {
        isLoading = false;
    }
}

//Função de inicialização
export function init(data, jamId) {
    const gamesContainer = $('#games-list-container');
    if (!gamesContainer.length) return;

    currentJamId = jamId;
    currentGameOffset = 0;
    totalGames = 0;
    isLoading = false;

    $('#load-more-games-btn').remove();

    loadGames();
}