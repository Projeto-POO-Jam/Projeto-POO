import { initializeJamForm } from './jamForm.js'; // Ajuste o caminho se necessário

$(function() {

    initializeJamForm({
        mode: 'create',
        method: 'POST',
        apiUrl: 'api/jams',
        redirectUrl: '/home',
        entityId: null,
        initialData: {}
    });

});