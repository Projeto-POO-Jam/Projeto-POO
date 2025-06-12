import { apiRequest } from '../common/api.js';

export function fetchCurrentUser() {
    return apiRequest('GET', '/api/users')
        .then(({ status, data }) => data);
}