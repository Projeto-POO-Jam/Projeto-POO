import { apiRequest } from '../common/api.js';

/**
 * Busca todos os dados do usuario específico.
 * @param {string|number} userId - O ID do usuario.
 * @returns {Promise<object>} - Uma promessa que resolve com os dados do usuario.
 */
export function fetchUserData(userId) {
    return apiRequest('GET', `api/users/id/${userId}`)
        .then(({ data }) => data);
}

/**
 * Busca os jogos criados por um usuário com paginação.
 * @param {string|number} userId - O ID do usuário.
 * @param {number} limit - A quantidade de itens por página.
 * @param {number} offset - O deslocamento para a paginação.
 * @returns {Promise<object>}
 */
export function fetchUserCreatedGames(userId, limit = 4, offset = 0) { // Adiciona o offset
    return apiRequest('GET', `api/games/user?userId=${userId}&offset=${offset}&limit=${limit}`)
        .then(({ data }) => data);
}
/**
 * Busca as Jams criadas por um usuário com paginação.
 * @param {string|number} userId - O ID do usuário.
 * @param {number} limit - A quantidade de itens por página.
 * @param {number} offset - O deslocamento para a paginação.
 * @returns {Promise<object>}
 */
export function fetchUserCreatedJams(userId, limit = 4, offset = 0) {
    return apiRequest('GET', `api/jams/createUser?userId=${userId}&offset=${offset}&limit=${limit}`)
        .then(({ data }) => data);
}

/**
 * Busca as Jams em que um usuário está inscrito com paginação.
 * @param {string|number} userId - O ID do usuário.
 * @param {number} limit - A quantidade de itens por página.
 * @param {number} offset - O deslocamento para a paginação.
 * @returns {Promise<object>}
 */
export function fetchUserRegisteredJams(userId, limit = 4, offset = 0) {
    return apiRequest('GET', `api/jams/user?userId=${userId}&offset=${offset}&limit=${limit}`)
        .then(({ data }) => data);
}

/**
 * Busca os jogos que um usuário curtiu.
 * @param {string|number} userId - O ID do usuário.
 * @param {number} limit - A quantidade de itens por página.
 * @param {number} offset - O deslocamento para a paginação.
 * @returns {Promise<object>}
 */
export function fetchUserLikedGames(userId, limit = 4, offset = 0) {
    return apiRequest('GET', `api/games/user/vote?userId=${userId}&offset=${offset}&limit=${limit}`)
        .then(({ data }) => data);
}