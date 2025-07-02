import { bindDataFields } from './bindDataFields.js';
import { removeSkeleton } from './skeleton.js';

//Função para formatar data
export function formatRelativeTime(diffMs) {
    if (diffMs < 0) return 'encerrado';

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

/**
 * Cria a estrutura HTML de um card de Jam com atributos data-field.
 * @returns {jQuery} Um elemento jQuery do template do card.
 */
export function createJamCardTemplate() {
    const cardHtml = `
        <div class="jam-card-home">
            <div class="header-jam-card-home">
                <h1 class="status-jam-card-home" data-field="jamStatusText"></h1>
                <div class="aling-qtd-members-jam-card-home">
                    <span class="material-symbols-outlined">account_circle</span>
                    <p data-field="jamTotalSubscribers"></p>
                </div>
            </div>
            <div class="container-jam-card-home">
                <h1 data-field="jamTitle"></h1>
                <div class="duration-jam-card" data-field="durationHtml"></div>
            </div>
            <div class="jam-btn-wrapper">
                 <button class="jam-btn-home">Ver Jam</button>
            </div>
        </div>
    `;
    return $(cardHtml);
}

/**
 * Preenche um elemento de card de Jam com os dados.
 * @param {jQuery} cardElement - O elemento do card (template).
 * @param {object} jam - O objeto da Jam com os dados.
 */
export function populateJamCard(cardElement, jam) {
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
        durationHtml = `<p>Começa em ${formatRelativeTime(diffStart)}</p>`;
    } else if (diffEnd > 0) {
        durationHtml = `<p class="duration-solo-jam-card">Termina em ${formatRelativeTime(diffEnd)}</p>`;
    } else {
        durationHtml = `<p>Essa jam acabou</p>`;
    }

    const dataToBind = {
        ...jam,
        jamStatusText: statusText,
    };

    bindDataFields(dataToBind, cardElement);
    cardElement.find('[data-field="durationHtml"]').html(durationHtml);
    cardElement.attr('data-jamid', jam.jamId);
    cardElement.find('.jam-btn-home').on('click', () => window.location.href = `/jams/${jam.jamId}`);

    removeSkeleton(cardElement);
    cardElement.removeClass('skeleton');
}

//Função criar card do game
export function createGameCard(game) {
    const cardHtml = `
        <a class="card-game-unic" href="/viewGame/${game.gameId}">
            <img class="game-card-photo"
                 src="${game.gamePhoto || '/img/imgCardGamePadrao.png'}"
                 alt="Capa do jogo ${game.gameTitle}">
            <div class="game-card-title">${game.gameTitle}</div>
        </a>
    `;
    return $(cardHtml);
}