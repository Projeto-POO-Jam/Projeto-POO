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
 * Inscreve o usuário em uma Jam.
 * @param {number|string} jamId - O ID da jam para se inscrever.
 * @returns {Promise<object>}
 */
export function subscribeToJam(jamId) {
    const body = { subscribeJamId: jamId };
    return apiRequest('POST', 'api/subscribes', body);
}

/**
 * Remove a inscrição do usuário de uma Jam.
 * @param {number|string} jamId - O ID da jam para sair.
 * @returns {Promise<object>}
 */
export function leaveJam(jamId) {
    const body = { subscribeJamId: jamId };
    return apiRequest('POST', 'api/subscribes', body);
}
