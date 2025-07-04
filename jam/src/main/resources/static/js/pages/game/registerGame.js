import { initializeGameForm } from './gameForm.js';
import { removeSkeleton } from '../../common/skeleton.js';

$(function() {

    //Extrai o ID da Jam da URL.
    const pathParts = window.location.pathname.split('/');
    const jamId = pathParts[pathParts.length - 1];

    //Gerenciador de formulário
    initializeGameForm({
        mode: 'create',
        method: 'POST',
        apiUrl: 'api/games',
        entityId: jamId,
        redirectUrl: `/jams/${jamId}`
    });

    removeSkeleton('.container-register-game');

});