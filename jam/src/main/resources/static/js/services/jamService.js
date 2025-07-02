import { apiRequest } from '../common/api.js';

/**
 * Busca Jams paginadas por mês.
 * @param {string} month – Mês no formato 'YYYY-MM'.
 * @param {number} offset – Quantos itens pular.
 * @param {number} limit – Quantos itens buscar.
 * @returns {Promise<{jams: Array, total: number}>}
 */
export function fetchJamsByMonth(month, offset = 0, limit = 4) {
    return apiRequest('GET', `api/jams/list?month=${month}&offset=${offset}&limit=${limit}`)
        .then(({ data }) => data);
}

/**
 * Busca todos os dados da view da Jam
 * @param {number|string} id – o ID da Jam
 * @returns {Promise<object>} – o objeto com todos os campos da Jam
 */
export function fetchViewJam(id) {
    return apiRequest('GET', `api/jams/${id}`)
        .then(({ status, data }) => data);
}

/**
 * Alterna (inscreve/desinscreve) a inscrição do usuário em uma Jam.
 * @param {number|string} jamId - O ID da jam.
 * @returns {Promise<object>}
 */
export function toggleSubscription(jamId) {
    const body = { subscribeJamId: jamId };
    return apiRequest('POST', 'api/subscribes', body);
}

/**
 * Verifica se o usuário atual está inscrito em uma Jam específica.
 * @param {number|string} jamId - O ID da Jam.
 * @returns {Promise<{subscribed: boolean}>} Uma promessa que resolve com o status da inscrição.
 */
export function checkSubscriptionStatus(jamId) {
    return apiRequest('GET', `api/subscribes/${jamId}`)
        .then(({ data }) => data);
}

/**
 * Busca Jams para o banner da home.
 * @param {number} limit – Quantos itens buscar.
 * @returns {Promise<{jams: Array, total: number}>}
 */
export function fetchBannerJams(limit = 5) {
    return apiRequest('GET', `api/jams/banner?limit=${limit}`)
        .then(({ data }) => data);
}

/**
 * Busca a lista de jogos de uma Jam.
 * @param {number|string} jamId - O ID da Jam.
 * @param {number} offset - Quantos itens pular.
 * @param {number} limit - Quantos itens buscar.
 * @returns {Promise<{games: Array, total: number}>}
 */
export function fetchJamGames(jamId, offset = 0, limit = 20) {
    return apiRequest('GET', `api/games/list?jamId=${jamId}&offset=${offset}&limit=${limit}`)
        .then(({ data }) => data);
}


/**
 * Busca o dados para preencher form updateJam.
 * @param {string|number} jamId - O ID da Jam.
 * @returns {Promise<object>} - Uma promessa que resolve os dados da jam.
 */
export function fetchDadoFormUpdate(jamId) {
    return apiRequest('GET', `api/jams/${jamId}`)
        .then(({ data }) => data);
}

/**
 * Exclui uma jam pelo seu ID.
 * @param {string|number} jamId - O ID da jam a ser excluída.
 * @returns {Promise<void>}
 */
export function deleteJam(jamId) {
    return apiRequest('DELETE', `api/jams/${jamId}`);
}
