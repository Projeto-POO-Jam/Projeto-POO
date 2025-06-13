import { apiRequest } from '../common/api.js';

/**
 * Busca Jams paginadas por mês.
 * @param {string} month – Mês no formato 'YYYY-MM'.
 * @param {number} offset – Quantos itens pular.
 * @param {number} limit – Quantos itens buscar.
 * @returns {Promise<{jams: Array, total: number}>}
 */
export function fetchJamsByMonth(month, offset = 0, limit = 20) {
    return apiRequest('GET', `jam/listaJam?month=${month}&offset=${offset}&limit=${limit}`)
        .then(({ data }) => data);
}
