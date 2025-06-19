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
    return apiRequest('GET', `api/jams/pegaViewJam/${id}`)
        .then(({ status, data }) => data);
}