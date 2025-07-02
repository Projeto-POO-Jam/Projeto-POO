import { initializeJamForm } from './jamForm.js';
import { removeSkeleton } from '../../common/skeleton.js';

$(function() {

    initializeJamForm({
        mode: 'create',
        method: 'POST',
        apiUrl: 'api/jams',
        redirectUrl: '/home',
        entityId: null,
        initialData: {}
    });

    removeSkeleton('.container-register-jam');

});