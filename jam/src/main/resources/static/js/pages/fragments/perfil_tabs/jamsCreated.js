import { initializePaginatedTab } from '../../perfil/commun/paginatedTabHandler.js';
import { fetchUserCreatedJams } from "../../../services/perfilService.js";
import { createJamCard } from '../../../common/cardBuilder.js';

export function init(userId) {
    initializePaginatedTab({
        tabName: 'jams-created',
        userId: userId,
        fetchFunction: fetchUserCreatedJams,
        cardCreator: createJamCard,
        dataExtractor: (data) => data.jams
    });
}