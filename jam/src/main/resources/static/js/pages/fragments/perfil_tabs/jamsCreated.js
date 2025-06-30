import { initializePaginatedTab } from '../../perfil/commun/paginatedTabHandler.js';
import { fetchUserCreatedJams } from "../../../services/perfilService.js";
import { createJamCard } from '../../perfil/commun/cardBuilder.js';

export function init(userId) {
    initializePaginatedTab({
        tabName: 'jams-created', // Deve corresponder aos IDs no HTML
        userId: userId,
        fetchFunction: fetchUserCreatedJams,
        cardCreator: createJamCard,
        dataExtractor: (data) => data.jams
    });
}