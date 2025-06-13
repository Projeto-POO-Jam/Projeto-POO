import { fetchCurrentUser } from '../../services/userService.js';
import { bindDataFields } from '../../common/bindDataFields.js';

$(function() {
    fetchCurrentUser()
        .then(data => {
            bindDataFields(data);
        })
        .catch(err => {
            console.error('Erro ao buscar usuário:', err);
            if (err.status === 401) {
                window.location.href = '/login';
            } else {
                alert('Não foi possível carregar os dados do usuário.');
            }
        });
});