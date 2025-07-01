import { apiRequest } from '../common/api.js';

/**
 * Fazer o logout do usuario
 * @returns {Promise<object>}
 */
export function fetchLogout() {
    return apiRequest('POST', 'perform_logout');
}

/**
 * Busca jams com mesmo nome da pesquisa recebida.
 * @param {string} query – Pesquisa do usuário.
 * @param {number} offset – Quantos itens pular.
 * @param {number} limit – Quantos itens buscar.
 * @returns {Promise<{jams: Array, total: number}>}
 */
export function fetchJamsSearch(query, offset = 0, limit = 10) {
    return apiRequest('GET', `api/jams/search?query=${query}&offset=${offset}&limit=${limit}`)
        .then(({ data }) => data);
}

/**
 * Busca total de novas notificações.
 * @returns {Promise<{notificationTotal: number}>}
 */
export function fetchNotificationTotal() {
    return apiRequest('GET', `api/notifications/total`)
        .then(({ data }) => data);
}

/**
 * Busca notificações paginadas do usuário.
 * @param {number} offset – Quantos itens pular.
 * @param {number} limit – Quantos itens buscar.
 * @returns {Promise<{notifications: Array, total: number}>}
 */
export function fetchNotifications(offset = 0, limit = 10) {
    return apiRequest('GET', `api/notifications?limit=${limit}&offset=${offset}`)
        .then(({ data }) => data);
}

/**
 * Marca as notificações do usuário como lidas.
 */
export function fetchMarkNotificationsAsRead() {
    // Corrigido para PUT, conforme seu NotificationController
    return apiRequest('PUT', 'api/notifications/read');
}