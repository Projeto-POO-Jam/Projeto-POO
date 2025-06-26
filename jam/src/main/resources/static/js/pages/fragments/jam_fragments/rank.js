import { fetchJamGames } from '../../../services/jamService.js';
import { showError } from '../../../common/notifications.js';
import { applySkeleton, removeSkeleton } from '../../../common/skeleton.js';
import { bindDataFields } from '../../../common/bindDataFields.js';

//Cria o template para um card do ranking
function createRankCardTemplate(rank) {
    const cardHtml = `
        <a class="card-game-unic">
            <span class="rank-position"></span>
            <img class="game-card-photo" data-field="gamePhoto" alt="Capa do jogo">
            <div class="game-card-title" data-field="gameTitle"></div>
            <div class="podio-rank${rank} bg-jam-color"></div>
        </a>
    `;
    return $(cardHtml);
}

//Preenche o card do ranking com os dados do jogo
function populateRankCard(card, game, rank) {
    bindDataFields(game, card);

    const image = card.find('img[data-field="gamePhoto"]');

    if (game.gamePhoto) {
        image.attr('src', game.gamePhoto);
    } else {
        image.attr('src', '../../../../img/imgCardGamePadrao.jpg');
    }

    card.attr('href', `/viewGame/${game.gameId}`);
    card.find('img[data-field="gamePhoto"]').attr('alt', `Capa do jogo ${game.gameTitle}`);

    card.addClass(`rank-${rank}`);

    card.find('.rank-position').text(`${rank}º`);

    removeSkeleton(card);
}

//Função principal para buscar e renderizar o ranking
function loadRank(jamId) {
    const rankContainer = $('#rank-list-container');
    const limit = 3;
    const offset = 0;

    //Limpa o container e aplica o skeleton
    rankContainer.empty();
    for (let i = 0; i < limit; i++) {
        rankContainer.append(createRankCardTemplate(i));
    }
    applySkeleton(rankContainer);

    //Busca na API
    fetchJamGames(jamId, offset, limit)
        .done(response => {
            const { games } = response;

            //Limpa os skeletons antes de adicionar os cards reais
            rankContainer.empty();

            if (games.length === 0) {
                rankContainer.html('<p>Ainda não há jogos suficientes para formar um ranking.</p>');
                return;
            }

            //Para cada jogo, cria e popula um card de ranking
            games.forEach((game, index) => {
                const rank = index + 1; // A posição é o índice + 1
                const newCard = createRankCardTemplate(rank);
                populateRankCard(newCard, game, rank);
                rankContainer.append(newCard);
            });
        })
        .fail(err => {
            showError('Não foi possível carregar o ranking.');
            rankContainer.html('<p class="error-message">Ocorreu um erro ao carregar o ranking.</p>');
        })
        .always(() => {
            removeSkeleton(rankContainer);
        });
}

//Função de inicialização
export function init(data, jamId) {
    const rankContainer = $('#rank-list-container');
    if (!rankContainer.length) return;

    loadRank(jamId);
}