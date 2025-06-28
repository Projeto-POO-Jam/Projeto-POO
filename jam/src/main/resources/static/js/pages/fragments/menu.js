import { fetchCurrentUser } from '../../services/userService.js';
import { bindDataFields } from '../../common/bindDataFields.js';
import { applySkeleton, removeSkeleton } from '../../common/skeleton.js';

$( async function() {
    const root = '.site-header';
    applySkeleton(root);

    try {
        const data = await fetchCurrentUser();
        bindDataFields(data, root);
        removeSkeleton(root);

    }catch (err){
        console.error('Erro ao buscar usuário:', err);
        removeSkeleton(root);
        if (err.status === 401) {
            window.location.href = '/login';
        } else {
            alert('Não foi possível carregar os dados do usuário.');
        }
    }
});
