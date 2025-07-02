import { initializePaginatedTab } from '../../perfil/commun/paginatedTabHandler.js';
import { fetchUserLikedGames } from "../../../services/perfilService.js";
import { createGameCard } from '../../../common/cardBuilder.js';

export function init(userId) {
    initializePaginatedTab({
        tabName: 'games-liked',
        userId: userId,
        fetchFunction: fetchUserLikedGames,
        cardCreator: createGameCard,
        dataExtractor: (data) => data.games
    });
}