import { apiRequest } from '../common/api.js';

/**
 * Busca todos os dados da view de um Game.
 * @param {string|number} id – O ID do game a ser buscado.
 * @returns {Promise<object>} – Uma promessa que resolve para o objeto com os dados do game.
 */
export function fetchGame(id) {
    return apiRequest('GET', `api/games/${id}`)
        .then(({ data }) => data);
}

/**
 * Registra ou remove um voto (like) para um game.
 * A API na rota '/api/votes' deve ser responsável por gerenciar a lógica de alternância (adicionar/remover).
 * @param {string|number} gameId - O ID do game que receberá o voto.
 * @returns {Promise<object>} – Uma promessa que resolve com a resposta da API após a operação.
 */
export function toggleVote(gameId) {
    const body = { voteGameId: gameId };
    return apiRequest('POST', 'api/votes', body);
}

/**
 * Retorna se ja foi se o usuairo ja voto no jogo.
 * @param {string|number} gameId - O ID do game que receberá o voto.
 * @returns {Promise<object>} – Uma promessa que resolve com a resposta da API após a operação.
 */
export function isLike(gameId) {
    return apiRequest('GET', `api/votes/${gameId}`)
        .then(({ data }) => data);
}

/**
 * Busca o número total de votos para um game específico.
 * @param {string|number} gameId - O ID do game.
 * @returns {Promise<object>} - Uma promessa que resolve com o total de votos.
 */
export function fetchTotalVotes(gameId) {
    return apiRequest('GET', `api/votes/total/${gameId}`)
        .then(({ data }) => data);
}
