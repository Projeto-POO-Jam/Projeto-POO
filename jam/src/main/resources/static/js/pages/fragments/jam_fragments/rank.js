import { fetchJamGames } from '../../../services/jamService.js';
import { showError } from '../../../common/notifications.js';
import { applySkeleton, removeSkeleton } from '../../../common/skeleton.js';
import { bindDataFields } from '../../../common/bindDataFields.js';

//Cria a estrutura base para um item do ranking
function createRankCardTemplate(rank) {
    const cardHtml = `
        <div class="aling-hank">
            <a class="card-game-unic">
                <img class="game-card-photo" data-field="gamePhoto" alt="Capa do jogo">
                <div class="game-card-title" data-field="gameTitle"></div>
            </a>
            <div class="podio-rank${rank} bg-jam-color">
            </div>
        </div>
    `;
    return $(cardHtml);
}

//Preenche a estrutura com os dados do jogo e insere a imagem no pódio.
function populateRankCard(newCard, game, rank) {
    const gameLink = newCard.find('.card-game-unic');
    bindDataFields(game, gameLink);

    const image = gameLink.find('img[data-field="gamePhoto"]');
    if (game.gamePhoto) {
        image.attr('src', game.gamePhoto);
    } else {
        image.attr('src', '../../../../img/imgCardGamePadrao.jpg');
    }
    gameLink.attr('href', `/viewGame/${game.gameId}`);
    image.attr('alt', `Capa do jogo ${game.gameTitle}`);

    //Adiciona a classe principal para posicionamento
    newCard.addClass(`rank-${rank}`);

    //Encontra o container do pódio específico e insere a imagem da medalha
    const podioContainer = newCard.find(`.podio-rank${rank}`);

    const medalImagePath = `../../../../img/medal/${rank}.png`;
    const medalHtml = `<img src="${medalImagePath}" alt="Posição ${rank}" class="podium-icon-img">`;

    podioContainer.html(medalHtml);
    removeSkeleton(newCard);
}

//Função principal que busca os jogos e monta o ranking.
async function loadRank(jamId) {
    const rankContainer = $('#rank-list-container');
    const podioContainer = $('.podio');
    const limit = 3;
    const offset = 0;

    rankContainer.empty();
    applySkeleton(rankContainer);

    try {
        const { games } = await fetchJamGames(jamId, offset, limit);
        rankContainer.empty();

        if (games.length === 0) {
            $("#itens-not-a-rank").html(`
                <img src="/img/naoTemJogo.png" alt="imagem que não tem jogo" class="img-not-game">
                <p>Nenhum jogo foi enviado para esta Jam ainda.</p>   
            `);
            podioContainer.remove();
            return;
        }

        games.forEach((game, index) => {
            const rank = index + 1;
            const newCard = createRankCardTemplate(rank);
            populateRankCard(newCard, game, rank);
            rankContainer.append(newCard);
        });
    }catch (err){
        showError('Não foi possível carregar o ranking.');
        rankContainer.html('<p class="error-message">Ocorreu um erro ao carregar o ranking.</p>');
    }finally {
        removeSkeleton(rankContainer);
    }

}

//Função de inicialização
export function init(data, jamId) {
    const rankContainer = $('#rank-list-container');
    if (!rankContainer.length) return;
    loadRank(jamId);
}