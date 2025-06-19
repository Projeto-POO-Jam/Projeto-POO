// Função para ser chamada quando os dados da Jam estiverem prontos
export function init(data) {
    // Monta o card de duração
    if (data.jamStartDate && data.jamEndDate) {
        const start = new Date(data.jamStartDate);
        const end = new Date(data.jamEndDate);

        const fmtStart = start.toLocaleDateString('pt-BR');
        const fmtEnd = end.toLocaleDateString('pt-BR');

        $('#jam-duration-container').html(`
            <div class="card-duration-view-jam-id duration-card">
                <p class="range-text">
                    Tempo de Inscrição: ${fmtStart} até ${fmtEnd}
                </p>
                <div class="participants-jam">
                    <h1 class="skeleton">${data.jamTotalSubscribers}</h1>
                    <p>Ingressou</p>
                </div>
                <div class="container-card-duration-view-jam-id">
                    <div class="countdown-text">
                        <span id="cd-prefix">Inicia em</span>
                        <span id="cd-timer">--:--:--:--</span>
                    </div>
                    <button id="join-jam-btn" class="join-btn">Participar</button>
                </div>
            </div>
        `);

        const timer = setInterval(() => updateCountdown(start, end, timer), 1000);
        updateCountdown(start, end, timer);
    }

    // Lógica do HTML do usuario
    const userCard = $('.page-user-jam-card');
    const userHtml = data.jamContent?.trim();

    if (userHtml) {
        const sanitizedHtml = $('<div>').html(userHtml);
        sanitizedHtml.find('script').remove();
        userCard.html(sanitizedHtml.html());
    } else {
        userCard.html(`
            <div class="default-jam-card">
                <p>Descrição da Jam.</p>
            </div>
        `);
    }
}

// Função de countdown
function updateCountdown(start, end, timer) {
    const now = new Date();
    let diff = start - now;
    let prefix = 'Inicia em';

    if (diff <= 0 && now < end) {
        diff = end - now;
        prefix = 'Encerra em';
    } else if (now >= end) {
        $('#cd-prefix').text('Encerrado');
        $('#cd-timer').text('00:00:00:00');
        clearInterval(timer);
        return;
    }

    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((diff % (1000 * 60)) / 1000);

    const pad = (num) => String(num).padStart(2, '0');
    const formattedTime = `${pad(days)}:${pad(hours)}:${pad(minutes)}:${pad(seconds)}`;

    $('#cd-prefix').text(prefix);
    $('#cd-timer').text(formattedTime);
}