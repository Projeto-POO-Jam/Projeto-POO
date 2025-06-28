//Função para formatar data,
export function formatRelativeTime(diffMs) {
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

//Função criar card
export function createJamCard(jam) {
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

//Função createGameCard
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