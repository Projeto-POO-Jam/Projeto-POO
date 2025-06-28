import { initializePaginatedTab } from '../../perfil/commun/paginatedTabHandler.js';
import { fetchUserCreatedGames } from "../../../services/perfilService.js";
import { createGameCard } from '../../perfil/commun/cardBuilder.js';

export function init(userId) {
    initializePaginatedTab({
        tabName: 'games',
        userId: userId,
        fetchFunction: fetchUserCreatedGames,
        cardCreator: createGameCard,
        dataExtractor: (data) => data.games
    });
}