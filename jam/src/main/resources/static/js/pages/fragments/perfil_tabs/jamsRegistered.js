import { initializePaginatedTab } from '../../perfil/commun/paginatedTabHandler.js';
import { fetchUserRegisteredJams } from "../../../services/perfilService.js";
import { createJamCard } from '../../perfil/commun/cardBuilder.js';

export function init(userId) {
    initializePaginatedTab({
        tabName: 'jams-registered',
        userId: userId,
        fetchFunction: fetchUserRegisteredJams,
        cardCreator: createJamCard,
        dataExtractor: (data) => data.jams
    });
}