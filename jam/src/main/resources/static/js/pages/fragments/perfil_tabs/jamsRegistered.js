import { fetchUserRegisteredJams } from "../../../services/perfilService.js";
import { applySkeleton, removeSkeleton } from '../../../common/skeleton.js';
import { showError } from "../../../common/notifications.js";

const PAGE_LIMIT = 10;
let currentPage = 0;
let isLoading = false;
let noMoreJams = false;
let currentUserId = null;

//Função auxiliar para tempo relativo
function formatRelativeTime(diffMs) {
    const totalSeconds = Math.floor(diffMs / 1000);
    const totalDays = Math.floor(totalSeconds / 86400);
    const months = Math.floor(totalDays / 30);
    const days = totalDays % 30;
    const hours = Math.floor((totalSeconds % 86400) / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);

    if (months > 0) return `${months} mês${months > 1 ? 'es' : ''}`;
    if (days > 0) return `${days} dia${days > 1 ? 's' : ''}`;
    if (hours > 0) return `${hours} hora${hours > 1 ? 's' : ''}`;
    if (minutes > 0) return `${minutes} minuto${minutes > 1 ? 's' : ''}`;
    return 'agora';
}

//Helper para criar o card de uma JAM
function createJamCard(jam) {
    const statusMap = {
        SCHEDULED: 'Agendada',
        ACTIVE: 'Em andamento',
        FINISHED: 'Finalizada',
    };
    const statusText = statusMap[jam.jamStatus] || jam.jamStatus;

    const agora = new Date();
    const dataInicio = new Date(jam.jamStartDate);
    const dataFim = new Date(jam.jamEndDate);

    const diffStart = dataInicio - agora;
    const diffEnd = dataFim - agora;

    let durationHtml;

    if (diffStart > 0) {
        durationHtml = `<div class="duration-jam-card"><p>Começa em ${formatRelativeTime(diffStart)}</p></div>`;
    } else if (diffEnd > 0) {
        durationHtml = `<div class="duration-jam-card"><p class="duration-solo-jam-card">Termina em ${formatRelativeTime(diffEnd)}</p></div>`;
    } else {
        durationHtml = `<div class="duration-jam-card"><p>Essa jam acabou</p></div>`;
    }

    const card = `
        <div class="jam-card-home" data-jamid="${jam.jamId}">
            <div class="header-jam-card-home">
                <h1 class="status-jam-card-home">${statusText}</h1>
                <div class="aling-qtd-members-jam-card-home">
                    <span class="material-symbols-outlined">account_circle</span>
                    <p>${jam.jamTotalSubscribers}</p>
                </div>
            </div>
            <div class="container-jam-card-home">
                <h1>${jam.jamTitle}</h1>
                ${durationHtml}
            </div>
            <div class="jam-btn-wrapper">
                 <button class="jam-btn-home" onclick="window.location.href='/jams/${jam.jamId}'">Ver Jam</button>
            </div>
        </div>
    `;

    return $(card);
}

async function loadJams() {
    if (isLoading || noMoreJams || !currentUserId) return;

    isLoading = true;
    const container = $('#user-jams-registered-list-container');
    const buttonContainer = $('#load-more-jams-registered-container');
    buttonContainer.find('button').text('Carregando...').prop('disabled', true);

    try {
        const jamData = await fetchUserRegisteredJams(currentUserId, PAGE_LIMIT, currentPage * PAGE_LIMIT);
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
        console.error("Erro ao buscar mais jams inscritas:", error);
        showError("Não foi possível carregar as jams inscritas.");
        noMoreJams = true;
        buttonContainer.hide();
    } finally {
        isLoading = false;
        removeSkeleton(container);
        buttonContainer.find('button').text('Carregar Mais').prop('disabled', false);
    }
}

function createLoadMoreButton() {
    const $buttonContainer = $('#load-more-jams-registered-container');
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

    const container = $('#user-jams-registered-list-container');
    container.empty();
    applySkeleton(container);

    await loadJams();

    if (!noMoreJams) {
        createLoadMoreButton();
    }
}