import { apiRequest } from '../common/api.js';

/**
 * Busca a lista de comentários de um game específico.
 * @param {string|number} gameId - O ID do game.
 * @returns {Promise<Array<object>>} - Uma promessa que resolve para um array de comentários.
 */
export function fetchComments(gameId) {
    return apiRequest('GET', `api/comments/list/${gameId}`)
        .then(({ data }) => data);
}

/**
 * Posta um novo comentário para um game.
 * @param {string} commentText - O texto do comentário.
 * @param {string|number} gameId - O ID do game que está sendo comentado.
 * @returns {Promise<object>} - Uma promessa que resolve com a resposta da API (o comentário criado).
 */
export function postComment(commentText, gameId) {
    const body = {
        commentText,
        gameId
    };
    return apiRequest('POST', 'api/comments', body)
        .then(({ data }) => data);
}

/**
 * Exclui um comentário.
 * @param {string|number} commentId - O ID do comentário a ser excluído.
 * @returns {Promise<object>} - Uma promessa que resolve com a resposta da API.
 */
export function deleteComment(commentId) {
    return apiRequest('DELETE', `api/comments/${commentId}`);
}