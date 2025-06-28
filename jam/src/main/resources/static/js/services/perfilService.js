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


