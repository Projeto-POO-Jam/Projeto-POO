import { initializePaginatedTab } from '../../perfil/commun/paginatedTabHandler.js';
import { fetchUserCreatedJams } from "../../../services/perfilService.js";
import { createJamCardTemplate, populateJamCard } from '../../../common/cardBuilder.js';

//Função wrapper para manter a compatibilidade com initializePaginatedTab
function createAndPopulateJamCard(jam) {
    const cardTemplate = createJamCardTemplate();
    populateJamCard(cardTemplate, jam);
    return cardTemplate;
}

export function init(userId) {
    initializePaginatedTab({
        tabName: 'jams-registered',
        userId: userId,
        fetchFunction: fetchUserCreatedJams,
        cardCreator: createAndPopulateJamCard,
        dataExtractor: (data) => data.jams
    });
}