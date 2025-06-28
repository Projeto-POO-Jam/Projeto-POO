import { fetchUserCreatedJams } from "../../../services/perfilService.js";
import { applySkeleton, removeSkeleton } from '../../../common/skeleton.js';
import { showError } from "../../../common/notifications.js";
import { createJamCard } from "./card-builder.js";

const PAGE_LIMIT = 10;
let currentPage = 0;
let isLoading = false;
let noMoreJams = false;
let currentUserId = null;

async function loadJams() {
    if (isLoading || noMoreJams || !currentUserId) return;

    isLoading = true;
    const container = $('#user-jams-created-list-container');
    const buttonContainer = $('#load-more-jams-created-container');
    buttonContainer.find('button').text('Carregando...').prop('disabled', true);

    try {
        const jamData = await fetchUserCreatedJams(currentUserId, PAGE_LIMIT, currentPage * PAGE_LIMIT);
        const jams = jamData.jams;

        if (jams && jams.length > 0) {
            jams.forEach(jam => {
                const card = createJamCard(jam);
                container.append(card);
            });
            currentPage++;
        }

        if (!jams || jams.length < PAGE_LIMIT) {
            noMoreJams = true;
            buttonContainer.hide();
        }

    } catch (error) {
        console.error("Erro ao buscar mais jams criadas:", error);
        showError("Não foi possível carregar as jams criadas.");
        noMoreJams = true;
        buttonContainer.hide();
    } finally {
        isLoading = false;
        removeSkeleton(container);
        buttonContainer.find('button').text('Carregar Mais').prop('disabled', false);
    }
}

function createLoadMoreButton() {
    const $buttonContainer = $('#load-more-jams-created-container');
    $buttonContainer.empty().show();

    const $button = $('<button class="load-more-btn">Carregar Mais</button>');
    $button.on('click', loadJams);
    $buttonContainer.append($button);
}

export async function init(userId) {
    currentPage = 0;
    isLoading = false;
    noMoreJams = false;
    currentUserId = userId;

    const container = $('#user-jams-created-list-container');
    container.empty();
    applySkeleton(container);

    await loadJams();

    if (!noMoreJams) {
        createLoadMoreButton();
    }
}